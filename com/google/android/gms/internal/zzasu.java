package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Pair;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzauh.zze;
import com.google.android.gms.internal.zzauh.zzf;
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

class zzasu extends zzats {
    private static final Map<String, String> zzbqp = new ArrayMap(18);
    private static final Map<String, String> zzbqq = new ArrayMap(1);
    private static final Map<String, String> zzbqr = new ArrayMap(1);
    private static final Map<String, String> zzbqs = new ArrayMap(1);
    private final zzc zzbqt = new zzc(this, getContext(), zznV());
    private final zzatz zzbqu = new zzatz(zznq());

    public static class zza {
        long zzbqv;
        long zzbqw;
        long zzbqx;
        long zzbqy;
        long zzbqz;
    }

    interface zzb {
        boolean zza(long j, com.google.android.gms.internal.zzauh.zzb com_google_android_gms_internal_zzauh_zzb);

        void zzb(zze com_google_android_gms_internal_zzauh_zze);
    }

    private class zzc extends SQLiteOpenHelper {
        final /* synthetic */ zzasu zzbqA;

        zzc(zzasu com_google_android_gms_internal_zzasu, Context context, String str) {
            this.zzbqA = com_google_android_gms_internal_zzasu;
            super(context, str, null, 1);
        }

        @WorkerThread
        public SQLiteDatabase getWritableDatabase() {
            if (this.zzbqA.zzbqu.zzz(this.zzbqA.zzJv().zzKg())) {
                SQLiteDatabase writableDatabase;
                try {
                    writableDatabase = super.getWritableDatabase();
                } catch (SQLiteException e) {
                    this.zzbqA.zzbqu.start();
                    this.zzbqA.zzJt().zzLa().log("Opening the database failed, dropping and recreating it");
                    String zznV = this.zzbqA.zznV();
                    if (!this.zzbqA.getContext().getDatabasePath(zznV).delete()) {
                        this.zzbqA.zzJt().zzLa().zzj("Failed to delete corrupted db file", zznV);
                    }
                    try {
                        writableDatabase = super.getWritableDatabase();
                        this.zzbqA.zzbqu.clear();
                    } catch (SQLiteException e2) {
                        this.zzbqA.zzJt().zzLa().zzj("Failed to open freshly created database", e2);
                        throw e2;
                    }
                }
                return writableDatabase;
            }
            throw new SQLiteException("Database open failed");
        }

        @WorkerThread
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            zzasu.zza(this.zzbqA.zzJt(), sQLiteDatabase);
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
            zzasu.zza(this.zzbqA.zzJt(), sQLiteDatabase, "events", "CREATE TABLE IF NOT EXISTS events ( app_id TEXT NOT NULL, name TEXT NOT NULL, lifetime_count INTEGER NOT NULL, current_bundle_count INTEGER NOT NULL, last_fire_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,lifetime_count,current_bundle_count,last_fire_timestamp", null);
            zzasu.zza(this.zzbqA.zzJt(), sQLiteDatabase, "user_attributes", "CREATE TABLE IF NOT EXISTS user_attributes ( app_id TEXT NOT NULL, name TEXT NOT NULL, set_timestamp INTEGER NOT NULL, value BLOB NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,set_timestamp,value", null);
            zzasu.zza(this.zzbqA.zzJt(), sQLiteDatabase, "apps", "CREATE TABLE IF NOT EXISTS apps ( app_id TEXT NOT NULL, app_instance_id TEXT, gmp_app_id TEXT, resettable_device_id_hash TEXT, last_bundle_index INTEGER NOT NULL, last_bundle_end_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id)) ;", "app_id,app_instance_id,gmp_app_id,resettable_device_id_hash,last_bundle_index,last_bundle_end_timestamp", zzasu.zzbqp);
            zzasu.zza(this.zzbqA.zzJt(), sQLiteDatabase, "queue", "CREATE TABLE IF NOT EXISTS queue ( app_id TEXT NOT NULL, bundle_end_timestamp INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,bundle_end_timestamp,data", zzasu.zzbqr);
            zzasu.zza(this.zzbqA.zzJt(), sQLiteDatabase, "raw_events_metadata", "CREATE TABLE IF NOT EXISTS raw_events_metadata ( app_id TEXT NOT NULL, metadata_fingerprint INTEGER NOT NULL, metadata BLOB NOT NULL, PRIMARY KEY (app_id, metadata_fingerprint));", "app_id,metadata_fingerprint,metadata", null);
            zzasu.zza(this.zzbqA.zzJt(), sQLiteDatabase, "raw_events", "CREATE TABLE IF NOT EXISTS raw_events ( app_id TEXT NOT NULL, name TEXT NOT NULL, timestamp INTEGER NOT NULL, metadata_fingerprint INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,name,timestamp,metadata_fingerprint,data", zzasu.zzbqq);
            zzasu.zza(this.zzbqA.zzJt(), sQLiteDatabase, "event_filters", "CREATE TABLE IF NOT EXISTS event_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, event_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, event_name, audience_id, filter_id));", "app_id,audience_id,filter_id,event_name,data", null);
            zzasu.zza(this.zzbqA.zzJt(), sQLiteDatabase, "property_filters", "CREATE TABLE IF NOT EXISTS property_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, property_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, property_name, audience_id, filter_id));", "app_id,audience_id,filter_id,property_name,data", null);
            zzasu.zza(this.zzbqA.zzJt(), sQLiteDatabase, "audience_filter_values", "CREATE TABLE IF NOT EXISTS audience_filter_values ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, current_results BLOB, PRIMARY KEY (app_id, audience_id));", "app_id,audience_id,current_results", null);
            zzasu.zza(this.zzbqA.zzJt(), sQLiteDatabase, "app2", "CREATE TABLE IF NOT EXISTS app2 ( app_id TEXT NOT NULL, first_open_count INTEGER NOT NULL, PRIMARY KEY (app_id));", "app_id,first_open_count", zzasu.zzbqs);
        }

        @WorkerThread
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    static {
        zzbqp.put("app_version", "ALTER TABLE apps ADD COLUMN app_version TEXT;");
        zzbqp.put("app_store", "ALTER TABLE apps ADD COLUMN app_store TEXT;");
        zzbqp.put("gmp_version", "ALTER TABLE apps ADD COLUMN gmp_version INTEGER;");
        zzbqp.put("dev_cert_hash", "ALTER TABLE apps ADD COLUMN dev_cert_hash INTEGER;");
        zzbqp.put("measurement_enabled", "ALTER TABLE apps ADD COLUMN measurement_enabled INTEGER;");
        zzbqp.put("last_bundle_start_timestamp", "ALTER TABLE apps ADD COLUMN last_bundle_start_timestamp INTEGER;");
        zzbqp.put("day", "ALTER TABLE apps ADD COLUMN day INTEGER;");
        zzbqp.put("daily_public_events_count", "ALTER TABLE apps ADD COLUMN daily_public_events_count INTEGER;");
        zzbqp.put("daily_events_count", "ALTER TABLE apps ADD COLUMN daily_events_count INTEGER;");
        zzbqp.put("daily_conversions_count", "ALTER TABLE apps ADD COLUMN daily_conversions_count INTEGER;");
        zzbqp.put("remote_config", "ALTER TABLE apps ADD COLUMN remote_config BLOB;");
        zzbqp.put("config_fetched_time", "ALTER TABLE apps ADD COLUMN config_fetched_time INTEGER;");
        zzbqp.put("failed_config_fetch_time", "ALTER TABLE apps ADD COLUMN failed_config_fetch_time INTEGER;");
        zzbqp.put("app_version_int", "ALTER TABLE apps ADD COLUMN app_version_int INTEGER;");
        zzbqp.put("firebase_instance_id", "ALTER TABLE apps ADD COLUMN firebase_instance_id TEXT;");
        zzbqp.put("daily_error_events_count", "ALTER TABLE apps ADD COLUMN daily_error_events_count INTEGER;");
        zzbqp.put("daily_realtime_events_count", "ALTER TABLE apps ADD COLUMN daily_realtime_events_count INTEGER;");
        zzbqp.put("health_monitor_sample", "ALTER TABLE apps ADD COLUMN health_monitor_sample TEXT;");
        zzbqq.put("realtime", "ALTER TABLE raw_events ADD COLUMN realtime INTEGER;");
        zzbqr.put("has_realtime", "ALTER TABLE queue ADD COLUMN has_realtime INTEGER;");
        zzbqs.put("previous_install_count", "ALTER TABLE app2 ADD COLUMN previous_install_count INTEGER;");
    }

    zzasu(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
    }

    private boolean zzKP() {
        return getContext().getDatabasePath(zznV()).exists();
    }

    @WorkerThread
    @TargetApi(11)
    static int zza(Cursor cursor, int i) {
        if (VERSION.SDK_INT >= 11) {
            return cursor.getType(i);
        }
        CursorWindow window = ((SQLiteCursor) cursor).getWindow();
        int position = cursor.getPosition();
        return window.isNull(position, i) ? 0 : window.isLong(position, i) ? 1 : window.isFloat(position, i) ? 2 : window.isString(position, i) ? 3 : window.isBlob(position, i) ? 4 : -1;
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
            zzJt().zzLa().zze("Database error", str, e);
            throw e;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    static void zza(zzati com_google_android_gms_internal_zzati, SQLiteDatabase sQLiteDatabase) {
        if (com_google_android_gms_internal_zzati == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        } else if (VERSION.SDK_INT >= 9) {
            File file = new File(sQLiteDatabase.getPath());
            if (!file.setReadable(false, false)) {
                com_google_android_gms_internal_zzati.zzLc().log("Failed to turn off database read permission");
            }
            if (!file.setWritable(false, false)) {
                com_google_android_gms_internal_zzati.zzLc().log("Failed to turn off database write permission");
            }
            if (!file.setReadable(true, true)) {
                com_google_android_gms_internal_zzati.zzLc().log("Failed to turn on database read permission for owner");
            }
            if (!file.setWritable(true, true)) {
                com_google_android_gms_internal_zzati.zzLc().log("Failed to turn on database write permission for owner");
            }
        }
    }

    @WorkerThread
    static void zza(zzati com_google_android_gms_internal_zzati, SQLiteDatabase sQLiteDatabase, String str, String str2, String str3, Map<String, String> map) throws SQLiteException {
        if (com_google_android_gms_internal_zzati == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        }
        if (!zza(com_google_android_gms_internal_zzati, sQLiteDatabase, str)) {
            sQLiteDatabase.execSQL(str2);
        }
        try {
            zza(com_google_android_gms_internal_zzati, sQLiteDatabase, str, str3, map);
        } catch (SQLiteException e) {
            com_google_android_gms_internal_zzati.zzLa().zzj("Failed to verify columns on table that was just created", str);
            throw e;
        }
    }

    @WorkerThread
    static void zza(zzati com_google_android_gms_internal_zzati, SQLiteDatabase sQLiteDatabase, String str, String str2, Map<String, String> map) throws SQLiteException {
        if (com_google_android_gms_internal_zzati == null) {
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
            com_google_android_gms_internal_zzati.zzLc().zze("Table has extra columns. table, columns", str, TextUtils.join(", ", zzb));
        }
    }

    @WorkerThread
    private void zza(String str, com.google.android.gms.internal.zzauf.zza com_google_android_gms_internal_zzauf_zza) {
        Object obj = null;
        zznA();
        zzmq();
        zzac.zzdv(str);
        zzac.zzw(com_google_android_gms_internal_zzauf_zza);
        zzac.zzw(com_google_android_gms_internal_zzauf_zza.zzbvj);
        zzac.zzw(com_google_android_gms_internal_zzauf_zza.zzbvi);
        if (com_google_android_gms_internal_zzauf_zza.zzbvh == null) {
            zzJt().zzLc().zzj("Audience with no ID. appId", zzati.zzfI(str));
            return;
        }
        int intValue = com_google_android_gms_internal_zzauf_zza.zzbvh.intValue();
        for (com.google.android.gms.internal.zzauf.zzb com_google_android_gms_internal_zzauf_zzb : com_google_android_gms_internal_zzauf_zza.zzbvj) {
            if (com_google_android_gms_internal_zzauf_zzb.zzbvl == null) {
                zzJt().zzLc().zze("Event filter with no ID. Audience definition ignored. appId, audienceId", zzati.zzfI(str), com_google_android_gms_internal_zzauf_zza.zzbvh);
                return;
            }
        }
        for (zzauf.zze com_google_android_gms_internal_zzauf_zze : com_google_android_gms_internal_zzauf_zza.zzbvi) {
            if (com_google_android_gms_internal_zzauf_zze.zzbvl == null) {
                zzJt().zzLc().zze("Property filter with no ID. Audience definition ignored. appId, audienceId", zzati.zzfI(str), com_google_android_gms_internal_zzauf_zza.zzbvh);
                return;
            }
        }
        Object obj2 = 1;
        for (com.google.android.gms.internal.zzauf.zzb zza : com_google_android_gms_internal_zzauf_zza.zzbvj) {
            if (!zza(str, intValue, zza)) {
                obj2 = null;
                break;
            }
        }
        if (obj2 != null) {
            for (zzauf.zze zza2 : com_google_android_gms_internal_zzauf_zza.zzbvi) {
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
    static boolean zza(zzati com_google_android_gms_internal_zzati, SQLiteDatabase sQLiteDatabase, String str) {
        Cursor query;
        Object e;
        Throwable th;
        Cursor cursor = null;
        if (com_google_android_gms_internal_zzati == null) {
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
                    com_google_android_gms_internal_zzati.zzLc().zze("Error querying for table", str, e);
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
            com_google_android_gms_internal_zzati.zzLc().zze("Error querying for table", str, e);
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
    private boolean zza(String str, int i, com.google.android.gms.internal.zzauf.zzb com_google_android_gms_internal_zzauf_zzb) {
        zznA();
        zzmq();
        zzac.zzdv(str);
        zzac.zzw(com_google_android_gms_internal_zzauf_zzb);
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzauf_zzb.zzbvm)) {
            zzJt().zzLc().zzd("Event filter had no event name. Audience definition ignored. appId, audienceId, filterId", zzati.zzfI(str), Integer.valueOf(i), String.valueOf(com_google_android_gms_internal_zzauf_zzb.zzbvl));
            return false;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzauf_zzb.zzacZ()];
            zzbum zzae = zzbum.zzae(bArr);
            com_google_android_gms_internal_zzauf_zzb.zza(zzae);
            zzae.zzacM();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("filter_id", com_google_android_gms_internal_zzauf_zzb.zzbvl);
            contentValues.put("event_name", com_google_android_gms_internal_zzauf_zzb.zzbvm);
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("event_filters", null, contentValues, 5) == -1) {
                    zzJt().zzLa().zzj("Failed to insert event filter (got -1). appId", zzati.zzfI(str));
                }
                return true;
            } catch (SQLiteException e) {
                zzJt().zzLa().zze("Error storing event filter. appId", zzati.zzfI(str), e);
                return false;
            }
        } catch (IOException e2) {
            zzJt().zzLa().zze("Configuration loss. Failed to serialize event filter. appId", zzati.zzfI(str), e2);
            return false;
        }
    }

    @WorkerThread
    private boolean zza(String str, int i, zzauf.zze com_google_android_gms_internal_zzauf_zze) {
        zznA();
        zzmq();
        zzac.zzdv(str);
        zzac.zzw(com_google_android_gms_internal_zzauf_zze);
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzauf_zze.zzbvB)) {
            zzJt().zzLc().zzd("Property filter had no property name. Audience definition ignored. appId, audienceId, filterId", zzati.zzfI(str), Integer.valueOf(i), String.valueOf(com_google_android_gms_internal_zzauf_zze.zzbvl));
            return false;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzauf_zze.zzacZ()];
            zzbum zzae = zzbum.zzae(bArr);
            com_google_android_gms_internal_zzauf_zze.zza(zzae);
            zzae.zzacM();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("filter_id", com_google_android_gms_internal_zzauf_zze.zzbvl);
            contentValues.put("property_name", com_google_android_gms_internal_zzauf_zze.zzbvB);
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("property_filters", null, contentValues, 5) != -1) {
                    return true;
                }
                zzJt().zzLa().zzj("Failed to insert property filter (got -1). appId", zzati.zzfI(str));
                return false;
            } catch (SQLiteException e) {
                zzJt().zzLa().zze("Error storing property filter. appId", zzati.zzfI(str), e);
                return false;
            }
        } catch (IOException e2) {
            zzJt().zzLa().zze("Configuration loss. Failed to serialize property filter. appId", zzati.zzfI(str), e2);
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
            zzJt().zzLa().zze("Database error", str, e);
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
        zznA();
        getWritableDatabase().beginTransaction();
    }

    @WorkerThread
    public void endTransaction() {
        zznA();
        getWritableDatabase().endTransaction();
    }

    @WorkerThread
    SQLiteDatabase getWritableDatabase() {
        zzmq();
        try {
            return this.zzbqt.getWritableDatabase();
        } catch (SQLiteException e) {
            zzJt().zzLc().zzj("Error opening database", e);
            throw e;
        }
    }

    @WorkerThread
    public void setTransactionSuccessful() {
        zznA();
        getWritableDatabase().setTransactionSuccessful();
    }

    @WorkerThread
    void zzA(String str, int i) {
        zznA();
        zzmq();
        zzac.zzdv(str);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete("property_filters", "app_id=? and audience_id=?", new String[]{str, String.valueOf(i)});
        writableDatabase.delete("event_filters", "app_id=? and audience_id=?", new String[]{str, String.valueOf(i)});
    }

    public void zzG(List<Long> list) {
        zzac.zzw(list);
        zzmq();
        zznA();
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
            zzJt().zzLa().zze("Deleted fewer rows from raw events table than expected", Integer.valueOf(delete), Integer.valueOf(list.size()));
        }
    }

    @WorkerThread
    public String zzKG() {
        Cursor rawQuery;
        Object e;
        Throwable th;
        String str = null;
        try {
            rawQuery = getWritableDatabase().rawQuery("select app_id from queue where app_id not in (select app_id from apps where measurement_enabled=0) order by has_realtime desc, rowid asc limit 1;", null);
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
                    zzJt().zzLa().zzj("Database error getting next bundle app id", e);
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
            zzJt().zzLa().zzj("Database error getting next bundle app id", e);
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

    public boolean zzKH() {
        return zzb("select count(1) > 0 from queue where has_realtime = 1", null) != 0;
    }

    @WorkerThread
    void zzKI() {
        zzmq();
        zznA();
        if (zzKP()) {
            long j = zzJu().zzbsj.get();
            long elapsedRealtime = zznq().elapsedRealtime();
            if (Math.abs(elapsedRealtime - j) > zzJv().zzKo()) {
                zzJu().zzbsj.set(elapsedRealtime);
                zzKJ();
            }
        }
    }

    @WorkerThread
    void zzKJ() {
        zzmq();
        zznA();
        if (zzKP()) {
            int delete = getWritableDatabase().delete("queue", "abs(bundle_end_timestamp - ?) > cast(? as integer)", new String[]{String.valueOf(zznq().currentTimeMillis()), String.valueOf(zzJv().zzKn())});
            if (delete > 0) {
                zzJt().zzLg().zzj("Deleted stale rows. rowsDeleted", Integer.valueOf(delete));
            }
        }
    }

    @WorkerThread
    public long zzKK() {
        return zza("select max(bundle_end_timestamp) from queue", null, 0);
    }

    @WorkerThread
    public long zzKL() {
        return zza("select max(timestamp) from raw_events", null, 0);
    }

    public boolean zzKM() {
        return zzb("select count(1) > 0 from raw_events", null) != 0;
    }

    public boolean zzKN() {
        return zzb("select count(1) > 0 from raw_events where realtime = 1", null) != 0;
    }

    public long zzKO() {
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
            zzJt().zzLa().zzj("Error querying raw events", e);
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
    public zzasy zzP(String str, String str2) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzac.zzdv(str);
        zzac.zzdv(str2);
        zzmq();
        zznA();
        try {
            Cursor query = getWritableDatabase().query("events", new String[]{"lifetime_count", "current_bundle_count", "last_fire_timestamp"}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zzasy com_google_android_gms_internal_zzasy = new zzasy(str, str2, query.getLong(0), query.getLong(1), query.getLong(2));
                    if (query.moveToNext()) {
                        zzJt().zzLa().zzj("Got multiple records for event aggregates, expected one. appId", zzati.zzfI(str));
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzasy;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzasy;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
                try {
                    zzJt().zzLa().zzd("Error querying events. appId", zzati.zzfI(str), str2, e);
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
            zzJt().zzLa().zzd("Error querying events. appId", zzati.zzfI(str), str2, e);
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
    public void zzQ(String str, String str2) {
        zzac.zzdv(str);
        zzac.zzdv(str2);
        zzmq();
        zznA();
        try {
            zzJt().zzLg().zzj("Deleted user attribute rows:", Integer.valueOf(getWritableDatabase().delete("user_attributes", "app_id=? and name=?", new String[]{str, str2})));
        } catch (SQLiteException e) {
            zzJt().zzLa().zzd("Error deleting user attribute. appId", zzati.zzfI(str), str2, e);
        }
    }

    @WorkerThread
    public zzaud zzR(String str, String str2) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzac.zzdv(str);
        zzac.zzdv(str2);
        zzmq();
        zznA();
        try {
            Cursor query = getWritableDatabase().query("user_attributes", new String[]{"set_timestamp", Param.VALUE}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zzaud com_google_android_gms_internal_zzaud = new zzaud(str, str2, query.getLong(0), zzb(query, 1));
                    if (query.moveToNext()) {
                        zzJt().zzLa().zzj("Got multiple records for user property, expected one. appId", zzati.zzfI(str));
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzaud;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzaud;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
                try {
                    zzJt().zzLa().zzd("Error querying user property. appId", zzati.zzfI(str), str2, e);
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
            zzJt().zzLa().zzd("Error querying user property. appId", zzati.zzfI(str), str2, e);
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

    Map<Integer, List<com.google.android.gms.internal.zzauf.zzb>> zzS(String str, String str2) {
        Object e;
        Throwable th;
        zznA();
        zzmq();
        zzac.zzdv(str);
        zzac.zzdv(str2);
        Map<Integer, List<com.google.android.gms.internal.zzauf.zzb>> arrayMap = new ArrayMap();
        Cursor query;
        try {
            query = getWritableDatabase().query("event_filters", new String[]{"audience_id", "data"}, "app_id=? AND event_name=?", new String[]{str, str2}, null, null, null);
            if (query.moveToFirst()) {
                do {
                    zzbul zzad = zzbul.zzad(query.getBlob(1));
                    com.google.android.gms.internal.zzauf.zzb com_google_android_gms_internal_zzauf_zzb = new com.google.android.gms.internal.zzauf.zzb();
                    try {
                        com_google_android_gms_internal_zzauf_zzb.zzb(zzad);
                        int i = query.getInt(0);
                        List list = (List) arrayMap.get(Integer.valueOf(i));
                        if (list == null) {
                            list = new ArrayList();
                            arrayMap.put(Integer.valueOf(i), list);
                        }
                        list.add(com_google_android_gms_internal_zzauf_zzb);
                    } catch (IOException e2) {
                        try {
                            zzJt().zzLa().zze("Failed to merge filter. appId", zzati.zzfI(str), e2);
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
            Map<Integer, List<com.google.android.gms.internal.zzauf.zzb>> emptyMap = Collections.emptyMap();
            if (query == null) {
                return emptyMap;
            }
            query.close();
            return emptyMap;
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            try {
                zzJt().zzLa().zze("Database error querying filters. appId", zzati.zzfI(str), e);
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

    Map<Integer, List<zzauf.zze>> zzT(String str, String str2) {
        Object e;
        Throwable th;
        zznA();
        zzmq();
        zzac.zzdv(str);
        zzac.zzdv(str2);
        Map<Integer, List<zzauf.zze>> arrayMap = new ArrayMap();
        Cursor query;
        try {
            query = getWritableDatabase().query("property_filters", new String[]{"audience_id", "data"}, "app_id=? AND property_name=?", new String[]{str, str2}, null, null, null);
            if (query.moveToFirst()) {
                do {
                    zzbul zzad = zzbul.zzad(query.getBlob(1));
                    zzauf.zze com_google_android_gms_internal_zzauf_zze = new zzauf.zze();
                    try {
                        com_google_android_gms_internal_zzauf_zze.zzb(zzad);
                        int i = query.getInt(0);
                        List list = (List) arrayMap.get(Integer.valueOf(i));
                        if (list == null) {
                            list = new ArrayList();
                            arrayMap.put(Integer.valueOf(i), list);
                        }
                        list.add(com_google_android_gms_internal_zzauf_zze);
                    } catch (IOException e2) {
                        try {
                            zzJt().zzLa().zze("Failed to merge filter", zzati.zzfI(str), e2);
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
            Map<Integer, List<zzauf.zze>> emptyMap = Collections.emptyMap();
            if (query == null) {
                return emptyMap;
            }
            query.close();
            return emptyMap;
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            try {
                zzJt().zzLa().zze("Database error querying filters. appId", zzati.zzfI(str), e);
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
    protected long zzU(String str, String str2) {
        Object e;
        zzac.zzdv(str);
        zzac.zzdv(str2);
        zzmq();
        zznA();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        long zza;
        try {
            zza = zza(new StringBuilder(String.valueOf(str2).length() + 32).append("select ").append(str2).append(" from app2 where app_id=?").toString(), new String[]{str}, -1);
            if (zza == -1) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("app_id", str);
                contentValues.put("first_open_count", Integer.valueOf(0));
                contentValues.put("previous_install_count", Integer.valueOf(0));
                if (writableDatabase.insertWithOnConflict("app2", null, contentValues, 5) == -1) {
                    zzJt().zzLa().zze("Failed to insert column (got -1). appId", zzati.zzfI(str), str2);
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
                    zzJt().zzLa().zze("Failed to update column (got 0). appId", zzati.zzfI(str), str2);
                    writableDatabase.endTransaction();
                    return -1;
                }
                writableDatabase.setTransactionSuccessful();
                writableDatabase.endTransaction();
                return zza;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzJt().zzLa().zzd("Error inserting column. appId", zzati.zzfI(str), str2, e);
                    return zza;
                } finally {
                    writableDatabase.endTransaction();
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            zza = 0;
            zzJt().zzLa().zzd("Error inserting column. appId", zzati.zzfI(str), str2, e);
            return zza;
        }
    }

    public long zza(zze com_google_android_gms_internal_zzauh_zze) throws IOException {
        zzmq();
        zznA();
        zzac.zzw(com_google_android_gms_internal_zzauh_zze);
        zzac.zzdv(com_google_android_gms_internal_zzauh_zze.zzaR);
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzauh_zze.zzacZ()];
            zzbum zzae = zzbum.zzae(bArr);
            com_google_android_gms_internal_zzauh_zze.zza(zzae);
            zzae.zzacM();
            long zzz = zzJp().zzz(bArr);
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzauh_zze.zzaR);
            contentValues.put("metadata_fingerprint", Long.valueOf(zzz));
            contentValues.put(TtmlNode.TAG_METADATA, bArr);
            try {
                getWritableDatabase().insertWithOnConflict("raw_events_metadata", null, contentValues, 4);
                return zzz;
            } catch (SQLiteException e) {
                zzJt().zzLa().zze("Error storing raw event metadata. appId", zzati.zzfI(com_google_android_gms_internal_zzauh_zze.zzaR), e);
                throw e;
            }
        } catch (IOException e2) {
            zzJt().zzLa().zze("Data loss. Failed to serialize event metadata. appId", zzati.zzfI(com_google_android_gms_internal_zzauh_zze.zzaR), e2);
            throw e2;
        }
    }

    @WorkerThread
    public zza zza(long j, String str, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        Cursor query;
        Object e;
        Throwable th;
        zzac.zzdv(str);
        zzmq();
        zznA();
        String[] strArr = new String[]{str};
        zza com_google_android_gms_internal_zzasu_zza = new zza();
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            query = writableDatabase.query("apps", new String[]{"day", "daily_events_count", "daily_public_events_count", "daily_conversions_count", "daily_error_events_count", "daily_realtime_events_count"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    if (query.getLong(0) == j) {
                        com_google_android_gms_internal_zzasu_zza.zzbqw = query.getLong(1);
                        com_google_android_gms_internal_zzasu_zza.zzbqv = query.getLong(2);
                        com_google_android_gms_internal_zzasu_zza.zzbqx = query.getLong(3);
                        com_google_android_gms_internal_zzasu_zza.zzbqy = query.getLong(4);
                        com_google_android_gms_internal_zzasu_zza.zzbqz = query.getLong(5);
                    }
                    if (z) {
                        com_google_android_gms_internal_zzasu_zza.zzbqw++;
                    }
                    if (z2) {
                        com_google_android_gms_internal_zzasu_zza.zzbqv++;
                    }
                    if (z3) {
                        com_google_android_gms_internal_zzasu_zza.zzbqx++;
                    }
                    if (z4) {
                        com_google_android_gms_internal_zzasu_zza.zzbqy++;
                    }
                    if (z5) {
                        com_google_android_gms_internal_zzasu_zza.zzbqz++;
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("day", Long.valueOf(j));
                    contentValues.put("daily_public_events_count", Long.valueOf(com_google_android_gms_internal_zzasu_zza.zzbqv));
                    contentValues.put("daily_events_count", Long.valueOf(com_google_android_gms_internal_zzasu_zza.zzbqw));
                    contentValues.put("daily_conversions_count", Long.valueOf(com_google_android_gms_internal_zzasu_zza.zzbqx));
                    contentValues.put("daily_error_events_count", Long.valueOf(com_google_android_gms_internal_zzasu_zza.zzbqy));
                    contentValues.put("daily_realtime_events_count", Long.valueOf(com_google_android_gms_internal_zzasu_zza.zzbqz));
                    writableDatabase.update("apps", contentValues, "app_id=?", strArr);
                    if (query != null) {
                        query.close();
                    }
                    return com_google_android_gms_internal_zzasu_zza;
                }
                zzJt().zzLc().zzj("Not updating daily counts, app is not known. appId", zzati.zzfI(str));
                if (query != null) {
                    query.close();
                }
                return com_google_android_gms_internal_zzasu_zza;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzJt().zzLa().zze("Error updating daily counts. appId", zzati.zzfI(str), e);
                    if (query != null) {
                        query.close();
                    }
                    return com_google_android_gms_internal_zzasu_zza;
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
            zzJt().zzLa().zze("Error updating daily counts. appId", zzati.zzfI(str), e);
            if (query != null) {
                query.close();
            }
            return com_google_android_gms_internal_zzasu_zza;
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
        zzac.zzdv(str);
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
    public void zza(zzasp com_google_android_gms_internal_zzasp) {
        zzac.zzw(com_google_android_gms_internal_zzasp);
        zzmq();
        zznA();
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_internal_zzasp.zzjI());
        contentValues.put("app_instance_id", com_google_android_gms_internal_zzasp.getAppInstanceId());
        contentValues.put("gmp_app_id", com_google_android_gms_internal_zzasp.getGmpAppId());
        contentValues.put("resettable_device_id_hash", com_google_android_gms_internal_zzasp.zzJx());
        contentValues.put("last_bundle_index", Long.valueOf(com_google_android_gms_internal_zzasp.zzJG()));
        contentValues.put("last_bundle_start_timestamp", Long.valueOf(com_google_android_gms_internal_zzasp.zzJz()));
        contentValues.put("last_bundle_end_timestamp", Long.valueOf(com_google_android_gms_internal_zzasp.zzJA()));
        contentValues.put("app_version", com_google_android_gms_internal_zzasp.zzmy());
        contentValues.put("app_store", com_google_android_gms_internal_zzasp.zzJC());
        contentValues.put("gmp_version", Long.valueOf(com_google_android_gms_internal_zzasp.zzJD()));
        contentValues.put("dev_cert_hash", Long.valueOf(com_google_android_gms_internal_zzasp.zzJE()));
        contentValues.put("measurement_enabled", Boolean.valueOf(com_google_android_gms_internal_zzasp.zzJF()));
        contentValues.put("day", Long.valueOf(com_google_android_gms_internal_zzasp.zzJK()));
        contentValues.put("daily_public_events_count", Long.valueOf(com_google_android_gms_internal_zzasp.zzJL()));
        contentValues.put("daily_events_count", Long.valueOf(com_google_android_gms_internal_zzasp.zzJM()));
        contentValues.put("daily_conversions_count", Long.valueOf(com_google_android_gms_internal_zzasp.zzJN()));
        contentValues.put("config_fetched_time", Long.valueOf(com_google_android_gms_internal_zzasp.zzJH()));
        contentValues.put("failed_config_fetch_time", Long.valueOf(com_google_android_gms_internal_zzasp.zzJI()));
        contentValues.put("app_version_int", Long.valueOf(com_google_android_gms_internal_zzasp.zzJB()));
        contentValues.put("firebase_instance_id", com_google_android_gms_internal_zzasp.zzJy());
        contentValues.put("daily_error_events_count", Long.valueOf(com_google_android_gms_internal_zzasp.zzJP()));
        contentValues.put("daily_realtime_events_count", Long.valueOf(com_google_android_gms_internal_zzasp.zzJO()));
        contentValues.put("health_monitor_sample", com_google_android_gms_internal_zzasp.zzJQ());
        try {
            if (getWritableDatabase().insertWithOnConflict("apps", null, contentValues, 5) == -1) {
                zzJt().zzLa().zzj("Failed to insert/update app (got -1). appId", zzati.zzfI(com_google_android_gms_internal_zzasp.zzjI()));
            }
        } catch (SQLiteException e) {
            zzJt().zzLa().zze("Error storing app. appId", zzati.zzfI(com_google_android_gms_internal_zzasp.zzjI()), e);
        }
    }

    public void zza(zzasx com_google_android_gms_internal_zzasx, long j, boolean z) {
        int i = 0;
        zzmq();
        zznA();
        zzac.zzw(com_google_android_gms_internal_zzasx);
        zzac.zzdv(com_google_android_gms_internal_zzasx.zzVQ);
        com.google.android.gms.internal.zzauh.zzb com_google_android_gms_internal_zzauh_zzb = new com.google.android.gms.internal.zzauh.zzb();
        com_google_android_gms_internal_zzauh_zzb.zzbvX = Long.valueOf(com_google_android_gms_internal_zzasx.zzbqH);
        com_google_android_gms_internal_zzauh_zzb.zzbvV = new com.google.android.gms.internal.zzauh.zzc[com_google_android_gms_internal_zzasx.zzbqI.size()];
        Iterator it = com_google_android_gms_internal_zzasx.zzbqI.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            String str = (String) it.next();
            com.google.android.gms.internal.zzauh.zzc com_google_android_gms_internal_zzauh_zzc = new com.google.android.gms.internal.zzauh.zzc();
            int i3 = i2 + 1;
            com_google_android_gms_internal_zzauh_zzb.zzbvV[i2] = com_google_android_gms_internal_zzauh_zzc;
            com_google_android_gms_internal_zzauh_zzc.name = str;
            zzJp().zza(com_google_android_gms_internal_zzauh_zzc, com_google_android_gms_internal_zzasx.zzbqI.get(str));
            i2 = i3;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzauh_zzb.zzacZ()];
            zzbum zzae = zzbum.zzae(bArr);
            com_google_android_gms_internal_zzauh_zzb.zza(zzae);
            zzae.zzacM();
            zzJt().zzLg().zze("Saving event, name, data size", com_google_android_gms_internal_zzasx.mName, Integer.valueOf(bArr.length));
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzasx.zzVQ);
            contentValues.put("name", com_google_android_gms_internal_zzasx.mName);
            contentValues.put("timestamp", Long.valueOf(com_google_android_gms_internal_zzasx.zzavX));
            contentValues.put("metadata_fingerprint", Long.valueOf(j));
            contentValues.put("data", bArr);
            str = "realtime";
            if (z) {
                i = 1;
            }
            contentValues.put(str, Integer.valueOf(i));
            try {
                if (getWritableDatabase().insert("raw_events", null, contentValues) == -1) {
                    zzJt().zzLa().zzj("Failed to insert raw event (got -1). appId", zzati.zzfI(com_google_android_gms_internal_zzasx.zzVQ));
                }
            } catch (SQLiteException e) {
                zzJt().zzLa().zze("Error storing raw event. appId", zzati.zzfI(com_google_android_gms_internal_zzasx.zzVQ), e);
            }
        } catch (IOException e2) {
            zzJt().zzLa().zze("Data loss. Failed to serialize event params/data. appId", zzati.zzfI(com_google_android_gms_internal_zzasx.zzVQ), e2);
        }
    }

    @WorkerThread
    public void zza(zzasy com_google_android_gms_internal_zzasy) {
        zzac.zzw(com_google_android_gms_internal_zzasy);
        zzmq();
        zznA();
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_internal_zzasy.zzVQ);
        contentValues.put("name", com_google_android_gms_internal_zzasy.mName);
        contentValues.put("lifetime_count", Long.valueOf(com_google_android_gms_internal_zzasy.zzbqJ));
        contentValues.put("current_bundle_count", Long.valueOf(com_google_android_gms_internal_zzasy.zzbqK));
        contentValues.put("last_fire_timestamp", Long.valueOf(com_google_android_gms_internal_zzasy.zzbqL));
        try {
            if (getWritableDatabase().insertWithOnConflict("events", null, contentValues, 5) == -1) {
                zzJt().zzLa().zzj("Failed to insert/update event aggregates (got -1). appId", zzati.zzfI(com_google_android_gms_internal_zzasy.zzVQ));
            }
        } catch (SQLiteException e) {
            zzJt().zzLa().zze("Error storing event aggregates. appId", zzati.zzfI(com_google_android_gms_internal_zzasy.zzVQ), e);
        }
    }

    @WorkerThread
    public void zza(zze com_google_android_gms_internal_zzauh_zze, boolean z) {
        zzmq();
        zznA();
        zzac.zzw(com_google_android_gms_internal_zzauh_zze);
        zzac.zzdv(com_google_android_gms_internal_zzauh_zze.zzaR);
        zzac.zzw(com_google_android_gms_internal_zzauh_zze.zzbwh);
        zzKI();
        long currentTimeMillis = zznq().currentTimeMillis();
        if (com_google_android_gms_internal_zzauh_zze.zzbwh.longValue() < currentTimeMillis - zzJv().zzKn() || com_google_android_gms_internal_zzauh_zze.zzbwh.longValue() > zzJv().zzKn() + currentTimeMillis) {
            zzJt().zzLc().zzd("Storing bundle outside of the max uploading time span. appId, now, timestamp", zzati.zzfI(com_google_android_gms_internal_zzauh_zze.zzaR), Long.valueOf(currentTimeMillis), com_google_android_gms_internal_zzauh_zze.zzbwh);
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzauh_zze.zzacZ()];
            zzbum zzae = zzbum.zzae(bArr);
            com_google_android_gms_internal_zzauh_zze.zza(zzae);
            zzae.zzacM();
            bArr = zzJp().zzk(bArr);
            zzJt().zzLg().zzj("Saving bundle, size", Integer.valueOf(bArr.length));
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzauh_zze.zzaR);
            contentValues.put("bundle_end_timestamp", com_google_android_gms_internal_zzauh_zze.zzbwh);
            contentValues.put("data", bArr);
            contentValues.put("has_realtime", Integer.valueOf(z ? 1 : 0));
            try {
                if (getWritableDatabase().insert("queue", null, contentValues) == -1) {
                    zzJt().zzLa().zzj("Failed to insert bundle (got -1). appId", zzati.zzfI(com_google_android_gms_internal_zzauh_zze.zzaR));
                }
            } catch (SQLiteException e) {
                zzJt().zzLa().zze("Error storing bundle. appId", zzati.zzfI(com_google_android_gms_internal_zzauh_zze.zzaR), e);
            }
        } catch (IOException e2) {
            zzJt().zzLa().zze("Data loss. Failed to serialize bundle. appId", zzati.zzfI(com_google_android_gms_internal_zzauh_zze.zzaR), e2);
        }
    }

    void zza(String str, int i, zzf com_google_android_gms_internal_zzauh_zzf) {
        zznA();
        zzmq();
        zzac.zzdv(str);
        zzac.zzw(com_google_android_gms_internal_zzauh_zzf);
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzauh_zzf.zzacZ()];
            zzbum zzae = zzbum.zzae(bArr);
            com_google_android_gms_internal_zzauh_zzf.zza(zzae);
            zzae.zzacM();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("current_results", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("audience_filter_values", null, contentValues, 5) == -1) {
                    zzJt().zzLa().zzj("Failed to insert filter results (got -1). appId", zzati.zzfI(str));
                }
            } catch (SQLiteException e) {
                zzJt().zzLa().zze("Error storing filter results. appId", zzati.zzfI(str), e);
            }
        } catch (IOException e2) {
            zzJt().zzLa().zze("Configuration loss. Failed to serialize filter results. appId", zzati.zzfI(str), e2);
        }
    }

    public void zza(String str, long j, long j2, zzb com_google_android_gms_internal_zzasu_zzb) {
        Object e;
        Throwable th;
        zzac.zzw(com_google_android_gms_internal_zzasu_zzb);
        zzmq();
        zznA();
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
                zzbul zzad = zzbul.zzad(cursor2.getBlob(0));
                zze com_google_android_gms_internal_zzauh_zze = new zze();
                try {
                    String str4;
                    String[] strArr2;
                    com_google_android_gms_internal_zzauh_zze.zzb(zzad);
                    if (cursor2.moveToNext()) {
                        zzJt().zzLc().zzj("Get multiple raw event metadata records, expected one. appId", zzati.zzfI(str));
                    }
                    cursor2.close();
                    com_google_android_gms_internal_zzasu_zzb.zzb(com_google_android_gms_internal_zzauh_zze);
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
                            try {
                                long j3 = cursor.getLong(0);
                                zzbul zzad2 = zzbul.zzad(cursor.getBlob(3));
                                com.google.android.gms.internal.zzauh.zzb com_google_android_gms_internal_zzauh_zzb = new com.google.android.gms.internal.zzauh.zzb();
                                try {
                                    com_google_android_gms_internal_zzauh_zzb.zzb(zzad2);
                                    com_google_android_gms_internal_zzauh_zzb.name = cursor.getString(1);
                                    com_google_android_gms_internal_zzauh_zzb.zzbvW = Long.valueOf(cursor.getLong(2));
                                    if (!com_google_android_gms_internal_zzasu_zzb.zza(j3, com_google_android_gms_internal_zzauh_zzb)) {
                                        if (cursor != null) {
                                            cursor.close();
                                            return;
                                        }
                                        return;
                                    }
                                } catch (IOException e2) {
                                    zzJt().zzLa().zze("Data loss. Failed to merge raw event. appId", zzati.zzfI(str), e2);
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
                    zzJt().zzLc().zzj("Raw event data disappeared while in transaction. appId", zzati.zzfI(str));
                    if (cursor != null) {
                        cursor.close();
                        return;
                    }
                    return;
                } catch (IOException e22) {
                    zzJt().zzLa().zze("Data loss. Failed to merge raw event metadata. appId", zzati.zzfI(str), e22);
                    if (cursor2 != null) {
                        cursor2.close();
                        return;
                    }
                    return;
                }
            }
            zzJt().zzLa().zzj("Raw event metadata record is missing. appId", zzati.zzfI(str));
            if (cursor2 != null) {
                cursor2.close();
            }
        } catch (SQLiteException e4) {
            e = e4;
            cursor = cursor2;
            try {
                zzJt().zzLa().zze("Data loss. Error selecting raw event. appId", zzati.zzfI(str), e);
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
    public boolean zza(zzaud com_google_android_gms_internal_zzaud) {
        zzac.zzw(com_google_android_gms_internal_zzaud);
        zzmq();
        zznA();
        if (zzR(com_google_android_gms_internal_zzaud.zzVQ, com_google_android_gms_internal_zzaud.mName) == null) {
            long zzb;
            if (zzaue.zzfW(com_google_android_gms_internal_zzaud.mName)) {
                zzb = zzb("select count(1) from user_attributes where app_id=? and name not like '!_%' escape '!'", new String[]{com_google_android_gms_internal_zzaud.zzVQ});
                zzJv().zzKe();
                if (zzb >= 25) {
                    return false;
                }
            }
            zzb = zzb("select count(1) from user_attributes where app_id=?", new String[]{com_google_android_gms_internal_zzaud.zzVQ});
            zzJv().zzKf();
            if (zzb >= 50) {
                return false;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_internal_zzaud.zzVQ);
        contentValues.put("name", com_google_android_gms_internal_zzaud.mName);
        contentValues.put("set_timestamp", Long.valueOf(com_google_android_gms_internal_zzaud.zzbvd));
        zza(contentValues, Param.VALUE, com_google_android_gms_internal_zzaud.zzYe);
        try {
            if (getWritableDatabase().insertWithOnConflict("user_attributes", null, contentValues, 5) == -1) {
                zzJt().zzLa().zzj("Failed to insert/update user property (got -1). appId", zzati.zzfI(com_google_android_gms_internal_zzaud.zzVQ));
            }
        } catch (SQLiteException e) {
            zzJt().zzLa().zze("Error storing user property. appId", zzati.zzfI(com_google_android_gms_internal_zzaud.zzVQ), e);
        }
        return true;
    }

    @WorkerThread
    public void zzal(long j) {
        zzmq();
        zznA();
        if (getWritableDatabase().delete("queue", "rowid=?", new String[]{String.valueOf(j)}) != 1) {
            zzJt().zzLa().log("Deleted fewer rows from queue than expected");
        }
    }

    public String zzam(long j) {
        Object e;
        Throwable th;
        String str = null;
        zzmq();
        zznA();
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
                    zzJt().zzLg().log("No expired configs for apps with pending events");
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                }
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzJt().zzLa().zzj("Error selecting expired configs", e);
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
            zzJt().zzLa().zzj("Error selecting expired configs", e);
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
                zzJt().zzLa().log("Loaded invalid null value from database");
                return null;
            case 1:
                return Long.valueOf(cursor.getLong(i));
            case 2:
                return Double.valueOf(cursor.getDouble(i));
            case 3:
                return cursor.getString(i);
            case 4:
                zzJt().zzLa().log("Loaded invalid blob type value, ignoring it");
                return null;
            default:
                zzJt().zzLa().zzj("Loaded invalid unknown value type, ignoring it", Integer.valueOf(zza));
                return null;
        }
    }

    @WorkerThread
    void zzb(String str, com.google.android.gms.internal.zzauf.zza[] com_google_android_gms_internal_zzauf_zzaArr) {
        int i = 0;
        zznA();
        zzmq();
        zzac.zzdv(str);
        zzac.zzw(com_google_android_gms_internal_zzauf_zzaArr);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            zzfB(str);
            for (com.google.android.gms.internal.zzauf.zza zza : com_google_android_gms_internal_zzauf_zzaArr) {
                zza(str, zza);
            }
            List arrayList = new ArrayList();
            int length = com_google_android_gms_internal_zzauf_zzaArr.length;
            while (i < length) {
                arrayList.add(com_google_android_gms_internal_zzauf_zzaArr[i].zzbvh);
                i++;
            }
            zzc(str, arrayList);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    boolean zzc(String str, List<Integer> list) {
        zzac.zzdv(str);
        zznA();
        zzmq();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        try {
            if (zzb("select count(1) from audience_filter_values where app_id=?", new String[]{str}) <= ((long) zzJv().zzfs(str))) {
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
            zzJt().zzLa().zze("Database error querying filters. appId", zzati.zzfI(str), e);
            return false;
        }
    }

    @WorkerThread
    public void zzd(String str, byte[] bArr) {
        zzac.zzdv(str);
        zzmq();
        zznA();
        ContentValues contentValues = new ContentValues();
        contentValues.put("remote_config", bArr);
        try {
            if (((long) getWritableDatabase().update("apps", contentValues, "app_id = ?", new String[]{str})) == 0) {
                zzJt().zzLa().zzj("Failed to update remote config (got 0). appId", zzati.zzfI(str));
            }
        } catch (SQLiteException e) {
            zzJt().zzLa().zze("Error storing remote config. appId", zzati.zzfI(str), e);
        }
    }

    @WorkerThread
    public byte[] zzfA(String str) {
        Object e;
        Throwable th;
        zzac.zzdv(str);
        zzmq();
        zznA();
        Cursor query;
        try {
            query = getWritableDatabase().query("apps", new String[]{"remote_config"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    byte[] blob = query.getBlob(0);
                    if (query.moveToNext()) {
                        zzJt().zzLa().zzj("Got multiple records for app config, expected one. appId", zzati.zzfI(str));
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
                    zzJt().zzLa().zze("Error querying remote config. appId", zzati.zzfI(str), e);
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
            zzJt().zzLa().zze("Error querying remote config. appId", zzati.zzfI(str), e);
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
    void zzfB(String str) {
        zznA();
        zzmq();
        zzac.zzdv(str);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete("property_filters", "app_id=?", new String[]{str});
        writableDatabase.delete("event_filters", "app_id=?", new String[]{str});
    }

    Map<Integer, zzf> zzfC(String str) {
        Cursor query;
        Object e;
        Throwable th;
        zznA();
        zzmq();
        zzac.zzdv(str);
        try {
            query = getWritableDatabase().query("audience_filter_values", new String[]{"audience_id", "current_results"}, "app_id=?", new String[]{str}, null, null, null);
            if (query.moveToFirst()) {
                Map<Integer, zzf> arrayMap = new ArrayMap();
                do {
                    int i = query.getInt(0);
                    zzbul zzad = zzbul.zzad(query.getBlob(1));
                    zzf com_google_android_gms_internal_zzauh_zzf = new zzf();
                    try {
                        com_google_android_gms_internal_zzauh_zzf.zzb(zzad);
                        try {
                            arrayMap.put(Integer.valueOf(i), com_google_android_gms_internal_zzauh_zzf);
                        } catch (SQLiteException e2) {
                            e = e2;
                        }
                    } catch (IOException e3) {
                        zzJt().zzLa().zzd("Failed to merge filter results. appId, audienceId, error", zzati.zzfI(str), Integer.valueOf(i), e3);
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
                zzJt().zzLa().zze("Database error querying filter results. appId", zzati.zzfI(str), e);
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
    void zzfD(String str) {
        zznA();
        zzmq();
        zzac.zzdv(str);
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            String[] strArr = new String[]{str};
            int delete = writableDatabase.delete("audience_filter_values", "app_id=?", strArr) + (((((((writableDatabase.delete("events", "app_id=?", strArr) + 0) + writableDatabase.delete("user_attributes", "app_id=?", strArr)) + writableDatabase.delete("apps", "app_id=?", strArr)) + writableDatabase.delete("raw_events", "app_id=?", strArr)) + writableDatabase.delete("raw_events_metadata", "app_id=?", strArr)) + writableDatabase.delete("event_filters", "app_id=?", strArr)) + writableDatabase.delete("property_filters", "app_id=?", strArr));
            if (delete > 0) {
                zzJt().zzLg().zze("Deleted application data. app, records", str, Integer.valueOf(delete));
            }
        } catch (SQLiteException e) {
            zzJt().zzLa().zze("Error deleting application data. appId, error", zzati.zzfI(str), e);
        }
    }

    @WorkerThread
    public long zzfE(String str) {
        zzac.zzdv(str);
        zzmq();
        zznA();
        return zzU(str, "first_open_count");
    }

    public void zzfF(String str) {
        try {
            getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str, str});
        } catch (SQLiteException e) {
            zzJt().zzLa().zze("Failed to remove unused event metadata. appId", zzati.zzfI(str), e);
        }
    }

    public long zzfG(String str) {
        zzac.zzdv(str);
        return zza("select count(1) from events where app_id=? and name not like '!_%' escape '!'", new String[]{str}, 0);
    }

    @WorkerThread
    public List<zzaud> zzfx(String str) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzac.zzdv(str);
        zzmq();
        zznA();
        List<zzaud> arrayList = new ArrayList();
        try {
            Cursor query = getWritableDatabase().query("user_attributes", new String[]{"name", "set_timestamp", Param.VALUE}, "app_id=?", new String[]{str}, null, null, "rowid", String.valueOf(zzJv().zzKf()));
            try {
                if (query.moveToFirst()) {
                    do {
                        String string = query.getString(0);
                        long j = query.getLong(1);
                        Object zzb = zzb(query, 2);
                        if (zzb == null) {
                            zzJt().zzLa().zzj("Read invalid user property value, ignoring it. appId", zzati.zzfI(str));
                        } else {
                            arrayList.add(new zzaud(str, string, j, zzb));
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
                zzJt().zzLa().zze("Error querying user properties. appId", zzati.zzfI(str), e);
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
    public zzasp zzfy(String str) {
        Cursor query;
        Object e;
        Throwable th;
        zzac.zzdv(str);
        zzmq();
        zznA();
        try {
            query = getWritableDatabase().query("apps", new String[]{"app_instance_id", "gmp_app_id", "resettable_device_id_hash", "last_bundle_index", "last_bundle_start_timestamp", "last_bundle_end_timestamp", "app_version", "app_store", "gmp_version", "dev_cert_hash", "measurement_enabled", "day", "daily_public_events_count", "daily_events_count", "daily_conversions_count", "config_fetched_time", "failed_config_fetch_time", "app_version_int", "firebase_instance_id", "daily_error_events_count", "daily_realtime_events_count", "health_monitor_sample"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zzasp com_google_android_gms_internal_zzasp = new zzasp(this.zzbpw, str);
                    com_google_android_gms_internal_zzasp.zzfh(query.getString(0));
                    com_google_android_gms_internal_zzasp.zzfi(query.getString(1));
                    com_google_android_gms_internal_zzasp.zzfj(query.getString(2));
                    com_google_android_gms_internal_zzasp.zzac(query.getLong(3));
                    com_google_android_gms_internal_zzasp.zzX(query.getLong(4));
                    com_google_android_gms_internal_zzasp.zzY(query.getLong(5));
                    com_google_android_gms_internal_zzasp.setAppVersion(query.getString(6));
                    com_google_android_gms_internal_zzasp.zzfl(query.getString(7));
                    com_google_android_gms_internal_zzasp.zzaa(query.getLong(8));
                    com_google_android_gms_internal_zzasp.zzab(query.getLong(9));
                    com_google_android_gms_internal_zzasp.setMeasurementEnabled((query.isNull(10) ? 1 : query.getInt(10)) != 0);
                    com_google_android_gms_internal_zzasp.zzaf(query.getLong(11));
                    com_google_android_gms_internal_zzasp.zzag(query.getLong(12));
                    com_google_android_gms_internal_zzasp.zzah(query.getLong(13));
                    com_google_android_gms_internal_zzasp.zzai(query.getLong(14));
                    com_google_android_gms_internal_zzasp.zzad(query.getLong(15));
                    com_google_android_gms_internal_zzasp.zzae(query.getLong(16));
                    com_google_android_gms_internal_zzasp.zzZ(query.isNull(17) ? -2147483648L : (long) query.getInt(17));
                    com_google_android_gms_internal_zzasp.zzfk(query.getString(18));
                    com_google_android_gms_internal_zzasp.zzak(query.getLong(19));
                    com_google_android_gms_internal_zzasp.zzaj(query.getLong(20));
                    com_google_android_gms_internal_zzasp.zzfm(query.getString(21));
                    com_google_android_gms_internal_zzasp.zzJw();
                    if (query.moveToNext()) {
                        zzJt().zzLa().zzj("Got multiple records for app, expected one. appId", zzati.zzfI(str));
                    }
                    if (query == null) {
                        return com_google_android_gms_internal_zzasp;
                    }
                    query.close();
                    return com_google_android_gms_internal_zzasp;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzJt().zzLa().zze("Error querying app. appId", zzati.zzfI(str), e);
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
            zzJt().zzLa().zze("Error querying app. appId", zzati.zzfI(str), e);
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

    public long zzfz(String str) {
        zzac.zzdv(str);
        zzmq();
        zznA();
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            String valueOf = String.valueOf(zzJv().zzfw(str));
            return (long) writableDatabase.delete("raw_events", "rowid in (select rowid from raw_events where app_id=? order by rowid desc limit -1 offset ?)", new String[]{str, valueOf});
        } catch (SQLiteException e) {
            zzJt().zzLa().zze("Error deleting over the limit events. appId", zzati.zzfI(str), e);
            return 0;
        }
    }

    protected void zzmr() {
    }

    @WorkerThread
    public List<Pair<zze, Long>> zzn(String str, int i, int i2) {
        List<Pair<zze, Long>> arrayList;
        Object e;
        Cursor cursor;
        Throwable th;
        boolean z = true;
        zzmq();
        zznA();
        zzac.zzas(i > 0);
        if (i2 <= 0) {
            z = false;
        }
        zzac.zzas(z);
        zzac.zzdv(str);
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
                            byte[] zzx = zzJp().zzx(query.getBlob(1));
                            if (!arrayList.isEmpty() && zzx.length + i3 > i2) {
                                break;
                            }
                            zzbul zzad = zzbul.zzad(zzx);
                            zze com_google_android_gms_internal_zzauh_zze = new zze();
                            try {
                                com_google_android_gms_internal_zzauh_zze.zzb(zzad);
                                length = zzx.length + i3;
                                arrayList.add(Pair.create(com_google_android_gms_internal_zzauh_zze, Long.valueOf(j)));
                            } catch (IOException e2) {
                                zzJt().zzLa().zze("Failed to merge queued bundle. appId", zzati.zzfI(str), e2);
                                length = i3;
                            }
                            if (!query.moveToNext() || length > i2) {
                                break;
                            }
                            i3 = length;
                        } catch (IOException e22) {
                            zzJt().zzLa().zze("Failed to unzip queued bundle. appId", zzati.zzfI(str), e22);
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
                zzJt().zzLa().zze("Error querying bundles. appId", zzati.zzfI(str), e);
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

    String zznV() {
        return zzJv().zzoV();
    }

    @WorkerThread
    public void zzz(String str, int i) {
        zzac.zzdv(str);
        zzmq();
        zznA();
        try {
            getWritableDatabase().execSQL("delete from user_attributes where app_id=? and name in (select name from user_attributes where app_id=? and name like '_ltv_%' order by set_timestamp desc limit ?,10);", new String[]{str, str, String.valueOf(i)});
        } catch (SQLiteException e) {
            zzJt().zzLa().zze("Error pruning currencies. appId", zzati.zzfI(str), e);
        }
    }
}
