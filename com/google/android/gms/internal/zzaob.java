package com.google.android.gms.internal;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class zzaob {
    private final ThreadLocal<Map<zzapx<?>, zza<?>>> bkJ;
    private final Map<zzapx<?>, zzaot<?>> bkK;
    private final List<zzaou> bkL;
    private final zzapb bkM;
    private final boolean bkN;
    private final boolean bkO;
    private final boolean bkP;
    private final boolean bkQ;
    final zzaof bkR;
    final zzaoo bkS;

    static class zza<T> extends zzaot<T> {
        private zzaot<T> bkU;

        zza() {
        }

        public void zza(zzaot<T> com_google_android_gms_internal_zzaot_T) {
            if (this.bkU != null) {
                throw new AssertionError();
            }
            this.bkU = com_google_android_gms_internal_zzaot_T;
        }

        public void zza(zzaqa com_google_android_gms_internal_zzaqa, T t) throws IOException {
            if (this.bkU == null) {
                throw new IllegalStateException();
            }
            this.bkU.zza(com_google_android_gms_internal_zzaqa, t);
        }

        public T zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
            if (this.bkU != null) {
                return this.bkU.zzb(com_google_android_gms_internal_zzapy);
            }
            throw new IllegalStateException();
        }
    }

    public zzaob() {
        this(zzapc.blF, zzanz.IDENTITY, Collections.emptyMap(), false, false, false, true, false, false, zzaor.DEFAULT, Collections.emptyList());
    }

    zzaob(zzapc com_google_android_gms_internal_zzapc, zzaoa com_google_android_gms_internal_zzaoa, Map<Type, zzaod<?>> map, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, zzaor com_google_android_gms_internal_zzaor, List<zzaou> list) {
        this.bkJ = new ThreadLocal();
        this.bkK = Collections.synchronizedMap(new HashMap());
        this.bkR = new zzaof(this) {
            final /* synthetic */ zzaob bkT;

            {
                this.bkT = r1;
            }
        };
        this.bkS = new zzaoo(this) {
            final /* synthetic */ zzaob bkT;

            {
                this.bkT = r1;
            }
        };
        this.bkM = new zzapb(map);
        this.bkN = z;
        this.bkP = z3;
        this.bkO = z4;
        this.bkQ = z5;
        List arrayList = new ArrayList();
        arrayList.add(zzapw.bnI);
        arrayList.add(zzapr.bmp);
        arrayList.add(com_google_android_gms_internal_zzapc);
        arrayList.addAll(list);
        arrayList.add(zzapw.bnp);
        arrayList.add(zzapw.bne);
        arrayList.add(zzapw.bmY);
        arrayList.add(zzapw.bna);
        arrayList.add(zzapw.bnc);
        arrayList.add(zzapw.zza(Long.TYPE, Long.class, zza(com_google_android_gms_internal_zzaor)));
        arrayList.add(zzapw.zza(Double.TYPE, Double.class, zzdd(z6)));
        arrayList.add(zzapw.zza(Float.TYPE, Float.class, zzde(z6)));
        arrayList.add(zzapw.bnj);
        arrayList.add(zzapw.bnl);
        arrayList.add(zzapw.bnr);
        arrayList.add(zzapw.bnt);
        arrayList.add(zzapw.zza(BigDecimal.class, zzapw.bnn));
        arrayList.add(zzapw.zza(BigInteger.class, zzapw.bno));
        arrayList.add(zzapw.bnv);
        arrayList.add(zzapw.bnx);
        arrayList.add(zzapw.bnB);
        arrayList.add(zzapw.bnG);
        arrayList.add(zzapw.bnz);
        arrayList.add(zzapw.bmV);
        arrayList.add(zzapm.bmp);
        arrayList.add(zzapw.bnE);
        arrayList.add(zzapu.bmp);
        arrayList.add(zzapt.bmp);
        arrayList.add(zzapw.bnC);
        arrayList.add(zzapk.bmp);
        arrayList.add(zzapw.bmT);
        arrayList.add(new zzapl(this.bkM));
        arrayList.add(new zzapq(this.bkM, z2));
        arrayList.add(new zzapn(this.bkM));
        arrayList.add(zzapw.bnJ);
        arrayList.add(new zzaps(this.bkM, com_google_android_gms_internal_zzaoa, com_google_android_gms_internal_zzapc));
        this.bkL = Collections.unmodifiableList(arrayList);
    }

    private zzaot<Number> zza(zzaor com_google_android_gms_internal_zzaor) {
        return com_google_android_gms_internal_zzaor == zzaor.DEFAULT ? zzapw.bnf : new zzaot<Number>(this) {
            final /* synthetic */ zzaob bkT;

            {
                this.bkT = r1;
            }

            public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) throws IOException {
                if (number == null) {
                    com_google_android_gms_internal_zzaqa.bx();
                } else {
                    com_google_android_gms_internal_zzaqa.zzut(number.toString());
                }
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
                return zzg(com_google_android_gms_internal_zzapy);
            }

            public Number zzg(zzapy com_google_android_gms_internal_zzapy) throws IOException {
                if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                    return Long.valueOf(com_google_android_gms_internal_zzapy.nextLong());
                }
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
        };
    }

    private static void zza(Object obj, zzapy com_google_android_gms_internal_zzapy) {
        if (obj != null) {
            try {
                if (com_google_android_gms_internal_zzapy.bn() != zzapz.END_DOCUMENT) {
                    throw new zzaoi("JSON document was not fully consumed.");
                }
            } catch (Throwable e) {
                throw new zzaoq(e);
            } catch (Throwable e2) {
                throw new zzaoi(e2);
            }
        }
    }

    private zzaot<Number> zzdd(boolean z) {
        return z ? zzapw.bnh : new zzaot<Number>(this) {
            final /* synthetic */ zzaob bkT;

            {
                this.bkT = r1;
            }

            public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) throws IOException {
                if (number == null) {
                    com_google_android_gms_internal_zzaqa.bx();
                    return;
                }
                this.bkT.zzn(number.doubleValue());
                com_google_android_gms_internal_zzaqa.zza(number);
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
                return zze(com_google_android_gms_internal_zzapy);
            }

            public Double zze(zzapy com_google_android_gms_internal_zzapy) throws IOException {
                if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                    return Double.valueOf(com_google_android_gms_internal_zzapy.nextDouble());
                }
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
        };
    }

    private zzaot<Number> zzde(boolean z) {
        return z ? zzapw.bng : new zzaot<Number>(this) {
            final /* synthetic */ zzaob bkT;

            {
                this.bkT = r1;
            }

            public void zza(zzaqa com_google_android_gms_internal_zzaqa, Number number) throws IOException {
                if (number == null) {
                    com_google_android_gms_internal_zzaqa.bx();
                    return;
                }
                this.bkT.zzn((double) number.floatValue());
                com_google_android_gms_internal_zzaqa.zza(number);
            }

            public /* synthetic */ Object zzb(zzapy com_google_android_gms_internal_zzapy) throws IOException {
                return zzf(com_google_android_gms_internal_zzapy);
            }

            public Float zzf(zzapy com_google_android_gms_internal_zzapy) throws IOException {
                if (com_google_android_gms_internal_zzapy.bn() != zzapz.NULL) {
                    return Float.valueOf((float) com_google_android_gms_internal_zzapy.nextDouble());
                }
                com_google_android_gms_internal_zzapy.nextNull();
                return null;
            }
        };
    }

    private void zzn(double d) {
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            throw new IllegalArgumentException(d + " is not a valid double value as per JSON specification. To override this" + " behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method.");
        }
    }

    public String toString() {
        return "{serializeNulls:" + this.bkN + "factories:" + this.bkL + ",instanceCreators:" + this.bkM + "}";
    }

    public <T> zzaot<T> zza(zzaou com_google_android_gms_internal_zzaou, zzapx<T> com_google_android_gms_internal_zzapx_T) {
        Object obj = null;
        if (!this.bkL.contains(com_google_android_gms_internal_zzaou)) {
            obj = 1;
        }
        Object obj2 = obj;
        for (zzaou com_google_android_gms_internal_zzaou2 : this.bkL) {
            if (obj2 != null) {
                zzaot<T> zza = com_google_android_gms_internal_zzaou2.zza(this, com_google_android_gms_internal_zzapx_T);
                if (zza != null) {
                    return zza;
                }
            } else if (com_google_android_gms_internal_zzaou2 == com_google_android_gms_internal_zzaou) {
                obj2 = 1;
            }
        }
        String valueOf = String.valueOf(com_google_android_gms_internal_zzapx_T);
        throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 22).append("GSON cannot serialize ").append(valueOf).toString());
    }

    public <T> zzaot<T> zza(zzapx<T> com_google_android_gms_internal_zzapx_T) {
        zzaot<T> com_google_android_gms_internal_zzaot_T = (zzaot) this.bkK.get(com_google_android_gms_internal_zzapx_T);
        if (com_google_android_gms_internal_zzaot_T == null) {
            Map map;
            Map map2 = (Map) this.bkJ.get();
            Object obj = null;
            if (map2 == null) {
                HashMap hashMap = new HashMap();
                this.bkJ.set(hashMap);
                map = hashMap;
                obj = 1;
            } else {
                map = map2;
            }
            zza com_google_android_gms_internal_zzaob_zza = (zza) map.get(com_google_android_gms_internal_zzapx_T);
            if (com_google_android_gms_internal_zzaob_zza == null) {
                try {
                    zza com_google_android_gms_internal_zzaob_zza2 = new zza();
                    map.put(com_google_android_gms_internal_zzapx_T, com_google_android_gms_internal_zzaob_zza2);
                    for (zzaou zza : this.bkL) {
                        com_google_android_gms_internal_zzaot_T = zza.zza(this, com_google_android_gms_internal_zzapx_T);
                        if (com_google_android_gms_internal_zzaot_T != null) {
                            com_google_android_gms_internal_zzaob_zza2.zza(com_google_android_gms_internal_zzaot_T);
                            this.bkK.put(com_google_android_gms_internal_zzapx_T, com_google_android_gms_internal_zzaot_T);
                            map.remove(com_google_android_gms_internal_zzapx_T);
                            if (obj != null) {
                                this.bkJ.remove();
                            }
                        }
                    }
                    String valueOf = String.valueOf(com_google_android_gms_internal_zzapx_T);
                    throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 19).append("GSON cannot handle ").append(valueOf).toString());
                } catch (Throwable th) {
                    map.remove(com_google_android_gms_internal_zzapx_T);
                    if (obj != null) {
                        this.bkJ.remove();
                    }
                }
            }
        }
        return com_google_android_gms_internal_zzaot_T;
    }

    public zzaqa zza(Writer writer) throws IOException {
        if (this.bkP) {
            writer.write(")]}'\n");
        }
        zzaqa com_google_android_gms_internal_zzaqa = new zzaqa(writer);
        if (this.bkQ) {
            com_google_android_gms_internal_zzaqa.setIndent("  ");
        }
        com_google_android_gms_internal_zzaqa.zzdi(this.bkN);
        return com_google_android_gms_internal_zzaqa;
    }

    public <T> T zza(zzaoh com_google_android_gms_internal_zzaoh, Class<T> cls) throws zzaoq {
        return zzaph.zzp(cls).cast(zza(com_google_android_gms_internal_zzaoh, (Type) cls));
    }

    public <T> T zza(zzaoh com_google_android_gms_internal_zzaoh, Type type) throws zzaoq {
        return com_google_android_gms_internal_zzaoh == null ? null : zza(new zzapo(com_google_android_gms_internal_zzaoh), type);
    }

    public <T> T zza(zzapy com_google_android_gms_internal_zzapy, Type type) throws zzaoi, zzaoq {
        boolean z = true;
        boolean isLenient = com_google_android_gms_internal_zzapy.isLenient();
        com_google_android_gms_internal_zzapy.setLenient(true);
        try {
            com_google_android_gms_internal_zzapy.bn();
            z = false;
            T zzb = zza(zzapx.zzl(type)).zzb(com_google_android_gms_internal_zzapy);
            com_google_android_gms_internal_zzapy.setLenient(isLenient);
            return zzb;
        } catch (Throwable e) {
            if (z) {
                com_google_android_gms_internal_zzapy.setLenient(isLenient);
                return null;
            }
            throw new zzaoq(e);
        } catch (Throwable e2) {
            throw new zzaoq(e2);
        } catch (Throwable e22) {
            throw new zzaoq(e22);
        } catch (Throwable th) {
            com_google_android_gms_internal_zzapy.setLenient(isLenient);
        }
    }

    public <T> T zza(Reader reader, Type type) throws zzaoi, zzaoq {
        zzapy com_google_android_gms_internal_zzapy = new zzapy(reader);
        Object zza = zza(com_google_android_gms_internal_zzapy, type);
        zza(zza, com_google_android_gms_internal_zzapy);
        return zza;
    }

    public <T> T zza(String str, Type type) throws zzaoq {
        return str == null ? null : zza(new StringReader(str), type);
    }

    public void zza(zzaoh com_google_android_gms_internal_zzaoh, zzaqa com_google_android_gms_internal_zzaqa) throws zzaoi {
        boolean isLenient = com_google_android_gms_internal_zzaqa.isLenient();
        com_google_android_gms_internal_zzaqa.setLenient(true);
        boolean bJ = com_google_android_gms_internal_zzaqa.bJ();
        com_google_android_gms_internal_zzaqa.zzdh(this.bkO);
        boolean bK = com_google_android_gms_internal_zzaqa.bK();
        com_google_android_gms_internal_zzaqa.zzdi(this.bkN);
        try {
            zzapi.zzb(com_google_android_gms_internal_zzaoh, com_google_android_gms_internal_zzaqa);
            com_google_android_gms_internal_zzaqa.setLenient(isLenient);
            com_google_android_gms_internal_zzaqa.zzdh(bJ);
            com_google_android_gms_internal_zzaqa.zzdi(bK);
        } catch (Throwable e) {
            throw new zzaoi(e);
        } catch (Throwable th) {
            com_google_android_gms_internal_zzaqa.setLenient(isLenient);
            com_google_android_gms_internal_zzaqa.zzdh(bJ);
            com_google_android_gms_internal_zzaqa.zzdi(bK);
        }
    }

    public void zza(zzaoh com_google_android_gms_internal_zzaoh, Appendable appendable) throws zzaoi {
        try {
            zza(com_google_android_gms_internal_zzaoh, zza(zzapi.zza(appendable)));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void zza(Object obj, Type type, zzaqa com_google_android_gms_internal_zzaqa) throws zzaoi {
        zzaot zza = zza(zzapx.zzl(type));
        boolean isLenient = com_google_android_gms_internal_zzaqa.isLenient();
        com_google_android_gms_internal_zzaqa.setLenient(true);
        boolean bJ = com_google_android_gms_internal_zzaqa.bJ();
        com_google_android_gms_internal_zzaqa.zzdh(this.bkO);
        boolean bK = com_google_android_gms_internal_zzaqa.bK();
        com_google_android_gms_internal_zzaqa.zzdi(this.bkN);
        try {
            zza.zza(com_google_android_gms_internal_zzaqa, obj);
            com_google_android_gms_internal_zzaqa.setLenient(isLenient);
            com_google_android_gms_internal_zzaqa.zzdh(bJ);
            com_google_android_gms_internal_zzaqa.zzdi(bK);
        } catch (Throwable e) {
            throw new zzaoi(e);
        } catch (Throwable th) {
            com_google_android_gms_internal_zzaqa.setLenient(isLenient);
            com_google_android_gms_internal_zzaqa.zzdh(bJ);
            com_google_android_gms_internal_zzaqa.zzdi(bK);
        }
    }

    public void zza(Object obj, Type type, Appendable appendable) throws zzaoi {
        try {
            zza(obj, type, zza(zzapi.zza(appendable)));
        } catch (Throwable e) {
            throw new zzaoi(e);
        }
    }

    public String zzb(zzaoh com_google_android_gms_internal_zzaoh) {
        Appendable stringWriter = new StringWriter();
        zza(com_google_android_gms_internal_zzaoh, stringWriter);
        return stringWriter.toString();
    }

    public String zzc(Object obj, Type type) {
        Appendable stringWriter = new StringWriter();
        zza(obj, type, stringWriter);
        return stringWriter.toString();
    }

    public String zzcl(Object obj) {
        return obj == null ? zzb(zzaoj.bld) : zzc(obj, obj.getClass());
    }

    public <T> T zzf(String str, Class<T> cls) throws zzaoq {
        return zzaph.zzp(cls).cast(zza(str, (Type) cls));
    }

    public <T> zzaot<T> zzk(Class<T> cls) {
        return zza(zzapx.zzr(cls));
    }
}
