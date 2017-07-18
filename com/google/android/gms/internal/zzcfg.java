package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.util.zze;

public final class zzcfg extends zzchi {
    private final zzcfh zzbqF = new zzcfh(this, super.getContext(), zzcel.zzxD());
    private boolean zzbqG;

    zzcfg(zzcgk com_google_android_gms_internal_zzcgk) {
        super(com_google_android_gms_internal_zzcgk);
    }

    @WorkerThread
    private final SQLiteDatabase getWritableDatabase() {
        if (this.zzbqG) {
            return null;
        }
        SQLiteDatabase writableDatabase = this.zzbqF.getWritableDatabase();
        if (writableDatabase != null) {
            return writableDatabase;
        }
        this.zzbqG = true;
        return null;
    }

    @WorkerThread
    @TargetApi(11)
    private final boolean zza(int i, byte[] bArr) {
        super.zzwp();
        super.zzjC();
        if (this.zzbqG) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", Integer.valueOf(i));
        contentValues.put("entry", bArr);
        zzcel.zzxN();
        int i2 = 0;
        int i3 = 5;
        while (i2 < 5) {
            SQLiteDatabase sQLiteDatabase = null;
            Cursor cursor = null;
            try {
                sQLiteDatabase = getWritableDatabase();
                if (sQLiteDatabase == null) {
                    this.zzbqG = true;
                    if (sQLiteDatabase != null) {
                        sQLiteDatabase.close();
                    }
                    return false;
                }
                sQLiteDatabase.beginTransaction();
                long j = 0;
                cursor = sQLiteDatabase.rawQuery("select count(1) from messages", null);
                if (cursor != null && cursor.moveToFirst()) {
                    j = cursor.getLong(0);
                }
                if (j >= 100000) {
                    super.zzwF().zzyx().log("Data loss, local db full");
                    j = (100000 - j) + 1;
                    long delete = (long) sQLiteDatabase.delete("messages", "rowid in (select rowid from messages order by rowid asc limit ?)", new String[]{Long.toString(j)});
                    if (delete != j) {
                        super.zzwF().zzyx().zzd("Different delete count than expected in local db. expected, received, difference", Long.valueOf(j), Long.valueOf(delete), Long.valueOf(j - delete));
                    }
                }
                sQLiteDatabase.insertOrThrow("messages", null, contentValues);
                sQLiteDatabase.setTransactionSuccessful();
                sQLiteDatabase.endTransaction();
                if (cursor != null) {
                    cursor.close();
                }
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
                return true;
            } catch (SQLiteFullException e) {
                super.zzwF().zzyx().zzj("Error writing entry to local database", e);
                this.zzbqG = true;
                if (cursor != null) {
                    cursor.close();
                }
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
                    super.zzwF().zzyx().zzj("Error writing entry to local database", e2);
                    this.zzbqG = true;
                } else {
                    SystemClock.sleep((long) i3);
                    i3 += 20;
                }
                if (cursor != null) {
                    cursor.close();
                }
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
                i2++;
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
                if (sQLiteDatabase != null) {
                    sQLiteDatabase.close();
                }
            }
        }
        super.zzwF().zzyz().log("Failed to write entry to local database");
        return false;
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final boolean zza(zzcey com_google_android_gms_internal_zzcey) {
        Parcel obtain = Parcel.obtain();
        com_google_android_gms_internal_zzcey.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        if (marshall.length <= 131072) {
            return zza(0, marshall);
        }
        super.zzwF().zzyz().log("Event is too long for local database. Sending event directly to service");
        return false;
    }

    public final boolean zza(zzcjh com_google_android_gms_internal_zzcjh) {
        Parcel obtain = Parcel.obtain();
        com_google_android_gms_internal_zzcjh.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        if (marshall.length <= 131072) {
            return zza(1, marshall);
        }
        super.zzwF().zzyz().log("User property too long for local database. Sending directly to service");
        return false;
    }

    @android.annotation.TargetApi(11)
    public final java.util.List<com.google.android.gms.common.internal.safeparcel.zza> zzbp(int r15) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:151:0x00fe
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.modifyBlocksTree(BlockProcessor.java:248)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.rerun(BlockProcessor.java:44)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:57)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r14 = this;
        super.zzjC();
        super.zzwp();
        r0 = r14.zzbqG;
        if (r0 == 0) goto L_0x000c;
    L_0x000a:
        r0 = 0;
    L_0x000b:
        return r0;
    L_0x000c:
        r10 = new java.util.ArrayList;
        r10.<init>();
        r0 = super.getContext();
        r1 = com.google.android.gms.internal.zzcel.zzxD();
        r0 = r0.getDatabasePath(r1);
        r0 = r0.exists();
        if (r0 != 0) goto L_0x0025;
    L_0x0023:
        r0 = r10;
        goto L_0x000b;
    L_0x0025:
        r9 = 5;
        r0 = 0;
        r12 = r0;
    L_0x0028:
        r0 = 5;
        if (r12 >= r0) goto L_0x01ef;
    L_0x002b:
        r1 = 0;
        r11 = 0;
        r0 = r14.getWritableDatabase();	 Catch:{ SQLiteFullException -> 0x021f, SQLiteException -> 0x0214, all -> 0x0200 }
        if (r0 != 0) goto L_0x003d;
    L_0x0033:
        r1 = 1;
        r14.zzbqG = r1;	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        if (r0 == 0) goto L_0x003b;
    L_0x0038:
        r0.close();
    L_0x003b:
        r0 = 0;
        goto L_0x000b;
    L_0x003d:
        r0.beginTransaction();	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r1 = "messages";	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r2 = 3;	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r2 = new java.lang.String[r2];	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r3 = 0;	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r4 = "rowid";	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r2[r3] = r4;	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r3 = 1;	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r4 = "type";	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r2[r3] = r4;	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r3 = 2;	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r4 = "entry";	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r2[r3] = r4;	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r3 = 0;	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r4 = 0;	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r5 = 0;	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r6 = 0;	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r7 = "rowid asc";	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r8 = 100;	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r8 = java.lang.Integer.toString(r8);	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r2 = r0.query(r1, r2, r3, r4, r5, r6, r7, r8);	 Catch:{ SQLiteFullException -> 0x0224, SQLiteException -> 0x0218, all -> 0x0204 }
        r4 = -1;
    L_0x006b:
        r1 = r2.moveToNext();	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        if (r1 == 0) goto L_0x0195;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
    L_0x0071:
        r1 = 0;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r4 = r2.getLong(r1);	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r1 = 1;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r1 = r2.getInt(r1);	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r3 = 2;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r6 = r2.getBlob(r3);	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        if (r1 != 0) goto L_0x010d;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
    L_0x0082:
        r3 = android.os.Parcel.obtain();	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r1 = 0;
        r7 = r6.length;	 Catch:{ zzc -> 0x00c7, all -> 0x00f9 }
        r3.unmarshall(r6, r1, r7);	 Catch:{ zzc -> 0x00c7, all -> 0x00f9 }
        r1 = 0;	 Catch:{ zzc -> 0x00c7, all -> 0x00f9 }
        r3.setDataPosition(r1);	 Catch:{ zzc -> 0x00c7, all -> 0x00f9 }
        r1 = com.google.android.gms.internal.zzcey.CREATOR;	 Catch:{ zzc -> 0x00c7, all -> 0x00f9 }
        r1 = r1.createFromParcel(r3);	 Catch:{ zzc -> 0x00c7, all -> 0x00f9 }
        r1 = (com.google.android.gms.internal.zzcey) r1;	 Catch:{ zzc -> 0x00c7, all -> 0x00f9 }
        r3.recycle();
        if (r1 == 0) goto L_0x006b;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
    L_0x009c:
        r10.add(r1);	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        goto L_0x006b;
    L_0x00a0:
        r1 = move-exception;
        r13 = r1;
        r1 = r2;
        r2 = r0;
        r0 = r13;
    L_0x00a5:
        r3 = super.zzwF();	 Catch:{ all -> 0x020b }
        r3 = r3.zzyx();	 Catch:{ all -> 0x020b }
        r4 = "Error reading entries from local database";	 Catch:{ all -> 0x020b }
        r3.zzj(r4, r0);	 Catch:{ all -> 0x020b }
        r0 = 1;	 Catch:{ all -> 0x020b }
        r14.zzbqG = r0;	 Catch:{ all -> 0x020b }
        if (r1 == 0) goto L_0x00bb;
    L_0x00b8:
        r1.close();
    L_0x00bb:
        if (r2 == 0) goto L_0x022a;
    L_0x00bd:
        r2.close();
        r0 = r9;
    L_0x00c1:
        r1 = r12 + 1;
        r12 = r1;
        r9 = r0;
        goto L_0x0028;
    L_0x00c7:
        r1 = move-exception;
        r1 = super.zzwF();	 Catch:{ zzc -> 0x00c7, all -> 0x00f9 }
        r1 = r1.zzyx();	 Catch:{ zzc -> 0x00c7, all -> 0x00f9 }
        r6 = "Failed to load event from local database";	 Catch:{ zzc -> 0x00c7, all -> 0x00f9 }
        r1.log(r6);	 Catch:{ zzc -> 0x00c7, all -> 0x00f9 }
        r3.recycle();
        goto L_0x006b;
    L_0x00da:
        r1 = move-exception;
        r13 = r1;
        r1 = r0;
        r0 = r13;
    L_0x00de:
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x0211 }
        r4 = 11;	 Catch:{ all -> 0x0211 }
        if (r3 < r4) goto L_0x01d0;	 Catch:{ all -> 0x0211 }
    L_0x00e4:
        r3 = r0 instanceof android.database.sqlite.SQLiteDatabaseLockedException;	 Catch:{ all -> 0x0211 }
        if (r3 == 0) goto L_0x01d0;	 Catch:{ all -> 0x0211 }
    L_0x00e8:
        r4 = (long) r9;	 Catch:{ all -> 0x0211 }
        android.os.SystemClock.sleep(r4);	 Catch:{ all -> 0x0211 }
        r0 = r9 + 20;
    L_0x00ee:
        if (r2 == 0) goto L_0x00f3;
    L_0x00f0:
        r2.close();
    L_0x00f3:
        if (r1 == 0) goto L_0x00c1;
    L_0x00f5:
        r1.close();
        goto L_0x00c1;
    L_0x00f9:
        r1 = move-exception;
        r3.recycle();	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        throw r1;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
    L_0x00fe:
        r1 = move-exception;
        r13 = r1;
        r1 = r0;
        r0 = r13;
    L_0x0102:
        if (r2 == 0) goto L_0x0107;
    L_0x0104:
        r2.close();
    L_0x0107:
        if (r1 == 0) goto L_0x010c;
    L_0x0109:
        r1.close();
    L_0x010c:
        throw r0;
    L_0x010d:
        r3 = 1;
        if (r1 != r3) goto L_0x0149;
    L_0x0110:
        r7 = android.os.Parcel.obtain();	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r3 = 0;
        r1 = 0;
        r8 = r6.length;	 Catch:{ zzc -> 0x0130, all -> 0x0144 }
        r7.unmarshall(r6, r1, r8);	 Catch:{ zzc -> 0x0130, all -> 0x0144 }
        r1 = 0;	 Catch:{ zzc -> 0x0130, all -> 0x0144 }
        r7.setDataPosition(r1);	 Catch:{ zzc -> 0x0130, all -> 0x0144 }
        r1 = com.google.android.gms.internal.zzcjh.CREATOR;	 Catch:{ zzc -> 0x0130, all -> 0x0144 }
        r1 = r1.createFromParcel(r7);	 Catch:{ zzc -> 0x0130, all -> 0x0144 }
        r1 = (com.google.android.gms.internal.zzcjh) r1;	 Catch:{ zzc -> 0x0130, all -> 0x0144 }
        r7.recycle();
    L_0x0129:
        if (r1 == 0) goto L_0x006b;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
    L_0x012b:
        r10.add(r1);	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        goto L_0x006b;
    L_0x0130:
        r1 = move-exception;
        r1 = super.zzwF();	 Catch:{ zzc -> 0x0130, all -> 0x0144 }
        r1 = r1.zzyx();	 Catch:{ zzc -> 0x0130, all -> 0x0144 }
        r6 = "Failed to load user property from local database";	 Catch:{ zzc -> 0x0130, all -> 0x0144 }
        r1.log(r6);	 Catch:{ zzc -> 0x0130, all -> 0x0144 }
        r7.recycle();
        r1 = r3;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        goto L_0x0129;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
    L_0x0144:
        r1 = move-exception;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r7.recycle();	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        throw r1;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
    L_0x0149:
        r3 = 2;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        if (r1 != r3) goto L_0x0185;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
    L_0x014c:
        r7 = android.os.Parcel.obtain();	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r3 = 0;
        r1 = 0;
        r8 = r6.length;	 Catch:{ zzc -> 0x016c, all -> 0x0180 }
        r7.unmarshall(r6, r1, r8);	 Catch:{ zzc -> 0x016c, all -> 0x0180 }
        r1 = 0;	 Catch:{ zzc -> 0x016c, all -> 0x0180 }
        r7.setDataPosition(r1);	 Catch:{ zzc -> 0x016c, all -> 0x0180 }
        r1 = com.google.android.gms.internal.zzcej.CREATOR;	 Catch:{ zzc -> 0x016c, all -> 0x0180 }
        r1 = r1.createFromParcel(r7);	 Catch:{ zzc -> 0x016c, all -> 0x0180 }
        r1 = (com.google.android.gms.internal.zzcej) r1;	 Catch:{ zzc -> 0x016c, all -> 0x0180 }
        r7.recycle();
    L_0x0165:
        if (r1 == 0) goto L_0x006b;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
    L_0x0167:
        r10.add(r1);	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        goto L_0x006b;
    L_0x016c:
        r1 = move-exception;
        r1 = super.zzwF();	 Catch:{ zzc -> 0x016c, all -> 0x0180 }
        r1 = r1.zzyx();	 Catch:{ zzc -> 0x016c, all -> 0x0180 }
        r6 = "Failed to load user property from local database";	 Catch:{ zzc -> 0x016c, all -> 0x0180 }
        r1.log(r6);	 Catch:{ zzc -> 0x016c, all -> 0x0180 }
        r7.recycle();
        r1 = r3;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        goto L_0x0165;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
    L_0x0180:
        r1 = move-exception;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r7.recycle();	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        throw r1;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
    L_0x0185:
        r1 = super.zzwF();	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r1 = r1.zzyx();	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r3 = "Unknown record type in local database";	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r1.log(r3);	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        goto L_0x006b;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
    L_0x0195:
        r1 = "messages";	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r3 = "rowid <= ?";	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r6 = 1;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r6 = new java.lang.String[r6];	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r7 = 0;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r4 = java.lang.Long.toString(r4);	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r6[r7] = r4;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r1 = r0.delete(r1, r3, r6);	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r3 = r10.size();	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        if (r1 >= r3) goto L_0x01bd;	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
    L_0x01af:
        r1 = super.zzwF();	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r1 = r1.zzyx();	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r3 = "Fewer entries removed from local database than expected";	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r1.log(r3);	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
    L_0x01bd:
        r0.setTransactionSuccessful();	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        r0.endTransaction();	 Catch:{ SQLiteFullException -> 0x00a0, SQLiteException -> 0x00da, all -> 0x00fe }
        if (r2 == 0) goto L_0x01c8;
    L_0x01c5:
        r2.close();
    L_0x01c8:
        if (r0 == 0) goto L_0x01cd;
    L_0x01ca:
        r0.close();
    L_0x01cd:
        r0 = r10;
        goto L_0x000b;
    L_0x01d0:
        if (r1 == 0) goto L_0x01db;
    L_0x01d2:
        r3 = r1.inTransaction();	 Catch:{ all -> 0x0211 }
        if (r3 == 0) goto L_0x01db;	 Catch:{ all -> 0x0211 }
    L_0x01d8:
        r1.endTransaction();	 Catch:{ all -> 0x0211 }
    L_0x01db:
        r3 = super.zzwF();	 Catch:{ all -> 0x0211 }
        r3 = r3.zzyx();	 Catch:{ all -> 0x0211 }
        r4 = "Error reading entries from local database";	 Catch:{ all -> 0x0211 }
        r3.zzj(r4, r0);	 Catch:{ all -> 0x0211 }
        r0 = 1;	 Catch:{ all -> 0x0211 }
        r14.zzbqG = r0;	 Catch:{ all -> 0x0211 }
        r0 = r9;
        goto L_0x00ee;
    L_0x01ef:
        r0 = super.zzwF();
        r0 = r0.zzyz();
        r1 = "Failed to read events from database in reasonable time";
        r0.log(r1);
        r0 = 0;
        goto L_0x000b;
    L_0x0200:
        r0 = move-exception;
        r2 = r11;
        goto L_0x0102;
    L_0x0204:
        r1 = move-exception;
        r2 = r11;
        r13 = r1;
        r1 = r0;
        r0 = r13;
        goto L_0x0102;
    L_0x020b:
        r0 = move-exception;
        r13 = r1;
        r1 = r2;
        r2 = r13;
        goto L_0x0102;
    L_0x0211:
        r0 = move-exception;
        goto L_0x0102;
    L_0x0214:
        r0 = move-exception;
        r2 = r11;
        goto L_0x00de;
    L_0x0218:
        r1 = move-exception;
        r2 = r11;
        r13 = r1;
        r1 = r0;
        r0 = r13;
        goto L_0x00de;
    L_0x021f:
        r0 = move-exception;
        r2 = r1;
        r1 = r11;
        goto L_0x00a5;
    L_0x0224:
        r1 = move-exception;
        r2 = r0;
        r0 = r1;
        r1 = r11;
        goto L_0x00a5;
    L_0x022a:
        r0 = r9;
        goto L_0x00c1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzcfg.zzbp(int):java.util.List<com.google.android.gms.common.internal.safeparcel.zza>");
    }

    public final boolean zzc(zzcej com_google_android_gms_internal_zzcej) {
        super.zzwB();
        byte[] zza = zzcjk.zza((Parcelable) com_google_android_gms_internal_zzcej);
        if (zza.length <= 131072) {
            return zza(2, zza);
        }
        super.zzwF().zzyz().log("Conditional user property too long for local database. Sending directly to service");
        return false;
    }

    public final /* bridge */ /* synthetic */ void zzjC() {
        super.zzjC();
    }

    protected final void zzjD() {
    }

    public final /* bridge */ /* synthetic */ zze zzkq() {
        return super.zzkq();
    }

    public final /* bridge */ /* synthetic */ zzcfi zzwA() {
        return super.zzwA();
    }

    public final /* bridge */ /* synthetic */ zzcjk zzwB() {
        return super.zzwB();
    }

    public final /* bridge */ /* synthetic */ zzcge zzwC() {
        return super.zzwC();
    }

    public final /* bridge */ /* synthetic */ zzciz zzwD() {
        return super.zzwD();
    }

    public final /* bridge */ /* synthetic */ zzcgf zzwE() {
        return super.zzwE();
    }

    public final /* bridge */ /* synthetic */ zzcfk zzwF() {
        return super.zzwF();
    }

    public final /* bridge */ /* synthetic */ zzcfv zzwG() {
        return super.zzwG();
    }

    public final /* bridge */ /* synthetic */ zzcel zzwH() {
        return super.zzwH();
    }

    public final /* bridge */ /* synthetic */ void zzwo() {
        super.zzwo();
    }

    public final /* bridge */ /* synthetic */ void zzwp() {
        super.zzwp();
    }

    public final /* bridge */ /* synthetic */ void zzwq() {
        super.zzwq();
    }

    public final /* bridge */ /* synthetic */ zzceb zzwr() {
        return super.zzwr();
    }

    public final /* bridge */ /* synthetic */ zzcei zzws() {
        return super.zzws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzwt() {
        return super.zzwt();
    }

    public final /* bridge */ /* synthetic */ zzcff zzwu() {
        return super.zzwu();
    }

    public final /* bridge */ /* synthetic */ zzces zzwv() {
        return super.zzwv();
    }

    public final /* bridge */ /* synthetic */ zzcic zzww() {
        return super.zzww();
    }

    public final /* bridge */ /* synthetic */ zzchy zzwx() {
        return super.zzwx();
    }

    public final /* bridge */ /* synthetic */ zzcfg zzwy() {
        return super.zzwy();
    }

    public final /* bridge */ /* synthetic */ zzcem zzwz() {
        return super.zzwz();
    }
}
