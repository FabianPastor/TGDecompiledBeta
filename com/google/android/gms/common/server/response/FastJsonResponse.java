package com.google.android.gms.common.server.response;

import android.os.Parcel;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;
import com.google.android.gms.common.internal.zzac;
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

        int zzavq();

        int zzavr();
    }

    public static class Field<I, O> extends AbstractSafeParcelable {
        public static final zza CREATOR = new zza();
        protected final String DA;
        private FieldMappingDictionary DB;
        private zza<I, O> DC;
        protected final int Dt;
        protected final boolean Du;
        protected final int Dv;
        protected final boolean Dw;
        protected final String Dx;
        protected final int Dy;
        protected final Class<? extends FastJsonResponse> Dz;
        private final int mVersionCode;

        Field(int i, int i2, boolean z, int i3, boolean z2, String str, int i4, String str2, ConverterWrapper converterWrapper) {
            this.mVersionCode = i;
            this.Dt = i2;
            this.Du = z;
            this.Dv = i3;
            this.Dw = z2;
            this.Dx = str;
            this.Dy = i4;
            if (str2 == null) {
                this.Dz = null;
                this.DA = null;
            } else {
                this.Dz = SafeParcelResponse.class;
                this.DA = str2;
            }
            if (converterWrapper == null) {
                this.DC = null;
            } else {
                this.DC = converterWrapper.zzavo();
            }
        }

        protected Field(int i, boolean z, int i2, boolean z2, String str, int i3, Class<? extends FastJsonResponse> cls, zza<I, O> com_google_android_gms_common_server_response_FastJsonResponse_zza_I__O) {
            this.mVersionCode = 1;
            this.Dt = i;
            this.Du = z;
            this.Dv = i2;
            this.Dw = z2;
            this.Dx = str;
            this.Dy = i3;
            this.Dz = cls;
            if (cls == null) {
                this.DA = null;
            } else {
                this.DA = cls.getCanonicalName();
            }
            this.DC = com_google_android_gms_common_server_response_FastJsonResponse_zza_I__O;
        }

        public static Field zza(String str, int i, zza<?, ?> com_google_android_gms_common_server_response_FastJsonResponse_zza___, boolean z) {
            return new Field(com_google_android_gms_common_server_response_FastJsonResponse_zza___.zzavq(), z, com_google_android_gms_common_server_response_FastJsonResponse_zza___.zzavr(), false, str, i, null, com_google_android_gms_common_server_response_FastJsonResponse_zza___);
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
            return this.DC.convertBack(o);
        }

        public int getVersionCode() {
            return this.mVersionCode;
        }

        public String toString() {
            com.google.android.gms.common.internal.zzab.zza zzg = zzab.zzx(this).zzg("versionCode", Integer.valueOf(this.mVersionCode)).zzg("typeIn", Integer.valueOf(this.Dt)).zzg("typeInArray", Boolean.valueOf(this.Du)).zzg("typeOut", Integer.valueOf(this.Dv)).zzg("typeOutArray", Boolean.valueOf(this.Dw)).zzg("outputFieldName", this.Dx).zzg("safeParcelFieldId", Integer.valueOf(this.Dy)).zzg("concreteTypeName", zzawa());
            Class zzavz = zzavz();
            if (zzavz != null) {
                zzg.zzg("concreteType.class", zzavz.getCanonicalName());
            }
            if (this.DC != null) {
                zzg.zzg("converterName", this.DC.getClass().getCanonicalName());
            }
            return zzg.toString();
        }

        public void writeToParcel(Parcel parcel, int i) {
            zza com_google_android_gms_common_server_response_zza = CREATOR;
            zza.zza(this, parcel, i);
        }

        public void zza(FieldMappingDictionary fieldMappingDictionary) {
            this.DB = fieldMappingDictionary;
        }

        public int zzavq() {
            return this.Dt;
        }

        public int zzavr() {
            return this.Dv;
        }

        public boolean zzavv() {
            return this.Du;
        }

        public boolean zzavw() {
            return this.Dw;
        }

        public String zzavx() {
            return this.Dx;
        }

        public int zzavy() {
            return this.Dy;
        }

        public Class<? extends FastJsonResponse> zzavz() {
            return this.Dz;
        }

        String zzawa() {
            return this.DA == null ? null : this.DA;
        }

        public boolean zzawb() {
            return this.DC != null;
        }

        ConverterWrapper zzawc() {
            return this.DC == null ? null : ConverterWrapper.zza(this.DC);
        }

        public Map<String, Field<?, ?>> zzawd() {
            zzac.zzy(this.DA);
            zzac.zzy(this.DB);
            return this.DB.zzie(this.DA);
        }
    }

    private void zza(StringBuilder stringBuilder, Field field, Object obj) {
        if (field.zzavq() == 11) {
            stringBuilder.append(((FastJsonResponse) field.zzavz().cast(obj)).toString());
        } else if (field.zzavq() == 7) {
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
        Map zzavs = zzavs();
        StringBuilder stringBuilder = new StringBuilder(100);
        for (String str : zzavs.keySet()) {
            Field field = (Field) zzavs.get(str);
            if (zza(field)) {
                Object zza = zza(field, zzb(field));
                if (stringBuilder.length() == 0) {
                    stringBuilder.append("{");
                } else {
                    stringBuilder.append(",");
                }
                stringBuilder.append("\"").append(str).append("\":");
                if (zza != null) {
                    switch (field.zzavr()) {
                        case 8:
                            stringBuilder.append("\"").append(zzc.zzp((byte[]) zza)).append("\"");
                            break;
                        case 9:
                            stringBuilder.append("\"").append(zzc.zzq((byte[]) zza)).append("\"");
                            break;
                        case 10:
                            zzq.zza(stringBuilder, (HashMap) zza);
                            break;
                        default:
                            if (!field.zzavv()) {
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
        return field.DC != null ? field.convertBack(obj) : obj;
    }

    protected boolean zza(Field field) {
        return field.zzavr() == 11 ? field.zzavw() ? zzid(field.zzavx()) : zzic(field.zzavx()) : zzib(field.zzavx());
    }

    public abstract Map<String, Field<?, ?>> zzavs();

    public HashMap<String, Object> zzavt() {
        return null;
    }

    public HashMap<String, Object> zzavu() {
        return null;
    }

    protected Object zzb(Field field) {
        String zzavx = field.zzavx();
        if (field.zzavz() == null) {
            return zzia(field.zzavx());
        }
        zzac.zza(zzia(field.zzavx()) == null, "Concrete field shouldn't be value object: %s", field.zzavx());
        Map zzavu = field.zzavw() ? zzavu() : zzavt();
        if (zzavu != null) {
            return zzavu.get(zzavx);
        }
        try {
            char toUpperCase = Character.toUpperCase(zzavx.charAt(0));
            String valueOf = String.valueOf(zzavx.substring(1));
            return getClass().getMethod(new StringBuilder(String.valueOf(valueOf).length() + 4).append("get").append(toUpperCase).append(valueOf).toString(), new Class[0]).invoke(this, new Object[0]);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Object zzia(String str);

    protected abstract boolean zzib(String str);

    protected boolean zzic(String str) {
        throw new UnsupportedOperationException("Concrete types not supported");
    }

    protected boolean zzid(String str) {
        throw new UnsupportedOperationException("Concrete type arrays not supported");
    }
}
