package org.telegram.messenger;

import android.os.SystemClock;
import android.text.TextUtils;
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v8, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v19, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v9, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v10, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v21, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v22, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v25, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v28, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v31, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v34, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v37, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v27, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v42, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v45, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v49, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v50, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v5, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v6, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v33, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v54, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v57, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v38, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v60, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v63, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v66, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v44, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v69, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v72, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v48, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v75, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v78, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v4, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v5, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v16, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v6, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v55, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v58, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v44, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v63, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v68, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v48, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v73, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v78, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v82, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v86, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v89, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v92, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v95, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v98, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v102, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v103, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v9, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v10, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v90, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v107, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v110, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v113, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v78, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v116, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v119, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v67, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v122, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v68, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v125, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v128, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v131, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v109, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v113, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v117, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v121, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v115, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v125, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v129, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v10, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v40, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v150, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v153, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v42, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v156, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v108, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v134, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v159, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v110, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v44, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v162, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v112, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v45, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v165, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v133, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v11, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v12, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v140, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v169, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v170, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v13, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v14, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v15, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v143, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v174, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v114, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v46, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v177, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v116, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v17, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v18, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v180, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v118, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v19, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v20, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v183, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v120, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v21, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v22, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v186, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v122, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v47, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v154, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v189, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v124, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v48, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v192, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v126, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v49, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v195, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v128, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v23, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v24, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v198, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v130, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v25, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v26, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v50, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v132, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v27, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v28, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v163, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v202, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v134, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v51, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v165, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v136, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v52, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v166, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v138, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v53, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v167, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v140, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v29, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v30, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v168, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v142, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v54, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v169, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v144, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v31, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v32, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v170, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v146, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v55, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v171, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v56, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v172, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v150, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v57, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v173, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v58, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v174, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v154, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v59, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v175, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v60, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v176, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v158, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v61, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v62, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v178, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v162, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v33, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v34, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v179, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v147, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v148, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v182, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v151, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v152, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v187, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v155, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v156, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v192, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v159, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v160, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v197, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v180, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v69, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v202, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v183, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v70, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v203, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v185, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v35, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v36, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v71, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v205, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v172, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v72, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v207, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v194, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v73, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v177, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v209, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v199, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v74, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v210, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v201, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v75, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v211, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v182, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v212, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v206, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v76, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v208, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v77, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v214, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v187, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v213, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v78, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v216, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v215, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v79, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v217, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v192, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v218, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v80, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v219, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v81, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v220, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v197, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v221, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v242, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v82, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v222, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v229, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v83, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v223, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v249, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v37, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v84, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v224, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v204, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v225, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v256, resolved type: java.lang.StringBuffer} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v85, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v226, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v227, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v86, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v228, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v87, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v229, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v88, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v39, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v89, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v231, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v90, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v232, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v91, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v233, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v41, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v92, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v234, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v93, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v259, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v94, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v236, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v261, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v43, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v95, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v237, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v263, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v96, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v238, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v230, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v239, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v268, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v97, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v270, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v98, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v241, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v235, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v242, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v275, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v99, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v243, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v277, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v100, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v244, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v240, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v282, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v101, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v246, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v284, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v102, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v247, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v245, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v248, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v289, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v103, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v249, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v291, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v104, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v250, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v251, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v296, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v105, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v252, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v298, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v106, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v253, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v254, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v302, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v107, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v255, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v164, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v303, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v256, resolved type: java.lang.Object[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v485, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v486, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v10, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v487, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v264, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v490, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v257, resolved type: java.lang.String[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v258, resolved type: java.lang.String[]} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x021c, code lost:
        if (r14 == 0) goto L_0x1var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x021e, code lost:
        if (r14 == 1) goto L_0x1eea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x0220, code lost:
        if (r14 == 2) goto L_0x1ed9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x0222, code lost:
        if (r14 == 3) goto L_0x1ebd;
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
        r47 = r3;
        r3 = -r3;
        r8 = r47;
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
        if (r3 == 0) goto L_0x1e8d;
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
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1e8d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x0380, code lost:
        org.telegram.messenger.FileLog.d(r1 + " received " + r14 + " for dialogId = " + r3 + " mids = " + android.text.TextUtils.join(",", r9));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x03b1, code lost:
        if (android.text.TextUtils.isEmpty(r14) != false) goto L_0x1e8d;
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
        if (r9 == false) goto L_0x1e8f;
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
        r47 = r11;
        r11 = r9;
        r9 = r10[1];
        r13 = r47;
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
        if (r14.startsWith("REACT_") != false) goto L_0x1d8c;
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
        r44 = r8;
        r45 = r12;
        r46 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0af7, code lost:
        switch(r9) {
            case 0: goto L_0x1d50;
            case 1: goto L_0x1d33;
            case 2: goto L_0x1d33;
            case 3: goto L_0x1d12;
            case 4: goto L_0x1cf3;
            case 5: goto L_0x1cd4;
            case 6: goto L_0x1cb5;
            case 7: goto L_0x1CLASSNAME;
            case 8: goto L_0x1c7d;
            case 9: goto L_0x1c5d;
            case 10: goto L_0x1c3d;
            case 11: goto L_0x1bde;
            case 12: goto L_0x1bbe;
            case 13: goto L_0x1b99;
            case 14: goto L_0x1b74;
            case 15: goto L_0x1b4f;
            case 16: goto L_0x1b2f;
            case 17: goto L_0x1b0f;
            case 18: goto L_0x1aef;
            case 19: goto L_0x1aca;
            case 20: goto L_0x1aa9;
            case 21: goto L_0x1aa9;
            case 22: goto L_0x1a84;
            case 23: goto L_0x1a57;
            case 24: goto L_0x1a2c;
            case 25: goto L_0x1a01;
            case 26: goto L_0x19d7;
            case 27: goto L_0x19ad;
            case 28: goto L_0x1990;
            case 29: goto L_0x196a;
            case 30: goto L_0x194b;
            case 31: goto L_0x192c;
            case 32: goto L_0x190d;
            case 33: goto L_0x18ed;
            case 34: goto L_0x188e;
            case 35: goto L_0x186e;
            case 36: goto L_0x1849;
            case 37: goto L_0x1824;
            case 38: goto L_0x17ff;
            case 39: goto L_0x17df;
            case 40: goto L_0x17bf;
            case 41: goto L_0x179f;
            case 42: goto L_0x177f;
            case 43: goto L_0x1750;
            case 44: goto L_0x1725;
            case 45: goto L_0x16fa;
            case 46: goto L_0x16cf;
            case 47: goto L_0x16a4;
            case 48: goto L_0x168d;
            case 49: goto L_0x166a;
            case 50: goto L_0x1645;
            case 51: goto L_0x1620;
            case 52: goto L_0x15fb;
            case 53: goto L_0x15d6;
            case 54: goto L_0x15b1;
            case 55: goto L_0x1531;
            case 56: goto L_0x150c;
            case 57: goto L_0x14e2;
            case 58: goto L_0x14b8;
            case 59: goto L_0x148e;
            case 60: goto L_0x1469;
            case 61: goto L_0x1444;
            case 62: goto L_0x141f;
            case 63: goto L_0x13f5;
            case 64: goto L_0x13d0;
            case 65: goto L_0x13a6;
            case 66: goto L_0x1388;
            case 67: goto L_0x1388;
            case 68: goto L_0x136d;
            case 69: goto L_0x1352;
            case 70: goto L_0x1332;
            case 71: goto L_0x1316;
            case 72: goto L_0x12f5;
            case 73: goto L_0x12d9;
            case 74: goto L_0x12bd;
            case 75: goto L_0x12a1;
            case 76: goto L_0x1285;
            case 77: goto L_0x1269;
            case 78: goto L_0x124d;
            case 79: goto L_0x1231;
            case 80: goto L_0x1215;
            case 81: goto L_0x11e3;
            case 82: goto L_0x11b2;
            case 83: goto L_0x1181;
            case 84: goto L_0x1150;
            case 85: goto L_0x111f;
            case 86: goto L_0x1103;
            case 87: goto L_0x10aa;
            case 88: goto L_0x105b;
            case 89: goto L_0x100c;
            case 90: goto L_0x0fbd;
            case 91: goto L_0x0f6e;
            case 92: goto L_0x0f1f;
            case 93: goto L_0x0e63;
            case 94: goto L_0x0e14;
            case 95: goto L_0x0dba;
            case 96: goto L_0x0d60;
            case 97: goto L_0x0d06;
            case 98: goto L_0x0cb7;
            case 99: goto L_0x0CLASSNAME;
            case 100: goto L_0x0CLASSNAME;
            case 101: goto L_0x0bca;
            case 102: goto L_0x0b7b;
            case 103: goto L_0x0b27;
            case 104: goto L_0x0b08;
            case 105: goto L_0x0b02;
            case 106: goto L_0x0b02;
            case 107: goto L_0x0b02;
            case 108: goto L_0x0b02;
            case 109: goto L_0x0b02;
            case 110: goto L_0x0b02;
            case 111: goto L_0x0b02;
            case 112: goto L_0x0b02;
            case 113: goto L_0x0b02;
            case 114: goto L_0x0b02;
            default: goto L_0x0afa;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0afa, code lost:
        r2 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0b02, code lost:
        r2 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0b08, code lost:
        r2 = org.telegram.messenger.LocaleController.getString("YouHaveNewMessage", NUM);
        r43 = org.telegram.messenger.LocaleController.getString("SecretChatName", NUM);
        r9 = r25;
        r18 = null;
        r25 = true;
        r7 = r2;
        r2 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0b2b, code lost:
        if (r3 <= 0) goto L_0x0b4a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0b2d, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r10[0], r10[1]);
        r2 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0b4a, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0b4e, code lost:
        if (r7 == false) goto L_0x0b68;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0b50, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0b68, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0b7b, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0b83, code lost:
        if (r3 <= 0) goto L_0x0b9d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0b85, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0b9d, code lost:
        if (r7 == false) goto L_0x0bb7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0b9f, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoice", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0bb7, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedInvoiceChannel", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0bca, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0bd2, code lost:
        if (r3 <= 0) goto L_0x0bec;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0bd4, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x0bec, code lost:
        if (r7 == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0bee, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScore", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameScoreChannel", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0CLASSNAME, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0CLASSNAME, code lost:
        if (r3 <= 0) goto L_0x0c3b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0c3b, code lost:
        if (r7 == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0c3d, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0CLASSNAME, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0CLASSNAME, code lost:
        if (r3 <= 0) goto L_0x0c8a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0CLASSNAME, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0c8a, code lost:
        if (r7 == false) goto L_0x0ca4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0c8c, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x0ca4, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0cb7, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0cbf, code lost:
        if (r3 <= 0) goto L_0x0cd9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0cc1, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0cd9, code lost:
        if (r7 == false) goto L_0x0cf3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0cdb, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0cf3, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0d06, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0d0e, code lost:
        if (r3 <= 0) goto L_0x0d28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0d10, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0d28, code lost:
        if (r7 == false) goto L_0x0d48;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0d2a, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r10[0], r10[2], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0d48, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0d60, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0d68, code lost:
        if (r3 <= 0) goto L_0x0d82;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0d6a, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0d82, code lost:
        if (r7 == false) goto L_0x0da2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0d84, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r10[0], r10[2], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0da2, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0dba, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0dc2, code lost:
        if (r3 <= 0) goto L_0x0ddc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0dc4, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0ddc, code lost:
        if (r7 == false) goto L_0x0dfc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0dde, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r10[0], r10[2], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0dfc, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0e14, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0e1c, code lost:
        if (r3 <= 0) goto L_0x0e36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0e1e, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0e36, code lost:
        if (r7 == false) goto L_0x0e50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0e38, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0e50, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0e63, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0e6b, code lost:
        if (r3 <= 0) goto L_0x0ea4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0e6f, code lost:
        if (r10.length <= 1) goto L_0x0e91;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0e77, code lost:
        if (android.text.TextUtils.isEmpty(r10[1]) != false) goto L_0x0e91;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0e79, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0e91, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0ea4, code lost:
        if (r7 == false) goto L_0x0ee8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0ea8, code lost:
        if (r10.length <= 2) goto L_0x0ed0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0eb0, code lost:
        if (android.text.TextUtils.isEmpty(r10[2]) != false) goto L_0x0ed0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0eb2, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r10[0], r10[2], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0ed0, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0eea, code lost:
        if (r10.length <= 1) goto L_0x0f0c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0ef2, code lost:
        if (android.text.TextUtils.isEmpty(r10[1]) != false) goto L_0x0f0c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0ef4, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0f0c, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0f1f, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0var_, code lost:
        if (r3 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0var_, code lost:
        if (r7 == false) goto L_0x0f5b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0f5b, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0f6e, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0var_, code lost:
        if (r3 <= 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x0var_, code lost:
        if (r7 == false) goto L_0x0faa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0var_, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x0faa, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0fbd, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0fc5, code lost:
        if (r3 <= 0) goto L_0x0fdf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x0fc7, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x0fdf, code lost:
        if (r7 == false) goto L_0x0ff9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x0fe1, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x0ff9, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x100c, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x1014, code lost:
        if (r3 <= 0) goto L_0x102e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x1016, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x102e, code lost:
        if (r7 == false) goto L_0x1048;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x1030, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x1048, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x105b, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x1063, code lost:
        if (r3 <= 0) goto L_0x107d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x1065, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x107d, code lost:
        if (r7 == false) goto L_0x1097;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:765:0x107f, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x1097, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:767:0x10aa, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x10b2, code lost:
        if (r3 <= 0) goto L_0x10cc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x10b4, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x10cc, code lost:
        if (r7 == false) goto L_0x10eb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x10ce, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r10[0], r10[1], r10[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x10eb, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:0x1103, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAlbum", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x111f, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r10[0], r10[1], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:0x1150, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r10[0], r10[1], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x1181, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r10[0], r10[1], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:0x11b2, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupFew", NUM, r10[0], r10[1], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x11e3, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupForwardedFew", NUM, r10[0], r10[1], org.telegram.messenger.LocaleController.formatPluralString(r26, org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[2]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:0x1215, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("UserAcceptedToGroupPushWithGroup", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x1231, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:0x124d, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x1269, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:0x1285, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x12a1, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:0x12bd, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:0x12d9, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupEndedCall", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:0x12f5, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r10[0], r10[1], r10[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:0x1316, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:0x1332, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r10[0], r10[1], r10[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:0x1352, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:0x136d, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:0x1388, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r10[0], r10[1]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:0x13a2, code lost:
        r7 = r2;
        r2 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:0x13a6, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupInvoice", NUM, r10[0], r10[1], r10[2]);
        r7 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:0x13d0, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGameScored", NUM, r10[0], r10[1], r10[2], r10[3]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:0x13f5, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r10[0], r10[1], r10[2]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:0x141f, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r10[0], r10[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:0x1444, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r10[0], r10[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:0x1469, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r10[0], r10[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:0x148e, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r10[0], r10[1], r10[2]);
        r7 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:0x14b8, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r10[0], r10[1], r10[2]);
        r7 = org.telegram.messenger.LocaleController.getString("PollQuiz", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:0x14e2, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r10[0], r10[1], r10[2]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:0x150c, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r10[0], r10[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:0x1531, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:0x1537, code lost:
        if (r10.length <= 2) goto L_0x157c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:0x153f, code lost:
        if (android.text.TextUtils.isEmpty(r10[2]) != false) goto L_0x157c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:0x1541, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r10[0], r10[1], r10[2]);
        r7 = r10[2] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:0x157c, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r10[0], r10[1]);
        r7 = r10[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:0x15b1, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r10[0], r10[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:0x15d6, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r10[0], r10[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:0x15fb, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupVideo", NUM, r10[0], r10[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:0x1620, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r10[0], r10[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:0x1645, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", NUM, r10[0], r10[1]);
        r7 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:0x166a, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r10[0], r10[1], r10[2]);
        r7 = r10[2];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:0x168d, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAlbum", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:0x16a4, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r10[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:0x16cf, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r10[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:0x16fa, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r10[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:0x1725, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r10[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:0x1750, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageFew", NUM, r10[0], org.telegram.messenger.LocaleController.formatPluralString("ForwardedMessageCount", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]).toLowerCase());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:0x177f, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r10[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:0x179f, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r10[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:824:0x17bf, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r10[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:825:0x17df, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r10[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:826:0x17ff, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r10[0], r10[1]);
        r7 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:827:0x1824, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r10[0], r10[1]);
        r7 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:828:0x1849, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r10[0], r10[1]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:829:0x186e, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r10[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:830:0x188e, code lost:
        r8 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:831:0x1894, code lost:
        if (r10.length <= 1) goto L_0x18d3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:833:0x189c, code lost:
        if (android.text.TextUtils.isEmpty(r10[1]) != false) goto L_0x18d3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:834:0x189e, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r10[0], r10[1]);
        r7 = r10[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:835:0x18d3, code lost:
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r10[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:836:0x18ed, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r10[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:837:0x190d, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r10[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:838:0x192c, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r10[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:839:0x194b, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r10[0]);
        r7 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:840:0x196a, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r10[0]);
        r7 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:841:0x1988, code lost:
        r18 = r7;
        r25 = false;
        r7 = r2;
        r2 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:842:0x1990, code lost:
        r8 = r17;
        r9 = r25;
        r2 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAlbum", NUM, r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:843:0x19a5, code lost:
        r7 = r2;
        r2 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:844:0x19a7, code lost:
        r18 = null;
        r25 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:845:0x19ad, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r10[0], org.telegram.messenger.LocaleController.formatPluralString("Files", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:846:0x19d7, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r10[0], org.telegram.messenger.LocaleController.formatPluralString("MusicFiles", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:847:0x1a01, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r10[0], org.telegram.messenger.LocaleController.formatPluralString("Videos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:848:0x1a2c, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageFew", NUM, r10[0], org.telegram.messenger.LocaleController.formatPluralString("Photos", org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:849:0x1a57, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageForwardFew", NUM, r10[0], org.telegram.messenger.LocaleController.formatPluralString(r26, org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10[1]).intValue(), new java.lang.Object[0]));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:850:0x1a84, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageInvoice", NUM, r10[0], r10[1]);
        r8 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:851:0x1aa9, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGameScored", NUM, r10[0], r10[1], r10[2]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:852:0x1aca, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGame", NUM, r10[0], r10[1]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:853:0x1aef, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGif", NUM, r10[0]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:854:0x1b0f, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageLiveLocation", NUM, r10[0]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:855:0x1b2f, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageMap", NUM, r10[0]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:856:0x1b4f, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePoll2", NUM, r10[0], r10[1]);
        r8 = org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:857:0x1b74, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageQuiz2", NUM, r10[0], r10[1]);
        r8 = org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:858:0x1b99, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageContact2", NUM, r10[0], r10[1]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:859:0x1bbe, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageAudio", NUM, r10[0]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:860:0x1bde, code lost:
        r2 = r17;
        r9 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:861:0x1be4, code lost:
        if (r10.length <= 1) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:863:0x1bec, code lost:
        if (android.text.TextUtils.isEmpty(r10[1]) != false) goto L_0x1CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:864:0x1bee, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageStickerEmoji", NUM, r10[0], r10[1]);
        r8 = r10[1] + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:865:0x1CLASSNAME, code lost:
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSticker", NUM, r10[0]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:866:0x1c3d, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageDocument", NUM, r10[0]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:867:0x1c5d, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRound", NUM, r10[0]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:868:0x1c7d, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.getString("ActionTakeScreenshoot", NUM).replace("un1", r10[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:869:0x1CLASSNAME, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDVideo", NUM, r10[0]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:870:0x1cb5, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageVideo", NUM, r10[0]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:871:0x1cd4, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageSDPhoto", NUM, r10[0]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:872:0x1cf3, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessagePhoto", NUM, r10[0]);
        r8 = org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:873:0x1d12, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageNoText", NUM, r10[0]);
        r8 = org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:874:0x1d30, code lost:
        r18 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:875:0x1d33, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r10[0], r10[1]);
        r8 = r10[1];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:876:0x1d50, code lost:
        r2 = r17;
        r9 = r25;
        r7 = org.telegram.messenger.LocaleController.formatString("NotificationMessageRecurringPay", NUM, r10[0], r10[1]);
        r8 = org.telegram.messenger.LocaleController.getString("PaymentInvoice", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:877:0x1d74, code lost:
        if (org.telegram.messenger.BuildVars.LOGS_ENABLED == false) goto L_0x1d8a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:878:0x1d76, code lost:
        org.telegram.messenger.FileLog.w("unhandled loc_key = " + r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:879:0x1d8a, code lost:
        r7 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:880:0x1d8c, code lost:
        r31 = "REACT_";
        r44 = r8;
        r46 = r11;
        r45 = r12;
        r2 = "CHAT_REACT_";
        r9 = r25;
        r7 = getReactedText(r14, r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:881:0x1d9b, code lost:
        r18 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:882:0x1d9d, code lost:
        r25 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:883:0x1d9f, code lost:
        if (r7 == null) goto L_0x1e8f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:884:0x1da1, code lost:
        r8 = new org.telegram.tgnet.TLRPC$TL_message();
        r8.id = r1;
        r8.random_id = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:885:0x1dac, code lost:
        if (r18 == null) goto L_0x1db1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:886:0x1dae, code lost:
        r1 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:887:0x1db1, code lost:
        r1 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:888:0x1db2, code lost:
        r8.message = r1;
        r8.date = (int) (r51 / 1000);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:889:0x1dbb, code lost:
        if (r16 == false) goto L_0x1dc4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:890:0x1dbd, code lost:
        r8.action = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:891:0x1dc4, code lost:
        if (r13 == false) goto L_0x1dcd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:892:0x1dc6, code lost:
        r8.flags |= Integer.MIN_VALUE;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:893:0x1dcd, code lost:
        r8.dialog_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:894:0x1dd3, code lost:
        if (r5 == 0) goto L_0x1de1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:895:0x1dd5, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r8.peer_id = r1;
        r1.channel_id = r5;
        r3 = r23;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:897:0x1de5, code lost:
        if (r23 == 0) goto L_0x1df3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:898:0x1de7, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r8.peer_id = r1;
        r3 = r23;
        r1.chat_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:899:0x1df3, code lost:
        r3 = r23;
        r1 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r8.peer_id = r1;
        r1.user_id = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:900:0x1e00, code lost:
        r8.flags |= 256;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:901:0x1e0a, code lost:
        if (r41 == 0) goto L_0x1e16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:902:0x1e0c, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChat();
        r8.from_id = r1;
        r1.chat_id = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:904:0x1e1a, code lost:
        if (r35 == 0) goto L_0x1e28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:905:0x1e1c, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel();
        r8.from_id = r1;
        r1.channel_id = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:907:0x1e2c, code lost:
        if (r39 == 0) goto L_0x1e3a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:908:0x1e2e, code lost:
        r1 = new org.telegram.tgnet.TLRPC$TL_peerUser();
        r8.from_id = r1;
        r1.user_id = r39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:909:0x1e3a, code lost:
        r8.from_id = r8.peer_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:910:0x1e3e, code lost:
        if (r37 != false) goto L_0x1e45;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:911:0x1e40, code lost:
        if (r16 == false) goto L_0x1e43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:913:0x1e43, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:914:0x1e45, code lost:
        r1 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:915:0x1e46, code lost:
        r8.mentioned = r1;
        r8.silent = r38;
        r8.from_scheduled = r9;
        r19 = new org.telegram.messenger.MessageObject(r29, r8, r7, r43, r46, r25, r45, r13, r44);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:916:0x1e6b, code lost:
        if (r14.startsWith(r31) != false) goto L_0x1e76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:918:0x1e71, code lost:
        if (r14.startsWith(r2) == false) goto L_0x1e74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:920:0x1e74, code lost:
        r2 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:921:0x1e76, code lost:
        r2 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:922:0x1e77, code lost:
        r19.isReactionPush = r2;
        r2 = new java.util.ArrayList();
        r2.add(r19);
        org.telegram.messenger.NotificationsController.getInstance(r29).processNewMessages(r2, true, true, countDownLatch);
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:923:0x1e8d, code lost:
        r30 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:924:0x1e8f, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:925:0x1e90, code lost:
        if (r8 == false) goto L_0x1e97;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:926:0x1e92, code lost:
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:927:0x1e97, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29);
        org.telegram.tgnet.ConnectionsManager.getInstance(r29).resumeNetworkMaybe();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:928:0x1ea3, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:929:0x1ea4, code lost:
        r1 = r0;
        r5 = r14;
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:930:0x1eaa, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:931:0x1eab, code lost:
        r30 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:932:0x1eae, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:933:0x1eaf, code lost:
        r30 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:934:0x1eb1, code lost:
        r14 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:935:0x1eb2, code lost:
        r1 = r0;
        r5 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:936:0x1eb4, code lost:
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:937:0x1eb8, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:938:0x1eb9, code lost:
        r29 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:939:0x1ebd, code lost:
        r29 = r4;
        r30 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:942:0x1ec6, code lost:
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:944:?, code lost:
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda0(r4));
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:945:0x1ed3, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:946:0x1ed4, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:947:0x1ed5, code lost:
        r4 = r29;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:948:0x1ed9, code lost:
        r30 = r7;
        r14 = r9;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda1(r4));
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:949:0x1ee9, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:950:0x1eea, code lost:
        r30 = r7;
        r14 = r9;
        r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification();
        r1.popup = false;
        r1.flags = 2;
        r1.inbox_date = (int) (r51 / 1000);
        r1.message = r6.getString("message");
        r1.type = "announcement";
        r1.media = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty();
        r2 = new org.telegram.tgnet.TLRPC$TL_updates();
        r2.updates.add(r1);
        org.telegram.messenger.Utilities.stageQueue.postRunnable(new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda3(r4, r2));
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).resumeNetworkMaybe();
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:951:0x1var_, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:952:0x1var_, code lost:
        r30 = r7;
        r14 = r9;
        r1 = r11.getInt("dc");
        r2 = r11.getString("addr").split(":");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:953:0x1f4b, code lost:
        if (r2.length == 2) goto L_0x1var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:954:0x1f4d, code lost:
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:955:0x1var_, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:956:0x1var_, code lost:
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).applyDatacenterAddress(r1, r2[0], java.lang.Integer.parseInt(r2[1]));
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).resumeNetworkMaybe();
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:957:0x1var_, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:958:0x1var_, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:959:0x1var_, code lost:
        r1 = r0;
        r5 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:960:0x1var_, code lost:
        r7 = r30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:973:0x1var_, code lost:
        org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r4);
        org.telegram.tgnet.ConnectionsManager.getInstance(r4).resumeNetworkMaybe();
        countDownLatch.countDown();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:974:0x1fa8, code lost:
        onDecryptError();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:977:0x1faf, code lost:
        org.telegram.messenger.FileLog.e("error in loc_key = " + r5 + " json " + r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:988:?, code lost:
        return;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:973:0x1var_  */
    /* JADX WARNING: Removed duplicated region for block: B:974:0x1fa8  */
    /* JADX WARNING: Removed duplicated region for block: B:977:0x1faf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ void lambda$processRemoteMessage$7(java.lang.String r49, java.lang.String r50, long r51) {
        /*
            r1 = r49
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
            r6 = r50
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
            org.json.JSONObject r6 = new org.json.JSONObject     // Catch:{ all -> 0x1var_ }
            r6.<init>(r7)     // Catch:{ all -> 0x1var_ }
            java.lang.String r9 = "loc_key"
            boolean r9 = r6.has(r9)     // Catch:{ all -> 0x1var_ }
            if (r9 == 0) goto L_0x011a
            java.lang.String r9 = "loc_key"
            java.lang.String r9 = r6.getString(r9)     // Catch:{ all -> 0x0117 }
            goto L_0x011c
        L_0x0117:
            r0 = move-exception
            goto L_0x1f8b
        L_0x011a:
            java.lang.String r9 = ""
        L_0x011c:
            java.lang.String r11 = "custom"
            java.lang.Object r11 = r6.get(r11)     // Catch:{ all -> 0x1f7f }
            boolean r11 = r11 instanceof org.json.JSONObject     // Catch:{ all -> 0x1f7f }
            if (r11 == 0) goto L_0x0132
            java.lang.String r11 = "custom"
            org.json.JSONObject r11 = r6.getJSONObject(r11)     // Catch:{ all -> 0x012d }
            goto L_0x0137
        L_0x012d:
            r0 = move-exception
            r1 = r0
            r5 = r9
            goto L_0x1var_
        L_0x0132:
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ all -> 0x1f7f }
            r11.<init>()     // Catch:{ all -> 0x1f7f }
        L_0x0137:
            java.lang.String r14 = "user_id"
            boolean r14 = r6.has(r14)     // Catch:{ all -> 0x1f7f }
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
            boolean r15 = r14 instanceof java.lang.Long     // Catch:{ all -> 0x1f7f }
            if (r15 == 0) goto L_0x015f
            java.lang.Long r14 = (java.lang.Long) r14     // Catch:{ all -> 0x012d }
            long r14 = r14.longValue()     // Catch:{ all -> 0x012d }
            goto L_0x0184
        L_0x015f:
            boolean r15 = r14 instanceof java.lang.Integer     // Catch:{ all -> 0x1f7f }
            if (r15 == 0) goto L_0x016b
            java.lang.Integer r14 = (java.lang.Integer) r14     // Catch:{ all -> 0x012d }
            int r14 = r14.intValue()     // Catch:{ all -> 0x012d }
        L_0x0169:
            long r14 = (long) r14
            goto L_0x0184
        L_0x016b:
            boolean r15 = r14 instanceof java.lang.String     // Catch:{ all -> 0x1f7f }
            if (r15 == 0) goto L_0x017a
            java.lang.String r14 = (java.lang.String) r14     // Catch:{ all -> 0x012d }
            java.lang.Integer r14 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14)     // Catch:{ all -> 0x012d }
            int r14 = r14.intValue()     // Catch:{ all -> 0x012d }
            goto L_0x0169
        L_0x017a:
            int r14 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1f7f }
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r14)     // Catch:{ all -> 0x1f7f }
            long r14 = r14.getClientUserId()     // Catch:{ all -> 0x1f7f }
        L_0x0184:
            int r16 = org.telegram.messenger.UserConfig.selectedAccount     // Catch:{ all -> 0x1f7f }
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
            org.telegram.messenger.UserConfig r14 = org.telegram.messenger.UserConfig.getInstance(r4)     // Catch:{ all -> 0x1var_ }
            boolean r14 = r14.isClientActivated()     // Catch:{ all -> 0x1var_ }
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
            goto L_0x1f7d
        L_0x01eb:
            int r14 = r9.hashCode()     // Catch:{ all -> 0x1var_ }
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
            if (r14 == 0) goto L_0x1var_
            if (r14 == r10) goto L_0x1eea
            if (r14 == r13) goto L_0x1ed9
            if (r14 == r12) goto L_0x1ebd
            java.lang.String r14 = "channel_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1eb8 }
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
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1eae }
            if (r14 == 0) goto L_0x025a
            java.lang.String r3 = "from_id"
            long r3 = r11.getLong(r3)     // Catch:{ all -> 0x0255 }
            r30 = r3
            goto L_0x025c
        L_0x0255:
            r0 = move-exception
            r1 = r0
            r5 = r9
            goto L_0x1eb4
        L_0x025a:
            r30 = r12
        L_0x025c:
            java.lang.String r14 = "chat_id"
            boolean r14 = r11.has(r14)     // Catch:{ all -> 0x1eae }
            if (r14 == 0) goto L_0x0275
            java.lang.String r3 = "chat_id"
            long r3 = r11.getLong(r3)     // Catch:{ all -> 0x0272 }
            r14 = r9
            long r8 = -r3
            r47 = r3
            r3 = r8
            r8 = r47
            goto L_0x0277
        L_0x0272:
            r0 = move-exception
            goto L_0x1eb1
        L_0x0275:
            r14 = r9
            r8 = r12
        L_0x0277:
            java.lang.String r15 = "encryption_id"
            boolean r15 = r11.has(r15)     // Catch:{ all -> 0x1eaa }
            if (r15 == 0) goto L_0x028e
            java.lang.String r3 = "encryption_id"
            int r3 = r11.getInt(r3)     // Catch:{ all -> 0x028b }
            long r3 = (long) r3     // Catch:{ all -> 0x028b }
            long r3 = org.telegram.messenger.DialogObject.makeEncryptedDialogId(r3)     // Catch:{ all -> 0x028b }
            goto L_0x028e
        L_0x028b:
            r0 = move-exception
            goto L_0x1eb2
        L_0x028e:
            java.lang.String r15 = "schedule"
            boolean r15 = r11.has(r15)     // Catch:{ all -> 0x1eaa }
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
            if (r10 == 0) goto L_0x1e8d
            java.lang.String r10 = "READ_HISTORY"
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x1eaa }
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
            goto L_0x1e8d
        L_0x0334:
            r32 = r30
            java.lang.String r10 = "MESSAGE_DELETED"
            boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x1eaa }
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
            if (r2 == 0) goto L_0x1e8d
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
            goto L_0x1e8d
        L_0x03ad:
            boolean r10 = android.text.TextUtils.isEmpty(r14)     // Catch:{ all -> 0x1eaa }
            if (r10 != 0) goto L_0x1e8d
            java.lang.String r10 = "msg_id"
            boolean r10 = r11.has(r10)     // Catch:{ all -> 0x1eaa }
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
            boolean r7 = r11.has(r7)     // Catch:{ all -> 0x1ea3 }
            if (r7 == 0) goto L_0x03e4
            java.lang.String r7 = "random_id"
            java.lang.String r7 = r11.getString(r7)     // Catch:{ all -> 0x1ea3 }
            java.lang.Long r7 = org.telegram.messenger.Utilities.parseLong(r7)     // Catch:{ all -> 0x1ea3 }
            long r23 = r7.longValue()     // Catch:{ all -> 0x1ea3 }
            r47 = r8
            r7 = r23
            r23 = r47
            goto L_0x03e8
        L_0x03e4:
            r23 = r8
            r7 = 0
        L_0x03e8:
            if (r10 == 0) goto L_0x0427
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r29)     // Catch:{ all -> 0x1ea3 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r9 = r9.dialogs_read_inbox_max     // Catch:{ all -> 0x1ea3 }
            r25 = r15
            java.lang.Long r15 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x1ea3 }
            java.lang.Object r9 = r9.get(r15)     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r9 = (java.lang.Integer) r9     // Catch:{ all -> 0x1ea3 }
            if (r9 != 0) goto L_0x041b
            org.telegram.messenger.MessagesStorage r9 = org.telegram.messenger.MessagesStorage.getInstance(r29)     // Catch:{ all -> 0x1ea3 }
            r15 = 0
            int r9 = r9.getDialogReadMax(r15, r3)     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x1ea3 }
            org.telegram.messenger.MessagesController r15 = org.telegram.messenger.MessagesController.getInstance(r29)     // Catch:{ all -> 0x1ea3 }
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r15 = r15.dialogs_read_inbox_max     // Catch:{ all -> 0x1ea3 }
            r26 = r13
            java.lang.Long r13 = java.lang.Long.valueOf(r3)     // Catch:{ all -> 0x1ea3 }
            r15.put(r13, r9)     // Catch:{ all -> 0x1ea3 }
            goto L_0x041d
        L_0x041b:
            r26 = r13
        L_0x041d:
            int r9 = r9.intValue()     // Catch:{ all -> 0x1ea3 }
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
            org.telegram.messenger.MessagesStorage r9 = org.telegram.messenger.MessagesStorage.getInstance(r29)     // Catch:{ all -> 0x1ea3 }
            boolean r9 = r9.checkMessageByRandomId(r7)     // Catch:{ all -> 0x1ea3 }
            if (r9 != 0) goto L_0x0425
            goto L_0x0423
        L_0x043c:
            boolean r13 = r14.startsWith(r2)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r15 = "CHAT_REACT_"
            if (r13 != 0) goto L_0x044a
            boolean r13 = r14.startsWith(r15)     // Catch:{ all -> 0x1ea3 }
            if (r13 == 0) goto L_0x044b
        L_0x044a:
            r9 = 1
        L_0x044b:
            if (r9 == 0) goto L_0x1e8f
            java.lang.String r9 = "chat_from_id"
            r27 = r7
            r13 = r10
            r7 = 0
            long r9 = r11.optLong(r9, r7)     // Catch:{ all -> 0x1ea3 }
            r31 = r13
            java.lang.String r13 = "chat_from_broadcast_id"
            r34 = r12
            long r12 = r11.optLong(r13, r7)     // Catch:{ all -> 0x1ea3 }
            r35 = r12
            java.lang.String r12 = "chat_from_group_id"
            long r12 = r11.optLong(r12, r7)     // Catch:{ all -> 0x1ea3 }
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
            boolean r8 = r11.has(r8)     // Catch:{ all -> 0x1ea3 }
            if (r8 == 0) goto L_0x0489
            java.lang.String r8 = "mention"
            int r8 = r11.getInt(r8)     // Catch:{ all -> 0x1ea3 }
            if (r8 == 0) goto L_0x0489
            r37 = 1
            goto L_0x048b
        L_0x0489:
            r37 = 0
        L_0x048b:
            java.lang.String r8 = "silent"
            boolean r8 = r11.has(r8)     // Catch:{ all -> 0x1ea3 }
            if (r8 == 0) goto L_0x049e
            java.lang.String r8 = "silent"
            int r8 = r11.getInt(r8)     // Catch:{ all -> 0x1ea3 }
            if (r8 == 0) goto L_0x049e
            r38 = 1
            goto L_0x04a0
        L_0x049e:
            r38 = 0
        L_0x04a0:
            java.lang.String r8 = "loc_args"
            r39 = r9
            r9 = r16
            boolean r8 = r9.has(r8)     // Catch:{ all -> 0x1ea3 }
            if (r8 == 0) goto L_0x04c8
            java.lang.String r8 = "loc_args"
            org.json.JSONArray r8 = r9.getJSONArray(r8)     // Catch:{ all -> 0x1ea3 }
            int r9 = r8.length()     // Catch:{ all -> 0x1ea3 }
            java.lang.String[] r10 = new java.lang.String[r9]     // Catch:{ all -> 0x1ea3 }
            r41 = r12
            r12 = 0
        L_0x04bb:
            if (r12 >= r9) goto L_0x04c6
            java.lang.String r13 = r8.getString(r12)     // Catch:{ all -> 0x1ea3 }
            r10[r12] = r13     // Catch:{ all -> 0x1ea3 }
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
            r9 = r10[r8]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "edit_date"
            boolean r8 = r11.has(r8)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r11 = "CHAT_"
            boolean r11 = r14.startsWith(r11)     // Catch:{ all -> 0x1ea3 }
            if (r11 == 0) goto L_0x0510
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((long) r3)     // Catch:{ all -> 0x1ea3 }
            if (r11 == 0) goto L_0x04fa
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x1ea3 }
            r11.<init>()     // Catch:{ all -> 0x1ea3 }
            r11.append(r9)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r9 = " @ "
            r11.append(r9)     // Catch:{ all -> 0x1ea3 }
            r9 = 1
            r12 = r10[r9]     // Catch:{ all -> 0x1ea3 }
            r11.append(r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r9 = r11.toString()     // Catch:{ all -> 0x1ea3 }
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
            r13 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r16 = 0
            r47 = r11
            r11 = r9
            r9 = r13
            r13 = r47
            goto L_0x0537
        L_0x0510:
            java.lang.String r11 = "PINNED_"
            boolean r11 = r14.startsWith(r11)     // Catch:{ all -> 0x1ea3 }
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
            boolean r11 = r14.startsWith(r11)     // Catch:{ all -> 0x1ea3 }
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
            boolean r43 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1ea3 }
            if (r43 == 0) goto L_0x0567
            r43 = r9
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x1ea3 }
            r9.<init>()     // Catch:{ all -> 0x1ea3 }
            r9.append(r1)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r1 = " received message notification "
            r9.append(r1)     // Catch:{ all -> 0x1ea3 }
            r9.append(r14)     // Catch:{ all -> 0x1ea3 }
            r1 = r34
            r9.append(r1)     // Catch:{ all -> 0x1ea3 }
            r9.append(r3)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r1 = " mid = "
            r9.append(r1)     // Catch:{ all -> 0x1ea3 }
            r1 = r31
            r9.append(r1)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x1ea3 }
            org.telegram.messenger.FileLog.d(r9)     // Catch:{ all -> 0x1ea3 }
            goto L_0x056b
        L_0x0567:
            r43 = r9
            r1 = r31
        L_0x056b:
            boolean r9 = r14.startsWith(r2)     // Catch:{ all -> 0x1ea3 }
            if (r9 != 0) goto L_0x1d8c
            boolean r9 = r14.startsWith(r15)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0579
            goto L_0x1d8c
        L_0x0579:
            int r9 = r14.hashCode()     // Catch:{ all -> 0x1ea3 }
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
            }     // Catch:{ all -> 0x1ea3 }
        L_0x0580:
            goto L_0x0ae2
        L_0x0582:
            java.lang.String r9 = "CHAT_MESSAGE_GEOLIVE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 61
            goto L_0x0adf
        L_0x058e:
            java.lang.String r9 = "CHANNEL_MESSAGE_PHOTOS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 44
            goto L_0x0adf
        L_0x059a:
            java.lang.String r9 = "CHANNEL_MESSAGE_NOTEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 29
            goto L_0x0adf
        L_0x05a6:
            java.lang.String r9 = "CHANNEL_MESSAGE_PLAYLIST"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 46
            goto L_0x0adf
        L_0x05b2:
            java.lang.String r9 = "PINNED_CONTACT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 95
            goto L_0x0adf
        L_0x05be:
            java.lang.String r9 = "CHAT_PHOTO_EDITED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 69
            goto L_0x0adf
        L_0x05ca:
            java.lang.String r9 = "LOCKED_MESSAGE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 109(0x6d, float:1.53E-43)
            goto L_0x0adf
        L_0x05d6:
            java.lang.String r9 = "CHAT_MESSAGE_PLAYLIST"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 84
            goto L_0x0adf
        L_0x05e2:
            java.lang.String r9 = "CHANNEL_MESSAGES"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 48
            goto L_0x0adf
        L_0x05ee:
            java.lang.String r9 = "MESSAGE_INVOICE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 22
            goto L_0x0adf
        L_0x05fa:
            java.lang.String r9 = "CHAT_MESSAGE_VIDEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 52
            goto L_0x0adf
        L_0x0606:
            java.lang.String r9 = "CHAT_MESSAGE_ROUND"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 53
            goto L_0x0adf
        L_0x0612:
            java.lang.String r9 = "CHAT_MESSAGE_PHOTO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 51
            goto L_0x0adf
        L_0x061e:
            java.lang.String r9 = "CHAT_MESSAGE_AUDIO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 56
            goto L_0x0adf
        L_0x062a:
            java.lang.String r9 = "MESSAGE_PLAYLIST"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 26
            goto L_0x0adf
        L_0x0636:
            java.lang.String r9 = "MESSAGE_VIDEOS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 25
            goto L_0x0adf
        L_0x0642:
            java.lang.String r9 = "PHONE_CALL_MISSED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 114(0x72, float:1.6E-43)
            goto L_0x0adf
        L_0x064e:
            java.lang.String r9 = "MESSAGE_PHOTOS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 24
            goto L_0x0adf
        L_0x065a:
            java.lang.String r9 = "CHAT_MESSAGE_VIDEOS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 83
            goto L_0x0adf
        L_0x0666:
            java.lang.String r9 = "MESSAGE_NOTEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r17 = r15
            r9 = 3
            goto L_0x0ae5
        L_0x0673:
            java.lang.String r9 = "MESSAGE_GIF"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 18
            goto L_0x0adf
        L_0x067f:
            java.lang.String r9 = "MESSAGE_GEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 16
            goto L_0x0adf
        L_0x068b:
            java.lang.String r9 = "MESSAGE_DOC"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 10
            goto L_0x0adf
        L_0x0697:
            java.lang.String r9 = "CHAT_MESSAGE_GAME_SCORE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 64
            goto L_0x0adf
        L_0x06a3:
            java.lang.String r9 = "CHANNEL_MESSAGE_GEOLIVE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 40
            goto L_0x0adf
        L_0x06af:
            java.lang.String r9 = "CHAT_MESSAGE_PHOTOS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 82
            goto L_0x0adf
        L_0x06bb:
            java.lang.String r9 = "CHAT_MESSAGE_NOTEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 50
            goto L_0x0adf
        L_0x06c7:
            java.lang.String r9 = "CHAT_TITLE_EDITED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 68
            goto L_0x0adf
        L_0x06d3:
            java.lang.String r9 = "PINNED_NOTEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 88
            goto L_0x0adf
        L_0x06df:
            java.lang.String r9 = "MESSAGE_TEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r17 = r15
            r9 = 1
            goto L_0x0ae5
        L_0x06ec:
            java.lang.String r9 = "MESSAGE_QUIZ"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 14
            goto L_0x0adf
        L_0x06f8:
            java.lang.String r9 = "MESSAGE_POLL"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 15
            goto L_0x0adf
        L_0x0704:
            java.lang.String r9 = "MESSAGE_GAME"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 19
            goto L_0x0adf
        L_0x0710:
            java.lang.String r9 = "MESSAGE_FWDS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 23
            goto L_0x0adf
        L_0x071c:
            java.lang.String r9 = "MESSAGE_DOCS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 27
            goto L_0x0adf
        L_0x0728:
            java.lang.String r9 = "CHAT_MESSAGE_TEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 49
            goto L_0x0adf
        L_0x0734:
            java.lang.String r9 = "CHAT_MESSAGE_QUIZ"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 58
            goto L_0x0adf
        L_0x0740:
            java.lang.String r9 = "CHAT_MESSAGE_POLL"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 59
            goto L_0x0adf
        L_0x074c:
            java.lang.String r9 = "CHAT_MESSAGE_GAME"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 63
            goto L_0x0adf
        L_0x0758:
            java.lang.String r9 = "CHAT_MESSAGE_FWDS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 81
            goto L_0x0adf
        L_0x0764:
            java.lang.String r9 = "CHAT_MESSAGE_DOCS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 85
            goto L_0x0adf
        L_0x0770:
            java.lang.String r9 = "CHANNEL_MESSAGE_GAME_SCORE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 21
            goto L_0x0adf
        L_0x077c:
            java.lang.String r9 = "PINNED_GEOLIVE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 99
            goto L_0x0adf
        L_0x0788:
            java.lang.String r9 = "MESSAGE_CONTACT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 13
            goto L_0x0adf
        L_0x0794:
            java.lang.String r9 = "PINNED_VIDEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 90
            goto L_0x0adf
        L_0x07a0:
            java.lang.String r9 = "PINNED_ROUND"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 91
            goto L_0x0adf
        L_0x07ac:
            java.lang.String r9 = "PINNED_PHOTO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 89
            goto L_0x0adf
        L_0x07b8:
            java.lang.String r9 = "PINNED_AUDIO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 94
            goto L_0x0adf
        L_0x07c4:
            java.lang.String r9 = "MESSAGE_PHOTO_SECRET"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 5
            goto L_0x0adf
        L_0x07cf:
            java.lang.String r9 = "CHAT_VOICECHAT_INVITE_YOU"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 74
            goto L_0x0adf
        L_0x07db:
            java.lang.String r9 = "CHANNEL_MESSAGE_VIDEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 31
            goto L_0x0adf
        L_0x07e7:
            java.lang.String r9 = "CHANNEL_MESSAGE_ROUND"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 32
            goto L_0x0adf
        L_0x07f3:
            java.lang.String r9 = "CHANNEL_MESSAGE_PHOTO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 30
            goto L_0x0adf
        L_0x07ff:
            java.lang.String r9 = "CHAT_VOICECHAT_END"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 73
            goto L_0x0adf
        L_0x080b:
            java.lang.String r9 = "CHANNEL_MESSAGE_AUDIO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 35
            goto L_0x0adf
        L_0x0817:
            java.lang.String r9 = "CHAT_MESSAGE_STICKER"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 55
            goto L_0x0adf
        L_0x0823:
            java.lang.String r9 = "MESSAGES"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 28
            goto L_0x0adf
        L_0x082f:
            java.lang.String r9 = "CHAT_MESSAGE_GIF"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 62
            goto L_0x0adf
        L_0x083b:
            java.lang.String r9 = "CHAT_MESSAGE_GEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 60
            goto L_0x0adf
        L_0x0847:
            java.lang.String r9 = "CHAT_MESSAGE_DOC"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 54
            goto L_0x0adf
        L_0x0853:
            java.lang.String r9 = "CHAT_VOICECHAT_INVITE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 72
            goto L_0x0adf
        L_0x085f:
            java.lang.String r9 = "CHAT_LEFT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 77
            goto L_0x0adf
        L_0x086b:
            java.lang.String r9 = "CHAT_ADD_YOU"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 67
            goto L_0x0adf
        L_0x0877:
            java.lang.String r9 = "REACT_TEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 105(0x69, float:1.47E-43)
            goto L_0x0adf
        L_0x0883:
            java.lang.String r9 = "CHAT_DELETE_MEMBER"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 75
            goto L_0x0adf
        L_0x088f:
            java.lang.String r9 = "MESSAGE_SCREENSHOT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r17 = r15
            r9 = 8
            goto L_0x0ae5
        L_0x089d:
            java.lang.String r9 = "AUTH_REGION"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 108(0x6c, float:1.51E-43)
            goto L_0x0adf
        L_0x08a9:
            java.lang.String r9 = "CONTACT_JOINED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 106(0x6a, float:1.49E-43)
            goto L_0x0adf
        L_0x08b5:
            java.lang.String r9 = "CHAT_MESSAGE_INVOICE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 65
            goto L_0x0adf
        L_0x08c1:
            java.lang.String r9 = "ENCRYPTION_REQUEST"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 110(0x6e, float:1.54E-43)
            goto L_0x0adf
        L_0x08cd:
            java.lang.String r9 = "MESSAGE_GEOLIVE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 17
            goto L_0x0adf
        L_0x08d9:
            java.lang.String r9 = "CHAT_DELETE_YOU"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 76
            goto L_0x0adf
        L_0x08e5:
            java.lang.String r9 = "AUTH_UNKNOWN"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 107(0x6b, float:1.5E-43)
            goto L_0x0adf
        L_0x08f1:
            java.lang.String r9 = "PINNED_GIF"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 103(0x67, float:1.44E-43)
            goto L_0x0adf
        L_0x08fd:
            java.lang.String r9 = "PINNED_GEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 98
            goto L_0x0adf
        L_0x0909:
            java.lang.String r9 = "PINNED_DOC"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 92
            goto L_0x0adf
        L_0x0915:
            java.lang.String r9 = "PINNED_GAME_SCORE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 101(0x65, float:1.42E-43)
            goto L_0x0adf
        L_0x0921:
            java.lang.String r9 = "CHANNEL_MESSAGE_STICKER"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 34
            goto L_0x0adf
        L_0x092d:
            java.lang.String r9 = "PHONE_CALL_REQUEST"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 112(0x70, float:1.57E-43)
            goto L_0x0adf
        L_0x0939:
            java.lang.String r9 = "PINNED_STICKER"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 93
            goto L_0x0adf
        L_0x0945:
            java.lang.String r9 = "PINNED_TEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 87
            goto L_0x0adf
        L_0x0951:
            java.lang.String r9 = "PINNED_QUIZ"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 96
            goto L_0x0adf
        L_0x095d:
            java.lang.String r9 = "PINNED_POLL"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 97
            goto L_0x0adf
        L_0x0969:
            java.lang.String r9 = "PINNED_GAME"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 100
            goto L_0x0adf
        L_0x0975:
            java.lang.String r9 = "CHAT_MESSAGE_CONTACT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 57
            goto L_0x0adf
        L_0x0981:
            java.lang.String r9 = "MESSAGE_VIDEO_SECRET"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 7
            goto L_0x0adf
        L_0x098c:
            java.lang.String r9 = "CHANNEL_MESSAGE_TEXT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r17 = r15
            r9 = 2
            goto L_0x0ae5
        L_0x0999:
            java.lang.String r9 = "CHANNEL_MESSAGE_QUIZ"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 37
            goto L_0x0adf
        L_0x09a5:
            java.lang.String r9 = "CHANNEL_MESSAGE_POLL"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 38
            goto L_0x0adf
        L_0x09b1:
            java.lang.String r9 = "CHANNEL_MESSAGE_GAME"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 42
            goto L_0x0adf
        L_0x09bd:
            java.lang.String r9 = "CHANNEL_MESSAGE_FWDS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 43
            goto L_0x0adf
        L_0x09c9:
            java.lang.String r9 = "CHANNEL_MESSAGE_DOCS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 47
            goto L_0x0adf
        L_0x09d5:
            java.lang.String r9 = "PINNED_INVOICE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 102(0x66, float:1.43E-43)
            goto L_0x0adf
        L_0x09e1:
            java.lang.String r9 = "CHAT_RETURNED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 78
            goto L_0x0adf
        L_0x09ed:
            java.lang.String r9 = "ENCRYPTED_MESSAGE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 104(0x68, float:1.46E-43)
            goto L_0x0adf
        L_0x09f9:
            java.lang.String r9 = "ENCRYPTION_ACCEPT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 111(0x6f, float:1.56E-43)
            goto L_0x0adf
        L_0x0a05:
            java.lang.String r9 = "MESSAGE_VIDEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 6
            goto L_0x0adf
        L_0x0a10:
            java.lang.String r9 = "MESSAGE_ROUND"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 9
            goto L_0x0adf
        L_0x0a1c:
            java.lang.String r9 = "MESSAGE_PHOTO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r17 = r15
            r9 = 4
            goto L_0x0ae5
        L_0x0a29:
            java.lang.String r9 = "MESSAGE_MUTED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 113(0x71, float:1.58E-43)
            goto L_0x0adf
        L_0x0a35:
            java.lang.String r9 = "MESSAGE_AUDIO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 12
            goto L_0x0adf
        L_0x0a41:
            java.lang.String r9 = "MESSAGE_RECURRING_PAY"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r17 = r15
            r9 = 0
            goto L_0x0ae5
        L_0x0a4e:
            java.lang.String r9 = "CHAT_MESSAGES"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 86
            goto L_0x0adf
        L_0x0a5a:
            java.lang.String r9 = "CHAT_VOICECHAT_START"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 71
            goto L_0x0adf
        L_0x0a66:
            java.lang.String r9 = "CHAT_REQ_JOINED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 80
            goto L_0x0adf
        L_0x0a72:
            java.lang.String r9 = "CHAT_JOINED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 79
            goto L_0x0adf
        L_0x0a7d:
            java.lang.String r9 = "CHAT_ADD_MEMBER"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 70
            goto L_0x0adf
        L_0x0a88:
            java.lang.String r9 = "CHANNEL_MESSAGE_GIF"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 41
            goto L_0x0adf
        L_0x0a93:
            java.lang.String r9 = "CHANNEL_MESSAGE_GEO"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 39
            goto L_0x0adf
        L_0x0a9e:
            java.lang.String r9 = "CHANNEL_MESSAGE_DOC"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 33
            goto L_0x0adf
        L_0x0aa9:
            java.lang.String r9 = "CHANNEL_MESSAGE_VIDEOS"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 45
            goto L_0x0adf
        L_0x0ab4:
            java.lang.String r9 = "MESSAGE_STICKER"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 11
            goto L_0x0adf
        L_0x0abf:
            java.lang.String r9 = "CHAT_CREATED"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 66
            goto L_0x0adf
        L_0x0aca:
            java.lang.String r9 = "CHANNEL_MESSAGE_CONTACT"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 36
            goto L_0x0adf
        L_0x0ad5:
            java.lang.String r9 = "MESSAGE_GAME_SCORE"
            boolean r9 = r14.equals(r9)     // Catch:{ all -> 0x1ea3 }
            if (r9 == 0) goto L_0x0ae2
            r9 = 20
        L_0x0adf:
            r17 = r15
            goto L_0x0ae5
        L_0x0ae2:
            r17 = r15
            r9 = -1
        L_0x0ae5:
            java.lang.String r15 = " "
            r31 = r2
            java.lang.String r2 = "NotificationGroupFew"
            r44 = r8
            java.lang.String r8 = "NotificationMessageFew"
            r45 = r12
            java.lang.String r12 = "ChannelMessageFew"
            r46 = r11
            java.lang.String r11 = "AttachSticker"
            switch(r9) {
                case 0: goto L_0x1d50;
                case 1: goto L_0x1d33;
                case 2: goto L_0x1d33;
                case 3: goto L_0x1d12;
                case 4: goto L_0x1cf3;
                case 5: goto L_0x1cd4;
                case 6: goto L_0x1cb5;
                case 7: goto L_0x1CLASSNAME;
                case 8: goto L_0x1c7d;
                case 9: goto L_0x1c5d;
                case 10: goto L_0x1c3d;
                case 11: goto L_0x1bde;
                case 12: goto L_0x1bbe;
                case 13: goto L_0x1b99;
                case 14: goto L_0x1b74;
                case 15: goto L_0x1b4f;
                case 16: goto L_0x1b2f;
                case 17: goto L_0x1b0f;
                case 18: goto L_0x1aef;
                case 19: goto L_0x1aca;
                case 20: goto L_0x1aa9;
                case 21: goto L_0x1aa9;
                case 22: goto L_0x1a84;
                case 23: goto L_0x1a57;
                case 24: goto L_0x1a2c;
                case 25: goto L_0x1a01;
                case 26: goto L_0x19d7;
                case 27: goto L_0x19ad;
                case 28: goto L_0x1990;
                case 29: goto L_0x196a;
                case 30: goto L_0x194b;
                case 31: goto L_0x192c;
                case 32: goto L_0x190d;
                case 33: goto L_0x18ed;
                case 34: goto L_0x188e;
                case 35: goto L_0x186e;
                case 36: goto L_0x1849;
                case 37: goto L_0x1824;
                case 38: goto L_0x17ff;
                case 39: goto L_0x17df;
                case 40: goto L_0x17bf;
                case 41: goto L_0x179f;
                case 42: goto L_0x177f;
                case 43: goto L_0x1750;
                case 44: goto L_0x1725;
                case 45: goto L_0x16fa;
                case 46: goto L_0x16cf;
                case 47: goto L_0x16a4;
                case 48: goto L_0x168d;
                case 49: goto L_0x166a;
                case 50: goto L_0x1645;
                case 51: goto L_0x1620;
                case 52: goto L_0x15fb;
                case 53: goto L_0x15d6;
                case 54: goto L_0x15b1;
                case 55: goto L_0x1531;
                case 56: goto L_0x150c;
                case 57: goto L_0x14e2;
                case 58: goto L_0x14b8;
                case 59: goto L_0x148e;
                case 60: goto L_0x1469;
                case 61: goto L_0x1444;
                case 62: goto L_0x141f;
                case 63: goto L_0x13f5;
                case 64: goto L_0x13d0;
                case 65: goto L_0x13a6;
                case 66: goto L_0x1388;
                case 67: goto L_0x1388;
                case 68: goto L_0x136d;
                case 69: goto L_0x1352;
                case 70: goto L_0x1332;
                case 71: goto L_0x1316;
                case 72: goto L_0x12f5;
                case 73: goto L_0x12d9;
                case 74: goto L_0x12bd;
                case 75: goto L_0x12a1;
                case 76: goto L_0x1285;
                case 77: goto L_0x1269;
                case 78: goto L_0x124d;
                case 79: goto L_0x1231;
                case 80: goto L_0x1215;
                case 81: goto L_0x11e3;
                case 82: goto L_0x11b2;
                case 83: goto L_0x1181;
                case 84: goto L_0x1150;
                case 85: goto L_0x111f;
                case 86: goto L_0x1103;
                case 87: goto L_0x10aa;
                case 88: goto L_0x105b;
                case 89: goto L_0x100c;
                case 90: goto L_0x0fbd;
                case 91: goto L_0x0f6e;
                case 92: goto L_0x0f1f;
                case 93: goto L_0x0e63;
                case 94: goto L_0x0e14;
                case 95: goto L_0x0dba;
                case 96: goto L_0x0d60;
                case 97: goto L_0x0d06;
                case 98: goto L_0x0cb7;
                case 99: goto L_0x0CLASSNAME;
                case 100: goto L_0x0CLASSNAME;
                case 101: goto L_0x0bca;
                case 102: goto L_0x0b7b;
                case 103: goto L_0x0b27;
                case 104: goto L_0x0b08;
                case 105: goto L_0x0b02;
                case 106: goto L_0x0b02;
                case 107: goto L_0x0b02;
                case 108: goto L_0x0b02;
                case 109: goto L_0x0b02;
                case 110: goto L_0x0b02;
                case 111: goto L_0x0b02;
                case 112: goto L_0x0b02;
                case 113: goto L_0x0b02;
                case 114: goto L_0x0b02;
                default: goto L_0x0afa;
            }
        L_0x0afa:
            r2 = r17
            r9 = r25
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d74
        L_0x0b02:
            r2 = r17
            r9 = r25
            goto L_0x1d8a
        L_0x0b08:
            java.lang.String r2 = "YouHaveNewMessage"
            r7 = 2131629357(0x7f0e152d, float:1.8886033E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r7)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "SecretChatName"
            r8 = 2131628222(0x7f0e10be, float:1.888373E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x1ea3 }
            r43 = r7
            r9 = r25
            r18 = 0
            r25 = 1
            r7 = r2
            r2 = r17
            goto L_0x1d9f
        L_0x0b27:
            r8 = 0
            int r2 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r2 <= 0) goto L_0x0b4a
            java.lang.String r2 = "NotificationActionPinnedGifUser"
            r7 = 2131626972(0x7f0e0bdc, float:1.8881195E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]     // Catch:{ all -> 0x1ea3 }
            r9 = 0
            r11 = r10[r9]     // Catch:{ all -> 0x1ea3 }
            r8[r9] = r11     // Catch:{ all -> 0x1ea3 }
            r9 = 1
            r10 = r10[r9]     // Catch:{ all -> 0x1ea3 }
            r8[r9] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r8)     // Catch:{ all -> 0x1ea3 }
            r7 = r2
            r2 = r17
            r9 = r25
            goto L_0x1d9b
        L_0x0b4a:
            r8 = r17
            r9 = r25
            if (r7 == 0) goto L_0x0b68
            java.lang.String r2 = "NotificationActionPinnedGif"
            r7 = 2131626970(0x7f0e0bda, float:1.8881191E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0b68:
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            r7 = 2131626971(0x7f0e0bdb, float:1.8881193E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0b7b:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x0b9d
            java.lang.String r2 = "NotificationActionPinnedInvoiceUser"
            r7 = 2131626975(0x7f0e0bdf, float:1.8881201E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0b9d:
            if (r7 == 0) goto L_0x0bb7
            java.lang.String r2 = "NotificationActionPinnedInvoice"
            r7 = 2131626973(0x7f0e0bdd, float:1.8881197E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0bb7:
            java.lang.String r2 = "NotificationActionPinnedInvoiceChannel"
            r7 = 2131626974(0x7f0e0bde, float:1.88812E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0bca:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x0bec
            java.lang.String r2 = "NotificationActionPinnedGameScoreUser"
            r7 = 2131626962(0x7f0e0bd2, float:1.8881175E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0bec:
            if (r7 == 0) goto L_0x0CLASSNAME
            java.lang.String r2 = "NotificationActionPinnedGameScore"
            r7 = 2131626960(0x7f0e0bd0, float:1.888117E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0CLASSNAME:
            java.lang.String r2 = "NotificationActionPinnedGameScoreChannel"
            r7 = 2131626961(0x7f0e0bd1, float:1.8881173E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0CLASSNAME:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x0c3b
            java.lang.String r2 = "NotificationActionPinnedGameUser"
            r7 = 2131626963(0x7f0e0bd3, float:1.8881177E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0c3b:
            if (r7 == 0) goto L_0x0CLASSNAME
            java.lang.String r2 = "NotificationActionPinnedGame"
            r7 = 2131626958(0x7f0e0bce, float:1.8881167E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0CLASSNAME:
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            r7 = 2131626959(0x7f0e0bcf, float:1.8881169E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0CLASSNAME:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x0c8a
            java.lang.String r2 = "NotificationActionPinnedGeoLiveUser"
            r7 = 2131626968(0x7f0e0bd8, float:1.8881187E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0c8a:
            if (r7 == 0) goto L_0x0ca4
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            r7 = 2131626966(0x7f0e0bd6, float:1.8881183E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0ca4:
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            r7 = 2131626967(0x7f0e0bd7, float:1.8881185E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0cb7:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x0cd9
            java.lang.String r2 = "NotificationActionPinnedGeoUser"
            r7 = 2131626969(0x7f0e0bd9, float:1.888119E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0cd9:
            if (r7 == 0) goto L_0x0cf3
            java.lang.String r2 = "NotificationActionPinnedGeo"
            r7 = 2131626964(0x7f0e0bd4, float:1.888118E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0cf3:
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            r7 = 2131626965(0x7f0e0bd5, float:1.8881181E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0d06:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x0d28
            java.lang.String r2 = "NotificationActionPinnedPollUser"
            r7 = 2131626987(0x7f0e0beb, float:1.8881226E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0d28:
            if (r7 == 0) goto L_0x0d48
            java.lang.String r2 = "NotificationActionPinnedPoll2"
            r7 = 2131626985(0x7f0e0be9, float:1.8881222E38)
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r17 = 1
            r11[r17] = r15     // Catch:{ all -> 0x1ea3 }
            r10 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0d48:
            java.lang.String r2 = "NotificationActionPinnedPollChannel2"
            r7 = 2131626986(0x7f0e0bea, float:1.8881224E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0d60:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x0d82
            java.lang.String r2 = "NotificationActionPinnedQuizUser"
            r7 = 2131626990(0x7f0e0bee, float:1.8881232E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0d82:
            if (r7 == 0) goto L_0x0da2
            java.lang.String r2 = "NotificationActionPinnedQuiz2"
            r7 = 2131626988(0x7f0e0bec, float:1.8881228E38)
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r17 = 1
            r11[r17] = r15     // Catch:{ all -> 0x1ea3 }
            r10 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0da2:
            java.lang.String r2 = "NotificationActionPinnedQuizChannel2"
            r7 = 2131626989(0x7f0e0bed, float:1.888123E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0dba:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x0ddc
            java.lang.String r2 = "NotificationActionPinnedContactUser"
            r7 = 2131626954(0x7f0e0bca, float:1.8881159E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0ddc:
            if (r7 == 0) goto L_0x0dfc
            java.lang.String r2 = "NotificationActionPinnedContact2"
            r7 = 2131626952(0x7f0e0bc8, float:1.8881155E38)
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r17 = 1
            r11[r17] = r15     // Catch:{ all -> 0x1ea3 }
            r10 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0dfc:
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            r7 = 2131626953(0x7f0e0bc9, float:1.8881157E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0e14:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x0e36
            java.lang.String r2 = "NotificationActionPinnedVoiceUser"
            r7 = 2131627008(0x7f0e0CLASSNAME, float:1.8881268E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0e36:
            if (r7 == 0) goto L_0x0e50
            java.lang.String r2 = "NotificationActionPinnedVoice"
            r7 = 2131627006(0x7f0e0bfe, float:1.8881264E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0e50:
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            r7 = 2131627007(0x7f0e0bff, float:1.8881266E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0e63:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x0ea4
            int r2 = r10.length     // Catch:{ all -> 0x1ea3 }
            r7 = 1
            if (r2 <= r7) goto L_0x0e91
            r2 = r10[r7]     // Catch:{ all -> 0x1ea3 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x1ea3 }
            if (r2 != 0) goto L_0x0e91
            java.lang.String r2 = "NotificationActionPinnedStickerEmojiUser"
            r7 = 2131626998(0x7f0e0bf6, float:1.8881248E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0e91:
            java.lang.String r2 = "NotificationActionPinnedStickerUser"
            r7 = 2131626999(0x7f0e0bf7, float:1.888125E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0ea4:
            if (r7 == 0) goto L_0x0ee8
            int r2 = r10.length     // Catch:{ all -> 0x1ea3 }
            r7 = 2
            if (r2 <= r7) goto L_0x0ed0
            r2 = r10[r7]     // Catch:{ all -> 0x1ea3 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x1ea3 }
            if (r2 != 0) goto L_0x0ed0
            java.lang.String r2 = "NotificationActionPinnedStickerEmoji"
            r7 = 2131626996(0x7f0e0bf4, float:1.8881244E38)
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r17 = 1
            r11[r17] = r15     // Catch:{ all -> 0x1ea3 }
            r10 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0ed0:
            java.lang.String r2 = "NotificationActionPinnedSticker"
            r7 = 2131626994(0x7f0e0bf2, float:1.888124E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0ee8:
            int r2 = r10.length     // Catch:{ all -> 0x1ea3 }
            r7 = 1
            if (r2 <= r7) goto L_0x0f0c
            r2 = r10[r7]     // Catch:{ all -> 0x1ea3 }
            boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x1ea3 }
            if (r2 != 0) goto L_0x0f0c
            java.lang.String r2 = "NotificationActionPinnedStickerEmojiChannel"
            r7 = 2131626997(0x7f0e0bf5, float:1.8881246E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0f0c:
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            r7 = 2131626995(0x7f0e0bf3, float:1.8881242E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0f1f:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x0var_
            java.lang.String r2 = "NotificationActionPinnedFileUser"
            r7 = 2131626957(0x7f0e0bcd, float:1.8881165E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0var_:
            if (r7 == 0) goto L_0x0f5b
            java.lang.String r2 = "NotificationActionPinnedFile"
            r7 = 2131626955(0x7f0e0bcb, float:1.888116E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0f5b:
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            r7 = 2131626956(0x7f0e0bcc, float:1.8881163E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0f6e:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x0var_
            java.lang.String r2 = "NotificationActionPinnedRoundUser"
            r7 = 2131626993(0x7f0e0bf1, float:1.8881238E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0var_:
            if (r7 == 0) goto L_0x0faa
            java.lang.String r2 = "NotificationActionPinnedRound"
            r7 = 2131626991(0x7f0e0bef, float:1.8881234E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0faa:
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            r7 = 2131626992(0x7f0e0bf0, float:1.8881236E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0fbd:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x0fdf
            java.lang.String r2 = "NotificationActionPinnedVideoUser"
            r7 = 2131627005(0x7f0e0bfd, float:1.8881262E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0fdf:
            if (r7 == 0) goto L_0x0ff9
            java.lang.String r2 = "NotificationActionPinnedVideo"
            r7 = 2131627003(0x7f0e0bfb, float:1.8881258E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x0ff9:
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            r7 = 2131627004(0x7f0e0bfc, float:1.888126E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x100c:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x102e
            java.lang.String r2 = "NotificationActionPinnedPhotoUser"
            r7 = 2131626984(0x7f0e0be8, float:1.888122E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x102e:
            if (r7 == 0) goto L_0x1048
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            r7 = 2131626982(0x7f0e0be6, float:1.8881216E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x1048:
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            r7 = 2131626983(0x7f0e0be7, float:1.8881218E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x105b:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x107d
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            r7 = 2131626981(0x7f0e0be5, float:1.8881214E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x107d:
            if (r7 == 0) goto L_0x1097
            java.lang.String r2 = "NotificationActionPinnedNoText"
            r7 = 2131626979(0x7f0e0be3, float:1.888121E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x1097:
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            r7 = 2131626980(0x7f0e0be4, float:1.8881212E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x10aa:
            r8 = r17
            r9 = r25
            r11 = 0
            int r2 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r2 <= 0) goto L_0x10cc
            java.lang.String r2 = "NotificationActionPinnedTextUser"
            r7 = 2131627002(0x7f0e0bfa, float:1.8881256E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x10cc:
            if (r7 == 0) goto L_0x10eb
            java.lang.String r2 = "NotificationActionPinnedText"
            r7 = 2131627000(0x7f0e0bf8, float:1.8881252E38)
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x10eb:
            java.lang.String r2 = "NotificationActionPinnedTextChannel"
            r7 = 2131627001(0x7f0e0bf9, float:1.8881254E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x1103:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationGroupAlbum"
            r7 = 2131627017(0x7f0e0CLASSNAME, float:1.8881287E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a5
        L_0x111f:
            r8 = r17
            r9 = r25
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r12 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r7[r11] = r12     // Catch:{ all -> 0x1ea3 }
            r11 = 1
            r12 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r7[r11] = r12     // Catch:{ all -> 0x1ea3 }
            java.lang.String r11 = "Files"
            r12 = 2
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1ea3 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1ea3 }
            r15 = 0
            java.lang.Object[] r12 = new java.lang.Object[r15]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r11, r10, r12)     // Catch:{ all -> 0x1ea3 }
            r11 = 2
            r7[r11] = r10     // Catch:{ all -> 0x1ea3 }
            r10 = 2131627020(0x7f0e0c0c, float:1.8881293E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r10, r7)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a5
        L_0x1150:
            r8 = r17
            r9 = r25
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r12 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r7[r11] = r12     // Catch:{ all -> 0x1ea3 }
            r11 = 1
            r12 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r7[r11] = r12     // Catch:{ all -> 0x1ea3 }
            java.lang.String r11 = "MusicFiles"
            r12 = 2
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1ea3 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1ea3 }
            r15 = 0
            java.lang.Object[] r12 = new java.lang.Object[r15]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r11, r10, r12)     // Catch:{ all -> 0x1ea3 }
            r11 = 2
            r7[r11] = r10     // Catch:{ all -> 0x1ea3 }
            r10 = 2131627020(0x7f0e0c0c, float:1.8881293E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r10, r7)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a5
        L_0x1181:
            r8 = r17
            r9 = r25
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r12 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r7[r11] = r12     // Catch:{ all -> 0x1ea3 }
            r11 = 1
            r12 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r7[r11] = r12     // Catch:{ all -> 0x1ea3 }
            java.lang.String r11 = "Videos"
            r12 = 2
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1ea3 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1ea3 }
            r15 = 0
            java.lang.Object[] r12 = new java.lang.Object[r15]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r11, r10, r12)     // Catch:{ all -> 0x1ea3 }
            r11 = 2
            r7[r11] = r10     // Catch:{ all -> 0x1ea3 }
            r10 = 2131627020(0x7f0e0c0c, float:1.8881293E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r10, r7)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a5
        L_0x11b2:
            r8 = r17
            r9 = r25
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r12 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r7[r11] = r12     // Catch:{ all -> 0x1ea3 }
            r11 = 1
            r12 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r7[r11] = r12     // Catch:{ all -> 0x1ea3 }
            java.lang.String r11 = "Photos"
            r12 = 2
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1ea3 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1ea3 }
            r15 = 0
            java.lang.Object[] r12 = new java.lang.Object[r15]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r11, r10, r12)     // Catch:{ all -> 0x1ea3 }
            r11 = 2
            r7[r11] = r10     // Catch:{ all -> 0x1ea3 }
            r10 = 2131627020(0x7f0e0c0c, float:1.8881293E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r10, r7)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a5
        L_0x11e3:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationGroupForwardedFew"
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1ea3 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1ea3 }
            r15 = 0
            java.lang.Object[] r7 = new java.lang.Object[r15]     // Catch:{ all -> 0x1ea3 }
            r15 = r26
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r15, r10, r7)     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r7     // Catch:{ all -> 0x1ea3 }
            r7 = 2131627021(0x7f0e0c0d, float:1.8881295E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a5
        L_0x1215:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "UserAcceptedToGroupPushWithGroup"
            r7 = 2131628886(0x7f0e1356, float:1.8885077E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x1231:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            r7 = 2131627016(0x7f0e0CLASSNAME, float:1.8881285E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x124d:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationGroupAddSelf"
            r7 = 2131627015(0x7f0e0CLASSNAME, float:1.8881282E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x1269:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationGroupLeftMember"
            r7 = 2131627026(0x7f0e0CLASSNAME, float:1.8881305E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x1285:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationGroupKickYou"
            r7 = 2131627025(0x7f0e0CLASSNAME, float:1.8881303E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x12a1:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationGroupKickMember"
            r7 = 2131627024(0x7f0e0CLASSNAME, float:1.88813E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x12bd:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationGroupInvitedYouToCall"
            r7 = 2131627023(0x7f0e0c0f, float:1.8881299E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x12d9:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationGroupEndedCall"
            r7 = 2131627019(0x7f0e0c0b, float:1.888129E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x12f5:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationGroupInvitedToCall"
            r7 = 2131627022(0x7f0e0c0e, float:1.8881297E38)
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x1316:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationGroupCreatedCall"
            r7 = 2131627018(0x7f0e0c0a, float:1.8881289E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x1332:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationGroupAddMember"
            r7 = 2131627014(0x7f0e0CLASSNAME, float:1.888128E38)
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x1352:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            r7 = 2131627012(0x7f0e0CLASSNAME, float:1.8881276E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x136d:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationEditedGroupName"
            r7 = 2131627011(0x7f0e0CLASSNAME, float:1.8881274E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x1388:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationInvitedToGroup"
            r7 = 2131627031(0x7f0e0CLASSNAME, float:1.8881315E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
        L_0x13a2:
            r7 = r2
            r2 = r8
            goto L_0x1d9b
        L_0x13a6:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupInvoice"
            r7 = 2131627048(0x7f0e0CLASSNAME, float:1.888135E38)
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "PaymentInvoice"
            r10 = 2131627458(0x7f0e0dc2, float:1.888218E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x13d0:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupGameScored"
            r7 = 2131627046(0x7f0e0CLASSNAME, float:1.8881345E38)
            r11 = 4
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 3
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x13a2
        L_0x13f5:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupGame"
            r7 = 2131627045(0x7f0e0CLASSNAME, float:1.8881343E38)
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachGame"
            r10 = 2131624499(0x7f0e0233, float:1.887618E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x141f:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupGif"
            r7 = 2131627047(0x7f0e0CLASSNAME, float:1.8881347E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachGif"
            r10 = 2131624500(0x7f0e0234, float:1.8876181E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x1444:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            r7 = 2131627049(0x7f0e0CLASSNAME, float:1.8881351E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachLiveLocation"
            r10 = 2131624505(0x7f0e0239, float:1.8876192E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x1469:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupMap"
            r7 = 2131627050(0x7f0e0c2a, float:1.8881353E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachLocation"
            r10 = 2131624509(0x7f0e023d, float:1.88762E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x148e:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupPoll2"
            r7 = 2131627054(0x7f0e0c2e, float:1.8881362E38)
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "Poll"
            r10 = 2131627642(0x7f0e0e7a, float:1.8882554E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x14b8:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupQuiz2"
            r7 = 2131627055(0x7f0e0c2f, float:1.8881364E38)
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "PollQuiz"
            r10 = 2131627649(0x7f0e0e81, float:1.8882568E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x14e2:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupContact2"
            r7 = 2131627043(0x7f0e0CLASSNAME, float:1.888134E38)
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachContact"
            r10 = 2131624495(0x7f0e022f, float:1.8876171E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x150c:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupAudio"
            r7 = 2131627042(0x7f0e0CLASSNAME, float:1.8881337E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachAudio"
            r10 = 2131624493(0x7f0e022d, float:1.8876167E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x1531:
            r8 = r17
            r9 = r25
            int r7 = r10.length     // Catch:{ all -> 0x1ea3 }
            r2 = 2
            if (r7 <= r2) goto L_0x157c
            r7 = r10[r2]     // Catch:{ all -> 0x1ea3 }
            boolean r7 = android.text.TextUtils.isEmpty(r7)     // Catch:{ all -> 0x1ea3 }
            if (r7 != 0) goto L_0x157c
            java.lang.String r7 = "NotificationMessageGroupStickerEmoji"
            r12 = 2131627058(0x7f0e0CLASSNAME, float:1.888137E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1ea3 }
            r17 = 0
            r18 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r2[r17] = r18     // Catch:{ all -> 0x1ea3 }
            r17 = 1
            r18 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r2[r17] = r18     // Catch:{ all -> 0x1ea3 }
            r17 = 2
            r18 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r2[r17] = r18     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r7, r12, r2)     // Catch:{ all -> 0x1ea3 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x1ea3 }
            r7.<init>()     // Catch:{ all -> 0x1ea3 }
            r10 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r7.append(r10)     // Catch:{ all -> 0x1ea3 }
            r7.append(r15)     // Catch:{ all -> 0x1ea3 }
            r10 = 2131624522(0x7f0e024a, float:1.8876226E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)     // Catch:{ all -> 0x1ea3 }
            r7.append(r10)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x157c:
            java.lang.String r7 = "NotificationMessageGroupSticker"
            r12 = 2131627057(0x7f0e0CLASSNAME, float:1.8881368E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1ea3 }
            r17 = 0
            r18 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r2[r17] = r18     // Catch:{ all -> 0x1ea3 }
            r17 = 1
            r18 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r2[r17] = r18     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r7, r12, r2)     // Catch:{ all -> 0x1ea3 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x1ea3 }
            r7.<init>()     // Catch:{ all -> 0x1ea3 }
            r10 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r7.append(r10)     // Catch:{ all -> 0x1ea3 }
            r7.append(r15)     // Catch:{ all -> 0x1ea3 }
            r10 = 2131624522(0x7f0e024a, float:1.8876226E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)     // Catch:{ all -> 0x1ea3 }
            r7.append(r10)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x15b1:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupDocument"
            r7 = 2131627044(0x7f0e0CLASSNAME, float:1.8881341E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachDocument"
            r10 = 2131624498(0x7f0e0232, float:1.8876177E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x15d6:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupRound"
            r7 = 2131627056(0x7f0e0CLASSNAME, float:1.8881366E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachRound"
            r10 = 2131624521(0x7f0e0249, float:1.8876224E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x15fb:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupVideo"
            r7 = 2131627060(0x7f0e0CLASSNAME, float:1.8881374E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachVideo"
            r10 = 2131624525(0x7f0e024d, float:1.8876232E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x1620:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            r7 = 2131627053(0x7f0e0c2d, float:1.888136E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachPhoto"
            r10 = 2131624519(0x7f0e0247, float:1.887622E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x1645:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupNoText"
            r7 = 2131627052(0x7f0e0c2c, float:1.8881358E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "Message"
            r10 = 2131626682(0x7f0e0aba, float:1.8880607E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x166a:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGroupText"
            r7 = 2131627059(0x7f0e0CLASSNAME, float:1.8881372E38)
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            r7 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x168d:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "ChannelMessageAlbum"
            r7 = 2131624938(0x7f0e03ea, float:1.887707E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a5
        L_0x16a4:
            r8 = r17
            r9 = r25
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1ea3 }
            r7 = 0
            r11 = r10[r7]     // Catch:{ all -> 0x1ea3 }
            r2[r7] = r11     // Catch:{ all -> 0x1ea3 }
            java.lang.String r11 = "Files"
            r15 = 1
            r10 = r10[r15]     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1ea3 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1ea3 }
            java.lang.Object[] r15 = new java.lang.Object[r7]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r11, r10, r15)     // Catch:{ all -> 0x1ea3 }
            r10 = 1
            r2[r10] = r7     // Catch:{ all -> 0x1ea3 }
            r7 = 2131624942(0x7f0e03ee, float:1.8877078E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r7, r2)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a5
        L_0x16cf:
            r8 = r17
            r9 = r25
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1ea3 }
            r7 = 0
            r11 = r10[r7]     // Catch:{ all -> 0x1ea3 }
            r2[r7] = r11     // Catch:{ all -> 0x1ea3 }
            java.lang.String r11 = "MusicFiles"
            r15 = 1
            r10 = r10[r15]     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1ea3 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1ea3 }
            java.lang.Object[] r15 = new java.lang.Object[r7]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r11, r10, r15)     // Catch:{ all -> 0x1ea3 }
            r10 = 1
            r2[r10] = r7     // Catch:{ all -> 0x1ea3 }
            r7 = 2131624942(0x7f0e03ee, float:1.8877078E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r7, r2)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a5
        L_0x16fa:
            r8 = r17
            r9 = r25
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1ea3 }
            r7 = 0
            r11 = r10[r7]     // Catch:{ all -> 0x1ea3 }
            r2[r7] = r11     // Catch:{ all -> 0x1ea3 }
            java.lang.String r11 = "Videos"
            r15 = 1
            r10 = r10[r15]     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1ea3 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1ea3 }
            java.lang.Object[] r15 = new java.lang.Object[r7]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r11, r10, r15)     // Catch:{ all -> 0x1ea3 }
            r10 = 1
            r2[r10] = r7     // Catch:{ all -> 0x1ea3 }
            r7 = 2131624942(0x7f0e03ee, float:1.8877078E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r7, r2)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a5
        L_0x1725:
            r8 = r17
            r9 = r25
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1ea3 }
            r7 = 0
            r11 = r10[r7]     // Catch:{ all -> 0x1ea3 }
            r2[r7] = r11     // Catch:{ all -> 0x1ea3 }
            java.lang.String r11 = "Photos"
            r15 = 1
            r10 = r10[r15]     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1ea3 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1ea3 }
            java.lang.Object[] r15 = new java.lang.Object[r7]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r11, r10, r15)     // Catch:{ all -> 0x1ea3 }
            r10 = 1
            r2[r10] = r7     // Catch:{ all -> 0x1ea3 }
            r7 = 2131624942(0x7f0e03ee, float:1.8877078E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r7, r2)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a5
        L_0x1750:
            r8 = r17
            r9 = r25
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1ea3 }
            r7 = 0
            r11 = r10[r7]     // Catch:{ all -> 0x1ea3 }
            r2[r7] = r11     // Catch:{ all -> 0x1ea3 }
            java.lang.String r11 = "ForwardedMessageCount"
            r15 = 1
            r10 = r10[r15]     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1ea3 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1ea3 }
            java.lang.Object[] r15 = new java.lang.Object[r7]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r11, r10, r15)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = r7.toLowerCase()     // Catch:{ all -> 0x1ea3 }
            r10 = 1
            r2[r10] = r7     // Catch:{ all -> 0x1ea3 }
            r7 = 2131624942(0x7f0e03ee, float:1.8877078E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r7, r2)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a5
        L_0x177f:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageGame"
            r7 = 2131627039(0x7f0e0c1f, float:1.8881331E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachGame"
            r10 = 2131624499(0x7f0e0233, float:1.887618E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x179f:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "ChannelMessageGIF"
            r7 = 2131624943(0x7f0e03ef, float:1.887708E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachGif"
            r10 = 2131624500(0x7f0e0234, float:1.8876181E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x17bf:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "ChannelMessageLiveLocation"
            r7 = 2131624944(0x7f0e03f0, float:1.8877082E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachLiveLocation"
            r10 = 2131624505(0x7f0e0239, float:1.8876192E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x17df:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "ChannelMessageMap"
            r7 = 2131624945(0x7f0e03f1, float:1.8877084E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachLocation"
            r10 = 2131624509(0x7f0e023d, float:1.88762E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x17ff:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "ChannelMessagePoll2"
            r7 = 2131624949(0x7f0e03f5, float:1.8877092E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "Poll"
            r10 = 2131627642(0x7f0e0e7a, float:1.8882554E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x1824:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "ChannelMessageQuiz2"
            r7 = 2131624950(0x7f0e03f6, float:1.8877094E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "QuizPoll"
            r10 = 2131627898(0x7f0e0f7a, float:1.8883073E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x1849:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "ChannelMessageContact2"
            r7 = 2131624940(0x7f0e03ec, float:1.8877074E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachContact"
            r10 = 2131624495(0x7f0e022f, float:1.8876171E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x186e:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "ChannelMessageAudio"
            r7 = 2131624939(0x7f0e03eb, float:1.8877072E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachAudio"
            r10 = 2131624493(0x7f0e022d, float:1.8876167E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x188e:
            r8 = r17
            r9 = r25
            int r7 = r10.length     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            if (r7 <= r12) goto L_0x18d3
            r7 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            boolean r7 = android.text.TextUtils.isEmpty(r7)     // Catch:{ all -> 0x1ea3 }
            if (r7 != 0) goto L_0x18d3
            java.lang.String r7 = "ChannelMessageStickerEmoji"
            r12 = 2131624953(0x7f0e03f9, float:1.88771E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x1ea3 }
            r17 = 0
            r18 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r2[r17] = r18     // Catch:{ all -> 0x1ea3 }
            r17 = 1
            r18 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r2[r17] = r18     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r7, r12, r2)     // Catch:{ all -> 0x1ea3 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x1ea3 }
            r7.<init>()     // Catch:{ all -> 0x1ea3 }
            r10 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r7.append(r10)     // Catch:{ all -> 0x1ea3 }
            r7.append(r15)     // Catch:{ all -> 0x1ea3 }
            r10 = 2131624522(0x7f0e024a, float:1.8876226E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)     // Catch:{ all -> 0x1ea3 }
            r7.append(r10)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x18d3:
            java.lang.String r2 = "ChannelMessageSticker"
            r7 = 2131624952(0x7f0e03f8, float:1.8877098E38)
            r12 = 1
            java.lang.Object[] r15 = new java.lang.Object[r12]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r15[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r15)     // Catch:{ all -> 0x1ea3 }
            r7 = 2131624522(0x7f0e024a, float:1.8876226E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r11, r7)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x18ed:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "ChannelMessageDocument"
            r7 = 2131624941(0x7f0e03ed, float:1.8877076E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachDocument"
            r10 = 2131624498(0x7f0e0232, float:1.8876177E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x190d:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "ChannelMessageRound"
            r7 = 2131624951(0x7f0e03f7, float:1.8877096E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachRound"
            r10 = 2131624521(0x7f0e0249, float:1.8876224E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x192c:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "ChannelMessageVideo"
            r7 = 2131624954(0x7f0e03fa, float:1.8877102E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachVideo"
            r10 = 2131624525(0x7f0e024d, float:1.8876232E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x194b:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "ChannelMessagePhoto"
            r7 = 2131624948(0x7f0e03f4, float:1.887709E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "AttachPhoto"
            r10 = 2131624519(0x7f0e0247, float:1.887622E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1988
        L_0x196a:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "ChannelMessageNoText"
            r7 = 2131624947(0x7f0e03f3, float:1.8877088E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = "Message"
            r10 = 2131626682(0x7f0e0aba, float:1.8880607E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r10)     // Catch:{ all -> 0x1ea3 }
        L_0x1988:
            r18 = r7
            r25 = 0
            r7 = r2
            r2 = r8
            goto L_0x1d9f
        L_0x1990:
            r8 = r17
            r9 = r25
            java.lang.String r2 = "NotificationMessageAlbum"
            r7 = 2131627033(0x7f0e0CLASSNAME, float:1.888132E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r7, r12)     // Catch:{ all -> 0x1ea3 }
        L_0x19a5:
            r7 = r2
            r2 = r8
        L_0x19a7:
            r18 = 0
            r25 = 1
            goto L_0x1d9f
        L_0x19ad:
            r2 = r17
            r9 = r25
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r12 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r7[r11] = r12     // Catch:{ all -> 0x1ea3 }
            java.lang.String r12 = "Files"
            r15 = 1
            r10 = r10[r15]     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1ea3 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1ea3 }
            java.lang.Object[] r15 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r12, r10, r15)     // Catch:{ all -> 0x1ea3 }
            r11 = 1
            r7[r11] = r10     // Catch:{ all -> 0x1ea3 }
            r10 = 2131627037(0x7f0e0c1d, float:1.8881327E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r10, r7)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a7
        L_0x19d7:
            r2 = r17
            r9 = r25
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r12 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r7[r11] = r12     // Catch:{ all -> 0x1ea3 }
            java.lang.String r12 = "MusicFiles"
            r15 = 1
            r10 = r10[r15]     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1ea3 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1ea3 }
            java.lang.Object[] r15 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r12, r10, r15)     // Catch:{ all -> 0x1ea3 }
            r11 = 1
            r7[r11] = r10     // Catch:{ all -> 0x1ea3 }
            r10 = 2131627037(0x7f0e0c1d, float:1.8881327E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r10, r7)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a7
        L_0x1a01:
            r2 = r17
            r9 = r25
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r12 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r7[r11] = r12     // Catch:{ all -> 0x1ea3 }
            java.lang.String r12 = "Videos"
            r15 = 1
            r10 = r10[r15]     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1ea3 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1ea3 }
            java.lang.Object[] r15 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r12, r10, r15)     // Catch:{ all -> 0x1ea3 }
            r11 = 1
            r7[r11] = r10     // Catch:{ all -> 0x1ea3 }
            r10 = 2131627037(0x7f0e0c1d, float:1.8881327E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r10, r7)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a7
        L_0x1a2c:
            r2 = r17
            r9 = r25
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r12 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r7[r11] = r12     // Catch:{ all -> 0x1ea3 }
            java.lang.String r12 = "Photos"
            r15 = 1
            r10 = r10[r15]     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1ea3 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1ea3 }
            java.lang.Object[] r15 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r10 = org.telegram.messenger.LocaleController.formatPluralString(r12, r10, r15)     // Catch:{ all -> 0x1ea3 }
            r11 = 1
            r7[r11] = r10     // Catch:{ all -> 0x1ea3 }
            r10 = 2131627037(0x7f0e0c1d, float:1.8881327E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r10, r7)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a7
        L_0x1a57:
            r2 = r17
            r9 = r25
            r7 = r26
            java.lang.String r8 = "NotificationMessageForwardFew"
            r12 = 2
            java.lang.Object[] r12 = new java.lang.Object[r12]     // Catch:{ all -> 0x1ea3 }
            r15 = 0
            r17 = r10[r15]     // Catch:{ all -> 0x1ea3 }
            r12[r15] = r17     // Catch:{ all -> 0x1ea3 }
            r17 = 1
            r10 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            java.lang.Integer r10 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r10)     // Catch:{ all -> 0x1ea3 }
            int r10 = r10.intValue()     // Catch:{ all -> 0x1ea3 }
            java.lang.Object[] r11 = new java.lang.Object[r15]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r10, r11)     // Catch:{ all -> 0x1ea3 }
            r12[r17] = r7     // Catch:{ all -> 0x1ea3 }
            r7 = 2131627038(0x7f0e0c1e, float:1.888133E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r7, r12)     // Catch:{ all -> 0x1ea3 }
            goto L_0x19a7
        L_0x1a84:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageInvoice"
            r8 = 2131627061(0x7f0e0CLASSNAME, float:1.8881376E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "PaymentInvoice"
            r10 = 2131627458(0x7f0e0dc2, float:1.888218E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1aa9:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageGameScored"
            r8 = 2131627040(0x7f0e0CLASSNAME, float:1.8881333E38)
            r11 = 3
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r17 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r17     // Catch:{ all -> 0x1ea3 }
            r12 = 2
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d9b
        L_0x1aca:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageGame"
            r8 = 2131627039(0x7f0e0c1f, float:1.8881331E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "AttachGame"
            r10 = 2131624499(0x7f0e0233, float:1.887618E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1aef:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageGif"
            r8 = 2131627041(0x7f0e0CLASSNAME, float:1.8881335E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "AttachGif"
            r10 = 2131624500(0x7f0e0234, float:1.8876181E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1b0f:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageLiveLocation"
            r8 = 2131627062(0x7f0e0CLASSNAME, float:1.8881378E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "AttachLiveLocation"
            r10 = 2131624505(0x7f0e0239, float:1.8876192E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1b2f:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageMap"
            r8 = 2131627063(0x7f0e0CLASSNAME, float:1.888138E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "AttachLocation"
            r10 = 2131624509(0x7f0e023d, float:1.88762E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1b4f:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessagePoll2"
            r8 = 2131627067(0x7f0e0c3b, float:1.8881388E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "Poll"
            r10 = 2131627642(0x7f0e0e7a, float:1.8882554E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1b74:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageQuiz2"
            r8 = 2131627068(0x7f0e0c3c, float:1.888139E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "QuizPoll"
            r10 = 2131627898(0x7f0e0f7a, float:1.8883073E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1b99:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageContact2"
            r8 = 2131627035(0x7f0e0c1b, float:1.8881323E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "AttachContact"
            r10 = 2131624495(0x7f0e022f, float:1.8876171E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1bbe:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageAudio"
            r8 = 2131627034(0x7f0e0c1a, float:1.8881321E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "AttachAudio"
            r10 = 2131624493(0x7f0e022d, float:1.8876167E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1bde:
            r2 = r17
            r9 = r25
            int r8 = r10.length     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            if (r8 <= r12) goto L_0x1CLASSNAME
            r8 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            boolean r8 = android.text.TextUtils.isEmpty(r8)     // Catch:{ all -> 0x1ea3 }
            if (r8 != 0) goto L_0x1CLASSNAME
            java.lang.String r8 = "NotificationMessageStickerEmoji"
            r12 = 2131627076(0x7f0e0CLASSNAME, float:1.8881406E38)
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ all -> 0x1ea3 }
            r17 = 0
            r18 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r7[r17] = r18     // Catch:{ all -> 0x1ea3 }
            r17 = 1
            r18 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r7[r17] = r18     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r12, r7)     // Catch:{ all -> 0x1ea3 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x1ea3 }
            r8.<init>()     // Catch:{ all -> 0x1ea3 }
            r10 = r10[r17]     // Catch:{ all -> 0x1ea3 }
            r8.append(r10)     // Catch:{ all -> 0x1ea3 }
            r8.append(r15)     // Catch:{ all -> 0x1ea3 }
            r10 = 2131624522(0x7f0e024a, float:1.8876226E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)     // Catch:{ all -> 0x1ea3 }
            r8.append(r10)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1CLASSNAME:
            java.lang.String r7 = "NotificationMessageSticker"
            r8 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            r12 = 1
            java.lang.Object[] r15 = new java.lang.Object[r12]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r15[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r15)     // Catch:{ all -> 0x1ea3 }
            r8 = 2131624522(0x7f0e024a, float:1.8876226E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1c3d:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageDocument"
            r8 = 2131627036(0x7f0e0c1c, float:1.8881325E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "AttachDocument"
            r10 = 2131624498(0x7f0e0232, float:1.8876177E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1c5d:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageRound"
            r8 = 2131627070(0x7f0e0c3e, float:1.8881394E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "AttachRound"
            r10 = 2131624521(0x7f0e0249, float:1.8876224E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1c7d:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "ActionTakeScreenshoot"
            r8 = 2131624221(0x7f0e011d, float:1.8875616E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r8)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "un1"
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = r7.replace(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d9b
        L_0x1CLASSNAME:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageSDVideo"
            r8 = 2131627072(0x7f0e0CLASSNAME, float:1.8881398E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "AttachDestructingVideo"
            r10 = 2131624497(0x7f0e0231, float:1.8876175E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1cb5:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageVideo"
            r8 = 2131627078(0x7f0e0CLASSNAME, float:1.888141E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "AttachVideo"
            r10 = 2131624525(0x7f0e024d, float:1.8876232E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1cd4:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageSDPhoto"
            r8 = 2131627071(0x7f0e0c3f, float:1.8881396E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "AttachDestructingPhoto"
            r10 = 2131624496(0x7f0e0230, float:1.8876173E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1cf3:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessagePhoto"
            r8 = 2131627066(0x7f0e0c3a, float:1.8881386E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "AttachPhoto"
            r10 = 2131624519(0x7f0e0247, float:1.887622E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1d12:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageNoText"
            r8 = 2131627065(0x7f0e0CLASSNAME, float:1.8881384E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r11 = 0
            r10 = r10[r11]     // Catch:{ all -> 0x1ea3 }
            r12[r11] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r12)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "Message"
            r10 = 2131626682(0x7f0e0aba, float:1.8880607E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
        L_0x1d30:
            r18 = r8
            goto L_0x1d9d
        L_0x1d33:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageText"
            r8 = 2131627077(0x7f0e0CLASSNAME, float:1.8881408E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x1ea3 }
            r8 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1d50:
            r2 = r17
            r9 = r25
            java.lang.String r7 = "NotificationMessageRecurringPay"
            r8 = 2131627069(0x7f0e0c3d, float:1.8881392E38)
            r11 = 2
            java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ all -> 0x1ea3 }
            r12 = 0
            r15 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r15     // Catch:{ all -> 0x1ea3 }
            r12 = 1
            r10 = r10[r12]     // Catch:{ all -> 0x1ea3 }
            r11[r12] = r10     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r11)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "PaymentInvoice"
            r10 = 2131627458(0x7f0e0dc2, float:1.888218E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r10)     // Catch:{ all -> 0x1ea3 }
            goto L_0x1d30
        L_0x1d74:
            if (r7 == 0) goto L_0x1d8a
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x1ea3 }
            r7.<init>()     // Catch:{ all -> 0x1ea3 }
            java.lang.String r8 = "unhandled loc_key = "
            r7.append(r8)     // Catch:{ all -> 0x1ea3 }
            r7.append(r14)     // Catch:{ all -> 0x1ea3 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x1ea3 }
            org.telegram.messenger.FileLog.w(r7)     // Catch:{ all -> 0x1ea3 }
        L_0x1d8a:
            r7 = 0
            goto L_0x1d9b
        L_0x1d8c:
            r31 = r2
            r44 = r8
            r46 = r11
            r45 = r12
            r2 = r15
            r9 = r25
            java.lang.String r7 = getReactedText(r14, r10)     // Catch:{ all -> 0x1ea3 }
        L_0x1d9b:
            r18 = 0
        L_0x1d9d:
            r25 = 0
        L_0x1d9f:
            if (r7 == 0) goto L_0x1e8f
            org.telegram.tgnet.TLRPC$TL_message r8 = new org.telegram.tgnet.TLRPC$TL_message     // Catch:{ all -> 0x1ea3 }
            r8.<init>()     // Catch:{ all -> 0x1ea3 }
            r8.id = r1     // Catch:{ all -> 0x1ea3 }
            r10 = r27
            r8.random_id = r10     // Catch:{ all -> 0x1ea3 }
            if (r18 == 0) goto L_0x1db1
            r1 = r18
            goto L_0x1db2
        L_0x1db1:
            r1 = r7
        L_0x1db2:
            r8.message = r1     // Catch:{ all -> 0x1ea3 }
            r10 = 1000(0x3e8, double:4.94E-321)
            long r10 = r51 / r10
            int r1 = (int) r10     // Catch:{ all -> 0x1ea3 }
            r8.date = r1     // Catch:{ all -> 0x1ea3 }
            if (r16 == 0) goto L_0x1dc4
            org.telegram.tgnet.TLRPC$TL_messageActionPinMessage r1 = new org.telegram.tgnet.TLRPC$TL_messageActionPinMessage     // Catch:{ all -> 0x1ea3 }
            r1.<init>()     // Catch:{ all -> 0x1ea3 }
            r8.action = r1     // Catch:{ all -> 0x1ea3 }
        L_0x1dc4:
            if (r13 == 0) goto L_0x1dcd
            int r1 = r8.flags     // Catch:{ all -> 0x1ea3 }
            r10 = -2147483648(0xfffffffvar_, float:-0.0)
            r1 = r1 | r10
            r8.flags = r1     // Catch:{ all -> 0x1ea3 }
        L_0x1dcd:
            r8.dialog_id = r3     // Catch:{ all -> 0x1ea3 }
            r3 = 0
            int r1 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x1de1
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x1ea3 }
            r1.<init>()     // Catch:{ all -> 0x1ea3 }
            r8.peer_id = r1     // Catch:{ all -> 0x1ea3 }
            r1.channel_id = r5     // Catch:{ all -> 0x1ea3 }
            r3 = r23
            goto L_0x1e00
        L_0x1de1:
            r3 = 0
            int r1 = (r23 > r3 ? 1 : (r23 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x1df3
            org.telegram.tgnet.TLRPC$TL_peerChat r1 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x1ea3 }
            r1.<init>()     // Catch:{ all -> 0x1ea3 }
            r8.peer_id = r1     // Catch:{ all -> 0x1ea3 }
            r3 = r23
            r1.chat_id = r3     // Catch:{ all -> 0x1ea3 }
            goto L_0x1e00
        L_0x1df3:
            r3 = r23
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1ea3 }
            r1.<init>()     // Catch:{ all -> 0x1ea3 }
            r8.peer_id = r1     // Catch:{ all -> 0x1ea3 }
            r5 = r32
            r1.user_id = r5     // Catch:{ all -> 0x1ea3 }
        L_0x1e00:
            int r1 = r8.flags     // Catch:{ all -> 0x1ea3 }
            r1 = r1 | 256(0x100, float:3.59E-43)
            r8.flags = r1     // Catch:{ all -> 0x1ea3 }
            r5 = 0
            int r1 = (r41 > r5 ? 1 : (r41 == r5 ? 0 : -1))
            if (r1 == 0) goto L_0x1e16
            org.telegram.tgnet.TLRPC$TL_peerChat r1 = new org.telegram.tgnet.TLRPC$TL_peerChat     // Catch:{ all -> 0x1ea3 }
            r1.<init>()     // Catch:{ all -> 0x1ea3 }
            r8.from_id = r1     // Catch:{ all -> 0x1ea3 }
            r1.chat_id = r3     // Catch:{ all -> 0x1ea3 }
            goto L_0x1e3e
        L_0x1e16:
            r3 = 0
            int r1 = (r35 > r3 ? 1 : (r35 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x1e28
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel     // Catch:{ all -> 0x1ea3 }
            r1.<init>()     // Catch:{ all -> 0x1ea3 }
            r8.from_id = r1     // Catch:{ all -> 0x1ea3 }
            r3 = r35
            r1.channel_id = r3     // Catch:{ all -> 0x1ea3 }
            goto L_0x1e3e
        L_0x1e28:
            r3 = 0
            int r1 = (r39 > r3 ? 1 : (r39 == r3 ? 0 : -1))
            if (r1 == 0) goto L_0x1e3a
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser     // Catch:{ all -> 0x1ea3 }
            r1.<init>()     // Catch:{ all -> 0x1ea3 }
            r8.from_id = r1     // Catch:{ all -> 0x1ea3 }
            r3 = r39
            r1.user_id = r3     // Catch:{ all -> 0x1ea3 }
            goto L_0x1e3e
        L_0x1e3a:
            org.telegram.tgnet.TLRPC$Peer r1 = r8.peer_id     // Catch:{ all -> 0x1ea3 }
            r8.from_id = r1     // Catch:{ all -> 0x1ea3 }
        L_0x1e3e:
            if (r37 != 0) goto L_0x1e45
            if (r16 == 0) goto L_0x1e43
            goto L_0x1e45
        L_0x1e43:
            r1 = 0
            goto L_0x1e46
        L_0x1e45:
            r1 = 1
        L_0x1e46:
            r8.mentioned = r1     // Catch:{ all -> 0x1ea3 }
            r1 = r38
            r8.silent = r1     // Catch:{ all -> 0x1ea3 }
            r8.from_scheduled = r9     // Catch:{ all -> 0x1ea3 }
            org.telegram.messenger.MessageObject r1 = new org.telegram.messenger.MessageObject     // Catch:{ all -> 0x1ea3 }
            r19 = r1
            r20 = r29
            r21 = r8
            r22 = r7
            r23 = r43
            r24 = r46
            r26 = r45
            r27 = r13
            r28 = r44
            r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27, r28)     // Catch:{ all -> 0x1ea3 }
            r3 = r31
            boolean r3 = r14.startsWith(r3)     // Catch:{ all -> 0x1ea3 }
            if (r3 != 0) goto L_0x1e76
            boolean r2 = r14.startsWith(r2)     // Catch:{ all -> 0x1ea3 }
            if (r2 == 0) goto L_0x1e74
            goto L_0x1e76
        L_0x1e74:
            r2 = 0
            goto L_0x1e77
        L_0x1e76:
            r2 = 1
        L_0x1e77:
            r1.isReactionPush = r2     // Catch:{ all -> 0x1ea3 }
            java.util.ArrayList r2 = new java.util.ArrayList     // Catch:{ all -> 0x1ea3 }
            r2.<init>()     // Catch:{ all -> 0x1ea3 }
            r2.add(r1)     // Catch:{ all -> 0x1ea3 }
            org.telegram.messenger.NotificationsController r1 = org.telegram.messenger.NotificationsController.getInstance(r29)     // Catch:{ all -> 0x1ea3 }
            java.util.concurrent.CountDownLatch r3 = countDownLatch     // Catch:{ all -> 0x1ea3 }
            r4 = 1
            r1.processNewMessages(r2, r4, r4, r3)     // Catch:{ all -> 0x1ea3 }
            r8 = 0
            goto L_0x1e90
        L_0x1e8d:
            r30 = r7
        L_0x1e8f:
            r8 = 1
        L_0x1e90:
            if (r8 == 0) goto L_0x1e97
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1ea3 }
            r1.countDown()     // Catch:{ all -> 0x1ea3 }
        L_0x1e97:
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r29)     // Catch:{ all -> 0x1ea3 }
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r29)     // Catch:{ all -> 0x1ea3 }
            r1.resumeNetworkMaybe()     // Catch:{ all -> 0x1ea3 }
            goto L_0x1fce
        L_0x1ea3:
            r0 = move-exception
            r1 = r0
            r5 = r14
            r4 = r29
            goto L_0x1var_
        L_0x1eaa:
            r0 = move-exception
            r30 = r7
            goto L_0x1eb2
        L_0x1eae:
            r0 = move-exception
            r30 = r7
        L_0x1eb1:
            r14 = r9
        L_0x1eb2:
            r1 = r0
            r5 = r14
        L_0x1eb4:
            r4 = r29
            goto L_0x1f7d
        L_0x1eb8:
            r0 = move-exception
            r29 = r4
            goto L_0x1var_
        L_0x1ebd:
            r29 = r4
            r30 = r7
            r14 = r9
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ all -> 0x1ed4 }
            org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda0 r2 = new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda0     // Catch:{ all -> 0x1ed4 }
            r4 = r29
            r2.<init>(r4)     // Catch:{ all -> 0x1var_ }
            r1.postRunnable(r2)     // Catch:{ all -> 0x1var_ }
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1var_ }
            r1.countDown()     // Catch:{ all -> 0x1var_ }
            return
        L_0x1ed4:
            r0 = move-exception
            r4 = r29
            goto L_0x1var_
        L_0x1ed9:
            r30 = r7
            r14 = r9
            org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda1 r1 = new org.telegram.messenger.PushListenerController$$ExternalSyntheticLambda1     // Catch:{ all -> 0x1var_ }
            r1.<init>(r4)     // Catch:{ all -> 0x1var_ }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)     // Catch:{ all -> 0x1var_ }
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1var_ }
            r1.countDown()     // Catch:{ all -> 0x1var_ }
            return
        L_0x1eea:
            r30 = r7
            r14 = r9
            r9 = r6
            org.telegram.tgnet.TLRPC$TL_updateServiceNotification r1 = new org.telegram.tgnet.TLRPC$TL_updateServiceNotification     // Catch:{ all -> 0x1var_ }
            r1.<init>()     // Catch:{ all -> 0x1var_ }
            r2 = 0
            r1.popup = r2     // Catch:{ all -> 0x1var_ }
            r2 = 2
            r1.flags = r2     // Catch:{ all -> 0x1var_ }
            r2 = 1000(0x3e8, double:4.94E-321)
            long r2 = r51 / r2
            int r3 = (int) r2     // Catch:{ all -> 0x1var_ }
            r1.inbox_date = r3     // Catch:{ all -> 0x1var_ }
            java.lang.String r2 = "message"
            java.lang.String r2 = r9.getString(r2)     // Catch:{ all -> 0x1var_ }
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
        L_0x1var_:
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
            if (r3 == r5) goto L_0x1var_
            java.util.concurrent.CountDownLatch r1 = countDownLatch     // Catch:{ all -> 0x1var_ }
            r1.countDown()     // Catch:{ all -> 0x1var_ }
            return
        L_0x1var_:
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
            goto L_0x1f7d
        L_0x1var_:
            r0 = move-exception
        L_0x1var_:
            r30 = r7
            r14 = r9
            r1 = r0
            r5 = r14
        L_0x1f7d:
            r2 = -1
            goto L_0x1var_
        L_0x1f7f:
            r0 = move-exception
            r30 = r7
            r14 = r9
            r1 = r0
            r5 = r14
        L_0x1var_:
            r2 = -1
            r4 = -1
            goto L_0x1var_
        L_0x1var_:
            r0 = move-exception
            r30 = r7
        L_0x1f8b:
            r1 = r0
            r2 = -1
            r4 = -1
            r5 = 0
            goto L_0x1var_
        L_0x1var_:
            r0 = move-exception
            r1 = r0
            r2 = -1
            r4 = -1
            r5 = 0
            r7 = 0
        L_0x1var_:
            if (r4 == r2) goto L_0x1fa8
            org.telegram.tgnet.ConnectionsManager.onInternalPushReceived(r4)
            org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            r2.resumeNetworkMaybe()
            java.util.concurrent.CountDownLatch r2 = countDownLatch
            r2.countDown()
            goto L_0x1fab
        L_0x1fa8:
            onDecryptError()
        L_0x1fab:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x1fcb
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
        L_0x1fcb:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x1fce:
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
                return LocaleController.formatString("PushChatReactContact", NUM, objArr);
            case 1:
                return LocaleController.formatString("PushReactGeoLocation", NUM, objArr);
            case 2:
                return LocaleController.formatString("PushChatReactNotext", NUM, objArr);
            case 3:
                return LocaleController.formatString("PushReactNoText", NUM, objArr);
            case 4:
                return LocaleController.formatString("PushChatReactInvoice", NUM, objArr);
            case 5:
                return LocaleController.formatString("PushReactContect", NUM, objArr);
            case 6:
                return LocaleController.formatString("PushChatReactSticker", NUM, objArr);
            case 7:
                return LocaleController.formatString("PushReactGame", NUM, objArr);
            case 8:
                return LocaleController.formatString("PushReactPoll", NUM, objArr);
            case 9:
                return LocaleController.formatString("PushReactQuiz", NUM, objArr);
            case 10:
                return LocaleController.formatString("PushReactText", NUM, objArr);
            case 11:
                return LocaleController.formatString("PushReactInvoice", NUM, objArr);
            case 12:
                return LocaleController.formatString("PushChatReactDoc", NUM, objArr);
            case 13:
                return LocaleController.formatString("PushChatReactGeo", NUM, objArr);
            case 14:
                return LocaleController.formatString("PushChatReactGif", NUM, objArr);
            case 15:
                return LocaleController.formatString("PushReactSticker", NUM, objArr);
            case 16:
                return LocaleController.formatString("PushChatReactAudio", NUM, objArr);
            case 17:
                return LocaleController.formatString("PushChatReactPhoto", NUM, objArr);
            case 18:
                return LocaleController.formatString("PushChatReactRound", NUM, objArr);
            case 19:
                return LocaleController.formatString("PushChatReactVideo", NUM, objArr);
            case 20:
                return LocaleController.formatString("PushChatReactGeoLive", NUM, objArr);
            case 21:
                return LocaleController.formatString("PushReactAudio", NUM, objArr);
            case 22:
                return LocaleController.formatString("PushReactPhoto", NUM, objArr);
            case 23:
                return LocaleController.formatString("PushReactRound", NUM, objArr);
            case 24:
                return LocaleController.formatString("PushReactVideo", NUM, objArr);
            case 25:
                return LocaleController.formatString("PushReactDoc", NUM, objArr);
            case 26:
                return LocaleController.formatString("PushReactGeo", NUM, objArr);
            case 27:
                return LocaleController.formatString("PushReactGif", NUM, objArr);
            case 28:
                return LocaleController.formatString("PushChatReactGame", NUM, objArr);
            case 29:
                return LocaleController.formatString("PushChatReactPoll", NUM, objArr);
            case 30:
                return LocaleController.formatString("PushChatReactQuiz", NUM, objArr);
            case 31:
                return LocaleController.formatString("PushChatReactText", NUM, objArr);
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
}
