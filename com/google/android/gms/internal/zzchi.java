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
import com.google.android.gms.common.util.zzd;

public final class zzchi extends zzcjl {
    private final zzchj zzjbn = new zzchj(this, getContext(), "google_app_measurement_local.db");
    private boolean zzjbo;

    zzchi(zzcim com_google_android_gms_internal_zzcim) {
        super(com_google_android_gms_internal_zzcim);
    }

    private final SQLiteDatabase getWritableDatabase() {
        if (this.zzjbo) {
            return null;
        }
        SQLiteDatabase writableDatabase = this.zzjbn.getWritableDatabase();
        if (writableDatabase != null) {
            return writableDatabase;
        }
        this.zzjbo = true;
        return null;
    }

    @TargetApi(11)
    private final boolean zzb(int i, byte[] bArr) {
        zzve();
        if (this.zzjbo) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("type", Integer.valueOf(i));
        contentValues.put("entry", bArr);
        int i2 = 0;
        int i3 = 5;
        while (i2 < 5) {
            SQLiteDatabase sQLiteDatabase = null;
            Cursor cursor = null;
            try {
                sQLiteDatabase = getWritableDatabase();
                if (sQLiteDatabase == null) {
                    this.zzjbo = true;
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
                    zzawy().zzazd().log("Data loss, local db full");
                    j = (100000 - j) + 1;
                    long delete = (long) sQLiteDatabase.delete("messages", "rowid in (select rowid from messages order by rowid asc limit ?)", new String[]{Long.toString(j)});
                    if (delete != j) {
                        zzawy().zzazd().zzd("Different delete count than expected in local db. expected, received, difference", Long.valueOf(j), Long.valueOf(delete), Long.valueOf(j - delete));
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
                zzawy().zzazd().zzj("Error writing entry to local database", e);
                this.zzjbo = true;
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
                    zzawy().zzazd().zzj("Error writing entry to local database", e2);
                    this.zzjbo = true;
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
        zzawy().zzazf().log("Failed to write entry to local database");
        return false;
    }

    public final /* bridge */ /* synthetic */ Context getContext() {
        return super.getContext();
    }

    public final void resetAnalyticsData() {
        zzve();
        try {
            int delete = getWritableDatabase().delete("messages", null, null) + 0;
            if (delete > 0) {
                zzawy().zzazj().zzj("Reset local analytics data. records", Integer.valueOf(delete));
            }
        } catch (SQLiteException e) {
            zzawy().zzazd().zzj("Error resetting local analytics data. error", e);
        }
    }

    public final boolean zza(zzcha com_google_android_gms_internal_zzcha) {
        Parcel obtain = Parcel.obtain();
        com_google_android_gms_internal_zzcha.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        if (marshall.length <= 131072) {
            return zzb(0, marshall);
        }
        zzawy().zzazf().log("Event is too long for local database. Sending event directly to service");
        return false;
    }

    public final boolean zza(zzcln com_google_android_gms_internal_zzcln) {
        Parcel obtain = Parcel.obtain();
        com_google_android_gms_internal_zzcln.writeToParcel(obtain, 0);
        byte[] marshall = obtain.marshall();
        obtain.recycle();
        if (marshall.length <= 131072) {
            return zzb(1, marshall);
        }
        zzawy().zzazf().log("User property too long for local database. Sending directly to service");
        return false;
    }

    public final /* bridge */ /* synthetic */ void zzawi() {
        super.zzawi();
    }

    public final /* bridge */ /* synthetic */ void zzawj() {
        super.zzawj();
    }

    public final /* bridge */ /* synthetic */ zzcgd zzawk() {
        return super.zzawk();
    }

    public final /* bridge */ /* synthetic */ zzcgk zzawl() {
        return super.zzawl();
    }

    public final /* bridge */ /* synthetic */ zzcjn zzawm() {
        return super.zzawm();
    }

    public final /* bridge */ /* synthetic */ zzchh zzawn() {
        return super.zzawn();
    }

    public final /* bridge */ /* synthetic */ zzcgu zzawo() {
        return super.zzawo();
    }

    public final /* bridge */ /* synthetic */ zzckg zzawp() {
        return super.zzawp();
    }

    public final /* bridge */ /* synthetic */ zzckc zzawq() {
        return super.zzawq();
    }

    public final /* bridge */ /* synthetic */ zzchi zzawr() {
        return super.zzawr();
    }

    public final /* bridge */ /* synthetic */ zzcgo zzaws() {
        return super.zzaws();
    }

    public final /* bridge */ /* synthetic */ zzchk zzawt() {
        return super.zzawt();
    }

    public final /* bridge */ /* synthetic */ zzclq zzawu() {
        return super.zzawu();
    }

    public final /* bridge */ /* synthetic */ zzcig zzawv() {
        return super.zzawv();
    }

    public final /* bridge */ /* synthetic */ zzclf zzaww() {
        return super.zzaww();
    }

    public final /* bridge */ /* synthetic */ zzcih zzawx() {
        return super.zzawx();
    }

    public final /* bridge */ /* synthetic */ zzchm zzawy() {
        return super.zzawy();
    }

    public final /* bridge */ /* synthetic */ zzchx zzawz() {
        return super.zzawz();
    }

    public final /* bridge */ /* synthetic */ zzcgn zzaxa() {
        return super.zzaxa();
    }

    protected final boolean zzaxz() {
        return false;
    }

    public final boolean zzc(zzcgl com_google_android_gms_internal_zzcgl) {
        zzawu();
        byte[] zza = zzclq.zza((Parcelable) com_google_android_gms_internal_zzcgl);
        if (zza.length <= 131072) {
            return zzb(2, zza);
        }
        zzawy().zzazf().log("Conditional user property too long for local database. Sending directly to service");
        return false;
    }

    @android.annotation.TargetApi(11)
    public final java.util.List<com.google.android.gms.internal.zzbfm> zzeb(int r15) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:149:0x00fa
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
        r14.zzve();
        r0 = r14.zzjbo;
        if (r0 == 0) goto L_0x0009;
    L_0x0007:
        r0 = 0;
    L_0x0008:
        return r0;
    L_0x0009:
        r10 = new java.util.ArrayList;
        r10.<init>();
        r0 = r14.getContext();
        r1 = "google_app_measurement_local.db";
        r0 = r0.getDatabasePath(r1);
        r0 = r0.exists();
        if (r0 != 0) goto L_0x0021;
    L_0x001f:
        r0 = r10;
        goto L_0x0008;
    L_0x0021:
        r9 = 5;
        r0 = 0;
        r12 = r0;
    L_0x0024:
        r0 = 5;
        if (r12 >= r0) goto L_0x01eb;
    L_0x0027:
        r1 = 0;
        r11 = 0;
        r0 = r14.getWritableDatabase();	 Catch:{ SQLiteFullException -> 0x021b, SQLiteException -> 0x0210, all -> 0x01fc }
        if (r0 != 0) goto L_0x0039;
    L_0x002f:
        r1 = 1;
        r14.zzjbo = r1;	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        if (r0 == 0) goto L_0x0037;
    L_0x0034:
        r0.close();
    L_0x0037:
        r0 = 0;
        goto L_0x0008;
    L_0x0039:
        r0.beginTransaction();	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r1 = "messages";	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r2 = 3;	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r2 = new java.lang.String[r2];	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r3 = 0;	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r4 = "rowid";	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r2[r3] = r4;	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r3 = 1;	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r4 = "type";	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r2[r3] = r4;	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r3 = 2;	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r4 = "entry";	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r2[r3] = r4;	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r3 = 0;	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r4 = 0;	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r5 = 0;	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r6 = 0;	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r7 = "rowid asc";	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r8 = 100;	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r8 = java.lang.Integer.toString(r8);	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r2 = r0.query(r1, r2, r3, r4, r5, r6, r7, r8);	 Catch:{ SQLiteFullException -> 0x0220, SQLiteException -> 0x0214, all -> 0x0200 }
        r4 = -1;
    L_0x0067:
        r1 = r2.moveToNext();	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        if (r1 == 0) goto L_0x0191;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
    L_0x006d:
        r1 = 0;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r4 = r2.getLong(r1);	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r1 = 1;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r1 = r2.getInt(r1);	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r3 = 2;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r6 = r2.getBlob(r3);	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        if (r1 != 0) goto L_0x0109;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
    L_0x007e:
        r3 = android.os.Parcel.obtain();	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r1 = 0;
        r7 = r6.length;	 Catch:{ zzbfo -> 0x00c3, all -> 0x00f5 }
        r3.unmarshall(r6, r1, r7);	 Catch:{ zzbfo -> 0x00c3, all -> 0x00f5 }
        r1 = 0;	 Catch:{ zzbfo -> 0x00c3, all -> 0x00f5 }
        r3.setDataPosition(r1);	 Catch:{ zzbfo -> 0x00c3, all -> 0x00f5 }
        r1 = com.google.android.gms.internal.zzcha.CREATOR;	 Catch:{ zzbfo -> 0x00c3, all -> 0x00f5 }
        r1 = r1.createFromParcel(r3);	 Catch:{ zzbfo -> 0x00c3, all -> 0x00f5 }
        r1 = (com.google.android.gms.internal.zzcha) r1;	 Catch:{ zzbfo -> 0x00c3, all -> 0x00f5 }
        r3.recycle();
        if (r1 == 0) goto L_0x0067;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
    L_0x0098:
        r10.add(r1);	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        goto L_0x0067;
    L_0x009c:
        r1 = move-exception;
        r13 = r1;
        r1 = r2;
        r2 = r0;
        r0 = r13;
    L_0x00a1:
        r3 = r14.zzawy();	 Catch:{ all -> 0x0207 }
        r3 = r3.zzazd();	 Catch:{ all -> 0x0207 }
        r4 = "Error reading entries from local database";	 Catch:{ all -> 0x0207 }
        r3.zzj(r4, r0);	 Catch:{ all -> 0x0207 }
        r0 = 1;	 Catch:{ all -> 0x0207 }
        r14.zzjbo = r0;	 Catch:{ all -> 0x0207 }
        if (r1 == 0) goto L_0x00b7;
    L_0x00b4:
        r1.close();
    L_0x00b7:
        if (r2 == 0) goto L_0x0226;
    L_0x00b9:
        r2.close();
        r0 = r9;
    L_0x00bd:
        r1 = r12 + 1;
        r12 = r1;
        r9 = r0;
        goto L_0x0024;
    L_0x00c3:
        r1 = move-exception;
        r1 = r14.zzawy();	 Catch:{ zzbfo -> 0x00c3, all -> 0x00f5 }
        r1 = r1.zzazd();	 Catch:{ zzbfo -> 0x00c3, all -> 0x00f5 }
        r6 = "Failed to load event from local database";	 Catch:{ zzbfo -> 0x00c3, all -> 0x00f5 }
        r1.log(r6);	 Catch:{ zzbfo -> 0x00c3, all -> 0x00f5 }
        r3.recycle();
        goto L_0x0067;
    L_0x00d6:
        r1 = move-exception;
        r13 = r1;
        r1 = r0;
        r0 = r13;
    L_0x00da:
        r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x020d }
        r4 = 11;	 Catch:{ all -> 0x020d }
        if (r3 < r4) goto L_0x01cc;	 Catch:{ all -> 0x020d }
    L_0x00e0:
        r3 = r0 instanceof android.database.sqlite.SQLiteDatabaseLockedException;	 Catch:{ all -> 0x020d }
        if (r3 == 0) goto L_0x01cc;	 Catch:{ all -> 0x020d }
    L_0x00e4:
        r4 = (long) r9;	 Catch:{ all -> 0x020d }
        android.os.SystemClock.sleep(r4);	 Catch:{ all -> 0x020d }
        r0 = r9 + 20;
    L_0x00ea:
        if (r2 == 0) goto L_0x00ef;
    L_0x00ec:
        r2.close();
    L_0x00ef:
        if (r1 == 0) goto L_0x00bd;
    L_0x00f1:
        r1.close();
        goto L_0x00bd;
    L_0x00f5:
        r1 = move-exception;
        r3.recycle();	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        throw r1;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
    L_0x00fa:
        r1 = move-exception;
        r13 = r1;
        r1 = r0;
        r0 = r13;
    L_0x00fe:
        if (r2 == 0) goto L_0x0103;
    L_0x0100:
        r2.close();
    L_0x0103:
        if (r1 == 0) goto L_0x0108;
    L_0x0105:
        r1.close();
    L_0x0108:
        throw r0;
    L_0x0109:
        r3 = 1;
        if (r1 != r3) goto L_0x0145;
    L_0x010c:
        r7 = android.os.Parcel.obtain();	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r3 = 0;
        r1 = 0;
        r8 = r6.length;	 Catch:{ zzbfo -> 0x012c, all -> 0x0140 }
        r7.unmarshall(r6, r1, r8);	 Catch:{ zzbfo -> 0x012c, all -> 0x0140 }
        r1 = 0;	 Catch:{ zzbfo -> 0x012c, all -> 0x0140 }
        r7.setDataPosition(r1);	 Catch:{ zzbfo -> 0x012c, all -> 0x0140 }
        r1 = com.google.android.gms.internal.zzcln.CREATOR;	 Catch:{ zzbfo -> 0x012c, all -> 0x0140 }
        r1 = r1.createFromParcel(r7);	 Catch:{ zzbfo -> 0x012c, all -> 0x0140 }
        r1 = (com.google.android.gms.internal.zzcln) r1;	 Catch:{ zzbfo -> 0x012c, all -> 0x0140 }
        r7.recycle();
    L_0x0125:
        if (r1 == 0) goto L_0x0067;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
    L_0x0127:
        r10.add(r1);	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        goto L_0x0067;
    L_0x012c:
        r1 = move-exception;
        r1 = r14.zzawy();	 Catch:{ zzbfo -> 0x012c, all -> 0x0140 }
        r1 = r1.zzazd();	 Catch:{ zzbfo -> 0x012c, all -> 0x0140 }
        r6 = "Failed to load user property from local database";	 Catch:{ zzbfo -> 0x012c, all -> 0x0140 }
        r1.log(r6);	 Catch:{ zzbfo -> 0x012c, all -> 0x0140 }
        r7.recycle();
        r1 = r3;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        goto L_0x0125;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
    L_0x0140:
        r1 = move-exception;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r7.recycle();	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        throw r1;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
    L_0x0145:
        r3 = 2;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        if (r1 != r3) goto L_0x0181;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
    L_0x0148:
        r7 = android.os.Parcel.obtain();	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r3 = 0;
        r1 = 0;
        r8 = r6.length;	 Catch:{ zzbfo -> 0x0168, all -> 0x017c }
        r7.unmarshall(r6, r1, r8);	 Catch:{ zzbfo -> 0x0168, all -> 0x017c }
        r1 = 0;	 Catch:{ zzbfo -> 0x0168, all -> 0x017c }
        r7.setDataPosition(r1);	 Catch:{ zzbfo -> 0x0168, all -> 0x017c }
        r1 = com.google.android.gms.internal.zzcgl.CREATOR;	 Catch:{ zzbfo -> 0x0168, all -> 0x017c }
        r1 = r1.createFromParcel(r7);	 Catch:{ zzbfo -> 0x0168, all -> 0x017c }
        r1 = (com.google.android.gms.internal.zzcgl) r1;	 Catch:{ zzbfo -> 0x0168, all -> 0x017c }
        r7.recycle();
    L_0x0161:
        if (r1 == 0) goto L_0x0067;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
    L_0x0163:
        r10.add(r1);	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        goto L_0x0067;
    L_0x0168:
        r1 = move-exception;
        r1 = r14.zzawy();	 Catch:{ zzbfo -> 0x0168, all -> 0x017c }
        r1 = r1.zzazd();	 Catch:{ zzbfo -> 0x0168, all -> 0x017c }
        r6 = "Failed to load user property from local database";	 Catch:{ zzbfo -> 0x0168, all -> 0x017c }
        r1.log(r6);	 Catch:{ zzbfo -> 0x0168, all -> 0x017c }
        r7.recycle();
        r1 = r3;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        goto L_0x0161;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
    L_0x017c:
        r1 = move-exception;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r7.recycle();	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        throw r1;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
    L_0x0181:
        r1 = r14.zzawy();	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r1 = r1.zzazd();	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r3 = "Unknown record type in local database";	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r1.log(r3);	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        goto L_0x0067;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
    L_0x0191:
        r1 = "messages";	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r3 = "rowid <= ?";	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r6 = 1;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r6 = new java.lang.String[r6];	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r7 = 0;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r4 = java.lang.Long.toString(r4);	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r6[r7] = r4;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r1 = r0.delete(r1, r3, r6);	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r3 = r10.size();	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        if (r1 >= r3) goto L_0x01b9;	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
    L_0x01ab:
        r1 = r14.zzawy();	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r1 = r1.zzazd();	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r3 = "Fewer entries removed from local database than expected";	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r1.log(r3);	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
    L_0x01b9:
        r0.setTransactionSuccessful();	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        r0.endTransaction();	 Catch:{ SQLiteFullException -> 0x009c, SQLiteException -> 0x00d6, all -> 0x00fa }
        if (r2 == 0) goto L_0x01c4;
    L_0x01c1:
        r2.close();
    L_0x01c4:
        if (r0 == 0) goto L_0x01c9;
    L_0x01c6:
        r0.close();
    L_0x01c9:
        r0 = r10;
        goto L_0x0008;
    L_0x01cc:
        if (r1 == 0) goto L_0x01d7;
    L_0x01ce:
        r3 = r1.inTransaction();	 Catch:{ all -> 0x020d }
        if (r3 == 0) goto L_0x01d7;	 Catch:{ all -> 0x020d }
    L_0x01d4:
        r1.endTransaction();	 Catch:{ all -> 0x020d }
    L_0x01d7:
        r3 = r14.zzawy();	 Catch:{ all -> 0x020d }
        r3 = r3.zzazd();	 Catch:{ all -> 0x020d }
        r4 = "Error reading entries from local database";	 Catch:{ all -> 0x020d }
        r3.zzj(r4, r0);	 Catch:{ all -> 0x020d }
        r0 = 1;	 Catch:{ all -> 0x020d }
        r14.zzjbo = r0;	 Catch:{ all -> 0x020d }
        r0 = r9;
        goto L_0x00ea;
    L_0x01eb:
        r0 = r14.zzawy();
        r0 = r0.zzazf();
        r1 = "Failed to read events from database in reasonable time";
        r0.log(r1);
        r0 = 0;
        goto L_0x0008;
    L_0x01fc:
        r0 = move-exception;
        r2 = r11;
        goto L_0x00fe;
    L_0x0200:
        r1 = move-exception;
        r2 = r11;
        r13 = r1;
        r1 = r0;
        r0 = r13;
        goto L_0x00fe;
    L_0x0207:
        r0 = move-exception;
        r13 = r1;
        r1 = r2;
        r2 = r13;
        goto L_0x00fe;
    L_0x020d:
        r0 = move-exception;
        goto L_0x00fe;
    L_0x0210:
        r0 = move-exception;
        r2 = r11;
        goto L_0x00da;
    L_0x0214:
        r1 = move-exception;
        r2 = r11;
        r13 = r1;
        r1 = r0;
        r0 = r13;
        goto L_0x00da;
    L_0x021b:
        r0 = move-exception;
        r2 = r1;
        r1 = r11;
        goto L_0x00a1;
    L_0x0220:
        r1 = move-exception;
        r2 = r0;
        r0 = r1;
        r1 = r11;
        goto L_0x00a1;
    L_0x0226:
        r0 = r9;
        goto L_0x00bd;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.zzchi.zzeb(int):java.util.List<com.google.android.gms.internal.zzbfm>");
    }

    public final /* bridge */ /* synthetic */ void zzve() {
        super.zzve();
    }

    public final /* bridge */ /* synthetic */ zzd zzws() {
        return super.zzws();
    }
}
