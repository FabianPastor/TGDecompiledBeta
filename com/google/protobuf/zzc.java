package com.google.protobuf;

final class zzc {
    private static Class<?> zzcrO = zzLq();

    private static Class<?> zzLq() {
        try {
            return Class.forName("com.google.protobuf.ExtensionRegistry");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static zzd zzLr() {
        if (zzcrO != null) {
            try {
                return (zzd) zzcrO.getMethod("getEmptyRegistry", new Class[0]).invoke(null, new Object[0]);
            } catch (Exception e) {
            }
        }
        return zzd.zzcrR;
    }
}
