package com.google.android.gms.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public final class zzaps {
    private final Map<Type, zzaou<?>> bop;

    public zzaps(Map<Type, zzaou<?>> map) {
        this.bop = map;
    }

    private <T> zzapx<T> zzc(final Type type, Class<? super T> cls) {
        return Collection.class.isAssignableFrom(cls) ? SortedSet.class.isAssignableFrom(cls) ? new zzapx<T>(this) {
            final /* synthetic */ zzaps boR;

            {
                this.boR = r1;
            }

            public T bj() {
                return new TreeSet();
            }
        } : EnumSet.class.isAssignableFrom(cls) ? new zzapx<T>(this) {
            final /* synthetic */ zzaps boR;

            public T bj() {
                if (type instanceof ParameterizedType) {
                    Type type = ((ParameterizedType) type).getActualTypeArguments()[0];
                    if (type instanceof Class) {
                        return EnumSet.noneOf((Class) type);
                    }
                    String str = "Invalid EnumSet type: ";
                    String valueOf = String.valueOf(type.toString());
                    throw new zzaoz(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
                }
                str = "Invalid EnumSet type: ";
                valueOf = String.valueOf(type.toString());
                throw new zzaoz(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
            }
        } : Set.class.isAssignableFrom(cls) ? new zzapx<T>(this) {
            final /* synthetic */ zzaps boR;

            {
                this.boR = r1;
            }

            public T bj() {
                return new LinkedHashSet();
            }
        } : Queue.class.isAssignableFrom(cls) ? new zzapx<T>(this) {
            final /* synthetic */ zzaps boR;

            {
                this.boR = r1;
            }

            public T bj() {
                return new LinkedList();
            }
        } : new zzapx<T>(this) {
            final /* synthetic */ zzaps boR;

            {
                this.boR = r1;
            }

            public T bj() {
                return new ArrayList();
            }
        } : Map.class.isAssignableFrom(cls) ? SortedMap.class.isAssignableFrom(cls) ? new zzapx<T>(this) {
            final /* synthetic */ zzaps boR;

            {
                this.boR = r1;
            }

            public T bj() {
                return new TreeMap();
            }
        } : (!(type instanceof ParameterizedType) || String.class.isAssignableFrom(zzaqo.zzl(((ParameterizedType) type).getActualTypeArguments()[0]).bB())) ? new zzapx<T>(this) {
            final /* synthetic */ zzaps boR;

            {
                this.boR = r1;
            }

            public T bj() {
                return new zzapw();
            }
        } : new zzapx<T>(this) {
            final /* synthetic */ zzaps boR;

            {
                this.boR = r1;
            }

            public T bj() {
                return new LinkedHashMap();
            }
        } : null;
    }

    private <T> zzapx<T> zzd(final Type type, final Class<? super T> cls) {
        return new zzapx<T>(this) {
            final /* synthetic */ zzaps boR;
            private final zzaqa boS = zzaqa.bo();

            public T bj() {
                try {
                    return this.boS.zzf(cls);
                } catch (Throwable e) {
                    String valueOf = String.valueOf(type);
                    throw new RuntimeException(new StringBuilder(String.valueOf(valueOf).length() + 116).append("Unable to invoke no-args constructor for ").append(valueOf).append(". ").append("Register an InstanceCreator with Gson for this type may fix this problem.").toString(), e);
                }
            }
        };
    }

    private <T> zzapx<T> zzl(Class<? super T> cls) {
        try {
            final Constructor declaredConstructor = cls.getDeclaredConstructor(new Class[0]);
            if (!declaredConstructor.isAccessible()) {
                declaredConstructor.setAccessible(true);
            }
            return new zzapx<T>(this) {
                final /* synthetic */ zzaps boR;

                public T bj() {
                    String valueOf;
                    try {
                        return declaredConstructor.newInstance(null);
                    } catch (Throwable e) {
                        valueOf = String.valueOf(declaredConstructor);
                        throw new RuntimeException(new StringBuilder(String.valueOf(valueOf).length() + 30).append("Failed to invoke ").append(valueOf).append(" with no args").toString(), e);
                    } catch (InvocationTargetException e2) {
                        valueOf = String.valueOf(declaredConstructor);
                        throw new RuntimeException(new StringBuilder(String.valueOf(valueOf).length() + 30).append("Failed to invoke ").append(valueOf).append(" with no args").toString(), e2.getTargetException());
                    } catch (IllegalAccessException e3) {
                        throw new AssertionError(e3);
                    }
                }
            };
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public String toString() {
        return this.bop.toString();
    }

    public <T> zzapx<T> zzb(zzaqo<T> com_google_android_gms_internal_zzaqo_T) {
        final Type bC = com_google_android_gms_internal_zzaqo_T.bC();
        Class bB = com_google_android_gms_internal_zzaqo_T.bB();
        final zzaou com_google_android_gms_internal_zzaou = (zzaou) this.bop.get(bC);
        if (com_google_android_gms_internal_zzaou != null) {
            return new zzapx<T>(this) {
                final /* synthetic */ zzaps boR;

                public T bj() {
                    return com_google_android_gms_internal_zzaou.zza(bC);
                }
            };
        }
        com_google_android_gms_internal_zzaou = (zzaou) this.bop.get(bB);
        if (com_google_android_gms_internal_zzaou != null) {
            return new zzapx<T>(this) {
                final /* synthetic */ zzaps boR;

                public T bj() {
                    return com_google_android_gms_internal_zzaou.zza(bC);
                }
            };
        }
        zzapx<T> zzl = zzl(bB);
        if (zzl != null) {
            return zzl;
        }
        zzl = zzc(bC, bB);
        return zzl == null ? zzd(bC, bB) : zzl;
    }
}
