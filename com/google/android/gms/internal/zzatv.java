package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.util.zze;
import java.util.ArrayList;
import java.util.List;

public class zzatv extends zzauh {
    private boolean zzbsA;
    private final zza zzbsz = new zza(this, getContext(), zzow());

    @TargetApi(11)
    private class zza extends SQLiteOpenHelper {
        final /* synthetic */ zzatv zzbsB;

        zza(zzatv com_google_android_gms_internal_zzatv, Context context, String str) {
            this.zzbsB = com_google_android_gms_internal_zzatv;
            super(context, str, null, 1);
        }

        @WorkerThread
        public SQLiteDatabase getWritableDatabase() {
            try {
                return super.getWritableDatabase();
            } catch (SQLiteException e) {
                if (VERSION.SDK_INT < 11 || !(e instanceof SQLiteDatabaseLockedException)) {
                    this.zzbsB.zzKl().zzLZ().log("Opening the local database failed, dropping and recreating it");
                    String zzow = this.zzbsB.zzow();
                    if (!this.zzbsB.getContext().getDatabasePath(zzow).delete()) {
                        this.zzbsB.zzKl().zzLZ().zzj("Failed to delete corrupted local db file", zzow);
                    }
                    try {
                        return super.getWritableDatabase();
                    } catch (SQLiteException e2) {
                        this.zzbsB.zzKl().zzLZ().zzj("Failed to open local database. Events will bypass local storage", e2);
                        return null;
                    }
                }
                throw e2;
            }
        }

        @WorkerThread
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            zzatj.zza(this.zzbsB.zzKl(), sQLiteDatabase);
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
            zzatj.zza(this.zzbsB.zzKl(), sQLiteDatabase, "messages", "create table if not exists messages ( type INTEGER NOT NULL, entry BLOB NOT NULL)", "type,entry", null);
        }

        @WorkerThread
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    zzatv(zzaue com_google_android_gms_internal_zzaue) {
        super(com_google_android_gms_internal_zzaue);
    }

    @WorkerThread
    @TargetApi(11)
    private boolean zza(int i, byte[] bArr) {
        zzJW();
        zzmR();
        if (this.zzbsA) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", Integer.valueOf(i));
        contentValues.put("entry", bArr);
        zzKn().zzLq();
        int i2 = 0;
        int i3 = 5;
        while (i2 < 5) {
            SQLiteDatabase sQLiteDatabase = null;
            try {
                sQLiteDatabase = getWritableDatabase();
                if (sQLiteDatabase == null) {
                    this.zzbsA = true;
                    if (sQLiteDatabase != null) {
                        sQLiteDatabase.close();
                    }
                    return false;
                }
                sQLiteDatabase.beginTransaction();
                long j = 0;
                Cursor rawQuery = sQLiteDatabase.rawQuery("select count(1) from messages", null);
                if (rawQuery != null && rawQuery.moveToFirst()) {
                    j = rawQuery.getLong(0);
                }
                if (j >= 100000) {
                    zzKl().zzLZ().log("Data loss, local db full");
                    j = (100000 - j) + 1;
                    long delete = (long) sQLiteDatabase.delete("messages", "rowid in (select rowid from messages order by rowid asc limit ?)", new String[]{Long.toString(j)});
                    if (delete != j) {
                        zzKl().zzLZ().zzd("Different delete count than expected in local db. expected, received, difference", Long.valueOf(j), Long.valueOf(delete), Long.valueOf(j - delete));
                    }
                }
                sQLiteDatabase.insertOrThrow("messages", null, contentValues);
                sQLiteDatabase.setTransactionSuccessful();
                sQLiteDatabase.endTransaction();
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
                return true;
            } catch (SQLiteFullException e) {
                zzKl().zzLZ().zzj("Error writing entry to local database", e);
                this.zzbsA = true;
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
                i2++;
            } catch (SQLiteException e2) {
                if (VERSION.SDK_INT < 11 || !(e2 instanceof SQLiteDatabaseLockedException)) {
                    if (sQLiteDatabase != null) {
                        if (sQLiteDatabase.inTransaction()) {
                            sQLiteDatabase.endTransaction();
                        }
                    }
                    zzKl().zzLZ().zzj("Error writing entry to local database", e2);
                    this.zzbsA = true;
                } else {
                    SystemClock.sleep((long) i3);
                    i3 += 20;
                }
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
                i2++;
            } catch (Throwable th) {
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
            }
        }
        zzKl().zzMb().log("Failed to write entry to local database");
        return false;
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @WorkerThread
    SQLiteDatabase getWritableDatabase() {
        int i = VERSION.SDK_INT;
        if (this.zzbsA) {
            return null;
        }
        SQLiteDatabase writableDatabase = this.zzbsz.getWritableDatabase();
        if (writableDatabase != null) {
            return writableDatabase;
        }
        this.zzbsA = true;
        return null;
    }

    public /* bridge */ /* synthetic */ void zzJV() {
        super.zzJV();
    }

    public /* bridge */ /* synthetic */ void zzJW() {
        super.zzJW();
    }

    public /* bridge */ /* synthetic */ void zzJX() {
        super.zzJX();
    }

    public /* bridge */ /* synthetic */ zzatb zzJY() {
        return super.zzJY();
    }

    public /* bridge */ /* synthetic */ zzatf zzJZ() {
        return super.zzJZ();
    }

    public /* bridge */ /* synthetic */ zzauj zzKa() {
        return super.zzKa();
    }

    public /* bridge */ /* synthetic */ zzatu zzKb() {
        return super.zzKb();
    }

    public /* bridge */ /* synthetic */ zzatl zzKc() {
        return super.zzKc();
    }

    public /* bridge */ /* synthetic */ zzaul zzKd() {
        return super.zzKd();
    }

    public /* bridge */ /* synthetic */ zzauk zzKe() {
        return super.zzKe();
    }

    public /* bridge */ /* synthetic */ zzatv zzKf() {
        return super.zzKf();
    }

    public /* bridge */ /* synthetic */ zzatj zzKg() {
        return super.zzKg();
    }

    public /* bridge */ /* synthetic */ zzaut zzKh() {
        return super.zzKh();
    }

    public /* bridge */ /* synthetic */ zzauc zzKi() {
        return super.zzKi();
    }

    public /* bridge */ /* synthetic */ zzaun zzKj() {
        return super.zzKj();
    }

    public /* bridge */ /* synthetic */ zzaud zzKk() {
        return super.zzKk();
    }

    public /* bridge */ /* synthetic */ zzatx zzKl() {
        return super.zzKl();
    }

    public /* bridge */ /* synthetic */ zzaua zzKm() {
        return super.zzKm();
    }

    public /* bridge */ /* synthetic */ zzati zzKn() {
        return super.zzKn();
    }

    boolean zzLN() {
        return getContext().getDatabasePath(zzow()).exists();
    }

    public boolean zza(zzatq com_google_android_gms_internal_zzatq) {
        int i = VERSION.SDK_INT;
        Parcel obtain = Parcel.obtain();
        com_google_android_gms_internal_zzatq.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        if (marshall.length <= 131072) {
            return zza(0, marshall);
        }
        zzKl().zzMb().log("Event is too long for local database. Sending event directly to service");
        return false;
    }

    public boolean zza(zzauq com_google_android_gms_internal_zzauq) {
        int i = VERSION.SDK_INT;
        Parcel obtain = Parcel.obtain();
        com_google_android_gms_internal_zzauq.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        if (marshall.length <= 131072) {
            return zza(1, marshall);
        }
        zzKl().zzMb().log("User property too long for local database. Sending directly to service");
        return false;
    }

    public boolean zzc(zzatg com_google_android_gms_internal_zzatg) {
        int i = VERSION.SDK_INT;
        byte[] zza = zzKh().zza((Parcelable) com_google_android_gms_internal_zzatg);
        if (zza.length <= 131072) {
            return zza(2, zza);
        }
        zzKl().zzMb().log("Conditional user property too long for local database. Sending directly to service");
        return false;
    }

    @TargetApi(11)
    public List<com.google.android.gms.common.internal.safeparcel.zza> zzlD(int i) {
        Object obj;
        Throwable th;
        Object obj2;
        zzmR();
        zzJW();
        int i2 = VERSION.SDK_INT;
        if (this.zzbsA) {
            return null;
        }
        List<com.google.android.gms.common.internal.safeparcel.zza> arrayList = new ArrayList();
        if (!zzLN()) {
            return arrayList;
        }
        int i3 = 5;
        int i4 = 0;
        while (i4 < 5) {
            SQLiteDatabase sQLiteDatabase = null;
            try {
                SQLiteDatabase writableDatabase = getWritableDatabase();
                if (writableDatabase == null) {
                    try {
                        this.zzbsA = true;
                        if (writableDatabase != null) {
                            writableDatabase.close();
                        }
                        return null;
                    } catch (SQLiteFullException e) {
                        SQLiteFullException sQLiteFullException = e;
                        sQLiteDatabase = writableDatabase;
                        obj = sQLiteFullException;
                    } catch (SQLiteException e2) {
                        SQLiteException sQLiteException = e2;
                        sQLiteDatabase = writableDatabase;
                        obj = sQLiteException;
                    } catch (Throwable th2) {
                        Throwable th3 = th2;
                        sQLiteDatabase = writableDatabase;
                        th = th3;
                    }
                } else {
                    writableDatabase.beginTransaction();
                    Cursor query = writableDatabase.query("messages", new String[]{"rowid", "type", "entry"}, null, null, null, null, "rowid asc", Integer.toString(i));
                    long j = -1;
                    while (query.moveToNext()) {
                        long j2 = query.getLong(0);
                        int i5 = query.getInt(1);
                        byte[] blob = query.getBlob(2);
                        if (i5 == 0) {
                            Parcel obtain = Parcel.obtain();
                            try {
                                obtain.unmarshall(blob, 0, blob.length);
                                obtain.setDataPosition(0);
                                obj2 = (zzatq) zzatq.CREATOR.createFromParcel(obtain);
                                if (obj2 != null) {
                                    arrayList.add(obj2);
                                }
                            } catch (com.google.android.gms.common.internal.safeparcel.zzb.zza e3) {
                                obj2 = zzKl().zzLZ();
                                obj2.log("Failed to load event from local database");
                                j = j2;
                            } finally {
                                obtain.recycle();
                            }
                        } else if (i5 == 1) {
                            r7 = Parcel.obtain();
                            try {
                                r7.unmarshall(blob, 0, blob.length);
                                r7.setDataPosition(0);
                                obj2 = (zzauq) zzauq.CREATOR.createFromParcel(r7);
                            } catch (com.google.android.gms.common.internal.safeparcel.zzb.zza e4) {
                                obj2 = zzKl().zzLZ();
                                obj2.log("Failed to load user property from local database");
                                obj2 = null;
                                if (obj2 != null) {
                                    arrayList.add(obj2);
                                }
                                j = j2;
                            } finally {
                                r7.recycle();
                            }
                            if (obj2 != null) {
                                arrayList.add(obj2);
                            }
                        } else if (i5 == 2) {
                            r7 = Parcel.obtain();
                            try {
                                r7.unmarshall(blob, 0, blob.length);
                                r7.setDataPosition(0);
                                obj2 = (zzatg) zzatg.CREATOR.createFromParcel(r7);
                            } catch (com.google.android.gms.common.internal.safeparcel.zzb.zza e5) {
                                obj2 = zzKl().zzLZ();
                                obj2.log("Failed to load user property from local database");
                                obj2 = null;
                                if (obj2 != null) {
                                    arrayList.add(obj2);
                                }
                                j = j2;
                            } finally {
                                r7.recycle();
                            }
                            if (obj2 != null) {
                                arrayList.add(obj2);
                            }
                        } else {
                            zzKl().zzLZ().log("Unknown record type in local database");
                        }
                        j = j2;
                    }
                    query.close();
                    if (writableDatabase.delete("messages", "rowid <= ?", new String[]{Long.toString(j)}) < arrayList.size()) {
                        zzKl().zzLZ().log("Fewer entries removed from local database than expected");
                    }
                    writableDatabase.setTransactionSuccessful();
                    writableDatabase.endTransaction();
                    if (writableDatabase != null) {
                        writableDatabase.close();
                    }
                    return arrayList;
                }
            } catch (SQLiteFullException e6) {
                obj = e6;
                try {
                    zzKl().zzLZ().zzj("Error reading entries from local database", obj);
                    this.zzbsA = true;
                    if (sQLiteDatabase != null) {
                        sQLiteDatabase.close();
                        i2 = i3;
                    } else {
                        i2 = i3;
                    }
                    i4++;
                    i3 = i2;
                } catch (Throwable th4) {
                    th = th4;
                }
            } catch (SQLiteException e7) {
                obj = e7;
                if (VERSION.SDK_INT < 11 || !(obj instanceof SQLiteDatabaseLockedException)) {
                    if (sQLiteDatabase != null) {
                        if (sQLiteDatabase.inTransaction()) {
                            sQLiteDatabase.endTransaction();
                        }
                    }
                    zzKl().zzLZ().zzj("Error reading entries from local database", obj);
                    this.zzbsA = true;
                    i2 = i3;
                } else {
                    SystemClock.sleep((long) i3);
                    i2 = i3 + 20;
                }
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
                i4++;
                i3 = i2;
            }
        }
        zzKl().zzMb().log("Failed to read events from database in reasonable time");
        return null;
        if (sQLiteDatabase != null) {
            sQLiteDatabase.close();
        }
        throw th;
    }

    public /* bridge */ /* synthetic */ void zzmR() {
        super.zzmR();
    }

    protected void zzmS() {
    }

    public /* bridge */ /* synthetic */ zze zznR() {
        return super.zznR();
    }

    String zzow() {
        return zzKn().zzLg();
    }
}
