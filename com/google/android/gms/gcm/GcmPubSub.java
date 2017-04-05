package com.google.android.gms.gcm;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import com.google.android.gms.iid.InstanceID;
import java.io.IOException;
import java.util.regex.Pattern;

public class GcmPubSub {
    private static GcmPubSub zzbgo;
    private static final Pattern zzbgq = Pattern.compile("/topics/[a-zA-Z0-9-_.~%]{1,900}");
    private InstanceID zzbgp;

    private GcmPubSub(Context context) {
        this.zzbgp = InstanceID.getInstance(context);
    }

    public static synchronized GcmPubSub getInstance(Context context) {
        GcmPubSub gcmPubSub;
        synchronized (GcmPubSub.class) {
            if (zzbgo == null) {
                zzbgo = new GcmPubSub(context);
            }
            gcmPubSub = zzbgo;
        }
        return gcmPubSub;
    }

    @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
    public void subscribe(String str, String str2, Bundle bundle) throws IOException {
        String str3;
        String valueOf;
        if (str == null || str.isEmpty()) {
            str3 = "Invalid appInstanceToken: ";
            valueOf = String.valueOf(str);
            throw new IllegalArgumentException(valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
        } else if (str2 == null || !zzbgq.matcher(str2).matches()) {
            str3 = "Invalid topic name: ";
            valueOf = String.valueOf(str2);
            throw new IllegalArgumentException(valueOf.length() != 0 ? str3.concat(valueOf) : new String(str3));
        } else {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putString("gcm.topic", str2);
            this.zzbgp.getToken(str, str2, bundle);
        }
    }

    @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
    public void unsubscribe(String str, String str2) throws IOException {
        Bundle bundle = new Bundle();
        bundle.putString("gcm.topic", str2);
        this.zzbgp.zzb(str, str2, bundle);
    }
}
