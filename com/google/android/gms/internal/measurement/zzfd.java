package com.google.android.gms.internal.measurement;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import java.io.File;

final class zzfd
  extends SQLiteOpenHelper
{
  zzfd(zzfc paramzzfc, Context paramContext, String paramString)
  {
    super(paramContext, paramString, null, 1);
  }
  
  public final SQLiteDatabase getWritableDatabase()
    throws SQLiteException
  {
    try
    {
      SQLiteDatabase localSQLiteDatabase1 = super.getWritableDatabase();
      return localSQLiteDatabase1;
    }
    catch (SQLiteDatabaseLockedException localSQLiteDatabaseLockedException)
    {
      throw localSQLiteDatabaseLockedException;
    }
    catch (SQLiteException localSQLiteException1)
    {
      for (;;)
      {
        this.zzaii.zzgg().zzil().log("Opening the local database failed, dropping and recreating it");
        if (!this.zzaii.getContext().getDatabasePath("google_app_measurement_local.db").delete()) {
          this.zzaii.zzgg().zzil().zzg("Failed to delete corrupted local db file", "google_app_measurement_local.db");
        }
        try
        {
          SQLiteDatabase localSQLiteDatabase2 = super.getWritableDatabase();
        }
        catch (SQLiteException localSQLiteException2)
        {
          this.zzaii.zzgg().zzil().zzg("Failed to open local database. Events will bypass local storage", localSQLiteException2);
          Object localObject = null;
        }
      }
    }
  }
  
  public final void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    zzei.zza(this.zzaii.zzgg(), paramSQLiteDatabase);
  }
  
  public final void onDowngrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
  
  public final void onOpen(SQLiteDatabase paramSQLiteDatabase)
  {
    localObject1 = null;
    if (Build.VERSION.SDK_INT < 15) {}
    try
    {
      Cursor localCursor = paramSQLiteDatabase.rawQuery("PRAGMA journal_mode=memory", null);
      if (paramSQLiteDatabase == null) {
        break label69;
      }
    }
    finally
    {
      try
      {
        localCursor.moveToFirst();
        if (localCursor != null) {
          localCursor.close();
        }
        zzei.zza(this.zzaii.zzgg(), paramSQLiteDatabase, "messages", "create table if not exists messages ( type INTEGER NOT NULL, entry BLOB NOT NULL)", "type,entry", null);
        return;
      }
      finally
      {
        localObject1 = paramSQLiteDatabase;
        paramSQLiteDatabase = (SQLiteDatabase)localObject2;
      }
      localObject2 = finally;
      paramSQLiteDatabase = (SQLiteDatabase)localObject1;
      localObject1 = localObject2;
    }
    paramSQLiteDatabase.close();
    label69:
    throw ((Throwable)localObject1);
  }
  
  public final void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzfd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */