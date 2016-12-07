package com.google.android.gms.flags.impl;

import android.content.SharedPreferences;
import com.google.android.gms.internal.zzvv;
import java.util.concurrent.Callable;

public abstract class zza<T> {

    public static class zza extends zza<Boolean> {

        class AnonymousClass1 implements Callable<Boolean> {
            final /* synthetic */ SharedPreferences WG;
            final /* synthetic */ String WH;
            final /* synthetic */ Boolean WI;

            AnonymousClass1(SharedPreferences sharedPreferences, String str, Boolean bool) {
                this.WG = sharedPreferences;
                this.WH = str;
                this.WI = bool;
            }

            public /* synthetic */ Object call() throws Exception {
                return zzwa();
            }

            public Boolean zzwa() {
                return Boolean.valueOf(this.WG.getBoolean(this.WH, this.WI.booleanValue()));
            }
        }

        public static Boolean zza(SharedPreferences sharedPreferences, String str, Boolean bool) {
            return (Boolean) zzvv.zzb(new AnonymousClass1(sharedPreferences, str, bool));
        }
    }

    public static class zzb extends zza<Integer> {

        class AnonymousClass1 implements Callable<Integer> {
            final /* synthetic */ SharedPreferences WG;
            final /* synthetic */ String WH;
            final /* synthetic */ Integer WJ;

            AnonymousClass1(SharedPreferences sharedPreferences, String str, Integer num) {
                this.WG = sharedPreferences;
                this.WH = str;
                this.WJ = num;
            }

            public /* synthetic */ Object call() throws Exception {
                return zzbhg();
            }

            public Integer zzbhg() {
                return Integer.valueOf(this.WG.getInt(this.WH, this.WJ.intValue()));
            }
        }

        public static Integer zza(SharedPreferences sharedPreferences, String str, Integer num) {
            return (Integer) zzvv.zzb(new AnonymousClass1(sharedPreferences, str, num));
        }
    }

    public static class zzc extends zza<Long> {

        class AnonymousClass1 implements Callable<Long> {
            final /* synthetic */ SharedPreferences WG;
            final /* synthetic */ String WH;
            final /* synthetic */ Long WK;

            AnonymousClass1(SharedPreferences sharedPreferences, String str, Long l) {
                this.WG = sharedPreferences;
                this.WH = str;
                this.WK = l;
            }

            public /* synthetic */ Object call() throws Exception {
                return zzbhh();
            }

            public Long zzbhh() {
                return Long.valueOf(this.WG.getLong(this.WH, this.WK.longValue()));
            }
        }

        public static Long zza(SharedPreferences sharedPreferences, String str, Long l) {
            return (Long) zzvv.zzb(new AnonymousClass1(sharedPreferences, str, l));
        }
    }

    public static class zzd extends zza<String> {

        class AnonymousClass1 implements Callable<String> {
            final /* synthetic */ SharedPreferences WG;
            final /* synthetic */ String WH;
            final /* synthetic */ String WL;

            AnonymousClass1(SharedPreferences sharedPreferences, String str, String str2) {
                this.WG = sharedPreferences;
                this.WH = str;
                this.WL = str2;
            }

            public /* synthetic */ Object call() throws Exception {
                return zzaed();
            }

            public String zzaed() {
                return this.WG.getString(this.WH, this.WL);
            }
        }

        public static String zza(SharedPreferences sharedPreferences, String str, String str2) {
            return (String) zzvv.zzb(new AnonymousClass1(sharedPreferences, str, str2));
        }
    }
}
