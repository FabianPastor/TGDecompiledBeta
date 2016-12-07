package com.google.android.gms.measurement.internal;

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
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.util.zze;
import java.util.ArrayList;
import java.util.List;

public class zzo extends zzaa {
    private final zza asx = new zza(this, getContext(), zzade());
    private boolean asy;

    @TargetApi(11)
    private class zza extends SQLiteOpenHelper {
        final /* synthetic */ zzo asz;

        zza(zzo com_google_android_gms_measurement_internal_zzo, Context context, String str) {
            this.asz = com_google_android_gms_measurement_internal_zzo;
            super(context, str, null, 1);
        }

        @WorkerThread
        public SQLiteDatabase getWritableDatabase() {
            try {
                return super.getWritableDatabase();
            } catch (SQLiteException e) {
                if (VERSION.SDK_INT < 11 || !(e instanceof SQLiteDatabaseLockedException)) {
                    this.asz.zzbwb().zzbwy().log("Opening the local database failed, dropping and recreating it");
                    String zzade = this.asz.zzade();
                    if (!this.asz.getContext().getDatabasePath(zzade).delete()) {
                        this.asz.zzbwb().zzbwy().zzj("Failed to delete corrupted local db file", zzade);
                    }
                    try {
                        return super.getWritableDatabase();
                    } catch (SQLiteException e2) {
                        this.asz.zzbwb().zzbwy().zzj("Failed to open local database. Events will bypass local storage", e2);
                        return null;
                    }
                }
                throw e2;
            }
        }

        @WorkerThread
        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            zze.zza(this.asz.zzbwb(), sQLiteDatabase);
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
            zze.zza(this.asz.zzbwb(), sQLiteDatabase, "messages", "create table if not exists messages ( type INTEGER NOT NULL, entry BLOB NOT NULL)", "type,entry", null);
        }

        @WorkerThread
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        }
    }

    zzo(zzx com_google_android_gms_measurement_internal_zzx) {
        super(com_google_android_gms_measurement_internal_zzx);
    }

    @WorkerThread
    @TargetApi(11)
    private boolean zza(int i, byte[] bArr) {
        zzaby();
        zzzx();
        if (this.asy) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", Integer.valueOf(i));
        contentValues.put("entry", bArr);
        zzbwd().zzbvb();
        int i2 = 0;
        int i3 = 5;
        while (i2 < 5) {
            SQLiteDatabase sQLiteDatabase = null;
            try {
                sQLiteDatabase = getWritableDatabase();
                if (sQLiteDatabase == null) {
                    this.asy = true;
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
                    zzbwb().zzbwy().log("Data loss, local db full");
                    j = (100000 - j) + 1;
                    long delete = (long) sQLiteDatabase.delete("messages", "rowid in (select rowid from messages order by rowid asc limit ?)", new String[]{Long.toString(j)});
                    if (delete != j) {
                        zzbwb().zzbwy().zzd("Different delete count than expected in local db. expected, received, difference", Long.valueOf(j), Long.valueOf(delete), Long.valueOf(j - delete));
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
                zzbwb().zzbwy().zzj("Error writing entry to local database", e);
                this.asy = true;
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
                    zzbwb().zzbwy().zzj("Error writing entry to local database", e2);
                    this.asy = true;
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
        zzbwb().zzbxa().log("Failed to write entry to local database");
        return false;
    }

    public /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    @WorkerThread
    SQLiteDatabase getWritableDatabase() {
        if (this.asy) {
            return null;
        }
        SQLiteDatabase writableDatabase = this.asx.getWritableDatabase();
        if (writableDatabase != null) {
            return writableDatabase;
        }
        this.asy = true;
        return null;
    }

    public boolean zza(EventParcel eventParcel) {
        Parcel obtain = Parcel.obtain();
        eventParcel.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        if (marshall.length <= 131072) {
            return zza(0, marshall);
        }
        zzbwb().zzbxa().log("Event is too long for local database. Sending event directly to service");
        return false;
    }

    public boolean zza(UserAttributeParcel userAttributeParcel) {
        Parcel obtain = Parcel.obtain();
        userAttributeParcel.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        if (marshall.length <= 131072) {
            return zza(1, marshall);
        }
        zzbwb().zzbxa().log("User property too long for local database. Sending directly to service");
        return false;
    }

    public /* bridge */ /* synthetic */ void zzaby() {
        super.zzaby();
    }

    public /* bridge */ /* synthetic */ zze zzabz() {
        return super.zzabz();
    }

    String zzade() {
        return zzbwd().zzbus();
    }

    public /* bridge */ /* synthetic */ void zzbvo() {
        super.zzbvo();
    }

    public /* bridge */ /* synthetic */ zzc zzbvp() {
        return super.zzbvp();
    }

    public /* bridge */ /* synthetic */ zzac zzbvq() {
        return super.zzbvq();
    }

    public /* bridge */ /* synthetic */ zzn zzbvr() {
        return super.zzbvr();
    }

    public /* bridge */ /* synthetic */ zzg zzbvs() {
        return super.zzbvs();
    }

    public /* bridge */ /* synthetic */ zzae zzbvt() {
        return super.zzbvt();
    }

    public /* bridge */ /* synthetic */ zzad zzbvu() {
        return super.zzbvu();
    }

    public /* bridge */ /* synthetic */ zzo zzbvv() {
        return super.zzbvv();
    }

    public /* bridge */ /* synthetic */ zze zzbvw() {
        return super.zzbvw();
    }

    public /* bridge */ /* synthetic */ zzal zzbvx() {
        return super.zzbvx();
    }

    public /* bridge */ /* synthetic */ zzv zzbvy() {
        return super.zzbvy();
    }

    public /* bridge */ /* synthetic */ zzag zzbvz() {
        return super.zzbvz();
    }

    public /* bridge */ /* synthetic */ zzw zzbwa() {
        return super.zzbwa();
    }

    public /* bridge */ /* synthetic */ zzq zzbwb() {
        return super.zzbwb();
    }

    public /* bridge */ /* synthetic */ zzt zzbwc() {
        return super.zzbwc();
    }

    public /* bridge */ /* synthetic */ zzd zzbwd() {
        return super.zzbwd();
    }

    boolean zzbwn() {
        return getContext().getDatabasePath(zzade()).exists();
    }

    @TargetApi(11)
    public List<AbstractSafeParcelable> zzxe(int i) {
        Object obj;
        Throwable th;
        int i2;
        zzzx();
        zzaby();
        if (this.asy) {
            return null;
        }
        List<AbstractSafeParcelable> arrayList = new ArrayList();
        if (!zzbwn()) {
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
                        this.asy = true;
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
                        Object obj2;
                        if (i5 == 0) {
                            Parcel obtain = Parcel.obtain();
                            try {
                                obtain.unmarshall(blob, 0, blob.length);
                                obtain.setDataPosition(0);
                                obj2 = (EventParcel) EventParcel.CREATOR.createFromParcel(obtain);
                                if (obj2 != null) {
                                    arrayList.add(obj2);
                                }
                            } catch (com.google.android.gms.common.internal.safeparcel.zza.zza e3) {
                                obj2 = zzbwb().zzbwy();
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
                                obj2 = (UserAttributeParcel) UserAttributeParcel.CREATOR.createFromParcel(obtain2);
                            } catch (com.google.android.gms.common.internal.safeparcel.zza.zza e4) {
                                obj2 = zzbwb().zzbwy();
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
                            zzbwb().zzbwy().log("Unknown record type in local database");
                        }
                        j = j2;
                    }
                    query.close();
                    if (writableDatabase.delete("messages", "rowid <= ?", new String[]{Long.toString(j)}) < arrayList.size()) {
                        zzbwb().zzbwy().log("Fewer entries removed from local database than expected");
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
                    zzbwb().zzbwy().zzj("Error reading entries from local database", obj);
                    this.asy = true;
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
                    zzbwb().zzbwy().zzj("Error reading entries from local database", obj);
                    this.asy = true;
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
        zzbwb().zzbxa().log("Failed to read events from database in reasonable time");
        return null;
        if (sQLiteDatabase != null) {
            sQLiteDatabase.close();
        }
        throw th;
    }

    public /* bridge */ /* synthetic */ void zzzx() {
        super.zzzx();
    }

    protected void zzzy() {
    }
}
