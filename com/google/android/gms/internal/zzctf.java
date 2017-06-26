package com.google.android.gms.internal;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.Scope;

public final class zzctf {
    public static final Api<zzctk> API = new Api("SignIn.API", zzajS, zzajR);
    private static Api<zzcti> zzaMc = new Api("SignIn.INTERNAL_API", zzbCK, zzbCJ);
    private static zzf<zzctt> zzajR = new zzf();
    public static final zza<zzctt, zzctk> zzajS = new zzctg();
    private static Scope zzalV = new Scope(Scopes.PROFILE);
    private static Scope zzalW = new Scope("email");
    private static zzf<zzctt> zzbCJ = new zzf();
    private static zza<zzctt, zzcti> zzbCK = new zzcth();
}
