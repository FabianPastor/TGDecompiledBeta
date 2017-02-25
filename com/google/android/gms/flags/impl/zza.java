package com.google.android.gms.flags.impl;

import android.content.SharedPreferences;
import com.google.android.gms.internal.zzaqf;
import java.util.concurrent.Callable;

public abstract class zza<T> {

    public static class zza extends zza<Boolean> {

        class AnonymousClass1 implements Callable<Boolean> {
            final /* synthetic */ SharedPreferences zzaXo;
            final /* synthetic */ String zzaXp;
            final /* synthetic */ Boolean zzaXq;

            AnonymousClass1(SharedPreferences sharedPreferences, String str, Boolean bool) {
                this.zzaXo = sharedPreferences;
                this.zzaXp = str;
                this.zzaXq = bool;
            }

            public /* synthetic */ Object call() throws Exception {
                return zzbX();
            }

            public Boolean zzbX() {
                return Boolean.valueOf(this.zzaXo.getBoolean(this.zzaXp, this.zzaXq.booleanValue()));
            }
        }

        public static Boolean zza(SharedPreferences sharedPreferences, String str, Boolean bool) {
            return (Boolean) zzaqf.zzb(new AnonymousClass1(sharedPreferences, str, bool));
        }
    }

    public static class zzb extends zza<Integer> {

        class AnonymousClass1 implements Callable<Integer> {
            final /* synthetic */ SharedPreferences zzaXo;
            final /* synthetic */ String zzaXp;
            final /* synthetic */ Integer zzaXr;

            AnonymousClass1(SharedPreferences sharedPreferences, String str, Integer num) {
                this.zzaXo = sharedPreferences;
                this.zzaXp = str;
                this.zzaXr = num;
            }

            public /* synthetic */ Object call() throws Exception {
                return zzDF();
            }

            public Integer zzDF() {
                return Integer.valueOf(this.zzaXo.getInt(this.zzaXp, this.zzaXr.intValue()));
            }
        }

        public static Integer zza(SharedPreferences sharedPreferences, String str, Integer num) {
            return (Integer) zzaqf.zzb(new AnonymousClass1(sharedPreferences, str, num));
        }
    }

    public static class zzc extends zza<Long> {

        class AnonymousClass1 implements Callable<Long> {
            final /* synthetic */ SharedPreferences zzaXo;
            final /* synthetic */ String zzaXp;
            final /* synthetic */ Long zzaXs;

            AnonymousClass1(SharedPreferences sharedPreferences, String str, Long l) {
                this.zzaXo = sharedPreferences;
                this.zzaXp = str;
                this.zzaXs = l;
            }

            public /* synthetic */ Object call() throws Exception {
                return zzDG();
            }

            public Long zzDG() {
                return Long.valueOf(this.zzaXo.getLong(this.zzaXp, this.zzaXs.longValue()));
            }
        }

        public static Long zza(SharedPreferences sharedPreferences, String str, Long l) {
            return (Long) zzaqf.zzb(new AnonymousClass1(sharedPreferences, str, l));
        }
    }

    public static class zzd extends zza<String> {

        class AnonymousClass1 implements Callable<String> {
            final /* synthetic */ SharedPreferences zzaXo;
            final /* synthetic */ String zzaXp;
            final /* synthetic */ String zzaXt;

            AnonymousClass1(SharedPreferences sharedPreferences, String str, String str2) {
                this.zzaXo = sharedPreferences;
                this.zzaXp = str;
                this.zzaXt = str2;
            }

            public /* synthetic */ Object call() throws Exception {
                return zzbY();
            }

            public String zzbY() {
                return this.zzaXo.getString(this.zzaXp, this.zzaXt);
            }
        }

        public static String zza(SharedPreferences sharedPreferences, String str, String str2) {
            return (String) zzaqf.zzb(new AnonymousClass1(sharedPreferences, str, str2));
        }
    }
}
