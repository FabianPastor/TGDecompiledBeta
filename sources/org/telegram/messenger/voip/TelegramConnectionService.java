package org.telegram.messenger.voip;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.ConnectionService;
import android.telecom.PhoneAccountHandle;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

@TargetApi(26)
public class TelegramConnectionService extends ConnectionService {
    public void onCreate() {
        super.onCreate();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m14w("ConnectionService created");
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m14w("ConnectionService destroyed");
        }
    }

    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m10d("onCreateIncomingConnection ");
        }
        Bundle extras = request.getExtras();
        if (extras.getInt("call_type") == 1) {
            VoIPService svc = VoIPService.getSharedInstance();
            if (svc == null || svc.isOutgoing()) {
                return null;
            }
            return svc.getConnectionAndStartCall();
        }
        if (extras.getInt("call_type") == 2) {
        }
        return null;
    }

    public void onCreateIncomingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m11e("onCreateIncomingConnectionFailed ");
        }
        if (VoIPBaseService.getSharedInstance() != null) {
            VoIPBaseService.getSharedInstance().callFailedFromConnectionService();
        }
    }

    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m11e("onCreateOutgoingConnectionFailed ");
        }
        if (VoIPBaseService.getSharedInstance() != null) {
            VoIPBaseService.getSharedInstance().callFailedFromConnectionService();
        }
    }

    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m10d("onCreateOutgoingConnection ");
        }
        Bundle extras = request.getExtras();
        if (extras.getInt("call_type") == 1) {
            VoIPService svc = VoIPService.getSharedInstance();
            if (svc == null) {
                return null;
            }
            return svc.getConnectionAndStartCall();
        }
        if (extras.getInt("call_type") == 2) {
        }
        return null;
    }
}
