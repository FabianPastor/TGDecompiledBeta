package com.google.android.gms.flags.impl;

import android.content.SharedPreferences;
import com.google.android.gms.internal.zzvb;
import java.util.concurrent.Callable;

public abstract class zza<T> {

    public static class zza extends zza<Boolean> {

        class AnonymousClass1 implements Callable<Boolean> {
            final /* synthetic */ String UA;
            final /* synthetic */ Boolean UB;
            final /* synthetic */ SharedPreferences Uz;

            AnonymousClass1(SharedPreferences sharedPreferences, String str, Boolean bool) {
                this.Uz = sharedPreferences;
                this.UA = str;
                this.UB = bool;
            }

            public /* synthetic */ Object call() throws Exception {
                return zzuq();
            }

            public Boolean zzuq() {
                return Boolean.valueOf(this.Uz.getBoolean(this.UA, this.UB.booleanValue()));
            }
        }

        public static Boolean zza(SharedPreferences sharedPreferences, String str, Boolean bool) {
            return (Boolean) zzvb.zzb(new AnonymousClass1(sharedPreferences, str, bool));
        }
    }

    public static class zzb extends zza<Integer> {

        class AnonymousClass1 implements Callable<Integer> {
            final /* synthetic */ String UA;
            final /* synthetic */ Integer UC;
            final /* synthetic */ SharedPreferences Uz;

            AnonymousClass1(SharedPreferences sharedPreferences, String str, Integer num) {
                this.Uz = sharedPreferences;
                this.UA = str;
                this.UC = num;
            }

            public /* synthetic */ Object call() throws Exception {
                return zzbho();
            }

            public Integer zzbho() {
                return Integer.valueOf(this.Uz.getInt(this.UA, this.UC.intValue()));
            }
        }

        public static Integer zza(SharedPreferences sharedPreferences, String str, Integer num) {
            return (Integer) zzvb.zzb(new AnonymousClass1(sharedPreferences, str, num));
        }
    }

    public static class zzc extends zza<Long> {

        class AnonymousClass1 implements Callable<Long> {
            final /* synthetic */ String UA;
            final /* synthetic */ Long UD;
            final /* synthetic */ SharedPreferences Uz;

            AnonymousClass1(SharedPreferences sharedPreferences, String str, Long l) {
                this.Uz = sharedPreferences;
                this.UA = str;
                this.UD = l;
            }

            public /* synthetic */ Object call() throws Exception {
                return zzbhp();
            }

            public Long zzbhp() {
                return Long.valueOf(this.Uz.getLong(this.UA, this.UD.longValue()));
            }
        }

        public static Long zza(SharedPreferences sharedPreferences, String str, Long l) {
            return (Long) zzvb.zzb(new AnonymousClass1(sharedPreferences, str, l));
        }
    }

    public static class zzd extends zza<String> {

        class AnonymousClass1 implements Callable<String> {
            final /* synthetic */ String UA;
            final /* synthetic */ String UE;
            final /* synthetic */ SharedPreferences Uz;

            AnonymousClass1(SharedPreferences sharedPreferences, String str, String str2) {
                this.Uz = sharedPreferences;
                this.UA = str;
                this.UE = str2;
            }

            public /* synthetic */ Object call() throws Exception {
                return zzacr();
            }

            public String zzacr() {
                return this.Uz.getString(this.UA, this.UE);
            }
        }

        public static String zza(SharedPreferences sharedPreferences, String str, String str2) {
            return (String) zzvb.zzb(new AnonymousClass1(sharedPreferences, str, str2));
        }
    }
}
