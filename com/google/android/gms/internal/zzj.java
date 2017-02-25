package com.google.android.gms.internal;

import java.util.Map;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public class zzj {
    public final byte[] data;
    public final int statusCode;
    public final boolean zzA;
    public final long zzB;
    public final Map<String, String> zzz;

    public zzj(int i, byte[] bArr, Map<String, String> map, boolean z, long j) {
        this.statusCode = i;
        this.data = bArr;
        this.zzz = map;
        this.zzA = z;
        this.zzB = j;
    }

    public zzj(byte[] bArr, Map<String, String> map) {
        this(Callback.DEFAULT_DRAG_ANIMATION_DURATION, bArr, map, false, 0);
    }
}
