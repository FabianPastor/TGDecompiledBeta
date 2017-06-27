package com.google.android.gms.internal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.measurement.AppMeasurement;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

final class zzcem extends zzchi {
    private static final Map<String, String> zzbpn;
    private static final Map<String, String> zzbpo;
    private static final Map<String, String> zzbpp;
    private static final Map<String, String> zzbpq;
    private static final Map<String, String> zzbpr;
    private final zzcep zzbps = new zzcep(this, getContext(), zzcel.zzxC());
    private final zzcje zzbpt = new zzcje(zzkq());

    static {
        Map arrayMap = new ArrayMap(1);
        zzbpn = arrayMap;
        arrayMap.put("origin", "ALTER TABLE user_attributes ADD COLUMN origin TEXT;");
        arrayMap = new ArrayMap(18);
        zzbpo = arrayMap;
        arrayMap.put("app_version", "ALTER TABLE apps ADD COLUMN app_version TEXT;");
        zzbpo.put("app_store", "ALTER TABLE apps ADD COLUMN app_store TEXT;");
        zzbpo.put("gmp_version", "ALTER TABLE apps ADD COLUMN gmp_version INTEGER;");
        zzbpo.put("dev_cert_hash", "ALTER TABLE apps ADD COLUMN dev_cert_hash INTEGER;");
        zzbpo.put("measurement_enabled", "ALTER TABLE apps ADD COLUMN measurement_enabled INTEGER;");
        zzbpo.put("last_bundle_start_timestamp", "ALTER TABLE apps ADD COLUMN last_bundle_start_timestamp INTEGER;");
        zzbpo.put("day", "ALTER TABLE apps ADD COLUMN day INTEGER;");
        zzbpo.put("daily_public_events_count", "ALTER TABLE apps ADD COLUMN daily_public_events_count INTEGER;");
        zzbpo.put("daily_events_count", "ALTER TABLE apps ADD COLUMN daily_events_count INTEGER;");
        zzbpo.put("daily_conversions_count", "ALTER TABLE apps ADD COLUMN daily_conversions_count INTEGER;");
        zzbpo.put("remote_config", "ALTER TABLE apps ADD COLUMN remote_config BLOB;");
        zzbpo.put("config_fetched_time", "ALTER TABLE apps ADD COLUMN config_fetched_time INTEGER;");
        zzbpo.put("failed_config_fetch_time", "ALTER TABLE apps ADD COLUMN failed_config_fetch_time INTEGER;");
        zzbpo.put("app_version_int", "ALTER TABLE apps ADD COLUMN app_version_int INTEGER;");
        zzbpo.put("firebase_instance_id", "ALTER TABLE apps ADD COLUMN firebase_instance_id TEXT;");
        zzbpo.put("daily_error_events_count", "ALTER TABLE apps ADD COLUMN daily_error_events_count INTEGER;");
        zzbpo.put("daily_realtime_events_count", "ALTER TABLE apps ADD COLUMN daily_realtime_events_count INTEGER;");
        zzbpo.put("health_monitor_sample", "ALTER TABLE apps ADD COLUMN health_monitor_sample TEXT;");
        zzbpo.put("android_id", "ALTER TABLE apps ADD COLUMN android_id INTEGER;");
        arrayMap = new ArrayMap(1);
        zzbpp = arrayMap;
        arrayMap.put("realtime", "ALTER TABLE raw_events ADD COLUMN realtime INTEGER;");
        arrayMap = new ArrayMap(1);
        zzbpq = arrayMap;
        arrayMap.put("has_realtime", "ALTER TABLE queue ADD COLUMN has_realtime INTEGER;");
        arrayMap = new ArrayMap(1);
        zzbpr = arrayMap;
        arrayMap.put("previous_install_count", "ALTER TABLE app2 ADD COLUMN previous_install_count INTEGER;");
    }

    zzcem(zzcgk com_google_android_gms_internal_zzcgk) {
        super(com_google_android_gms_internal_zzcgk);
    }

    @WorkerThread
    private final long zza(String str, String[] strArr, long j) {
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery(str, strArr);
            if (cursor.moveToFirst()) {
                j = cursor.getLong(0);
                if (cursor != null) {
                    cursor.close();
                }
            } else if (cursor != null) {
                cursor.close();
            }
            return j;
        } catch (SQLiteException e) {
            zzwF().zzyx().zze("Database error", str, e);
            throw e;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @WorkerThread
    private final Object zza(Cursor cursor, int i) {
        int type = cursor.getType(i);
        switch (type) {
            case 0:
                zzwF().zzyx().log("Loaded invalid null value from database");
                return null;
            case 1:
                return Long.valueOf(cursor.getLong(i));
            case 2:
                return Double.valueOf(cursor.getDouble(i));
            case 3:
                return cursor.getString(i);
            case 4:
                zzwF().zzyx().log("Loaded invalid blob type value, ignoring it");
                return null;
            default:
                zzwF().zzyx().zzj("Loaded invalid unknown value type, ignoring it", Integer.valueOf(type));
                return null;
        }
    }

    @WorkerThread
    private static void zza(ContentValues contentValues, String str, Object obj) {
        zzbo.zzcF(str);
        zzbo.zzu(obj);
        if (obj instanceof String) {
            contentValues.put(str, (String) obj);
        } else if (obj instanceof Long) {
            contentValues.put(str, (Long) obj);
        } else if (obj instanceof Double) {
            contentValues.put(str, (Double) obj);
        } else {
            throw new IllegalArgumentException("Invalid value type");
        }
    }

    static void zza(zzcfk com_google_android_gms_internal_zzcfk, SQLiteDatabase sQLiteDatabase) {
        if (com_google_android_gms_internal_zzcfk == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        }
        File file = new File(sQLiteDatabase.getPath());
        if (!file.setReadable(false, false)) {
            com_google_android_gms_internal_zzcfk.zzyz().log("Failed to turn off database read permission");
        }
        if (!file.setWritable(false, false)) {
            com_google_android_gms_internal_zzcfk.zzyz().log("Failed to turn off database write permission");
        }
        if (!file.setReadable(true, true)) {
            com_google_android_gms_internal_zzcfk.zzyz().log("Failed to turn on database read permission for owner");
        }
        if (!file.setWritable(true, true)) {
            com_google_android_gms_internal_zzcfk.zzyz().log("Failed to turn on database write permission for owner");
        }
    }

    @WorkerThread
    static void zza(zzcfk com_google_android_gms_internal_zzcfk, SQLiteDatabase sQLiteDatabase, String str, String str2, String str3, Map<String, String> map) throws SQLiteException {
        if (com_google_android_gms_internal_zzcfk == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        }
        if (!zza(com_google_android_gms_internal_zzcfk, sQLiteDatabase, str)) {
            sQLiteDatabase.execSQL(str2);
        }
        try {
            zza(com_google_android_gms_internal_zzcfk, sQLiteDatabase, str, str3, map);
        } catch (SQLiteException e) {
            com_google_android_gms_internal_zzcfk.zzyx().zzj("Failed to verify columns on table that was just created", str);
            throw e;
        }
    }

    @WorkerThread
    private static void zza(zzcfk com_google_android_gms_internal_zzcfk, SQLiteDatabase sQLiteDatabase, String str, String str2, Map<String, String> map) throws SQLiteException {
        if (com_google_android_gms_internal_zzcfk == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        }
        Iterable zzb = zzb(sQLiteDatabase, str);
        String[] split = str2.split(",");
        int length = split.length;
        int i = 0;
        while (i < length) {
            String str3 = split[i];
            if (zzb.remove(str3)) {
                i++;
            } else {
                throw new SQLiteException(new StringBuilder((String.valueOf(str).length() + 35) + String.valueOf(str3).length()).append("Table ").append(str).append(" is missing required column: ").append(str3).toString());
            }
        }
        if (map != null) {
            for (Entry entry : map.entrySet()) {
                if (!zzb.remove(entry.getKey())) {
                    sQLiteDatabase.execSQL((String) entry.getValue());
                }
            }
        }
        if (!zzb.isEmpty()) {
            com_google_android_gms_internal_zzcfk.zzyz().zze("Table has extra columns. table, columns", str, TextUtils.join(", ", zzb));
        }
    }

    @WorkerThread
    private static boolean zza(zzcfk com_google_android_gms_internal_zzcfk, SQLiteDatabase sQLiteDatabase, String str) {
        Object e;
        Throwable th;
        Cursor cursor = null;
        if (com_google_android_gms_internal_zzcfk == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        }
        Cursor query;
        try {
            SQLiteDatabase sQLiteDatabase2 = sQLiteDatabase;
            query = sQLiteDatabase2.query("SQLITE_MASTER", new String[]{"name"}, "name=?", new String[]{str}, null, null, null);
            try {
                boolean moveToFirst = query.moveToFirst();
                if (query == null) {
                    return moveToFirst;
                }
                query.close();
                return moveToFirst;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    com_google_android_gms_internal_zzcfk.zzyz().zze("Error querying for table", str, e);
                    if (query != null) {
                        query.close();
                    }
                    return false;
                } catch (Throwable th2) {
                    th = th2;
                    cursor = query;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            query = null;
            com_google_android_gms_internal_zzcfk.zzyz().zze("Error querying for table", str, e);
            if (query != null) {
                query.close();
            }
            return false;
        } catch (Throwable th3) {
            th = th3;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    @WorkerThread
    private final boolean zza(String str, int i, zzcjm com_google_android_gms_internal_zzcjm) {
        zzkD();
        zzjC();
        zzbo.zzcF(str);
        zzbo.zzu(com_google_android_gms_internal_zzcjm);
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzcjm.zzbuN)) {
            zzwF().zzyz().zzd("Event filter had no event name. Audience definition ignored. appId, audienceId, filterId", zzcfk.zzdZ(str), Integer.valueOf(i), String.valueOf(com_google_android_gms_internal_zzcjm.zzbuM));
            return false;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzcjm.zzLT()];
            acy zzc = acy.zzc(bArr, 0, bArr.length);
            com_google_android_gms_internal_zzcjm.zza(zzc);
            zzc.zzLK();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("filter_id", com_google_android_gms_internal_zzcjm.zzbuM);
            contentValues.put("event_name", com_google_android_gms_internal_zzcjm.zzbuN);
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("event_filters", null, contentValues, 5) == -1) {
                    zzwF().zzyx().zzj("Failed to insert event filter (got -1). appId", zzcfk.zzdZ(str));
                }
                return true;
            } catch (SQLiteException e) {
                zzwF().zzyx().zze("Error storing event filter. appId", zzcfk.zzdZ(str), e);
                return false;
            }
        } catch (IOException e2) {
            zzwF().zzyx().zze("Configuration loss. Failed to serialize event filter. appId", zzcfk.zzdZ(str), e2);
            return false;
        }
    }

    @WorkerThread
    private final boolean zza(String str, int i, zzcjp com_google_android_gms_internal_zzcjp) {
        zzkD();
        zzjC();
        zzbo.zzcF(str);
        zzbo.zzu(com_google_android_gms_internal_zzcjp);
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzcjp.zzbvc)) {
            zzwF().zzyz().zzd("Property filter had no property name. Audience definition ignored. appId, audienceId, filterId", zzcfk.zzdZ(str), Integer.valueOf(i), String.valueOf(com_google_android_gms_internal_zzcjp.zzbuM));
            return false;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzcjp.zzLT()];
            acy zzc = acy.zzc(bArr, 0, bArr.length);
            com_google_android_gms_internal_zzcjp.zza(zzc);
            zzc.zzLK();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("filter_id", com_google_android_gms_internal_zzcjp.zzbuM);
            contentValues.put("property_name", com_google_android_gms_internal_zzcjp.zzbvc);
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("property_filters", null, contentValues, 5) != -1) {
                    return true;
                }
                zzwF().zzyx().zzj("Failed to insert property filter (got -1). appId", zzcfk.zzdZ(str));
                return false;
            } catch (SQLiteException e) {
                zzwF().zzyx().zze("Error storing property filter. appId", zzcfk.zzdZ(str), e);
                return false;
            }
        } catch (IOException e2) {
            zzwF().zzyx().zze("Configuration loss. Failed to serialize property filter. appId", zzcfk.zzdZ(str), e2);
            return false;
        }
    }

    @WorkerThread
    private final long zzb(String str, String[] strArr) {
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery(str, strArr);
            if (cursor.moveToFirst()) {
                long j = cursor.getLong(0);
                if (cursor != null) {
                    cursor.close();
                }
                return j;
            }
            throw new SQLiteException("Database returned empty set");
        } catch (SQLiteException e) {
            zzwF().zzyx().zze("Database error", str, e);
            throw e;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @WorkerThread
    private static Set<String> zzb(SQLiteDatabase sQLiteDatabase, String str) {
        Set<String> hashSet = new HashSet();
        Cursor rawQuery = sQLiteDatabase.rawQuery(new StringBuilder(String.valueOf(str).length() + 22).append("SELECT * FROM ").append(str).append(" LIMIT 0").toString(), null);
        try {
            Collections.addAll(hashSet, rawQuery.getColumnNames());
            return hashSet;
        } finally {
            rawQuery.close();
        }
    }

    private final boolean zzc(String str, List<Integer> list) {
        zzbo.zzcF(str);
        zzkD();
        zzjC();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        try {
            if (zzb("select count(1) from audience_filter_values where app_id=?", new String[]{str}) <= ((long) Math.max(0, Math.min(2000, zzwH().zzb(str, zzcfa.zzbqA))))) {
                return false;
            }
            Iterable arrayList = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                Integer num = (Integer) list.get(i);
                if (num == null || !(num instanceof Integer)) {
                    return false;
                }
                arrayList.add(Integer.toString(num.intValue()));
            }
            String valueOf = String.valueOf(TextUtils.join(",", arrayList));
            valueOf = new StringBuilder(String.valueOf(valueOf).length() + 2).append("(").append(valueOf).append(")").toString();
            return writableDatabase.delete("audience_filter_values", new StringBuilder(String.valueOf(valueOf).length() + 140).append("audience_id in (select audience_id from audience_filter_values where app_id=? and audience_id not in ").append(valueOf).append(" order by rowid desc limit -1 offset ?)").toString(), new String[]{str, Integer.toString(r5)}) > 0;
        } catch (SQLiteException e) {
            zzwF().zzyx().zze("Database error querying filters. appId", zzcfk.zzdZ(str), e);
            return false;
        }
    }

    private final boolean zzyk() {
        return getContext().getDatabasePath(zzcel.zzxC()).exists();
    }

    @WorkerThread
    public final void beginTransaction() {
        zzkD();
        getWritableDatabase().beginTransaction();
    }

    @WorkerThread
    public final void endTransaction() {
        zzkD();
        getWritableDatabase().endTransaction();
    }

    @WorkerThread
    final SQLiteDatabase getWritableDatabase() {
        zzjC();
        try {
            return this.zzbps.getWritableDatabase();
        } catch (SQLiteException e) {
            zzwF().zzyz().zzj("Error opening database", e);
            throw e;
        }
    }

    @WorkerThread
    public final void setTransactionSuccessful() {
        zzkD();
        getWritableDatabase().setTransactionSuccessful();
    }

    @WorkerThread
    public final zzceu zzE(String str, String str2) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzbo.zzcF(str);
        zzbo.zzcF(str2);
        zzjC();
        zzkD();
        try {
            Cursor query = getWritableDatabase().query("events", new String[]{"lifetime_count", "current_bundle_count", "last_fire_timestamp"}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zzceu com_google_android_gms_internal_zzceu = new zzceu(str, str2, query.getLong(0), query.getLong(1), query.getLong(2));
                    if (query.moveToNext()) {
                        zzwF().zzyx().zzj("Got multiple records for event aggregates, expected one. appId", zzcfk.zzdZ(str));
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzceu;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzceu;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
                try {
                    zzwF().zzyx().zzd("Error querying events. appId", zzcfk.zzdZ(str), zzwA().zzdW(str2), e);
                    if (cursor != null) {
                        cursor.close();
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    cursor2 = cursor;
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                cursor2 = query;
                if (cursor2 != null) {
                    cursor2.close();
                }
                throw th;
            }
        } catch (SQLiteException e3) {
            e = e3;
            cursor = null;
            zzwF().zzyx().zzd("Error querying events. appId", zzcfk.zzdZ(str), zzwA().zzdW(str2), e);
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } catch (Throwable th4) {
            th = th4;
            if (cursor2 != null) {
                cursor2.close();
            }
            throw th;
        }
    }

    @WorkerThread
    public final void zzF(String str, String str2) {
        zzbo.zzcF(str);
        zzbo.zzcF(str2);
        zzjC();
        zzkD();
        try {
            zzwF().zzyD().zzj("Deleted user attribute rows", Integer.valueOf(getWritableDatabase().delete("user_attributes", "app_id=? and name=?", new String[]{str, str2})));
        } catch (SQLiteException e) {
            zzwF().zzyx().zzd("Error deleting user attribute. appId", zzcfk.zzdZ(str), zzwA().zzdY(str2), e);
        }
    }

    @WorkerThread
    public final zzcjj zzG(String str, String str2) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzbo.zzcF(str);
        zzbo.zzcF(str2);
        zzjC();
        zzkD();
        try {
            Cursor query = getWritableDatabase().query("user_attributes", new String[]{"set_timestamp", Param.VALUE, "origin"}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    String str3 = str;
                    zzcjj com_google_android_gms_internal_zzcjj = new zzcjj(str3, query.getString(2), str2, query.getLong(0), zza(query, 1));
                    if (query.moveToNext()) {
                        zzwF().zzyx().zzj("Got multiple records for user property, expected one. appId", zzcfk.zzdZ(str));
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzcjj;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzcjj;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
                try {
                    zzwF().zzyx().zzd("Error querying user property. appId", zzcfk.zzdZ(str), zzwA().zzdY(str2), e);
                    if (cursor != null) {
                        cursor.close();
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    cursor2 = cursor;
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                cursor2 = query;
                if (cursor2 != null) {
                    cursor2.close();
                }
                throw th;
            }
        } catch (SQLiteException e3) {
            e = e3;
            cursor = null;
            zzwF().zzyx().zzd("Error querying user property. appId", zzcfk.zzdZ(str), zzwA().zzdY(str2), e);
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } catch (Throwable th4) {
            th = th4;
            if (cursor2 != null) {
                cursor2.close();
            }
            throw th;
        }
    }

    public final void zzG(List<Long> list) {
        zzbo.zzu(list);
        zzjC();
        zzkD();
        StringBuilder stringBuilder = new StringBuilder("rowid in (");
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(((Long) list.get(i)).longValue());
        }
        stringBuilder.append(")");
        int delete = getWritableDatabase().delete("raw_events", stringBuilder.toString(), null);
        if (delete != list.size()) {
            zzwF().zzyx().zze("Deleted fewer rows from raw events table than expected", Integer.valueOf(delete), Integer.valueOf(list.size()));
        }
    }

    @WorkerThread
    public final zzcej zzH(String str, String str2) {
        Object e;
        Cursor cursor;
        Throwable th;
        zzbo.zzcF(str);
        zzbo.zzcF(str2);
        zzjC();
        zzkD();
        Cursor query;
        try {
            query = getWritableDatabase().query("conditional_properties", new String[]{"origin", Param.VALUE, "active", "trigger_event_name", "trigger_timeout", "timed_out_event", "creation_timestamp", "triggered_event", "triggered_timestamp", "time_to_live", "expired_event"}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    String string = query.getString(0);
                    Object zza = zza(query, 1);
                    boolean z = query.getInt(2) != 0;
                    String string2 = query.getString(3);
                    long j = query.getLong(4);
                    zzcey com_google_android_gms_internal_zzcey = (zzcey) zzwB().zzb(query.getBlob(5), zzcey.CREATOR);
                    long j2 = query.getLong(6);
                    zzcey com_google_android_gms_internal_zzcey2 = (zzcey) zzwB().zzb(query.getBlob(7), zzcey.CREATOR);
                    long j3 = query.getLong(8);
                    zzcej com_google_android_gms_internal_zzcej = new zzcej(str, string, new zzcjh(str2, j3, zza, string), j2, z, string2, com_google_android_gms_internal_zzcey, j, com_google_android_gms_internal_zzcey2, query.getLong(9), (zzcey) zzwB().zzb(query.getBlob(10), zzcey.CREATOR));
                    if (query.moveToNext()) {
                        zzwF().zzyx().zze("Got multiple records for conditional property, expected one", zzcfk.zzdZ(str), zzwA().zzdY(str2));
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzcej;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzcej;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
                try {
                    zzwF().zzyx().zzd("Error querying conditional property", zzcfk.zzdZ(str), zzwA().zzdY(str2), e);
                    if (cursor != null) {
                        cursor.close();
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    query = cursor;
                    if (query != null) {
                        query.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (query != null) {
                    query.close();
                }
                throw th;
            }
        } catch (SQLiteException e3) {
            e = e3;
            cursor = null;
            zzwF().zzyx().zzd("Error querying conditional property", zzcfk.zzdZ(str), zzwA().zzdY(str2), e);
            if (cursor != null) {
                cursor.close();
            }
            return null;
        } catch (Throwable th4) {
            th = th4;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    @WorkerThread
    public final int zzI(String str, String str2) {
        int i = 0;
        zzbo.zzcF(str);
        zzbo.zzcF(str2);
        zzjC();
        zzkD();
        try {
            i = getWritableDatabase().delete("conditional_properties", "app_id=? and name=?", new String[]{str, str2});
        } catch (SQLiteException e) {
            zzwF().zzyx().zzd("Error deleting conditional property", zzcfk.zzdZ(str), zzwA().zzdY(str2), e);
        }
        return i;
    }

    final Map<Integer, List<zzcjm>> zzJ(String str, String str2) {
        Cursor query;
        Object e;
        Throwable th;
        zzkD();
        zzjC();
        zzbo.zzcF(str);
        zzbo.zzcF(str2);
        Map<Integer, List<zzcjm>> arrayMap = new ArrayMap();
        try {
            query = getWritableDatabase().query("event_filters", new String[]{"audience_id", "data"}, "app_id=? AND event_name=?", new String[]{str, str2}, null, null, null);
            if (query.moveToFirst()) {
                do {
                    try {
                        byte[] blob = query.getBlob(1);
                        acx zzb = acx.zzb(blob, 0, blob.length);
                        zzcjm com_google_android_gms_internal_zzcjm = new zzcjm();
                        try {
                            com_google_android_gms_internal_zzcjm.zza(zzb);
                            int i = query.getInt(0);
                            List list = (List) arrayMap.get(Integer.valueOf(i));
                            if (list == null) {
                                list = new ArrayList();
                                arrayMap.put(Integer.valueOf(i), list);
                            }
                            list.add(com_google_android_gms_internal_zzcjm);
                        } catch (IOException e2) {
                            zzwF().zzyx().zze("Failed to merge filter. appId", zzcfk.zzdZ(str), e2);
                        }
                    } catch (SQLiteException e3) {
                        e = e3;
                    }
                } while (query.moveToNext());
                if (query != null) {
                    query.close();
                }
                return arrayMap;
            }
            Map<Integer, List<zzcjm>> emptyMap = Collections.emptyMap();
            if (query == null) {
                return emptyMap;
            }
            query.close();
            return emptyMap;
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            try {
                zzwF().zzyx().zze("Database error querying filters. appId", zzcfk.zzdZ(str), e);
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (Throwable th2) {
                th = th2;
                if (query != null) {
                    query.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    final Map<Integer, List<zzcjp>> zzK(String str, String str2) {
        Object e;
        Throwable th;
        zzkD();
        zzjC();
        zzbo.zzcF(str);
        zzbo.zzcF(str2);
        Map<Integer, List<zzcjp>> arrayMap = new ArrayMap();
        Cursor query;
        try {
            query = getWritableDatabase().query("property_filters", new String[]{"audience_id", "data"}, "app_id=? AND property_name=?", new String[]{str, str2}, null, null, null);
            if (query.moveToFirst()) {
                do {
                    try {
                        byte[] blob = query.getBlob(1);
                        acx zzb = acx.zzb(blob, 0, blob.length);
                        zzcjp com_google_android_gms_internal_zzcjp = new zzcjp();
                        try {
                            com_google_android_gms_internal_zzcjp.zza(zzb);
                            int i = query.getInt(0);
                            List list = (List) arrayMap.get(Integer.valueOf(i));
                            if (list == null) {
                                list = new ArrayList();
                                arrayMap.put(Integer.valueOf(i), list);
                            }
                            list.add(com_google_android_gms_internal_zzcjp);
                        } catch (IOException e2) {
                            zzwF().zzyx().zze("Failed to merge filter", zzcfk.zzdZ(str), e2);
                        }
                    } catch (SQLiteException e3) {
                        e = e3;
                    }
                } while (query.moveToNext());
                if (query != null) {
                    query.close();
                }
                return arrayMap;
            }
            Map<Integer, List<zzcjp>> emptyMap = Collections.emptyMap();
            if (query == null) {
                return emptyMap;
            }
            query.close();
            return emptyMap;
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            try {
                zzwF().zzyx().zze("Database error querying filters. appId", zzcfk.zzdZ(str), e);
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (Throwable th2) {
                th = th2;
                if (query != null) {
                    query.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    @WorkerThread
    protected final long zzL(String str, String str2) {
        long zza;
        Object e;
        zzbo.zzcF(str);
        zzbo.zzcF(str2);
        zzjC();
        zzkD();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            zza = zza(new StringBuilder(String.valueOf(str2).length() + 32).append("select ").append(str2).append(" from app2 where app_id=?").toString(), new String[]{str}, -1);
            if (zza == -1) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("app_id", str);
                contentValues.put("first_open_count", Integer.valueOf(0));
                contentValues.put("previous_install_count", Integer.valueOf(0));
                if (writableDatabase.insertWithOnConflict("app2", null, contentValues, 5) == -1) {
                    zzwF().zzyx().zze("Failed to insert column (got -1). appId", zzcfk.zzdZ(str), str2);
                    writableDatabase.endTransaction();
                    return -1;
                }
                zza = 0;
            }
            try {
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("app_id", str);
                contentValues2.put(str2, Long.valueOf(1 + zza));
                if (((long) writableDatabase.update("app2", contentValues2, "app_id = ?", new String[]{str})) == 0) {
                    zzwF().zzyx().zze("Failed to update column (got 0). appId", zzcfk.zzdZ(str), str2);
                    writableDatabase.endTransaction();
                    return -1;
                }
                writableDatabase.setTransactionSuccessful();
                writableDatabase.endTransaction();
                return zza;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzwF().zzyx().zzd("Error inserting column. appId", zzcfk.zzdZ(str), str2, e);
                    return zza;
                } finally {
                    writableDatabase.endTransaction();
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            zza = 0;
            zzwF().zzyx().zzd("Error inserting column. appId", zzcfk.zzdZ(str), str2, e);
            return zza;
        }
    }

    public final long zza(zzcjy com_google_android_gms_internal_zzcjy) throws IOException {
        zzjC();
        zzkD();
        zzbo.zzu(com_google_android_gms_internal_zzcjy);
        zzbo.zzcF(com_google_android_gms_internal_zzcjy.zzaJ);
        try {
            long j;
            Object obj = new byte[com_google_android_gms_internal_zzcjy.zzLT()];
            acy zzc = acy.zzc(obj, 0, obj.length);
            com_google_android_gms_internal_zzcjy.zza(zzc);
            zzc.zzLK();
            zzcjk zzwB = zzwB();
            zzbo.zzu(obj);
            zzwB.zzjC();
            MessageDigest zzbE = zzcjk.zzbE("MD5");
            if (zzbE == null) {
                zzwB.zzwF().zzyx().log("Failed to get MD5");
                j = 0;
            } else {
                j = zzcjk.zzn(zzbE.digest(obj));
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzcjy.zzaJ);
            contentValues.put("metadata_fingerprint", Long.valueOf(j));
            contentValues.put(TtmlNode.TAG_METADATA, obj);
            try {
                getWritableDatabase().insertWithOnConflict("raw_events_metadata", null, contentValues, 4);
                return j;
            } catch (SQLiteException e) {
                zzwF().zzyx().zze("Error storing raw event metadata. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcjy.zzaJ), e);
                throw e;
            }
        } catch (IOException e2) {
            zzwF().zzyx().zze("Data loss. Failed to serialize event metadata. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcjy.zzaJ), e2);
            throw e2;
        }
    }

    @WorkerThread
    public final zzcen zza(long j, String str, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        Cursor query;
        Object e;
        Throwable th;
        zzbo.zzcF(str);
        zzjC();
        zzkD();
        String[] strArr = new String[]{str};
        zzcen com_google_android_gms_internal_zzcen = new zzcen();
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            query = writableDatabase.query("apps", new String[]{"day", "daily_events_count", "daily_public_events_count", "daily_conversions_count", "daily_error_events_count", "daily_realtime_events_count"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    if (query.getLong(0) == j) {
                        com_google_android_gms_internal_zzcen.zzbpv = query.getLong(1);
                        com_google_android_gms_internal_zzcen.zzbpu = query.getLong(2);
                        com_google_android_gms_internal_zzcen.zzbpw = query.getLong(3);
                        com_google_android_gms_internal_zzcen.zzbpx = query.getLong(4);
                        com_google_android_gms_internal_zzcen.zzbpy = query.getLong(5);
                    }
                    if (z) {
                        com_google_android_gms_internal_zzcen.zzbpv++;
                    }
                    if (z2) {
                        com_google_android_gms_internal_zzcen.zzbpu++;
                    }
                    if (z3) {
                        com_google_android_gms_internal_zzcen.zzbpw++;
                    }
                    if (z4) {
                        com_google_android_gms_internal_zzcen.zzbpx++;
                    }
                    if (z5) {
                        com_google_android_gms_internal_zzcen.zzbpy++;
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("day", Long.valueOf(j));
                    contentValues.put("daily_public_events_count", Long.valueOf(com_google_android_gms_internal_zzcen.zzbpu));
                    contentValues.put("daily_events_count", Long.valueOf(com_google_android_gms_internal_zzcen.zzbpv));
                    contentValues.put("daily_conversions_count", Long.valueOf(com_google_android_gms_internal_zzcen.zzbpw));
                    contentValues.put("daily_error_events_count", Long.valueOf(com_google_android_gms_internal_zzcen.zzbpx));
                    contentValues.put("daily_realtime_events_count", Long.valueOf(com_google_android_gms_internal_zzcen.zzbpy));
                    writableDatabase.update("apps", contentValues, "app_id=?", strArr);
                    if (query != null) {
                        query.close();
                    }
                    return com_google_android_gms_internal_zzcen;
                }
                zzwF().zzyz().zzj("Not updating daily counts, app is not known. appId", zzcfk.zzdZ(str));
                if (query != null) {
                    query.close();
                }
                return com_google_android_gms_internal_zzcen;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzwF().zzyx().zze("Error updating daily counts. appId", zzcfk.zzdZ(str), e);
                    if (query != null) {
                        query.close();
                    }
                    return com_google_android_gms_internal_zzcen;
                } catch (Throwable th2) {
                    th = th2;
                    if (query != null) {
                        query.close();
                    }
                    throw th;
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            query = null;
            zzwF().zzyx().zze("Error updating daily counts. appId", zzcfk.zzdZ(str), e);
            if (query != null) {
                query.close();
            }
            return com_google_android_gms_internal_zzcen;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    @WorkerThread
    public final void zza(zzcef com_google_android_gms_internal_zzcef) {
        zzbo.zzu(com_google_android_gms_internal_zzcef);
        zzjC();
        zzkD();
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_internal_zzcef.zzhl());
        contentValues.put("app_instance_id", com_google_android_gms_internal_zzcef.getAppInstanceId());
        contentValues.put("gmp_app_id", com_google_android_gms_internal_zzcef.getGmpAppId());
        contentValues.put("resettable_device_id_hash", com_google_android_gms_internal_zzcef.zzwJ());
        contentValues.put("last_bundle_index", Long.valueOf(com_google_android_gms_internal_zzcef.zzwS()));
        contentValues.put("last_bundle_start_timestamp", Long.valueOf(com_google_android_gms_internal_zzcef.zzwL()));
        contentValues.put("last_bundle_end_timestamp", Long.valueOf(com_google_android_gms_internal_zzcef.zzwM()));
        contentValues.put("app_version", com_google_android_gms_internal_zzcef.zzjH());
        contentValues.put("app_store", com_google_android_gms_internal_zzcef.zzwO());
        contentValues.put("gmp_version", Long.valueOf(com_google_android_gms_internal_zzcef.zzwP()));
        contentValues.put("dev_cert_hash", Long.valueOf(com_google_android_gms_internal_zzcef.zzwQ()));
        contentValues.put("measurement_enabled", Boolean.valueOf(com_google_android_gms_internal_zzcef.zzwR()));
        contentValues.put("day", Long.valueOf(com_google_android_gms_internal_zzcef.zzwW()));
        contentValues.put("daily_public_events_count", Long.valueOf(com_google_android_gms_internal_zzcef.zzwX()));
        contentValues.put("daily_events_count", Long.valueOf(com_google_android_gms_internal_zzcef.zzwY()));
        contentValues.put("daily_conversions_count", Long.valueOf(com_google_android_gms_internal_zzcef.zzwZ()));
        contentValues.put("config_fetched_time", Long.valueOf(com_google_android_gms_internal_zzcef.zzwT()));
        contentValues.put("failed_config_fetch_time", Long.valueOf(com_google_android_gms_internal_zzcef.zzwU()));
        contentValues.put("app_version_int", Long.valueOf(com_google_android_gms_internal_zzcef.zzwN()));
        contentValues.put("firebase_instance_id", com_google_android_gms_internal_zzcef.zzwK());
        contentValues.put("daily_error_events_count", Long.valueOf(com_google_android_gms_internal_zzcef.zzxb()));
        contentValues.put("daily_realtime_events_count", Long.valueOf(com_google_android_gms_internal_zzcef.zzxa()));
        contentValues.put("health_monitor_sample", com_google_android_gms_internal_zzcef.zzxc());
        contentValues.put("android_id", Long.valueOf(com_google_android_gms_internal_zzcef.zzxe()));
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            if (((long) writableDatabase.update("apps", contentValues, "app_id = ?", new String[]{com_google_android_gms_internal_zzcef.zzhl()})) == 0 && writableDatabase.insertWithOnConflict("apps", null, contentValues, 5) == -1) {
                zzwF().zzyx().zzj("Failed to insert/update app (got -1). appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcef.zzhl()));
            }
        } catch (SQLiteException e) {
            zzwF().zzyx().zze("Error storing app. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcef.zzhl()), e);
        }
    }

    @WorkerThread
    public final void zza(zzceu com_google_android_gms_internal_zzceu) {
        zzbo.zzu(com_google_android_gms_internal_zzceu);
        zzjC();
        zzkD();
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_internal_zzceu.mAppId);
        contentValues.put("name", com_google_android_gms_internal_zzceu.mName);
        contentValues.put("lifetime_count", Long.valueOf(com_google_android_gms_internal_zzceu.zzbpG));
        contentValues.put("current_bundle_count", Long.valueOf(com_google_android_gms_internal_zzceu.zzbpH));
        contentValues.put("last_fire_timestamp", Long.valueOf(com_google_android_gms_internal_zzceu.zzbpI));
        try {
            if (getWritableDatabase().insertWithOnConflict("events", null, contentValues, 5) == -1) {
                zzwF().zzyx().zzj("Failed to insert/update event aggregates (got -1). appId", zzcfk.zzdZ(com_google_android_gms_internal_zzceu.mAppId));
            }
        } catch (SQLiteException e) {
            zzwF().zzyx().zze("Error storing event aggregates. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzceu.mAppId), e);
        }
    }

    @WorkerThread
    final void zza(String str, zzcjl[] com_google_android_gms_internal_zzcjlArr) {
        int i = 0;
        zzkD();
        zzjC();
        zzbo.zzcF(str);
        zzbo.zzu(com_google_android_gms_internal_zzcjlArr);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            int i2;
            zzkD();
            zzjC();
            zzbo.zzcF(str);
            SQLiteDatabase writableDatabase2 = getWritableDatabase();
            writableDatabase2.delete("property_filters", "app_id=?", new String[]{str});
            writableDatabase2.delete("event_filters", "app_id=?", new String[]{str});
            for (zzcjl com_google_android_gms_internal_zzcjl : com_google_android_gms_internal_zzcjlArr) {
                zzkD();
                zzjC();
                zzbo.zzcF(str);
                zzbo.zzu(com_google_android_gms_internal_zzcjl);
                zzbo.zzu(com_google_android_gms_internal_zzcjl.zzbuK);
                zzbo.zzu(com_google_android_gms_internal_zzcjl.zzbuJ);
                if (com_google_android_gms_internal_zzcjl.zzbuI == null) {
                    zzwF().zzyz().zzj("Audience with no ID. appId", zzcfk.zzdZ(str));
                } else {
                    int intValue = com_google_android_gms_internal_zzcjl.zzbuI.intValue();
                    for (zzcjm com_google_android_gms_internal_zzcjm : com_google_android_gms_internal_zzcjl.zzbuK) {
                        if (com_google_android_gms_internal_zzcjm.zzbuM == null) {
                            zzwF().zzyz().zze("Event filter with no ID. Audience definition ignored. appId, audienceId", zzcfk.zzdZ(str), com_google_android_gms_internal_zzcjl.zzbuI);
                            break;
                        }
                    }
                    for (zzcjp com_google_android_gms_internal_zzcjp : com_google_android_gms_internal_zzcjl.zzbuJ) {
                        if (com_google_android_gms_internal_zzcjp.zzbuM == null) {
                            zzwF().zzyz().zze("Property filter with no ID. Audience definition ignored. appId, audienceId", zzcfk.zzdZ(str), com_google_android_gms_internal_zzcjl.zzbuI);
                            break;
                        }
                    }
                    for (zzcjm com_google_android_gms_internal_zzcjm2 : com_google_android_gms_internal_zzcjl.zzbuK) {
                        if (!zza(str, intValue, com_google_android_gms_internal_zzcjm2)) {
                            i2 = 0;
                            break;
                        }
                    }
                    i2 = 1;
                    if (i2 != 0) {
                        for (zzcjp com_google_android_gms_internal_zzcjp2 : com_google_android_gms_internal_zzcjl.zzbuJ) {
                            if (!zza(str, intValue, com_google_android_gms_internal_zzcjp2)) {
                                i2 = 0;
                                break;
                            }
                        }
                    }
                    if (i2 == 0) {
                        zzkD();
                        zzjC();
                        zzbo.zzcF(str);
                        SQLiteDatabase writableDatabase3 = getWritableDatabase();
                        writableDatabase3.delete("property_filters", "app_id=? and audience_id=?", new String[]{str, String.valueOf(intValue)});
                        writableDatabase3.delete("event_filters", "app_id=? and audience_id=?", new String[]{str, String.valueOf(intValue)});
                    }
                }
            }
            List arrayList = new ArrayList();
            i2 = com_google_android_gms_internal_zzcjlArr.length;
            while (i < i2) {
                arrayList.add(com_google_android_gms_internal_zzcjlArr[i].zzbuI);
                i++;
            }
            zzc(str, arrayList);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    @WorkerThread
    public final boolean zza(zzcej com_google_android_gms_internal_zzcej) {
        zzbo.zzu(com_google_android_gms_internal_zzcej);
        zzjC();
        zzkD();
        if (zzG(com_google_android_gms_internal_zzcej.packageName, com_google_android_gms_internal_zzcej.zzbpd.name) == null) {
            long zzb = zzb("SELECT COUNT(1) FROM conditional_properties WHERE app_id=?", new String[]{com_google_android_gms_internal_zzcej.packageName});
            zzcel.zzxv();
            if (zzb >= 1000) {
                return false;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_internal_zzcej.packageName);
        contentValues.put("origin", com_google_android_gms_internal_zzcej.zzbpc);
        contentValues.put("name", com_google_android_gms_internal_zzcej.zzbpd.name);
        zza(contentValues, Param.VALUE, com_google_android_gms_internal_zzcej.zzbpd.getValue());
        contentValues.put("active", Boolean.valueOf(com_google_android_gms_internal_zzcej.zzbpf));
        contentValues.put("trigger_event_name", com_google_android_gms_internal_zzcej.zzbpg);
        contentValues.put("trigger_timeout", Long.valueOf(com_google_android_gms_internal_zzcej.zzbpi));
        zzwB();
        contentValues.put("timed_out_event", zzcjk.zza(com_google_android_gms_internal_zzcej.zzbph));
        contentValues.put("creation_timestamp", Long.valueOf(com_google_android_gms_internal_zzcej.zzbpe));
        zzwB();
        contentValues.put("triggered_event", zzcjk.zza(com_google_android_gms_internal_zzcej.zzbpj));
        contentValues.put("triggered_timestamp", Long.valueOf(com_google_android_gms_internal_zzcej.zzbpd.zzbuy));
        contentValues.put("time_to_live", Long.valueOf(com_google_android_gms_internal_zzcej.zzbpk));
        zzwB();
        contentValues.put("expired_event", zzcjk.zza(com_google_android_gms_internal_zzcej.zzbpl));
        try {
            if (getWritableDatabase().insertWithOnConflict("conditional_properties", null, contentValues, 5) == -1) {
                zzwF().zzyx().zzj("Failed to insert/update conditional user property (got -1)", zzcfk.zzdZ(com_google_android_gms_internal_zzcej.packageName));
            }
        } catch (SQLiteException e) {
            zzwF().zzyx().zze("Error storing conditional user property", zzcfk.zzdZ(com_google_android_gms_internal_zzcej.packageName), e);
        }
        return true;
    }

    public final boolean zza(zzcet com_google_android_gms_internal_zzcet, long j, boolean z) {
        zzjC();
        zzkD();
        zzbo.zzu(com_google_android_gms_internal_zzcet);
        zzbo.zzcF(com_google_android_gms_internal_zzcet.mAppId);
        zzcjv com_google_android_gms_internal_zzcjv = new zzcjv();
        com_google_android_gms_internal_zzcjv.zzbvy = Long.valueOf(com_google_android_gms_internal_zzcet.zzbpE);
        com_google_android_gms_internal_zzcjv.zzbvw = new zzcjw[com_google_android_gms_internal_zzcet.zzbpF.size()];
        Iterator it = com_google_android_gms_internal_zzcet.zzbpF.iterator();
        int i = 0;
        while (it.hasNext()) {
            String str = (String) it.next();
            zzcjw com_google_android_gms_internal_zzcjw = new zzcjw();
            int i2 = i + 1;
            com_google_android_gms_internal_zzcjv.zzbvw[i] = com_google_android_gms_internal_zzcjw;
            com_google_android_gms_internal_zzcjw.name = str;
            zzwB().zza(com_google_android_gms_internal_zzcjw, com_google_android_gms_internal_zzcet.zzbpF.get(str));
            i = i2;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzcjv.zzLT()];
            acy zzc = acy.zzc(bArr, 0, bArr.length);
            com_google_android_gms_internal_zzcjv.zza(zzc);
            zzc.zzLK();
            zzwF().zzyD().zze("Saving event, name, data size", zzwA().zzdW(com_google_android_gms_internal_zzcet.mName), Integer.valueOf(bArr.length));
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzcet.mAppId);
            contentValues.put("name", com_google_android_gms_internal_zzcet.mName);
            contentValues.put(AppMeasurement.Param.TIMESTAMP, Long.valueOf(com_google_android_gms_internal_zzcet.zzayS));
            contentValues.put("metadata_fingerprint", Long.valueOf(j));
            contentValues.put("data", bArr);
            contentValues.put("realtime", Integer.valueOf(z ? 1 : 0));
            try {
                if (getWritableDatabase().insert("raw_events", null, contentValues) != -1) {
                    return true;
                }
                zzwF().zzyx().zzj("Failed to insert raw event (got -1). appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcet.mAppId));
                return false;
            } catch (SQLiteException e) {
                zzwF().zzyx().zze("Error storing raw event. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcet.mAppId), e);
                return false;
            }
        } catch (IOException e2) {
            zzwF().zzyx().zze("Data loss. Failed to serialize event params/data. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcet.mAppId), e2);
            return false;
        }
    }

    @WorkerThread
    public final boolean zza(zzcjj com_google_android_gms_internal_zzcjj) {
        zzbo.zzu(com_google_android_gms_internal_zzcjj);
        zzjC();
        zzkD();
        if (zzG(com_google_android_gms_internal_zzcjj.mAppId, com_google_android_gms_internal_zzcjj.mName) == null) {
            long zzb;
            if (zzcjk.zzeo(com_google_android_gms_internal_zzcjj.mName)) {
                zzb = zzb("select count(1) from user_attributes where app_id=? and name not like '!_%' escape '!'", new String[]{com_google_android_gms_internal_zzcjj.mAppId});
                zzcel.zzxs();
                if (zzb >= 25) {
                    return false;
                }
            }
            zzb = zzb("select count(1) from user_attributes where app_id=? and origin=? AND name like '!_%' escape '!'", new String[]{com_google_android_gms_internal_zzcjj.mAppId, com_google_android_gms_internal_zzcjj.mOrigin});
            zzcel.zzxu();
            if (zzb >= 25) {
                return false;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_internal_zzcjj.mAppId);
        contentValues.put("origin", com_google_android_gms_internal_zzcjj.mOrigin);
        contentValues.put("name", com_google_android_gms_internal_zzcjj.mName);
        contentValues.put("set_timestamp", Long.valueOf(com_google_android_gms_internal_zzcjj.zzbuC));
        zza(contentValues, Param.VALUE, com_google_android_gms_internal_zzcjj.mValue);
        try {
            if (getWritableDatabase().insertWithOnConflict("user_attributes", null, contentValues, 5) == -1) {
                zzwF().zzyx().zzj("Failed to insert/update user property (got -1). appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcjj.mAppId));
            }
        } catch (SQLiteException e) {
            zzwF().zzyx().zze("Error storing user property. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcjj.mAppId), e);
        }
        return true;
    }

    @WorkerThread
    public final boolean zza(zzcjy com_google_android_gms_internal_zzcjy, boolean z) {
        zzjC();
        zzkD();
        zzbo.zzu(com_google_android_gms_internal_zzcjy);
        zzbo.zzcF(com_google_android_gms_internal_zzcjy.zzaJ);
        zzbo.zzu(com_google_android_gms_internal_zzcjy.zzbvI);
        zzye();
        long currentTimeMillis = zzkq().currentTimeMillis();
        if (com_google_android_gms_internal_zzcjy.zzbvI.longValue() < currentTimeMillis - zzcel.zzxG() || com_google_android_gms_internal_zzcjy.zzbvI.longValue() > zzcel.zzxG() + currentTimeMillis) {
            zzwF().zzyz().zzd("Storing bundle outside of the max uploading time span. appId, now, timestamp", zzcfk.zzdZ(com_google_android_gms_internal_zzcjy.zzaJ), Long.valueOf(currentTimeMillis), com_google_android_gms_internal_zzcjy.zzbvI);
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzcjy.zzLT()];
            acy zzc = acy.zzc(bArr, 0, bArr.length);
            com_google_android_gms_internal_zzcjy.zza(zzc);
            zzc.zzLK();
            bArr = zzwB().zzl(bArr);
            zzwF().zzyD().zzj("Saving bundle, size", Integer.valueOf(bArr.length));
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzcjy.zzaJ);
            contentValues.put("bundle_end_timestamp", com_google_android_gms_internal_zzcjy.zzbvI);
            contentValues.put("data", bArr);
            contentValues.put("has_realtime", Integer.valueOf(z ? 1 : 0));
            try {
                if (getWritableDatabase().insert("queue", null, contentValues) != -1) {
                    return true;
                }
                zzwF().zzyx().zzj("Failed to insert bundle (got -1). appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcjy.zzaJ));
                return false;
            } catch (SQLiteException e) {
                zzwF().zzyx().zze("Error storing bundle. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcjy.zzaJ), e);
                return false;
            }
        } catch (IOException e2) {
            zzwF().zzyx().zze("Data loss. Failed to serialize bundle. appId", zzcfk.zzdZ(com_google_android_gms_internal_zzcjy.zzaJ), e2);
            return false;
        }
    }

    public final String zzaa(long j) {
        Object e;
        Throwable th;
        String str = null;
        zzjC();
        zzkD();
        Cursor rawQuery;
        try {
            rawQuery = getWritableDatabase().rawQuery("select app_id from apps where app_id in (select distinct app_id from raw_events) and config_fetched_time < ? order by failed_config_fetch_time limit 1;", new String[]{String.valueOf(j)});
            try {
                if (rawQuery.moveToFirst()) {
                    str = rawQuery.getString(0);
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                } else {
                    zzwF().zzyD().log("No expired configs for apps with pending events");
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                }
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzwF().zzyx().zzj("Error selecting expired configs", e);
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                    return str;
                } catch (Throwable th2) {
                    th = th2;
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                    throw th;
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            rawQuery = str;
            zzwF().zzyx().zzj("Error selecting expired configs", e);
            if (rawQuery != null) {
                rawQuery.close();
            }
            return str;
        } catch (Throwable th3) {
            rawQuery = str;
            th = th3;
            if (rawQuery != null) {
                rawQuery.close();
            }
            throw th;
        }
        return str;
    }

    public final List<zzcej> zzc(String str, String[] strArr) {
        Cursor query;
        Object e;
        Cursor cursor;
        Throwable th;
        zzjC();
        zzkD();
        List<zzcej> arrayList = new ArrayList();
        try {
            String[] strArr2 = new String[]{"app_id", "origin", "name", Param.VALUE, "active", "trigger_event_name", "trigger_timeout", "timed_out_event", "creation_timestamp", "triggered_event", "triggered_timestamp", "time_to_live", "expired_event"};
            zzcel.zzxv();
            query = getWritableDatabase().query("conditional_properties", strArr2, str, strArr, null, null, "rowid", "1001");
            try {
                if (query.moveToFirst()) {
                    do {
                        if (arrayList.size() >= zzcel.zzxv()) {
                            zzwF().zzyx().zzj("Read more than the max allowed conditional properties, ignoring extra", Integer.valueOf(zzcel.zzxv()));
                            break;
                        }
                        String string = query.getString(0);
                        String string2 = query.getString(1);
                        String string3 = query.getString(2);
                        Object zza = zza(query, 3);
                        boolean z = query.getInt(4) != 0;
                        String string4 = query.getString(5);
                        long j = query.getLong(6);
                        zzcey com_google_android_gms_internal_zzcey = (zzcey) zzwB().zzb(query.getBlob(7), zzcey.CREATOR);
                        long j2 = query.getLong(8);
                        zzcey com_google_android_gms_internal_zzcey2 = (zzcey) zzwB().zzb(query.getBlob(9), zzcey.CREATOR);
                        long j3 = query.getLong(10);
                        List<zzcej> list = arrayList;
                        list.add(new zzcej(string, string2, new zzcjh(string3, j3, zza, string2), j2, z, string4, com_google_android_gms_internal_zzcey, j, com_google_android_gms_internal_zzcey2, query.getLong(11), (zzcey) zzwB().zzb(query.getBlob(12), zzcey.CREATOR)));
                    } while (query.moveToNext());
                    if (query != null) {
                        query.close();
                    }
                    return arrayList;
                }
                if (query != null) {
                    query.close();
                }
                return arrayList;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (SQLiteException e3) {
            e = e3;
            cursor = null;
            try {
                zzwF().zzyx().zzj("Error querying conditional user property value", e);
                List<zzcej> emptyList = Collections.emptyList();
                if (cursor == null) {
                    return emptyList;
                }
                cursor.close();
                return emptyList;
            } catch (Throwable th3) {
                th = th3;
                query = cursor;
                if (query != null) {
                    query.close();
                }
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    @WorkerThread
    public final List<zzcjj> zzdP(String str) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzbo.zzcF(str);
        zzjC();
        zzkD();
        List<zzcjj> arrayList = new ArrayList();
        try {
            Cursor query = getWritableDatabase().query("user_attributes", new String[]{"name", "origin", "set_timestamp", Param.VALUE}, "app_id=?", new String[]{str}, null, null, "rowid", String.valueOf(zzcel.zzxt()));
            try {
                if (query.moveToFirst()) {
                    do {
                        String string = query.getString(0);
                        String string2 = query.getString(1);
                        if (string2 == null) {
                            string2 = "";
                        }
                        long j = query.getLong(2);
                        Object zza = zza(query, 3);
                        if (zza == null) {
                            zzwF().zzyx().zzj("Read invalid user property value, ignoring it. appId", zzcfk.zzdZ(str));
                        } else {
                            arrayList.add(new zzcjj(str, string2, string, j, zza));
                        }
                    } while (query.moveToNext());
                    if (query != null) {
                        query.close();
                    }
                    return arrayList;
                }
                if (query != null) {
                    query.close();
                }
                return arrayList;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
            } catch (Throwable th2) {
                th = th2;
                cursor2 = query;
            }
        } catch (SQLiteException e3) {
            e = e3;
            cursor = null;
            try {
                zzwF().zzyx().zze("Error querying user properties. appId", zzcfk.zzdZ(str), e);
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            } catch (Throwable th3) {
                th = th3;
                cursor2 = cursor;
                if (cursor2 != null) {
                    cursor2.close();
                }
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            if (cursor2 != null) {
                cursor2.close();
            }
            throw th;
        }
    }

    @WorkerThread
    public final zzcef zzdQ(String str) {
        Object e;
        Throwable th;
        zzbo.zzcF(str);
        zzjC();
        zzkD();
        Cursor query;
        try {
            query = getWritableDatabase().query("apps", new String[]{"app_instance_id", "gmp_app_id", "resettable_device_id_hash", "last_bundle_index", "last_bundle_start_timestamp", "last_bundle_end_timestamp", "app_version", "app_store", "gmp_version", "dev_cert_hash", "measurement_enabled", "day", "daily_public_events_count", "daily_events_count", "daily_conversions_count", "config_fetched_time", "failed_config_fetch_time", "app_version_int", "firebase_instance_id", "daily_error_events_count", "daily_realtime_events_count", "health_monitor_sample", "android_id"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zzcef com_google_android_gms_internal_zzcef = new zzcef(this.zzboe, str);
                    com_google_android_gms_internal_zzcef.zzdG(query.getString(0));
                    com_google_android_gms_internal_zzcef.zzdH(query.getString(1));
                    com_google_android_gms_internal_zzcef.zzdI(query.getString(2));
                    com_google_android_gms_internal_zzcef.zzQ(query.getLong(3));
                    com_google_android_gms_internal_zzcef.zzL(query.getLong(4));
                    com_google_android_gms_internal_zzcef.zzM(query.getLong(5));
                    com_google_android_gms_internal_zzcef.setAppVersion(query.getString(6));
                    com_google_android_gms_internal_zzcef.zzdK(query.getString(7));
                    com_google_android_gms_internal_zzcef.zzO(query.getLong(8));
                    com_google_android_gms_internal_zzcef.zzP(query.getLong(9));
                    com_google_android_gms_internal_zzcef.setMeasurementEnabled((query.isNull(10) ? 1 : query.getInt(10)) != 0);
                    com_google_android_gms_internal_zzcef.zzT(query.getLong(11));
                    com_google_android_gms_internal_zzcef.zzU(query.getLong(12));
                    com_google_android_gms_internal_zzcef.zzV(query.getLong(13));
                    com_google_android_gms_internal_zzcef.zzW(query.getLong(14));
                    com_google_android_gms_internal_zzcef.zzR(query.getLong(15));
                    com_google_android_gms_internal_zzcef.zzS(query.getLong(16));
                    com_google_android_gms_internal_zzcef.zzN(query.isNull(17) ? -2147483648L : (long) query.getInt(17));
                    com_google_android_gms_internal_zzcef.zzdJ(query.getString(18));
                    com_google_android_gms_internal_zzcef.zzY(query.getLong(19));
                    com_google_android_gms_internal_zzcef.zzX(query.getLong(20));
                    com_google_android_gms_internal_zzcef.zzdL(query.getString(21));
                    com_google_android_gms_internal_zzcef.zzZ(query.isNull(22) ? 0 : query.getLong(22));
                    com_google_android_gms_internal_zzcef.zzwI();
                    if (query.moveToNext()) {
                        zzwF().zzyx().zzj("Got multiple records for app, expected one. appId", zzcfk.zzdZ(str));
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzcef;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzcef;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzwF().zzyx().zze("Error querying app. appId", zzcfk.zzdZ(str), e);
                    if (query != null) {
                        query.close();
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (query != null) {
                        query.close();
                    }
                    throw th;
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            query = null;
            zzwF().zzyx().zze("Error querying app. appId", zzcfk.zzdZ(str), e);
            if (query != null) {
                query.close();
            }
            return null;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public final long zzdR(String str) {
        zzbo.zzcF(str);
        zzjC();
        zzkD();
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            String valueOf = String.valueOf(Math.max(0, Math.min(1000000, zzwH().zzb(str, zzcfa.zzbqk))));
            return (long) writableDatabase.delete("raw_events", "rowid in (select rowid from raw_events where app_id=? order by rowid desc limit -1 offset ?)", new String[]{str, valueOf});
        } catch (SQLiteException e) {
            zzwF().zzyx().zze("Error deleting over the limit events. appId", zzcfk.zzdZ(str), e);
            return 0;
        }
    }

    @WorkerThread
    public final byte[] zzdS(String str) {
        Cursor query;
        Object e;
        Throwable th;
        zzbo.zzcF(str);
        zzjC();
        zzkD();
        try {
            query = getWritableDatabase().query("apps", new String[]{"remote_config"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    byte[] blob = query.getBlob(0);
                    if (query.moveToNext()) {
                        zzwF().zzyx().zzj("Got multiple records for app config, expected one. appId", zzcfk.zzdZ(str));
                    }
                    if (query == null) {
                        return blob;
                    }
                    query.close();
                    return blob;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzwF().zzyx().zze("Error querying remote config. appId", zzcfk.zzdZ(str), e);
                    if (query != null) {
                        query.close();
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (query != null) {
                        query.close();
                    }
                    throw th;
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            query = null;
            zzwF().zzyx().zze("Error querying remote config. appId", zzcfk.zzdZ(str), e);
            if (query != null) {
                query.close();
            }
            return null;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    final Map<Integer, zzcjz> zzdT(String str) {
        Object e;
        Throwable th;
        zzkD();
        zzjC();
        zzbo.zzcF(str);
        Cursor query;
        try {
            query = getWritableDatabase().query("audience_filter_values", new String[]{"audience_id", "current_results"}, "app_id=?", new String[]{str}, null, null, null);
            if (query.moveToFirst()) {
                Map<Integer, zzcjz> arrayMap = new ArrayMap();
                do {
                    int i = query.getInt(0);
                    byte[] blob = query.getBlob(1);
                    acx zzb = acx.zzb(blob, 0, blob.length);
                    zzcjz com_google_android_gms_internal_zzcjz = new zzcjz();
                    try {
                        com_google_android_gms_internal_zzcjz.zza(zzb);
                    } catch (IOException e2) {
                        zzwF().zzyx().zzd("Failed to merge filter results. appId, audienceId, error", zzcfk.zzdZ(str), Integer.valueOf(i), e2);
                    }
                    try {
                        arrayMap.put(Integer.valueOf(i), com_google_android_gms_internal_zzcjz);
                    } catch (SQLiteException e3) {
                        e = e3;
                    }
                } while (query.moveToNext());
                if (query == null) {
                    return arrayMap;
                }
                query.close();
                return arrayMap;
            }
            if (query != null) {
                query.close();
            }
            return null;
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            try {
                zzwF().zzyx().zze("Database error querying filter results. appId", zzcfk.zzdZ(str), e);
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (Throwable th2) {
                th = th2;
                if (query != null) {
                    query.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public final long zzdU(String str) {
        zzbo.zzcF(str);
        return zza("select count(1) from events where app_id=? and name not like '!_%' escape '!'", new String[]{str}, 0);
    }

    @WorkerThread
    public final List<zzcjj> zzh(String str, String str2, String str3) {
        Object obj;
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzbo.zzcF(str);
        zzjC();
        zzkD();
        List<zzcjj> arrayList = new ArrayList();
        try {
            List arrayList2 = new ArrayList(3);
            arrayList2.add(str);
            StringBuilder stringBuilder = new StringBuilder("app_id=?");
            if (!TextUtils.isEmpty(str2)) {
                arrayList2.add(str2);
                stringBuilder.append(" and origin=?");
            }
            if (!TextUtils.isEmpty(str3)) {
                arrayList2.add(String.valueOf(str3).concat("*"));
                stringBuilder.append(" and name glob ?");
            }
            String[] strArr = (String[]) arrayList2.toArray(new String[arrayList2.size()]);
            String[] strArr2 = new String[]{"name", "set_timestamp", Param.VALUE, "origin"};
            zzcel.zzxt();
            Cursor query = getWritableDatabase().query("user_attributes", strArr2, stringBuilder.toString(), strArr, null, null, "rowid", "1001");
            try {
                if (query.moveToFirst()) {
                    while (arrayList.size() < zzcel.zzxt()) {
                        String string;
                        try {
                            String string2 = query.getString(0);
                            long j = query.getLong(1);
                            Object zza = zza(query, 2);
                            string = query.getString(3);
                            if (zza == null) {
                                zzwF().zzyx().zzd("(2)Read invalid user property value, ignoring it", zzcfk.zzdZ(str), string, str3);
                            } else {
                                arrayList.add(new zzcjj(str, string, string2, j, zza));
                            }
                            if (!query.moveToNext()) {
                                break;
                            }
                            obj = string;
                        } catch (SQLiteException e2) {
                            e = e2;
                            cursor = query;
                            obj = string;
                        } catch (Throwable th2) {
                            th = th2;
                            cursor2 = query;
                        }
                    }
                    zzwF().zzyx().zzj("Read more than the max allowed user properties, ignoring excess", Integer.valueOf(zzcel.zzxt()));
                    if (query != null) {
                        query.close();
                    }
                    return arrayList;
                }
                if (query != null) {
                    query.close();
                }
                return arrayList;
            } catch (SQLiteException e3) {
                e = e3;
                cursor = query;
            } catch (Throwable th22) {
                th = th22;
                cursor2 = query;
            }
        } catch (SQLiteException e4) {
            e = e4;
            cursor = null;
            try {
                zzwF().zzyx().zzd("(2)Error querying user properties", zzcfk.zzdZ(str), obj, e);
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            } catch (Throwable th3) {
                th = th3;
                cursor2 = cursor;
                if (cursor2 != null) {
                    cursor2.close();
                }
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            if (cursor2 != null) {
                cursor2.close();
            }
            throw th;
        }
    }

    @WorkerThread
    public final List<zzcej> zzi(String str, String str2, String str3) {
        zzbo.zzcF(str);
        zzjC();
        zzkD();
        List arrayList = new ArrayList(3);
        arrayList.add(str);
        StringBuilder stringBuilder = new StringBuilder("app_id=?");
        if (!TextUtils.isEmpty(str2)) {
            arrayList.add(str2);
            stringBuilder.append(" and origin=?");
        }
        if (!TextUtils.isEmpty(str3)) {
            arrayList.add(String.valueOf(str3).concat("*"));
            stringBuilder.append(" and name glob ?");
        }
        return zzc(stringBuilder.toString(), (String[]) arrayList.toArray(new String[arrayList.size()]));
    }

    protected final void zzjD() {
    }

    @WorkerThread
    public final List<Pair<zzcjy, Long>> zzl(String str, int i, int i2) {
        List<Pair<zzcjy, Long>> arrayList;
        Object e;
        Cursor cursor;
        Throwable th;
        boolean z = true;
        zzjC();
        zzkD();
        zzbo.zzaf(i > 0);
        if (i2 <= 0) {
            z = false;
        }
        zzbo.zzaf(z);
        zzbo.zzcF(str);
        Cursor query;
        try {
            query = getWritableDatabase().query("queue", new String[]{"rowid", "data"}, "app_id=?", new String[]{str}, null, null, "rowid", String.valueOf(i));
            try {
                if (query.moveToFirst()) {
                    arrayList = new ArrayList();
                    int i3 = 0;
                    while (true) {
                        long j = query.getLong(0);
                        int length;
                        try {
                            byte[] zzm = zzwB().zzm(query.getBlob(1));
                            if (!arrayList.isEmpty() && zzm.length + i3 > i2) {
                                break;
                            }
                            acx zzb = acx.zzb(zzm, 0, zzm.length);
                            zzcjy com_google_android_gms_internal_zzcjy = new zzcjy();
                            try {
                                com_google_android_gms_internal_zzcjy.zza(zzb);
                                length = zzm.length + i3;
                                arrayList.add(Pair.create(com_google_android_gms_internal_zzcjy, Long.valueOf(j)));
                            } catch (IOException e2) {
                                zzwF().zzyx().zze("Failed to merge queued bundle. appId", zzcfk.zzdZ(str), e2);
                                length = i3;
                            }
                            if (!query.moveToNext() || length > i2) {
                                break;
                            }
                            i3 = length;
                        } catch (IOException e22) {
                            zzwF().zzyx().zze("Failed to unzip queued bundle. appId", zzcfk.zzdZ(str), e22);
                            length = i3;
                        }
                    }
                    if (query != null) {
                        query.close();
                    }
                } else {
                    arrayList = Collections.emptyList();
                    if (query != null) {
                        query.close();
                    }
                }
            } catch (SQLiteException e3) {
                e = e3;
                cursor = query;
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (SQLiteException e4) {
            e = e4;
            cursor = null;
            try {
                zzwF().zzyx().zze("Error querying bundles. appId", zzcfk.zzdZ(str), e);
                arrayList = Collections.emptyList();
                if (cursor != null) {
                    cursor.close();
                }
                return arrayList;
            } catch (Throwable th3) {
                th = th3;
                query = cursor;
                if (query != null) {
                    query.close();
                }
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
        return arrayList;
    }

    @WorkerThread
    public final String zzyc() {
        Cursor rawQuery;
        Object e;
        Throwable th;
        String str = null;
        try {
            rawQuery = getWritableDatabase().rawQuery("select app_id from queue order by has_realtime desc, rowid asc limit 1;", null);
            try {
                if (rawQuery.moveToFirst()) {
                    str = rawQuery.getString(0);
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                } else if (rawQuery != null) {
                    rawQuery.close();
                }
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzwF().zzyx().zzj("Database error getting next bundle app id", e);
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                    return str;
                } catch (Throwable th2) {
                    th = th2;
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                    throw th;
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            rawQuery = null;
            zzwF().zzyx().zzj("Database error getting next bundle app id", e);
            if (rawQuery != null) {
                rawQuery.close();
            }
            return str;
        } catch (Throwable th3) {
            rawQuery = null;
            th = th3;
            if (rawQuery != null) {
                rawQuery.close();
            }
            throw th;
        }
        return str;
    }

    public final boolean zzyd() {
        return zzb("select count(1) > 0 from queue where has_realtime = 1", null) != 0;
    }

    @WorkerThread
    final void zzye() {
        zzjC();
        zzkD();
        if (zzyk()) {
            long j = zzwG().zzbrn.get();
            long elapsedRealtime = zzkq().elapsedRealtime();
            if (Math.abs(elapsedRealtime - j) > zzcel.zzxH()) {
                zzwG().zzbrn.set(elapsedRealtime);
                zzjC();
                zzkD();
                if (zzyk()) {
                    int delete = getWritableDatabase().delete("queue", "abs(bundle_end_timestamp - ?) > cast(? as integer)", new String[]{String.valueOf(zzkq().currentTimeMillis()), String.valueOf(zzcel.zzxG())});
                    if (delete > 0) {
                        zzwF().zzyD().zzj("Deleted stale rows. rowsDeleted", Integer.valueOf(delete));
                    }
                }
            }
        }
    }

    @WorkerThread
    public final long zzyf() {
        return zza("select max(bundle_end_timestamp) from queue", null, 0);
    }

    @WorkerThread
    public final long zzyg() {
        return zza("select max(timestamp) from raw_events", null, 0);
    }

    public final boolean zzyh() {
        return zzb("select count(1) > 0 from raw_events", null) != 0;
    }

    public final boolean zzyi() {
        return zzb("select count(1) > 0 from raw_events where realtime = 1", null) != 0;
    }

    public final long zzyj() {
        long j = -1;
        Cursor cursor = null;
        try {
            cursor = getWritableDatabase().rawQuery("select rowid from raw_events order by rowid desc limit 1;", null);
            if (cursor.moveToFirst()) {
                j = cursor.getLong(0);
                if (cursor != null) {
                    cursor.close();
                }
            } else if (cursor != null) {
                cursor.close();
            }
        } catch (SQLiteException e) {
            zzwF().zzyx().zzj("Error querying raw events", e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return j;
    }
}
