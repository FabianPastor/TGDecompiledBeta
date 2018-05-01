package org.telegram.messenger.voip;

import android.annotation.TargetApi;
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
            FileLog.m4w("ConnectionService created");
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m4w("ConnectionService destroyed");
        }
    }

    public Connection onCreateIncomingConnection(PhoneAccountHandle phoneAccountHandle, ConnectionRequest connectionRequest) {
        if (BuildVars.LOGS_ENABLED != null) {
            FileLog.m0d("onCreateIncomingConnection ");
        }
        phoneAccountHandle = connectionRequest.getExtras();
        if (phoneAccountHandle.getInt("call_type") == 1) {
            phoneAccountHandle = VoIPService.getSharedInstance();
            if (phoneAccountHandle != null && phoneAccountHandle.isOutgoing() == null) {
                return phoneAccountHandle.getConnectionAndStartCall();
            }
            return null;
        }
        phoneAccountHandle.getInt("call_type");
        return null;
    }

    public void onCreateIncomingConnectionFailed(PhoneAccountHandle phoneAccountHandle, ConnectionRequest connectionRequest) {
        if (BuildVars.LOGS_ENABLED != null) {
            FileLog.m1e("onCreateIncomingConnectionFailed ");
        }
        if (VoIPBaseService.getSharedInstance() != null) {
            VoIPBaseService.getSharedInstance().callFailedFromConnectionService();
        }
    }

    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle phoneAccountHandle, ConnectionRequest connectionRequest) {
        if (BuildVars.LOGS_ENABLED != null) {
            FileLog.m1e("onCreateOutgoingConnectionFailed ");
        }
        if (VoIPBaseService.getSharedInstance() != null) {
            VoIPBaseService.getSharedInstance().callFailedFromConnectionService();
        }
    }

    public Connection onCreateOutgoingConnection(PhoneAccountHandle phoneAccountHandle, ConnectionRequest connectionRequest) {
        if (BuildVars.LOGS_ENABLED != null) {
            FileLog.m0d("onCreateOutgoingConnection ");
        }
        phoneAccountHandle = connectionRequest.getExtras();
        if (phoneAccountHandle.getInt("call_type") == 1) {
            phoneAccountHandle = VoIPService.getSharedInstance();
            if (phoneAccountHandle == null) {
                return null;
            }
            return phoneAccountHandle.getConnectionAndStartCall();
        }
        phoneAccountHandle.getInt("call_type");
        return null;
    }
}
