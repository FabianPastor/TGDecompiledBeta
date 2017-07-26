package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.Param;
import com.google.android.gms.measurement.AppMeasurement.UserProperty;
import org.telegram.messenger.NotificationBadge.NewHtcHomeBadger;

public final class zzcfj extends zzchj {
    private static String[] zzbqI = new String[Event.zzbof.length];
    private static String[] zzbqJ = new String[Param.zzboh.length];
    private static String[] zzbqK = new String[UserProperty.zzbom.length];

    zzcfj(zzcgl com_google_android_gms_internal_zzcgl) {
        super(com_google_android_gms_internal_zzcgl);
    }

    @Nullable
    private static String zza(String str, String[] strArr, String[] strArr2, String[] strArr3) {
        boolean z = true;
        int i = 0;
        zzbo.zzu(strArr);
        zzbo.zzu(strArr2);
        zzbo.zzu(strArr3);
        zzbo.zzaf(strArr.length == strArr2.length);
        if (strArr.length != strArr3.length) {
            z = false;
        }
        zzbo.zzaf(z);
        while (i < strArr.length) {
            if (zzcjl.zzR(str, strArr[i])) {
                synchronized (strArr3) {
                    if (strArr3[i] == null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(strArr2[i]);
                        stringBuilder.append("(");
                        stringBuilder.append(strArr[i]);
                        stringBuilder.append(")");
                        strArr3[i] = stringBuilder.toString();
                    }
                    str = strArr3[i];
                }
                return str;
            }
            i++;
        }
        return str;
    }

    private static void zza(StringBuilder stringBuilder, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            stringBuilder.append("  ");
        }
    }

    private final void zza(StringBuilder stringBuilder, int i, zzcjo com_google_android_gms_internal_zzcjo) {
        if (com_google_android_gms_internal_zzcjo != null) {
            zza(stringBuilder, i);
            stringBuilder.append("filter {\n");
            zza(stringBuilder, i, "complement", com_google_android_gms_internal_zzcjo.zzbuU);
            zza(stringBuilder, i, "param_name", zzdX(com_google_android_gms_internal_zzcjo.zzbuV));
            int i2 = i + 1;
            String str = "string_filter";
            zzcjr com_google_android_gms_internal_zzcjr = com_google_android_gms_internal_zzcjo.zzbuS;
            if (com_google_android_gms_internal_zzcjr != null) {
                zza(stringBuilder, i2);
                stringBuilder.append(str);
                stringBuilder.append(" {\n");
                if (com_google_android_gms_internal_zzcjr.zzbve != null) {
                    Object obj = "UNKNOWN_MATCH_TYPE";
                    switch (com_google_android_gms_internal_zzcjr.zzbve.intValue()) {
                        case 1:
                            obj = "REGEXP";
                            break;
                        case 2:
                            obj = "BEGINS_WITH";
                            break;
                        case 3:
                            obj = "ENDS_WITH";
                            break;
                        case 4:
                            obj = "PARTIAL";
                            break;
                        case 5:
                            obj = "EXACT";
                            break;
                        case 6:
                            obj = "IN_LIST";
                            break;
                    }
                    zza(stringBuilder, i2, "match_type", obj);
                }
                zza(stringBuilder, i2, "expression", com_google_android_gms_internal_zzcjr.zzbvf);
                zza(stringBuilder, i2, "case_sensitive", com_google_android_gms_internal_zzcjr.zzbvg);
                if (com_google_android_gms_internal_zzcjr.zzbvh.length > 0) {
                    zza(stringBuilder, i2 + 1);
                    stringBuilder.append("expression_list {\n");
                    for (String str2 : com_google_android_gms_internal_zzcjr.zzbvh) {
                        zza(stringBuilder, i2 + 2);
                        stringBuilder.append(str2);
                        stringBuilder.append("\n");
                    }
                    stringBuilder.append("}\n");
                }
                zza(stringBuilder, i2);
                stringBuilder.append("}\n");
            }
            zza(stringBuilder, i + 1, "number_filter", com_google_android_gms_internal_zzcjo.zzbuT);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private final void zza(StringBuilder stringBuilder, int i, String str, zzcjp com_google_android_gms_internal_zzcjp) {
        if (com_google_android_gms_internal_zzcjp != null) {
            zza(stringBuilder, i);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzcjp.zzbuW != null) {
                Object obj = "UNKNOWN_COMPARISON_TYPE";
                switch (com_google_android_gms_internal_zzcjp.zzbuW.intValue()) {
                    case 1:
                        obj = "LESS_THAN";
                        break;
                    case 2:
                        obj = "GREATER_THAN";
                        break;
                    case 3:
                        obj = "EQUAL";
                        break;
                    case 4:
                        obj = "BETWEEN";
                        break;
                }
                zza(stringBuilder, i, "comparison_type", obj);
            }
            zza(stringBuilder, i, "match_as_float", com_google_android_gms_internal_zzcjp.zzbuX);
            zza(stringBuilder, i, "comparison_value", com_google_android_gms_internal_zzcjp.zzbuY);
            zza(stringBuilder, i, "min_comparison_value", com_google_android_gms_internal_zzcjp.zzbuZ);
            zza(stringBuilder, i, "max_comparison_value", com_google_android_gms_internal_zzcjp.zzbva);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, zzcka com_google_android_gms_internal_zzcka) {
        int i2 = 0;
        if (com_google_android_gms_internal_zzcka != null) {
            int i3;
            int i4;
            int i5 = i + 1;
            zza(stringBuilder, i5);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzcka.zzbwf != null) {
                zza(stringBuilder, i5 + 1);
                stringBuilder.append("results: ");
                long[] jArr = com_google_android_gms_internal_zzcka.zzbwf;
                int length = jArr.length;
                i3 = 0;
                i4 = 0;
                while (i3 < length) {
                    Long valueOf = Long.valueOf(jArr[i3]);
                    int i6 = i4 + 1;
                    if (i4 != 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(valueOf);
                    i3++;
                    i4 = i6;
                }
                stringBuilder.append('\n');
            }
            if (com_google_android_gms_internal_zzcka.zzbwe != null) {
                zza(stringBuilder, i5 + 1);
                stringBuilder.append("status: ");
                long[] jArr2 = com_google_android_gms_internal_zzcka.zzbwe;
                int length2 = jArr2.length;
                i3 = 0;
                while (i2 < length2) {
                    Long valueOf2 = Long.valueOf(jArr2[i2]);
                    i4 = i3 + 1;
                    if (i3 != 0) {
                        stringBuilder.append(", ");
                    }
                    stringBuilder.append(valueOf2);
                    i2++;
                    i3 = i4;
                }
                stringBuilder.append('\n');
            }
            zza(stringBuilder, i5);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, Object obj) {
        if (obj != null) {
            zza(stringBuilder, i + 1);
            stringBuilder.append(str);
            stringBuilder.append(": ");
            stringBuilder.append(obj);
            stringBuilder.append('\n');
        }
    }

    private final void zza(StringBuilder stringBuilder, int i, zzcjv[] com_google_android_gms_internal_zzcjvArr) {
        if (com_google_android_gms_internal_zzcjvArr != null) {
            for (zzcjv com_google_android_gms_internal_zzcjv : com_google_android_gms_internal_zzcjvArr) {
                if (com_google_android_gms_internal_zzcjv != null) {
                    zza(stringBuilder, 2);
                    stringBuilder.append("audience_membership {\n");
                    zza(stringBuilder, 2, "audience_id", com_google_android_gms_internal_zzcjv.zzbuI);
                    zza(stringBuilder, 2, "new_audience", com_google_android_gms_internal_zzcjv.zzbvu);
                    zza(stringBuilder, 2, "current_data", com_google_android_gms_internal_zzcjv.zzbvs);
                    zza(stringBuilder, 2, "previous_data", com_google_android_gms_internal_zzcjv.zzbvt);
                    zza(stringBuilder, 2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private final void zza(StringBuilder stringBuilder, int i, zzcjw[] com_google_android_gms_internal_zzcjwArr) {
        if (com_google_android_gms_internal_zzcjwArr != null) {
            for (zzcjw com_google_android_gms_internal_zzcjw : com_google_android_gms_internal_zzcjwArr) {
                if (com_google_android_gms_internal_zzcjw != null) {
                    zza(stringBuilder, 2);
                    stringBuilder.append("event {\n");
                    zza(stringBuilder, 2, "name", zzdW(com_google_android_gms_internal_zzcjw.name));
                    zza(stringBuilder, 2, "timestamp_millis", com_google_android_gms_internal_zzcjw.zzbvx);
                    zza(stringBuilder, 2, "previous_timestamp_millis", com_google_android_gms_internal_zzcjw.zzbvy);
                    zza(stringBuilder, 2, NewHtcHomeBadger.COUNT, com_google_android_gms_internal_zzcjw.count);
                    zzcjx[] com_google_android_gms_internal_zzcjxArr = com_google_android_gms_internal_zzcjw.zzbvw;
                    if (com_google_android_gms_internal_zzcjxArr != null) {
                        for (zzcjx com_google_android_gms_internal_zzcjx : com_google_android_gms_internal_zzcjxArr) {
                            if (com_google_android_gms_internal_zzcjx != null) {
                                zza(stringBuilder, 3);
                                stringBuilder.append("param {\n");
                                zza(stringBuilder, 3, "name", zzdX(com_google_android_gms_internal_zzcjx.name));
                                zza(stringBuilder, 3, "string_value", com_google_android_gms_internal_zzcjx.zzaIF);
                                zza(stringBuilder, 3, "int_value", com_google_android_gms_internal_zzcjx.zzbvA);
                                zza(stringBuilder, 3, "double_value", com_google_android_gms_internal_zzcjx.zzbuB);
                                zza(stringBuilder, 3);
                                stringBuilder.append("}\n");
                            }
                        }
                    }
                    zza(stringBuilder, 2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private final void zza(StringBuilder stringBuilder, int i, zzckb[] com_google_android_gms_internal_zzckbArr) {
        if (com_google_android_gms_internal_zzckbArr != null) {
            for (zzckb com_google_android_gms_internal_zzckb : com_google_android_gms_internal_zzckbArr) {
                if (com_google_android_gms_internal_zzckb != null) {
                    zza(stringBuilder, 2);
                    stringBuilder.append("user_property {\n");
                    zza(stringBuilder, 2, "set_timestamp_millis", com_google_android_gms_internal_zzckb.zzbwh);
                    zza(stringBuilder, 2, "name", zzdY(com_google_android_gms_internal_zzckb.name));
                    zza(stringBuilder, 2, "string_value", com_google_android_gms_internal_zzckb.zzaIF);
                    zza(stringBuilder, 2, "int_value", com_google_android_gms_internal_zzckb.zzbvA);
                    zza(stringBuilder, 2, "double_value", com_google_android_gms_internal_zzckb.zzbuB);
                    zza(stringBuilder, 2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    @Nullable
    private final String zzb(zzcew com_google_android_gms_internal_zzcew) {
        return com_google_android_gms_internal_zzcew == null ? null : !zzyw() ? com_google_android_gms_internal_zzcew.toString() : zzA(com_google_android_gms_internal_zzcew.zzyt());
    }

    private final boolean zzyw() {
        return this.zzboe.zzwF().zzz(3);
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @Nullable
    protected final String zzA(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        if (!zzyw()) {
            return bundle.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : bundle.keySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(", ");
            } else {
                stringBuilder.append("Bundle[{");
            }
            stringBuilder.append(zzdX(str));
            stringBuilder.append("=");
            stringBuilder.append(bundle.get(str));
        }
        stringBuilder.append("}]");
        return stringBuilder.toString();
    }

    @Nullable
    protected final String zza(zzceu com_google_android_gms_internal_zzceu) {
        if (com_google_android_gms_internal_zzceu == null) {
            return null;
        }
        if (!zzyw()) {
            return com_google_android_gms_internal_zzceu.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Event{appId='");
        stringBuilder.append(com_google_android_gms_internal_zzceu.mAppId);
        stringBuilder.append("', name='");
        stringBuilder.append(zzdW(com_google_android_gms_internal_zzceu.mName));
        stringBuilder.append("', params=");
        stringBuilder.append(zzb(com_google_android_gms_internal_zzceu.zzbpF));
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    protected final String zza(zzcjn com_google_android_gms_internal_zzcjn) {
        int i = 0;
        if (com_google_android_gms_internal_zzcjn == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nevent_filter {\n");
        zza(stringBuilder, 0, "filter_id", com_google_android_gms_internal_zzcjn.zzbuM);
        zza(stringBuilder, 0, "event_name", zzdW(com_google_android_gms_internal_zzcjn.zzbuN));
        zza(stringBuilder, 1, "event_count_filter", com_google_android_gms_internal_zzcjn.zzbuQ);
        stringBuilder.append("  filters {\n");
        zzcjo[] com_google_android_gms_internal_zzcjoArr = com_google_android_gms_internal_zzcjn.zzbuO;
        int length = com_google_android_gms_internal_zzcjoArr.length;
        while (i < length) {
            zza(stringBuilder, 2, com_google_android_gms_internal_zzcjoArr[i]);
            i++;
        }
        zza(stringBuilder, 1);
        stringBuilder.append("}\n}\n");
        return stringBuilder.toString();
    }

    protected final String zza(zzcjq com_google_android_gms_internal_zzcjq) {
        if (com_google_android_gms_internal_zzcjq == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nproperty_filter {\n");
        zza(stringBuilder, 0, "filter_id", com_google_android_gms_internal_zzcjq.zzbuM);
        zza(stringBuilder, 0, "property_name", zzdY(com_google_android_gms_internal_zzcjq.zzbvc));
        zza(stringBuilder, 1, com_google_android_gms_internal_zzcjq.zzbvd);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    protected final String zza(zzcjy com_google_android_gms_internal_zzcjy) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nbatch {\n");
        if (com_google_android_gms_internal_zzcjy.zzbvB != null) {
            for (zzcjz com_google_android_gms_internal_zzcjz : com_google_android_gms_internal_zzcjy.zzbvB) {
                if (!(com_google_android_gms_internal_zzcjz == null || com_google_android_gms_internal_zzcjz == null)) {
                    zza(stringBuilder, 1);
                    stringBuilder.append("bundle {\n");
                    zza(stringBuilder, 1, "protocol_version", com_google_android_gms_internal_zzcjz.zzbvD);
                    zza(stringBuilder, 1, "platform", com_google_android_gms_internal_zzcjz.zzbvL);
                    zza(stringBuilder, 1, "gmp_version", com_google_android_gms_internal_zzcjz.zzbvP);
                    zza(stringBuilder, 1, "uploading_gmp_version", com_google_android_gms_internal_zzcjz.zzbvQ);
                    zza(stringBuilder, 1, "config_version", com_google_android_gms_internal_zzcjz.zzbwb);
                    zza(stringBuilder, 1, "gmp_app_id", com_google_android_gms_internal_zzcjz.zzboQ);
                    zza(stringBuilder, 1, "app_id", com_google_android_gms_internal_zzcjz.zzaH);
                    zza(stringBuilder, 1, "app_version", com_google_android_gms_internal_zzcjz.zzbgW);
                    zza(stringBuilder, 1, "app_version_major", com_google_android_gms_internal_zzcjz.zzbvY);
                    zza(stringBuilder, 1, "firebase_instance_id", com_google_android_gms_internal_zzcjz.zzboY);
                    zza(stringBuilder, 1, "dev_cert_hash", com_google_android_gms_internal_zzcjz.zzbvU);
                    zza(stringBuilder, 1, "app_store", com_google_android_gms_internal_zzcjz.zzboR);
                    zza(stringBuilder, 1, "upload_timestamp_millis", com_google_android_gms_internal_zzcjz.zzbvG);
                    zza(stringBuilder, 1, "start_timestamp_millis", com_google_android_gms_internal_zzcjz.zzbvH);
                    zza(stringBuilder, 1, "end_timestamp_millis", com_google_android_gms_internal_zzcjz.zzbvI);
                    zza(stringBuilder, 1, "previous_bundle_start_timestamp_millis", com_google_android_gms_internal_zzcjz.zzbvJ);
                    zza(stringBuilder, 1, "previous_bundle_end_timestamp_millis", com_google_android_gms_internal_zzcjz.zzbvK);
                    zza(stringBuilder, 1, "app_instance_id", com_google_android_gms_internal_zzcjz.zzbvT);
                    zza(stringBuilder, 1, "resettable_device_id", com_google_android_gms_internal_zzcjz.zzbvR);
                    zza(stringBuilder, 1, "limited_ad_tracking", com_google_android_gms_internal_zzcjz.zzbvS);
                    zza(stringBuilder, 1, "os_version", com_google_android_gms_internal_zzcjz.zzaY);
                    zza(stringBuilder, 1, "device_model", com_google_android_gms_internal_zzcjz.zzbvM);
                    zza(stringBuilder, 1, "user_default_language", com_google_android_gms_internal_zzcjz.zzbvN);
                    zza(stringBuilder, 1, "time_zone_offset_minutes", com_google_android_gms_internal_zzcjz.zzbvO);
                    zza(stringBuilder, 1, "bundle_sequential_index", com_google_android_gms_internal_zzcjz.zzbvV);
                    zza(stringBuilder, 1, "service_upload", com_google_android_gms_internal_zzcjz.zzbvW);
                    zza(stringBuilder, 1, "health_monitor", com_google_android_gms_internal_zzcjz.zzboU);
                    if (com_google_android_gms_internal_zzcjz.zzbwc.longValue() != 0) {
                        zza(stringBuilder, 1, "android_id", com_google_android_gms_internal_zzcjz.zzbwc);
                    }
                    zza(stringBuilder, 1, com_google_android_gms_internal_zzcjz.zzbvF);
                    zza(stringBuilder, 1, com_google_android_gms_internal_zzcjz.zzbvX);
                    zza(stringBuilder, 1, com_google_android_gms_internal_zzcjz.zzbvE);
                    zza(stringBuilder, 1);
                    stringBuilder.append("}\n");
                }
            }
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    @Nullable
    protected final String zzb(zzcez com_google_android_gms_internal_zzcez) {
        if (com_google_android_gms_internal_zzcez == null) {
            return null;
        }
        if (!zzyw()) {
            return com_google_android_gms_internal_zzcez.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("origin=");
        stringBuilder.append(com_google_android_gms_internal_zzcez.zzbpc);
        stringBuilder.append(",name=");
        stringBuilder.append(zzdW(com_google_android_gms_internal_zzcez.name));
        stringBuilder.append(",params=");
        stringBuilder.append(zzb(com_google_android_gms_internal_zzcez.zzbpM));
        return stringBuilder.toString();
    }

    @Nullable
    protected final String zzdW(String str) {
        return str == null ? null : zzyw() ? zza(str, Event.zzbog, Event.zzbof, zzbqI) : str;
    }

    @Nullable
    protected final String zzdX(String str) {
        return str == null ? null : zzyw() ? zza(str, Param.zzboi, Param.zzboh, zzbqJ) : str;
    }

    @Nullable
    protected final String zzdY(String str) {
        if (str == null) {
            return null;
        }
        if (!zzyw()) {
            return str;
        }
        if (!str.startsWith("_exp_")) {
            return zza(str, UserProperty.zzbon, UserProperty.zzbom, zzbqK);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("experiment_id");
        stringBuilder.append("(");
        stringBuilder.append(str);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    protected final void zzjD() {
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    public final /* bridge */ /* synthetic */ zzcfj zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjl zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzcja zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgg zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfl zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfw zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwH() {
        return super.zzwH();
    }

    public final /* bridge */ /* synthetic */ void zzwo() {
        super.zzwo();
    }

    public final /* bridge */ /* synthetic */ void zzwp() {
        super.zzwp();
    }

    public final /* bridge */ /* synthetic */ void zzwq() {
        super.zzwq();
    }

    public final /* bridge */ /* synthetic */ zzcec zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcej zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchl zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzcet zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcid zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchz zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfh zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcen zzwz() {
        return super.zzwz();
    }
}
