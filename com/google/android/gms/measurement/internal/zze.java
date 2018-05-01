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
import com.google.android.gms.internal.zzart;
import com.google.android.gms.internal.zzwa.zza;
import com.google.android.gms.internal.zzwa.zzb;
import com.google.android.gms.internal.zzwa.zze;
import com.google.android.gms.internal.zzwc.zzb;
import com.google.android.gms.internal.zzwc.zze;
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
  private static final Map<String, String> arj = new ArrayMap(17);
  private static final Map<String, String> ark;
  private static final Map<String, String> arl;
  private static final Map<String, String> arm;
  private final zzc arn;
  private final zzah aro = new zzah(zzabz());
  
  static
  {
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
    ark = new ArrayMap(1);
    ark.put("realtime", "ALTER TABLE raw_events ADD COLUMN realtime INTEGER;");
    arl = new ArrayMap(1);
    arl.put("has_realtime", "ALTER TABLE queue ADD COLUMN has_realtime INTEGER;");
    arm = new ArrayMap(1);
    arm.put("previous_install_count", "ALTER TABLE app2 ADD COLUMN previous_install_count INTEGER;");
  }
  
  zze(zzx paramzzx)
  {
    super(paramzzx);
    paramzzx = zzade();
    this.arn = new zzc(getContext(), paramzzx);
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
      zzbwb().zzbwy().zze("Database error", paramString, paramArrayOfString);
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
  
  static void zza(zzq paramzzq, SQLiteDatabase paramSQLiteDatabase)
  {
    if (paramzzq == null) {
      throw new IllegalArgumentException("Monitor must not be null");
    }
    if (Build.VERSION.SDK_INT >= 9)
    {
      paramSQLiteDatabase = new File(paramSQLiteDatabase.getPath());
      if (!paramSQLiteDatabase.setReadable(false, false)) {
        paramzzq.zzbxa().log("Failed to turn off database read permission");
      }
      if (!paramSQLiteDatabase.setWritable(false, false)) {
        paramzzq.zzbxa().log("Failed to turn off database write permission");
      }
      if (!paramSQLiteDatabase.setReadable(true, true)) {
        paramzzq.zzbxa().log("Failed to turn on database read permission for owner");
      }
      if (!paramSQLiteDatabase.setWritable(true, true)) {
        paramzzq.zzbxa().log("Failed to turn on database write permission for owner");
      }
    }
  }
  
  @WorkerThread
  static void zza(zzq paramzzq, SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, String paramString3, Map<String, String> paramMap)
    throws SQLiteException
  {
    if (paramzzq == null) {
      throw new IllegalArgumentException("Monitor must not be null");
    }
    if (!zza(paramzzq, paramSQLiteDatabase, paramString1)) {
      paramSQLiteDatabase.execSQL(paramString2);
    }
    try
    {
      zza(paramzzq, paramSQLiteDatabase, paramString1, paramString3, paramMap);
      return;
    }
    catch (SQLiteException paramSQLiteDatabase)
    {
      paramzzq.zzbwy().zzj("Failed to verify columns on table that was just created", paramString1);
      throw paramSQLiteDatabase;
    }
  }
  
  @WorkerThread
  static void zza(zzq paramzzq, SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, Map<String, String> paramMap)
    throws SQLiteException
  {
    if (paramzzq == null) {
      throw new IllegalArgumentException("Monitor must not be null");
    }
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
      paramzzq.zzbxa().zze("Table has extra columns. table, columns", paramString1, TextUtils.join(", ", localSet));
    }
  }
  
  @WorkerThread
  private void zza(String paramString, zzwa.zza paramzza)
  {
    int k = 0;
    zzacj();
    zzzx();
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    com.google.android.gms.common.internal.zzaa.zzy(paramzza);
    com.google.android.gms.common.internal.zzaa.zzy(paramzza.awb);
    com.google.android.gms.common.internal.zzaa.zzy(paramzza.awa);
    if (paramzza.avZ == null) {
      zzbwb().zzbxa().log("Audience with no ID");
    }
    label237:
    label282:
    label291:
    label292:
    for (;;)
    {
      return;
      int n = paramzza.avZ.intValue();
      Object localObject = paramzza.awb;
      int j = localObject.length;
      int i = 0;
      while (i < j)
      {
        if (localObject[i].awd == null)
        {
          zzbwb().zzbxa().zze("Event filter with no ID. Audience definition ignored. appId, audienceId", paramString, paramzza.avZ);
          return;
        }
        i += 1;
      }
      localObject = paramzza.awa;
      j = localObject.length;
      i = 0;
      while (i < j)
      {
        if (localObject[i].awd == null)
        {
          zzbwb().zzbxa().zze("Property filter with no ID. Audience definition ignored. appId, audienceId", paramString, paramzza.avZ);
          return;
        }
        i += 1;
      }
      int m = 1;
      localObject = paramzza.awb;
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
        paramzza = paramzza.awa;
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
  
  /* Error */
  @WorkerThread
  static boolean zza(zzq paramzzq, SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: ifnonnull +13 -> 17
    //   7: new 245	java/lang/IllegalArgumentException
    //   10: dup
    //   11: ldc -9
    //   13: invokespecial 250	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   16: athrow
    //   17: aload_1
    //   18: ldc_w 436
    //   21: iconst_1
    //   22: anewarray 303	java/lang/String
    //   25: dup
    //   26: iconst_0
    //   27: ldc_w 438
    //   30: aastore
    //   31: ldc_w 440
    //   34: iconst_1
    //   35: anewarray 303	java/lang/String
    //   38: dup
    //   39: iconst_0
    //   40: aload_2
    //   41: aastore
    //   42: aconst_null
    //   43: aconst_null
    //   44: aconst_null
    //   45: invokevirtual 444	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   48: astore_1
    //   49: aload_1
    //   50: astore 4
    //   52: aload 4
    //   54: astore_1
    //   55: aload 4
    //   57: invokeinterface 216 1 0
    //   62: istore_3
    //   63: aload 4
    //   65: ifnull +10 -> 75
    //   68: aload 4
    //   70: invokeinterface 223 1 0
    //   75: iload_3
    //   76: ireturn
    //   77: astore 5
    //   79: aconst_null
    //   80: astore 4
    //   82: aload 4
    //   84: astore_1
    //   85: aload_0
    //   86: invokevirtual 263	com/google/android/gms/measurement/internal/zzq:zzbxa	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   89: ldc_w 446
    //   92: aload_2
    //   93: aload 5
    //   95: invokevirtual 241	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   98: aload 4
    //   100: ifnull +10 -> 110
    //   103: aload 4
    //   105: invokeinterface 223 1 0
    //   110: iconst_0
    //   111: ireturn
    //   112: astore_0
    //   113: aload 4
    //   115: astore_1
    //   116: aload_1
    //   117: ifnull +9 -> 126
    //   120: aload_1
    //   121: invokeinterface 223 1 0
    //   126: aload_0
    //   127: athrow
    //   128: astore_0
    //   129: goto -13 -> 116
    //   132: astore 5
    //   134: goto -52 -> 82
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	137	0	paramzzq	zzq
    //   0	137	1	paramSQLiteDatabase	SQLiteDatabase
    //   0	137	2	paramString	String
    //   62	14	3	bool	boolean
    //   1	113	4	localSQLiteDatabase	SQLiteDatabase
    //   77	17	5	localSQLiteException1	SQLiteException
    //   132	1	5	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   17	49	77	android/database/sqlite/SQLiteException
    //   17	49	112	finally
    //   55	63	128	finally
    //   85	98	128	finally
    //   55	63	132	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  private boolean zza(String paramString, int paramInt, zzwa.zzb paramzzb)
  {
    zzacj();
    zzzx();
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    com.google.android.gms.common.internal.zzaa.zzy(paramzzb);
    if (TextUtils.isEmpty(paramzzb.awe))
    {
      zzbwb().zzbxa().zze("Event filter had no event name. Audience definition ignored. audienceId, filterId", Integer.valueOf(paramInt), String.valueOf(paramzzb.awd));
      return false;
    }
    try
    {
      byte[] arrayOfByte = new byte[paramzzb.cz()];
      Object localObject = zzart.zzbe(arrayOfByte);
      paramzzb.zza((zzart)localObject);
      ((zzart)localObject).cm();
      localObject = new ContentValues();
      ((ContentValues)localObject).put("app_id", paramString);
      ((ContentValues)localObject).put("audience_id", Integer.valueOf(paramInt));
      ((ContentValues)localObject).put("filter_id", paramzzb.awd);
      ((ContentValues)localObject).put("event_name", paramzzb.awe);
      ((ContentValues)localObject).put("data", arrayOfByte);
      return false;
    }
    catch (IOException paramString)
    {
      try
      {
        if (getWritableDatabase().insertWithOnConflict("event_filters", null, (ContentValues)localObject, 5) == -1L) {
          zzbwb().zzbwy().log("Failed to insert event filter (got -1)");
        }
        return true;
      }
      catch (SQLiteException paramString)
      {
        zzbwb().zzbwy().zzj("Error storing event filter", paramString);
      }
      paramString = paramString;
      zzbwb().zzbwy().zzj("Configuration loss. Failed to serialize event filter", paramString);
      return false;
    }
  }
  
  @WorkerThread
  private boolean zza(String paramString, int paramInt, zzwa.zze paramzze)
  {
    zzacj();
    zzzx();
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    com.google.android.gms.common.internal.zzaa.zzy(paramzze);
    if (TextUtils.isEmpty(paramzze.awt))
    {
      zzbwb().zzbxa().zze("Property filter had no property name. Audience definition ignored. audienceId, filterId", Integer.valueOf(paramInt), String.valueOf(paramzze.awd));
      return false;
    }
    try
    {
      byte[] arrayOfByte = new byte[paramzze.cz()];
      Object localObject = zzart.zzbe(arrayOfByte);
      paramzze.zza((zzart)localObject);
      ((zzart)localObject).cm();
      localObject = new ContentValues();
      ((ContentValues)localObject).put("app_id", paramString);
      ((ContentValues)localObject).put("audience_id", Integer.valueOf(paramInt));
      ((ContentValues)localObject).put("filter_id", paramzze.awd);
      ((ContentValues)localObject).put("property_name", paramzze.awt);
      ((ContentValues)localObject).put("data", arrayOfByte);
      try
      {
        if (getWritableDatabase().insertWithOnConflict("property_filters", null, (ContentValues)localObject, 5) == -1L)
        {
          zzbwb().zzbwy().log("Failed to insert property filter (got -1)");
          return false;
        }
      }
      catch (SQLiteException paramString)
      {
        zzbwb().zzbwy().zzj("Error storing property filter", paramString);
        return false;
      }
      return true;
    }
    catch (IOException paramString)
    {
      zzbwb().zzbwy().zzj("Configuration loss. Failed to serialize property filter", paramString);
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
      zzbwb().zzbwy().zze("Database error", paramString, paramArrayOfString);
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
  static Set<String> zzb(SQLiteDatabase paramSQLiteDatabase, String paramString)
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
  
  private boolean zzbwn()
  {
    return getContext().getDatabasePath(zzade()).exists();
  }
  
  @WorkerThread
  public void beginTransaction()
  {
    zzacj();
    getWritableDatabase().beginTransaction();
  }
  
  @WorkerThread
  public void endTransaction()
  {
    zzacj();
    getWritableDatabase().endTransaction();
  }
  
  @WorkerThread
  SQLiteDatabase getWritableDatabase()
  {
    zzzx();
    try
    {
      SQLiteDatabase localSQLiteDatabase = this.arn.getWritableDatabase();
      return localSQLiteDatabase;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzbwb().zzbxa().zzj("Error opening database", localSQLiteException);
      throw localSQLiteException;
    }
  }
  
  @WorkerThread
  public void setTransactionSuccessful()
  {
    zzacj();
    getWritableDatabase().setTransactionSuccessful();
  }
  
  /* Error */
  public long zza(zzwc.zze paramzze)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   4: aload_0
    //   5: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   8: aload_1
    //   9: invokestatic 391	com/google/android/gms/common/internal/zzaa:zzy	(Ljava/lang/Object;)Ljava/lang/Object;
    //   12: pop
    //   13: aload_1
    //   14: getfield 583	com/google/android/gms/internal/zzwc$zze:zzcs	Ljava/lang/String;
    //   17: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   20: pop
    //   21: aload_1
    //   22: invokevirtual 584	com/google/android/gms/internal/zzwc$zze:cz	()I
    //   25: newarray <illegal type>
    //   27: astore 4
    //   29: aload 4
    //   31: invokestatic 469	com/google/android/gms/internal/zzart:zzbe	([B)Lcom/google/android/gms/internal/zzart;
    //   34: astore 5
    //   36: aload_1
    //   37: aload 5
    //   39: invokevirtual 585	com/google/android/gms/internal/zzwc$zze:zza	(Lcom/google/android/gms/internal/zzart;)V
    //   42: aload 5
    //   44: invokevirtual 475	com/google/android/gms/internal/zzart:cm	()V
    //   47: aload_0
    //   48: invokevirtual 589	com/google/android/gms/measurement/internal/zze:zzbvx	()Lcom/google/android/gms/measurement/internal/zzal;
    //   51: aload 4
    //   53: invokevirtual 595	com/google/android/gms/measurement/internal/zzal:zzz	([B)J
    //   56: lstore_2
    //   57: new 477	android/content/ContentValues
    //   60: dup
    //   61: invokespecial 479	android/content/ContentValues:<init>	()V
    //   64: astore 5
    //   66: aload 5
    //   68: ldc_w 481
    //   71: aload_1
    //   72: getfield 583	com/google/android/gms/internal/zzwc$zze:zzcs	Ljava/lang/String;
    //   75: invokevirtual 484	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   78: aload 5
    //   80: ldc_w 597
    //   83: lload_2
    //   84: invokestatic 602	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   87: invokevirtual 605	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   90: aload 5
    //   92: ldc_w 607
    //   95: aload 4
    //   97: invokevirtual 498	android/content/ContentValues:put	(Ljava/lang/String;[B)V
    //   100: aload_0
    //   101: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   104: ldc_w 609
    //   107: aconst_null
    //   108: aload 5
    //   110: iconst_4
    //   111: invokevirtual 504	android/database/sqlite/SQLiteDatabase:insertWithOnConflict	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    //   114: pop2
    //   115: lload_2
    //   116: lreturn
    //   117: astore_1
    //   118: aload_0
    //   119: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   122: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   125: ldc_w 611
    //   128: aload_1
    //   129: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   132: aload_1
    //   133: athrow
    //   134: astore_1
    //   135: aload_0
    //   136: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   139: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   142: ldc_w 613
    //   145: aload_1
    //   146: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   149: aload_1
    //   150: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	151	0	this	zze
    //   0	151	1	paramzze	zzwc.zze
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
    //   1: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   9: aload_0
    //   10: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   13: new 6	com/google/android/gms/measurement/internal/zze$zza
    //   16: dup
    //   17: invokespecial 615	com/google/android/gms/measurement/internal/zze$zza:<init>	()V
    //   20: astore 11
    //   22: aload_0
    //   23: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   26: astore 12
    //   28: aload 12
    //   30: ldc_w 617
    //   33: bipush 6
    //   35: anewarray 303	java/lang/String
    //   38: dup
    //   39: iconst_0
    //   40: ldc 65
    //   42: aastore
    //   43: dup
    //   44: iconst_1
    //   45: ldc 73
    //   47: aastore
    //   48: dup
    //   49: iconst_2
    //   50: ldc 69
    //   52: aastore
    //   53: dup
    //   54: iconst_3
    //   55: ldc 77
    //   57: aastore
    //   58: dup
    //   59: iconst_4
    //   60: ldc 101
    //   62: aastore
    //   63: dup
    //   64: iconst_5
    //   65: ldc 105
    //   67: aastore
    //   68: ldc_w 619
    //   71: iconst_1
    //   72: anewarray 303	java/lang/String
    //   75: dup
    //   76: iconst_0
    //   77: aload_3
    //   78: aastore
    //   79: aconst_null
    //   80: aconst_null
    //   81: aconst_null
    //   82: invokevirtual 444	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   85: astore 10
    //   87: aload 10
    //   89: astore 9
    //   91: aload 10
    //   93: invokeinterface 216 1 0
    //   98: ifne +36 -> 134
    //   101: aload 10
    //   103: astore 9
    //   105: aload_0
    //   106: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   109: invokevirtual 263	com/google/android/gms/measurement/internal/zzq:zzbxa	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   112: ldc_w 621
    //   115: aload_3
    //   116: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   119: aload 10
    //   121: ifnull +10 -> 131
    //   124: aload 10
    //   126: invokeinterface 223 1 0
    //   131: aload 11
    //   133: areturn
    //   134: aload 10
    //   136: astore 9
    //   138: aload 10
    //   140: iconst_0
    //   141: invokeinterface 220 2 0
    //   146: lload_1
    //   147: lcmp
    //   148: ifne +88 -> 236
    //   151: aload 10
    //   153: astore 9
    //   155: aload 11
    //   157: aload 10
    //   159: iconst_1
    //   160: invokeinterface 220 2 0
    //   165: putfield 625	com/google/android/gms/measurement/internal/zze$zza:arq	J
    //   168: aload 10
    //   170: astore 9
    //   172: aload 11
    //   174: aload 10
    //   176: iconst_2
    //   177: invokeinterface 220 2 0
    //   182: putfield 628	com/google/android/gms/measurement/internal/zze$zza:arp	J
    //   185: aload 10
    //   187: astore 9
    //   189: aload 11
    //   191: aload 10
    //   193: iconst_3
    //   194: invokeinterface 220 2 0
    //   199: putfield 631	com/google/android/gms/measurement/internal/zze$zza:arr	J
    //   202: aload 10
    //   204: astore 9
    //   206: aload 11
    //   208: aload 10
    //   210: iconst_4
    //   211: invokeinterface 220 2 0
    //   216: putfield 634	com/google/android/gms/measurement/internal/zze$zza:ars	J
    //   219: aload 10
    //   221: astore 9
    //   223: aload 11
    //   225: aload 10
    //   227: iconst_5
    //   228: invokeinterface 220 2 0
    //   233: putfield 637	com/google/android/gms/measurement/internal/zze$zza:art	J
    //   236: iload 4
    //   238: ifeq +19 -> 257
    //   241: aload 10
    //   243: astore 9
    //   245: aload 11
    //   247: aload 11
    //   249: getfield 625	com/google/android/gms/measurement/internal/zze$zza:arq	J
    //   252: lconst_1
    //   253: ladd
    //   254: putfield 625	com/google/android/gms/measurement/internal/zze$zza:arq	J
    //   257: iload 5
    //   259: ifeq +19 -> 278
    //   262: aload 10
    //   264: astore 9
    //   266: aload 11
    //   268: aload 11
    //   270: getfield 628	com/google/android/gms/measurement/internal/zze$zza:arp	J
    //   273: lconst_1
    //   274: ladd
    //   275: putfield 628	com/google/android/gms/measurement/internal/zze$zza:arp	J
    //   278: iload 6
    //   280: ifeq +19 -> 299
    //   283: aload 10
    //   285: astore 9
    //   287: aload 11
    //   289: aload 11
    //   291: getfield 631	com/google/android/gms/measurement/internal/zze$zza:arr	J
    //   294: lconst_1
    //   295: ladd
    //   296: putfield 631	com/google/android/gms/measurement/internal/zze$zza:arr	J
    //   299: iload 7
    //   301: ifeq +19 -> 320
    //   304: aload 10
    //   306: astore 9
    //   308: aload 11
    //   310: aload 11
    //   312: getfield 634	com/google/android/gms/measurement/internal/zze$zza:ars	J
    //   315: lconst_1
    //   316: ladd
    //   317: putfield 634	com/google/android/gms/measurement/internal/zze$zza:ars	J
    //   320: iload 8
    //   322: ifeq +19 -> 341
    //   325: aload 10
    //   327: astore 9
    //   329: aload 11
    //   331: aload 11
    //   333: getfield 637	com/google/android/gms/measurement/internal/zze$zza:art	J
    //   336: lconst_1
    //   337: ladd
    //   338: putfield 637	com/google/android/gms/measurement/internal/zze$zza:art	J
    //   341: aload 10
    //   343: astore 9
    //   345: new 477	android/content/ContentValues
    //   348: dup
    //   349: invokespecial 479	android/content/ContentValues:<init>	()V
    //   352: astore 13
    //   354: aload 10
    //   356: astore 9
    //   358: aload 13
    //   360: ldc 65
    //   362: lload_1
    //   363: invokestatic 602	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   366: invokevirtual 605	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   369: aload 10
    //   371: astore 9
    //   373: aload 13
    //   375: ldc 69
    //   377: aload 11
    //   379: getfield 628	com/google/android/gms/measurement/internal/zze$zza:arp	J
    //   382: invokestatic 602	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   385: invokevirtual 605	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   388: aload 10
    //   390: astore 9
    //   392: aload 13
    //   394: ldc 73
    //   396: aload 11
    //   398: getfield 625	com/google/android/gms/measurement/internal/zze$zza:arq	J
    //   401: invokestatic 602	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   404: invokevirtual 605	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   407: aload 10
    //   409: astore 9
    //   411: aload 13
    //   413: ldc 77
    //   415: aload 11
    //   417: getfield 631	com/google/android/gms/measurement/internal/zze$zza:arr	J
    //   420: invokestatic 602	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   423: invokevirtual 605	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   426: aload 10
    //   428: astore 9
    //   430: aload 13
    //   432: ldc 101
    //   434: aload 11
    //   436: getfield 634	com/google/android/gms/measurement/internal/zze$zza:ars	J
    //   439: invokestatic 602	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   442: invokevirtual 605	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   445: aload 10
    //   447: astore 9
    //   449: aload 13
    //   451: ldc 105
    //   453: aload 11
    //   455: getfield 637	com/google/android/gms/measurement/internal/zze$zza:art	J
    //   458: invokestatic 602	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   461: invokevirtual 605	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   464: aload 10
    //   466: astore 9
    //   468: aload 12
    //   470: ldc_w 617
    //   473: aload 13
    //   475: ldc_w 619
    //   478: iconst_1
    //   479: anewarray 303	java/lang/String
    //   482: dup
    //   483: iconst_0
    //   484: aload_3
    //   485: aastore
    //   486: invokevirtual 641	android/database/sqlite/SQLiteDatabase:update	(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   489: pop
    //   490: aload 10
    //   492: ifnull +10 -> 502
    //   495: aload 10
    //   497: invokeinterface 223 1 0
    //   502: aload 11
    //   504: areturn
    //   505: astore_3
    //   506: aconst_null
    //   507: astore 10
    //   509: aload 10
    //   511: astore 9
    //   513: aload_0
    //   514: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   517: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   520: ldc_w 643
    //   523: aload_3
    //   524: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   527: aload 10
    //   529: ifnull +10 -> 539
    //   532: aload 10
    //   534: invokeinterface 223 1 0
    //   539: aload 11
    //   541: areturn
    //   542: astore_3
    //   543: aconst_null
    //   544: astore 9
    //   546: aload 9
    //   548: ifnull +10 -> 558
    //   551: aload 9
    //   553: invokeinterface 223 1 0
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
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    com.google.android.gms.common.internal.zzaa.zzy(paramObject);
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
  public void zza(zzwc.zze paramzze, boolean paramBoolean)
  {
    zzzx();
    zzacj();
    com.google.android.gms.common.internal.zzaa.zzy(paramzze);
    com.google.android.gms.common.internal.zzaa.zzib(paramzze.zzcs);
    com.google.android.gms.common.internal.zzaa.zzy(paramzze.awZ);
    zzbwg();
    long l = zzabz().currentTimeMillis();
    if ((paramzze.awZ.longValue() < l - zzbwd().zzbuv()) || (paramzze.awZ.longValue() > zzbwd().zzbuv() + l)) {
      zzbwb().zzbxa().zze("Storing bundle outside of the max uploading time span. now, timestamp", Long.valueOf(l), paramzze.awZ);
    }
    for (;;)
    {
      try
      {
        byte[] arrayOfByte = new byte[paramzze.cz()];
        Object localObject = zzart.zzbe(arrayOfByte);
        paramzze.zza((zzart)localObject);
        ((zzart)localObject).cm();
        arrayOfByte = zzbvx().zzk(arrayOfByte);
        zzbwb().zzbxe().zzj("Saving bundle, size", Integer.valueOf(arrayOfByte.length));
        localObject = new ContentValues();
        ((ContentValues)localObject).put("app_id", paramzze.zzcs);
        ((ContentValues)localObject).put("bundle_end_timestamp", paramzze.awZ);
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
            zzbwb().zzbwy().log("Failed to insert bundle (got -1)");
          }
          return;
        }
        catch (SQLiteException paramzze)
        {
          zzbwb().zzbwy().zzj("Error storing bundle", paramzze);
        }
        paramzze = paramzze;
        zzbwb().zzbwy().zzj("Data loss. Failed to serialize bundle", paramzze);
        return;
      }
    }
  }
  
  @WorkerThread
  public void zza(zza paramzza)
  {
    com.google.android.gms.common.internal.zzaa.zzy(paramzza);
    zzzx();
    zzacj();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzza.zzup());
    localContentValues.put("app_instance_id", paramzza.zzazn());
    localContentValues.put("gmp_app_id", paramzza.zzbth());
    localContentValues.put("resettable_device_id_hash", paramzza.zzbti());
    localContentValues.put("last_bundle_index", Long.valueOf(paramzza.zzbtr()));
    localContentValues.put("last_bundle_start_timestamp", Long.valueOf(paramzza.zzbtk()));
    localContentValues.put("last_bundle_end_timestamp", Long.valueOf(paramzza.zzbtl()));
    localContentValues.put("app_version", paramzza.zzaaf());
    localContentValues.put("app_store", paramzza.zzbtn());
    localContentValues.put("gmp_version", Long.valueOf(paramzza.zzbto()));
    localContentValues.put("dev_cert_hash", Long.valueOf(paramzza.zzbtp()));
    localContentValues.put("measurement_enabled", Boolean.valueOf(paramzza.zzbtq()));
    localContentValues.put("day", Long.valueOf(paramzza.zzbtv()));
    localContentValues.put("daily_public_events_count", Long.valueOf(paramzza.zzbtw()));
    localContentValues.put("daily_events_count", Long.valueOf(paramzza.zzbtx()));
    localContentValues.put("daily_conversions_count", Long.valueOf(paramzza.zzbty()));
    localContentValues.put("config_fetched_time", Long.valueOf(paramzza.zzbts()));
    localContentValues.put("failed_config_fetch_time", Long.valueOf(paramzza.zzbtt()));
    localContentValues.put("app_version_int", Long.valueOf(paramzza.zzbtm()));
    localContentValues.put("firebase_instance_id", paramzza.zzbtj());
    localContentValues.put("daily_error_events_count", Long.valueOf(paramzza.zzbua()));
    localContentValues.put("daily_realtime_events_count", Long.valueOf(paramzza.zzbtz()));
    try
    {
      if (getWritableDatabase().insertWithOnConflict("apps", null, localContentValues, 5) == -1L) {
        zzbwb().zzbwy().log("Failed to insert/update app (got -1)");
      }
      return;
    }
    catch (SQLiteException paramzza)
    {
      zzbwb().zzbwy().zzj("Error storing app", paramzza);
    }
  }
  
  /* Error */
  public void zza(zzh paramzzh, long paramLong, boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 6
    //   3: aload_0
    //   4: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   7: aload_0
    //   8: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   11: aload_1
    //   12: invokestatic 391	com/google/android/gms/common/internal/zzaa:zzy	(Ljava/lang/Object;)Ljava/lang/Object;
    //   15: pop
    //   16: aload_1
    //   17: getfield 799	com/google/android/gms/measurement/internal/zzh:zzctj	Ljava/lang/String;
    //   20: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   23: pop
    //   24: new 801	com/google/android/gms/internal/zzwc$zzb
    //   27: dup
    //   28: invokespecial 802	com/google/android/gms/internal/zzwc$zzb:<init>	()V
    //   31: astore 7
    //   33: aload 7
    //   35: aload_1
    //   36: getfield 805	com/google/android/gms/measurement/internal/zzh:arB	J
    //   39: invokestatic 602	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   42: putfield 808	com/google/android/gms/internal/zzwc$zzb:awP	Ljava/lang/Long;
    //   45: aload 7
    //   47: aload_1
    //   48: getfield 812	com/google/android/gms/measurement/internal/zzh:arC	Lcom/google/android/gms/measurement/internal/EventParams;
    //   51: invokevirtual 817	com/google/android/gms/measurement/internal/EventParams:size	()I
    //   54: anewarray 819	com/google/android/gms/internal/zzwc$zzc
    //   57: putfield 823	com/google/android/gms/internal/zzwc$zzb:awN	[Lcom/google/android/gms/internal/zzwc$zzc;
    //   60: aload_1
    //   61: getfield 812	com/google/android/gms/measurement/internal/zzh:arC	Lcom/google/android/gms/measurement/internal/EventParams;
    //   64: invokevirtual 824	com/google/android/gms/measurement/internal/EventParams:iterator	()Ljava/util/Iterator;
    //   67: astore 8
    //   69: iconst_0
    //   70: istore 5
    //   72: aload 8
    //   74: invokeinterface 348 1 0
    //   79: ifeq +72 -> 151
    //   82: aload 8
    //   84: invokeinterface 352 1 0
    //   89: checkcast 303	java/lang/String
    //   92: astore 10
    //   94: new 819	com/google/android/gms/internal/zzwc$zzc
    //   97: dup
    //   98: invokespecial 825	com/google/android/gms/internal/zzwc$zzc:<init>	()V
    //   101: astore 9
    //   103: aload 7
    //   105: getfield 823	com/google/android/gms/internal/zzwc$zzb:awN	[Lcom/google/android/gms/internal/zzwc$zzc;
    //   108: iload 5
    //   110: aload 9
    //   112: aastore
    //   113: aload 9
    //   115: aload 10
    //   117: putfield 827	com/google/android/gms/internal/zzwc$zzc:name	Ljava/lang/String;
    //   120: aload_1
    //   121: getfield 812	com/google/android/gms/measurement/internal/zzh:arC	Lcom/google/android/gms/measurement/internal/EventParams;
    //   124: aload 10
    //   126: invokevirtual 831	com/google/android/gms/measurement/internal/EventParams:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   129: astore 10
    //   131: aload_0
    //   132: invokevirtual 589	com/google/android/gms/measurement/internal/zze:zzbvx	()Lcom/google/android/gms/measurement/internal/zzal;
    //   135: aload 9
    //   137: aload 10
    //   139: invokevirtual 834	com/google/android/gms/measurement/internal/zzal:zza	(Lcom/google/android/gms/internal/zzwc$zzc;Ljava/lang/Object;)V
    //   142: iload 5
    //   144: iconst_1
    //   145: iadd
    //   146: istore 5
    //   148: goto -76 -> 72
    //   151: aload 7
    //   153: invokevirtual 835	com/google/android/gms/internal/zzwc$zzb:cz	()I
    //   156: newarray <illegal type>
    //   158: astore 8
    //   160: aload 8
    //   162: invokestatic 469	com/google/android/gms/internal/zzart:zzbe	([B)Lcom/google/android/gms/internal/zzart;
    //   165: astore 9
    //   167: aload 7
    //   169: aload 9
    //   171: invokevirtual 836	com/google/android/gms/internal/zzwc$zzb:zza	(Lcom/google/android/gms/internal/zzart;)V
    //   174: aload 9
    //   176: invokevirtual 475	com/google/android/gms/internal/zzart:cm	()V
    //   179: aload_0
    //   180: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   183: invokevirtual 686	com/google/android/gms/measurement/internal/zzq:zzbxe	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   186: ldc_w 838
    //   189: aload_1
    //   190: getfield 841	com/google/android/gms/measurement/internal/zzh:mName	Ljava/lang/String;
    //   193: aload 8
    //   195: arraylength
    //   196: invokestatic 460	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   199: invokevirtual 241	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   202: new 477	android/content/ContentValues
    //   205: dup
    //   206: invokespecial 479	android/content/ContentValues:<init>	()V
    //   209: astore 7
    //   211: aload 7
    //   213: ldc_w 481
    //   216: aload_1
    //   217: getfield 799	com/google/android/gms/measurement/internal/zzh:zzctj	Ljava/lang/String;
    //   220: invokevirtual 484	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   223: aload 7
    //   225: ldc_w 438
    //   228: aload_1
    //   229: getfield 841	com/google/android/gms/measurement/internal/zzh:mName	Ljava/lang/String;
    //   232: invokevirtual 484	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   235: aload 7
    //   237: ldc_w 843
    //   240: aload_1
    //   241: getfield 846	com/google/android/gms/measurement/internal/zzh:vO	J
    //   244: invokestatic 602	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   247: invokevirtual 605	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   250: aload 7
    //   252: ldc_w 597
    //   255: lload_2
    //   256: invokestatic 602	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   259: invokevirtual 605	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   262: aload 7
    //   264: ldc_w 495
    //   267: aload 8
    //   269: invokevirtual 498	android/content/ContentValues:put	(Ljava/lang/String;[B)V
    //   272: iload 6
    //   274: istore 5
    //   276: iload 4
    //   278: ifeq +6 -> 284
    //   281: iconst_1
    //   282: istore 5
    //   284: aload 7
    //   286: ldc 111
    //   288: iload 5
    //   290: invokestatic 460	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   293: invokevirtual 489	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   296: aload_0
    //   297: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   300: ldc_w 848
    //   303: aconst_null
    //   304: aload 7
    //   306: invokevirtual 696	android/database/sqlite/SQLiteDatabase:insert	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;)J
    //   309: ldc2_w 505
    //   312: lcmp
    //   313: ifne +16 -> 329
    //   316: aload_0
    //   317: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   320: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   323: ldc_w 850
    //   326: invokevirtual 268	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   329: return
    //   330: astore_1
    //   331: aload_0
    //   332: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   335: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   338: ldc_w 852
    //   341: aload_1
    //   342: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   345: return
    //   346: astore_1
    //   347: aload_0
    //   348: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   351: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   354: ldc_w 854
    //   357: aload_1
    //   358: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
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
    com.google.android.gms.common.internal.zzaa.zzy(paramzzi);
    zzzx();
    zzacj();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzi.zzctj);
    localContentValues.put("name", paramzzi.mName);
    localContentValues.put("lifetime_count", Long.valueOf(paramzzi.arD));
    localContentValues.put("current_bundle_count", Long.valueOf(paramzzi.arE));
    localContentValues.put("last_fire_timestamp", Long.valueOf(paramzzi.arF));
    try
    {
      if (getWritableDatabase().insertWithOnConflict("events", null, localContentValues, 5) == -1L) {
        zzbwb().zzbwy().log("Failed to insert/update event aggregates (got -1)");
      }
      return;
    }
    catch (SQLiteException paramzzi)
    {
      zzbwb().zzbwy().zzj("Error storing event aggregates", paramzzi);
    }
  }
  
  /* Error */
  void zza(String paramString, int paramInt, com.google.android.gms.internal.zzwc.zzf paramzzf)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   4: aload_0
    //   5: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   8: aload_1
    //   9: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_3
    //   14: invokestatic 391	com/google/android/gms/common/internal/zzaa:zzy	(Ljava/lang/Object;)Ljava/lang/Object;
    //   17: pop
    //   18: aload_3
    //   19: invokevirtual 884	com/google/android/gms/internal/zzwc$zzf:cz	()I
    //   22: newarray <illegal type>
    //   24: astore 4
    //   26: aload 4
    //   28: invokestatic 469	com/google/android/gms/internal/zzart:zzbe	([B)Lcom/google/android/gms/internal/zzart;
    //   31: astore 5
    //   33: aload_3
    //   34: aload 5
    //   36: invokevirtual 885	com/google/android/gms/internal/zzwc$zzf:zza	(Lcom/google/android/gms/internal/zzart;)V
    //   39: aload 5
    //   41: invokevirtual 475	com/google/android/gms/internal/zzart:cm	()V
    //   44: new 477	android/content/ContentValues
    //   47: dup
    //   48: invokespecial 479	android/content/ContentValues:<init>	()V
    //   51: astore_3
    //   52: aload_3
    //   53: ldc_w 481
    //   56: aload_1
    //   57: invokevirtual 484	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   60: aload_3
    //   61: ldc_w 486
    //   64: iload_2
    //   65: invokestatic 460	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   68: invokevirtual 489	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   71: aload_3
    //   72: ldc_w 887
    //   75: aload 4
    //   77: invokevirtual 498	android/content/ContentValues:put	(Ljava/lang/String;[B)V
    //   80: aload_0
    //   81: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   84: ldc_w 889
    //   87: aconst_null
    //   88: aload_3
    //   89: iconst_5
    //   90: invokevirtual 504	android/database/sqlite/SQLiteDatabase:insertWithOnConflict	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    //   93: ldc2_w 505
    //   96: lcmp
    //   97: ifne +16 -> 113
    //   100: aload_0
    //   101: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   104: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   107: ldc_w 891
    //   110: invokevirtual 268	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   113: return
    //   114: astore_1
    //   115: aload_0
    //   116: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   119: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   122: ldc_w 893
    //   125: aload_1
    //   126: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   129: return
    //   130: astore_1
    //   131: aload_0
    //   132: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   135: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   138: ldc_w 895
    //   141: aload_1
    //   142: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   145: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	146	0	this	zze
    //   0	146	1	paramString	String
    //   0	146	2	paramInt	int
    //   0	146	3	paramzzf	com.google.android.gms.internal.zzwc.zzf
    //   24	52	4	arrayOfByte	byte[]
    //   31	9	5	localzzart	zzart
    // Exception table:
    //   from	to	target	type
    //   18	44	114	java/io/IOException
    //   80	113	130	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  public void zza(String paramString, long paramLong1, long paramLong2, zzb paramzzb)
  {
    // Byte code:
    //   0: aload 6
    //   2: invokestatic 391	com/google/android/gms/common/internal/zzaa:zzy	(Ljava/lang/Object;)Ljava/lang/Object;
    //   5: pop
    //   6: aload_0
    //   7: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   10: aload_0
    //   11: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   14: aconst_null
    //   15: astore 13
    //   17: aconst_null
    //   18: astore 12
    //   20: aload 12
    //   22: astore 8
    //   24: aload 13
    //   26: astore 9
    //   28: aload_0
    //   29: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   32: astore 14
    //   34: aload 12
    //   36: astore 8
    //   38: aload 13
    //   40: astore 9
    //   42: aload_1
    //   43: invokestatic 455	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   46: ifeq +297 -> 343
    //   49: lload 4
    //   51: ldc2_w 505
    //   54: lcmp
    //   55: ifeq +118 -> 173
    //   58: aload 12
    //   60: astore 8
    //   62: aload 13
    //   64: astore 9
    //   66: iconst_2
    //   67: anewarray 303	java/lang/String
    //   70: dup
    //   71: iconst_0
    //   72: lload 4
    //   74: invokestatic 899	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   77: aastore
    //   78: dup
    //   79: iconst_1
    //   80: lload_2
    //   81: invokestatic 899	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   84: aastore
    //   85: astore_1
    //   86: goto +1068 -> 1154
    //   89: aload 12
    //   91: astore 8
    //   93: aload 13
    //   95: astore 9
    //   97: aload 14
    //   99: new 315	java/lang/StringBuilder
    //   102: dup
    //   103: aload 10
    //   105: invokestatic 319	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   108: invokevirtual 322	java/lang/String:length	()I
    //   111: sipush 148
    //   114: iadd
    //   115: invokespecial 323	java/lang/StringBuilder:<init>	(I)V
    //   118: ldc_w 901
    //   121: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   124: aload 10
    //   126: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   129: ldc_w 903
    //   132: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   135: invokevirtual 334	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   138: aload_1
    //   139: invokevirtual 212	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   142: astore_1
    //   143: aload_1
    //   144: astore 8
    //   146: aload_1
    //   147: astore 9
    //   149: aload_1
    //   150: invokeinterface 216 1 0
    //   155: istore 7
    //   157: iload 7
    //   159: ifne +37 -> 196
    //   162: aload_1
    //   163: ifnull +9 -> 172
    //   166: aload_1
    //   167: invokeinterface 223 1 0
    //   172: return
    //   173: aload 12
    //   175: astore 8
    //   177: aload 13
    //   179: astore 9
    //   181: iconst_1
    //   182: anewarray 303	java/lang/String
    //   185: dup
    //   186: iconst_0
    //   187: lload_2
    //   188: invokestatic 899	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   191: aastore
    //   192: astore_1
    //   193: goto +961 -> 1154
    //   196: aload_1
    //   197: astore 8
    //   199: aload_1
    //   200: astore 9
    //   202: aload_1
    //   203: iconst_0
    //   204: invokeinterface 907 2 0
    //   209: astore 10
    //   211: aload_1
    //   212: astore 8
    //   214: aload_1
    //   215: astore 9
    //   217: aload_1
    //   218: iconst_1
    //   219: invokeinterface 907 2 0
    //   224: astore 11
    //   226: aload_1
    //   227: astore 8
    //   229: aload_1
    //   230: astore 9
    //   232: aload_1
    //   233: invokeinterface 223 1 0
    //   238: aload 11
    //   240: astore 9
    //   242: aload_1
    //   243: astore 8
    //   245: aload 8
    //   247: astore_1
    //   248: aload 14
    //   250: ldc_w 609
    //   253: iconst_1
    //   254: anewarray 303	java/lang/String
    //   257: dup
    //   258: iconst_0
    //   259: ldc_w 607
    //   262: aastore
    //   263: ldc_w 909
    //   266: iconst_2
    //   267: anewarray 303	java/lang/String
    //   270: dup
    //   271: iconst_0
    //   272: aload 10
    //   274: aastore
    //   275: dup
    //   276: iconst_1
    //   277: aload 9
    //   279: aastore
    //   280: aconst_null
    //   281: aconst_null
    //   282: ldc_w 911
    //   285: ldc_w 913
    //   288: invokevirtual 916	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   291: astore 11
    //   293: aload 11
    //   295: astore_1
    //   296: aload 11
    //   298: astore 8
    //   300: aload 11
    //   302: invokeinterface 216 1 0
    //   307: ifne +232 -> 539
    //   310: aload 11
    //   312: astore_1
    //   313: aload 11
    //   315: astore 8
    //   317: aload_0
    //   318: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   321: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   324: ldc_w 918
    //   327: invokevirtual 268	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   330: aload 11
    //   332: ifnull -160 -> 172
    //   335: aload 11
    //   337: invokeinterface 223 1 0
    //   342: return
    //   343: lload 4
    //   345: ldc2_w 505
    //   348: lcmp
    //   349: ifeq +122 -> 471
    //   352: aload 12
    //   354: astore 8
    //   356: aload 13
    //   358: astore 9
    //   360: iconst_2
    //   361: anewarray 303	java/lang/String
    //   364: dup
    //   365: iconst_0
    //   366: aload_1
    //   367: aastore
    //   368: dup
    //   369: iconst_1
    //   370: lload 4
    //   372: invokestatic 899	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   375: aastore
    //   376: astore 10
    //   378: goto +801 -> 1179
    //   381: aload 12
    //   383: astore 8
    //   385: aload 13
    //   387: astore 9
    //   389: aload 14
    //   391: new 315	java/lang/StringBuilder
    //   394: dup
    //   395: aload 11
    //   397: invokestatic 319	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   400: invokevirtual 322	java/lang/String:length	()I
    //   403: bipush 84
    //   405: iadd
    //   406: invokespecial 323	java/lang/StringBuilder:<init>	(I)V
    //   409: ldc_w 920
    //   412: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   415: aload 11
    //   417: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   420: ldc_w 922
    //   423: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   426: invokevirtual 334	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   429: aload 10
    //   431: invokevirtual 212	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   434: astore 10
    //   436: aload 10
    //   438: astore 8
    //   440: aload 10
    //   442: astore 9
    //   444: aload 10
    //   446: invokeinterface 216 1 0
    //   451: istore 7
    //   453: iload 7
    //   455: ifne +37 -> 492
    //   458: aload 10
    //   460: ifnull -288 -> 172
    //   463: aload 10
    //   465: invokeinterface 223 1 0
    //   470: return
    //   471: aload 12
    //   473: astore 8
    //   475: aload 13
    //   477: astore 9
    //   479: iconst_1
    //   480: anewarray 303	java/lang/String
    //   483: dup
    //   484: iconst_0
    //   485: aload_1
    //   486: aastore
    //   487: astore 10
    //   489: goto +690 -> 1179
    //   492: aload 10
    //   494: astore 8
    //   496: aload 10
    //   498: astore 9
    //   500: aload 10
    //   502: iconst_0
    //   503: invokeinterface 907 2 0
    //   508: astore 11
    //   510: aload 10
    //   512: astore 8
    //   514: aload 10
    //   516: astore 9
    //   518: aload 10
    //   520: invokeinterface 223 1 0
    //   525: aload 11
    //   527: astore 9
    //   529: aload 10
    //   531: astore 8
    //   533: aload_1
    //   534: astore 10
    //   536: goto -291 -> 245
    //   539: aload 11
    //   541: astore_1
    //   542: aload 11
    //   544: astore 8
    //   546: aload 11
    //   548: iconst_0
    //   549: invokeinterface 926 2 0
    //   554: invokestatic 932	com/google/android/gms/internal/zzars:zzbd	([B)Lcom/google/android/gms/internal/zzars;
    //   557: astore 13
    //   559: aload 11
    //   561: astore_1
    //   562: aload 11
    //   564: astore 8
    //   566: new 580	com/google/android/gms/internal/zzwc$zze
    //   569: dup
    //   570: invokespecial 933	com/google/android/gms/internal/zzwc$zze:<init>	()V
    //   573: astore 12
    //   575: aload 11
    //   577: astore_1
    //   578: aload 11
    //   580: astore 8
    //   582: aload 12
    //   584: aload 13
    //   586: invokevirtual 936	com/google/android/gms/internal/zzwc$zze:zzb	(Lcom/google/android/gms/internal/zzars;)Lcom/google/android/gms/internal/zzasa;
    //   589: checkcast 580	com/google/android/gms/internal/zzwc$zze
    //   592: astore 13
    //   594: aload 11
    //   596: astore_1
    //   597: aload 11
    //   599: astore 8
    //   601: aload 11
    //   603: invokeinterface 939 1 0
    //   608: ifeq +23 -> 631
    //   611: aload 11
    //   613: astore_1
    //   614: aload 11
    //   616: astore 8
    //   618: aload_0
    //   619: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   622: invokevirtual 263	com/google/android/gms/measurement/internal/zzq:zzbxa	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   625: ldc_w 941
    //   628: invokevirtual 268	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   631: aload 11
    //   633: astore_1
    //   634: aload 11
    //   636: astore 8
    //   638: aload 11
    //   640: invokeinterface 223 1 0
    //   645: aload 11
    //   647: astore_1
    //   648: aload 11
    //   650: astore 8
    //   652: aload 6
    //   654: aload 12
    //   656: invokeinterface 944 2 0
    //   661: lload 4
    //   663: ldc2_w 505
    //   666: lcmp
    //   667: ifeq +195 -> 862
    //   670: ldc_w 946
    //   673: astore 13
    //   675: aload 11
    //   677: astore_1
    //   678: aload 11
    //   680: astore 8
    //   682: iconst_3
    //   683: anewarray 303	java/lang/String
    //   686: astore 12
    //   688: aload 12
    //   690: iconst_0
    //   691: aload 10
    //   693: aastore
    //   694: aload 12
    //   696: iconst_1
    //   697: aload 9
    //   699: aastore
    //   700: aload 11
    //   702: astore_1
    //   703: aload 11
    //   705: astore 8
    //   707: aload 12
    //   709: iconst_2
    //   710: lload 4
    //   712: invokestatic 899	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   715: aastore
    //   716: aload 13
    //   718: astore 9
    //   720: aload 11
    //   722: astore_1
    //   723: aload 11
    //   725: astore 8
    //   727: aload 14
    //   729: ldc_w 848
    //   732: iconst_4
    //   733: anewarray 303	java/lang/String
    //   736: dup
    //   737: iconst_0
    //   738: ldc_w 911
    //   741: aastore
    //   742: dup
    //   743: iconst_1
    //   744: ldc_w 438
    //   747: aastore
    //   748: dup
    //   749: iconst_2
    //   750: ldc_w 843
    //   753: aastore
    //   754: dup
    //   755: iconst_3
    //   756: ldc_w 495
    //   759: aastore
    //   760: aload 9
    //   762: aload 12
    //   764: aconst_null
    //   765: aconst_null
    //   766: ldc_w 911
    //   769: aconst_null
    //   770: invokevirtual 916	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   773: astore 9
    //   775: aload 9
    //   777: astore_1
    //   778: aload_1
    //   779: astore 8
    //   781: aload_1
    //   782: astore 9
    //   784: aload_1
    //   785: invokeinterface 216 1 0
    //   790: ifne +148 -> 938
    //   793: aload_1
    //   794: astore 8
    //   796: aload_1
    //   797: astore 9
    //   799: aload_0
    //   800: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   803: invokevirtual 263	com/google/android/gms/measurement/internal/zzq:zzbxa	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   806: ldc_w 948
    //   809: invokevirtual 268	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   812: aload_1
    //   813: ifnull -641 -> 172
    //   816: aload_1
    //   817: invokeinterface 223 1 0
    //   822: return
    //   823: astore 6
    //   825: aload 11
    //   827: astore_1
    //   828: aload 11
    //   830: astore 8
    //   832: aload_0
    //   833: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   836: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   839: ldc_w 950
    //   842: aload 10
    //   844: aload 6
    //   846: invokevirtual 241	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   849: aload 11
    //   851: ifnull -679 -> 172
    //   854: aload 11
    //   856: invokeinterface 223 1 0
    //   861: return
    //   862: ldc_w 909
    //   865: astore 13
    //   867: aload 11
    //   869: astore_1
    //   870: aload 11
    //   872: astore 8
    //   874: iconst_2
    //   875: anewarray 303	java/lang/String
    //   878: astore 12
    //   880: aload 12
    //   882: iconst_0
    //   883: aload 10
    //   885: aastore
    //   886: aload 12
    //   888: iconst_1
    //   889: aload 9
    //   891: aastore
    //   892: aload 13
    //   894: astore 9
    //   896: goto -176 -> 720
    //   899: astore 6
    //   901: aload_1
    //   902: astore 9
    //   904: aload 6
    //   906: astore_1
    //   907: aload 9
    //   909: astore 8
    //   911: aload_0
    //   912: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   915: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   918: ldc_w 952
    //   921: aload_1
    //   922: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   925: aload 9
    //   927: ifnull -755 -> 172
    //   930: aload 9
    //   932: invokeinterface 223 1 0
    //   937: return
    //   938: aload_1
    //   939: astore 8
    //   941: aload_1
    //   942: astore 9
    //   944: aload_1
    //   945: iconst_0
    //   946: invokeinterface 220 2 0
    //   951: lstore_2
    //   952: aload_1
    //   953: astore 8
    //   955: aload_1
    //   956: astore 9
    //   958: aload_1
    //   959: iconst_3
    //   960: invokeinterface 926 2 0
    //   965: invokestatic 932	com/google/android/gms/internal/zzars:zzbd	([B)Lcom/google/android/gms/internal/zzars;
    //   968: astore 12
    //   970: aload_1
    //   971: astore 8
    //   973: aload_1
    //   974: astore 9
    //   976: new 801	com/google/android/gms/internal/zzwc$zzb
    //   979: dup
    //   980: invokespecial 802	com/google/android/gms/internal/zzwc$zzb:<init>	()V
    //   983: astore 11
    //   985: aload_1
    //   986: astore 8
    //   988: aload_1
    //   989: astore 9
    //   991: aload 11
    //   993: aload 12
    //   995: invokevirtual 953	com/google/android/gms/internal/zzwc$zzb:zzb	(Lcom/google/android/gms/internal/zzars;)Lcom/google/android/gms/internal/zzasa;
    //   998: checkcast 801	com/google/android/gms/internal/zzwc$zzb
    //   1001: astore 12
    //   1003: aload_1
    //   1004: astore 8
    //   1006: aload_1
    //   1007: astore 9
    //   1009: aload 11
    //   1011: aload_1
    //   1012: iconst_1
    //   1013: invokeinterface 907 2 0
    //   1018: putfield 954	com/google/android/gms/internal/zzwc$zzb:name	Ljava/lang/String;
    //   1021: aload_1
    //   1022: astore 8
    //   1024: aload_1
    //   1025: astore 9
    //   1027: aload 11
    //   1029: aload_1
    //   1030: iconst_2
    //   1031: invokeinterface 220 2 0
    //   1036: invokestatic 602	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   1039: putfield 957	com/google/android/gms/internal/zzwc$zzb:awO	Ljava/lang/Long;
    //   1042: aload_1
    //   1043: astore 8
    //   1045: aload_1
    //   1046: astore 9
    //   1048: aload 6
    //   1050: lload_2
    //   1051: aload 11
    //   1053: invokeinterface 960 4 0
    //   1058: istore 7
    //   1060: iload 7
    //   1062: ifne +39 -> 1101
    //   1065: aload_1
    //   1066: ifnull -894 -> 172
    //   1069: aload_1
    //   1070: invokeinterface 223 1 0
    //   1075: return
    //   1076: astore 11
    //   1078: aload_1
    //   1079: astore 8
    //   1081: aload_1
    //   1082: astore 9
    //   1084: aload_0
    //   1085: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   1088: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   1091: ldc_w 962
    //   1094: aload 10
    //   1096: aload 11
    //   1098: invokevirtual 241	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   1101: aload_1
    //   1102: astore 8
    //   1104: aload_1
    //   1105: astore 9
    //   1107: aload_1
    //   1108: invokeinterface 939 1 0
    //   1113: istore 7
    //   1115: iload 7
    //   1117: ifne -179 -> 938
    //   1120: aload_1
    //   1121: ifnull -949 -> 172
    //   1124: aload_1
    //   1125: invokeinterface 223 1 0
    //   1130: return
    //   1131: astore_1
    //   1132: aload 8
    //   1134: ifnull +10 -> 1144
    //   1137: aload 8
    //   1139: invokeinterface 223 1 0
    //   1144: aload_1
    //   1145: athrow
    //   1146: astore_1
    //   1147: goto -15 -> 1132
    //   1150: astore_1
    //   1151: goto -244 -> 907
    //   1154: lload 4
    //   1156: ldc2_w 505
    //   1159: lcmp
    //   1160: ifeq +11 -> 1171
    //   1163: ldc_w 964
    //   1166: astore 10
    //   1168: goto -1079 -> 89
    //   1171: ldc_w 966
    //   1174: astore 10
    //   1176: goto -1087 -> 89
    //   1179: lload 4
    //   1181: ldc2_w 505
    //   1184: lcmp
    //   1185: ifeq +11 -> 1196
    //   1188: ldc_w 968
    //   1191: astore 11
    //   1193: goto -812 -> 381
    //   1196: ldc_w 966
    //   1199: astore 11
    //   1201: goto -820 -> 381
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1204	0	this	zze
    //   0	1204	1	paramString	String
    //   0	1204	2	paramLong1	long
    //   0	1204	4	paramLong2	long
    //   0	1204	6	paramzzb	zzb
    //   155	961	7	bool	boolean
    //   22	1116	8	localObject1	Object
    //   26	1080	9	localObject2	Object
    //   103	1072	10	localObject3	Object
    //   224	828	11	localObject4	Object
    //   1076	21	11	localIOException	IOException
    //   1191	9	11	str	String
    //   18	984	12	localObject5	Object
    //   15	878	13	localObject6	Object
    //   32	696	14	localSQLiteDatabase	SQLiteDatabase
    // Exception table:
    //   from	to	target	type
    //   582	594	823	java/io/IOException
    //   248	293	899	android/database/sqlite/SQLiteException
    //   300	310	899	android/database/sqlite/SQLiteException
    //   317	330	899	android/database/sqlite/SQLiteException
    //   546	559	899	android/database/sqlite/SQLiteException
    //   566	575	899	android/database/sqlite/SQLiteException
    //   582	594	899	android/database/sqlite/SQLiteException
    //   601	611	899	android/database/sqlite/SQLiteException
    //   618	631	899	android/database/sqlite/SQLiteException
    //   638	645	899	android/database/sqlite/SQLiteException
    //   652	661	899	android/database/sqlite/SQLiteException
    //   682	688	899	android/database/sqlite/SQLiteException
    //   707	716	899	android/database/sqlite/SQLiteException
    //   727	775	899	android/database/sqlite/SQLiteException
    //   832	849	899	android/database/sqlite/SQLiteException
    //   874	880	899	android/database/sqlite/SQLiteException
    //   991	1003	1076	java/io/IOException
    //   28	34	1131	finally
    //   42	49	1131	finally
    //   66	86	1131	finally
    //   97	143	1131	finally
    //   149	157	1131	finally
    //   181	193	1131	finally
    //   202	211	1131	finally
    //   217	226	1131	finally
    //   232	238	1131	finally
    //   360	378	1131	finally
    //   389	436	1131	finally
    //   444	453	1131	finally
    //   479	489	1131	finally
    //   500	510	1131	finally
    //   518	525	1131	finally
    //   784	793	1131	finally
    //   799	812	1131	finally
    //   911	925	1131	finally
    //   944	952	1131	finally
    //   958	970	1131	finally
    //   976	985	1131	finally
    //   991	1003	1131	finally
    //   1009	1021	1131	finally
    //   1027	1042	1131	finally
    //   1048	1060	1131	finally
    //   1084	1101	1131	finally
    //   1107	1115	1131	finally
    //   248	293	1146	finally
    //   300	310	1146	finally
    //   317	330	1146	finally
    //   546	559	1146	finally
    //   566	575	1146	finally
    //   582	594	1146	finally
    //   601	611	1146	finally
    //   618	631	1146	finally
    //   638	645	1146	finally
    //   652	661	1146	finally
    //   682	688	1146	finally
    //   707	716	1146	finally
    //   727	775	1146	finally
    //   832	849	1146	finally
    //   874	880	1146	finally
    //   28	34	1150	android/database/sqlite/SQLiteException
    //   42	49	1150	android/database/sqlite/SQLiteException
    //   66	86	1150	android/database/sqlite/SQLiteException
    //   97	143	1150	android/database/sqlite/SQLiteException
    //   149	157	1150	android/database/sqlite/SQLiteException
    //   181	193	1150	android/database/sqlite/SQLiteException
    //   202	211	1150	android/database/sqlite/SQLiteException
    //   217	226	1150	android/database/sqlite/SQLiteException
    //   232	238	1150	android/database/sqlite/SQLiteException
    //   360	378	1150	android/database/sqlite/SQLiteException
    //   389	436	1150	android/database/sqlite/SQLiteException
    //   444	453	1150	android/database/sqlite/SQLiteException
    //   479	489	1150	android/database/sqlite/SQLiteException
    //   500	510	1150	android/database/sqlite/SQLiteException
    //   518	525	1150	android/database/sqlite/SQLiteException
    //   784	793	1150	android/database/sqlite/SQLiteException
    //   799	812	1150	android/database/sqlite/SQLiteException
    //   944	952	1150	android/database/sqlite/SQLiteException
    //   958	970	1150	android/database/sqlite/SQLiteException
    //   976	985	1150	android/database/sqlite/SQLiteException
    //   991	1003	1150	android/database/sqlite/SQLiteException
    //   1009	1021	1150	android/database/sqlite/SQLiteException
    //   1027	1042	1150	android/database/sqlite/SQLiteException
    //   1048	1060	1150	android/database/sqlite/SQLiteException
    //   1084	1101	1150	android/database/sqlite/SQLiteException
    //   1107	1115	1150	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  public boolean zza(zzak paramzzak)
  {
    com.google.android.gms.common.internal.zzaa.zzy(paramzzak);
    zzzx();
    zzacj();
    if (zzar(paramzzak.zzctj, paramzzak.mName) == null)
    {
      long l;
      if (zzal.zzmu(paramzzak.mName))
      {
        l = zzb("select count(1) from user_attributes where app_id=? and name not like '!_%' escape '!'", new String[] { paramzzak.zzctj });
        zzbwd().zzbun();
        if (l < 25L) {}
      }
      else
      {
        do
        {
          return false;
          l = zzb("select count(1) from user_attributes where app_id=?", new String[] { paramzzak.zzctj });
          zzbwd().zzbuo();
        } while (l >= 50L);
      }
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzak.zzctj);
    localContentValues.put("name", paramzzak.mName);
    localContentValues.put("set_timestamp", Long.valueOf(paramzzak.avX));
    zza(localContentValues, "value", paramzzak.zzcyd);
    try
    {
      if (getWritableDatabase().insertWithOnConflict("user_attributes", null, localContentValues, 5) == -1L) {
        zzbwb().zzbwy().log("Failed to insert/update user property (got -1)");
      }
      return true;
    }
    catch (SQLiteException paramzzak)
    {
      for (;;)
      {
        zzbwb().zzbwy().zzj("Error storing user property", paramzzak);
      }
    }
  }
  
  @WorkerThread
  void zzaa(String paramString, int paramInt)
  {
    zzacj();
    zzzx();
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.delete("property_filters", "app_id=? and audience_id=?", new String[] { paramString, String.valueOf(paramInt) });
    localSQLiteDatabase.delete("event_filters", "app_id=? and audience_id=?", new String[] { paramString, String.valueOf(paramInt) });
  }
  
  String zzade()
  {
    return zzbwd().zzafe();
  }
  
  public void zzaf(List<Long> paramList)
  {
    com.google.android.gms.common.internal.zzaa.zzy(paramList);
    zzzx();
    zzacj();
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
      zzbwb().zzbwy().zze("Deleted fewer rows from raw events table than expected", Integer.valueOf(i), Integer.valueOf(paramList.size()));
    }
  }
  
  /* Error */
  @WorkerThread
  public zzi zzap(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aload_1
    //   4: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_2
    //   9: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   17: aload_0
    //   18: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   21: aload_0
    //   22: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   25: ldc_w 876
    //   28: iconst_3
    //   29: anewarray 303	java/lang/String
    //   32: dup
    //   33: iconst_0
    //   34: ldc_w 861
    //   37: aastore
    //   38: dup
    //   39: iconst_1
    //   40: ldc_w 866
    //   43: aastore
    //   44: dup
    //   45: iconst_2
    //   46: ldc_w 871
    //   49: aastore
    //   50: ldc_w 1049
    //   53: iconst_2
    //   54: anewarray 303	java/lang/String
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
    //   68: invokevirtual 444	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   71: astore 4
    //   73: aload 4
    //   75: invokeinterface 216 1 0
    //   80: istore_3
    //   81: iload_3
    //   82: ifne +19 -> 101
    //   85: aload 4
    //   87: ifnull +10 -> 97
    //   90: aload 4
    //   92: invokeinterface 223 1 0
    //   97: aconst_null
    //   98: astore_1
    //   99: aload_1
    //   100: areturn
    //   101: new 857	com/google/android/gms/measurement/internal/zzi
    //   104: dup
    //   105: aload_1
    //   106: aload_2
    //   107: aload 4
    //   109: iconst_0
    //   110: invokeinterface 220 2 0
    //   115: aload 4
    //   117: iconst_1
    //   118: invokeinterface 220 2 0
    //   123: aload 4
    //   125: iconst_2
    //   126: invokeinterface 220 2 0
    //   131: invokespecial 1052	com/google/android/gms/measurement/internal/zzi:<init>	(Ljava/lang/String;Ljava/lang/String;JJJ)V
    //   134: astore 5
    //   136: aload 4
    //   138: invokeinterface 939 1 0
    //   143: ifeq +16 -> 159
    //   146: aload_0
    //   147: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   150: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   153: ldc_w 1054
    //   156: invokevirtual 268	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   159: aload 5
    //   161: astore_1
    //   162: aload 4
    //   164: ifnull -65 -> 99
    //   167: aload 4
    //   169: invokeinterface 223 1 0
    //   174: aload 5
    //   176: areturn
    //   177: astore 5
    //   179: aconst_null
    //   180: astore 4
    //   182: aload_0
    //   183: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   186: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   189: ldc_w 1056
    //   192: aload_1
    //   193: aload_2
    //   194: aload 5
    //   196: invokevirtual 1060	com/google/android/gms/measurement/internal/zzq$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   199: aload 4
    //   201: ifnull +10 -> 211
    //   204: aload 4
    //   206: invokeinterface 223 1 0
    //   211: aconst_null
    //   212: areturn
    //   213: astore_1
    //   214: aload 5
    //   216: astore_2
    //   217: aload_2
    //   218: ifnull +9 -> 227
    //   221: aload_2
    //   222: invokeinterface 223 1 0
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
  public void zzaq(String paramString1, String paramString2)
  {
    com.google.android.gms.common.internal.zzaa.zzib(paramString1);
    com.google.android.gms.common.internal.zzaa.zzib(paramString2);
    zzzx();
    zzacj();
    try
    {
      int i = getWritableDatabase().delete("user_attributes", "app_id=? and name=?", new String[] { paramString1, paramString2 });
      zzbwb().zzbxe().zzj("Deleted user attribute rows:", Integer.valueOf(i));
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzbwb().zzbwy().zzd("Error deleting user attribute", paramString1, paramString2, localSQLiteException);
    }
  }
  
  /* Error */
  @WorkerThread
  public zzak zzar(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aload_1
    //   4: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_2
    //   9: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   17: aload_0
    //   18: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   21: aload_0
    //   22: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   25: ldc_w 1011
    //   28: iconst_2
    //   29: anewarray 303	java/lang/String
    //   32: dup
    //   33: iconst_0
    //   34: ldc_w 999
    //   37: aastore
    //   38: dup
    //   39: iconst_1
    //   40: ldc_w 1003
    //   43: aastore
    //   44: ldc_w 1049
    //   47: iconst_2
    //   48: anewarray 303	java/lang/String
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
    //   62: invokevirtual 444	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   65: astore 4
    //   67: aload 4
    //   69: invokeinterface 216 1 0
    //   74: istore_3
    //   75: iload_3
    //   76: ifne +19 -> 95
    //   79: aload 4
    //   81: ifnull +10 -> 91
    //   84: aload 4
    //   86: invokeinterface 223 1 0
    //   91: aconst_null
    //   92: astore_1
    //   93: aload_1
    //   94: areturn
    //   95: new 971	com/google/android/gms/measurement/internal/zzak
    //   98: dup
    //   99: aload_1
    //   100: aload_2
    //   101: aload 4
    //   103: iconst_0
    //   104: invokeinterface 220 2 0
    //   109: aload_0
    //   110: aload 4
    //   112: iconst_1
    //   113: invokevirtual 1068	com/google/android/gms/measurement/internal/zze:zzb	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   116: invokespecial 1071	com/google/android/gms/measurement/internal/zzak:<init>	(Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   119: astore 5
    //   121: aload 4
    //   123: invokeinterface 939 1 0
    //   128: ifeq +16 -> 144
    //   131: aload_0
    //   132: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   135: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   138: ldc_w 1073
    //   141: invokevirtual 268	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   144: aload 5
    //   146: astore_1
    //   147: aload 4
    //   149: ifnull -56 -> 93
    //   152: aload 4
    //   154: invokeinterface 223 1 0
    //   159: aload 5
    //   161: areturn
    //   162: astore 5
    //   164: aconst_null
    //   165: astore 4
    //   167: aload_0
    //   168: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   171: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   174: ldc_w 1075
    //   177: aload_1
    //   178: aload_2
    //   179: aload 5
    //   181: invokevirtual 1060	com/google/android/gms/measurement/internal/zzq$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   184: aload 4
    //   186: ifnull +10 -> 196
    //   189: aload 4
    //   191: invokeinterface 223 1 0
    //   196: aconst_null
    //   197: areturn
    //   198: astore_1
    //   199: aload 5
    //   201: astore_2
    //   202: aload_2
    //   203: ifnull +9 -> 212
    //   206: aload_2
    //   207: invokeinterface 223 1 0
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
  Map<Integer, List<zzwa.zzb>> zzas(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   4: aload_0
    //   5: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   8: aload_1
    //   9: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_2
    //   14: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   17: pop
    //   18: new 27	android/support/v4/util/ArrayMap
    //   21: dup
    //   22: invokespecial 1078	android/support/v4/util/ArrayMap:<init>	()V
    //   25: astore 8
    //   27: aload_0
    //   28: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   31: astore 5
    //   33: aload 5
    //   35: ldc_w 500
    //   38: iconst_2
    //   39: anewarray 303	java/lang/String
    //   42: dup
    //   43: iconst_0
    //   44: ldc_w 486
    //   47: aastore
    //   48: dup
    //   49: iconst_1
    //   50: ldc_w 495
    //   53: aastore
    //   54: ldc_w 1080
    //   57: iconst_2
    //   58: anewarray 303	java/lang/String
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
    //   72: invokevirtual 444	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   75: astore 5
    //   77: aload 5
    //   79: astore_2
    //   80: aload 5
    //   82: invokeinterface 216 1 0
    //   87: ifne +24 -> 111
    //   90: aload 5
    //   92: astore_2
    //   93: invokestatic 1083	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   96: astore_1
    //   97: aload 5
    //   99: ifnull +10 -> 109
    //   102: aload 5
    //   104: invokeinterface 223 1 0
    //   109: aload_1
    //   110: areturn
    //   111: aload 5
    //   113: astore_2
    //   114: aload 5
    //   116: iconst_1
    //   117: invokeinterface 926 2 0
    //   122: invokestatic 932	com/google/android/gms/internal/zzars:zzbd	([B)Lcom/google/android/gms/internal/zzars;
    //   125: astore 6
    //   127: aload 5
    //   129: astore_2
    //   130: new 414	com/google/android/gms/internal/zzwa$zzb
    //   133: dup
    //   134: invokespecial 1084	com/google/android/gms/internal/zzwa$zzb:<init>	()V
    //   137: astore 9
    //   139: aload 5
    //   141: astore_2
    //   142: aload 9
    //   144: aload 6
    //   146: invokevirtual 1085	com/google/android/gms/internal/zzwa$zzb:zzb	(Lcom/google/android/gms/internal/zzars;)Lcom/google/android/gms/internal/zzasa;
    //   149: checkcast 414	com/google/android/gms/internal/zzwa$zzb
    //   152: astore 6
    //   154: aload 5
    //   156: astore_2
    //   157: aload 5
    //   159: iconst_0
    //   160: invokeinterface 1088 2 0
    //   165: istore_3
    //   166: aload 5
    //   168: astore_2
    //   169: aload 8
    //   171: iload_3
    //   172: invokestatic 460	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   175: invokeinterface 1090 2 0
    //   180: checkcast 1033	java/util/List
    //   183: astore 7
    //   185: aload 7
    //   187: astore 6
    //   189: aload 7
    //   191: ifnonnull +32 -> 223
    //   194: aload 5
    //   196: astore_2
    //   197: new 1092	java/util/ArrayList
    //   200: dup
    //   201: invokespecial 1093	java/util/ArrayList:<init>	()V
    //   204: astore 6
    //   206: aload 5
    //   208: astore_2
    //   209: aload 8
    //   211: iload_3
    //   212: invokestatic 460	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   215: aload 6
    //   217: invokeinterface 43 3 0
    //   222: pop
    //   223: aload 5
    //   225: astore_2
    //   226: aload 6
    //   228: aload 9
    //   230: invokeinterface 1096 2 0
    //   235: pop
    //   236: aload 5
    //   238: astore_2
    //   239: aload 5
    //   241: invokeinterface 939 1 0
    //   246: istore 4
    //   248: iload 4
    //   250: ifne -139 -> 111
    //   253: aload 5
    //   255: ifnull +10 -> 265
    //   258: aload 5
    //   260: invokeinterface 223 1 0
    //   265: aload 8
    //   267: areturn
    //   268: astore 6
    //   270: aload 5
    //   272: astore_2
    //   273: aload_0
    //   274: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   277: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   280: ldc_w 1098
    //   283: aload_1
    //   284: aload 6
    //   286: invokevirtual 241	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   289: goto -53 -> 236
    //   292: astore_1
    //   293: aload 5
    //   295: astore_2
    //   296: aload_0
    //   297: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   300: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   303: ldc_w 1100
    //   306: aload_1
    //   307: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   310: aload 5
    //   312: ifnull +10 -> 322
    //   315: aload 5
    //   317: invokeinterface 223 1 0
    //   322: aconst_null
    //   323: areturn
    //   324: astore_1
    //   325: aconst_null
    //   326: astore_2
    //   327: aload_2
    //   328: ifnull +9 -> 337
    //   331: aload_2
    //   332: invokeinterface 223 1 0
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
    //   137	92	9	localzzb	zzwa.zzb
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
  Map<Integer, List<zzwa.zze>> zzat(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   4: aload_0
    //   5: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   8: aload_1
    //   9: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_2
    //   14: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   17: pop
    //   18: new 27	android/support/v4/util/ArrayMap
    //   21: dup
    //   22: invokespecial 1078	android/support/v4/util/ArrayMap:<init>	()V
    //   25: astore 8
    //   27: aload_0
    //   28: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   31: astore 5
    //   33: aload 5
    //   35: ldc_w 523
    //   38: iconst_2
    //   39: anewarray 303	java/lang/String
    //   42: dup
    //   43: iconst_0
    //   44: ldc_w 486
    //   47: aastore
    //   48: dup
    //   49: iconst_1
    //   50: ldc_w 495
    //   53: aastore
    //   54: ldc_w 1104
    //   57: iconst_2
    //   58: anewarray 303	java/lang/String
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
    //   72: invokevirtual 444	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   75: astore 5
    //   77: aload 5
    //   79: astore_2
    //   80: aload 5
    //   82: invokeinterface 216 1 0
    //   87: ifne +24 -> 111
    //   90: aload 5
    //   92: astore_2
    //   93: invokestatic 1083	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   96: astore_1
    //   97: aload 5
    //   99: ifnull +10 -> 109
    //   102: aload 5
    //   104: invokeinterface 223 1 0
    //   109: aload_1
    //   110: areturn
    //   111: aload 5
    //   113: astore_2
    //   114: aload 5
    //   116: iconst_1
    //   117: invokeinterface 926 2 0
    //   122: invokestatic 932	com/google/android/gms/internal/zzars:zzbd	([B)Lcom/google/android/gms/internal/zzars;
    //   125: astore 6
    //   127: aload 5
    //   129: astore_2
    //   130: new 421	com/google/android/gms/internal/zzwa$zze
    //   133: dup
    //   134: invokespecial 1105	com/google/android/gms/internal/zzwa$zze:<init>	()V
    //   137: astore 9
    //   139: aload 5
    //   141: astore_2
    //   142: aload 9
    //   144: aload 6
    //   146: invokevirtual 1106	com/google/android/gms/internal/zzwa$zze:zzb	(Lcom/google/android/gms/internal/zzars;)Lcom/google/android/gms/internal/zzasa;
    //   149: checkcast 421	com/google/android/gms/internal/zzwa$zze
    //   152: astore 6
    //   154: aload 5
    //   156: astore_2
    //   157: aload 5
    //   159: iconst_0
    //   160: invokeinterface 1088 2 0
    //   165: istore_3
    //   166: aload 5
    //   168: astore_2
    //   169: aload 8
    //   171: iload_3
    //   172: invokestatic 460	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   175: invokeinterface 1090 2 0
    //   180: checkcast 1033	java/util/List
    //   183: astore 7
    //   185: aload 7
    //   187: astore 6
    //   189: aload 7
    //   191: ifnonnull +32 -> 223
    //   194: aload 5
    //   196: astore_2
    //   197: new 1092	java/util/ArrayList
    //   200: dup
    //   201: invokespecial 1093	java/util/ArrayList:<init>	()V
    //   204: astore 6
    //   206: aload 5
    //   208: astore_2
    //   209: aload 8
    //   211: iload_3
    //   212: invokestatic 460	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   215: aload 6
    //   217: invokeinterface 43 3 0
    //   222: pop
    //   223: aload 5
    //   225: astore_2
    //   226: aload 6
    //   228: aload 9
    //   230: invokeinterface 1096 2 0
    //   235: pop
    //   236: aload 5
    //   238: astore_2
    //   239: aload 5
    //   241: invokeinterface 939 1 0
    //   246: istore 4
    //   248: iload 4
    //   250: ifne -139 -> 111
    //   253: aload 5
    //   255: ifnull +10 -> 265
    //   258: aload 5
    //   260: invokeinterface 223 1 0
    //   265: aload 8
    //   267: areturn
    //   268: astore 6
    //   270: aload 5
    //   272: astore_2
    //   273: aload_0
    //   274: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   277: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   280: ldc_w 1098
    //   283: aload_1
    //   284: aload 6
    //   286: invokevirtual 241	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   289: goto -53 -> 236
    //   292: astore_1
    //   293: aload 5
    //   295: astore_2
    //   296: aload_0
    //   297: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   300: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   303: ldc_w 1100
    //   306: aload_1
    //   307: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   310: aload 5
    //   312: ifnull +10 -> 322
    //   315: aload 5
    //   317: invokeinterface 223 1 0
    //   322: aconst_null
    //   323: areturn
    //   324: astore_1
    //   325: aconst_null
    //   326: astore_2
    //   327: aload_2
    //   328: ifnull +9 -> 337
    //   331: aload_2
    //   332: invokeinterface 223 1 0
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
    //   137	92	9	localzze	zzwa.zze
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
  @WorkerThread
  protected long zzau(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_2
    //   6: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   9: pop
    //   10: aload_0
    //   11: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   14: aload_0
    //   15: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   18: aload_0
    //   19: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   22: astore 7
    //   24: aload 7
    //   26: invokevirtual 568	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   29: aload_0
    //   30: new 315	java/lang/StringBuilder
    //   33: dup
    //   34: aload_2
    //   35: invokestatic 319	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   38: invokevirtual 322	java/lang/String:length	()I
    //   41: bipush 32
    //   43: iadd
    //   44: invokespecial 323	java/lang/StringBuilder:<init>	(I)V
    //   47: ldc_w 1111
    //   50: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: aload_2
    //   54: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: ldc_w 1113
    //   60: invokevirtual 329	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   63: invokevirtual 334	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   66: iconst_1
    //   67: anewarray 303	java/lang/String
    //   70: dup
    //   71: iconst_0
    //   72: aload_1
    //   73: aastore
    //   74: ldc2_w 505
    //   77: invokespecial 1115	com/google/android/gms/measurement/internal/zze:zza	(Ljava/lang/String;[Ljava/lang/String;J)J
    //   80: lstore 5
    //   82: lload 5
    //   84: lstore_3
    //   85: lload 5
    //   87: ldc2_w 505
    //   90: lcmp
    //   91: ifne +88 -> 179
    //   94: new 477	android/content/ContentValues
    //   97: dup
    //   98: invokespecial 479	android/content/ContentValues:<init>	()V
    //   101: astore 8
    //   103: aload 8
    //   105: ldc_w 481
    //   108: aload_1
    //   109: invokevirtual 484	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   112: aload 8
    //   114: ldc_w 1117
    //   117: iconst_0
    //   118: invokestatic 460	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   121: invokevirtual 489	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   124: aload 8
    //   126: ldc 123
    //   128: iconst_0
    //   129: invokestatic 460	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   132: invokevirtual 489	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   135: aload 7
    //   137: ldc_w 1119
    //   140: aconst_null
    //   141: aload 8
    //   143: iconst_5
    //   144: invokevirtual 504	android/database/sqlite/SQLiteDatabase:insertWithOnConflict	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    //   147: ldc2_w 505
    //   150: lcmp
    //   151: ifne +26 -> 177
    //   154: aload_0
    //   155: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   158: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   161: ldc_w 1121
    //   164: aload_2
    //   165: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   168: aload 7
    //   170: invokevirtual 571	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   173: ldc2_w 505
    //   176: lreturn
    //   177: lconst_0
    //   178: lstore_3
    //   179: new 477	android/content/ContentValues
    //   182: dup
    //   183: invokespecial 479	android/content/ContentValues:<init>	()V
    //   186: astore 8
    //   188: aload 8
    //   190: ldc_w 481
    //   193: aload_1
    //   194: invokevirtual 484	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   197: aload 8
    //   199: aload_2
    //   200: lconst_1
    //   201: lload_3
    //   202: ladd
    //   203: invokestatic 602	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   206: invokevirtual 605	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   209: aload 7
    //   211: ldc_w 1119
    //   214: aload 8
    //   216: ldc_w 1123
    //   219: iconst_1
    //   220: anewarray 303	java/lang/String
    //   223: dup
    //   224: iconst_0
    //   225: aload_1
    //   226: aastore
    //   227: invokevirtual 641	android/database/sqlite/SQLiteDatabase:update	(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   230: i2l
    //   231: lconst_0
    //   232: lcmp
    //   233: ifne +26 -> 259
    //   236: aload_0
    //   237: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   240: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   243: ldc_w 1125
    //   246: aload_2
    //   247: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   250: aload 7
    //   252: invokevirtual 571	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   255: ldc2_w 505
    //   258: lreturn
    //   259: aload 7
    //   261: invokevirtual 577	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   264: aload 7
    //   266: invokevirtual 571	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   269: lload_3
    //   270: lreturn
    //   271: astore_1
    //   272: lconst_0
    //   273: lstore_3
    //   274: aload_0
    //   275: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   278: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   281: ldc_w 1127
    //   284: aload_2
    //   285: aload_1
    //   286: invokevirtual 241	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   289: aload 7
    //   291: invokevirtual 571	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   294: lload_3
    //   295: lreturn
    //   296: astore_1
    //   297: aload 7
    //   299: invokevirtual 571	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   302: aload_1
    //   303: athrow
    //   304: astore_1
    //   305: goto -31 -> 274
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	308	0	this	zze
    //   0	308	1	paramString1	String
    //   0	308	2	paramString2	String
    //   84	211	3	l1	long
    //   80	6	5	l2	long
    //   22	276	7	localSQLiteDatabase	SQLiteDatabase
    //   101	114	8	localContentValues	ContentValues
    // Exception table:
    //   from	to	target	type
    //   29	82	271	android/database/sqlite/SQLiteException
    //   94	168	271	android/database/sqlite/SQLiteException
    //   29	82	296	finally
    //   94	168	296	finally
    //   179	250	296	finally
    //   259	264	296	finally
    //   274	289	296	finally
    //   179	250	304	android/database/sqlite/SQLiteException
    //   259	264	304	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  Object zzb(Cursor paramCursor, int paramInt)
  {
    int i = zza(paramCursor, paramInt);
    switch (i)
    {
    default: 
      zzbwb().zzbwy().zzj("Loaded invalid unknown value type, ignoring it", Integer.valueOf(i));
      return null;
    case 0: 
      zzbwb().zzbwy().log("Loaded invalid null value from database");
      return null;
    case 1: 
      return Long.valueOf(paramCursor.getLong(paramInt));
    case 2: 
      return Double.valueOf(paramCursor.getDouble(paramInt));
    case 3: 
      return paramCursor.getString(paramInt);
    }
    zzbwb().zzbwy().log("Loaded invalid blob type value, ignoring it");
    return null;
  }
  
  @WorkerThread
  void zzb(String paramString, zzwa.zza[] paramArrayOfzza)
  {
    int j = 0;
    zzacj();
    zzzx();
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    com.google.android.gms.common.internal.zzaa.zzy(paramArrayOfzza);
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
        localArrayList.add(paramArrayOfzza[i].avZ);
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
  public void zzbj(long paramLong)
  {
    zzzx();
    zzacj();
    if (getWritableDatabase().delete("queue", "rowid=?", new String[] { String.valueOf(paramLong) }) != 1) {
      zzbwb().zzbwy().log("Deleted fewer rows from queue than expected");
    }
  }
  
  /* Error */
  public String zzbk(long paramLong)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aload_0
    //   4: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   7: aload_0
    //   8: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   11: aload_0
    //   12: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   15: ldc_w 1160
    //   18: iconst_1
    //   19: anewarray 303	java/lang/String
    //   22: dup
    //   23: iconst_0
    //   24: lload_1
    //   25: invokestatic 899	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   28: aastore
    //   29: invokevirtual 212	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   32: astore_3
    //   33: aload_3
    //   34: astore 4
    //   36: aload_3
    //   37: invokeinterface 216 1 0
    //   42: ifne +40 -> 82
    //   45: aload_3
    //   46: astore 4
    //   48: aload_0
    //   49: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   52: invokevirtual 686	com/google/android/gms/measurement/internal/zzq:zzbxe	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   55: ldc_w 1162
    //   58: invokevirtual 268	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   61: aload 6
    //   63: astore 4
    //   65: aload_3
    //   66: ifnull +13 -> 79
    //   69: aload_3
    //   70: invokeinterface 223 1 0
    //   75: aload 6
    //   77: astore 4
    //   79: aload 4
    //   81: areturn
    //   82: aload_3
    //   83: astore 4
    //   85: aload_3
    //   86: iconst_0
    //   87: invokeinterface 907 2 0
    //   92: astore 5
    //   94: aload 5
    //   96: astore 4
    //   98: aload_3
    //   99: ifnull -20 -> 79
    //   102: aload_3
    //   103: invokeinterface 223 1 0
    //   108: aload 5
    //   110: areturn
    //   111: astore 5
    //   113: aconst_null
    //   114: astore_3
    //   115: aload_3
    //   116: astore 4
    //   118: aload_0
    //   119: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   122: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   125: ldc_w 1164
    //   128: aload 5
    //   130: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   133: aload 6
    //   135: astore 4
    //   137: aload_3
    //   138: ifnull -59 -> 79
    //   141: aload_3
    //   142: invokeinterface 223 1 0
    //   147: aconst_null
    //   148: areturn
    //   149: astore_3
    //   150: aconst_null
    //   151: astore 4
    //   153: aload 4
    //   155: ifnull +10 -> 165
    //   158: aload 4
    //   160: invokeinterface 223 1 0
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
  public String zzbwe()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   7: astore_1
    //   8: aload_1
    //   9: ldc_w 1167
    //   12: aconst_null
    //   13: invokevirtual 212	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   16: astore_1
    //   17: aload_1
    //   18: astore_2
    //   19: aload_1
    //   20: invokeinterface 216 1 0
    //   25: ifeq +29 -> 54
    //   28: aload_1
    //   29: astore_2
    //   30: aload_1
    //   31: iconst_0
    //   32: invokeinterface 907 2 0
    //   37: astore_3
    //   38: aload_3
    //   39: astore_2
    //   40: aload_1
    //   41: ifnull +11 -> 52
    //   44: aload_1
    //   45: invokeinterface 223 1 0
    //   50: aload_3
    //   51: astore_2
    //   52: aload_2
    //   53: areturn
    //   54: aload 4
    //   56: astore_2
    //   57: aload_1
    //   58: ifnull -6 -> 52
    //   61: aload_1
    //   62: invokeinterface 223 1 0
    //   67: aconst_null
    //   68: areturn
    //   69: astore_3
    //   70: aconst_null
    //   71: astore_1
    //   72: aload_1
    //   73: astore_2
    //   74: aload_0
    //   75: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   78: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   81: ldc_w 1169
    //   84: aload_3
    //   85: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   88: aload 4
    //   90: astore_2
    //   91: aload_1
    //   92: ifnull -40 -> 52
    //   95: aload_1
    //   96: invokeinterface 223 1 0
    //   101: aconst_null
    //   102: areturn
    //   103: astore_1
    //   104: aconst_null
    //   105: astore_2
    //   106: aload_2
    //   107: ifnull +9 -> 116
    //   110: aload_2
    //   111: invokeinterface 223 1 0
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
  
  public boolean zzbwf()
  {
    return zzb("select count(1) > 0 from queue where has_realtime = 1", null) != 0L;
  }
  
  @WorkerThread
  void zzbwg()
  {
    zzzx();
    zzacj();
    if (!zzbwn()) {}
    long l1;
    long l2;
    do
    {
      return;
      l1 = zzbwc().atc.get();
      l2 = zzabz().elapsedRealtime();
    } while (Math.abs(l2 - l1) <= zzbwd().zzbuw());
    zzbwc().atc.set(l2);
    zzbwh();
  }
  
  @WorkerThread
  void zzbwh()
  {
    zzzx();
    zzacj();
    if (!zzbwn()) {}
    int i;
    do
    {
      return;
      i = getWritableDatabase().delete("queue", "abs(bundle_end_timestamp - ?) > cast(? as integer)", new String[] { String.valueOf(zzabz().currentTimeMillis()), String.valueOf(zzbwd().zzbuv()) });
    } while (i <= 0);
    zzbwb().zzbxe().zzj("Deleted stale rows. rowsDeleted", Integer.valueOf(i));
  }
  
  @WorkerThread
  public long zzbwi()
  {
    return zza("select max(bundle_end_timestamp) from queue", null, 0L);
  }
  
  @WorkerThread
  public long zzbwj()
  {
    return zza("select max(timestamp) from raw_events", null, 0L);
  }
  
  public boolean zzbwk()
  {
    return zzb("select count(1) > 0 from raw_events", null) != 0L;
  }
  
  public boolean zzbwl()
  {
    return zzb("select count(1) > 0 from raw_events where realtime = 1", null) != 0L;
  }
  
  public long zzbwm()
  {
    l2 = -1L;
    localObject3 = null;
    localObject1 = null;
    label63:
    do
    {
      try
      {
        localCursor = getWritableDatabase().rawQuery("select rowid from raw_events order by rowid desc limit 1;", null);
        localObject1 = localCursor;
        localObject3 = localCursor;
        boolean bool = localCursor.moveToFirst();
        if (bool) {
          break label63;
        }
        l1 = l2;
        if (localCursor != null)
        {
          localCursor.close();
          l1 = l2;
        }
      }
      catch (SQLiteException localSQLiteException)
      {
        Cursor localCursor;
        localObject3 = localObject1;
        zzbwb().zzbwy().zzj("Error querying raw events", localSQLiteException);
        long l1 = l2;
        return -1L;
      }
      finally
      {
        if (localObject3 == null) {
          break;
        }
        ((Cursor)localObject3).close();
      }
      return l1;
      localObject1 = localCursor;
      localObject3 = localCursor;
      l1 = localCursor.getLong(0);
      l2 = l1;
      l1 = l2;
    } while (localCursor == null);
    localCursor.close();
    return l2;
  }
  
  boolean zzc(String paramString, List<Integer> paramList)
  {
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    zzacj();
    zzzx();
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    int j;
    label147:
    do
    {
      try
      {
        long l = zzb("select count(1) from audience_filter_values where app_id=?", new String[] { paramString });
        j = zzbwd().zzlt(paramString);
        if (l <= j) {
          return false;
        }
      }
      catch (SQLiteException paramString)
      {
        zzbwb().zzbwy().zzj("Database error querying filters", paramString);
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
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    zzzx();
    zzacj();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("remote_config", paramArrayOfByte);
    try
    {
      if (getWritableDatabase().update("apps", localContentValues, "app_id = ?", new String[] { paramString }) == 0L) {
        zzbwb().zzbwy().log("Failed to update remote config (got 0)");
      }
      return;
    }
    catch (SQLiteException paramString)
    {
      zzbwb().zzbwy().zzj("Error storing remote config", paramString);
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
    //   4: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_0
    //   9: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   12: aload_0
    //   13: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   16: new 1092	java/util/ArrayList
    //   19: dup
    //   20: invokespecial 1093	java/util/ArrayList:<init>	()V
    //   23: astore 8
    //   25: aload_0
    //   26: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   29: astore 6
    //   31: aload_0
    //   32: invokevirtual 672	com/google/android/gms/measurement/internal/zze:zzbwd	()Lcom/google/android/gms/measurement/internal/zzd;
    //   35: invokevirtual 995	com/google/android/gms/measurement/internal/zzd:zzbuo	()I
    //   38: istore_2
    //   39: aload 6
    //   41: ldc_w 1011
    //   44: iconst_3
    //   45: anewarray 303	java/lang/String
    //   48: dup
    //   49: iconst_0
    //   50: ldc_w 438
    //   53: aastore
    //   54: dup
    //   55: iconst_1
    //   56: ldc_w 999
    //   59: aastore
    //   60: dup
    //   61: iconst_2
    //   62: ldc_w 1003
    //   65: aastore
    //   66: ldc_w 619
    //   69: iconst_1
    //   70: anewarray 303	java/lang/String
    //   73: dup
    //   74: iconst_0
    //   75: aload_1
    //   76: aastore
    //   77: aconst_null
    //   78: aconst_null
    //   79: ldc_w 911
    //   82: iload_2
    //   83: invokestatic 1019	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   86: invokevirtual 916	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   89: astore 6
    //   91: aload 6
    //   93: invokeinterface 216 1 0
    //   98: istore_3
    //   99: iload_3
    //   100: ifne +18 -> 118
    //   103: aload 6
    //   105: ifnull +10 -> 115
    //   108: aload 6
    //   110: invokeinterface 223 1 0
    //   115: aload 8
    //   117: areturn
    //   118: aload 6
    //   120: iconst_0
    //   121: invokeinterface 907 2 0
    //   126: astore 7
    //   128: aload 6
    //   130: iconst_1
    //   131: invokeinterface 220 2 0
    //   136: lstore 4
    //   138: aload_0
    //   139: aload 6
    //   141: iconst_2
    //   142: invokevirtual 1068	com/google/android/gms/measurement/internal/zze:zzb	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   145: astore 9
    //   147: aload 9
    //   149: ifnonnull +43 -> 192
    //   152: aload_0
    //   153: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   156: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   159: ldc_w 1250
    //   162: invokevirtual 268	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   165: aload 6
    //   167: invokeinterface 939 1 0
    //   172: istore_3
    //   173: iload_3
    //   174: ifne -56 -> 118
    //   177: aload 6
    //   179: ifnull +10 -> 189
    //   182: aload 6
    //   184: invokeinterface 223 1 0
    //   189: aload 8
    //   191: areturn
    //   192: aload 8
    //   194: new 971	com/google/android/gms/measurement/internal/zzak
    //   197: dup
    //   198: aload_1
    //   199: aload 7
    //   201: lload 4
    //   203: aload 9
    //   205: invokespecial 1071	com/google/android/gms/measurement/internal/zzak:<init>	(Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   208: invokeinterface 1096 2 0
    //   213: pop
    //   214: goto -49 -> 165
    //   217: astore 7
    //   219: aload_0
    //   220: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   223: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   226: ldc_w 1252
    //   229: aload_1
    //   230: aload 7
    //   232: invokevirtual 241	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   235: aload 6
    //   237: ifnull +10 -> 247
    //   240: aload 6
    //   242: invokeinterface 223 1 0
    //   247: aconst_null
    //   248: areturn
    //   249: astore_1
    //   250: aload 7
    //   252: astore 6
    //   254: aload 6
    //   256: ifnull +10 -> 266
    //   259: aload 6
    //   261: invokeinterface 223 1 0
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
    //   1: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   9: aload_0
    //   10: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   13: aload_0
    //   14: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: ldc_w 617
    //   20: bipush 21
    //   22: anewarray 303	java/lang/String
    //   25: dup
    //   26: iconst_0
    //   27: ldc_w 710
    //   30: aastore
    //   31: dup
    //   32: iconst_1
    //   33: ldc_w 715
    //   36: aastore
    //   37: dup
    //   38: iconst_2
    //   39: ldc_w 720
    //   42: aastore
    //   43: dup
    //   44: iconst_3
    //   45: ldc_w 725
    //   48: aastore
    //   49: dup
    //   50: iconst_4
    //   51: ldc 61
    //   53: aastore
    //   54: dup
    //   55: iconst_5
    //   56: ldc_w 733
    //   59: aastore
    //   60: dup
    //   61: bipush 6
    //   63: ldc 35
    //   65: aastore
    //   66: dup
    //   67: bipush 7
    //   69: ldc 45
    //   71: aastore
    //   72: dup
    //   73: bipush 8
    //   75: ldc 49
    //   77: aastore
    //   78: dup
    //   79: bipush 9
    //   81: ldc 53
    //   83: aastore
    //   84: dup
    //   85: bipush 10
    //   87: ldc 57
    //   89: aastore
    //   90: dup
    //   91: bipush 11
    //   93: ldc 65
    //   95: aastore
    //   96: dup
    //   97: bipush 12
    //   99: ldc 69
    //   101: aastore
    //   102: dup
    //   103: bipush 13
    //   105: ldc 73
    //   107: aastore
    //   108: dup
    //   109: bipush 14
    //   111: ldc 77
    //   113: aastore
    //   114: dup
    //   115: bipush 15
    //   117: ldc 85
    //   119: aastore
    //   120: dup
    //   121: bipush 16
    //   123: ldc 89
    //   125: aastore
    //   126: dup
    //   127: bipush 17
    //   129: ldc 93
    //   131: aastore
    //   132: dup
    //   133: bipush 18
    //   135: ldc 97
    //   137: aastore
    //   138: dup
    //   139: bipush 19
    //   141: ldc 101
    //   143: aastore
    //   144: dup
    //   145: bipush 20
    //   147: ldc 105
    //   149: aastore
    //   150: ldc_w 619
    //   153: iconst_1
    //   154: anewarray 303	java/lang/String
    //   157: dup
    //   158: iconst_0
    //   159: aload_1
    //   160: aastore
    //   161: aconst_null
    //   162: aconst_null
    //   163: aconst_null
    //   164: invokevirtual 444	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   167: astore 7
    //   169: aload 7
    //   171: astore 6
    //   173: aload 7
    //   175: invokeinterface 216 1 0
    //   180: istore_3
    //   181: iload_3
    //   182: ifne +19 -> 201
    //   185: aload 7
    //   187: ifnull +10 -> 197
    //   190: aload 7
    //   192: invokeinterface 223 1 0
    //   197: aconst_null
    //   198: astore_1
    //   199: aload_1
    //   200: areturn
    //   201: aload 7
    //   203: astore 6
    //   205: new 705	com/google/android/gms/measurement/internal/zza
    //   208: dup
    //   209: aload_0
    //   210: getfield 1259	com/google/android/gms/measurement/internal/zze:aqw	Lcom/google/android/gms/measurement/internal/zzx;
    //   213: aload_1
    //   214: invokespecial 1262	com/google/android/gms/measurement/internal/zza:<init>	(Lcom/google/android/gms/measurement/internal/zzx;Ljava/lang/String;)V
    //   217: astore 8
    //   219: aload 7
    //   221: astore 6
    //   223: aload 8
    //   225: aload 7
    //   227: iconst_0
    //   228: invokeinterface 907 2 0
    //   233: invokevirtual 1265	com/google/android/gms/measurement/internal/zza:zzlj	(Ljava/lang/String;)V
    //   236: aload 7
    //   238: astore 6
    //   240: aload 8
    //   242: aload 7
    //   244: iconst_1
    //   245: invokeinterface 907 2 0
    //   250: invokevirtual 1268	com/google/android/gms/measurement/internal/zza:zzlk	(Ljava/lang/String;)V
    //   253: aload 7
    //   255: astore 6
    //   257: aload 8
    //   259: aload 7
    //   261: iconst_2
    //   262: invokeinterface 907 2 0
    //   267: invokevirtual 1271	com/google/android/gms/measurement/internal/zza:zzll	(Ljava/lang/String;)V
    //   270: aload 7
    //   272: astore 6
    //   274: aload 8
    //   276: aload 7
    //   278: iconst_3
    //   279: invokeinterface 220 2 0
    //   284: invokevirtual 1274	com/google/android/gms/measurement/internal/zza:zzba	(J)V
    //   287: aload 7
    //   289: astore 6
    //   291: aload 8
    //   293: aload 7
    //   295: iconst_4
    //   296: invokeinterface 220 2 0
    //   301: invokevirtual 1277	com/google/android/gms/measurement/internal/zza:zzav	(J)V
    //   304: aload 7
    //   306: astore 6
    //   308: aload 8
    //   310: aload 7
    //   312: iconst_5
    //   313: invokeinterface 220 2 0
    //   318: invokevirtual 1280	com/google/android/gms/measurement/internal/zza:zzaw	(J)V
    //   321: aload 7
    //   323: astore 6
    //   325: aload 8
    //   327: aload 7
    //   329: bipush 6
    //   331: invokeinterface 907 2 0
    //   336: invokevirtual 1283	com/google/android/gms/measurement/internal/zza:setAppVersion	(Ljava/lang/String;)V
    //   339: aload 7
    //   341: astore 6
    //   343: aload 8
    //   345: aload 7
    //   347: bipush 7
    //   349: invokeinterface 907 2 0
    //   354: invokevirtual 1286	com/google/android/gms/measurement/internal/zza:zzln	(Ljava/lang/String;)V
    //   357: aload 7
    //   359: astore 6
    //   361: aload 8
    //   363: aload 7
    //   365: bipush 8
    //   367: invokeinterface 220 2 0
    //   372: invokevirtual 1289	com/google/android/gms/measurement/internal/zza:zzay	(J)V
    //   375: aload 7
    //   377: astore 6
    //   379: aload 8
    //   381: aload 7
    //   383: bipush 9
    //   385: invokeinterface 220 2 0
    //   390: invokevirtual 1292	com/google/android/gms/measurement/internal/zza:zzaz	(J)V
    //   393: aload 7
    //   395: astore 6
    //   397: aload 7
    //   399: bipush 10
    //   401: invokeinterface 1295 2 0
    //   406: ifeq +270 -> 676
    //   409: iconst_1
    //   410: istore_2
    //   411: goto +369 -> 780
    //   414: aload 7
    //   416: astore 6
    //   418: aload 8
    //   420: iload_3
    //   421: invokevirtual 1299	com/google/android/gms/measurement/internal/zza:setMeasurementEnabled	(Z)V
    //   424: aload 7
    //   426: astore 6
    //   428: aload 8
    //   430: aload 7
    //   432: bipush 11
    //   434: invokeinterface 220 2 0
    //   439: invokevirtual 1301	com/google/android/gms/measurement/internal/zza:zzbd	(J)V
    //   442: aload 7
    //   444: astore 6
    //   446: aload 8
    //   448: aload 7
    //   450: bipush 12
    //   452: invokeinterface 220 2 0
    //   457: invokevirtual 1303	com/google/android/gms/measurement/internal/zza:zzbe	(J)V
    //   460: aload 7
    //   462: astore 6
    //   464: aload 8
    //   466: aload 7
    //   468: bipush 13
    //   470: invokeinterface 220 2 0
    //   475: invokevirtual 1306	com/google/android/gms/measurement/internal/zza:zzbf	(J)V
    //   478: aload 7
    //   480: astore 6
    //   482: aload 8
    //   484: aload 7
    //   486: bipush 14
    //   488: invokeinterface 220 2 0
    //   493: invokevirtual 1309	com/google/android/gms/measurement/internal/zza:zzbg	(J)V
    //   496: aload 7
    //   498: astore 6
    //   500: aload 8
    //   502: aload 7
    //   504: bipush 15
    //   506: invokeinterface 220 2 0
    //   511: invokevirtual 1312	com/google/android/gms/measurement/internal/zza:zzbb	(J)V
    //   514: aload 7
    //   516: astore 6
    //   518: aload 8
    //   520: aload 7
    //   522: bipush 16
    //   524: invokeinterface 220 2 0
    //   529: invokevirtual 1315	com/google/android/gms/measurement/internal/zza:zzbc	(J)V
    //   532: aload 7
    //   534: astore 6
    //   536: aload 7
    //   538: bipush 17
    //   540: invokeinterface 1295 2 0
    //   545: ifeq +148 -> 693
    //   548: ldc2_w 1316
    //   551: lstore 4
    //   553: aload 7
    //   555: astore 6
    //   557: aload 8
    //   559: lload 4
    //   561: invokevirtual 1320	com/google/android/gms/measurement/internal/zza:zzax	(J)V
    //   564: aload 7
    //   566: astore 6
    //   568: aload 8
    //   570: aload 7
    //   572: bipush 18
    //   574: invokeinterface 907 2 0
    //   579: invokevirtual 1323	com/google/android/gms/measurement/internal/zza:zzlm	(Ljava/lang/String;)V
    //   582: aload 7
    //   584: astore 6
    //   586: aload 8
    //   588: aload 7
    //   590: bipush 19
    //   592: invokeinterface 220 2 0
    //   597: invokevirtual 1326	com/google/android/gms/measurement/internal/zza:zzbi	(J)V
    //   600: aload 7
    //   602: astore 6
    //   604: aload 8
    //   606: aload 7
    //   608: bipush 20
    //   610: invokeinterface 220 2 0
    //   615: invokevirtual 1329	com/google/android/gms/measurement/internal/zza:zzbh	(J)V
    //   618: aload 7
    //   620: astore 6
    //   622: aload 8
    //   624: invokevirtual 1332	com/google/android/gms/measurement/internal/zza:zzbtg	()V
    //   627: aload 7
    //   629: astore 6
    //   631: aload 7
    //   633: invokeinterface 939 1 0
    //   638: ifeq +20 -> 658
    //   641: aload 7
    //   643: astore 6
    //   645: aload_0
    //   646: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   649: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   652: ldc_w 1334
    //   655: invokevirtual 268	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   658: aload 8
    //   660: astore_1
    //   661: aload 7
    //   663: ifnull -464 -> 199
    //   666: aload 7
    //   668: invokeinterface 223 1 0
    //   673: aload 8
    //   675: areturn
    //   676: aload 7
    //   678: astore 6
    //   680: aload 7
    //   682: bipush 10
    //   684: invokeinterface 1088 2 0
    //   689: istore_2
    //   690: goto +90 -> 780
    //   693: aload 7
    //   695: astore 6
    //   697: aload 7
    //   699: bipush 17
    //   701: invokeinterface 1088 2 0
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
    //   724: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   727: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   730: ldc_w 1336
    //   733: aload_1
    //   734: aload 8
    //   736: invokevirtual 241	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   739: aload 7
    //   741: ifnull +10 -> 751
    //   744: aload 7
    //   746: invokeinterface 223 1 0
    //   751: aconst_null
    //   752: areturn
    //   753: astore_1
    //   754: aconst_null
    //   755: astore 6
    //   757: aload 6
    //   759: ifnull +10 -> 769
    //   762: aload 6
    //   764: invokeinterface 223 1 0
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
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    zzzx();
    zzacj();
    try
    {
      int i = getWritableDatabase().delete("raw_events", "rowid in (select rowid from raw_events where app_id=? order by rowid desc limit -1 offset ?)", new String[] { paramString, String.valueOf(zzbwd().zzlx(paramString)) });
      return i;
    }
    catch (SQLiteException paramString)
    {
      zzbwb().zzbwy().zzj("Error deleting over the limit events", paramString);
    }
    return 0L;
  }
  
  /* Error */
  @WorkerThread
  public byte[] zzmb(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   9: aload_0
    //   10: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   13: aload_0
    //   14: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: ldc_w 617
    //   20: iconst_1
    //   21: anewarray 303	java/lang/String
    //   24: dup
    //   25: iconst_0
    //   26: ldc 81
    //   28: aastore
    //   29: ldc_w 619
    //   32: iconst_1
    //   33: anewarray 303	java/lang/String
    //   36: dup
    //   37: iconst_0
    //   38: aload_1
    //   39: aastore
    //   40: aconst_null
    //   41: aconst_null
    //   42: aconst_null
    //   43: invokevirtual 444	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   46: astore 4
    //   48: aload 4
    //   50: astore_3
    //   51: aload 4
    //   53: invokeinterface 216 1 0
    //   58: istore_2
    //   59: iload_2
    //   60: ifne +19 -> 79
    //   63: aload 4
    //   65: ifnull +10 -> 75
    //   68: aload 4
    //   70: invokeinterface 223 1 0
    //   75: aconst_null
    //   76: astore_1
    //   77: aload_1
    //   78: areturn
    //   79: aload 4
    //   81: astore_3
    //   82: aload 4
    //   84: iconst_0
    //   85: invokeinterface 926 2 0
    //   90: astore 5
    //   92: aload 4
    //   94: astore_3
    //   95: aload 4
    //   97: invokeinterface 939 1 0
    //   102: ifeq +19 -> 121
    //   105: aload 4
    //   107: astore_3
    //   108: aload_0
    //   109: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   112: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   115: ldc_w 1349
    //   118: invokevirtual 268	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   121: aload 5
    //   123: astore_1
    //   124: aload 4
    //   126: ifnull -49 -> 77
    //   129: aload 4
    //   131: invokeinterface 223 1 0
    //   136: aload 5
    //   138: areturn
    //   139: astore 5
    //   141: aconst_null
    //   142: astore 4
    //   144: aload 4
    //   146: astore_3
    //   147: aload_0
    //   148: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   151: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   154: ldc_w 1351
    //   157: aload_1
    //   158: aload 5
    //   160: invokevirtual 241	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   163: aload 4
    //   165: ifnull +10 -> 175
    //   168: aload 4
    //   170: invokeinterface 223 1 0
    //   175: aconst_null
    //   176: areturn
    //   177: astore_1
    //   178: aconst_null
    //   179: astore_3
    //   180: aload_3
    //   181: ifnull +9 -> 190
    //   184: aload_3
    //   185: invokeinterface 223 1 0
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
    zzacj();
    zzzx();
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.delete("property_filters", "app_id=?", new String[] { paramString });
    localSQLiteDatabase.delete("event_filters", "app_id=?", new String[] { paramString });
  }
  
  /* Error */
  Map<Integer, com.google.android.gms.internal.zzwc.zzf> zzmd(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   4: aload_0
    //   5: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   8: aload_1
    //   9: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: astore 4
    //   19: aload 4
    //   21: ldc_w 889
    //   24: iconst_2
    //   25: anewarray 303	java/lang/String
    //   28: dup
    //   29: iconst_0
    //   30: ldc_w 486
    //   33: aastore
    //   34: dup
    //   35: iconst_1
    //   36: ldc_w 887
    //   39: aastore
    //   40: ldc_w 619
    //   43: iconst_1
    //   44: anewarray 303	java/lang/String
    //   47: dup
    //   48: iconst_0
    //   49: aload_1
    //   50: aastore
    //   51: aconst_null
    //   52: aconst_null
    //   53: aconst_null
    //   54: invokevirtual 444	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   57: astore 4
    //   59: aload 4
    //   61: invokeinterface 216 1 0
    //   66: istore_3
    //   67: iload_3
    //   68: ifne +17 -> 85
    //   71: aload 4
    //   73: ifnull +10 -> 83
    //   76: aload 4
    //   78: invokeinterface 223 1 0
    //   83: aconst_null
    //   84: areturn
    //   85: new 27	android/support/v4/util/ArrayMap
    //   88: dup
    //   89: invokespecial 1078	android/support/v4/util/ArrayMap:<init>	()V
    //   92: astore 5
    //   94: aload 4
    //   96: iconst_0
    //   97: invokeinterface 1088 2 0
    //   102: istore_2
    //   103: aload 4
    //   105: iconst_1
    //   106: invokeinterface 926 2 0
    //   111: invokestatic 932	com/google/android/gms/internal/zzars:zzbd	([B)Lcom/google/android/gms/internal/zzars;
    //   114: astore 7
    //   116: new 883	com/google/android/gms/internal/zzwc$zzf
    //   119: dup
    //   120: invokespecial 1354	com/google/android/gms/internal/zzwc$zzf:<init>	()V
    //   123: astore 6
    //   125: aload 6
    //   127: aload 7
    //   129: invokevirtual 1355	com/google/android/gms/internal/zzwc$zzf:zzb	(Lcom/google/android/gms/internal/zzars;)Lcom/google/android/gms/internal/zzasa;
    //   132: checkcast 883	com/google/android/gms/internal/zzwc$zzf
    //   135: astore 7
    //   137: aload 5
    //   139: iload_2
    //   140: invokestatic 460	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   143: aload 6
    //   145: invokeinterface 43 3 0
    //   150: pop
    //   151: aload 4
    //   153: invokeinterface 939 1 0
    //   158: istore_3
    //   159: iload_3
    //   160: ifne -66 -> 94
    //   163: aload 4
    //   165: ifnull +10 -> 175
    //   168: aload 4
    //   170: invokeinterface 223 1 0
    //   175: aload 5
    //   177: areturn
    //   178: astore 6
    //   180: aload_0
    //   181: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   184: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   187: ldc_w 1357
    //   190: aload_1
    //   191: iload_2
    //   192: invokestatic 460	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   195: aload 6
    //   197: invokevirtual 1060	com/google/android/gms/measurement/internal/zzq$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   200: goto -49 -> 151
    //   203: astore 5
    //   205: aload 4
    //   207: astore_1
    //   208: aload 5
    //   210: astore 4
    //   212: aload_0
    //   213: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   216: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   219: ldc_w 1359
    //   222: aload 4
    //   224: invokevirtual 293	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   227: aload_1
    //   228: ifnull +9 -> 237
    //   231: aload_1
    //   232: invokeinterface 223 1 0
    //   237: aconst_null
    //   238: areturn
    //   239: astore_1
    //   240: aconst_null
    //   241: astore 4
    //   243: aload 4
    //   245: ifnull +10 -> 255
    //   248: aload 4
    //   250: invokeinterface 223 1 0
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
    //   123	21	6	localzzf	com.google.android.gms.internal.zzwc.zzf
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
    zzacj();
    zzzx();
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
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
        zzbwb().zzbxe().zze("Deleted application data. app, records", paramString, Integer.valueOf(i));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzbwb().zzbwy().zze("Error deleting application data. appId, error", paramString, localSQLiteException);
    }
  }
  
  @WorkerThread
  public long zzmf(String paramString)
  {
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    zzzx();
    zzacj();
    return zzau(paramString, "first_open_count");
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
      zzbwb().zzbwy().zzj("Failed to remove unused event metadata", paramString);
    }
  }
  
  public long zzmh(String paramString)
  {
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    return zza("select count(1) from events where app_id=? and name not like '!_%' escape '!'", new String[] { paramString }, 0L);
  }
  
  /* Error */
  @WorkerThread
  public List<android.util.Pair<zzwc.zze, Long>> zzn(String paramString, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore 6
    //   3: aload_0
    //   4: invokevirtual 381	com/google/android/gms/measurement/internal/zze:zzzx	()V
    //   7: aload_0
    //   8: invokevirtual 378	com/google/android/gms/measurement/internal/zze:zzacj	()V
    //   11: iload_2
    //   12: ifle +112 -> 124
    //   15: iconst_1
    //   16: istore 5
    //   18: iload 5
    //   20: invokestatic 1384	com/google/android/gms/common/internal/zzaa:zzbt	(Z)V
    //   23: iload_3
    //   24: ifle +106 -> 130
    //   27: iload 6
    //   29: istore 5
    //   31: iload 5
    //   33: invokestatic 1384	com/google/android/gms/common/internal/zzaa:zzbt	(Z)V
    //   36: aload_1
    //   37: invokestatic 387	com/google/android/gms/common/internal/zzaa:zzib	(Ljava/lang/String;)Ljava/lang/String;
    //   40: pop
    //   41: aload_0
    //   42: invokevirtual 206	com/google/android/gms/measurement/internal/zze:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   45: ldc_w 692
    //   48: iconst_2
    //   49: anewarray 303	java/lang/String
    //   52: dup
    //   53: iconst_0
    //   54: ldc_w 911
    //   57: aastore
    //   58: dup
    //   59: iconst_1
    //   60: ldc_w 495
    //   63: aastore
    //   64: ldc_w 619
    //   67: iconst_1
    //   68: anewarray 303	java/lang/String
    //   71: dup
    //   72: iconst_0
    //   73: aload_1
    //   74: aastore
    //   75: aconst_null
    //   76: aconst_null
    //   77: ldc_w 911
    //   80: iload_2
    //   81: invokestatic 1019	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   84: invokevirtual 916	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   87: astore 9
    //   89: aload 9
    //   91: invokeinterface 216 1 0
    //   96: ifne +40 -> 136
    //   99: invokestatic 1388	java/util/Collections:emptyList	()Ljava/util/List;
    //   102: astore 10
    //   104: aload 10
    //   106: astore_1
    //   107: aload 9
    //   109: ifnull +13 -> 122
    //   112: aload 9
    //   114: invokeinterface 223 1 0
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
    //   136: new 1092	java/util/ArrayList
    //   139: dup
    //   140: invokespecial 1093	java/util/ArrayList:<init>	()V
    //   143: astore 10
    //   145: iconst_0
    //   146: istore_2
    //   147: aload 9
    //   149: iconst_0
    //   150: invokeinterface 220 2 0
    //   155: lstore 7
    //   157: aload 9
    //   159: iconst_1
    //   160: invokeinterface 926 2 0
    //   165: astore 11
    //   167: aload_0
    //   168: invokevirtual 589	com/google/android/gms/measurement/internal/zze:zzbvx	()Lcom/google/android/gms/measurement/internal/zzal;
    //   171: aload 11
    //   173: invokevirtual 1391	com/google/android/gms/measurement/internal/zzal:zzx	([B)[B
    //   176: astore 11
    //   178: aload 10
    //   180: invokeinterface 1392 1 0
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
    //   208: invokeinterface 223 1 0
    //   213: aload 10
    //   215: areturn
    //   216: astore 11
    //   218: aload_0
    //   219: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   222: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   225: ldc_w 1394
    //   228: aload_1
    //   229: aload 11
    //   231: invokevirtual 241	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   234: aload 9
    //   236: invokeinterface 939 1 0
    //   241: ifeq -40 -> 201
    //   244: iload_2
    //   245: iload_3
    //   246: if_icmpgt -45 -> 201
    //   249: goto -102 -> 147
    //   252: aload 11
    //   254: invokestatic 932	com/google/android/gms/internal/zzars:zzbd	([B)Lcom/google/android/gms/internal/zzars;
    //   257: astore 13
    //   259: new 580	com/google/android/gms/internal/zzwc$zze
    //   262: dup
    //   263: invokespecial 933	com/google/android/gms/internal/zzwc$zze:<init>	()V
    //   266: astore 12
    //   268: aload 12
    //   270: aload 13
    //   272: invokevirtual 936	com/google/android/gms/internal/zzwc$zze:zzb	(Lcom/google/android/gms/internal/zzars;)Lcom/google/android/gms/internal/zzasa;
    //   275: checkcast 580	com/google/android/gms/internal/zzwc$zze
    //   278: astore 13
    //   280: aload 11
    //   282: arraylength
    //   283: iload_2
    //   284: iadd
    //   285: istore_2
    //   286: aload 10
    //   288: aload 12
    //   290: lload 7
    //   292: invokestatic 602	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   295: invokestatic 1400	android/util/Pair:create	(Ljava/lang/Object;Ljava/lang/Object;)Landroid/util/Pair;
    //   298: invokeinterface 1096 2 0
    //   303: pop
    //   304: goto -70 -> 234
    //   307: astore 10
    //   309: aload_0
    //   310: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   313: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   316: ldc_w 1402
    //   319: aload_1
    //   320: aload 10
    //   322: invokevirtual 241	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   325: invokestatic 1388	java/util/Collections:emptyList	()Ljava/util/List;
    //   328: astore 10
    //   330: aload 10
    //   332: astore_1
    //   333: aload 9
    //   335: ifnull -213 -> 122
    //   338: aload 9
    //   340: invokeinterface 223 1 0
    //   345: aload 10
    //   347: areturn
    //   348: astore 11
    //   350: aload_0
    //   351: invokevirtual 227	com/google/android/gms/measurement/internal/zze:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   354: invokevirtual 233	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   357: ldc_w 1404
    //   360: aload_1
    //   361: aload 11
    //   363: invokevirtual 241	com/google/android/gms/measurement/internal/zzq$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   366: goto -132 -> 234
    //   369: astore_1
    //   370: aconst_null
    //   371: astore 9
    //   373: aload 9
    //   375: ifnull +10 -> 385
    //   378: aload 9
    //   380: invokeinterface 223 1 0
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
    //   266	23	12	localzze	zzwc.zze
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
  
  @WorkerThread
  public void zzz(String paramString, int paramInt)
  {
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    zzzx();
    zzacj();
    try
    {
      getWritableDatabase().execSQL("delete from user_attributes where app_id=? and name in (select name from user_attributes where app_id=? and name like '_ltv_%' order by set_timestamp desc limit ?,10);", new String[] { paramString, paramString, String.valueOf(paramInt) });
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzbwb().zzbwy().zze("Error pruning currencies", paramString, localSQLiteException);
    }
  }
  
  protected void zzzy() {}
  
  public static class zza
  {
    long arp;
    long arq;
    long arr;
    long ars;
    long art;
  }
  
  static abstract interface zzb
  {
    public abstract boolean zza(long paramLong, zzwc.zzb paramzzb);
    
    public abstract void zzb(zzwc.zze paramzze);
  }
  
  private class zzc
    extends SQLiteOpenHelper
  {
    zzc(Context paramContext, String paramString)
    {
      super(paramString, null, 1);
    }
    
    @WorkerThread
    public SQLiteDatabase getWritableDatabase()
    {
      if (!zze.zza(zze.this).zzz(zze.this.zzbwd().zzbup())) {
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
        zze.this.zzbwb().zzbwy().log("Opening the database failed, dropping and recreating it");
        Object localObject = zze.this.zzade();
        if (!zze.this.getContext().getDatabasePath((String)localObject).delete()) {
          zze.this.zzbwb().zzbwy().zzj("Failed to delete corrupted db file", localObject);
        }
        try
        {
          localObject = super.getWritableDatabase();
          zze.zza(zze.this).clear();
          return (SQLiteDatabase)localObject;
        }
        catch (SQLiteException localSQLiteException2)
        {
          zze.this.zzbwb().zzbwy().zzj("Failed to open freshly created database", localSQLiteException2);
          throw localSQLiteException2;
        }
      }
    }
    
    @WorkerThread
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      zze.zza(zze.this.zzbwb(), paramSQLiteDatabase);
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
        zze.zza(zze.this.zzbwb(), paramSQLiteDatabase, "events", "CREATE TABLE IF NOT EXISTS events ( app_id TEXT NOT NULL, name TEXT NOT NULL, lifetime_count INTEGER NOT NULL, current_bundle_count INTEGER NOT NULL, last_fire_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,lifetime_count,current_bundle_count,last_fire_timestamp", null);
        zze.zza(zze.this.zzbwb(), paramSQLiteDatabase, "user_attributes", "CREATE TABLE IF NOT EXISTS user_attributes ( app_id TEXT NOT NULL, name TEXT NOT NULL, set_timestamp INTEGER NOT NULL, value BLOB NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,set_timestamp,value", null);
        zze.zza(zze.this.zzbwb(), paramSQLiteDatabase, "apps", "CREATE TABLE IF NOT EXISTS apps ( app_id TEXT NOT NULL, app_instance_id TEXT, gmp_app_id TEXT, resettable_device_id_hash TEXT, last_bundle_index INTEGER NOT NULL, last_bundle_end_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id)) ;", "app_id,app_instance_id,gmp_app_id,resettable_device_id_hash,last_bundle_index,last_bundle_end_timestamp", zze.zzbwo());
        zze.zza(zze.this.zzbwb(), paramSQLiteDatabase, "queue", "CREATE TABLE IF NOT EXISTS queue ( app_id TEXT NOT NULL, bundle_end_timestamp INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,bundle_end_timestamp,data", zze.zzbwp());
        zze.zza(zze.this.zzbwb(), paramSQLiteDatabase, "raw_events_metadata", "CREATE TABLE IF NOT EXISTS raw_events_metadata ( app_id TEXT NOT NULL, metadata_fingerprint INTEGER NOT NULL, metadata BLOB NOT NULL, PRIMARY KEY (app_id, metadata_fingerprint));", "app_id,metadata_fingerprint,metadata", null);
        zze.zza(zze.this.zzbwb(), paramSQLiteDatabase, "raw_events", "CREATE TABLE IF NOT EXISTS raw_events ( app_id TEXT NOT NULL, name TEXT NOT NULL, timestamp INTEGER NOT NULL, metadata_fingerprint INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,name,timestamp,metadata_fingerprint,data", zze.zzbwq());
        zze.zza(zze.this.zzbwb(), paramSQLiteDatabase, "event_filters", "CREATE TABLE IF NOT EXISTS event_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, event_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, event_name, audience_id, filter_id));", "app_id,audience_id,filter_id,event_name,data", null);
        zze.zza(zze.this.zzbwb(), paramSQLiteDatabase, "property_filters", "CREATE TABLE IF NOT EXISTS property_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, property_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, property_name, audience_id, filter_id));", "app_id,audience_id,filter_id,property_name,data", null);
        zze.zza(zze.this.zzbwb(), paramSQLiteDatabase, "audience_filter_values", "CREATE TABLE IF NOT EXISTS audience_filter_values ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, current_results BLOB, PRIMARY KEY (app_id, audience_id));", "app_id,audience_id,current_results", null);
        zze.zza(zze.this.zzbwb(), paramSQLiteDatabase, "app2", "CREATE TABLE IF NOT EXISTS app2 ( app_id TEXT NOT NULL, first_open_count INTEGER NOT NULL, PRIMARY KEY (app_id));", "app_id,first_open_count", zze.zzbwr());
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