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
import android.os.SystemClock;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.util.zze;
import java.util.ArrayList;
import java.util.List;

public class zzatg extends zzats {
    private final zza zzbrD = new zza(this, getContext(), zznV());
    private boolean zzbrE;

    @TargetApi(11)
    private class zza extends SQLiteOpenHelper {
        final /* synthetic */ zzatg zzbrF;

        zza(zzatg com_google_android_gms_internal_zzatg, Context context, String str) {
            this.zzbrF = com_google_android_gms_internal_zzatg;
            super(context, str, null, 1);
        }

        @WorkerThread
        public SQLiteDatabase getWritableDatabase() {
            try {
                return super.getWritableDatabase();
            } catch (SQLiteException e) {
                if (VERSION.SDK_INT < 11 || !(e instanceof SQLiteDatabaseLockedException)) {
                    this.zzbrF.zzJt().zzLa().log("Opening the local database failed, dropping and recreating it");
                    String zznV = this.zzbrF.zznV();
                    if (!this.zzbrF.getContext().getDatabasePath(zznV).delete()) {
                        this.zzbrF.zzJt().zzLa().zzj("Failed to delete corrupted local db file", zznV);
                    }
                    try {
                        return super.getWritableDatabase();
                    } catch (SQLiteException e2) {
                        this.zzbrF.zzJt().zzLa().zzj("Failed to open local database. Events will bypass local storage", e2);
                        return null;
                    }
                }
                throw e2;
            }
        }

        @WorkerThread
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            zzasu.zza(this.zzbrF.zzJt(), sQLiteDatabase);
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
            zzasu.zza(this.zzbrF.zzJt(), sQLiteDatabase, "messages", "create table if not exists messages ( type INTEGER NOT NULL, entry BLOB NOT NULL)", "type,entry", null);
        }

        @WorkerThread
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    zzatg(zzatp com_google_android_gms_internal_zzatp) {
        super(com_google_android_gms_internal_zzatp);
    }

    @WorkerThread
    @TargetApi(11)
    private boolean zza(int i, byte[] bArr) {
        zzJe();
        zzmq();
        if (this.zzbrE) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", Integer.valueOf(i));
        contentValues.put("entry", bArr);
        zzJv().zzKt();
        int i2 = 0;
        int i3 = 5;
        while (i2 < 5) {
            SQLiteDatabase sQLiteDatabase = null;
            try {
                sQLiteDatabase = getWritableDatabase();
                if (sQLiteDatabase == null) {
                    this.zzbrE = true;
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
                    zzJt().zzLa().log("Data loss, local db full");
                    j = (100000 - j) + 1;
                    long delete = (long) sQLiteDatabase.delete("messages", "rowid in (select rowid from messages order by rowid asc limit ?)", new String[]{Long.toString(j)});
                    if (delete != j) {
                        zzJt().zzLa().zzd("Different delete count than expected in local db. expected, received, difference", Long.valueOf(j), Long.valueOf(delete), Long.valueOf(j - delete));
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
                zzJt().zzLa().zzj("Error writing entry to local database", e);
                this.zzbrE = true;
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
                    zzJt().zzLa().zzj("Error writing entry to local database", e2);
                    this.zzbrE = true;
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
        zzJt().zzLc().log("Failed to write entry to local database");
        return false;
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @WorkerThread
    SQLiteDatabase getWritableDatabase() {
        if (VERSION.SDK_INT < 11 || this.zzbrE) {
            return null;
        }
        SQLiteDatabase writableDatabase = this.zzbrD.getWritableDatabase();
        if (writableDatabase != null) {
            return writableDatabase;
        }
        this.zzbrE = true;
        return null;
    }

    public /* bridge */ /* synthetic */ void zzJd() {
        super.zzJd();
    }

    public /* bridge */ /* synthetic */ void zzJe() {
        super.zzJe();
    }

    public /* bridge */ /* synthetic */ void zzJf() {
        super.zzJf();
    }

    public /* bridge */ /* synthetic */ zzaso zzJg() {
        return super.zzJg();
    }

    public /* bridge */ /* synthetic */ zzass zzJh() {
        return super.zzJh();
    }

    public /* bridge */ /* synthetic */ zzatu zzJi() {
        return super.zzJi();
    }

    public /* bridge */ /* synthetic */ zzatf zzJj() {
        return super.zzJj();
    }

    public /* bridge */ /* synthetic */ zzasw zzJk() {
        return super.zzJk();
    }

    public /* bridge */ /* synthetic */ zzatw zzJl() {
        return super.zzJl();
    }

    public /* bridge */ /* synthetic */ zzatv zzJm() {
        return super.zzJm();
    }

    public /* bridge */ /* synthetic */ zzatg zzJn() {
        return super.zzJn();
    }

    public /* bridge */ /* synthetic */ zzasu zzJo() {
        return super.zzJo();
    }

    public /* bridge */ /* synthetic */ zzaue zzJp() {
        return super.zzJp();
    }

    public /* bridge */ /* synthetic */ zzatn zzJq() {
        return super.zzJq();
    }

    public /* bridge */ /* synthetic */ zzaty zzJr() {
        return super.zzJr();
    }

    public /* bridge */ /* synthetic */ zzato zzJs() {
        return super.zzJs();
    }

    public /* bridge */ /* synthetic */ zzati zzJt() {
        return super.zzJt();
    }

    public /* bridge */ /* synthetic */ zzatl zzJu() {
        return super.zzJu();
    }

    public /* bridge */ /* synthetic */ zzast zzJv() {
        return super.zzJv();
    }

    boolean zzKP() {
        return getContext().getDatabasePath(zznV()).exists();
    }

    public boolean zza(zzatb com_google_android_gms_internal_zzatb) {
        if (VERSION.SDK_INT < 11) {
            return false;
        }
        Parcel obtain = Parcel.obtain();
        com_google_android_gms_internal_zzatb.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        if (marshall.length <= 131072) {
            return zza(0, marshall);
        }
        zzJt().zzLc().log("Event is too long for local database. Sending event directly to service");
        return false;
    }

    public boolean zza(zzaub com_google_android_gms_internal_zzaub) {
        if (VERSION.SDK_INT < 11) {
            return false;
        }
        Parcel obtain = Parcel.obtain();
        com_google_android_gms_internal_zzaub.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        if (marshall.length <= 131072) {
            return zza(1, marshall);
        }
        zzJt().zzLc().log("User property too long for local database. Sending directly to service");
        return false;
    }

    @TargetApi(11)
    public List<com.google.android.gms.common.internal.safeparcel.zza> zzls(int i) {
        Object obj;
        Throwable th;
        Object obj2;
        int i2;
        zzmq();
        zzJe();
        if (VERSION.SDK_INT < 11) {
            return null;
        }
        if (this.zzbrE) {
            return null;
        }
        List<com.google.android.gms.common.internal.safeparcel.zza> arrayList = new ArrayList();
        if (!zzKP()) {
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
                        this.zzbrE = true;
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
                                obj2 = (zzatb) zzatb.CREATOR.createFromParcel(obtain);
                                if (obj2 != null) {
                                    arrayList.add(obj2);
                                }
                            } catch (com.google.android.gms.common.internal.safeparcel.zzb.zza e3) {
                                obj2 = zzJt().zzLa();
                                obj2.log("Failed to load event from local database");
                                j = j2;
                            } finally {
                                obtain.recycle();
                            }
                        } else if (i5 == 1) {
                            Parcel obtain2 = Parcel.obtain();
                            try {
                                obtain2.unmarshall(blob, 0, blob.length);
                                obtain2.setDataPosition(0);
                                obj2 = (zzaub) zzaub.CREATOR.createFromParcel(obtain2);
                            } catch (com.google.android.gms.common.internal.safeparcel.zzb.zza e4) {
                                obj2 = zzJt().zzLa();
                                obj2.log("Failed to load user property from local database");
                                obj2 = null;
                                if (obj2 != null) {
                                    arrayList.add(obj2);
                                }
                                j = j2;
                            } finally {
                                obtain2.recycle();
                            }
                            if (obj2 != null) {
                                arrayList.add(obj2);
                            }
                        } else {
                            zzJt().zzLa().log("Unknown record type in local database");
                        }
                        j = j2;
                    }
                    query.close();
                    if (writableDatabase.delete("messages", "rowid <= ?", new String[]{Long.toString(j)}) < arrayList.size()) {
                        zzJt().zzLa().log("Fewer entries removed from local database than expected");
                    }
                    writableDatabase.setTransactionSuccessful();
                    writableDatabase.endTransaction();
                    if (writableDatabase != null) {
                        writableDatabase.close();
                    }
                    return arrayList;
                }
            } catch (SQLiteFullException e5) {
                obj = e5;
                try {
                    zzJt().zzLa().zzj("Error reading entries from local database", obj);
                    this.zzbrE = true;
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
            } catch (SQLiteException e6) {
                obj = e6;
                if (VERSION.SDK_INT < 11 || !(obj instanceof SQLiteDatabaseLockedException)) {
                    if (sQLiteDatabase != null) {
                        if (sQLiteDatabase.inTransaction()) {
                            sQLiteDatabase.endTransaction();
                        }
                    }
                    zzJt().zzLa().zzj("Error reading entries from local database", obj);
                    this.zzbrE = true;
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
        zzJt().zzLc().log("Failed to read events from database in reasonable time");
        return null;
        if (sQLiteDatabase != null) {
            sQLiteDatabase.close();
        }
        throw th;
    }

    public /* bridge */ /* synthetic */ void zzmq() {
        super.zzmq();
    }

    protected void zzmr() {
    }

    String zznV() {
        return zzJv().zzKj();
    }

    public /* bridge */ /* synthetic */ zze zznq() {
        return super.zznq();
    }
}
