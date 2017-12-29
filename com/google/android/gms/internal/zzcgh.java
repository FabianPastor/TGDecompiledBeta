package com.google.android.gms.internal;

import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;

final class zzcgh {
    private final String mAppId;
    private String zzcwz;
    private String zzdra;
    private String zzggb;
    private final zzcim zziwf;
    private String zziww;
    private String zziwx;
    private long zziwy;
    private long zziwz;
    private long zzixa;
    private long zzixb;
    private String zzixc;
    private long zzixd;
    private long zzixe;
    private boolean zzixf;
    private long zzixg;
    private boolean zzixh;
    private long zzixi;
    private long zzixj;
    private long zzixk;
    private long zzixl;
    private long zzixm;
    private long zzixn;
    private String zzixo;
    private boolean zzixp;
    private long zzixq;
    private long zzixr;

    zzcgh(zzcim com_google_android_gms_internal_zzcim, String str) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcim);
        zzbq.zzgm(str);
        this.zziwf = com_google_android_gms_internal_zzcim;
        this.mAppId = str;
        this.zziwf.zzawx().zzve();
    }

    public final String getAppId() {
        this.zziwf.zzawx().zzve();
        return this.mAppId;
    }

    public final String getAppInstanceId() {
        this.zziwf.zzawx().zzve();
        return this.zzggb;
    }

    public final String getGmpAppId() {
        this.zziwf.zzawx().zzve();
        return this.zzcwz;
    }

    public final void setAppVersion(String str) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (!zzclq.zzas(this.zzdra, str) ? 1 : 0) | this.zzixp;
        this.zzdra = str;
    }

    public final void setMeasurementEnabled(boolean z) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (this.zzixf != z ? 1 : 0) | this.zzixp;
        this.zzixf = z;
    }

    public final void zzal(long j) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (this.zziwz != j ? 1 : 0) | this.zzixp;
        this.zziwz = j;
    }

    public final void zzam(long j) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (this.zzixa != j ? 1 : 0) | this.zzixp;
        this.zzixa = j;
    }

    public final void zzan(long j) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (this.zzixb != j ? 1 : 0) | this.zzixp;
        this.zzixb = j;
    }

    public final void zzao(long j) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (this.zzixd != j ? 1 : 0) | this.zzixp;
        this.zzixd = j;
    }

    public final void zzap(long j) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (this.zzixe != j ? 1 : 0) | this.zzixp;
        this.zzixe = j;
    }

    public final void zzaq(long j) {
        int i = 1;
        zzbq.checkArgument(j >= 0);
        this.zziwf.zzawx().zzve();
        boolean z = this.zzixp;
        if (this.zziwy == j) {
            i = 0;
        }
        this.zzixp = z | i;
        this.zziwy = j;
    }

    public final void zzar(long j) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (this.zzixq != j ? 1 : 0) | this.zzixp;
        this.zzixq = j;
    }

    public final void zzas(long j) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (this.zzixr != j ? 1 : 0) | this.zzixp;
        this.zzixr = j;
    }

    public final void zzat(long j) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (this.zzixi != j ? 1 : 0) | this.zzixp;
        this.zzixi = j;
    }

    public final void zzau(long j) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (this.zzixj != j ? 1 : 0) | this.zzixp;
        this.zzixj = j;
    }

    public final void zzav(long j) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (this.zzixk != j ? 1 : 0) | this.zzixp;
        this.zzixk = j;
    }

    public final void zzaw(long j) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (this.zzixl != j ? 1 : 0) | this.zzixp;
        this.zzixl = j;
    }

    public final void zzax(long j) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (this.zzixn != j ? 1 : 0) | this.zzixp;
        this.zzixn = j;
    }

    public final void zzaxb() {
        this.zziwf.zzawx().zzve();
        this.zzixp = false;
    }

    public final String zzaxc() {
        this.zziwf.zzawx().zzve();
        return this.zziww;
    }

    public final String zzaxd() {
        this.zziwf.zzawx().zzve();
        return this.zziwx;
    }

    public final long zzaxe() {
        this.zziwf.zzawx().zzve();
        return this.zziwz;
    }

    public final long zzaxf() {
        this.zziwf.zzawx().zzve();
        return this.zzixa;
    }

    public final long zzaxg() {
        this.zziwf.zzawx().zzve();
        return this.zzixb;
    }

    public final String zzaxh() {
        this.zziwf.zzawx().zzve();
        return this.zzixc;
    }

    public final long zzaxi() {
        this.zziwf.zzawx().zzve();
        return this.zzixd;
    }

    public final long zzaxj() {
        this.zziwf.zzawx().zzve();
        return this.zzixe;
    }

    public final boolean zzaxk() {
        this.zziwf.zzawx().zzve();
        return this.zzixf;
    }

    public final long zzaxl() {
        this.zziwf.zzawx().zzve();
        return this.zziwy;
    }

    public final long zzaxm() {
        this.zziwf.zzawx().zzve();
        return this.zzixq;
    }

    public final long zzaxn() {
        this.zziwf.zzawx().zzve();
        return this.zzixr;
    }

    public final void zzaxo() {
        this.zziwf.zzawx().zzve();
        long j = this.zziwy + 1;
        if (j > 2147483647L) {
            this.zziwf.zzawy().zzazf().zzj("Bundle index overflow. appId", zzchm.zzjk(this.mAppId));
            j = 0;
        }
        this.zzixp = true;
        this.zziwy = j;
    }

    public final long zzaxp() {
        this.zziwf.zzawx().zzve();
        return this.zzixi;
    }

    public final long zzaxq() {
        this.zziwf.zzawx().zzve();
        return this.zzixj;
    }

    public final long zzaxr() {
        this.zziwf.zzawx().zzve();
        return this.zzixk;
    }

    public final long zzaxs() {
        this.zziwf.zzawx().zzve();
        return this.zzixl;
    }

    public final long zzaxt() {
        this.zziwf.zzawx().zzve();
        return this.zzixn;
    }

    public final long zzaxu() {
        this.zziwf.zzawx().zzve();
        return this.zzixm;
    }

    public final String zzaxv() {
        this.zziwf.zzawx().zzve();
        return this.zzixo;
    }

    public final String zzaxw() {
        this.zziwf.zzawx().zzve();
        String str = this.zzixo;
        zziw(null);
        return str;
    }

    public final long zzaxx() {
        this.zziwf.zzawx().zzve();
        return this.zzixg;
    }

    public final boolean zzaxy() {
        this.zziwf.zzawx().zzve();
        return this.zzixh;
    }

    public final void zzay(long j) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (this.zzixm != j ? 1 : 0) | this.zzixp;
        this.zzixm = j;
    }

    public final void zzaz(long j) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (this.zzixg != j ? 1 : 0) | this.zzixp;
        this.zzixg = j;
    }

    public final void zzbl(boolean z) {
        this.zziwf.zzawx().zzve();
        this.zzixp = this.zzixh != z;
        this.zzixh = z;
    }

    public final void zzir(String str) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (!zzclq.zzas(this.zzggb, str) ? 1 : 0) | this.zzixp;
        this.zzggb = str;
    }

    public final void zzis(String str) {
        this.zziwf.zzawx().zzve();
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.zzixp = (!zzclq.zzas(this.zzcwz, str) ? 1 : 0) | this.zzixp;
        this.zzcwz = str;
    }

    public final void zzit(String str) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (!zzclq.zzas(this.zziww, str) ? 1 : 0) | this.zzixp;
        this.zziww = str;
    }

    public final void zziu(String str) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (!zzclq.zzas(this.zziwx, str) ? 1 : 0) | this.zzixp;
        this.zziwx = str;
    }

    public final void zziv(String str) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (!zzclq.zzas(this.zzixc, str) ? 1 : 0) | this.zzixp;
        this.zzixc = str;
    }

    public final void zziw(String str) {
        this.zziwf.zzawx().zzve();
        this.zzixp = (!zzclq.zzas(this.zzixo, str) ? 1 : 0) | this.zzixp;
        this.zzixo = str;
    }

    public final String zzvj() {
        this.zziwf.zzawx().zzve();
        return this.zzdra;
    }
}
