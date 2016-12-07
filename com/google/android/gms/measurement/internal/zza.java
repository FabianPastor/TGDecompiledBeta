package com.google.android.gms.measurement.internal;

import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzaa;

class zza {
    private String Hh;
    private String aqF;
    private String aqG;
    private String aqH;
    private long aqI;
    private long aqJ;
    private long aqK;
    private long aqL;
    private String aqM;
    private long aqN;
    private long aqO;
    private boolean aqP;
    private long aqQ;
    private long aqR;
    private long aqS;
    private long aqT;
    private long aqU;
    private long aqV;
    private boolean aqW;
    private long aqX;
    private long aqY;
    private final zzx aqw;
    private String bO;
    private final String zzctj;

    @WorkerThread
    zza(zzx com_google_android_gms_measurement_internal_zzx, String str) {
        zzaa.zzy(com_google_android_gms_measurement_internal_zzx);
        zzaa.zzib(str);
        this.aqw = com_google_android_gms_measurement_internal_zzx;
        this.zzctj = str;
        this.aqw.zzzx();
    }

    @WorkerThread
    public void setAppVersion(String str) {
        this.aqw.zzzx();
        this.aqW = (!zzal.zzbb(this.bO, str) ? 1 : 0) | this.aqW;
        this.bO = str;
    }

    @WorkerThread
    public void setMeasurementEnabled(boolean z) {
        this.aqw.zzzx();
        this.aqW = (this.aqP != z ? 1 : 0) | this.aqW;
        this.aqP = z;
    }

    @WorkerThread
    public String zzaaf() {
        this.aqw.zzzx();
        return this.bO;
    }

    @WorkerThread
    public void zzav(long j) {
        this.aqw.zzzx();
        this.aqW = (this.aqJ != j ? 1 : 0) | this.aqW;
        this.aqJ = j;
    }

    @WorkerThread
    public void zzaw(long j) {
        this.aqw.zzzx();
        this.aqW = (this.aqK != j ? 1 : 0) | this.aqW;
        this.aqK = j;
    }

    @WorkerThread
    public void zzax(long j) {
        this.aqw.zzzx();
        this.aqW = (this.aqL != j ? 1 : 0) | this.aqW;
        this.aqL = j;
    }

    @WorkerThread
    public void zzay(long j) {
        this.aqw.zzzx();
        this.aqW = (this.aqN != j ? 1 : 0) | this.aqW;
        this.aqN = j;
    }

    @WorkerThread
    public void zzaz(long j) {
        this.aqw.zzzx();
        this.aqW = (this.aqO != j ? 1 : 0) | this.aqW;
        this.aqO = j;
    }

    @WorkerThread
    public String zzazn() {
        this.aqw.zzzx();
        return this.Hh;
    }

    @WorkerThread
    public void zzba(long j) {
        int i = 1;
        zzaa.zzbt(j >= 0);
        this.aqw.zzzx();
        boolean z = this.aqW;
        if (this.aqI == j) {
            i = 0;
        }
        this.aqW = z | i;
        this.aqI = j;
    }

    @WorkerThread
    public void zzbb(long j) {
        this.aqw.zzzx();
        this.aqW = (this.aqX != j ? 1 : 0) | this.aqW;
        this.aqX = j;
    }

    @WorkerThread
    public void zzbc(long j) {
        this.aqw.zzzx();
        this.aqW = (this.aqY != j ? 1 : 0) | this.aqW;
        this.aqY = j;
    }

    @WorkerThread
    public void zzbd(long j) {
        this.aqw.zzzx();
        this.aqW = (this.aqQ != j ? 1 : 0) | this.aqW;
        this.aqQ = j;
    }

    @WorkerThread
    public void zzbe(long j) {
        this.aqw.zzzx();
        this.aqW = (this.aqR != j ? 1 : 0) | this.aqW;
        this.aqR = j;
    }

    @WorkerThread
    public void zzbf(long j) {
        this.aqw.zzzx();
        this.aqW = (this.aqS != j ? 1 : 0) | this.aqW;
        this.aqS = j;
    }

    @WorkerThread
    public void zzbg(long j) {
        this.aqw.zzzx();
        this.aqW = (this.aqT != j ? 1 : 0) | this.aqW;
        this.aqT = j;
    }

    @WorkerThread
    public void zzbh(long j) {
        this.aqw.zzzx();
        this.aqW = (this.aqV != j ? 1 : 0) | this.aqW;
        this.aqV = j;
    }

    @WorkerThread
    public void zzbi(long j) {
        this.aqw.zzzx();
        this.aqW = (this.aqU != j ? 1 : 0) | this.aqW;
        this.aqU = j;
    }

    @WorkerThread
    public void zzbtg() {
        this.aqw.zzzx();
        this.aqW = false;
    }

    @WorkerThread
    public String zzbth() {
        this.aqw.zzzx();
        return this.aqF;
    }

    @WorkerThread
    public String zzbti() {
        this.aqw.zzzx();
        return this.aqG;
    }

    @WorkerThread
    public String zzbtj() {
        this.aqw.zzzx();
        return this.aqH;
    }

    @WorkerThread
    public long zzbtk() {
        this.aqw.zzzx();
        return this.aqJ;
    }

    @WorkerThread
    public long zzbtl() {
        this.aqw.zzzx();
        return this.aqK;
    }

    @WorkerThread
    public long zzbtm() {
        this.aqw.zzzx();
        return this.aqL;
    }

    @WorkerThread
    public String zzbtn() {
        this.aqw.zzzx();
        return this.aqM;
    }

    @WorkerThread
    public long zzbto() {
        this.aqw.zzzx();
        return this.aqN;
    }

    @WorkerThread
    public long zzbtp() {
        this.aqw.zzzx();
        return this.aqO;
    }

    @WorkerThread
    public boolean zzbtq() {
        this.aqw.zzzx();
        return this.aqP;
    }

    @WorkerThread
    public long zzbtr() {
        this.aqw.zzzx();
        return this.aqI;
    }

    @WorkerThread
    public long zzbts() {
        this.aqw.zzzx();
        return this.aqX;
    }

    @WorkerThread
    public long zzbtt() {
        this.aqw.zzzx();
        return this.aqY;
    }

    @WorkerThread
    public void zzbtu() {
        this.aqw.zzzx();
        long j = this.aqI + 1;
        if (j > 2147483647L) {
            this.aqw.zzbwb().zzbxa().log("Bundle index overflow");
            j = 0;
        }
        this.aqW = true;
        this.aqI = j;
    }

    @WorkerThread
    public long zzbtv() {
        this.aqw.zzzx();
        return this.aqQ;
    }

    @WorkerThread
    public long zzbtw() {
        this.aqw.zzzx();
        return this.aqR;
    }

    @WorkerThread
    public long zzbtx() {
        this.aqw.zzzx();
        return this.aqS;
    }

    @WorkerThread
    public long zzbty() {
        this.aqw.zzzx();
        return this.aqT;
    }

    @WorkerThread
    public long zzbtz() {
        this.aqw.zzzx();
        return this.aqV;
    }

    @WorkerThread
    public long zzbua() {
        this.aqw.zzzx();
        return this.aqU;
    }

    @WorkerThread
    public void zzlj(String str) {
        this.aqw.zzzx();
        this.aqW = (!zzal.zzbb(this.Hh, str) ? 1 : 0) | this.aqW;
        this.Hh = str;
    }

    @WorkerThread
    public void zzlk(String str) {
        this.aqw.zzzx();
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.aqW = (!zzal.zzbb(this.aqF, str) ? 1 : 0) | this.aqW;
        this.aqF = str;
    }

    @WorkerThread
    public void zzll(String str) {
        this.aqw.zzzx();
        this.aqW = (!zzal.zzbb(this.aqG, str) ? 1 : 0) | this.aqW;
        this.aqG = str;
    }

    @WorkerThread
    public void zzlm(String str) {
        this.aqw.zzzx();
        this.aqW = (!zzal.zzbb(this.aqH, str) ? 1 : 0) | this.aqW;
        this.aqH = str;
    }

    @WorkerThread
    public void zzln(String str) {
        this.aqw.zzzx();
        this.aqW = (!zzal.zzbb(this.aqM, str) ? 1 : 0) | this.aqW;
        this.aqM = str;
    }

    @WorkerThread
    public String zzup() {
        this.aqw.zzzx();
        return this.zzctj;
    }
}
