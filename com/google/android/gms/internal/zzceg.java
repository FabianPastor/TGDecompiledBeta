package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;

final class zzceg {
    private final String mAppId;
    private String zzXB;
    private String zzaKE;
    private String zzaeI;
    private long zzboA;
    private String zzboB;
    private long zzboC;
    private long zzboD;
    private boolean zzboE;
    private long zzboF;
    private long zzboG;
    private long zzboH;
    private long zzboI;
    private long zzboJ;
    private long zzboK;
    private long zzboL;
    private String zzboM;
    private boolean zzboN;
    private long zzboO;
    private long zzboP;
    private final zzcgl zzboe;
    private String zzbov;
    private String zzbow;
    private long zzbox;
    private long zzboy;
    private long zzboz;

    @WorkerThread
    zzceg(zzcgl com_google_android_gms_internal_zzcgl, String str) {
        zzbo.zzu(com_google_android_gms_internal_zzcgl);
        zzbo.zzcF(str);
        this.zzboe = com_google_android_gms_internal_zzcgl;
        this.mAppId = str;
        this.zzboe.zzwE().zzjC();
    }

    @WorkerThread
    public final String getAppInstanceId() {
        this.zzboe.zzwE().zzjC();
        return this.zzaKE;
    }

    @WorkerThread
    public final String getGmpAppId() {
        this.zzboe.zzwE().zzjC();
        return this.zzXB;
    }

    @WorkerThread
    public final void setAppVersion(String str) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (!zzcjl.zzR(this.zzaeI, str) ? 1 : 0) | this.zzboN;
        this.zzaeI = str;
    }

    @WorkerThread
    public final void setMeasurementEnabled(boolean z) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (this.zzboE != z ? 1 : 0) | this.zzboN;
        this.zzboE = z;
    }

    @WorkerThread
    public final void zzL(long j) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (this.zzboy != j ? 1 : 0) | this.zzboN;
        this.zzboy = j;
    }

    @WorkerThread
    public final void zzM(long j) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (this.zzboz != j ? 1 : 0) | this.zzboN;
        this.zzboz = j;
    }

    @WorkerThread
    public final void zzN(long j) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (this.zzboA != j ? 1 : 0) | this.zzboN;
        this.zzboA = j;
    }

    @WorkerThread
    public final void zzO(long j) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (this.zzboC != j ? 1 : 0) | this.zzboN;
        this.zzboC = j;
    }

    @WorkerThread
    public final void zzP(long j) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (this.zzboD != j ? 1 : 0) | this.zzboN;
        this.zzboD = j;
    }

    @WorkerThread
    public final void zzQ(long j) {
        int i = 1;
        zzbo.zzaf(j >= 0);
        this.zzboe.zzwE().zzjC();
        boolean z = this.zzboN;
        if (this.zzbox == j) {
            i = 0;
        }
        this.zzboN = z | i;
        this.zzbox = j;
    }

    @WorkerThread
    public final void zzR(long j) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (this.zzboO != j ? 1 : 0) | this.zzboN;
        this.zzboO = j;
    }

    @WorkerThread
    public final void zzS(long j) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (this.zzboP != j ? 1 : 0) | this.zzboN;
        this.zzboP = j;
    }

    @WorkerThread
    public final void zzT(long j) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (this.zzboG != j ? 1 : 0) | this.zzboN;
        this.zzboG = j;
    }

    @WorkerThread
    public final void zzU(long j) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (this.zzboH != j ? 1 : 0) | this.zzboN;
        this.zzboH = j;
    }

    @WorkerThread
    public final void zzV(long j) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (this.zzboI != j ? 1 : 0) | this.zzboN;
        this.zzboI = j;
    }

    @WorkerThread
    public final void zzW(long j) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (this.zzboJ != j ? 1 : 0) | this.zzboN;
        this.zzboJ = j;
    }

    @WorkerThread
    public final void zzX(long j) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (this.zzboL != j ? 1 : 0) | this.zzboN;
        this.zzboL = j;
    }

    @WorkerThread
    public final void zzY(long j) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (this.zzboK != j ? 1 : 0) | this.zzboN;
        this.zzboK = j;
    }

    @WorkerThread
    public final void zzZ(long j) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (this.zzboF != j ? 1 : 0) | this.zzboN;
        this.zzboF = j;
    }

    @WorkerThread
    public final void zzdG(String str) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (!zzcjl.zzR(this.zzaKE, str) ? 1 : 0) | this.zzboN;
        this.zzaKE = str;
    }

    @WorkerThread
    public final void zzdH(String str) {
        this.zzboe.zzwE().zzjC();
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.zzboN = (!zzcjl.zzR(this.zzXB, str) ? 1 : 0) | this.zzboN;
        this.zzXB = str;
    }

    @WorkerThread
    public final void zzdI(String str) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (!zzcjl.zzR(this.zzbov, str) ? 1 : 0) | this.zzboN;
        this.zzbov = str;
    }

    @WorkerThread
    public final void zzdJ(String str) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (!zzcjl.zzR(this.zzbow, str) ? 1 : 0) | this.zzboN;
        this.zzbow = str;
    }

    @WorkerThread
    public final void zzdK(String str) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (!zzcjl.zzR(this.zzboB, str) ? 1 : 0) | this.zzboN;
        this.zzboB = str;
    }

    @WorkerThread
    public final void zzdL(String str) {
        this.zzboe.zzwE().zzjC();
        this.zzboN = (!zzcjl.zzR(this.zzboM, str) ? 1 : 0) | this.zzboN;
        this.zzboM = str;
    }

    @WorkerThread
    public final String zzhl() {
        this.zzboe.zzwE().zzjC();
        return this.mAppId;
    }

    @WorkerThread
    public final String zzjH() {
        this.zzboe.zzwE().zzjC();
        return this.zzaeI;
    }

    @WorkerThread
    public final void zzwI() {
        this.zzboe.zzwE().zzjC();
        this.zzboN = false;
    }

    @WorkerThread
    public final String zzwJ() {
        this.zzboe.zzwE().zzjC();
        return this.zzbov;
    }

    @WorkerThread
    public final String zzwK() {
        this.zzboe.zzwE().zzjC();
        return this.zzbow;
    }

    @WorkerThread
    public final long zzwL() {
        this.zzboe.zzwE().zzjC();
        return this.zzboy;
    }

    @WorkerThread
    public final long zzwM() {
        this.zzboe.zzwE().zzjC();
        return this.zzboz;
    }

    @WorkerThread
    public final long zzwN() {
        this.zzboe.zzwE().zzjC();
        return this.zzboA;
    }

    @WorkerThread
    public final String zzwO() {
        this.zzboe.zzwE().zzjC();
        return this.zzboB;
    }

    @WorkerThread
    public final long zzwP() {
        this.zzboe.zzwE().zzjC();
        return this.zzboC;
    }

    @WorkerThread
    public final long zzwQ() {
        this.zzboe.zzwE().zzjC();
        return this.zzboD;
    }

    @WorkerThread
    public final boolean zzwR() {
        this.zzboe.zzwE().zzjC();
        return this.zzboE;
    }

    @WorkerThread
    public final long zzwS() {
        this.zzboe.zzwE().zzjC();
        return this.zzbox;
    }

    @WorkerThread
    public final long zzwT() {
        this.zzboe.zzwE().zzjC();
        return this.zzboO;
    }

    @WorkerThread
    public final long zzwU() {
        this.zzboe.zzwE().zzjC();
        return this.zzboP;
    }

    @WorkerThread
    public final void zzwV() {
        this.zzboe.zzwE().zzjC();
        long j = this.zzbox + 1;
        if (j > 2147483647L) {
            this.zzboe.zzwF().zzyz().zzj("Bundle index overflow. appId", zzcfl.zzdZ(this.mAppId));
            j = 0;
        }
        this.zzboN = true;
        this.zzbox = j;
    }

    @WorkerThread
    public final long zzwW() {
        this.zzboe.zzwE().zzjC();
        return this.zzboG;
    }

    @WorkerThread
    public final long zzwX() {
        this.zzboe.zzwE().zzjC();
        return this.zzboH;
    }

    @WorkerThread
    public final long zzwY() {
        this.zzboe.zzwE().zzjC();
        return this.zzboI;
    }

    @WorkerThread
    public final long zzwZ() {
        this.zzboe.zzwE().zzjC();
        return this.zzboJ;
    }

    @WorkerThread
    public final long zzxa() {
        this.zzboe.zzwE().zzjC();
        return this.zzboL;
    }

    @WorkerThread
    public final long zzxb() {
        this.zzboe.zzwE().zzjC();
        return this.zzboK;
    }

    @WorkerThread
    public final String zzxc() {
        this.zzboe.zzwE().zzjC();
        return this.zzboM;
    }

    @WorkerThread
    public final String zzxd() {
        this.zzboe.zzwE().zzjC();
        String str = this.zzboM;
        zzdL(null);
        return str;
    }

    @WorkerThread
    public final long zzxe() {
        this.zzboe.zzwE().zzjC();
        return this.zzboF;
    }
}
