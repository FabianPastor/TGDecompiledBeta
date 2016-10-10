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
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzard;
import com.google.android.gms.internal.zzvk.zza;
import com.google.android.gms.internal.zzvk.zzb;
import com.google.android.gms.internal.zzvk.zze;
import com.google.android.gms.internal.zzvm.zzb;
import com.google.android.gms.internal.zzvm.zze;
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

class zze
  extends zzaa
{
  private static final Map<String, String> aoa = new ArrayMap(17);
  private static final Map<String, String> aob;
  private static final Map<String, String> aoc;
  private final zzc aod;
  private final zzah aoe = new zzah(zzaan());
  
  static
  {
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
    aob = new ArrayMap(1);
    aob.put("realtime", "ALTER TABLE raw_events ADD COLUMN realtime INTEGER;");
    aoc = new ArrayMap(1);
    aoc.put("has_realtime", "ALTER TABLE queue ADD COLUMN has_realtime INTEGER;");
  }
  
  zze(zzx paramzzx)
  {
    super(paramzzx);
    paramzzx = zzabs();
    this.aod = new zzc(getContext(), paramzzx);
  }
  
  @TargetApi(11)
  @WorkerThread
  static int zza(Cursor paramCursor, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 11) {
      return paramCursor.getType(paramInt);
    }
    CursorWindow localCursorWindow = ((SQLiteCursor)paramCursor).getWindow();
    int i = paramCursor.getPosition();
    if (localCursorWindow.isNull(i, paramInt)) {
      return 0;
    }
    if (localCursorWindow.isLong(i, paramInt)) {
      return 1;
    }
    if (localCursorWindow.isFloat(i, paramInt)) {
      return 2;
    }
    if (localCursorWindow.isString(i, paramInt)) {
      return 3;
    }
    if (localCursorWindow.isBlob(i, paramInt)) {
      return 4;
    }
    return -1;
  }
  
  @WorkerThread
  private long zza(String paramString, String[] paramArrayOfString, long paramLong)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    Object localObject = null;
    String[] arrayOfString = null;
    try
    {
      paramArrayOfString = localSQLiteDatabase.rawQuery(paramString, paramArrayOfString);
      arrayOfString = paramArrayOfString;
      localObject = paramArrayOfString;
      long l;
      if (paramArrayOfString.moveToFirst())
      {
        arrayOfString = paramArrayOfString;
        localObject = paramArrayOfString;
        paramLong = paramArrayOfString.getLong(0);
        l = paramLong;
        if (paramArrayOfString != null)
        {
          paramArrayOfString.close();
          l = paramLong;
        }
      }
      do
      {
        return l;
        l = paramLong;
      } while (paramArrayOfString == null);
      paramArrayOfString.close();
      return paramLong;
    }
    catch (SQLiteException paramArrayOfString)
    {
      localObject = arrayOfString;
      zzbvg().zzbwc().zze("Database error", paramString, paramArrayOfString);
      localObject = arrayOfString;
      throw paramArrayOfString;
    }
    finally
    {
      if (localObject != null) {
        ((Cursor)localObject).close();
      }
    }
  }
  
  @WorkerThread
  private void zza(String paramString, zzvk.zza paramzza)
  {
    int k = 0;
    zzaax();
    zzyl();
    zzac.zzhz(paramString);
    zzac.zzy(paramzza);
    zzac.zzy(paramzza.asC);
    zzac.zzy(paramzza.asB);
    if (paramzza.asA == null) {
      zzbvg().zzbwe().log("Audience with no ID");
    }
    label237:
    label282:
    label291:
    label292:
    for (;;)
    {
      return;
      int n = paramzza.asA.intValue();
      Object localObject = paramzza.asC;
      int j = localObject.length;
      int i = 0;
      while (i < j)
      {
        if (localObject[i].asE == null)
        {
          zzbvg().zzbwe().zze("Event filter with no ID. Audience definition ignored. appId, audienceId", paramString, paramzza.asA);
          return;
        }
        i += 1;
      }
      localObject = paramzza.asB;
      j = localObject.length;
      i = 0;
      while (i < j)
      {
        if (localObject[i].asE == null)
        {
          zzbvg().zzbwe().zze("Property filter with no ID. Audience definition ignored. appId, audienceId", paramString, paramzza.asA);
          return;
        }
        i += 1;
      }
      int m = 1;
      localObject = paramzza.asC;
      int i1 = localObject.length;
      j = 0;
      i = m;
      if (j < i1)
      {
        if (!zza(paramString, n, localObject[j])) {
          i = 0;
        }
      }
      else
      {
        if (i == 0) {
          break label291;
        }
        paramzza = paramzza.asB;
        m = paramzza.length;
        j = 0;
        if (j >= m) {
          break label291;
        }
        if (zza(paramString, n, paramzza[j])) {
          break label282;
        }
        i = k;
      }
      for (;;)
      {
        if (i != 0) {
          break label292;
        }
        zzaa(paramString, n);
        return;
        j += 1;
        break;
        j += 1;
        break label237;
      }
    }
  }
  
  @WorkerThread
  private boolean zza(String paramString, int paramInt, zzvk.zzb paramzzb)
  {
    zzaax();
    zzyl();
    zzac.zzhz(paramString);
    zzac.zzy(paramzzb);
    if (TextUtils.isEmpty(paramzzb.asF))
    {
      zzbvg().zzbwe().zze("Event filter had no event name. Audience definition ignored. audienceId, filterId", Integer.valueOf(paramInt), String.valueOf(paramzzb.asE));
      return false;
    }
    try
    {
      byte[] arrayOfByte = new byte[paramzzb.db()];
      Object localObject = zzard.zzbe(arrayOfByte);
      paramzzb.zza((zzard)localObject);
      ((zzard)localObject).cO();
      localObject = new ContentValues();
      ((ContentValues)localObject).put("app_id", paramString);
      ((ContentValues)localObject).put("audience_id", Integer.valueOf(paramInt));
      ((ContentValues)localObject).put("filter_id", paramzzb.asE);
      ((ContentValues)localObject).put("event_name", paramzzb.asF);
      ((ContentValues)localObject).put("data", arrayOfByte);
      return false;
    }
    catch (IOException paramString)
    {
      try
      {
        if (getWritableDatabase().insertWithOnConflict("event_filters", null, (ContentValues)localObject, 5) == -1L) {
          zzbvg().zzbwc().log("Failed to insert event filter (got -1)");
        }
        return true;
      }
      catch (SQLiteException paramString)
      {
        zzbvg().zzbwc().zzj("Error storing event filter", paramString);
      }
      paramString = paramString;
      zzbvg().zzbwc().zzj("Configuration loss. Failed to serialize event filter", paramString);
      return false;
    }
  }
  
  @WorkerThread
  private boolean zza(String paramString, int paramInt, zzvk.zze paramzze)
  {
    zzaax();
    zzyl();
    zzac.zzhz(paramString);
    zzac.zzy(paramzze);
    if (TextUtils.isEmpty(paramzze.asU))
    {
      zzbvg().zzbwe().zze("Property filter had no property name. Audience definition ignored. audienceId, filterId", Integer.valueOf(paramInt), String.valueOf(paramzze.asE));
      return false;
    }
    try
    {
      byte[] arrayOfByte = new byte[paramzze.db()];
      Object localObject = zzard.zzbe(arrayOfByte);
      paramzze.zza((zzard)localObject);
      ((zzard)localObject).cO();
      localObject = new ContentValues();
      ((ContentValues)localObject).put("app_id", paramString);
      ((ContentValues)localObject).put("audience_id", Integer.valueOf(paramInt));
      ((ContentValues)localObject).put("filter_id", paramzze.asE);
      ((ContentValues)localObject).put("property_name", paramzze.asU);
      ((ContentValues)localObject).put("data", arrayOfByte);
      try
      {
        if (getWritableDatabase().insertWithOnConflict("property_filters", null, (ContentValues)localObject, 5) == -1L)
        {
          zzbvg().zzbwc().log("Failed to insert property filter (got -1)");
          return false;
        }
      }
      catch (SQLiteException paramString)
      {
        zzbvg().zzbwc().zzj("Error storing property filter", paramString);
        return false;
      }
      return true;
    }
    catch (IOException paramString)
    {
      zzbvg().zzbwc().zzj("Configuration loss. Failed to serialize property filter", paramString);
      return false;
    }
  }
  
  @WorkerThread
  private long zzb(String paramString, String[] paramArrayOfString)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    Object localObject = null;
    String[] arrayOfString = null;
    try
    {
      paramArrayOfString = localSQLiteDatabase.rawQuery(paramString, paramArrayOfString);
      arrayOfString = paramArrayOfString;
      localObject = paramArrayOfString;
      if (paramArrayOfString.moveToFirst())
      {
        arrayOfString = paramArrayOfString;
        localObject = paramArrayOfString;
        long l = paramArrayOfString.getLong(0);
        if (paramArrayOfString != null) {
          paramArrayOfString.close();
        }
        return l;
      }
      arrayOfString = paramArrayOfString;
      localObject = paramArrayOfString;
      throw new SQLiteException("Database returned empty set");
    }
    catch (SQLiteException paramArrayOfString)
    {
      localObject = arrayOfString;
      zzbvg().zzbwc().zze("Database error", paramString, paramArrayOfString);
      localObject = arrayOfString;
      throw paramArrayOfString;
    }
    finally
    {
      if (localObject != null) {
        ((Cursor)localObject).close();
      }
    }
  }
  
  private boolean zzbvr()
  {
    return getContext().getDatabasePath(zzabs()).exists();
  }
  
  @WorkerThread
  public void beginTransaction()
  {
    zzaax();
    getWritableDatabase().beginTransaction();
  }
  
  @WorkerThread
  public void endTransaction()
  {
    zzaax();
    getWritableDatabase().endTransaction();
  }
  
  @WorkerThread
  SQLiteDatabase getWritableDatabase()
  {
    zzyl();
    try
    {
      SQLiteDatabase localSQLiteDatabase = this.aod.getWritableDatabase();
      return localSQLiteDatabase;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzbvg().zzbwe().zzj("Error opening database", localSQLiteException);
      throw localSQLiteException;
    }
  }
  
  @WorkerThread
  public void setTransactionSuccessful()
  {
    zzaax();
    getWritableDatabase().setTransactionSuccessful();
  }
  
  /* Error */
  public long zza(zzvm.zze paramzze)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 242	com/google/android/gms/measurement/internal/zze:zzyl	()V
    //   4: aload_0
    //   5: invokevirtual 239	com/google/android/gms/measurement/internal/zze:zzaax	()V
    //   8: aload_1
    //   9: invokestatic 252	com/google/android/gms/common/internal/zzac:zzy	(Ljava/lang/Object;)Ljava/lang/Object;
    //   12: pop
    //   13: aload_1
    //   14: getfield 437	com/google/android/gms/internal/zzvm$zze:zzck	Ljava/lang/String;
    //   17: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   20: pop
    //   21: aload_1
    //   22: invokevirtual 438	com/google/android/gms/internal/zzvm$zze:db	()I
    //   25: newarray <illegal type>
    //   27: astore 4
    //   29: aload 4
    //   31: invokestatic 334	com/google/android/gms/internal/zzard:zzbe	([B)Lcom/google/android/gms/internal/zzard;
    //   34: astore 5
    //   36: aload_1
    //   37: aload 5
    //   39: invokevirtual 439	com/google/android/gms/internal/zzvm$zze:zza	(Lcom/google/android/gms/internal/zzard;)V
    //   42: aload 5
    //   44: invokevirtual 340	com/google/android/gms/internal/zzard:cO	()V
    //   47: aload_0
    //   48: invokevirtual 443	com/google/android/gms/measurement/internal/zze:zzbvc	()Lcom/google/android/gms/measurement/internal/zzal;
    //   51: aload 4
    //   53: invokevirtual 448	com/google/android/gms/measurement/internal/zzal:zzy	([B)J
    //   56: lstore_2
    //   57: new 342	android/content/ContentValues
    //   60: dup
    //   61: invokespecial 344	android/content/ContentValues:<init>	()V
    //   64: astore 5
    //   66: aload 5
    //   68: ldc_w 346
    //   71: aload_1
    //   72: getfield 437	com/google/android/gms/internal/zzvm$zze:zzck	Ljava/lang/String;
    //   75: invokevirtual 349	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   78: aload 5
    //   80: ldc_w 450
    //   83: lload_2
    //   84: invokestatic 455	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   87: invokevirtual 458	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   90: aload 5
    //   92: ldc_w 460
    //   95: aload 4
    //   97: invokevirtual 363	android/content/ContentValues:put	(Ljava/lang/String;[B)V
    //   100: aload_0
    //   101: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   104: ldc_w 462
    //   107: aconst_null
    //   108: aload 5
    //   110: iconst_4
    //   111: invokevirtual 369	android/database/sqlite/SQLiteDatabase:insertWithOnConflict	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    //   114: pop2
    //   115: lload_2
    //   116: lreturn
    //   117: astore_1
    //   118: aload_0
    //   119: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   122: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   125: ldc_w 464
    //   128: aload_1
    //   129: invokevirtual 379	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   132: aload_1
    //   133: athrow
    //   134: astore_1
    //   135: aload_0
    //   136: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   139: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   142: ldc_w 466
    //   145: aload_1
    //   146: invokevirtual 379	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   149: aload_1
    //   150: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	151	0	this	zze
    //   0	151	1	paramzze	zzvm.zze
    //   56	60	2	l	long
    //   27	69	4	arrayOfByte	byte[]
    //   34	75	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   21	47	117	java/io/IOException
    //   100	115	134	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  @WorkerThread
  public zza zza(long paramLong, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
  {
    // Byte code:
    //   0: aload_3
    //   1: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 242	com/google/android/gms/measurement/internal/zze:zzyl	()V
    //   9: aload_0
    //   10: invokevirtual 239	com/google/android/gms/measurement/internal/zze:zzaax	()V
    //   13: new 6	com/google/android/gms/measurement/internal/zze$zza
    //   16: dup
    //   17: invokespecial 469	com/google/android/gms/measurement/internal/zze$zza:<init>	()V
    //   20: astore 11
    //   22: aload_0
    //   23: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   26: astore 12
    //   28: aload 12
    //   30: ldc_w 471
    //   33: bipush 6
    //   35: anewarray 322	java/lang/String
    //   38: dup
    //   39: iconst_0
    //   40: ldc 64
    //   42: aastore
    //   43: dup
    //   44: iconst_1
    //   45: ldc 72
    //   47: aastore
    //   48: dup
    //   49: iconst_2
    //   50: ldc 68
    //   52: aastore
    //   53: dup
    //   54: iconst_3
    //   55: ldc 76
    //   57: aastore
    //   58: dup
    //   59: iconst_4
    //   60: ldc 100
    //   62: aastore
    //   63: dup
    //   64: iconst_5
    //   65: ldc 104
    //   67: aastore
    //   68: ldc_w 473
    //   71: iconst_1
    //   72: anewarray 322	java/lang/String
    //   75: dup
    //   76: iconst_0
    //   77: aload_3
    //   78: aastore
    //   79: aconst_null
    //   80: aconst_null
    //   81: aconst_null
    //   82: invokevirtual 477	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   85: astore 10
    //   87: aload 10
    //   89: astore 9
    //   91: aload 10
    //   93: invokeinterface 209 1 0
    //   98: ifne +36 -> 134
    //   101: aload 10
    //   103: astore 9
    //   105: aload_0
    //   106: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   109: invokevirtual 269	com/google/android/gms/measurement/internal/zzp:zzbwe	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   112: ldc_w 479
    //   115: aload_3
    //   116: invokevirtual 379	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   119: aload 10
    //   121: ifnull +10 -> 131
    //   124: aload 10
    //   126: invokeinterface 216 1 0
    //   131: aload 11
    //   133: areturn
    //   134: aload 10
    //   136: astore 9
    //   138: aload 10
    //   140: iconst_0
    //   141: invokeinterface 213 2 0
    //   146: lload_1
    //   147: lcmp
    //   148: ifne +88 -> 236
    //   151: aload 10
    //   153: astore 9
    //   155: aload 11
    //   157: aload 10
    //   159: iconst_1
    //   160: invokeinterface 213 2 0
    //   165: putfield 483	com/google/android/gms/measurement/internal/zze$zza:aog	J
    //   168: aload 10
    //   170: astore 9
    //   172: aload 11
    //   174: aload 10
    //   176: iconst_2
    //   177: invokeinterface 213 2 0
    //   182: putfield 486	com/google/android/gms/measurement/internal/zze$zza:aof	J
    //   185: aload 10
    //   187: astore 9
    //   189: aload 11
    //   191: aload 10
    //   193: iconst_3
    //   194: invokeinterface 213 2 0
    //   199: putfield 489	com/google/android/gms/measurement/internal/zze$zza:aoh	J
    //   202: aload 10
    //   204: astore 9
    //   206: aload 11
    //   208: aload 10
    //   210: iconst_4
    //   211: invokeinterface 213 2 0
    //   216: putfield 492	com/google/android/gms/measurement/internal/zze$zza:aoi	J
    //   219: aload 10
    //   221: astore 9
    //   223: aload 11
    //   225: aload 10
    //   227: iconst_5
    //   228: invokeinterface 213 2 0
    //   233: putfield 495	com/google/android/gms/measurement/internal/zze$zza:aoj	J
    //   236: iload 4
    //   238: ifeq +19 -> 257
    //   241: aload 10
    //   243: astore 9
    //   245: aload 11
    //   247: aload 11
    //   249: getfield 483	com/google/android/gms/measurement/internal/zze$zza:aog	J
    //   252: lconst_1
    //   253: ladd
    //   254: putfield 483	com/google/android/gms/measurement/internal/zze$zza:aog	J
    //   257: iload 5
    //   259: ifeq +19 -> 278
    //   262: aload 10
    //   264: astore 9
    //   266: aload 11
    //   268: aload 11
    //   270: getfield 486	com/google/android/gms/measurement/internal/zze$zza:aof	J
    //   273: lconst_1
    //   274: ladd
    //   275: putfield 486	com/google/android/gms/measurement/internal/zze$zza:aof	J
    //   278: iload 6
    //   280: ifeq +19 -> 299
    //   283: aload 10
    //   285: astore 9
    //   287: aload 11
    //   289: aload 11
    //   291: getfield 489	com/google/android/gms/measurement/internal/zze$zza:aoh	J
    //   294: lconst_1
    //   295: ladd
    //   296: putfield 489	com/google/android/gms/measurement/internal/zze$zza:aoh	J
    //   299: iload 7
    //   301: ifeq +19 -> 320
    //   304: aload 10
    //   306: astore 9
    //   308: aload 11
    //   310: aload 11
    //   312: getfield 492	com/google/android/gms/measurement/internal/zze$zza:aoi	J
    //   315: lconst_1
    //   316: ladd
    //   317: putfield 492	com/google/android/gms/measurement/internal/zze$zza:aoi	J
    //   320: iload 8
    //   322: ifeq +19 -> 341
    //   325: aload 10
    //   327: astore 9
    //   329: aload 11
    //   331: aload 11
    //   333: getfield 495	com/google/android/gms/measurement/internal/zze$zza:aoj	J
    //   336: lconst_1
    //   337: ladd
    //   338: putfield 495	com/google/android/gms/measurement/internal/zze$zza:aoj	J
    //   341: aload 10
    //   343: astore 9
    //   345: new 342	android/content/ContentValues
    //   348: dup
    //   349: invokespecial 344	android/content/ContentValues:<init>	()V
    //   352: astore 13
    //   354: aload 10
    //   356: astore 9
    //   358: aload 13
    //   360: ldc 64
    //   362: lload_1
    //   363: invokestatic 455	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   366: invokevirtual 458	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   369: aload 10
    //   371: astore 9
    //   373: aload 13
    //   375: ldc 68
    //   377: aload 11
    //   379: getfield 486	com/google/android/gms/measurement/internal/zze$zza:aof	J
    //   382: invokestatic 455	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   385: invokevirtual 458	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   388: aload 10
    //   390: astore 9
    //   392: aload 13
    //   394: ldc 72
    //   396: aload 11
    //   398: getfield 483	com/google/android/gms/measurement/internal/zze$zza:aog	J
    //   401: invokestatic 455	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   404: invokevirtual 458	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   407: aload 10
    //   409: astore 9
    //   411: aload 13
    //   413: ldc 76
    //   415: aload 11
    //   417: getfield 489	com/google/android/gms/measurement/internal/zze$zza:aoh	J
    //   420: invokestatic 455	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   423: invokevirtual 458	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   426: aload 10
    //   428: astore 9
    //   430: aload 13
    //   432: ldc 100
    //   434: aload 11
    //   436: getfield 492	com/google/android/gms/measurement/internal/zze$zza:aoi	J
    //   439: invokestatic 455	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   442: invokevirtual 458	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   445: aload 10
    //   447: astore 9
    //   449: aload 13
    //   451: ldc 104
    //   453: aload 11
    //   455: getfield 495	com/google/android/gms/measurement/internal/zze$zza:aoj	J
    //   458: invokestatic 455	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   461: invokevirtual 458	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   464: aload 10
    //   466: astore 9
    //   468: aload 12
    //   470: ldc_w 471
    //   473: aload 13
    //   475: ldc_w 473
    //   478: iconst_1
    //   479: anewarray 322	java/lang/String
    //   482: dup
    //   483: iconst_0
    //   484: aload_3
    //   485: aastore
    //   486: invokevirtual 499	android/database/sqlite/SQLiteDatabase:update	(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   489: pop
    //   490: aload 10
    //   492: ifnull +10 -> 502
    //   495: aload 10
    //   497: invokeinterface 216 1 0
    //   502: aload 11
    //   504: areturn
    //   505: astore_3
    //   506: aconst_null
    //   507: astore 10
    //   509: aload 10
    //   511: astore 9
    //   513: aload_0
    //   514: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   517: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   520: ldc_w 501
    //   523: aload_3
    //   524: invokevirtual 379	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   527: aload 10
    //   529: ifnull +10 -> 539
    //   532: aload 10
    //   534: invokeinterface 216 1 0
    //   539: aload 11
    //   541: areturn
    //   542: astore_3
    //   543: aconst_null
    //   544: astore 9
    //   546: aload 9
    //   548: ifnull +10 -> 558
    //   551: aload 9
    //   553: invokeinterface 216 1 0
    //   558: aload_3
    //   559: athrow
    //   560: astore_3
    //   561: goto -15 -> 546
    //   564: astore_3
    //   565: goto -56 -> 509
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	568	0	this	zze
    //   0	568	1	paramLong	long
    //   0	568	3	paramString	String
    //   0	568	4	paramBoolean1	boolean
    //   0	568	5	paramBoolean2	boolean
    //   0	568	6	paramBoolean3	boolean
    //   0	568	7	paramBoolean4	boolean
    //   0	568	8	paramBoolean5	boolean
    //   89	463	9	localCursor1	Cursor
    //   85	448	10	localCursor2	Cursor
    //   20	520	11	localzza	zza
    //   26	443	12	localSQLiteDatabase	SQLiteDatabase
    //   352	122	13	localContentValues	ContentValues
    // Exception table:
    //   from	to	target	type
    //   22	87	505	android/database/sqlite/SQLiteException
    //   22	87	542	finally
    //   91	101	560	finally
    //   105	119	560	finally
    //   138	151	560	finally
    //   155	168	560	finally
    //   172	185	560	finally
    //   189	202	560	finally
    //   206	219	560	finally
    //   223	236	560	finally
    //   245	257	560	finally
    //   266	278	560	finally
    //   287	299	560	finally
    //   308	320	560	finally
    //   329	341	560	finally
    //   345	354	560	finally
    //   358	369	560	finally
    //   373	388	560	finally
    //   392	407	560	finally
    //   411	426	560	finally
    //   430	445	560	finally
    //   449	464	560	finally
    //   468	490	560	finally
    //   513	527	560	finally
    //   91	101	564	android/database/sqlite/SQLiteException
    //   105	119	564	android/database/sqlite/SQLiteException
    //   138	151	564	android/database/sqlite/SQLiteException
    //   155	168	564	android/database/sqlite/SQLiteException
    //   172	185	564	android/database/sqlite/SQLiteException
    //   189	202	564	android/database/sqlite/SQLiteException
    //   206	219	564	android/database/sqlite/SQLiteException
    //   223	236	564	android/database/sqlite/SQLiteException
    //   245	257	564	android/database/sqlite/SQLiteException
    //   266	278	564	android/database/sqlite/SQLiteException
    //   287	299	564	android/database/sqlite/SQLiteException
    //   308	320	564	android/database/sqlite/SQLiteException
    //   329	341	564	android/database/sqlite/SQLiteException
    //   345	354	564	android/database/sqlite/SQLiteException
    //   358	369	564	android/database/sqlite/SQLiteException
    //   373	388	564	android/database/sqlite/SQLiteException
    //   392	407	564	android/database/sqlite/SQLiteException
    //   411	426	564	android/database/sqlite/SQLiteException
    //   430	445	564	android/database/sqlite/SQLiteException
    //   449	464	564	android/database/sqlite/SQLiteException
    //   468	490	564	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  void zza(ContentValues paramContentValues, String paramString, Object paramObject)
  {
    zzac.zzhz(paramString);
    zzac.zzy(paramObject);
    if ((paramObject instanceof String))
    {
      paramContentValues.put(paramString, (String)paramObject);
      return;
    }
    if ((paramObject instanceof Long))
    {
      paramContentValues.put(paramString, (Long)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      paramContentValues.put(paramString, (Double)paramObject);
      return;
    }
    throw new IllegalArgumentException("Invalid value type");
  }
  
  @WorkerThread
  public void zza(zzvm.zze paramzze, boolean paramBoolean)
  {
    zzyl();
    zzaax();
    zzac.zzy(paramzze);
    zzac.zzhz(paramzze.zzck);
    zzac.zzy(paramzze.atA);
    zzbvl();
    long l = zzaan().currentTimeMillis();
    if ((paramzze.atA.longValue() < l - zzbvi().zzbue()) || (paramzze.atA.longValue() > zzbvi().zzbue() + l)) {
      zzbvg().zzbwe().zze("Storing bundle outside of the max uploading time span. now, timestamp", Long.valueOf(l), paramzze.atA);
    }
    for (;;)
    {
      try
      {
        byte[] arrayOfByte = new byte[paramzze.db()];
        Object localObject = zzard.zzbe(arrayOfByte);
        paramzze.zza((zzard)localObject);
        ((zzard)localObject).cO();
        arrayOfByte = zzbvc().zzj(arrayOfByte);
        zzbvg().zzbwj().zzj("Saving bundle, size", Integer.valueOf(arrayOfByte.length));
        localObject = new ContentValues();
        ((ContentValues)localObject).put("app_id", paramzze.zzck);
        ((ContentValues)localObject).put("bundle_end_timestamp", paramzze.atA);
        ((ContentValues)localObject).put("data", arrayOfByte);
        if (paramBoolean)
        {
          i = 1;
          ((ContentValues)localObject).put("has_realtime", Integer.valueOf(i));
        }
        int i = 0;
      }
      catch (IOException paramzze)
      {
        try
        {
          if (getWritableDatabase().insert("queue", null, (ContentValues)localObject) == -1L) {
            zzbvg().zzbwc().log("Failed to insert bundle (got -1)");
          }
          return;
        }
        catch (SQLiteException paramzze)
        {
          zzbvg().zzbwc().zzj("Error storing bundle", paramzze);
        }
        paramzze = paramzze;
        zzbvg().zzbwc().zzj("Data loss. Failed to serialize bundle", paramzze);
        return;
      }
    }
  }
  
  @WorkerThread
  public void zza(zza paramzza)
  {
    zzac.zzy(paramzza);
    zzyl();
    zzaax();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzza.zzti());
    localContentValues.put("app_instance_id", paramzza.zzayn());
    localContentValues.put("gmp_app_id", paramzza.zzbsr());
    localContentValues.put("resettable_device_id_hash", paramzza.zzbss());
    localContentValues.put("last_bundle_index", Long.valueOf(paramzza.zzbtb()));
    localContentValues.put("last_bundle_start_timestamp", Long.valueOf(paramzza.zzbsu()));
    localContentValues.put("last_bundle_end_timestamp", Long.valueOf(paramzza.zzbsv()));
    localContentValues.put("app_version", paramzza.zzyt());
    localContentValues.put("app_store", paramzza.zzbsx());
    localContentValues.put("gmp_version", Long.valueOf(paramzza.zzbsy()));
    localContentValues.put("dev_cert_hash", Long.valueOf(paramzza.zzbsz()));
    localContentValues.put("measurement_enabled", Boolean.valueOf(paramzza.zzbta()));
    localContentValues.put("day", Long.valueOf(paramzza.zzbtf()));
    localContentValues.put("daily_public_events_count", Long.valueOf(paramzza.zzbtg()));
    localContentValues.put("daily_events_count", Long.valueOf(paramzza.zzbth()));
    localContentValues.put("daily_conversions_count", Long.valueOf(paramzza.zzbti()));
    localContentValues.put("config_fetched_time", Long.valueOf(paramzza.zzbtc()));
    localContentValues.put("failed_config_fetch_time", Long.valueOf(paramzza.zzbtd()));
    localContentValues.put("app_version_int", Long.valueOf(paramzza.zzbsw()));
    localContentValues.put("firebase_instance_id", paramzza.zzbst());
    localContentValues.put("daily_error_events_count", Long.valueOf(paramzza.zzbtk()));
    localContentValues.put("daily_realtime_events_count", Long.valueOf(paramzza.zzbtj()));
    try
    {
      if (getWritableDatabase().insertWithOnConflict("apps", null, localContentValues, 5) == -1L) {
        zzbvg().zzbwc().log("Failed to insert/update app (got -1)");
      }
      return;
    }
    catch (SQLiteException paramzza)
    {
      zzbvg().zzbwc().zzj("Error storing app", paramzza);
    }
  }
  
  /* Error */
  public void zza(zzh paramzzh, long paramLong, boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 6
    //   3: aload_0
    //   4: invokevirtual 242	com/google/android/gms/measurement/internal/zze:zzyl	()V
    //   7: aload_0
    //   8: invokevirtual 239	com/google/android/gms/measurement/internal/zze:zzaax	()V
    //   11: aload_1
    //   12: invokestatic 252	com/google/android/gms/common/internal/zzac:zzy	(Ljava/lang/Object;)Ljava/lang/Object;
    //   15: pop
    //   16: aload_1
    //   17: getfield 659	com/google/android/gms/measurement/internal/zzh:zzcpe	Ljava/lang/String;
    //   20: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   23: pop
    //   24: new 661	com/google/android/gms/internal/zzvm$zzb
    //   27: dup
    //   28: invokespecial 662	com/google/android/gms/internal/zzvm$zzb:<init>	()V
    //   31: astore 7
    //   33: aload 7
    //   35: aload_1
    //   36: getfield 665	com/google/android/gms/measurement/internal/zzh:aor	J
    //   39: invokestatic 455	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   42: putfield 668	com/google/android/gms/internal/zzvm$zzb:atq	Ljava/lang/Long;
    //   45: aload 7
    //   47: aload_1
    //   48: getfield 672	com/google/android/gms/measurement/internal/zzh:aos	Lcom/google/android/gms/measurement/internal/EventParams;
    //   51: invokevirtual 677	com/google/android/gms/measurement/internal/EventParams:size	()I
    //   54: anewarray 679	com/google/android/gms/internal/zzvm$zzc
    //   57: putfield 683	com/google/android/gms/internal/zzvm$zzb:ato	[Lcom/google/android/gms/internal/zzvm$zzc;
    //   60: aload_1
    //   61: getfield 672	com/google/android/gms/measurement/internal/zzh:aos	Lcom/google/android/gms/measurement/internal/EventParams;
    //   64: invokevirtual 687	com/google/android/gms/measurement/internal/EventParams:iterator	()Ljava/util/Iterator;
    //   67: astore 8
    //   69: iconst_0
    //   70: istore 5
    //   72: aload 8
    //   74: invokeinterface 692 1 0
    //   79: ifeq +72 -> 151
    //   82: aload 8
    //   84: invokeinterface 696 1 0
    //   89: checkcast 322	java/lang/String
    //   92: astore 10
    //   94: new 679	com/google/android/gms/internal/zzvm$zzc
    //   97: dup
    //   98: invokespecial 697	com/google/android/gms/internal/zzvm$zzc:<init>	()V
    //   101: astore 9
    //   103: aload 7
    //   105: getfield 683	com/google/android/gms/internal/zzvm$zzb:ato	[Lcom/google/android/gms/internal/zzvm$zzc;
    //   108: iload 5
    //   110: aload 9
    //   112: aastore
    //   113: aload 9
    //   115: aload 10
    //   117: putfield 700	com/google/android/gms/internal/zzvm$zzc:name	Ljava/lang/String;
    //   120: aload_1
    //   121: getfield 672	com/google/android/gms/measurement/internal/zzh:aos	Lcom/google/android/gms/measurement/internal/EventParams;
    //   124: aload 10
    //   126: invokevirtual 704	com/google/android/gms/measurement/internal/EventParams:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   129: astore 10
    //   131: aload_0
    //   132: invokevirtual 443	com/google/android/gms/measurement/internal/zze:zzbvc	()Lcom/google/android/gms/measurement/internal/zzal;
    //   135: aload 9
    //   137: aload 10
    //   139: invokevirtual 707	com/google/android/gms/measurement/internal/zzal:zza	(Lcom/google/android/gms/internal/zzvm$zzc;Ljava/lang/Object;)V
    //   142: iload 5
    //   144: iconst_1
    //   145: iadd
    //   146: istore 5
    //   148: goto -76 -> 72
    //   151: aload 7
    //   153: invokevirtual 708	com/google/android/gms/internal/zzvm$zzb:db	()I
    //   156: newarray <illegal type>
    //   158: astore 8
    //   160: aload 8
    //   162: invokestatic 334	com/google/android/gms/internal/zzard:zzbe	([B)Lcom/google/android/gms/internal/zzard;
    //   165: astore 9
    //   167: aload 7
    //   169: aload 9
    //   171: invokevirtual 709	com/google/android/gms/internal/zzvm$zzb:zza	(Lcom/google/android/gms/internal/zzard;)V
    //   174: aload 9
    //   176: invokevirtual 340	com/google/android/gms/internal/zzard:cO	()V
    //   179: aload_0
    //   180: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   183: invokevirtual 546	com/google/android/gms/measurement/internal/zzp:zzbwj	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   186: ldc_w 711
    //   189: aload_1
    //   190: getfield 714	com/google/android/gms/measurement/internal/zzh:mName	Ljava/lang/String;
    //   193: aload 8
    //   195: arraylength
    //   196: invokestatic 320	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   199: invokevirtual 234	com/google/android/gms/measurement/internal/zzp$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   202: new 342	android/content/ContentValues
    //   205: dup
    //   206: invokespecial 344	android/content/ContentValues:<init>	()V
    //   209: astore 7
    //   211: aload 7
    //   213: ldc_w 346
    //   216: aload_1
    //   217: getfield 659	com/google/android/gms/measurement/internal/zzh:zzcpe	Ljava/lang/String;
    //   220: invokevirtual 349	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   223: aload 7
    //   225: ldc_w 715
    //   228: aload_1
    //   229: getfield 714	com/google/android/gms/measurement/internal/zzh:mName	Ljava/lang/String;
    //   232: invokevirtual 349	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   235: aload 7
    //   237: ldc_w 717
    //   240: aload_1
    //   241: getfield 720	com/google/android/gms/measurement/internal/zzh:tr	J
    //   244: invokestatic 455	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   247: invokevirtual 458	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   250: aload 7
    //   252: ldc_w 450
    //   255: lload_2
    //   256: invokestatic 455	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   259: invokevirtual 458	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   262: aload 7
    //   264: ldc_w 360
    //   267: aload 8
    //   269: invokevirtual 363	android/content/ContentValues:put	(Ljava/lang/String;[B)V
    //   272: iload 6
    //   274: istore 5
    //   276: iload 4
    //   278: ifeq +6 -> 284
    //   281: iconst_1
    //   282: istore 5
    //   284: aload 7
    //   286: ldc 110
    //   288: iload 5
    //   290: invokestatic 320	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   293: invokevirtual 354	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   296: aload_0
    //   297: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   300: ldc_w 722
    //   303: aconst_null
    //   304: aload 7
    //   306: invokevirtual 556	android/database/sqlite/SQLiteDatabase:insert	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;)J
    //   309: ldc2_w 370
    //   312: lcmp
    //   313: ifne +16 -> 329
    //   316: aload_0
    //   317: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   320: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   323: ldc_w 724
    //   326: invokevirtual 275	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
    //   329: return
    //   330: astore_1
    //   331: aload_0
    //   332: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   335: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   338: ldc_w 726
    //   341: aload_1
    //   342: invokevirtual 379	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   345: return
    //   346: astore_1
    //   347: aload_0
    //   348: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   351: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   354: ldc_w 728
    //   357: aload_1
    //   358: invokevirtual 379	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   361: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	362	0	this	zze
    //   0	362	1	paramzzh	zzh
    //   0	362	2	paramLong	long
    //   0	362	4	paramBoolean	boolean
    //   70	219	5	i	int
    //   1	272	6	j	int
    //   31	274	7	localObject1	Object
    //   67	201	8	localObject2	Object
    //   101	74	9	localObject3	Object
    //   92	46	10	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   151	179	330	java/io/IOException
    //   296	329	346	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  public void zza(zzi paramzzi)
  {
    zzac.zzy(paramzzi);
    zzyl();
    zzaax();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzi.zzcpe);
    localContentValues.put("name", paramzzi.mName);
    localContentValues.put("lifetime_count", Long.valueOf(paramzzi.aot));
    localContentValues.put("current_bundle_count", Long.valueOf(paramzzi.aou));
    localContentValues.put("last_fire_timestamp", Long.valueOf(paramzzi.aov));
    try
    {
      if (getWritableDatabase().insertWithOnConflict("events", null, localContentValues, 5) == -1L) {
        zzbvg().zzbwc().log("Failed to insert/update event aggregates (got -1)");
      }
      return;
    }
    catch (SQLiteException paramzzi)
    {
      zzbvg().zzbwc().zzj("Error storing event aggregates", paramzzi);
    }
  }
  
  /* Error */
  void zza(String paramString, int paramInt, com.google.android.gms.internal.zzvm.zzf paramzzf)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 239	com/google/android/gms/measurement/internal/zze:zzaax	()V
    //   4: aload_0
    //   5: invokevirtual 242	com/google/android/gms/measurement/internal/zze:zzyl	()V
    //   8: aload_1
    //   9: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_3
    //   14: invokestatic 252	com/google/android/gms/common/internal/zzac:zzy	(Ljava/lang/Object;)Ljava/lang/Object;
    //   17: pop
    //   18: aload_3
    //   19: invokevirtual 758	com/google/android/gms/internal/zzvm$zzf:db	()I
    //   22: newarray <illegal type>
    //   24: astore 4
    //   26: aload 4
    //   28: invokestatic 334	com/google/android/gms/internal/zzard:zzbe	([B)Lcom/google/android/gms/internal/zzard;
    //   31: astore 5
    //   33: aload_3
    //   34: aload 5
    //   36: invokevirtual 759	com/google/android/gms/internal/zzvm$zzf:zza	(Lcom/google/android/gms/internal/zzard;)V
    //   39: aload 5
    //   41: invokevirtual 340	com/google/android/gms/internal/zzard:cO	()V
    //   44: new 342	android/content/ContentValues
    //   47: dup
    //   48: invokespecial 344	android/content/ContentValues:<init>	()V
    //   51: astore_3
    //   52: aload_3
    //   53: ldc_w 346
    //   56: aload_1
    //   57: invokevirtual 349	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   60: aload_3
    //   61: ldc_w 351
    //   64: iload_2
    //   65: invokestatic 320	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   68: invokevirtual 354	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   71: aload_3
    //   72: ldc_w 761
    //   75: aload 4
    //   77: invokevirtual 363	android/content/ContentValues:put	(Ljava/lang/String;[B)V
    //   80: aload_0
    //   81: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   84: ldc_w 763
    //   87: aconst_null
    //   88: aload_3
    //   89: iconst_5
    //   90: invokevirtual 369	android/database/sqlite/SQLiteDatabase:insertWithOnConflict	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    //   93: ldc2_w 370
    //   96: lcmp
    //   97: ifne +16 -> 113
    //   100: aload_0
    //   101: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   104: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   107: ldc_w 765
    //   110: invokevirtual 275	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
    //   113: return
    //   114: astore_1
    //   115: aload_0
    //   116: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   119: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   122: ldc_w 767
    //   125: aload_1
    //   126: invokevirtual 379	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   129: return
    //   130: astore_1
    //   131: aload_0
    //   132: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   135: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   138: ldc_w 769
    //   141: aload_1
    //   142: invokevirtual 379	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   145: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	146	0	this	zze
    //   0	146	1	paramString	String
    //   0	146	2	paramInt	int
    //   0	146	3	paramzzf	com.google.android.gms.internal.zzvm.zzf
    //   24	52	4	arrayOfByte	byte[]
    //   31	9	5	localzzard	zzard
    // Exception table:
    //   from	to	target	type
    //   18	44	114	java/io/IOException
    //   80	113	130	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  public void zza(String paramString, long paramLong, zzb paramzzb)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aload 4
    //   5: invokestatic 252	com/google/android/gms/common/internal/zzac:zzy	(Ljava/lang/Object;)Ljava/lang/Object;
    //   8: pop
    //   9: aload_0
    //   10: invokevirtual 242	com/google/android/gms/measurement/internal/zze:zzyl	()V
    //   13: aload_0
    //   14: invokevirtual 239	com/google/android/gms/measurement/internal/zze:zzaax	()V
    //   17: aload 7
    //   19: astore 6
    //   21: aload_0
    //   22: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   25: astore 10
    //   27: aload 7
    //   29: astore 6
    //   31: aload_1
    //   32: invokestatic 314	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   35: ifeq +204 -> 239
    //   38: aload 7
    //   40: astore 6
    //   42: aload 10
    //   44: ldc_w 772
    //   47: iconst_1
    //   48: anewarray 322	java/lang/String
    //   51: dup
    //   52: iconst_0
    //   53: lload_2
    //   54: invokestatic 775	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   57: aastore
    //   58: invokevirtual 205	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   61: astore_1
    //   62: aload_1
    //   63: astore 6
    //   65: aload_1
    //   66: astore 7
    //   68: aload_1
    //   69: invokeinterface 209 1 0
    //   74: istore 5
    //   76: iload 5
    //   78: ifne +14 -> 92
    //   81: aload_1
    //   82: ifnull +9 -> 91
    //   85: aload_1
    //   86: invokeinterface 216 1 0
    //   91: return
    //   92: aload_1
    //   93: astore 6
    //   95: aload_1
    //   96: astore 7
    //   98: aload_1
    //   99: iconst_0
    //   100: invokeinterface 779 2 0
    //   105: astore 8
    //   107: aload_1
    //   108: astore 6
    //   110: aload_1
    //   111: astore 7
    //   113: aload_1
    //   114: iconst_1
    //   115: invokeinterface 779 2 0
    //   120: astore 9
    //   122: aload_1
    //   123: astore 6
    //   125: aload_1
    //   126: astore 7
    //   128: aload_1
    //   129: invokeinterface 216 1 0
    //   134: aload 9
    //   136: astore 7
    //   138: aload_1
    //   139: astore 6
    //   141: aload 6
    //   143: astore_1
    //   144: aload 10
    //   146: ldc_w 462
    //   149: iconst_1
    //   150: anewarray 322	java/lang/String
    //   153: dup
    //   154: iconst_0
    //   155: ldc_w 460
    //   158: aastore
    //   159: ldc_w 781
    //   162: iconst_2
    //   163: anewarray 322	java/lang/String
    //   166: dup
    //   167: iconst_0
    //   168: aload 8
    //   170: aastore
    //   171: dup
    //   172: iconst_1
    //   173: aload 7
    //   175: aastore
    //   176: aconst_null
    //   177: aconst_null
    //   178: ldc_w 783
    //   181: ldc_w 785
    //   184: invokevirtual 788	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   187: astore 9
    //   189: aload 9
    //   191: astore_1
    //   192: aload 9
    //   194: astore 6
    //   196: aload 9
    //   198: invokeinterface 209 1 0
    //   203: ifne +140 -> 343
    //   206: aload 9
    //   208: astore_1
    //   209: aload 9
    //   211: astore 6
    //   213: aload_0
    //   214: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   217: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   220: ldc_w 790
    //   223: invokevirtual 275	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
    //   226: aload 9
    //   228: ifnull -137 -> 91
    //   231: aload 9
    //   233: invokeinterface 216 1 0
    //   238: return
    //   239: aload 7
    //   241: astore 6
    //   243: aload 10
    //   245: ldc_w 792
    //   248: iconst_1
    //   249: anewarray 322	java/lang/String
    //   252: dup
    //   253: iconst_0
    //   254: aload_1
    //   255: aastore
    //   256: invokevirtual 205	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   259: astore 8
    //   261: aload 8
    //   263: astore 6
    //   265: aload 8
    //   267: astore 7
    //   269: aload 8
    //   271: invokeinterface 209 1 0
    //   276: istore 5
    //   278: iload 5
    //   280: ifne +16 -> 296
    //   283: aload 8
    //   285: ifnull -194 -> 91
    //   288: aload 8
    //   290: invokeinterface 216 1 0
    //   295: return
    //   296: aload 8
    //   298: astore 6
    //   300: aload 8
    //   302: astore 7
    //   304: aload 8
    //   306: iconst_0
    //   307: invokeinterface 779 2 0
    //   312: astore 9
    //   314: aload 8
    //   316: astore 6
    //   318: aload 8
    //   320: astore 7
    //   322: aload 8
    //   324: invokeinterface 216 1 0
    //   329: aload 9
    //   331: astore 7
    //   333: aload 8
    //   335: astore 6
    //   337: aload_1
    //   338: astore 8
    //   340: goto -199 -> 141
    //   343: aload 9
    //   345: astore_1
    //   346: aload 9
    //   348: astore 6
    //   350: aload 9
    //   352: iconst_0
    //   353: invokeinterface 796 2 0
    //   358: invokestatic 802	com/google/android/gms/internal/zzarc:zzbd	([B)Lcom/google/android/gms/internal/zzarc;
    //   361: astore 12
    //   363: aload 9
    //   365: astore_1
    //   366: aload 9
    //   368: astore 6
    //   370: new 434	com/google/android/gms/internal/zzvm$zze
    //   373: dup
    //   374: invokespecial 803	com/google/android/gms/internal/zzvm$zze:<init>	()V
    //   377: astore 11
    //   379: aload 9
    //   381: astore_1
    //   382: aload 9
    //   384: astore 6
    //   386: aload 11
    //   388: aload 12
    //   390: invokevirtual 806	com/google/android/gms/internal/zzvm$zze:zzb	(Lcom/google/android/gms/internal/zzarc;)Lcom/google/android/gms/internal/zzark;
    //   393: checkcast 434	com/google/android/gms/internal/zzvm$zze
    //   396: astore 12
    //   398: aload 9
    //   400: astore_1
    //   401: aload 9
    //   403: astore 6
    //   405: aload 9
    //   407: invokeinterface 809 1 0
    //   412: ifeq +23 -> 435
    //   415: aload 9
    //   417: astore_1
    //   418: aload 9
    //   420: astore 6
    //   422: aload_0
    //   423: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   426: invokevirtual 269	com/google/android/gms/measurement/internal/zzp:zzbwe	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   429: ldc_w 811
    //   432: invokevirtual 275	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
    //   435: aload 9
    //   437: astore_1
    //   438: aload 9
    //   440: astore 6
    //   442: aload 9
    //   444: invokeinterface 216 1 0
    //   449: aload 9
    //   451: astore_1
    //   452: aload 9
    //   454: astore 6
    //   456: aload 4
    //   458: aload 11
    //   460: invokeinterface 814 2 0
    //   465: aload 9
    //   467: astore_1
    //   468: aload 9
    //   470: astore 6
    //   472: aload 10
    //   474: ldc_w 722
    //   477: iconst_4
    //   478: anewarray 322	java/lang/String
    //   481: dup
    //   482: iconst_0
    //   483: ldc_w 783
    //   486: aastore
    //   487: dup
    //   488: iconst_1
    //   489: ldc_w 715
    //   492: aastore
    //   493: dup
    //   494: iconst_2
    //   495: ldc_w 717
    //   498: aastore
    //   499: dup
    //   500: iconst_3
    //   501: ldc_w 360
    //   504: aastore
    //   505: ldc_w 781
    //   508: iconst_2
    //   509: anewarray 322	java/lang/String
    //   512: dup
    //   513: iconst_0
    //   514: aload 8
    //   516: aastore
    //   517: dup
    //   518: iconst_1
    //   519: aload 7
    //   521: aastore
    //   522: aconst_null
    //   523: aconst_null
    //   524: ldc_w 783
    //   527: aconst_null
    //   528: invokevirtual 788	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   531: astore 7
    //   533: aload 7
    //   535: astore_1
    //   536: aload_1
    //   537: astore 6
    //   539: aload_1
    //   540: astore 7
    //   542: aload_1
    //   543: invokeinterface 209 1 0
    //   548: ifne +72 -> 620
    //   551: aload_1
    //   552: astore 6
    //   554: aload_1
    //   555: astore 7
    //   557: aload_0
    //   558: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   561: invokevirtual 269	com/google/android/gms/measurement/internal/zzp:zzbwe	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   564: ldc_w 816
    //   567: invokevirtual 275	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
    //   570: aload_1
    //   571: ifnull -480 -> 91
    //   574: aload_1
    //   575: invokeinterface 216 1 0
    //   580: return
    //   581: astore 4
    //   583: aload 9
    //   585: astore_1
    //   586: aload 9
    //   588: astore 6
    //   590: aload_0
    //   591: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   594: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   597: ldc_w 818
    //   600: aload 8
    //   602: aload 4
    //   604: invokevirtual 234	com/google/android/gms/measurement/internal/zzp$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   607: aload 9
    //   609: ifnull -518 -> 91
    //   612: aload 9
    //   614: invokeinterface 216 1 0
    //   619: return
    //   620: aload_1
    //   621: astore 6
    //   623: aload_1
    //   624: astore 7
    //   626: aload_1
    //   627: iconst_0
    //   628: invokeinterface 213 2 0
    //   633: lstore_2
    //   634: aload_1
    //   635: astore 6
    //   637: aload_1
    //   638: astore 7
    //   640: aload_1
    //   641: iconst_3
    //   642: invokeinterface 796 2 0
    //   647: invokestatic 802	com/google/android/gms/internal/zzarc:zzbd	([B)Lcom/google/android/gms/internal/zzarc;
    //   650: astore 10
    //   652: aload_1
    //   653: astore 6
    //   655: aload_1
    //   656: astore 7
    //   658: new 661	com/google/android/gms/internal/zzvm$zzb
    //   661: dup
    //   662: invokespecial 662	com/google/android/gms/internal/zzvm$zzb:<init>	()V
    //   665: astore 9
    //   667: aload_1
    //   668: astore 6
    //   670: aload_1
    //   671: astore 7
    //   673: aload 9
    //   675: aload 10
    //   677: invokevirtual 819	com/google/android/gms/internal/zzvm$zzb:zzb	(Lcom/google/android/gms/internal/zzarc;)Lcom/google/android/gms/internal/zzark;
    //   680: checkcast 661	com/google/android/gms/internal/zzvm$zzb
    //   683: astore 10
    //   685: aload_1
    //   686: astore 6
    //   688: aload_1
    //   689: astore 7
    //   691: aload 9
    //   693: aload_1
    //   694: iconst_1
    //   695: invokeinterface 779 2 0
    //   700: putfield 820	com/google/android/gms/internal/zzvm$zzb:name	Ljava/lang/String;
    //   703: aload_1
    //   704: astore 6
    //   706: aload_1
    //   707: astore 7
    //   709: aload 9
    //   711: aload_1
    //   712: iconst_2
    //   713: invokeinterface 213 2 0
    //   718: invokestatic 455	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   721: putfield 823	com/google/android/gms/internal/zzvm$zzb:atp	Ljava/lang/Long;
    //   724: aload_1
    //   725: astore 6
    //   727: aload_1
    //   728: astore 7
    //   730: aload 4
    //   732: lload_2
    //   733: aload 9
    //   735: invokeinterface 826 4 0
    //   740: istore 5
    //   742: iload 5
    //   744: ifne +39 -> 783
    //   747: aload_1
    //   748: ifnull -657 -> 91
    //   751: aload_1
    //   752: invokeinterface 216 1 0
    //   757: return
    //   758: astore 9
    //   760: aload_1
    //   761: astore 6
    //   763: aload_1
    //   764: astore 7
    //   766: aload_0
    //   767: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   770: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   773: ldc_w 828
    //   776: aload 8
    //   778: aload 9
    //   780: invokevirtual 234	com/google/android/gms/measurement/internal/zzp$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   783: aload_1
    //   784: astore 6
    //   786: aload_1
    //   787: astore 7
    //   789: aload_1
    //   790: invokeinterface 809 1 0
    //   795: istore 5
    //   797: iload 5
    //   799: ifne -179 -> 620
    //   802: aload_1
    //   803: ifnull -712 -> 91
    //   806: aload_1
    //   807: invokeinterface 216 1 0
    //   812: return
    //   813: astore_1
    //   814: aload 6
    //   816: astore 7
    //   818: aload_0
    //   819: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   822: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   825: ldc_w 830
    //   828: aload_1
    //   829: invokevirtual 379	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   832: aload 6
    //   834: ifnull -743 -> 91
    //   837: aload 6
    //   839: invokeinterface 216 1 0
    //   844: return
    //   845: astore 4
    //   847: aconst_null
    //   848: astore_1
    //   849: aload_1
    //   850: ifnull +9 -> 859
    //   853: aload_1
    //   854: invokeinterface 216 1 0
    //   859: aload 4
    //   861: athrow
    //   862: astore 4
    //   864: aload 7
    //   866: astore_1
    //   867: goto -18 -> 849
    //   870: astore 4
    //   872: goto -23 -> 849
    //   875: astore_1
    //   876: goto -62 -> 814
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	879	0	this	zze
    //   0	879	1	paramString	String
    //   0	879	2	paramLong	long
    //   0	879	4	paramzzb	zzb
    //   74	724	5	bool	boolean
    //   19	819	6	localObject1	Object
    //   1	864	7	localObject2	Object
    //   105	672	8	localObject3	Object
    //   120	614	9	localObject4	Object
    //   758	21	9	localIOException	IOException
    //   25	659	10	localObject5	Object
    //   377	82	11	localzze	zzvm.zze
    //   361	36	12	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   386	398	581	java/io/IOException
    //   673	685	758	java/io/IOException
    //   21	27	813	android/database/sqlite/SQLiteException
    //   31	38	813	android/database/sqlite/SQLiteException
    //   42	62	813	android/database/sqlite/SQLiteException
    //   68	76	813	android/database/sqlite/SQLiteException
    //   98	107	813	android/database/sqlite/SQLiteException
    //   113	122	813	android/database/sqlite/SQLiteException
    //   128	134	813	android/database/sqlite/SQLiteException
    //   243	261	813	android/database/sqlite/SQLiteException
    //   269	278	813	android/database/sqlite/SQLiteException
    //   304	314	813	android/database/sqlite/SQLiteException
    //   322	329	813	android/database/sqlite/SQLiteException
    //   542	551	813	android/database/sqlite/SQLiteException
    //   557	570	813	android/database/sqlite/SQLiteException
    //   626	634	813	android/database/sqlite/SQLiteException
    //   640	652	813	android/database/sqlite/SQLiteException
    //   658	667	813	android/database/sqlite/SQLiteException
    //   673	685	813	android/database/sqlite/SQLiteException
    //   691	703	813	android/database/sqlite/SQLiteException
    //   709	724	813	android/database/sqlite/SQLiteException
    //   730	742	813	android/database/sqlite/SQLiteException
    //   766	783	813	android/database/sqlite/SQLiteException
    //   789	797	813	android/database/sqlite/SQLiteException
    //   21	27	845	finally
    //   31	38	845	finally
    //   42	62	845	finally
    //   243	261	845	finally
    //   68	76	862	finally
    //   98	107	862	finally
    //   113	122	862	finally
    //   128	134	862	finally
    //   269	278	862	finally
    //   304	314	862	finally
    //   322	329	862	finally
    //   542	551	862	finally
    //   557	570	862	finally
    //   626	634	862	finally
    //   640	652	862	finally
    //   658	667	862	finally
    //   673	685	862	finally
    //   691	703	862	finally
    //   709	724	862	finally
    //   730	742	862	finally
    //   766	783	862	finally
    //   789	797	862	finally
    //   818	832	862	finally
    //   144	189	870	finally
    //   196	206	870	finally
    //   213	226	870	finally
    //   350	363	870	finally
    //   370	379	870	finally
    //   386	398	870	finally
    //   405	415	870	finally
    //   422	435	870	finally
    //   442	449	870	finally
    //   456	465	870	finally
    //   472	533	870	finally
    //   590	607	870	finally
    //   144	189	875	android/database/sqlite/SQLiteException
    //   196	206	875	android/database/sqlite/SQLiteException
    //   213	226	875	android/database/sqlite/SQLiteException
    //   350	363	875	android/database/sqlite/SQLiteException
    //   370	379	875	android/database/sqlite/SQLiteException
    //   386	398	875	android/database/sqlite/SQLiteException
    //   405	415	875	android/database/sqlite/SQLiteException
    //   422	435	875	android/database/sqlite/SQLiteException
    //   442	449	875	android/database/sqlite/SQLiteException
    //   456	465	875	android/database/sqlite/SQLiteException
    //   472	533	875	android/database/sqlite/SQLiteException
    //   590	607	875	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  public boolean zza(zzak paramzzak)
  {
    zzac.zzy(paramzzak);
    zzyl();
    zzaax();
    if (zzas(paramzzak.zzcpe, paramzzak.mName) == null) {
      if (zzal.zzmx(paramzzak.mName))
      {
        if (zzb("select count(1) from user_attributes where app_id=? and name not like '!_%' escape '!'", new String[] { paramzzak.zzcpe }) < zzbvi().zzbtx()) {}
      }
      else {
        while (zzb("select count(1) from user_attributes where app_id=?", new String[] { paramzzak.zzcpe }) >= zzbvi().zzbty()) {
          return false;
        }
      }
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzak.zzcpe);
    localContentValues.put("name", paramzzak.mName);
    localContentValues.put("set_timestamp", Long.valueOf(paramzzak.asy));
    zza(localContentValues, "value", paramzzak.zzctv);
    try
    {
      if (getWritableDatabase().insertWithOnConflict("user_attributes", null, localContentValues, 5) == -1L) {
        zzbvg().zzbwc().log("Failed to insert/update user property (got -1)");
      }
      return true;
    }
    catch (SQLiteException paramzzak)
    {
      for (;;)
      {
        zzbvg().zzbwc().zzj("Error storing user property", paramzzak);
      }
    }
  }
  
  @WorkerThread
  void zzaa(String paramString, int paramInt)
  {
    zzaax();
    zzyl();
    zzac.zzhz(paramString);
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.delete("property_filters", "app_id=? and audience_id=?", new String[] { paramString, String.valueOf(paramInt) });
    localSQLiteDatabase.delete("event_filters", "app_id=? and audience_id=?", new String[] { paramString, String.valueOf(paramInt) });
  }
  
  String zzabs()
  {
    if (!zzbvi().zzact()) {
      return zzbvi().zzadt();
    }
    if (zzbvi().zzacu()) {
      return zzbvi().zzadt();
    }
    zzbvg().zzbwf().log("Using secondary database");
    return zzbvi().zzadu();
  }
  
  public void zzaf(List<Long> paramList)
  {
    zzac.zzy(paramList);
    zzyl();
    zzaax();
    StringBuilder localStringBuilder = new StringBuilder("rowid in (");
    int i = 0;
    while (i < paramList.size())
    {
      if (i != 0) {
        localStringBuilder.append(",");
      }
      localStringBuilder.append(((Long)paramList.get(i)).longValue());
      i += 1;
    }
    localStringBuilder.append(")");
    i = getWritableDatabase().delete("raw_events", localStringBuilder.toString(), null);
    if (i != paramList.size()) {
      zzbvg().zzbwc().zze("Deleted fewer rows from raw events table than expected", Integer.valueOf(i), Integer.valueOf(paramList.size()));
    }
  }
  
  /* Error */
  @WorkerThread
  public zzi zzaq(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aload_1
    //   4: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_2
    //   9: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 242	com/google/android/gms/measurement/internal/zze:zzyl	()V
    //   17: aload_0
    //   18: invokevirtual 239	com/google/android/gms/measurement/internal/zze:zzaax	()V
    //   21: aload_0
    //   22: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   25: ldc_w 750
    //   28: iconst_3
    //   29: anewarray 322	java/lang/String
    //   32: dup
    //   33: iconst_0
    //   34: ldc_w 735
    //   37: aastore
    //   38: dup
    //   39: iconst_1
    //   40: ldc_w 740
    //   43: aastore
    //   44: dup
    //   45: iconst_2
    //   46: ldc_w 745
    //   49: aastore
    //   50: ldc_w 933
    //   53: iconst_2
    //   54: anewarray 322	java/lang/String
    //   57: dup
    //   58: iconst_0
    //   59: aload_1
    //   60: aastore
    //   61: dup
    //   62: iconst_1
    //   63: aload_2
    //   64: aastore
    //   65: aconst_null
    //   66: aconst_null
    //   67: aconst_null
    //   68: invokevirtual 477	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   71: astore 4
    //   73: aload 4
    //   75: invokeinterface 209 1 0
    //   80: istore_3
    //   81: iload_3
    //   82: ifne +19 -> 101
    //   85: aload 4
    //   87: ifnull +10 -> 97
    //   90: aload 4
    //   92: invokeinterface 216 1 0
    //   97: aconst_null
    //   98: astore_1
    //   99: aload_1
    //   100: areturn
    //   101: new 731	com/google/android/gms/measurement/internal/zzi
    //   104: dup
    //   105: aload_1
    //   106: aload_2
    //   107: aload 4
    //   109: iconst_0
    //   110: invokeinterface 213 2 0
    //   115: aload 4
    //   117: iconst_1
    //   118: invokeinterface 213 2 0
    //   123: aload 4
    //   125: iconst_2
    //   126: invokeinterface 213 2 0
    //   131: invokespecial 936	com/google/android/gms/measurement/internal/zzi:<init>	(Ljava/lang/String;Ljava/lang/String;JJJ)V
    //   134: astore 5
    //   136: aload 4
    //   138: invokeinterface 809 1 0
    //   143: ifeq +16 -> 159
    //   146: aload_0
    //   147: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   150: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   153: ldc_w 938
    //   156: invokevirtual 275	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
    //   159: aload 5
    //   161: astore_1
    //   162: aload 4
    //   164: ifnull -65 -> 99
    //   167: aload 4
    //   169: invokeinterface 216 1 0
    //   174: aload 5
    //   176: areturn
    //   177: astore 5
    //   179: aconst_null
    //   180: astore 4
    //   182: aload_0
    //   183: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   186: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   189: ldc_w 940
    //   192: aload_1
    //   193: aload_2
    //   194: aload 5
    //   196: invokevirtual 944	com/google/android/gms/measurement/internal/zzp$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   199: aload 4
    //   201: ifnull +10 -> 211
    //   204: aload 4
    //   206: invokeinterface 216 1 0
    //   211: aconst_null
    //   212: areturn
    //   213: astore_1
    //   214: aload 5
    //   216: astore_2
    //   217: aload_2
    //   218: ifnull +9 -> 227
    //   221: aload_2
    //   222: invokeinterface 216 1 0
    //   227: aload_1
    //   228: athrow
    //   229: astore_1
    //   230: aload 4
    //   232: astore_2
    //   233: goto -16 -> 217
    //   236: astore_1
    //   237: aload 4
    //   239: astore_2
    //   240: goto -23 -> 217
    //   243: astore 5
    //   245: goto -63 -> 182
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	248	0	this	zze
    //   0	248	1	paramString1	String
    //   0	248	2	paramString2	String
    //   80	2	3	bool	boolean
    //   71	167	4	localCursor	Cursor
    //   1	174	5	localzzi	zzi
    //   177	38	5	localSQLiteException1	SQLiteException
    //   243	1	5	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   21	73	177	android/database/sqlite/SQLiteException
    //   21	73	213	finally
    //   73	81	229	finally
    //   101	159	229	finally
    //   182	199	236	finally
    //   73	81	243	android/database/sqlite/SQLiteException
    //   101	159	243	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  public void zzar(String paramString1, String paramString2)
  {
    zzac.zzhz(paramString1);
    zzac.zzhz(paramString2);
    zzyl();
    zzaax();
    try
    {
      int i = getWritableDatabase().delete("user_attributes", "app_id=? and name=?", new String[] { paramString1, paramString2 });
      zzbvg().zzbwj().zzj("Deleted user attribute rows:", Integer.valueOf(i));
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzbvg().zzbwc().zzd("Error deleting user attribute", paramString1, paramString2, localSQLiteException);
    }
  }
  
  /* Error */
  @WorkerThread
  public zzak zzas(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aload_1
    //   4: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_2
    //   9: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 242	com/google/android/gms/measurement/internal/zze:zzyl	()V
    //   17: aload_0
    //   18: invokevirtual 239	com/google/android/gms/measurement/internal/zze:zzaax	()V
    //   21: aload_0
    //   22: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   25: ldc_w 869
    //   28: iconst_2
    //   29: anewarray 322	java/lang/String
    //   32: dup
    //   33: iconst_0
    //   34: ldc_w 857
    //   37: aastore
    //   38: dup
    //   39: iconst_1
    //   40: ldc_w 861
    //   43: aastore
    //   44: ldc_w 933
    //   47: iconst_2
    //   48: anewarray 322	java/lang/String
    //   51: dup
    //   52: iconst_0
    //   53: aload_1
    //   54: aastore
    //   55: dup
    //   56: iconst_1
    //   57: aload_2
    //   58: aastore
    //   59: aconst_null
    //   60: aconst_null
    //   61: aconst_null
    //   62: invokevirtual 477	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   65: astore 4
    //   67: aload 4
    //   69: invokeinterface 209 1 0
    //   74: istore_3
    //   75: iload_3
    //   76: ifne +19 -> 95
    //   79: aload 4
    //   81: ifnull +10 -> 91
    //   84: aload 4
    //   86: invokeinterface 216 1 0
    //   91: aconst_null
    //   92: astore_1
    //   93: aload_1
    //   94: areturn
    //   95: new 833	com/google/android/gms/measurement/internal/zzak
    //   98: dup
    //   99: aload_1
    //   100: aload_2
    //   101: aload 4
    //   103: iconst_0
    //   104: invokeinterface 213 2 0
    //   109: aload_0
    //   110: aload 4
    //   112: iconst_1
    //   113: invokevirtual 952	com/google/android/gms/measurement/internal/zze:zzb	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   116: invokespecial 955	com/google/android/gms/measurement/internal/zzak:<init>	(Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   119: astore 5
    //   121: aload 4
    //   123: invokeinterface 809 1 0
    //   128: ifeq +16 -> 144
    //   131: aload_0
    //   132: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   135: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   138: ldc_w 957
    //   141: invokevirtual 275	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
    //   144: aload 5
    //   146: astore_1
    //   147: aload 4
    //   149: ifnull -56 -> 93
    //   152: aload 4
    //   154: invokeinterface 216 1 0
    //   159: aload 5
    //   161: areturn
    //   162: astore 5
    //   164: aconst_null
    //   165: astore 4
    //   167: aload_0
    //   168: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   171: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   174: ldc_w 959
    //   177: aload_1
    //   178: aload_2
    //   179: aload 5
    //   181: invokevirtual 944	com/google/android/gms/measurement/internal/zzp$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   184: aload 4
    //   186: ifnull +10 -> 196
    //   189: aload 4
    //   191: invokeinterface 216 1 0
    //   196: aconst_null
    //   197: areturn
    //   198: astore_1
    //   199: aload 5
    //   201: astore_2
    //   202: aload_2
    //   203: ifnull +9 -> 212
    //   206: aload_2
    //   207: invokeinterface 216 1 0
    //   212: aload_1
    //   213: athrow
    //   214: astore_1
    //   215: aload 4
    //   217: astore_2
    //   218: goto -16 -> 202
    //   221: astore_1
    //   222: aload 4
    //   224: astore_2
    //   225: goto -23 -> 202
    //   228: astore 5
    //   230: goto -63 -> 167
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	233	0	this	zze
    //   0	233	1	paramString1	String
    //   0	233	2	paramString2	String
    //   74	2	3	bool	boolean
    //   65	158	4	localCursor	Cursor
    //   1	159	5	localzzak	zzak
    //   162	38	5	localSQLiteException1	SQLiteException
    //   228	1	5	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   21	67	162	android/database/sqlite/SQLiteException
    //   21	67	198	finally
    //   67	75	214	finally
    //   95	144	214	finally
    //   167	184	221	finally
    //   67	75	228	android/database/sqlite/SQLiteException
    //   95	144	228	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  Map<Integer, List<zzvk.zzb>> zzat(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 239	com/google/android/gms/measurement/internal/zze:zzaax	()V
    //   4: aload_0
    //   5: invokevirtual 242	com/google/android/gms/measurement/internal/zze:zzyl	()V
    //   8: aload_1
    //   9: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_2
    //   14: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   17: pop
    //   18: new 26	android/support/v4/util/ArrayMap
    //   21: dup
    //   22: invokespecial 962	android/support/v4/util/ArrayMap:<init>	()V
    //   25: astore 8
    //   27: aload_0
    //   28: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   31: astore 5
    //   33: aload 5
    //   35: ldc_w 365
    //   38: iconst_2
    //   39: anewarray 322	java/lang/String
    //   42: dup
    //   43: iconst_0
    //   44: ldc_w 351
    //   47: aastore
    //   48: dup
    //   49: iconst_1
    //   50: ldc_w 360
    //   53: aastore
    //   54: ldc_w 964
    //   57: iconst_2
    //   58: anewarray 322	java/lang/String
    //   61: dup
    //   62: iconst_0
    //   63: aload_1
    //   64: aastore
    //   65: dup
    //   66: iconst_1
    //   67: aload_2
    //   68: aastore
    //   69: aconst_null
    //   70: aconst_null
    //   71: aconst_null
    //   72: invokevirtual 477	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   75: astore 5
    //   77: aload 5
    //   79: astore_2
    //   80: aload 5
    //   82: invokeinterface 209 1 0
    //   87: ifne +24 -> 111
    //   90: aload 5
    //   92: astore_2
    //   93: invokestatic 969	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   96: astore_1
    //   97: aload 5
    //   99: ifnull +10 -> 109
    //   102: aload 5
    //   104: invokeinterface 216 1 0
    //   109: aload_1
    //   110: areturn
    //   111: aload 5
    //   113: astore_2
    //   114: aload 5
    //   116: iconst_1
    //   117: invokeinterface 796 2 0
    //   122: invokestatic 802	com/google/android/gms/internal/zzarc:zzbd	([B)Lcom/google/android/gms/internal/zzarc;
    //   125: astore 6
    //   127: aload 5
    //   129: astore_2
    //   130: new 282	com/google/android/gms/internal/zzvk$zzb
    //   133: dup
    //   134: invokespecial 970	com/google/android/gms/internal/zzvk$zzb:<init>	()V
    //   137: astore 9
    //   139: aload 5
    //   141: astore_2
    //   142: aload 9
    //   144: aload 6
    //   146: invokevirtual 971	com/google/android/gms/internal/zzvk$zzb:zzb	(Lcom/google/android/gms/internal/zzarc;)Lcom/google/android/gms/internal/zzark;
    //   149: checkcast 282	com/google/android/gms/internal/zzvk$zzb
    //   152: astore 6
    //   154: aload 5
    //   156: astore_2
    //   157: aload 5
    //   159: iconst_0
    //   160: invokeinterface 974 2 0
    //   165: istore_3
    //   166: aload 5
    //   168: astore_2
    //   169: aload 8
    //   171: iload_3
    //   172: invokestatic 320	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   175: invokeinterface 976 2 0
    //   180: checkcast 907	java/util/List
    //   183: astore 7
    //   185: aload 7
    //   187: astore 6
    //   189: aload 7
    //   191: ifnonnull +32 -> 223
    //   194: aload 5
    //   196: astore_2
    //   197: new 978	java/util/ArrayList
    //   200: dup
    //   201: invokespecial 979	java/util/ArrayList:<init>	()V
    //   204: astore 6
    //   206: aload 5
    //   208: astore_2
    //   209: aload 8
    //   211: iload_3
    //   212: invokestatic 320	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   215: aload 6
    //   217: invokeinterface 42 3 0
    //   222: pop
    //   223: aload 5
    //   225: astore_2
    //   226: aload 6
    //   228: aload 9
    //   230: invokeinterface 983 2 0
    //   235: pop
    //   236: aload 5
    //   238: astore_2
    //   239: aload 5
    //   241: invokeinterface 809 1 0
    //   246: istore 4
    //   248: iload 4
    //   250: ifne -139 -> 111
    //   253: aload 5
    //   255: ifnull +10 -> 265
    //   258: aload 5
    //   260: invokeinterface 216 1 0
    //   265: aload 8
    //   267: areturn
    //   268: astore 6
    //   270: aload 5
    //   272: astore_2
    //   273: aload_0
    //   274: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   277: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   280: ldc_w 985
    //   283: aload_1
    //   284: aload 6
    //   286: invokevirtual 234	com/google/android/gms/measurement/internal/zzp$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   289: goto -53 -> 236
    //   292: astore_1
    //   293: aload 5
    //   295: astore_2
    //   296: aload_0
    //   297: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   300: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   303: ldc_w 987
    //   306: aload_1
    //   307: invokevirtual 379	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   310: aload 5
    //   312: ifnull +10 -> 322
    //   315: aload 5
    //   317: invokeinterface 216 1 0
    //   322: aconst_null
    //   323: areturn
    //   324: astore_1
    //   325: aconst_null
    //   326: astore_2
    //   327: aload_2
    //   328: ifnull +9 -> 337
    //   331: aload_2
    //   332: invokeinterface 216 1 0
    //   337: aload_1
    //   338: athrow
    //   339: astore_1
    //   340: goto -13 -> 327
    //   343: astore_1
    //   344: aconst_null
    //   345: astore 5
    //   347: goto -54 -> 293
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	350	0	this	zze
    //   0	350	1	paramString1	String
    //   0	350	2	paramString2	String
    //   165	47	3	i	int
    //   246	3	4	bool	boolean
    //   31	315	5	localObject1	Object
    //   125	102	6	localObject2	Object
    //   268	17	6	localIOException	IOException
    //   183	7	7	localList	List
    //   25	241	8	localArrayMap	ArrayMap
    //   137	92	9	localzzb	zzvk.zzb
    // Exception table:
    //   from	to	target	type
    //   142	154	268	java/io/IOException
    //   80	90	292	android/database/sqlite/SQLiteException
    //   93	97	292	android/database/sqlite/SQLiteException
    //   114	127	292	android/database/sqlite/SQLiteException
    //   130	139	292	android/database/sqlite/SQLiteException
    //   142	154	292	android/database/sqlite/SQLiteException
    //   157	166	292	android/database/sqlite/SQLiteException
    //   169	185	292	android/database/sqlite/SQLiteException
    //   197	206	292	android/database/sqlite/SQLiteException
    //   209	223	292	android/database/sqlite/SQLiteException
    //   226	236	292	android/database/sqlite/SQLiteException
    //   239	248	292	android/database/sqlite/SQLiteException
    //   273	289	292	android/database/sqlite/SQLiteException
    //   33	77	324	finally
    //   80	90	339	finally
    //   93	97	339	finally
    //   114	127	339	finally
    //   130	139	339	finally
    //   142	154	339	finally
    //   157	166	339	finally
    //   169	185	339	finally
    //   197	206	339	finally
    //   209	223	339	finally
    //   226	236	339	finally
    //   239	248	339	finally
    //   273	289	339	finally
    //   296	310	339	finally
    //   33	77	343	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  Map<Integer, List<zzvk.zze>> zzau(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 239	com/google/android/gms/measurement/internal/zze:zzaax	()V
    //   4: aload_0
    //   5: invokevirtual 242	com/google/android/gms/measurement/internal/zze:zzyl	()V
    //   8: aload_1
    //   9: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_2
    //   14: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   17: pop
    //   18: new 26	android/support/v4/util/ArrayMap
    //   21: dup
    //   22: invokespecial 962	android/support/v4/util/ArrayMap:<init>	()V
    //   25: astore 8
    //   27: aload_0
    //   28: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   31: astore 5
    //   33: aload 5
    //   35: ldc_w 392
    //   38: iconst_2
    //   39: anewarray 322	java/lang/String
    //   42: dup
    //   43: iconst_0
    //   44: ldc_w 351
    //   47: aastore
    //   48: dup
    //   49: iconst_1
    //   50: ldc_w 360
    //   53: aastore
    //   54: ldc_w 991
    //   57: iconst_2
    //   58: anewarray 322	java/lang/String
    //   61: dup
    //   62: iconst_0
    //   63: aload_1
    //   64: aastore
    //   65: dup
    //   66: iconst_1
    //   67: aload_2
    //   68: aastore
    //   69: aconst_null
    //   70: aconst_null
    //   71: aconst_null
    //   72: invokevirtual 477	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   75: astore 5
    //   77: aload 5
    //   79: astore_2
    //   80: aload 5
    //   82: invokeinterface 209 1 0
    //   87: ifne +24 -> 111
    //   90: aload 5
    //   92: astore_2
    //   93: invokestatic 969	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   96: astore_1
    //   97: aload 5
    //   99: ifnull +10 -> 109
    //   102: aload 5
    //   104: invokeinterface 216 1 0
    //   109: aload_1
    //   110: areturn
    //   111: aload 5
    //   113: astore_2
    //   114: aload 5
    //   116: iconst_1
    //   117: invokeinterface 796 2 0
    //   122: invokestatic 802	com/google/android/gms/internal/zzarc:zzbd	([B)Lcom/google/android/gms/internal/zzarc;
    //   125: astore 6
    //   127: aload 5
    //   129: astore_2
    //   130: new 289	com/google/android/gms/internal/zzvk$zze
    //   133: dup
    //   134: invokespecial 992	com/google/android/gms/internal/zzvk$zze:<init>	()V
    //   137: astore 9
    //   139: aload 5
    //   141: astore_2
    //   142: aload 9
    //   144: aload 6
    //   146: invokevirtual 993	com/google/android/gms/internal/zzvk$zze:zzb	(Lcom/google/android/gms/internal/zzarc;)Lcom/google/android/gms/internal/zzark;
    //   149: checkcast 289	com/google/android/gms/internal/zzvk$zze
    //   152: astore 6
    //   154: aload 5
    //   156: astore_2
    //   157: aload 5
    //   159: iconst_0
    //   160: invokeinterface 974 2 0
    //   165: istore_3
    //   166: aload 5
    //   168: astore_2
    //   169: aload 8
    //   171: iload_3
    //   172: invokestatic 320	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   175: invokeinterface 976 2 0
    //   180: checkcast 907	java/util/List
    //   183: astore 7
    //   185: aload 7
    //   187: astore 6
    //   189: aload 7
    //   191: ifnonnull +32 -> 223
    //   194: aload 5
    //   196: astore_2
    //   197: new 978	java/util/ArrayList
    //   200: dup
    //   201: invokespecial 979	java/util/ArrayList:<init>	()V
    //   204: astore 6
    //   206: aload 5
    //   208: astore_2
    //   209: aload 8
    //   211: iload_3
    //   212: invokestatic 320	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   215: aload 6
    //   217: invokeinterface 42 3 0
    //   222: pop
    //   223: aload 5
    //   225: astore_2
    //   226: aload 6
    //   228: aload 9
    //   230: invokeinterface 983 2 0
    //   235: pop
    //   236: aload 5
    //   238: astore_2
    //   239: aload 5
    //   241: invokeinterface 809 1 0
    //   246: istore 4
    //   248: iload 4
    //   250: ifne -139 -> 111
    //   253: aload 5
    //   255: ifnull +10 -> 265
    //   258: aload 5
    //   260: invokeinterface 216 1 0
    //   265: aload 8
    //   267: areturn
    //   268: astore 6
    //   270: aload 5
    //   272: astore_2
    //   273: aload_0
    //   274: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   277: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   280: ldc_w 985
    //   283: aload_1
    //   284: aload 6
    //   286: invokevirtual 234	com/google/android/gms/measurement/internal/zzp$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   289: goto -53 -> 236
    //   292: astore_1
    //   293: aload 5
    //   295: astore_2
    //   296: aload_0
    //   297: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   300: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   303: ldc_w 987
    //   306: aload_1
    //   307: invokevirtual 379	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   310: aload 5
    //   312: ifnull +10 -> 322
    //   315: aload 5
    //   317: invokeinterface 216 1 0
    //   322: aconst_null
    //   323: areturn
    //   324: astore_1
    //   325: aconst_null
    //   326: astore_2
    //   327: aload_2
    //   328: ifnull +9 -> 337
    //   331: aload_2
    //   332: invokeinterface 216 1 0
    //   337: aload_1
    //   338: athrow
    //   339: astore_1
    //   340: goto -13 -> 327
    //   343: astore_1
    //   344: aconst_null
    //   345: astore 5
    //   347: goto -54 -> 293
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	350	0	this	zze
    //   0	350	1	paramString1	String
    //   0	350	2	paramString2	String
    //   165	47	3	i	int
    //   246	3	4	bool	boolean
    //   31	315	5	localObject1	Object
    //   125	102	6	localObject2	Object
    //   268	17	6	localIOException	IOException
    //   183	7	7	localList	List
    //   25	241	8	localArrayMap	ArrayMap
    //   137	92	9	localzze	zzvk.zze
    // Exception table:
    //   from	to	target	type
    //   142	154	268	java/io/IOException
    //   80	90	292	android/database/sqlite/SQLiteException
    //   93	97	292	android/database/sqlite/SQLiteException
    //   114	127	292	android/database/sqlite/SQLiteException
    //   130	139	292	android/database/sqlite/SQLiteException
    //   142	154	292	android/database/sqlite/SQLiteException
    //   157	166	292	android/database/sqlite/SQLiteException
    //   169	185	292	android/database/sqlite/SQLiteException
    //   197	206	292	android/database/sqlite/SQLiteException
    //   209	223	292	android/database/sqlite/SQLiteException
    //   226	236	292	android/database/sqlite/SQLiteException
    //   239	248	292	android/database/sqlite/SQLiteException
    //   273	289	292	android/database/sqlite/SQLiteException
    //   33	77	324	finally
    //   80	90	339	finally
    //   93	97	339	finally
    //   114	127	339	finally
    //   130	139	339	finally
    //   142	154	339	finally
    //   157	166	339	finally
    //   169	185	339	finally
    //   197	206	339	finally
    //   209	223	339	finally
    //   226	236	339	finally
    //   239	248	339	finally
    //   273	289	339	finally
    //   296	310	339	finally
    //   33	77	343	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  Object zzb(Cursor paramCursor, int paramInt)
  {
    int i = zza(paramCursor, paramInt);
    switch (i)
    {
    default: 
      zzbvg().zzbwc().zzj("Loaded invalid unknown value type, ignoring it", Integer.valueOf(i));
      return null;
    case 0: 
      zzbvg().zzbwc().log("Loaded invalid null value from database");
      return null;
    case 1: 
      return Long.valueOf(paramCursor.getLong(paramInt));
    case 2: 
      return Double.valueOf(paramCursor.getDouble(paramInt));
    case 3: 
      return paramCursor.getString(paramInt);
    }
    zzbvg().zzbwc().log("Loaded invalid blob type value, ignoring it");
    return null;
  }
  
  @WorkerThread
  void zzb(String paramString, zzvk.zza[] paramArrayOfzza)
  {
    int j = 0;
    zzaax();
    zzyl();
    zzac.zzhz(paramString);
    zzac.zzy(paramArrayOfzza);
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    try
    {
      zzmc(paramString);
      int k = paramArrayOfzza.length;
      int i = 0;
      while (i < k)
      {
        zza(paramString, paramArrayOfzza[i]);
        i += 1;
      }
      ArrayList localArrayList = new ArrayList();
      k = paramArrayOfzza.length;
      i = j;
      while (i < k)
      {
        localArrayList.add(paramArrayOfzza[i].asA);
        i += 1;
      }
      zzc(paramString, localArrayList);
      localSQLiteDatabase.setTransactionSuccessful();
      return;
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  @WorkerThread
  public void zzbk(long paramLong)
  {
    zzyl();
    zzaax();
    if (getWritableDatabase().delete("queue", "rowid=?", new String[] { String.valueOf(paramLong) }) != 1) {
      zzbvg().zzbwc().log("Deleted fewer rows from queue than expected");
    }
  }
  
  /* Error */
  public String zzbl(long paramLong)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aload_0
    //   4: invokevirtual 242	com/google/android/gms/measurement/internal/zze:zzyl	()V
    //   7: aload_0
    //   8: invokevirtual 239	com/google/android/gms/measurement/internal/zze:zzaax	()V
    //   11: aload_0
    //   12: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   15: ldc_w 1027
    //   18: iconst_1
    //   19: anewarray 322	java/lang/String
    //   22: dup
    //   23: iconst_0
    //   24: lload_1
    //   25: invokestatic 775	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   28: aastore
    //   29: invokevirtual 205	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   32: astore_3
    //   33: aload_3
    //   34: astore 4
    //   36: aload_3
    //   37: invokeinterface 209 1 0
    //   42: ifne +40 -> 82
    //   45: aload_3
    //   46: astore 4
    //   48: aload_0
    //   49: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   52: invokevirtual 546	com/google/android/gms/measurement/internal/zzp:zzbwj	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   55: ldc_w 1029
    //   58: invokevirtual 275	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
    //   61: aload 6
    //   63: astore 4
    //   65: aload_3
    //   66: ifnull +13 -> 79
    //   69: aload_3
    //   70: invokeinterface 216 1 0
    //   75: aload 6
    //   77: astore 4
    //   79: aload 4
    //   81: areturn
    //   82: aload_3
    //   83: astore 4
    //   85: aload_3
    //   86: iconst_0
    //   87: invokeinterface 779 2 0
    //   92: astore 5
    //   94: aload 5
    //   96: astore 4
    //   98: aload_3
    //   99: ifnull -20 -> 79
    //   102: aload_3
    //   103: invokeinterface 216 1 0
    //   108: aload 5
    //   110: areturn
    //   111: astore 5
    //   113: aconst_null
    //   114: astore_3
    //   115: aload_3
    //   116: astore 4
    //   118: aload_0
    //   119: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   122: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   125: ldc_w 1031
    //   128: aload 5
    //   130: invokevirtual 379	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   133: aload 6
    //   135: astore 4
    //   137: aload_3
    //   138: ifnull -59 -> 79
    //   141: aload_3
    //   142: invokeinterface 216 1 0
    //   147: aconst_null
    //   148: areturn
    //   149: astore_3
    //   150: aconst_null
    //   151: astore 4
    //   153: aload 4
    //   155: ifnull +10 -> 165
    //   158: aload 4
    //   160: invokeinterface 216 1 0
    //   165: aload_3
    //   166: athrow
    //   167: astore_3
    //   168: goto -15 -> 153
    //   171: astore 5
    //   173: goto -58 -> 115
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	176	0	this	zze
    //   0	176	1	paramLong	long
    //   32	110	3	localCursor	Cursor
    //   149	17	3	localObject1	Object
    //   167	1	3	localObject2	Object
    //   34	125	4	localObject3	Object
    //   92	17	5	str	String
    //   111	18	5	localSQLiteException1	SQLiteException
    //   171	1	5	localSQLiteException2	SQLiteException
    //   1	133	6	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   11	33	111	android/database/sqlite/SQLiteException
    //   11	33	149	finally
    //   36	45	167	finally
    //   48	61	167	finally
    //   85	94	167	finally
    //   118	133	167	finally
    //   36	45	171	android/database/sqlite/SQLiteException
    //   48	61	171	android/database/sqlite/SQLiteException
    //   85	94	171	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  @WorkerThread
  public String zzbvj()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   7: astore_1
    //   8: aload_1
    //   9: ldc_w 1034
    //   12: aconst_null
    //   13: invokevirtual 205	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   16: astore_1
    //   17: aload_1
    //   18: astore_2
    //   19: aload_1
    //   20: invokeinterface 209 1 0
    //   25: ifeq +29 -> 54
    //   28: aload_1
    //   29: astore_2
    //   30: aload_1
    //   31: iconst_0
    //   32: invokeinterface 779 2 0
    //   37: astore_3
    //   38: aload_3
    //   39: astore_2
    //   40: aload_1
    //   41: ifnull +11 -> 52
    //   44: aload_1
    //   45: invokeinterface 216 1 0
    //   50: aload_3
    //   51: astore_2
    //   52: aload_2
    //   53: areturn
    //   54: aload 4
    //   56: astore_2
    //   57: aload_1
    //   58: ifnull -6 -> 52
    //   61: aload_1
    //   62: invokeinterface 216 1 0
    //   67: aconst_null
    //   68: areturn
    //   69: astore_3
    //   70: aconst_null
    //   71: astore_1
    //   72: aload_1
    //   73: astore_2
    //   74: aload_0
    //   75: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   78: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   81: ldc_w 1036
    //   84: aload_3
    //   85: invokevirtual 379	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   88: aload 4
    //   90: astore_2
    //   91: aload_1
    //   92: ifnull -40 -> 52
    //   95: aload_1
    //   96: invokeinterface 216 1 0
    //   101: aconst_null
    //   102: areturn
    //   103: astore_1
    //   104: aconst_null
    //   105: astore_2
    //   106: aload_2
    //   107: ifnull +9 -> 116
    //   110: aload_2
    //   111: invokeinterface 216 1 0
    //   116: aload_1
    //   117: athrow
    //   118: astore_1
    //   119: goto -13 -> 106
    //   122: astore_3
    //   123: goto -51 -> 72
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	this	zze
    //   7	89	1	localObject1	Object
    //   103	14	1	localObject2	Object
    //   118	1	1	localObject3	Object
    //   18	93	2	localObject4	Object
    //   37	14	3	str	String
    //   69	16	3	localSQLiteException1	SQLiteException
    //   122	1	3	localSQLiteException2	SQLiteException
    //   1	88	4	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   8	17	69	android/database/sqlite/SQLiteException
    //   8	17	103	finally
    //   19	28	118	finally
    //   30	38	118	finally
    //   74	88	118	finally
    //   19	28	122	android/database/sqlite/SQLiteException
    //   30	38	122	android/database/sqlite/SQLiteException
  }
  
  public boolean zzbvk()
  {
    return zzb("select count(1) > 0 from queue where has_realtime = 1", null) != 0L;
  }
  
  @WorkerThread
  void zzbvl()
  {
    zzyl();
    zzaax();
    if (!zzbvr()) {}
    long l1;
    long l2;
    do
    {
      return;
      l1 = zzbvh().apT.get();
      l2 = zzaan().elapsedRealtime();
    } while (Math.abs(l2 - l1) <= zzbvi().zzbuf());
    zzbvh().apT.set(l2);
    zzbvm();
  }
  
  @WorkerThread
  void zzbvm()
  {
    zzyl();
    zzaax();
    if (!zzbvr()) {}
    int i;
    do
    {
      return;
      i = getWritableDatabase().delete("queue", "abs(bundle_end_timestamp - ?) > cast(? as integer)", new String[] { String.valueOf(zzaan().currentTimeMillis()), String.valueOf(zzbvi().zzbue()) });
    } while (i <= 0);
    zzbvg().zzbwj().zzj("Deleted stale rows. rowsDeleted", Integer.valueOf(i));
  }
  
  @WorkerThread
  public long zzbvn()
  {
    return zza("select max(bundle_end_timestamp) from queue", null, 0L);
  }
  
  @WorkerThread
  public long zzbvo()
  {
    return zza("select max(timestamp) from raw_events", null, 0L);
  }
  
  public boolean zzbvp()
  {
    return zzb("select count(1) > 0 from raw_events", null) != 0L;
  }
  
  public boolean zzbvq()
  {
    return zzb("select count(1) > 0 from raw_events where realtime = 1", null) != 0L;
  }
  
  boolean zzc(String paramString, List<Integer> paramList)
  {
    zzac.zzhz(paramString);
    zzaax();
    zzyl();
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    int j;
    label147:
    do
    {
      try
      {
        long l = zzb("select count(1) from audience_filter_values where app_id=?", new String[] { paramString });
        j = zzbvi().zzlt(paramString);
        if (l <= j) {
          return false;
        }
      }
      catch (SQLiteException paramString)
      {
        zzbvg().zzbwc().zzj("Database error querying filters", paramString);
        return false;
      }
      ArrayList localArrayList = new ArrayList();
      if (paramList != null)
      {
        int i = 0;
        for (;;)
        {
          if (i >= paramList.size()) {
            break label147;
          }
          Integer localInteger = (Integer)paramList.get(i);
          if ((localInteger == null) || (!(localInteger instanceof Integer))) {
            break;
          }
          localArrayList.add(Integer.toString(localInteger.intValue()));
          i += 1;
        }
      }
      paramList = String.valueOf(TextUtils.join(",", localArrayList));
      paramList = String.valueOf(paramList).length() + 2 + "(" + paramList + ")";
    } while (localSQLiteDatabase.delete("audience_filter_values", String.valueOf(paramList).length() + 140 + "audience_id in (select audience_id from audience_filter_values where app_id=? and audience_id not in " + paramList + " order by rowid desc limit -1 offset ?)", new String[] { paramString, Integer.toString(j) }) <= 0);
    return true;
  }
  
  @WorkerThread
  public void zzd(String paramString, byte[] paramArrayOfByte)
  {
    zzac.zzhz(paramString);
    zzyl();
    zzaax();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("remote_config", paramArrayOfByte);
    try
    {
      if (getWritableDatabase().update("apps", localContentValues, "app_id = ?", new String[] { paramString }) == 0L) {
        zzbvg().zzbwc().log("Failed to update remote config (got 0)");
      }
      return;
    }
    catch (SQLiteException paramString)
    {
      zzbvg().zzbwc().zzj("Error storing remote config", paramString);
    }
  }
  
  @WorkerThread
  protected void zzg(String paramString, long paramLong)
  {
    zzac.zzhz(paramString);
    zzyl();
    zzaax();
    if (paramLong <= 0L) {
      zzbvg().zzbwc().log("Nonpositive first open count received, ignoring");
    }
    for (;;)
    {
      return;
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("app_id", paramString);
      localContentValues.put("first_open_count", Long.valueOf(paramLong));
      try
      {
        if (getWritableDatabase().insertWithOnConflict("app2", null, localContentValues, 5) == -1L)
        {
          zzbvg().zzbwc().log("Failed to insert/replace first open count (got -1)");
          return;
        }
      }
      catch (SQLiteException paramString)
      {
        zzbvg().zzbwc().zzj("Error inserting/replacing first open count", paramString);
      }
    }
  }
  
  /* Error */
  @WorkerThread
  public List<zzak> zzly(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aload_1
    //   4: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_0
    //   9: invokevirtual 242	com/google/android/gms/measurement/internal/zze:zzyl	()V
    //   12: aload_0
    //   13: invokevirtual 239	com/google/android/gms/measurement/internal/zze:zzaax	()V
    //   16: new 978	java/util/ArrayList
    //   19: dup
    //   20: invokespecial 979	java/util/ArrayList:<init>	()V
    //   23: astore 8
    //   25: aload_0
    //   26: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   29: astore 6
    //   31: aload_0
    //   32: invokevirtual 533	com/google/android/gms/measurement/internal/zze:zzbvi	()Lcom/google/android/gms/measurement/internal/zzd;
    //   35: invokevirtual 855	com/google/android/gms/measurement/internal/zzd:zzbty	()I
    //   38: istore_2
    //   39: aload 6
    //   41: ldc_w 869
    //   44: iconst_3
    //   45: anewarray 322	java/lang/String
    //   48: dup
    //   49: iconst_0
    //   50: ldc_w 715
    //   53: aastore
    //   54: dup
    //   55: iconst_1
    //   56: ldc_w 857
    //   59: aastore
    //   60: dup
    //   61: iconst_2
    //   62: ldc_w 861
    //   65: aastore
    //   66: ldc_w 473
    //   69: iconst_1
    //   70: anewarray 322	java/lang/String
    //   73: dup
    //   74: iconst_0
    //   75: aload_1
    //   76: aastore
    //   77: aconst_null
    //   78: aconst_null
    //   79: ldc_w 783
    //   82: iload_2
    //   83: invokestatic 877	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   86: invokevirtual 788	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   89: astore 6
    //   91: aload 6
    //   93: invokeinterface 209 1 0
    //   98: istore_3
    //   99: iload_3
    //   100: ifne +18 -> 118
    //   103: aload 6
    //   105: ifnull +10 -> 115
    //   108: aload 6
    //   110: invokeinterface 216 1 0
    //   115: aload 8
    //   117: areturn
    //   118: aload 6
    //   120: iconst_0
    //   121: invokeinterface 779 2 0
    //   126: astore 7
    //   128: aload 6
    //   130: iconst_1
    //   131: invokeinterface 213 2 0
    //   136: lstore 4
    //   138: aload_0
    //   139: aload 6
    //   141: iconst_2
    //   142: invokevirtual 952	com/google/android/gms/measurement/internal/zze:zzb	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   145: astore 9
    //   147: aload 9
    //   149: ifnonnull +43 -> 192
    //   152: aload_0
    //   153: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   156: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   159: ldc_w 1136
    //   162: invokevirtual 275	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
    //   165: aload 6
    //   167: invokeinterface 809 1 0
    //   172: istore_3
    //   173: iload_3
    //   174: ifne -56 -> 118
    //   177: aload 6
    //   179: ifnull +10 -> 189
    //   182: aload 6
    //   184: invokeinterface 216 1 0
    //   189: aload 8
    //   191: areturn
    //   192: aload 8
    //   194: new 833	com/google/android/gms/measurement/internal/zzak
    //   197: dup
    //   198: aload_1
    //   199: aload 7
    //   201: lload 4
    //   203: aload 9
    //   205: invokespecial 955	com/google/android/gms/measurement/internal/zzak:<init>	(Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   208: invokeinterface 983 2 0
    //   213: pop
    //   214: goto -49 -> 165
    //   217: astore 7
    //   219: aload_0
    //   220: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   223: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   226: ldc_w 1138
    //   229: aload_1
    //   230: aload 7
    //   232: invokevirtual 234	com/google/android/gms/measurement/internal/zzp$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   235: aload 6
    //   237: ifnull +10 -> 247
    //   240: aload 6
    //   242: invokeinterface 216 1 0
    //   247: aconst_null
    //   248: areturn
    //   249: astore_1
    //   250: aload 7
    //   252: astore 6
    //   254: aload 6
    //   256: ifnull +10 -> 266
    //   259: aload 6
    //   261: invokeinterface 216 1 0
    //   266: aload_1
    //   267: athrow
    //   268: astore_1
    //   269: goto -15 -> 254
    //   272: astore_1
    //   273: goto -19 -> 254
    //   276: astore 7
    //   278: aconst_null
    //   279: astore 6
    //   281: goto -62 -> 219
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	284	0	this	zze
    //   0	284	1	paramString	String
    //   38	45	2	i	int
    //   98	76	3	bool	boolean
    //   136	66	4	l	long
    //   29	251	6	localObject1	Object
    //   1	199	7	str	String
    //   217	34	7	localSQLiteException1	SQLiteException
    //   276	1	7	localSQLiteException2	SQLiteException
    //   23	170	8	localArrayList	ArrayList
    //   145	59	9	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   91	99	217	android/database/sqlite/SQLiteException
    //   118	147	217	android/database/sqlite/SQLiteException
    //   152	165	217	android/database/sqlite/SQLiteException
    //   165	173	217	android/database/sqlite/SQLiteException
    //   192	214	217	android/database/sqlite/SQLiteException
    //   25	91	249	finally
    //   91	99	268	finally
    //   118	147	268	finally
    //   152	165	268	finally
    //   165	173	268	finally
    //   192	214	268	finally
    //   219	235	272	finally
    //   25	91	276	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  @WorkerThread
  public zza zzlz(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 242	com/google/android/gms/measurement/internal/zze:zzyl	()V
    //   9: aload_0
    //   10: invokevirtual 239	com/google/android/gms/measurement/internal/zze:zzaax	()V
    //   13: aload_0
    //   14: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: ldc_w 471
    //   20: bipush 21
    //   22: anewarray 322	java/lang/String
    //   25: dup
    //   26: iconst_0
    //   27: ldc_w 570
    //   30: aastore
    //   31: dup
    //   32: iconst_1
    //   33: ldc_w 575
    //   36: aastore
    //   37: dup
    //   38: iconst_2
    //   39: ldc_w 580
    //   42: aastore
    //   43: dup
    //   44: iconst_3
    //   45: ldc_w 585
    //   48: aastore
    //   49: dup
    //   50: iconst_4
    //   51: ldc 60
    //   53: aastore
    //   54: dup
    //   55: iconst_5
    //   56: ldc_w 593
    //   59: aastore
    //   60: dup
    //   61: bipush 6
    //   63: ldc 34
    //   65: aastore
    //   66: dup
    //   67: bipush 7
    //   69: ldc 44
    //   71: aastore
    //   72: dup
    //   73: bipush 8
    //   75: ldc 48
    //   77: aastore
    //   78: dup
    //   79: bipush 9
    //   81: ldc 52
    //   83: aastore
    //   84: dup
    //   85: bipush 10
    //   87: ldc 56
    //   89: aastore
    //   90: dup
    //   91: bipush 11
    //   93: ldc 64
    //   95: aastore
    //   96: dup
    //   97: bipush 12
    //   99: ldc 68
    //   101: aastore
    //   102: dup
    //   103: bipush 13
    //   105: ldc 72
    //   107: aastore
    //   108: dup
    //   109: bipush 14
    //   111: ldc 76
    //   113: aastore
    //   114: dup
    //   115: bipush 15
    //   117: ldc 84
    //   119: aastore
    //   120: dup
    //   121: bipush 16
    //   123: ldc 88
    //   125: aastore
    //   126: dup
    //   127: bipush 17
    //   129: ldc 92
    //   131: aastore
    //   132: dup
    //   133: bipush 18
    //   135: ldc 96
    //   137: aastore
    //   138: dup
    //   139: bipush 19
    //   141: ldc 100
    //   143: aastore
    //   144: dup
    //   145: bipush 20
    //   147: ldc 104
    //   149: aastore
    //   150: ldc_w 473
    //   153: iconst_1
    //   154: anewarray 322	java/lang/String
    //   157: dup
    //   158: iconst_0
    //   159: aload_1
    //   160: aastore
    //   161: aconst_null
    //   162: aconst_null
    //   163: aconst_null
    //   164: invokevirtual 477	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   167: astore 7
    //   169: aload 7
    //   171: astore 6
    //   173: aload 7
    //   175: invokeinterface 209 1 0
    //   180: istore_3
    //   181: iload_3
    //   182: ifne +19 -> 201
    //   185: aload 7
    //   187: ifnull +10 -> 197
    //   190: aload 7
    //   192: invokeinterface 216 1 0
    //   197: aconst_null
    //   198: astore_1
    //   199: aload_1
    //   200: areturn
    //   201: aload 7
    //   203: astore 6
    //   205: new 565	com/google/android/gms/measurement/internal/zza
    //   208: dup
    //   209: aload_0
    //   210: getfield 1145	com/google/android/gms/measurement/internal/zze:anq	Lcom/google/android/gms/measurement/internal/zzx;
    //   213: aload_1
    //   214: invokespecial 1148	com/google/android/gms/measurement/internal/zza:<init>	(Lcom/google/android/gms/measurement/internal/zzx;Ljava/lang/String;)V
    //   217: astore 8
    //   219: aload 7
    //   221: astore 6
    //   223: aload 8
    //   225: aload 7
    //   227: iconst_0
    //   228: invokeinterface 779 2 0
    //   233: invokevirtual 1151	com/google/android/gms/measurement/internal/zza:zzlj	(Ljava/lang/String;)V
    //   236: aload 7
    //   238: astore 6
    //   240: aload 8
    //   242: aload 7
    //   244: iconst_1
    //   245: invokeinterface 779 2 0
    //   250: invokevirtual 1154	com/google/android/gms/measurement/internal/zza:zzlk	(Ljava/lang/String;)V
    //   253: aload 7
    //   255: astore 6
    //   257: aload 8
    //   259: aload 7
    //   261: iconst_2
    //   262: invokeinterface 779 2 0
    //   267: invokevirtual 1157	com/google/android/gms/measurement/internal/zza:zzll	(Ljava/lang/String;)V
    //   270: aload 7
    //   272: astore 6
    //   274: aload 8
    //   276: aload 7
    //   278: iconst_3
    //   279: invokeinterface 213 2 0
    //   284: invokevirtual 1160	com/google/android/gms/measurement/internal/zza:zzbb	(J)V
    //   287: aload 7
    //   289: astore 6
    //   291: aload 8
    //   293: aload 7
    //   295: iconst_4
    //   296: invokeinterface 213 2 0
    //   301: invokevirtual 1163	com/google/android/gms/measurement/internal/zza:zzaw	(J)V
    //   304: aload 7
    //   306: astore 6
    //   308: aload 8
    //   310: aload 7
    //   312: iconst_5
    //   313: invokeinterface 213 2 0
    //   318: invokevirtual 1166	com/google/android/gms/measurement/internal/zza:zzax	(J)V
    //   321: aload 7
    //   323: astore 6
    //   325: aload 8
    //   327: aload 7
    //   329: bipush 6
    //   331: invokeinterface 779 2 0
    //   336: invokevirtual 1169	com/google/android/gms/measurement/internal/zza:setAppVersion	(Ljava/lang/String;)V
    //   339: aload 7
    //   341: astore 6
    //   343: aload 8
    //   345: aload 7
    //   347: bipush 7
    //   349: invokeinterface 779 2 0
    //   354: invokevirtual 1172	com/google/android/gms/measurement/internal/zza:zzln	(Ljava/lang/String;)V
    //   357: aload 7
    //   359: astore 6
    //   361: aload 8
    //   363: aload 7
    //   365: bipush 8
    //   367: invokeinterface 213 2 0
    //   372: invokevirtual 1175	com/google/android/gms/measurement/internal/zza:zzaz	(J)V
    //   375: aload 7
    //   377: astore 6
    //   379: aload 8
    //   381: aload 7
    //   383: bipush 9
    //   385: invokeinterface 213 2 0
    //   390: invokevirtual 1178	com/google/android/gms/measurement/internal/zza:zzba	(J)V
    //   393: aload 7
    //   395: astore 6
    //   397: aload 7
    //   399: bipush 10
    //   401: invokeinterface 1181 2 0
    //   406: ifeq +270 -> 676
    //   409: iconst_1
    //   410: istore_2
    //   411: goto +369 -> 780
    //   414: aload 7
    //   416: astore 6
    //   418: aload 8
    //   420: iload_3
    //   421: invokevirtual 1185	com/google/android/gms/measurement/internal/zza:setMeasurementEnabled	(Z)V
    //   424: aload 7
    //   426: astore 6
    //   428: aload 8
    //   430: aload 7
    //   432: bipush 11
    //   434: invokeinterface 213 2 0
    //   439: invokevirtual 1187	com/google/android/gms/measurement/internal/zza:zzbe	(J)V
    //   442: aload 7
    //   444: astore 6
    //   446: aload 8
    //   448: aload 7
    //   450: bipush 12
    //   452: invokeinterface 213 2 0
    //   457: invokevirtual 1190	com/google/android/gms/measurement/internal/zza:zzbf	(J)V
    //   460: aload 7
    //   462: astore 6
    //   464: aload 8
    //   466: aload 7
    //   468: bipush 13
    //   470: invokeinterface 213 2 0
    //   475: invokevirtual 1193	com/google/android/gms/measurement/internal/zza:zzbg	(J)V
    //   478: aload 7
    //   480: astore 6
    //   482: aload 8
    //   484: aload 7
    //   486: bipush 14
    //   488: invokeinterface 213 2 0
    //   493: invokevirtual 1196	com/google/android/gms/measurement/internal/zza:zzbh	(J)V
    //   496: aload 7
    //   498: astore 6
    //   500: aload 8
    //   502: aload 7
    //   504: bipush 15
    //   506: invokeinterface 213 2 0
    //   511: invokevirtual 1199	com/google/android/gms/measurement/internal/zza:zzbc	(J)V
    //   514: aload 7
    //   516: astore 6
    //   518: aload 8
    //   520: aload 7
    //   522: bipush 16
    //   524: invokeinterface 213 2 0
    //   529: invokevirtual 1201	com/google/android/gms/measurement/internal/zza:zzbd	(J)V
    //   532: aload 7
    //   534: astore 6
    //   536: aload 7
    //   538: bipush 17
    //   540: invokeinterface 1181 2 0
    //   545: ifeq +148 -> 693
    //   548: ldc2_w 1202
    //   551: lstore 4
    //   553: aload 7
    //   555: astore 6
    //   557: aload 8
    //   559: lload 4
    //   561: invokevirtual 1206	com/google/android/gms/measurement/internal/zza:zzay	(J)V
    //   564: aload 7
    //   566: astore 6
    //   568: aload 8
    //   570: aload 7
    //   572: bipush 18
    //   574: invokeinterface 779 2 0
    //   579: invokevirtual 1209	com/google/android/gms/measurement/internal/zza:zzlm	(Ljava/lang/String;)V
    //   582: aload 7
    //   584: astore 6
    //   586: aload 8
    //   588: aload 7
    //   590: bipush 19
    //   592: invokeinterface 213 2 0
    //   597: invokevirtual 1212	com/google/android/gms/measurement/internal/zza:zzbj	(J)V
    //   600: aload 7
    //   602: astore 6
    //   604: aload 8
    //   606: aload 7
    //   608: bipush 20
    //   610: invokeinterface 213 2 0
    //   615: invokevirtual 1215	com/google/android/gms/measurement/internal/zza:zzbi	(J)V
    //   618: aload 7
    //   620: astore 6
    //   622: aload 8
    //   624: invokevirtual 1218	com/google/android/gms/measurement/internal/zza:zzbsq	()V
    //   627: aload 7
    //   629: astore 6
    //   631: aload 7
    //   633: invokeinterface 809 1 0
    //   638: ifeq +20 -> 658
    //   641: aload 7
    //   643: astore 6
    //   645: aload_0
    //   646: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   649: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   652: ldc_w 1220
    //   655: invokevirtual 275	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
    //   658: aload 8
    //   660: astore_1
    //   661: aload 7
    //   663: ifnull -464 -> 199
    //   666: aload 7
    //   668: invokeinterface 216 1 0
    //   673: aload 8
    //   675: areturn
    //   676: aload 7
    //   678: astore 6
    //   680: aload 7
    //   682: bipush 10
    //   684: invokeinterface 974 2 0
    //   689: istore_2
    //   690: goto +90 -> 780
    //   693: aload 7
    //   695: astore 6
    //   697: aload 7
    //   699: bipush 17
    //   701: invokeinterface 974 2 0
    //   706: istore_2
    //   707: iload_2
    //   708: i2l
    //   709: lstore 4
    //   711: goto -158 -> 553
    //   714: astore 8
    //   716: aconst_null
    //   717: astore 7
    //   719: aload 7
    //   721: astore 6
    //   723: aload_0
    //   724: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   727: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   730: ldc_w 1222
    //   733: aload_1
    //   734: aload 8
    //   736: invokevirtual 234	com/google/android/gms/measurement/internal/zzp$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   739: aload 7
    //   741: ifnull +10 -> 751
    //   744: aload 7
    //   746: invokeinterface 216 1 0
    //   751: aconst_null
    //   752: areturn
    //   753: astore_1
    //   754: aconst_null
    //   755: astore 6
    //   757: aload 6
    //   759: ifnull +10 -> 769
    //   762: aload 6
    //   764: invokeinterface 216 1 0
    //   769: aload_1
    //   770: athrow
    //   771: astore_1
    //   772: goto -15 -> 757
    //   775: astore 8
    //   777: goto -58 -> 719
    //   780: iload_2
    //   781: ifeq +8 -> 789
    //   784: iconst_1
    //   785: istore_3
    //   786: goto -372 -> 414
    //   789: iconst_0
    //   790: istore_3
    //   791: goto -377 -> 414
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	794	0	this	zze
    //   0	794	1	paramString	String
    //   410	371	2	i	int
    //   180	611	3	bool	boolean
    //   551	159	4	l	long
    //   171	592	6	localCursor1	Cursor
    //   167	578	7	localCursor2	Cursor
    //   217	457	8	localzza	zza
    //   714	21	8	localSQLiteException1	SQLiteException
    //   775	1	8	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   13	169	714	android/database/sqlite/SQLiteException
    //   13	169	753	finally
    //   173	181	771	finally
    //   205	219	771	finally
    //   223	236	771	finally
    //   240	253	771	finally
    //   257	270	771	finally
    //   274	287	771	finally
    //   291	304	771	finally
    //   308	321	771	finally
    //   325	339	771	finally
    //   343	357	771	finally
    //   361	375	771	finally
    //   379	393	771	finally
    //   397	409	771	finally
    //   418	424	771	finally
    //   428	442	771	finally
    //   446	460	771	finally
    //   464	478	771	finally
    //   482	496	771	finally
    //   500	514	771	finally
    //   518	532	771	finally
    //   536	548	771	finally
    //   557	564	771	finally
    //   568	582	771	finally
    //   586	600	771	finally
    //   604	618	771	finally
    //   622	627	771	finally
    //   631	641	771	finally
    //   645	658	771	finally
    //   680	690	771	finally
    //   697	707	771	finally
    //   723	739	771	finally
    //   173	181	775	android/database/sqlite/SQLiteException
    //   205	219	775	android/database/sqlite/SQLiteException
    //   223	236	775	android/database/sqlite/SQLiteException
    //   240	253	775	android/database/sqlite/SQLiteException
    //   257	270	775	android/database/sqlite/SQLiteException
    //   274	287	775	android/database/sqlite/SQLiteException
    //   291	304	775	android/database/sqlite/SQLiteException
    //   308	321	775	android/database/sqlite/SQLiteException
    //   325	339	775	android/database/sqlite/SQLiteException
    //   343	357	775	android/database/sqlite/SQLiteException
    //   361	375	775	android/database/sqlite/SQLiteException
    //   379	393	775	android/database/sqlite/SQLiteException
    //   397	409	775	android/database/sqlite/SQLiteException
    //   418	424	775	android/database/sqlite/SQLiteException
    //   428	442	775	android/database/sqlite/SQLiteException
    //   446	460	775	android/database/sqlite/SQLiteException
    //   464	478	775	android/database/sqlite/SQLiteException
    //   482	496	775	android/database/sqlite/SQLiteException
    //   500	514	775	android/database/sqlite/SQLiteException
    //   518	532	775	android/database/sqlite/SQLiteException
    //   536	548	775	android/database/sqlite/SQLiteException
    //   557	564	775	android/database/sqlite/SQLiteException
    //   568	582	775	android/database/sqlite/SQLiteException
    //   586	600	775	android/database/sqlite/SQLiteException
    //   604	618	775	android/database/sqlite/SQLiteException
    //   622	627	775	android/database/sqlite/SQLiteException
    //   631	641	775	android/database/sqlite/SQLiteException
    //   645	658	775	android/database/sqlite/SQLiteException
    //   680	690	775	android/database/sqlite/SQLiteException
    //   697	707	775	android/database/sqlite/SQLiteException
  }
  
  public long zzma(String paramString)
  {
    zzac.zzhz(paramString);
    zzyl();
    zzaax();
    try
    {
      int i = getWritableDatabase().delete("raw_events", "rowid in (select rowid from raw_events where app_id=? order by rowid desc limit -1 offset ?)", new String[] { paramString, String.valueOf(zzbvi().zzlx(paramString)) });
      return i;
    }
    catch (SQLiteException paramString)
    {
      zzbvg().zzbwc().zzj("Error deleting over the limit events", paramString);
    }
    return 0L;
  }
  
  /* Error */
  @WorkerThread
  public byte[] zzmb(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 242	com/google/android/gms/measurement/internal/zze:zzyl	()V
    //   9: aload_0
    //   10: invokevirtual 239	com/google/android/gms/measurement/internal/zze:zzaax	()V
    //   13: aload_0
    //   14: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: ldc_w 471
    //   20: iconst_1
    //   21: anewarray 322	java/lang/String
    //   24: dup
    //   25: iconst_0
    //   26: ldc 80
    //   28: aastore
    //   29: ldc_w 473
    //   32: iconst_1
    //   33: anewarray 322	java/lang/String
    //   36: dup
    //   37: iconst_0
    //   38: aload_1
    //   39: aastore
    //   40: aconst_null
    //   41: aconst_null
    //   42: aconst_null
    //   43: invokevirtual 477	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   46: astore 4
    //   48: aload 4
    //   50: astore_3
    //   51: aload 4
    //   53: invokeinterface 209 1 0
    //   58: istore_2
    //   59: iload_2
    //   60: ifne +19 -> 79
    //   63: aload 4
    //   65: ifnull +10 -> 75
    //   68: aload 4
    //   70: invokeinterface 216 1 0
    //   75: aconst_null
    //   76: astore_1
    //   77: aload_1
    //   78: areturn
    //   79: aload 4
    //   81: astore_3
    //   82: aload 4
    //   84: iconst_0
    //   85: invokeinterface 796 2 0
    //   90: astore 5
    //   92: aload 4
    //   94: astore_3
    //   95: aload 4
    //   97: invokeinterface 809 1 0
    //   102: ifeq +19 -> 121
    //   105: aload 4
    //   107: astore_3
    //   108: aload_0
    //   109: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   112: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   115: ldc_w 1235
    //   118: invokevirtual 275	com/google/android/gms/measurement/internal/zzp$zza:log	(Ljava/lang/String;)V
    //   121: aload 5
    //   123: astore_1
    //   124: aload 4
    //   126: ifnull -49 -> 77
    //   129: aload 4
    //   131: invokeinterface 216 1 0
    //   136: aload 5
    //   138: areturn
    //   139: astore 5
    //   141: aconst_null
    //   142: astore 4
    //   144: aload 4
    //   146: astore_3
    //   147: aload_0
    //   148: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   151: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   154: ldc_w 1237
    //   157: aload_1
    //   158: aload 5
    //   160: invokevirtual 234	com/google/android/gms/measurement/internal/zzp$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   163: aload 4
    //   165: ifnull +10 -> 175
    //   168: aload 4
    //   170: invokeinterface 216 1 0
    //   175: aconst_null
    //   176: areturn
    //   177: astore_1
    //   178: aconst_null
    //   179: astore_3
    //   180: aload_3
    //   181: ifnull +9 -> 190
    //   184: aload_3
    //   185: invokeinterface 216 1 0
    //   190: aload_1
    //   191: athrow
    //   192: astore_1
    //   193: goto -13 -> 180
    //   196: astore 5
    //   198: goto -54 -> 144
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	201	0	this	zze
    //   0	201	1	paramString	String
    //   58	2	2	bool	boolean
    //   50	135	3	localCursor1	Cursor
    //   46	123	4	localCursor2	Cursor
    //   90	47	5	arrayOfByte	byte[]
    //   139	20	5	localSQLiteException1	SQLiteException
    //   196	1	5	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   13	48	139	android/database/sqlite/SQLiteException
    //   13	48	177	finally
    //   51	59	192	finally
    //   82	92	192	finally
    //   95	105	192	finally
    //   108	121	192	finally
    //   147	163	192	finally
    //   51	59	196	android/database/sqlite/SQLiteException
    //   82	92	196	android/database/sqlite/SQLiteException
    //   95	105	196	android/database/sqlite/SQLiteException
    //   108	121	196	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  void zzmc(String paramString)
  {
    zzaax();
    zzyl();
    zzac.zzhz(paramString);
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.delete("property_filters", "app_id=?", new String[] { paramString });
    localSQLiteDatabase.delete("event_filters", "app_id=?", new String[] { paramString });
  }
  
  /* Error */
  Map<Integer, com.google.android.gms.internal.zzvm.zzf> zzmd(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 239	com/google/android/gms/measurement/internal/zze:zzaax	()V
    //   4: aload_0
    //   5: invokevirtual 242	com/google/android/gms/measurement/internal/zze:zzyl	()V
    //   8: aload_1
    //   9: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: astore 4
    //   19: aload 4
    //   21: ldc_w 763
    //   24: iconst_2
    //   25: anewarray 322	java/lang/String
    //   28: dup
    //   29: iconst_0
    //   30: ldc_w 351
    //   33: aastore
    //   34: dup
    //   35: iconst_1
    //   36: ldc_w 761
    //   39: aastore
    //   40: ldc_w 473
    //   43: iconst_1
    //   44: anewarray 322	java/lang/String
    //   47: dup
    //   48: iconst_0
    //   49: aload_1
    //   50: aastore
    //   51: aconst_null
    //   52: aconst_null
    //   53: aconst_null
    //   54: invokevirtual 477	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   57: astore 4
    //   59: aload 4
    //   61: invokeinterface 209 1 0
    //   66: istore_3
    //   67: iload_3
    //   68: ifne +17 -> 85
    //   71: aload 4
    //   73: ifnull +10 -> 83
    //   76: aload 4
    //   78: invokeinterface 216 1 0
    //   83: aconst_null
    //   84: areturn
    //   85: new 26	android/support/v4/util/ArrayMap
    //   88: dup
    //   89: invokespecial 962	android/support/v4/util/ArrayMap:<init>	()V
    //   92: astore 5
    //   94: aload 4
    //   96: iconst_0
    //   97: invokeinterface 974 2 0
    //   102: istore_2
    //   103: aload 4
    //   105: iconst_1
    //   106: invokeinterface 796 2 0
    //   111: invokestatic 802	com/google/android/gms/internal/zzarc:zzbd	([B)Lcom/google/android/gms/internal/zzarc;
    //   114: astore 7
    //   116: new 757	com/google/android/gms/internal/zzvm$zzf
    //   119: dup
    //   120: invokespecial 1240	com/google/android/gms/internal/zzvm$zzf:<init>	()V
    //   123: astore 6
    //   125: aload 6
    //   127: aload 7
    //   129: invokevirtual 1241	com/google/android/gms/internal/zzvm$zzf:zzb	(Lcom/google/android/gms/internal/zzarc;)Lcom/google/android/gms/internal/zzark;
    //   132: checkcast 757	com/google/android/gms/internal/zzvm$zzf
    //   135: astore 7
    //   137: aload 5
    //   139: iload_2
    //   140: invokestatic 320	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   143: aload 6
    //   145: invokeinterface 42 3 0
    //   150: pop
    //   151: aload 4
    //   153: invokeinterface 809 1 0
    //   158: istore_3
    //   159: iload_3
    //   160: ifne -66 -> 94
    //   163: aload 4
    //   165: ifnull +10 -> 175
    //   168: aload 4
    //   170: invokeinterface 216 1 0
    //   175: aload 5
    //   177: areturn
    //   178: astore 6
    //   180: aload_0
    //   181: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   184: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   187: ldc_w 1243
    //   190: aload_1
    //   191: iload_2
    //   192: invokestatic 320	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   195: aload 6
    //   197: invokevirtual 944	com/google/android/gms/measurement/internal/zzp$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   200: goto -49 -> 151
    //   203: astore 5
    //   205: aload 4
    //   207: astore_1
    //   208: aload 5
    //   210: astore 4
    //   212: aload_0
    //   213: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   216: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   219: ldc_w 1245
    //   222: aload 4
    //   224: invokevirtual 379	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   227: aload_1
    //   228: ifnull +9 -> 237
    //   231: aload_1
    //   232: invokeinterface 216 1 0
    //   237: aconst_null
    //   238: areturn
    //   239: astore_1
    //   240: aconst_null
    //   241: astore 4
    //   243: aload 4
    //   245: ifnull +10 -> 255
    //   248: aload 4
    //   250: invokeinterface 216 1 0
    //   255: aload_1
    //   256: athrow
    //   257: astore_1
    //   258: goto -15 -> 243
    //   261: astore 5
    //   263: aload_1
    //   264: astore 4
    //   266: aload 5
    //   268: astore_1
    //   269: goto -26 -> 243
    //   272: astore 4
    //   274: aconst_null
    //   275: astore_1
    //   276: goto -64 -> 212
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	279	0	this	zze
    //   0	279	1	paramString	String
    //   102	90	2	i	int
    //   66	94	3	bool	boolean
    //   17	248	4	localObject1	Object
    //   272	1	4	localSQLiteException1	SQLiteException
    //   92	84	5	localArrayMap	ArrayMap
    //   203	6	5	localSQLiteException2	SQLiteException
    //   261	6	5	localObject2	Object
    //   123	21	6	localzzf	com.google.android.gms.internal.zzvm.zzf
    //   178	18	6	localIOException	IOException
    //   114	22	7	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   125	137	178	java/io/IOException
    //   59	67	203	android/database/sqlite/SQLiteException
    //   85	94	203	android/database/sqlite/SQLiteException
    //   94	125	203	android/database/sqlite/SQLiteException
    //   125	137	203	android/database/sqlite/SQLiteException
    //   137	151	203	android/database/sqlite/SQLiteException
    //   151	159	203	android/database/sqlite/SQLiteException
    //   180	200	203	android/database/sqlite/SQLiteException
    //   19	59	239	finally
    //   59	67	257	finally
    //   85	94	257	finally
    //   94	125	257	finally
    //   125	137	257	finally
    //   137	151	257	finally
    //   151	159	257	finally
    //   180	200	257	finally
    //   212	227	261	finally
    //   19	59	272	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  void zzme(String paramString)
  {
    zzaax();
    zzyl();
    zzac.zzhz(paramString);
    try
    {
      SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
      String[] arrayOfString = new String[1];
      arrayOfString[0] = paramString;
      int i = localSQLiteDatabase.delete("events", "app_id=?", arrayOfString);
      int j = localSQLiteDatabase.delete("user_attributes", "app_id=?", arrayOfString);
      int k = localSQLiteDatabase.delete("apps", "app_id=?", arrayOfString);
      int m = localSQLiteDatabase.delete("raw_events", "app_id=?", arrayOfString);
      int n = localSQLiteDatabase.delete("raw_events_metadata", "app_id=?", arrayOfString);
      int i1 = localSQLiteDatabase.delete("event_filters", "app_id=?", arrayOfString);
      int i2 = localSQLiteDatabase.delete("property_filters", "app_id=?", arrayOfString);
      i = localSQLiteDatabase.delete("audience_filter_values", "app_id=?", arrayOfString) + (i + 0 + j + k + m + n + i1 + i2);
      if (i > 0) {
        zzbvg().zzbwj().zze("Deleted application data. app, records", paramString, Integer.valueOf(i));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzbvg().zzbwc().zze("Error deleting application data. appId, error", paramString, localSQLiteException);
    }
  }
  
  @WorkerThread
  public long zzmf(String paramString)
  {
    zzac.zzhz(paramString);
    zzyl();
    zzaax();
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    try
    {
      long l = zza("select first_open_count from app2 where app_id=?", new String[] { paramString }, 0L);
      zzg(paramString, 1L + l);
      localSQLiteDatabase.setTransactionSuccessful();
      return l;
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  public void zzmg(String paramString)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    try
    {
      localSQLiteDatabase.execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[] { paramString, paramString });
      return;
    }
    catch (SQLiteException paramString)
    {
      zzbvg().zzbwc().zzj("Failed to remove unused event metadata", paramString);
    }
  }
  
  public long zzmh(String paramString)
  {
    zzac.zzhz(paramString);
    return zza("select count(1) from events where app_id=? and name not like '!_%' escape '!'", new String[] { paramString }, 0L);
  }
  
  /* Error */
  @WorkerThread
  public List<android.util.Pair<zzvm.zze, Long>> zzn(String paramString, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore 6
    //   3: aload_0
    //   4: invokevirtual 242	com/google/android/gms/measurement/internal/zze:zzyl	()V
    //   7: aload_0
    //   8: invokevirtual 239	com/google/android/gms/measurement/internal/zze:zzaax	()V
    //   11: iload_2
    //   12: ifle +112 -> 124
    //   15: iconst_1
    //   16: istore 5
    //   18: iload 5
    //   20: invokestatic 1273	com/google/android/gms/common/internal/zzac:zzbs	(Z)V
    //   23: iload_3
    //   24: ifle +106 -> 130
    //   27: iload 6
    //   29: istore 5
    //   31: iload 5
    //   33: invokestatic 1273	com/google/android/gms/common/internal/zzac:zzbs	(Z)V
    //   36: aload_1
    //   37: invokestatic 248	com/google/android/gms/common/internal/zzac:zzhz	(Ljava/lang/String;)Ljava/lang/String;
    //   40: pop
    //   41: aload_0
    //   42: invokevirtual 199	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   45: ldc_w 552
    //   48: iconst_2
    //   49: anewarray 322	java/lang/String
    //   52: dup
    //   53: iconst_0
    //   54: ldc_w 783
    //   57: aastore
    //   58: dup
    //   59: iconst_1
    //   60: ldc_w 360
    //   63: aastore
    //   64: ldc_w 473
    //   67: iconst_1
    //   68: anewarray 322	java/lang/String
    //   71: dup
    //   72: iconst_0
    //   73: aload_1
    //   74: aastore
    //   75: aconst_null
    //   76: aconst_null
    //   77: ldc_w 783
    //   80: iload_2
    //   81: invokestatic 877	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   84: invokevirtual 788	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   87: astore 9
    //   89: aload 9
    //   91: invokeinterface 209 1 0
    //   96: ifne +40 -> 136
    //   99: invokestatic 1277	java/util/Collections:emptyList	()Ljava/util/List;
    //   102: astore 10
    //   104: aload 10
    //   106: astore_1
    //   107: aload 9
    //   109: ifnull +13 -> 122
    //   112: aload 9
    //   114: invokeinterface 216 1 0
    //   119: aload 10
    //   121: astore_1
    //   122: aload_1
    //   123: areturn
    //   124: iconst_0
    //   125: istore 5
    //   127: goto -109 -> 18
    //   130: iconst_0
    //   131: istore 5
    //   133: goto -102 -> 31
    //   136: new 978	java/util/ArrayList
    //   139: dup
    //   140: invokespecial 979	java/util/ArrayList:<init>	()V
    //   143: astore 10
    //   145: iconst_0
    //   146: istore_2
    //   147: aload 9
    //   149: iconst_0
    //   150: invokeinterface 213 2 0
    //   155: lstore 7
    //   157: aload 9
    //   159: iconst_1
    //   160: invokeinterface 796 2 0
    //   165: astore 11
    //   167: aload_0
    //   168: invokevirtual 443	com/google/android/gms/measurement/internal/zze:zzbvc	()Lcom/google/android/gms/measurement/internal/zzal;
    //   171: aload 11
    //   173: invokevirtual 1280	com/google/android/gms/measurement/internal/zzal:zzw	([B)[B
    //   176: astore 11
    //   178: aload 10
    //   180: invokeinterface 1282 1 0
    //   185: ifne +67 -> 252
    //   188: aload 11
    //   190: arraylength
    //   191: istore 4
    //   193: iload 4
    //   195: iload_2
    //   196: iadd
    //   197: iload_3
    //   198: if_icmple +54 -> 252
    //   201: aload 9
    //   203: ifnull +10 -> 213
    //   206: aload 9
    //   208: invokeinterface 216 1 0
    //   213: aload 10
    //   215: areturn
    //   216: astore 11
    //   218: aload_0
    //   219: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   222: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   225: ldc_w 1284
    //   228: aload_1
    //   229: aload 11
    //   231: invokevirtual 234	com/google/android/gms/measurement/internal/zzp$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   234: aload 9
    //   236: invokeinterface 809 1 0
    //   241: ifeq -40 -> 201
    //   244: iload_2
    //   245: iload_3
    //   246: if_icmpgt -45 -> 201
    //   249: goto -102 -> 147
    //   252: aload 11
    //   254: invokestatic 802	com/google/android/gms/internal/zzarc:zzbd	([B)Lcom/google/android/gms/internal/zzarc;
    //   257: astore 13
    //   259: new 434	com/google/android/gms/internal/zzvm$zze
    //   262: dup
    //   263: invokespecial 803	com/google/android/gms/internal/zzvm$zze:<init>	()V
    //   266: astore 12
    //   268: aload 12
    //   270: aload 13
    //   272: invokevirtual 806	com/google/android/gms/internal/zzvm$zze:zzb	(Lcom/google/android/gms/internal/zzarc;)Lcom/google/android/gms/internal/zzark;
    //   275: checkcast 434	com/google/android/gms/internal/zzvm$zze
    //   278: astore 13
    //   280: aload 11
    //   282: arraylength
    //   283: iload_2
    //   284: iadd
    //   285: istore_2
    //   286: aload 10
    //   288: aload 12
    //   290: lload 7
    //   292: invokestatic 455	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   295: invokestatic 1290	android/util/Pair:create	(Ljava/lang/Object;Ljava/lang/Object;)Landroid/util/Pair;
    //   298: invokeinterface 983 2 0
    //   303: pop
    //   304: goto -70 -> 234
    //   307: astore 10
    //   309: aload_0
    //   310: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   313: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   316: ldc_w 1292
    //   319: aload_1
    //   320: aload 10
    //   322: invokevirtual 234	com/google/android/gms/measurement/internal/zzp$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   325: invokestatic 1277	java/util/Collections:emptyList	()Ljava/util/List;
    //   328: astore 10
    //   330: aload 10
    //   332: astore_1
    //   333: aload 9
    //   335: ifnull -213 -> 122
    //   338: aload 9
    //   340: invokeinterface 216 1 0
    //   345: aload 10
    //   347: areturn
    //   348: astore 11
    //   350: aload_0
    //   351: invokevirtual 220	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   354: invokevirtual 226	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   357: ldc_w 1294
    //   360: aload_1
    //   361: aload 11
    //   363: invokevirtual 234	com/google/android/gms/measurement/internal/zzp$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   366: goto -132 -> 234
    //   369: astore_1
    //   370: aconst_null
    //   371: astore 9
    //   373: aload 9
    //   375: ifnull +10 -> 385
    //   378: aload 9
    //   380: invokeinterface 216 1 0
    //   385: aload_1
    //   386: athrow
    //   387: astore_1
    //   388: goto -15 -> 373
    //   391: astore_1
    //   392: goto -19 -> 373
    //   395: astore 10
    //   397: aconst_null
    //   398: astore 9
    //   400: goto -91 -> 309
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	403	0	this	zze
    //   0	403	1	paramString	String
    //   0	403	2	paramInt1	int
    //   0	403	3	paramInt2	int
    //   191	6	4	i	int
    //   16	116	5	bool1	boolean
    //   1	27	6	bool2	boolean
    //   155	136	7	l	long
    //   87	312	9	localCursor	Cursor
    //   102	185	10	localObject1	Object
    //   307	14	10	localSQLiteException1	SQLiteException
    //   328	18	10	localList	List
    //   395	1	10	localSQLiteException2	SQLiteException
    //   165	24	11	arrayOfByte	byte[]
    //   216	65	11	localIOException1	IOException
    //   348	14	11	localIOException2	IOException
    //   266	23	12	localzze	zzvm.zze
    //   257	22	13	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   157	178	216	java/io/IOException
    //   89	104	307	android/database/sqlite/SQLiteException
    //   136	145	307	android/database/sqlite/SQLiteException
    //   147	157	307	android/database/sqlite/SQLiteException
    //   157	178	307	android/database/sqlite/SQLiteException
    //   178	193	307	android/database/sqlite/SQLiteException
    //   218	234	307	android/database/sqlite/SQLiteException
    //   234	244	307	android/database/sqlite/SQLiteException
    //   252	268	307	android/database/sqlite/SQLiteException
    //   268	280	307	android/database/sqlite/SQLiteException
    //   280	304	307	android/database/sqlite/SQLiteException
    //   350	366	307	android/database/sqlite/SQLiteException
    //   268	280	348	java/io/IOException
    //   41	89	369	finally
    //   89	104	387	finally
    //   136	145	387	finally
    //   147	157	387	finally
    //   157	178	387	finally
    //   178	193	387	finally
    //   218	234	387	finally
    //   234	244	387	finally
    //   252	268	387	finally
    //   268	280	387	finally
    //   280	304	387	finally
    //   350	366	387	finally
    //   309	330	391	finally
    //   41	89	395	android/database/sqlite/SQLiteException
  }
  
  protected void zzym() {}
  
  @WorkerThread
  public void zzz(String paramString, int paramInt)
  {
    zzac.zzhz(paramString);
    zzyl();
    zzaax();
    try
    {
      getWritableDatabase().execSQL("delete from user_attributes where app_id=? and name in (select name from user_attributes where app_id=? and name like '_ltv_%' order by set_timestamp desc limit ?,10);", new String[] { paramString, paramString, String.valueOf(paramInt) });
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzbvg().zzbwc().zze("Error pruning currencies", paramString, localSQLiteException);
    }
  }
  
  public static class zza
  {
    long aof;
    long aog;
    long aoh;
    long aoi;
    long aoj;
  }
  
  static abstract interface zzb
  {
    public abstract boolean zza(long paramLong, zzvm.zzb paramzzb);
    
    public abstract void zzb(zzvm.zze paramzze);
  }
  
  private class zzc
    extends SQLiteOpenHelper
  {
    zzc(Context paramContext, String paramString)
    {
      super(paramString, null, 1);
    }
    
    @WorkerThread
    private void zza(SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, String paramString3, Map<String, String> paramMap)
      throws SQLiteException
    {
      if (!zza(paramSQLiteDatabase, paramString1)) {
        paramSQLiteDatabase.execSQL(paramString2);
      }
      try
      {
        zza(paramSQLiteDatabase, paramString1, paramString3, paramMap);
        return;
      }
      catch (SQLiteException paramSQLiteDatabase)
      {
        zze.this.zzbvg().zzbwc().zzj("Failed to verify columns on table that was just created", paramString1);
        throw paramSQLiteDatabase;
      }
    }
    
    @WorkerThread
    private void zza(SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, Map<String, String> paramMap)
      throws SQLiteException
    {
      Set localSet = zzb(paramSQLiteDatabase, paramString1);
      paramString2 = paramString2.split(",");
      int j = paramString2.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramString2[i];
        if (!localSet.remove(localObject)) {
          throw new SQLiteException(String.valueOf(paramString1).length() + 35 + String.valueOf(localObject).length() + "Table " + paramString1 + " is missing required column: " + (String)localObject);
        }
        i += 1;
      }
      if (paramMap != null)
      {
        paramString2 = paramMap.entrySet().iterator();
        while (paramString2.hasNext())
        {
          paramMap = (Map.Entry)paramString2.next();
          if (!localSet.remove(paramMap.getKey())) {
            paramSQLiteDatabase.execSQL((String)paramMap.getValue());
          }
        }
      }
      if (!localSet.isEmpty()) {
        zze.this.zzbvg().zzbwe().zze("Table has extra columns. table, columns", paramString1, TextUtils.join(", ", localSet));
      }
    }
    
    /* Error */
    @WorkerThread
    private boolean zza(SQLiteDatabase paramSQLiteDatabase, String paramString)
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore 4
      //   3: aload_1
      //   4: ldc -104
      //   6: iconst_1
      //   7: anewarray 64	java/lang/String
      //   10: dup
      //   11: iconst_0
      //   12: ldc -102
      //   14: aastore
      //   15: ldc -100
      //   17: iconst_1
      //   18: anewarray 64	java/lang/String
      //   21: dup
      //   22: iconst_0
      //   23: aload_2
      //   24: aastore
      //   25: aconst_null
      //   26: aconst_null
      //   27: aconst_null
      //   28: invokevirtual 160	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
      //   31: astore_1
      //   32: aload_1
      //   33: astore 4
      //   35: aload 4
      //   37: astore_1
      //   38: aload 4
      //   40: invokeinterface 165 1 0
      //   45: istore_3
      //   46: aload 4
      //   48: ifnull +10 -> 58
      //   51: aload 4
      //   53: invokeinterface 169 1 0
      //   58: iload_3
      //   59: ireturn
      //   60: astore 5
      //   62: aconst_null
      //   63: astore 4
      //   65: aload 4
      //   67: astore_1
      //   68: aload_0
      //   69: getfield 13	com/google/android/gms/measurement/internal/zze$zzc:aok	Lcom/google/android/gms/measurement/internal/zze;
      //   72: invokevirtual 38	com/google/android/gms/measurement/internal/zze:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
      //   75: invokevirtual 135	com/google/android/gms/measurement/internal/zzp:zzbwe	()Lcom/google/android/gms/measurement/internal/zzp$zza;
      //   78: ldc -85
      //   80: aload_2
      //   81: aload 5
      //   83: invokevirtual 149	com/google/android/gms/measurement/internal/zzp$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
      //   86: aload 4
      //   88: ifnull +10 -> 98
      //   91: aload 4
      //   93: invokeinterface 169 1 0
      //   98: iconst_0
      //   99: ireturn
      //   100: astore_1
      //   101: aload 4
      //   103: astore_2
      //   104: aload_2
      //   105: ifnull +9 -> 114
      //   108: aload_2
      //   109: invokeinterface 169 1 0
      //   114: aload_1
      //   115: athrow
      //   116: astore 4
      //   118: aload_1
      //   119: astore_2
      //   120: aload 4
      //   122: astore_1
      //   123: goto -19 -> 104
      //   126: astore 5
      //   128: goto -63 -> 65
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	131	0	this	zzc
      //   0	131	1	paramSQLiteDatabase	SQLiteDatabase
      //   0	131	2	paramString	String
      //   45	14	3	bool	boolean
      //   1	101	4	localSQLiteDatabase	SQLiteDatabase
      //   116	5	4	localObject	Object
      //   60	22	5	localSQLiteException1	SQLiteException
      //   126	1	5	localSQLiteException2	SQLiteException
      // Exception table:
      //   from	to	target	type
      //   3	32	60	android/database/sqlite/SQLiteException
      //   3	32	100	finally
      //   38	46	116	finally
      //   68	86	116	finally
      //   38	46	126	android/database/sqlite/SQLiteException
    }
    
    @WorkerThread
    private Set<String> zzb(SQLiteDatabase paramSQLiteDatabase, String paramString)
    {
      HashSet localHashSet = new HashSet();
      paramSQLiteDatabase = paramSQLiteDatabase.rawQuery(String.valueOf(paramString).length() + 22 + "SELECT * FROM " + paramString + " LIMIT 0", null);
      try
      {
        Collections.addAll(localHashSet, paramSQLiteDatabase.getColumnNames());
        return localHashSet;
      }
      finally
      {
        paramSQLiteDatabase.close();
      }
    }
    
    @WorkerThread
    public SQLiteDatabase getWritableDatabase()
    {
      if (!zze.zza(zze.this).zzz(zze.this.zzbvi().zzbtz())) {
        throw new SQLiteException("Database open failed");
      }
      try
      {
        SQLiteDatabase localSQLiteDatabase = super.getWritableDatabase();
        return localSQLiteDatabase;
      }
      catch (SQLiteException localSQLiteException1)
      {
        zze.zza(zze.this).start();
        zze.this.zzbvg().zzbwc().log("Opening the database failed, dropping and recreating it");
        Object localObject = zze.this.zzabs();
        if (!zze.this.getContext().getDatabasePath((String)localObject).delete()) {
          zze.this.zzbvg().zzbwc().zzj("Failed to delete corrupted db file", localObject);
        }
        try
        {
          localObject = super.getWritableDatabase();
          zze.zza(zze.this).clear();
          return (SQLiteDatabase)localObject;
        }
        catch (SQLiteException localSQLiteException2)
        {
          zze.this.zzbvg().zzbwc().zzj("Failed to open freshly created database", localSQLiteException2);
          throw localSQLiteException2;
        }
      }
    }
    
    @WorkerThread
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      if (Build.VERSION.SDK_INT >= 9)
      {
        paramSQLiteDatabase = new File(paramSQLiteDatabase.getPath());
        if (!paramSQLiteDatabase.setReadable(false, false)) {
          zze.this.zzbvg().zzbwe().log("Failed to turn off database read permission");
        }
        if (!paramSQLiteDatabase.setWritable(false, false)) {
          zze.this.zzbvg().zzbwe().log("Failed to turn off database write permission");
        }
        if (!paramSQLiteDatabase.setReadable(true, true)) {
          zze.this.zzbvg().zzbwe().log("Failed to turn on database read permission for owner");
        }
        if (!paramSQLiteDatabase.setWritable(true, true)) {
          zze.this.zzbvg().zzbwe().log("Failed to turn on database write permission for owner");
        }
      }
    }
    
    @WorkerThread
    public void onOpen(SQLiteDatabase paramSQLiteDatabase)
    {
      Cursor localCursor;
      if (Build.VERSION.SDK_INT < 15) {
        localCursor = paramSQLiteDatabase.rawQuery("PRAGMA journal_mode=memory", null);
      }
      try
      {
        localCursor.moveToFirst();
        localCursor.close();
        zza(paramSQLiteDatabase, "events", "CREATE TABLE IF NOT EXISTS events ( app_id TEXT NOT NULL, name TEXT NOT NULL, lifetime_count INTEGER NOT NULL, current_bundle_count INTEGER NOT NULL, last_fire_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,lifetime_count,current_bundle_count,last_fire_timestamp", null);
        zza(paramSQLiteDatabase, "user_attributes", "CREATE TABLE IF NOT EXISTS user_attributes ( app_id TEXT NOT NULL, name TEXT NOT NULL, set_timestamp INTEGER NOT NULL, value BLOB NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,set_timestamp,value", null);
        zza(paramSQLiteDatabase, "apps", "CREATE TABLE IF NOT EXISTS apps ( app_id TEXT NOT NULL, app_instance_id TEXT, gmp_app_id TEXT, resettable_device_id_hash TEXT, last_bundle_index INTEGER NOT NULL, last_bundle_end_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id)) ;", "app_id,app_instance_id,gmp_app_id,resettable_device_id_hash,last_bundle_index,last_bundle_end_timestamp", zze.zzbvs());
        zza(paramSQLiteDatabase, "queue", "CREATE TABLE IF NOT EXISTS queue ( app_id TEXT NOT NULL, bundle_end_timestamp INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,bundle_end_timestamp,data", zze.zzbvt());
        zza(paramSQLiteDatabase, "raw_events_metadata", "CREATE TABLE IF NOT EXISTS raw_events_metadata ( app_id TEXT NOT NULL, metadata_fingerprint INTEGER NOT NULL, metadata BLOB NOT NULL, PRIMARY KEY (app_id, metadata_fingerprint));", "app_id,metadata_fingerprint,metadata", null);
        zza(paramSQLiteDatabase, "raw_events", "CREATE TABLE IF NOT EXISTS raw_events ( app_id TEXT NOT NULL, name TEXT NOT NULL, timestamp INTEGER NOT NULL, metadata_fingerprint INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,name,timestamp,metadata_fingerprint,data", zze.zzbvu());
        zza(paramSQLiteDatabase, "event_filters", "CREATE TABLE IF NOT EXISTS event_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, event_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, event_name, audience_id, filter_id));", "app_id,audience_id,filter_id,event_name,data", null);
        zza(paramSQLiteDatabase, "property_filters", "CREATE TABLE IF NOT EXISTS property_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, property_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, property_name, audience_id, filter_id));", "app_id,audience_id,filter_id,property_name,data", null);
        zza(paramSQLiteDatabase, "audience_filter_values", "CREATE TABLE IF NOT EXISTS audience_filter_values ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, current_results BLOB, PRIMARY KEY (app_id, audience_id));", "app_id,audience_id,current_results", null);
        zza(paramSQLiteDatabase, "app2", "CREATE TABLE IF NOT EXISTS app2 ( app_id TEXT NOT NULL, first_open_count INTEGER NOT NULL, PRIMARY KEY (app_id));", "app_id,first_open_count", null);
        return;
      }
      finally
      {
        localCursor.close();
      }
    }
    
    @WorkerThread
    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */