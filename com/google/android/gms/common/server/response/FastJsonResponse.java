package com.google.android.gms.common.server.response;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzz;
import com.google.android.gms.common.server.converter.ConverterWrapper;
import com.google.android.gms.common.util.zzc;
import com.google.android.gms.common.util.zzp;
import com.google.android.gms.common.util.zzq;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class FastJsonResponse {

    public interface zza<I, O> {
        I convertBack(O o);
    }

    public static class Field<I, O> extends AbstractSafeParcelable {
        public static final zza CREATOR = new zza();
        protected final int Fg;
        protected final boolean Fh;
        protected final int Fi;
        protected final boolean Fj;
        protected final String Fk;
        protected final int Fl;
        protected final Class<? extends FastJsonResponse> Fm;
        protected final String Fn;
        private FieldMappingDictionary Fo;
        private zza<I, O> Fp;
        private final int mVersionCode;

        Field(int i, int i2, boolean z, int i3, boolean z2, String str, int i4, String str2, ConverterWrapper converterWrapper) {
            this.mVersionCode = i;
            this.Fg = i2;
            this.Fh = z;
            this.Fi = i3;
            this.Fj = z2;
            this.Fk = str;
            this.Fl = i4;
            if (str2 == null) {
                this.Fm = null;
                this.Fn = null;
            } else {
                this.Fm = SafeParcelResponse.class;
                this.Fn = str2;
            }
            if (converterWrapper == null) {
                this.Fp = null;
            } else {
                this.Fp = converterWrapper.zzawx();
            }
        }

        protected Field(int i, boolean z, int i2, boolean z2, String str, int i3, Class<? extends FastJsonResponse> cls, zza<I, O> com_google_android_gms_common_server_response_FastJsonResponse_zza_I__O) {
            this.mVersionCode = 1;
            this.Fg = i;
            this.Fh = z;
            this.Fi = i2;
            this.Fj = z2;
            this.Fk = str;
            this.Fl = i3;
            this.Fm = cls;
            if (cls == null) {
                this.Fn = null;
            } else {
                this.Fn = cls.getCanonicalName();
            }
            this.Fp = com_google_android_gms_common_server_response_FastJsonResponse_zza_I__O;
        }

        public static Field zza(String str, int i, zza<?, ?> com_google_android_gms_common_server_response_FastJsonResponse_zza___, boolean z) {
            return new Field(7, z, 0, false, str, i, null, com_google_android_gms_common_server_response_FastJsonResponse_zza___);
        }

        public static <T extends FastJsonResponse> Field<T, T> zza(String str, int i, Class<T> cls) {
            return new Field(11, false, 11, false, str, i, cls, null);
        }

        public static <T extends FastJsonResponse> Field<ArrayList<T>, ArrayList<T>> zzb(String str, int i, Class<T> cls) {
            return new Field(11, true, 11, true, str, i, cls, null);
        }

        public static Field<Integer, Integer> zzk(String str, int i) {
            return new Field(0, false, 0, false, str, i, null, null);
        }

        public static Field<Boolean, Boolean> zzl(String str, int i) {
            return new Field(6, false, 6, false, str, i, null, null);
        }

        public static Field<String, String> zzm(String str, int i) {
            return new Field(7, false, 7, false, str, i, null, null);
        }

        public I convertBack(O o) {
            return this.Fp.convertBack(o);
        }

        public int getVersionCode() {
            return this.mVersionCode;
        }

        public String toString() {
            com.google.android.gms.common.internal.zzz.zza zzg = zzz.zzx(this).zzg("versionCode", Integer.valueOf(this.mVersionCode)).zzg("typeIn", Integer.valueOf(this.Fg)).zzg("typeInArray", Boolean.valueOf(this.Fh)).zzg("typeOut", Integer.valueOf(this.Fi)).zzg("typeOutArray", Boolean.valueOf(this.Fj)).zzg("outputFieldName", this.Fk).zzg("safeParcelFieldId", Integer.valueOf(this.Fl)).zzg("concreteTypeName", zzaxh());
            Class zzaxg = zzaxg();
            if (zzaxg != null) {
                zzg.zzg("concreteType.class", zzaxg.getCanonicalName());
            }
            if (this.Fp != null) {
                zzg.zzg("converterName", this.Fp.getClass().getCanonicalName());
            }
            return zzg.toString();
        }

        public void writeToParcel(Parcel parcel, int i) {
            zza.zza(this, parcel, i);
        }

        public void zza(FieldMappingDictionary fieldMappingDictionary) {
            this.Fo = fieldMappingDictionary;
        }

        public int zzaxa() {
            return this.Fg;
        }

        public boolean zzaxb() {
            return this.Fh;
        }

        public int zzaxc() {
            return this.Fi;
        }

        public boolean zzaxd() {
            return this.Fj;
        }

        public String zzaxe() {
            return this.Fk;
        }

        public int zzaxf() {
            return this.Fl;
        }

        public Class<? extends FastJsonResponse> zzaxg() {
            return this.Fm;
        }

        String zzaxh() {
            return this.Fn == null ? null : this.Fn;
        }

        public boolean zzaxi() {
            return this.Fp != null;
        }

        ConverterWrapper zzaxj() {
            return this.Fp == null ? null : ConverterWrapper.zza(this.Fp);
        }

        public Map<String, Field<?, ?>> zzaxk() {
            zzaa.zzy(this.Fn);
            zzaa.zzy(this.Fo);
            return this.Fo.zzig(this.Fn);
        }
    }

    private void zza(StringBuilder stringBuilder, Field field, Object obj) {
        if (field.zzaxa() == 11) {
            stringBuilder.append(((FastJsonResponse) field.zzaxg().cast(obj)).toString());
        } else if (field.zzaxa() == 7) {
            stringBuilder.append("\"");
            stringBuilder.append(zzp.zzii((String) obj));
            stringBuilder.append("\"");
        } else {
            stringBuilder.append(obj);
        }
    }

    private void zza(StringBuilder stringBuilder, Field field, ArrayList<Object> arrayList) {
        stringBuilder.append("[");
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            Object obj = arrayList.get(i);
            if (obj != null) {
                zza(stringBuilder, field, obj);
            }
        }
        stringBuilder.append("]");
    }

    public String toString() {
        Map zzawz = zzawz();
        StringBuilder stringBuilder = new StringBuilder(100);
        for (String str : zzawz.keySet()) {
            Field field = (Field) zzawz.get(str);
            if (zza(field)) {
                Object zza = zza(field, zzb(field));
                if (stringBuilder.length() == 0) {
                    stringBuilder.append("{");
                } else {
                    stringBuilder.append(",");
                }
                stringBuilder.append("\"").append(str).append("\":");
                if (zza != null) {
                    switch (field.zzaxc()) {
                        case 8:
                            stringBuilder.append("\"").append(zzc.zzq((byte[]) zza)).append("\"");
                            break;
                        case 9:
                            stringBuilder.append("\"").append(zzc.zzr((byte[]) zza)).append("\"");
                            break;
                        case 10:
                            zzq.zza(stringBuilder, (HashMap) zza);
                            break;
                        default:
                            if (!field.zzaxb()) {
                                zza(stringBuilder, field, zza);
                                break;
                            }
                            zza(stringBuilder, field, (ArrayList) zza);
                            break;
                    }
                }
                stringBuilder.append("null");
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.append("}");
        } else {
            stringBuilder.append("{}");
        }
        return stringBuilder.toString();
    }

    protected <O, I> I zza(Field<I, O> field, Object obj) {
        return field.Fp != null ? field.convertBack(obj) : obj;
    }

    protected boolean zza(Field field) {
        return field.zzaxc() == 11 ? field.zzaxd() ? zzif(field.zzaxe()) : zzie(field.zzaxe()) : zzid(field.zzaxe());
    }

    public abstract Map<String, Field<?, ?>> zzawz();

    protected Object zzb(Field field) {
        String zzaxe = field.zzaxe();
        if (field.zzaxg() == null) {
            return zzic(field.zzaxe());
        }
        zzaa.zza(zzic(field.zzaxe()) == null, "Concrete field shouldn't be value object: %s", field.zzaxe());
        if (field.zzaxd()) {
        }
        try {
            char toUpperCase = Character.toUpperCase(zzaxe.charAt(0));
            String valueOf = String.valueOf(zzaxe.substring(1));
            return getClass().getMethod(new StringBuilder(String.valueOf(valueOf).length() + 4).append("get").append(toUpperCase).append(valueOf).toString(), new Class[0]).invoke(this, new Object[0]);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Object zzic(String str);

    protected abstract boolean zzid(String str);

    protected boolean zzie(String str) {
        throw new UnsupportedOperationException("Concrete types not supported");
    }

    protected boolean zzif(String str) {
        throw new UnsupportedOperationException("Concrete type arrays not supported");
    }
}
