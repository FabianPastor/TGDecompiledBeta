package com.google.android.gms.internal;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

public final class zzapa {
    static final Type[] blr = new Type[0];

    private static final class zza implements Serializable, GenericArrayType {
        private final Type bls;

        public zza(Type type) {
            this.bls = zzapa.zze(type);
        }

        public boolean equals(Object obj) {
            return (obj instanceof GenericArrayType) && zzapa.zza((Type) this, (GenericArrayType) obj);
        }

        public Type getGenericComponentType() {
            return this.bls;
        }

        public int hashCode() {
            return this.bls.hashCode();
        }

        public String toString() {
            return String.valueOf(zzapa.zzg(this.bls)).concat("[]");
        }
    }

    private static final class zzb implements Serializable, ParameterizedType {
        private final Type blt;
        private final Type blu;
        private final Type[] blv;

        public zzb(Type type, Type type2, Type... typeArr) {
            int i = 0;
            if (type2 instanceof Class) {
                Class cls = (Class) type2;
                int i2 = (Modifier.isStatic(cls.getModifiers()) || cls.getEnclosingClass() == null) ? 1 : 0;
                boolean z = (type == null && i2 == 0) ? false : true;
                zzaoz.zzbs(z);
            }
            this.blt = type == null ? null : zzapa.zze(type);
            this.blu = zzapa.zze(type2);
            this.blv = (Type[]) typeArr.clone();
            while (i < this.blv.length) {
                zzaoz.zzy(this.blv[i]);
                zzapa.zzi(this.blv[i]);
                this.blv[i] = zzapa.zze(this.blv[i]);
                i++;
            }
        }

        public boolean equals(Object obj) {
            return (obj instanceof ParameterizedType) && zzapa.zza((Type) this, (ParameterizedType) obj);
        }

        public Type[] getActualTypeArguments() {
            return (Type[]) this.blv.clone();
        }

        public Type getOwnerType() {
            return this.blt;
        }

        public Type getRawType() {
            return this.blu;
        }

        public int hashCode() {
            return (Arrays.hashCode(this.blv) ^ this.blu.hashCode()) ^ zzapa.zzcp(this.blt);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder((this.blv.length + 1) * 30);
            stringBuilder.append(zzapa.zzg(this.blu));
            if (this.blv.length == 0) {
                return stringBuilder.toString();
            }
            stringBuilder.append("<").append(zzapa.zzg(this.blv[0]));
            for (int i = 1; i < this.blv.length; i++) {
                stringBuilder.append(", ").append(zzapa.zzg(this.blv[i]));
            }
            return stringBuilder.append(">").toString();
        }
    }

    private static final class zzc implements Serializable, WildcardType {
        private final Type blw;
        private final Type blx;

        public zzc(Type[] typeArr, Type[] typeArr2) {
            boolean z = true;
            zzaoz.zzbs(typeArr2.length <= 1);
            zzaoz.zzbs(typeArr.length == 1);
            if (typeArr2.length == 1) {
                zzaoz.zzy(typeArr2[0]);
                zzapa.zzi(typeArr2[0]);
                if (typeArr[0] != Object.class) {
                    z = false;
                }
                zzaoz.zzbs(z);
                this.blx = zzapa.zze(typeArr2[0]);
                this.blw = Object.class;
                return;
            }
            zzaoz.zzy(typeArr[0]);
            zzapa.zzi(typeArr[0]);
            this.blx = null;
            this.blw = zzapa.zze(typeArr[0]);
        }

        public boolean equals(Object obj) {
            return (obj instanceof WildcardType) && zzapa.zza((Type) this, (WildcardType) obj);
        }

        public Type[] getLowerBounds() {
            if (this.blx == null) {
                return zzapa.blr;
            }
            return new Type[]{this.blx};
        }

        public Type[] getUpperBounds() {
            return new Type[]{this.blw};
        }

        public int hashCode() {
            return (this.blx != null ? this.blx.hashCode() + 31 : 1) ^ (this.blw.hashCode() + 31);
        }

        public String toString() {
            String str;
            String valueOf;
            if (this.blx != null) {
                str = "? super ";
                valueOf = String.valueOf(zzapa.zzg(this.blx));
                return valueOf.length() != 0 ? str.concat(valueOf) : new String(str);
            } else if (this.blw == Object.class) {
                return "?";
            } else {
                str = "? extends ";
                valueOf = String.valueOf(zzapa.zzg(this.blw));
                return valueOf.length() != 0 ? str.concat(valueOf) : new String(str);
            }
        }
    }

    static boolean equal(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    private static int zza(Object[] objArr, Object obj) {
        for (int i = 0; i < objArr.length; i++) {
            if (obj.equals(objArr[i])) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }

    private static Class<?> zza(TypeVariable<?> typeVariable) {
        GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
        return genericDeclaration instanceof Class ? (Class) genericDeclaration : null;
    }

    public static ParameterizedType zza(Type type, Type type2, Type... typeArr) {
        return new zzb(type, type2, typeArr);
    }

    public static Type zza(Type type, Class<?> cls) {
        Type zzb = zzb(type, cls, Collection.class);
        if (zzb instanceof WildcardType) {
            zzb = ((WildcardType) zzb).getUpperBounds()[0];
        }
        return zzb instanceof ParameterizedType ? ((ParameterizedType) zzb).getActualTypeArguments()[0] : Object.class;
    }

    static Type zza(Type type, Class<?> cls, Class<?> cls2) {
        if (cls2 == cls) {
            return type;
        }
        if (cls2.isInterface()) {
            Class[] interfaces = cls.getInterfaces();
            int length = interfaces.length;
            for (int i = 0; i < length; i++) {
                if (interfaces[i] == cls2) {
                    return cls.getGenericInterfaces()[i];
                }
                if (cls2.isAssignableFrom(interfaces[i])) {
                    return zza(cls.getGenericInterfaces()[i], interfaces[i], (Class) cls2);
                }
            }
        }
        if (!cls.isInterface()) {
            Class cls3;
            while (cls3 != Object.class) {
                Class superclass = cls3.getSuperclass();
                if (superclass == cls2) {
                    return cls3.getGenericSuperclass();
                }
                if (cls2.isAssignableFrom(superclass)) {
                    return zza(cls3.getGenericSuperclass(), superclass, (Class) cls2);
                }
                cls3 = superclass;
            }
        }
        return cls2;
    }

    public static Type zza(Type type, Class<?> cls, Type type2) {
        Type type3 = type2;
        while (type3 instanceof TypeVariable) {
            type3 = (TypeVariable) type3;
            type2 = zza(type, (Class) cls, (TypeVariable) type3);
            if (type2 == type3) {
                return type2;
            }
            type3 = type2;
        }
        Type componentType;
        Type zza;
        if ((type3 instanceof Class) && ((Class) type3).isArray()) {
            Class cls2 = (Class) type3;
            componentType = cls2.getComponentType();
            zza = zza(type, (Class) cls, componentType);
            return componentType != zza ? zzb(zza) : cls2;
        } else if (type3 instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType) type3;
            componentType = genericArrayType.getGenericComponentType();
            zza = zza(type, (Class) cls, componentType);
            return componentType != zza ? zzb(zza) : genericArrayType;
        } else if (type3 instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type3;
            componentType = parameterizedType.getOwnerType();
            Type zza2 = zza(type, (Class) cls, componentType);
            int i = zza2 != componentType ? 1 : 0;
            r4 = parameterizedType.getActualTypeArguments();
            int length = r4.length;
            int i2 = i;
            r1 = r4;
            for (int i3 = 0; i3 < length; i3++) {
                Type zza3 = zza(type, (Class) cls, r1[i3]);
                if (zza3 != r1[i3]) {
                    if (i2 == 0) {
                        r1 = (Type[]) r1.clone();
                        i2 = 1;
                    }
                    r1[i3] = zza3;
                }
            }
            return i2 != 0 ? zza(zza2, parameterizedType.getRawType(), r1) : parameterizedType;
        } else if (!(type3 instanceof WildcardType)) {
            return type3;
        } else {
            WildcardType wildcardType = (WildcardType) type3;
            r1 = wildcardType.getLowerBounds();
            r4 = wildcardType.getUpperBounds();
            if (r1.length == 1) {
                zza = zza(type, (Class) cls, r1[0]);
                return zza != r1[0] ? zzd(zza) : wildcardType;
            } else if (r4.length != 1) {
                return wildcardType;
            } else {
                componentType = zza(type, (Class) cls, r4[0]);
                return componentType != r4[0] ? zzc(componentType) : wildcardType;
            }
        }
    }

    static Type zza(Type type, Class<?> cls, TypeVariable<?> typeVariable) {
        Class zza = zza(typeVariable);
        if (zza == null) {
            return typeVariable;
        }
        Type zza2 = zza(type, (Class) cls, zza);
        if (!(zza2 instanceof ParameterizedType)) {
            return typeVariable;
        }
        return ((ParameterizedType) zza2).getActualTypeArguments()[zza(zza.getTypeParameters(), (Object) typeVariable)];
    }

    public static boolean zza(Type type, Type type2) {
        boolean z = true;
        if (type == type2) {
            return true;
        }
        if (type instanceof Class) {
            return type.equals(type2);
        }
        if (type instanceof ParameterizedType) {
            if (!(type2 instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType parameterizedType = (ParameterizedType) type;
            ParameterizedType parameterizedType2 = (ParameterizedType) type2;
            if (!(equal(parameterizedType.getOwnerType(), parameterizedType2.getOwnerType()) && parameterizedType.getRawType().equals(parameterizedType2.getRawType()) && Arrays.equals(parameterizedType.getActualTypeArguments(), parameterizedType2.getActualTypeArguments()))) {
                z = false;
            }
            return z;
        } else if (type instanceof GenericArrayType) {
            if (!(type2 instanceof GenericArrayType)) {
                return false;
            }
            return zza(((GenericArrayType) type).getGenericComponentType(), ((GenericArrayType) type2).getGenericComponentType());
        } else if (type instanceof WildcardType) {
            if (!(type2 instanceof WildcardType)) {
                return false;
            }
            WildcardType wildcardType = (WildcardType) type;
            WildcardType wildcardType2 = (WildcardType) type2;
            if (!(Arrays.equals(wildcardType.getUpperBounds(), wildcardType2.getUpperBounds()) && Arrays.equals(wildcardType.getLowerBounds(), wildcardType2.getLowerBounds()))) {
                z = false;
            }
            return z;
        } else if (!(type instanceof TypeVariable) || !(type2 instanceof TypeVariable)) {
            return false;
        } else {
            TypeVariable typeVariable = (TypeVariable) type;
            TypeVariable typeVariable2 = (TypeVariable) type2;
            if (!(typeVariable.getGenericDeclaration() == typeVariable2.getGenericDeclaration() && typeVariable.getName().equals(typeVariable2.getName()))) {
                z = false;
            }
            return z;
        }
    }

    public static GenericArrayType zzb(Type type) {
        return new zza(type);
    }

    static Type zzb(Type type, Class<?> cls, Class<?> cls2) {
        zzaoz.zzbs(cls2.isAssignableFrom(cls));
        return zza(type, (Class) cls, zza(type, (Class) cls, (Class) cls2));
    }

    public static Type[] zzb(Type type, Class<?> cls) {
        if (type == Properties.class) {
            return new Type[]{String.class, String.class};
        }
        Type zzb = zzb(type, cls, Map.class);
        if (zzb instanceof ParameterizedType) {
            return ((ParameterizedType) zzb).getActualTypeArguments();
        }
        return new Type[]{Object.class, Object.class};
    }

    public static WildcardType zzc(Type type) {
        return new zzc(new Type[]{type}, blr);
    }

    private static int zzcp(Object obj) {
        return obj != null ? obj.hashCode() : 0;
    }

    public static WildcardType zzd(Type type) {
        return new zzc(new Type[]{Object.class}, new Type[]{type});
    }

    public static Type zze(Type type) {
        if (type instanceof Class) {
            zza com_google_android_gms_internal_zzapa_zza;
            Class cls = (Class) type;
            if (cls.isArray()) {
                com_google_android_gms_internal_zzapa_zza = new zza(zze(cls.getComponentType()));
            } else {
                Object obj = cls;
            }
            return com_google_android_gms_internal_zzapa_zza;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return new zzb(parameterizedType.getOwnerType(), parameterizedType.getRawType(), parameterizedType.getActualTypeArguments());
        } else if (type instanceof GenericArrayType) {
            return new zza(((GenericArrayType) type).getGenericComponentType());
        } else {
            if (!(type instanceof WildcardType)) {
                return type;
            }
            WildcardType wildcardType = (WildcardType) type;
            return new zzc(wildcardType.getUpperBounds(), wildcardType.getLowerBounds());
        }
    }

    public static Class<?> zzf(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        }
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            zzaoz.zzbs(rawType instanceof Class);
            return (Class) rawType;
        } else if (type instanceof GenericArrayType) {
            return Array.newInstance(zzf(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
        } else {
            if (type instanceof TypeVariable) {
                return Object.class;
            }
            if (type instanceof WildcardType) {
                return zzf(((WildcardType) type).getUpperBounds()[0]);
            }
            String name = type == null ? "null" : type.getClass().getName();
            String valueOf = String.valueOf("Expected a Class, ParameterizedType, or GenericArrayType, but <");
            String valueOf2 = String.valueOf(type);
            throw new IllegalArgumentException(new StringBuilder(((String.valueOf(valueOf).length() + 13) + String.valueOf(valueOf2).length()) + String.valueOf(name).length()).append(valueOf).append(valueOf2).append("> is of type ").append(name).toString());
        }
    }

    public static String zzg(Type type) {
        return type instanceof Class ? ((Class) type).getName() : type.toString();
    }

    public static Type zzh(Type type) {
        return type instanceof GenericArrayType ? ((GenericArrayType) type).getGenericComponentType() : ((Class) type).getComponentType();
    }

    private static void zzi(Type type) {
        boolean z = ((type instanceof Class) && ((Class) type).isPrimitive()) ? false : true;
        zzaoz.zzbs(z);
    }
}
