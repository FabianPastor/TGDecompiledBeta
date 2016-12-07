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

public final class zzapb {
    private final Map<Type, zzaod<?>> bkY;

    public zzapb(Map<Type, zzaod<?>> map) {
        this.bkY = map;
    }

    private <T> zzapg<T> zzc(final Type type, Class<? super T> cls) {
        return Collection.class.isAssignableFrom(cls) ? SortedSet.class.isAssignableFrom(cls) ? new zzapg<T>(this) {
            final /* synthetic */ zzapb blA;

            {
                this.blA = r1;
            }

            public T bg() {
                return new TreeSet();
            }
        } : EnumSet.class.isAssignableFrom(cls) ? new zzapg<T>(this) {
            final /* synthetic */ zzapb blA;

            public T bg() {
                if (type instanceof ParameterizedType) {
                    Type type = ((ParameterizedType) type).getActualTypeArguments()[0];
                    if (type instanceof Class) {
                        return EnumSet.noneOf((Class) type);
                    }
                    String str = "Invalid EnumSet type: ";
                    String valueOf = String.valueOf(type.toString());
                    throw new zzaoi(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
                }
                str = "Invalid EnumSet type: ";
                valueOf = String.valueOf(type.toString());
                throw new zzaoi(valueOf.length() != 0 ? str.concat(valueOf) : new String(str));
            }
        } : Set.class.isAssignableFrom(cls) ? new zzapg<T>(this) {
            final /* synthetic */ zzapb blA;

            {
                this.blA = r1;
            }

            public T bg() {
                return new LinkedHashSet();
            }
        } : Queue.class.isAssignableFrom(cls) ? new zzapg<T>(this) {
            final /* synthetic */ zzapb blA;

            {
                this.blA = r1;
            }

            public T bg() {
                return new LinkedList();
            }
        } : new zzapg<T>(this) {
            final /* synthetic */ zzapb blA;

            {
                this.blA = r1;
            }

            public T bg() {
                return new ArrayList();
            }
        } : Map.class.isAssignableFrom(cls) ? SortedMap.class.isAssignableFrom(cls) ? new zzapg<T>(this) {
            final /* synthetic */ zzapb blA;

            {
                this.blA = r1;
            }

            public T bg() {
                return new TreeMap();
            }
        } : (!(type instanceof ParameterizedType) || String.class.isAssignableFrom(zzapx.zzl(((ParameterizedType) type).getActualTypeArguments()[0]).by())) ? new zzapg<T>(this) {
            final /* synthetic */ zzapb blA;

            {
                this.blA = r1;
            }

            public T bg() {
                return new zzapf();
            }
        } : new zzapg<T>(this) {
            final /* synthetic */ zzapb blA;

            {
                this.blA = r1;
            }

            public T bg() {
                return new LinkedHashMap();
            }
        } : null;
    }

    private <T> zzapg<T> zzd(final Type type, final Class<? super T> cls) {
        return new zzapg<T>(this) {
            final /* synthetic */ zzapb blA;
            private final zzapj blB = zzapj.bl();

            public T bg() {
                try {
                    return this.blB.zzf(cls);
                } catch (Throwable e) {
                    String valueOf = String.valueOf(type);
                    throw new RuntimeException(new StringBuilder(String.valueOf(valueOf).length() + 116).append("Unable to invoke no-args constructor for ").append(valueOf).append(". ").append("Register an InstanceCreator with Gson for this type may fix this problem.").toString(), e);
                }
            }
        };
    }

    private <T> zzapg<T> zzl(Class<? super T> cls) {
        try {
            final Constructor declaredConstructor = cls.getDeclaredConstructor(new Class[0]);
            if (!declaredConstructor.isAccessible()) {
                declaredConstructor.setAccessible(true);
            }
            return new zzapg<T>(this) {
                final /* synthetic */ zzapb blA;

                public T bg() {
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
        return this.bkY.toString();
    }

    public <T> zzapg<T> zzb(zzapx<T> com_google_android_gms_internal_zzapx_T) {
        final Type bz = com_google_android_gms_internal_zzapx_T.bz();
        Class by = com_google_android_gms_internal_zzapx_T.by();
        final zzaod com_google_android_gms_internal_zzaod = (zzaod) this.bkY.get(bz);
        if (com_google_android_gms_internal_zzaod != null) {
            return new zzapg<T>(this) {
                final /* synthetic */ zzapb blA;

                public T bg() {
                    return com_google_android_gms_internal_zzaod.zza(bz);
                }
            };
        }
        com_google_android_gms_internal_zzaod = (zzaod) this.bkY.get(by);
        if (com_google_android_gms_internal_zzaod != null) {
            return new zzapg<T>(this) {
                final /* synthetic */ zzapb blA;

                public T bg() {
                    return com_google_android_gms_internal_zzaod.zza(bz);
                }
            };
        }
        zzapg<T> zzl = zzl(by);
        if (zzl != null) {
            return zzl;
        }
        zzl = zzc(bz, by);
        return zzl == null ? zzd(bz, by) : zzl;
    }
}
