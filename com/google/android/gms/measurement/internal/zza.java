package com.google.android.gms.measurement.internal;

import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;

class zza {
    private String FA;
    private String G;
    private long anA;
    private long anB;
    private long anC;
    private String anD;
    private long anE;
    private long anF;
    private boolean anG;
    private long anH;
    private long anI;
    private long anJ;
    private long anK;
    private long anL;
    private long anM;
    private boolean anN;
    private long anO;
    private long anP;
    private final zzx anq;
    private String anw;
    private String anx;
    private String any;
    private long anz;
    private final String zzcpe;

    @WorkerThread
    zza(zzx com_google_android_gms_measurement_internal_zzx, String str) {
        zzac.zzy(com_google_android_gms_measurement_internal_zzx);
        zzac.zzhz(str);
        this.anq = com_google_android_gms_measurement_internal_zzx;
        this.zzcpe = str;
        this.anq.zzyl();
    }

    @WorkerThread
    public void setAppVersion(String str) {
        this.anq.zzyl();
        this.anN = (!zzal.zzbb(this.G, str) ? 1 : 0) | this.anN;
        this.G = str;
    }

    @WorkerThread
    public void setMeasurementEnabled(boolean z) {
        this.anq.zzyl();
        this.anN = (this.anG != z ? 1 : 0) | this.anN;
        this.anG = z;
    }

    @WorkerThread
    public void zzaw(long j) {
        this.anq.zzyl();
        this.anN = (this.anA != j ? 1 : 0) | this.anN;
        this.anA = j;
    }

    @WorkerThread
    public void zzax(long j) {
        this.anq.zzyl();
        this.anN = (this.anB != j ? 1 : 0) | this.anN;
        this.anB = j;
    }

    @WorkerThread
    public void zzay(long j) {
        this.anq.zzyl();
        this.anN = (this.anC != j ? 1 : 0) | this.anN;
        this.anC = j;
    }

    @WorkerThread
    public String zzayn() {
        this.anq.zzyl();
        return this.FA;
    }

    @WorkerThread
    public void zzaz(long j) {
        this.anq.zzyl();
        this.anN = (this.anE != j ? 1 : 0) | this.anN;
        this.anE = j;
    }

    @WorkerThread
    public void zzba(long j) {
        this.anq.zzyl();
        this.anN = (this.anF != j ? 1 : 0) | this.anN;
        this.anF = j;
    }

    @WorkerThread
    public void zzbb(long j) {
        int i = 1;
        zzac.zzbs(j >= 0);
        this.anq.zzyl();
        boolean z = this.anN;
        if (this.anz == j) {
            i = 0;
        }
        this.anN = z | i;
        this.anz = j;
    }

    @WorkerThread
    public void zzbc(long j) {
        this.anq.zzyl();
        this.anN = (this.anO != j ? 1 : 0) | this.anN;
        this.anO = j;
    }

    @WorkerThread
    public void zzbd(long j) {
        this.anq.zzyl();
        this.anN = (this.anP != j ? 1 : 0) | this.anN;
        this.anP = j;
    }

    @WorkerThread
    public void zzbe(long j) {
        this.anq.zzyl();
        this.anN = (this.anH != j ? 1 : 0) | this.anN;
        this.anH = j;
    }

    @WorkerThread
    public void zzbf(long j) {
        this.anq.zzyl();
        this.anN = (this.anI != j ? 1 : 0) | this.anN;
        this.anI = j;
    }

    @WorkerThread
    public void zzbg(long j) {
        this.anq.zzyl();
        this.anN = (this.anJ != j ? 1 : 0) | this.anN;
        this.anJ = j;
    }

    @WorkerThread
    public void zzbh(long j) {
        this.anq.zzyl();
        this.anN = (this.anK != j ? 1 : 0) | this.anN;
        this.anK = j;
    }

    @WorkerThread
    public void zzbi(long j) {
        this.anq.zzyl();
        this.anN = (this.anM != j ? 1 : 0) | this.anN;
        this.anM = j;
    }

    @WorkerThread
    public void zzbj(long j) {
        this.anq.zzyl();
        this.anN = (this.anL != j ? 1 : 0) | this.anN;
        this.anL = j;
    }

    @WorkerThread
    public void zzbsq() {
        this.anq.zzyl();
        this.anN = false;
    }

    @WorkerThread
    public String zzbsr() {
        this.anq.zzyl();
        return this.anw;
    }

    @WorkerThread
    public String zzbss() {
        this.anq.zzyl();
        return this.anx;
    }

    @WorkerThread
    public String zzbst() {
        this.anq.zzyl();
        return this.any;
    }

    @WorkerThread
    public long zzbsu() {
        this.anq.zzyl();
        return this.anA;
    }

    @WorkerThread
    public long zzbsv() {
        this.anq.zzyl();
        return this.anB;
    }

    @WorkerThread
    public long zzbsw() {
        this.anq.zzyl();
        return this.anC;
    }

    @WorkerThread
    public String zzbsx() {
        this.anq.zzyl();
        return this.anD;
    }

    @WorkerThread
    public long zzbsy() {
        this.anq.zzyl();
        return this.anE;
    }

    @WorkerThread
    public long zzbsz() {
        this.anq.zzyl();
        return this.anF;
    }

    @WorkerThread
    public boolean zzbta() {
        this.anq.zzyl();
        return this.anG;
    }

    @WorkerThread
    public long zzbtb() {
        this.anq.zzyl();
        return this.anz;
    }

    @WorkerThread
    public long zzbtc() {
        this.anq.zzyl();
        return this.anO;
    }

    @WorkerThread
    public long zzbtd() {
        this.anq.zzyl();
        return this.anP;
    }

    @WorkerThread
    public void zzbte() {
        this.anq.zzyl();
        long j = this.anz + 1;
        if (j > 2147483647L) {
            this.anq.zzbvg().zzbwe().log("Bundle index overflow");
            j = 0;
        }
        this.anN = true;
        this.anz = j;
    }

    @WorkerThread
    public long zzbtf() {
        this.anq.zzyl();
        return this.anH;
    }

    @WorkerThread
    public long zzbtg() {
        this.anq.zzyl();
        return this.anI;
    }

    @WorkerThread
    public long zzbth() {
        this.anq.zzyl();
        return this.anJ;
    }

    @WorkerThread
    public long zzbti() {
        this.anq.zzyl();
        return this.anK;
    }

    @WorkerThread
    public long zzbtj() {
        this.anq.zzyl();
        return this.anM;
    }

    @WorkerThread
    public long zzbtk() {
        this.anq.zzyl();
        return this.anL;
    }

    @WorkerThread
    public void zzlj(String str) {
        this.anq.zzyl();
        this.anN = (!zzal.zzbb(this.FA, str) ? 1 : 0) | this.anN;
        this.FA = str;
    }

    @WorkerThread
    public void zzlk(String str) {
        this.anq.zzyl();
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.anN = (!zzal.zzbb(this.anw, str) ? 1 : 0) | this.anN;
        this.anw = str;
    }

    @WorkerThread
    public void zzll(String str) {
        this.anq.zzyl();
        this.anN = (!zzal.zzbb(this.anx, str) ? 1 : 0) | this.anN;
        this.anx = str;
    }

    @WorkerThread
    public void zzlm(String str) {
        this.anq.zzyl();
        this.anN = (!zzal.zzbb(this.any, str) ? 1 : 0) | this.anN;
        this.any = str;
    }

    @WorkerThread
    public void zzln(String str) {
        this.anq.zzyl();
        this.anN = (!zzal.zzbb(this.anD, str) ? 1 : 0) | this.anN;
        this.anD = str;
    }

    @WorkerThread
    public String zzti() {
        this.anq.zzyl();
        return this.zzcpe;
    }

    @WorkerThread
    public String zzyt() {
        this.anq.zzyl();
        return this.G;
    }
}
