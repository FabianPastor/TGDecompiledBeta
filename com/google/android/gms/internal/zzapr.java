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

public final class zzapr {
    static final Type[] boI = new Type[0];

    private static final class zza implements Serializable, GenericArrayType {
        private final Type boJ;

        public zza(Type type) {
            this.boJ = zzapr.zze(type);
        }

        public boolean equals(Object obj) {
            return (obj instanceof GenericArrayType) && zzapr.zza((Type) this, (GenericArrayType) obj);
        }

        public Type getGenericComponentType() {
            return this.boJ;
        }

        public int hashCode() {
            return this.boJ.hashCode();
        }

        public String toString() {
            return String.valueOf(zzapr.zzg(this.boJ)).concat("[]");
        }
    }

    private static final class zzb implements Serializable, ParameterizedType {
        private final Type boK;
        private final Type boL;
        private final Type[] boM;

        public zzb(Type type, Type type2, Type... typeArr) {
            int i = 0;
            if (type2 instanceof Class) {
                Class cls = (Class) type2;
                int i2 = (Modifier.isStatic(cls.getModifiers()) || cls.getEnclosingClass() == null) ? 1 : 0;
                boolean z = (type == null && i2 == 0) ? false : true;
                zzapq.zzbt(z);
            }
            this.boK = type == null ? null : zzapr.zze(type);
            this.boL = zzapr.zze(type2);
            this.boM = (Type[]) typeArr.clone();
            while (i < this.boM.length) {
                zzapq.zzy(this.boM[i]);
                zzapr.zzi(this.boM[i]);
                this.boM[i] = zzapr.zze(this.boM[i]);
                i++;
            }
        }

        public boolean equals(Object obj) {
            return (obj instanceof ParameterizedType) && zzapr.zza((Type) this, (ParameterizedType) obj);
        }

        public Type[] getActualTypeArguments() {
            return (Type[]) this.boM.clone();
        }

        public Type getOwnerType() {
            return this.boK;
        }

        public Type getRawType() {
            return this.boL;
        }

        public int hashCode() {
            return (Arrays.hashCode(this.boM) ^ this.boL.hashCode()) ^ zzapr.zzco(this.boK);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder((this.boM.length + 1) * 30);
            stringBuilder.append(zzapr.zzg(this.boL));
            if (this.boM.length == 0) {
                return stringBuilder.toString();
            }
            stringBuilder.append("<").append(zzapr.zzg(this.boM[0]));
            for (int i = 1; i < this.boM.length; i++) {
                stringBuilder.append(", ").append(zzapr.zzg(this.boM[i]));
            }
            return stringBuilder.append(">").toString();
        }
    }

    private static final class zzc implements Serializable, WildcardType {
        private final Type boN;
        private final Type boO;

        public zzc(Type[] typeArr, Type[] typeArr2) {
            boolean z = true;
            zzapq.zzbt(typeArr2.length <= 1);
            zzapq.zzbt(typeArr.length == 1);
            if (typeArr2.length == 1) {
                zzapq.zzy(typeArr2[0]);
                zzapr.zzi(typeArr2[0]);
                if (typeArr[0] != Object.class) {
                    z = false;
                }
                zzapq.zzbt(z);
                this.boO = zzapr.zze(typeArr2[0]);
                this.boN = Object.class;
                return;
            }
            zzapq.zzy(typeArr[0]);
            zzapr.zzi(typeArr[0]);
            this.boO = null;
            this.boN = zzapr.zze(typeArr[0]);
        }

        public boolean equals(Object obj) {
            return (obj instanceof WildcardType) && zzapr.zza((Type) this, (WildcardType) obj);
        }

        public Type[] getLowerBounds() {
            if (this.boO == null) {
                return zzapr.boI;
            }
            return new Type[]{this.boO};
        }

        public Type[] getUpperBounds() {
            return new Type[]{this.boN};
        }

        public int hashCode() {
            return (this.boO != null ? this.boO.hashCode() + 31 : 1) ^ (this.boN.hashCode() + 31);
        }

        public String toString() {
            String str;
            String valueOf;
            if (this.boO != null) {
                str = "? super ";
                valueOf = String.valueOf(zzapr.zzg(this.boO));
                return valueOf.length() != 0 ? str.concat(valueOf) : new String(str);
            } else if (this.boN == Object.class) {
                return "?";
            } else {
                str = "? extends ";
                valueOf = String.valueOf(zzapr.zzg(this.boN));
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
        zzapq.zzbt(cls2.isAssignableFrom(cls));
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
        return new zzc(new Type[]{type}, boI);
    }

    private static int zzco(Object obj) {
        return obj != null ? obj.hashCode() : 0;
    }

    public static WildcardType zzd(Type type) {
        return new zzc(new Type[]{Object.class}, new Type[]{type});
    }

    public static Type zze(Type type) {
        if (type instanceof Class) {
            zza com_google_android_gms_internal_zzapr_zza;
            Class cls = (Class) type;
            if (cls.isArray()) {
                com_google_android_gms_internal_zzapr_zza = new zza(zze(cls.getComponentType()));
            } else {
                Object obj = cls;
            }
            return com_google_android_gms_internal_zzapr_zza;
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
            zzapq.zzbt(rawType instanceof Class);
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
        zzapq.zzbt(z);
    }
}
