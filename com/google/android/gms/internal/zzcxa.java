package com.google.android.gms.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.Scope;

public final class zzcxa {
    public static final Api<zzcxe> API = new Api("SignIn.API", zzebg, zzebf);
    private static zzf<zzcxn> zzebf = new zzf();
    public static final zza<zzcxn, zzcxe> zzebg = new zzcxb();
    private static Scope zzehi = new Scope("profile");
    private static Scope zzehj = new Scope("email");
    private static Api<Object> zzgjb = new Api("SignIn.INTERNAL_API", zzkbr, zzkbq);
    private static zzf<zzcxn> zzkbq = new zzf();
    private static zza<zzcxn, Object> zzkbr = new zzcxc();
}
