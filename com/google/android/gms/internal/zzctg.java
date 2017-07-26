package com.google.android.gms.internal;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.zza;
import com.google.android.gms.common.api.Api.zzf;
import com.google.android.gms.common.api.Scope;

public final class zzctg {
    public static final Api<zzctl> API = new Api("SignIn.API", zzajS, zzajR);
    private static Api<zzctj> zzaMc = new Api("SignIn.INTERNAL_API", zzbCK, zzbCJ);
    private static zzf<zzctu> zzajR = new zzf();
    public static final zza<zzctu, zzctl> zzajS = new zzcth();
    private static Scope zzalV = new Scope(Scopes.PROFILE);
    private static Scope zzalW = new Scope("email");
    private static zzf<zzctu> zzbCJ = new zzf();
    private static zza<zzctu, zzctj> zzbCK = new zzcti();
}
