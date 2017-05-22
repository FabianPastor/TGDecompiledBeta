package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PointerIconCompat;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzauw.zze;
import com.google.android.gms.internal.zzauw.zzf;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class zzatj extends zzauh {
    private static final Map<String, String> zzbrh = new ArrayMap(1);
    private static final Map<String, String> zzbri = new ArrayMap(18);
    private static final Map<String, String> zzbrj = new ArrayMap(1);
    private static final Map<String, String> zzbrk = new ArrayMap(1);
    private static final Map<String, String> zzbrl = new ArrayMap(1);
    private final zzc zzbrm = new zzc(this, getContext(), zzow());
    private final zzauo zzbrn = new zzauo(zznR());

    public static class zza {
        long zzbro;
        long zzbrp;
        long zzbrq;
        long zzbrr;
        long zzbrs;
    }

    interface zzb {
        boolean zza(long j, com.google.android.gms.internal.zzauw.zzb com_google_android_gms_internal_zzauw_zzb);

        void zzb(zze com_google_android_gms_internal_zzauw_zze);
    }

    private class zzc extends SQLiteOpenHelper {
        final /* synthetic */ zzatj zzbrt;

        zzc(zzatj com_google_android_gms_internal_zzatj, Context context, String str) {
            this.zzbrt = com_google_android_gms_internal_zzatj;
            super(context, str, null, 1);
        }

        @WorkerThread
        public SQLiteDatabase getWritableDatabase() {
            if (this.zzbrt.zzbrn.zzA(this.zzbrt.zzKn().zzLd())) {
                SQLiteDatabase writableDatabase;
                try {
                    writableDatabase = super.getWritableDatabase();
                } catch (SQLiteException e) {
                    this.zzbrt.zzbrn.start();
                    this.zzbrt.zzKl().zzLZ().log("Opening the database failed, dropping and recreating it");
                    String zzow = this.zzbrt.zzow();
                    if (!this.zzbrt.getContext().getDatabasePath(zzow).delete()) {
                        this.zzbrt.zzKl().zzLZ().zzj("Failed to delete corrupted db file", zzow);
                    }
                    try {
                        writableDatabase = super.getWritableDatabase();
                        this.zzbrt.zzbrn.clear();
                    } catch (SQLiteException e2) {
                        this.zzbrt.zzKl().zzLZ().zzj("Failed to open freshly created database", e2);
                        throw e2;
                    }
                }
                return writableDatabase;
            }
            throw new SQLiteException("Database open failed");
        }

        @WorkerThread
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            zzatj.zza(this.zzbrt.zzKl(), sQLiteDatabase);
        }

        @WorkerThread
        public void onOpen(SQLiteDatabase sQLiteDatabase) {
            if (VERSION.SDK_INT < 15) {
                Cursor rawQuery = sQLiteDatabase.rawQuery("PRAGMA journal_mode=memory", null);
                try {
                    rawQuery.moveToFirst();
                } finally {
                    rawQuery.close();
                }
            }
            zzatj.zza(this.zzbrt.zzKl(), sQLiteDatabase, "events", "CREATE TABLE IF NOT EXISTS events ( app_id TEXT NOT NULL, name TEXT NOT NULL, lifetime_count INTEGER NOT NULL, current_bundle_count INTEGER NOT NULL, last_fire_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,lifetime_count,current_bundle_count,last_fire_timestamp", null);
            zzatj.zza(this.zzbrt.zzKl(), sQLiteDatabase, "conditional_properties", "CREATE TABLE IF NOT EXISTS conditional_properties ( app_id TEXT NOT NULL, origin TEXT NOT NULL, name TEXT NOT NULL, value BLOB NOT NULL, creation_timestamp INTEGER NOT NULL, active INTEGER NOT NULL, trigger_event_name TEXT, trigger_timeout INTEGER NOT NULL, timed_out_event BLOB,triggered_event BLOB, triggered_timestamp INTEGER NOT NULL, time_to_live INTEGER NOT NULL, expired_event BLOB, PRIMARY KEY (app_id, name)) ;", "app_id,origin,name,value,active,trigger_event_name,trigger_timeout,creation_timestamp,timed_out_event,triggered_event,triggered_timestamp,time_to_live,expired_event", null);
            zzatj.zza(this.zzbrt.zzKl(), sQLiteDatabase, "user_attributes", "CREATE TABLE IF NOT EXISTS user_attributes ( app_id TEXT NOT NULL, name TEXT NOT NULL, triggered_timestamp INTEGER NOT NULL, value BLOB NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,triggered_timestamp,value", zzatj.zzbrh);
            zzatj.zza(this.zzbrt.zzKl(), sQLiteDatabase, "apps", "CREATE TABLE IF NOT EXISTS apps ( app_id TEXT NOT NULL, app_instance_id TEXT, gmp_app_id TEXT, resettable_device_id_hash TEXT, last_bundle_index INTEGER NOT NULL, last_bundle_end_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id)) ;", "app_id,app_instance_id,gmp_app_id,resettable_device_id_hash,last_bundle_index,last_bundle_end_timestamp", zzatj.zzbri);
            zzatj.zza(this.zzbrt.zzKl(), sQLiteDatabase, "queue", "CREATE TABLE IF NOT EXISTS queue ( app_id TEXT NOT NULL, bundle_end_timestamp INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,bundle_end_timestamp,data", zzatj.zzbrk);
            zzatj.zza(this.zzbrt.zzKl(), sQLiteDatabase, "raw_events_metadata", "CREATE TABLE IF NOT EXISTS raw_events_metadata ( app_id TEXT NOT NULL, metadata_fingerprint INTEGER NOT NULL, metadata BLOB NOT NULL, PRIMARY KEY (app_id, metadata_fingerprint));", "app_id,metadata_fingerprint,metadata", null);
            zzatj.zza(this.zzbrt.zzKl(), sQLiteDatabase, "raw_events", "CREATE TABLE IF NOT EXISTS raw_events ( app_id TEXT NOT NULL, name TEXT NOT NULL, timestamp INTEGER NOT NULL, metadata_fingerprint INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,name,timestamp,metadata_fingerprint,data", zzatj.zzbrj);
            zzatj.zza(this.zzbrt.zzKl(), sQLiteDatabase, "event_filters", "CREATE TABLE IF NOT EXISTS event_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, event_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, event_name, audience_id, filter_id));", "app_id,audience_id,filter_id,event_name,data", null);
            zzatj.zza(this.zzbrt.zzKl(), sQLiteDatabase, "property_filters", "CREATE TABLE IF NOT EXISTS property_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, property_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, property_name, audience_id, filter_id));", "app_id,audience_id,filter_id,property_name,data", null);
            zzatj.zza(this.zzbrt.zzKl(), sQLiteDatabase, "audience_filter_values", "CREATE TABLE IF NOT EXISTS audience_filter_values ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, current_results BLOB, PRIMARY KEY (app_id, audience_id));", "app_id,audience_id,current_results", null);
            zzatj.zza(this.zzbrt.zzKl(), sQLiteDatabase, "app2", "CREATE TABLE IF NOT EXISTS app2 ( app_id TEXT NOT NULL, first_open_count INTEGER NOT NULL, PRIMARY KEY (app_id));", "app_id,first_open_count", zzatj.zzbrl);
        }

        @WorkerThread
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    static {
        zzbrh.put("origin", "ALTER TABLE user_attributes ADD COLUMN origin TEXT;");
        zzbri.put("app_version", "ALTER TABLE apps ADD COLUMN app_version TEXT;");
        zzbri.put("app_store", "ALTER TABLE apps ADD COLUMN app_store TEXT;");
        zzbri.put("gmp_version", "ALTER TABLE apps ADD COLUMN gmp_version INTEGER;");
        zzbri.put("dev_cert_hash", "ALTER TABLE apps ADD COLUMN dev_cert_hash INTEGER;");
        zzbri.put("measurement_enabled", "ALTER TABLE apps ADD COLUMN measurement_enabled INTEGER;");
        zzbri.put("last_bundle_start_timestamp", "ALTER TABLE apps ADD COLUMN last_bundle_start_timestamp INTEGER;");
        zzbri.put("day", "ALTER TABLE apps ADD COLUMN day INTEGER;");
        zzbri.put("daily_public_events_count", "ALTER TABLE apps ADD COLUMN daily_public_events_count INTEGER;");
        zzbri.put("daily_events_count", "ALTER TABLE apps ADD COLUMN daily_events_count INTEGER;");
        zzbri.put("daily_conversions_count", "ALTER TABLE apps ADD COLUMN daily_conversions_count INTEGER;");
        zzbri.put("remote_config", "ALTER TABLE apps ADD COLUMN remote_config BLOB;");
        zzbri.put("config_fetched_time", "ALTER TABLE apps ADD COLUMN config_fetched_time INTEGER;");
        zzbri.put("failed_config_fetch_time", "ALTER TABLE apps ADD COLUMN failed_config_fetch_time INTEGER;");
        zzbri.put("app_version_int", "ALTER TABLE apps ADD COLUMN app_version_int INTEGER;");
        zzbri.put("firebase_instance_id", "ALTER TABLE apps ADD COLUMN firebase_instance_id TEXT;");
        zzbri.put("daily_error_events_count", "ALTER TABLE apps ADD COLUMN daily_error_events_count INTEGER;");
        zzbri.put("daily_realtime_events_count", "ALTER TABLE apps ADD COLUMN daily_realtime_events_count INTEGER;");
        zzbri.put("health_monitor_sample", "ALTER TABLE apps ADD COLUMN health_monitor_sample TEXT;");
        zzbri.put("android_id", "ALTER TABLE apps ADD COLUMN android_id INTEGER;");
        zzbrj.put("realtime", "ALTER TABLE raw_events ADD COLUMN realtime INTEGER;");
        zzbrk.put("has_realtime", "ALTER TABLE queue ADD COLUMN has_realtime INTEGER;");
        zzbrl.put("previous_install_count", "ALTER TABLE app2 ADD COLUMN previous_install_count INTEGER;");
    }

    zzatj(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    private boolean zzLN() {
        return getContext().getDatabasePath(zzow()).exists();
    }

    @WorkerThread
    @TargetApi(11)
    static int zza(Cursor cursor, int i) {
        int i2 = VERSION.SDK_INT;
        return cursor.getType(i);
    }

    @WorkerThread
    private long zza(String str, String[] strArr, long j) {
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
            zzKl().zzLZ().zze("Database error", str, e);
            throw e;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    static void zza(zzatx com_google_android_gms_internal_zzatx, SQLiteDatabase sQLiteDatabase) {
        if (com_google_android_gms_internal_zzatx == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        }
        int i = VERSION.SDK_INT;
        File file = new File(sQLiteDatabase.getPath());
        if (!file.setReadable(false, false)) {
            com_google_android_gms_internal_zzatx.zzMb().log("Failed to turn off database read permission");
        }
        if (!file.setWritable(false, false)) {
            com_google_android_gms_internal_zzatx.zzMb().log("Failed to turn off database write permission");
        }
        if (!file.setReadable(true, true)) {
            com_google_android_gms_internal_zzatx.zzMb().log("Failed to turn on database read permission for owner");
        }
        if (!file.setWritable(true, true)) {
            com_google_android_gms_internal_zzatx.zzMb().log("Failed to turn on database write permission for owner");
        }
    }

    @WorkerThread
    static void zza(zzatx com_google_android_gms_internal_zzatx, SQLiteDatabase sQLiteDatabase, String str, String str2, String str3, Map<String, String> map) throws SQLiteException {
        if (com_google_android_gms_internal_zzatx == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        }
        if (!zza(com_google_android_gms_internal_zzatx, sQLiteDatabase, str)) {
            sQLiteDatabase.execSQL(str2);
        }
        try {
            zza(com_google_android_gms_internal_zzatx, sQLiteDatabase, str, str3, map);
        } catch (SQLiteException e) {
            com_google_android_gms_internal_zzatx.zzLZ().zzj("Failed to verify columns on table that was just created", str);
            throw e;
        }
    }

    @WorkerThread
    static void zza(zzatx com_google_android_gms_internal_zzatx, SQLiteDatabase sQLiteDatabase, String str, String str2, Map<String, String> map) throws SQLiteException {
        if (com_google_android_gms_internal_zzatx == null) {
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
            com_google_android_gms_internal_zzatx.zzMb().zze("Table has extra columns. table, columns", str, TextUtils.join(", ", zzb));
        }
    }

    @WorkerThread
    private void zza(String str, com.google.android.gms.internal.zzauu.zza com_google_android_gms_internal_zzauu_zza) {
        Object obj = null;
        zzob();
        zzmR();
        zzac.zzdr(str);
        zzac.zzw(com_google_android_gms_internal_zzauu_zza);
        zzac.zzw(com_google_android_gms_internal_zzauu_zza.zzbwp);
        zzac.zzw(com_google_android_gms_internal_zzauu_zza.zzbwo);
        if (com_google_android_gms_internal_zzauu_zza.zzbwn == null) {
            zzKl().zzMb().zzj("Audience with no ID. appId", zzatx.zzfE(str));
            return;
        }
        int intValue = com_google_android_gms_internal_zzauu_zza.zzbwn.intValue();
        for (com.google.android.gms.internal.zzauu.zzb com_google_android_gms_internal_zzauu_zzb : com_google_android_gms_internal_zzauu_zza.zzbwp) {
            if (com_google_android_gms_internal_zzauu_zzb.zzbwr == null) {
                zzKl().zzMb().zze("Event filter with no ID. Audience definition ignored. appId, audienceId", zzatx.zzfE(str), com_google_android_gms_internal_zzauu_zza.zzbwn);
                return;
            }
        }
        for (zzauu.zze com_google_android_gms_internal_zzauu_zze : com_google_android_gms_internal_zzauu_zza.zzbwo) {
            if (com_google_android_gms_internal_zzauu_zze.zzbwr == null) {
                zzKl().zzMb().zze("Property filter with no ID. Audience definition ignored. appId, audienceId", zzatx.zzfE(str), com_google_android_gms_internal_zzauu_zza.zzbwn);
                return;
            }
        }
        Object obj2 = 1;
        for (com.google.android.gms.internal.zzauu.zzb zza : com_google_android_gms_internal_zzauu_zza.zzbwp) {
            if (!zza(str, intValue, zza)) {
                obj2 = null;
                break;
            }
        }
        if (obj2 != null) {
            for (zzauu.zze zza2 : com_google_android_gms_internal_zzauu_zza.zzbwo) {
                if (!zza(str, intValue, zza2)) {
                    break;
                }
            }
        }
        obj = obj2;
        if (obj == null) {
            zzA(str, intValue);
        }
    }

    @WorkerThread
    static boolean zza(zzatx com_google_android_gms_internal_zzatx, SQLiteDatabase sQLiteDatabase, String str) {
        Cursor query;
        Object e;
        Throwable th;
        Cursor cursor = null;
        if (com_google_android_gms_internal_zzatx == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        }
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
                    com_google_android_gms_internal_zzatx.zzMb().zze("Error querying for table", str, e);
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
            com_google_android_gms_internal_zzatx.zzMb().zze("Error querying for table", str, e);
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
    private boolean zza(String str, int i, com.google.android.gms.internal.zzauu.zzb com_google_android_gms_internal_zzauu_zzb) {
        zzob();
        zzmR();
        zzac.zzdr(str);
        zzac.zzw(com_google_android_gms_internal_zzauu_zzb);
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzauu_zzb.zzbws)) {
            zzKl().zzMb().zzd("Event filter had no event name. Audience definition ignored. appId, audienceId, filterId", zzatx.zzfE(str), Integer.valueOf(i), String.valueOf(com_google_android_gms_internal_zzauu_zzb.zzbwr));
            return false;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzauu_zzb.zzafB()];
            zzbyc zzah = zzbyc.zzah(bArr);
            com_google_android_gms_internal_zzauu_zzb.zza(zzah);
            zzah.zzafo();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("filter_id", com_google_android_gms_internal_zzauu_zzb.zzbwr);
            contentValues.put("event_name", com_google_android_gms_internal_zzauu_zzb.zzbws);
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("event_filters", null, contentValues, 5) == -1) {
                    zzKl().zzLZ().zzj("Failed to insert event filter (got -1). appId", zzatx.zzfE(str));
                }
                return true;
            } catch (SQLiteException e) {
                zzKl().zzLZ().zze("Error storing event filter. appId", zzatx.zzfE(str), e);
                return false;
            }
        } catch (IOException e2) {
            zzKl().zzLZ().zze("Configuration loss. Failed to serialize event filter. appId", zzatx.zzfE(str), e2);
            return false;
        }
    }

    @WorkerThread
    private boolean zza(String str, int i, zzauu.zze com_google_android_gms_internal_zzauu_zze) {
        zzob();
        zzmR();
        zzac.zzdr(str);
        zzac.zzw(com_google_android_gms_internal_zzauu_zze);
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzauu_zze.zzbwH)) {
            zzKl().zzMb().zzd("Property filter had no property name. Audience definition ignored. appId, audienceId, filterId", zzatx.zzfE(str), Integer.valueOf(i), String.valueOf(com_google_android_gms_internal_zzauu_zze.zzbwr));
            return false;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzauu_zze.zzafB()];
            zzbyc zzah = zzbyc.zzah(bArr);
            com_google_android_gms_internal_zzauu_zze.zza(zzah);
            zzah.zzafo();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("filter_id", com_google_android_gms_internal_zzauu_zze.zzbwr);
            contentValues.put("property_name", com_google_android_gms_internal_zzauu_zze.zzbwH);
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("property_filters", null, contentValues, 5) != -1) {
                    return true;
                }
                zzKl().zzLZ().zzj("Failed to insert property filter (got -1). appId", zzatx.zzfE(str));
                return false;
            } catch (SQLiteException e) {
                zzKl().zzLZ().zze("Error storing property filter. appId", zzatx.zzfE(str), e);
                return false;
            }
        } catch (IOException e2) {
            zzKl().zzLZ().zze("Configuration loss. Failed to serialize property filter. appId", zzatx.zzfE(str), e2);
            return false;
        }
    }

    @WorkerThread
    private long zzb(String str, String[] strArr) {
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
            zzKl().zzLZ().zze("Database error", str, e);
            throw e;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @WorkerThread
    static Set<String> zzb(SQLiteDatabase sQLiteDatabase, String str) {
        Set<String> hashSet = new HashSet();
        Cursor rawQuery = sQLiteDatabase.rawQuery(new StringBuilder(String.valueOf(str).length() + 22).append("SELECT * FROM ").append(str).append(" LIMIT 0").toString(), null);
        try {
            Collections.addAll(hashSet, rawQuery.getColumnNames());
            return hashSet;
        } finally {
            rawQuery.close();
        }
    }

    @WorkerThread
    public void beginTransaction() {
        zzob();
        getWritableDatabase().beginTransaction();
    }

    @WorkerThread
    public void endTransaction() {
        zzob();
        getWritableDatabase().endTransaction();
    }

    @WorkerThread
    SQLiteDatabase getWritableDatabase() {
        zzmR();
        try {
            return this.zzbrm.getWritableDatabase();
        } catch (SQLiteException e) {
            zzKl().zzMb().zzj("Error opening database", e);
            throw e;
        }
    }

    @WorkerThread
    public void setTransactionSuccessful() {
        zzob();
        getWritableDatabase().setTransactionSuccessful();
    }

    @WorkerThread
    void zzA(String str, int i) {
        zzob();
        zzmR();
        zzac.zzdr(str);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete("property_filters", "app_id=? and audience_id=?", new String[]{str, String.valueOf(i)});
        writableDatabase.delete("event_filters", "app_id=? and audience_id=?", new String[]{str, String.valueOf(i)});
    }

    public void zzJ(List<Long> list) {
        zzac.zzw(list);
        zzmR();
        zzob();
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
            zzKl().zzLZ().zze("Deleted fewer rows from raw events table than expected", Integer.valueOf(delete), Integer.valueOf(list.size()));
        }
    }

    @WorkerThread
    public String zzLE() {
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
                    zzKl().zzLZ().zzj("Database error getting next bundle app id", e);
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
            zzKl().zzLZ().zzj("Database error getting next bundle app id", e);
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

    public boolean zzLF() {
        return zzb("select count(1) > 0 from queue where has_realtime = 1", null) != 0;
    }

    @WorkerThread
    void zzLG() {
        zzmR();
        zzob();
        if (zzLN()) {
            long j = zzKm().zzbte.get();
            long elapsedRealtime = zznR().elapsedRealtime();
            if (Math.abs(elapsedRealtime - j) > zzKn().zzLl()) {
                zzKm().zzbte.set(elapsedRealtime);
                zzLH();
            }
        }
    }

    @WorkerThread
    void zzLH() {
        zzmR();
        zzob();
        if (zzLN()) {
            int delete = getWritableDatabase().delete("queue", "abs(bundle_end_timestamp - ?) > cast(? as integer)", new String[]{String.valueOf(zznR().currentTimeMillis()), String.valueOf(zzKn().zzLk())});
            if (delete > 0) {
                zzKl().zzMf().zzj("Deleted stale rows. rowsDeleted", Integer.valueOf(delete));
            }
        }
    }

    @WorkerThread
    public long zzLI() {
        return zza("select max(bundle_end_timestamp) from queue", null, 0);
    }

    @WorkerThread
    public long zzLJ() {
        return zza("select max(timestamp) from raw_events", null, 0);
    }

    public boolean zzLK() {
        return zzb("select count(1) > 0 from raw_events", null) != 0;
    }

    public boolean zzLL() {
        return zzb("select count(1) > 0 from raw_events where realtime = 1", null) != 0;
    }

    public long zzLM() {
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
            zzKl().zzLZ().zzj("Error querying raw events", e);
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

    @WorkerThread
    public zzatn zzQ(String str, String str2) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzac.zzdr(str);
        zzac.zzdr(str2);
        zzmR();
        zzob();
        try {
            Cursor query = getWritableDatabase().query("events", new String[]{"lifetime_count", "current_bundle_count", "last_fire_timestamp"}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zzatn com_google_android_gms_internal_zzatn = new zzatn(str, str2, query.getLong(0), query.getLong(1), query.getLong(2));
                    if (query.moveToNext()) {
                        zzKl().zzLZ().zzj("Got multiple records for event aggregates, expected one. appId", zzatx.zzfE(str));
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzatn;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzatn;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
                try {
                    zzKl().zzLZ().zzd("Error querying events. appId", zzatx.zzfE(str), str2, e);
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
            zzKl().zzLZ().zzd("Error querying events. appId", zzatx.zzfE(str), str2, e);
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
    public void zzR(String str, String str2) {
        zzac.zzdr(str);
        zzac.zzdr(str2);
        zzmR();
        zzob();
        try {
            zzKl().zzMf().zzj("Deleted user attribute rows", Integer.valueOf(getWritableDatabase().delete("user_attributes", "app_id=? and name=?", new String[]{str, str2})));
        } catch (SQLiteException e) {
            zzKl().zzLZ().zzd("Error deleting user attribute. appId", zzatx.zzfE(str), str2, e);
        }
    }

    @WorkerThread
    public zzaus zzS(String str, String str2) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzac.zzdr(str);
        zzac.zzdr(str2);
        zzmR();
        zzob();
        try {
            Cursor query = getWritableDatabase().query("user_attributes", new String[]{"triggered_timestamp", Param.VALUE, "origin"}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    String str3 = str;
                    zzaus com_google_android_gms_internal_zzaus = new zzaus(str3, query.getString(2), str2, query.getLong(0), zzb(query, 1));
                    if (query.moveToNext()) {
                        zzKl().zzLZ().zzj("Got multiple records for user property, expected one. appId", zzatx.zzfE(str));
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzaus;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzaus;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
                try {
                    zzKl().zzLZ().zzd("Error querying user property. appId", zzatx.zzfE(str), str2, e);
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
            zzKl().zzLZ().zzd("Error querying user property. appId", zzatx.zzfE(str), str2, e);
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
    public zzatg zzT(String str, String str2) {
        Cursor query;
        Object e;
        Cursor cursor;
        Throwable th;
        zzac.zzdr(str);
        zzac.zzdr(str2);
        zzmR();
        zzob();
        try {
            query = getWritableDatabase().query("conditional_properties", new String[]{"origin", Param.VALUE, "active", "trigger_event_name", "trigger_timeout", "timed_out_event", "creation_timestamp", "triggered_event", "triggered_timestamp", "time_to_live", "expired_event"}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    String string = query.getString(0);
                    Object zzb = zzb(query, 1);
                    boolean z = query.getInt(2) != 0;
                    String string2 = query.getString(3);
                    long j = query.getLong(4);
                    zzatq com_google_android_gms_internal_zzatq = (zzatq) zzKh().zzb(query.getBlob(5), zzatq.CREATOR);
                    long j2 = query.getLong(6);
                    zzatq com_google_android_gms_internal_zzatq2 = (zzatq) zzKh().zzb(query.getBlob(7), zzatq.CREATOR);
                    long j3 = query.getLong(8);
                    zzatg com_google_android_gms_internal_zzatg = new zzatg(str, string, new zzauq(str2, j3, zzb, string), j2, z, string2, com_google_android_gms_internal_zzatq, j, com_google_android_gms_internal_zzatq2, query.getLong(9), (zzatq) zzKh().zzb(query.getBlob(10), zzatq.CREATOR));
                    if (query.moveToNext()) {
                        zzKl().zzLZ().zze("Got multiple records for conditional property, expected one", zzatx.zzfE(str), str2);
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzatg;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzatg;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
                try {
                    zzKl().zzLZ().zzd("Error querying conditional property", zzatx.zzfE(str), str2, e);
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
            zzKl().zzLZ().zzd("Error querying conditional property", zzatx.zzfE(str), str2, e);
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
    public int zzU(String str, String str2) {
        int i = 0;
        zzac.zzdr(str);
        zzac.zzdr(str2);
        zzmR();
        zzob();
        try {
            i = getWritableDatabase().delete("conditional_properties", "app_id=? and name=?", new String[]{str, str2});
        } catch (SQLiteException e) {
            zzKl().zzLZ().zzd("Error deleting conditional property", zzatx.zzfE(str), str2, e);
        }
        return i;
    }

    Map<Integer, List<com.google.android.gms.internal.zzauu.zzb>> zzV(String str, String str2) {
        Object e;
        Throwable th;
        zzob();
        zzmR();
        zzac.zzdr(str);
        zzac.zzdr(str2);
        Map<Integer, List<com.google.android.gms.internal.zzauu.zzb>> arrayMap = new ArrayMap();
        Cursor query;
        try {
            query = getWritableDatabase().query("event_filters", new String[]{"audience_id", "data"}, "app_id=? AND event_name=?", new String[]{str, str2}, null, null, null);
            if (query.moveToFirst()) {
                do {
                    zzbyb zzag = zzbyb.zzag(query.getBlob(1));
                    com.google.android.gms.internal.zzauu.zzb com_google_android_gms_internal_zzauu_zzb = new com.google.android.gms.internal.zzauu.zzb();
                    try {
                        com_google_android_gms_internal_zzauu_zzb.zzb(zzag);
                        int i = query.getInt(0);
                        List list = (List) arrayMap.get(Integer.valueOf(i));
                        if (list == null) {
                            list = new ArrayList();
                            arrayMap.put(Integer.valueOf(i), list);
                        }
                        list.add(com_google_android_gms_internal_zzauu_zzb);
                    } catch (IOException e2) {
                        try {
                            zzKl().zzLZ().zze("Failed to merge filter. appId", zzatx.zzfE(str), e2);
                        } catch (SQLiteException e3) {
                            e = e3;
                        }
                    }
                } while (query.moveToNext());
                if (query != null) {
                    query.close();
                }
                return arrayMap;
            }
            Map<Integer, List<com.google.android.gms.internal.zzauu.zzb>> emptyMap = Collections.emptyMap();
            if (query == null) {
                return emptyMap;
            }
            query.close();
            return emptyMap;
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            try {
                zzKl().zzLZ().zze("Database error querying filters. appId", zzatx.zzfE(str), e);
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

    Map<Integer, List<zzauu.zze>> zzW(String str, String str2) {
        Cursor query;
        Object e;
        Throwable th;
        zzob();
        zzmR();
        zzac.zzdr(str);
        zzac.zzdr(str2);
        Map<Integer, List<zzauu.zze>> arrayMap = new ArrayMap();
        try {
            query = getWritableDatabase().query("property_filters", new String[]{"audience_id", "data"}, "app_id=? AND property_name=?", new String[]{str, str2}, null, null, null);
            if (query.moveToFirst()) {
                do {
                    zzbyb zzag = zzbyb.zzag(query.getBlob(1));
                    zzauu.zze com_google_android_gms_internal_zzauu_zze = new zzauu.zze();
                    try {
                        com_google_android_gms_internal_zzauu_zze.zzb(zzag);
                        int i = query.getInt(0);
                        List list = (List) arrayMap.get(Integer.valueOf(i));
                        if (list == null) {
                            list = new ArrayList();
                            arrayMap.put(Integer.valueOf(i), list);
                        }
                        list.add(com_google_android_gms_internal_zzauu_zze);
                    } catch (IOException e2) {
                        try {
                            zzKl().zzLZ().zze("Failed to merge filter", zzatx.zzfE(str), e2);
                        } catch (SQLiteException e3) {
                            e = e3;
                        }
                    }
                } while (query.moveToNext());
                if (query != null) {
                    query.close();
                }
                return arrayMap;
            }
            Map<Integer, List<zzauu.zze>> emptyMap = Collections.emptyMap();
            if (query == null) {
                return emptyMap;
            }
            query.close();
            return emptyMap;
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            try {
                zzKl().zzLZ().zze("Database error querying filters. appId", zzatx.zzfE(str), e);
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
    protected long zzX(String str, String str2) {
        long zza;
        Object e;
        zzac.zzdr(str);
        zzac.zzdr(str2);
        zzmR();
        zzob();
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
                    zzKl().zzLZ().zze("Failed to insert column (got -1). appId", zzatx.zzfE(str), str2);
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
                    zzKl().zzLZ().zze("Failed to update column (got 0). appId", zzatx.zzfE(str), str2);
                    writableDatabase.endTransaction();
                    return -1;
                }
                writableDatabase.setTransactionSuccessful();
                writableDatabase.endTransaction();
                return zza;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzKl().zzLZ().zzd("Error inserting column. appId", zzatx.zzfE(str), str2, e);
                    return zza;
                } finally {
                    writableDatabase.endTransaction();
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            zza = 0;
            zzKl().zzLZ().zzd("Error inserting column. appId", zzatx.zzfE(str), str2, e);
            return zza;
        }
    }

    public long zza(zze com_google_android_gms_internal_zzauw_zze) throws IOException {
        zzmR();
        zzob();
        zzac.zzw(com_google_android_gms_internal_zzauw_zze);
        zzac.zzdr(com_google_android_gms_internal_zzauw_zze.zzaS);
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzauw_zze.zzafB()];
            zzbyc zzah = zzbyc.zzah(bArr);
            com_google_android_gms_internal_zzauw_zze.zza(zzah);
            zzah.zzafo();
            long zzz = zzKh().zzz(bArr);
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzauw_zze.zzaS);
            contentValues.put("metadata_fingerprint", Long.valueOf(zzz));
            contentValues.put(TtmlNode.TAG_METADATA, bArr);
            try {
                getWritableDatabase().insertWithOnConflict("raw_events_metadata", null, contentValues, 4);
                return zzz;
            } catch (SQLiteException e) {
                zzKl().zzLZ().zze("Error storing raw event metadata. appId", zzatx.zzfE(com_google_android_gms_internal_zzauw_zze.zzaS), e);
                throw e;
            }
        } catch (IOException e2) {
            zzKl().zzLZ().zze("Data loss. Failed to serialize event metadata. appId", zzatx.zzfE(com_google_android_gms_internal_zzauw_zze.zzaS), e2);
            throw e2;
        }
    }

    @WorkerThread
    public zza zza(long j, String str, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        Cursor query;
        Object e;
        Throwable th;
        zzac.zzdr(str);
        zzmR();
        zzob();
        String[] strArr = new String[]{str};
        zza com_google_android_gms_internal_zzatj_zza = new zza();
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            query = writableDatabase.query("apps", new String[]{"day", "daily_events_count", "daily_public_events_count", "daily_conversions_count", "daily_error_events_count", "daily_realtime_events_count"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    if (query.getLong(0) == j) {
                        com_google_android_gms_internal_zzatj_zza.zzbrp = query.getLong(1);
                        com_google_android_gms_internal_zzatj_zza.zzbro = query.getLong(2);
                        com_google_android_gms_internal_zzatj_zza.zzbrq = query.getLong(3);
                        com_google_android_gms_internal_zzatj_zza.zzbrr = query.getLong(4);
                        com_google_android_gms_internal_zzatj_zza.zzbrs = query.getLong(5);
                    }
                    if (z) {
                        com_google_android_gms_internal_zzatj_zza.zzbrp++;
                    }
                    if (z2) {
                        com_google_android_gms_internal_zzatj_zza.zzbro++;
                    }
                    if (z3) {
                        com_google_android_gms_internal_zzatj_zza.zzbrq++;
                    }
                    if (z4) {
                        com_google_android_gms_internal_zzatj_zza.zzbrr++;
                    }
                    if (z5) {
                        com_google_android_gms_internal_zzatj_zza.zzbrs++;
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("day", Long.valueOf(j));
                    contentValues.put("daily_public_events_count", Long.valueOf(com_google_android_gms_internal_zzatj_zza.zzbro));
                    contentValues.put("daily_events_count", Long.valueOf(com_google_android_gms_internal_zzatj_zza.zzbrp));
                    contentValues.put("daily_conversions_count", Long.valueOf(com_google_android_gms_internal_zzatj_zza.zzbrq));
                    contentValues.put("daily_error_events_count", Long.valueOf(com_google_android_gms_internal_zzatj_zza.zzbrr));
                    contentValues.put("daily_realtime_events_count", Long.valueOf(com_google_android_gms_internal_zzatj_zza.zzbrs));
                    writableDatabase.update("apps", contentValues, "app_id=?", strArr);
                    if (query != null) {
                        query.close();
                    }
                    return com_google_android_gms_internal_zzatj_zza;
                }
                zzKl().zzMb().zzj("Not updating daily counts, app is not known. appId", zzatx.zzfE(str));
                if (query != null) {
                    query.close();
                }
                return com_google_android_gms_internal_zzatj_zza;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzKl().zzLZ().zze("Error updating daily counts. appId", zzatx.zzfE(str), e);
                    if (query != null) {
                        query.close();
                    }
                    return com_google_android_gms_internal_zzatj_zza;
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
            zzKl().zzLZ().zze("Error updating daily counts. appId", zzatx.zzfE(str), e);
            if (query != null) {
                query.close();
            }
            return com_google_android_gms_internal_zzatj_zza;
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
    void zza(ContentValues contentValues, String str, Object obj) {
        zzac.zzdr(str);
        zzac.zzw(obj);
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

    @WorkerThread
    public void zza(zzatc com_google_android_gms_internal_zzatc) {
        zzac.zzw(com_google_android_gms_internal_zzatc);
        zzmR();
        zzob();
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_internal_zzatc.zzke());
        contentValues.put("app_instance_id", com_google_android_gms_internal_zzatc.getAppInstanceId());
        contentValues.put("gmp_app_id", com_google_android_gms_internal_zzatc.getGmpAppId());
        contentValues.put("resettable_device_id_hash", com_google_android_gms_internal_zzatc.zzKp());
        contentValues.put("last_bundle_index", Long.valueOf(com_google_android_gms_internal_zzatc.zzKy()));
        contentValues.put("last_bundle_start_timestamp", Long.valueOf(com_google_android_gms_internal_zzatc.zzKr()));
        contentValues.put("last_bundle_end_timestamp", Long.valueOf(com_google_android_gms_internal_zzatc.zzKs()));
        contentValues.put("app_version", com_google_android_gms_internal_zzatc.zzmZ());
        contentValues.put("app_store", com_google_android_gms_internal_zzatc.zzKu());
        contentValues.put("gmp_version", Long.valueOf(com_google_android_gms_internal_zzatc.zzKv()));
        contentValues.put("dev_cert_hash", Long.valueOf(com_google_android_gms_internal_zzatc.zzKw()));
        contentValues.put("measurement_enabled", Boolean.valueOf(com_google_android_gms_internal_zzatc.zzKx()));
        contentValues.put("day", Long.valueOf(com_google_android_gms_internal_zzatc.zzKC()));
        contentValues.put("daily_public_events_count", Long.valueOf(com_google_android_gms_internal_zzatc.zzKD()));
        contentValues.put("daily_events_count", Long.valueOf(com_google_android_gms_internal_zzatc.zzKE()));
        contentValues.put("daily_conversions_count", Long.valueOf(com_google_android_gms_internal_zzatc.zzKF()));
        contentValues.put("config_fetched_time", Long.valueOf(com_google_android_gms_internal_zzatc.zzKz()));
        contentValues.put("failed_config_fetch_time", Long.valueOf(com_google_android_gms_internal_zzatc.zzKA()));
        contentValues.put("app_version_int", Long.valueOf(com_google_android_gms_internal_zzatc.zzKt()));
        contentValues.put("firebase_instance_id", com_google_android_gms_internal_zzatc.zzKq());
        contentValues.put("daily_error_events_count", Long.valueOf(com_google_android_gms_internal_zzatc.zzKH()));
        contentValues.put("daily_realtime_events_count", Long.valueOf(com_google_android_gms_internal_zzatc.zzKG()));
        contentValues.put("health_monitor_sample", com_google_android_gms_internal_zzatc.zzKI());
        contentValues.put("android_id", Long.valueOf(com_google_android_gms_internal_zzatc.zzuW()));
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            if (((long) writableDatabase.update("apps", contentValues, "app_id = ?", new String[]{com_google_android_gms_internal_zzatc.zzke()})) == 0 && writableDatabase.insertWithOnConflict("apps", null, contentValues, 5) == -1) {
                zzKl().zzLZ().zzj("Failed to insert/update app (got -1). appId", zzatx.zzfE(com_google_android_gms_internal_zzatc.zzke()));
            }
        } catch (SQLiteException e) {
            zzKl().zzLZ().zze("Error storing app. appId", zzatx.zzfE(com_google_android_gms_internal_zzatc.zzke()), e);
        }
    }

    @WorkerThread
    public void zza(zzatn com_google_android_gms_internal_zzatn) {
        zzac.zzw(com_google_android_gms_internal_zzatn);
        zzmR();
        zzob();
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_internal_zzatn.mAppId);
        contentValues.put("name", com_google_android_gms_internal_zzatn.mName);
        contentValues.put("lifetime_count", Long.valueOf(com_google_android_gms_internal_zzatn.zzbrB));
        contentValues.put("current_bundle_count", Long.valueOf(com_google_android_gms_internal_zzatn.zzbrC));
        contentValues.put("last_fire_timestamp", Long.valueOf(com_google_android_gms_internal_zzatn.zzbrD));
        try {
            if (getWritableDatabase().insertWithOnConflict("events", null, contentValues, 5) == -1) {
                zzKl().zzLZ().zzj("Failed to insert/update event aggregates (got -1). appId", zzatx.zzfE(com_google_android_gms_internal_zzatn.mAppId));
            }
        } catch (SQLiteException e) {
            zzKl().zzLZ().zze("Error storing event aggregates. appId", zzatx.zzfE(com_google_android_gms_internal_zzatn.mAppId), e);
        }
    }

    void zza(String str, int i, zzf com_google_android_gms_internal_zzauw_zzf) {
        zzob();
        zzmR();
        zzac.zzdr(str);
        zzac.zzw(com_google_android_gms_internal_zzauw_zzf);
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzauw_zzf.zzafB()];
            zzbyc zzah = zzbyc.zzah(bArr);
            com_google_android_gms_internal_zzauw_zzf.zza(zzah);
            zzah.zzafo();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("current_results", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("audience_filter_values", null, contentValues, 5) == -1) {
                    zzKl().zzLZ().zzj("Failed to insert filter results (got -1). appId", zzatx.zzfE(str));
                }
            } catch (SQLiteException e) {
                zzKl().zzLZ().zze("Error storing filter results. appId", zzatx.zzfE(str), e);
            }
        } catch (IOException e2) {
            zzKl().zzLZ().zze("Configuration loss. Failed to serialize filter results. appId", zzatx.zzfE(str), e2);
        }
    }

    public void zza(String str, long j, long j2, zzb com_google_android_gms_internal_zzatj_zzb) {
        Object e;
        Throwable th;
        zzac.zzw(com_google_android_gms_internal_zzatj_zzb);
        zzmR();
        zzob();
        Cursor cursor = null;
        SQLiteDatabase writableDatabase = getWritableDatabase();
        String str2;
        if (TextUtils.isEmpty(str)) {
            String[] strArr = j2 != -1 ? new String[]{String.valueOf(j2), String.valueOf(j)} : new String[]{String.valueOf(j)};
            str2 = j2 != -1 ? "rowid <= ? and " : "";
            cursor = writableDatabase.rawQuery(new StringBuilder(String.valueOf(str2).length() + 148).append("select app_id, metadata_fingerprint from raw_events where ").append(str2).append("app_id in (select app_id from apps where config_fetched_time >= ?) order by rowid limit 1;").toString(), strArr);
            if (cursor.moveToFirst()) {
                str = cursor.getString(0);
                str2 = cursor.getString(1);
                cursor.close();
                String str3 = str2;
                Cursor cursor2 = cursor;
            } else if (cursor != null) {
                cursor.close();
                return;
            } else {
                return;
            }
        }
        strArr = j2 != -1 ? new String[]{str, String.valueOf(j2)} : new String[]{str};
        str2 = j2 != -1 ? " and rowid <= ?" : "";
        cursor = writableDatabase.rawQuery(new StringBuilder(String.valueOf(str2).length() + 84).append("select metadata_fingerprint from raw_events where app_id = ?").append(str2).append(" order by rowid limit 1;").toString(), strArr);
        if (cursor.moveToFirst()) {
            str2 = cursor.getString(0);
            cursor.close();
            str3 = str2;
            cursor2 = cursor;
        } else if (cursor != null) {
            cursor.close();
            return;
        } else {
            return;
        }
        try {
            cursor2 = writableDatabase.query("raw_events_metadata", new String[]{TtmlNode.TAG_METADATA}, "app_id = ? and metadata_fingerprint = ?", new String[]{str, str3}, null, null, "rowid", "2");
            if (cursor2.moveToFirst()) {
                zzbyb zzag = zzbyb.zzag(cursor2.getBlob(0));
                zze com_google_android_gms_internal_zzauw_zze = new zze();
                try {
                    String str4;
                    String[] strArr2;
                    com_google_android_gms_internal_zzauw_zze.zzb(zzag);
                    if (cursor2.moveToNext()) {
                        zzKl().zzMb().zzj("Get multiple raw event metadata records, expected one. appId", zzatx.zzfE(str));
                    }
                    cursor2.close();
                    com_google_android_gms_internal_zzatj_zzb.zzb(com_google_android_gms_internal_zzauw_zze);
                    if (j2 != -1) {
                        str4 = "app_id = ? and metadata_fingerprint = ? and rowid <= ?";
                        strArr2 = new String[]{str, str3, String.valueOf(j2)};
                    } else {
                        str4 = "app_id = ? and metadata_fingerprint = ?";
                        strArr2 = new String[]{str, str3};
                    }
                    cursor = writableDatabase.query("raw_events", new String[]{"rowid", "name", "timestamp", "data"}, str4, strArr2, null, null, "rowid", null);
                    if (cursor.moveToFirst()) {
                        do {
                            long j3 = cursor.getLong(0);
                            zzbyb zzag2 = zzbyb.zzag(cursor.getBlob(3));
                            com.google.android.gms.internal.zzauw.zzb com_google_android_gms_internal_zzauw_zzb = new com.google.android.gms.internal.zzauw.zzb();
                            try {
                                com_google_android_gms_internal_zzauw_zzb.zzb(zzag2);
                            } catch (IOException e2) {
                                zzKl().zzLZ().zze("Data loss. Failed to merge raw event. appId", zzatx.zzfE(str), e2);
                            }
                            try {
                                com_google_android_gms_internal_zzauw_zzb.name = cursor.getString(1);
                                com_google_android_gms_internal_zzauw_zzb.zzbxc = Long.valueOf(cursor.getLong(2));
                                if (!com_google_android_gms_internal_zzatj_zzb.zza(j3, com_google_android_gms_internal_zzauw_zzb)) {
                                    if (cursor != null) {
                                        cursor.close();
                                        return;
                                    }
                                    return;
                                }
                            } catch (SQLiteException e3) {
                                e = e3;
                            }
                        } while (cursor.moveToNext());
                        if (cursor != null) {
                            cursor.close();
                            return;
                        }
                        return;
                    }
                    zzKl().zzMb().zzj("Raw event data disappeared while in transaction. appId", zzatx.zzfE(str));
                    if (cursor != null) {
                        cursor.close();
                        return;
                    }
                    return;
                } catch (IOException e22) {
                    zzKl().zzLZ().zze("Data loss. Failed to merge raw event metadata. appId", zzatx.zzfE(str), e22);
                    if (cursor2 != null) {
                        cursor2.close();
                        return;
                    }
                    return;
                }
            }
            zzKl().zzLZ().zzj("Raw event metadata record is missing. appId", zzatx.zzfE(str));
            if (cursor2 != null) {
                cursor2.close();
            }
        } catch (SQLiteException e4) {
            e = e4;
            cursor = cursor2;
            try {
                zzKl().zzLZ().zze("Data loss. Error selecting raw event. appId", zzatx.zzfE(str), e);
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Throwable th2) {
                th = th2;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            cursor = cursor2;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    @WorkerThread
    public boolean zza(zzatg com_google_android_gms_internal_zzatg) {
        zzac.zzw(com_google_android_gms_internal_zzatg);
        zzmR();
        zzob();
        if (zzS(com_google_android_gms_internal_zzatg.packageName, com_google_android_gms_internal_zzatg.zzbqX.name) == null) {
            long zzb = zzb("SELECT COUNT(1) FROM conditional_properties WHERE app_id=?", new String[]{com_google_android_gms_internal_zzatg.packageName});
            zzKn().zzLa();
            if (zzb >= 1000) {
                return false;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_internal_zzatg.packageName);
        contentValues.put("origin", com_google_android_gms_internal_zzatg.zzbqW);
        contentValues.put("name", com_google_android_gms_internal_zzatg.zzbqX.name);
        zza(contentValues, Param.VALUE, com_google_android_gms_internal_zzatg.zzbqX.getValue());
        contentValues.put("active", Boolean.valueOf(com_google_android_gms_internal_zzatg.zzbqZ));
        contentValues.put("trigger_event_name", com_google_android_gms_internal_zzatg.zzbra);
        contentValues.put("trigger_timeout", Long.valueOf(com_google_android_gms_internal_zzatg.zzbrc));
        contentValues.put("timed_out_event", zzKh().zza(com_google_android_gms_internal_zzatg.zzbrb));
        contentValues.put("creation_timestamp", Long.valueOf(com_google_android_gms_internal_zzatg.zzbqY));
        contentValues.put("triggered_event", zzKh().zza(com_google_android_gms_internal_zzatg.zzbrd));
        contentValues.put("triggered_timestamp", Long.valueOf(com_google_android_gms_internal_zzatg.zzbqX.zzbwf));
        contentValues.put("time_to_live", Long.valueOf(com_google_android_gms_internal_zzatg.zzbre));
        contentValues.put("expired_event", zzKh().zza(com_google_android_gms_internal_zzatg.zzbrf));
        try {
            if (getWritableDatabase().insertWithOnConflict("conditional_properties", null, contentValues, 5) == -1) {
                zzKl().zzLZ().zzj("Failed to insert/update conditional user property (got -1)", zzatx.zzfE(com_google_android_gms_internal_zzatg.packageName));
            }
        } catch (SQLiteException e) {
            zzKl().zzLZ().zze("Error storing conditional user property", zzatx.zzfE(com_google_android_gms_internal_zzatg.packageName), e);
        }
        return true;
    }

    public boolean zza(zzatm com_google_android_gms_internal_zzatm, long j, boolean z) {
        zzmR();
        zzob();
        zzac.zzw(com_google_android_gms_internal_zzatm);
        zzac.zzdr(com_google_android_gms_internal_zzatm.mAppId);
        com.google.android.gms.internal.zzauw.zzb com_google_android_gms_internal_zzauw_zzb = new com.google.android.gms.internal.zzauw.zzb();
        com_google_android_gms_internal_zzauw_zzb.zzbxd = Long.valueOf(com_google_android_gms_internal_zzatm.zzbrz);
        com_google_android_gms_internal_zzauw_zzb.zzbxb = new com.google.android.gms.internal.zzauw.zzc[com_google_android_gms_internal_zzatm.zzbrA.size()];
        Iterator it = com_google_android_gms_internal_zzatm.zzbrA.iterator();
        int i = 0;
        while (it.hasNext()) {
            String str = (String) it.next();
            com.google.android.gms.internal.zzauw.zzc com_google_android_gms_internal_zzauw_zzc = new com.google.android.gms.internal.zzauw.zzc();
            int i2 = i + 1;
            com_google_android_gms_internal_zzauw_zzb.zzbxb[i] = com_google_android_gms_internal_zzauw_zzc;
            com_google_android_gms_internal_zzauw_zzc.name = str;
            zzKh().zza(com_google_android_gms_internal_zzauw_zzc, com_google_android_gms_internal_zzatm.zzbrA.get(str));
            i = i2;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzauw_zzb.zzafB()];
            zzbyc zzah = zzbyc.zzah(bArr);
            com_google_android_gms_internal_zzauw_zzb.zza(zzah);
            zzah.zzafo();
            zzKl().zzMf().zze("Saving event, name, data size", com_google_android_gms_internal_zzatm.mName, Integer.valueOf(bArr.length));
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzatm.mAppId);
            contentValues.put("name", com_google_android_gms_internal_zzatm.mName);
            contentValues.put("timestamp", Long.valueOf(com_google_android_gms_internal_zzatm.zzaxb));
            contentValues.put("metadata_fingerprint", Long.valueOf(j));
            contentValues.put("data", bArr);
            contentValues.put("realtime", Integer.valueOf(z ? 1 : 0));
            try {
                if (getWritableDatabase().insert("raw_events", null, contentValues) != -1) {
                    return true;
                }
                zzKl().zzLZ().zzj("Failed to insert raw event (got -1). appId", zzatx.zzfE(com_google_android_gms_internal_zzatm.mAppId));
                return false;
            } catch (SQLiteException e) {
                zzKl().zzLZ().zze("Error storing raw event. appId", zzatx.zzfE(com_google_android_gms_internal_zzatm.mAppId), e);
                return false;
            }
        } catch (IOException e2) {
            zzKl().zzLZ().zze("Data loss. Failed to serialize event params/data. appId", zzatx.zzfE(com_google_android_gms_internal_zzatm.mAppId), e2);
            return false;
        }
    }

    @WorkerThread
    public boolean zza(zzaus com_google_android_gms_internal_zzaus) {
        zzac.zzw(com_google_android_gms_internal_zzaus);
        zzmR();
        zzob();
        if (zzS(com_google_android_gms_internal_zzaus.mAppId, com_google_android_gms_internal_zzaus.mName) == null) {
            long zzb;
            if (zzaut.zzfT(com_google_android_gms_internal_zzaus.mName)) {
                zzb = zzb("select count(1) from user_attributes where app_id=? and name not like '!_%' escape '!'", new String[]{com_google_android_gms_internal_zzaus.mAppId});
                zzKn().zzKX();
                if (zzb >= 25) {
                    return false;
                }
            }
            zzb = zzb("select count(1) from user_attributes where app_id=? and origin=? AND name like '!_%' escape '!'", new String[]{com_google_android_gms_internal_zzaus.mAppId, com_google_android_gms_internal_zzaus.mOrigin});
            zzKn().zzKZ();
            if (zzb >= 25) {
                return false;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_internal_zzaus.mAppId);
        contentValues.put("origin", com_google_android_gms_internal_zzaus.mOrigin);
        contentValues.put("name", com_google_android_gms_internal_zzaus.mName);
        contentValues.put("triggered_timestamp", Long.valueOf(com_google_android_gms_internal_zzaus.zzbwj));
        zza(contentValues, Param.VALUE, com_google_android_gms_internal_zzaus.mValue);
        try {
            if (getWritableDatabase().insertWithOnConflict("user_attributes", null, contentValues, 5) == -1) {
                zzKl().zzLZ().zzj("Failed to insert/update user property (got -1). appId", zzatx.zzfE(com_google_android_gms_internal_zzaus.mAppId));
            }
        } catch (SQLiteException e) {
            zzKl().zzLZ().zze("Error storing user property. appId", zzatx.zzfE(com_google_android_gms_internal_zzaus.mAppId), e);
        }
        return true;
    }

    @WorkerThread
    public boolean zza(zze com_google_android_gms_internal_zzauw_zze, boolean z) {
        zzmR();
        zzob();
        zzac.zzw(com_google_android_gms_internal_zzauw_zze);
        zzac.zzdr(com_google_android_gms_internal_zzauw_zze.zzaS);
        zzac.zzw(com_google_android_gms_internal_zzauw_zze.zzbxn);
        zzLG();
        long currentTimeMillis = zznR().currentTimeMillis();
        if (com_google_android_gms_internal_zzauw_zze.zzbxn.longValue() < currentTimeMillis - zzKn().zzLk() || com_google_android_gms_internal_zzauw_zze.zzbxn.longValue() > zzKn().zzLk() + currentTimeMillis) {
            zzKl().zzMb().zzd("Storing bundle outside of the max uploading time span. appId, now, timestamp", zzatx.zzfE(com_google_android_gms_internal_zzauw_zze.zzaS), Long.valueOf(currentTimeMillis), com_google_android_gms_internal_zzauw_zze.zzbxn);
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzauw_zze.zzafB()];
            zzbyc zzah = zzbyc.zzah(bArr);
            com_google_android_gms_internal_zzauw_zze.zza(zzah);
            zzah.zzafo();
            bArr = zzKh().zzk(bArr);
            zzKl().zzMf().zzj("Saving bundle, size", Integer.valueOf(bArr.length));
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzauw_zze.zzaS);
            contentValues.put("bundle_end_timestamp", com_google_android_gms_internal_zzauw_zze.zzbxn);
            contentValues.put("data", bArr);
            contentValues.put("has_realtime", Integer.valueOf(z ? 1 : 0));
            try {
                if (getWritableDatabase().insert("queue", null, contentValues) != -1) {
                    return true;
                }
                zzKl().zzLZ().zzj("Failed to insert bundle (got -1). appId", zzatx.zzfE(com_google_android_gms_internal_zzauw_zze.zzaS));
                return false;
            } catch (SQLiteException e) {
                zzKl().zzLZ().zze("Error storing bundle. appId", zzatx.zzfE(com_google_android_gms_internal_zzauw_zze.zzaS), e);
                return false;
            }
        } catch (IOException e2) {
            zzKl().zzLZ().zze("Data loss. Failed to serialize bundle. appId", zzatx.zzfE(com_google_android_gms_internal_zzauw_zze.zzaS), e2);
            return false;
        }
    }

    @WorkerThread
    public void zzan(long j) {
        zzmR();
        zzob();
        try {
            if (getWritableDatabase().delete("queue", "rowid=?", new String[]{String.valueOf(j)}) != 1) {
                throw new SQLiteException("Deleted fewer rows from queue than expected");
            }
        } catch (SQLiteException e) {
            zzKl().zzLZ().zzj("Failed to delete a bundle in a queue table", e);
            throw e;
        }
    }

    public String zzao(long j) {
        Object e;
        Throwable th;
        String str = null;
        zzmR();
        zzob();
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
                    zzKl().zzMf().log("No expired configs for apps with pending events");
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                }
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzKl().zzLZ().zzj("Error selecting expired configs", e);
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
            zzKl().zzLZ().zzj("Error selecting expired configs", e);
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

    @WorkerThread
    Object zzb(Cursor cursor, int i) {
        int zza = zza(cursor, i);
        switch (zza) {
            case 0:
                zzKl().zzLZ().log("Loaded invalid null value from database");
                return null;
            case 1:
                return Long.valueOf(cursor.getLong(i));
            case 2:
                return Double.valueOf(cursor.getDouble(i));
            case 3:
                return cursor.getString(i);
            case 4:
                zzKl().zzLZ().log("Loaded invalid blob type value, ignoring it");
                return null;
            default:
                zzKl().zzLZ().zzj("Loaded invalid unknown value type, ignoring it", Integer.valueOf(zza));
                return null;
        }
    }

    @WorkerThread
    void zzb(String str, com.google.android.gms.internal.zzauu.zza[] com_google_android_gms_internal_zzauu_zzaArr) {
        int i = 0;
        zzob();
        zzmR();
        zzac.zzdr(str);
        zzac.zzw(com_google_android_gms_internal_zzauu_zzaArr);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            zzfx(str);
            for (com.google.android.gms.internal.zzauu.zza zza : com_google_android_gms_internal_zzauu_zzaArr) {
                zza(str, zza);
            }
            List arrayList = new ArrayList();
            int length = com_google_android_gms_internal_zzauu_zzaArr.length;
            while (i < length) {
                arrayList.add(com_google_android_gms_internal_zzauu_zzaArr[i].zzbwn);
                i++;
            }
            zzd(str, arrayList);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    @WorkerThread
    public List<zzatg> zzc(String str, String str2, long j) {
        zzac.zzdr(str);
        zzac.zzdr(str2);
        zzmR();
        zzob();
        if (j < 0) {
            zzKl().zzMb().zzd("Invalid time querying triggered conditional properties", zzatx.zzfE(str), str2, Long.valueOf(j));
            return Collections.emptyList();
        }
        return zzc("active=0 and app_id=? and trigger_event_name=? and abs(? - creation_timestamp) <= trigger_timeout", new String[]{str, str2, String.valueOf(j)});
    }

    public List<zzatg> zzc(String str, String[] strArr) {
        Object e;
        Cursor cursor;
        Throwable th;
        zzmR();
        zzob();
        List<zzatg> arrayList = new ArrayList();
        Cursor query;
        try {
            String[] strArr2 = new String[]{"app_id", "origin", "name", Param.VALUE, "active", "trigger_event_name", "trigger_timeout", "timed_out_event", "creation_timestamp", "triggered_event", "triggered_timestamp", "time_to_live", "expired_event"};
            zzKn().zzLa();
            query = getWritableDatabase().query("conditional_properties", strArr2, str, strArr, null, null, "rowid", String.valueOf(PointerIconCompat.TYPE_CONTEXT_MENU));
            try {
                if (query.moveToFirst()) {
                    do {
                        if (arrayList.size() >= zzKn().zzLa()) {
                            zzKl().zzLZ().zzj("Read more than the max allowed conditional properties, ignoring extra", Integer.valueOf(zzKn().zzLa()));
                            break;
                        }
                        String string = query.getString(0);
                        String string2 = query.getString(1);
                        String string3 = query.getString(2);
                        Object zzb = zzb(query, 3);
                        boolean z = query.getInt(4) != 0;
                        String string4 = query.getString(5);
                        long j = query.getLong(6);
                        zzatq com_google_android_gms_internal_zzatq = (zzatq) zzKh().zzb(query.getBlob(7), zzatq.CREATOR);
                        long j2 = query.getLong(8);
                        zzatq com_google_android_gms_internal_zzatq2 = (zzatq) zzKh().zzb(query.getBlob(9), zzatq.CREATOR);
                        long j3 = query.getLong(10);
                        List<zzatg> list = arrayList;
                        list.add(new zzatg(string, string2, new zzauq(string3, j3, zzb, string2), j2, z, string4, com_google_android_gms_internal_zzatq, j, com_google_android_gms_internal_zzatq2, query.getLong(11), (zzatq) zzKh().zzb(query.getBlob(12), zzatq.CREATOR)));
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
                zzKl().zzLZ().zzj("Error querying conditional user property value", e);
                List<zzatg> emptyList = Collections.emptyList();
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
    public void zzd(String str, byte[] bArr) {
        zzac.zzdr(str);
        zzmR();
        zzob();
        ContentValues contentValues = new ContentValues();
        contentValues.put("remote_config", bArr);
        try {
            if (((long) getWritableDatabase().update("apps", contentValues, "app_id = ?", new String[]{str})) == 0) {
                zzKl().zzLZ().zzj("Failed to update remote config (got 0). appId", zzatx.zzfE(str));
            }
        } catch (SQLiteException e) {
            zzKl().zzLZ().zze("Error storing remote config. appId", zzatx.zzfE(str), e);
        }
    }

    boolean zzd(String str, List<Integer> list) {
        zzac.zzdr(str);
        zzob();
        zzmR();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        try {
            if (zzb("select count(1) from audience_filter_values where app_id=?", new String[]{str}) <= ((long) zzKn().zzfo(str))) {
                return false;
            }
            Iterable arrayList = new ArrayList();
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    Integer num = (Integer) list.get(i);
                    if (num == null || !(num instanceof Integer)) {
                        return false;
                    }
                    arrayList.add(Integer.toString(num.intValue()));
                }
            }
            String valueOf = String.valueOf(TextUtils.join(",", arrayList));
            valueOf = new StringBuilder(String.valueOf(valueOf).length() + 2).append("(").append(valueOf).append(")").toString();
            return writableDatabase.delete("audience_filter_values", new StringBuilder(String.valueOf(valueOf).length() + 140).append("audience_id in (select audience_id from audience_filter_values where app_id=? and audience_id not in ").append(valueOf).append(" order by rowid desc limit -1 offset ?)").toString(), new String[]{str, Integer.toString(r5)}) > 0;
        } catch (SQLiteException e) {
            zzKl().zzLZ().zze("Database error querying filters. appId", zzatx.zzfE(str), e);
            return false;
        }
    }

    @WorkerThread
    public long zzfA(String str) {
        zzac.zzdr(str);
        zzmR();
        zzob();
        return zzX(str, "first_open_count");
    }

    public void zzfB(String str) {
        try {
            getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str, str});
        } catch (SQLiteException e) {
            zzKl().zzLZ().zze("Failed to remove unused event metadata. appId", zzatx.zzfE(str), e);
        }
    }

    public long zzfC(String str) {
        zzac.zzdr(str);
        return zza("select count(1) from events where app_id=? and name not like '!_%' escape '!'", new String[]{str}, 0);
    }

    @WorkerThread
    public List<zzaus> zzft(String str) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzac.zzdr(str);
        zzmR();
        zzob();
        List<zzaus> arrayList = new ArrayList();
        try {
            Cursor query = getWritableDatabase().query("user_attributes", new String[]{"name", "origin", "triggered_timestamp", Param.VALUE}, "app_id=?", new String[]{str}, null, null, "rowid", String.valueOf(zzKn().zzKY()));
            try {
                if (query.moveToFirst()) {
                    do {
                        String string = query.getString(0);
                        String string2 = query.getString(1);
                        if (string2 == null) {
                            string2 = "";
                        }
                        long j = query.getLong(2);
                        Object zzb = zzb(query, 3);
                        if (zzb == null) {
                            zzKl().zzLZ().zzj("Read invalid user property value, ignoring it. appId", zzatx.zzfE(str));
                        } else {
                            arrayList.add(new zzaus(str, string2, string, j, zzb));
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
                zzKl().zzLZ().zze("Error querying user properties. appId", zzatx.zzfE(str), e);
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
    public zzatc zzfu(String str) {
        Cursor query;
        Object e;
        Throwable th;
        zzac.zzdr(str);
        zzmR();
        zzob();
        try {
            query = getWritableDatabase().query("apps", new String[]{"app_instance_id", "gmp_app_id", "resettable_device_id_hash", "last_bundle_index", "last_bundle_start_timestamp", "last_bundle_end_timestamp", "app_version", "app_store", "gmp_version", "dev_cert_hash", "measurement_enabled", "day", "daily_public_events_count", "daily_events_count", "daily_conversions_count", "config_fetched_time", "failed_config_fetch_time", "app_version_int", "firebase_instance_id", "daily_error_events_count", "daily_realtime_events_count", "health_monitor_sample", "android_id"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zzatc com_google_android_gms_internal_zzatc = new zzatc(this.zzbqb, str);
                    com_google_android_gms_internal_zzatc.zzfd(query.getString(0));
                    com_google_android_gms_internal_zzatc.zzfe(query.getString(1));
                    com_google_android_gms_internal_zzatc.zzff(query.getString(2));
                    com_google_android_gms_internal_zzatc.zzad(query.getLong(3));
                    com_google_android_gms_internal_zzatc.zzY(query.getLong(4));
                    com_google_android_gms_internal_zzatc.zzZ(query.getLong(5));
                    com_google_android_gms_internal_zzatc.setAppVersion(query.getString(6));
                    com_google_android_gms_internal_zzatc.zzfh(query.getString(7));
                    com_google_android_gms_internal_zzatc.zzab(query.getLong(8));
                    com_google_android_gms_internal_zzatc.zzac(query.getLong(9));
                    com_google_android_gms_internal_zzatc.setMeasurementEnabled((query.isNull(10) ? 1 : query.getInt(10)) != 0);
                    com_google_android_gms_internal_zzatc.zzag(query.getLong(11));
                    com_google_android_gms_internal_zzatc.zzah(query.getLong(12));
                    com_google_android_gms_internal_zzatc.zzai(query.getLong(13));
                    com_google_android_gms_internal_zzatc.zzaj(query.getLong(14));
                    com_google_android_gms_internal_zzatc.zzae(query.getLong(15));
                    com_google_android_gms_internal_zzatc.zzaf(query.getLong(16));
                    com_google_android_gms_internal_zzatc.zzaa(query.isNull(17) ? -2147483648L : (long) query.getInt(17));
                    com_google_android_gms_internal_zzatc.zzfg(query.getString(18));
                    com_google_android_gms_internal_zzatc.zzal(query.getLong(19));
                    com_google_android_gms_internal_zzatc.zzak(query.getLong(20));
                    com_google_android_gms_internal_zzatc.zzfi(query.getString(21));
                    com_google_android_gms_internal_zzatc.zzam(query.isNull(22) ? 0 : query.getLong(22));
                    com_google_android_gms_internal_zzatc.zzKo();
                    if (query.moveToNext()) {
                        zzKl().zzLZ().zzj("Got multiple records for app, expected one. appId", zzatx.zzfE(str));
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzatc;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzatc;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzKl().zzLZ().zze("Error querying app. appId", zzatx.zzfE(str), e);
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
            zzKl().zzLZ().zze("Error querying app. appId", zzatx.zzfE(str), e);
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

    public long zzfv(String str) {
        zzac.zzdr(str);
        zzmR();
        zzob();
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            String valueOf = String.valueOf(zzKn().zzfs(str));
            return (long) writableDatabase.delete("raw_events", "rowid in (select rowid from raw_events where app_id=? order by rowid desc limit -1 offset ?)", new String[]{str, valueOf});
        } catch (SQLiteException e) {
            zzKl().zzLZ().zze("Error deleting over the limit events. appId", zzatx.zzfE(str), e);
            return 0;
        }
    }

    @WorkerThread
    public byte[] zzfw(String str) {
        Cursor query;
        Object e;
        Throwable th;
        zzac.zzdr(str);
        zzmR();
        zzob();
        try {
            query = getWritableDatabase().query("apps", new String[]{"remote_config"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    byte[] blob = query.getBlob(0);
                    if (query.moveToNext()) {
                        zzKl().zzLZ().zzj("Got multiple records for app config, expected one. appId", zzatx.zzfE(str));
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
                    zzKl().zzLZ().zze("Error querying remote config. appId", zzatx.zzfE(str), e);
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
            zzKl().zzLZ().zze("Error querying remote config. appId", zzatx.zzfE(str), e);
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

    @WorkerThread
    void zzfx(String str) {
        zzob();
        zzmR();
        zzac.zzdr(str);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete("property_filters", "app_id=?", new String[]{str});
        writableDatabase.delete("event_filters", "app_id=?", new String[]{str});
    }

    Map<Integer, zzf> zzfy(String str) {
        Cursor query;
        Object e;
        Throwable th;
        zzob();
        zzmR();
        zzac.zzdr(str);
        try {
            query = getWritableDatabase().query("audience_filter_values", new String[]{"audience_id", "current_results"}, "app_id=?", new String[]{str}, null, null, null);
            if (query.moveToFirst()) {
                Map<Integer, zzf> arrayMap = new ArrayMap();
                do {
                    int i = query.getInt(0);
                    zzbyb zzag = zzbyb.zzag(query.getBlob(1));
                    zzf com_google_android_gms_internal_zzauw_zzf = new zzf();
                    try {
                        com_google_android_gms_internal_zzauw_zzf.zzb(zzag);
                    } catch (IOException e2) {
                        zzKl().zzLZ().zzd("Failed to merge filter results. appId, audienceId, error", zzatx.zzfE(str), Integer.valueOf(i), e2);
                    }
                    try {
                        arrayMap.put(Integer.valueOf(i), com_google_android_gms_internal_zzauw_zzf);
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
                zzKl().zzLZ().zze("Database error querying filter results. appId", zzatx.zzfE(str), e);
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
    void zzfz(String str) {
        zzob();
        zzmR();
        zzac.zzdr(str);
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            String[] strArr = new String[]{str};
            int delete = writableDatabase.delete("audience_filter_values", "app_id=?", strArr) + ((((((((writableDatabase.delete("events", "app_id=?", strArr) + 0) + writableDatabase.delete("user_attributes", "app_id=?", strArr)) + writableDatabase.delete("conditional_properties", "app_id=?", strArr)) + writableDatabase.delete("apps", "app_id=?", strArr)) + writableDatabase.delete("raw_events", "app_id=?", strArr)) + writableDatabase.delete("raw_events_metadata", "app_id=?", strArr)) + writableDatabase.delete("event_filters", "app_id=?", strArr)) + writableDatabase.delete("property_filters", "app_id=?", strArr));
            if (delete > 0) {
                zzKl().zzMf().zze("Deleted application data. app, records", str, Integer.valueOf(delete));
            }
        } catch (SQLiteException e) {
            zzKl().zzLZ().zze("Error deleting application data. appId, error", zzatx.zzfE(str), e);
        }
    }

    @WorkerThread
    public List<zzatg> zzh(String str, long j) {
        zzac.zzdr(str);
        zzmR();
        zzob();
        if (j < 0) {
            zzKl().zzMb().zze("Invalid time querying timed out conditional properties", zzatx.zzfE(str), Long.valueOf(j));
            return Collections.emptyList();
        }
        return zzc("active=0 and app_id=? and abs(? - creation_timestamp) > trigger_timeout", new String[]{str, String.valueOf(j)});
    }

    @WorkerThread
    public List<zzatg> zzi(String str, long j) {
        zzac.zzdr(str);
        zzmR();
        zzob();
        if (j < 0) {
            zzKl().zzMb().zze("Invalid time querying expired conditional properties", zzatx.zzfE(str), Long.valueOf(j));
            return Collections.emptyList();
        }
        return zzc("active<>0 and app_id=? and abs(? - triggered_timestamp) > time_to_live", new String[]{str, String.valueOf(j)});
    }

    @WorkerThread
    public List<zzaus> zzk(String str, String str2, String str3) {
        Object e;
        Cursor cursor;
        Object obj;
        Throwable th;
        Cursor cursor2 = null;
        zzac.zzdr(str);
        zzmR();
        zzob();
        List<zzaus> arrayList = new ArrayList();
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
            String[] strArr2 = new String[]{"name", "triggered_timestamp", Param.VALUE, "origin"};
            zzKn().zzKY();
            Cursor query = getWritableDatabase().query("user_attributes", strArr2, stringBuilder.toString(), strArr, null, null, "rowid", String.valueOf(PointerIconCompat.TYPE_CONTEXT_MENU));
            try {
                if (query.moveToFirst()) {
                    while (arrayList.size() < zzKn().zzKY()) {
                        String string = query.getString(0);
                        long j = query.getLong(1);
                        Object zzb = zzb(query, 2);
                        String string2 = query.getString(3);
                        if (zzb == null) {
                            try {
                                zzKl().zzLZ().zzd("(2)Read invalid user property value, ignoring it", zzatx.zzfE(str), string2, str3);
                            } catch (SQLiteException e2) {
                                e = e2;
                                cursor = query;
                                obj = string2;
                            } catch (Throwable th2) {
                                th = th2;
                                cursor2 = query;
                            }
                        } else {
                            arrayList.add(new zzaus(str, string2, string, j, zzb));
                        }
                        if (!query.moveToNext()) {
                            break;
                        }
                        obj = string2;
                    }
                    zzKl().zzLZ().zzj("Read more than the max allowed user properties, ignoring excess", Integer.valueOf(zzKn().zzKY()));
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
                zzKl().zzLZ().zzd("(2)Error querying user properties", zzatx.zzfE(str), obj, e);
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
    public List<zzatg> zzl(String str, String str2, String str3) {
        zzac.zzdr(str);
        zzmR();
        zzob();
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

    protected void zzmS() {
    }

    @WorkerThread
    public List<Pair<zze, Long>> zzn(String str, int i, int i2) {
        List<Pair<zze, Long>> arrayList;
        Object e;
        Cursor cursor;
        Throwable th;
        boolean z = true;
        zzmR();
        zzob();
        zzac.zzaw(i > 0);
        if (i2 <= 0) {
            z = false;
        }
        zzac.zzaw(z);
        zzac.zzdr(str);
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
                            byte[] zzx = zzKh().zzx(query.getBlob(1));
                            if (!arrayList.isEmpty() && zzx.length + i3 > i2) {
                                break;
                            }
                            zzbyb zzag = zzbyb.zzag(zzx);
                            zze com_google_android_gms_internal_zzauw_zze = new zze();
                            try {
                                com_google_android_gms_internal_zzauw_zze.zzb(zzag);
                                length = zzx.length + i3;
                                arrayList.add(Pair.create(com_google_android_gms_internal_zzauw_zze, Long.valueOf(j)));
                            } catch (IOException e2) {
                                zzKl().zzLZ().zze("Failed to merge queued bundle. appId", zzatx.zzfE(str), e2);
                                length = i3;
                            }
                            if (!query.moveToNext() || length > i2) {
                                break;
                            }
                            i3 = length;
                        } catch (IOException e22) {
                            zzKl().zzLZ().zze("Failed to unzip queued bundle. appId", zzatx.zzfE(str), e22);
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
                zzKl().zzLZ().zze("Error querying bundles. appId", zzatx.zzfE(str), e);
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

    String zzow() {
        return zzKn().zzpv();
    }

    @WorkerThread
    public void zzz(String str, int i) {
        zzac.zzdr(str);
        zzmR();
        zzob();
        try {
            getWritableDatabase().execSQL("delete from user_attributes where app_id=? and name in (select name from user_attributes where app_id=? and name like '_ltv_%' order by triggered_timestamp desc limit ?,10);", new String[]{str, str, String.valueOf(i)});
        } catch (SQLiteException e) {
            zzKl().zzLZ().zze("Error pruning currencies. appId", zzatx.zzfE(str), e);
        }
    }
}
