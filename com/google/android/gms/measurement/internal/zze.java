package com.google.android.gms.measurement.internal;

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
import com.google.android.gms.internal.zzarc;
import com.google.android.gms.internal.zzard;
import com.google.android.gms.internal.zzvm.zzf;
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

class zze extends zzaa {
    private static final Map<String, String> aoa = new ArrayMap(17);
    private static final Map<String, String> aob = new ArrayMap(1);
    private static final Map<String, String> aoc = new ArrayMap(1);
    private final zzc aod = new zzc(this, getContext(), zzabs());
    private final zzah aoe = new zzah(zzaan());

    public static class zza {
        long aof;
        long aog;
        long aoh;
        long aoi;
        long aoj;
    }

    interface zzb {
        boolean zza(long j, com.google.android.gms.internal.zzvm.zzb com_google_android_gms_internal_zzvm_zzb);

        void zzb(com.google.android.gms.internal.zzvm.zze com_google_android_gms_internal_zzvm_zze);
    }

    private class zzc extends SQLiteOpenHelper {
        final /* synthetic */ zze aok;

        zzc(zze com_google_android_gms_measurement_internal_zze, Context context, String str) {
            this.aok = com_google_android_gms_measurement_internal_zze;
            super(context, str, null, 1);
        }

        @WorkerThread
        private void zza(SQLiteDatabase sQLiteDatabase, String str, String str2, String str3, Map<String, String> map) throws SQLiteException {
            if (!zza(sQLiteDatabase, str)) {
                sQLiteDatabase.execSQL(str2);
            }
            try {
                zza(sQLiteDatabase, str, str3, map);
            } catch (SQLiteException e) {
                this.aok.zzbvg().zzbwc().zzj("Failed to verify columns on table that was just created", str);
                throw e;
            }
        }

        @WorkerThread
        private void zza(SQLiteDatabase sQLiteDatabase, String str, String str2, Map<String, String> map) throws SQLiteException {
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
                this.aok.zzbvg().zzbwe().zze("Table has extra columns. table, columns", str, TextUtils.join(", ", zzb));
            }
        }

        @WorkerThread
        private boolean zza(SQLiteDatabase sQLiteDatabase, String str) {
            Cursor query;
            Object e;
            Throwable th;
            Cursor cursor = null;
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
                        this.aok.zzbvg().zzbwe().zze("Error querying for table", str, e);
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
                this.aok.zzbvg().zzbwe().zze("Error querying for table", str, e);
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
        private Set<String> zzb(SQLiteDatabase sQLiteDatabase, String str) {
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
        public SQLiteDatabase getWritableDatabase() {
            if (this.aok.aoe.zzz(this.aok.zzbvi().zzbtz())) {
                SQLiteDatabase writableDatabase;
                try {
                    writableDatabase = super.getWritableDatabase();
                } catch (SQLiteException e) {
                    this.aok.aoe.start();
                    this.aok.zzbvg().zzbwc().log("Opening the database failed, dropping and recreating it");
                    String zzabs = this.aok.zzabs();
                    if (!this.aok.getContext().getDatabasePath(zzabs).delete()) {
                        this.aok.zzbvg().zzbwc().zzj("Failed to delete corrupted db file", zzabs);
                    }
                    try {
                        writableDatabase = super.getWritableDatabase();
                        this.aok.aoe.clear();
                    } catch (SQLiteException e2) {
                        this.aok.zzbvg().zzbwc().zzj("Failed to open freshly created database", e2);
                        throw e2;
                    }
                }
                return writableDatabase;
            }
            throw new SQLiteException("Database open failed");
        }

        @WorkerThread
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            if (VERSION.SDK_INT >= 9) {
                File file = new File(sQLiteDatabase.getPath());
                if (!file.setReadable(false, false)) {
                    this.aok.zzbvg().zzbwe().log("Failed to turn off database read permission");
                }
                if (!file.setWritable(false, false)) {
                    this.aok.zzbvg().zzbwe().log("Failed to turn off database write permission");
                }
                if (!file.setReadable(true, true)) {
                    this.aok.zzbvg().zzbwe().log("Failed to turn on database read permission for owner");
                }
                if (!file.setWritable(true, true)) {
                    this.aok.zzbvg().zzbwe().log("Failed to turn on database write permission for owner");
                }
            }
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
            zza(sQLiteDatabase, "events", "CREATE TABLE IF NOT EXISTS events ( app_id TEXT NOT NULL, name TEXT NOT NULL, lifetime_count INTEGER NOT NULL, current_bundle_count INTEGER NOT NULL, last_fire_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,lifetime_count,current_bundle_count,last_fire_timestamp", null);
            zza(sQLiteDatabase, "user_attributes", "CREATE TABLE IF NOT EXISTS user_attributes ( app_id TEXT NOT NULL, name TEXT NOT NULL, set_timestamp INTEGER NOT NULL, value BLOB NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,set_timestamp,value", null);
            zza(sQLiteDatabase, "apps", "CREATE TABLE IF NOT EXISTS apps ( app_id TEXT NOT NULL, app_instance_id TEXT, gmp_app_id TEXT, resettable_device_id_hash TEXT, last_bundle_index INTEGER NOT NULL, last_bundle_end_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id)) ;", "app_id,app_instance_id,gmp_app_id,resettable_device_id_hash,last_bundle_index,last_bundle_end_timestamp", zze.aoa);
            zza(sQLiteDatabase, "queue", "CREATE TABLE IF NOT EXISTS queue ( app_id TEXT NOT NULL, bundle_end_timestamp INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,bundle_end_timestamp,data", zze.aoc);
            zza(sQLiteDatabase, "raw_events_metadata", "CREATE TABLE IF NOT EXISTS raw_events_metadata ( app_id TEXT NOT NULL, metadata_fingerprint INTEGER NOT NULL, metadata BLOB NOT NULL, PRIMARY KEY (app_id, metadata_fingerprint));", "app_id,metadata_fingerprint,metadata", null);
            zza(sQLiteDatabase, "raw_events", "CREATE TABLE IF NOT EXISTS raw_events ( app_id TEXT NOT NULL, name TEXT NOT NULL, timestamp INTEGER NOT NULL, metadata_fingerprint INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,name,timestamp,metadata_fingerprint,data", zze.aob);
            zza(sQLiteDatabase, "event_filters", "CREATE TABLE IF NOT EXISTS event_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, event_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, event_name, audience_id, filter_id));", "app_id,audience_id,filter_id,event_name,data", null);
            zza(sQLiteDatabase, "property_filters", "CREATE TABLE IF NOT EXISTS property_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, property_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, property_name, audience_id, filter_id));", "app_id,audience_id,filter_id,property_name,data", null);
            zza(sQLiteDatabase, "audience_filter_values", "CREATE TABLE IF NOT EXISTS audience_filter_values ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, current_results BLOB, PRIMARY KEY (app_id, audience_id));", "app_id,audience_id,current_results", null);
            zza(sQLiteDatabase, "app2", "CREATE TABLE IF NOT EXISTS app2 ( app_id TEXT NOT NULL, first_open_count INTEGER NOT NULL, PRIMARY KEY (app_id));", "app_id,first_open_count", null);
        }

        @WorkerThread
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    static {
        aoa.put("app_version", "ALTER TABLE apps ADD COLUMN app_version TEXT;");
        aoa.put("app_store", "ALTER TABLE apps ADD COLUMN app_store TEXT;");
        aoa.put("gmp_version", "ALTER TABLE apps ADD COLUMN gmp_version INTEGER;");
        aoa.put("dev_cert_hash", "ALTER TABLE apps ADD COLUMN dev_cert_hash INTEGER;");
        aoa.put("measurement_enabled", "ALTER TABLE apps ADD COLUMN measurement_enabled INTEGER;");
        aoa.put("last_bundle_start_timestamp", "ALTER TABLE apps ADD COLUMN last_bundle_start_timestamp INTEGER;");
        aoa.put("day", "ALTER TABLE apps ADD COLUMN day INTEGER;");
        aoa.put("daily_public_events_count", "ALTER TABLE apps ADD COLUMN daily_public_events_count INTEGER;");
        aoa.put("daily_events_count", "ALTER TABLE apps ADD COLUMN daily_events_count INTEGER;");
        aoa.put("daily_conversions_count", "ALTER TABLE apps ADD COLUMN daily_conversions_count INTEGER;");
        aoa.put("remote_config", "ALTER TABLE apps ADD COLUMN remote_config BLOB;");
        aoa.put("config_fetched_time", "ALTER TABLE apps ADD COLUMN config_fetched_time INTEGER;");
        aoa.put("failed_config_fetch_time", "ALTER TABLE apps ADD COLUMN failed_config_fetch_time INTEGER;");
        aoa.put("app_version_int", "ALTER TABLE apps ADD COLUMN app_version_int INTEGER;");
        aoa.put("firebase_instance_id", "ALTER TABLE apps ADD COLUMN firebase_instance_id TEXT;");
        aoa.put("daily_error_events_count", "ALTER TABLE apps ADD COLUMN daily_error_events_count INTEGER;");
        aoa.put("daily_realtime_events_count", "ALTER TABLE apps ADD COLUMN daily_realtime_events_count INTEGER;");
        aob.put("realtime", "ALTER TABLE raw_events ADD COLUMN realtime INTEGER;");
        aoc.put("has_realtime", "ALTER TABLE queue ADD COLUMN has_realtime INTEGER;");
    }

    zze(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
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
            zzbvg().zzbwc().zze("Database error", str, e);
            throw e;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @WorkerThread
    private void zza(String str, com.google.android.gms.internal.zzvk.zza com_google_android_gms_internal_zzvk_zza) {
        Object obj = null;
        zzaax();
        zzyl();
        zzac.zzhz(str);
        zzac.zzy(com_google_android_gms_internal_zzvk_zza);
        zzac.zzy(com_google_android_gms_internal_zzvk_zza.asC);
        zzac.zzy(com_google_android_gms_internal_zzvk_zza.asB);
        if (com_google_android_gms_internal_zzvk_zza.asA == null) {
            zzbvg().zzbwe().log("Audience with no ID");
            return;
        }
        int intValue = com_google_android_gms_internal_zzvk_zza.asA.intValue();
        for (com.google.android.gms.internal.zzvk.zzb com_google_android_gms_internal_zzvk_zzb : com_google_android_gms_internal_zzvk_zza.asC) {
            if (com_google_android_gms_internal_zzvk_zzb.asE == null) {
                zzbvg().zzbwe().zze("Event filter with no ID. Audience definition ignored. appId, audienceId", str, com_google_android_gms_internal_zzvk_zza.asA);
                return;
            }
        }
        for (com.google.android.gms.internal.zzvk.zze com_google_android_gms_internal_zzvk_zze : com_google_android_gms_internal_zzvk_zza.asB) {
            if (com_google_android_gms_internal_zzvk_zze.asE == null) {
                zzbvg().zzbwe().zze("Property filter with no ID. Audience definition ignored. appId, audienceId", str, com_google_android_gms_internal_zzvk_zza.asA);
                return;
            }
        }
        Object obj2 = 1;
        for (com.google.android.gms.internal.zzvk.zzb zza : com_google_android_gms_internal_zzvk_zza.asC) {
            if (!zza(str, intValue, zza)) {
                obj2 = null;
                break;
            }
        }
        if (obj2 != null) {
            for (com.google.android.gms.internal.zzvk.zze zza2 : com_google_android_gms_internal_zzvk_zza.asB) {
                if (!zza(str, intValue, zza2)) {
                    break;
                }
            }
        }
        obj = obj2;
        if (obj == null) {
            zzaa(str, intValue);
        }
    }

    @WorkerThread
    private boolean zza(String str, int i, com.google.android.gms.internal.zzvk.zzb com_google_android_gms_internal_zzvk_zzb) {
        zzaax();
        zzyl();
        zzac.zzhz(str);
        zzac.zzy(com_google_android_gms_internal_zzvk_zzb);
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzvk_zzb.asF)) {
            zzbvg().zzbwe().zze("Event filter had no event name. Audience definition ignored. audienceId, filterId", Integer.valueOf(i), String.valueOf(com_google_android_gms_internal_zzvk_zzb.asE));
            return false;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzvk_zzb.db()];
            zzard zzbe = zzard.zzbe(bArr);
            com_google_android_gms_internal_zzvk_zzb.zza(zzbe);
            zzbe.cO();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("filter_id", com_google_android_gms_internal_zzvk_zzb.asE);
            contentValues.put("event_name", com_google_android_gms_internal_zzvk_zzb.asF);
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("event_filters", null, contentValues, 5) == -1) {
                    zzbvg().zzbwc().log("Failed to insert event filter (got -1)");
                }
                return true;
            } catch (SQLiteException e) {
                zzbvg().zzbwc().zzj("Error storing event filter", e);
                return false;
            }
        } catch (IOException e2) {
            zzbvg().zzbwc().zzj("Configuration loss. Failed to serialize event filter", e2);
            return false;
        }
    }

    @WorkerThread
    private boolean zza(String str, int i, com.google.android.gms.internal.zzvk.zze com_google_android_gms_internal_zzvk_zze) {
        zzaax();
        zzyl();
        zzac.zzhz(str);
        zzac.zzy(com_google_android_gms_internal_zzvk_zze);
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzvk_zze.asU)) {
            zzbvg().zzbwe().zze("Property filter had no property name. Audience definition ignored. audienceId, filterId", Integer.valueOf(i), String.valueOf(com_google_android_gms_internal_zzvk_zze.asE));
            return false;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzvk_zze.db()];
            zzard zzbe = zzard.zzbe(bArr);
            com_google_android_gms_internal_zzvk_zze.zza(zzbe);
            zzbe.cO();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("filter_id", com_google_android_gms_internal_zzvk_zze.asE);
            contentValues.put("property_name", com_google_android_gms_internal_zzvk_zze.asU);
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("property_filters", null, contentValues, 5) != -1) {
                    return true;
                }
                zzbvg().zzbwc().log("Failed to insert property filter (got -1)");
                return false;
            } catch (SQLiteException e) {
                zzbvg().zzbwc().zzj("Error storing property filter", e);
                return false;
            }
        } catch (IOException e2) {
            zzbvg().zzbwc().zzj("Configuration loss. Failed to serialize property filter", e2);
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
            zzbvg().zzbwc().zze("Database error", str, e);
            throw e;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean zzbvr() {
        return getContext().getDatabasePath(zzabs()).exists();
    }

    @WorkerThread
    public void beginTransaction() {
        zzaax();
        getWritableDatabase().beginTransaction();
    }

    @WorkerThread
    public void endTransaction() {
        zzaax();
        getWritableDatabase().endTransaction();
    }

    @WorkerThread
    SQLiteDatabase getWritableDatabase() {
        zzyl();
        try {
            return this.aod.getWritableDatabase();
        } catch (SQLiteException e) {
            zzbvg().zzbwe().zzj("Error opening database", e);
            throw e;
        }
    }

    @WorkerThread
    public void setTransactionSuccessful() {
        zzaax();
        getWritableDatabase().setTransactionSuccessful();
    }

    public long zza(com.google.android.gms.internal.zzvm.zze com_google_android_gms_internal_zzvm_zze) throws IOException {
        zzyl();
        zzaax();
        zzac.zzy(com_google_android_gms_internal_zzvm_zze);
        zzac.zzhz(com_google_android_gms_internal_zzvm_zze.zzck);
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzvm_zze.db()];
            zzard zzbe = zzard.zzbe(bArr);
            com_google_android_gms_internal_zzvm_zze.zza(zzbe);
            zzbe.cO();
            long zzy = zzbvc().zzy(bArr);
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzvm_zze.zzck);
            contentValues.put("metadata_fingerprint", Long.valueOf(zzy));
            contentValues.put(TtmlNode.TAG_METADATA, bArr);
            try {
                getWritableDatabase().insertWithOnConflict("raw_events_metadata", null, contentValues, 4);
                return zzy;
            } catch (SQLiteException e) {
                zzbvg().zzbwc().zzj("Error storing raw event metadata", e);
                throw e;
            }
        } catch (IOException e2) {
            zzbvg().zzbwc().zzj("Data loss. Failed to serialize event metadata", e2);
            throw e2;
        }
    }

    @WorkerThread
    public zza zza(long j, String str, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        Object e;
        Throwable th;
        zzac.zzhz(str);
        zzyl();
        zzaax();
        String[] strArr = new String[]{str};
        zza com_google_android_gms_measurement_internal_zze_zza = new zza();
        Cursor query;
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            query = writableDatabase.query("apps", new String[]{"day", "daily_events_count", "daily_public_events_count", "daily_conversions_count", "daily_error_events_count", "daily_realtime_events_count"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    if (query.getLong(0) == j) {
                        com_google_android_gms_measurement_internal_zze_zza.aog = query.getLong(1);
                        com_google_android_gms_measurement_internal_zze_zza.aof = query.getLong(2);
                        com_google_android_gms_measurement_internal_zze_zza.aoh = query.getLong(3);
                        com_google_android_gms_measurement_internal_zze_zza.aoi = query.getLong(4);
                        com_google_android_gms_measurement_internal_zze_zza.aoj = query.getLong(5);
                    }
                    if (z) {
                        com_google_android_gms_measurement_internal_zze_zza.aog++;
                    }
                    if (z2) {
                        com_google_android_gms_measurement_internal_zze_zza.aof++;
                    }
                    if (z3) {
                        com_google_android_gms_measurement_internal_zze_zza.aoh++;
                    }
                    if (z4) {
                        com_google_android_gms_measurement_internal_zze_zza.aoi++;
                    }
                    if (z5) {
                        com_google_android_gms_measurement_internal_zze_zza.aoj++;
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("day", Long.valueOf(j));
                    contentValues.put("daily_public_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zze_zza.aof));
                    contentValues.put("daily_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zze_zza.aog));
                    contentValues.put("daily_conversions_count", Long.valueOf(com_google_android_gms_measurement_internal_zze_zza.aoh));
                    contentValues.put("daily_error_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zze_zza.aoi));
                    contentValues.put("daily_realtime_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zze_zza.aoj));
                    writableDatabase.update("apps", contentValues, "app_id=?", strArr);
                    if (query != null) {
                        query.close();
                    }
                    return com_google_android_gms_measurement_internal_zze_zza;
                }
                zzbvg().zzbwe().zzj("Not updating daily counts, app is not known", str);
                if (query != null) {
                    query.close();
                }
                return com_google_android_gms_measurement_internal_zze_zza;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzbvg().zzbwc().zzj("Error updating daily counts", e);
                    if (query != null) {
                        query.close();
                    }
                    return com_google_android_gms_measurement_internal_zze_zza;
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
            zzbvg().zzbwc().zzj("Error updating daily counts", e);
            if (query != null) {
                query.close();
            }
            return com_google_android_gms_measurement_internal_zze_zza;
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
        zzac.zzhz(str);
        zzac.zzy(obj);
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
    public void zza(com.google.android.gms.internal.zzvm.zze com_google_android_gms_internal_zzvm_zze, boolean z) {
        zzyl();
        zzaax();
        zzac.zzy(com_google_android_gms_internal_zzvm_zze);
        zzac.zzhz(com_google_android_gms_internal_zzvm_zze.zzck);
        zzac.zzy(com_google_android_gms_internal_zzvm_zze.atA);
        zzbvl();
        long currentTimeMillis = zzaan().currentTimeMillis();
        if (com_google_android_gms_internal_zzvm_zze.atA.longValue() < currentTimeMillis - zzbvi().zzbue() || com_google_android_gms_internal_zzvm_zze.atA.longValue() > zzbvi().zzbue() + currentTimeMillis) {
            zzbvg().zzbwe().zze("Storing bundle outside of the max uploading time span. now, timestamp", Long.valueOf(currentTimeMillis), com_google_android_gms_internal_zzvm_zze.atA);
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzvm_zze.db()];
            zzard zzbe = zzard.zzbe(bArr);
            com_google_android_gms_internal_zzvm_zze.zza(zzbe);
            zzbe.cO();
            bArr = zzbvc().zzj(bArr);
            zzbvg().zzbwj().zzj("Saving bundle, size", Integer.valueOf(bArr.length));
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzvm_zze.zzck);
            contentValues.put("bundle_end_timestamp", com_google_android_gms_internal_zzvm_zze.atA);
            contentValues.put("data", bArr);
            contentValues.put("has_realtime", Integer.valueOf(z ? 1 : 0));
            try {
                if (getWritableDatabase().insert("queue", null, contentValues) == -1) {
                    zzbvg().zzbwc().log("Failed to insert bundle (got -1)");
                }
            } catch (SQLiteException e) {
                zzbvg().zzbwc().zzj("Error storing bundle", e);
            }
        } catch (IOException e2) {
            zzbvg().zzbwc().zzj("Data loss. Failed to serialize bundle", e2);
        }
    }

    @WorkerThread
    public void zza(zza com_google_android_gms_measurement_internal_zza) {
        zzac.zzy(com_google_android_gms_measurement_internal_zza);
        zzyl();
        zzaax();
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_measurement_internal_zza.zzti());
        contentValues.put("app_instance_id", com_google_android_gms_measurement_internal_zza.zzayn());
        contentValues.put("gmp_app_id", com_google_android_gms_measurement_internal_zza.zzbsr());
        contentValues.put("resettable_device_id_hash", com_google_android_gms_measurement_internal_zza.zzbss());
        contentValues.put("last_bundle_index", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtb()));
        contentValues.put("last_bundle_start_timestamp", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbsu()));
        contentValues.put("last_bundle_end_timestamp", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbsv()));
        contentValues.put("app_version", com_google_android_gms_measurement_internal_zza.zzyt());
        contentValues.put("app_store", com_google_android_gms_measurement_internal_zza.zzbsx());
        contentValues.put("gmp_version", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbsy()));
        contentValues.put("dev_cert_hash", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbsz()));
        contentValues.put("measurement_enabled", Boolean.valueOf(com_google_android_gms_measurement_internal_zza.zzbta()));
        contentValues.put("day", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtf()));
        contentValues.put("daily_public_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtg()));
        contentValues.put("daily_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbth()));
        contentValues.put("daily_conversions_count", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbti()));
        contentValues.put("config_fetched_time", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtc()));
        contentValues.put("failed_config_fetch_time", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtd()));
        contentValues.put("app_version_int", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbsw()));
        contentValues.put("firebase_instance_id", com_google_android_gms_measurement_internal_zza.zzbst());
        contentValues.put("daily_error_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtk()));
        contentValues.put("daily_realtime_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtj()));
        try {
            if (getWritableDatabase().insertWithOnConflict("apps", null, contentValues, 5) == -1) {
                zzbvg().zzbwc().log("Failed to insert/update app (got -1)");
            }
        } catch (SQLiteException e) {
            zzbvg().zzbwc().zzj("Error storing app", e);
        }
    }

    public void zza(zzh com_google_android_gms_measurement_internal_zzh, long j, boolean z) {
        int i = 0;
        zzyl();
        zzaax();
        zzac.zzy(com_google_android_gms_measurement_internal_zzh);
        zzac.zzhz(com_google_android_gms_measurement_internal_zzh.zzcpe);
        com.google.android.gms.internal.zzvm.zzb com_google_android_gms_internal_zzvm_zzb = new com.google.android.gms.internal.zzvm.zzb();
        com_google_android_gms_internal_zzvm_zzb.atq = Long.valueOf(com_google_android_gms_measurement_internal_zzh.aor);
        com_google_android_gms_internal_zzvm_zzb.ato = new com.google.android.gms.internal.zzvm.zzc[com_google_android_gms_measurement_internal_zzh.aos.size()];
        Iterator it = com_google_android_gms_measurement_internal_zzh.aos.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            String str = (String) it.next();
            com.google.android.gms.internal.zzvm.zzc com_google_android_gms_internal_zzvm_zzc = new com.google.android.gms.internal.zzvm.zzc();
            int i3 = i2 + 1;
            com_google_android_gms_internal_zzvm_zzb.ato[i2] = com_google_android_gms_internal_zzvm_zzc;
            com_google_android_gms_internal_zzvm_zzc.name = str;
            zzbvc().zza(com_google_android_gms_internal_zzvm_zzc, com_google_android_gms_measurement_internal_zzh.aos.get(str));
            i2 = i3;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzvm_zzb.db()];
            zzard zzbe = zzard.zzbe(bArr);
            com_google_android_gms_internal_zzvm_zzb.zza(zzbe);
            zzbe.cO();
            zzbvg().zzbwj().zze("Saving event, name, data size", com_google_android_gms_measurement_internal_zzh.mName, Integer.valueOf(bArr.length));
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_measurement_internal_zzh.zzcpe);
            contentValues.put("name", com_google_android_gms_measurement_internal_zzh.mName);
            contentValues.put("timestamp", Long.valueOf(com_google_android_gms_measurement_internal_zzh.tr));
            contentValues.put("metadata_fingerprint", Long.valueOf(j));
            contentValues.put("data", bArr);
            str = "realtime";
            if (z) {
                i = 1;
            }
            contentValues.put(str, Integer.valueOf(i));
            try {
                if (getWritableDatabase().insert("raw_events", null, contentValues) == -1) {
                    zzbvg().zzbwc().log("Failed to insert raw event (got -1)");
                }
            } catch (SQLiteException e) {
                zzbvg().zzbwc().zzj("Error storing raw event", e);
            }
        } catch (IOException e2) {
            zzbvg().zzbwc().zzj("Data loss. Failed to serialize event params/data", e2);
        }
    }

    @WorkerThread
    public void zza(zzi com_google_android_gms_measurement_internal_zzi) {
        zzac.zzy(com_google_android_gms_measurement_internal_zzi);
        zzyl();
        zzaax();
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_measurement_internal_zzi.zzcpe);
        contentValues.put("name", com_google_android_gms_measurement_internal_zzi.mName);
        contentValues.put("lifetime_count", Long.valueOf(com_google_android_gms_measurement_internal_zzi.aot));
        contentValues.put("current_bundle_count", Long.valueOf(com_google_android_gms_measurement_internal_zzi.aou));
        contentValues.put("last_fire_timestamp", Long.valueOf(com_google_android_gms_measurement_internal_zzi.aov));
        try {
            if (getWritableDatabase().insertWithOnConflict("events", null, contentValues, 5) == -1) {
                zzbvg().zzbwc().log("Failed to insert/update event aggregates (got -1)");
            }
        } catch (SQLiteException e) {
            zzbvg().zzbwc().zzj("Error storing event aggregates", e);
        }
    }

    void zza(String str, int i, zzf com_google_android_gms_internal_zzvm_zzf) {
        zzaax();
        zzyl();
        zzac.zzhz(str);
        zzac.zzy(com_google_android_gms_internal_zzvm_zzf);
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzvm_zzf.db()];
            zzard zzbe = zzard.zzbe(bArr);
            com_google_android_gms_internal_zzvm_zzf.zza(zzbe);
            zzbe.cO();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("current_results", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("audience_filter_values", null, contentValues, 5) == -1) {
                    zzbvg().zzbwc().log("Failed to insert filter results (got -1)");
                }
            } catch (SQLiteException e) {
                zzbvg().zzbwc().zzj("Error storing filter results", e);
            }
        } catch (IOException e2) {
            zzbvg().zzbwc().zzj("Configuration loss. Failed to serialize filter results", e2);
        }
    }

    public void zza(String str, long j, zzb com_google_android_gms_measurement_internal_zze_zzb) {
        Object string;
        Cursor cursor;
        Object e;
        Throwable th;
        Cursor cursor2 = null;
        zzac.zzy(com_google_android_gms_measurement_internal_zze_zzb);
        zzyl();
        zzaax();
        try {
            String str2;
            SQLiteDatabase writableDatabase = getWritableDatabase();
            String string2;
            if (TextUtils.isEmpty(str)) {
                cursor2 = writableDatabase.rawQuery("select app_id, metadata_fingerprint from raw_events where app_id in (select app_id from apps where config_fetched_time >= ?) order by rowid limit 1;", new String[]{String.valueOf(j)});
                if (cursor2.moveToFirst()) {
                    string = cursor2.getString(0);
                    string2 = cursor2.getString(1);
                    cursor2.close();
                    str2 = string2;
                    cursor = cursor2;
                } else if (cursor2 != null) {
                    cursor2.close();
                    return;
                } else {
                    return;
                }
            }
            cursor2 = writableDatabase.rawQuery("select metadata_fingerprint from raw_events where app_id = ? order by rowid limit 1;", new String[]{str});
            if (cursor2.moveToFirst()) {
                string2 = cursor2.getString(0);
                cursor2.close();
                str2 = string2;
                cursor = cursor2;
            } else if (cursor2 != null) {
                cursor2.close();
                return;
            } else {
                return;
            }
            try {
                cursor = writableDatabase.query("raw_events_metadata", new String[]{TtmlNode.TAG_METADATA}, "app_id=? and metadata_fingerprint=?", new String[]{string, str2}, null, null, "rowid", "2");
                if (cursor.moveToFirst()) {
                    zzarc zzbd = zzarc.zzbd(cursor.getBlob(0));
                    com.google.android.gms.internal.zzvm.zze com_google_android_gms_internal_zzvm_zze = new com.google.android.gms.internal.zzvm.zze();
                    try {
                        com.google.android.gms.internal.zzvm.zze com_google_android_gms_internal_zzvm_zze2 = (com.google.android.gms.internal.zzvm.zze) com_google_android_gms_internal_zzvm_zze.zzb(zzbd);
                        if (cursor.moveToNext()) {
                            zzbvg().zzbwe().log("Get multiple raw event metadata records, expected one");
                        }
                        cursor.close();
                        com_google_android_gms_measurement_internal_zze_zzb.zzb(com_google_android_gms_internal_zzvm_zze);
                        cursor2 = writableDatabase.query("raw_events", new String[]{"rowid", "name", "timestamp", "data"}, "app_id=? and metadata_fingerprint=?", new String[]{string, str2}, null, null, "rowid", null);
                        if (cursor2.moveToFirst()) {
                            do {
                                long j2 = cursor2.getLong(0);
                                zzarc zzbd2 = zzarc.zzbd(cursor2.getBlob(3));
                                com.google.android.gms.internal.zzvm.zzb com_google_android_gms_internal_zzvm_zzb = new com.google.android.gms.internal.zzvm.zzb();
                                try {
                                    com.google.android.gms.internal.zzvm.zzb com_google_android_gms_internal_zzvm_zzb2 = (com.google.android.gms.internal.zzvm.zzb) com_google_android_gms_internal_zzvm_zzb.zzb(zzbd2);
                                    com_google_android_gms_internal_zzvm_zzb.name = cursor2.getString(1);
                                    com_google_android_gms_internal_zzvm_zzb.atp = Long.valueOf(cursor2.getLong(2));
                                    if (!com_google_android_gms_measurement_internal_zze_zzb.zza(j2, com_google_android_gms_internal_zzvm_zzb)) {
                                        if (cursor2 != null) {
                                            cursor2.close();
                                            return;
                                        }
                                        return;
                                    }
                                } catch (IOException e2) {
                                    try {
                                        zzbvg().zzbwc().zze("Data loss. Failed to merge raw event", string, e2);
                                    } catch (SQLiteException e3) {
                                        e = e3;
                                    }
                                }
                            } while (cursor2.moveToNext());
                            if (cursor2 != null) {
                                cursor2.close();
                                return;
                            }
                            return;
                        }
                        zzbvg().zzbwe().log("Raw event data disappeared while in transaction");
                        if (cursor2 != null) {
                            cursor2.close();
                            return;
                        }
                        return;
                    } catch (IOException e22) {
                        zzbvg().zzbwc().zze("Data loss. Failed to merge raw event metadata", string, e22);
                        if (cursor != null) {
                            cursor.close();
                            return;
                        }
                        return;
                    }
                }
                zzbvg().zzbwc().log("Raw event metadata record is missing");
                if (cursor != null) {
                    cursor.close();
                }
            } catch (SQLiteException e4) {
                e = e4;
                cursor2 = cursor;
                try {
                    zzbvg().zzbwc().zzj("Data loss. Error selecting raw event", e);
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    cursor = cursor2;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        } catch (SQLiteException e32) {
            e = e32;
        } catch (Throwable th4) {
            th = th4;
            cursor = cursor2;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    @WorkerThread
    public boolean zza(zzak com_google_android_gms_measurement_internal_zzak) {
        zzac.zzy(com_google_android_gms_measurement_internal_zzak);
        zzyl();
        zzaax();
        if (zzas(com_google_android_gms_measurement_internal_zzak.zzcpe, com_google_android_gms_measurement_internal_zzak.mName) == null) {
            if (zzal.zzmx(com_google_android_gms_measurement_internal_zzak.mName)) {
                if (zzb("select count(1) from user_attributes where app_id=? and name not like '!_%' escape '!'", new String[]{com_google_android_gms_measurement_internal_zzak.zzcpe}) >= ((long) zzbvi().zzbtx())) {
                    return false;
                }
            }
            if (zzb("select count(1) from user_attributes where app_id=?", new String[]{com_google_android_gms_measurement_internal_zzak.zzcpe}) >= ((long) zzbvi().zzbty())) {
                return false;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_measurement_internal_zzak.zzcpe);
        contentValues.put("name", com_google_android_gms_measurement_internal_zzak.mName);
        contentValues.put("set_timestamp", Long.valueOf(com_google_android_gms_measurement_internal_zzak.asy));
        zza(contentValues, Param.VALUE, com_google_android_gms_measurement_internal_zzak.zzctv);
        try {
            if (getWritableDatabase().insertWithOnConflict("user_attributes", null, contentValues, 5) == -1) {
                zzbvg().zzbwc().log("Failed to insert/update user property (got -1)");
            }
        } catch (SQLiteException e) {
            zzbvg().zzbwc().zzj("Error storing user property", e);
        }
        return true;
    }

    @WorkerThread
    void zzaa(String str, int i) {
        zzaax();
        zzyl();
        zzac.zzhz(str);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete("property_filters", "app_id=? and audience_id=?", new String[]{str, String.valueOf(i)});
        writableDatabase.delete("event_filters", "app_id=? and audience_id=?", new String[]{str, String.valueOf(i)});
    }

    String zzabs() {
        if (!zzbvi().zzact()) {
            return zzbvi().zzadt();
        }
        if (zzbvi().zzacu()) {
            return zzbvi().zzadt();
        }
        zzbvg().zzbwf().log("Using secondary database");
        return zzbvi().zzadu();
    }

    public void zzaf(List<Long> list) {
        zzac.zzy(list);
        zzyl();
        zzaax();
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
            zzbvg().zzbwc().zze("Deleted fewer rows from raw events table than expected", Integer.valueOf(delete), Integer.valueOf(list.size()));
        }
    }

    @WorkerThread
    public zzi zzaq(String str, String str2) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzac.zzhz(str);
        zzac.zzhz(str2);
        zzyl();
        zzaax();
        try {
            Cursor query = getWritableDatabase().query("events", new String[]{"lifetime_count", "current_bundle_count", "last_fire_timestamp"}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zzi com_google_android_gms_measurement_internal_zzi = new zzi(str, str2, query.getLong(0), query.getLong(1), query.getLong(2));
                    if (query.moveToNext()) {
                        zzbvg().zzbwc().log("Got multiple records for event aggregates, expected one");
                    }
                    if (query == null) {
                        return com_google_android_gms_measurement_internal_zzi;
                    }
                    query.close();
                    return com_google_android_gms_measurement_internal_zzi;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
                try {
                    zzbvg().zzbwc().zzd("Error querying events", str, str2, e);
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
            zzbvg().zzbwc().zzd("Error querying events", str, str2, e);
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
    public void zzar(String str, String str2) {
        zzac.zzhz(str);
        zzac.zzhz(str2);
        zzyl();
        zzaax();
        try {
            zzbvg().zzbwj().zzj("Deleted user attribute rows:", Integer.valueOf(getWritableDatabase().delete("user_attributes", "app_id=? and name=?", new String[]{str, str2})));
        } catch (SQLiteException e) {
            zzbvg().zzbwc().zzd("Error deleting user attribute", str, str2, e);
        }
    }

    @WorkerThread
    public zzak zzas(String str, String str2) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzac.zzhz(str);
        zzac.zzhz(str2);
        zzyl();
        zzaax();
        try {
            Cursor query = getWritableDatabase().query("user_attributes", new String[]{"set_timestamp", Param.VALUE}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zzak com_google_android_gms_measurement_internal_zzak = new zzak(str, str2, query.getLong(0), zzb(query, 1));
                    if (query.moveToNext()) {
                        zzbvg().zzbwc().log("Got multiple records for user property, expected one");
                    }
                    if (query == null) {
                        return com_google_android_gms_measurement_internal_zzak;
                    }
                    query.close();
                    return com_google_android_gms_measurement_internal_zzak;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                cursor = query;
                try {
                    zzbvg().zzbwc().zzd("Error querying user property", str, str2, e);
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
            zzbvg().zzbwc().zzd("Error querying user property", str, str2, e);
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

    Map<Integer, List<com.google.android.gms.internal.zzvk.zzb>> zzat(String str, String str2) {
        Cursor query;
        Object e;
        Throwable th;
        zzaax();
        zzyl();
        zzac.zzhz(str);
        zzac.zzhz(str2);
        Map<Integer, List<com.google.android.gms.internal.zzvk.zzb>> arrayMap = new ArrayMap();
        try {
            query = getWritableDatabase().query("event_filters", new String[]{"audience_id", "data"}, "app_id=? AND event_name=?", new String[]{str, str2}, null, null, null);
            if (query.moveToFirst()) {
                do {
                    try {
                        zzarc zzbd = zzarc.zzbd(query.getBlob(1));
                        com.google.android.gms.internal.zzvk.zzb com_google_android_gms_internal_zzvk_zzb = new com.google.android.gms.internal.zzvk.zzb();
                        try {
                            com.google.android.gms.internal.zzvk.zzb com_google_android_gms_internal_zzvk_zzb2 = (com.google.android.gms.internal.zzvk.zzb) com_google_android_gms_internal_zzvk_zzb.zzb(zzbd);
                            int i = query.getInt(0);
                            List list = (List) arrayMap.get(Integer.valueOf(i));
                            if (list == null) {
                                list = new ArrayList();
                                arrayMap.put(Integer.valueOf(i), list);
                            }
                            list.add(com_google_android_gms_internal_zzvk_zzb);
                        } catch (IOException e2) {
                            zzbvg().zzbwc().zze("Failed to merge filter", str, e2);
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
            Map<Integer, List<com.google.android.gms.internal.zzvk.zzb>> emptyMap = Collections.emptyMap();
            if (query == null) {
                return emptyMap;
            }
            query.close();
            return emptyMap;
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            try {
                zzbvg().zzbwc().zzj("Database error querying filters", e);
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

    Map<Integer, List<com.google.android.gms.internal.zzvk.zze>> zzau(String str, String str2) {
        Object e;
        Throwable th;
        zzaax();
        zzyl();
        zzac.zzhz(str);
        zzac.zzhz(str2);
        Map<Integer, List<com.google.android.gms.internal.zzvk.zze>> arrayMap = new ArrayMap();
        Cursor query;
        try {
            query = getWritableDatabase().query("property_filters", new String[]{"audience_id", "data"}, "app_id=? AND property_name=?", new String[]{str, str2}, null, null, null);
            if (query.moveToFirst()) {
                do {
                    try {
                        zzarc zzbd = zzarc.zzbd(query.getBlob(1));
                        com.google.android.gms.internal.zzvk.zze com_google_android_gms_internal_zzvk_zze = new com.google.android.gms.internal.zzvk.zze();
                        try {
                            com.google.android.gms.internal.zzvk.zze com_google_android_gms_internal_zzvk_zze2 = (com.google.android.gms.internal.zzvk.zze) com_google_android_gms_internal_zzvk_zze.zzb(zzbd);
                            int i = query.getInt(0);
                            List list = (List) arrayMap.get(Integer.valueOf(i));
                            if (list == null) {
                                list = new ArrayList();
                                arrayMap.put(Integer.valueOf(i), list);
                            }
                            list.add(com_google_android_gms_internal_zzvk_zze);
                        } catch (IOException e2) {
                            zzbvg().zzbwc().zze("Failed to merge filter", str, e2);
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
            Map<Integer, List<com.google.android.gms.internal.zzvk.zze>> emptyMap = Collections.emptyMap();
            if (query == null) {
                return emptyMap;
            }
            query.close();
            return emptyMap;
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            try {
                zzbvg().zzbwc().zzj("Database error querying filters", e);
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
    Object zzb(Cursor cursor, int i) {
        int zza = zza(cursor, i);
        switch (zza) {
            case 0:
                zzbvg().zzbwc().log("Loaded invalid null value from database");
                return null;
            case 1:
                return Long.valueOf(cursor.getLong(i));
            case 2:
                return Double.valueOf(cursor.getDouble(i));
            case 3:
                return cursor.getString(i);
            case 4:
                zzbvg().zzbwc().log("Loaded invalid blob type value, ignoring it");
                return null;
            default:
                zzbvg().zzbwc().zzj("Loaded invalid unknown value type, ignoring it", Integer.valueOf(zza));
                return null;
        }
    }

    @WorkerThread
    void zzb(String str, com.google.android.gms.internal.zzvk.zza[] com_google_android_gms_internal_zzvk_zzaArr) {
        int i = 0;
        zzaax();
        zzyl();
        zzac.zzhz(str);
        zzac.zzy(com_google_android_gms_internal_zzvk_zzaArr);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            zzmc(str);
            for (com.google.android.gms.internal.zzvk.zza zza : com_google_android_gms_internal_zzvk_zzaArr) {
                zza(str, zza);
            }
            List arrayList = new ArrayList();
            int length = com_google_android_gms_internal_zzvk_zzaArr.length;
            while (i < length) {
                arrayList.add(com_google_android_gms_internal_zzvk_zzaArr[i].asA);
                i++;
            }
            zzc(str, arrayList);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    @WorkerThread
    public void zzbk(long j) {
        zzyl();
        zzaax();
        if (getWritableDatabase().delete("queue", "rowid=?", new String[]{String.valueOf(j)}) != 1) {
            zzbvg().zzbwc().log("Deleted fewer rows from queue than expected");
        }
    }

    public String zzbl(long j) {
        Object e;
        Throwable th;
        String str = null;
        zzyl();
        zzaax();
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
                    zzbvg().zzbwj().log("No expired configs for apps with pending events");
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                }
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzbvg().zzbwc().zzj("Error selecting expired configs", e);
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
            zzbvg().zzbwc().zzj("Error selecting expired configs", e);
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
    public String zzbvj() {
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
                    zzbvg().zzbwc().zzj("Database error getting next bundle app id", e);
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
            zzbvg().zzbwc().zzj("Database error getting next bundle app id", e);
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

    public boolean zzbvk() {
        return zzb("select count(1) > 0 from queue where has_realtime = 1", null) != 0;
    }

    @WorkerThread
    void zzbvl() {
        zzyl();
        zzaax();
        if (zzbvr()) {
            long j = zzbvh().apT.get();
            long elapsedRealtime = zzaan().elapsedRealtime();
            if (Math.abs(elapsedRealtime - j) > zzbvi().zzbuf()) {
                zzbvh().apT.set(elapsedRealtime);
                zzbvm();
            }
        }
    }

    @WorkerThread
    void zzbvm() {
        zzyl();
        zzaax();
        if (zzbvr()) {
            int delete = getWritableDatabase().delete("queue", "abs(bundle_end_timestamp - ?) > cast(? as integer)", new String[]{String.valueOf(zzaan().currentTimeMillis()), String.valueOf(zzbvi().zzbue())});
            if (delete > 0) {
                zzbvg().zzbwj().zzj("Deleted stale rows. rowsDeleted", Integer.valueOf(delete));
            }
        }
    }

    @WorkerThread
    public long zzbvn() {
        return zza("select max(bundle_end_timestamp) from queue", null, 0);
    }

    @WorkerThread
    public long zzbvo() {
        return zza("select max(timestamp) from raw_events", null, 0);
    }

    public boolean zzbvp() {
        return zzb("select count(1) > 0 from raw_events", null) != 0;
    }

    public boolean zzbvq() {
        return zzb("select count(1) > 0 from raw_events where realtime = 1", null) != 0;
    }

    boolean zzc(String str, List<Integer> list) {
        zzac.zzhz(str);
        zzaax();
        zzyl();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        try {
            if (zzb("select count(1) from audience_filter_values where app_id=?", new String[]{str}) <= ((long) zzbvi().zzlt(str))) {
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
            zzbvg().zzbwc().zzj("Database error querying filters", e);
            return false;
        }
    }

    @WorkerThread
    public void zzd(String str, byte[] bArr) {
        zzac.zzhz(str);
        zzyl();
        zzaax();
        ContentValues contentValues = new ContentValues();
        contentValues.put("remote_config", bArr);
        try {
            if (((long) getWritableDatabase().update("apps", contentValues, "app_id = ?", new String[]{str})) == 0) {
                zzbvg().zzbwc().log("Failed to update remote config (got 0)");
            }
        } catch (SQLiteException e) {
            zzbvg().zzbwc().zzj("Error storing remote config", e);
        }
    }

    @WorkerThread
    protected void zzg(String str, long j) {
        zzac.zzhz(str);
        zzyl();
        zzaax();
        if (j <= 0) {
            zzbvg().zzbwc().log("Nonpositive first open count received, ignoring");
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", str);
        contentValues.put("first_open_count", Long.valueOf(j));
        try {
            if (getWritableDatabase().insertWithOnConflict("app2", null, contentValues, 5) == -1) {
                zzbvg().zzbwc().log("Failed to insert/replace first open count (got -1)");
            }
        } catch (SQLiteException e) {
            zzbvg().zzbwc().zzj("Error inserting/replacing first open count", e);
        }
    }

    @WorkerThread
    public List<zzak> zzly(String str) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzac.zzhz(str);
        zzyl();
        zzaax();
        List<zzak> arrayList = new ArrayList();
        try {
            Cursor query = getWritableDatabase().query("user_attributes", new String[]{"name", "set_timestamp", Param.VALUE}, "app_id=?", new String[]{str}, null, null, "rowid", String.valueOf(zzbvi().zzbty()));
            try {
                if (query.moveToFirst()) {
                    do {
                        String string = query.getString(0);
                        long j = query.getLong(1);
                        Object zzb = zzb(query, 2);
                        if (zzb == null) {
                            zzbvg().zzbwc().log("Read invalid user property value, ignoring it");
                        } else {
                            arrayList.add(new zzak(str, string, j, zzb));
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
                zzbvg().zzbwc().zze("Error querying user properties", str, e);
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
    public zza zzlz(String str) {
        Cursor query;
        Object e;
        Throwable th;
        zzac.zzhz(str);
        zzyl();
        zzaax();
        try {
            query = getWritableDatabase().query("apps", new String[]{"app_instance_id", "gmp_app_id", "resettable_device_id_hash", "last_bundle_index", "last_bundle_start_timestamp", "last_bundle_end_timestamp", "app_version", "app_store", "gmp_version", "dev_cert_hash", "measurement_enabled", "day", "daily_public_events_count", "daily_events_count", "daily_conversions_count", "config_fetched_time", "failed_config_fetch_time", "app_version_int", "firebase_instance_id", "daily_error_events_count", "daily_realtime_events_count"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zza com_google_android_gms_measurement_internal_zza = new zza(this.anq, str);
                    com_google_android_gms_measurement_internal_zza.zzlj(query.getString(0));
                    com_google_android_gms_measurement_internal_zza.zzlk(query.getString(1));
                    com_google_android_gms_measurement_internal_zza.zzll(query.getString(2));
                    com_google_android_gms_measurement_internal_zza.zzbb(query.getLong(3));
                    com_google_android_gms_measurement_internal_zza.zzaw(query.getLong(4));
                    com_google_android_gms_measurement_internal_zza.zzax(query.getLong(5));
                    com_google_android_gms_measurement_internal_zza.setAppVersion(query.getString(6));
                    com_google_android_gms_measurement_internal_zza.zzln(query.getString(7));
                    com_google_android_gms_measurement_internal_zza.zzaz(query.getLong(8));
                    com_google_android_gms_measurement_internal_zza.zzba(query.getLong(9));
                    com_google_android_gms_measurement_internal_zza.setMeasurementEnabled((query.isNull(10) ? 1 : query.getInt(10)) != 0);
                    com_google_android_gms_measurement_internal_zza.zzbe(query.getLong(11));
                    com_google_android_gms_measurement_internal_zza.zzbf(query.getLong(12));
                    com_google_android_gms_measurement_internal_zza.zzbg(query.getLong(13));
                    com_google_android_gms_measurement_internal_zza.zzbh(query.getLong(14));
                    com_google_android_gms_measurement_internal_zza.zzbc(query.getLong(15));
                    com_google_android_gms_measurement_internal_zza.zzbd(query.getLong(16));
                    com_google_android_gms_measurement_internal_zza.zzay(query.isNull(17) ? -2147483648L : (long) query.getInt(17));
                    com_google_android_gms_measurement_internal_zza.zzlm(query.getString(18));
                    com_google_android_gms_measurement_internal_zza.zzbj(query.getLong(19));
                    com_google_android_gms_measurement_internal_zza.zzbi(query.getLong(20));
                    com_google_android_gms_measurement_internal_zza.zzbsq();
                    if (query.moveToNext()) {
                        zzbvg().zzbwc().log("Got multiple records for app, expected one");
                    }
                    if (query == null) {
                        return com_google_android_gms_measurement_internal_zza;
                    }
                    query.close();
                    return com_google_android_gms_measurement_internal_zza;
                }
                if (query != null) {
                    query.close();
                }
                return null;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzbvg().zzbwc().zze("Error querying app", str, e);
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
            zzbvg().zzbwc().zze("Error querying app", str, e);
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

    public long zzma(String str) {
        zzac.zzhz(str);
        zzyl();
        zzaax();
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            String valueOf = String.valueOf(zzbvi().zzlx(str));
            return (long) writableDatabase.delete("raw_events", "rowid in (select rowid from raw_events where app_id=? order by rowid desc limit -1 offset ?)", new String[]{str, valueOf});
        } catch (SQLiteException e) {
            zzbvg().zzbwc().zzj("Error deleting over the limit events", e);
            return 0;
        }
    }

    @WorkerThread
    public byte[] zzmb(String str) {
        Cursor query;
        Object e;
        Throwable th;
        zzac.zzhz(str);
        zzyl();
        zzaax();
        try {
            query = getWritableDatabase().query("apps", new String[]{"remote_config"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    byte[] blob = query.getBlob(0);
                    if (query.moveToNext()) {
                        zzbvg().zzbwc().log("Got multiple records for app config, expected one");
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
                    zzbvg().zzbwc().zze("Error querying remote config", str, e);
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
            zzbvg().zzbwc().zze("Error querying remote config", str, e);
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
    void zzmc(String str) {
        zzaax();
        zzyl();
        zzac.zzhz(str);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete("property_filters", "app_id=?", new String[]{str});
        writableDatabase.delete("event_filters", "app_id=?", new String[]{str});
    }

    Map<Integer, zzf> zzmd(String str) {
        Object e;
        Cursor cursor;
        Throwable th;
        zzaax();
        zzyl();
        zzac.zzhz(str);
        Cursor query;
        try {
            query = getWritableDatabase().query("audience_filter_values", new String[]{"audience_id", "current_results"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    Map<Integer, zzf> arrayMap = new ArrayMap();
                    do {
                        int i = query.getInt(0);
                        zzarc zzbd = zzarc.zzbd(query.getBlob(1));
                        zzf com_google_android_gms_internal_zzvm_zzf = new zzf();
                        try {
                            zzf com_google_android_gms_internal_zzvm_zzf2 = (zzf) com_google_android_gms_internal_zzvm_zzf.zzb(zzbd);
                            arrayMap.put(Integer.valueOf(i), com_google_android_gms_internal_zzvm_zzf);
                        } catch (IOException e2) {
                            zzbvg().zzbwc().zzd("Failed to merge filter results. appId, audienceId, error", str, Integer.valueOf(i), e2);
                        }
                    } while (query.moveToNext());
                    if (query != null) {
                        query.close();
                    }
                    return arrayMap;
                }
                if (query != null) {
                    query.close();
                }
                return null;
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
                zzbvg().zzbwc().zzj("Database error querying filter results", e);
                if (cursor != null) {
                    cursor.close();
                }
                return null;
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
    void zzme(String str) {
        zzaax();
        zzyl();
        zzac.zzhz(str);
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            String[] strArr = new String[]{str};
            int delete = writableDatabase.delete("audience_filter_values", "app_id=?", strArr) + (((((((writableDatabase.delete("events", "app_id=?", strArr) + 0) + writableDatabase.delete("user_attributes", "app_id=?", strArr)) + writableDatabase.delete("apps", "app_id=?", strArr)) + writableDatabase.delete("raw_events", "app_id=?", strArr)) + writableDatabase.delete("raw_events_metadata", "app_id=?", strArr)) + writableDatabase.delete("event_filters", "app_id=?", strArr)) + writableDatabase.delete("property_filters", "app_id=?", strArr));
            if (delete > 0) {
                zzbvg().zzbwj().zze("Deleted application data. app, records", str, Integer.valueOf(delete));
            }
        } catch (SQLiteException e) {
            zzbvg().zzbwc().zze("Error deleting application data. appId, error", str, e);
        }
    }

    @WorkerThread
    public long zzmf(String str) {
        zzac.zzhz(str);
        zzyl();
        zzaax();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            long zza = zza("select first_open_count from app2 where app_id=?", new String[]{str}, 0);
            zzg(str, 1 + zza);
            writableDatabase.setTransactionSuccessful();
            return zza;
        } finally {
            writableDatabase.endTransaction();
        }
    }

    public void zzmg(String str) {
        try {
            getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str, str});
        } catch (SQLiteException e) {
            zzbvg().zzbwc().zzj("Failed to remove unused event metadata", e);
        }
    }

    public long zzmh(String str) {
        zzac.zzhz(str);
        return zza("select count(1) from events where app_id=? and name not like '!_%' escape '!'", new String[]{str}, 0);
    }

    @WorkerThread
    public List<Pair<com.google.android.gms.internal.zzvm.zze, Long>> zzn(String str, int i, int i2) {
        List<Pair<com.google.android.gms.internal.zzvm.zze, Long>> emptyList;
        Object e;
        Cursor cursor;
        Throwable th;
        boolean z = true;
        zzyl();
        zzaax();
        zzac.zzbs(i > 0);
        if (i2 <= 0) {
            z = false;
        }
        zzac.zzbs(z);
        zzac.zzhz(str);
        Cursor query;
        try {
            query = getWritableDatabase().query("queue", new String[]{"rowid", "data"}, "app_id=?", new String[]{str}, null, null, "rowid", String.valueOf(i));
            try {
                if (query.moveToFirst()) {
                    List<Pair<com.google.android.gms.internal.zzvm.zze, Long>> arrayList = new ArrayList();
                    int i3 = 0;
                    while (true) {
                        long j = query.getLong(0);
                        int length;
                        try {
                            byte[] zzw = zzbvc().zzw(query.getBlob(1));
                            if (!arrayList.isEmpty() && zzw.length + i3 > i2) {
                                break;
                            }
                            zzarc zzbd = zzarc.zzbd(zzw);
                            com.google.android.gms.internal.zzvm.zze com_google_android_gms_internal_zzvm_zze = new com.google.android.gms.internal.zzvm.zze();
                            try {
                                com.google.android.gms.internal.zzvm.zze com_google_android_gms_internal_zzvm_zze2 = (com.google.android.gms.internal.zzvm.zze) com_google_android_gms_internal_zzvm_zze.zzb(zzbd);
                                length = zzw.length + i3;
                                arrayList.add(Pair.create(com_google_android_gms_internal_zzvm_zze, Long.valueOf(j)));
                            } catch (IOException e2) {
                                zzbvg().zzbwc().zze("Failed to merge queued bundle", str, e2);
                                length = i3;
                            }
                            if (!query.moveToNext() || length > i2) {
                                break;
                            }
                            i3 = length;
                        } catch (IOException e22) {
                            zzbvg().zzbwc().zze("Failed to unzip queued bundle", str, e22);
                            length = i3;
                        }
                    }
                    if (query != null) {
                        query.close();
                    }
                    return arrayList;
                }
                emptyList = Collections.emptyList();
                if (query == null) {
                    return emptyList;
                }
                query.close();
                return emptyList;
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
                zzbvg().zzbwc().zze("Error querying bundles", str, e);
                emptyList = Collections.emptyList();
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

    protected void zzym() {
    }

    @WorkerThread
    public void zzz(String str, int i) {
        zzac.zzhz(str);
        zzyl();
        zzaax();
        try {
            getWritableDatabase().execSQL("delete from user_attributes where app_id=? and name in (select name from user_attributes where app_id=? and name like '_ltv_%' order by set_timestamp desc limit ?,10);", new String[]{str, str, String.valueOf(i)});
        } catch (SQLiteException e) {
            zzbvg().zzbwc().zze("Error pruning currencies", str, e);
        }
    }
}
