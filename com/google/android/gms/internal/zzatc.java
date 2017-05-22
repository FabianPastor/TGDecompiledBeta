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
    private String zzbqG;
    private boolean zzbqH;
    private long zzbqI;
    private long zzbqJ;
    private final zzaue zzbqb;
    private String zzbqp;
    private String zzbqq;
    private long zzbqr;
    private long zzbqs;
    private long zzbqt;
    private long zzbqu;
    private String zzbqv;
    private long zzbqw;
    private long zzbqx;
    private boolean zzbqy;
    private long zzbqz;

    @WorkerThread
    zzatc(zzaue com_google_android_gms_internal_zzaue, String str) {
        zzac.zzw(com_google_android_gms_internal_zzaue);
        zzac.zzdr(str);
        this.zzbqb = com_google_android_gms_internal_zzaue;
        this.mAppId = str;
        this.zzbqb.zzmR();
    }

    @WorkerThread
    public String getAppInstanceId() {
        this.zzbqb.zzmR();
        return this.zzaIU;
    }

    @WorkerThread
    public String getGmpAppId() {
        this.zzbqb.zzmR();
        return this.zzVX;
    }

    @WorkerThread
    public void setAppVersion(String str) {
        this.zzbqb.zzmR();
        this.zzbqH = (!zzaut.zzae(this.zzacM, str) ? 1 : 0) | this.zzbqH;
        this.zzacM = str;
    }

    @WorkerThread
    public void setMeasurementEnabled(boolean z) {
        this.zzbqb.zzmR();
        this.zzbqH = (this.zzbqy != z ? 1 : 0) | this.zzbqH;
        this.zzbqy = z;
    }

    @WorkerThread
    public long zzKA() {
        this.zzbqb.zzmR();
        return this.zzbqJ;
    }

    @WorkerThread
    public void zzKB() {
        this.zzbqb.zzmR();
        long j = this.zzbqr + 1;
        if (j > 2147483647L) {
            this.zzbqb.zzKl().zzMb().zzj("Bundle index overflow. appId", zzatx.zzfE(this.mAppId));
            j = 0;
        }
        this.zzbqH = true;
        this.zzbqr = j;
    }

    @WorkerThread
    public long zzKC() {
        this.zzbqb.zzmR();
        return this.zzbqA;
    }

    @WorkerThread
    public long zzKD() {
        this.zzbqb.zzmR();
        return this.zzbqB;
    }

    @WorkerThread
    public long zzKE() {
        this.zzbqb.zzmR();
        return this.zzbqC;
    }

    @WorkerThread
    public long zzKF() {
        this.zzbqb.zzmR();
        return this.zzbqD;
    }

    @WorkerThread
    public long zzKG() {
        this.zzbqb.zzmR();
        return this.zzbqF;
    }

    @WorkerThread
    public long zzKH() {
        this.zzbqb.zzmR();
        return this.zzbqE;
    }

    @WorkerThread
    public String zzKI() {
        this.zzbqb.zzmR();
        return this.zzbqG;
    }

    @WorkerThread
    public String zzKJ() {
        this.zzbqb.zzmR();
        String str = this.zzbqG;
        zzfi(null);
        return str;
    }

    @WorkerThread
    public void zzKo() {
        this.zzbqb.zzmR();
        this.zzbqH = false;
    }

    @WorkerThread
    public String zzKp() {
        this.zzbqb.zzmR();
        return this.zzbqp;
    }

    @WorkerThread
    public String zzKq() {
        this.zzbqb.zzmR();
        return this.zzbqq;
    }

    @WorkerThread
    public long zzKr() {
        this.zzbqb.zzmR();
        return this.zzbqs;
    }

    @WorkerThread
    public long zzKs() {
        this.zzbqb.zzmR();
        return this.zzbqt;
    }

    @WorkerThread
    public long zzKt() {
        this.zzbqb.zzmR();
        return this.zzbqu;
    }

    @WorkerThread
    public String zzKu() {
        this.zzbqb.zzmR();
        return this.zzbqv;
    }

    @WorkerThread
    public long zzKv() {
        this.zzbqb.zzmR();
        return this.zzbqw;
    }

    @WorkerThread
    public long zzKw() {
        this.zzbqb.zzmR();
        return this.zzbqx;
    }

    @WorkerThread
    public boolean zzKx() {
        this.zzbqb.zzmR();
        return this.zzbqy;
    }

    @WorkerThread
    public long zzKy() {
        this.zzbqb.zzmR();
        return this.zzbqr;
    }

    @WorkerThread
    public long zzKz() {
        this.zzbqb.zzmR();
        return this.zzbqI;
    }

    @WorkerThread
    public void zzY(long j) {
        this.zzbqb.zzmR();
        this.zzbqH = (this.zzbqs != j ? 1 : 0) | this.zzbqH;
        this.zzbqs = j;
    }

    @WorkerThread
    public void zzZ(long j) {
        this.zzbqb.zzmR();
        this.zzbqH = (this.zzbqt != j ? 1 : 0) | this.zzbqH;
        this.zzbqt = j;
    }

    @WorkerThread
    public void zzaa(long j) {
        this.zzbqb.zzmR();
        this.zzbqH = (this.zzbqu != j ? 1 : 0) | this.zzbqH;
        this.zzbqu = j;
    }

    @WorkerThread
    public void zzab(long j) {
        this.zzbqb.zzmR();
        this.zzbqH = (this.zzbqw != j ? 1 : 0) | this.zzbqH;
        this.zzbqw = j;
    }

    @WorkerThread
    public void zzac(long j) {
        this.zzbqb.zzmR();
        this.zzbqH = (this.zzbqx != j ? 1 : 0) | this.zzbqH;
        this.zzbqx = j;
    }

    @WorkerThread
    public void zzad(long j) {
        int i = 1;
        zzac.zzaw(j >= 0);
        this.zzbqb.zzmR();
        boolean z = this.zzbqH;
        if (this.zzbqr == j) {
            i = 0;
        }
        this.zzbqH = z | i;
        this.zzbqr = j;
    }

    @WorkerThread
    public void zzae(long j) {
        this.zzbqb.zzmR();
        this.zzbqH = (this.zzbqI != j ? 1 : 0) | this.zzbqH;
        this.zzbqI = j;
    }

    @WorkerThread
    public void zzaf(long j) {
        this.zzbqb.zzmR();
        this.zzbqH = (this.zzbqJ != j ? 1 : 0) | this.zzbqH;
        this.zzbqJ = j;
    }

    @WorkerThread
    public void zzag(long j) {
        this.zzbqb.zzmR();
        this.zzbqH = (this.zzbqA != j ? 1 : 0) | this.zzbqH;
        this.zzbqA = j;
    }

    @WorkerThread
    public void zzah(long j) {
        this.zzbqb.zzmR();
        this.zzbqH = (this.zzbqB != j ? 1 : 0) | this.zzbqH;
        this.zzbqB = j;
    }

    @WorkerThread
    public void zzai(long j) {
        this.zzbqb.zzmR();
        this.zzbqH = (this.zzbqC != j ? 1 : 0) | this.zzbqH;
        this.zzbqC = j;
    }

    @WorkerThread
    public void zzaj(long j) {
        this.zzbqb.zzmR();
        this.zzbqH = (this.zzbqD != j ? 1 : 0) | this.zzbqH;
        this.zzbqD = j;
    }

    @WorkerThread
    public void zzak(long j) {
        this.zzbqb.zzmR();
        this.zzbqH = (this.zzbqF != j ? 1 : 0) | this.zzbqH;
        this.zzbqF = j;
    }

    @WorkerThread
    public void zzal(long j) {
        this.zzbqb.zzmR();
        this.zzbqH = (this.zzbqE != j ? 1 : 0) | this.zzbqH;
        this.zzbqE = j;
    }

    @WorkerThread
    public void zzam(long j) {
        this.zzbqb.zzmR();
        this.zzbqH = (this.zzbqz != j ? 1 : 0) | this.zzbqH;
        this.zzbqz = j;
    }

    @WorkerThread
    public void zzfd(String str) {
        this.zzbqb.zzmR();
        this.zzbqH = (!zzaut.zzae(this.zzaIU, str) ? 1 : 0) | this.zzbqH;
        this.zzaIU = str;
    }

    @WorkerThread
    public void zzfe(String str) {
        this.zzbqb.zzmR();
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.zzbqH = (!zzaut.zzae(this.zzVX, str) ? 1 : 0) | this.zzbqH;
        this.zzVX = str;
    }

    @WorkerThread
    public void zzff(String str) {
        this.zzbqb.zzmR();
        this.zzbqH = (!zzaut.zzae(this.zzbqp, str) ? 1 : 0) | this.zzbqH;
        this.zzbqp = str;
    }

    @WorkerThread
    public void zzfg(String str) {
        this.zzbqb.zzmR();
        this.zzbqH = (!zzaut.zzae(this.zzbqq, str) ? 1 : 0) | this.zzbqH;
        this.zzbqq = str;
    }

    @WorkerThread
    public void zzfh(String str) {
        this.zzbqb.zzmR();
        this.zzbqH = (!zzaut.zzae(this.zzbqv, str) ? 1 : 0) | this.zzbqH;
        this.zzbqv = str;
    }

    @WorkerThread
    public void zzfi(String str) {
        this.zzbqb.zzmR();
        this.zzbqH = (!zzaut.zzae(this.zzbqG, str) ? 1 : 0) | this.zzbqH;
        this.zzbqG = str;
    }

    @WorkerThread
    public String zzke() {
        this.zzbqb.zzmR();
        return this.mAppId;
    }

    @WorkerThread
    public String zzmZ() {
        this.zzbqb.zzmR();
        return this.zzacM;
    }

    @WorkerThread
    public long zzuW() {
        this.zzbqb.zzmR();
        return this.zzbqz;
    }
}
