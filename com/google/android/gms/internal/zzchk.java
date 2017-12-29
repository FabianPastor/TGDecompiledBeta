package com.google.android.gms.internal;

import android.content.Context;
import android.os.Bundle;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.Param;
import com.google.android.gms.measurement.AppMeasurement.UserProperty;
import org.telegram.messenger.NotificationBadge.NewHtcHomeBadger;

public final class zzchk extends zzcjl {
    private static String[] zzjbq = new String[Event.zziwg.length];
    private static String[] zzjbr = new String[Param.zziwi.length];
    private static String[] zzjbs = new String[UserProperty.zziwn.length];

    zzchk(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
    }

    private static String zza(String str, String[] strArr, String[] strArr2, String[] strArr3) {
        boolean z = true;
        int i = 0;
        zzbq.checkNotNull(strArr);
        zzbq.checkNotNull(strArr2);
        zzbq.checkNotNull(strArr3);
        zzbq.checkArgument(strArr.length == strArr2.length);
        if (strArr.length != strArr3.length) {
            z = false;
        }
        zzbq.checkArgument(z);
        while (i < strArr.length) {
            if (zzclq.zzas(str, strArr[i])) {
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

    private final void zza(StringBuilder stringBuilder, int i, zzclt com_google_android_gms_internal_zzclt) {
        if (com_google_android_gms_internal_zzclt != null) {
            zza(stringBuilder, i);
            stringBuilder.append("filter {\n");
            zza(stringBuilder, i, "complement", com_google_android_gms_internal_zzclt.zzjke);
            zza(stringBuilder, i, "param_name", zzji(com_google_android_gms_internal_zzclt.zzjkf));
            int i2 = i + 1;
            String str = "string_filter";
            zzclw com_google_android_gms_internal_zzclw = com_google_android_gms_internal_zzclt.zzjkc;
            if (com_google_android_gms_internal_zzclw != null) {
                zza(stringBuilder, i2);
                stringBuilder.append(str);
                stringBuilder.append(" {\n");
                if (com_google_android_gms_internal_zzclw.zzjko != null) {
                    Object obj = "UNKNOWN_MATCH_TYPE";
                    switch (com_google_android_gms_internal_zzclw.zzjko.intValue()) {
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
                zza(stringBuilder, i2, "expression", com_google_android_gms_internal_zzclw.zzjkp);
                zza(stringBuilder, i2, "case_sensitive", com_google_android_gms_internal_zzclw.zzjkq);
                if (com_google_android_gms_internal_zzclw.zzjkr.length > 0) {
                    zza(stringBuilder, i2 + 1);
                    stringBuilder.append("expression_list {\n");
                    for (String str2 : com_google_android_gms_internal_zzclw.zzjkr) {
                        zza(stringBuilder, i2 + 2);
                        stringBuilder.append(str2);
                        stringBuilder.append("\n");
                    }
                    stringBuilder.append("}\n");
                }
                zza(stringBuilder, i2);
                stringBuilder.append("}\n");
            }
            zza(stringBuilder, i + 1, "number_filter", com_google_android_gms_internal_zzclt.zzjkd);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private final void zza(StringBuilder stringBuilder, int i, String str, zzclu com_google_android_gms_internal_zzclu) {
        if (com_google_android_gms_internal_zzclu != null) {
            zza(stringBuilder, i);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzclu.zzjkg != null) {
                Object obj = "UNKNOWN_COMPARISON_TYPE";
                switch (com_google_android_gms_internal_zzclu.zzjkg.intValue()) {
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
            zza(stringBuilder, i, "match_as_float", com_google_android_gms_internal_zzclu.zzjkh);
            zza(stringBuilder, i, "comparison_value", com_google_android_gms_internal_zzclu.zzjki);
            zza(stringBuilder, i, "min_comparison_value", com_google_android_gms_internal_zzclu.zzjkj);
            zza(stringBuilder, i, "max_comparison_value", com_google_android_gms_internal_zzclu.zzjkk);
            zza(stringBuilder, i);
            stringBuilder.append("}\n");
        }
    }

    private static void zza(StringBuilder stringBuilder, int i, String str, zzcmf com_google_android_gms_internal_zzcmf) {
        int i2 = 0;
        if (com_google_android_gms_internal_zzcmf != null) {
            int i3;
            int i4;
            int i5 = i + 1;
            zza(stringBuilder, i5);
            stringBuilder.append(str);
            stringBuilder.append(" {\n");
            if (com_google_android_gms_internal_zzcmf.zzjmq != null) {
                zza(stringBuilder, i5 + 1);
                stringBuilder.append("results: ");
                long[] jArr = com_google_android_gms_internal_zzcmf.zzjmq;
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
            if (com_google_android_gms_internal_zzcmf.zzjmp != null) {
                zza(stringBuilder, i5 + 1);
                stringBuilder.append("status: ");
                long[] jArr2 = com_google_android_gms_internal_zzcmf.zzjmp;
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

    private final void zza(StringBuilder stringBuilder, int i, zzcma[] com_google_android_gms_internal_zzcmaArr) {
        if (com_google_android_gms_internal_zzcmaArr != null) {
            for (zzcma com_google_android_gms_internal_zzcma : com_google_android_gms_internal_zzcmaArr) {
                if (com_google_android_gms_internal_zzcma != null) {
                    zza(stringBuilder, 2);
                    stringBuilder.append("audience_membership {\n");
                    zza(stringBuilder, 2, "audience_id", com_google_android_gms_internal_zzcma.zzjjs);
                    zza(stringBuilder, 2, "new_audience", com_google_android_gms_internal_zzcma.zzjlf);
                    zza(stringBuilder, 2, "current_data", com_google_android_gms_internal_zzcma.zzjld);
                    zza(stringBuilder, 2, "previous_data", com_google_android_gms_internal_zzcma.zzjle);
                    zza(stringBuilder, 2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private final void zza(StringBuilder stringBuilder, int i, zzcmb[] com_google_android_gms_internal_zzcmbArr) {
        if (com_google_android_gms_internal_zzcmbArr != null) {
            for (zzcmb com_google_android_gms_internal_zzcmb : com_google_android_gms_internal_zzcmbArr) {
                if (com_google_android_gms_internal_zzcmb != null) {
                    zza(stringBuilder, 2);
                    stringBuilder.append("event {\n");
                    zza(stringBuilder, 2, "name", zzjh(com_google_android_gms_internal_zzcmb.name));
                    zza(stringBuilder, 2, "timestamp_millis", com_google_android_gms_internal_zzcmb.zzjli);
                    zza(stringBuilder, 2, "previous_timestamp_millis", com_google_android_gms_internal_zzcmb.zzjlj);
                    zza(stringBuilder, 2, NewHtcHomeBadger.COUNT, com_google_android_gms_internal_zzcmb.count);
                    zzcmc[] com_google_android_gms_internal_zzcmcArr = com_google_android_gms_internal_zzcmb.zzjlh;
                    if (com_google_android_gms_internal_zzcmcArr != null) {
                        for (zzcmc com_google_android_gms_internal_zzcmc : com_google_android_gms_internal_zzcmcArr) {
                            if (com_google_android_gms_internal_zzcmc != null) {
                                zza(stringBuilder, 3);
                                stringBuilder.append("param {\n");
                                zza(stringBuilder, 3, "name", zzji(com_google_android_gms_internal_zzcmc.name));
                                zza(stringBuilder, 3, "string_value", com_google_android_gms_internal_zzcmc.zzgcc);
                                zza(stringBuilder, 3, "int_value", com_google_android_gms_internal_zzcmc.zzjll);
                                zza(stringBuilder, 3, "double_value", com_google_android_gms_internal_zzcmc.zzjjl);
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

    private final void zza(StringBuilder stringBuilder, int i, zzcmg[] com_google_android_gms_internal_zzcmgArr) {
        if (com_google_android_gms_internal_zzcmgArr != null) {
            for (zzcmg com_google_android_gms_internal_zzcmg : com_google_android_gms_internal_zzcmgArr) {
                if (com_google_android_gms_internal_zzcmg != null) {
                    zza(stringBuilder, 2);
                    stringBuilder.append("user_property {\n");
                    zza(stringBuilder, 2, "set_timestamp_millis", com_google_android_gms_internal_zzcmg.zzjms);
                    zza(stringBuilder, 2, "name", zzjj(com_google_android_gms_internal_zzcmg.name));
                    zza(stringBuilder, 2, "string_value", com_google_android_gms_internal_zzcmg.zzgcc);
                    zza(stringBuilder, 2, "int_value", com_google_android_gms_internal_zzcmg.zzjll);
                    zza(stringBuilder, 2, "double_value", com_google_android_gms_internal_zzcmg.zzjjl);
                    zza(stringBuilder, 2);
                    stringBuilder.append("}\n");
                }
            }
        }
    }

    private final boolean zzazc() {
        return this.zziwf.zzawy().zzae(3);
    }

    private final String zzb(zzcgx com_google_android_gms_internal_zzcgx) {
        return com_google_android_gms_internal_zzcgx == null ? null : !zzazc() ? com_google_android_gms_internal_zzcgx.toString() : zzx(com_google_android_gms_internal_zzcgx.zzayx());
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    protected final String zza(zzcgv com_google_android_gms_internal_zzcgv) {
        if (com_google_android_gms_internal_zzcgv == null) {
            return null;
        }
        if (!zzazc()) {
            return com_google_android_gms_internal_zzcgv.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Event{appId='");
        stringBuilder.append(com_google_android_gms_internal_zzcgv.mAppId);
        stringBuilder.append("', name='");
        stringBuilder.append(zzjh(com_google_android_gms_internal_zzcgv.mName));
        stringBuilder.append("', params=");
        stringBuilder.append(zzb(com_google_android_gms_internal_zzcgv.zzizj));
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    protected final String zza(zzcls com_google_android_gms_internal_zzcls) {
        int i = 0;
        if (com_google_android_gms_internal_zzcls == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nevent_filter {\n");
        zza(stringBuilder, 0, "filter_id", com_google_android_gms_internal_zzcls.zzjjw);
        zza(stringBuilder, 0, "event_name", zzjh(com_google_android_gms_internal_zzcls.zzjjx));
        zza(stringBuilder, 1, "event_count_filter", com_google_android_gms_internal_zzcls.zzjka);
        stringBuilder.append("  filters {\n");
        zzclt[] com_google_android_gms_internal_zzcltArr = com_google_android_gms_internal_zzcls.zzjjy;
        int length = com_google_android_gms_internal_zzcltArr.length;
        while (i < length) {
            zza(stringBuilder, 2, com_google_android_gms_internal_zzcltArr[i]);
            i++;
        }
        zza(stringBuilder, 1);
        stringBuilder.append("}\n}\n");
        return stringBuilder.toString();
    }

    protected final String zza(zzclv com_google_android_gms_internal_zzclv) {
        if (com_google_android_gms_internal_zzclv == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nproperty_filter {\n");
        zza(stringBuilder, 0, "filter_id", com_google_android_gms_internal_zzclv.zzjjw);
        zza(stringBuilder, 0, "property_name", zzjj(com_google_android_gms_internal_zzclv.zzjkm));
        zza(stringBuilder, 1, com_google_android_gms_internal_zzclv.zzjkn);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    protected final String zza(zzcmd com_google_android_gms_internal_zzcmd) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\nbatch {\n");
        if (com_google_android_gms_internal_zzcmd.zzjlm != null) {
            for (zzcme com_google_android_gms_internal_zzcme : com_google_android_gms_internal_zzcmd.zzjlm) {
                if (!(com_google_android_gms_internal_zzcme == null || com_google_android_gms_internal_zzcme == null)) {
                    zza(stringBuilder, 1);
                    stringBuilder.append("bundle {\n");
                    zza(stringBuilder, 1, "protocol_version", com_google_android_gms_internal_zzcme.zzjlo);
                    zza(stringBuilder, 1, "platform", com_google_android_gms_internal_zzcme.zzjlw);
                    zza(stringBuilder, 1, "gmp_version", com_google_android_gms_internal_zzcme.zzjma);
                    zza(stringBuilder, 1, "uploading_gmp_version", com_google_android_gms_internal_zzcme.zzjmb);
                    zza(stringBuilder, 1, "config_version", com_google_android_gms_internal_zzcme.zzjmn);
                    zza(stringBuilder, 1, "gmp_app_id", com_google_android_gms_internal_zzcme.zzixs);
                    zza(stringBuilder, 1, "app_id", com_google_android_gms_internal_zzcme.zzcn);
                    zza(stringBuilder, 1, "app_version", com_google_android_gms_internal_zzcme.zzifm);
                    zza(stringBuilder, 1, "app_version_major", com_google_android_gms_internal_zzcme.zzjmj);
                    zza(stringBuilder, 1, "firebase_instance_id", com_google_android_gms_internal_zzcme.zziya);
                    zza(stringBuilder, 1, "dev_cert_hash", com_google_android_gms_internal_zzcme.zzjmf);
                    zza(stringBuilder, 1, "app_store", com_google_android_gms_internal_zzcme.zzixt);
                    zza(stringBuilder, 1, "upload_timestamp_millis", com_google_android_gms_internal_zzcme.zzjlr);
                    zza(stringBuilder, 1, "start_timestamp_millis", com_google_android_gms_internal_zzcme.zzjls);
                    zza(stringBuilder, 1, "end_timestamp_millis", com_google_android_gms_internal_zzcme.zzjlt);
                    zza(stringBuilder, 1, "previous_bundle_start_timestamp_millis", com_google_android_gms_internal_zzcme.zzjlu);
                    zza(stringBuilder, 1, "previous_bundle_end_timestamp_millis", com_google_android_gms_internal_zzcme.zzjlv);
                    zza(stringBuilder, 1, "app_instance_id", com_google_android_gms_internal_zzcme.zzjme);
                    zza(stringBuilder, 1, "resettable_device_id", com_google_android_gms_internal_zzcme.zzjmc);
                    zza(stringBuilder, 1, "device_id", com_google_android_gms_internal_zzcme.zzjmm);
                    zza(stringBuilder, 1, "limited_ad_tracking", com_google_android_gms_internal_zzcme.zzjmd);
                    zza(stringBuilder, 1, "os_version", com_google_android_gms_internal_zzcme.zzdb);
                    zza(stringBuilder, 1, "device_model", com_google_android_gms_internal_zzcme.zzjlx);
                    zza(stringBuilder, 1, "user_default_language", com_google_android_gms_internal_zzcme.zzjly);
                    zza(stringBuilder, 1, "time_zone_offset_minutes", com_google_android_gms_internal_zzcme.zzjlz);
                    zza(stringBuilder, 1, "bundle_sequential_index", com_google_android_gms_internal_zzcme.zzjmg);
                    zza(stringBuilder, 1, "service_upload", com_google_android_gms_internal_zzcme.zzjmh);
                    zza(stringBuilder, 1, "health_monitor", com_google_android_gms_internal_zzcme.zzixw);
                    if (com_google_android_gms_internal_zzcme.zzfkk.longValue() != 0) {
                        zza(stringBuilder, 1, "android_id", com_google_android_gms_internal_zzcme.zzfkk);
                    }
                    zza(stringBuilder, 1, com_google_android_gms_internal_zzcme.zzjlq);
                    zza(stringBuilder, 1, com_google_android_gms_internal_zzcme.zzjmi);
                    zza(stringBuilder, 1, com_google_android_gms_internal_zzcme.zzjlp);
                    zza(stringBuilder, 1);
                    stringBuilder.append("}\n");
                }
            }
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString();
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

    protected final String zzb(zzcha com_google_android_gms_internal_zzcha) {
        if (com_google_android_gms_internal_zzcha == null) {
            return null;
        }
        if (!zzazc()) {
            return com_google_android_gms_internal_zzcha.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("origin=");
        stringBuilder.append(com_google_android_gms_internal_zzcha.zziyf);
        stringBuilder.append(",name=");
        stringBuilder.append(zzjh(com_google_android_gms_internal_zzcha.name));
        stringBuilder.append(",params=");
        stringBuilder.append(zzb(com_google_android_gms_internal_zzcha.zzizt));
        return stringBuilder.toString();
    }

    protected final String zzjh(String str) {
        return str == null ? null : zzazc() ? zza(str, Event.zziwh, Event.zziwg, zzjbq) : str;
    }

    protected final String zzji(String str) {
        return str == null ? null : zzazc() ? zza(str, Param.zziwj, Param.zziwi, zzjbr) : str;
    }

    protected final String zzjj(String str) {
        if (str == null) {
            return null;
        }
        if (!zzazc()) {
            return str;
        }
        if (!str.startsWith("_exp_")) {
            return zza(str, UserProperty.zziwo, UserProperty.zziwn, zzjbs);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("experiment_id");
        stringBuilder.append("(");
        stringBuilder.append(str);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public final /* bridge */ /* synthetic */ void zzve() {
        super.zzve();
    }

    public final /* bridge */ /* synthetic */ zzd zzws() {
        return super.zzws();
    }

    protected final String zzx(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        if (!zzazc()) {
            return bundle.toString();
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : bundle.keySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(", ");
            } else {
                stringBuilder.append("Bundle[{");
            }
            stringBuilder.append(zzji(str));
            stringBuilder.append("=");
            stringBuilder.append(bundle.get(str));
        }
        stringBuilder.append("}]");
        return stringBuilder.toString();
    }
}
