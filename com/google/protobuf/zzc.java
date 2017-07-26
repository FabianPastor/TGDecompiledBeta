package com.google.protobuf;

final class zzc {
    private static Class<?> zzcrD = zzLr();

    private static Class<?> zzLr() {
        try {
            return Class.forName("com.google.protobuf.ExtensionRegistry");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static zzd zzLs() {
        if (zzcrD != null) {
            try {
                return (zzd) zzcrD.getMethod("getEmptyRegistry", new Class[0]).invoke(null, new Object[0]);
            } catch (Exception e) {
            }
        }
        return zzd.zzcrG;
    }
}
