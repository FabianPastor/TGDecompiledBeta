package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import android.support.annotation.WorkerThread;
import java.io.File;

@TargetApi(11)
final class zzcfi
  extends SQLiteOpenHelper
{
  zzcfi(zzcfh paramzzcfh, Context paramContext, String paramString)
  {
    super(paramContext, paramString, null, 1);
  }
  
  @WorkerThread
  public final SQLiteDatabase getWritableDatabase()
  {
    try
    {
      SQLiteDatabase localSQLiteDatabase = super.getWritableDatabase();
      return localSQLiteDatabase;
    }
    catch (SQLiteException localSQLiteException1)
    {
      if ((Build.VERSION.SDK_INT >= 11) && ((localSQLiteException1 instanceof SQLiteDatabaseLockedException))) {
        throw localSQLiteException1;
      }
      this.zzbqH.zzwF().zzyx().log("Opening the local database failed, dropping and recreating it");
      Object localObject = zzcem.zzxD();
      if (!this.zzbqH.getContext().getDatabasePath((String)localObject).delete()) {
        this.zzbqH.zzwF().zzyx().zzj("Failed to delete corrupted local db file", localObject);
      }
      try
      {
        localObject = super.getWritableDatabase();
        return (SQLiteDatabase)localObject;
      }
      catch (SQLiteException localSQLiteException2)
      {
        this.zzbqH.zzwF().zzyx().zzj("Failed to open local database. Events will bypass local storage", localSQLiteException2);
      }
    }
    return null;
  }
  
  @WorkerThread
  public final void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    zzcen.zza(this.zzbqH.zzwF(), paramSQLiteDatabase);
  }
  
  @WorkerThread
  public final void onDowngrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
  
  @WorkerThread
  public final void onOpen(SQLiteDatabase paramSQLiteDatabase)
  {
    Cursor localCursor;
    if (Build.VERSION.SDK_INT < 15) {
      localCursor = paramSQLiteDatabase.rawQuery("PRAGMA journal_mode=memory", null);
    }
    try
    {
      localCursor.moveToFirst();
      localCursor.close();
      zzcen.zza(this.zzbqH.zzwF(), paramSQLiteDatabase, "messages", "create table if not exists messages ( type INTEGER NOT NULL, entry BLOB NOT NULL)", "type,entry", null);
      return;
    }
    finally
    {
      localCursor.close();
    }
  }
  
  @WorkerThread
  public final void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcfi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */