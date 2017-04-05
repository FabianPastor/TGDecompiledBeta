package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;

class zzatc {
    private final String mAppId;
    private String zzVX;
    private String zzaIU;
    private String zzacM;
    private long zzbqA;
    private long zzbqB;
    private long zzbqC;
    private long zzbqD;
    private long zzbqE;
    private long zzbqF;
    private long zzbqG;
    private String zzbqH;
    private boolean zzbqI;
    private long zzbqJ;
    private long zzbqK;
    private final zzaue zzbqc;
    private String zzbqq;
    private String zzbqr;
    private long zzbqs;
    private long zzbqt;
    private long zzbqu;
    private long zzbqv;
    private String zzbqw;
    private long zzbqx;
    private long zzbqy;
    private boolean zzbqz;

    @WorkerThread
    zzatc(zzaue com_google_android_gms_internal_zzaue, String str) {
        zzac.zzw(com_google_android_gms_internal_zzaue);
        zzac.zzdr(str);
        this.zzbqc = com_google_android_gms_internal_zzaue;
        this.mAppId = str;
        this.zzbqc.zzmR();
    }

    @WorkerThread
    public String getAppInstanceId() {
        this.zzbqc.zzmR();
        return this.zzaIU;
    }

    @WorkerThread
    public String getGmpAppId() {
        this.zzbqc.zzmR();
        return this.zzVX;
    }

    @WorkerThread
    public void setAppVersion(String str) {
        this.zzbqc.zzmR();
        this.zzbqI = (!zzaut.zzae(this.zzacM, str) ? 1 : 0) | this.zzbqI;
        this.zzacM = str;
    }

    @WorkerThread
    public void setMeasurementEnabled(boolean z) {
        this.zzbqc.zzmR();
        this.zzbqI = (this.zzbqz != z ? 1 : 0) | this.zzbqI;
        this.zzbqz = z;
    }

    @WorkerThread
    public long zzKA() {
        this.zzbqc.zzmR();
        return this.zzbqK;
    }

    @WorkerThread
    public void zzKB() {
        this.zzbqc.zzmR();
        long j = this.zzbqs + 1;
        if (j > 2147483647L) {
            this.zzbqc.zzKl().zzMa().zzj("Bundle index overflow. appId", zzatx.zzfE(this.mAppId));
            j = 0;
        }
        this.zzbqI = true;
        this.zzbqs = j;
    }

    @WorkerThread
    public long zzKC() {
        this.zzbqc.zzmR();
        return this.zzbqB;
    }

    @WorkerThread
    public long zzKD() {
        this.zzbqc.zzmR();
        return this.zzbqC;
    }

    @WorkerThread
    public long zzKE() {
        this.zzbqc.zzmR();
        return this.zzbqD;
    }

    @WorkerThread
    public long zzKF() {
        this.zzbqc.zzmR();
        return this.zzbqE;
    }

    @WorkerThread
    public long zzKG() {
        this.zzbqc.zzmR();
        return this.zzbqG;
    }

    @WorkerThread
    public long zzKH() {
        this.zzbqc.zzmR();
        return this.zzbqF;
    }

    @WorkerThread
    public String zzKI() {
        this.zzbqc.zzmR();
        return this.zzbqH;
    }

    @WorkerThread
    public String zzKJ() {
        this.zzbqc.zzmR();
        String str = this.zzbqH;
        zzfi(null);
        return str;
    }

    @WorkerThread
    public void zzKo() {
        this.zzbqc.zzmR();
        this.zzbqI = false;
    }

    @WorkerThread
    public String zzKp() {
        this.zzbqc.zzmR();
        return this.zzbqq;
    }

    @WorkerThread
    public String zzKq() {
        this.zzbqc.zzmR();
        return this.zzbqr;
    }

    @WorkerThread
    public long zzKr() {
        this.zzbqc.zzmR();
        return this.zzbqt;
    }

    @WorkerThread
    public long zzKs() {
        this.zzbqc.zzmR();
        return this.zzbqu;
    }

    @WorkerThread
    public long zzKt() {
        this.zzbqc.zzmR();
        return this.zzbqv;
    }

    @WorkerThread
    public String zzKu() {
        this.zzbqc.zzmR();
        return this.zzbqw;
    }

    @WorkerThread
    public long zzKv() {
        this.zzbqc.zzmR();
        return this.zzbqx;
    }

    @WorkerThread
    public long zzKw() {
        this.zzbqc.zzmR();
        return this.zzbqy;
    }

    @WorkerThread
    public boolean zzKx() {
        this.zzbqc.zzmR();
        return this.zzbqz;
    }

    @WorkerThread
    public long zzKy() {
        this.zzbqc.zzmR();
        return this.zzbqs;
    }

    @WorkerThread
    public long zzKz() {
        this.zzbqc.zzmR();
        return this.zzbqJ;
    }

    @WorkerThread
    public void zzY(long j) {
        this.zzbqc.zzmR();
        this.zzbqI = (this.zzbqt != j ? 1 : 0) | this.zzbqI;
        this.zzbqt = j;
    }

    @WorkerThread
    public void zzZ(long j) {
        this.zzbqc.zzmR();
        this.zzbqI = (this.zzbqu != j ? 1 : 0) | this.zzbqI;
        this.zzbqu = j;
    }

    @WorkerThread
    public void zzaa(long j) {
        this.zzbqc.zzmR();
        this.zzbqI = (this.zzbqv != j ? 1 : 0) | this.zzbqI;
        this.zzbqv = j;
    }

    @WorkerThread
    public void zzab(long j) {
        this.zzbqc.zzmR();
        this.zzbqI = (this.zzbqx != j ? 1 : 0) | this.zzbqI;
        this.zzbqx = j;
    }

    @WorkerThread
    public void zzac(long j) {
        this.zzbqc.zzmR();
        this.zzbqI = (this.zzbqy != j ? 1 : 0) | this.zzbqI;
        this.zzbqy = j;
    }

    @WorkerThread
    public void zzad(long j) {
        int i = 1;
        zzac.zzax(j >= 0);
        this.zzbqc.zzmR();
        boolean z = this.zzbqI;
        if (this.zzbqs == j) {
            i = 0;
        }
        this.zzbqI = z | i;
        this.zzbqs = j;
    }

    @WorkerThread
    public void zzae(long j) {
        this.zzbqc.zzmR();
        this.zzbqI = (this.zzbqJ != j ? 1 : 0) | this.zzbqI;
        this.zzbqJ = j;
    }

    @WorkerThread
    public void zzaf(long j) {
        this.zzbqc.zzmR();
        this.zzbqI = (this.zzbqK != j ? 1 : 0) | this.zzbqI;
        this.zzbqK = j;
    }

    @WorkerThread
    public void zzag(long j) {
        this.zzbqc.zzmR();
        this.zzbqI = (this.zzbqB != j ? 1 : 0) | this.zzbqI;
        this.zzbqB = j;
    }

    @WorkerThread
    public void zzah(long j) {
        this.zzbqc.zzmR();
        this.zzbqI = (this.zzbqC != j ? 1 : 0) | this.zzbqI;
        this.zzbqC = j;
    }

    @WorkerThread
    public void zzai(long j) {
        this.zzbqc.zzmR();
        this.zzbqI = (this.zzbqD != j ? 1 : 0) | this.zzbqI;
        this.zzbqD = j;
    }

    @WorkerThread
    public void zzaj(long j) {
        this.zzbqc.zzmR();
        this.zzbqI = (this.zzbqE != j ? 1 : 0) | this.zzbqI;
        this.zzbqE = j;
    }

    @WorkerThread
    public void zzak(long j) {
        this.zzbqc.zzmR();
        this.zzbqI = (this.zzbqG != j ? 1 : 0) | this.zzbqI;
        this.zzbqG = j;
    }

    @WorkerThread
    public void zzal(long j) {
        this.zzbqc.zzmR();
        this.zzbqI = (this.zzbqF != j ? 1 : 0) | this.zzbqI;
        this.zzbqF = j;
    }

    @WorkerThread
    public void zzam(long j) {
        this.zzbqc.zzmR();
        this.zzbqI = (this.zzbqA != j ? 1 : 0) | this.zzbqI;
        this.zzbqA = j;
    }

    @WorkerThread
    public void zzfd(String str) {
        this.zzbqc.zzmR();
        this.zzbqI = (!zzaut.zzae(this.zzaIU, str) ? 1 : 0) | this.zzbqI;
        this.zzaIU = str;
    }

    @WorkerThread
    public void zzfe(String str) {
        this.zzbqc.zzmR();
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.zzbqI = (!zzaut.zzae(this.zzVX, str) ? 1 : 0) | this.zzbqI;
        this.zzVX = str;
    }

    @WorkerThread
    public void zzff(String str) {
        this.zzbqc.zzmR();
        this.zzbqI = (!zzaut.zzae(this.zzbqq, str) ? 1 : 0) | this.zzbqI;
        this.zzbqq = str;
    }

    @WorkerThread
    public void zzfg(String str) {
        this.zzbqc.zzmR();
        this.zzbqI = (!zzaut.zzae(this.zzbqr, str) ? 1 : 0) | this.zzbqI;
        this.zzbqr = str;
    }

    @WorkerThread
    public void zzfh(String str) {
        this.zzbqc.zzmR();
        this.zzbqI = (!zzaut.zzae(this.zzbqw, str) ? 1 : 0) | this.zzbqI;
        this.zzbqw = str;
    }

    @WorkerThread
    public void zzfi(String str) {
        this.zzbqc.zzmR();
        this.zzbqI = (!zzaut.zzae(this.zzbqH, str) ? 1 : 0) | this.zzbqI;
        this.zzbqH = str;
    }

    @WorkerThread
    public String zzke() {
        this.zzbqc.zzmR();
        return this.mAppId;
    }

    @WorkerThread
    public String zzmZ() {
        this.zzbqc.zzmR();
        return this.zzacM;
    }

    @WorkerThread
    public long zzuW() {
        this.zzbqc.zzmR();
        return this.zzbqA;
    }
}
