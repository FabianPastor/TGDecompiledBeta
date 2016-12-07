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

public final class zzaos {
    private final ThreadLocal<Map<zzaqo<?>, zza<?>>> boa;
    private final Map<zzaqo<?>, zzapk<?>> bob;
    private final List<zzapl> boc;
    private final zzaps bod;
    private final boolean boe;
    private final boolean bof;
    private final boolean bog;
    private final boolean boh;
    final zzaow boi;
    final zzapf boj;

    static class zza<T> extends zzapk<T> {
        private zzapk<T> bol;

        zza() {
        }

        public void zza(zzapk<T> com_google_android_gms_internal_zzapk_T) {
            if (this.bol != null) {
                throw new AssertionError();
            }
            this.bol = com_google_android_gms_internal_zzapk_T;
        }

        public void zza(zzaqr com_google_android_gms_internal_zzaqr, T t) throws IOException {
            if (this.bol == null) {
                throw new IllegalStateException();
            }
            this.bol.zza(com_google_android_gms_internal_zzaqr, t);
        }

        public T zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
            if (this.bol != null) {
                return this.bol.zzb(com_google_android_gms_internal_zzaqp);
            }
            throw new IllegalStateException();
        }
    }

    public zzaos() {
        this(zzapt.boW, zzaoq.IDENTITY, Collections.emptyMap(), false, false, false, true, false, false, zzapi.DEFAULT, Collections.emptyList());
    }

    zzaos(zzapt com_google_android_gms_internal_zzapt, zzaor com_google_android_gms_internal_zzaor, Map<Type, zzaou<?>> map, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, zzapi com_google_android_gms_internal_zzapi, List<zzapl> list) {
        this.boa = new ThreadLocal();
        this.bob = Collections.synchronizedMap(new HashMap());
        this.boi = new zzaow(this) {
            final /* synthetic */ zzaos bok;

            {
                this.bok = r1;
            }
        };
        this.boj = new zzapf(this) {
            final /* synthetic */ zzaos bok;

            {
                this.bok = r1;
            }
        };
        this.bod = new zzaps(map);
        this.boe = z;
        this.bog = z3;
        this.bof = z4;
        this.boh = z5;
        List arrayList = new ArrayList();
        arrayList.add(zzaqn.bqZ);
        arrayList.add(zzaqi.bpG);
        arrayList.add(com_google_android_gms_internal_zzapt);
        arrayList.addAll(list);
        arrayList.add(zzaqn.bqG);
        arrayList.add(zzaqn.bqv);
        arrayList.add(zzaqn.bqp);
        arrayList.add(zzaqn.bqr);
        arrayList.add(zzaqn.bqt);
        arrayList.add(zzaqn.zza(Long.TYPE, Long.class, zza(com_google_android_gms_internal_zzapi)));
        arrayList.add(zzaqn.zza(Double.TYPE, Double.class, zzdf(z6)));
        arrayList.add(zzaqn.zza(Float.TYPE, Float.class, zzdg(z6)));
        arrayList.add(zzaqn.bqA);
        arrayList.add(zzaqn.bqC);
        arrayList.add(zzaqn.bqI);
        arrayList.add(zzaqn.bqK);
        arrayList.add(zzaqn.zza(BigDecimal.class, zzaqn.bqE));
        arrayList.add(zzaqn.zza(BigInteger.class, zzaqn.bqF));
        arrayList.add(zzaqn.bqM);
        arrayList.add(zzaqn.bqO);
        arrayList.add(zzaqn.bqS);
        arrayList.add(zzaqn.bqX);
        arrayList.add(zzaqn.bqQ);
        arrayList.add(zzaqn.bqm);
        arrayList.add(zzaqd.bpG);
        arrayList.add(zzaqn.bqV);
        arrayList.add(zzaql.bpG);
        arrayList.add(zzaqk.bpG);
        arrayList.add(zzaqn.bqT);
        arrayList.add(zzaqb.bpG);
        arrayList.add(zzaqn.bqk);
        arrayList.add(new zzaqc(this.bod));
        arrayList.add(new zzaqh(this.bod, z2));
        arrayList.add(new zzaqe(this.bod));
        arrayList.add(zzaqn.bra);
        arrayList.add(new zzaqj(this.bod, com_google_android_gms_internal_zzaor, com_google_android_gms_internal_zzapt));
        this.boc = Collections.unmodifiableList(arrayList);
    }

    private zzapk<Number> zza(zzapi com_google_android_gms_internal_zzapi) {
        return com_google_android_gms_internal_zzapi == zzapi.DEFAULT ? zzaqn.bqw : new zzapk<Number>(this) {
            final /* synthetic */ zzaos bok;

            {
                this.bok = r1;
            }

            public void zza(zzaqr com_google_android_gms_internal_zzaqr, Number number) throws IOException {
                if (number == null) {
                    com_google_android_gms_internal_zzaqr.bA();
                } else {
                    com_google_android_gms_internal_zzaqr.zzut(number.toString());
                }
            }

            public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
                return zzg(com_google_android_gms_internal_zzaqp);
            }

            public Number zzg(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
                if (com_google_android_gms_internal_zzaqp.bq() != zzaqq.NULL) {
                    return Long.valueOf(com_google_android_gms_internal_zzaqp.nextLong());
                }
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
        };
    }

    private static void zza(Object obj, zzaqp com_google_android_gms_internal_zzaqp) {
        if (obj != null) {
            try {
                if (com_google_android_gms_internal_zzaqp.bq() != zzaqq.END_DOCUMENT) {
                    throw new zzaoz("JSON document was not fully consumed.");
                }
            } catch (Throwable e) {
                throw new zzaph(e);
            } catch (Throwable e2) {
                throw new zzaoz(e2);
            }
        }
    }

    private zzapk<Number> zzdf(boolean z) {
        return z ? zzaqn.bqy : new zzapk<Number>(this) {
            final /* synthetic */ zzaos bok;

            {
                this.bok = r1;
            }

            public void zza(zzaqr com_google_android_gms_internal_zzaqr, Number number) throws IOException {
                if (number == null) {
                    com_google_android_gms_internal_zzaqr.bA();
                    return;
                }
                this.bok.zzm(number.doubleValue());
                com_google_android_gms_internal_zzaqr.zza(number);
            }

            public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
                return zze(com_google_android_gms_internal_zzaqp);
            }

            public Double zze(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
                if (com_google_android_gms_internal_zzaqp.bq() != zzaqq.NULL) {
                    return Double.valueOf(com_google_android_gms_internal_zzaqp.nextDouble());
                }
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
        };
    }

    private zzapk<Number> zzdg(boolean z) {
        return z ? zzaqn.bqx : new zzapk<Number>(this) {
            final /* synthetic */ zzaos bok;

            {
                this.bok = r1;
            }

            public void zza(zzaqr com_google_android_gms_internal_zzaqr, Number number) throws IOException {
                if (number == null) {
                    com_google_android_gms_internal_zzaqr.bA();
                    return;
                }
                this.bok.zzm((double) number.floatValue());
                com_google_android_gms_internal_zzaqr.zza(number);
            }

            public /* synthetic */ Object zzb(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
                return zzf(com_google_android_gms_internal_zzaqp);
            }

            public Float zzf(zzaqp com_google_android_gms_internal_zzaqp) throws IOException {
                if (com_google_android_gms_internal_zzaqp.bq() != zzaqq.NULL) {
                    return Float.valueOf((float) com_google_android_gms_internal_zzaqp.nextDouble());
                }
                com_google_android_gms_internal_zzaqp.nextNull();
                return null;
            }
        };
    }

    private void zzm(double d) {
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            throw new IllegalArgumentException(d + " is not a valid double value as per JSON specification. To override this" + " behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method.");
        }
    }

    public String toString() {
        return "{serializeNulls:" + this.boe + "factories:" + this.boc + ",instanceCreators:" + this.bod + "}";
    }

    public <T> zzapk<T> zza(zzapl com_google_android_gms_internal_zzapl, zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
        Object obj = null;
        if (!this.boc.contains(com_google_android_gms_internal_zzapl)) {
            obj = 1;
        }
        Object obj2 = obj;
        for (zzapl com_google_android_gms_internal_zzapl2 : this.boc) {
            if (obj2 != null) {
                zzapk<T> zza = com_google_android_gms_internal_zzapl2.zza(this, com_google_android_gms_internal_zzaqo_T);
                if (zza != null) {
                    return zza;
                }
            } else if (com_google_android_gms_internal_zzapl2 == com_google_android_gms_internal_zzapl) {
                obj2 = 1;
            }
        }
        String valueOf = String.valueOf(com_google_android_gms_internal_zzaqo_T);
        throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 22).append("GSON cannot serialize ").append(valueOf).toString());
    }

    public <T> zzapk<T> zza(zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
        zzapk<T> com_google_android_gms_internal_zzapk_T = (zzapk) this.bob.get(com_google_android_gms_internal_zzaqo_T);
        if (com_google_android_gms_internal_zzapk_T == null) {
            Map map;
            Map map2 = (Map) this.boa.get();
            Object obj = null;
            if (map2 == null) {
                HashMap hashMap = new HashMap();
                this.boa.set(hashMap);
                map = hashMap;
                obj = 1;
            } else {
                map = map2;
            }
            zza com_google_android_gms_internal_zzaos_zza = (zza) map.get(com_google_android_gms_internal_zzaqo_T);
            if (com_google_android_gms_internal_zzaos_zza == null) {
                try {
                    zza com_google_android_gms_internal_zzaos_zza2 = new zza();
                    map.put(com_google_android_gms_internal_zzaqo_T, com_google_android_gms_internal_zzaos_zza2);
                    for (zzapl zza : this.boc) {
                        com_google_android_gms_internal_zzapk_T = zza.zza(this, com_google_android_gms_internal_zzaqo_T);
                        if (com_google_android_gms_internal_zzapk_T != null) {
                            com_google_android_gms_internal_zzaos_zza2.zza(com_google_android_gms_internal_zzapk_T);
                            this.bob.put(com_google_android_gms_internal_zzaqo_T, com_google_android_gms_internal_zzapk_T);
                            map.remove(com_google_android_gms_internal_zzaqo_T);
                            if (obj != null) {
                                this.boa.remove();
                            }
                        }
                    }
                    String valueOf = String.valueOf(com_google_android_gms_internal_zzaqo_T);
                    throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 19).append("GSON cannot handle ").append(valueOf).toString());
                } catch (Throwable th) {
                    map.remove(com_google_android_gms_internal_zzaqo_T);
                    if (obj != null) {
                        this.boa.remove();
                    }
                }
            }
        }
        return com_google_android_gms_internal_zzapk_T;
    }

    public zzaqr zza(Writer writer) throws IOException {
        if (this.bog) {
            writer.write(")]}'\n");
        }
        zzaqr com_google_android_gms_internal_zzaqr = new zzaqr(writer);
        if (this.boh) {
            com_google_android_gms_internal_zzaqr.setIndent("  ");
        }
        com_google_android_gms_internal_zzaqr.zzdk(this.boe);
        return com_google_android_gms_internal_zzaqr;
    }

    public <T> T zza(zzaoy com_google_android_gms_internal_zzaoy, Class<T> cls) throws zzaph {
        return zzapy.zzp(cls).cast(zza(com_google_android_gms_internal_zzaoy, (Type) cls));
    }

    public <T> T zza(zzaoy com_google_android_gms_internal_zzaoy, Type type) throws zzaph {
        return com_google_android_gms_internal_zzaoy == null ? null : zza(new zzaqf(com_google_android_gms_internal_zzaoy), type);
    }

    public <T> T zza(zzaqp com_google_android_gms_internal_zzaqp, Type type) throws zzaoz, zzaph {
        boolean z = true;
        boolean isLenient = com_google_android_gms_internal_zzaqp.isLenient();
        com_google_android_gms_internal_zzaqp.setLenient(true);
        try {
            com_google_android_gms_internal_zzaqp.bq();
            z = false;
            T zzb = zza(zzaqo.zzl(type)).zzb(com_google_android_gms_internal_zzaqp);
            com_google_android_gms_internal_zzaqp.setLenient(isLenient);
            return zzb;
        } catch (Throwable e) {
            if (z) {
                com_google_android_gms_internal_zzaqp.setLenient(isLenient);
                return null;
            }
            throw new zzaph(e);
        } catch (Throwable e2) {
            throw new zzaph(e2);
        } catch (Throwable e22) {
            throw new zzaph(e22);
        } catch (Throwable th) {
            com_google_android_gms_internal_zzaqp.setLenient(isLenient);
        }
    }

    public <T> T zza(Reader reader, Type type) throws zzaoz, zzaph {
        zzaqp com_google_android_gms_internal_zzaqp = new zzaqp(reader);
        Object zza = zza(com_google_android_gms_internal_zzaqp, type);
        zza(zza, com_google_android_gms_internal_zzaqp);
        return zza;
    }

    public <T> T zza(String str, Type type) throws zzaph {
        return str == null ? null : zza(new StringReader(str), type);
    }

    public void zza(zzaoy com_google_android_gms_internal_zzaoy, zzaqr com_google_android_gms_internal_zzaqr) throws zzaoz {
        boolean isLenient = com_google_android_gms_internal_zzaqr.isLenient();
        com_google_android_gms_internal_zzaqr.setLenient(true);
        boolean bM = com_google_android_gms_internal_zzaqr.bM();
        com_google_android_gms_internal_zzaqr.zzdj(this.bof);
        boolean bN = com_google_android_gms_internal_zzaqr.bN();
        com_google_android_gms_internal_zzaqr.zzdk(this.boe);
        try {
            zzapz.zzb(com_google_android_gms_internal_zzaoy, com_google_android_gms_internal_zzaqr);
            com_google_android_gms_internal_zzaqr.setLenient(isLenient);
            com_google_android_gms_internal_zzaqr.zzdj(bM);
            com_google_android_gms_internal_zzaqr.zzdk(bN);
        } catch (Throwable e) {
            throw new zzaoz(e);
        } catch (Throwable th) {
            com_google_android_gms_internal_zzaqr.setLenient(isLenient);
            com_google_android_gms_internal_zzaqr.zzdj(bM);
            com_google_android_gms_internal_zzaqr.zzdk(bN);
        }
    }

    public void zza(zzaoy com_google_android_gms_internal_zzaoy, Appendable appendable) throws zzaoz {
        try {
            zza(com_google_android_gms_internal_zzaoy, zza(zzapz.zza(appendable)));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void zza(Object obj, Type type, zzaqr com_google_android_gms_internal_zzaqr) throws zzaoz {
        zzapk zza = zza(zzaqo.zzl(type));
        boolean isLenient = com_google_android_gms_internal_zzaqr.isLenient();
        com_google_android_gms_internal_zzaqr.setLenient(true);
        boolean bM = com_google_android_gms_internal_zzaqr.bM();
        com_google_android_gms_internal_zzaqr.zzdj(this.bof);
        boolean bN = com_google_android_gms_internal_zzaqr.bN();
        com_google_android_gms_internal_zzaqr.zzdk(this.boe);
        try {
            zza.zza(com_google_android_gms_internal_zzaqr, obj);
            com_google_android_gms_internal_zzaqr.setLenient(isLenient);
            com_google_android_gms_internal_zzaqr.zzdj(bM);
            com_google_android_gms_internal_zzaqr.zzdk(bN);
        } catch (Throwable e) {
            throw new zzaoz(e);
        } catch (Throwable th) {
            com_google_android_gms_internal_zzaqr.setLenient(isLenient);
            com_google_android_gms_internal_zzaqr.zzdj(bM);
            com_google_android_gms_internal_zzaqr.zzdk(bN);
        }
    }

    public void zza(Object obj, Type type, Appendable appendable) throws zzaoz {
        try {
            zza(obj, type, zza(zzapz.zza(appendable)));
        } catch (Throwable e) {
            throw new zzaoz(e);
        }
    }

    public String zzb(zzaoy com_google_android_gms_internal_zzaoy) {
        Appendable stringWriter = new StringWriter();
        zza(com_google_android_gms_internal_zzaoy, stringWriter);
        return stringWriter.toString();
    }

    public String zzc(Object obj, Type type) {
        Appendable stringWriter = new StringWriter();
        zza(obj, type, stringWriter);
        return stringWriter.toString();
    }

    public String zzck(Object obj) {
        return obj == null ? zzb(zzapa.bou) : zzc(obj, obj.getClass());
    }

    public <T> T zzf(String str, Class<T> cls) throws zzaph {
        return zzapy.zzp(cls).cast(zza(str, (Type) cls));
    }

    public <T> zzapk<T> zzk(Class<T> cls) {
        return zza(zzaqo.zzr(cls));
    }
}
