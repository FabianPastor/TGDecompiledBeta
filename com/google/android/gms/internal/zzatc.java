package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;

class zzatc {
    private final String mAppId;
    private String zzVX;
    private String zzaIU;
    private String zzacM;
    private String zzbqA;
    private long zzbqB;
    private long zzbqC;
    private boolean zzbqD;
    private long zzbqE;
    private long zzbqF;
    private long zzbqG;
    private long zzbqH;
    private long zzbqI;
    private long zzbqJ;
    private long zzbqK;
    private String zzbqL;
    private boolean zzbqM;
    private long zzbqN;
    private long zzbqO;
    private final zzaue zzbqg;
    private String zzbqu;
    private String zzbqv;
    private long zzbqw;
    private long zzbqx;
    private long zzbqy;
    private long zzbqz;

    @WorkerThread
    zzatc(zzaue com_google_android_gms_internal_zzaue, String str) {
        zzac.zzw(com_google_android_gms_internal_zzaue);
        zzac.zzdr(str);
        this.zzbqg = com_google_android_gms_internal_zzaue;
        this.mAppId = str;
        this.zzbqg.zzmR();
    }

    @WorkerThread
    public String getAppInstanceId() {
        this.zzbqg.zzmR();
        return this.zzaIU;
    }

    @WorkerThread
    public String getGmpAppId() {
        this.zzbqg.zzmR();
        return this.zzVX;
    }

    @WorkerThread
    public void setAppVersion(String str) {
        this.zzbqg.zzmR();
        this.zzbqM = (!zzaut.zzae(this.zzacM, str) ? 1 : 0) | this.zzbqM;
        this.zzacM = str;
    }

    @WorkerThread
    public void setMeasurementEnabled(boolean z) {
        this.zzbqg.zzmR();
        this.zzbqM = (this.zzbqD != z ? 1 : 0) | this.zzbqM;
        this.zzbqD = z;
    }

    @WorkerThread
    public void zzKA() {
        this.zzbqg.zzmR();
        long j = this.zzbqw + 1;
        if (j > 2147483647L) {
            this.zzbqg.zzKk().zzLZ().zzj("Bundle index overflow. appId", zzatx.zzfE(this.mAppId));
            j = 0;
        }
        this.zzbqM = true;
        this.zzbqw = j;
    }

    @WorkerThread
    public long zzKB() {
        this.zzbqg.zzmR();
        return this.zzbqF;
    }

    @WorkerThread
    public long zzKC() {
        this.zzbqg.zzmR();
        return this.zzbqG;
    }

    @WorkerThread
    public long zzKD() {
        this.zzbqg.zzmR();
        return this.zzbqH;
    }

    @WorkerThread
    public long zzKE() {
        this.zzbqg.zzmR();
        return this.zzbqI;
    }

    @WorkerThread
    public long zzKF() {
        this.zzbqg.zzmR();
        return this.zzbqK;
    }

    @WorkerThread
    public long zzKG() {
        this.zzbqg.zzmR();
        return this.zzbqJ;
    }

    @WorkerThread
    public String zzKH() {
        this.zzbqg.zzmR();
        return this.zzbqL;
    }

    @WorkerThread
    public String zzKI() {
        this.zzbqg.zzmR();
        String str = this.zzbqL;
        zzfi(null);
        return str;
    }

    @WorkerThread
    public void zzKn() {
        this.zzbqg.zzmR();
        this.zzbqM = false;
    }

    @WorkerThread
    public String zzKo() {
        this.zzbqg.zzmR();
        return this.zzbqu;
    }

    @WorkerThread
    public String zzKp() {
        this.zzbqg.zzmR();
        return this.zzbqv;
    }

    @WorkerThread
    public long zzKq() {
        this.zzbqg.zzmR();
        return this.zzbqx;
    }

    @WorkerThread
    public long zzKr() {
        this.zzbqg.zzmR();
        return this.zzbqy;
    }

    @WorkerThread
    public long zzKs() {
        this.zzbqg.zzmR();
        return this.zzbqz;
    }

    @WorkerThread
    public String zzKt() {
        this.zzbqg.zzmR();
        return this.zzbqA;
    }

    @WorkerThread
    public long zzKu() {
        this.zzbqg.zzmR();
        return this.zzbqB;
    }

    @WorkerThread
    public long zzKv() {
        this.zzbqg.zzmR();
        return this.zzbqC;
    }

    @WorkerThread
    public boolean zzKw() {
        this.zzbqg.zzmR();
        return this.zzbqD;
    }

    @WorkerThread
    public long zzKx() {
        this.zzbqg.zzmR();
        return this.zzbqw;
    }

    @WorkerThread
    public long zzKy() {
        this.zzbqg.zzmR();
        return this.zzbqN;
    }

    @WorkerThread
    public long zzKz() {
        this.zzbqg.zzmR();
        return this.zzbqO;
    }

    @WorkerThread
    public void zzY(long j) {
        this.zzbqg.zzmR();
        this.zzbqM = (this.zzbqx != j ? 1 : 0) | this.zzbqM;
        this.zzbqx = j;
    }

    @WorkerThread
    public void zzZ(long j) {
        this.zzbqg.zzmR();
        this.zzbqM = (this.zzbqy != j ? 1 : 0) | this.zzbqM;
        this.zzbqy = j;
    }

    @WorkerThread
    public void zzaa(long j) {
        this.zzbqg.zzmR();
        this.zzbqM = (this.zzbqz != j ? 1 : 0) | this.zzbqM;
        this.zzbqz = j;
    }

    @WorkerThread
    public void zzab(long j) {
        this.zzbqg.zzmR();
        this.zzbqM = (this.zzbqB != j ? 1 : 0) | this.zzbqM;
        this.zzbqB = j;
    }

    @WorkerThread
    public void zzac(long j) {
        this.zzbqg.zzmR();
        this.zzbqM = (this.zzbqC != j ? 1 : 0) | this.zzbqM;
        this.zzbqC = j;
    }

    @WorkerThread
    public void zzad(long j) {
        int i = 1;
        zzac.zzax(j >= 0);
        this.zzbqg.zzmR();
        boolean z = this.zzbqM;
        if (this.zzbqw == j) {
            i = 0;
        }
        this.zzbqM = z | i;
        this.zzbqw = j;
    }

    @WorkerThread
    public void zzae(long j) {
        this.zzbqg.zzmR();
        this.zzbqM = (this.zzbqN != j ? 1 : 0) | this.zzbqM;
        this.zzbqN = j;
    }

    @WorkerThread
    public void zzaf(long j) {
        this.zzbqg.zzmR();
        this.zzbqM = (this.zzbqO != j ? 1 : 0) | this.zzbqM;
        this.zzbqO = j;
    }

    @WorkerThread
    public void zzag(long j) {
        this.zzbqg.zzmR();
        this.zzbqM = (this.zzbqF != j ? 1 : 0) | this.zzbqM;
        this.zzbqF = j;
    }

    @WorkerThread
    public void zzah(long j) {
        this.zzbqg.zzmR();
        this.zzbqM = (this.zzbqG != j ? 1 : 0) | this.zzbqM;
        this.zzbqG = j;
    }

    @WorkerThread
    public void zzai(long j) {
        this.zzbqg.zzmR();
        this.zzbqM = (this.zzbqH != j ? 1 : 0) | this.zzbqM;
        this.zzbqH = j;
    }

    @WorkerThread
    public void zzaj(long j) {
        this.zzbqg.zzmR();
        this.zzbqM = (this.zzbqI != j ? 1 : 0) | this.zzbqM;
        this.zzbqI = j;
    }

    @WorkerThread
    public void zzak(long j) {
        this.zzbqg.zzmR();
        this.zzbqM = (this.zzbqK != j ? 1 : 0) | this.zzbqM;
        this.zzbqK = j;
    }

    @WorkerThread
    public void zzal(long j) {
        this.zzbqg.zzmR();
        this.zzbqM = (this.zzbqJ != j ? 1 : 0) | this.zzbqM;
        this.zzbqJ = j;
    }

    @WorkerThread
    public void zzam(long j) {
        this.zzbqg.zzmR();
        this.zzbqM = (this.zzbqE != j ? 1 : 0) | this.zzbqM;
        this.zzbqE = j;
    }

    @WorkerThread
    public void zzfd(String str) {
        this.zzbqg.zzmR();
        this.zzbqM = (!zzaut.zzae(this.zzaIU, str) ? 1 : 0) | this.zzbqM;
        this.zzaIU = str;
    }

    @WorkerThread
    public void zzfe(String str) {
        this.zzbqg.zzmR();
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        this.zzbqM = (!zzaut.zzae(this.zzVX, str) ? 1 : 0) | this.zzbqM;
        this.zzVX = str;
    }

    @WorkerThread
    public void zzff(String str) {
        this.zzbqg.zzmR();
        this.zzbqM = (!zzaut.zzae(this.zzbqu, str) ? 1 : 0) | this.zzbqM;
        this.zzbqu = str;
    }

    @WorkerThread
    public void zzfg(String str) {
        this.zzbqg.zzmR();
        this.zzbqM = (!zzaut.zzae(this.zzbqv, str) ? 1 : 0) | this.zzbqM;
        this.zzbqv = str;
    }

    @WorkerThread
    public void zzfh(String str) {
        this.zzbqg.zzmR();
        this.zzbqM = (!zzaut.zzae(this.zzbqA, str) ? 1 : 0) | this.zzbqM;
        this.zzbqA = str;
    }

    @WorkerThread
    public void zzfi(String str) {
        this.zzbqg.zzmR();
        this.zzbqM = (!zzaut.zzae(this.zzbqL, str) ? 1 : 0) | this.zzbqM;
        this.zzbqL = str;
    }

    @WorkerThread
    public String zzke() {
        this.zzbqg.zzmR();
        return this.mAppId;
    }

    @WorkerThread
    public String zzmZ() {
        this.zzbqg.zzmR();
        return this.zzacM;
    }

    @WorkerThread
    public long zzuW() {
        this.zzbqg.zzmR();
        return this.zzbqE;
    }
}
