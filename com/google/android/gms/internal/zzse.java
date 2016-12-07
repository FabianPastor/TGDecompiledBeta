package com.google.android.gms.internal;

import android.os.Build.VERSION;
import android.util.Log;
import com.google.android.gms.common.internal.zzq;
import org.telegram.tgnet.ConnectionsManager;

public class zzse {
    private final String CR;
    private final zzq Dk;
    private final int bX;
    private final String mTag;

    private zzse(String str, String str2) {
        this.CR = str2;
        this.mTag = str;
        this.Dk = new zzq(str);
        this.bX = getLogLevel();
    }

    public zzse(String str, String... strArr) {
        this(str, zzd(strArr));
    }

    private int getLogLevel() {
        int i;
        if (VERSION.SDK_INT == 23) {
            String str = "log.tag.";
            String valueOf = String.valueOf(this.mTag);
            str = System.getProperty(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
            if (str != null) {
                Object obj = -1;
                switch (str.hashCode()) {
                    case -880503115:
                        if (str.equals("SUPPRESS")) {
                            i = 6;
                            break;
                        }
                        break;
                    case 2251950:
                        if (str.equals("INFO")) {
                            i = 2;
                            break;
                        }
                        break;
                    case 2656902:
                        if (str.equals("WARN")) {
                            i = 3;
                            break;
                        }
                        break;
                    case 64921139:
                        if (str.equals("DEBUG")) {
                            obj = 1;
                            break;
                        }
                        break;
                    case 66247144:
                        if (str.equals("ERROR")) {
                            i = 4;
                            break;
                        }
                        break;
                    case 1069090146:
                        if (str.equals("VERBOSE")) {
                            obj = null;
                            break;
                        }
                        break;
                    case 1940088646:
                        if (str.equals("ASSERT")) {
                            i = 5;
                            break;
                        }
                        break;
                }
                switch (obj) {
                    case null:
                        return 2;
                    case 1:
                        return 3;
                    case 2:
                        return 4;
                    case 3:
                        return 5;
                    case 4:
                        return 6;
                    case 5:
                        return 7;
                    case 6:
                        return ConnectionsManager.DEFAULT_DATACENTER_ID;
                    default:
                        return 4;
                }
            }
        }
        i = 2;
        while (7 >= i && !Log.isLoggable(this.mTag, i)) {
            i++;
        }
        return i;
    }

    private static String zzd(String... strArr) {
        if (strArr.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        for (String str : strArr) {
            if (stringBuilder.length() > 1) {
                stringBuilder.append(",");
            }
            stringBuilder.append(str);
        }
        stringBuilder.append(']').append(' ');
        return stringBuilder.toString();
    }

    protected String format(String str, Object... objArr) {
        if (objArr != null && objArr.length > 0) {
            str = String.format(str, objArr);
        }
        return this.CR.concat(str);
    }

    public void zza(String str, Object... objArr) {
        if (zzbf(2)) {
            Log.v(this.mTag, format(str, objArr));
        }
    }

    public boolean zzbf(int i) {
        return this.bX <= i;
    }

    public void zze(String str, Object... objArr) {
        Log.i(this.mTag, format(str, objArr));
    }

    public void zzf(String str, Object... objArr) {
        Log.w(this.mTag, format(str, objArr));
    }
}
