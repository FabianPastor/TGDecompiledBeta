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

public final class zzcfi extends zzchi {
    private static String[] zzbqI = new String[Event.zzbof.length];
    private static String[] zzbqJ = new String[Param.zzboh.length];
    private static String[] zzbqK = new String[UserProperty.zzbom.length];

    zzcfi(zzcgk com_google_android_gms_internal_zzcgk) {
        super(com_google_android_gms_internal_zzcgk);
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
            if (zzcjk.zzR(str, strArr[i])) {
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

    private final void zza(StringBuilder stringBuilder, int i, zzcjn com_google_android_gms_internal_zzcjn) {
        if (com_google_android_gms_internal_zzcjn != null) {
            zza(stringBuilder, i);
            stringBuilder.append("filter {\n");
            zza(stringBuilder, i, "complement", com_google_android_gms_internal_zzcjn.zzbuU);
            zza(stringBuilder, i, "param_name", zzdX(com_google_android_gms_internal_zzcjn.zzbuV));
            int i2 = i + 1;
            String str = "string_filter";
            zzcjq com_google_android_gms_internal_zzcjq = com_google_android_gms_internal_zzcjn.zzbuS;
            if (com_google_android_gms_internal_zzcjq != null) {
                zza(stringBuilder, i2);
                stringBuilder.append(str);
                stringBuilder.append(" {\n");
                if (com_google_android_gms_internal_zzcjq.zzbve != null) {
                    Object obj = "UNKNOWN_MATCH_TYPE";
                    switch (com_google_android_gms_internal_zzcjq.zzbve.intValue()) {
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
                zza(stringBuilder, i2, "expression", com_google_android_gms_internal_zzcjq.zzbvf);
                zza(stringBuilder, i2, "case_sensitive", com_google_android_gms_internal_zzcjq.zzbvg);
                if (com_google_android_gms_internal_zzcjq.zzbvh.length > 0) {
                    zza(stringBuilder, i2 + 1);
                    stringBuilder.append("expression_list {\n");
                    for (String str2 : com_google_android_gms_internal_zzcjq.zzbvh) {
                        zza(stringBuilder, i2 + 2);
                        stringBuilder.append(str2);
                        stringBuilder.append("\n");
                    }
                    stringBuilder.append("}\n");
                }
                zza(stringBuilder, i2);
                stringBuilder.append("}\n");
            }
            zza(stringBuilder, i + 1, "number_filter", com_google_android_gms_internal_zzcjn.zzbuT);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private final void zza(StringBuilder stringBuilder, int i, String str, zzcjo com_google_android_gms_internal_zzcjo) {
        if (com_google_android_gms_internal_zzcjo != null) {
            zza(stringBuilder, i);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzcjo.zzbuW != null) {
                Object obj = "UNKNOWN_COMPARISON_TYPE";
                switch (com_google_android_gms_internal_zzcjo.zzbuW.intValue()) {
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
            zza(stringBuilder, i, "match_as_float", com_google_android_gms_internal_zzcjo.zzbuX);
            zza(stringBuilder, i, "comparison_value", com_google_android_gms_internal_zzcjo.zzbuY);
            zza(stringBuilder, i, "min_comparison_value", com_google_android_gms_internal_zzcjo.zzbuZ);
            zza(stringBuilder, i, "max_comparison_value", com_google_android_gms_internal_zzcjo.zzbva);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, zzcjz com_google_android_gms_internal_zzcjz) {
        int i2 = 0;
        if (com_google_android_gms_internal_zzcjz != null) {
            int i3;
            int i4;
            int i5 = i + 1;
            zza(stringBuilder, i5);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzcjz.zzbwf != null) {
                zza(stringBuilder, i5 + 1);
                stringBuilder.append("results: ");
                long[] jArr = com_google_android_gms_internal_zzcjz.zzbwf;
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
            if (com_google_android_gms_internal_zzcjz.zzbwe != null) {
                zza(stringBuilder, i5 + 1);
                stringBuilder.append("status: ");
                long[] jArr2 = com_google_android_gms_internal_zzcjz.zzbwe;
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

    private final void zza(StringBuilder stringBuilder, int i, zzcju[] com_google_android_gms_internal_zzcjuArr) {
        if (com_google_android_gms_internal_zzcjuArr != null) {
            for (zzcju com_google_android_gms_internal_zzcju : com_google_android_gms_internal_zzcjuArr) {
                if (com_google_android_gms_internal_zzcju != null) {
                    zza(stringBuilder, 2);
                    stringBuilder.append("audience_membership {\n");
                    zza(stringBuilder, 2, "audience_id", com_google_android_gms_internal_zzcju.zzbuI);
                    zza(stringBuilder, 2, "new_audience", com_google_android_gms_internal_zzcju.zzbvu);
                    zza(stringBuilder, 2, "current_data", com_google_android_gms_internal_zzcju.zzbvs);
                    zza(stringBuilder, 2, "previous_data", com_google_android_gms_internal_zzcju.zzbvt);
                    zza(stringBuilder, 2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private final void zza(StringBuilder stringBuilder, int i, zzcjv[] com_google_android_gms_internal_zzcjvArr) {
        if (com_google_android_gms_internal_zzcjvArr != null) {
            for (zzcjv com_google_android_gms_internal_zzcjv : com_google_android_gms_internal_zzcjvArr) {
                if (com_google_android_gms_internal_zzcjv != null) {
                    zza(stringBuilder, 2);
                    stringBuilder.append("event {\n");
                    zza(stringBuilder, 2, "name", zzdW(com_google_android_gms_internal_zzcjv.name));
                    zza(stringBuilder, 2, "timestamp_millis", com_google_android_gms_internal_zzcjv.zzbvx);
                    zza(stringBuilder, 2, "previous_timestamp_millis", com_google_android_gms_internal_zzcjv.zzbvy);
                    zza(stringBuilder, 2, NewHtcHomeBadger.COUNT, com_google_android_gms_internal_zzcjv.count);
                    zzcjw[] com_google_android_gms_internal_zzcjwArr = com_google_android_gms_internal_zzcjv.zzbvw;
                    if (com_google_android_gms_internal_zzcjwArr != null) {
                        for (zzcjw com_google_android_gms_internal_zzcjw : com_google_android_gms_internal_zzcjwArr) {
                            if (com_google_android_gms_internal_zzcjw != null) {
                                zza(stringBuilder, 3);
                                stringBuilder.append("param {\n");
                                zza(stringBuilder, 3, "name", zzdX(com_google_android_gms_internal_zzcjw.name));
                                zza(stringBuilder, 3, "string_value", com_google_android_gms_internal_zzcjw.zzaIF);
                                zza(stringBuilder, 3, "int_value", com_google_android_gms_internal_zzcjw.zzbvA);
                                zza(stringBuilder, 3, "double_value", com_google_android_gms_internal_zzcjw.zzbuB);
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

    private final void zza(StringBuilder stringBuilder, int i, zzcka[] com_google_android_gms_internal_zzckaArr) {
        if (com_google_android_gms_internal_zzckaArr != null) {
            for (zzcka com_google_android_gms_internal_zzcka : com_google_android_gms_internal_zzckaArr) {
                if (com_google_android_gms_internal_zzcka != null) {
                    zza(stringBuilder, 2);
                    stringBuilder.append("user_property {\n");
                    zza(stringBuilder, 2, "set_timestamp_millis", com_google_android_gms_internal_zzcka.zzbwh);
                    zza(stringBuilder, 2, "name", zzdY(com_google_android_gms_internal_zzcka.name));
                    zza(stringBuilder, 2, "string_value", com_google_android_gms_internal_zzcka.zzaIF);
                    zza(stringBuilder, 2, "int_value", com_google_android_gms_internal_zzcka.zzbvA);
                    zza(stringBuilder, 2, "double_value", com_google_android_gms_internal_zzcka.zzbuB);
                    zza(stringBuilder, 2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    @Nullable
    private final String zzb(zzcev com_google_android_gms_internal_zzcev) {
        return com_google_android_gms_internal_zzcev == null ? null : !zzyw() ? com_google_android_gms_internal_zzcev.toString() : zzA(com_google_android_gms_internal_zzcev.zzyt());
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
    protected final String zza(zzcet com_google_android_gms_internal_zzcet) {
        if (com_google_android_gms_internal_zzcet == null) {
            return null;
        }
        if (!zzyw()) {
            return com_google_android_gms_internal_zzcet.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Event{appId='");
        stringBuilder.append(com_google_android_gms_internal_zzcet.mAppId);
        stringBuilder.append("', name='");
        stringBuilder.append(zzdW(com_google_android_gms_internal_zzcet.mName));
        stringBuilder.append("', params=");
        stringBuilder.append(zzb(com_google_android_gms_internal_zzcet.zzbpF));
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    protected final String zza(zzcjm com_google_android_gms_internal_zzcjm) {
        int i = 0;
        if (com_google_android_gms_internal_zzcjm == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nevent_filter {\n");
        zza(stringBuilder, 0, "filter_id", com_google_android_gms_internal_zzcjm.zzbuM);
        zza(stringBuilder, 0, "event_name", zzdW(com_google_android_gms_internal_zzcjm.zzbuN));
        zza(stringBuilder, 1, "event_count_filter", com_google_android_gms_internal_zzcjm.zzbuQ);
        stringBuilder.append("  filters {\n");
        zzcjn[] com_google_android_gms_internal_zzcjnArr = com_google_android_gms_internal_zzcjm.zzbuO;
        int length = com_google_android_gms_internal_zzcjnArr.length;
        while (i < length) {
            zza(stringBuilder, 2, com_google_android_gms_internal_zzcjnArr[i]);
            i++;
        }
        zza(stringBuilder, 1);
        stringBuilder.append("}\n}\n");
        return stringBuilder.toString();
    }

    protected final String zza(zzcjp com_google_android_gms_internal_zzcjp) {
        if (com_google_android_gms_internal_zzcjp == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nproperty_filter {\n");
        zza(stringBuilder, 0, "filter_id", com_google_android_gms_internal_zzcjp.zzbuM);
        zza(stringBuilder, 0, "property_name", zzdY(com_google_android_gms_internal_zzcjp.zzbvc));
        zza(stringBuilder, 1, com_google_android_gms_internal_zzcjp.zzbvd);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    protected final String zza(zzcjx com_google_android_gms_internal_zzcjx) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nbatch {\n");
        if (com_google_android_gms_internal_zzcjx.zzbvB != null) {
            for (zzcjy com_google_android_gms_internal_zzcjy : com_google_android_gms_internal_zzcjx.zzbvB) {
                if (!(com_google_android_gms_internal_zzcjy == null || com_google_android_gms_internal_zzcjy == null)) {
                    zza(stringBuilder, 1);
                    stringBuilder.append("bundle {\n");
                    zza(stringBuilder, 1, "protocol_version", com_google_android_gms_internal_zzcjy.zzbvD);
                    zza(stringBuilder, 1, "platform", com_google_android_gms_internal_zzcjy.zzbvL);
                    zza(stringBuilder, 1, "gmp_version", com_google_android_gms_internal_zzcjy.zzbvP);
                    zza(stringBuilder, 1, "uploading_gmp_version", com_google_android_gms_internal_zzcjy.zzbvQ);
                    zza(stringBuilder, 1, "config_version", com_google_android_gms_internal_zzcjy.zzbwb);
                    zza(stringBuilder, 1, "gmp_app_id", com_google_android_gms_internal_zzcjy.zzboQ);
                    zza(stringBuilder, 1, "app_id", com_google_android_gms_internal_zzcjy.zzaJ);
                    zza(stringBuilder, 1, "app_version", com_google_android_gms_internal_zzcjy.zzbgW);
                    zza(stringBuilder, 1, "app_version_major", com_google_android_gms_internal_zzcjy.zzbvY);
                    zza(stringBuilder, 1, "firebase_instance_id", com_google_android_gms_internal_zzcjy.zzboY);
                    zza(stringBuilder, 1, "dev_cert_hash", com_google_android_gms_internal_zzcjy.zzbvU);
                    zza(stringBuilder, 1, "app_store", com_google_android_gms_internal_zzcjy.zzboR);
                    zza(stringBuilder, 1, "upload_timestamp_millis", com_google_android_gms_internal_zzcjy.zzbvG);
                    zza(stringBuilder, 1, "start_timestamp_millis", com_google_android_gms_internal_zzcjy.zzbvH);
                    zza(stringBuilder, 1, "end_timestamp_millis", com_google_android_gms_internal_zzcjy.zzbvI);
                    zza(stringBuilder, 1, "previous_bundle_start_timestamp_millis", com_google_android_gms_internal_zzcjy.zzbvJ);
                    zza(stringBuilder, 1, "previous_bundle_end_timestamp_millis", com_google_android_gms_internal_zzcjy.zzbvK);
                    zza(stringBuilder, 1, "app_instance_id", com_google_android_gms_internal_zzcjy.zzbvT);
                    zza(stringBuilder, 1, "resettable_device_id", com_google_android_gms_internal_zzcjy.zzbvR);
                    zza(stringBuilder, 1, "limited_ad_tracking", com_google_android_gms_internal_zzcjy.zzbvS);
                    zza(stringBuilder, 1, "os_version", com_google_android_gms_internal_zzcjy.zzba);
                    zza(stringBuilder, 1, "device_model", com_google_android_gms_internal_zzcjy.zzbvM);
                    zza(stringBuilder, 1, "user_default_language", com_google_android_gms_internal_zzcjy.zzbvN);
                    zza(stringBuilder, 1, "time_zone_offset_minutes", com_google_android_gms_internal_zzcjy.zzbvO);
                    zza(stringBuilder, 1, "bundle_sequential_index", com_google_android_gms_internal_zzcjy.zzbvV);
                    zza(stringBuilder, 1, "service_upload", com_google_android_gms_internal_zzcjy.zzbvW);
                    zza(stringBuilder, 1, "health_monitor", com_google_android_gms_internal_zzcjy.zzboU);
                    if (com_google_android_gms_internal_zzcjy.zzbwc.longValue() != 0) {
                        zza(stringBuilder, 1, "android_id", com_google_android_gms_internal_zzcjy.zzbwc);
                    }
                    zza(stringBuilder, 1, com_google_android_gms_internal_zzcjy.zzbvF);
                    zza(stringBuilder, 1, com_google_android_gms_internal_zzcjy.zzbvX);
                    zza(stringBuilder, 1, com_google_android_gms_internal_zzcjy.zzbvE);
                    zza(stringBuilder, 1);
                    stringBuilder.append("}\n");
                }
            }
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    @Nullable
    protected final String zzb(zzcey com_google_android_gms_internal_zzcey) {
        if (com_google_android_gms_internal_zzcey == null) {
            return null;
        }
        if (!zzyw()) {
            return com_google_android_gms_internal_zzcey.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("origin=");
        stringBuilder.append(com_google_android_gms_internal_zzcey.zzbpc);
        stringBuilder.append(",name=");
        stringBuilder.append(zzdW(com_google_android_gms_internal_zzcey.name));
        stringBuilder.append(",params=");
        stringBuilder.append(zzb(com_google_android_gms_internal_zzcey.zzbpM));
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

    public final /* bridge */ /* synthetic */ zzcfi zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjk zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcge zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzciz zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfk zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfv zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcel zzwH() {
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

    public final /* bridge */ /* synthetic */ zzceb zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcei zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcff zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzces zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcic zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchy zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwz() {
        return super.zzwz();
    }
}
