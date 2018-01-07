package org.telegram.messenger;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import com.google.android.gms.gcm.GcmListenerService;
import org.json.JSONObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_updateServiceNotification;
import org.telegram.tgnet.TLRPC.TL_updates;

public class GcmPushListenerService extends GcmListenerService {
    public static final int NOTIFICATION_ID = 1;

    public void onMessageReceived(String from, final Bundle bundle) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("GCM received bundle: " + bundle + " from: " + from);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int userId;
                ApplicationLoader.postInitApplication();
                Object userIdObject = bundle.get("user_id");
                if (userIdObject == null) {
                    userId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                } else if (userIdObject instanceof Integer) {
                    userId = ((Integer) userIdObject).intValue();
                } else if (userIdObject instanceof String) {
                    userId = Utilities.parseInt((String) userIdObject).intValue();
                } else {
                    userId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
                }
                int account = UserConfig.selectedAccount;
                for (int a = 0; a < 3; a++) {
                    if (UserConfig.getInstance(a).getClientUserId() == userId) {
                        account = a;
                        break;
                    }
                }
                final int currentAccount = account;
                try {
                    String key = bundle.getString("loc_key");
                    if ("DC_UPDATE".equals(key)) {
                        JSONObject jSONObject = new JSONObject(bundle.getString("custom"));
                        int dc = jSONObject.getInt("dc");
                        String[] parts = jSONObject.getString("addr").split(":");
                        if (parts.length == 2) {
                            ConnectionsManager.getInstance(currentAccount).applyDatacenterAddress(dc, parts[0], Integer.parseInt(parts[1]));
                            ConnectionsManager.onInternalPushReceived(currentAccount);
                            ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                        }
                        return;
                    }
                    Object obj;
                    long time;
                    if ("MESSAGE_ANNOUNCEMENT".equals(key)) {
                        obj = bundle.get("google.sent_time");
                        try {
                            if (obj instanceof String) {
                                time = Utilities.parseLong((String) obj).longValue();
                            } else if (obj instanceof Long) {
                                time = ((Long) obj).longValue();
                            } else {
                                time = System.currentTimeMillis();
                            }
                        } catch (Exception e) {
                            time = System.currentTimeMillis();
                        }
                        TL_updateServiceNotification update = new TL_updateServiceNotification();
                        update.popup = false;
                        update.flags = 2;
                        update.inbox_date = (int) (time / 1000);
                        update.message = bundle.getString("message");
                        update.type = "announcement";
                        update.media = new TL_messageMediaEmpty();
                        TL_updates updates = new TL_updates();
                        updates.updates.add(update);
                        final TL_updates tL_updates = updates;
                        Utilities.stageQueue.postRunnable(new Runnable() {
                            public void run() {
                                MessagesController.getInstance(currentAccount).processUpdates(tL_updates, false);
                            }
                        });
                    } else if (VERSION.SDK_INT >= 24 && ApplicationLoader.mainInterfacePaused && UserConfig.getInstance(currentAccount).isClientActivated() && bundle.get("badge") == null) {
                        obj = bundle.get("google.sent_time");
                        if (obj instanceof String) {
                            time = Utilities.parseLong((String) obj).longValue();
                        } else if (obj instanceof Long) {
                            time = ((Long) obj).longValue();
                        } else {
                            time = -1;
                        }
                        if (time == -1 || SharedConfig.lastAppPauseTime < time) {
                            ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext.getSystemService("connectivity");
                            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("try show notification in background with time " + time + " with nework info " + netInfo + " and status " + connectivityManager.getRestrictBackgroundStatus());
                            }
                            if (connectivityManager.getRestrictBackgroundStatus() == 3 && netInfo.getType() == 0) {
                                NotificationsController.getInstance(currentAccount).showSingleBackgroundNotification();
                            }
                        }
                    }
                    ConnectionsManager.onInternalPushReceived(currentAccount);
                    ConnectionsManager.getInstance(currentAccount).resumeNetworkMaybe();
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
            }
        });
    }
}
