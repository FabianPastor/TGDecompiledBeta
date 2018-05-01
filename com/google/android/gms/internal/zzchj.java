package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import java.io.File;

@TargetApi(11)
final class zzchj
  extends SQLiteOpenHelper
{
  zzchj(zzchi paramzzchi, Context paramContext, String paramString)
  {
    super(paramContext, paramString, null, 1);
  }
  
  public final SQLiteDatabase getWritableDatabase()
  {
    try
    {
      SQLiteDatabase localSQLiteDatabase1 = super.getWritableDatabase();
      return localSQLiteDatabase1;
    }
    catch (SQLiteException localSQLiteException1)
    {
      if ((Build.VERSION.SDK_INT >= 11) && ((localSQLiteException1 instanceof SQLiteDatabaseLockedException))) {
        throw localSQLiteException1;
      }
      this.zzjbp.zzawy().zzazd().log("Opening the local database failed, dropping and recreating it");
      if (!this.zzjbp.getContext().getDatabasePath("google_app_measurement_local.db").delete()) {
        this.zzjbp.zzawy().zzazd().zzj("Failed to delete corrupted local db file", "google_app_measurement_local.db");
      }
      try
      {
        SQLiteDatabase localSQLiteDatabase2 = super.getWritableDatabase();
        return localSQLiteDatabase2;
      }
      catch (SQLiteException localSQLiteException2)
      {
        this.zzjbp.zzawy().zzazd().zzj("Failed to open local database. Events will bypass local storage", localSQLiteException2);
      }
    }
    return null;
  }
  
  public final void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    zzcgo.zza(this.zzjbp.zzawy(), paramSQLiteDatabase);
  }
  
  public final void onDowngrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
  
  /* Error */
  public final void onOpen(SQLiteDatabase paramSQLiteDatabase)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: getstatic 29	android/os/Build$VERSION:SDK_INT	I
    //   5: bipush 15
    //   7: if_icmpge +28 -> 35
    //   10: aload_1
    //   11: ldc 90
    //   13: aconst_null
    //   14: invokevirtual 96	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   17: astore_3
    //   18: aload_3
    //   19: invokeinterface 101 1 0
    //   24: pop
    //   25: aload_3
    //   26: ifnull +9 -> 35
    //   29: aload_3
    //   30: invokeinterface 105 1 0
    //   35: aload_0
    //   36: getfield 13	com/google/android/gms/internal/zzchj:zzjbp	Lcom/google/android/gms/internal/zzchi;
    //   39: invokevirtual 37	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   42: aload_1
    //   43: ldc 107
    //   45: ldc 109
    //   47: ldc 111
    //   49: aconst_null
    //   50: invokestatic 114	com/google/android/gms/internal/zzcgo:zza	(Lcom/google/android/gms/internal/zzchm;Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)V
    //   53: return
    //   54: astore_1
    //   55: aload_2
    //   56: ifnull +9 -> 65
    //   59: aload_2
    //   60: invokeinterface 105 1 0
    //   65: aload_1
    //   66: athrow
    //   67: astore_1
    //   68: aload_3
    //   69: astore_2
    //   70: goto -15 -> 55
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	73	0	this	zzchj
    //   0	73	1	paramSQLiteDatabase	SQLiteDatabase
    //   1	69	2	localObject	Object
    //   17	52	3	localCursor	android.database.Cursor
    // Exception table:
    //   from	to	target	type
    //   10	18	54	finally
    //   18	25	67	finally
  }
  
  public final void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzchj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */