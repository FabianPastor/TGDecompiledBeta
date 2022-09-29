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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v23, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v24, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v13, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v14, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v16, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v26, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v36, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v39, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v40, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v73, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v7, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v8, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v48, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v51, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v31, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v34, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v37, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v40, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v43, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v63, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v64, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v132, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v133, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v112, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v137, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v65, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v66, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v146, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v70, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v149, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v155, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v158, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v161, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v47, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v50, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v53, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v56, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v59, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v87, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v176, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v179, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v182, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v185, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v188, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v191, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v152, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v195, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v196, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v200, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v203, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v153, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v206, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v209, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v109, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v212, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v111, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v215, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v113, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v218, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v221, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v117, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v119, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v224, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v174, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v121, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v123, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v125, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v229, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v127, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v181, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v131, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v134, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v135, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v137, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v139, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v141, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v143, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v145, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v149, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v153, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v154, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v157, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v159, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v220, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v166, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v229, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v172, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v175, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v178, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v179, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v181, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v184, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v186, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v188, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v189, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v190, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v191, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v193, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v195, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v196, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v198, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v200, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v317, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v210, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v262, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v324, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v264, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v216, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v277, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v331, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v268, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v276, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v278, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v229, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v280, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v282, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v284, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v286, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v288, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v290, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v292, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v303, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v296, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v298, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v308, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v302, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v304, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v313, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v308, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v257, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v258, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v310, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v260, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v318, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v314, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v262, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v316, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v264, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v265, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v323, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v266, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v320, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v267, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v268, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v322, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v269, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v270, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v328, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v271, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v326, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v272, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v273, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v331, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v332, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v498, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v499, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r40v3, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v500, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v59, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v503, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v67, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v68, resolved type: java.lang.String[]} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x021c, code lost:
        if (r14 == 0) goto L_0x1ed9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x021e, code lost:
        if (r14 == 1) goto L_0x1e8f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x0220, code lost:
        if (r14 == 2) goto L_0x1e7e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x0222, code lost:
        if (r14 == 3) goto L_0x1e62;
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
        if (r3 == 0) goto L_0x1e32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:0x02bb, code lost:
        if ("READ_HISTORY".equals(r14) == false) goto L_0x0337;
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
        if (r5 == 0) goto L_0x02ff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:183:0x02ef, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox();
        r1.channel_id = r5;
        r1.max_id = r2;
        r1.still_unread_count = 0;
        r10.add(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x02ff, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox();
        r3 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x030a, code lost:
        if (r3 == 0) goto L_0x0316;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x030c, code lost:
        r5 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r1.peer = r5;
        r5.user_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x0316, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r1.peer = r3;
        r3.chat_id = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x031f, code lost:
        r1.max_id = r2;
        r10.add(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x0324, code lost:
        org.telegram.messenger.MessagesController.getInstance(r29).processUpdateArray(r10, (java.util.ArrayList<org.telegram.tgnet.TLRPC$User>) null, (java.util.ArrayList<org.telegram.tgnet.TLRPC$Chat>) null, false, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x0337, code lost:
        r32 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x033f, code lost:
        r30 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:194:0x0345, code lost:
        if ("MESSAGE_DELETED".equals(r14) == false) goto L_0x03b1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:?, code lost:
        r2 = r11.getString("messages").split(",");
        r7 = new androidx.collection.LongSparseArray();
        r8 = new java.util.ArrayList();
        r9 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x035b, code lost:
        if (r9 >= r2.length) goto L_0x0369;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x035d, code lost:
        r8.add(org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2[r9]));
        r9 = r9 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x0369, code lost:
        r7.put(-r5, r8);
        org.telegram.messenger.NotificationsController.getInstance(r29).removeDeletedMessagesFromNotifications(r7, false);
        org.telegram.messenger.MessagesController.getInstance(r29).deleteMessagesByPush(r3, r8, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x0384, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1e34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x0386, code lost:
        org.telegram.messenger.FileLog.d(r1 + " received " + r14 + " for dialogId = " + r3 + " mids = " + android.text.TextUtils.join(",", r8));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x03b7, code lost:
        if ("READ_REACTION".equals(r14) == false) goto L_0x042e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x03b9, code lost:
        r2 = r11.getString("messages").split(",");
        r7 = new androidx.collection.LongSparseArray();
        r8 = new java.util.ArrayList();
        r9 = new android.util.SparseBooleanArray();
        r10 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x03d2, code lost:
        if (r10 >= r2.length) goto L_0x03ec;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x03d4, code lost:
        r11 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r2[r10]).intValue();
        r8.add(java.lang.Integer.valueOf(r11));
        r9.put(r11, false);
        r10 = r10 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x03ec, code lost:
        r7.put(-r5, r8);
        org.telegram.messenger.NotificationsController.getInstance(r29).removeDeletedMessagesFromNotifications(r7, true);
        org.telegram.messenger.MessagesController.getInstance(r29).checkUnreadReactions(r3, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x0401, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1e34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0403, code lost:
        org.telegram.messenger.FileLog.d(r1 + " received " + r14 + " for dialogId = " + r3 + " mids = " + android.text.TextUtils.join(",", r8));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x0432, code lost:
        if (android.text.TextUtils.isEmpty(r14) != false) goto L_0x1e34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x043a, code lost:
        if (r11.has("msg_id") == false) goto L_0x0443;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x043c, code lost:
        r10 = r11.getInt("msg_id");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x0443, code lost:
        r10 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x044a, code lost:
        if (r11.has("random_id") == false) goto L_0x0461;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x044c, code lost:
        r49 = r8;
        r8 = org.telegram.messenger.Utilities.parseLong(r11.getString("random_id")).longValue();
        r23 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x0461, code lost:
        r23 = r8;
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x0465, code lost:
        if (r10 == 0) goto L_0x04a4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x0467, code lost:
        r25 = r15;
        r13 = org.telegram.messenger.MessagesController.getInstance(r29).dialogs_read_inbox_max.get(java.lang.Long.valueOf(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x0479, code lost:
        if (r13 != null) goto L_0x0498;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x047b, code lost:
        r13 = java.lang.Integer.valueOf(org.telegram.messenger.MessagesStorage.getInstance(r29).getDialogReadMax(false, r3));
        r26 = "messages";
        org.telegram.messenger.MessagesController.getInstance(r29).dialogs_read_inbox_max.put(java.lang.Long.valueOf(r3), r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x0498, code lost:
        r26 = "messages";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x049e, code lost:
        if (r10 <= r13.intValue()) goto L_0x04a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x04a0, code lost:
        r7 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x04a2, code lost:
        r7 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x04a4, code lost:
        r26 = "messages";
        r25 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x04ac, code lost:
        if (r8 == 0) goto L_0x04a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x04b6, code lost:
        if (org.telegram.messenger.MessagesStorage.getInstance(r29).checkMessageByRandomId(r8) != false) goto L_0x04a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x04bf, code lost:
        if (r14.startsWith("REACT_") != false) goto L_0x04c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x04c5, code lost:
        if (r14.startsWith("CHAT_REACT_") == false) goto L_0x04c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x04c7, code lost:
        r7 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x04c8, code lost:
        if (r7 == false) goto L_0x1e34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x04ca, code lost:
        r27 = r8;
        r31 = " for dialogId = ";
        r12 = r11.optLong("chat_from_id", 0);
        r34 = "REACT_";
        r1 = r11.optLong("chat_from_broadcast_id", 0);
        r35 = r11.optLong("chat_from_group_id", 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x04e6, code lost:
        if (r12 != 0) goto L_0x04ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x04ea, code lost:
        if (r35 == 0) goto L_0x04ed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x04ed, code lost:
        r7 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x04ef, code lost:
        r7 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x04f6, code lost:
        if (r11.has("mention") == false) goto L_0x0502;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x04fe, code lost:
        if (r11.getInt("mention") == 0) goto L_0x0502;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x0500, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x0502, code lost:
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x0509, code lost:
        if (r11.has("silent") == false) goto L_0x0517;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x0511, code lost:
        if (r11.getInt("silent") == 0) goto L_0x0517;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x0513, code lost:
        r37 = r1;
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x0517, code lost:
        r37 = r1;
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x051a, code lost:
        r2 = r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0522, code lost:
        if (r2.has("loc_args") == false) goto L_0x0542;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x0524, code lost:
        r1 = r2.getJSONArray("loc_args");
        r2 = r1.length();
        r16 = r9;
        r9 = new java.lang.String[r2];
        r39 = r8;
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x0535, code lost:
        if (r8 >= r2) goto L_0x0540;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x0537, code lost:
        r9[r8] = r1.getString(r8);
        r8 = r8 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x0540, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x0542, code lost:
        r39 = r8;
        r16 = r9;
        r1 = 0;
        r9 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x0548, code lost:
        r2 = r9[r1];
        r1 = r11.has("edit_date");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x0556, code lost:
        if (r14.startsWith("CHAT_") == false) goto L_0x058d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:273:0x055c, code lost:
        if (org.telegram.messenger.UserObject.isReplyUser(r3) == false) goto L_0x0576;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x055e, code lost:
        r2 = r2 + " @ " + r9[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x057a, code lost:
        if (r5 == 0) goto L_0x057e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:277:0x057c, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x057e, code lost:
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:279:0x057f, code lost:
        r11 = false;
        r41 = false;
        r49 = r8;
        r8 = r2;
        r2 = r9[1];
        r40 = r49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0593, code lost:
        if (r14.startsWith("PINNED_") == false) goto L_0x05a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x0599, code lost:
        if (r5 == 0) goto L_0x059d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x059b, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x059d, code lost:
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x059e, code lost:
        r40 = r8;
        r8 = null;
        r11 = false;
        r41 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x05ab, code lost:
        if (r14.startsWith("CHANNEL_") == false) goto L_0x05b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x05ad, code lost:
        r8 = null;
        r11 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x05b0, code lost:
        r8 = null;
        r11 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x05b2, code lost:
        r40 = false;
        r41 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x05b8, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x05ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x05ba, code lost:
        r42 = r2;
        r2 = new java.lang.StringBuilder();
        r43 = r1;
        r13 = r11;
        r11 = r37;
        r37 = r12;
        r2.append(r51);
        r2.append(" received message notification ");
        r2.append(r14);
        r2.append(r31);
        r2.append(r3);
        r2.append(" mid = ");
        r2.append(r10);
        org.telegram.messenger.FileLog.d(r2.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x05ef, code lost:
        r43 = r1;
        r42 = r2;
        r13 = r11;
        r11 = r37;
        r37 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x05fa, code lost:
        r1 = r34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x0600, code lost:
        if (r14.startsWith(r1) != false) goto L_0x1d2d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x0606, code lost:
        if (r14.startsWith("CHAT_REACT_") == false) goto L_0x060a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x060e, code lost:
        switch(r14.hashCode()) {
            case -2100047043: goto L_0x0b66;
            case -2091498420: goto L_0x0b5b;
            case -2053872415: goto L_0x0b50;
            case -2039746363: goto L_0x0b45;
            case -2023218804: goto L_0x0b3a;
            case -1979538588: goto L_0x0b2f;
            case -1979536003: goto L_0x0b24;
            case -1979535888: goto L_0x0b19;
            case -1969004705: goto L_0x0b0e;
            case -1946699248: goto L_0x0b03;
            case -1717283471: goto L_0x0af7;
            case -1646640058: goto L_0x0aeb;
            case -1528047021: goto L_0x0adf;
            case -1507149394: goto L_0x0ad2;
            case -1493579426: goto L_0x0ac6;
            case -1482481933: goto L_0x0aba;
            case -1480102982: goto L_0x0aad;
            case -1478041834: goto L_0x0aa1;
            case -1474543101: goto L_0x0a96;
            case -1465695932: goto L_0x0a8a;
            case -1374906292: goto L_0x0a7e;
            case -1372940586: goto L_0x0a72;
            case -1264245338: goto L_0x0a66;
            case -1236154001: goto L_0x0a5a;
            case -1236086700: goto L_0x0a4e;
            case -1236077786: goto L_0x0a42;
            case -1235796237: goto L_0x0a36;
            case -1235760759: goto L_0x0a2a;
            case -1235686303: goto L_0x0a1d;
            case -1198046100: goto L_0x0a12;
            case -1124254527: goto L_0x0a06;
            case -1085137927: goto L_0x09fa;
            case -1084856378: goto L_0x09ee;
            case -1084820900: goto L_0x09e2;
            case -1084746444: goto L_0x09d6;
            case -819729482: goto L_0x09ca;
            case -772141857: goto L_0x09be;
            case -638310039: goto L_0x09b2;
            case -590403924: goto L_0x09a6;
            case -589196239: goto L_0x099a;
            case -589193654: goto L_0x098e;
            case -589193539: goto L_0x0982;
            case -440169325: goto L_0x0976;
            case -412748110: goto L_0x096a;
            case -228518075: goto L_0x095e;
            case -213586509: goto L_0x0952;
            case -115582002: goto L_0x0946;
            case -112621464: goto L_0x093a;
            case -108522133: goto L_0x092e;
            case -107572034: goto L_0x0920;
            case -40534265: goto L_0x0914;
            case 52369421: goto L_0x0908;
            case 65254746: goto L_0x08fc;
            case 141040782: goto L_0x08f0;
            case 202550149: goto L_0x08e4;
            case 309993049: goto L_0x08d8;
            case 309995634: goto L_0x08cc;
            case 309995749: goto L_0x08c0;
            case 320532812: goto L_0x08b4;
            case 328933854: goto L_0x08a8;
            case 331340546: goto L_0x089c;
            case 342406591: goto L_0x0890;
            case 344816990: goto L_0x0884;
            case 346878138: goto L_0x0878;
            case 350376871: goto L_0x086c;
            case 608430149: goto L_0x0860;
            case 615714517: goto L_0x0855;
            case 715508879: goto L_0x0849;
            case 728985323: goto L_0x083d;
            case 731046471: goto L_0x0831;
            case 734545204: goto L_0x0825;
            case 802032552: goto L_0x0819;
            case 991498806: goto L_0x080d;
            case 1007364121: goto L_0x0801;
            case 1019850010: goto L_0x07f5;
            case 1019917311: goto L_0x07e9;
            case 1019926225: goto L_0x07dd;
            case 1020207774: goto L_0x07d1;
            case 1020243252: goto L_0x07c5;
            case 1020317708: goto L_0x07b9;
            case 1060282259: goto L_0x07ad;
            case 1060349560: goto L_0x07a1;
            case 1060358474: goto L_0x0795;
            case 1060640023: goto L_0x0789;
            case 1060675501: goto L_0x077d;
            case 1060749957: goto L_0x0770;
            case 1073049781: goto L_0x0764;
            case 1078101399: goto L_0x0758;
            case 1110103437: goto L_0x074c;
            case 1160762272: goto L_0x0740;
            case 1172918249: goto L_0x0734;
            case 1234591620: goto L_0x0728;
            case 1281128640: goto L_0x071c;
            case 1281131225: goto L_0x0710;
            case 1281131340: goto L_0x0704;
            case 1310789062: goto L_0x06f7;
            case 1333118583: goto L_0x06eb;
            case 1361447897: goto L_0x06df;
            case 1498266155: goto L_0x06d3;
            case 1533804208: goto L_0x06c7;
            case 1540131626: goto L_0x06bb;
            case 1547988151: goto L_0x06af;
            case 1561464595: goto L_0x06a3;
            case 1563525743: goto L_0x0697;
            case 1567024476: goto L_0x068b;
            case 1810705077: goto L_0x067f;
            case 1815177512: goto L_0x0673;
            case 1954774321: goto L_0x0667;
            case 1963241394: goto L_0x065b;
            case 2014789757: goto L_0x064f;
            case 2022049433: goto L_0x0643;
            case 2034984710: goto L_0x0637;
            case 2048733346: goto L_0x062b;
            case 2099392181: goto L_0x061f;
            case 2140162142: goto L_0x0613;
            default: goto L_0x0611;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x0619, code lost:
        if (r14.equals("CHAT_MESSAGE_GEOLIVE") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x061b, code lost:
        r2 = '=';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x0625, code lost:
        if (r14.equals("CHANNEL_MESSAGE_PHOTOS") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x0627, code lost:
        r2 = ',';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x0631, code lost:
        if (r14.equals("CHANNEL_MESSAGE_NOTEXT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x0633, code lost:
        r2 = 29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x063d, code lost:
        if (r14.equals("CHANNEL_MESSAGE_PLAYLIST") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x063f, code lost:
        r2 = '.';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x0649, code lost:
        if (r14.equals("PINNED_CONTACT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x064b, code lost:
        r2 = '_';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x0655, code lost:
        if (r14.equals("CHAT_PHOTO_EDITED") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x0657, code lost:
        r2 = 'E';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x0661, code lost:
        if (r14.equals("LOCKED_MESSAGE") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x0663, code lost:
        r2 = 'm';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x066d, code lost:
        if (r14.equals("CHAT_MESSAGE_PLAYLIST") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x066f, code lost:
        r2 = 'T';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x0679, code lost:
        if (r14.equals("CHANNEL_MESSAGES") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x067b, code lost:
        r2 = '0';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x0685, code lost:
        if (r14.equals("MESSAGE_INVOICE") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:333:0x0687, code lost:
        r2 = 22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x0691, code lost:
        if (r14.equals("CHAT_MESSAGE_VIDEO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x0693, code lost:
        r2 = '4';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x069d, code lost:
        if (r14.equals("CHAT_MESSAGE_ROUND") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:339:0x069f, code lost:
        r2 = '5';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x06a9, code lost:
        if (r14.equals("CHAT_MESSAGE_PHOTO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x06ab, code lost:
        r2 = '3';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x06b5, code lost:
        if (r14.equals("CHAT_MESSAGE_AUDIO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x06b7, code lost:
        r2 = '8';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x06c1, code lost:
        if (r14.equals("MESSAGE_PLAYLIST") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x06c3, code lost:
        r2 = 26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x06cd, code lost:
        if (r14.equals("MESSAGE_VIDEOS") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x06cf, code lost:
        r2 = 25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x06d9, code lost:
        if (r14.equals("PHONE_CALL_MISSED") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x06db, code lost:
        r2 = 'r';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x06e5, code lost:
        if (r14.equals("MESSAGE_PHOTOS") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x06e7, code lost:
        r2 = 24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x06f1, code lost:
        if (r14.equals("CHAT_MESSAGE_VIDEOS") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x06f3, code lost:
        r2 = 'S';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x06fd, code lost:
        if (r14.equals("MESSAGE_NOTEXT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x06ff, code lost:
        r17 = "CHAT_REACT_";
        r2 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x070a, code lost:
        if (r14.equals("MESSAGE_GIF") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x070c, code lost:
        r2 = 18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x0716, code lost:
        if (r14.equals("MESSAGE_GEO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x0718, code lost:
        r2 = 16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x0722, code lost:
        if (r14.equals("MESSAGE_DOC") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x0724, code lost:
        r2 = 10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x072e, code lost:
        if (r14.equals("CHAT_MESSAGE_GAME_SCORE") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x0730, code lost:
        r2 = '@';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x073a, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GEOLIVE") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x073c, code lost:
        r2 = '(';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x0746, code lost:
        if (r14.equals("CHAT_MESSAGE_PHOTOS") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:0x0748, code lost:
        r2 = 'R';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x0752, code lost:
        if (r14.equals("CHAT_MESSAGE_NOTEXT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x0754, code lost:
        r2 = '2';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x075e, code lost:
        if (r14.equals("CHAT_TITLE_EDITED") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x0760, code lost:
        r2 = 'D';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x076a, code lost:
        if (r14.equals("PINNED_NOTEXT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x076c, code lost:
        r2 = 'X';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x0776, code lost:
        if (r14.equals("MESSAGE_TEXT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x0778, code lost:
        r17 = "CHAT_REACT_";
        r2 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0783, code lost:
        if (r14.equals("MESSAGE_QUIZ") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x0785, code lost:
        r2 = 14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x078f, code lost:
        if (r14.equals("MESSAGE_POLL") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x0791, code lost:
        r2 = 15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x079b, code lost:
        if (r14.equals("MESSAGE_GAME") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x079d, code lost:
        r2 = 19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x07a7, code lost:
        if (r14.equals("MESSAGE_FWDS") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x07a9, code lost:
        r2 = 23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x07b3, code lost:
        if (r14.equals("MESSAGE_DOCS") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x07b5, code lost:
        r2 = 27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x07bf, code lost:
        if (r14.equals("CHAT_MESSAGE_TEXT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x07c1, code lost:
        r2 = '1';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x07cb, code lost:
        if (r14.equals("CHAT_MESSAGE_QUIZ") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x07cd, code lost:
        r2 = ':';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x07d7, code lost:
        if (r14.equals("CHAT_MESSAGE_POLL") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x07d9, code lost:
        r2 = ';';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x07e3, code lost:
        if (r14.equals("CHAT_MESSAGE_GAME") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x07e5, code lost:
        r2 = '?';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x07ef, code lost:
        if (r14.equals("CHAT_MESSAGE_FWDS") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x07f1, code lost:
        r2 = 'Q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x07fb, code lost:
        if (r14.equals("CHAT_MESSAGE_DOCS") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x07fd, code lost:
        r2 = 'U';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x0807, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GAME_SCORE") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x0809, code lost:
        r2 = 21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x0813, code lost:
        if (r14.equals("PINNED_GEOLIVE") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x0815, code lost:
        r2 = 'c';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x081f, code lost:
        if (r14.equals("MESSAGE_CONTACT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x0821, code lost:
        r2 = 13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x082b, code lost:
        if (r14.equals("PINNED_VIDEO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x082d, code lost:
        r2 = 'Z';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x0837, code lost:
        if (r14.equals("PINNED_ROUND") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x0839, code lost:
        r2 = '[';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x0843, code lost:
        if (r14.equals("PINNED_PHOTO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:444:0x0845, code lost:
        r2 = 'Y';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x084f, code lost:
        if (r14.equals("PINNED_AUDIO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x0851, code lost:
        r2 = '^';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x085b, code lost:
        if (r14.equals("MESSAGE_PHOTO_SECRET") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x085d, code lost:
        r2 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x0866, code lost:
        if (r14.equals("CHAT_VOICECHAT_INVITE_YOU") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x0868, code lost:
        r2 = 'J';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x0872, code lost:
        if (r14.equals("CHANNEL_MESSAGE_VIDEO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:456:0x0874, code lost:
        r2 = 31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x087e, code lost:
        if (r14.equals("CHANNEL_MESSAGE_ROUND") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x0880, code lost:
        r2 = ' ';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x088a, code lost:
        if (r14.equals("CHANNEL_MESSAGE_PHOTO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x088c, code lost:
        r2 = 30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x0896, code lost:
        if (r14.equals("CHAT_VOICECHAT_END") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x0898, code lost:
        r2 = 'I';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x08a2, code lost:
        if (r14.equals("CHANNEL_MESSAGE_AUDIO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x08a4, code lost:
        r2 = '#';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x08ae, code lost:
        if (r14.equals("CHAT_MESSAGE_STICKER") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x08b0, code lost:
        r2 = '7';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x08ba, code lost:
        if (r14.equals("MESSAGES") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x08bc, code lost:
        r2 = 28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x08c6, code lost:
        if (r14.equals("CHAT_MESSAGE_GIF") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x08c8, code lost:
        r2 = '>';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x08d2, code lost:
        if (r14.equals("CHAT_MESSAGE_GEO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x08d4, code lost:
        r2 = '<';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x08de, code lost:
        if (r14.equals("CHAT_MESSAGE_DOC") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x08e0, code lost:
        r2 = '6';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x08ea, code lost:
        if (r14.equals("CHAT_VOICECHAT_INVITE") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:486:0x08ec, code lost:
        r2 = 'H';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x08f6, code lost:
        if (r14.equals("CHAT_LEFT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x08f8, code lost:
        r2 = 'M';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0902, code lost:
        if (r14.equals("CHAT_ADD_YOU") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x0904, code lost:
        r2 = 'C';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x090e, code lost:
        if (r14.equals("REACT_TEXT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x0910, code lost:
        r2 = 'i';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x091a, code lost:
        if (r14.equals("CHAT_DELETE_MEMBER") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x091c, code lost:
        r2 = 'K';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x0926, code lost:
        if (r14.equals("MESSAGE_SCREENSHOT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x0928, code lost:
        r17 = "CHAT_REACT_";
        r2 = 8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x0934, code lost:
        if (r14.equals("AUTH_REGION") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x0936, code lost:
        r2 = 'l';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x0940, code lost:
        if (r14.equals("CONTACT_JOINED") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x0942, code lost:
        r2 = 'j';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x094c, code lost:
        if (r14.equals("CHAT_MESSAGE_INVOICE") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x094e, code lost:
        r2 = 'A';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x0958, code lost:
        if (r14.equals("ENCRYPTION_REQUEST") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x095a, code lost:
        r2 = 'n';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x0964, code lost:
        if (r14.equals("MESSAGE_GEOLIVE") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x0966, code lost:
        r2 = 17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x0970, code lost:
        if (r14.equals("CHAT_DELETE_YOU") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x0972, code lost:
        r2 = 'L';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x097c, code lost:
        if (r14.equals("AUTH_UNKNOWN") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x097e, code lost:
        r2 = 'k';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0988, code lost:
        if (r14.equals("PINNED_GIF") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x098a, code lost:
        r2 = 'g';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0994, code lost:
        if (r14.equals("PINNED_GEO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0996, code lost:
        r2 = 'b';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x09a0, code lost:
        if (r14.equals("PINNED_DOC") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x09a2, code lost:
        r2 = '\\';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x09ac, code lost:
        if (r14.equals("PINNED_GAME_SCORE") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x09ae, code lost:
        r2 = 'e';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x09b8, code lost:
        if (r14.equals("CHANNEL_MESSAGE_STICKER") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x09ba, code lost:
        r2 = '\"';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x09c4, code lost:
        if (r14.equals("PHONE_CALL_REQUEST") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:540:0x09c6, code lost:
        r2 = 'p';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x09d0, code lost:
        if (r14.equals("PINNED_STICKER") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x09d2, code lost:
        r2 = ']';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x09dc, code lost:
        if (r14.equals("PINNED_TEXT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x09de, code lost:
        r2 = 'W';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x09e8, code lost:
        if (r14.equals("PINNED_QUIZ") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x09ea, code lost:
        r2 = '`';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x09f4, code lost:
        if (r14.equals("PINNED_POLL") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x09f6, code lost:
        r2 = 'a';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x0a00, code lost:
        if (r14.equals("PINNED_GAME") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x0a02, code lost:
        r2 = 'd';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x0a0c, code lost:
        if (r14.equals("CHAT_MESSAGE_CONTACT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x0a0e, code lost:
        r2 = '9';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x0a18, code lost:
        if (r14.equals("MESSAGE_VIDEO_SECRET") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x0a1a, code lost:
        r2 = 7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x0a23, code lost:
        if (r14.equals("CHANNEL_MESSAGE_TEXT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x0a25, code lost:
        r17 = "CHAT_REACT_";
        r2 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x0a30, code lost:
        if (r14.equals("CHANNEL_MESSAGE_QUIZ") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x0a32, code lost:
        r2 = '%';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0a3c, code lost:
        if (r14.equals("CHANNEL_MESSAGE_POLL") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x0a3e, code lost:
        r2 = '&';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x0a48, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GAME") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0a4a, code lost:
        r2 = '*';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x0a54, code lost:
        if (r14.equals("CHANNEL_MESSAGE_FWDS") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x0a56, code lost:
        r2 = '+';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x0a60, code lost:
        if (r14.equals("CHANNEL_MESSAGE_DOCS") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0a62, code lost:
        r2 = '/';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x0a6c, code lost:
        if (r14.equals("PINNED_INVOICE") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:582:0x0a6e, code lost:
        r2 = 'f';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x0a78, code lost:
        if (r14.equals("CHAT_RETURNED") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x0a7a, code lost:
        r2 = 'N';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x0a84, code lost:
        if (r14.equals("ENCRYPTED_MESSAGE") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x0a86, code lost:
        r2 = 'h';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0a90, code lost:
        if (r14.equals("ENCRYPTION_ACCEPT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0a92, code lost:
        r2 = 'o';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0a9c, code lost:
        if (r14.equals("MESSAGE_VIDEO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0a9e, code lost:
        r2 = 6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0aa7, code lost:
        if (r14.equals("MESSAGE_ROUND") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0aa9, code lost:
        r2 = 9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0ab3, code lost:
        if (r14.equals("MESSAGE_PHOTO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:600:0x0ab5, code lost:
        r17 = "CHAT_REACT_";
        r2 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0ac0, code lost:
        if (r14.equals("MESSAGE_MUTED") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0ac2, code lost:
        r2 = 'q';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0acc, code lost:
        if (r14.equals("MESSAGE_AUDIO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x0ace, code lost:
        r2 = 12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x0ad8, code lost:
        if (r14.equals("MESSAGE_RECURRING_PAY") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x0ada, code lost:
        r17 = "CHAT_REACT_";
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x0ae5, code lost:
        if (r14.equals("CHAT_MESSAGES") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:612:0x0ae7, code lost:
        r2 = 'V';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x0af1, code lost:
        if (r14.equals("CHAT_VOICECHAT_START") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0af3, code lost:
        r2 = 'G';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x0afd, code lost:
        if (r14.equals("CHAT_REQ_JOINED") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x0aff, code lost:
        r2 = 'P';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0b09, code lost:
        if (r14.equals("CHAT_JOINED") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x0b0b, code lost:
        r2 = 'O';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x0b14, code lost:
        if (r14.equals("CHAT_ADD_MEMBER") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x0b16, code lost:
        r2 = 'F';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x0b1f, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GIF") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x0b21, code lost:
        r2 = ')';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x0b2a, code lost:
        if (r14.equals("CHANNEL_MESSAGE_GEO") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x0b2c, code lost:
        r2 = '\'';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0b35, code lost:
        if (r14.equals("CHANNEL_MESSAGE_DOC") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x0b37, code lost:
        r2 = '!';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x0b40, code lost:
        if (r14.equals("CHANNEL_MESSAGE_VIDEOS") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0b42, code lost:
        r2 = '-';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0b4b, code lost:
        if (r14.equals("MESSAGE_STICKER") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x0b4d, code lost:
        r2 = 11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0b56, code lost:
        if (r14.equals("CHAT_CREATED") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0b58, code lost:
        r2 = 'B';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0b61, code lost:
        if (r14.equals("CHANNEL_MESSAGE_CONTACT") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0b63, code lost:
        r2 = '$';
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x0b6c, code lost:
        if (r14.equals("MESSAGE_GAME_SCORE") == false) goto L_0x0b73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0b6e, code lost:
        r2 = 20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0b70, code lost:
        r17 = "CHAT_REACT_";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0b73, code lost:
        r17 = "CHAT_REACT_";
        r2 = 65535;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0b76, code lost:
        r34 = r1;
        r31 = r13;
        r44 = r8;
        r45 = r11;
        r47 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0b90, code lost:
        switch(r2) {
            case 0: goto L_0x1cf3;
            case 1: goto L_0x1cd5;
            case 2: goto L_0x1cd5;
            case 3: goto L_0x1cb8;
            case 4: goto L_0x1c9b;
            case 5: goto L_0x1c7e;
            case 6: goto L_0x1CLASSNAME;
            case 7: goto L_0x1CLASSNAME;
            case 8: goto L_0x1c2b;
            case 9: goto L_0x1c0d;
            case 10: goto L_0x1bef;
            case 11: goto L_0x1b96;
            case 12: goto L_0x1b78;
            case 13: goto L_0x1b55;
            case 14: goto L_0x1b32;
            case 15: goto L_0x1b0f;
            case 16: goto L_0x1af1;
            case 17: goto L_0x1ad3;
            case 18: goto L_0x1ab5;
            case 19: goto L_0x1a92;
            case 20: goto L_0x1a72;
            case 21: goto L_0x1a72;
            case 22: goto L_0x1a4f;
            case 23: goto L_0x1a24;
            case 24: goto L_0x19fd;
            case 25: goto L_0x19d7;
            case 26: goto L_0x19b1;
            case 27: goto L_0x1989;
            case 28: goto L_0x196e;
            case 29: goto L_0x194c;
            case 30: goto L_0x192f;
            case 31: goto L_0x1912;
            case 32: goto L_0x18f5;
            case 33: goto L_0x18d7;
            case 34: goto L_0x187e;
            case 35: goto L_0x1860;
            case 36: goto L_0x183d;
            case 37: goto L_0x181a;
            case 38: goto L_0x17f7;
            case 39: goto L_0x17d9;
            case 40: goto L_0x17bb;
            case 41: goto L_0x179d;
            case 42: goto L_0x177f;
            case 43: goto L_0x1751;
            case 44: goto L_0x172a;
            case 45: goto L_0x1703;
            case 46: goto L_0x16dc;
            case 47: goto L_0x16b3;
            case 48: goto L_0x169d;
            case 49: goto L_0x167b;
            case 50: goto L_0x1658;
            case 51: goto L_0x1635;
            case 52: goto L_0x1612;
            case 53: goto L_0x15ef;
            case 54: goto L_0x15cc;
            case 55: goto L_0x1555;
            case 56: goto L_0x1532;
            case 57: goto L_0x150a;
            case 58: goto L_0x14e2;
            case 59: goto L_0x14ba;
            case 60: goto L_0x1497;
            case 61: goto L_0x1474;
            case 62: goto L_0x1451;
            case 63: goto L_0x1429;
            case 64: goto L_0x1405;
            case 65: goto L_0x13dd;
            case 66: goto L_0x13c1;
            case 67: goto L_0x13c1;
            case 68: goto L_0x13a7;
            case 69: goto L_0x138d;
            case 70: goto L_0x136e;
            case 71: goto L_0x1354;
            case 72: goto L_0x1334;
            case 73: goto L_0x1319;
            case 74: goto L_0x12fe;
            case 75: goto L_0x12e3;
            case 76: goto L_0x12c8;
            case 77: goto L_0x12ad;
            case 78: goto L_0x1292;
            case 79: goto L_0x1277;
            case 80: goto L_0x125c;
            case 81: goto L_0x122b;
            case 82: goto L_0x11fe;
            case 83: goto L_0x11d1;
            case 84: goto L_0x11a4;
            case 85: goto L_0x1175;
            case 86: goto L_0x115a;
            case 87: goto L_0x1104;
            case 88: goto L_0x10b8;
            case 89: goto L_0x106c;
            case 90: goto L_0x1020;
            case 91: goto L_0x0fd4;
            case 92: goto L_0x0var_;
            case 93: goto L_0x0ed3;
            case 94: goto L_0x0e87;
            case 95: goto L_0x0e31;
            case 96: goto L_0x0ddb;
            case 97: goto L_0x0d85;
            case 98: goto L_0x0d39;
            case 99: goto L_0x0ced;
            case 100: goto L_0x0ca1;
            case 101: goto L_0x0CLASSNAME;
            case 102: goto L_0x0CLASSNAME;
            case 103: goto L_0x0bb9;
            case 104: goto L_0x0ba1;
            case 105: goto L_0x0b9b;
            case 106: goto L_0x0b9b;
            case 107: goto L_0x0b9b;
            case 108: goto L_0x0b9b;
            case 109: goto L_0x0b9b;
            case 110: goto L_0x0b9b;
            case 111: goto L_0x0b9b;
            case 112: goto L_0x0b9b;
            case 113: goto L_0x0b9b;
            case 114: goto L_0x0b9b;
            default: goto L_0x0b93;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0b93, code lost:
        r5 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0b9b, code lost:
        r5 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0ba1, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", org.telegram.messenger.R.string.YouHaveNewMessage);
        r42 = org.telegram.messenger.LocaleController.getString("SecretChatName", org.telegram.messenger.R.string.SecretChatName);
        r5 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0bbd, code lost:
        if (r3 <= 0) goto L_0x0bda;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0bbf, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", org.telegram.messenger.R.string.NotificationActionPinnedGifUser, r9[0], r9[1]);
        r5 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0bda, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0bde, code lost:
        if (r7 == false) goto L_0x0bf7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0be0, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", org.telegram.messenger.R.string.NotificationActionPinnedGif, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0bf7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", org.telegram.messenger.R.string.NotificationActionPinnedGifChannel, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0CLASSNAME, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0CLASSNAME, code lost:
        if (r3 <= 0) goto L_0x0c2a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", org.telegram.messenger.R.string.NotificationActionPinnedInvoiceUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0c2a, code lost:
        if (r7 == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0c2c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", org.telegram.messenger.R.string.NotificationActionPinnedInvoice, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", org.telegram.messenger.R.string.NotificationActionPinnedInvoiceChannel, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x0CLASSNAME, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0c5d, code lost:
        if (r3 <= 0) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x0c5f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", org.telegram.messenger.R.string.NotificationActionPinnedGameScoreUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0CLASSNAME, code lost:
        if (r7 == false) goto L_0x0c8f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0CLASSNAME, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", org.telegram.messenger.R.string.NotificationActionPinnedGameScore, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0c8f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", org.telegram.messenger.R.string.NotificationActionPinnedGameScoreChannel, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0ca1, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0ca9, code lost:
        if (r3 <= 0) goto L_0x0cc2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x0cab, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", org.telegram.messenger.R.string.NotificationActionPinnedGameUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0cc2, code lost:
        if (r7 == false) goto L_0x0cdb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0cc4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", org.telegram.messenger.R.string.NotificationActionPinnedGame, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0cdb, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", org.telegram.messenger.R.string.NotificationActionPinnedGameChannel, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0ced, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0cf5, code lost:
        if (r3 <= 0) goto L_0x0d0e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x0cf7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0d0e, code lost:
        if (r7 == false) goto L_0x0d27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0d10, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", org.telegram.messenger.R.string.NotificationActionPinnedGeoLive, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0d27, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveChannel, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0d39, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0d41, code lost:
        if (r3 <= 0) goto L_0x0d5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0d43, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", org.telegram.messenger.R.string.NotificationActionPinnedGeoUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0d5a, code lost:
        if (r7 == false) goto L_0x0d73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0d5c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", org.telegram.messenger.R.string.NotificationActionPinnedGeo, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0d73, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", org.telegram.messenger.R.string.NotificationActionPinnedGeoChannel, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0d85, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0d8d, code lost:
        if (r3 <= 0) goto L_0x0da6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0d8f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", org.telegram.messenger.R.string.NotificationActionPinnedPollUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0da6, code lost:
        if (r7 == false) goto L_0x0dc4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0da8, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", org.telegram.messenger.R.string.NotificationActionPinnedPoll2, r9[0], r9[2], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0dc4, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", org.telegram.messenger.R.string.NotificationActionPinnedPollChannel2, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0ddb, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0de3, code lost:
        if (r3 <= 0) goto L_0x0dfc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0de5, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", org.telegram.messenger.R.string.NotificationActionPinnedQuizUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0dfc, code lost:
        if (r7 == false) goto L_0x0e1a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0dfe, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", org.telegram.messenger.R.string.NotificationActionPinnedQuiz2, r9[0], r9[2], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0e1a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", org.telegram.messenger.R.string.NotificationActionPinnedQuizChannel2, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0e31, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0e39, code lost:
        if (r3 <= 0) goto L_0x0e52;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0e3b, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", org.telegram.messenger.R.string.NotificationActionPinnedContactUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0e52, code lost:
        if (r7 == false) goto L_0x0e70;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0e54, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", org.telegram.messenger.R.string.NotificationActionPinnedContact2, r9[0], r9[2], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0e70, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", org.telegram.messenger.R.string.NotificationActionPinnedContactChannel2, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0e87, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0e8f, code lost:
        if (r3 <= 0) goto L_0x0ea8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0e91, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", org.telegram.messenger.R.string.NotificationActionPinnedVoiceUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0ea8, code lost:
        if (r7 == false) goto L_0x0ec1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0eaa, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", org.telegram.messenger.R.string.NotificationActionPinnedVoice, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0ec1, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", org.telegram.messenger.R.string.NotificationActionPinnedVoiceChannel, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0ed3, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0edb, code lost:
        if (r3 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0edf, code lost:
        if (r9.length <= 1) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0ee7, code lost:
        if (android.text.TextUtils.isEmpty(r9[1]) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0ee9, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", org.telegram.messenger.R.string.NotificationActionPinnedStickerUser, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0var_, code lost:
        if (r7 == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0var_, code lost:
        if (r9.length <= 2) goto L_0x0f3c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0f1e, code lost:
        if (android.text.TextUtils.isEmpty(r9[2]) != false) goto L_0x0f3c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmoji, r9[0], r9[2], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0f3c, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", org.telegram.messenger.R.string.NotificationActionPinnedSticker, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0var_, code lost:
        if (r9.length <= 1) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0f5d, code lost:
        if (android.text.TextUtils.isEmpty(r9[1]) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0f5f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiChannel, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", org.telegram.messenger.R.string.NotificationActionPinnedStickerChannel, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0var_, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x0var_, code lost:
        if (r3 <= 0) goto L_0x0fa9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0var_, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", org.telegram.messenger.R.string.NotificationActionPinnedFileUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0fa9, code lost:
        if (r7 == false) goto L_0x0fc2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0fab, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", org.telegram.messenger.R.string.NotificationActionPinnedFile, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0fc2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", org.telegram.messenger.R.string.NotificationActionPinnedFileChannel, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x0fd4, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0fdc, code lost:
        if (r3 <= 0) goto L_0x0ff5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x0fde, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", org.telegram.messenger.R.string.NotificationActionPinnedRoundUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0ff5, code lost:
        if (r7 == false) goto L_0x100e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0ff7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", org.telegram.messenger.R.string.NotificationActionPinnedRound, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x100e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", org.telegram.messenger.R.string.NotificationActionPinnedRoundChannel, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x1020, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x1028, code lost:
        if (r3 <= 0) goto L_0x1041;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x102a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", org.telegram.messenger.R.string.NotificationActionPinnedVideoUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x1041, code lost:
        if (r7 == false) goto L_0x105a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x1043, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", org.telegram.messenger.R.string.NotificationActionPinnedVideo, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x105a, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", org.telegram.messenger.R.string.NotificationActionPinnedVideoChannel, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x106c, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x1074, code lost:
        if (r3 <= 0) goto L_0x108d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x1076, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", org.telegram.messenger.R.string.NotificationActionPinnedPhotoUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x108d, code lost:
        if (r7 == false) goto L_0x10a6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x108f, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", org.telegram.messenger.R.string.NotificationActionPinnedPhoto, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x10a6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", org.telegram.messenger.R.string.NotificationActionPinnedPhotoChannel, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x10b8, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x10c0, code lost:
        if (r3 <= 0) goto L_0x10d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x10c2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", org.telegram.messenger.R.string.NotificationActionPinnedNoTextUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x10d9, code lost:
        if (r7 == false) goto L_0x10f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x10db, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", org.telegram.messenger.R.string.NotificationActionPinnedNoText, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x10f2, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x1104, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x110c, code lost:
        if (r3 <= 0) goto L_0x1125;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x110e, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.R.string.NotificationActionPinnedTextUser, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x1125, code lost:
        if (r7 == false) goto L_0x1143;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x1127, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.R.string.NotificationActionPinnedText, r9[0], r9[1], r9[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x1143, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedTextChannel, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x115a, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", org.telegram.messenger.R.string.NotificationGroupAlbum, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x1175, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", org.telegram.messenger.R.string.NotificationGroupFew, r9[0], r9[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x11a4, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", org.telegram.messenger.R.string.NotificationGroupFew, r9[0], r9[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x11d1, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", org.telegram.messenger.R.string.NotificationGroupFew, r9[0], r9[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x11fe, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", org.telegram.messenger.R.string.NotificationGroupFew, r9[0], r9[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x122b, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", org.telegram.messenger.R.string.NotificationGroupForwardedFew, r9[0], r9[1], org.telegram.messenger.LocaleController.formatPluralString(r26, org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x125c, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("UserAcceptedToGroupPushWithGroup", org.telegram.messenger.R.string.UserAcceptedToGroupPushWithGroup, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x1277, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", org.telegram.messenger.R.string.NotificationGroupAddSelfMega, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x1292, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", org.telegram.messenger.R.string.NotificationGroupAddSelf, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x12ad, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", org.telegram.messenger.R.string.NotificationGroupLeftMember, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x12c8, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", org.telegram.messenger.R.string.NotificationGroupKickYou, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x12e3, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", org.telegram.messenger.R.string.NotificationGroupKickMember, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x12fe, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", org.telegram.messenger.R.string.NotificationGroupInvitedYouToCall, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x1319, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupEndedCall", org.telegram.messenger.R.string.NotificationGroupEndedCall, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x1334, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", org.telegram.messenger.R.string.NotificationGroupInvitedToCall, r9[0], r9[1], r9[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x1354, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", org.telegram.messenger.R.string.NotificationGroupCreatedCall, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x136e, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", org.telegram.messenger.R.string.NotificationGroupAddMember, r9[0], r9[1], r9[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x138d, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", org.telegram.messenger.R.string.NotificationEditedGroupPhoto, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x13a7, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", org.telegram.messenger.R.string.NotificationEditedGroupName, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x13c1, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", org.telegram.messenger.R.string.NotificationInvitedToGroup, r9[0], r9[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x13da, code lost:
        r5 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x13dd, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", org.telegram.messenger.R.string.NotificationMessageGroupInvoice, r9[0], r9[1], r9[2]);
        r1 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", org.telegram.messenger.R.string.PaymentInvoice);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x1405, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", org.telegram.messenger.R.string.NotificationMessageGroupGameScored, r9[0], r9[1], r9[2], r9[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x1429, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", org.telegram.messenger.R.string.NotificationMessageGroupGame, r9[0], r9[1], r9[2]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachGame", org.telegram.messenger.R.string.AttachGame);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x1451, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", org.telegram.messenger.R.string.NotificationMessageGroupGif, r9[0], r9[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachGif", org.telegram.messenger.R.string.AttachGif);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x1474, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", org.telegram.messenger.R.string.NotificationMessageGroupLiveLocation, r9[0], r9[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", org.telegram.messenger.R.string.AttachLiveLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x1497, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", org.telegram.messenger.R.string.NotificationMessageGroupMap, r9[0], r9[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachLocation", org.telegram.messenger.R.string.AttachLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x14ba, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", org.telegram.messenger.R.string.NotificationMessageGroupPoll2, r9[0], r9[1], r9[2]);
        r1 = org.telegram.messenger.LocaleController.getString("Poll", org.telegram.messenger.R.string.Poll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x14e2, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", org.telegram.messenger.R.string.NotificationMessageGroupQuiz2, r9[0], r9[1], r9[2]);
        r1 = org.telegram.messenger.LocaleController.getString("PollQuiz", org.telegram.messenger.R.string.PollQuiz);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x150a, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", org.telegram.messenger.R.string.NotificationMessageGroupContact2, r9[0], r9[1], r9[2]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachContact", org.telegram.messenger.R.string.AttachContact);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:0x1532, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", org.telegram.messenger.R.string.NotificationMessageGroupAudio, r9[0], r9[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachAudio", org.telegram.messenger.R.string.AttachAudio);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x1555, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x155b, code lost:
        if (r9.length <= 2) goto L_0x159b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x1563, code lost:
        if (android.text.TextUtils.isEmpty(r9[2]) != false) goto L_0x159b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x1565, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", org.telegram.messenger.R.string.NotificationMessageGroupStickerEmoji, r9[0], r9[1], r9[2]);
        r1 = r9[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x159b, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", org.telegram.messenger.R.string.NotificationMessageGroupSticker, r9[0], r9[1]);
        r1 = r9[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x15cc, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", org.telegram.messenger.R.string.NotificationMessageGroupDocument, r9[0], r9[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachDocument", org.telegram.messenger.R.string.AttachDocument);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x15ef, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", org.telegram.messenger.R.string.NotificationMessageGroupRound, r9[0], r9[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachRound", org.telegram.messenger.R.string.AttachRound);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x1612, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", org.telegram.messenger.R.string.NotificationMessageGroupVideo, r9[0], r9[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachVideo", org.telegram.messenger.R.string.AttachVideo);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x1635, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", org.telegram.messenger.R.string.NotificationMessageGroupPhoto, r9[0], r9[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachPhoto", org.telegram.messenger.R.string.AttachPhoto);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x1658, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", org.telegram.messenger.R.string.NotificationMessageGroupNoText, r9[0], r9[1]);
        r1 = org.telegram.messenger.LocaleController.getString("Message", org.telegram.messenger.R.string.Message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x167b, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", org.telegram.messenger.R.string.NotificationMessageGroupText, r9[0], r9[1], r9[2]);
        r1 = r9[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x169d, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", org.telegram.messenger.R.string.ChannelMessageAlbum, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x16b3, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", org.telegram.messenger.R.string.ChannelMessageFew, r9[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x16dc, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", org.telegram.messenger.R.string.ChannelMessageFew, r9[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x1703, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", org.telegram.messenger.R.string.ChannelMessageFew, r9[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x172a, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", org.telegram.messenger.R.string.ChannelMessageFew, r9[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x1751, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", org.telegram.messenger.R.string.ChannelMessageFew, r9[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9[1]).intValue(), new java.lang.Object[0]).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x177f, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", org.telegram.messenger.R.string.NotificationMessageGame, r9[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachGame", org.telegram.messenger.R.string.AttachGame);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x179d, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", org.telegram.messenger.R.string.ChannelMessageGIF, r9[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachGif", org.telegram.messenger.R.string.AttachGif);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x17bb, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", org.telegram.messenger.R.string.ChannelMessageLiveLocation, r9[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", org.telegram.messenger.R.string.AttachLiveLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x17d9, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", org.telegram.messenger.R.string.ChannelMessageMap, r9[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachLocation", org.telegram.messenger.R.string.AttachLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x17f7, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", org.telegram.messenger.R.string.ChannelMessagePoll2, r9[0], r9[1]);
        r1 = org.telegram.messenger.LocaleController.getString("Poll", org.telegram.messenger.R.string.Poll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x181a, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", org.telegram.messenger.R.string.ChannelMessageQuiz2, r9[0], r9[1]);
        r1 = org.telegram.messenger.LocaleController.getString("QuizPoll", org.telegram.messenger.R.string.QuizPoll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x183d, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", org.telegram.messenger.R.string.ChannelMessageContact2, r9[0], r9[1]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachContact", org.telegram.messenger.R.string.AttachContact);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:832:0x1860, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", org.telegram.messenger.R.string.ChannelMessageAudio, r9[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachAudio", org.telegram.messenger.R.string.AttachAudio);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x187e, code lost:
        r12 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x1884, code lost:
        if (r9.length <= 1) goto L_0x18bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x188c, code lost:
        if (android.text.TextUtils.isEmpty(r9[1]) != false) goto L_0x18bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x188e, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", org.telegram.messenger.R.string.ChannelMessageStickerEmoji, r9[0], r9[1]);
        r1 = r9[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x18bf, code lost:
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", org.telegram.messenger.R.string.ChannelMessageSticker, r9[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x18d7, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", org.telegram.messenger.R.string.ChannelMessageDocument, r9[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachDocument", org.telegram.messenger.R.string.AttachDocument);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x18f5, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", org.telegram.messenger.R.string.ChannelMessageRound, r9[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachRound", org.telegram.messenger.R.string.AttachRound);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x1912, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", org.telegram.messenger.R.string.ChannelMessageVideo, r9[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachVideo", org.telegram.messenger.R.string.AttachVideo);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x192f, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", org.telegram.messenger.R.string.ChannelMessagePhoto, r9[0]);
        r1 = org.telegram.messenger.LocaleController.getString("AttachPhoto", org.telegram.messenger.R.string.AttachPhoto);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x194c, code lost:
        r12 = r17;
        r2 = r25;
        r5 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", org.telegram.messenger.R.string.ChannelMessageNoText, r9[0]);
        r1 = org.telegram.messenger.LocaleController.getString("Message", org.telegram.messenger.R.string.Message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x1968, code lost:
        r18 = r1;
        r1 = r5;
        r5 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x196e, code lost:
        r12 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", org.telegram.messenger.R.string.NotificationMessageAlbum, r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x1982, code lost:
        r5 = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x1983, code lost:
        r18 = null;
        r25 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x1989, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", org.telegram.messenger.R.string.NotificationMessageFew, r9[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x19b1, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", org.telegram.messenger.R.string.NotificationMessageFew, r9[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x19d7, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", org.telegram.messenger.R.string.NotificationMessageFew, r9[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x19fd, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", org.telegram.messenger.R.string.NotificationMessageFew, r9[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x1a24, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", org.telegram.messenger.R.string.NotificationMessageForwardFew, r9[0], org.telegram.messenger.LocaleController.formatPluralString(r26, org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x1a4f, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", org.telegram.messenger.R.string.NotificationMessageInvoice, r9[0], r9[1]);
        r6 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", org.telegram.messenger.R.string.PaymentInvoice);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x1a72, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", org.telegram.messenger.R.string.NotificationMessageGameScored, r9[0], r9[1], r9[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x1a92, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", org.telegram.messenger.R.string.NotificationMessageGame, r9[0], r9[1]);
        r6 = org.telegram.messenger.LocaleController.getString("AttachGame", org.telegram.messenger.R.string.AttachGame);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x1ab5, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", org.telegram.messenger.R.string.NotificationMessageGif, r9[0]);
        r6 = org.telegram.messenger.LocaleController.getString("AttachGif", org.telegram.messenger.R.string.AttachGif);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x1ad3, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", org.telegram.messenger.R.string.NotificationMessageLiveLocation, r9[0]);
        r6 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", org.telegram.messenger.R.string.AttachLiveLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x1af1, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", org.telegram.messenger.R.string.NotificationMessageMap, r9[0]);
        r6 = org.telegram.messenger.LocaleController.getString("AttachLocation", org.telegram.messenger.R.string.AttachLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x1b0f, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", org.telegram.messenger.R.string.NotificationMessagePoll2, r9[0], r9[1]);
        r6 = org.telegram.messenger.LocaleController.getString("Poll", org.telegram.messenger.R.string.Poll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x1b32, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", org.telegram.messenger.R.string.NotificationMessageQuiz2, r9[0], r9[1]);
        r6 = org.telegram.messenger.LocaleController.getString("QuizPoll", org.telegram.messenger.R.string.QuizPoll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x1b55, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", org.telegram.messenger.R.string.NotificationMessageContact2, r9[0], r9[1]);
        r6 = org.telegram.messenger.LocaleController.getString("AttachContact", org.telegram.messenger.R.string.AttachContact);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:862:0x1b78, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", org.telegram.messenger.R.string.NotificationMessageAudio, r9[0]);
        r6 = org.telegram.messenger.LocaleController.getString("AttachAudio", org.telegram.messenger.R.string.AttachAudio);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x1b96, code lost:
        r5 = r17;
        r2 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x1b9c, code lost:
        if (r9.length <= 1) goto L_0x1bd7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:866:0x1ba4, code lost:
        if (android.text.TextUtils.isEmpty(r9[1]) != false) goto L_0x1bd7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x1ba6, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", org.telegram.messenger.R.string.NotificationMessageStickerEmoji, r9[0], r9[1]);
        r6 = r9[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x1bd7, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", org.telegram.messenger.R.string.NotificationMessageSticker, r9[0]);
        r6 = org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x1bef, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", org.telegram.messenger.R.string.NotificationMessageDocument, r9[0]);
        r6 = org.telegram.messenger.LocaleController.getString("AttachDocument", org.telegram.messenger.R.string.AttachDocument);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x1c0d, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", org.telegram.messenger.R.string.NotificationMessageRound, r9[0]);
        r6 = org.telegram.messenger.LocaleController.getString("AttachRound", org.telegram.messenger.R.string.AttachRound);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x1c2b, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", org.telegram.messenger.R.string.ActionTakeScreenshoot).replace("un1", r9[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x1CLASSNAME, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", org.telegram.messenger.R.string.NotificationMessageSDVideo, r9[0]);
        r6 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", org.telegram.messenger.R.string.AttachDestructingVideo);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x1CLASSNAME, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", org.telegram.messenger.R.string.NotificationMessageVideo, r9[0]);
        r6 = org.telegram.messenger.LocaleController.getString("AttachVideo", org.telegram.messenger.R.string.AttachVideo);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x1c7e, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", org.telegram.messenger.R.string.NotificationMessageSDPhoto, r9[0]);
        r6 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", org.telegram.messenger.R.string.AttachDestructingPhoto);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x1c9b, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", org.telegram.messenger.R.string.NotificationMessagePhoto, r9[0]);
        r6 = org.telegram.messenger.LocaleController.getString("AttachPhoto", org.telegram.messenger.R.string.AttachPhoto);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x1cb8, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", org.telegram.messenger.R.string.NotificationMessageNoText, r9[0]);
        r6 = org.telegram.messenger.LocaleController.getString("Message", org.telegram.messenger.R.string.Message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x1cd5, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", org.telegram.messenger.R.string.NotificationMessageText, r9[0], r9[1]);
        r6 = r9[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x1cf0, code lost:
        r18 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x1cf3, code lost:
        r5 = r17;
        r2 = r25;
        r1 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRecurringPay", org.telegram.messenger.R.string.NotificationMessageRecurringPay, r9[0], r9[1]);
        r6 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", org.telegram.messenger.R.string.PaymentInvoice);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x1d15, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1d2b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x1d17, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x1d2b, code lost:
        r1 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x1d2d, code lost:
        r34 = r1;
        r47 = r5;
        r44 = r8;
        r45 = r11;
        r31 = r13;
        r5 = "CHAT_REACT_";
        r2 = r25;
        r1 = getReactedText(r14, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x1d3e, code lost:
        r18 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x1d40, code lost:
        r25 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x1d42, code lost:
        if (r1 == null) goto L_0x1e34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x1d44, code lost:
        r6 = new org.telegram.tgnet.TLRPC$TL_message();
        r6.id = r10;
        r6.random_id = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x1d4f, code lost:
        if (r18 == null) goto L_0x1d54;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x1d51, code lost:
        r7 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x1d54, code lost:
        r7 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x1d55, code lost:
        r6.message = r7;
        r6.date = (int) (r53 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x1d5e, code lost:
        if (r41 == false) goto L_0x1d67;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x1d60, code lost:
        r6.action = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x1d67, code lost:
        if (r40 == false) goto L_0x1d70;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x1d69, code lost:
        r6.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:896:0x1d70, code lost:
        r6.dialog_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x1d76, code lost:
        if (r47 == 0) goto L_0x1d86;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1d78, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r6.peer_id = r3;
        r3.channel_id = r47;
        r12 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:0x1d8a, code lost:
        if (r23 == 0) goto L_0x1d98;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1d8c, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.peer_id = r3;
        r12 = r23;
        r3.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1d98, code lost:
        r12 = r23;
        r3 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.peer_id = r3;
        r3.user_id = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:903:0x1da5, code lost:
        r6.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x1daf, code lost:
        if (r35 == 0) goto L_0x1dbb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1db1, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r6.from_id = r3;
        r3.chat_id = r12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:0x1dbf, code lost:
        if (r45 == 0) goto L_0x1dcd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x1dc1, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r6.from_id = r3;
        r3.channel_id = r45;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:0x1dd1, code lost:
        if (r37 == 0) goto L_0x1ddf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:0x1dd3, code lost:
        r3 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r6.from_id = r3;
        r3.user_id = r37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:912:0x1ddf, code lost:
        r6.from_id = r6.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:913:0x1de3, code lost:
        if (r39 != false) goto L_0x1dea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x1de5, code lost:
        if (r41 == false) goto L_0x1de8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x1de8, code lost:
        r3 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:917:0x1dea, code lost:
        r3 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:918:0x1deb, code lost:
        r6.mentioned = r3;
        r6.silent = r16;
        r6.from_scheduled = r2;
        r19 = new org.telegram.messenger.MessageObject(r29, r6, r1, r42, r44, r25, r31, r40, r43);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:919:0x1e10, code lost:
        if (r14.startsWith(r34) != false) goto L_0x1e1b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1e16, code lost:
        if (r14.startsWith(r5) == false) goto L_0x1e19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x1e19, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1e1b, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x1e1c, code lost:
        r19.isReactionPush = r1;
        r1 = new java.util.ArrayList();
        r1.add(r19);
        org.telegram.messenger.NotificationsController.getInstance(r29).processNewMessages(r1, true, true, countDownLatch);
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x1e32, code lost:
        r30 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1e34, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:0x1e35, code lost:
        if (r8 == false) goto L_0x1e3c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x1e37, code lost:
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x1e3c, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29);
        org.telegram.tgnet.ConnectionsManager.getInstance(r29).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x1e48, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x1e49, code lost:
        r1 = r0;
        r5 = r14;
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x1e4f, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x1e50, code lost:
        r30 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:0x1e53, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:0x1e54, code lost:
        r30 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x1e56, code lost:
        r14 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x1e57, code lost:
        r1 = r0;
        r5 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x1e59, code lost:
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:940:0x1e5d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:941:0x1e5e, code lost:
        r29 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x1e62, code lost:
        r29 = r4;
        r30 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:0x1e6b, code lost:
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda0(r4));
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1e78, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x1e79, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x1e7a, code lost:
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x1e7e, code lost:
        r30 = r7;
        r14 = r9;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda1(r4));
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1e8e, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x1e8f, code lost:
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
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x1ed8, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x1ed9, code lost:
        r30 = r7;
        r14 = r9;
        r1 = r11.getInt("dc");
        r2 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x1ef0, code lost:
        if (r2.length == 2) goto L_0x1ef8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x1ef2, code lost:
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x1ef7, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x1ef8, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).applyDatacenterAddress(r1, r2[0], java.lang.Integer.parseInt(r2[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).resumeNetworkMaybe();
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1var_, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:961:0x1var_, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:962:0x1var_, code lost:
        r1 = r0;
        r5 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:963:0x1var_, code lost:
        r7 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:992:?, code lost:
        return;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:976:0x1f3d  */
    /* JADX WARNING: Removed duplicated region for block: B:977:0x1f4d  */
    /* JADX WARNING: Removed duplicated region for block: B:980:0x1var_  */
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
            byte[] r6 = android.util.Base64.decode(r6, r4)     // Catch:{ all -> 0x1var_ }
            org.telegram.tgnet.NativeByteBuffer r7 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ all -> 0x1var_ }
            int r8 = r6.length     // Catch:{ all -> 0x1var_ }
            r7.<init>((int) r8)     // Catch:{ all -> 0x1var_ }
            r7.writeBytes((byte[]) r6)     // Catch:{ all -> 0x1var_ }
            r8 = 0
            r7.position(r8)     // Catch:{ all -> 0x1var_ }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1var_ }
            if (r9 != 0) goto L_0x0046
            byte[] r9 = new byte[r4]     // Catch:{ all -> 0x1var_ }
            org.telegram.messenger.SharedConfig.pushAuthKeyId = r9     // Catch:{ all -> 0x1var_ }
            byte[] r9 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1var_ }
            byte[] r9 = org.telegram.messenger.Utilities.computeSHA1((byte[]) r9)     // Catch:{ all -> 0x1var_ }
            int r10 = r9.length     // Catch:{ all -> 0x1var_ }
            int r10 = r10 - r4
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1var_ }
            java.lang.System.arraycopy(r9, r10, r11, r8, r4)     // Catch:{ all -> 0x1var_ }
        L_0x0046:
            byte[] r9 = new byte[r4]     // Catch:{ all -> 0x1var_ }
            r10 = 1
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1var_ }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1var_ }
            boolean r11 = java.util.Arrays.equals(r11, r9)     // Catch:{ all -> 0x1var_ }
            r12 = 3
            r13 = 2
            if (r11 != 0) goto L_0x0090
            onDecryptError()     // Catch:{ all -> 0x1var_ }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1var_ }
            if (r2 == 0) goto L_0x008f
            java.util.Locale r2 = java.util.Locale.US     // Catch:{ all -> 0x1var_ }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x1var_ }
            r4.<init>()     // Catch:{ all -> 0x1var_ }
            r4.append(r1)     // Catch:{ all -> 0x1var_ }
            java.lang.String r1 = " DECRYPT ERROR 2 k1=%s k2=%s, key=%s"
            r4.append(r1)     // Catch:{ all -> 0x1var_ }
            java.lang.String r1 = r4.toString()     // Catch:{ all -> 0x1var_ }
            java.lang.Object[] r4 = new java.lang.Object[r12]     // Catch:{ all -> 0x1var_ }
            byte[] r6 = org.telegram.messenger.SharedConfig.pushAuthKeyId     // Catch:{ all -> 0x1var_ }
            java.lang.String r6 = org.telegram.messenger.Utilities.bytesToHex(r6)     // Catch:{ all -> 0x1var_ }
            r4[r8] = r6     // Catch:{ all -> 0x1var_ }
            java.lang.String r6 = org.telegram.messenger.Utilities.bytesToHex(r9)     // Catch:{ all -> 0x1var_ }
            r4[r10] = r6     // Catch:{ all -> 0x1var_ }
            byte[] r6 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1var_ }
            java.lang.String r6 = org.telegram.messenger.Utilities.bytesToHex(r6)     // Catch:{ all -> 0x1var_ }
            r4[r13] = r6     // Catch:{ all -> 0x1var_ }
            java.lang.String r1 = java.lang.String.format(r2, r1, r4)     // Catch:{ all -> 0x1var_ }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x1var_ }
        L_0x008f:
            return
        L_0x0090:
            r9 = 16
            byte[] r9 = new byte[r9]     // Catch:{ all -> 0x1var_ }
            r7.readBytes(r9, r10)     // Catch:{ all -> 0x1var_ }
            byte[] r11 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1var_ }
            org.telegram.messenger.MessageKeyData r11 = org.telegram.messenger.MessageKeyData.generateMessageKeyData(r11, r9, r10, r13)     // Catch:{ all -> 0x1var_ }
            java.nio.ByteBuffer r14 = r7.buffer     // Catch:{ all -> 0x1var_ }
            byte[] r15 = r11.aesKey     // Catch:{ all -> 0x1var_ }
            byte[] r11 = r11.aesIv     // Catch:{ all -> 0x1var_ }
            r17 = 0
            r18 = 0
            r19 = 24
            int r6 = r6.length     // Catch:{ all -> 0x1var_ }
            int r20 = r6 + -24
            r16 = r11
            org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ all -> 0x1var_ }
            byte[] r21 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1var_ }
            r22 = 96
            r23 = 32
            java.nio.ByteBuffer r6 = r7.buffer     // Catch:{ all -> 0x1var_ }
            r25 = 24
            int r26 = r6.limit()     // Catch:{ all -> 0x1var_ }
            r24 = r6
            byte[] r6 = org.telegram.messenger.Utilities.computeSHA256(r21, r22, r23, r24, r25, r26)     // Catch:{ all -> 0x1var_ }
            boolean r6 = org.telegram.messenger.Utilities.arraysEquals(r9, r8, r6, r4)     // Catch:{ all -> 0x1var_ }
            if (r6 != 0) goto L_0x00f5
            onDecryptError()     // Catch:{ all -> 0x1var_ }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1var_ }
            if (r2 == 0) goto L_0x00f4
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x1var_ }
            r2.<init>()     // Catch:{ all -> 0x1var_ }
            r2.append(r1)     // Catch:{ all -> 0x1var_ }
            java.lang.String r1 = " DECRYPT ERROR 3, key = %s"
            r2.append(r1)     // Catch:{ all -> 0x1var_ }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x1var_ }
            java.lang.Object[] r2 = new java.lang.Object[r10]     // Catch:{ all -> 0x1var_ }
            byte[] r4 = org.telegram.messenger.SharedConfig.pushAuthKey     // Catch:{ all -> 0x1var_ }
            java.lang.String r4 = org.telegram.messenger.Utilities.bytesToHex(r4)     // Catch:{ all -> 0x1var_ }
            r2[r8] = r4     // Catch:{ all -> 0x1var_ }
            java.lang.String r1 = java.lang.String.format(r1, r2)     // Catch:{ all -> 0x1var_ }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x1var_ }
        L_0x00f4:
            return
        L_0x00f5:
            int r6 = r7.readInt32(r10)     // Catch:{ all -> 0x1var_ }
            byte[] r6 = new byte[r6]     // Catch:{ all -> 0x1var_ }
            r7.readBytes(r6, r10)     // Catch:{ all -> 0x1var_ }
            java.lang.String r7 = new java.lang.String     // Catch:{ all -> 0x1var_ }
            r7.<init>(r6)     // Catch:{ all -> 0x1var_ }
            org.json.JSONObject r6 = new org.json.JSONObject     // Catch:{ all -> 0x1f2d }
            r6.<init>(r7)     // Catch:{ all -> 0x1f2d }
            java.lang.String r9 = "loc_key"
            boolean r9 = r6.has(r9)     // Catch:{ all -> 0x1f2d }
            if (r9 == 0) goto L_0x011a
            java.lang.String r9 = "loc_key"
            java.lang.String r9 = r6.getString(r9)     // Catch:{ all -> 0x0117 }
            goto L_0x011c
        L_0x0117:
            r0 = move-exception
            goto L_0x1var_
        L_0x011a:
            java.lang.String r9 = ""
        L_0x011c:
            java.lang.String r11 = "custom"
            java.lang.Object r11 = r6.get(r11)     // Catch:{ all -> 0x1var_ }
            boolean r11 = r11 instanceof org.json.JSONObject     // Catch:{ all -> 0x1var_ }
            if (r11 == 0) goto L_0x0132
            java.lang.String r11 = "custom"
            org.json.JSONObject r11 = r6.getJSONObject(r11)     // Catch:{ all -> 0x012d }
            goto L_0x0137
        L_0x012d:
            r0 = move-exception
            r1 = r0
            r5 = r9
            goto L_0x1f2a
        L_0x0132:
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ all -> 0x1var_ }
            r11.<init>()     // Catch:{ all -> 0x1var_ }
        L_0x0137:
            java.lang.String r14 = "user_id"
            boolean r14 = r6.has(r14)     // Catch:{ all -> 0x1var_ }
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
            boolean r15 = r14 instanceof java.lang.Long     // Catch:{ all -> 0x1var_ }
            if (r15 == 0) goto L_0x015f
            java.lang.Long r14 = (java.lang.Long) r14     // Catch:{ all -> 0x012d }
            long r14 = r14.longValue()     // Catch:{ all -> 0x012d }
            goto L_0x0184
        L_0x015f:
            boolean r15 = r14 instanceof java.lang.Integer     // Catch:{ all -> 0x1var_ }
            if (r15 == 0) goto L_0x016b
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ all -> 0x012d }
            int r14 = r14.intValue()     // Catch:{ all -> 0x012d }
        L_0x0169:
            long r14 = (long) r14
            goto L_0x0184
        L_0x016b:
            boolean r15 = r14 instanceof java.lang.String     // Catch:{ all -> 0x1var_ }
            if (r15 == 0) goto L_0x017a
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x012d }
            java.lang.Integer r14 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14)     // Catch:{ all -> 0x012d }
            int r14 = r14.intValue()     // Catch:{ all -> 0x012d }
            goto L_0x0169
        L_0x017a:
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1var_ }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x1var_ }
            long r14 = r14.getClientUserId()     // Catch:{ all -> 0x1var_ }
        L_0x0184:
            int r16 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1var_ }
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
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x1f1c }
            boolean r14 = r14.isClientActivated()     // Catch:{ all -> 0x1f1c }
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
            goto L_0x1var_
        L_0x01eb:
            int r14 = r9.hashCode()     // Catch:{ all -> 0x1f1c }
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
            if (r14 == 0) goto L_0x1ed9
            if (r14 == r10) goto L_0x1e8f
            if (r14 == r13) goto L_0x1e7e
            if (r14 == r12) goto L_0x1e62
            java.lang.String r14 = "channel_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1e5d }
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
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1e53 }
            if (r14 == 0) goto L_0x025a
            java.lang.String r3 = "from_id"
            long r3 = r11.getLong(r3)     // Catch:{ all -> 0x0255 }
            r30 = r3
            goto L_0x025c
        L_0x0255:
            r0 = move-exception
            r1 = r0
            r5 = r9
            goto L_0x1e59
        L_0x025a:
            r30 = r12
        L_0x025c:
            java.lang.String r14 = "chat_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1e53 }
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
            goto L_0x1e56
        L_0x0275:
            r14 = r9
            r8 = r12
        L_0x0277:
            java.lang.String r15 = "encryption_id"
            boolean r15 = r11.has(r15)     // Catch:{ all -> 0x1e4f }
            if (r15 == 0) goto L_0x028e
            java.lang.String r3 = "encryption_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x028b }
            long r3 = (long) r3     // Catch:{ all -> 0x028b }
            long r3 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r3)     // Catch:{ all -> 0x028b }
            goto L_0x028e
        L_0x028b:
            r0 = move-exception
            goto L_0x1e57
        L_0x028e:
            java.lang.String r15 = "schedule"
            boolean r15 = r11.has(r15)     // Catch:{ all -> 0x1e4f }
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
            if (r10 == 0) goto L_0x1e32
            java.lang.String r10 = "READ_HISTORY"
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x1e4f }
            java.lang.String r12 = " for dialogId = "
            if (r10 == 0) goto L_0x0337
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
            if (r1 == 0) goto L_0x02ff
            org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox r1 = new org.telegram.tgnet.TLRPC$TL_updateReadChannelInbox     // Catch:{ all -> 0x028b }
            r1.<init>()     // Catch:{ all -> 0x028b }
            r1.channel_id = r5     // Catch:{ all -> 0x028b }
            r1.max_id = r2     // Catch:{ all -> 0x028b }
            r2 = 0
            r1.still_unread_count = r2     // Catch:{ all -> 0x028b }
            r10.add(r1)     // Catch:{ all -> 0x028b }
            goto L_0x0324
        L_0x02ff:
            org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox r1 = new org.telegram.tgnet.TLRPC$TL_updateReadHistoryInbox     // Catch:{ all -> 0x028b }
            r1.<init>()     // Catch:{ all -> 0x028b }
            r3 = r30
            r5 = 0
            int r11 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r11 == 0) goto L_0x0316
            org.telegram.tgnet.TLRPC$TL_peerUser r5 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x028b }
            r5.<init>()     // Catch:{ all -> 0x028b }
            r1.peer = r5     // Catch:{ all -> 0x028b }
            r5.user_id = r3     // Catch:{ all -> 0x028b }
            goto L_0x031f
        L_0x0316:
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x028b }
            r3.<init>()     // Catch:{ all -> 0x028b }
            r1.peer = r3     // Catch:{ all -> 0x028b }
            r3.chat_id = r8     // Catch:{ all -> 0x028b }
        L_0x031f:
            r1.max_id = r2     // Catch:{ all -> 0x028b }
            r10.add(r1)     // Catch:{ all -> 0x028b }
        L_0x0324:
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r29)     // Catch:{ all -> 0x028b }
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r16 = r10
            r15.processUpdateArray(r16, r17, r18, r19, r20)     // Catch:{ all -> 0x028b }
            goto L_0x1e32
        L_0x0337:
            r32 = r30
            java.lang.String r10 = "MESSAGE_DELETED"
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x1e4f }
            java.lang.String r13 = ","
            r30 = r7
            java.lang.String r7 = "messages"
            if (r10 == 0) goto L_0x03b1
            java.lang.String r2 = r11.getString(r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String[] r2 = r2.split(r13)     // Catch:{ all -> 0x1e48 }
            androidx.collection.LongSparseArray r7 = new androidx.collection.LongSparseArray     // Catch:{ all -> 0x1e48 }
            r7.<init>()     // Catch:{ all -> 0x1e48 }
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ all -> 0x1e48 }
            r8.<init>()     // Catch:{ all -> 0x1e48 }
            r9 = 0
        L_0x035a:
            int r10 = r2.length     // Catch:{ all -> 0x1e48 }
            if (r9 >= r10) goto L_0x0369
            r10 = r2[r9]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1e48 }
            r8.add(r10)     // Catch:{ all -> 0x1e48 }
            int r9 = r9 + 1
            goto L_0x035a
        L_0x0369:
            long r9 = -r5
            r7.put(r9, r8)     // Catch:{ all -> 0x1e48 }
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r29)     // Catch:{ all -> 0x1e48 }
            r9 = 0
            r2.removeDeletedMessagesFromNotifications(r7, r9)     // Catch:{ all -> 0x1e48 }
            org.telegram.messenger.MessagesController r20 = org.telegram.messenger.MessagesController.getInstance(r29)     // Catch:{ all -> 0x1e48 }
            r21 = r3
            r23 = r8
            r24 = r5
            r20.deleteMessagesByPush(r21, r23, r24)     // Catch:{ all -> 0x1e48 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x1e34
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x1e48 }
            r2.<init>()     // Catch:{ all -> 0x1e48 }
            r2.append(r1)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = " received "
            r2.append(r1)     // Catch:{ all -> 0x1e48 }
            r2.append(r14)     // Catch:{ all -> 0x1e48 }
            r2.append(r12)     // Catch:{ all -> 0x1e48 }
            r2.append(r3)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = " mids = "
            r2.append(r1)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = android.text.TextUtils.join(r13, r8)     // Catch:{ all -> 0x1e48 }
            r2.append(r1)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x1e48 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x1e48 }
            goto L_0x1e34
        L_0x03b1:
            java.lang.String r10 = "READ_REACTION"
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x1e48 }
            if (r10 == 0) goto L_0x042e
            java.lang.String r2 = r11.getString(r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String[] r2 = r2.split(r13)     // Catch:{ all -> 0x1e48 }
            androidx.collection.LongSparseArray r7 = new androidx.collection.LongSparseArray     // Catch:{ all -> 0x1e48 }
            r7.<init>()     // Catch:{ all -> 0x1e48 }
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ all -> 0x1e48 }
            r8.<init>()     // Catch:{ all -> 0x1e48 }
            android.util.SparseBooleanArray r9 = new android.util.SparseBooleanArray     // Catch:{ all -> 0x1e48 }
            r9.<init>()     // Catch:{ all -> 0x1e48 }
            r10 = 0
        L_0x03d1:
            int r11 = r2.length     // Catch:{ all -> 0x1e48 }
            if (r10 >= r11) goto L_0x03ec
            r11 = r2[r10]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r11)     // Catch:{ all -> 0x1e48 }
            int r11 = r11.intValue()     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r15 = java.lang.Integer.valueOf(r11)     // Catch:{ all -> 0x1e48 }
            r8.add(r15)     // Catch:{ all -> 0x1e48 }
            r15 = 0
            r9.put(r11, r15)     // Catch:{ all -> 0x1e48 }
            int r10 = r10 + 1
            goto L_0x03d1
        L_0x03ec:
            long r5 = -r5
            r7.put(r5, r8)     // Catch:{ all -> 0x1e48 }
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r29)     // Catch:{ all -> 0x1e48 }
            r5 = 1
            r2.removeDeletedMessagesFromNotifications(r7, r5)     // Catch:{ all -> 0x1e48 }
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r29)     // Catch:{ all -> 0x1e48 }
            r2.checkUnreadReactions(r3, r9)     // Catch:{ all -> 0x1e48 }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x1e34
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x1e48 }
            r2.<init>()     // Catch:{ all -> 0x1e48 }
            r2.append(r1)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = " received "
            r2.append(r1)     // Catch:{ all -> 0x1e48 }
            r2.append(r14)     // Catch:{ all -> 0x1e48 }
            r2.append(r12)     // Catch:{ all -> 0x1e48 }
            r2.append(r3)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = " mids = "
            r2.append(r1)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = android.text.TextUtils.join(r13, r8)     // Catch:{ all -> 0x1e48 }
            r2.append(r1)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x1e48 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x1e48 }
            goto L_0x1e34
        L_0x042e:
            boolean r10 = android.text.TextUtils.isEmpty(r14)     // Catch:{ all -> 0x1e48 }
            if (r10 != 0) goto L_0x1e34
            java.lang.String r10 = "msg_id"
            boolean r10 = r11.has(r10)     // Catch:{ all -> 0x1e48 }
            if (r10 == 0) goto L_0x0443
            java.lang.String r10 = "msg_id"
            int r10 = r11.getInt(r10)     // Catch:{ all -> 0x1e48 }
            goto L_0x0444
        L_0x0443:
            r10 = 0
        L_0x0444:
            java.lang.String r13 = "random_id"
            boolean r13 = r11.has(r13)     // Catch:{ all -> 0x1e48 }
            if (r13 == 0) goto L_0x0461
            java.lang.String r13 = "random_id"
            java.lang.String r13 = r11.getString(r13)     // Catch:{ all -> 0x1e48 }
            java.lang.Long r13 = org.telegram.messenger.Utilities.parseLong(r13)     // Catch:{ all -> 0x1e48 }
            long r23 = r13.longValue()     // Catch:{ all -> 0x1e48 }
            r49 = r8
            r8 = r23
            r23 = r49
            goto L_0x0465
        L_0x0461:
            r23 = r8
            r8 = 0
        L_0x0465:
            if (r10 == 0) goto L_0x04a4
            org.telegram.messenger.MessagesController r13 = org.telegram.messenger.MessagesController.getInstance(r29)     // Catch:{ all -> 0x1e48 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r13 = r13.dialogs_read_inbox_max     // Catch:{ all -> 0x1e48 }
            r25 = r15
            java.lang.Long r15 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x1e48 }
            java.lang.Object r13 = r13.get(r15)     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r13 = (java.lang.Integer) r13     // Catch:{ all -> 0x1e48 }
            if (r13 != 0) goto L_0x0498
            org.telegram.messenger.MessagesStorage r13 = org.telegram.messenger.MessagesStorage.getInstance(r29)     // Catch:{ all -> 0x1e48 }
            r15 = 0
            int r13 = r13.getDialogReadMax(r15, r3)     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)     // Catch:{ all -> 0x1e48 }
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r29)     // Catch:{ all -> 0x1e48 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r15 = r15.dialogs_read_inbox_max     // Catch:{ all -> 0x1e48 }
            r26 = r7
            java.lang.Long r7 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x1e48 }
            r15.put(r7, r13)     // Catch:{ all -> 0x1e48 }
            goto L_0x049a
        L_0x0498:
            r26 = r7
        L_0x049a:
            int r7 = r13.intValue()     // Catch:{ all -> 0x1e48 }
            if (r10 <= r7) goto L_0x04a2
        L_0x04a0:
            r7 = 1
            goto L_0x04b9
        L_0x04a2:
            r7 = 0
            goto L_0x04b9
        L_0x04a4:
            r26 = r7
            r25 = r15
            r21 = 0
            int r7 = (r8 > r21 ? 1 : (r8 == r21 ? 0 : -1))
            if (r7 == 0) goto L_0x04a2
            org.telegram.messenger.MessagesStorage r7 = org.telegram.messenger.MessagesStorage.getInstance(r29)     // Catch:{ all -> 0x1e48 }
            boolean r7 = r7.checkMessageByRandomId(r8)     // Catch:{ all -> 0x1e48 }
            if (r7 != 0) goto L_0x04a2
            goto L_0x04a0
        L_0x04b9:
            boolean r13 = r14.startsWith(r2)     // Catch:{ all -> 0x1e48 }
            java.lang.String r15 = "CHAT_REACT_"
            if (r13 != 0) goto L_0x04c7
            boolean r13 = r14.startsWith(r15)     // Catch:{ all -> 0x1e48 }
            if (r13 == 0) goto L_0x04c8
        L_0x04c7:
            r7 = 1
        L_0x04c8:
            if (r7 == 0) goto L_0x1e34
            java.lang.String r7 = "chat_from_id"
            r27 = r8
            r31 = r12
            r8 = 0
            long r12 = r11.optLong(r7, r8)     // Catch:{ all -> 0x1e48 }
            java.lang.String r7 = "chat_from_broadcast_id"
            r34 = r2
            long r1 = r11.optLong(r7, r8)     // Catch:{ all -> 0x1e48 }
            java.lang.String r7 = "chat_from_group_id"
            long r35 = r11.optLong(r7, r8)     // Catch:{ all -> 0x1e48 }
            int r7 = (r12 > r8 ? 1 : (r12 == r8 ? 0 : -1))
            if (r7 != 0) goto L_0x04ef
            int r7 = (r35 > r8 ? 1 : (r35 == r8 ? 0 : -1))
            if (r7 == 0) goto L_0x04ed
            goto L_0x04ef
        L_0x04ed:
            r7 = 0
            goto L_0x04f0
        L_0x04ef:
            r7 = 1
        L_0x04f0:
            java.lang.String r8 = "mention"
            boolean r8 = r11.has(r8)     // Catch:{ all -> 0x1e48 }
            if (r8 == 0) goto L_0x0502
            java.lang.String r8 = "mention"
            int r8 = r11.getInt(r8)     // Catch:{ all -> 0x1e48 }
            if (r8 == 0) goto L_0x0502
            r8 = 1
            goto L_0x0503
        L_0x0502:
            r8 = 0
        L_0x0503:
            java.lang.String r9 = "silent"
            boolean r9 = r11.has(r9)     // Catch:{ all -> 0x1e48 }
            if (r9 == 0) goto L_0x0517
            java.lang.String r9 = "silent"
            int r9 = r11.getInt(r9)     // Catch:{ all -> 0x1e48 }
            if (r9 == 0) goto L_0x0517
            r37 = r1
            r9 = 1
            goto L_0x051a
        L_0x0517:
            r37 = r1
            r9 = 0
        L_0x051a:
            java.lang.String r1 = "loc_args"
            r2 = r16
            boolean r1 = r2.has(r1)     // Catch:{ all -> 0x1e48 }
            if (r1 == 0) goto L_0x0542
            java.lang.String r1 = "loc_args"
            org.json.JSONArray r1 = r2.getJSONArray(r1)     // Catch:{ all -> 0x1e48 }
            int r2 = r1.length()     // Catch:{ all -> 0x1e48 }
            r16 = r9
            java.lang.String[] r9 = new java.lang.String[r2]     // Catch:{ all -> 0x1e48 }
            r39 = r8
            r8 = 0
        L_0x0535:
            if (r8 >= r2) goto L_0x0540
            java.lang.String r40 = r1.getString(r8)     // Catch:{ all -> 0x1e48 }
            r9[r8] = r40     // Catch:{ all -> 0x1e48 }
            int r8 = r8 + 1
            goto L_0x0535
        L_0x0540:
            r1 = 0
            goto L_0x0548
        L_0x0542:
            r39 = r8
            r16 = r9
            r1 = 0
            r9 = 0
        L_0x0548:
            r2 = r9[r1]     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "edit_date"
            boolean r1 = r11.has(r1)     // Catch:{ all -> 0x1e48 }
            java.lang.String r8 = "CHAT_"
            boolean r8 = r14.startsWith(r8)     // Catch:{ all -> 0x1e48 }
            if (r8 == 0) goto L_0x058d
            boolean r8 = org.telegram.messenger.UserObject.isReplyUser((long) r3)     // Catch:{ all -> 0x1e48 }
            if (r8 == 0) goto L_0x0576
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x1e48 }
            r8.<init>()     // Catch:{ all -> 0x1e48 }
            r8.append(r2)     // Catch:{ all -> 0x1e48 }
            java.lang.String r2 = " @ "
            r8.append(r2)     // Catch:{ all -> 0x1e48 }
            r2 = 1
            r11 = r9[r2]     // Catch:{ all -> 0x1e48 }
            r8.append(r11)     // Catch:{ all -> 0x1e48 }
            java.lang.String r2 = r8.toString()     // Catch:{ all -> 0x1e48 }
            goto L_0x05b0
        L_0x0576:
            r21 = 0
            int r8 = (r5 > r21 ? 1 : (r5 == r21 ? 0 : -1))
            if (r8 == 0) goto L_0x057e
            r8 = 1
            goto L_0x057f
        L_0x057e:
            r8 = 0
        L_0x057f:
            r11 = 1
            r40 = r9[r11]     // Catch:{ all -> 0x1e48 }
            r11 = 0
            r41 = 0
            r49 = r8
            r8 = r2
            r2 = r40
            r40 = r49
            goto L_0x05b6
        L_0x058d:
            java.lang.String r8 = "PINNED_"
            boolean r8 = r14.startsWith(r8)     // Catch:{ all -> 0x1e48 }
            if (r8 == 0) goto L_0x05a5
            r21 = 0
            int r8 = (r5 > r21 ? 1 : (r5 == r21 ? 0 : -1))
            if (r8 == 0) goto L_0x059d
            r8 = 1
            goto L_0x059e
        L_0x059d:
            r8 = 0
        L_0x059e:
            r40 = r8
            r8 = 0
            r11 = 0
            r41 = 1
            goto L_0x05b6
        L_0x05a5:
            java.lang.String r8 = "CHANNEL_"
            boolean r8 = r14.startsWith(r8)     // Catch:{ all -> 0x1e48 }
            if (r8 == 0) goto L_0x05b0
            r8 = 0
            r11 = 1
            goto L_0x05b2
        L_0x05b0:
            r8 = 0
            r11 = 0
        L_0x05b2:
            r40 = 0
            r41 = 0
        L_0x05b6:
            boolean r42 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1e48 }
            if (r42 == 0) goto L_0x05ef
            r42 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x1e48 }
            r2.<init>()     // Catch:{ all -> 0x1e48 }
            r43 = r1
            r1 = r51
            r49 = r12
            r13 = r11
            r11 = r37
            r37 = r49
            r2.append(r1)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = " received message notification "
            r2.append(r1)     // Catch:{ all -> 0x1e48 }
            r2.append(r14)     // Catch:{ all -> 0x1e48 }
            r1 = r31
            r2.append(r1)     // Catch:{ all -> 0x1e48 }
            r2.append(r3)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = " mid = "
            r2.append(r1)     // Catch:{ all -> 0x1e48 }
            r2.append(r10)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = r2.toString()     // Catch:{ all -> 0x1e48 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ all -> 0x1e48 }
            goto L_0x05fa
        L_0x05ef:
            r43 = r1
            r42 = r2
            r49 = r12
            r13 = r11
            r11 = r37
            r37 = r49
        L_0x05fa:
            r1 = r34
            boolean r2 = r14.startsWith(r1)     // Catch:{ all -> 0x1e48 }
            if (r2 != 0) goto L_0x1d2d
            boolean r2 = r14.startsWith(r15)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x060a
            goto L_0x1d2d
        L_0x060a:
            int r2 = r14.hashCode()     // Catch:{ all -> 0x1e48 }
            switch(r2) {
                case -2100047043: goto L_0x0b66;
                case -2091498420: goto L_0x0b5b;
                case -2053872415: goto L_0x0b50;
                case -2039746363: goto L_0x0b45;
                case -2023218804: goto L_0x0b3a;
                case -1979538588: goto L_0x0b2f;
                case -1979536003: goto L_0x0b24;
                case -1979535888: goto L_0x0b19;
                case -1969004705: goto L_0x0b0e;
                case -1946699248: goto L_0x0b03;
                case -1717283471: goto L_0x0af7;
                case -1646640058: goto L_0x0aeb;
                case -1528047021: goto L_0x0adf;
                case -1507149394: goto L_0x0ad2;
                case -1493579426: goto L_0x0ac6;
                case -1482481933: goto L_0x0aba;
                case -1480102982: goto L_0x0aad;
                case -1478041834: goto L_0x0aa1;
                case -1474543101: goto L_0x0a96;
                case -1465695932: goto L_0x0a8a;
                case -1374906292: goto L_0x0a7e;
                case -1372940586: goto L_0x0a72;
                case -1264245338: goto L_0x0a66;
                case -1236154001: goto L_0x0a5a;
                case -1236086700: goto L_0x0a4e;
                case -1236077786: goto L_0x0a42;
                case -1235796237: goto L_0x0a36;
                case -1235760759: goto L_0x0a2a;
                case -1235686303: goto L_0x0a1d;
                case -1198046100: goto L_0x0a12;
                case -1124254527: goto L_0x0a06;
                case -1085137927: goto L_0x09fa;
                case -1084856378: goto L_0x09ee;
                case -1084820900: goto L_0x09e2;
                case -1084746444: goto L_0x09d6;
                case -819729482: goto L_0x09ca;
                case -772141857: goto L_0x09be;
                case -638310039: goto L_0x09b2;
                case -590403924: goto L_0x09a6;
                case -589196239: goto L_0x099a;
                case -589193654: goto L_0x098e;
                case -589193539: goto L_0x0982;
                case -440169325: goto L_0x0976;
                case -412748110: goto L_0x096a;
                case -228518075: goto L_0x095e;
                case -213586509: goto L_0x0952;
                case -115582002: goto L_0x0946;
                case -112621464: goto L_0x093a;
                case -108522133: goto L_0x092e;
                case -107572034: goto L_0x0920;
                case -40534265: goto L_0x0914;
                case 52369421: goto L_0x0908;
                case 65254746: goto L_0x08fc;
                case 141040782: goto L_0x08f0;
                case 202550149: goto L_0x08e4;
                case 309993049: goto L_0x08d8;
                case 309995634: goto L_0x08cc;
                case 309995749: goto L_0x08c0;
                case 320532812: goto L_0x08b4;
                case 328933854: goto L_0x08a8;
                case 331340546: goto L_0x089c;
                case 342406591: goto L_0x0890;
                case 344816990: goto L_0x0884;
                case 346878138: goto L_0x0878;
                case 350376871: goto L_0x086c;
                case 608430149: goto L_0x0860;
                case 615714517: goto L_0x0855;
                case 715508879: goto L_0x0849;
                case 728985323: goto L_0x083d;
                case 731046471: goto L_0x0831;
                case 734545204: goto L_0x0825;
                case 802032552: goto L_0x0819;
                case 991498806: goto L_0x080d;
                case 1007364121: goto L_0x0801;
                case 1019850010: goto L_0x07f5;
                case 1019917311: goto L_0x07e9;
                case 1019926225: goto L_0x07dd;
                case 1020207774: goto L_0x07d1;
                case 1020243252: goto L_0x07c5;
                case 1020317708: goto L_0x07b9;
                case 1060282259: goto L_0x07ad;
                case 1060349560: goto L_0x07a1;
                case 1060358474: goto L_0x0795;
                case 1060640023: goto L_0x0789;
                case 1060675501: goto L_0x077d;
                case 1060749957: goto L_0x0770;
                case 1073049781: goto L_0x0764;
                case 1078101399: goto L_0x0758;
                case 1110103437: goto L_0x074c;
                case 1160762272: goto L_0x0740;
                case 1172918249: goto L_0x0734;
                case 1234591620: goto L_0x0728;
                case 1281128640: goto L_0x071c;
                case 1281131225: goto L_0x0710;
                case 1281131340: goto L_0x0704;
                case 1310789062: goto L_0x06f7;
                case 1333118583: goto L_0x06eb;
                case 1361447897: goto L_0x06df;
                case 1498266155: goto L_0x06d3;
                case 1533804208: goto L_0x06c7;
                case 1540131626: goto L_0x06bb;
                case 1547988151: goto L_0x06af;
                case 1561464595: goto L_0x06a3;
                case 1563525743: goto L_0x0697;
                case 1567024476: goto L_0x068b;
                case 1810705077: goto L_0x067f;
                case 1815177512: goto L_0x0673;
                case 1954774321: goto L_0x0667;
                case 1963241394: goto L_0x065b;
                case 2014789757: goto L_0x064f;
                case 2022049433: goto L_0x0643;
                case 2034984710: goto L_0x0637;
                case 2048733346: goto L_0x062b;
                case 2099392181: goto L_0x061f;
                case 2140162142: goto L_0x0613;
                default: goto L_0x0611;
            }     // Catch:{ all -> 0x1e48 }
        L_0x0611:
            goto L_0x0b73
        L_0x0613:
            java.lang.String r2 = "CHAT_MESSAGE_GEOLIVE"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 61
            goto L_0x0b70
        L_0x061f:
            java.lang.String r2 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 44
            goto L_0x0b70
        L_0x062b:
            java.lang.String r2 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 29
            goto L_0x0b70
        L_0x0637:
            java.lang.String r2 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 46
            goto L_0x0b70
        L_0x0643:
            java.lang.String r2 = "PINNED_CONTACT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 95
            goto L_0x0b70
        L_0x064f:
            java.lang.String r2 = "CHAT_PHOTO_EDITED"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 69
            goto L_0x0b70
        L_0x065b:
            java.lang.String r2 = "LOCKED_MESSAGE"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 109(0x6d, float:1.53E-43)
            goto L_0x0b70
        L_0x0667:
            java.lang.String r2 = "CHAT_MESSAGE_PLAYLIST"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 84
            goto L_0x0b70
        L_0x0673:
            java.lang.String r2 = "CHANNEL_MESSAGES"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 48
            goto L_0x0b70
        L_0x067f:
            java.lang.String r2 = "MESSAGE_INVOICE"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 22
            goto L_0x0b70
        L_0x068b:
            java.lang.String r2 = "CHAT_MESSAGE_VIDEO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 52
            goto L_0x0b70
        L_0x0697:
            java.lang.String r2 = "CHAT_MESSAGE_ROUND"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 53
            goto L_0x0b70
        L_0x06a3:
            java.lang.String r2 = "CHAT_MESSAGE_PHOTO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 51
            goto L_0x0b70
        L_0x06af:
            java.lang.String r2 = "CHAT_MESSAGE_AUDIO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 56
            goto L_0x0b70
        L_0x06bb:
            java.lang.String r2 = "MESSAGE_PLAYLIST"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 26
            goto L_0x0b70
        L_0x06c7:
            java.lang.String r2 = "MESSAGE_VIDEOS"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 25
            goto L_0x0b70
        L_0x06d3:
            java.lang.String r2 = "PHONE_CALL_MISSED"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 114(0x72, float:1.6E-43)
            goto L_0x0b70
        L_0x06df:
            java.lang.String r2 = "MESSAGE_PHOTOS"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 24
            goto L_0x0b70
        L_0x06eb:
            java.lang.String r2 = "CHAT_MESSAGE_VIDEOS"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 83
            goto L_0x0b70
        L_0x06f7:
            java.lang.String r2 = "MESSAGE_NOTEXT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r17 = r15
            r2 = 3
            goto L_0x0b76
        L_0x0704:
            java.lang.String r2 = "MESSAGE_GIF"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 18
            goto L_0x0b70
        L_0x0710:
            java.lang.String r2 = "MESSAGE_GEO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 16
            goto L_0x0b70
        L_0x071c:
            java.lang.String r2 = "MESSAGE_DOC"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 10
            goto L_0x0b70
        L_0x0728:
            java.lang.String r2 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 64
            goto L_0x0b70
        L_0x0734:
            java.lang.String r2 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 40
            goto L_0x0b70
        L_0x0740:
            java.lang.String r2 = "CHAT_MESSAGE_PHOTOS"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 82
            goto L_0x0b70
        L_0x074c:
            java.lang.String r2 = "CHAT_MESSAGE_NOTEXT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 50
            goto L_0x0b70
        L_0x0758:
            java.lang.String r2 = "CHAT_TITLE_EDITED"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 68
            goto L_0x0b70
        L_0x0764:
            java.lang.String r2 = "PINNED_NOTEXT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 88
            goto L_0x0b70
        L_0x0770:
            java.lang.String r2 = "MESSAGE_TEXT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r17 = r15
            r2 = 1
            goto L_0x0b76
        L_0x077d:
            java.lang.String r2 = "MESSAGE_QUIZ"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 14
            goto L_0x0b70
        L_0x0789:
            java.lang.String r2 = "MESSAGE_POLL"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 15
            goto L_0x0b70
        L_0x0795:
            java.lang.String r2 = "MESSAGE_GAME"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 19
            goto L_0x0b70
        L_0x07a1:
            java.lang.String r2 = "MESSAGE_FWDS"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 23
            goto L_0x0b70
        L_0x07ad:
            java.lang.String r2 = "MESSAGE_DOCS"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 27
            goto L_0x0b70
        L_0x07b9:
            java.lang.String r2 = "CHAT_MESSAGE_TEXT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 49
            goto L_0x0b70
        L_0x07c5:
            java.lang.String r2 = "CHAT_MESSAGE_QUIZ"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 58
            goto L_0x0b70
        L_0x07d1:
            java.lang.String r2 = "CHAT_MESSAGE_POLL"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 59
            goto L_0x0b70
        L_0x07dd:
            java.lang.String r2 = "CHAT_MESSAGE_GAME"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 63
            goto L_0x0b70
        L_0x07e9:
            java.lang.String r2 = "CHAT_MESSAGE_FWDS"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 81
            goto L_0x0b70
        L_0x07f5:
            java.lang.String r2 = "CHAT_MESSAGE_DOCS"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 85
            goto L_0x0b70
        L_0x0801:
            java.lang.String r2 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 21
            goto L_0x0b70
        L_0x080d:
            java.lang.String r2 = "PINNED_GEOLIVE"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 99
            goto L_0x0b70
        L_0x0819:
            java.lang.String r2 = "MESSAGE_CONTACT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 13
            goto L_0x0b70
        L_0x0825:
            java.lang.String r2 = "PINNED_VIDEO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 90
            goto L_0x0b70
        L_0x0831:
            java.lang.String r2 = "PINNED_ROUND"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 91
            goto L_0x0b70
        L_0x083d:
            java.lang.String r2 = "PINNED_PHOTO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 89
            goto L_0x0b70
        L_0x0849:
            java.lang.String r2 = "PINNED_AUDIO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 94
            goto L_0x0b70
        L_0x0855:
            java.lang.String r2 = "MESSAGE_PHOTO_SECRET"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 5
            goto L_0x0b70
        L_0x0860:
            java.lang.String r2 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 74
            goto L_0x0b70
        L_0x086c:
            java.lang.String r2 = "CHANNEL_MESSAGE_VIDEO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 31
            goto L_0x0b70
        L_0x0878:
            java.lang.String r2 = "CHANNEL_MESSAGE_ROUND"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 32
            goto L_0x0b70
        L_0x0884:
            java.lang.String r2 = "CHANNEL_MESSAGE_PHOTO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 30
            goto L_0x0b70
        L_0x0890:
            java.lang.String r2 = "CHAT_VOICECHAT_END"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 73
            goto L_0x0b70
        L_0x089c:
            java.lang.String r2 = "CHANNEL_MESSAGE_AUDIO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 35
            goto L_0x0b70
        L_0x08a8:
            java.lang.String r2 = "CHAT_MESSAGE_STICKER"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 55
            goto L_0x0b70
        L_0x08b4:
            java.lang.String r2 = "MESSAGES"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 28
            goto L_0x0b70
        L_0x08c0:
            java.lang.String r2 = "CHAT_MESSAGE_GIF"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 62
            goto L_0x0b70
        L_0x08cc:
            java.lang.String r2 = "CHAT_MESSAGE_GEO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 60
            goto L_0x0b70
        L_0x08d8:
            java.lang.String r2 = "CHAT_MESSAGE_DOC"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 54
            goto L_0x0b70
        L_0x08e4:
            java.lang.String r2 = "CHAT_VOICECHAT_INVITE"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 72
            goto L_0x0b70
        L_0x08f0:
            java.lang.String r2 = "CHAT_LEFT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 77
            goto L_0x0b70
        L_0x08fc:
            java.lang.String r2 = "CHAT_ADD_YOU"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 67
            goto L_0x0b70
        L_0x0908:
            java.lang.String r2 = "REACT_TEXT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 105(0x69, float:1.47E-43)
            goto L_0x0b70
        L_0x0914:
            java.lang.String r2 = "CHAT_DELETE_MEMBER"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 75
            goto L_0x0b70
        L_0x0920:
            java.lang.String r2 = "MESSAGE_SCREENSHOT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r17 = r15
            r2 = 8
            goto L_0x0b76
        L_0x092e:
            java.lang.String r2 = "AUTH_REGION"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 108(0x6c, float:1.51E-43)
            goto L_0x0b70
        L_0x093a:
            java.lang.String r2 = "CONTACT_JOINED"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 106(0x6a, float:1.49E-43)
            goto L_0x0b70
        L_0x0946:
            java.lang.String r2 = "CHAT_MESSAGE_INVOICE"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 65
            goto L_0x0b70
        L_0x0952:
            java.lang.String r2 = "ENCRYPTION_REQUEST"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 110(0x6e, float:1.54E-43)
            goto L_0x0b70
        L_0x095e:
            java.lang.String r2 = "MESSAGE_GEOLIVE"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 17
            goto L_0x0b70
        L_0x096a:
            java.lang.String r2 = "CHAT_DELETE_YOU"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 76
            goto L_0x0b70
        L_0x0976:
            java.lang.String r2 = "AUTH_UNKNOWN"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 107(0x6b, float:1.5E-43)
            goto L_0x0b70
        L_0x0982:
            java.lang.String r2 = "PINNED_GIF"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 103(0x67, float:1.44E-43)
            goto L_0x0b70
        L_0x098e:
            java.lang.String r2 = "PINNED_GEO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 98
            goto L_0x0b70
        L_0x099a:
            java.lang.String r2 = "PINNED_DOC"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 92
            goto L_0x0b70
        L_0x09a6:
            java.lang.String r2 = "PINNED_GAME_SCORE"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 101(0x65, float:1.42E-43)
            goto L_0x0b70
        L_0x09b2:
            java.lang.String r2 = "CHANNEL_MESSAGE_STICKER"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 34
            goto L_0x0b70
        L_0x09be:
            java.lang.String r2 = "PHONE_CALL_REQUEST"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 112(0x70, float:1.57E-43)
            goto L_0x0b70
        L_0x09ca:
            java.lang.String r2 = "PINNED_STICKER"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 93
            goto L_0x0b70
        L_0x09d6:
            java.lang.String r2 = "PINNED_TEXT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 87
            goto L_0x0b70
        L_0x09e2:
            java.lang.String r2 = "PINNED_QUIZ"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 96
            goto L_0x0b70
        L_0x09ee:
            java.lang.String r2 = "PINNED_POLL"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 97
            goto L_0x0b70
        L_0x09fa:
            java.lang.String r2 = "PINNED_GAME"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 100
            goto L_0x0b70
        L_0x0a06:
            java.lang.String r2 = "CHAT_MESSAGE_CONTACT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 57
            goto L_0x0b70
        L_0x0a12:
            java.lang.String r2 = "MESSAGE_VIDEO_SECRET"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 7
            goto L_0x0b70
        L_0x0a1d:
            java.lang.String r2 = "CHANNEL_MESSAGE_TEXT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r17 = r15
            r2 = 2
            goto L_0x0b76
        L_0x0a2a:
            java.lang.String r2 = "CHANNEL_MESSAGE_QUIZ"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 37
            goto L_0x0b70
        L_0x0a36:
            java.lang.String r2 = "CHANNEL_MESSAGE_POLL"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 38
            goto L_0x0b70
        L_0x0a42:
            java.lang.String r2 = "CHANNEL_MESSAGE_GAME"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 42
            goto L_0x0b70
        L_0x0a4e:
            java.lang.String r2 = "CHANNEL_MESSAGE_FWDS"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 43
            goto L_0x0b70
        L_0x0a5a:
            java.lang.String r2 = "CHANNEL_MESSAGE_DOCS"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 47
            goto L_0x0b70
        L_0x0a66:
            java.lang.String r2 = "PINNED_INVOICE"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 102(0x66, float:1.43E-43)
            goto L_0x0b70
        L_0x0a72:
            java.lang.String r2 = "CHAT_RETURNED"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 78
            goto L_0x0b70
        L_0x0a7e:
            java.lang.String r2 = "ENCRYPTED_MESSAGE"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 104(0x68, float:1.46E-43)
            goto L_0x0b70
        L_0x0a8a:
            java.lang.String r2 = "ENCRYPTION_ACCEPT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 111(0x6f, float:1.56E-43)
            goto L_0x0b70
        L_0x0a96:
            java.lang.String r2 = "MESSAGE_VIDEO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 6
            goto L_0x0b70
        L_0x0aa1:
            java.lang.String r2 = "MESSAGE_ROUND"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 9
            goto L_0x0b70
        L_0x0aad:
            java.lang.String r2 = "MESSAGE_PHOTO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r17 = r15
            r2 = 4
            goto L_0x0b76
        L_0x0aba:
            java.lang.String r2 = "MESSAGE_MUTED"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 113(0x71, float:1.58E-43)
            goto L_0x0b70
        L_0x0ac6:
            java.lang.String r2 = "MESSAGE_AUDIO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 12
            goto L_0x0b70
        L_0x0ad2:
            java.lang.String r2 = "MESSAGE_RECURRING_PAY"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r17 = r15
            r2 = 0
            goto L_0x0b76
        L_0x0adf:
            java.lang.String r2 = "CHAT_MESSAGES"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 86
            goto L_0x0b70
        L_0x0aeb:
            java.lang.String r2 = "CHAT_VOICECHAT_START"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 71
            goto L_0x0b70
        L_0x0af7:
            java.lang.String r2 = "CHAT_REQ_JOINED"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 80
            goto L_0x0b70
        L_0x0b03:
            java.lang.String r2 = "CHAT_JOINED"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 79
            goto L_0x0b70
        L_0x0b0e:
            java.lang.String r2 = "CHAT_ADD_MEMBER"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 70
            goto L_0x0b70
        L_0x0b19:
            java.lang.String r2 = "CHANNEL_MESSAGE_GIF"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 41
            goto L_0x0b70
        L_0x0b24:
            java.lang.String r2 = "CHANNEL_MESSAGE_GEO"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 39
            goto L_0x0b70
        L_0x0b2f:
            java.lang.String r2 = "CHANNEL_MESSAGE_DOC"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 33
            goto L_0x0b70
        L_0x0b3a:
            java.lang.String r2 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 45
            goto L_0x0b70
        L_0x0b45:
            java.lang.String r2 = "MESSAGE_STICKER"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 11
            goto L_0x0b70
        L_0x0b50:
            java.lang.String r2 = "CHAT_CREATED"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 66
            goto L_0x0b70
        L_0x0b5b:
            java.lang.String r2 = "CHANNEL_MESSAGE_CONTACT"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 36
            goto L_0x0b70
        L_0x0b66:
            java.lang.String r2 = "MESSAGE_GAME_SCORE"
            boolean r2 = r14.equals(r2)     // Catch:{ all -> 0x1e48 }
            if (r2 == 0) goto L_0x0b73
            r2 = 20
        L_0x0b70:
            r17 = r15
            goto L_0x0b76
        L_0x0b73:
            r17 = r15
            r2 = -1
        L_0x0b76:
            java.lang.String r15 = "MusicFiles"
            r34 = r1
            java.lang.String r1 = "Videos"
            r31 = r13
            java.lang.String r13 = "Photos"
            r44 = r8
            java.lang.String r8 = " "
            r45 = r11
            java.lang.String r11 = "NotificationGroupFew"
            java.lang.String r12 = "NotificationMessageFew"
            r47 = r5
            java.lang.String r5 = "ChannelMessageFew"
            java.lang.String r6 = "AttachSticker"
            switch(r2) {
                case 0: goto L_0x1cf3;
                case 1: goto L_0x1cd5;
                case 2: goto L_0x1cd5;
                case 3: goto L_0x1cb8;
                case 4: goto L_0x1c9b;
                case 5: goto L_0x1c7e;
                case 6: goto L_0x1CLASSNAME;
                case 7: goto L_0x1CLASSNAME;
                case 8: goto L_0x1c2b;
                case 9: goto L_0x1c0d;
                case 10: goto L_0x1bef;
                case 11: goto L_0x1b96;
                case 12: goto L_0x1b78;
                case 13: goto L_0x1b55;
                case 14: goto L_0x1b32;
                case 15: goto L_0x1b0f;
                case 16: goto L_0x1af1;
                case 17: goto L_0x1ad3;
                case 18: goto L_0x1ab5;
                case 19: goto L_0x1a92;
                case 20: goto L_0x1a72;
                case 21: goto L_0x1a72;
                case 22: goto L_0x1a4f;
                case 23: goto L_0x1a24;
                case 24: goto L_0x19fd;
                case 25: goto L_0x19d7;
                case 26: goto L_0x19b1;
                case 27: goto L_0x1989;
                case 28: goto L_0x196e;
                case 29: goto L_0x194c;
                case 30: goto L_0x192f;
                case 31: goto L_0x1912;
                case 32: goto L_0x18f5;
                case 33: goto L_0x18d7;
                case 34: goto L_0x187e;
                case 35: goto L_0x1860;
                case 36: goto L_0x183d;
                case 37: goto L_0x181a;
                case 38: goto L_0x17f7;
                case 39: goto L_0x17d9;
                case 40: goto L_0x17bb;
                case 41: goto L_0x179d;
                case 42: goto L_0x177f;
                case 43: goto L_0x1751;
                case 44: goto L_0x172a;
                case 45: goto L_0x1703;
                case 46: goto L_0x16dc;
                case 47: goto L_0x16b3;
                case 48: goto L_0x169d;
                case 49: goto L_0x167b;
                case 50: goto L_0x1658;
                case 51: goto L_0x1635;
                case 52: goto L_0x1612;
                case 53: goto L_0x15ef;
                case 54: goto L_0x15cc;
                case 55: goto L_0x1555;
                case 56: goto L_0x1532;
                case 57: goto L_0x150a;
                case 58: goto L_0x14e2;
                case 59: goto L_0x14ba;
                case 60: goto L_0x1497;
                case 61: goto L_0x1474;
                case 62: goto L_0x1451;
                case 63: goto L_0x1429;
                case 64: goto L_0x1405;
                case 65: goto L_0x13dd;
                case 66: goto L_0x13c1;
                case 67: goto L_0x13c1;
                case 68: goto L_0x13a7;
                case 69: goto L_0x138d;
                case 70: goto L_0x136e;
                case 71: goto L_0x1354;
                case 72: goto L_0x1334;
                case 73: goto L_0x1319;
                case 74: goto L_0x12fe;
                case 75: goto L_0x12e3;
                case 76: goto L_0x12c8;
                case 77: goto L_0x12ad;
                case 78: goto L_0x1292;
                case 79: goto L_0x1277;
                case 80: goto L_0x125c;
                case 81: goto L_0x122b;
                case 82: goto L_0x11fe;
                case 83: goto L_0x11d1;
                case 84: goto L_0x11a4;
                case 85: goto L_0x1175;
                case 86: goto L_0x115a;
                case 87: goto L_0x1104;
                case 88: goto L_0x10b8;
                case 89: goto L_0x106c;
                case 90: goto L_0x1020;
                case 91: goto L_0x0fd4;
                case 92: goto L_0x0var_;
                case 93: goto L_0x0ed3;
                case 94: goto L_0x0e87;
                case 95: goto L_0x0e31;
                case 96: goto L_0x0ddb;
                case 97: goto L_0x0d85;
                case 98: goto L_0x0d39;
                case 99: goto L_0x0ced;
                case 100: goto L_0x0ca1;
                case 101: goto L_0x0CLASSNAME;
                case 102: goto L_0x0CLASSNAME;
                case 103: goto L_0x0bb9;
                case 104: goto L_0x0ba1;
                case 105: goto L_0x0b9b;
                case 106: goto L_0x0b9b;
                case 107: goto L_0x0b9b;
                case 108: goto L_0x0b9b;
                case 109: goto L_0x0b9b;
                case 110: goto L_0x0b9b;
                case 111: goto L_0x0b9b;
                case 112: goto L_0x0b9b;
                case 113: goto L_0x0b9b;
                case 114: goto L_0x0b9b;
                default: goto L_0x0b93;
            }
        L_0x0b93:
            r5 = r17
            r2 = r25
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1e48 }
            goto L_0x1d15
        L_0x0b9b:
            r5 = r17
            r2 = r25
            goto L_0x1d2b
        L_0x0ba1:
            java.lang.String r1 = "YouHaveNewMessage"
            int r2 = org.telegram.messenger.R.string.YouHaveNewMessage     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ all -> 0x1e48 }
            java.lang.String r2 = "SecretChatName"
            int r5 = org.telegram.messenger.R.string.SecretChatName     // Catch:{ all -> 0x1e48 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ all -> 0x1e48 }
            r42 = r2
            r5 = r17
            r2 = r25
            goto L_0x1983
        L_0x0bb9:
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 <= 0) goto L_0x0bda
            java.lang.String r1 = "NotificationActionPinnedGifUser"
            int r2 = org.telegram.messenger.R.string.NotificationActionPinnedGifUser     // Catch:{ all -> 0x1e48 }
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r7 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r5[r6] = r7     // Catch:{ all -> 0x1e48 }
            r6 = 1
            r7 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r5[r6] = r7     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)     // Catch:{ all -> 0x1e48 }
            r5 = r17
            r2 = r25
            goto L_0x1d3e
        L_0x0bda:
            r12 = r17
            r2 = r25
            if (r7 == 0) goto L_0x0bf7
            java.lang.String r1 = "NotificationActionPinnedGif"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGif     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0bf7:
            java.lang.String r1 = "NotificationActionPinnedGifChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGifChannel     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0CLASSNAME:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0c2a
            java.lang.String r1 = "NotificationActionPinnedInvoiceUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedInvoiceUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0c2a:
            if (r7 == 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedInvoice"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedInvoice     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0CLASSNAME:
            java.lang.String r1 = "NotificationActionPinnedInvoiceChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedInvoiceChannel     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0CLASSNAME:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0CLASSNAME
            java.lang.String r1 = "NotificationActionPinnedGameScoreUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGameScoreUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0CLASSNAME:
            if (r7 == 0) goto L_0x0c8f
            java.lang.String r1 = "NotificationActionPinnedGameScore"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGameScore     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0c8f:
            java.lang.String r1 = "NotificationActionPinnedGameScoreChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGameScoreChannel     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0ca1:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0cc2
            java.lang.String r1 = "NotificationActionPinnedGameUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGameUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0cc2:
            if (r7 == 0) goto L_0x0cdb
            java.lang.String r1 = "NotificationActionPinnedGame"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGame     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0cdb:
            java.lang.String r1 = "NotificationActionPinnedGameChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGameChannel     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0ced:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0d0e
            java.lang.String r1 = "NotificationActionPinnedGeoLiveUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0d0e:
            if (r7 == 0) goto L_0x0d27
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGeoLive     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0d27:
            java.lang.String r1 = "NotificationActionPinnedGeoLiveChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveChannel     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0d39:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0d5a
            java.lang.String r1 = "NotificationActionPinnedGeoUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGeoUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0d5a:
            if (r7 == 0) goto L_0x0d73
            java.lang.String r1 = "NotificationActionPinnedGeo"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGeo     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0d73:
            java.lang.String r1 = "NotificationActionPinnedGeoChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedGeoChannel     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0d85:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0da6
            java.lang.String r1 = "NotificationActionPinnedPollUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedPollUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0da6:
            if (r7 == 0) goto L_0x0dc4
            java.lang.String r1 = "NotificationActionPinnedPoll2"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedPoll2     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r11 = 1
            r6[r11] = r8     // Catch:{ all -> 0x1e48 }
            r8 = r9[r11]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0dc4:
            java.lang.String r1 = "NotificationActionPinnedPollChannel2"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedPollChannel2     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0ddb:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0dfc
            java.lang.String r1 = "NotificationActionPinnedQuizUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedQuizUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0dfc:
            if (r7 == 0) goto L_0x0e1a
            java.lang.String r1 = "NotificationActionPinnedQuiz2"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedQuiz2     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r11 = 1
            r6[r11] = r8     // Catch:{ all -> 0x1e48 }
            r8 = r9[r11]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0e1a:
            java.lang.String r1 = "NotificationActionPinnedQuizChannel2"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedQuizChannel2     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0e31:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0e52
            java.lang.String r1 = "NotificationActionPinnedContactUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedContactUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0e52:
            if (r7 == 0) goto L_0x0e70
            java.lang.String r1 = "NotificationActionPinnedContact2"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedContact2     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r11 = 1
            r6[r11] = r8     // Catch:{ all -> 0x1e48 }
            r8 = r9[r11]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0e70:
            java.lang.String r1 = "NotificationActionPinnedContactChannel2"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedContactChannel2     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0e87:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0ea8
            java.lang.String r1 = "NotificationActionPinnedVoiceUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedVoiceUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0ea8:
            if (r7 == 0) goto L_0x0ec1
            java.lang.String r1 = "NotificationActionPinnedVoice"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedVoice     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0ec1:
            java.lang.String r1 = "NotificationActionPinnedVoiceChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedVoiceChannel     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0ed3:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0var_
            int r1 = r9.length     // Catch:{ all -> 0x1e48 }
            r5 = 1
            if (r1 <= r5) goto L_0x0var_
            r1 = r9[r5]     // Catch:{ all -> 0x1e48 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x1e48 }
            if (r1 != 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0var_:
            java.lang.String r1 = "NotificationActionPinnedStickerUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedStickerUser     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0var_:
            if (r7 == 0) goto L_0x0var_
            int r1 = r9.length     // Catch:{ all -> 0x1e48 }
            r5 = 2
            if (r1 <= r5) goto L_0x0f3c
            r1 = r9[r5]     // Catch:{ all -> 0x1e48 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x1e48 }
            if (r1 != 0) goto L_0x0f3c
            java.lang.String r1 = "NotificationActionPinnedStickerEmoji"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedStickerEmoji     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r11 = 1
            r6[r11] = r8     // Catch:{ all -> 0x1e48 }
            r8 = r9[r11]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0f3c:
            java.lang.String r1 = "NotificationActionPinnedSticker"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedSticker     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0var_:
            int r1 = r9.length     // Catch:{ all -> 0x1e48 }
            r5 = 1
            if (r1 <= r5) goto L_0x0var_
            r1 = r9[r5]     // Catch:{ all -> 0x1e48 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x1e48 }
            if (r1 != 0) goto L_0x0var_
            java.lang.String r1 = "NotificationActionPinnedStickerEmojiChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiChannel     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0var_:
            java.lang.String r1 = "NotificationActionPinnedStickerChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedStickerChannel     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0var_:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0fa9
            java.lang.String r1 = "NotificationActionPinnedFileUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedFileUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0fa9:
            if (r7 == 0) goto L_0x0fc2
            java.lang.String r1 = "NotificationActionPinnedFile"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedFile     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0fc2:
            java.lang.String r1 = "NotificationActionPinnedFileChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedFileChannel     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0fd4:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x0ff5
            java.lang.String r1 = "NotificationActionPinnedRoundUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedRoundUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x0ff5:
            if (r7 == 0) goto L_0x100e
            java.lang.String r1 = "NotificationActionPinnedRound"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedRound     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x100e:
            java.lang.String r1 = "NotificationActionPinnedRoundChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedRoundChannel     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x1020:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x1041
            java.lang.String r1 = "NotificationActionPinnedVideoUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedVideoUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x1041:
            if (r7 == 0) goto L_0x105a
            java.lang.String r1 = "NotificationActionPinnedVideo"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedVideo     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x105a:
            java.lang.String r1 = "NotificationActionPinnedVideoChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedVideoChannel     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x106c:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x108d
            java.lang.String r1 = "NotificationActionPinnedPhotoUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedPhotoUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x108d:
            if (r7 == 0) goto L_0x10a6
            java.lang.String r1 = "NotificationActionPinnedPhoto"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedPhoto     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x10a6:
            java.lang.String r1 = "NotificationActionPinnedPhotoChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedPhotoChannel     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x10b8:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x10d9
            java.lang.String r1 = "NotificationActionPinnedNoTextUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedNoTextUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x10d9:
            if (r7 == 0) goto L_0x10f2
            java.lang.String r1 = "NotificationActionPinnedNoText"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedNoText     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x10f2:
            java.lang.String r1 = "NotificationActionPinnedNoTextChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x1104:
            r12 = r17
            r2 = r25
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 <= 0) goto L_0x1125
            java.lang.String r1 = "NotificationActionPinnedTextUser"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedTextUser     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x1125:
            if (r7 == 0) goto L_0x1143
            java.lang.String r1 = "NotificationActionPinnedText"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedText     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x1143:
            java.lang.String r1 = "NotificationActionPinnedTextChannel"
            int r5 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x115a:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationGroupAlbum"
            int r5 = org.telegram.messenger.R.string.NotificationGroupAlbum     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1982
        L_0x1175:
            r12 = r17
            r2 = r25
            int r1 = org.telegram.messenger.R.string.NotificationGroupFew     // Catch:{ all -> 0x1e48 }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r7 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r5[r6] = r7     // Catch:{ all -> 0x1e48 }
            r6 = 1
            r7 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r5[r6] = r7     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "Files"
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x1e48 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x1e48 }
            r9 = 0
            java.lang.Object[] r13 = new java.lang.Object[r9]     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r6, r8, r13)     // Catch:{ all -> 0x1e48 }
            r5[r7] = r6     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r11, r1, r5)     // Catch:{ all -> 0x1e48 }
            goto L_0x1982
        L_0x11a4:
            r12 = r17
            r2 = r25
            int r5 = org.telegram.messenger.R.string.NotificationGroupFew     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r1 = 2
            r7 = r9[r1]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r7)     // Catch:{ all -> 0x1e48 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x1e48 }
            r8 = 0
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x1e48 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r15, r7, r9)     // Catch:{ all -> 0x1e48 }
            r6[r1] = r7     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r11, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1982
        L_0x11d1:
            r12 = r17
            r2 = r25
            int r5 = org.telegram.messenger.R.string.NotificationGroupFew     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x1e48 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x1e48 }
            r9 = 0
            java.lang.Object[] r13 = new java.lang.Object[r9]     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r8, r13)     // Catch:{ all -> 0x1e48 }
            r6[r7] = r1     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r11, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1982
        L_0x11fe:
            r12 = r17
            r2 = r25
            int r1 = org.telegram.messenger.R.string.NotificationGroupFew     // Catch:{ all -> 0x1e48 }
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r7 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r5[r6] = r7     // Catch:{ all -> 0x1e48 }
            r6 = 1
            r7 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r5[r6] = r7     // Catch:{ all -> 0x1e48 }
            r6 = 2
            r7 = r9[r6]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r7)     // Catch:{ all -> 0x1e48 }
            int r7 = r7.intValue()     // Catch:{ all -> 0x1e48 }
            r8 = 0
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ all -> 0x1e48 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r13, r7, r9)     // Catch:{ all -> 0x1e48 }
            r5[r6] = r7     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r11, r1, r5)     // Catch:{ all -> 0x1e48 }
            goto L_0x1982
        L_0x122b:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationGroupForwardedFew"
            int r5 = org.telegram.messenger.R.string.NotificationGroupForwardedFew     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r8 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r8)     // Catch:{ all -> 0x1e48 }
            int r8 = r8.intValue()     // Catch:{ all -> 0x1e48 }
            r9 = 0
            java.lang.Object[] r11 = new java.lang.Object[r9]     // Catch:{ all -> 0x1e48 }
            r13 = r26
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r13, r8, r11)     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1982
        L_0x125c:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "UserAcceptedToGroupPushWithGroup"
            int r5 = org.telegram.messenger.R.string.UserAcceptedToGroupPushWithGroup     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x1277:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationGroupAddSelfMega"
            int r5 = org.telegram.messenger.R.string.NotificationGroupAddSelfMega     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x1292:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationGroupAddSelf"
            int r5 = org.telegram.messenger.R.string.NotificationGroupAddSelf     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x12ad:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationGroupLeftMember"
            int r5 = org.telegram.messenger.R.string.NotificationGroupLeftMember     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x12c8:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationGroupKickYou"
            int r5 = org.telegram.messenger.R.string.NotificationGroupKickYou     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x12e3:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationGroupKickMember"
            int r5 = org.telegram.messenger.R.string.NotificationGroupKickMember     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x12fe:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationGroupInvitedYouToCall"
            int r5 = org.telegram.messenger.R.string.NotificationGroupInvitedYouToCall     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x1319:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationGroupEndedCall"
            int r5 = org.telegram.messenger.R.string.NotificationGroupEndedCall     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x1334:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationGroupInvitedToCall"
            int r5 = org.telegram.messenger.R.string.NotificationGroupInvitedToCall     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x1354:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationGroupCreatedCall"
            int r5 = org.telegram.messenger.R.string.NotificationGroupCreatedCall     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x136e:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationGroupAddMember"
            int r5 = org.telegram.messenger.R.string.NotificationGroupAddMember     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x138d:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationEditedGroupPhoto"
            int r5 = org.telegram.messenger.R.string.NotificationEditedGroupPhoto     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x13a7:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationEditedGroupName"
            int r5 = org.telegram.messenger.R.string.NotificationEditedGroupName     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x13c1:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationInvitedToGroup"
            int r5 = org.telegram.messenger.R.string.NotificationInvitedToGroup     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
        L_0x13da:
            r5 = r12
            goto L_0x1d3e
        L_0x13dd:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupInvoice"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupInvoice     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "PaymentInvoice"
            int r6 = org.telegram.messenger.R.string.PaymentInvoice     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x1405:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupGameScored"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupGameScored     // Catch:{ all -> 0x1e48 }
            r6 = 4
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 3
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x13da
        L_0x1429:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupGame"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupGame     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachGame"
            int r6 = org.telegram.messenger.R.string.AttachGame     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x1451:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupGif"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupGif     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachGif"
            int r6 = org.telegram.messenger.R.string.AttachGif     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x1474:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupLiveLocation"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupLiveLocation     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachLiveLocation"
            int r6 = org.telegram.messenger.R.string.AttachLiveLocation     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x1497:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupMap"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupMap     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachLocation"
            int r6 = org.telegram.messenger.R.string.AttachLocation     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x14ba:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupPoll2"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupPoll2     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "Poll"
            int r6 = org.telegram.messenger.R.string.Poll     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x14e2:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupQuiz2"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupQuiz2     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "PollQuiz"
            int r6 = org.telegram.messenger.R.string.PollQuiz     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x150a:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupContact2"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupContact2     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachContact"
            int r6 = org.telegram.messenger.R.string.AttachContact     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x1532:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupAudio"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupAudio     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachAudio"
            int r6 = org.telegram.messenger.R.string.AttachAudio     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x1555:
            r12 = r17
            r2 = r25
            int r1 = r9.length     // Catch:{ all -> 0x1e48 }
            r5 = 2
            if (r1 <= r5) goto L_0x159b
            r1 = r9[r5]     // Catch:{ all -> 0x1e48 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x1e48 }
            if (r1 != 0) goto L_0x159b
            java.lang.String r1 = "NotificationMessageGroupStickerEmoji"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupStickerEmoji     // Catch:{ all -> 0x1e48 }
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r11 = 0
            r13 = r9[r11]     // Catch:{ all -> 0x1e48 }
            r7[r11] = r13     // Catch:{ all -> 0x1e48 }
            r11 = 1
            r13 = r9[r11]     // Catch:{ all -> 0x1e48 }
            r7[r11] = r13     // Catch:{ all -> 0x1e48 }
            r11 = 2
            r13 = r9[r11]     // Catch:{ all -> 0x1e48 }
            r7[r11] = r13     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x1e48 }
            r1.<init>()     // Catch:{ all -> 0x1e48 }
            r7 = r9[r11]     // Catch:{ all -> 0x1e48 }
            r1.append(r7)     // Catch:{ all -> 0x1e48 }
            r1.append(r8)     // Catch:{ all -> 0x1e48 }
            int r7 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            r1.append(r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x159b:
            java.lang.String r1 = "NotificationMessageGroupSticker"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupSticker     // Catch:{ all -> 0x1e48 }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r11 = 0
            r13 = r9[r11]     // Catch:{ all -> 0x1e48 }
            r7[r11] = r13     // Catch:{ all -> 0x1e48 }
            r11 = 1
            r13 = r9[r11]     // Catch:{ all -> 0x1e48 }
            r7[r11] = r13     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x1e48 }
            r1.<init>()     // Catch:{ all -> 0x1e48 }
            r7 = r9[r11]     // Catch:{ all -> 0x1e48 }
            r1.append(r7)     // Catch:{ all -> 0x1e48 }
            r1.append(r8)     // Catch:{ all -> 0x1e48 }
            int r7 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            r1.append(r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x15cc:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupDocument"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupDocument     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachDocument"
            int r6 = org.telegram.messenger.R.string.AttachDocument     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x15ef:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupRound"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupRound     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachRound"
            int r6 = org.telegram.messenger.R.string.AttachRound     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x1612:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupVideo"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupVideo     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachVideo"
            int r6 = org.telegram.messenger.R.string.AttachVideo     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x1635:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupPhoto"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupPhoto     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachPhoto"
            int r6 = org.telegram.messenger.R.string.AttachPhoto     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x1658:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupNoText"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupNoText     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "Message"
            int r6 = org.telegram.messenger.R.string.Message     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x167b:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGroupText"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGroupText     // Catch:{ all -> 0x1e48 }
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 2
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            r1 = r9[r7]     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x169d:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "ChannelMessageAlbum"
            int r5 = org.telegram.messenger.R.string.ChannelMessageAlbum     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1982
        L_0x16b3:
            r12 = r17
            r2 = r25
            int r1 = org.telegram.messenger.R.string.ChannelMessageFew     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r8 = "Files"
            r11 = 1
            r9 = r9[r11]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x1e48 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x1e48 }
            java.lang.Object[] r13 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r8, r9, r13)     // Catch:{ all -> 0x1e48 }
            r6[r11] = r7     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1982
        L_0x16dc:
            r12 = r17
            r2 = r25
            int r6 = org.telegram.messenger.R.string.ChannelMessageFew     // Catch:{ all -> 0x1e48 }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r1[r7] = r8     // Catch:{ all -> 0x1e48 }
            r8 = 1
            r9 = r9[r8]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x1e48 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x1e48 }
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r15, r9, r11)     // Catch:{ all -> 0x1e48 }
            r1[r8] = r7     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r6, r1)     // Catch:{ all -> 0x1e48 }
            goto L_0x1982
        L_0x1703:
            r12 = r17
            r2 = r25
            int r6 = org.telegram.messenger.R.string.ChannelMessageFew     // Catch:{ all -> 0x1e48 }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r8 = 0
            r11 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r11     // Catch:{ all -> 0x1e48 }
            r11 = 1
            r9 = r9[r11]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x1e48 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x1e48 }
            java.lang.Object[] r13 = new java.lang.Object[r8]     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r9, r13)     // Catch:{ all -> 0x1e48 }
            r7[r11] = r1     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1982
        L_0x172a:
            r12 = r17
            r2 = r25
            int r1 = org.telegram.messenger.R.string.ChannelMessageFew     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r8 = 1
            r9 = r9[r8]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x1e48 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x1e48 }
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r13, r9, r11)     // Catch:{ all -> 0x1e48 }
            r6[r8] = r7     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1982
        L_0x1751:
            r12 = r17
            r2 = r25
            int r1 = org.telegram.messenger.R.string.ChannelMessageFew     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r8 = "ForwardedMessageCount"
            r11 = 1
            r9 = r9[r11]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x1e48 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x1e48 }
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r8, r9, r11)     // Catch:{ all -> 0x1e48 }
            java.lang.String r7 = r7.toLowerCase()     // Catch:{ all -> 0x1e48 }
            r8 = 1
            r6[r8] = r7     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1982
        L_0x177f:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGame"
            int r5 = org.telegram.messenger.R.string.NotificationMessageGame     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachGame"
            int r6 = org.telegram.messenger.R.string.AttachGame     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x179d:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "ChannelMessageGIF"
            int r5 = org.telegram.messenger.R.string.ChannelMessageGIF     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachGif"
            int r6 = org.telegram.messenger.R.string.AttachGif     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x17bb:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "ChannelMessageLiveLocation"
            int r5 = org.telegram.messenger.R.string.ChannelMessageLiveLocation     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachLiveLocation"
            int r6 = org.telegram.messenger.R.string.AttachLiveLocation     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x17d9:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "ChannelMessageMap"
            int r5 = org.telegram.messenger.R.string.ChannelMessageMap     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachLocation"
            int r6 = org.telegram.messenger.R.string.AttachLocation     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x17f7:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "ChannelMessagePoll2"
            int r5 = org.telegram.messenger.R.string.ChannelMessagePoll2     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "Poll"
            int r6 = org.telegram.messenger.R.string.Poll     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x181a:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "ChannelMessageQuiz2"
            int r5 = org.telegram.messenger.R.string.ChannelMessageQuiz2     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "QuizPoll"
            int r6 = org.telegram.messenger.R.string.QuizPoll     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x183d:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "ChannelMessageContact2"
            int r5 = org.telegram.messenger.R.string.ChannelMessageContact2     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r7 = 1
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachContact"
            int r6 = org.telegram.messenger.R.string.AttachContact     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x1860:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "ChannelMessageAudio"
            int r5 = org.telegram.messenger.R.string.ChannelMessageAudio     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachAudio"
            int r6 = org.telegram.messenger.R.string.AttachAudio     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x187e:
            r12 = r17
            r2 = r25
            int r1 = r9.length     // Catch:{ all -> 0x1e48 }
            r5 = 1
            if (r1 <= r5) goto L_0x18bf
            r1 = r9[r5]     // Catch:{ all -> 0x1e48 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x1e48 }
            if (r1 != 0) goto L_0x18bf
            java.lang.String r1 = "ChannelMessageStickerEmoji"
            int r5 = org.telegram.messenger.R.string.ChannelMessageStickerEmoji     // Catch:{ all -> 0x1e48 }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r11 = 0
            r13 = r9[r11]     // Catch:{ all -> 0x1e48 }
            r7[r11] = r13     // Catch:{ all -> 0x1e48 }
            r11 = 1
            r13 = r9[r11]     // Catch:{ all -> 0x1e48 }
            r7[r11] = r13     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x1e48 }
            r1.<init>()     // Catch:{ all -> 0x1e48 }
            r7 = r9[r11]     // Catch:{ all -> 0x1e48 }
            r1.append(r7)     // Catch:{ all -> 0x1e48 }
            r1.append(r8)     // Catch:{ all -> 0x1e48 }
            int r7 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            r1.append(r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x18bf:
            java.lang.String r1 = "ChannelMessageSticker"
            int r5 = org.telegram.messenger.R.string.ChannelMessageSticker     // Catch:{ all -> 0x1e48 }
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r9 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r8[r7] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r8)     // Catch:{ all -> 0x1e48 }
            int r1 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r6, r1)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x18d7:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "ChannelMessageDocument"
            int r5 = org.telegram.messenger.R.string.ChannelMessageDocument     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachDocument"
            int r6 = org.telegram.messenger.R.string.AttachDocument     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x18f5:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "ChannelMessageRound"
            int r5 = org.telegram.messenger.R.string.ChannelMessageRound     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachRound"
            int r6 = org.telegram.messenger.R.string.AttachRound     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x1912:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "ChannelMessageVideo"
            int r5 = org.telegram.messenger.R.string.ChannelMessageVideo     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachVideo"
            int r6 = org.telegram.messenger.R.string.AttachVideo     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x192f:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "ChannelMessagePhoto"
            int r5 = org.telegram.messenger.R.string.ChannelMessagePhoto     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "AttachPhoto"
            int r6 = org.telegram.messenger.R.string.AttachPhoto     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1968
        L_0x194c:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "ChannelMessageNoText"
            int r5 = org.telegram.messenger.R.string.ChannelMessageNoText     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = "Message"
            int r6 = org.telegram.messenger.R.string.Message     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
        L_0x1968:
            r18 = r1
            r1 = r5
            r5 = r12
            goto L_0x1d40
        L_0x196e:
            r12 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageAlbum"
            int r5 = org.telegram.messenger.R.string.NotificationMessageAlbum     // Catch:{ all -> 0x1e48 }
            r6 = 1
            java.lang.Object[] r7 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r6 = 0
            r8 = r9[r6]     // Catch:{ all -> 0x1e48 }
            r7[r6] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r5, r7)     // Catch:{ all -> 0x1e48 }
        L_0x1982:
            r5 = r12
        L_0x1983:
            r18 = 0
            r25 = 1
            goto L_0x1d42
        L_0x1989:
            r5 = r17
            r2 = r25
            int r1 = org.telegram.messenger.R.string.NotificationMessageFew     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r8 = "Files"
            r11 = 1
            r9 = r9[r11]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x1e48 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x1e48 }
            java.lang.Object[] r13 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r8, r9, r13)     // Catch:{ all -> 0x1e48 }
            r6[r11] = r7     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r12, r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1983
        L_0x19b1:
            r5 = r17
            r2 = r25
            int r6 = org.telegram.messenger.R.string.NotificationMessageFew     // Catch:{ all -> 0x1e48 }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r1[r7] = r8     // Catch:{ all -> 0x1e48 }
            r8 = 1
            r9 = r9[r8]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x1e48 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x1e48 }
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r15, r9, r11)     // Catch:{ all -> 0x1e48 }
            r1[r8] = r7     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r12, r6, r1)     // Catch:{ all -> 0x1e48 }
            goto L_0x1983
        L_0x19d7:
            r5 = r17
            r2 = r25
            int r6 = org.telegram.messenger.R.string.NotificationMessageFew     // Catch:{ all -> 0x1e48 }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r8 = 0
            r11 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r11     // Catch:{ all -> 0x1e48 }
            r11 = 1
            r9 = r9[r11]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x1e48 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x1e48 }
            java.lang.Object[] r13 = new java.lang.Object[r8]     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r1, r9, r13)     // Catch:{ all -> 0x1e48 }
            r7[r11] = r1     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r12, r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1983
        L_0x19fd:
            r5 = r17
            r2 = r25
            int r1 = org.telegram.messenger.R.string.NotificationMessageFew     // Catch:{ all -> 0x1e48 }
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r6[r7] = r8     // Catch:{ all -> 0x1e48 }
            r8 = 1
            r9 = r9[r8]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x1e48 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x1e48 }
            java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r13, r9, r11)     // Catch:{ all -> 0x1e48 }
            r6[r8] = r7     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r12, r1, r6)     // Catch:{ all -> 0x1e48 }
            goto L_0x1983
        L_0x1a24:
            r5 = r17
            r2 = r25
            r13 = r26
            java.lang.String r1 = "NotificationMessageForwardFew"
            int r6 = org.telegram.messenger.R.string.NotificationMessageForwardFew     // Catch:{ all -> 0x1e48 }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r8 = 0
            r11 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r11     // Catch:{ all -> 0x1e48 }
            r11 = 1
            r9 = r9[r11]     // Catch:{ all -> 0x1e48 }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r9)     // Catch:{ all -> 0x1e48 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x1e48 }
            java.lang.Object[] r12 = new java.lang.Object[r8]     // Catch:{ all -> 0x1e48 }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r13, r9, r12)     // Catch:{ all -> 0x1e48 }
            r7[r11] = r8     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1983
        L_0x1a4f:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageInvoice"
            int r6 = org.telegram.messenger.R.string.NotificationMessageInvoice     // Catch:{ all -> 0x1e48 }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r8 = 0
            r11 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r11     // Catch:{ all -> 0x1e48 }
            r8 = 1
            r9 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "PaymentInvoice"
            int r7 = org.telegram.messenger.R.string.PaymentInvoice     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1a72:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGameScored"
            int r6 = org.telegram.messenger.R.string.NotificationMessageGameScored     // Catch:{ all -> 0x1e48 }
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r8 = 0
            r11 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r11     // Catch:{ all -> 0x1e48 }
            r8 = 1
            r11 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r11     // Catch:{ all -> 0x1e48 }
            r8 = 2
            r9 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1d3e
        L_0x1a92:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGame"
            int r6 = org.telegram.messenger.R.string.NotificationMessageGame     // Catch:{ all -> 0x1e48 }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r8 = 0
            r11 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r11     // Catch:{ all -> 0x1e48 }
            r8 = 1
            r9 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "AttachGame"
            int r7 = org.telegram.messenger.R.string.AttachGame     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1ab5:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageGif"
            int r6 = org.telegram.messenger.R.string.NotificationMessageGif     // Catch:{ all -> 0x1e48 }
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r9 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r8[r7] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r8)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "AttachGif"
            int r7 = org.telegram.messenger.R.string.AttachGif     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1ad3:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageLiveLocation"
            int r6 = org.telegram.messenger.R.string.NotificationMessageLiveLocation     // Catch:{ all -> 0x1e48 }
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r9 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r8[r7] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r8)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "AttachLiveLocation"
            int r7 = org.telegram.messenger.R.string.AttachLiveLocation     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1af1:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageMap"
            int r6 = org.telegram.messenger.R.string.NotificationMessageMap     // Catch:{ all -> 0x1e48 }
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r9 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r8[r7] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r8)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "AttachLocation"
            int r7 = org.telegram.messenger.R.string.AttachLocation     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1b0f:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessagePoll2"
            int r6 = org.telegram.messenger.R.string.NotificationMessagePoll2     // Catch:{ all -> 0x1e48 }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r8 = 0
            r11 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r11     // Catch:{ all -> 0x1e48 }
            r8 = 1
            r9 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "Poll"
            int r7 = org.telegram.messenger.R.string.Poll     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1b32:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageQuiz2"
            int r6 = org.telegram.messenger.R.string.NotificationMessageQuiz2     // Catch:{ all -> 0x1e48 }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r8 = 0
            r11 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r11     // Catch:{ all -> 0x1e48 }
            r8 = 1
            r9 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "QuizPoll"
            int r7 = org.telegram.messenger.R.string.QuizPoll     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1b55:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageContact2"
            int r6 = org.telegram.messenger.R.string.NotificationMessageContact2     // Catch:{ all -> 0x1e48 }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r8 = 0
            r11 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r11     // Catch:{ all -> 0x1e48 }
            r8 = 1
            r9 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "AttachContact"
            int r7 = org.telegram.messenger.R.string.AttachContact     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1b78:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageAudio"
            int r6 = org.telegram.messenger.R.string.NotificationMessageAudio     // Catch:{ all -> 0x1e48 }
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r9 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r8[r7] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r8)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "AttachAudio"
            int r7 = org.telegram.messenger.R.string.AttachAudio     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1b96:
            r5 = r17
            r2 = r25
            int r1 = r9.length     // Catch:{ all -> 0x1e48 }
            r7 = 1
            if (r1 <= r7) goto L_0x1bd7
            r1 = r9[r7]     // Catch:{ all -> 0x1e48 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ all -> 0x1e48 }
            if (r1 != 0) goto L_0x1bd7
            java.lang.String r1 = "NotificationMessageStickerEmoji"
            int r7 = org.telegram.messenger.R.string.NotificationMessageStickerEmoji     // Catch:{ all -> 0x1e48 }
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1e48 }
            r12 = 0
            r13 = r9[r12]     // Catch:{ all -> 0x1e48 }
            r11[r12] = r13     // Catch:{ all -> 0x1e48 }
            r12 = 1
            r13 = r9[r12]     // Catch:{ all -> 0x1e48 }
            r11[r12] = r13     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x1e48 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x1e48 }
            r7.<init>()     // Catch:{ all -> 0x1e48 }
            r9 = r9[r12]     // Catch:{ all -> 0x1e48 }
            r7.append(r9)     // Catch:{ all -> 0x1e48 }
            r7.append(r8)     // Catch:{ all -> 0x1e48 }
            int r8 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r8)     // Catch:{ all -> 0x1e48 }
            r7.append(r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = r7.toString()     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1bd7:
            java.lang.String r1 = "NotificationMessageSticker"
            int r7 = org.telegram.messenger.R.string.NotificationMessageSticker     // Catch:{ all -> 0x1e48 }
            r8 = 1
            java.lang.Object[] r11 = new java.lang.Object[r8]     // Catch:{ all -> 0x1e48 }
            r8 = 0
            r9 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r11[r8] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r7, r11)     // Catch:{ all -> 0x1e48 }
            int r7 = org.telegram.messenger.R.string.AttachSticker     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1bef:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageDocument"
            int r6 = org.telegram.messenger.R.string.NotificationMessageDocument     // Catch:{ all -> 0x1e48 }
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r9 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r8[r7] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r8)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "AttachDocument"
            int r7 = org.telegram.messenger.R.string.AttachDocument     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1c0d:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageRound"
            int r6 = org.telegram.messenger.R.string.NotificationMessageRound     // Catch:{ all -> 0x1e48 }
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r9 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r8[r7] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r8)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "AttachRound"
            int r7 = org.telegram.messenger.R.string.AttachRound     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1c2b:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "ActionTakeScreenshoot"
            int r6 = org.telegram.messenger.R.string.ActionTakeScreenshoot     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r6)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "un1"
            r7 = 0
            r8 = r9[r7]     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = r1.replace(r6, r8)     // Catch:{ all -> 0x1e48 }
            goto L_0x1d3e
        L_0x1CLASSNAME:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageSDVideo"
            int r6 = org.telegram.messenger.R.string.NotificationMessageSDVideo     // Catch:{ all -> 0x1e48 }
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r9 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r8[r7] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r8)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "AttachDestructingVideo"
            int r7 = org.telegram.messenger.R.string.AttachDestructingVideo     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1CLASSNAME:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageVideo"
            int r6 = org.telegram.messenger.R.string.NotificationMessageVideo     // Catch:{ all -> 0x1e48 }
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r9 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r8[r7] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r8)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "AttachVideo"
            int r7 = org.telegram.messenger.R.string.AttachVideo     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1c7e:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageSDPhoto"
            int r6 = org.telegram.messenger.R.string.NotificationMessageSDPhoto     // Catch:{ all -> 0x1e48 }
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r9 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r8[r7] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r8)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "AttachDestructingPhoto"
            int r7 = org.telegram.messenger.R.string.AttachDestructingPhoto     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1c9b:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessagePhoto"
            int r6 = org.telegram.messenger.R.string.NotificationMessagePhoto     // Catch:{ all -> 0x1e48 }
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r9 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r8[r7] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r8)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "AttachPhoto"
            int r7 = org.telegram.messenger.R.string.AttachPhoto     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1cb8:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageNoText"
            int r6 = org.telegram.messenger.R.string.NotificationMessageNoText     // Catch:{ all -> 0x1e48 }
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r7 = 0
            r9 = r9[r7]     // Catch:{ all -> 0x1e48 }
            r8[r7] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r8)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "Message"
            int r7 = org.telegram.messenger.R.string.Message     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1cd5:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageText"
            int r6 = org.telegram.messenger.R.string.NotificationMessageText     // Catch:{ all -> 0x1e48 }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r8 = 0
            r11 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r11     // Catch:{ all -> 0x1e48 }
            r8 = 1
            r11 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r11     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r7)     // Catch:{ all -> 0x1e48 }
            r6 = r9[r8]     // Catch:{ all -> 0x1e48 }
        L_0x1cf0:
            r18 = r6
            goto L_0x1d40
        L_0x1cf3:
            r5 = r17
            r2 = r25
            java.lang.String r1 = "NotificationMessageRecurringPay"
            int r6 = org.telegram.messenger.R.string.NotificationMessageRecurringPay     // Catch:{ all -> 0x1e48 }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1e48 }
            r8 = 0
            r11 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r11     // Catch:{ all -> 0x1e48 }
            r8 = 1
            r9 = r9[r8]     // Catch:{ all -> 0x1e48 }
            r7[r8] = r9     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r6, r7)     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "PaymentInvoice"
            int r7 = org.telegram.messenger.R.string.PaymentInvoice     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r7)     // Catch:{ all -> 0x1e48 }
            goto L_0x1cf0
        L_0x1d15:
            if (r1 == 0) goto L_0x1d2b
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x1e48 }
            r1.<init>()     // Catch:{ all -> 0x1e48 }
            java.lang.String r6 = "unhandled loc_key = "
            r1.append(r6)     // Catch:{ all -> 0x1e48 }
            r1.append(r14)     // Catch:{ all -> 0x1e48 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x1e48 }
            org.telegram.messenger.FileLog.w(r1)     // Catch:{ all -> 0x1e48 }
        L_0x1d2b:
            r1 = 0
            goto L_0x1d3e
        L_0x1d2d:
            r34 = r1
            r47 = r5
            r44 = r8
            r45 = r11
            r31 = r13
            r5 = r15
            r2 = r25
            java.lang.String r1 = getReactedText(r14, r9)     // Catch:{ all -> 0x1e48 }
        L_0x1d3e:
            r18 = 0
        L_0x1d40:
            r25 = 0
        L_0x1d42:
            if (r1 == 0) goto L_0x1e34
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x1e48 }
            r6.<init>()     // Catch:{ all -> 0x1e48 }
            r6.id = r10     // Catch:{ all -> 0x1e48 }
            r7 = r27
            r6.random_id = r7     // Catch:{ all -> 0x1e48 }
            if (r18 == 0) goto L_0x1d54
            r7 = r18
            goto L_0x1d55
        L_0x1d54:
            r7 = r1
        L_0x1d55:
            r6.message = r7     // Catch:{ all -> 0x1e48 }
            r7 = 1000(0x3e8, double:4.94E-321)
            long r7 = r53 / r7
            int r8 = (int) r7     // Catch:{ all -> 0x1e48 }
            r6.date = r8     // Catch:{ all -> 0x1e48 }
            if (r41 == 0) goto L_0x1d67
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r7 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x1e48 }
            r7.<init>()     // Catch:{ all -> 0x1e48 }
            r6.action = r7     // Catch:{ all -> 0x1e48 }
        L_0x1d67:
            if (r40 == 0) goto L_0x1d70
            int r7 = r6.flags     // Catch:{ all -> 0x1e48 }
            r8 = -2147483648(0xfffffffvar_, float:-0.0)
            r7 = r7 | r8
            r6.flags = r7     // Catch:{ all -> 0x1e48 }
        L_0x1d70:
            r6.dialog_id = r3     // Catch:{ all -> 0x1e48 }
            r3 = 0
            int r7 = (r47 > r3 ? 1 : (r47 == r3 ? 0 : -1))
            if (r7 == 0) goto L_0x1d86
            org.telegram.tgnet.TLRPC$TL_peerChannel r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x1e48 }
            r3.<init>()     // Catch:{ all -> 0x1e48 }
            r6.peer_id = r3     // Catch:{ all -> 0x1e48 }
            r12 = r47
            r3.channel_id = r12     // Catch:{ all -> 0x1e48 }
            r12 = r23
            goto L_0x1da5
        L_0x1d86:
            r3 = 0
            int r7 = (r23 > r3 ? 1 : (r23 == r3 ? 0 : -1))
            if (r7 == 0) goto L_0x1d98
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x1e48 }
            r3.<init>()     // Catch:{ all -> 0x1e48 }
            r6.peer_id = r3     // Catch:{ all -> 0x1e48 }
            r12 = r23
            r3.chat_id = r12     // Catch:{ all -> 0x1e48 }
            goto L_0x1da5
        L_0x1d98:
            r12 = r23
            org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1e48 }
            r3.<init>()     // Catch:{ all -> 0x1e48 }
            r6.peer_id = r3     // Catch:{ all -> 0x1e48 }
            r7 = r32
            r3.user_id = r7     // Catch:{ all -> 0x1e48 }
        L_0x1da5:
            int r3 = r6.flags     // Catch:{ all -> 0x1e48 }
            r3 = r3 | 256(0x100, float:3.59E-43)
            r6.flags = r3     // Catch:{ all -> 0x1e48 }
            r3 = 0
            int r7 = (r35 > r3 ? 1 : (r35 == r3 ? 0 : -1))
            if (r7 == 0) goto L_0x1dbb
            org.telegram.tgnet.TLRPC$TL_peerChat r3 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x1e48 }
            r3.<init>()     // Catch:{ all -> 0x1e48 }
            r6.from_id = r3     // Catch:{ all -> 0x1e48 }
            r3.chat_id = r12     // Catch:{ all -> 0x1e48 }
            goto L_0x1de3
        L_0x1dbb:
            r3 = 0
            int r7 = (r45 > r3 ? 1 : (r45 == r3 ? 0 : -1))
            if (r7 == 0) goto L_0x1dcd
            org.telegram.tgnet.TLRPC$TL_peerChannel r3 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x1e48 }
            r3.<init>()     // Catch:{ all -> 0x1e48 }
            r6.from_id = r3     // Catch:{ all -> 0x1e48 }
            r7 = r45
            r3.channel_id = r7     // Catch:{ all -> 0x1e48 }
            goto L_0x1de3
        L_0x1dcd:
            r3 = 0
            int r7 = (r37 > r3 ? 1 : (r37 == r3 ? 0 : -1))
            if (r7 == 0) goto L_0x1ddf
            org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1e48 }
            r3.<init>()     // Catch:{ all -> 0x1e48 }
            r6.from_id = r3     // Catch:{ all -> 0x1e48 }
            r7 = r37
            r3.user_id = r7     // Catch:{ all -> 0x1e48 }
            goto L_0x1de3
        L_0x1ddf:
            org.telegram.tgnet.TLRPC$Peer r3 = r6.peer_id     // Catch:{ all -> 0x1e48 }
            r6.from_id = r3     // Catch:{ all -> 0x1e48 }
        L_0x1de3:
            if (r39 != 0) goto L_0x1dea
            if (r41 == 0) goto L_0x1de8
            goto L_0x1dea
        L_0x1de8:
            r3 = 0
            goto L_0x1deb
        L_0x1dea:
            r3 = 1
        L_0x1deb:
            r6.mentioned = r3     // Catch:{ all -> 0x1e48 }
            r3 = r16
            r6.silent = r3     // Catch:{ all -> 0x1e48 }
            r6.from_scheduled = r2     // Catch:{ all -> 0x1e48 }
            org.telegram.messenger.MessageObject r2 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x1e48 }
            r19 = r2
            r20 = r29
            r21 = r6
            r22 = r1
            r23 = r42
            r24 = r44
            r26 = r31
            r27 = r40
            r28 = r43
            r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27, r28)     // Catch:{ all -> 0x1e48 }
            r1 = r34
            boolean r1 = r14.startsWith(r1)     // Catch:{ all -> 0x1e48 }
            if (r1 != 0) goto L_0x1e1b
            boolean r1 = r14.startsWith(r5)     // Catch:{ all -> 0x1e48 }
            if (r1 == 0) goto L_0x1e19
            goto L_0x1e1b
        L_0x1e19:
            r1 = 0
            goto L_0x1e1c
        L_0x1e1b:
            r1 = 1
        L_0x1e1c:
            r2.isReactionPush = r1     // Catch:{ all -> 0x1e48 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ all -> 0x1e48 }
            r1.<init>()     // Catch:{ all -> 0x1e48 }
            r1.add(r2)     // Catch:{ all -> 0x1e48 }
            org.telegram.messenger.NotificationsController r2 = org.telegram.messenger.NotificationsController.getInstance(r29)     // Catch:{ all -> 0x1e48 }
            java.util.concurrent.CountDownLatch r3 = countDownLatch     // Catch:{ all -> 0x1e48 }
            r4 = 1
            r2.processNewMessages(r1, r4, r4, r3)     // Catch:{ all -> 0x1e48 }
            r8 = 0
            goto L_0x1e35
        L_0x1e32:
            r30 = r7
        L_0x1e34:
            r8 = 1
        L_0x1e35:
            if (r8 == 0) goto L_0x1e3c
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1e48 }
            r1.countDown()     // Catch:{ all -> 0x1e48 }
        L_0x1e3c:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29)     // Catch:{ all -> 0x1e48 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r29)     // Catch:{ all -> 0x1e48 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1e48 }
            goto L_0x1var_
        L_0x1e48:
            r0 = move-exception
            r1 = r0
            r5 = r14
            r4 = r29
            goto L_0x1var_
        L_0x1e4f:
            r0 = move-exception
            r30 = r7
            goto L_0x1e57
        L_0x1e53:
            r0 = move-exception
            r30 = r7
        L_0x1e56:
            r14 = r9
        L_0x1e57:
            r1 = r0
            r5 = r14
        L_0x1e59:
            r4 = r29
            goto L_0x1var_
        L_0x1e5d:
            r0 = move-exception
            r29 = r4
            goto L_0x1f1d
        L_0x1e62:
            r29 = r4
            r30 = r7
            r14 = r9
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1e79 }
            org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda0 r2 = new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda0     // Catch:{ all -> 0x1e79 }
            r4 = r29
            r2.<init>(r4)     // Catch:{ all -> 0x1var_ }
            r1.postRunnable(r2)     // Catch:{ all -> 0x1var_ }
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1var_ }
            r1.countDown()     // Catch:{ all -> 0x1var_ }
            return
        L_0x1e79:
            r0 = move-exception
            r4 = r29
            goto L_0x1var_
        L_0x1e7e:
            r30 = r7
            r14 = r9
            org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda1 r1 = new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda1     // Catch:{ all -> 0x1var_ }
            r1.<init>(r4)     // Catch:{ all -> 0x1var_ }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ all -> 0x1var_ }
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1var_ }
            r1.countDown()     // Catch:{ all -> 0x1var_ }
            return
        L_0x1e8f:
            r2 = r6
            r30 = r7
            r14 = r9
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x1var_ }
            r1.<init>()     // Catch:{ all -> 0x1var_ }
            r3 = 0
            r1.popup = r3     // Catch:{ all -> 0x1var_ }
            r3 = 2
            r1.flags = r3     // Catch:{ all -> 0x1var_ }
            r5 = 1000(0x3e8, double:4.94E-321)
            long r5 = r53 / r5
            int r3 = (int) r5     // Catch:{ all -> 0x1var_ }
            r1.inbox_date = r3     // Catch:{ all -> 0x1var_ }
            java.lang.String r3 = "message"
            java.lang.String r2 = r2.getString(r3)     // Catch:{ all -> 0x1var_ }
            r1.message = r2     // Catch:{ all -> 0x1var_ }
            java.lang.String r2 = "announcement"
            r1.type = r2     // Catch:{ all -> 0x1var_ }
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty     // Catch:{ all -> 0x1var_ }
            r2.<init>()     // Catch:{ all -> 0x1var_ }
            r1.media = r2     // Catch:{ all -> 0x1var_ }
            org.telegram.tgnet.TLRPC$TL_updates r2 = new org.telegram.tgnet.TLRPC$TL_updates     // Catch:{ all -> 0x1var_ }
            r2.<init>()     // Catch:{ all -> 0x1var_ }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Update> r3 = r2.updates     // Catch:{ all -> 0x1var_ }
            r3.add(r1)     // Catch:{ all -> 0x1var_ }
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1var_ }
            org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda3 r3 = new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda3     // Catch:{ all -> 0x1var_ }
            r3.<init>(r4, r2)     // Catch:{ all -> 0x1var_ }
            r1.postRunnable(r3)     // Catch:{ all -> 0x1var_ }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1var_ }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1var_ }
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1var_ }
            r1.countDown()     // Catch:{ all -> 0x1var_ }
            return
        L_0x1ed9:
            r30 = r7
            r14 = r9
            java.lang.String r1 = "dc"
            int r1 = r11.getInt(r1)     // Catch:{ all -> 0x1var_ }
            java.lang.String r2 = "addr"
            java.lang.String r2 = r11.getString(r2)     // Catch:{ all -> 0x1var_ }
            java.lang.String r3 = ":"
            java.lang.String[] r2 = r2.split(r3)     // Catch:{ all -> 0x1var_ }
            int r3 = r2.length     // Catch:{ all -> 0x1var_ }
            r5 = 2
            if (r3 == r5) goto L_0x1ef8
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1var_ }
            r1.countDown()     // Catch:{ all -> 0x1var_ }
            return
        L_0x1ef8:
            r3 = 0
            r3 = r2[r3]     // Catch:{ all -> 0x1var_ }
            r5 = 1
            r2 = r2[r5]     // Catch:{ all -> 0x1var_ }
            int r2 = java.lang.Integer.parseInt(r2)     // Catch:{ all -> 0x1var_ }
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1var_ }
            r5.applyDatacenterAddress(r1, r3, r2)     // Catch:{ all -> 0x1var_ }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)     // Catch:{ all -> 0x1var_ }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1var_ }
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1var_ }
            r1.countDown()     // Catch:{ all -> 0x1var_ }
            return
        L_0x1var_:
            r0 = move-exception
        L_0x1var_:
            r1 = r0
            r5 = r14
        L_0x1var_:
            r7 = r30
            goto L_0x1var_
        L_0x1f1c:
            r0 = move-exception
        L_0x1f1d:
            r30 = r7
            r14 = r9
            r1 = r0
            r5 = r14
        L_0x1var_:
            r2 = -1
            goto L_0x1f3b
        L_0x1var_:
            r0 = move-exception
            r30 = r7
            r14 = r9
            r1 = r0
            r5 = r14
        L_0x1f2a:
            r2 = -1
            r4 = -1
            goto L_0x1f3b
        L_0x1f2d:
            r0 = move-exception
            r30 = r7
        L_0x1var_:
            r1 = r0
            r2 = -1
            r4 = -1
            r5 = 0
            goto L_0x1f3b
        L_0x1var_:
            r0 = move-exception
            r1 = r0
            r2 = -1
            r4 = -1
            r5 = 0
            r7 = 0
        L_0x1f3b:
            if (r4 == r2) goto L_0x1f4d
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r4)
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            r2.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r2 = countDownLatch
            r2.countDown()
            goto L_0x1var_
        L_0x1f4d:
            onDecryptError()
        L_0x1var_:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x1var_
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
        L_0x1var_:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1var_:
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
