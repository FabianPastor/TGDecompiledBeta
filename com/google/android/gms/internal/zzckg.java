package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.stats.zza;
import com.google.android.gms.common.util.zzd;
import com.google.android.gms.common.zzf;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;

public final class zzckg extends zzcjl {
    private final zzcku zzjic;
    private zzche zzjid;
    private volatile Boolean zzjie;
    private final zzcgs zzjif;
    private final zzclk zzjig;
    private final List<Runnable> zzjih = new ArrayList();
    private final zzcgs zzjii;

    protected zzckg(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
        this.zzjig = new zzclk(com_google_android_gms_internal_zzcim.zzws());
        this.zzjic = new zzcku(this);
        this.zzjif = new zzckh(this, com_google_android_gms_internal_zzcim);
        this.zzjii = new zzckm(this, com_google_android_gms_internal_zzcim);
    }

    private final void onServiceDisconnected(ComponentName componentName) {
        zzve();
        if (this.zzjid != null) {
            this.zzjid = null;
            zzawy().zzazj().zzj("Disconnected from device MeasurementService", componentName);
            zzve();
            zzyc();
        }
    }

    private final void zzbat() {
        zzve();
        zzawy().zzazj().zzj("Processing queued up service tasks", Integer.valueOf(this.zzjih.size()));
        for (Runnable run : this.zzjih) {
            try {
                run.run();
            } catch (Throwable th) {
                zzawy().zzazd().zzj("Task exception while flushing queue", th);
            }
        }
        this.zzjih.clear();
        this.zzjii.cancel();
    }

    private final zzcgi zzbr(boolean z) {
        return zzawn().zzjg(z ? zzawy().zzazk() : null);
    }

    private final void zzj(Runnable runnable) throws IllegalStateException {
        zzve();
        if (isConnected()) {
            runnable.run();
        } else if (((long) this.zzjih.size()) >= 1000) {
            zzawy().zzazd().log("Discarding data. Max runnable queue size reached");
        } else {
            this.zzjih.add(runnable);
            this.zzjii.zzs(ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
            zzyc();
        }
    }

    private final void zzxr() {
        zzve();
        this.zzjig.start();
        this.zzjif.zzs(((Long) zzchc.zzjbj.get()).longValue());
    }

    private final void zzxs() {
        zzve();
        if (isConnected()) {
            zzawy().zzazj().log("Inactivity, disconnecting from the service");
            disconnect();
        }
    }

    public final void disconnect() {
        zzve();
        zzxf();
        try {
            zza.zzamc();
            getContext().unbindService(this.zzjic);
        } catch (IllegalStateException e) {
        } catch (IllegalArgumentException e2) {
        }
        this.zzjid = null;
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final boolean isConnected() {
        zzve();
        zzxf();
        return this.zzjid != null;
    }

    protected final void resetAnalyticsData() {
        zzve();
        zzxf();
        zzcgi zzbr = zzbr(false);
        zzawr().resetAnalyticsData();
        zzj(new zzcki(this, zzbr));
    }

    protected final void zza(zzche com_google_android_gms_internal_zzche) {
        zzve();
        zzbq.checkNotNull(com_google_android_gms_internal_zzche);
        this.zzjid = com_google_android_gms_internal_zzche;
        zzxr();
        zzbat();
    }

    final void zza(zzche com_google_android_gms_internal_zzche, zzbfm com_google_android_gms_internal_zzbfm, zzcgi com_google_android_gms_internal_zzcgi) {
        zzve();
        zzxf();
        int i = 100;
        for (int i2 = 0; i2 < 1001 && r4 == 100; i2++) {
            List arrayList = new ArrayList();
            Object zzeb = zzawr().zzeb(100);
            if (zzeb != null) {
                arrayList.addAll(zzeb);
                i = zzeb.size();
            } else {
                i = 0;
            }
            if (com_google_android_gms_internal_zzbfm != null && r4 < 100) {
                arrayList.add(com_google_android_gms_internal_zzbfm);
            }
            ArrayList arrayList2 = (ArrayList) arrayList;
            int size = arrayList2.size();
            int i3 = 0;
            while (i3 < size) {
                zzeb = arrayList2.get(i3);
                i3++;
                zzbfm com_google_android_gms_internal_zzbfm2 = (zzbfm) zzeb;
                if (com_google_android_gms_internal_zzbfm2 instanceof zzcha) {
                    try {
                        com_google_android_gms_internal_zzche.zza((zzcha) com_google_android_gms_internal_zzbfm2, com_google_android_gms_internal_zzcgi);
                    } catch (RemoteException e) {
                        zzawy().zzazd().zzj("Failed to send event to the service", e);
                    }
                } else if (com_google_android_gms_internal_zzbfm2 instanceof zzcln) {
                    try {
                        com_google_android_gms_internal_zzche.zza((zzcln) com_google_android_gms_internal_zzbfm2, com_google_android_gms_internal_zzcgi);
                    } catch (RemoteException e2) {
                        zzawy().zzazd().zzj("Failed to send attribute to the service", e2);
                    }
                } else if (com_google_android_gms_internal_zzbfm2 instanceof zzcgl) {
                    try {
                        com_google_android_gms_internal_zzche.zza((zzcgl) com_google_android_gms_internal_zzbfm2, com_google_android_gms_internal_zzcgi);
                    } catch (RemoteException e22) {
                        zzawy().zzazd().zzj("Failed to send conditional property to the service", e22);
                    }
                } else {
                    zzawy().zzazd().log("Discarding data. Unrecognized parcel type.");
                }
            }
        }
    }

    protected final void zza(zzb com_google_android_gms_measurement_AppMeasurement_zzb) {
        zzve();
        zzxf();
        zzj(new zzckl(this, com_google_android_gms_measurement_AppMeasurement_zzb));
    }

    public final void zza(AtomicReference<String> atomicReference) {
        zzve();
        zzxf();
        zzj(new zzckj(this, atomicReference, zzbr(false)));
    }

    protected final void zza(AtomicReference<List<zzcgl>> atomicReference, String str, String str2, String str3) {
        zzve();
        zzxf();
        zzj(new zzckq(this, atomicReference, str, str2, str3, zzbr(false)));
    }

    protected final void zza(AtomicReference<List<zzcln>> atomicReference, String str, String str2, String str3, boolean z) {
        zzve();
        zzxf();
        zzj(new zzckr(this, atomicReference, str, str2, str3, z, zzbr(false)));
    }

    protected final void zza(AtomicReference<List<zzcln>> atomicReference, boolean z) {
        zzve();
        zzxf();
        zzj(new zzckt(this, atomicReference, zzbr(false), z));
    }

    public final /* bridge */ /* synthetic */ void zzawi() {
        super.zzawi();
    }

    public final /* bridge */ /* synthetic */ void zzawj() {
        super.zzawj();
    }

    public final /* bridge */ /* synthetic */ zzcgd zzawk() {
        return super.zzawk();
    }

    public final /* bridge */ /* synthetic */ zzcgk zzawl() {
        return super.zzawl();
    }

    public final /* bridge */ /* synthetic */ zzcjn zzawm() {
        return super.zzawm();
    }

    public final /* bridge */ /* synthetic */ zzchh zzawn() {
        return super.zzawn();
    }

    public final /* bridge */ /* synthetic */ zzcgu zzawo() {
        return super.zzawo();
    }

    public final /* bridge */ /* synthetic */ zzckg zzawp() {
        return super.zzawp();
    }

    public final /* bridge */ /* synthetic */ zzckc zzawq() {
        return super.zzawq();
    }

    public final /* bridge */ /* synthetic */ zzchi zzawr() {
        return super.zzawr();
    }

    public final /* bridge */ /* synthetic */ zzcgo zzaws() {
        return super.zzaws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzawt() {
        return super.zzawt();
    }

    public final /* bridge */ /* synthetic */ zzclq zzawu() {
        return super.zzawu();
    }

    public final /* bridge */ /* synthetic */ zzcig zzawv() {
        return super.zzawv();
    }

    public final /* bridge */ /* synthetic */ zzclf zzaww() {
        return super.zzaww();
    }

    public final /* bridge */ /* synthetic */ zzcih zzawx() {
        return super.zzawx();
    }

    public final /* bridge */ /* synthetic */ zzchm zzawy() {
        return super.zzawy();
    }

    public final /* bridge */ /* synthetic */ zzchx zzawz() {
        return super.zzawz();
    }

    public final /* bridge */ /* synthetic */ zzcgn zzaxa() {
        return super.zzaxa();
    }

    protected final boolean zzaxz() {
        return false;
    }

    protected final void zzb(zzcln com_google_android_gms_internal_zzcln) {
        zzve();
        zzxf();
        zzj(new zzcks(this, zzawr().zza(com_google_android_gms_internal_zzcln), com_google_android_gms_internal_zzcln, zzbr(true)));
    }

    protected final void zzbaq() {
        zzve();
        zzxf();
        zzj(new zzckn(this, zzbr(true)));
    }

    protected final void zzbar() {
        zzve();
        zzxf();
        zzj(new zzckk(this, zzbr(true)));
    }

    final Boolean zzbas() {
        return this.zzjie;
    }

    protected final void zzc(zzcha com_google_android_gms_internal_zzcha, String str) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcha);
        zzve();
        zzxf();
        zzj(new zzcko(this, true, zzawr().zza(com_google_android_gms_internal_zzcha), com_google_android_gms_internal_zzcha, zzbr(true), str));
    }

    protected final void zzf(zzcgl com_google_android_gms_internal_zzcgl) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcgl);
        zzve();
        zzxf();
        zzj(new zzckp(this, true, zzawr().zzc(com_google_android_gms_internal_zzcgl), new zzcgl(com_google_android_gms_internal_zzcgl), zzbr(true), com_google_android_gms_internal_zzcgl));
    }

    public final /* bridge */ /* synthetic */ void zzve() {
        super.zzve();
    }

    public final /* bridge */ /* synthetic */ zzd zzws() {
        return super.zzws();
    }

    final void zzyc() {
        Object obj = 1;
        zzve();
        zzxf();
        if (!isConnected()) {
            if (this.zzjie == null) {
                boolean z;
                zzve();
                zzxf();
                Boolean zzazo = zzawz().zzazo();
                if (zzazo == null || !zzazo.booleanValue()) {
                    Object obj2;
                    if (zzawn().zzazb() != 1) {
                        zzawy().zzazj().log("Checking service availability");
                        int isGooglePlayServicesAvailable = zzf.zzafy().isGooglePlayServicesAvailable(zzawu().getContext());
                        int i;
                        switch (isGooglePlayServicesAvailable) {
                            case 0:
                                zzawy().zzazj().log("Service available");
                                i = 1;
                                z = true;
                                break;
                            case 1:
                                zzawy().zzazj().log("Service missing");
                                i = 1;
                                z = false;
                                break;
                            case 2:
                                zzawy().zzazi().log("Service container out of date");
                                zzcjk zzawu = zzawu();
                                zzf.zzafy();
                                if (zzf.zzcf(zzawu.getContext()) >= 11400) {
                                    zzazo = zzawz().zzazo();
                                    z = zzazo == null || zzazo.booleanValue();
                                    obj2 = null;
                                    break;
                                }
                                i = 1;
                                z = false;
                                break;
                                break;
                            case 3:
                                zzawy().zzazf().log("Service disabled");
                                obj2 = null;
                                z = false;
                                break;
                            case 9:
                                zzawy().zzazf().log("Service invalid");
                                obj2 = null;
                                z = false;
                                break;
                            case 18:
                                zzawy().zzazf().log("Service updating");
                                i = 1;
                                z = true;
                                break;
                            default:
                                zzawy().zzazf().zzj("Unexpected service status", Integer.valueOf(isGooglePlayServicesAvailable));
                                obj2 = null;
                                z = false;
                                break;
                        }
                    }
                    obj2 = 1;
                    z = true;
                    if (obj2 != null) {
                        zzawz().zzbm(z);
                    }
                } else {
                    z = true;
                }
                this.zzjie = Boolean.valueOf(z);
            }
            if (this.zzjie.booleanValue()) {
                this.zzjic.zzbau();
                return;
            }
            List queryIntentServices = getContext().getPackageManager().queryIntentServices(new Intent().setClassName(getContext(), "com.google.android.gms.measurement.AppMeasurementService"), C.DEFAULT_BUFFER_SEGMENT_SIZE);
            if (queryIntentServices == null || queryIntentServices.size() <= 0) {
                obj = null;
            }
            if (obj != null) {
                Intent intent = new Intent("com.google.android.gms.measurement.START");
                intent.setComponent(new ComponentName(getContext(), "com.google.android.gms.measurement.AppMeasurementService"));
                this.zzjic.zzn(intent);
                return;
            }
            zzawy().zzazd().log("Unable to use remote or local measurement implementation. Please register the AppMeasurementService service in the app manifest");
        }
    }
}
