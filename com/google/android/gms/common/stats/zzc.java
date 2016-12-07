package com.google.android.gms.common.stats;

import com.google.android.gms.internal.zzrs;

public final class zzc {
    public static zzrs<Integer> Ef = zzrs.zza("gms:common:stats:max_num_of_events", Integer.valueOf(100));
    public static zzrs<Integer> Eg = zzrs.zza("gms:common:stats:max_chunk_size", Integer.valueOf(100));

    public static final class zza {
        public static zzrs<Integer> Eh = zzrs.zza("gms:common:stats:connections:level", Integer.valueOf(zzd.LOG_LEVEL_OFF));
        public static zzrs<String> Ei = zzrs.zzab("gms:common:stats:connections:ignored_calling_processes", "");
        public static zzrs<String> Ej = zzrs.zzab("gms:common:stats:connections:ignored_calling_services", "");
        public static zzrs<String> Ek = zzrs.zzab("gms:common:stats:connections:ignored_target_processes", "");
        public static zzrs<String> El = zzrs.zzab("gms:common:stats:connections:ignored_target_services", "com.google.android.gms.auth.GetToken");
        public static zzrs<Long> Em = zzrs.zza("gms:common:stats:connections:time_out_duration", Long.valueOf(600000));
    }

    public static final class zzb {
        public static zzrs<Integer> Eh = zzrs.zza("gms:common:stats:wakeLocks:level", Integer.valueOf(zzd.LOG_LEVEL_OFF));
        public static zzrs<Long> Em = zzrs.zza("gms:common:stats:wakelocks:time_out_duration", Long.valueOf(600000));
    }
}
