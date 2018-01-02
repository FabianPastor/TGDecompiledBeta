package com.google.android.gms.internal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.common.internal.zzbq;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class zzcgo extends zzcjl {
    private static final String[] zziyp = new String[]{"last_bundled_timestamp", "ALTER TABLE events ADD COLUMN last_bundled_timestamp INTEGER;", "last_sampled_complex_event_id", "ALTER TABLE events ADD COLUMN last_sampled_complex_event_id INTEGER;", "last_sampling_rate", "ALTER TABLE events ADD COLUMN last_sampling_rate INTEGER;", "last_exempt_from_sampling", "ALTER TABLE events ADD COLUMN last_exempt_from_sampling INTEGER;"};
    private static final String[] zziyq = new String[]{TtmlNode.ATTR_TTS_ORIGIN, "ALTER TABLE user_attributes ADD COLUMN origin TEXT;"};
    private static final String[] zziyr = new String[]{"app_version", "ALTER TABLE apps ADD COLUMN app_version TEXT;", "app_store", "ALTER TABLE apps ADD COLUMN app_store TEXT;", "gmp_version", "ALTER TABLE apps ADD COLUMN gmp_version INTEGER;", "dev_cert_hash", "ALTER TABLE apps ADD COLUMN dev_cert_hash INTEGER;", "measurement_enabled", "ALTER TABLE apps ADD COLUMN measurement_enabled INTEGER;", "last_bundle_start_timestamp", "ALTER TABLE apps ADD COLUMN last_bundle_start_timestamp INTEGER;", "day", "ALTER TABLE apps ADD COLUMN day INTEGER;", "daily_public_events_count", "ALTER TABLE apps ADD COLUMN daily_public_events_count INTEGER;", "daily_events_count", "ALTER TABLE apps ADD COLUMN daily_events_count INTEGER;", "daily_conversions_count", "ALTER TABLE apps ADD COLUMN daily_conversions_count INTEGER;", "remote_config", "ALTER TABLE apps ADD COLUMN remote_config BLOB;", "config_fetched_time", "ALTER TABLE apps ADD COLUMN config_fetched_time INTEGER;", "failed_config_fetch_time", "ALTER TABLE apps ADD COLUMN failed_config_fetch_time INTEGER;", "app_version_int", "ALTER TABLE apps ADD COLUMN app_version_int INTEGER;", "firebase_instance_id", "ALTER TABLE apps ADD COLUMN firebase_instance_id TEXT;", "daily_error_events_count", "ALTER TABLE apps ADD COLUMN daily_error_events_count INTEGER;", "daily_realtime_events_count", "ALTER TABLE apps ADD COLUMN daily_realtime_events_count INTEGER;", "health_monitor_sample", "ALTER TABLE apps ADD COLUMN health_monitor_sample TEXT;", "android_id", "ALTER TABLE apps ADD COLUMN android_id INTEGER;", "adid_reporting_enabled", "ALTER TABLE apps ADD COLUMN adid_reporting_enabled INTEGER;"};
    private static final String[] zziys = new String[]{"realtime", "ALTER TABLE raw_events ADD COLUMN realtime INTEGER;"};
    private static final String[] zziyt = new String[]{"has_realtime", "ALTER TABLE queue ADD COLUMN has_realtime INTEGER;"};
    private static final String[] zziyu = new String[]{"previous_install_count", "ALTER TABLE app2 ADD COLUMN previous_install_count INTEGER;"};
    private final zzcgr zziyv = new zzcgr(this, getContext(), "google_app_measurement.db");
    private final zzclk zziyw = new zzclk(zzws());

    zzcgo(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
    }

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
            zzawy().zzazd().zze("Database error", str, e);
            throw e;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private final Object zza(Cursor cursor, int i) {
        int type = cursor.getType(i);
        switch (type) {
            case 0:
                zzawy().zzazd().log("Loaded invalid null value from database");
                return null;
            case 1:
                return Long.valueOf(cursor.getLong(i));
            case 2:
                return Double.valueOf(cursor.getDouble(i));
            case 3:
                return cursor.getString(i);
            case 4:
                zzawy().zzazd().log("Loaded invalid blob type value, ignoring it");
                return null;
            default:
                zzawy().zzazd().zzj("Loaded invalid unknown value type, ignoring it", Integer.valueOf(type));
                return null;
        }
    }

    private static void zza(ContentValues contentValues, String str, Object obj) {
        zzbq.zzgm(str);
        zzbq.checkNotNull(obj);
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

    static void zza(zzchm com_google_android_gms_internal_zzchm, SQLiteDatabase sQLiteDatabase) {
        if (com_google_android_gms_internal_zzchm == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        }
        File file = new File(sQLiteDatabase.getPath());
        if (!file.setReadable(false, false)) {
            com_google_android_gms_internal_zzchm.zzazf().log("Failed to turn off database read permission");
        }
        if (!file.setWritable(false, false)) {
            com_google_android_gms_internal_zzchm.zzazf().log("Failed to turn off database write permission");
        }
        if (!file.setReadable(true, true)) {
            com_google_android_gms_internal_zzchm.zzazf().log("Failed to turn on database read permission for owner");
        }
        if (!file.setWritable(true, true)) {
            com_google_android_gms_internal_zzchm.zzazf().log("Failed to turn on database write permission for owner");
        }
    }

    static void zza(zzchm com_google_android_gms_internal_zzchm, SQLiteDatabase sQLiteDatabase, String str, String str2, String str3, String[] strArr) throws SQLiteException {
        if (com_google_android_gms_internal_zzchm == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        }
        if (!zza(com_google_android_gms_internal_zzchm, sQLiteDatabase, str)) {
            sQLiteDatabase.execSQL(str2);
        }
        try {
            zza(com_google_android_gms_internal_zzchm, sQLiteDatabase, str, str3, strArr);
        } catch (SQLiteException e) {
            com_google_android_gms_internal_zzchm.zzazd().zzj("Failed to verify columns on table that was just created", str);
            throw e;
        }
    }

    private static void zza(zzchm com_google_android_gms_internal_zzchm, SQLiteDatabase sQLiteDatabase, String str, String str2, String[] strArr) throws SQLiteException {
        int i = 0;
        if (com_google_android_gms_internal_zzchm == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        }
        Iterable zzb = zzb(sQLiteDatabase, str);
        String[] split = str2.split(",");
        int length = split.length;
        int i2 = 0;
        while (i2 < length) {
            String str3 = split[i2];
            if (zzb.remove(str3)) {
                i2++;
            } else {
                throw new SQLiteException(new StringBuilder((String.valueOf(str).length() + 35) + String.valueOf(str3).length()).append("Table ").append(str).append(" is missing required column: ").append(str3).toString());
            }
        }
        if (strArr != null) {
            while (i < strArr.length) {
                if (!zzb.remove(strArr[i])) {
                    sQLiteDatabase.execSQL(strArr[i + 1]);
                }
                i += 2;
            }
        }
        if (!zzb.isEmpty()) {
            com_google_android_gms_internal_zzchm.zzazf().zze("Table has extra columns. table, columns", str, TextUtils.join(", ", zzb));
        }
    }

    private static boolean zza(zzchm com_google_android_gms_internal_zzchm, SQLiteDatabase sQLiteDatabase, String str) {
        Object e;
        Throwable th;
        Cursor cursor = null;
        if (com_google_android_gms_internal_zzchm == null) {
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
                    com_google_android_gms_internal_zzchm.zzazf().zze("Error querying for table", str, e);
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
            com_google_android_gms_internal_zzchm.zzazf().zze("Error querying for table", str, e);
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

    private final boolean zza(String str, int i, zzcls com_google_android_gms_internal_zzcls) {
        zzxf();
        zzve();
        zzbq.zzgm(str);
        zzbq.checkNotNull(com_google_android_gms_internal_zzcls);
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzcls.zzjjx)) {
            zzawy().zzazf().zzd("Event filter had no event name. Audience definition ignored. appId, audienceId, filterId", zzchm.zzjk(str), Integer.valueOf(i), String.valueOf(com_google_android_gms_internal_zzcls.zzjjw));
            return false;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzcls.zzho()];
            zzfjk zzo = zzfjk.zzo(bArr, 0, bArr.length);
            com_google_android_gms_internal_zzcls.zza(zzo);
            zzo.zzcwt();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("filter_id", com_google_android_gms_internal_zzcls.zzjjw);
            contentValues.put("event_name", com_google_android_gms_internal_zzcls.zzjjx);
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("event_filters", null, contentValues, 5) == -1) {
                    zzawy().zzazd().zzj("Failed to insert event filter (got -1). appId", zzchm.zzjk(str));
                }
                return true;
            } catch (SQLiteException e) {
                zzawy().zzazd().zze("Error storing event filter. appId", zzchm.zzjk(str), e);
                return false;
            }
        } catch (IOException e2) {
            zzawy().zzazd().zze("Configuration loss. Failed to serialize event filter. appId", zzchm.zzjk(str), e2);
            return false;
        }
    }

    private final boolean zza(String str, int i, zzclv com_google_android_gms_internal_zzclv) {
        zzxf();
        zzve();
        zzbq.zzgm(str);
        zzbq.checkNotNull(com_google_android_gms_internal_zzclv);
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzclv.zzjkm)) {
            zzawy().zzazf().zzd("Property filter had no property name. Audience definition ignored. appId, audienceId, filterId", zzchm.zzjk(str), Integer.valueOf(i), String.valueOf(com_google_android_gms_internal_zzclv.zzjjw));
            return false;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzclv.zzho()];
            zzfjk zzo = zzfjk.zzo(bArr, 0, bArr.length);
            com_google_android_gms_internal_zzclv.zza(zzo);
            zzo.zzcwt();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("filter_id", com_google_android_gms_internal_zzclv.zzjjw);
            contentValues.put("property_name", com_google_android_gms_internal_zzclv.zzjkm);
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("property_filters", null, contentValues, 5) != -1) {
                    return true;
                }
                zzawy().zzazd().zzj("Failed to insert property filter (got -1). appId", zzchm.zzjk(str));
                return false;
            } catch (SQLiteException e) {
                zzawy().zzazd().zze("Error storing property filter. appId", zzchm.zzjk(str), e);
                return false;
            }
        } catch (IOException e2) {
            zzawy().zzazd().zze("Configuration loss. Failed to serialize property filter. appId", zzchm.zzjk(str), e2);
            return false;
        }
    }

    private final boolean zzayn() {
        return getContext().getDatabasePath("google_app_measurement.db").exists();
    }

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
            zzawy().zzazd().zze("Database error", str, e);
            throw e;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

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

    private final boolean zze(String str, List<Integer> list) {
        zzbq.zzgm(str);
        zzxf();
        zzve();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        try {
            if (zzb("select count(1) from audience_filter_values where app_id=?", new String[]{str}) <= ((long) Math.max(0, Math.min(2000, zzaxa().zzb(str, zzchc.zzjbi))))) {
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
            String join = TextUtils.join(",", arrayList);
            join = new StringBuilder(String.valueOf(join).length() + 2).append("(").append(join).append(")").toString();
            return writableDatabase.delete("audience_filter_values", new StringBuilder(String.valueOf(join).length() + 140).append("audience_id in (select audience_id from audience_filter_values where app_id=? and audience_id not in ").append(join).append(" order by rowid desc limit -1 offset ?)").toString(), new String[]{str, Integer.toString(r5)}) > 0;
        } catch (SQLiteException e) {
            zzawy().zzazd().zze("Database error querying filters. appId", zzchm.zzjk(str), e);
            return false;
        }
    }

    public final void beginTransaction() {
        zzxf();
        getWritableDatabase().beginTransaction();
    }

    public final void endTransaction() {
        zzxf();
        getWritableDatabase().endTransaction();
    }

    final SQLiteDatabase getWritableDatabase() {
        zzve();
        try {
            return this.zziyv.getWritableDatabase();
        } catch (SQLiteException e) {
            zzawy().zzazf().zzj("Error opening database", e);
            throw e;
        }
    }

    public final void setTransactionSuccessful() {
        zzxf();
        getWritableDatabase().setTransactionSuccessful();
    }

    public final long zza(zzcme com_google_android_gms_internal_zzcme) throws IOException {
        zzve();
        zzxf();
        zzbq.checkNotNull(com_google_android_gms_internal_zzcme);
        zzbq.zzgm(com_google_android_gms_internal_zzcme.zzcn);
        try {
            long j;
            Object obj = new byte[com_google_android_gms_internal_zzcme.zzho()];
            zzfjk zzo = zzfjk.zzo(obj, 0, obj.length);
            com_google_android_gms_internal_zzcme.zza(zzo);
            zzo.zzcwt();
            zzcjk zzawu = zzawu();
            zzbq.checkNotNull(obj);
            zzawu.zzve();
            MessageDigest zzek = zzclq.zzek("MD5");
            if (zzek == null) {
                zzawu.zzawy().zzazd().log("Failed to get MD5");
                j = 0;
            } else {
                j = zzclq.zzs(zzek.digest(obj));
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzcme.zzcn);
            contentValues.put("metadata_fingerprint", Long.valueOf(j));
            contentValues.put(TtmlNode.TAG_METADATA, obj);
            try {
                getWritableDatabase().insertWithOnConflict("raw_events_metadata", null, contentValues, 4);
                return j;
            } catch (SQLiteException e) {
                zzawy().zzazd().zze("Error storing raw event metadata. appId", zzchm.zzjk(com_google_android_gms_internal_zzcme.zzcn), e);
                throw e;
            }
        } catch (IOException e2) {
            zzawy().zzazd().zze("Data loss. Failed to serialize event metadata. appId", zzchm.zzjk(com_google_android_gms_internal_zzcme.zzcn), e2);
            throw e2;
        }
    }

    public final zzcgp zza(long j, String str, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        Object e;
        Throwable th;
        zzbq.zzgm(str);
        zzve();
        zzxf();
        String[] strArr = new String[]{str};
        zzcgp com_google_android_gms_internal_zzcgp = new zzcgp();
        Cursor query;
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            query = writableDatabase.query("apps", new String[]{"day", "daily_events_count", "daily_public_events_count", "daily_conversions_count", "daily_error_events_count", "daily_realtime_events_count"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    if (query.getLong(0) == j) {
                        com_google_android_gms_internal_zzcgp.zziyy = query.getLong(1);
                        com_google_android_gms_internal_zzcgp.zziyx = query.getLong(2);
                        com_google_android_gms_internal_zzcgp.zziyz = query.getLong(3);
                        com_google_android_gms_internal_zzcgp.zziza = query.getLong(4);
                        com_google_android_gms_internal_zzcgp.zzizb = query.getLong(5);
                    }
                    if (z) {
                        com_google_android_gms_internal_zzcgp.zziyy++;
                    }
                    if (z2) {
                        com_google_android_gms_internal_zzcgp.zziyx++;
                    }
                    if (z3) {
                        com_google_android_gms_internal_zzcgp.zziyz++;
                    }
                    if (z4) {
                        com_google_android_gms_internal_zzcgp.zziza++;
                    }
                    if (z5) {
                        com_google_android_gms_internal_zzcgp.zzizb++;
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("day", Long.valueOf(j));
                    contentValues.put("daily_public_events_count", Long.valueOf(com_google_android_gms_internal_zzcgp.zziyx));
                    contentValues.put("daily_events_count", Long.valueOf(com_google_android_gms_internal_zzcgp.zziyy));
                    contentValues.put("daily_conversions_count", Long.valueOf(com_google_android_gms_internal_zzcgp.zziyz));
                    contentValues.put("daily_error_events_count", Long.valueOf(com_google_android_gms_internal_zzcgp.zziza));
                    contentValues.put("daily_realtime_events_count", Long.valueOf(com_google_android_gms_internal_zzcgp.zzizb));
                    writableDatabase.update("apps", contentValues, "app_id=?", strArr);
                    if (query != null) {
                        query.close();
                    }
                    return com_google_android_gms_internal_zzcgp;
                }
                zzawy().zzazf().zzj("Not updating daily counts, app is not known. appId", zzchm.zzjk(str));
                if (query != null) {
                    query.close();
                }
                return com_google_android_gms_internal_zzcgp;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzawy().zzazd().zze("Error updating daily counts. appId", zzchm.zzjk(str), e);
                    if (query != null) {
                        query.close();
                    }
                    return com_google_android_gms_internal_zzcgp;
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
            zzawy().zzazd().zze("Error updating daily counts. appId", zzchm.zzjk(str), e);
            if (query != null) {
                query.close();
            }
            return com_google_android_gms_internal_zzcgp;
        } catch (Throwable th3) {
            th = th3;
            query = null;
            if (query != null) {
                query.close();
            }
            throw th;
        }
    }

    public final void zza(zzcgh com_google_android_gms_internal_zzcgh) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcgh);
        zzve();
        zzxf();
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_internal_zzcgh.getAppId());
        contentValues.put("app_instance_id", com_google_android_gms_internal_zzcgh.getAppInstanceId());
        contentValues.put("gmp_app_id", com_google_android_gms_internal_zzcgh.getGmpAppId());
        contentValues.put("resettable_device_id_hash", com_google_android_gms_internal_zzcgh.zzaxc());
        contentValues.put("last_bundle_index", Long.valueOf(com_google_android_gms_internal_zzcgh.zzaxl()));
        contentValues.put("last_bundle_start_timestamp", Long.valueOf(com_google_android_gms_internal_zzcgh.zzaxe()));
        contentValues.put("last_bundle_end_timestamp", Long.valueOf(com_google_android_gms_internal_zzcgh.zzaxf()));
        contentValues.put("app_version", com_google_android_gms_internal_zzcgh.zzvj());
        contentValues.put("app_store", com_google_android_gms_internal_zzcgh.zzaxh());
        contentValues.put("gmp_version", Long.valueOf(com_google_android_gms_internal_zzcgh.zzaxi()));
        contentValues.put("dev_cert_hash", Long.valueOf(com_google_android_gms_internal_zzcgh.zzaxj()));
        contentValues.put("measurement_enabled", Boolean.valueOf(com_google_android_gms_internal_zzcgh.zzaxk()));
        contentValues.put("day", Long.valueOf(com_google_android_gms_internal_zzcgh.zzaxp()));
        contentValues.put("daily_public_events_count", Long.valueOf(com_google_android_gms_internal_zzcgh.zzaxq()));
        contentValues.put("daily_events_count", Long.valueOf(com_google_android_gms_internal_zzcgh.zzaxr()));
        contentValues.put("daily_conversions_count", Long.valueOf(com_google_android_gms_internal_zzcgh.zzaxs()));
        contentValues.put("config_fetched_time", Long.valueOf(com_google_android_gms_internal_zzcgh.zzaxm()));
        contentValues.put("failed_config_fetch_time", Long.valueOf(com_google_android_gms_internal_zzcgh.zzaxn()));
        contentValues.put("app_version_int", Long.valueOf(com_google_android_gms_internal_zzcgh.zzaxg()));
        contentValues.put("firebase_instance_id", com_google_android_gms_internal_zzcgh.zzaxd());
        contentValues.put("daily_error_events_count", Long.valueOf(com_google_android_gms_internal_zzcgh.zzaxu()));
        contentValues.put("daily_realtime_events_count", Long.valueOf(com_google_android_gms_internal_zzcgh.zzaxt()));
        contentValues.put("health_monitor_sample", com_google_android_gms_internal_zzcgh.zzaxv());
        contentValues.put("android_id", Long.valueOf(com_google_android_gms_internal_zzcgh.zzaxx()));
        contentValues.put("adid_reporting_enabled", Boolean.valueOf(com_google_android_gms_internal_zzcgh.zzaxy()));
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            if (((long) writableDatabase.update("apps", contentValues, "app_id = ?", new String[]{com_google_android_gms_internal_zzcgh.getAppId()})) == 0 && writableDatabase.insertWithOnConflict("apps", null, contentValues, 5) == -1) {
                zzawy().zzazd().zzj("Failed to insert/update app (got -1). appId", zzchm.zzjk(com_google_android_gms_internal_zzcgh.getAppId()));
            }
        } catch (SQLiteException e) {
            zzawy().zzazd().zze("Error storing app. appId", zzchm.zzjk(com_google_android_gms_internal_zzcgh.getAppId()), e);
        }
    }

    public final void zza(zzcgw com_google_android_gms_internal_zzcgw) {
        Long l = null;
        zzbq.checkNotNull(com_google_android_gms_internal_zzcgw);
        zzve();
        zzxf();
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_internal_zzcgw.mAppId);
        contentValues.put("name", com_google_android_gms_internal_zzcgw.mName);
        contentValues.put("lifetime_count", Long.valueOf(com_google_android_gms_internal_zzcgw.zzizk));
        contentValues.put("current_bundle_count", Long.valueOf(com_google_android_gms_internal_zzcgw.zzizl));
        contentValues.put("last_fire_timestamp", Long.valueOf(com_google_android_gms_internal_zzcgw.zzizm));
        contentValues.put("last_bundled_timestamp", Long.valueOf(com_google_android_gms_internal_zzcgw.zzizn));
        contentValues.put("last_sampled_complex_event_id", com_google_android_gms_internal_zzcgw.zzizo);
        contentValues.put("last_sampling_rate", com_google_android_gms_internal_zzcgw.zzizp);
        if (com_google_android_gms_internal_zzcgw.zzizq != null && com_google_android_gms_internal_zzcgw.zzizq.booleanValue()) {
            l = Long.valueOf(1);
        }
        contentValues.put("last_exempt_from_sampling", l);
        try {
            if (getWritableDatabase().insertWithOnConflict("events", null, contentValues, 5) == -1) {
                zzawy().zzazd().zzj("Failed to insert/update event aggregates (got -1). appId", zzchm.zzjk(com_google_android_gms_internal_zzcgw.mAppId));
            }
        } catch (SQLiteException e) {
            zzawy().zzazd().zze("Error storing event aggregates. appId", zzchm.zzjk(com_google_android_gms_internal_zzcgw.mAppId), e);
        }
    }

    final void zza(String str, zzclr[] com_google_android_gms_internal_zzclrArr) {
        int i = 0;
        zzxf();
        zzve();
        zzbq.zzgm(str);
        zzbq.checkNotNull(com_google_android_gms_internal_zzclrArr);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            int i2;
            zzxf();
            zzve();
            zzbq.zzgm(str);
            SQLiteDatabase writableDatabase2 = getWritableDatabase();
            writableDatabase2.delete("property_filters", "app_id=?", new String[]{str});
            writableDatabase2.delete("event_filters", "app_id=?", new String[]{str});
            for (zzclr com_google_android_gms_internal_zzclr : com_google_android_gms_internal_zzclrArr) {
                zzxf();
                zzve();
                zzbq.zzgm(str);
                zzbq.checkNotNull(com_google_android_gms_internal_zzclr);
                zzbq.checkNotNull(com_google_android_gms_internal_zzclr.zzjju);
                zzbq.checkNotNull(com_google_android_gms_internal_zzclr.zzjjt);
                if (com_google_android_gms_internal_zzclr.zzjjs == null) {
                    zzawy().zzazf().zzj("Audience with no ID. appId", zzchm.zzjk(str));
                } else {
                    int intValue = com_google_android_gms_internal_zzclr.zzjjs.intValue();
                    for (zzcls com_google_android_gms_internal_zzcls : com_google_android_gms_internal_zzclr.zzjju) {
                        if (com_google_android_gms_internal_zzcls.zzjjw == null) {
                            zzawy().zzazf().zze("Event filter with no ID. Audience definition ignored. appId, audienceId", zzchm.zzjk(str), com_google_android_gms_internal_zzclr.zzjjs);
                            break;
                        }
                    }
                    for (zzclv com_google_android_gms_internal_zzclv : com_google_android_gms_internal_zzclr.zzjjt) {
                        if (com_google_android_gms_internal_zzclv.zzjjw == null) {
                            zzawy().zzazf().zze("Property filter with no ID. Audience definition ignored. appId, audienceId", zzchm.zzjk(str), com_google_android_gms_internal_zzclr.zzjjs);
                            break;
                        }
                    }
                    for (zzcls com_google_android_gms_internal_zzcls2 : com_google_android_gms_internal_zzclr.zzjju) {
                        if (!zza(str, intValue, com_google_android_gms_internal_zzcls2)) {
                            i2 = 0;
                            break;
                        }
                    }
                    i2 = 1;
                    if (i2 != 0) {
                        for (zzclv com_google_android_gms_internal_zzclv2 : com_google_android_gms_internal_zzclr.zzjjt) {
                            if (!zza(str, intValue, com_google_android_gms_internal_zzclv2)) {
                                i2 = 0;
                                break;
                            }
                        }
                    }
                    if (i2 == 0) {
                        zzxf();
                        zzve();
                        zzbq.zzgm(str);
                        SQLiteDatabase writableDatabase3 = getWritableDatabase();
                        writableDatabase3.delete("property_filters", "app_id=? and audience_id=?", new String[]{str, String.valueOf(intValue)});
                        writableDatabase3.delete("event_filters", "app_id=? and audience_id=?", new String[]{str, String.valueOf(intValue)});
                    }
                }
            }
            List arrayList = new ArrayList();
            i2 = com_google_android_gms_internal_zzclrArr.length;
            while (i < i2) {
                arrayList.add(com_google_android_gms_internal_zzclrArr[i].zzjjs);
                i++;
            }
            zze(str, arrayList);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public final boolean zza(zzcgl com_google_android_gms_internal_zzcgl) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzcgl);
        zzve();
        zzxf();
        if (zzag(com_google_android_gms_internal_zzcgl.packageName, com_google_android_gms_internal_zzcgl.zziyg.name) == null) {
            if (zzb("SELECT COUNT(1) FROM conditional_properties WHERE app_id=?", new String[]{com_google_android_gms_internal_zzcgl.packageName}) >= 1000) {
                return false;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_internal_zzcgl.packageName);
        contentValues.put(TtmlNode.ATTR_TTS_ORIGIN, com_google_android_gms_internal_zzcgl.zziyf);
        contentValues.put("name", com_google_android_gms_internal_zzcgl.zziyg.name);
        zza(contentValues, "value", com_google_android_gms_internal_zzcgl.zziyg.getValue());
        contentValues.put("active", Boolean.valueOf(com_google_android_gms_internal_zzcgl.zziyi));
        contentValues.put("trigger_event_name", com_google_android_gms_internal_zzcgl.zziyj);
        contentValues.put("trigger_timeout", Long.valueOf(com_google_android_gms_internal_zzcgl.zziyl));
        zzawu();
        contentValues.put("timed_out_event", zzclq.zza(com_google_android_gms_internal_zzcgl.zziyk));
        contentValues.put("creation_timestamp", Long.valueOf(com_google_android_gms_internal_zzcgl.zziyh));
        zzawu();
        contentValues.put("triggered_event", zzclq.zza(com_google_android_gms_internal_zzcgl.zziym));
        contentValues.put("triggered_timestamp", Long.valueOf(com_google_android_gms_internal_zzcgl.zziyg.zzjji));
        contentValues.put("time_to_live", Long.valueOf(com_google_android_gms_internal_zzcgl.zziyn));
        zzawu();
        contentValues.put("expired_event", zzclq.zza(com_google_android_gms_internal_zzcgl.zziyo));
        try {
            if (getWritableDatabase().insertWithOnConflict("conditional_properties", null, contentValues, 5) == -1) {
                zzawy().zzazd().zzj("Failed to insert/update conditional user property (got -1)", zzchm.zzjk(com_google_android_gms_internal_zzcgl.packageName));
            }
        } catch (SQLiteException e) {
            zzawy().zzazd().zze("Error storing conditional user property", zzchm.zzjk(com_google_android_gms_internal_zzcgl.packageName), e);
        }
        return true;
    }

    public final boolean zza(zzcgv com_google_android_gms_internal_zzcgv, long j, boolean z) {
        zzve();
        zzxf();
        zzbq.checkNotNull(com_google_android_gms_internal_zzcgv);
        zzbq.zzgm(com_google_android_gms_internal_zzcgv.mAppId);
        zzfjs com_google_android_gms_internal_zzcmb = new zzcmb();
        com_google_android_gms_internal_zzcmb.zzjlj = Long.valueOf(com_google_android_gms_internal_zzcgv.zzizi);
        com_google_android_gms_internal_zzcmb.zzjlh = new zzcmc[com_google_android_gms_internal_zzcgv.zzizj.size()];
        Iterator it = com_google_android_gms_internal_zzcgv.zzizj.iterator();
        int i = 0;
        while (it.hasNext()) {
            String str = (String) it.next();
            zzcmc com_google_android_gms_internal_zzcmc = new zzcmc();
            int i2 = i + 1;
            com_google_android_gms_internal_zzcmb.zzjlh[i] = com_google_android_gms_internal_zzcmc;
            com_google_android_gms_internal_zzcmc.name = str;
            zzawu().zza(com_google_android_gms_internal_zzcmc, com_google_android_gms_internal_zzcgv.zzizj.get(str));
            i = i2;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzcmb.zzho()];
            zzfjk zzo = zzfjk.zzo(bArr, 0, bArr.length);
            com_google_android_gms_internal_zzcmb.zza(zzo);
            zzo.zzcwt();
            zzawy().zzazj().zze("Saving event, name, data size", zzawt().zzjh(com_google_android_gms_internal_zzcgv.mName), Integer.valueOf(bArr.length));
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzcgv.mAppId);
            contentValues.put("name", com_google_android_gms_internal_zzcgv.mName);
            contentValues.put("timestamp", Long.valueOf(com_google_android_gms_internal_zzcgv.zzfij));
            contentValues.put("metadata_fingerprint", Long.valueOf(j));
            contentValues.put("data", bArr);
            contentValues.put("realtime", Integer.valueOf(z ? 1 : 0));
            try {
                if (getWritableDatabase().insert("raw_events", null, contentValues) != -1) {
                    return true;
                }
                zzawy().zzazd().zzj("Failed to insert raw event (got -1). appId", zzchm.zzjk(com_google_android_gms_internal_zzcgv.mAppId));
                return false;
            } catch (SQLiteException e) {
                zzawy().zzazd().zze("Error storing raw event. appId", zzchm.zzjk(com_google_android_gms_internal_zzcgv.mAppId), e);
                return false;
            }
        } catch (IOException e2) {
            zzawy().zzazd().zze("Data loss. Failed to serialize event params/data. appId", zzchm.zzjk(com_google_android_gms_internal_zzcgv.mAppId), e2);
            return false;
        }
    }

    public final boolean zza(zzclp com_google_android_gms_internal_zzclp) {
        zzbq.checkNotNull(com_google_android_gms_internal_zzclp);
        zzve();
        zzxf();
        if (zzag(com_google_android_gms_internal_zzclp.mAppId, com_google_android_gms_internal_zzclp.mName) == null) {
            if (zzclq.zzjz(com_google_android_gms_internal_zzclp.mName)) {
                if (zzb("select count(1) from user_attributes where app_id=? and name not like '!_%' escape '!'", new String[]{com_google_android_gms_internal_zzclp.mAppId}) >= 25) {
                    return false;
                }
            }
            if (zzb("select count(1) from user_attributes where app_id=? and origin=? AND name like '!_%' escape '!'", new String[]{com_google_android_gms_internal_zzclp.mAppId, com_google_android_gms_internal_zzclp.mOrigin}) >= 25) {
                return false;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_internal_zzclp.mAppId);
        contentValues.put(TtmlNode.ATTR_TTS_ORIGIN, com_google_android_gms_internal_zzclp.mOrigin);
        contentValues.put("name", com_google_android_gms_internal_zzclp.mName);
        contentValues.put("set_timestamp", Long.valueOf(com_google_android_gms_internal_zzclp.zzjjm));
        zza(contentValues, "value", com_google_android_gms_internal_zzclp.mValue);
        try {
            if (getWritableDatabase().insertWithOnConflict("user_attributes", null, contentValues, 5) == -1) {
                zzawy().zzazd().zzj("Failed to insert/update user property (got -1). appId", zzchm.zzjk(com_google_android_gms_internal_zzclp.mAppId));
            }
        } catch (SQLiteException e) {
            zzawy().zzazd().zze("Error storing user property. appId", zzchm.zzjk(com_google_android_gms_internal_zzclp.mAppId), e);
        }
        return true;
    }

    public final boolean zza(zzcme com_google_android_gms_internal_zzcme, boolean z) {
        zzve();
        zzxf();
        zzbq.checkNotNull(com_google_android_gms_internal_zzcme);
        zzbq.zzgm(com_google_android_gms_internal_zzcme.zzcn);
        zzbq.checkNotNull(com_google_android_gms_internal_zzcme.zzjlt);
        zzayh();
        long currentTimeMillis = zzws().currentTimeMillis();
        if (com_google_android_gms_internal_zzcme.zzjlt.longValue() < currentTimeMillis - zzcgn.zzayb() || com_google_android_gms_internal_zzcme.zzjlt.longValue() > zzcgn.zzayb() + currentTimeMillis) {
            zzawy().zzazf().zzd("Storing bundle outside of the max uploading time span. appId, now, timestamp", zzchm.zzjk(com_google_android_gms_internal_zzcme.zzcn), Long.valueOf(currentTimeMillis), com_google_android_gms_internal_zzcme.zzjlt);
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzcme.zzho()];
            zzfjk zzo = zzfjk.zzo(bArr, 0, bArr.length);
            com_google_android_gms_internal_zzcme.zza(zzo);
            zzo.zzcwt();
            bArr = zzawu().zzq(bArr);
            zzawy().zzazj().zzj("Saving bundle, size", Integer.valueOf(bArr.length));
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzcme.zzcn);
            contentValues.put("bundle_end_timestamp", com_google_android_gms_internal_zzcme.zzjlt);
            contentValues.put("data", bArr);
            contentValues.put("has_realtime", Integer.valueOf(z ? 1 : 0));
            try {
                if (getWritableDatabase().insert("queue", null, contentValues) != -1) {
                    return true;
                }
                zzawy().zzazd().zzj("Failed to insert bundle (got -1). appId", zzchm.zzjk(com_google_android_gms_internal_zzcme.zzcn));
                return false;
            } catch (SQLiteException e) {
                zzawy().zzazd().zze("Error storing bundle. appId", zzchm.zzjk(com_google_android_gms_internal_zzcme.zzcn), e);
                return false;
            }
        } catch (IOException e2) {
            zzawy().zzazd().zze("Data loss. Failed to serialize bundle. appId", zzchm.zzjk(com_google_android_gms_internal_zzcme.zzcn), e2);
            return false;
        }
    }

    public final zzcgw zzae(String str, String str2) {
        Cursor query;
        Object e;
        Cursor cursor;
        Throwable th;
        zzbq.zzgm(str);
        zzbq.zzgm(str2);
        zzve();
        zzxf();
        try {
            query = getWritableDatabase().query("events", new String[]{"lifetime_count", "current_bundle_count", "last_fire_timestamp", "last_bundled_timestamp", "last_sampled_complex_event_id", "last_sampling_rate", "last_exempt_from_sampling"}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    long j = query.getLong(0);
                    long j2 = query.getLong(1);
                    long j3 = query.getLong(2);
                    long j4 = query.isNull(3) ? 0 : query.getLong(3);
                    Long valueOf = query.isNull(4) ? null : Long.valueOf(query.getLong(4));
                    Long valueOf2 = query.isNull(5) ? null : Long.valueOf(query.getLong(5));
                    Boolean bool = null;
                    if (!query.isNull(6)) {
                        bool = Boolean.valueOf(query.getLong(6) == 1);
                    }
                    zzcgw com_google_android_gms_internal_zzcgw = new zzcgw(str, str2, j, j2, j3, j4, valueOf, valueOf2, bool);
                    if (query.moveToNext()) {
                        zzawy().zzazd().zzj("Got multiple records for event aggregates, expected one. appId", zzchm.zzjk(str));
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzcgw;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzcgw;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
                try {
                    zzawy().zzazd().zzd("Error querying events. appId", zzchm.zzjk(str), zzawt().zzjh(str2), e);
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
            zzawy().zzazd().zzd("Error querying events. appId", zzchm.zzjk(str), zzawt().zzjh(str2), e);
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

    public final void zzaf(String str, String str2) {
        zzbq.zzgm(str);
        zzbq.zzgm(str2);
        zzve();
        zzxf();
        try {
            zzawy().zzazj().zzj("Deleted user attribute rows", Integer.valueOf(getWritableDatabase().delete("user_attributes", "app_id=? and name=?", new String[]{str, str2})));
        } catch (SQLiteException e) {
            zzawy().zzazd().zzd("Error deleting user attribute. appId", zzchm.zzjk(str), zzawt().zzjj(str2), e);
        }
    }

    public final zzclp zzag(String str, String str2) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzbq.zzgm(str);
        zzbq.zzgm(str2);
        zzve();
        zzxf();
        try {
            Cursor query = getWritableDatabase().query("user_attributes", new String[]{"set_timestamp", "value", TtmlNode.ATTR_TTS_ORIGIN}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    String str3 = str;
                    zzclp com_google_android_gms_internal_zzclp = new zzclp(str3, query.getString(2), str2, query.getLong(0), zza(query, 1));
                    if (query.moveToNext()) {
                        zzawy().zzazd().zzj("Got multiple records for user property, expected one. appId", zzchm.zzjk(str));
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzclp;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzclp;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
                try {
                    zzawy().zzazd().zzd("Error querying user property. appId", zzchm.zzjk(str), zzawt().zzjj(str2), e);
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
            zzawy().zzazd().zzd("Error querying user property. appId", zzchm.zzjk(str), zzawt().zzjj(str2), e);
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

    public final zzcgl zzah(String str, String str2) {
        Object e;
        Cursor cursor;
        Throwable th;
        zzbq.zzgm(str);
        zzbq.zzgm(str2);
        zzve();
        zzxf();
        Cursor query;
        try {
            query = getWritableDatabase().query("conditional_properties", new String[]{TtmlNode.ATTR_TTS_ORIGIN, "value", "active", "trigger_event_name", "trigger_timeout", "timed_out_event", "creation_timestamp", "triggered_event", "triggered_timestamp", "time_to_live", "expired_event"}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    String string = query.getString(0);
                    Object zza = zza(query, 1);
                    boolean z = query.getInt(2) != 0;
                    String string2 = query.getString(3);
                    long j = query.getLong(4);
                    zzcha com_google_android_gms_internal_zzcha = (zzcha) zzawu().zzb(query.getBlob(5), zzcha.CREATOR);
                    long j2 = query.getLong(6);
                    zzcha com_google_android_gms_internal_zzcha2 = (zzcha) zzawu().zzb(query.getBlob(7), zzcha.CREATOR);
                    long j3 = query.getLong(8);
                    zzcgl com_google_android_gms_internal_zzcgl = new zzcgl(str, string, new zzcln(str2, j3, zza, string), j2, z, string2, com_google_android_gms_internal_zzcha, j, com_google_android_gms_internal_zzcha2, query.getLong(9), (zzcha) zzawu().zzb(query.getBlob(10), zzcha.CREATOR));
                    if (query.moveToNext()) {
                        zzawy().zzazd().zze("Got multiple records for conditional property, expected one", zzchm.zzjk(str), zzawt().zzjj(str2));
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzcgl;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzcgl;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
                try {
                    zzawy().zzazd().zzd("Error querying conditional property", zzchm.zzjk(str), zzawt().zzjj(str2), e);
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
            zzawy().zzazd().zzd("Error querying conditional property", zzchm.zzjk(str), zzawt().zzjj(str2), e);
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

    public final void zzah(List<Long> list) {
        zzbq.checkNotNull(list);
        zzve();
        zzxf();
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
            zzawy().zzazd().zze("Deleted fewer rows from raw events table than expected", Integer.valueOf(delete), Integer.valueOf(list.size()));
        }
    }

    public final int zzai(String str, String str2) {
        int i = 0;
        zzbq.zzgm(str);
        zzbq.zzgm(str2);
        zzve();
        zzxf();
        try {
            i = getWritableDatabase().delete("conditional_properties", "app_id=? and name=?", new String[]{str, str2});
        } catch (SQLiteException e) {
            zzawy().zzazd().zzd("Error deleting conditional property", zzchm.zzjk(str), zzawt().zzjj(str2), e);
        }
        return i;
    }

    final Map<Integer, List<zzcls>> zzaj(String str, String str2) {
        Object e;
        Throwable th;
        zzxf();
        zzve();
        zzbq.zzgm(str);
        zzbq.zzgm(str2);
        Map<Integer, List<zzcls>> arrayMap = new ArrayMap();
        Cursor query;
        try {
            query = getWritableDatabase().query("event_filters", new String[]{"audience_id", "data"}, "app_id=? AND event_name=?", new String[]{str, str2}, null, null, null);
            if (query.moveToFirst()) {
                do {
                    try {
                        byte[] blob = query.getBlob(1);
                        zzfjj zzn = zzfjj.zzn(blob, 0, blob.length);
                        zzfjs com_google_android_gms_internal_zzcls = new zzcls();
                        try {
                            com_google_android_gms_internal_zzcls.zza(zzn);
                            int i = query.getInt(0);
                            List list = (List) arrayMap.get(Integer.valueOf(i));
                            if (list == null) {
                                list = new ArrayList();
                                arrayMap.put(Integer.valueOf(i), list);
                            }
                            list.add(com_google_android_gms_internal_zzcls);
                        } catch (IOException e2) {
                            zzawy().zzazd().zze("Failed to merge filter. appId", zzchm.zzjk(str), e2);
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
            Map<Integer, List<zzcls>> emptyMap = Collections.emptyMap();
            if (query == null) {
                return emptyMap;
            }
            query.close();
            return emptyMap;
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            try {
                zzawy().zzazd().zze("Database error querying filters. appId", zzchm.zzjk(str), e);
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

    final Map<Integer, List<zzclv>> zzak(String str, String str2) {
        Object e;
        Throwable th;
        zzxf();
        zzve();
        zzbq.zzgm(str);
        zzbq.zzgm(str2);
        Map<Integer, List<zzclv>> arrayMap = new ArrayMap();
        Cursor query;
        try {
            query = getWritableDatabase().query("property_filters", new String[]{"audience_id", "data"}, "app_id=? AND property_name=?", new String[]{str, str2}, null, null, null);
            if (query.moveToFirst()) {
                do {
                    try {
                        byte[] blob = query.getBlob(1);
                        zzfjj zzn = zzfjj.zzn(blob, 0, blob.length);
                        zzfjs com_google_android_gms_internal_zzclv = new zzclv();
                        try {
                            com_google_android_gms_internal_zzclv.zza(zzn);
                            int i = query.getInt(0);
                            List list = (List) arrayMap.get(Integer.valueOf(i));
                            if (list == null) {
                                list = new ArrayList();
                                arrayMap.put(Integer.valueOf(i), list);
                            }
                            list.add(com_google_android_gms_internal_zzclv);
                        } catch (IOException e2) {
                            zzawy().zzazd().zze("Failed to merge filter", zzchm.zzjk(str), e2);
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
            Map<Integer, List<zzclv>> emptyMap = Collections.emptyMap();
            if (query == null) {
                return emptyMap;
            }
            query.close();
            return emptyMap;
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            try {
                zzawy().zzazd().zze("Database error querying filters. appId", zzchm.zzjk(str), e);
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

    protected final long zzal(String str, String str2) {
        long zza;
        Object e;
        zzbq.zzgm(str);
        zzbq.zzgm(str2);
        zzve();
        zzxf();
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
                    zzawy().zzazd().zze("Failed to insert column (got -1). appId", zzchm.zzjk(str), str2);
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
                    zzawy().zzazd().zze("Failed to update column (got 0). appId", zzchm.zzjk(str), str2);
                    writableDatabase.endTransaction();
                    return -1;
                }
                writableDatabase.setTransactionSuccessful();
                writableDatabase.endTransaction();
                return zza;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzawy().zzazd().zzd("Error inserting column. appId", zzchm.zzjk(str), str2, e);
                    return zza;
                } finally {
                    writableDatabase.endTransaction();
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            zza = 0;
            zzawy().zzazd().zzd("Error inserting column. appId", zzchm.zzjk(str), str2, e);
            return zza;
        }
    }

    protected final boolean zzaxz() {
        return false;
    }

    public final String zzayf() {
        Object e;
        Throwable th;
        String str = null;
        Cursor rawQuery;
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
                    zzawy().zzazd().zzj("Database error getting next bundle app id", e);
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
            zzawy().zzazd().zzj("Database error getting next bundle app id", e);
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

    public final boolean zzayg() {
        return zzb("select count(1) > 0 from queue where has_realtime = 1", null) != 0;
    }

    final void zzayh() {
        zzve();
        zzxf();
        if (zzayn()) {
            long j = zzawz().zzjcu.get();
            long elapsedRealtime = zzws().elapsedRealtime();
            if (Math.abs(elapsedRealtime - j) > ((Long) zzchc.zzjbb.get()).longValue()) {
                zzawz().zzjcu.set(elapsedRealtime);
                zzve();
                zzxf();
                if (zzayn()) {
                    int delete = getWritableDatabase().delete("queue", "abs(bundle_end_timestamp - ?) > cast(? as integer)", new String[]{String.valueOf(zzws().currentTimeMillis()), String.valueOf(zzcgn.zzayb())});
                    if (delete > 0) {
                        zzawy().zzazj().zzj("Deleted stale rows. rowsDeleted", Integer.valueOf(delete));
                    }
                }
            }
        }
    }

    public final long zzayi() {
        return zza("select max(bundle_end_timestamp) from queue", null, 0);
    }

    public final long zzayj() {
        return zza("select max(timestamp) from raw_events", null, 0);
    }

    public final boolean zzayk() {
        return zzb("select count(1) > 0 from raw_events", null) != 0;
    }

    public final boolean zzayl() {
        return zzb("select count(1) > 0 from raw_events where realtime = 1", null) != 0;
    }

    public final long zzaym() {
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
            zzawy().zzazd().zzj("Error querying raw events", e);
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

    public final String zzba(long j) {
        Cursor rawQuery;
        Object e;
        Throwable th;
        String str = null;
        zzve();
        zzxf();
        try {
            rawQuery = getWritableDatabase().rawQuery("select app_id from apps where app_id in (select distinct app_id from raw_events) and config_fetched_time < ? order by failed_config_fetch_time limit 1;", new String[]{String.valueOf(j)});
            try {
                if (rawQuery.moveToFirst()) {
                    str = rawQuery.getString(0);
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                } else {
                    zzawy().zzazj().log("No expired configs for apps with pending events");
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                }
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzawy().zzazd().zzj("Error selecting expired configs", e);
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
            zzawy().zzazd().zzj("Error selecting expired configs", e);
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

    public final List<zzcgl> zzc(String str, String[] strArr) {
        Object e;
        Cursor cursor;
        Throwable th;
        zzve();
        zzxf();
        List<zzcgl> arrayList = new ArrayList();
        Cursor query;
        try {
            query = getWritableDatabase().query("conditional_properties", new String[]{"app_id", TtmlNode.ATTR_TTS_ORIGIN, "name", "value", "active", "trigger_event_name", "trigger_timeout", "timed_out_event", "creation_timestamp", "triggered_event", "triggered_timestamp", "time_to_live", "expired_event"}, str, strArr, null, null, "rowid", "1001");
            try {
                if (query.moveToFirst()) {
                    do {
                        if (arrayList.size() >= 1000) {
                            zzawy().zzazd().zzj("Read more than the max allowed conditional properties, ignoring extra", Integer.valueOf(1000));
                            break;
                        }
                        String string = query.getString(0);
                        String string2 = query.getString(1);
                        String string3 = query.getString(2);
                        Object zza = zza(query, 3);
                        boolean z = query.getInt(4) != 0;
                        String string4 = query.getString(5);
                        long j = query.getLong(6);
                        zzcha com_google_android_gms_internal_zzcha = (zzcha) zzawu().zzb(query.getBlob(7), zzcha.CREATOR);
                        long j2 = query.getLong(8);
                        zzcha com_google_android_gms_internal_zzcha2 = (zzcha) zzawu().zzb(query.getBlob(9), zzcha.CREATOR);
                        long j3 = query.getLong(10);
                        List<zzcgl> list = arrayList;
                        list.add(new zzcgl(string, string2, new zzcln(string3, j3, zza, string2), j2, z, string4, com_google_android_gms_internal_zzcha, j, com_google_android_gms_internal_zzcha2, query.getLong(11), (zzcha) zzawu().zzb(query.getBlob(12), zzcha.CREATOR)));
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
                zzawy().zzazd().zzj("Error querying conditional user property value", e);
                List<zzcgl> emptyList = Collections.emptyList();
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

    public final List<zzclp> zzg(String str, String str2, String str3) {
        Object obj;
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzbq.zzgm(str);
        zzve();
        zzxf();
        List<zzclp> arrayList = new ArrayList();
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
            String[] strArr = new String[]{"name", "set_timestamp", "value", TtmlNode.ATTR_TTS_ORIGIN};
            Cursor query = getWritableDatabase().query("user_attributes", strArr, stringBuilder.toString(), (String[]) arrayList2.toArray(new String[arrayList2.size()]), null, null, "rowid", "1001");
            try {
                if (query.moveToFirst()) {
                    while (arrayList.size() < 1000) {
                        String string;
                        try {
                            String string2 = query.getString(0);
                            long j = query.getLong(1);
                            Object zza = zza(query, 2);
                            string = query.getString(3);
                            if (zza == null) {
                                zzawy().zzazd().zzd("(2)Read invalid user property value, ignoring it", zzchm.zzjk(str), string, str3);
                            } else {
                                arrayList.add(new zzclp(str, string, string2, j, zza));
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
                    zzawy().zzazd().zzj("Read more than the max allowed user properties, ignoring excess", Integer.valueOf(1000));
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
                zzawy().zzazd().zzd("(2)Error querying user properties", zzchm.zzjk(str), obj, e);
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

    public final List<zzcgl> zzh(String str, String str2, String str3) {
        zzbq.zzgm(str);
        zzve();
        zzxf();
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

    public final List<zzclp> zzja(String str) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzbq.zzgm(str);
        zzve();
        zzxf();
        List<zzclp> arrayList = new ArrayList();
        try {
            Cursor query = getWritableDatabase().query("user_attributes", new String[]{"name", TtmlNode.ATTR_TTS_ORIGIN, "set_timestamp", "value"}, "app_id=?", new String[]{str}, null, null, "rowid", "1000");
            try {
                if (query.moveToFirst()) {
                    do {
                        String string = query.getString(0);
                        String string2 = query.getString(1);
                        if (string2 == null) {
                            string2 = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        long j = query.getLong(2);
                        Object zza = zza(query, 3);
                        if (zza == null) {
                            zzawy().zzazd().zzj("Read invalid user property value, ignoring it. appId", zzchm.zzjk(str));
                        } else {
                            arrayList.add(new zzclp(str, string2, string, j, zza));
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
                zzawy().zzazd().zze("Error querying user properties. appId", zzchm.zzjk(str), e);
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

    public final zzcgh zzjb(String str) {
        Cursor query;
        Object e;
        Throwable th;
        zzbq.zzgm(str);
        zzve();
        zzxf();
        try {
            query = getWritableDatabase().query("apps", new String[]{"app_instance_id", "gmp_app_id", "resettable_device_id_hash", "last_bundle_index", "last_bundle_start_timestamp", "last_bundle_end_timestamp", "app_version", "app_store", "gmp_version", "dev_cert_hash", "measurement_enabled", "day", "daily_public_events_count", "daily_events_count", "daily_conversions_count", "config_fetched_time", "failed_config_fetch_time", "app_version_int", "firebase_instance_id", "daily_error_events_count", "daily_realtime_events_count", "health_monitor_sample", "android_id", "adid_reporting_enabled"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zzcgh com_google_android_gms_internal_zzcgh = new zzcgh(this.zziwf, str);
                    com_google_android_gms_internal_zzcgh.zzir(query.getString(0));
                    com_google_android_gms_internal_zzcgh.zzis(query.getString(1));
                    com_google_android_gms_internal_zzcgh.zzit(query.getString(2));
                    com_google_android_gms_internal_zzcgh.zzaq(query.getLong(3));
                    com_google_android_gms_internal_zzcgh.zzal(query.getLong(4));
                    com_google_android_gms_internal_zzcgh.zzam(query.getLong(5));
                    com_google_android_gms_internal_zzcgh.setAppVersion(query.getString(6));
                    com_google_android_gms_internal_zzcgh.zziv(query.getString(7));
                    com_google_android_gms_internal_zzcgh.zzao(query.getLong(8));
                    com_google_android_gms_internal_zzcgh.zzap(query.getLong(9));
                    boolean z = query.isNull(10) || query.getInt(10) != 0;
                    com_google_android_gms_internal_zzcgh.setMeasurementEnabled(z);
                    com_google_android_gms_internal_zzcgh.zzat(query.getLong(11));
                    com_google_android_gms_internal_zzcgh.zzau(query.getLong(12));
                    com_google_android_gms_internal_zzcgh.zzav(query.getLong(13));
                    com_google_android_gms_internal_zzcgh.zzaw(query.getLong(14));
                    com_google_android_gms_internal_zzcgh.zzar(query.getLong(15));
                    com_google_android_gms_internal_zzcgh.zzas(query.getLong(16));
                    com_google_android_gms_internal_zzcgh.zzan(query.isNull(17) ? -2147483648L : (long) query.getInt(17));
                    com_google_android_gms_internal_zzcgh.zziu(query.getString(18));
                    com_google_android_gms_internal_zzcgh.zzay(query.getLong(19));
                    com_google_android_gms_internal_zzcgh.zzax(query.getLong(20));
                    com_google_android_gms_internal_zzcgh.zziw(query.getString(21));
                    com_google_android_gms_internal_zzcgh.zzaz(query.isNull(22) ? 0 : query.getLong(22));
                    z = query.isNull(23) || query.getInt(23) != 0;
                    com_google_android_gms_internal_zzcgh.zzbl(z);
                    com_google_android_gms_internal_zzcgh.zzaxb();
                    if (query.moveToNext()) {
                        zzawy().zzazd().zzj("Got multiple records for app, expected one. appId", zzchm.zzjk(str));
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzcgh;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzcgh;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzawy().zzazd().zze("Error querying app. appId", zzchm.zzjk(str), e);
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
            zzawy().zzazd().zze("Error querying app. appId", zzchm.zzjk(str), e);
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

    public final long zzjc(String str) {
        zzbq.zzgm(str);
        zzve();
        zzxf();
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            String valueOf = String.valueOf(Math.max(0, Math.min(1000000, zzaxa().zzb(str, zzchc.zzjas))));
            return (long) writableDatabase.delete("raw_events", "rowid in (select rowid from raw_events where app_id=? order by rowid desc limit -1 offset ?)", new String[]{str, valueOf});
        } catch (SQLiteException e) {
            zzawy().zzazd().zze("Error deleting over the limit events. appId", zzchm.zzjk(str), e);
            return 0;
        }
    }

    public final byte[] zzjd(String str) {
        Object e;
        Throwable th;
        zzbq.zzgm(str);
        zzve();
        zzxf();
        Cursor query;
        try {
            query = getWritableDatabase().query("apps", new String[]{"remote_config"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    byte[] blob = query.getBlob(0);
                    if (query.moveToNext()) {
                        zzawy().zzazd().zzj("Got multiple records for app config, expected one. appId", zzchm.zzjk(str));
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
                    zzawy().zzazd().zze("Error querying remote config. appId", zzchm.zzjk(str), e);
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
            zzawy().zzazd().zze("Error querying remote config. appId", zzchm.zzjk(str), e);
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

    final Map<Integer, zzcmf> zzje(String str) {
        Cursor query;
        Object e;
        Throwable th;
        zzxf();
        zzve();
        zzbq.zzgm(str);
        try {
            query = getWritableDatabase().query("audience_filter_values", new String[]{"audience_id", "current_results"}, "app_id=?", new String[]{str}, null, null, null);
            if (query.moveToFirst()) {
                Map<Integer, zzcmf> arrayMap = new ArrayMap();
                do {
                    int i = query.getInt(0);
                    byte[] blob = query.getBlob(1);
                    zzfjj zzn = zzfjj.zzn(blob, 0, blob.length);
                    zzfjs com_google_android_gms_internal_zzcmf = new zzcmf();
                    try {
                        com_google_android_gms_internal_zzcmf.zza(zzn);
                    } catch (IOException e2) {
                        zzawy().zzazd().zzd("Failed to merge filter results. appId, audienceId, error", zzchm.zzjk(str), Integer.valueOf(i), e2);
                    }
                    try {
                        arrayMap.put(Integer.valueOf(i), com_google_android_gms_internal_zzcmf);
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
                zzawy().zzazd().zze("Database error querying filter results. appId", zzchm.zzjk(str), e);
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

    public final long zzjf(String str) {
        zzbq.zzgm(str);
        return zza("select count(1) from events where app_id=? and name not like '!_%' escape '!'", new String[]{str}, 0);
    }

    public final List<Pair<zzcme, Long>> zzl(String str, int i, int i2) {
        List<Pair<zzcme, Long>> arrayList;
        Object e;
        Cursor cursor;
        Throwable th;
        boolean z = true;
        zzve();
        zzxf();
        zzbq.checkArgument(i > 0);
        if (i2 <= 0) {
            z = false;
        }
        zzbq.checkArgument(z);
        zzbq.zzgm(str);
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
                            byte[] zzr = zzawu().zzr(query.getBlob(1));
                            if (!arrayList.isEmpty() && zzr.length + i3 > i2) {
                                break;
                            }
                            zzfjj zzn = zzfjj.zzn(zzr, 0, zzr.length);
                            zzfjs com_google_android_gms_internal_zzcme = new zzcme();
                            try {
                                com_google_android_gms_internal_zzcme.zza(zzn);
                                length = zzr.length + i3;
                                arrayList.add(Pair.create(com_google_android_gms_internal_zzcme, Long.valueOf(j)));
                            } catch (IOException e2) {
                                zzawy().zzazd().zze("Failed to merge queued bundle. appId", zzchm.zzjk(str), e2);
                                length = i3;
                            }
                            if (!query.moveToNext() || length > i2) {
                                break;
                            }
                            i3 = length;
                        } catch (IOException e22) {
                            zzawy().zzazd().zze("Failed to unzip queued bundle. appId", zzchm.zzjk(str), e22);
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
                zzawy().zzazd().zze("Error querying bundles. appId", zzchm.zzjk(str), e);
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
}
