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
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.internal.zzars;
import com.google.android.gms.internal.zzart;
import com.google.android.gms.internal.zzwc.zzf;
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
    private static final Map<String, String> arj = new ArrayMap(17);
    private static final Map<String, String> ark = new ArrayMap(1);
    private static final Map<String, String> arl = new ArrayMap(1);
    private static final Map<String, String> arm = new ArrayMap(1);
    private final zzc arn = new zzc(this, getContext(), zzade());
    private final zzah aro = new zzah(zzabz());

    public static class zza {
        long arp;
        long arq;
        long arr;
        long ars;
        long art;
    }

    interface zzb {
        boolean zza(long j, com.google.android.gms.internal.zzwc.zzb com_google_android_gms_internal_zzwc_zzb);

        void zzb(com.google.android.gms.internal.zzwc.zze com_google_android_gms_internal_zzwc_zze);
    }

    private class zzc extends SQLiteOpenHelper {
        final /* synthetic */ zze aru;

        zzc(zze com_google_android_gms_measurement_internal_zze, Context context, String str) {
            this.aru = com_google_android_gms_measurement_internal_zze;
            super(context, str, null, 1);
        }

        @WorkerThread
        public SQLiteDatabase getWritableDatabase() {
            if (this.aru.aro.zzz(this.aru.zzbwd().zzbup())) {
                SQLiteDatabase writableDatabase;
                try {
                    writableDatabase = super.getWritableDatabase();
                } catch (SQLiteException e) {
                    this.aru.aro.start();
                    this.aru.zzbwb().zzbwy().log("Opening the database failed, dropping and recreating it");
                    String zzade = this.aru.zzade();
                    if (!this.aru.getContext().getDatabasePath(zzade).delete()) {
                        this.aru.zzbwb().zzbwy().zzj("Failed to delete corrupted db file", zzade);
                    }
                    try {
                        writableDatabase = super.getWritableDatabase();
                        this.aru.aro.clear();
                    } catch (SQLiteException e2) {
                        this.aru.zzbwb().zzbwy().zzj("Failed to open freshly created database", e2);
                        throw e2;
                    }
                }
                return writableDatabase;
            }
            throw new SQLiteException("Database open failed");
        }

        @WorkerThread
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            zze.zza(this.aru.zzbwb(), sQLiteDatabase);
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
            zze.zza(this.aru.zzbwb(), sQLiteDatabase, "events", "CREATE TABLE IF NOT EXISTS events ( app_id TEXT NOT NULL, name TEXT NOT NULL, lifetime_count INTEGER NOT NULL, current_bundle_count INTEGER NOT NULL, last_fire_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,lifetime_count,current_bundle_count,last_fire_timestamp", null);
            zze.zza(this.aru.zzbwb(), sQLiteDatabase, "user_attributes", "CREATE TABLE IF NOT EXISTS user_attributes ( app_id TEXT NOT NULL, name TEXT NOT NULL, set_timestamp INTEGER NOT NULL, value BLOB NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,set_timestamp,value", null);
            zze.zza(this.aru.zzbwb(), sQLiteDatabase, "apps", "CREATE TABLE IF NOT EXISTS apps ( app_id TEXT NOT NULL, app_instance_id TEXT, gmp_app_id TEXT, resettable_device_id_hash TEXT, last_bundle_index INTEGER NOT NULL, last_bundle_end_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id)) ;", "app_id,app_instance_id,gmp_app_id,resettable_device_id_hash,last_bundle_index,last_bundle_end_timestamp", zze.arj);
            zze.zza(this.aru.zzbwb(), sQLiteDatabase, "queue", "CREATE TABLE IF NOT EXISTS queue ( app_id TEXT NOT NULL, bundle_end_timestamp INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,bundle_end_timestamp,data", zze.arl);
            zze.zza(this.aru.zzbwb(), sQLiteDatabase, "raw_events_metadata", "CREATE TABLE IF NOT EXISTS raw_events_metadata ( app_id TEXT NOT NULL, metadata_fingerprint INTEGER NOT NULL, metadata BLOB NOT NULL, PRIMARY KEY (app_id, metadata_fingerprint));", "app_id,metadata_fingerprint,metadata", null);
            zze.zza(this.aru.zzbwb(), sQLiteDatabase, "raw_events", "CREATE TABLE IF NOT EXISTS raw_events ( app_id TEXT NOT NULL, name TEXT NOT NULL, timestamp INTEGER NOT NULL, metadata_fingerprint INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,name,timestamp,metadata_fingerprint,data", zze.ark);
            zze.zza(this.aru.zzbwb(), sQLiteDatabase, "event_filters", "CREATE TABLE IF NOT EXISTS event_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, event_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, event_name, audience_id, filter_id));", "app_id,audience_id,filter_id,event_name,data", null);
            zze.zza(this.aru.zzbwb(), sQLiteDatabase, "property_filters", "CREATE TABLE IF NOT EXISTS property_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, property_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, property_name, audience_id, filter_id));", "app_id,audience_id,filter_id,property_name,data", null);
            zze.zza(this.aru.zzbwb(), sQLiteDatabase, "audience_filter_values", "CREATE TABLE IF NOT EXISTS audience_filter_values ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, current_results BLOB, PRIMARY KEY (app_id, audience_id));", "app_id,audience_id,current_results", null);
            zze.zza(this.aru.zzbwb(), sQLiteDatabase, "app2", "CREATE TABLE IF NOT EXISTS app2 ( app_id TEXT NOT NULL, first_open_count INTEGER NOT NULL, PRIMARY KEY (app_id));", "app_id,first_open_count", zze.arm);
        }

        @WorkerThread
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    static {
        arj.put("app_version", "ALTER TABLE apps ADD COLUMN app_version TEXT;");
        arj.put("app_store", "ALTER TABLE apps ADD COLUMN app_store TEXT;");
        arj.put("gmp_version", "ALTER TABLE apps ADD COLUMN gmp_version INTEGER;");
        arj.put("dev_cert_hash", "ALTER TABLE apps ADD COLUMN dev_cert_hash INTEGER;");
        arj.put("measurement_enabled", "ALTER TABLE apps ADD COLUMN measurement_enabled INTEGER;");
        arj.put("last_bundle_start_timestamp", "ALTER TABLE apps ADD COLUMN last_bundle_start_timestamp INTEGER;");
        arj.put("day", "ALTER TABLE apps ADD COLUMN day INTEGER;");
        arj.put("daily_public_events_count", "ALTER TABLE apps ADD COLUMN daily_public_events_count INTEGER;");
        arj.put("daily_events_count", "ALTER TABLE apps ADD COLUMN daily_events_count INTEGER;");
        arj.put("daily_conversions_count", "ALTER TABLE apps ADD COLUMN daily_conversions_count INTEGER;");
        arj.put("remote_config", "ALTER TABLE apps ADD COLUMN remote_config BLOB;");
        arj.put("config_fetched_time", "ALTER TABLE apps ADD COLUMN config_fetched_time INTEGER;");
        arj.put("failed_config_fetch_time", "ALTER TABLE apps ADD COLUMN failed_config_fetch_time INTEGER;");
        arj.put("app_version_int", "ALTER TABLE apps ADD COLUMN app_version_int INTEGER;");
        arj.put("firebase_instance_id", "ALTER TABLE apps ADD COLUMN firebase_instance_id TEXT;");
        arj.put("daily_error_events_count", "ALTER TABLE apps ADD COLUMN daily_error_events_count INTEGER;");
        arj.put("daily_realtime_events_count", "ALTER TABLE apps ADD COLUMN daily_realtime_events_count INTEGER;");
        ark.put("realtime", "ALTER TABLE raw_events ADD COLUMN realtime INTEGER;");
        arl.put("has_realtime", "ALTER TABLE queue ADD COLUMN has_realtime INTEGER;");
        arm.put("previous_install_count", "ALTER TABLE app2 ADD COLUMN previous_install_count INTEGER;");
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
            zzbwb().zzbwy().zze("Database error", str, e);
            throw e;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    static void zza(zzq com_google_android_gms_measurement_internal_zzq, SQLiteDatabase sQLiteDatabase) {
        if (com_google_android_gms_measurement_internal_zzq == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        } else if (VERSION.SDK_INT >= 9) {
            File file = new File(sQLiteDatabase.getPath());
            if (!file.setReadable(false, false)) {
                com_google_android_gms_measurement_internal_zzq.zzbxa().log("Failed to turn off database read permission");
            }
            if (!file.setWritable(false, false)) {
                com_google_android_gms_measurement_internal_zzq.zzbxa().log("Failed to turn off database write permission");
            }
            if (!file.setReadable(true, true)) {
                com_google_android_gms_measurement_internal_zzq.zzbxa().log("Failed to turn on database read permission for owner");
            }
            if (!file.setWritable(true, true)) {
                com_google_android_gms_measurement_internal_zzq.zzbxa().log("Failed to turn on database write permission for owner");
            }
        }
    }

    @WorkerThread
    static void zza(zzq com_google_android_gms_measurement_internal_zzq, SQLiteDatabase sQLiteDatabase, String str, String str2, String str3, Map<String, String> map) throws SQLiteException {
        if (com_google_android_gms_measurement_internal_zzq == null) {
            throw new IllegalArgumentException("Monitor must not be null");
        }
        if (!zza(com_google_android_gms_measurement_internal_zzq, sQLiteDatabase, str)) {
            sQLiteDatabase.execSQL(str2);
        }
        try {
            zza(com_google_android_gms_measurement_internal_zzq, sQLiteDatabase, str, str3, map);
        } catch (SQLiteException e) {
            com_google_android_gms_measurement_internal_zzq.zzbwy().zzj("Failed to verify columns on table that was just created", str);
            throw e;
        }
    }

    @WorkerThread
    static void zza(zzq com_google_android_gms_measurement_internal_zzq, SQLiteDatabase sQLiteDatabase, String str, String str2, Map<String, String> map) throws SQLiteException {
        if (com_google_android_gms_measurement_internal_zzq == null) {
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
            com_google_android_gms_measurement_internal_zzq.zzbxa().zze("Table has extra columns. table, columns", str, TextUtils.join(", ", zzb));
        }
    }

    @WorkerThread
    private void zza(String str, com.google.android.gms.internal.zzwa.zza com_google_android_gms_internal_zzwa_zza) {
        Object obj = null;
        zzacj();
        zzzx();
        zzaa.zzib(str);
        zzaa.zzy(com_google_android_gms_internal_zzwa_zza);
        zzaa.zzy(com_google_android_gms_internal_zzwa_zza.awb);
        zzaa.zzy(com_google_android_gms_internal_zzwa_zza.awa);
        if (com_google_android_gms_internal_zzwa_zza.avZ == null) {
            zzbwb().zzbxa().log("Audience with no ID");
            return;
        }
        int intValue = com_google_android_gms_internal_zzwa_zza.avZ.intValue();
        for (com.google.android.gms.internal.zzwa.zzb com_google_android_gms_internal_zzwa_zzb : com_google_android_gms_internal_zzwa_zza.awb) {
            if (com_google_android_gms_internal_zzwa_zzb.awd == null) {
                zzbwb().zzbxa().zze("Event filter with no ID. Audience definition ignored. appId, audienceId", str, com_google_android_gms_internal_zzwa_zza.avZ);
                return;
            }
        }
        for (com.google.android.gms.internal.zzwa.zze com_google_android_gms_internal_zzwa_zze : com_google_android_gms_internal_zzwa_zza.awa) {
            if (com_google_android_gms_internal_zzwa_zze.awd == null) {
                zzbwb().zzbxa().zze("Property filter with no ID. Audience definition ignored. appId, audienceId", str, com_google_android_gms_internal_zzwa_zza.avZ);
                return;
            }
        }
        Object obj2 = 1;
        for (com.google.android.gms.internal.zzwa.zzb zza : com_google_android_gms_internal_zzwa_zza.awb) {
            if (!zza(str, intValue, zza)) {
                obj2 = null;
                break;
            }
        }
        if (obj2 != null) {
            for (com.google.android.gms.internal.zzwa.zze zza2 : com_google_android_gms_internal_zzwa_zza.awa) {
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
    static boolean zza(zzq com_google_android_gms_measurement_internal_zzq, SQLiteDatabase sQLiteDatabase, String str) {
        Cursor query;
        Object e;
        Throwable th;
        Cursor cursor = null;
        if (com_google_android_gms_measurement_internal_zzq == null) {
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
                    com_google_android_gms_measurement_internal_zzq.zzbxa().zze("Error querying for table", str, e);
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
            com_google_android_gms_measurement_internal_zzq.zzbxa().zze("Error querying for table", str, e);
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
    private boolean zza(String str, int i, com.google.android.gms.internal.zzwa.zzb com_google_android_gms_internal_zzwa_zzb) {
        zzacj();
        zzzx();
        zzaa.zzib(str);
        zzaa.zzy(com_google_android_gms_internal_zzwa_zzb);
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzwa_zzb.awe)) {
            zzbwb().zzbxa().zze("Event filter had no event name. Audience definition ignored. audienceId, filterId", Integer.valueOf(i), String.valueOf(com_google_android_gms_internal_zzwa_zzb.awd));
            return false;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzwa_zzb.cz()];
            zzart zzbe = zzart.zzbe(bArr);
            com_google_android_gms_internal_zzwa_zzb.zza(zzbe);
            zzbe.cm();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("filter_id", com_google_android_gms_internal_zzwa_zzb.awd);
            contentValues.put("event_name", com_google_android_gms_internal_zzwa_zzb.awe);
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("event_filters", null, contentValues, 5) == -1) {
                    zzbwb().zzbwy().log("Failed to insert event filter (got -1)");
                }
                return true;
            } catch (SQLiteException e) {
                zzbwb().zzbwy().zzj("Error storing event filter", e);
                return false;
            }
        } catch (IOException e2) {
            zzbwb().zzbwy().zzj("Configuration loss. Failed to serialize event filter", e2);
            return false;
        }
    }

    @WorkerThread
    private boolean zza(String str, int i, com.google.android.gms.internal.zzwa.zze com_google_android_gms_internal_zzwa_zze) {
        zzacj();
        zzzx();
        zzaa.zzib(str);
        zzaa.zzy(com_google_android_gms_internal_zzwa_zze);
        if (TextUtils.isEmpty(com_google_android_gms_internal_zzwa_zze.awt)) {
            zzbwb().zzbxa().zze("Property filter had no property name. Audience definition ignored. audienceId, filterId", Integer.valueOf(i), String.valueOf(com_google_android_gms_internal_zzwa_zze.awd));
            return false;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzwa_zze.cz()];
            zzart zzbe = zzart.zzbe(bArr);
            com_google_android_gms_internal_zzwa_zze.zza(zzbe);
            zzbe.cm();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("filter_id", com_google_android_gms_internal_zzwa_zze.awd);
            contentValues.put("property_name", com_google_android_gms_internal_zzwa_zze.awt);
            contentValues.put("data", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("property_filters", null, contentValues, 5) != -1) {
                    return true;
                }
                zzbwb().zzbwy().log("Failed to insert property filter (got -1)");
                return false;
            } catch (SQLiteException e) {
                zzbwb().zzbwy().zzj("Error storing property filter", e);
                return false;
            }
        } catch (IOException e2) {
            zzbwb().zzbwy().zzj("Configuration loss. Failed to serialize property filter", e2);
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
            zzbwb().zzbwy().zze("Database error", str, e);
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

    private boolean zzbwn() {
        return getContext().getDatabasePath(zzade()).exists();
    }

    @WorkerThread
    public void beginTransaction() {
        zzacj();
        getWritableDatabase().beginTransaction();
    }

    @WorkerThread
    public void endTransaction() {
        zzacj();
        getWritableDatabase().endTransaction();
    }

    @WorkerThread
    SQLiteDatabase getWritableDatabase() {
        zzzx();
        try {
            return this.arn.getWritableDatabase();
        } catch (SQLiteException e) {
            zzbwb().zzbxa().zzj("Error opening database", e);
            throw e;
        }
    }

    @WorkerThread
    public void setTransactionSuccessful() {
        zzacj();
        getWritableDatabase().setTransactionSuccessful();
    }

    public long zza(com.google.android.gms.internal.zzwc.zze com_google_android_gms_internal_zzwc_zze) throws IOException {
        zzzx();
        zzacj();
        zzaa.zzy(com_google_android_gms_internal_zzwc_zze);
        zzaa.zzib(com_google_android_gms_internal_zzwc_zze.zzcs);
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzwc_zze.cz()];
            zzart zzbe = zzart.zzbe(bArr);
            com_google_android_gms_internal_zzwc_zze.zza(zzbe);
            zzbe.cm();
            long zzz = zzbvx().zzz(bArr);
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzwc_zze.zzcs);
            contentValues.put("metadata_fingerprint", Long.valueOf(zzz));
            contentValues.put(TtmlNode.TAG_METADATA, bArr);
            try {
                getWritableDatabase().insertWithOnConflict("raw_events_metadata", null, contentValues, 4);
                return zzz;
            } catch (SQLiteException e) {
                zzbwb().zzbwy().zzj("Error storing raw event metadata", e);
                throw e;
            }
        } catch (IOException e2) {
            zzbwb().zzbwy().zzj("Data loss. Failed to serialize event metadata", e2);
            throw e2;
        }
    }

    @WorkerThread
    public zza zza(long j, String str, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        Cursor query;
        Object e;
        Throwable th;
        zzaa.zzib(str);
        zzzx();
        zzacj();
        String[] strArr = new String[]{str};
        zza com_google_android_gms_measurement_internal_zze_zza = new zza();
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            query = writableDatabase.query("apps", new String[]{"day", "daily_events_count", "daily_public_events_count", "daily_conversions_count", "daily_error_events_count", "daily_realtime_events_count"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    if (query.getLong(0) == j) {
                        com_google_android_gms_measurement_internal_zze_zza.arq = query.getLong(1);
                        com_google_android_gms_measurement_internal_zze_zza.arp = query.getLong(2);
                        com_google_android_gms_measurement_internal_zze_zza.arr = query.getLong(3);
                        com_google_android_gms_measurement_internal_zze_zza.ars = query.getLong(4);
                        com_google_android_gms_measurement_internal_zze_zza.art = query.getLong(5);
                    }
                    if (z) {
                        com_google_android_gms_measurement_internal_zze_zza.arq++;
                    }
                    if (z2) {
                        com_google_android_gms_measurement_internal_zze_zza.arp++;
                    }
                    if (z3) {
                        com_google_android_gms_measurement_internal_zze_zza.arr++;
                    }
                    if (z4) {
                        com_google_android_gms_measurement_internal_zze_zza.ars++;
                    }
                    if (z5) {
                        com_google_android_gms_measurement_internal_zze_zza.art++;
                    }
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("day", Long.valueOf(j));
                    contentValues.put("daily_public_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zze_zza.arp));
                    contentValues.put("daily_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zze_zza.arq));
                    contentValues.put("daily_conversions_count", Long.valueOf(com_google_android_gms_measurement_internal_zze_zza.arr));
                    contentValues.put("daily_error_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zze_zza.ars));
                    contentValues.put("daily_realtime_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zze_zza.art));
                    writableDatabase.update("apps", contentValues, "app_id=?", strArr);
                    if (query != null) {
                        query.close();
                    }
                    return com_google_android_gms_measurement_internal_zze_zza;
                }
                zzbwb().zzbxa().zzj("Not updating daily counts, app is not known", str);
                if (query != null) {
                    query.close();
                }
                return com_google_android_gms_measurement_internal_zze_zza;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzbwb().zzbwy().zzj("Error updating daily counts", e);
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
            zzbwb().zzbwy().zzj("Error updating daily counts", e);
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
        zzaa.zzib(str);
        zzaa.zzy(obj);
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
    public void zza(com.google.android.gms.internal.zzwc.zze com_google_android_gms_internal_zzwc_zze, boolean z) {
        zzzx();
        zzacj();
        zzaa.zzy(com_google_android_gms_internal_zzwc_zze);
        zzaa.zzib(com_google_android_gms_internal_zzwc_zze.zzcs);
        zzaa.zzy(com_google_android_gms_internal_zzwc_zze.awZ);
        zzbwg();
        long currentTimeMillis = zzabz().currentTimeMillis();
        if (com_google_android_gms_internal_zzwc_zze.awZ.longValue() < currentTimeMillis - zzbwd().zzbuv() || com_google_android_gms_internal_zzwc_zze.awZ.longValue() > zzbwd().zzbuv() + currentTimeMillis) {
            zzbwb().zzbxa().zze("Storing bundle outside of the max uploading time span. now, timestamp", Long.valueOf(currentTimeMillis), com_google_android_gms_internal_zzwc_zze.awZ);
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzwc_zze.cz()];
            zzart zzbe = zzart.zzbe(bArr);
            com_google_android_gms_internal_zzwc_zze.zza(zzbe);
            zzbe.cm();
            bArr = zzbvx().zzk(bArr);
            zzbwb().zzbxe().zzj("Saving bundle, size", Integer.valueOf(bArr.length));
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_internal_zzwc_zze.zzcs);
            contentValues.put("bundle_end_timestamp", com_google_android_gms_internal_zzwc_zze.awZ);
            contentValues.put("data", bArr);
            contentValues.put("has_realtime", Integer.valueOf(z ? 1 : 0));
            try {
                if (getWritableDatabase().insert("queue", null, contentValues) == -1) {
                    zzbwb().zzbwy().log("Failed to insert bundle (got -1)");
                }
            } catch (SQLiteException e) {
                zzbwb().zzbwy().zzj("Error storing bundle", e);
            }
        } catch (IOException e2) {
            zzbwb().zzbwy().zzj("Data loss. Failed to serialize bundle", e2);
        }
    }

    @WorkerThread
    public void zza(zza com_google_android_gms_measurement_internal_zza) {
        zzaa.zzy(com_google_android_gms_measurement_internal_zza);
        zzzx();
        zzacj();
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_measurement_internal_zza.zzup());
        contentValues.put("app_instance_id", com_google_android_gms_measurement_internal_zza.zzazn());
        contentValues.put("gmp_app_id", com_google_android_gms_measurement_internal_zza.zzbth());
        contentValues.put("resettable_device_id_hash", com_google_android_gms_measurement_internal_zza.zzbti());
        contentValues.put("last_bundle_index", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtr()));
        contentValues.put("last_bundle_start_timestamp", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtk()));
        contentValues.put("last_bundle_end_timestamp", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtl()));
        contentValues.put("app_version", com_google_android_gms_measurement_internal_zza.zzaaf());
        contentValues.put("app_store", com_google_android_gms_measurement_internal_zza.zzbtn());
        contentValues.put("gmp_version", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbto()));
        contentValues.put("dev_cert_hash", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtp()));
        contentValues.put("measurement_enabled", Boolean.valueOf(com_google_android_gms_measurement_internal_zza.zzbtq()));
        contentValues.put("day", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtv()));
        contentValues.put("daily_public_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtw()));
        contentValues.put("daily_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtx()));
        contentValues.put("daily_conversions_count", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbty()));
        contentValues.put("config_fetched_time", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbts()));
        contentValues.put("failed_config_fetch_time", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtt()));
        contentValues.put("app_version_int", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtm()));
        contentValues.put("firebase_instance_id", com_google_android_gms_measurement_internal_zza.zzbtj());
        contentValues.put("daily_error_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbua()));
        contentValues.put("daily_realtime_events_count", Long.valueOf(com_google_android_gms_measurement_internal_zza.zzbtz()));
        try {
            if (getWritableDatabase().insertWithOnConflict("apps", null, contentValues, 5) == -1) {
                zzbwb().zzbwy().log("Failed to insert/update app (got -1)");
            }
        } catch (SQLiteException e) {
            zzbwb().zzbwy().zzj("Error storing app", e);
        }
    }

    public void zza(zzh com_google_android_gms_measurement_internal_zzh, long j, boolean z) {
        int i = 0;
        zzzx();
        zzacj();
        zzaa.zzy(com_google_android_gms_measurement_internal_zzh);
        zzaa.zzib(com_google_android_gms_measurement_internal_zzh.zzctj);
        com.google.android.gms.internal.zzwc.zzb com_google_android_gms_internal_zzwc_zzb = new com.google.android.gms.internal.zzwc.zzb();
        com_google_android_gms_internal_zzwc_zzb.awP = Long.valueOf(com_google_android_gms_measurement_internal_zzh.arB);
        com_google_android_gms_internal_zzwc_zzb.awN = new com.google.android.gms.internal.zzwc.zzc[com_google_android_gms_measurement_internal_zzh.arC.size()];
        Iterator it = com_google_android_gms_measurement_internal_zzh.arC.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            String str = (String) it.next();
            com.google.android.gms.internal.zzwc.zzc com_google_android_gms_internal_zzwc_zzc = new com.google.android.gms.internal.zzwc.zzc();
            int i3 = i2 + 1;
            com_google_android_gms_internal_zzwc_zzb.awN[i2] = com_google_android_gms_internal_zzwc_zzc;
            com_google_android_gms_internal_zzwc_zzc.name = str;
            zzbvx().zza(com_google_android_gms_internal_zzwc_zzc, com_google_android_gms_measurement_internal_zzh.arC.get(str));
            i2 = i3;
        }
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzwc_zzb.cz()];
            zzart zzbe = zzart.zzbe(bArr);
            com_google_android_gms_internal_zzwc_zzb.zza(zzbe);
            zzbe.cm();
            zzbwb().zzbxe().zze("Saving event, name, data size", com_google_android_gms_measurement_internal_zzh.mName, Integer.valueOf(bArr.length));
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", com_google_android_gms_measurement_internal_zzh.zzctj);
            contentValues.put("name", com_google_android_gms_measurement_internal_zzh.mName);
            contentValues.put("timestamp", Long.valueOf(com_google_android_gms_measurement_internal_zzh.vO));
            contentValues.put("metadata_fingerprint", Long.valueOf(j));
            contentValues.put("data", bArr);
            str = "realtime";
            if (z) {
                i = 1;
            }
            contentValues.put(str, Integer.valueOf(i));
            try {
                if (getWritableDatabase().insert("raw_events", null, contentValues) == -1) {
                    zzbwb().zzbwy().log("Failed to insert raw event (got -1)");
                }
            } catch (SQLiteException e) {
                zzbwb().zzbwy().zzj("Error storing raw event", e);
            }
        } catch (IOException e2) {
            zzbwb().zzbwy().zzj("Data loss. Failed to serialize event params/data", e2);
        }
    }

    @WorkerThread
    public void zza(zzi com_google_android_gms_measurement_internal_zzi) {
        zzaa.zzy(com_google_android_gms_measurement_internal_zzi);
        zzzx();
        zzacj();
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_measurement_internal_zzi.zzctj);
        contentValues.put("name", com_google_android_gms_measurement_internal_zzi.mName);
        contentValues.put("lifetime_count", Long.valueOf(com_google_android_gms_measurement_internal_zzi.arD));
        contentValues.put("current_bundle_count", Long.valueOf(com_google_android_gms_measurement_internal_zzi.arE));
        contentValues.put("last_fire_timestamp", Long.valueOf(com_google_android_gms_measurement_internal_zzi.arF));
        try {
            if (getWritableDatabase().insertWithOnConflict("events", null, contentValues, 5) == -1) {
                zzbwb().zzbwy().log("Failed to insert/update event aggregates (got -1)");
            }
        } catch (SQLiteException e) {
            zzbwb().zzbwy().zzj("Error storing event aggregates", e);
        }
    }

    void zza(String str, int i, zzf com_google_android_gms_internal_zzwc_zzf) {
        zzacj();
        zzzx();
        zzaa.zzib(str);
        zzaa.zzy(com_google_android_gms_internal_zzwc_zzf);
        try {
            byte[] bArr = new byte[com_google_android_gms_internal_zzwc_zzf.cz()];
            zzart zzbe = zzart.zzbe(bArr);
            com_google_android_gms_internal_zzwc_zzf.zza(zzbe);
            zzbe.cm();
            ContentValues contentValues = new ContentValues();
            contentValues.put("app_id", str);
            contentValues.put("audience_id", Integer.valueOf(i));
            contentValues.put("current_results", bArr);
            try {
                if (getWritableDatabase().insertWithOnConflict("audience_filter_values", null, contentValues, 5) == -1) {
                    zzbwb().zzbwy().log("Failed to insert filter results (got -1)");
                }
            } catch (SQLiteException e) {
                zzbwb().zzbwy().zzj("Error storing filter results", e);
            }
        } catch (IOException e2) {
            zzbwb().zzbwy().zzj("Configuration loss. Failed to serialize filter results", e2);
        }
    }

    public void zza(String str, long j, long j2, zzb com_google_android_gms_measurement_internal_zze_zzb) {
        Object e;
        Throwable th;
        zzaa.zzy(com_google_android_gms_measurement_internal_zze_zzb);
        zzzx();
        zzacj();
        Cursor cursor = null;
        SQLiteDatabase writableDatabase = getWritableDatabase();
        String str2;
        if (TextUtils.isEmpty(str)) {
            String[] strArr = j2 != -1 ? new String[]{String.valueOf(j2), String.valueOf(j)} : new String[]{String.valueOf(j)};
            str2 = j2 != -1 ? "rowid <= ? and " : "";
            cursor = writableDatabase.rawQuery(new StringBuilder(String.valueOf(str2).length() + 148).append("select app_id, metadata_fingerprint from raw_events where ").append(str2).append("app_id in (select app_id from apps where config_fetched_time >= ?) order by rowid limit 1;").toString(), strArr);
            if (cursor.moveToFirst()) {
                Object string = cursor.getString(0);
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
            cursor2 = writableDatabase.query("raw_events_metadata", new String[]{TtmlNode.TAG_METADATA}, "app_id = ? and metadata_fingerprint = ?", new String[]{string, str3}, null, null, "rowid", "2");
            if (cursor2.moveToFirst()) {
                zzars zzbd = zzars.zzbd(cursor2.getBlob(0));
                com.google.android.gms.internal.zzwc.zze com_google_android_gms_internal_zzwc_zze = new com.google.android.gms.internal.zzwc.zze();
                try {
                    String str4;
                    String[] strArr2;
                    com.google.android.gms.internal.zzwc.zze com_google_android_gms_internal_zzwc_zze2 = (com.google.android.gms.internal.zzwc.zze) com_google_android_gms_internal_zzwc_zze.zzb(zzbd);
                    if (cursor2.moveToNext()) {
                        zzbwb().zzbxa().log("Get multiple raw event metadata records, expected one");
                    }
                    cursor2.close();
                    com_google_android_gms_measurement_internal_zze_zzb.zzb(com_google_android_gms_internal_zzwc_zze);
                    if (j2 != -1) {
                        str4 = "app_id = ? and metadata_fingerprint = ? and rowid <= ?";
                        strArr2 = new String[]{string, str3, String.valueOf(j2)};
                    } else {
                        str4 = "app_id = ? and metadata_fingerprint = ?";
                        strArr2 = new String[]{string, str3};
                    }
                    cursor = writableDatabase.query("raw_events", new String[]{"rowid", "name", "timestamp", "data"}, str4, strArr2, null, null, "rowid", null);
                    if (cursor.moveToFirst()) {
                        do {
                            long j3 = cursor.getLong(0);
                            zzars zzbd2 = zzars.zzbd(cursor.getBlob(3));
                            com.google.android.gms.internal.zzwc.zzb com_google_android_gms_internal_zzwc_zzb = new com.google.android.gms.internal.zzwc.zzb();
                            try {
                                com.google.android.gms.internal.zzwc.zzb com_google_android_gms_internal_zzwc_zzb2 = (com.google.android.gms.internal.zzwc.zzb) com_google_android_gms_internal_zzwc_zzb.zzb(zzbd2);
                            } catch (IOException e2) {
                                zzbwb().zzbwy().zze("Data loss. Failed to merge raw event", string, e2);
                            }
                            try {
                                com_google_android_gms_internal_zzwc_zzb.name = cursor.getString(1);
                                com_google_android_gms_internal_zzwc_zzb.awO = Long.valueOf(cursor.getLong(2));
                                if (!com_google_android_gms_measurement_internal_zze_zzb.zza(j3, com_google_android_gms_internal_zzwc_zzb)) {
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
                    zzbwb().zzbxa().log("Raw event data disappeared while in transaction");
                    if (cursor != null) {
                        cursor.close();
                        return;
                    }
                    return;
                } catch (IOException e22) {
                    zzbwb().zzbwy().zze("Data loss. Failed to merge raw event metadata", string, e22);
                    if (cursor2 != null) {
                        cursor2.close();
                        return;
                    }
                    return;
                }
            }
            zzbwb().zzbwy().log("Raw event metadata record is missing");
            if (cursor2 != null) {
                cursor2.close();
            }
        } catch (SQLiteException e4) {
            e = e4;
            cursor = cursor2;
            try {
                zzbwb().zzbwy().zzj("Data loss. Error selecting raw event", e);
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
    public boolean zza(zzak com_google_android_gms_measurement_internal_zzak) {
        zzaa.zzy(com_google_android_gms_measurement_internal_zzak);
        zzzx();
        zzacj();
        if (zzar(com_google_android_gms_measurement_internal_zzak.zzctj, com_google_android_gms_measurement_internal_zzak.mName) == null) {
            long zzb;
            if (zzal.zzmu(com_google_android_gms_measurement_internal_zzak.mName)) {
                zzb = zzb("select count(1) from user_attributes where app_id=? and name not like '!_%' escape '!'", new String[]{com_google_android_gms_measurement_internal_zzak.zzctj});
                zzbwd().zzbun();
                if (zzb >= 25) {
                    return false;
                }
            }
            zzb = zzb("select count(1) from user_attributes where app_id=?", new String[]{com_google_android_gms_measurement_internal_zzak.zzctj});
            zzbwd().zzbuo();
            if (zzb >= 50) {
                return false;
            }
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("app_id", com_google_android_gms_measurement_internal_zzak.zzctj);
        contentValues.put("name", com_google_android_gms_measurement_internal_zzak.mName);
        contentValues.put("set_timestamp", Long.valueOf(com_google_android_gms_measurement_internal_zzak.avX));
        zza(contentValues, Param.VALUE, com_google_android_gms_measurement_internal_zzak.zzcyd);
        try {
            if (getWritableDatabase().insertWithOnConflict("user_attributes", null, contentValues, 5) == -1) {
                zzbwb().zzbwy().log("Failed to insert/update user property (got -1)");
            }
        } catch (SQLiteException e) {
            zzbwb().zzbwy().zzj("Error storing user property", e);
        }
        return true;
    }

    @WorkerThread
    void zzaa(String str, int i) {
        zzacj();
        zzzx();
        zzaa.zzib(str);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete("property_filters", "app_id=? and audience_id=?", new String[]{str, String.valueOf(i)});
        writableDatabase.delete("event_filters", "app_id=? and audience_id=?", new String[]{str, String.valueOf(i)});
    }

    String zzade() {
        return zzbwd().zzafe();
    }

    public void zzaf(List<Long> list) {
        zzaa.zzy(list);
        zzzx();
        zzacj();
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
            zzbwb().zzbwy().zze("Deleted fewer rows from raw events table than expected", Integer.valueOf(delete), Integer.valueOf(list.size()));
        }
    }

    @WorkerThread
    public zzi zzap(String str, String str2) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzaa.zzib(str);
        zzaa.zzib(str2);
        zzzx();
        zzacj();
        try {
            Cursor query = getWritableDatabase().query("events", new String[]{"lifetime_count", "current_bundle_count", "last_fire_timestamp"}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zzi com_google_android_gms_measurement_internal_zzi = new zzi(str, str2, query.getLong(0), query.getLong(1), query.getLong(2));
                    if (query.moveToNext()) {
                        zzbwb().zzbwy().log("Got multiple records for event aggregates, expected one");
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
                    zzbwb().zzbwy().zzd("Error querying events", str, str2, e);
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
            zzbwb().zzbwy().zzd("Error querying events", str, str2, e);
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
    public void zzaq(String str, String str2) {
        zzaa.zzib(str);
        zzaa.zzib(str2);
        zzzx();
        zzacj();
        try {
            zzbwb().zzbxe().zzj("Deleted user attribute rows:", Integer.valueOf(getWritableDatabase().delete("user_attributes", "app_id=? and name=?", new String[]{str, str2})));
        } catch (SQLiteException e) {
            zzbwb().zzbwy().zzd("Error deleting user attribute", str, str2, e);
        }
    }

    @WorkerThread
    public zzak zzar(String str, String str2) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzaa.zzib(str);
        zzaa.zzib(str2);
        zzzx();
        zzacj();
        try {
            Cursor query = getWritableDatabase().query("user_attributes", new String[]{"set_timestamp", Param.VALUE}, "app_id=? and name=?", new String[]{str, str2}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zzak com_google_android_gms_measurement_internal_zzak = new zzak(str, str2, query.getLong(0), zzb(query, 1));
                    if (query.moveToNext()) {
                        zzbwb().zzbwy().log("Got multiple records for user property, expected one");
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
                    zzbwb().zzbwy().zzd("Error querying user property", str, str2, e);
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
            zzbwb().zzbwy().zzd("Error querying user property", str, str2, e);
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

    Map<Integer, List<com.google.android.gms.internal.zzwa.zzb>> zzas(String str, String str2) {
        Object e;
        Throwable th;
        zzacj();
        zzzx();
        zzaa.zzib(str);
        zzaa.zzib(str2);
        Map<Integer, List<com.google.android.gms.internal.zzwa.zzb>> arrayMap = new ArrayMap();
        Cursor query;
        try {
            query = getWritableDatabase().query("event_filters", new String[]{"audience_id", "data"}, "app_id=? AND event_name=?", new String[]{str, str2}, null, null, null);
            if (query.moveToFirst()) {
                do {
                    try {
                        zzars zzbd = zzars.zzbd(query.getBlob(1));
                        com.google.android.gms.internal.zzwa.zzb com_google_android_gms_internal_zzwa_zzb = new com.google.android.gms.internal.zzwa.zzb();
                        try {
                            com.google.android.gms.internal.zzwa.zzb com_google_android_gms_internal_zzwa_zzb2 = (com.google.android.gms.internal.zzwa.zzb) com_google_android_gms_internal_zzwa_zzb.zzb(zzbd);
                            int i = query.getInt(0);
                            List list = (List) arrayMap.get(Integer.valueOf(i));
                            if (list == null) {
                                list = new ArrayList();
                                arrayMap.put(Integer.valueOf(i), list);
                            }
                            list.add(com_google_android_gms_internal_zzwa_zzb);
                        } catch (IOException e2) {
                            zzbwb().zzbwy().zze("Failed to merge filter", str, e2);
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
            Map<Integer, List<com.google.android.gms.internal.zzwa.zzb>> emptyMap = Collections.emptyMap();
            if (query == null) {
                return emptyMap;
            }
            query.close();
            return emptyMap;
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            try {
                zzbwb().zzbwy().zzj("Database error querying filters", e);
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

    Map<Integer, List<com.google.android.gms.internal.zzwa.zze>> zzat(String str, String str2) {
        Cursor query;
        Object e;
        Throwable th;
        zzacj();
        zzzx();
        zzaa.zzib(str);
        zzaa.zzib(str2);
        Map<Integer, List<com.google.android.gms.internal.zzwa.zze>> arrayMap = new ArrayMap();
        try {
            query = getWritableDatabase().query("property_filters", new String[]{"audience_id", "data"}, "app_id=? AND property_name=?", new String[]{str, str2}, null, null, null);
            if (query.moveToFirst()) {
                do {
                    try {
                        zzars zzbd = zzars.zzbd(query.getBlob(1));
                        com.google.android.gms.internal.zzwa.zze com_google_android_gms_internal_zzwa_zze = new com.google.android.gms.internal.zzwa.zze();
                        try {
                            com.google.android.gms.internal.zzwa.zze com_google_android_gms_internal_zzwa_zze2 = (com.google.android.gms.internal.zzwa.zze) com_google_android_gms_internal_zzwa_zze.zzb(zzbd);
                            int i = query.getInt(0);
                            List list = (List) arrayMap.get(Integer.valueOf(i));
                            if (list == null) {
                                list = new ArrayList();
                                arrayMap.put(Integer.valueOf(i), list);
                            }
                            list.add(com_google_android_gms_internal_zzwa_zze);
                        } catch (IOException e2) {
                            zzbwb().zzbwy().zze("Failed to merge filter", str, e2);
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
            Map<Integer, List<com.google.android.gms.internal.zzwa.zze>> emptyMap = Collections.emptyMap();
            if (query == null) {
                return emptyMap;
            }
            query.close();
            return emptyMap;
        } catch (SQLiteException e4) {
            e = e4;
            query = null;
            try {
                zzbwb().zzbwy().zzj("Database error querying filters", e);
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
    protected long zzau(String str, String str2) {
        Object e;
        zzaa.zzib(str);
        zzaa.zzib(str2);
        zzzx();
        zzacj();
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
                    zzbwb().zzbwy().zzj("Failed to insert column (got -1)", str2);
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
                    zzbwb().zzbwy().zzj("Failed to update column (got 0)", str2);
                    writableDatabase.endTransaction();
                    return -1;
                }
                writableDatabase.setTransactionSuccessful();
                writableDatabase.endTransaction();
                return zza;
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzbwb().zzbwy().zze("Error inserting column", str2, e);
                    return zza;
                } finally {
                    writableDatabase.endTransaction();
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            zza = 0;
            zzbwb().zzbwy().zze("Error inserting column", str2, e);
            return zza;
        }
    }

    @WorkerThread
    Object zzb(Cursor cursor, int i) {
        int zza = zza(cursor, i);
        switch (zza) {
            case 0:
                zzbwb().zzbwy().log("Loaded invalid null value from database");
                return null;
            case 1:
                return Long.valueOf(cursor.getLong(i));
            case 2:
                return Double.valueOf(cursor.getDouble(i));
            case 3:
                return cursor.getString(i);
            case 4:
                zzbwb().zzbwy().log("Loaded invalid blob type value, ignoring it");
                return null;
            default:
                zzbwb().zzbwy().zzj("Loaded invalid unknown value type, ignoring it", Integer.valueOf(zza));
                return null;
        }
    }

    @WorkerThread
    void zzb(String str, com.google.android.gms.internal.zzwa.zza[] com_google_android_gms_internal_zzwa_zzaArr) {
        int i = 0;
        zzacj();
        zzzx();
        zzaa.zzib(str);
        zzaa.zzy(com_google_android_gms_internal_zzwa_zzaArr);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            zzmc(str);
            for (com.google.android.gms.internal.zzwa.zza zza : com_google_android_gms_internal_zzwa_zzaArr) {
                zza(str, zza);
            }
            List arrayList = new ArrayList();
            int length = com_google_android_gms_internal_zzwa_zzaArr.length;
            while (i < length) {
                arrayList.add(com_google_android_gms_internal_zzwa_zzaArr[i].avZ);
                i++;
            }
            zzc(str, arrayList);
            writableDatabase.setTransactionSuccessful();
        } finally {
            writableDatabase.endTransaction();
        }
    }

    @WorkerThread
    public void zzbj(long j) {
        zzzx();
        zzacj();
        if (getWritableDatabase().delete("queue", "rowid=?", new String[]{String.valueOf(j)}) != 1) {
            zzbwb().zzbwy().log("Deleted fewer rows from queue than expected");
        }
    }

    public String zzbk(long j) {
        Cursor rawQuery;
        Object e;
        Throwable th;
        String str = null;
        zzzx();
        zzacj();
        try {
            rawQuery = getWritableDatabase().rawQuery("select app_id from apps where app_id in (select distinct app_id from raw_events) and config_fetched_time < ? order by failed_config_fetch_time limit 1;", new String[]{String.valueOf(j)});
            try {
                if (rawQuery.moveToFirst()) {
                    str = rawQuery.getString(0);
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                } else {
                    zzbwb().zzbxe().log("No expired configs for apps with pending events");
                    if (rawQuery != null) {
                        rawQuery.close();
                    }
                }
            } catch (SQLiteException e2) {
                e = e2;
                try {
                    zzbwb().zzbwy().zzj("Error selecting expired configs", e);
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
            zzbwb().zzbwy().zzj("Error selecting expired configs", e);
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
    public String zzbwe() {
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
                    zzbwb().zzbwy().zzj("Database error getting next bundle app id", e);
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
            zzbwb().zzbwy().zzj("Database error getting next bundle app id", e);
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

    public boolean zzbwf() {
        return zzb("select count(1) > 0 from queue where has_realtime = 1", null) != 0;
    }

    @WorkerThread
    void zzbwg() {
        zzzx();
        zzacj();
        if (zzbwn()) {
            long j = zzbwc().atc.get();
            long elapsedRealtime = zzabz().elapsedRealtime();
            if (Math.abs(elapsedRealtime - j) > zzbwd().zzbuw()) {
                zzbwc().atc.set(elapsedRealtime);
                zzbwh();
            }
        }
    }

    @WorkerThread
    void zzbwh() {
        zzzx();
        zzacj();
        if (zzbwn()) {
            int delete = getWritableDatabase().delete("queue", "abs(bundle_end_timestamp - ?) > cast(? as integer)", new String[]{String.valueOf(zzabz().currentTimeMillis()), String.valueOf(zzbwd().zzbuv())});
            if (delete > 0) {
                zzbwb().zzbxe().zzj("Deleted stale rows. rowsDeleted", Integer.valueOf(delete));
            }
        }
    }

    @WorkerThread
    public long zzbwi() {
        return zza("select max(bundle_end_timestamp) from queue", null, 0);
    }

    @WorkerThread
    public long zzbwj() {
        return zza("select max(timestamp) from raw_events", null, 0);
    }

    public boolean zzbwk() {
        return zzb("select count(1) > 0 from raw_events", null) != 0;
    }

    public boolean zzbwl() {
        return zzb("select count(1) > 0 from raw_events where realtime = 1", null) != 0;
    }

    public long zzbwm() {
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
            zzbwb().zzbwy().zzj("Error querying raw events", e);
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

    boolean zzc(String str, List<Integer> list) {
        zzaa.zzib(str);
        zzacj();
        zzzx();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        try {
            if (zzb("select count(1) from audience_filter_values where app_id=?", new String[]{str}) <= ((long) zzbwd().zzlt(str))) {
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
            zzbwb().zzbwy().zzj("Database error querying filters", e);
            return false;
        }
    }

    @WorkerThread
    public void zzd(String str, byte[] bArr) {
        zzaa.zzib(str);
        zzzx();
        zzacj();
        ContentValues contentValues = new ContentValues();
        contentValues.put("remote_config", bArr);
        try {
            if (((long) getWritableDatabase().update("apps", contentValues, "app_id = ?", new String[]{str})) == 0) {
                zzbwb().zzbwy().log("Failed to update remote config (got 0)");
            }
        } catch (SQLiteException e) {
            zzbwb().zzbwy().zzj("Error storing remote config", e);
        }
    }

    @WorkerThread
    public List<zzak> zzly(String str) {
        Object e;
        Cursor cursor;
        Throwable th;
        Cursor cursor2 = null;
        zzaa.zzib(str);
        zzzx();
        zzacj();
        List<zzak> arrayList = new ArrayList();
        try {
            Cursor query = getWritableDatabase().query("user_attributes", new String[]{"name", "set_timestamp", Param.VALUE}, "app_id=?", new String[]{str}, null, null, "rowid", String.valueOf(zzbwd().zzbuo()));
            try {
                if (query.moveToFirst()) {
                    do {
                        String string = query.getString(0);
                        long j = query.getLong(1);
                        Object zzb = zzb(query, 2);
                        if (zzb == null) {
                            zzbwb().zzbwy().log("Read invalid user property value, ignoring it");
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
                zzbwb().zzbwy().zze("Error querying user properties", str, e);
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
        zzaa.zzib(str);
        zzzx();
        zzacj();
        try {
            query = getWritableDatabase().query("apps", new String[]{"app_instance_id", "gmp_app_id", "resettable_device_id_hash", "last_bundle_index", "last_bundle_start_timestamp", "last_bundle_end_timestamp", "app_version", "app_store", "gmp_version", "dev_cert_hash", "measurement_enabled", "day", "daily_public_events_count", "daily_events_count", "daily_conversions_count", "config_fetched_time", "failed_config_fetch_time", "app_version_int", "firebase_instance_id", "daily_error_events_count", "daily_realtime_events_count"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    zza com_google_android_gms_measurement_internal_zza = new zza(this.aqw, str);
                    com_google_android_gms_measurement_internal_zza.zzlj(query.getString(0));
                    com_google_android_gms_measurement_internal_zza.zzlk(query.getString(1));
                    com_google_android_gms_measurement_internal_zza.zzll(query.getString(2));
                    com_google_android_gms_measurement_internal_zza.zzba(query.getLong(3));
                    com_google_android_gms_measurement_internal_zza.zzav(query.getLong(4));
                    com_google_android_gms_measurement_internal_zza.zzaw(query.getLong(5));
                    com_google_android_gms_measurement_internal_zza.setAppVersion(query.getString(6));
                    com_google_android_gms_measurement_internal_zza.zzln(query.getString(7));
                    com_google_android_gms_measurement_internal_zza.zzay(query.getLong(8));
                    com_google_android_gms_measurement_internal_zza.zzaz(query.getLong(9));
                    com_google_android_gms_measurement_internal_zza.setMeasurementEnabled((query.isNull(10) ? 1 : query.getInt(10)) != 0);
                    com_google_android_gms_measurement_internal_zza.zzbd(query.getLong(11));
                    com_google_android_gms_measurement_internal_zza.zzbe(query.getLong(12));
                    com_google_android_gms_measurement_internal_zza.zzbf(query.getLong(13));
                    com_google_android_gms_measurement_internal_zza.zzbg(query.getLong(14));
                    com_google_android_gms_measurement_internal_zza.zzbb(query.getLong(15));
                    com_google_android_gms_measurement_internal_zza.zzbc(query.getLong(16));
                    com_google_android_gms_measurement_internal_zza.zzax(query.isNull(17) ? -2147483648L : (long) query.getInt(17));
                    com_google_android_gms_measurement_internal_zza.zzlm(query.getString(18));
                    com_google_android_gms_measurement_internal_zza.zzbi(query.getLong(19));
                    com_google_android_gms_measurement_internal_zza.zzbh(query.getLong(20));
                    com_google_android_gms_measurement_internal_zza.zzbtg();
                    if (query.moveToNext()) {
                        zzbwb().zzbwy().log("Got multiple records for app, expected one");
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
                    zzbwb().zzbwy().zze("Error querying app", str, e);
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
            zzbwb().zzbwy().zze("Error querying app", str, e);
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
        zzaa.zzib(str);
        zzzx();
        zzacj();
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            String valueOf = String.valueOf(zzbwd().zzlx(str));
            return (long) writableDatabase.delete("raw_events", "rowid in (select rowid from raw_events where app_id=? order by rowid desc limit -1 offset ?)", new String[]{str, valueOf});
        } catch (SQLiteException e) {
            zzbwb().zzbwy().zzj("Error deleting over the limit events", e);
            return 0;
        }
    }

    @WorkerThread
    public byte[] zzmb(String str) {
        Object e;
        Throwable th;
        zzaa.zzib(str);
        zzzx();
        zzacj();
        Cursor query;
        try {
            query = getWritableDatabase().query("apps", new String[]{"remote_config"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    byte[] blob = query.getBlob(0);
                    if (query.moveToNext()) {
                        zzbwb().zzbwy().log("Got multiple records for app config, expected one");
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
                    zzbwb().zzbwy().zze("Error querying remote config", str, e);
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
            zzbwb().zzbwy().zze("Error querying remote config", str, e);
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
        zzacj();
        zzzx();
        zzaa.zzib(str);
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.delete("property_filters", "app_id=?", new String[]{str});
        writableDatabase.delete("event_filters", "app_id=?", new String[]{str});
    }

    Map<Integer, zzf> zzmd(String str) {
        Object e;
        Cursor cursor;
        Throwable th;
        zzacj();
        zzzx();
        zzaa.zzib(str);
        Cursor query;
        try {
            query = getWritableDatabase().query("audience_filter_values", new String[]{"audience_id", "current_results"}, "app_id=?", new String[]{str}, null, null, null);
            try {
                if (query.moveToFirst()) {
                    Map<Integer, zzf> arrayMap = new ArrayMap();
                    do {
                        int i = query.getInt(0);
                        zzars zzbd = zzars.zzbd(query.getBlob(1));
                        zzf com_google_android_gms_internal_zzwc_zzf = new zzf();
                        try {
                            zzf com_google_android_gms_internal_zzwc_zzf2 = (zzf) com_google_android_gms_internal_zzwc_zzf.zzb(zzbd);
                            arrayMap.put(Integer.valueOf(i), com_google_android_gms_internal_zzwc_zzf);
                        } catch (IOException e2) {
                            zzbwb().zzbwy().zzd("Failed to merge filter results. appId, audienceId, error", str, Integer.valueOf(i), e2);
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
                zzbwb().zzbwy().zzj("Database error querying filter results", e);
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
        zzacj();
        zzzx();
        zzaa.zzib(str);
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            String[] strArr = new String[]{str};
            int delete = writableDatabase.delete("audience_filter_values", "app_id=?", strArr) + (((((((writableDatabase.delete("events", "app_id=?", strArr) + 0) + writableDatabase.delete("user_attributes", "app_id=?", strArr)) + writableDatabase.delete("apps", "app_id=?", strArr)) + writableDatabase.delete("raw_events", "app_id=?", strArr)) + writableDatabase.delete("raw_events_metadata", "app_id=?", strArr)) + writableDatabase.delete("event_filters", "app_id=?", strArr)) + writableDatabase.delete("property_filters", "app_id=?", strArr));
            if (delete > 0) {
                zzbwb().zzbxe().zze("Deleted application data. app, records", str, Integer.valueOf(delete));
            }
        } catch (SQLiteException e) {
            zzbwb().zzbwy().zze("Error deleting application data. appId, error", str, e);
        }
    }

    @WorkerThread
    public long zzmf(String str) {
        zzaa.zzib(str);
        zzzx();
        zzacj();
        return zzau(str, "first_open_count");
    }

    public void zzmg(String str) {
        try {
            getWritableDatabase().execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[]{str, str});
        } catch (SQLiteException e) {
            zzbwb().zzbwy().zzj("Failed to remove unused event metadata", e);
        }
    }

    public long zzmh(String str) {
        zzaa.zzib(str);
        return zza("select count(1) from events where app_id=? and name not like '!_%' escape '!'", new String[]{str}, 0);
    }

    @WorkerThread
    public List<Pair<com.google.android.gms.internal.zzwc.zze, Long>> zzn(String str, int i, int i2) {
        Object e;
        Cursor cursor;
        Throwable th;
        boolean z = true;
        zzzx();
        zzacj();
        zzaa.zzbt(i > 0);
        if (i2 <= 0) {
            z = false;
        }
        zzaa.zzbt(z);
        zzaa.zzib(str);
        Cursor query;
        List<Pair<com.google.android.gms.internal.zzwc.zze, Long>> emptyList;
        try {
            query = getWritableDatabase().query("queue", new String[]{"rowid", "data"}, "app_id=?", new String[]{str}, null, null, "rowid", String.valueOf(i));
            try {
                if (query.moveToFirst()) {
                    List<Pair<com.google.android.gms.internal.zzwc.zze, Long>> arrayList = new ArrayList();
                    int i3 = 0;
                    while (true) {
                        long j = query.getLong(0);
                        int length;
                        try {
                            byte[] zzx = zzbvx().zzx(query.getBlob(1));
                            if (!arrayList.isEmpty() && zzx.length + i3 > i2) {
                                break;
                            }
                            zzars zzbd = zzars.zzbd(zzx);
                            com.google.android.gms.internal.zzwc.zze com_google_android_gms_internal_zzwc_zze = new com.google.android.gms.internal.zzwc.zze();
                            try {
                                com.google.android.gms.internal.zzwc.zze com_google_android_gms_internal_zzwc_zze2 = (com.google.android.gms.internal.zzwc.zze) com_google_android_gms_internal_zzwc_zze.zzb(zzbd);
                                length = zzx.length + i3;
                                arrayList.add(Pair.create(com_google_android_gms_internal_zzwc_zze, Long.valueOf(j)));
                            } catch (IOException e2) {
                                zzbwb().zzbwy().zze("Failed to merge queued bundle", str, e2);
                                length = i3;
                            }
                            if (!query.moveToNext() || length > i2) {
                                break;
                            }
                            i3 = length;
                        } catch (IOException e22) {
                            zzbwb().zzbwy().zze("Failed to unzip queued bundle", str, e22);
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
                zzbwb().zzbwy().zze("Error querying bundles", str, e);
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

    @WorkerThread
    public void zzz(String str, int i) {
        zzaa.zzib(str);
        zzzx();
        zzacj();
        try {
            getWritableDatabase().execSQL("delete from user_attributes where app_id=? and name in (select name from user_attributes where app_id=? and name like '_ltv_%' order by set_timestamp desc limit ?,10);", new String[]{str, str, String.valueOf(i)});
        } catch (SQLiteException e) {
            zzbwb().zzbwy().zze("Error pruning currencies", str, e);
        }
    }

    protected void zzzy() {
    }
}
