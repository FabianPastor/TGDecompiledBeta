package org.telegram.messenger.exoplayer2.scheduler;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.PowerManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.util.Util;

public final class Requirements {
    private static final int DEVICE_CHARGING = 16;
    private static final int DEVICE_IDLE = 8;
    public static final int NETWORK_TYPE_ANY = 1;
    private static final int NETWORK_TYPE_MASK = 7;
    public static final int NETWORK_TYPE_METERED = 4;
    public static final int NETWORK_TYPE_NONE = 0;
    public static final int NETWORK_TYPE_NOT_ROAMING = 3;
    private static final String[] NETWORK_TYPE_STRINGS = null;
    public static final int NETWORK_TYPE_UNMETERED = 2;
    private static final String TAG = "Requirements";
    private final int requirements;

    @Retention(RetentionPolicy.SOURCE)
    public @interface NetworkType {
    }

    public Requirements(int networkType, boolean charging, boolean idle) {
        int i;
        int i2 = 0;
        if (charging) {
            i = 16;
        } else {
            i = 0;
        }
        i |= networkType;
        if (idle) {
            i2 = 8;
        }
        this(i2 | i);
    }

    public Requirements(int requirementsData) {
        this.requirements = requirementsData;
    }

    public int getRequiredNetworkType() {
        return this.requirements & 7;
    }

    public boolean isChargingRequired() {
        return (this.requirements & 16) != 0;
    }

    public boolean isIdleRequired() {
        return (this.requirements & 8) != 0;
    }

    public boolean checkRequirements(Context context) {
        return checkNetworkRequirements(context) && checkChargingRequirement(context) && checkIdleRequirement(context);
    }

    public int getRequirementsData() {
        return this.requirements;
    }

    private boolean checkNetworkRequirements(Context context) {
        int networkRequirement = getRequiredNetworkType();
        if (networkRequirement == 0) {
            return true;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            logd("No network info or no connection.");
            return false;
        } else if (!checkInternetConnectivity(connectivityManager)) {
            return false;
        } else {
            if (networkRequirement == 1) {
                return true;
            }
            if (networkRequirement == 3) {
                boolean roaming = networkInfo.isRoaming();
                logd("Roaming: " + roaming);
                if (roaming) {
                    return false;
                }
                return true;
            }
            boolean activeNetworkMetered = isActiveNetworkMetered(connectivityManager, networkInfo);
            logd("Metered network: " + activeNetworkMetered);
            if (networkRequirement == 2) {
                if (activeNetworkMetered) {
                    return false;
                }
                return true;
            } else if (networkRequirement == 4) {
                return activeNetworkMetered;
            } else {
                throw new IllegalStateException();
            }
        }
    }

    private boolean checkChargingRequirement(Context context) {
        if (!isChargingRequired()) {
            return true;
        }
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (batteryStatus == null) {
            return false;
        }
        int status = batteryStatus.getIntExtra("status", -1);
        if (status == 2 || status == 5) {
            return true;
        }
        return false;
    }

    private boolean checkIdleRequirement(Context context) {
        if (!isIdleRequired()) {
            return true;
        }
        PowerManager powerManager = (PowerManager) context.getSystemService("power");
        if (Util.SDK_INT >= 23) {
            if (powerManager.isDeviceIdleMode()) {
                return false;
            }
            return true;
        } else if (Util.SDK_INT >= 20) {
            if (powerManager.isInteractive()) {
                return false;
            }
            return true;
        } else if (powerManager.isScreenOn()) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean checkInternetConnectivity(ConnectivityManager connectivityManager) {
        if (Util.SDK_INT < 23) {
            return true;
        }
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) {
            logd("No active network.");
            return false;
        }
        boolean validated;
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        if (networkCapabilities == null || !networkCapabilities.hasCapability(16)) {
            validated = true;
        } else {
            validated = false;
        }
        logd("Network capability validated: " + validated);
        if (validated) {
            return false;
        }
        return true;
    }

    private static boolean isActiveNetworkMetered(ConnectivityManager connectivityManager, NetworkInfo networkInfo) {
        if (Util.SDK_INT >= 16) {
            return connectivityManager.isActiveNetworkMetered();
        }
        int type = networkInfo.getType();
        if (type == 1 || type == 7 || type == 9) {
            return false;
        }
        return true;
    }

    private static void logd(String message) {
    }

    public String toString() {
        return super.toString();
    }
}
