package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
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

class zzatj
  extends zzauh
{
  private static final Map<String, String> zzbrh = new ArrayMap(1);
  private static final Map<String, String> zzbri;
  private static final Map<String, String> zzbrj;
  private static final Map<String, String> zzbrk;
  private static final Map<String, String> zzbrl;
  private final zzc zzbrm;
  private final zzauo zzbrn = new zzauo(zznR());
  
  static
  {
    zzbrh.put("origin", "ALTER TABLE user_attributes ADD COLUMN origin TEXT;");
    zzbri = new ArrayMap(18);
    zzbri.put("app_version", "ALTER TABLE apps ADD COLUMN app_version TEXT;");
    zzbri.put("app_store", "ALTER TABLE apps ADD COLUMN app_store TEXT;");
    zzbri.put("gmp_version", "ALTER TABLE apps ADD COLUMN gmp_version INTEGER;");
    zzbri.put("dev_cert_hash", "ALTER TABLE apps ADD COLUMN dev_cert_hash INTEGER;");
    zzbri.put("measurement_enabled", "ALTER TABLE apps ADD COLUMN measurement_enabled INTEGER;");
    zzbri.put("last_bundle_start_timestamp", "ALTER TABLE apps ADD COLUMN last_bundle_start_timestamp INTEGER;");
    zzbri.put("day", "ALTER TABLE apps ADD COLUMN day INTEGER;");
    zzbri.put("daily_public_events_count", "ALTER TABLE apps ADD COLUMN daily_public_events_count INTEGER;");
    zzbri.put("daily_events_count", "ALTER TABLE apps ADD COLUMN daily_events_count INTEGER;");
    zzbri.put("daily_conversions_count", "ALTER TABLE apps ADD COLUMN daily_conversions_count INTEGER;");
    zzbri.put("remote_config", "ALTER TABLE apps ADD COLUMN remote_config BLOB;");
    zzbri.put("config_fetched_time", "ALTER TABLE apps ADD COLUMN config_fetched_time INTEGER;");
    zzbri.put("failed_config_fetch_time", "ALTER TABLE apps ADD COLUMN failed_config_fetch_time INTEGER;");
    zzbri.put("app_version_int", "ALTER TABLE apps ADD COLUMN app_version_int INTEGER;");
    zzbri.put("firebase_instance_id", "ALTER TABLE apps ADD COLUMN firebase_instance_id TEXT;");
    zzbri.put("daily_error_events_count", "ALTER TABLE apps ADD COLUMN daily_error_events_count INTEGER;");
    zzbri.put("daily_realtime_events_count", "ALTER TABLE apps ADD COLUMN daily_realtime_events_count INTEGER;");
    zzbri.put("health_monitor_sample", "ALTER TABLE apps ADD COLUMN health_monitor_sample TEXT;");
    zzbri.put("android_id", "ALTER TABLE apps ADD COLUMN android_id INTEGER;");
    zzbrj = new ArrayMap(1);
    zzbrj.put("realtime", "ALTER TABLE raw_events ADD COLUMN realtime INTEGER;");
    zzbrk = new ArrayMap(1);
    zzbrk.put("has_realtime", "ALTER TABLE queue ADD COLUMN has_realtime INTEGER;");
    zzbrl = new ArrayMap(1);
    zzbrl.put("previous_install_count", "ALTER TABLE app2 ADD COLUMN previous_install_count INTEGER;");
  }
  
  zzatj(zzaue paramzzaue)
  {
    super(paramzzaue);
    paramzzaue = zzow();
    this.zzbrm = new zzc(getContext(), paramzzaue);
  }
  
  private boolean zzLN()
  {
    return getContext().getDatabasePath(zzow()).exists();
  }
  
  @TargetApi(11)
  @WorkerThread
  static int zza(Cursor paramCursor, int paramInt)
  {
    int i = Build.VERSION.SDK_INT;
    return paramCursor.getType(paramInt);
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
      zzKl().zzLZ().zze("Database error", paramString, paramArrayOfString);
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
  
  static void zza(zzatx paramzzatx, SQLiteDatabase paramSQLiteDatabase)
  {
    if (paramzzatx == null) {
      throw new IllegalArgumentException("Monitor must not be null");
    }
    int i = Build.VERSION.SDK_INT;
    paramSQLiteDatabase = new File(paramSQLiteDatabase.getPath());
    if (!paramSQLiteDatabase.setReadable(false, false)) {
      paramzzatx.zzMb().log("Failed to turn off database read permission");
    }
    if (!paramSQLiteDatabase.setWritable(false, false)) {
      paramzzatx.zzMb().log("Failed to turn off database write permission");
    }
    if (!paramSQLiteDatabase.setReadable(true, true)) {
      paramzzatx.zzMb().log("Failed to turn on database read permission for owner");
    }
    if (!paramSQLiteDatabase.setWritable(true, true)) {
      paramzzatx.zzMb().log("Failed to turn on database write permission for owner");
    }
  }
  
  @WorkerThread
  static void zza(zzatx paramzzatx, SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, String paramString3, Map<String, String> paramMap)
    throws SQLiteException
  {
    if (paramzzatx == null) {
      throw new IllegalArgumentException("Monitor must not be null");
    }
    if (!zza(paramzzatx, paramSQLiteDatabase, paramString1)) {
      paramSQLiteDatabase.execSQL(paramString2);
    }
    try
    {
      zza(paramzzatx, paramSQLiteDatabase, paramString1, paramString3, paramMap);
      return;
    }
    catch (SQLiteException paramSQLiteDatabase)
    {
      paramzzatx.zzLZ().zzj("Failed to verify columns on table that was just created", paramString1);
      throw paramSQLiteDatabase;
    }
  }
  
  @WorkerThread
  static void zza(zzatx paramzzatx, SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, Map<String, String> paramMap)
    throws SQLiteException
  {
    if (paramzzatx == null) {
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
      paramzzatx.zzMb().zze("Table has extra columns. table, columns", paramString1, TextUtils.join(", ", localSet));
    }
  }
  
  @WorkerThread
  private void zza(String paramString, zzauu.zza paramzza)
  {
    int k = 0;
    zzob();
    zzmR();
    zzac.zzdr(paramString);
    zzac.zzw(paramzza);
    zzac.zzw(paramzza.zzbwp);
    zzac.zzw(paramzza.zzbwo);
    if (paramzza.zzbwn == null) {
      zzKl().zzMb().zzj("Audience with no ID. appId", zzatx.zzfE(paramString));
    }
    label247:
    label292:
    label301:
    label302:
    for (;;)
    {
      return;
      int n = paramzza.zzbwn.intValue();
      Object localObject = paramzza.zzbwp;
      int j = localObject.length;
      int i = 0;
      while (i < j)
      {
        if (localObject[i].zzbwr == null)
        {
          zzKl().zzMb().zze("Event filter with no ID. Audience definition ignored. appId, audienceId", zzatx.zzfE(paramString), paramzza.zzbwn);
          return;
        }
        i += 1;
      }
      localObject = paramzza.zzbwo;
      j = localObject.length;
      i = 0;
      while (i < j)
      {
        if (localObject[i].zzbwr == null)
        {
          zzKl().zzMb().zze("Property filter with no ID. Audience definition ignored. appId, audienceId", zzatx.zzfE(paramString), paramzza.zzbwn);
          return;
        }
        i += 1;
      }
      int m = 1;
      localObject = paramzza.zzbwp;
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
          break label301;
        }
        paramzza = paramzza.zzbwo;
        m = paramzza.length;
        j = 0;
        if (j >= m) {
          break label301;
        }
        if (zza(paramString, n, paramzza[j])) {
          break label292;
        }
        i = k;
      }
      for (;;)
      {
        if (i != 0) {
          break label302;
        }
        zzA(paramString, n);
        return;
        j += 1;
        break;
        j += 1;
        break label247;
      }
    }
  }
  
  /* Error */
  @WorkerThread
  static boolean zza(zzatx paramzzatx, SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: ifnonnull +13 -> 17
    //   7: new 250	java/lang/IllegalArgumentException
    //   10: dup
    //   11: ldc -4
    //   13: invokespecial 255	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   16: athrow
    //   17: aload_1
    //   18: ldc_w 444
    //   21: iconst_1
    //   22: anewarray 306	java/lang/String
    //   25: dup
    //   26: iconst_0
    //   27: ldc_w 446
    //   30: aastore
    //   31: ldc_w 448
    //   34: iconst_1
    //   35: anewarray 306	java/lang/String
    //   38: dup
    //   39: iconst_0
    //   40: aload_2
    //   41: aastore
    //   42: aconst_null
    //   43: aconst_null
    //   44: aconst_null
    //   45: invokevirtual 452	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   48: astore_1
    //   49: aload_1
    //   50: astore 4
    //   52: aload 4
    //   54: astore_1
    //   55: aload 4
    //   57: invokeinterface 221 1 0
    //   62: istore_3
    //   63: aload 4
    //   65: ifnull +10 -> 75
    //   68: aload 4
    //   70: invokeinterface 228 1 0
    //   75: iload_3
    //   76: ireturn
    //   77: astore 5
    //   79: aconst_null
    //   80: astore 4
    //   82: aload 4
    //   84: astore_1
    //   85: aload_0
    //   86: invokevirtual 266	com/google/android/gms/internal/zzatx:zzMb	()Lcom/google/android/gms/internal/zzatx$zza;
    //   89: ldc_w 454
    //   92: aload_2
    //   93: aload 5
    //   95: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   98: aload 4
    //   100: ifnull +10 -> 110
    //   103: aload 4
    //   105: invokeinterface 228 1 0
    //   110: iconst_0
    //   111: ireturn
    //   112: astore_0
    //   113: aload 4
    //   115: astore_1
    //   116: aload_1
    //   117: ifnull +9 -> 126
    //   120: aload_1
    //   121: invokeinterface 228 1 0
    //   126: aload_0
    //   127: athrow
    //   128: astore_0
    //   129: goto -13 -> 116
    //   132: astore 5
    //   134: goto -52 -> 82
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	137	0	paramzzatx	zzatx
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
  private boolean zza(String paramString, int paramInt, zzauu.zzb paramzzb)
  {
    zzob();
    zzmR();
    zzac.zzdr(paramString);
    zzac.zzw(paramzzb);
    if (TextUtils.isEmpty(paramzzb.zzbws))
    {
      zzKl().zzMb().zzd("Event filter had no event name. Audience definition ignored. appId, audienceId, filterId", zzatx.zzfE(paramString), Integer.valueOf(paramInt), String.valueOf(paramzzb.zzbwr));
      return false;
    }
    try
    {
      byte[] arrayOfByte = new byte[paramzzb.zzafB()];
      Object localObject = zzbyc.zzah(arrayOfByte);
      paramzzb.zza((zzbyc)localObject);
      ((zzbyc)localObject).zzafo();
      localObject = new ContentValues();
      ((ContentValues)localObject).put("app_id", paramString);
      ((ContentValues)localObject).put("audience_id", Integer.valueOf(paramInt));
      ((ContentValues)localObject).put("filter_id", paramzzb.zzbwr);
      ((ContentValues)localObject).put("event_name", paramzzb.zzbws);
      ((ContentValues)localObject).put("data", arrayOfByte);
      return false;
    }
    catch (IOException paramzzb)
    {
      try
      {
        if (getWritableDatabase().insertWithOnConflict("event_filters", null, (ContentValues)localObject, 5) == -1L) {
          zzKl().zzLZ().zzj("Failed to insert event filter (got -1). appId", zzatx.zzfE(paramString));
        }
        return true;
      }
      catch (SQLiteException paramzzb)
      {
        zzKl().zzLZ().zze("Error storing event filter. appId", zzatx.zzfE(paramString), paramzzb);
      }
      paramzzb = paramzzb;
      zzKl().zzLZ().zze("Configuration loss. Failed to serialize event filter. appId", zzatx.zzfE(paramString), paramzzb);
      return false;
    }
  }
  
  @WorkerThread
  private boolean zza(String paramString, int paramInt, zzauu.zze paramzze)
  {
    zzob();
    zzmR();
    zzac.zzdr(paramString);
    zzac.zzw(paramzze);
    if (TextUtils.isEmpty(paramzze.zzbwH))
    {
      zzKl().zzMb().zzd("Property filter had no property name. Audience definition ignored. appId, audienceId, filterId", zzatx.zzfE(paramString), Integer.valueOf(paramInt), String.valueOf(paramzze.zzbwr));
      return false;
    }
    try
    {
      byte[] arrayOfByte = new byte[paramzze.zzafB()];
      Object localObject = zzbyc.zzah(arrayOfByte);
      paramzze.zza((zzbyc)localObject);
      ((zzbyc)localObject).zzafo();
      localObject = new ContentValues();
      ((ContentValues)localObject).put("app_id", paramString);
      ((ContentValues)localObject).put("audience_id", Integer.valueOf(paramInt));
      ((ContentValues)localObject).put("filter_id", paramzze.zzbwr);
      ((ContentValues)localObject).put("property_name", paramzze.zzbwH);
      ((ContentValues)localObject).put("data", arrayOfByte);
      try
      {
        if (getWritableDatabase().insertWithOnConflict("property_filters", null, (ContentValues)localObject, 5) == -1L)
        {
          zzKl().zzLZ().zzj("Failed to insert property filter (got -1). appId", zzatx.zzfE(paramString));
          return false;
        }
      }
      catch (SQLiteException paramzze)
      {
        zzKl().zzLZ().zze("Error storing property filter. appId", zzatx.zzfE(paramString), paramzze);
        return false;
      }
      return true;
    }
    catch (IOException paramzze)
    {
      zzKl().zzLZ().zze("Configuration loss. Failed to serialize property filter. appId", zzatx.zzfE(paramString), paramzze);
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
      zzKl().zzLZ().zze("Database error", paramString, paramArrayOfString);
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
  
  @WorkerThread
  public void beginTransaction()
  {
    zzob();
    getWritableDatabase().beginTransaction();
  }
  
  @WorkerThread
  public void endTransaction()
  {
    zzob();
    getWritableDatabase().endTransaction();
  }
  
  @WorkerThread
  SQLiteDatabase getWritableDatabase()
  {
    zzmR();
    try
    {
      SQLiteDatabase localSQLiteDatabase = this.zzbrm.getWritableDatabase();
      return localSQLiteDatabase;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzKl().zzMb().zzj("Error opening database", localSQLiteException);
      throw localSQLiteException;
    }
  }
  
  @WorkerThread
  public void setTransactionSuccessful()
  {
    zzob();
    getWritableDatabase().setTransactionSuccessful();
  }
  
  @WorkerThread
  void zzA(String paramString, int paramInt)
  {
    zzob();
    zzmR();
    zzac.zzdr(paramString);
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.delete("property_filters", "app_id=? and audience_id=?", new String[] { paramString, String.valueOf(paramInt) });
    localSQLiteDatabase.delete("event_filters", "app_id=? and audience_id=?", new String[] { paramString, String.valueOf(paramInt) });
  }
  
  public void zzJ(List<Long> paramList)
  {
    zzac.zzw(paramList);
    zzmR();
    zzob();
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
      zzKl().zzLZ().zze("Deleted fewer rows from raw events table than expected", Integer.valueOf(i), Integer.valueOf(paramList.size()));
    }
  }
  
  /* Error */
  @WorkerThread
  public String zzLE()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   7: astore_1
    //   8: aload_1
    //   9: ldc_w 616
    //   12: aconst_null
    //   13: invokevirtual 218	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   16: astore_1
    //   17: aload_1
    //   18: astore_2
    //   19: aload_1
    //   20: invokeinterface 221 1 0
    //   25: ifeq +29 -> 54
    //   28: aload_1
    //   29: astore_2
    //   30: aload_1
    //   31: iconst_0
    //   32: invokeinterface 619 2 0
    //   37: astore_3
    //   38: aload_3
    //   39: astore_2
    //   40: aload_1
    //   41: ifnull +11 -> 52
    //   44: aload_1
    //   45: invokeinterface 228 1 0
    //   50: aload_3
    //   51: astore_2
    //   52: aload_2
    //   53: areturn
    //   54: aload 4
    //   56: astore_2
    //   57: aload_1
    //   58: ifnull -6 -> 52
    //   61: aload_1
    //   62: invokeinterface 228 1 0
    //   67: aconst_null
    //   68: areturn
    //   69: astore_3
    //   70: aconst_null
    //   71: astore_1
    //   72: aload_1
    //   73: astore_2
    //   74: aload_0
    //   75: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   78: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   81: ldc_w 621
    //   84: aload_3
    //   85: invokevirtual 296	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   88: aload 4
    //   90: astore_2
    //   91: aload_1
    //   92: ifnull -40 -> 52
    //   95: aload_1
    //   96: invokeinterface 228 1 0
    //   101: aconst_null
    //   102: areturn
    //   103: astore_1
    //   104: aconst_null
    //   105: astore_2
    //   106: aload_2
    //   107: ifnull +9 -> 116
    //   110: aload_2
    //   111: invokeinterface 228 1 0
    //   116: aload_1
    //   117: athrow
    //   118: astore_1
    //   119: goto -13 -> 106
    //   122: astore_3
    //   123: goto -51 -> 72
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	this	zzatj
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
  
  public boolean zzLF()
  {
    return zzb("select count(1) > 0 from queue where has_realtime = 1", null) != 0L;
  }
  
  @WorkerThread
  void zzLG()
  {
    zzmR();
    zzob();
    if (!zzLN()) {}
    long l1;
    long l2;
    do
    {
      return;
      l1 = zzKm().zzbte.get();
      l2 = zznR().elapsedRealtime();
    } while (Math.abs(l2 - l1) <= zzKn().zzLl());
    zzKm().zzbte.set(l2);
    zzLH();
  }
  
  @WorkerThread
  void zzLH()
  {
    zzmR();
    zzob();
    if (!zzLN()) {}
    int i;
    do
    {
      return;
      i = getWritableDatabase().delete("queue", "abs(bundle_end_timestamp - ?) > cast(? as integer)", new String[] { String.valueOf(zznR().currentTimeMillis()), String.valueOf(zzKn().zzLk()) });
    } while (i <= 0);
    zzKl().zzMf().zzj("Deleted stale rows. rowsDeleted", Integer.valueOf(i));
  }
  
  @WorkerThread
  public long zzLI()
  {
    return zza("select max(bundle_end_timestamp) from queue", null, 0L);
  }
  
  @WorkerThread
  public long zzLJ()
  {
    return zza("select max(timestamp) from raw_events", null, 0L);
  }
  
  public boolean zzLK()
  {
    return zzb("select count(1) > 0 from raw_events", null) != 0L;
  }
  
  public boolean zzLL()
  {
    return zzb("select count(1) > 0 from raw_events where realtime = 1", null) != 0L;
  }
  
  public long zzLM()
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
        zzKl().zzLZ().zzj("Error querying raw events", localSQLiteException);
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
  
  /* Error */
  @WorkerThread
  public zzatn zzQ(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aload_1
    //   4: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_2
    //   9: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   17: aload_0
    //   18: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   21: aload_0
    //   22: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   25: ldc_w 711
    //   28: iconst_3
    //   29: anewarray 306	java/lang/String
    //   32: dup
    //   33: iconst_0
    //   34: ldc_w 713
    //   37: aastore
    //   38: dup
    //   39: iconst_1
    //   40: ldc_w 715
    //   43: aastore
    //   44: dup
    //   45: iconst_2
    //   46: ldc_w 717
    //   49: aastore
    //   50: ldc_w 719
    //   53: iconst_2
    //   54: anewarray 306	java/lang/String
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
    //   68: invokevirtual 452	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   71: astore 4
    //   73: aload 4
    //   75: invokeinterface 221 1 0
    //   80: istore_3
    //   81: iload_3
    //   82: ifne +19 -> 101
    //   85: aload 4
    //   87: ifnull +10 -> 97
    //   90: aload 4
    //   92: invokeinterface 228 1 0
    //   97: aconst_null
    //   98: astore_1
    //   99: aload_1
    //   100: areturn
    //   101: new 721	com/google/android/gms/internal/zzatn
    //   104: dup
    //   105: aload_1
    //   106: aload_2
    //   107: aload 4
    //   109: iconst_0
    //   110: invokeinterface 225 2 0
    //   115: aload 4
    //   117: iconst_1
    //   118: invokeinterface 225 2 0
    //   123: aload 4
    //   125: iconst_2
    //   126: invokeinterface 225 2 0
    //   131: invokespecial 724	com/google/android/gms/internal/zzatn:<init>	(Ljava/lang/String;Ljava/lang/String;JJJ)V
    //   134: astore 5
    //   136: aload 4
    //   138: invokeinterface 727 1 0
    //   143: ifeq +20 -> 163
    //   146: aload_0
    //   147: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   150: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   153: ldc_w 729
    //   156: aload_1
    //   157: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   160: invokevirtual 296	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   163: aload 5
    //   165: astore_1
    //   166: aload 4
    //   168: ifnull -69 -> 99
    //   171: aload 4
    //   173: invokeinterface 228 1 0
    //   178: aload 5
    //   180: areturn
    //   181: astore 5
    //   183: aconst_null
    //   184: astore 4
    //   186: aload_0
    //   187: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   190: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   193: ldc_w 731
    //   196: aload_1
    //   197: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   200: aload_2
    //   201: aload 5
    //   203: invokevirtual 472	com/google/android/gms/internal/zzatx$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   206: aload 4
    //   208: ifnull +10 -> 218
    //   211: aload 4
    //   213: invokeinterface 228 1 0
    //   218: aconst_null
    //   219: areturn
    //   220: astore_1
    //   221: aload 5
    //   223: astore_2
    //   224: aload_2
    //   225: ifnull +9 -> 234
    //   228: aload_2
    //   229: invokeinterface 228 1 0
    //   234: aload_1
    //   235: athrow
    //   236: astore_1
    //   237: aload 4
    //   239: astore_2
    //   240: goto -16 -> 224
    //   243: astore_1
    //   244: aload 4
    //   246: astore_2
    //   247: goto -23 -> 224
    //   250: astore 5
    //   252: goto -66 -> 186
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	255	0	this	zzatj
    //   0	255	1	paramString1	String
    //   0	255	2	paramString2	String
    //   80	2	3	bool	boolean
    //   71	174	4	localCursor	Cursor
    //   1	178	5	localzzatn	zzatn
    //   181	41	5	localSQLiteException1	SQLiteException
    //   250	1	5	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   21	73	181	android/database/sqlite/SQLiteException
    //   21	73	220	finally
    //   73	81	236	finally
    //   101	163	236	finally
    //   186	206	243	finally
    //   73	81	250	android/database/sqlite/SQLiteException
    //   101	163	250	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  public void zzR(String paramString1, String paramString2)
  {
    zzac.zzdr(paramString1);
    zzac.zzdr(paramString2);
    zzmR();
    zzob();
    try
    {
      int i = getWritableDatabase().delete("user_attributes", "app_id=? and name=?", new String[] { paramString1, paramString2 });
      zzKl().zzMf().zzj("Deleted user attribute rows", Integer.valueOf(i));
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzKl().zzLZ().zzd("Error deleting user attribute. appId", zzatx.zzfE(paramString1), paramString2, localSQLiteException);
    }
  }
  
  /* Error */
  @WorkerThread
  public zzaus zzS(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aload_1
    //   4: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_2
    //   9: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   17: aload_0
    //   18: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   21: aload_0
    //   22: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   25: ldc_w 734
    //   28: iconst_3
    //   29: anewarray 306	java/lang/String
    //   32: dup
    //   33: iconst_0
    //   34: ldc_w 742
    //   37: aastore
    //   38: dup
    //   39: iconst_1
    //   40: ldc_w 743
    //   43: aastore
    //   44: dup
    //   45: iconst_2
    //   46: ldc 36
    //   48: aastore
    //   49: ldc_w 719
    //   52: iconst_2
    //   53: anewarray 306	java/lang/String
    //   56: dup
    //   57: iconst_0
    //   58: aload_1
    //   59: aastore
    //   60: dup
    //   61: iconst_1
    //   62: aload_2
    //   63: aastore
    //   64: aconst_null
    //   65: aconst_null
    //   66: aconst_null
    //   67: invokevirtual 452	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   70: astore 6
    //   72: aload 6
    //   74: invokeinterface 221 1 0
    //   79: istore_3
    //   80: iload_3
    //   81: ifne +19 -> 100
    //   84: aload 6
    //   86: ifnull +10 -> 96
    //   89: aload 6
    //   91: invokeinterface 228 1 0
    //   96: aconst_null
    //   97: astore_1
    //   98: aload_1
    //   99: areturn
    //   100: aload 6
    //   102: iconst_0
    //   103: invokeinterface 225 2 0
    //   108: lstore 4
    //   110: aload_0
    //   111: aload 6
    //   113: iconst_1
    //   114: invokevirtual 746	com/google/android/gms/internal/zzatj:zzb	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   117: astore 7
    //   119: new 748	com/google/android/gms/internal/zzaus
    //   122: dup
    //   123: aload_1
    //   124: aload 6
    //   126: iconst_2
    //   127: invokeinterface 619 2 0
    //   132: aload_2
    //   133: lload 4
    //   135: aload 7
    //   137: invokespecial 751	com/google/android/gms/internal/zzaus:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   140: astore 7
    //   142: aload 6
    //   144: invokeinterface 727 1 0
    //   149: ifeq +20 -> 169
    //   152: aload_0
    //   153: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   156: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   159: ldc_w 753
    //   162: aload_1
    //   163: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   166: invokevirtual 296	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   169: aload 7
    //   171: astore_1
    //   172: aload 6
    //   174: ifnull -76 -> 98
    //   177: aload 6
    //   179: invokeinterface 228 1 0
    //   184: aload 7
    //   186: areturn
    //   187: astore 7
    //   189: aconst_null
    //   190: astore 6
    //   192: aload_0
    //   193: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   196: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   199: ldc_w 755
    //   202: aload_1
    //   203: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   206: aload_2
    //   207: aload 7
    //   209: invokevirtual 472	com/google/android/gms/internal/zzatx$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   212: aload 6
    //   214: ifnull +10 -> 224
    //   217: aload 6
    //   219: invokeinterface 228 1 0
    //   224: aconst_null
    //   225: areturn
    //   226: astore_1
    //   227: aload 7
    //   229: astore_2
    //   230: aload_2
    //   231: ifnull +9 -> 240
    //   234: aload_2
    //   235: invokeinterface 228 1 0
    //   240: aload_1
    //   241: athrow
    //   242: astore_1
    //   243: aload 6
    //   245: astore_2
    //   246: goto -16 -> 230
    //   249: astore_1
    //   250: aload 6
    //   252: astore_2
    //   253: goto -23 -> 230
    //   256: astore 7
    //   258: goto -66 -> 192
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	261	0	this	zzatj
    //   0	261	1	paramString1	String
    //   0	261	2	paramString2	String
    //   79	2	3	bool	boolean
    //   108	26	4	l	long
    //   70	181	6	localCursor	Cursor
    //   1	184	7	localObject	Object
    //   187	41	7	localSQLiteException1	SQLiteException
    //   256	1	7	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   21	72	187	android/database/sqlite/SQLiteException
    //   21	72	226	finally
    //   72	80	242	finally
    //   100	169	242	finally
    //   192	212	249	finally
    //   72	80	256	android/database/sqlite/SQLiteException
    //   100	169	256	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  @WorkerThread
  public zzatg zzT(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_2
    //   6: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   9: pop
    //   10: aload_0
    //   11: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   14: aload_0
    //   15: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   18: aload_0
    //   19: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   22: ldc_w 759
    //   25: bipush 11
    //   27: anewarray 306	java/lang/String
    //   30: dup
    //   31: iconst_0
    //   32: ldc 36
    //   34: aastore
    //   35: dup
    //   36: iconst_1
    //   37: ldc_w 743
    //   40: aastore
    //   41: dup
    //   42: iconst_2
    //   43: ldc_w 761
    //   46: aastore
    //   47: dup
    //   48: iconst_3
    //   49: ldc_w 763
    //   52: aastore
    //   53: dup
    //   54: iconst_4
    //   55: ldc_w 765
    //   58: aastore
    //   59: dup
    //   60: iconst_5
    //   61: ldc_w 767
    //   64: aastore
    //   65: dup
    //   66: bipush 6
    //   68: ldc_w 769
    //   71: aastore
    //   72: dup
    //   73: bipush 7
    //   75: ldc_w 771
    //   78: aastore
    //   79: dup
    //   80: bipush 8
    //   82: ldc_w 742
    //   85: aastore
    //   86: dup
    //   87: bipush 9
    //   89: ldc_w 773
    //   92: aastore
    //   93: dup
    //   94: bipush 10
    //   96: ldc_w 775
    //   99: aastore
    //   100: ldc_w 719
    //   103: iconst_2
    //   104: anewarray 306	java/lang/String
    //   107: dup
    //   108: iconst_0
    //   109: aload_1
    //   110: aastore
    //   111: dup
    //   112: iconst_1
    //   113: aload_2
    //   114: aastore
    //   115: aconst_null
    //   116: aconst_null
    //   117: aconst_null
    //   118: invokevirtual 452	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   121: astore 12
    //   123: aload 12
    //   125: invokeinterface 221 1 0
    //   130: istore_3
    //   131: iload_3
    //   132: ifne +19 -> 151
    //   135: aload 12
    //   137: ifnull +10 -> 147
    //   140: aload 12
    //   142: invokeinterface 228 1 0
    //   147: aconst_null
    //   148: astore_1
    //   149: aload_1
    //   150: areturn
    //   151: aload 12
    //   153: iconst_0
    //   154: invokeinterface 619 2 0
    //   159: astore 13
    //   161: aload_0
    //   162: aload 12
    //   164: iconst_1
    //   165: invokevirtual 746	com/google/android/gms/internal/zzatj:zzb	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   168: astore 14
    //   170: aload 12
    //   172: iconst_2
    //   173: invokeinterface 778 2 0
    //   178: ifeq +216 -> 394
    //   181: iconst_1
    //   182: istore_3
    //   183: aload 12
    //   185: iconst_3
    //   186: invokeinterface 619 2 0
    //   191: astore 15
    //   193: aload 12
    //   195: iconst_4
    //   196: invokeinterface 225 2 0
    //   201: lstore 4
    //   203: aload_0
    //   204: invokevirtual 782	com/google/android/gms/internal/zzatj:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   207: aload 12
    //   209: iconst_5
    //   210: invokeinterface 786 2 0
    //   215: getstatic 792	com/google/android/gms/internal/zzatq:CREATOR	Landroid/os/Parcelable$Creator;
    //   218: invokevirtual 797	com/google/android/gms/internal/zzaut:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   221: checkcast 788	com/google/android/gms/internal/zzatq
    //   224: astore 16
    //   226: aload 12
    //   228: bipush 6
    //   230: invokeinterface 225 2 0
    //   235: lstore 6
    //   237: aload_0
    //   238: invokevirtual 782	com/google/android/gms/internal/zzatj:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   241: aload 12
    //   243: bipush 7
    //   245: invokeinterface 786 2 0
    //   250: getstatic 792	com/google/android/gms/internal/zzatq:CREATOR	Landroid/os/Parcelable$Creator;
    //   253: invokevirtual 797	com/google/android/gms/internal/zzaut:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   256: checkcast 788	com/google/android/gms/internal/zzatq
    //   259: astore 17
    //   261: aload 12
    //   263: bipush 8
    //   265: invokeinterface 225 2 0
    //   270: lstore 8
    //   272: aload 12
    //   274: bipush 9
    //   276: invokeinterface 225 2 0
    //   281: lstore 10
    //   283: aload_0
    //   284: invokevirtual 782	com/google/android/gms/internal/zzatj:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   287: aload 12
    //   289: bipush 10
    //   291: invokeinterface 786 2 0
    //   296: getstatic 792	com/google/android/gms/internal/zzatq:CREATOR	Landroid/os/Parcelable$Creator;
    //   299: invokevirtual 797	com/google/android/gms/internal/zzaut:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   302: checkcast 788	com/google/android/gms/internal/zzatq
    //   305: astore 18
    //   307: new 799	com/google/android/gms/internal/zzatg
    //   310: dup
    //   311: aload_1
    //   312: aload 13
    //   314: new 801	com/google/android/gms/internal/zzauq
    //   317: dup
    //   318: aload_2
    //   319: lload 8
    //   321: aload 14
    //   323: aload 13
    //   325: invokespecial 804	com/google/android/gms/internal/zzauq:<init>	(Ljava/lang/String;JLjava/lang/Object;Ljava/lang/String;)V
    //   328: lload 6
    //   330: iload_3
    //   331: aload 15
    //   333: aload 16
    //   335: lload 4
    //   337: aload 17
    //   339: lload 10
    //   341: aload 18
    //   343: invokespecial 807	com/google/android/gms/internal/zzatg:<init>	(Ljava/lang/String;Ljava/lang/String;Lcom/google/android/gms/internal/zzauq;JZLjava/lang/String;Lcom/google/android/gms/internal/zzatq;JLcom/google/android/gms/internal/zzatq;JLcom/google/android/gms/internal/zzatq;)V
    //   346: astore 13
    //   348: aload 12
    //   350: invokeinterface 727 1 0
    //   355: ifeq +21 -> 376
    //   358: aload_0
    //   359: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   362: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   365: ldc_w 809
    //   368: aload_1
    //   369: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   372: aload_2
    //   373: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   376: aload 13
    //   378: astore_1
    //   379: aload 12
    //   381: ifnull -232 -> 149
    //   384: aload 12
    //   386: invokeinterface 228 1 0
    //   391: aload 13
    //   393: areturn
    //   394: iconst_0
    //   395: istore_3
    //   396: goto -213 -> 183
    //   399: astore 13
    //   401: aconst_null
    //   402: astore 12
    //   404: aload_0
    //   405: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   408: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   411: ldc_w 811
    //   414: aload_1
    //   415: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   418: aload_2
    //   419: aload 13
    //   421: invokevirtual 472	com/google/android/gms/internal/zzatx$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   424: aload 12
    //   426: ifnull +10 -> 436
    //   429: aload 12
    //   431: invokeinterface 228 1 0
    //   436: aconst_null
    //   437: areturn
    //   438: astore_1
    //   439: aconst_null
    //   440: astore 12
    //   442: aload 12
    //   444: ifnull +10 -> 454
    //   447: aload 12
    //   449: invokeinterface 228 1 0
    //   454: aload_1
    //   455: athrow
    //   456: astore_1
    //   457: goto -15 -> 442
    //   460: astore_1
    //   461: goto -19 -> 442
    //   464: astore 13
    //   466: goto -62 -> 404
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	469	0	this	zzatj
    //   0	469	1	paramString1	String
    //   0	469	2	paramString2	String
    //   130	266	3	bool	boolean
    //   201	135	4	l1	long
    //   235	94	6	l2	long
    //   270	50	8	l3	long
    //   281	59	10	l4	long
    //   121	327	12	localCursor	Cursor
    //   159	233	13	localObject1	Object
    //   399	21	13	localSQLiteException1	SQLiteException
    //   464	1	13	localSQLiteException2	SQLiteException
    //   168	154	14	localObject2	Object
    //   191	141	15	str	String
    //   224	110	16	localzzatq1	zzatq
    //   259	79	17	localzzatq2	zzatq
    //   305	37	18	localzzatq3	zzatq
    // Exception table:
    //   from	to	target	type
    //   18	123	399	android/database/sqlite/SQLiteException
    //   18	123	438	finally
    //   123	131	456	finally
    //   151	181	456	finally
    //   183	376	456	finally
    //   404	424	460	finally
    //   123	131	464	android/database/sqlite/SQLiteException
    //   151	181	464	android/database/sqlite/SQLiteException
    //   183	376	464	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  public int zzU(String paramString1, String paramString2)
  {
    zzac.zzdr(paramString1);
    zzac.zzdr(paramString2);
    zzmR();
    zzob();
    try
    {
      int i = getWritableDatabase().delete("conditional_properties", "app_id=? and name=?", new String[] { paramString1, paramString2 });
      return i;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzKl().zzLZ().zzd("Error deleting conditional property", zzatx.zzfE(paramString1), paramString2, localSQLiteException);
    }
    return 0;
  }
  
  /* Error */
  Map<Integer, List<zzauu.zzb>> zzV(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   4: aload_0
    //   5: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   8: aload_1
    //   9: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_2
    //   14: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   17: pop
    //   18: new 28	android/support/v4/util/ArrayMap
    //   21: dup
    //   22: invokespecial 818	android/support/v4/util/ArrayMap:<init>	()V
    //   25: astore 8
    //   27: aload_0
    //   28: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   31: astore 5
    //   33: aload 5
    //   35: ldc_w 512
    //   38: iconst_2
    //   39: anewarray 306	java/lang/String
    //   42: dup
    //   43: iconst_0
    //   44: ldc_w 498
    //   47: aastore
    //   48: dup
    //   49: iconst_1
    //   50: ldc_w 507
    //   53: aastore
    //   54: ldc_w 820
    //   57: iconst_2
    //   58: anewarray 306	java/lang/String
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
    //   72: invokevirtual 452	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   75: astore 5
    //   77: aload 5
    //   79: astore_2
    //   80: aload 5
    //   82: invokeinterface 221 1 0
    //   87: ifne +26 -> 113
    //   90: aload 5
    //   92: astore_2
    //   93: invokestatic 823	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   96: astore 6
    //   98: aload 5
    //   100: ifnull +10 -> 110
    //   103: aload 5
    //   105: invokeinterface 228 1 0
    //   110: aload 6
    //   112: areturn
    //   113: aload 5
    //   115: astore_2
    //   116: aload 5
    //   118: iconst_1
    //   119: invokeinterface 786 2 0
    //   124: invokestatic 829	com/google/android/gms/internal/zzbyb:zzag	([B)Lcom/google/android/gms/internal/zzbyb;
    //   127: astore 6
    //   129: aload 5
    //   131: astore_2
    //   132: new 422	com/google/android/gms/internal/zzauu$zzb
    //   135: dup
    //   136: invokespecial 830	com/google/android/gms/internal/zzauu$zzb:<init>	()V
    //   139: astore 9
    //   141: aload 5
    //   143: astore_2
    //   144: aload 9
    //   146: aload 6
    //   148: invokevirtual 833	com/google/android/gms/internal/zzauu$zzb:zzb	(Lcom/google/android/gms/internal/zzbyb;)Lcom/google/android/gms/internal/zzbyj;
    //   151: pop
    //   152: aload 5
    //   154: astore_2
    //   155: aload 5
    //   157: iconst_0
    //   158: invokeinterface 778 2 0
    //   163: istore_3
    //   164: aload 5
    //   166: astore_2
    //   167: aload 8
    //   169: iload_3
    //   170: invokestatic 468	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   173: invokeinterface 835 2 0
    //   178: checkcast 590	java/util/List
    //   181: astore 7
    //   183: aload 7
    //   185: astore 6
    //   187: aload 7
    //   189: ifnonnull +32 -> 221
    //   192: aload 5
    //   194: astore_2
    //   195: new 837	java/util/ArrayList
    //   198: dup
    //   199: invokespecial 838	java/util/ArrayList:<init>	()V
    //   202: astore 6
    //   204: aload 5
    //   206: astore_2
    //   207: aload 8
    //   209: iload_3
    //   210: invokestatic 468	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   213: aload 6
    //   215: invokeinterface 44 3 0
    //   220: pop
    //   221: aload 5
    //   223: astore_2
    //   224: aload 6
    //   226: aload 9
    //   228: invokeinterface 841 2 0
    //   233: pop
    //   234: aload 5
    //   236: astore_2
    //   237: aload 5
    //   239: invokeinterface 727 1 0
    //   244: istore 4
    //   246: iload 4
    //   248: ifne -135 -> 113
    //   251: aload 5
    //   253: ifnull +10 -> 263
    //   256: aload 5
    //   258: invokeinterface 228 1 0
    //   263: aload 8
    //   265: areturn
    //   266: astore 6
    //   268: aload 5
    //   270: astore_2
    //   271: aload_0
    //   272: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   275: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   278: ldc_w 843
    //   281: aload_1
    //   282: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   285: aload 6
    //   287: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   290: goto -56 -> 234
    //   293: astore 6
    //   295: aload 5
    //   297: astore_2
    //   298: aload_0
    //   299: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   302: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   305: ldc_w 845
    //   308: aload_1
    //   309: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   312: aload 6
    //   314: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   317: aload 5
    //   319: ifnull +10 -> 329
    //   322: aload 5
    //   324: invokeinterface 228 1 0
    //   329: aconst_null
    //   330: areturn
    //   331: astore_1
    //   332: aconst_null
    //   333: astore_2
    //   334: aload_2
    //   335: ifnull +9 -> 344
    //   338: aload_2
    //   339: invokeinterface 228 1 0
    //   344: aload_1
    //   345: athrow
    //   346: astore_1
    //   347: goto -13 -> 334
    //   350: astore 6
    //   352: aconst_null
    //   353: astore 5
    //   355: goto -60 -> 295
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	358	0	this	zzatj
    //   0	358	1	paramString1	String
    //   0	358	2	paramString2	String
    //   163	47	3	i	int
    //   244	3	4	bool	boolean
    //   31	323	5	localObject1	Object
    //   96	129	6	localObject2	Object
    //   266	20	6	localIOException	IOException
    //   293	20	6	localSQLiteException1	SQLiteException
    //   350	1	6	localSQLiteException2	SQLiteException
    //   181	7	7	localList	List
    //   25	239	8	localArrayMap	ArrayMap
    //   139	88	9	localzzb	zzauu.zzb
    // Exception table:
    //   from	to	target	type
    //   144	152	266	java/io/IOException
    //   80	90	293	android/database/sqlite/SQLiteException
    //   93	98	293	android/database/sqlite/SQLiteException
    //   116	129	293	android/database/sqlite/SQLiteException
    //   132	141	293	android/database/sqlite/SQLiteException
    //   144	152	293	android/database/sqlite/SQLiteException
    //   155	164	293	android/database/sqlite/SQLiteException
    //   167	183	293	android/database/sqlite/SQLiteException
    //   195	204	293	android/database/sqlite/SQLiteException
    //   207	221	293	android/database/sqlite/SQLiteException
    //   224	234	293	android/database/sqlite/SQLiteException
    //   237	246	293	android/database/sqlite/SQLiteException
    //   271	290	293	android/database/sqlite/SQLiteException
    //   33	77	331	finally
    //   80	90	346	finally
    //   93	98	346	finally
    //   116	129	346	finally
    //   132	141	346	finally
    //   144	152	346	finally
    //   155	164	346	finally
    //   167	183	346	finally
    //   195	204	346	finally
    //   207	221	346	finally
    //   224	234	346	finally
    //   237	246	346	finally
    //   271	290	346	finally
    //   298	317	346	finally
    //   33	77	350	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  Map<Integer, List<zzauu.zze>> zzW(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   4: aload_0
    //   5: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   8: aload_1
    //   9: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_2
    //   14: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   17: pop
    //   18: new 28	android/support/v4/util/ArrayMap
    //   21: dup
    //   22: invokespecial 818	android/support/v4/util/ArrayMap:<init>	()V
    //   25: astore 8
    //   27: aload_0
    //   28: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   31: astore 5
    //   33: aload 5
    //   35: ldc_w 535
    //   38: iconst_2
    //   39: anewarray 306	java/lang/String
    //   42: dup
    //   43: iconst_0
    //   44: ldc_w 498
    //   47: aastore
    //   48: dup
    //   49: iconst_1
    //   50: ldc_w 507
    //   53: aastore
    //   54: ldc_w 849
    //   57: iconst_2
    //   58: anewarray 306	java/lang/String
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
    //   72: invokevirtual 452	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   75: astore 5
    //   77: aload 5
    //   79: astore_2
    //   80: aload 5
    //   82: invokeinterface 221 1 0
    //   87: ifne +26 -> 113
    //   90: aload 5
    //   92: astore_2
    //   93: invokestatic 823	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   96: astore 6
    //   98: aload 5
    //   100: ifnull +10 -> 110
    //   103: aload 5
    //   105: invokeinterface 228 1 0
    //   110: aload 6
    //   112: areturn
    //   113: aload 5
    //   115: astore_2
    //   116: aload 5
    //   118: iconst_1
    //   119: invokeinterface 786 2 0
    //   124: invokestatic 829	com/google/android/gms/internal/zzbyb:zzag	([B)Lcom/google/android/gms/internal/zzbyb;
    //   127: astore 6
    //   129: aload 5
    //   131: astore_2
    //   132: new 429	com/google/android/gms/internal/zzauu$zze
    //   135: dup
    //   136: invokespecial 850	com/google/android/gms/internal/zzauu$zze:<init>	()V
    //   139: astore 9
    //   141: aload 5
    //   143: astore_2
    //   144: aload 9
    //   146: aload 6
    //   148: invokevirtual 851	com/google/android/gms/internal/zzauu$zze:zzb	(Lcom/google/android/gms/internal/zzbyb;)Lcom/google/android/gms/internal/zzbyj;
    //   151: pop
    //   152: aload 5
    //   154: astore_2
    //   155: aload 5
    //   157: iconst_0
    //   158: invokeinterface 778 2 0
    //   163: istore_3
    //   164: aload 5
    //   166: astore_2
    //   167: aload 8
    //   169: iload_3
    //   170: invokestatic 468	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   173: invokeinterface 835 2 0
    //   178: checkcast 590	java/util/List
    //   181: astore 7
    //   183: aload 7
    //   185: astore 6
    //   187: aload 7
    //   189: ifnonnull +32 -> 221
    //   192: aload 5
    //   194: astore_2
    //   195: new 837	java/util/ArrayList
    //   198: dup
    //   199: invokespecial 838	java/util/ArrayList:<init>	()V
    //   202: astore 6
    //   204: aload 5
    //   206: astore_2
    //   207: aload 8
    //   209: iload_3
    //   210: invokestatic 468	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   213: aload 6
    //   215: invokeinterface 44 3 0
    //   220: pop
    //   221: aload 5
    //   223: astore_2
    //   224: aload 6
    //   226: aload 9
    //   228: invokeinterface 841 2 0
    //   233: pop
    //   234: aload 5
    //   236: astore_2
    //   237: aload 5
    //   239: invokeinterface 727 1 0
    //   244: istore 4
    //   246: iload 4
    //   248: ifne -135 -> 113
    //   251: aload 5
    //   253: ifnull +10 -> 263
    //   256: aload 5
    //   258: invokeinterface 228 1 0
    //   263: aload 8
    //   265: areturn
    //   266: astore 6
    //   268: aload 5
    //   270: astore_2
    //   271: aload_0
    //   272: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   275: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   278: ldc_w 853
    //   281: aload_1
    //   282: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   285: aload 6
    //   287: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   290: goto -56 -> 234
    //   293: astore 6
    //   295: aload 5
    //   297: astore_2
    //   298: aload_0
    //   299: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   302: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   305: ldc_w 845
    //   308: aload_1
    //   309: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   312: aload 6
    //   314: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   317: aload 5
    //   319: ifnull +10 -> 329
    //   322: aload 5
    //   324: invokeinterface 228 1 0
    //   329: aconst_null
    //   330: areturn
    //   331: astore_1
    //   332: aconst_null
    //   333: astore_2
    //   334: aload_2
    //   335: ifnull +9 -> 344
    //   338: aload_2
    //   339: invokeinterface 228 1 0
    //   344: aload_1
    //   345: athrow
    //   346: astore_1
    //   347: goto -13 -> 334
    //   350: astore 6
    //   352: aconst_null
    //   353: astore 5
    //   355: goto -60 -> 295
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	358	0	this	zzatj
    //   0	358	1	paramString1	String
    //   0	358	2	paramString2	String
    //   163	47	3	i	int
    //   244	3	4	bool	boolean
    //   31	323	5	localObject1	Object
    //   96	129	6	localObject2	Object
    //   266	20	6	localIOException	IOException
    //   293	20	6	localSQLiteException1	SQLiteException
    //   350	1	6	localSQLiteException2	SQLiteException
    //   181	7	7	localList	List
    //   25	239	8	localArrayMap	ArrayMap
    //   139	88	9	localzze	zzauu.zze
    // Exception table:
    //   from	to	target	type
    //   144	152	266	java/io/IOException
    //   80	90	293	android/database/sqlite/SQLiteException
    //   93	98	293	android/database/sqlite/SQLiteException
    //   116	129	293	android/database/sqlite/SQLiteException
    //   132	141	293	android/database/sqlite/SQLiteException
    //   144	152	293	android/database/sqlite/SQLiteException
    //   155	164	293	android/database/sqlite/SQLiteException
    //   167	183	293	android/database/sqlite/SQLiteException
    //   195	204	293	android/database/sqlite/SQLiteException
    //   207	221	293	android/database/sqlite/SQLiteException
    //   224	234	293	android/database/sqlite/SQLiteException
    //   237	246	293	android/database/sqlite/SQLiteException
    //   271	290	293	android/database/sqlite/SQLiteException
    //   33	77	331	finally
    //   80	90	346	finally
    //   93	98	346	finally
    //   116	129	346	finally
    //   132	141	346	finally
    //   144	152	346	finally
    //   155	164	346	finally
    //   167	183	346	finally
    //   195	204	346	finally
    //   207	221	346	finally
    //   224	234	346	finally
    //   237	246	346	finally
    //   271	290	346	finally
    //   298	317	346	finally
    //   33	77	350	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  @WorkerThread
  protected long zzX(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_2
    //   6: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   9: pop
    //   10: aload_0
    //   11: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   14: aload_0
    //   15: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   18: aload_0
    //   19: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   22: astore 8
    //   24: aload 8
    //   26: invokevirtual 565	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   29: aload_0
    //   30: new 318	java/lang/StringBuilder
    //   33: dup
    //   34: aload_2
    //   35: invokestatic 322	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   38: invokevirtual 326	java/lang/String:length	()I
    //   41: bipush 32
    //   43: iadd
    //   44: invokespecial 327	java/lang/StringBuilder:<init>	(I)V
    //   47: ldc_w 858
    //   50: invokevirtual 333	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: aload_2
    //   54: invokevirtual 333	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: ldc_w 860
    //   60: invokevirtual 333	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   63: invokevirtual 338	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   66: iconst_1
    //   67: anewarray 306	java/lang/String
    //   70: dup
    //   71: iconst_0
    //   72: aload_1
    //   73: aastore
    //   74: ldc2_w 517
    //   77: invokespecial 693	com/google/android/gms/internal/zzatj:zza	(Ljava/lang/String;[Ljava/lang/String;J)J
    //   80: lstore 5
    //   82: lload 5
    //   84: lstore_3
    //   85: lload 5
    //   87: ldc2_w 517
    //   90: lcmp
    //   91: ifne +92 -> 183
    //   94: new 489	android/content/ContentValues
    //   97: dup
    //   98: invokespecial 491	android/content/ContentValues:<init>	()V
    //   101: astore 7
    //   103: aload 7
    //   105: ldc_w 493
    //   108: aload_1
    //   109: invokevirtual 496	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   112: aload 7
    //   114: ldc_w 862
    //   117: iconst_0
    //   118: invokestatic 468	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   121: invokevirtual 501	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   124: aload 7
    //   126: ldc -118
    //   128: iconst_0
    //   129: invokestatic 468	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   132: invokevirtual 501	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   135: aload 8
    //   137: ldc_w 864
    //   140: aconst_null
    //   141: aload 7
    //   143: iconst_5
    //   144: invokevirtual 516	android/database/sqlite/SQLiteDatabase:insertWithOnConflict	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    //   147: ldc2_w 517
    //   150: lcmp
    //   151: ifne +30 -> 181
    //   154: aload_0
    //   155: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   158: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   161: ldc_w 866
    //   164: aload_1
    //   165: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   168: aload_2
    //   169: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   172: aload 8
    //   174: invokevirtual 568	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   177: ldc2_w 517
    //   180: lreturn
    //   181: lconst_0
    //   182: lstore_3
    //   183: new 489	android/content/ContentValues
    //   186: dup
    //   187: invokespecial 491	android/content/ContentValues:<init>	()V
    //   190: astore 7
    //   192: aload 7
    //   194: ldc_w 493
    //   197: aload_1
    //   198: invokevirtual 496	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   201: aload 7
    //   203: aload_2
    //   204: lconst_1
    //   205: lload_3
    //   206: ladd
    //   207: invokestatic 869	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   210: invokevirtual 872	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   213: aload 8
    //   215: ldc_w 864
    //   218: aload 7
    //   220: ldc_w 874
    //   223: iconst_1
    //   224: anewarray 306	java/lang/String
    //   227: dup
    //   228: iconst_0
    //   229: aload_1
    //   230: aastore
    //   231: invokevirtual 878	android/database/sqlite/SQLiteDatabase:update	(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   234: i2l
    //   235: lconst_0
    //   236: lcmp
    //   237: ifne +30 -> 267
    //   240: aload_0
    //   241: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   244: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   247: ldc_w 880
    //   250: aload_1
    //   251: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   254: aload_2
    //   255: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   258: aload 8
    //   260: invokevirtual 568	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   263: ldc2_w 517
    //   266: lreturn
    //   267: aload 8
    //   269: invokevirtual 574	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   272: aload 8
    //   274: invokevirtual 568	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   277: lload_3
    //   278: lreturn
    //   279: astore 7
    //   281: lconst_0
    //   282: lstore_3
    //   283: aload_0
    //   284: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   287: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   290: ldc_w 882
    //   293: aload_1
    //   294: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   297: aload_2
    //   298: aload 7
    //   300: invokevirtual 472	com/google/android/gms/internal/zzatx$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   303: aload 8
    //   305: invokevirtual 568	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   308: lload_3
    //   309: lreturn
    //   310: astore_1
    //   311: aload 8
    //   313: invokevirtual 568	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   316: aload_1
    //   317: athrow
    //   318: astore 7
    //   320: goto -37 -> 283
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	323	0	this	zzatj
    //   0	323	1	paramString1	String
    //   0	323	2	paramString2	String
    //   84	225	3	l1	long
    //   80	6	5	l2	long
    //   101	118	7	localContentValues	ContentValues
    //   279	20	7	localSQLiteException1	SQLiteException
    //   318	1	7	localSQLiteException2	SQLiteException
    //   22	290	8	localSQLiteDatabase	SQLiteDatabase
    // Exception table:
    //   from	to	target	type
    //   29	82	279	android/database/sqlite/SQLiteException
    //   94	172	279	android/database/sqlite/SQLiteException
    //   29	82	310	finally
    //   94	172	310	finally
    //   183	258	310	finally
    //   267	272	310	finally
    //   283	303	310	finally
    //   183	258	318	android/database/sqlite/SQLiteException
    //   267	272	318	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  public long zza(zzauw.zze paramzze)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   4: aload_0
    //   5: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   8: aload_1
    //   9: invokestatic 395	com/google/android/gms/common/internal/zzac:zzw	(Ljava/lang/Object;)Ljava/lang/Object;
    //   12: pop
    //   13: aload_1
    //   14: getfield 888	com/google/android/gms/internal/zzauw$zze:zzaS	Ljava/lang/String;
    //   17: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   20: pop
    //   21: aload_1
    //   22: invokevirtual 889	com/google/android/gms/internal/zzauw$zze:zzafB	()I
    //   25: newarray <illegal type>
    //   27: astore 4
    //   29: aload 4
    //   31: invokestatic 481	com/google/android/gms/internal/zzbyc:zzah	([B)Lcom/google/android/gms/internal/zzbyc;
    //   34: astore 5
    //   36: aload_1
    //   37: aload 5
    //   39: invokevirtual 890	com/google/android/gms/internal/zzauw$zze:zza	(Lcom/google/android/gms/internal/zzbyc;)V
    //   42: aload 5
    //   44: invokevirtual 487	com/google/android/gms/internal/zzbyc:zzafo	()V
    //   47: aload_0
    //   48: invokevirtual 782	com/google/android/gms/internal/zzatj:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   51: aload 4
    //   53: invokevirtual 894	com/google/android/gms/internal/zzaut:zzz	([B)J
    //   56: lstore_2
    //   57: new 489	android/content/ContentValues
    //   60: dup
    //   61: invokespecial 491	android/content/ContentValues:<init>	()V
    //   64: astore 5
    //   66: aload 5
    //   68: ldc_w 493
    //   71: aload_1
    //   72: getfield 888	com/google/android/gms/internal/zzauw$zze:zzaS	Ljava/lang/String;
    //   75: invokevirtual 496	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   78: aload 5
    //   80: ldc_w 896
    //   83: lload_2
    //   84: invokestatic 869	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   87: invokevirtual 872	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   90: aload 5
    //   92: ldc_w 898
    //   95: aload 4
    //   97: invokevirtual 510	android/content/ContentValues:put	(Ljava/lang/String;[B)V
    //   100: aload_0
    //   101: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   104: ldc_w 900
    //   107: aconst_null
    //   108: aload 5
    //   110: iconst_4
    //   111: invokevirtual 516	android/database/sqlite/SQLiteDatabase:insertWithOnConflict	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    //   114: pop2
    //   115: lload_2
    //   116: lreturn
    //   117: astore 4
    //   119: aload_0
    //   120: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   123: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   126: ldc_w 902
    //   129: aload_1
    //   130: getfield 888	com/google/android/gms/internal/zzauw$zze:zzaS	Ljava/lang/String;
    //   133: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   136: aload 4
    //   138: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   141: aload 4
    //   143: athrow
    //   144: astore 4
    //   146: aload_0
    //   147: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   150: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   153: ldc_w 904
    //   156: aload_1
    //   157: getfield 888	com/google/android/gms/internal/zzauw$zze:zzaS	Ljava/lang/String;
    //   160: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   163: aload 4
    //   165: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   168: aload 4
    //   170: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	171	0	this	zzatj
    //   0	171	1	paramzze	zzauw.zze
    //   56	60	2	l	long
    //   27	69	4	arrayOfByte	byte[]
    //   117	25	4	localIOException	IOException
    //   144	25	4	localSQLiteException	SQLiteException
    //   34	75	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   21	47	117	java/io/IOException
    //   100	115	144	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  @WorkerThread
  public zza zza(long paramLong, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
  {
    // Byte code:
    //   0: aload_3
    //   1: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   9: aload_0
    //   10: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   13: new 6	com/google/android/gms/internal/zzatj$zza
    //   16: dup
    //   17: invokespecial 906	com/google/android/gms/internal/zzatj$zza:<init>	()V
    //   20: astore 12
    //   22: aload_0
    //   23: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   26: astore 11
    //   28: aload 11
    //   30: ldc_w 908
    //   33: bipush 6
    //   35: anewarray 306	java/lang/String
    //   38: dup
    //   39: iconst_0
    //   40: ldc 72
    //   42: aastore
    //   43: dup
    //   44: iconst_1
    //   45: ldc 80
    //   47: aastore
    //   48: dup
    //   49: iconst_2
    //   50: ldc 76
    //   52: aastore
    //   53: dup
    //   54: iconst_3
    //   55: ldc 84
    //   57: aastore
    //   58: dup
    //   59: iconst_4
    //   60: ldc 108
    //   62: aastore
    //   63: dup
    //   64: iconst_5
    //   65: ldc 112
    //   67: aastore
    //   68: ldc_w 910
    //   71: iconst_1
    //   72: anewarray 306	java/lang/String
    //   75: dup
    //   76: iconst_0
    //   77: aload_3
    //   78: aastore
    //   79: aconst_null
    //   80: aconst_null
    //   81: aconst_null
    //   82: invokevirtual 452	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   85: astore 10
    //   87: aload 10
    //   89: astore 9
    //   91: aload 10
    //   93: invokeinterface 221 1 0
    //   98: ifne +39 -> 137
    //   101: aload 10
    //   103: astore 9
    //   105: aload_0
    //   106: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   109: invokevirtual 266	com/google/android/gms/internal/zzatx:zzMb	()Lcom/google/android/gms/internal/zzatx$zza;
    //   112: ldc_w 912
    //   115: aload_3
    //   116: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   119: invokevirtual 296	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   122: aload 10
    //   124: ifnull +10 -> 134
    //   127: aload 10
    //   129: invokeinterface 228 1 0
    //   134: aload 12
    //   136: areturn
    //   137: aload 10
    //   139: astore 9
    //   141: aload 10
    //   143: iconst_0
    //   144: invokeinterface 225 2 0
    //   149: lload_1
    //   150: lcmp
    //   151: ifne +88 -> 239
    //   154: aload 10
    //   156: astore 9
    //   158: aload 12
    //   160: aload 10
    //   162: iconst_1
    //   163: invokeinterface 225 2 0
    //   168: putfield 916	com/google/android/gms/internal/zzatj$zza:zzbrp	J
    //   171: aload 10
    //   173: astore 9
    //   175: aload 12
    //   177: aload 10
    //   179: iconst_2
    //   180: invokeinterface 225 2 0
    //   185: putfield 919	com/google/android/gms/internal/zzatj$zza:zzbro	J
    //   188: aload 10
    //   190: astore 9
    //   192: aload 12
    //   194: aload 10
    //   196: iconst_3
    //   197: invokeinterface 225 2 0
    //   202: putfield 922	com/google/android/gms/internal/zzatj$zza:zzbrq	J
    //   205: aload 10
    //   207: astore 9
    //   209: aload 12
    //   211: aload 10
    //   213: iconst_4
    //   214: invokeinterface 225 2 0
    //   219: putfield 925	com/google/android/gms/internal/zzatj$zza:zzbrr	J
    //   222: aload 10
    //   224: astore 9
    //   226: aload 12
    //   228: aload 10
    //   230: iconst_5
    //   231: invokeinterface 225 2 0
    //   236: putfield 928	com/google/android/gms/internal/zzatj$zza:zzbrs	J
    //   239: iload 4
    //   241: ifeq +19 -> 260
    //   244: aload 10
    //   246: astore 9
    //   248: aload 12
    //   250: aload 12
    //   252: getfield 916	com/google/android/gms/internal/zzatj$zza:zzbrp	J
    //   255: lconst_1
    //   256: ladd
    //   257: putfield 916	com/google/android/gms/internal/zzatj$zza:zzbrp	J
    //   260: iload 5
    //   262: ifeq +19 -> 281
    //   265: aload 10
    //   267: astore 9
    //   269: aload 12
    //   271: aload 12
    //   273: getfield 919	com/google/android/gms/internal/zzatj$zza:zzbro	J
    //   276: lconst_1
    //   277: ladd
    //   278: putfield 919	com/google/android/gms/internal/zzatj$zza:zzbro	J
    //   281: iload 6
    //   283: ifeq +19 -> 302
    //   286: aload 10
    //   288: astore 9
    //   290: aload 12
    //   292: aload 12
    //   294: getfield 922	com/google/android/gms/internal/zzatj$zza:zzbrq	J
    //   297: lconst_1
    //   298: ladd
    //   299: putfield 922	com/google/android/gms/internal/zzatj$zza:zzbrq	J
    //   302: iload 7
    //   304: ifeq +19 -> 323
    //   307: aload 10
    //   309: astore 9
    //   311: aload 12
    //   313: aload 12
    //   315: getfield 925	com/google/android/gms/internal/zzatj$zza:zzbrr	J
    //   318: lconst_1
    //   319: ladd
    //   320: putfield 925	com/google/android/gms/internal/zzatj$zza:zzbrr	J
    //   323: iload 8
    //   325: ifeq +19 -> 344
    //   328: aload 10
    //   330: astore 9
    //   332: aload 12
    //   334: aload 12
    //   336: getfield 928	com/google/android/gms/internal/zzatj$zza:zzbrs	J
    //   339: lconst_1
    //   340: ladd
    //   341: putfield 928	com/google/android/gms/internal/zzatj$zza:zzbrs	J
    //   344: aload 10
    //   346: astore 9
    //   348: new 489	android/content/ContentValues
    //   351: dup
    //   352: invokespecial 491	android/content/ContentValues:<init>	()V
    //   355: astore 13
    //   357: aload 10
    //   359: astore 9
    //   361: aload 13
    //   363: ldc 72
    //   365: lload_1
    //   366: invokestatic 869	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   369: invokevirtual 872	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   372: aload 10
    //   374: astore 9
    //   376: aload 13
    //   378: ldc 76
    //   380: aload 12
    //   382: getfield 919	com/google/android/gms/internal/zzatj$zza:zzbro	J
    //   385: invokestatic 869	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   388: invokevirtual 872	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   391: aload 10
    //   393: astore 9
    //   395: aload 13
    //   397: ldc 80
    //   399: aload 12
    //   401: getfield 916	com/google/android/gms/internal/zzatj$zza:zzbrp	J
    //   404: invokestatic 869	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   407: invokevirtual 872	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   410: aload 10
    //   412: astore 9
    //   414: aload 13
    //   416: ldc 84
    //   418: aload 12
    //   420: getfield 922	com/google/android/gms/internal/zzatj$zza:zzbrq	J
    //   423: invokestatic 869	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   426: invokevirtual 872	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   429: aload 10
    //   431: astore 9
    //   433: aload 13
    //   435: ldc 108
    //   437: aload 12
    //   439: getfield 925	com/google/android/gms/internal/zzatj$zza:zzbrr	J
    //   442: invokestatic 869	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   445: invokevirtual 872	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   448: aload 10
    //   450: astore 9
    //   452: aload 13
    //   454: ldc 112
    //   456: aload 12
    //   458: getfield 928	com/google/android/gms/internal/zzatj$zza:zzbrs	J
    //   461: invokestatic 869	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   464: invokevirtual 872	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   467: aload 10
    //   469: astore 9
    //   471: aload 11
    //   473: ldc_w 908
    //   476: aload 13
    //   478: ldc_w 910
    //   481: iconst_1
    //   482: anewarray 306	java/lang/String
    //   485: dup
    //   486: iconst_0
    //   487: aload_3
    //   488: aastore
    //   489: invokevirtual 878	android/database/sqlite/SQLiteDatabase:update	(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   492: pop
    //   493: aload 10
    //   495: ifnull +10 -> 505
    //   498: aload 10
    //   500: invokeinterface 228 1 0
    //   505: aload 12
    //   507: areturn
    //   508: astore 11
    //   510: aconst_null
    //   511: astore 10
    //   513: aload 10
    //   515: astore 9
    //   517: aload_0
    //   518: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   521: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   524: ldc_w 930
    //   527: aload_3
    //   528: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   531: aload 11
    //   533: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   536: aload 10
    //   538: ifnull +10 -> 548
    //   541: aload 10
    //   543: invokeinterface 228 1 0
    //   548: aload 12
    //   550: areturn
    //   551: astore_3
    //   552: aconst_null
    //   553: astore 9
    //   555: aload 9
    //   557: ifnull +10 -> 567
    //   560: aload 9
    //   562: invokeinterface 228 1 0
    //   567: aload_3
    //   568: athrow
    //   569: astore_3
    //   570: goto -15 -> 555
    //   573: astore 11
    //   575: goto -62 -> 513
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	578	0	this	zzatj
    //   0	578	1	paramLong	long
    //   0	578	3	paramString	String
    //   0	578	4	paramBoolean1	boolean
    //   0	578	5	paramBoolean2	boolean
    //   0	578	6	paramBoolean3	boolean
    //   0	578	7	paramBoolean4	boolean
    //   0	578	8	paramBoolean5	boolean
    //   89	472	9	localCursor1	Cursor
    //   85	457	10	localCursor2	Cursor
    //   26	446	11	localSQLiteDatabase	SQLiteDatabase
    //   508	24	11	localSQLiteException1	SQLiteException
    //   573	1	11	localSQLiteException2	SQLiteException
    //   20	529	12	localzza	zza
    //   355	122	13	localContentValues	ContentValues
    // Exception table:
    //   from	to	target	type
    //   22	87	508	android/database/sqlite/SQLiteException
    //   22	87	551	finally
    //   91	101	569	finally
    //   105	122	569	finally
    //   141	154	569	finally
    //   158	171	569	finally
    //   175	188	569	finally
    //   192	205	569	finally
    //   209	222	569	finally
    //   226	239	569	finally
    //   248	260	569	finally
    //   269	281	569	finally
    //   290	302	569	finally
    //   311	323	569	finally
    //   332	344	569	finally
    //   348	357	569	finally
    //   361	372	569	finally
    //   376	391	569	finally
    //   395	410	569	finally
    //   414	429	569	finally
    //   433	448	569	finally
    //   452	467	569	finally
    //   471	493	569	finally
    //   517	536	569	finally
    //   91	101	573	android/database/sqlite/SQLiteException
    //   105	122	573	android/database/sqlite/SQLiteException
    //   141	154	573	android/database/sqlite/SQLiteException
    //   158	171	573	android/database/sqlite/SQLiteException
    //   175	188	573	android/database/sqlite/SQLiteException
    //   192	205	573	android/database/sqlite/SQLiteException
    //   209	222	573	android/database/sqlite/SQLiteException
    //   226	239	573	android/database/sqlite/SQLiteException
    //   248	260	573	android/database/sqlite/SQLiteException
    //   269	281	573	android/database/sqlite/SQLiteException
    //   290	302	573	android/database/sqlite/SQLiteException
    //   311	323	573	android/database/sqlite/SQLiteException
    //   332	344	573	android/database/sqlite/SQLiteException
    //   348	357	573	android/database/sqlite/SQLiteException
    //   361	372	573	android/database/sqlite/SQLiteException
    //   376	391	573	android/database/sqlite/SQLiteException
    //   395	410	573	android/database/sqlite/SQLiteException
    //   414	429	573	android/database/sqlite/SQLiteException
    //   433	448	573	android/database/sqlite/SQLiteException
    //   452	467	573	android/database/sqlite/SQLiteException
    //   471	493	573	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  void zza(ContentValues paramContentValues, String paramString, Object paramObject)
  {
    zzac.zzdr(paramString);
    zzac.zzw(paramObject);
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
  public void zza(zzatc paramzzatc)
  {
    zzac.zzw(paramzzatc);
    zzmR();
    zzob();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzatc.zzke());
    localContentValues.put("app_instance_id", paramzzatc.getAppInstanceId());
    localContentValues.put("gmp_app_id", paramzzatc.getGmpAppId());
    localContentValues.put("resettable_device_id_hash", paramzzatc.zzKp());
    localContentValues.put("last_bundle_index", Long.valueOf(paramzzatc.zzKy()));
    localContentValues.put("last_bundle_start_timestamp", Long.valueOf(paramzzatc.zzKr()));
    localContentValues.put("last_bundle_end_timestamp", Long.valueOf(paramzzatc.zzKs()));
    localContentValues.put("app_version", paramzzatc.zzmZ());
    localContentValues.put("app_store", paramzzatc.zzKu());
    localContentValues.put("gmp_version", Long.valueOf(paramzzatc.zzKv()));
    localContentValues.put("dev_cert_hash", Long.valueOf(paramzzatc.zzKw()));
    localContentValues.put("measurement_enabled", Boolean.valueOf(paramzzatc.zzKx()));
    localContentValues.put("day", Long.valueOf(paramzzatc.zzKC()));
    localContentValues.put("daily_public_events_count", Long.valueOf(paramzzatc.zzKD()));
    localContentValues.put("daily_events_count", Long.valueOf(paramzzatc.zzKE()));
    localContentValues.put("daily_conversions_count", Long.valueOf(paramzzatc.zzKF()));
    localContentValues.put("config_fetched_time", Long.valueOf(paramzzatc.zzKz()));
    localContentValues.put("failed_config_fetch_time", Long.valueOf(paramzzatc.zzKA()));
    localContentValues.put("app_version_int", Long.valueOf(paramzzatc.zzKt()));
    localContentValues.put("firebase_instance_id", paramzzatc.zzKq());
    localContentValues.put("daily_error_events_count", Long.valueOf(paramzzatc.zzKH()));
    localContentValues.put("daily_realtime_events_count", Long.valueOf(paramzzatc.zzKG()));
    localContentValues.put("health_monitor_sample", paramzzatc.zzKI());
    localContentValues.put("android_id", Long.valueOf(paramzzatc.zzuW()));
    try
    {
      SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
      if ((localSQLiteDatabase.update("apps", localContentValues, "app_id = ?", new String[] { paramzzatc.zzke() }) == 0L) && (localSQLiteDatabase.insertWithOnConflict("apps", null, localContentValues, 5) == -1L)) {
        zzKl().zzLZ().zzj("Failed to insert/update app (got -1). appId", zzatx.zzfE(paramzzatc.zzke()));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzKl().zzLZ().zze("Error storing app. appId", zzatx.zzfE(paramzzatc.zzke()), localSQLiteException);
    }
  }
  
  @WorkerThread
  public void zza(zzatn paramzzatn)
  {
    zzac.zzw(paramzzatn);
    zzmR();
    zzob();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzatn.mAppId);
    localContentValues.put("name", paramzzatn.mName);
    localContentValues.put("lifetime_count", Long.valueOf(paramzzatn.zzbrB));
    localContentValues.put("current_bundle_count", Long.valueOf(paramzzatn.zzbrC));
    localContentValues.put("last_fire_timestamp", Long.valueOf(paramzzatn.zzbrD));
    try
    {
      if (getWritableDatabase().insertWithOnConflict("events", null, localContentValues, 5) == -1L) {
        zzKl().zzLZ().zzj("Failed to insert/update event aggregates (got -1). appId", zzatx.zzfE(paramzzatn.mAppId));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzKl().zzLZ().zze("Error storing event aggregates. appId", zzatx.zzfE(paramzzatn.mAppId), localSQLiteException);
    }
  }
  
  /* Error */
  void zza(String paramString, int paramInt, zzauw.zzf paramzzf)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   4: aload_0
    //   5: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   8: aload_1
    //   9: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_3
    //   14: invokestatic 395	com/google/android/gms/common/internal/zzac:zzw	(Ljava/lang/Object;)Ljava/lang/Object;
    //   17: pop
    //   18: aload_3
    //   19: invokevirtual 1059	com/google/android/gms/internal/zzauw$zzf:zzafB	()I
    //   22: newarray <illegal type>
    //   24: astore 4
    //   26: aload 4
    //   28: invokestatic 481	com/google/android/gms/internal/zzbyc:zzah	([B)Lcom/google/android/gms/internal/zzbyc;
    //   31: astore 5
    //   33: aload_3
    //   34: aload 5
    //   36: invokevirtual 1060	com/google/android/gms/internal/zzauw$zzf:zza	(Lcom/google/android/gms/internal/zzbyc;)V
    //   39: aload 5
    //   41: invokevirtual 487	com/google/android/gms/internal/zzbyc:zzafo	()V
    //   44: new 489	android/content/ContentValues
    //   47: dup
    //   48: invokespecial 491	android/content/ContentValues:<init>	()V
    //   51: astore_3
    //   52: aload_3
    //   53: ldc_w 493
    //   56: aload_1
    //   57: invokevirtual 496	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   60: aload_3
    //   61: ldc_w 498
    //   64: iload_2
    //   65: invokestatic 468	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   68: invokevirtual 501	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   71: aload_3
    //   72: ldc_w 1062
    //   75: aload 4
    //   77: invokevirtual 510	android/content/ContentValues:put	(Ljava/lang/String;[B)V
    //   80: aload_0
    //   81: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   84: ldc_w 1064
    //   87: aconst_null
    //   88: aload_3
    //   89: iconst_5
    //   90: invokevirtual 516	android/database/sqlite/SQLiteDatabase:insertWithOnConflict	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    //   93: ldc2_w 517
    //   96: lcmp
    //   97: ifne +20 -> 117
    //   100: aload_0
    //   101: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   104: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   107: ldc_w 1066
    //   110: aload_1
    //   111: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   114: invokevirtual 296	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   117: return
    //   118: astore_3
    //   119: aload_0
    //   120: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   123: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   126: ldc_w 1068
    //   129: aload_1
    //   130: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   133: aload_3
    //   134: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   137: return
    //   138: astore_3
    //   139: aload_0
    //   140: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   143: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   146: ldc_w 1070
    //   149: aload_1
    //   150: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   153: aload_3
    //   154: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   157: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	158	0	this	zzatj
    //   0	158	1	paramString	String
    //   0	158	2	paramInt	int
    //   0	158	3	paramzzf	zzauw.zzf
    //   24	52	4	arrayOfByte	byte[]
    //   31	9	5	localzzbyc	zzbyc
    // Exception table:
    //   from	to	target	type
    //   18	44	118	java/io/IOException
    //   80	117	138	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  public void zza(String paramString, long paramLong1, long paramLong2, zzb paramzzb)
  {
    // Byte code:
    //   0: aload 6
    //   2: invokestatic 395	com/google/android/gms/common/internal/zzac:zzw	(Ljava/lang/Object;)Ljava/lang/Object;
    //   5: pop
    //   6: aload_0
    //   7: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   10: aload_0
    //   11: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   14: aconst_null
    //   15: astore 14
    //   17: aconst_null
    //   18: astore 13
    //   20: aload 13
    //   22: astore 8
    //   24: aload 14
    //   26: astore 9
    //   28: aload_1
    //   29: astore 10
    //   31: aload_0
    //   32: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   35: astore 15
    //   37: aload 13
    //   39: astore 8
    //   41: aload 14
    //   43: astore 9
    //   45: aload_1
    //   46: astore 10
    //   48: aload_1
    //   49: invokestatic 463	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   52: ifeq +342 -> 394
    //   55: lload 4
    //   57: ldc2_w 517
    //   60: lcmp
    //   61: ifeq +135 -> 196
    //   64: aload 13
    //   66: astore 8
    //   68: aload 14
    //   70: astore 9
    //   72: aload_1
    //   73: astore 10
    //   75: iconst_2
    //   76: anewarray 306	java/lang/String
    //   79: dup
    //   80: iconst_0
    //   81: lload 4
    //   83: invokestatic 680	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   86: aastore
    //   87: dup
    //   88: iconst_1
    //   89: lload_2
    //   90: invokestatic 680	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   93: aastore
    //   94: astore 11
    //   96: goto +1207 -> 1303
    //   99: aload 13
    //   101: astore 8
    //   103: aload 14
    //   105: astore 9
    //   107: aload_1
    //   108: astore 10
    //   110: aload 15
    //   112: new 318	java/lang/StringBuilder
    //   115: dup
    //   116: aload 12
    //   118: invokestatic 322	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   121: invokevirtual 326	java/lang/String:length	()I
    //   124: sipush 148
    //   127: iadd
    //   128: invokespecial 327	java/lang/StringBuilder:<init>	(I)V
    //   131: ldc_w 1073
    //   134: invokevirtual 333	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   137: aload 12
    //   139: invokevirtual 333	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   142: ldc_w 1075
    //   145: invokevirtual 333	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   148: invokevirtual 338	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   151: aload 11
    //   153: invokevirtual 218	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   156: astore 11
    //   158: aload 11
    //   160: astore 8
    //   162: aload 11
    //   164: astore 9
    //   166: aload_1
    //   167: astore 10
    //   169: aload 11
    //   171: invokeinterface 221 1 0
    //   176: istore 7
    //   178: iload 7
    //   180: ifne +43 -> 223
    //   183: aload 11
    //   185: ifnull +10 -> 195
    //   188: aload 11
    //   190: invokeinterface 228 1 0
    //   195: return
    //   196: aload 13
    //   198: astore 8
    //   200: aload 14
    //   202: astore 9
    //   204: aload_1
    //   205: astore 10
    //   207: iconst_1
    //   208: anewarray 306	java/lang/String
    //   211: dup
    //   212: iconst_0
    //   213: lload_2
    //   214: invokestatic 680	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   217: aastore
    //   218: astore 11
    //   220: goto +1083 -> 1303
    //   223: aload 11
    //   225: astore 8
    //   227: aload 11
    //   229: astore 9
    //   231: aload_1
    //   232: astore 10
    //   234: aload 11
    //   236: iconst_0
    //   237: invokeinterface 619 2 0
    //   242: astore_1
    //   243: aload 11
    //   245: astore 8
    //   247: aload 11
    //   249: astore 9
    //   251: aload_1
    //   252: astore 10
    //   254: aload 11
    //   256: iconst_1
    //   257: invokeinterface 619 2 0
    //   262: astore 12
    //   264: aload 11
    //   266: astore 8
    //   268: aload 11
    //   270: astore 9
    //   272: aload_1
    //   273: astore 10
    //   275: aload 11
    //   277: invokeinterface 228 1 0
    //   282: aload 12
    //   284: astore 10
    //   286: aload 11
    //   288: astore 8
    //   290: aload 8
    //   292: astore 9
    //   294: aload 15
    //   296: ldc_w 900
    //   299: iconst_1
    //   300: anewarray 306	java/lang/String
    //   303: dup
    //   304: iconst_0
    //   305: ldc_w 898
    //   308: aastore
    //   309: ldc_w 1077
    //   312: iconst_2
    //   313: anewarray 306	java/lang/String
    //   316: dup
    //   317: iconst_0
    //   318: aload_1
    //   319: aastore
    //   320: dup
    //   321: iconst_1
    //   322: aload 10
    //   324: aastore
    //   325: aconst_null
    //   326: aconst_null
    //   327: ldc_w 1079
    //   330: ldc_w 1081
    //   333: invokevirtual 1084	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   336: astore 11
    //   338: aload 11
    //   340: astore 9
    //   342: aload 11
    //   344: astore 8
    //   346: aload 11
    //   348: invokeinterface 221 1 0
    //   353: ifne +252 -> 605
    //   356: aload 11
    //   358: astore 9
    //   360: aload 11
    //   362: astore 8
    //   364: aload_0
    //   365: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   368: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   371: ldc_w 1086
    //   374: aload_1
    //   375: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   378: invokevirtual 296	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   381: aload 11
    //   383: ifnull -188 -> 195
    //   386: aload 11
    //   388: invokeinterface 228 1 0
    //   393: return
    //   394: lload 4
    //   396: ldc2_w 517
    //   399: lcmp
    //   400: ifeq +131 -> 531
    //   403: aload 13
    //   405: astore 8
    //   407: aload 14
    //   409: astore 9
    //   411: aload_1
    //   412: astore 10
    //   414: iconst_2
    //   415: anewarray 306	java/lang/String
    //   418: dup
    //   419: iconst_0
    //   420: aload_1
    //   421: aastore
    //   422: dup
    //   423: iconst_1
    //   424: lload 4
    //   426: invokestatic 680	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   429: aastore
    //   430: astore 11
    //   432: goto +896 -> 1328
    //   435: aload 13
    //   437: astore 8
    //   439: aload 14
    //   441: astore 9
    //   443: aload_1
    //   444: astore 10
    //   446: aload 15
    //   448: new 318	java/lang/StringBuilder
    //   451: dup
    //   452: aload 12
    //   454: invokestatic 322	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   457: invokevirtual 326	java/lang/String:length	()I
    //   460: bipush 84
    //   462: iadd
    //   463: invokespecial 327	java/lang/StringBuilder:<init>	(I)V
    //   466: ldc_w 1088
    //   469: invokevirtual 333	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   472: aload 12
    //   474: invokevirtual 333	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   477: ldc_w 1090
    //   480: invokevirtual 333	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   483: invokevirtual 338	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   486: aload 11
    //   488: invokevirtual 218	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   491: astore 11
    //   493: aload 11
    //   495: astore 8
    //   497: aload 11
    //   499: astore 9
    //   501: aload_1
    //   502: astore 10
    //   504: aload 11
    //   506: invokeinterface 221 1 0
    //   511: istore 7
    //   513: iload 7
    //   515: ifne +40 -> 555
    //   518: aload 11
    //   520: ifnull -325 -> 195
    //   523: aload 11
    //   525: invokeinterface 228 1 0
    //   530: return
    //   531: aload 13
    //   533: astore 8
    //   535: aload 14
    //   537: astore 9
    //   539: aload_1
    //   540: astore 10
    //   542: iconst_1
    //   543: anewarray 306	java/lang/String
    //   546: dup
    //   547: iconst_0
    //   548: aload_1
    //   549: aastore
    //   550: astore 11
    //   552: goto +776 -> 1328
    //   555: aload 11
    //   557: astore 8
    //   559: aload 11
    //   561: astore 9
    //   563: aload_1
    //   564: astore 10
    //   566: aload 11
    //   568: iconst_0
    //   569: invokeinterface 619 2 0
    //   574: astore 12
    //   576: aload 11
    //   578: astore 8
    //   580: aload 11
    //   582: astore 9
    //   584: aload_1
    //   585: astore 10
    //   587: aload 11
    //   589: invokeinterface 228 1 0
    //   594: aload 12
    //   596: astore 10
    //   598: aload 11
    //   600: astore 8
    //   602: goto -312 -> 290
    //   605: aload 11
    //   607: astore 9
    //   609: aload 11
    //   611: astore 8
    //   613: aload 11
    //   615: iconst_0
    //   616: invokeinterface 786 2 0
    //   621: invokestatic 829	com/google/android/gms/internal/zzbyb:zzag	([B)Lcom/google/android/gms/internal/zzbyb;
    //   624: astore 12
    //   626: aload 11
    //   628: astore 9
    //   630: aload 11
    //   632: astore 8
    //   634: new 885	com/google/android/gms/internal/zzauw$zze
    //   637: dup
    //   638: invokespecial 1091	com/google/android/gms/internal/zzauw$zze:<init>	()V
    //   641: astore 13
    //   643: aload 11
    //   645: astore 9
    //   647: aload 11
    //   649: astore 8
    //   651: aload 13
    //   653: aload 12
    //   655: invokevirtual 1092	com/google/android/gms/internal/zzauw$zze:zzb	(Lcom/google/android/gms/internal/zzbyb;)Lcom/google/android/gms/internal/zzbyj;
    //   658: pop
    //   659: aload 11
    //   661: astore 9
    //   663: aload 11
    //   665: astore 8
    //   667: aload 11
    //   669: invokeinterface 727 1 0
    //   674: ifeq +28 -> 702
    //   677: aload 11
    //   679: astore 9
    //   681: aload 11
    //   683: astore 8
    //   685: aload_0
    //   686: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   689: invokevirtual 266	com/google/android/gms/internal/zzatx:zzMb	()Lcom/google/android/gms/internal/zzatx$zza;
    //   692: ldc_w 1094
    //   695: aload_1
    //   696: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   699: invokevirtual 296	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   702: aload 11
    //   704: astore 9
    //   706: aload 11
    //   708: astore 8
    //   710: aload 11
    //   712: invokeinterface 228 1 0
    //   717: aload 11
    //   719: astore 9
    //   721: aload 11
    //   723: astore 8
    //   725: aload 6
    //   727: aload 13
    //   729: invokeinterface 1097 2 0
    //   734: lload 4
    //   736: ldc2_w 517
    //   739: lcmp
    //   740: ifeq +214 -> 954
    //   743: ldc_w 1099
    //   746: astore 13
    //   748: aload 11
    //   750: astore 9
    //   752: aload 11
    //   754: astore 8
    //   756: iconst_3
    //   757: anewarray 306	java/lang/String
    //   760: astore 12
    //   762: aload 12
    //   764: iconst_0
    //   765: aload_1
    //   766: aastore
    //   767: aload 12
    //   769: iconst_1
    //   770: aload 10
    //   772: aastore
    //   773: aload 11
    //   775: astore 9
    //   777: aload 11
    //   779: astore 8
    //   781: aload 12
    //   783: iconst_2
    //   784: lload 4
    //   786: invokestatic 680	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   789: aastore
    //   790: aload 13
    //   792: astore 10
    //   794: aload 11
    //   796: astore 9
    //   798: aload 11
    //   800: astore 8
    //   802: aload 15
    //   804: ldc_w 610
    //   807: iconst_4
    //   808: anewarray 306	java/lang/String
    //   811: dup
    //   812: iconst_0
    //   813: ldc_w 1079
    //   816: aastore
    //   817: dup
    //   818: iconst_1
    //   819: ldc_w 446
    //   822: aastore
    //   823: dup
    //   824: iconst_2
    //   825: ldc_w 1101
    //   828: aastore
    //   829: dup
    //   830: iconst_3
    //   831: ldc_w 507
    //   834: aastore
    //   835: aload 10
    //   837: aload 12
    //   839: aconst_null
    //   840: aconst_null
    //   841: ldc_w 1079
    //   844: aconst_null
    //   845: invokevirtual 1084	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   848: astore 11
    //   850: aload 11
    //   852: astore 8
    //   854: aload 11
    //   856: astore 9
    //   858: aload_1
    //   859: astore 10
    //   861: aload 11
    //   863: invokeinterface 221 1 0
    //   868: ifne +167 -> 1035
    //   871: aload 11
    //   873: astore 8
    //   875: aload 11
    //   877: astore 9
    //   879: aload_1
    //   880: astore 10
    //   882: aload_0
    //   883: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   886: invokevirtual 266	com/google/android/gms/internal/zzatx:zzMb	()Lcom/google/android/gms/internal/zzatx$zza;
    //   889: ldc_w 1103
    //   892: aload_1
    //   893: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   896: invokevirtual 296	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   899: aload 11
    //   901: ifnull -706 -> 195
    //   904: aload 11
    //   906: invokeinterface 228 1 0
    //   911: return
    //   912: astore 6
    //   914: aload 11
    //   916: astore 9
    //   918: aload 11
    //   920: astore 8
    //   922: aload_0
    //   923: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   926: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   929: ldc_w 1105
    //   932: aload_1
    //   933: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   936: aload 6
    //   938: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   941: aload 11
    //   943: ifnull -748 -> 195
    //   946: aload 11
    //   948: invokeinterface 228 1 0
    //   953: return
    //   954: ldc_w 1077
    //   957: astore 13
    //   959: aload 11
    //   961: astore 9
    //   963: aload 11
    //   965: astore 8
    //   967: iconst_2
    //   968: anewarray 306	java/lang/String
    //   971: astore 12
    //   973: aload 12
    //   975: iconst_0
    //   976: aload_1
    //   977: aastore
    //   978: aload 12
    //   980: iconst_1
    //   981: aload 10
    //   983: aastore
    //   984: aload 13
    //   986: astore 10
    //   988: goto -194 -> 794
    //   991: astore 6
    //   993: aload_1
    //   994: astore 10
    //   996: aload 6
    //   998: astore_1
    //   999: aload 9
    //   1001: astore 8
    //   1003: aload_0
    //   1004: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   1007: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   1010: ldc_w 1107
    //   1013: aload 10
    //   1015: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   1018: aload_1
    //   1019: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   1022: aload 9
    //   1024: ifnull -829 -> 195
    //   1027: aload 9
    //   1029: invokeinterface 228 1 0
    //   1034: return
    //   1035: aload 11
    //   1037: astore 8
    //   1039: aload 11
    //   1041: astore 9
    //   1043: aload_1
    //   1044: astore 10
    //   1046: aload 11
    //   1048: iconst_0
    //   1049: invokeinterface 225 2 0
    //   1054: lstore_2
    //   1055: aload 11
    //   1057: astore 8
    //   1059: aload 11
    //   1061: astore 9
    //   1063: aload_1
    //   1064: astore 10
    //   1066: aload 11
    //   1068: iconst_3
    //   1069: invokeinterface 786 2 0
    //   1074: invokestatic 829	com/google/android/gms/internal/zzbyb:zzag	([B)Lcom/google/android/gms/internal/zzbyb;
    //   1077: astore 12
    //   1079: aload 11
    //   1081: astore 8
    //   1083: aload 11
    //   1085: astore 9
    //   1087: aload_1
    //   1088: astore 10
    //   1090: new 1109	com/google/android/gms/internal/zzauw$zzb
    //   1093: dup
    //   1094: invokespecial 1110	com/google/android/gms/internal/zzauw$zzb:<init>	()V
    //   1097: astore 13
    //   1099: aload 11
    //   1101: astore 8
    //   1103: aload 11
    //   1105: astore 9
    //   1107: aload_1
    //   1108: astore 10
    //   1110: aload 13
    //   1112: aload 12
    //   1114: invokevirtual 1111	com/google/android/gms/internal/zzauw$zzb:zzb	(Lcom/google/android/gms/internal/zzbyb;)Lcom/google/android/gms/internal/zzbyj;
    //   1117: pop
    //   1118: aload 11
    //   1120: astore 8
    //   1122: aload 11
    //   1124: astore 9
    //   1126: aload_1
    //   1127: astore 10
    //   1129: aload 13
    //   1131: aload 11
    //   1133: iconst_1
    //   1134: invokeinterface 619 2 0
    //   1139: putfield 1113	com/google/android/gms/internal/zzauw$zzb:name	Ljava/lang/String;
    //   1142: aload 11
    //   1144: astore 8
    //   1146: aload 11
    //   1148: astore 9
    //   1150: aload_1
    //   1151: astore 10
    //   1153: aload 13
    //   1155: aload 11
    //   1157: iconst_2
    //   1158: invokeinterface 225 2 0
    //   1163: invokestatic 869	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   1166: putfield 1117	com/google/android/gms/internal/zzauw$zzb:zzbxc	Ljava/lang/Long;
    //   1169: aload 11
    //   1171: astore 8
    //   1173: aload 11
    //   1175: astore 9
    //   1177: aload_1
    //   1178: astore 10
    //   1180: aload 6
    //   1182: lload_2
    //   1183: aload 13
    //   1185: invokeinterface 1120 4 0
    //   1190: istore 7
    //   1192: iload 7
    //   1194: ifne +48 -> 1242
    //   1197: aload 11
    //   1199: ifnull -1004 -> 195
    //   1202: aload 11
    //   1204: invokeinterface 228 1 0
    //   1209: return
    //   1210: astore 12
    //   1212: aload 11
    //   1214: astore 8
    //   1216: aload 11
    //   1218: astore 9
    //   1220: aload_1
    //   1221: astore 10
    //   1223: aload_0
    //   1224: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   1227: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   1230: ldc_w 1122
    //   1233: aload_1
    //   1234: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   1237: aload 12
    //   1239: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   1242: aload 11
    //   1244: astore 8
    //   1246: aload 11
    //   1248: astore 9
    //   1250: aload_1
    //   1251: astore 10
    //   1253: aload 11
    //   1255: invokeinterface 727 1 0
    //   1260: istore 7
    //   1262: iload 7
    //   1264: ifne -229 -> 1035
    //   1267: aload 11
    //   1269: ifnull -1074 -> 195
    //   1272: aload 11
    //   1274: invokeinterface 228 1 0
    //   1279: return
    //   1280: astore_1
    //   1281: aload 8
    //   1283: ifnull +10 -> 1293
    //   1286: aload 8
    //   1288: invokeinterface 228 1 0
    //   1293: aload_1
    //   1294: athrow
    //   1295: astore_1
    //   1296: goto -15 -> 1281
    //   1299: astore_1
    //   1300: goto -301 -> 999
    //   1303: lload 4
    //   1305: ldc2_w 517
    //   1308: lcmp
    //   1309: ifeq +11 -> 1320
    //   1312: ldc_w 1124
    //   1315: astore 12
    //   1317: goto -1218 -> 99
    //   1320: ldc_w 1126
    //   1323: astore 12
    //   1325: goto -1226 -> 99
    //   1328: lload 4
    //   1330: ldc2_w 517
    //   1333: lcmp
    //   1334: ifeq +11 -> 1345
    //   1337: ldc_w 1128
    //   1340: astore 12
    //   1342: goto -907 -> 435
    //   1345: ldc_w 1126
    //   1348: astore 12
    //   1350: goto -915 -> 435
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1353	0	this	zzatj
    //   0	1353	1	paramString	String
    //   0	1353	2	paramLong1	long
    //   0	1353	4	paramLong2	long
    //   0	1353	6	paramzzb	zzb
    //   176	1087	7	bool	boolean
    //   22	1265	8	localObject1	Object
    //   26	1223	9	localObject2	Object
    //   29	1223	10	localObject3	Object
    //   94	1179	11	localObject4	Object
    //   116	997	12	localObject5	Object
    //   1210	28	12	localIOException	IOException
    //   1315	34	12	str	String
    //   18	1166	13	localObject6	Object
    //   15	521	14	localObject7	Object
    //   35	768	15	localSQLiteDatabase	SQLiteDatabase
    // Exception table:
    //   from	to	target	type
    //   651	659	912	java/io/IOException
    //   294	338	991	android/database/sqlite/SQLiteException
    //   346	356	991	android/database/sqlite/SQLiteException
    //   364	381	991	android/database/sqlite/SQLiteException
    //   613	626	991	android/database/sqlite/SQLiteException
    //   634	643	991	android/database/sqlite/SQLiteException
    //   651	659	991	android/database/sqlite/SQLiteException
    //   667	677	991	android/database/sqlite/SQLiteException
    //   685	702	991	android/database/sqlite/SQLiteException
    //   710	717	991	android/database/sqlite/SQLiteException
    //   725	734	991	android/database/sqlite/SQLiteException
    //   756	762	991	android/database/sqlite/SQLiteException
    //   781	790	991	android/database/sqlite/SQLiteException
    //   802	850	991	android/database/sqlite/SQLiteException
    //   922	941	991	android/database/sqlite/SQLiteException
    //   967	973	991	android/database/sqlite/SQLiteException
    //   1110	1118	1210	java/io/IOException
    //   31	37	1280	finally
    //   48	55	1280	finally
    //   75	96	1280	finally
    //   110	158	1280	finally
    //   169	178	1280	finally
    //   207	220	1280	finally
    //   234	243	1280	finally
    //   254	264	1280	finally
    //   275	282	1280	finally
    //   414	432	1280	finally
    //   446	493	1280	finally
    //   504	513	1280	finally
    //   542	552	1280	finally
    //   566	576	1280	finally
    //   587	594	1280	finally
    //   861	871	1280	finally
    //   882	899	1280	finally
    //   1003	1022	1280	finally
    //   1046	1055	1280	finally
    //   1066	1079	1280	finally
    //   1090	1099	1280	finally
    //   1110	1118	1280	finally
    //   1129	1142	1280	finally
    //   1153	1169	1280	finally
    //   1180	1192	1280	finally
    //   1223	1242	1280	finally
    //   1253	1262	1280	finally
    //   294	338	1295	finally
    //   346	356	1295	finally
    //   364	381	1295	finally
    //   613	626	1295	finally
    //   634	643	1295	finally
    //   651	659	1295	finally
    //   667	677	1295	finally
    //   685	702	1295	finally
    //   710	717	1295	finally
    //   725	734	1295	finally
    //   756	762	1295	finally
    //   781	790	1295	finally
    //   802	850	1295	finally
    //   922	941	1295	finally
    //   967	973	1295	finally
    //   31	37	1299	android/database/sqlite/SQLiteException
    //   48	55	1299	android/database/sqlite/SQLiteException
    //   75	96	1299	android/database/sqlite/SQLiteException
    //   110	158	1299	android/database/sqlite/SQLiteException
    //   169	178	1299	android/database/sqlite/SQLiteException
    //   207	220	1299	android/database/sqlite/SQLiteException
    //   234	243	1299	android/database/sqlite/SQLiteException
    //   254	264	1299	android/database/sqlite/SQLiteException
    //   275	282	1299	android/database/sqlite/SQLiteException
    //   414	432	1299	android/database/sqlite/SQLiteException
    //   446	493	1299	android/database/sqlite/SQLiteException
    //   504	513	1299	android/database/sqlite/SQLiteException
    //   542	552	1299	android/database/sqlite/SQLiteException
    //   566	576	1299	android/database/sqlite/SQLiteException
    //   587	594	1299	android/database/sqlite/SQLiteException
    //   861	871	1299	android/database/sqlite/SQLiteException
    //   882	899	1299	android/database/sqlite/SQLiteException
    //   1046	1055	1299	android/database/sqlite/SQLiteException
    //   1066	1079	1299	android/database/sqlite/SQLiteException
    //   1090	1099	1299	android/database/sqlite/SQLiteException
    //   1110	1118	1299	android/database/sqlite/SQLiteException
    //   1129	1142	1299	android/database/sqlite/SQLiteException
    //   1153	1169	1299	android/database/sqlite/SQLiteException
    //   1180	1192	1299	android/database/sqlite/SQLiteException
    //   1223	1242	1299	android/database/sqlite/SQLiteException
    //   1253	1262	1299	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  public boolean zza(zzatg paramzzatg)
  {
    zzac.zzw(paramzzatg);
    zzmR();
    zzob();
    if (zzS(paramzzatg.packageName, paramzzatg.zzbqX.name) == null)
    {
      long l = zzb("SELECT COUNT(1) FROM conditional_properties WHERE app_id=?", new String[] { paramzzatg.packageName });
      zzKn().zzLa();
      if (l >= 1000L) {
        return false;
      }
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzatg.packageName);
    localContentValues.put("origin", paramzzatg.zzbqW);
    localContentValues.put("name", paramzzatg.zzbqX.name);
    zza(localContentValues, "value", paramzzatg.zzbqX.getValue());
    localContentValues.put("active", Boolean.valueOf(paramzzatg.zzbqZ));
    localContentValues.put("trigger_event_name", paramzzatg.zzbra);
    localContentValues.put("trigger_timeout", Long.valueOf(paramzzatg.zzbrc));
    localContentValues.put("timed_out_event", zzKh().zza(paramzzatg.zzbrb));
    localContentValues.put("creation_timestamp", Long.valueOf(paramzzatg.zzbqY));
    localContentValues.put("triggered_event", zzKh().zza(paramzzatg.zzbrd));
    localContentValues.put("triggered_timestamp", Long.valueOf(paramzzatg.zzbqX.zzbwf));
    localContentValues.put("time_to_live", Long.valueOf(paramzzatg.zzbre));
    localContentValues.put("expired_event", zzKh().zza(paramzzatg.zzbrf));
    try
    {
      if (getWritableDatabase().insertWithOnConflict("conditional_properties", null, localContentValues, 5) == -1L) {
        zzKl().zzLZ().zzj("Failed to insert/update conditional user property (got -1)", zzatx.zzfE(paramzzatg.packageName));
      }
      return true;
    }
    catch (SQLiteException localSQLiteException)
    {
      for (;;)
      {
        zzKl().zzLZ().zze("Error storing conditional user property", zzatx.zzfE(paramzzatg.packageName), localSQLiteException);
      }
    }
  }
  
  public boolean zza(zzatm paramzzatm, long paramLong, boolean paramBoolean)
  {
    zzmR();
    zzob();
    zzac.zzw(paramzzatm);
    zzac.zzdr(paramzzatm.mAppId);
    Object localObject1 = new zzauw.zzb();
    ((zzauw.zzb)localObject1).zzbxd = Long.valueOf(paramzzatm.zzbrz);
    ((zzauw.zzb)localObject1).zzbxb = new zzauw.zzc[paramzzatm.zzbrA.size()];
    Object localObject2 = paramzzatm.zzbrA.iterator();
    int i = 0;
    Object localObject3;
    while (((Iterator)localObject2).hasNext())
    {
      Object localObject4 = (String)((Iterator)localObject2).next();
      localObject3 = new zzauw.zzc();
      ((zzauw.zzb)localObject1).zzbxb[i] = localObject3;
      ((zzauw.zzc)localObject3).name = ((String)localObject4);
      localObject4 = paramzzatm.zzbrA.get((String)localObject4);
      zzKh().zza((zzauw.zzc)localObject3, localObject4);
      i += 1;
    }
    for (;;)
    {
      try
      {
        localObject2 = new byte[((zzauw.zzb)localObject1).zzafB()];
        localObject3 = zzbyc.zzah((byte[])localObject2);
        ((zzauw.zzb)localObject1).zza((zzbyc)localObject3);
        ((zzbyc)localObject3).zzafo();
        zzKl().zzMf().zze("Saving event, name, data size", paramzzatm.mName, Integer.valueOf(localObject2.length));
        localObject1 = new ContentValues();
        ((ContentValues)localObject1).put("app_id", paramzzatm.mAppId);
        ((ContentValues)localObject1).put("name", paramzzatm.mName);
        ((ContentValues)localObject1).put("timestamp", Long.valueOf(paramzzatm.zzaxb));
        ((ContentValues)localObject1).put("metadata_fingerprint", Long.valueOf(paramLong));
        ((ContentValues)localObject1).put("data", (byte[])localObject2);
        if (paramBoolean)
        {
          i = 1;
          ((ContentValues)localObject1).put("realtime", Integer.valueOf(i));
        }
        i = 0;
      }
      catch (IOException localIOException)
      {
        try
        {
          if (getWritableDatabase().insert("raw_events", null, (ContentValues)localObject1) != -1L) {
            break;
          }
          zzKl().zzLZ().zzj("Failed to insert raw event (got -1). appId", zzatx.zzfE(paramzzatm.mAppId));
          return false;
        }
        catch (SQLiteException localSQLiteException)
        {
          zzKl().zzLZ().zze("Error storing raw event. appId", zzatx.zzfE(paramzzatm.mAppId), localSQLiteException);
          return false;
        }
        localIOException = localIOException;
        zzKl().zzLZ().zze("Data loss. Failed to serialize event params/data. appId", zzatx.zzfE(paramzzatm.mAppId), localIOException);
        return false;
      }
    }
    return true;
  }
  
  @WorkerThread
  public boolean zza(zzaus paramzzaus)
  {
    zzac.zzw(paramzzaus);
    zzmR();
    zzob();
    if (zzS(paramzzaus.mAppId, paramzzaus.mName) == null)
    {
      long l;
      if (zzaut.zzfT(paramzzaus.mName))
      {
        l = zzb("select count(1) from user_attributes where app_id=? and name not like '!_%' escape '!'", new String[] { paramzzaus.mAppId });
        zzKn().zzKX();
        if (l < 25L) {}
      }
      else
      {
        do
        {
          return false;
          l = zzb("select count(1) from user_attributes where app_id=? and origin=? AND name like '!_%' escape '!'", new String[] { paramzzaus.mAppId, paramzzaus.mOrigin });
          zzKn().zzKZ();
        } while (l >= 25L);
      }
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzaus.mAppId);
    localContentValues.put("origin", paramzzaus.mOrigin);
    localContentValues.put("name", paramzzaus.mName);
    localContentValues.put("triggered_timestamp", Long.valueOf(paramzzaus.zzbwj));
    zza(localContentValues, "value", paramzzaus.mValue);
    try
    {
      if (getWritableDatabase().insertWithOnConflict("user_attributes", null, localContentValues, 5) == -1L) {
        zzKl().zzLZ().zzj("Failed to insert/update user property (got -1). appId", zzatx.zzfE(paramzzaus.mAppId));
      }
      return true;
    }
    catch (SQLiteException localSQLiteException)
    {
      for (;;)
      {
        zzKl().zzLZ().zze("Error storing user property. appId", zzatx.zzfE(paramzzaus.mAppId), localSQLiteException);
      }
    }
  }
  
  @WorkerThread
  public boolean zza(zzauw.zze paramzze, boolean paramBoolean)
  {
    zzmR();
    zzob();
    zzac.zzw(paramzze);
    zzac.zzdr(paramzze.zzaS);
    zzac.zzw(paramzze.zzbxn);
    zzLG();
    long l = zznR().currentTimeMillis();
    if ((paramzze.zzbxn.longValue() < l - zzKn().zzLk()) || (paramzze.zzbxn.longValue() > zzKn().zzLk() + l)) {
      zzKl().zzMb().zzd("Storing bundle outside of the max uploading time span. appId, now, timestamp", zzatx.zzfE(paramzze.zzaS), Long.valueOf(l), paramzze.zzbxn);
    }
    for (;;)
    {
      try
      {
        byte[] arrayOfByte = new byte[paramzze.zzafB()];
        Object localObject = zzbyc.zzah(arrayOfByte);
        paramzze.zza((zzbyc)localObject);
        ((zzbyc)localObject).zzafo();
        arrayOfByte = zzKh().zzk(arrayOfByte);
        zzKl().zzMf().zzj("Saving bundle, size", Integer.valueOf(arrayOfByte.length));
        localObject = new ContentValues();
        ((ContentValues)localObject).put("app_id", paramzze.zzaS);
        ((ContentValues)localObject).put("bundle_end_timestamp", paramzze.zzbxn);
        ((ContentValues)localObject).put("data", arrayOfByte);
        if (paramBoolean)
        {
          i = 1;
          ((ContentValues)localObject).put("has_realtime", Integer.valueOf(i));
        }
        int i = 0;
      }
      catch (IOException localIOException)
      {
        try
        {
          if (getWritableDatabase().insert("queue", null, (ContentValues)localObject) != -1L) {
            break;
          }
          zzKl().zzLZ().zzj("Failed to insert bundle (got -1). appId", zzatx.zzfE(paramzze.zzaS));
          return false;
        }
        catch (SQLiteException localSQLiteException)
        {
          zzKl().zzLZ().zze("Error storing bundle. appId", zzatx.zzfE(paramzze.zzaS), localSQLiteException);
          return false;
        }
        localIOException = localIOException;
        zzKl().zzLZ().zze("Data loss. Failed to serialize bundle. appId", zzatx.zzfE(paramzze.zzaS), localIOException);
        return false;
      }
    }
    return true;
  }
  
  @WorkerThread
  public void zzan(long paramLong)
  {
    zzmR();
    zzob();
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    try
    {
      if (localSQLiteDatabase.delete("queue", "rowid=?", new String[] { String.valueOf(paramLong) }) != 1) {
        throw new SQLiteException("Deleted fewer rows from queue than expected");
      }
    }
    catch (SQLiteException localSQLiteException)
    {
      zzKl().zzLZ().zzj("Failed to delete a bundle in a queue table", localSQLiteException);
      throw localSQLiteException;
    }
  }
  
  /* Error */
  public String zzao(long paramLong)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aload_0
    //   4: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   7: aload_0
    //   8: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   11: aload_0
    //   12: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   15: ldc_w 1302
    //   18: iconst_1
    //   19: anewarray 306	java/lang/String
    //   22: dup
    //   23: iconst_0
    //   24: lload_1
    //   25: invokestatic 680	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   28: aastore
    //   29: invokevirtual 218	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   32: astore_3
    //   33: aload_3
    //   34: astore 4
    //   36: aload_3
    //   37: invokeinterface 221 1 0
    //   42: ifne +40 -> 82
    //   45: aload_3
    //   46: astore 4
    //   48: aload_0
    //   49: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   52: invokevirtual 686	com/google/android/gms/internal/zzatx:zzMf	()Lcom/google/android/gms/internal/zzatx$zza;
    //   55: ldc_w 1304
    //   58: invokevirtual 271	com/google/android/gms/internal/zzatx$zza:log	(Ljava/lang/String;)V
    //   61: aload 6
    //   63: astore 4
    //   65: aload_3
    //   66: ifnull +13 -> 79
    //   69: aload_3
    //   70: invokeinterface 228 1 0
    //   75: aload 6
    //   77: astore 4
    //   79: aload 4
    //   81: areturn
    //   82: aload_3
    //   83: astore 4
    //   85: aload_3
    //   86: iconst_0
    //   87: invokeinterface 619 2 0
    //   92: astore 5
    //   94: aload 5
    //   96: astore 4
    //   98: aload_3
    //   99: ifnull -20 -> 79
    //   102: aload_3
    //   103: invokeinterface 228 1 0
    //   108: aload 5
    //   110: areturn
    //   111: astore 5
    //   113: aconst_null
    //   114: astore_3
    //   115: aload_3
    //   116: astore 4
    //   118: aload_0
    //   119: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   122: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   125: ldc_w 1306
    //   128: aload 5
    //   130: invokevirtual 296	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   133: aload 6
    //   135: astore 4
    //   137: aload_3
    //   138: ifnull -59 -> 79
    //   141: aload_3
    //   142: invokeinterface 228 1 0
    //   147: aconst_null
    //   148: areturn
    //   149: astore_3
    //   150: aconst_null
    //   151: astore 4
    //   153: aload 4
    //   155: ifnull +10 -> 165
    //   158: aload 4
    //   160: invokeinterface 228 1 0
    //   165: aload_3
    //   166: athrow
    //   167: astore_3
    //   168: goto -15 -> 153
    //   171: astore 5
    //   173: goto -58 -> 115
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	176	0	this	zzatj
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
  
  @WorkerThread
  Object zzb(Cursor paramCursor, int paramInt)
  {
    int i = zza(paramCursor, paramInt);
    switch (i)
    {
    default: 
      zzKl().zzLZ().zzj("Loaded invalid unknown value type, ignoring it", Integer.valueOf(i));
      return null;
    case 0: 
      zzKl().zzLZ().log("Loaded invalid null value from database");
      return null;
    case 1: 
      return Long.valueOf(paramCursor.getLong(paramInt));
    case 2: 
      return Double.valueOf(paramCursor.getDouble(paramInt));
    case 3: 
      return paramCursor.getString(paramInt);
    }
    zzKl().zzLZ().log("Loaded invalid blob type value, ignoring it");
    return null;
  }
  
  @WorkerThread
  void zzb(String paramString, zzauu.zza[] paramArrayOfzza)
  {
    int j = 0;
    zzob();
    zzmR();
    zzac.zzdr(paramString);
    zzac.zzw(paramArrayOfzza);
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    try
    {
      zzfx(paramString);
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
        localArrayList.add(paramArrayOfzza[i].zzbwn);
        i += 1;
      }
      zzd(paramString, localArrayList);
      localSQLiteDatabase.setTransactionSuccessful();
      return;
    }
    finally
    {
      localSQLiteDatabase.endTransaction();
    }
  }
  
  @WorkerThread
  public List<zzatg> zzc(String paramString1, String paramString2, long paramLong)
  {
    zzac.zzdr(paramString1);
    zzac.zzdr(paramString2);
    zzmR();
    zzob();
    if (paramLong < 0L)
    {
      zzKl().zzMb().zzd("Invalid time querying triggered conditional properties", zzatx.zzfE(paramString1), paramString2, Long.valueOf(paramLong));
      return Collections.emptyList();
    }
    return zzc("active=0 and app_id=? and trigger_event_name=? and abs(? - creation_timestamp) <= trigger_timeout", new String[] { paramString1, paramString2, String.valueOf(paramLong) });
  }
  
  /* Error */
  public List<zzatg> zzc(String paramString, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   4: aload_0
    //   5: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   8: new 837	java/util/ArrayList
    //   11: dup
    //   12: invokespecial 838	java/util/ArrayList:<init>	()V
    //   15: astore 12
    //   17: aload_0
    //   18: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   21: astore 13
    //   23: aload_0
    //   24: invokevirtual 658	com/google/android/gms/internal/zzatj:zzKn	()Lcom/google/android/gms/internal/zzati;
    //   27: invokevirtual 1144	com/google/android/gms/internal/zzati:zzLa	()I
    //   30: pop
    //   31: aload 13
    //   33: ldc_w 759
    //   36: bipush 13
    //   38: anewarray 306	java/lang/String
    //   41: dup
    //   42: iconst_0
    //   43: ldc_w 493
    //   46: aastore
    //   47: dup
    //   48: iconst_1
    //   49: ldc 36
    //   51: aastore
    //   52: dup
    //   53: iconst_2
    //   54: ldc_w 446
    //   57: aastore
    //   58: dup
    //   59: iconst_3
    //   60: ldc_w 743
    //   63: aastore
    //   64: dup
    //   65: iconst_4
    //   66: ldc_w 761
    //   69: aastore
    //   70: dup
    //   71: iconst_5
    //   72: ldc_w 763
    //   75: aastore
    //   76: dup
    //   77: bipush 6
    //   79: ldc_w 765
    //   82: aastore
    //   83: dup
    //   84: bipush 7
    //   86: ldc_w 767
    //   89: aastore
    //   90: dup
    //   91: bipush 8
    //   93: ldc_w 769
    //   96: aastore
    //   97: dup
    //   98: bipush 9
    //   100: ldc_w 771
    //   103: aastore
    //   104: dup
    //   105: bipush 10
    //   107: ldc_w 742
    //   110: aastore
    //   111: dup
    //   112: bipush 11
    //   114: ldc_w 773
    //   117: aastore
    //   118: dup
    //   119: bipush 12
    //   121: ldc_w 775
    //   124: aastore
    //   125: aload_1
    //   126: aload_2
    //   127: aconst_null
    //   128: aconst_null
    //   129: ldc_w 1079
    //   132: sipush 1001
    //   135: invokestatic 579	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   138: invokevirtual 1084	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   141: astore_1
    //   142: aload_1
    //   143: invokeinterface 221 1 0
    //   148: istore_3
    //   149: iload_3
    //   150: ifne +18 -> 168
    //   153: aload_1
    //   154: ifnull +9 -> 163
    //   157: aload_1
    //   158: invokeinterface 228 1 0
    //   163: aload 12
    //   165: astore_2
    //   166: aload_2
    //   167: areturn
    //   168: aload 12
    //   170: invokeinterface 593 1 0
    //   175: aload_0
    //   176: invokevirtual 658	com/google/android/gms/internal/zzatj:zzKn	()Lcom/google/android/gms/internal/zzati;
    //   179: invokevirtual 1144	com/google/android/gms/internal/zzati:zzLa	()I
    //   182: if_icmplt +39 -> 221
    //   185: aload_0
    //   186: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   189: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   192: ldc_w 1345
    //   195: aload_0
    //   196: invokevirtual 658	com/google/android/gms/internal/zzatj:zzKn	()Lcom/google/android/gms/internal/zzati;
    //   199: invokevirtual 1144	com/google/android/gms/internal/zzati:zzLa	()I
    //   202: invokestatic 468	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   205: invokevirtual 296	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   208: aload_1
    //   209: ifnull +9 -> 218
    //   212: aload_1
    //   213: invokeinterface 228 1 0
    //   218: aload 12
    //   220: areturn
    //   221: aload_1
    //   222: iconst_0
    //   223: invokeinterface 619 2 0
    //   228: astore_2
    //   229: aload_1
    //   230: iconst_1
    //   231: invokeinterface 619 2 0
    //   236: astore 13
    //   238: aload_1
    //   239: iconst_2
    //   240: invokeinterface 619 2 0
    //   245: astore 14
    //   247: aload_0
    //   248: aload_1
    //   249: iconst_3
    //   250: invokevirtual 746	com/google/android/gms/internal/zzatj:zzb	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   253: astore 15
    //   255: aload_1
    //   256: iconst_4
    //   257: invokeinterface 778 2 0
    //   262: ifeq +185 -> 447
    //   265: iconst_1
    //   266: istore_3
    //   267: aload_1
    //   268: iconst_5
    //   269: invokeinterface 619 2 0
    //   274: astore 16
    //   276: aload_1
    //   277: bipush 6
    //   279: invokeinterface 225 2 0
    //   284: lstore 4
    //   286: aload_0
    //   287: invokevirtual 782	com/google/android/gms/internal/zzatj:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   290: aload_1
    //   291: bipush 7
    //   293: invokeinterface 786 2 0
    //   298: getstatic 792	com/google/android/gms/internal/zzatq:CREATOR	Landroid/os/Parcelable$Creator;
    //   301: invokevirtual 797	com/google/android/gms/internal/zzaut:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   304: checkcast 788	com/google/android/gms/internal/zzatq
    //   307: astore 17
    //   309: aload_1
    //   310: bipush 8
    //   312: invokeinterface 225 2 0
    //   317: lstore 6
    //   319: aload_0
    //   320: invokevirtual 782	com/google/android/gms/internal/zzatj:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   323: aload_1
    //   324: bipush 9
    //   326: invokeinterface 786 2 0
    //   331: getstatic 792	com/google/android/gms/internal/zzatq:CREATOR	Landroid/os/Parcelable$Creator;
    //   334: invokevirtual 797	com/google/android/gms/internal/zzaut:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   337: checkcast 788	com/google/android/gms/internal/zzatq
    //   340: astore 18
    //   342: aload_1
    //   343: bipush 10
    //   345: invokeinterface 225 2 0
    //   350: lstore 8
    //   352: aload_1
    //   353: bipush 11
    //   355: invokeinterface 225 2 0
    //   360: lstore 10
    //   362: aload_0
    //   363: invokevirtual 782	com/google/android/gms/internal/zzatj:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   366: aload_1
    //   367: bipush 12
    //   369: invokeinterface 786 2 0
    //   374: getstatic 792	com/google/android/gms/internal/zzatq:CREATOR	Landroid/os/Parcelable$Creator;
    //   377: invokevirtual 797	com/google/android/gms/internal/zzaut:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   380: checkcast 788	com/google/android/gms/internal/zzatq
    //   383: astore 19
    //   385: aload 12
    //   387: new 799	com/google/android/gms/internal/zzatg
    //   390: dup
    //   391: aload_2
    //   392: aload 13
    //   394: new 801	com/google/android/gms/internal/zzauq
    //   397: dup
    //   398: aload 14
    //   400: lload 8
    //   402: aload 15
    //   404: aload 13
    //   406: invokespecial 804	com/google/android/gms/internal/zzauq:<init>	(Ljava/lang/String;JLjava/lang/Object;Ljava/lang/String;)V
    //   409: lload 6
    //   411: iload_3
    //   412: aload 16
    //   414: aload 17
    //   416: lload 4
    //   418: aload 18
    //   420: lload 10
    //   422: aload 19
    //   424: invokespecial 807	com/google/android/gms/internal/zzatg:<init>	(Ljava/lang/String;Ljava/lang/String;Lcom/google/android/gms/internal/zzauq;JZLjava/lang/String;Lcom/google/android/gms/internal/zzatq;JLcom/google/android/gms/internal/zzatq;JLcom/google/android/gms/internal/zzatq;)V
    //   427: invokeinterface 841 2 0
    //   432: pop
    //   433: aload_1
    //   434: invokeinterface 727 1 0
    //   439: istore_3
    //   440: iload_3
    //   441: ifne -273 -> 168
    //   444: goto -236 -> 208
    //   447: iconst_0
    //   448: istore_3
    //   449: goto -182 -> 267
    //   452: astore_2
    //   453: aconst_null
    //   454: astore_1
    //   455: aload_0
    //   456: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   459: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   462: ldc_w 1347
    //   465: aload_2
    //   466: invokevirtual 296	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   469: invokestatic 1337	java/util/Collections:emptyList	()Ljava/util/List;
    //   472: astore 12
    //   474: aload 12
    //   476: astore_2
    //   477: aload_1
    //   478: ifnull -312 -> 166
    //   481: aload_1
    //   482: invokeinterface 228 1 0
    //   487: aload 12
    //   489: areturn
    //   490: astore_2
    //   491: aconst_null
    //   492: astore_1
    //   493: aload_1
    //   494: ifnull +9 -> 503
    //   497: aload_1
    //   498: invokeinterface 228 1 0
    //   503: aload_2
    //   504: athrow
    //   505: astore_2
    //   506: goto -13 -> 493
    //   509: astore_2
    //   510: goto -17 -> 493
    //   513: astore_2
    //   514: goto -59 -> 455
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	517	0	this	zzatj
    //   0	517	1	paramString	String
    //   0	517	2	paramArrayOfString	String[]
    //   148	301	3	bool	boolean
    //   284	133	4	l1	long
    //   317	93	6	l2	long
    //   350	51	8	l3	long
    //   360	61	10	l4	long
    //   15	473	12	localObject1	Object
    //   21	384	13	localObject2	Object
    //   245	154	14	str1	String
    //   253	150	15	localObject3	Object
    //   274	139	16	str2	String
    //   307	108	17	localzzatq1	zzatq
    //   340	79	18	localzzatq2	zzatq
    //   383	40	19	localzzatq3	zzatq
    // Exception table:
    //   from	to	target	type
    //   17	142	452	android/database/sqlite/SQLiteException
    //   17	142	490	finally
    //   142	149	505	finally
    //   168	208	505	finally
    //   221	265	505	finally
    //   267	440	505	finally
    //   455	474	509	finally
    //   142	149	513	android/database/sqlite/SQLiteException
    //   168	208	513	android/database/sqlite/SQLiteException
    //   221	265	513	android/database/sqlite/SQLiteException
    //   267	440	513	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  public void zzd(String paramString, byte[] paramArrayOfByte)
  {
    zzac.zzdr(paramString);
    zzmR();
    zzob();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("remote_config", paramArrayOfByte);
    try
    {
      if (getWritableDatabase().update("apps", localContentValues, "app_id = ?", new String[] { paramString }) == 0L) {
        zzKl().zzLZ().zzj("Failed to update remote config (got 0). appId", zzatx.zzfE(paramString));
      }
      return;
    }
    catch (SQLiteException paramArrayOfByte)
    {
      zzKl().zzLZ().zze("Error storing remote config. appId", zzatx.zzfE(paramString), paramArrayOfByte);
    }
  }
  
  boolean zzd(String paramString, List<Integer> paramList)
  {
    zzac.zzdr(paramString);
    zzob();
    zzmR();
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    int j;
    label151:
    do
    {
      try
      {
        long l = zzb("select count(1) from audience_filter_values where app_id=?", new String[] { paramString });
        j = zzKn().zzfo(paramString);
        if (l <= j) {
          return false;
        }
      }
      catch (SQLiteException paramList)
      {
        zzKl().zzLZ().zze("Database error querying filters. appId", zzatx.zzfE(paramString), paramList);
        return false;
      }
      ArrayList localArrayList = new ArrayList();
      if (paramList != null)
      {
        int i = 0;
        for (;;)
        {
          if (i >= paramList.size()) {
            break label151;
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
  public long zzfA(String paramString)
  {
    zzac.zzdr(paramString);
    zzmR();
    zzob();
    return zzX(paramString, "first_open_count");
  }
  
  public void zzfB(String paramString)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    try
    {
      localSQLiteDatabase.execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[] { paramString, paramString });
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzKl().zzLZ().zze("Failed to remove unused event metadata. appId", zzatx.zzfE(paramString), localSQLiteException);
    }
  }
  
  public long zzfC(String paramString)
  {
    zzac.zzdr(paramString);
    return zza("select count(1) from events where app_id=? and name not like '!_%' escape '!'", new String[] { paramString }, 0L);
  }
  
  /* Error */
  @WorkerThread
  public List<zzaus> zzft(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aload_1
    //   4: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_0
    //   9: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   12: aload_0
    //   13: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   16: new 837	java/util/ArrayList
    //   19: dup
    //   20: invokespecial 838	java/util/ArrayList:<init>	()V
    //   23: astore 9
    //   25: aload_0
    //   26: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   29: astore 6
    //   31: aload_0
    //   32: invokevirtual 658	com/google/android/gms/internal/zzatj:zzKn	()Lcom/google/android/gms/internal/zzati;
    //   35: invokevirtual 1387	com/google/android/gms/internal/zzati:zzKY	()I
    //   38: istore_2
    //   39: aload 6
    //   41: ldc_w 734
    //   44: iconst_4
    //   45: anewarray 306	java/lang/String
    //   48: dup
    //   49: iconst_0
    //   50: ldc_w 446
    //   53: aastore
    //   54: dup
    //   55: iconst_1
    //   56: ldc 36
    //   58: aastore
    //   59: dup
    //   60: iconst_2
    //   61: ldc_w 742
    //   64: aastore
    //   65: dup
    //   66: iconst_3
    //   67: ldc_w 743
    //   70: aastore
    //   71: ldc_w 910
    //   74: iconst_1
    //   75: anewarray 306	java/lang/String
    //   78: dup
    //   79: iconst_0
    //   80: aload_1
    //   81: aastore
    //   82: aconst_null
    //   83: aconst_null
    //   84: ldc_w 1079
    //   87: iload_2
    //   88: invokestatic 579	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   91: invokevirtual 1084	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   94: astore 6
    //   96: aload 6
    //   98: invokeinterface 221 1 0
    //   103: istore_3
    //   104: iload_3
    //   105: ifne +18 -> 123
    //   108: aload 6
    //   110: ifnull +10 -> 120
    //   113: aload 6
    //   115: invokeinterface 228 1 0
    //   120: aload 9
    //   122: areturn
    //   123: aload 6
    //   125: iconst_0
    //   126: invokeinterface 619 2 0
    //   131: astore 10
    //   133: aload 6
    //   135: iconst_1
    //   136: invokeinterface 619 2 0
    //   141: astore 8
    //   143: aload 8
    //   145: astore 7
    //   147: aload 8
    //   149: ifnonnull +8 -> 157
    //   152: ldc_w 1126
    //   155: astore 7
    //   157: aload 6
    //   159: iconst_2
    //   160: invokeinterface 225 2 0
    //   165: lstore 4
    //   167: aload_0
    //   168: aload 6
    //   170: iconst_3
    //   171: invokevirtual 746	com/google/android/gms/internal/zzatj:zzb	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   174: astore 8
    //   176: aload 8
    //   178: ifnonnull +47 -> 225
    //   181: aload_0
    //   182: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   185: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   188: ldc_w 1389
    //   191: aload_1
    //   192: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   195: invokevirtual 296	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   198: aload 6
    //   200: invokeinterface 727 1 0
    //   205: istore_3
    //   206: iload_3
    //   207: ifne -84 -> 123
    //   210: aload 6
    //   212: ifnull +10 -> 222
    //   215: aload 6
    //   217: invokeinterface 228 1 0
    //   222: aload 9
    //   224: areturn
    //   225: aload 9
    //   227: new 748	com/google/android/gms/internal/zzaus
    //   230: dup
    //   231: aload_1
    //   232: aload 7
    //   234: aload 10
    //   236: lload 4
    //   238: aload 8
    //   240: invokespecial 751	com/google/android/gms/internal/zzaus:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   243: invokeinterface 841 2 0
    //   248: pop
    //   249: goto -51 -> 198
    //   252: astore 7
    //   254: aload_0
    //   255: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   258: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   261: ldc_w 1391
    //   264: aload_1
    //   265: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   268: aload 7
    //   270: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   273: aload 6
    //   275: ifnull +10 -> 285
    //   278: aload 6
    //   280: invokeinterface 228 1 0
    //   285: aconst_null
    //   286: areturn
    //   287: astore_1
    //   288: aload 7
    //   290: astore 6
    //   292: aload 6
    //   294: ifnull +10 -> 304
    //   297: aload 6
    //   299: invokeinterface 228 1 0
    //   304: aload_1
    //   305: athrow
    //   306: astore_1
    //   307: goto -15 -> 292
    //   310: astore_1
    //   311: goto -19 -> 292
    //   314: astore 7
    //   316: aconst_null
    //   317: astore 6
    //   319: goto -65 -> 254
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	322	0	this	zzatj
    //   0	322	1	paramString	String
    //   38	50	2	i	int
    //   103	104	3	bool	boolean
    //   165	72	4	l	long
    //   29	289	6	localObject1	Object
    //   1	232	7	localObject2	Object
    //   252	37	7	localSQLiteException1	SQLiteException
    //   314	1	7	localSQLiteException2	SQLiteException
    //   141	98	8	localObject3	Object
    //   23	203	9	localArrayList	ArrayList
    //   131	104	10	str	String
    // Exception table:
    //   from	to	target	type
    //   96	104	252	android/database/sqlite/SQLiteException
    //   123	143	252	android/database/sqlite/SQLiteException
    //   157	176	252	android/database/sqlite/SQLiteException
    //   181	198	252	android/database/sqlite/SQLiteException
    //   198	206	252	android/database/sqlite/SQLiteException
    //   225	249	252	android/database/sqlite/SQLiteException
    //   25	96	287	finally
    //   96	104	306	finally
    //   123	143	306	finally
    //   157	176	306	finally
    //   181	198	306	finally
    //   198	206	306	finally
    //   225	249	306	finally
    //   254	273	310	finally
    //   25	96	314	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  @WorkerThread
  public zzatc zzfu(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   9: aload_0
    //   10: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   13: aload_0
    //   14: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: ldc_w 908
    //   20: bipush 23
    //   22: anewarray 306	java/lang/String
    //   25: dup
    //   26: iconst_0
    //   27: ldc_w 946
    //   30: aastore
    //   31: dup
    //   32: iconst_1
    //   33: ldc_w 951
    //   36: aastore
    //   37: dup
    //   38: iconst_2
    //   39: ldc_w 956
    //   42: aastore
    //   43: dup
    //   44: iconst_3
    //   45: ldc_w 961
    //   48: aastore
    //   49: dup
    //   50: iconst_4
    //   51: ldc 68
    //   53: aastore
    //   54: dup
    //   55: iconst_5
    //   56: ldc_w 969
    //   59: aastore
    //   60: dup
    //   61: bipush 6
    //   63: ldc 48
    //   65: aastore
    //   66: dup
    //   67: bipush 7
    //   69: ldc 52
    //   71: aastore
    //   72: dup
    //   73: bipush 8
    //   75: ldc 56
    //   77: aastore
    //   78: dup
    //   79: bipush 9
    //   81: ldc 60
    //   83: aastore
    //   84: dup
    //   85: bipush 10
    //   87: ldc 64
    //   89: aastore
    //   90: dup
    //   91: bipush 11
    //   93: ldc 72
    //   95: aastore
    //   96: dup
    //   97: bipush 12
    //   99: ldc 76
    //   101: aastore
    //   102: dup
    //   103: bipush 13
    //   105: ldc 80
    //   107: aastore
    //   108: dup
    //   109: bipush 14
    //   111: ldc 84
    //   113: aastore
    //   114: dup
    //   115: bipush 15
    //   117: ldc 92
    //   119: aastore
    //   120: dup
    //   121: bipush 16
    //   123: ldc 96
    //   125: aastore
    //   126: dup
    //   127: bipush 17
    //   129: ldc 100
    //   131: aastore
    //   132: dup
    //   133: bipush 18
    //   135: ldc 104
    //   137: aastore
    //   138: dup
    //   139: bipush 19
    //   141: ldc 108
    //   143: aastore
    //   144: dup
    //   145: bipush 20
    //   147: ldc 112
    //   149: aastore
    //   150: dup
    //   151: bipush 21
    //   153: ldc 116
    //   155: aastore
    //   156: dup
    //   157: bipush 22
    //   159: ldc 120
    //   161: aastore
    //   162: ldc_w 910
    //   165: iconst_1
    //   166: anewarray 306	java/lang/String
    //   169: dup
    //   170: iconst_0
    //   171: aload_1
    //   172: aastore
    //   173: aconst_null
    //   174: aconst_null
    //   175: aconst_null
    //   176: invokevirtual 452	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   179: astore 7
    //   181: aload 7
    //   183: astore 6
    //   185: aload 7
    //   187: invokeinterface 221 1 0
    //   192: istore_3
    //   193: iload_3
    //   194: ifne +19 -> 213
    //   197: aload 7
    //   199: ifnull +10 -> 209
    //   202: aload 7
    //   204: invokeinterface 228 1 0
    //   209: aconst_null
    //   210: astore_1
    //   211: aload_1
    //   212: areturn
    //   213: aload 7
    //   215: astore 6
    //   217: new 941	com/google/android/gms/internal/zzatc
    //   220: dup
    //   221: aload_0
    //   222: getfield 1398	com/google/android/gms/internal/zzatj:zzbqb	Lcom/google/android/gms/internal/zzaue;
    //   225: aload_1
    //   226: invokespecial 1401	com/google/android/gms/internal/zzatc:<init>	(Lcom/google/android/gms/internal/zzaue;Ljava/lang/String;)V
    //   229: astore 8
    //   231: aload 7
    //   233: astore 6
    //   235: aload 8
    //   237: aload 7
    //   239: iconst_0
    //   240: invokeinterface 619 2 0
    //   245: invokevirtual 1404	com/google/android/gms/internal/zzatc:zzfd	(Ljava/lang/String;)V
    //   248: aload 7
    //   250: astore 6
    //   252: aload 8
    //   254: aload 7
    //   256: iconst_1
    //   257: invokeinterface 619 2 0
    //   262: invokevirtual 1407	com/google/android/gms/internal/zzatc:zzfe	(Ljava/lang/String;)V
    //   265: aload 7
    //   267: astore 6
    //   269: aload 8
    //   271: aload 7
    //   273: iconst_2
    //   274: invokeinterface 619 2 0
    //   279: invokevirtual 1410	com/google/android/gms/internal/zzatc:zzff	(Ljava/lang/String;)V
    //   282: aload 7
    //   284: astore 6
    //   286: aload 8
    //   288: aload 7
    //   290: iconst_3
    //   291: invokeinterface 225 2 0
    //   296: invokevirtual 1413	com/google/android/gms/internal/zzatc:zzad	(J)V
    //   299: aload 7
    //   301: astore 6
    //   303: aload 8
    //   305: aload 7
    //   307: iconst_4
    //   308: invokeinterface 225 2 0
    //   313: invokevirtual 1416	com/google/android/gms/internal/zzatc:zzY	(J)V
    //   316: aload 7
    //   318: astore 6
    //   320: aload 8
    //   322: aload 7
    //   324: iconst_5
    //   325: invokeinterface 225 2 0
    //   330: invokevirtual 1419	com/google/android/gms/internal/zzatc:zzZ	(J)V
    //   333: aload 7
    //   335: astore 6
    //   337: aload 8
    //   339: aload 7
    //   341: bipush 6
    //   343: invokeinterface 619 2 0
    //   348: invokevirtual 1422	com/google/android/gms/internal/zzatc:setAppVersion	(Ljava/lang/String;)V
    //   351: aload 7
    //   353: astore 6
    //   355: aload 8
    //   357: aload 7
    //   359: bipush 7
    //   361: invokeinterface 619 2 0
    //   366: invokevirtual 1425	com/google/android/gms/internal/zzatc:zzfh	(Ljava/lang/String;)V
    //   369: aload 7
    //   371: astore 6
    //   373: aload 8
    //   375: aload 7
    //   377: bipush 8
    //   379: invokeinterface 225 2 0
    //   384: invokevirtual 1428	com/google/android/gms/internal/zzatc:zzab	(J)V
    //   387: aload 7
    //   389: astore 6
    //   391: aload 8
    //   393: aload 7
    //   395: bipush 9
    //   397: invokeinterface 225 2 0
    //   402: invokevirtual 1431	com/google/android/gms/internal/zzatc:zzac	(J)V
    //   405: aload 7
    //   407: astore 6
    //   409: aload 7
    //   411: bipush 10
    //   413: invokeinterface 1435 2 0
    //   418: ifeq +322 -> 740
    //   421: iconst_1
    //   422: istore_2
    //   423: goto +440 -> 863
    //   426: aload 7
    //   428: astore 6
    //   430: aload 8
    //   432: iload_3
    //   433: invokevirtual 1439	com/google/android/gms/internal/zzatc:setMeasurementEnabled	(Z)V
    //   436: aload 7
    //   438: astore 6
    //   440: aload 8
    //   442: aload 7
    //   444: bipush 11
    //   446: invokeinterface 225 2 0
    //   451: invokevirtual 1441	com/google/android/gms/internal/zzatc:zzag	(J)V
    //   454: aload 7
    //   456: astore 6
    //   458: aload 8
    //   460: aload 7
    //   462: bipush 12
    //   464: invokeinterface 225 2 0
    //   469: invokevirtual 1443	com/google/android/gms/internal/zzatc:zzah	(J)V
    //   472: aload 7
    //   474: astore 6
    //   476: aload 8
    //   478: aload 7
    //   480: bipush 13
    //   482: invokeinterface 225 2 0
    //   487: invokevirtual 1446	com/google/android/gms/internal/zzatc:zzai	(J)V
    //   490: aload 7
    //   492: astore 6
    //   494: aload 8
    //   496: aload 7
    //   498: bipush 14
    //   500: invokeinterface 225 2 0
    //   505: invokevirtual 1449	com/google/android/gms/internal/zzatc:zzaj	(J)V
    //   508: aload 7
    //   510: astore 6
    //   512: aload 8
    //   514: aload 7
    //   516: bipush 15
    //   518: invokeinterface 225 2 0
    //   523: invokevirtual 1452	com/google/android/gms/internal/zzatc:zzae	(J)V
    //   526: aload 7
    //   528: astore 6
    //   530: aload 8
    //   532: aload 7
    //   534: bipush 16
    //   536: invokeinterface 225 2 0
    //   541: invokevirtual 1455	com/google/android/gms/internal/zzatc:zzaf	(J)V
    //   544: aload 7
    //   546: astore 6
    //   548: aload 7
    //   550: bipush 17
    //   552: invokeinterface 1435 2 0
    //   557: ifeq +200 -> 757
    //   560: ldc2_w 1456
    //   563: lstore 4
    //   565: aload 7
    //   567: astore 6
    //   569: aload 8
    //   571: lload 4
    //   573: invokevirtual 1460	com/google/android/gms/internal/zzatc:zzaa	(J)V
    //   576: aload 7
    //   578: astore 6
    //   580: aload 8
    //   582: aload 7
    //   584: bipush 18
    //   586: invokeinterface 619 2 0
    //   591: invokevirtual 1463	com/google/android/gms/internal/zzatc:zzfg	(Ljava/lang/String;)V
    //   594: aload 7
    //   596: astore 6
    //   598: aload 8
    //   600: aload 7
    //   602: bipush 19
    //   604: invokeinterface 225 2 0
    //   609: invokevirtual 1466	com/google/android/gms/internal/zzatc:zzal	(J)V
    //   612: aload 7
    //   614: astore 6
    //   616: aload 8
    //   618: aload 7
    //   620: bipush 20
    //   622: invokeinterface 225 2 0
    //   627: invokevirtual 1469	com/google/android/gms/internal/zzatc:zzak	(J)V
    //   630: aload 7
    //   632: astore 6
    //   634: aload 8
    //   636: aload 7
    //   638: bipush 21
    //   640: invokeinterface 619 2 0
    //   645: invokevirtual 1472	com/google/android/gms/internal/zzatc:zzfi	(Ljava/lang/String;)V
    //   648: aload 7
    //   650: astore 6
    //   652: aload 7
    //   654: bipush 22
    //   656: invokeinterface 1435 2 0
    //   661: ifeq +115 -> 776
    //   664: lconst_0
    //   665: lstore 4
    //   667: aload 7
    //   669: astore 6
    //   671: aload 8
    //   673: lload 4
    //   675: invokevirtual 1475	com/google/android/gms/internal/zzatc:zzam	(J)V
    //   678: aload 7
    //   680: astore 6
    //   682: aload 8
    //   684: invokevirtual 1478	com/google/android/gms/internal/zzatc:zzKo	()V
    //   687: aload 7
    //   689: astore 6
    //   691: aload 7
    //   693: invokeinterface 727 1 0
    //   698: ifeq +24 -> 722
    //   701: aload 7
    //   703: astore 6
    //   705: aload_0
    //   706: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   709: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   712: ldc_w 1480
    //   715: aload_1
    //   716: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   719: invokevirtual 296	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   722: aload 8
    //   724: astore_1
    //   725: aload 7
    //   727: ifnull -516 -> 211
    //   730: aload 7
    //   732: invokeinterface 228 1 0
    //   737: aload 8
    //   739: areturn
    //   740: aload 7
    //   742: astore 6
    //   744: aload 7
    //   746: bipush 10
    //   748: invokeinterface 778 2 0
    //   753: istore_2
    //   754: goto +109 -> 863
    //   757: aload 7
    //   759: astore 6
    //   761: aload 7
    //   763: bipush 17
    //   765: invokeinterface 778 2 0
    //   770: i2l
    //   771: lstore 4
    //   773: goto -208 -> 565
    //   776: aload 7
    //   778: astore 6
    //   780: aload 7
    //   782: bipush 22
    //   784: invokeinterface 225 2 0
    //   789: lstore 4
    //   791: goto -124 -> 667
    //   794: astore 8
    //   796: aconst_null
    //   797: astore 7
    //   799: aload 7
    //   801: astore 6
    //   803: aload_0
    //   804: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   807: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   810: ldc_w 1482
    //   813: aload_1
    //   814: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   817: aload 8
    //   819: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   822: aload 7
    //   824: ifnull +10 -> 834
    //   827: aload 7
    //   829: invokeinterface 228 1 0
    //   834: aconst_null
    //   835: areturn
    //   836: astore_1
    //   837: aconst_null
    //   838: astore 6
    //   840: aload 6
    //   842: ifnull +10 -> 852
    //   845: aload 6
    //   847: invokeinterface 228 1 0
    //   852: aload_1
    //   853: athrow
    //   854: astore_1
    //   855: goto -15 -> 840
    //   858: astore 8
    //   860: goto -61 -> 799
    //   863: iload_2
    //   864: ifeq +8 -> 872
    //   867: iconst_1
    //   868: istore_3
    //   869: goto -443 -> 426
    //   872: iconst_0
    //   873: istore_3
    //   874: goto -448 -> 426
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	877	0	this	zzatj
    //   0	877	1	paramString	String
    //   422	442	2	i	int
    //   192	682	3	bool	boolean
    //   563	227	4	l	long
    //   183	663	6	localCursor1	Cursor
    //   179	649	7	localCursor2	Cursor
    //   229	509	8	localzzatc	zzatc
    //   794	24	8	localSQLiteException1	SQLiteException
    //   858	1	8	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   13	181	794	android/database/sqlite/SQLiteException
    //   13	181	836	finally
    //   185	193	854	finally
    //   217	231	854	finally
    //   235	248	854	finally
    //   252	265	854	finally
    //   269	282	854	finally
    //   286	299	854	finally
    //   303	316	854	finally
    //   320	333	854	finally
    //   337	351	854	finally
    //   355	369	854	finally
    //   373	387	854	finally
    //   391	405	854	finally
    //   409	421	854	finally
    //   430	436	854	finally
    //   440	454	854	finally
    //   458	472	854	finally
    //   476	490	854	finally
    //   494	508	854	finally
    //   512	526	854	finally
    //   530	544	854	finally
    //   548	560	854	finally
    //   569	576	854	finally
    //   580	594	854	finally
    //   598	612	854	finally
    //   616	630	854	finally
    //   634	648	854	finally
    //   652	664	854	finally
    //   671	678	854	finally
    //   682	687	854	finally
    //   691	701	854	finally
    //   705	722	854	finally
    //   744	754	854	finally
    //   761	773	854	finally
    //   780	791	854	finally
    //   803	822	854	finally
    //   185	193	858	android/database/sqlite/SQLiteException
    //   217	231	858	android/database/sqlite/SQLiteException
    //   235	248	858	android/database/sqlite/SQLiteException
    //   252	265	858	android/database/sqlite/SQLiteException
    //   269	282	858	android/database/sqlite/SQLiteException
    //   286	299	858	android/database/sqlite/SQLiteException
    //   303	316	858	android/database/sqlite/SQLiteException
    //   320	333	858	android/database/sqlite/SQLiteException
    //   337	351	858	android/database/sqlite/SQLiteException
    //   355	369	858	android/database/sqlite/SQLiteException
    //   373	387	858	android/database/sqlite/SQLiteException
    //   391	405	858	android/database/sqlite/SQLiteException
    //   409	421	858	android/database/sqlite/SQLiteException
    //   430	436	858	android/database/sqlite/SQLiteException
    //   440	454	858	android/database/sqlite/SQLiteException
    //   458	472	858	android/database/sqlite/SQLiteException
    //   476	490	858	android/database/sqlite/SQLiteException
    //   494	508	858	android/database/sqlite/SQLiteException
    //   512	526	858	android/database/sqlite/SQLiteException
    //   530	544	858	android/database/sqlite/SQLiteException
    //   548	560	858	android/database/sqlite/SQLiteException
    //   569	576	858	android/database/sqlite/SQLiteException
    //   580	594	858	android/database/sqlite/SQLiteException
    //   598	612	858	android/database/sqlite/SQLiteException
    //   616	630	858	android/database/sqlite/SQLiteException
    //   634	648	858	android/database/sqlite/SQLiteException
    //   652	664	858	android/database/sqlite/SQLiteException
    //   671	678	858	android/database/sqlite/SQLiteException
    //   682	687	858	android/database/sqlite/SQLiteException
    //   691	701	858	android/database/sqlite/SQLiteException
    //   705	722	858	android/database/sqlite/SQLiteException
    //   744	754	858	android/database/sqlite/SQLiteException
    //   761	773	858	android/database/sqlite/SQLiteException
    //   780	791	858	android/database/sqlite/SQLiteException
  }
  
  public long zzfv(String paramString)
  {
    zzac.zzdr(paramString);
    zzmR();
    zzob();
    try
    {
      int i = getWritableDatabase().delete("raw_events", "rowid in (select rowid from raw_events where app_id=? order by rowid desc limit -1 offset ?)", new String[] { paramString, String.valueOf(zzKn().zzfs(paramString)) });
      return i;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzKl().zzLZ().zze("Error deleting over the limit events. appId", zzatx.zzfE(paramString), localSQLiteException);
    }
    return 0L;
  }
  
  /* Error */
  @WorkerThread
  public byte[] zzfw(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   9: aload_0
    //   10: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   13: aload_0
    //   14: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: ldc_w 908
    //   20: iconst_1
    //   21: anewarray 306	java/lang/String
    //   24: dup
    //   25: iconst_0
    //   26: ldc 88
    //   28: aastore
    //   29: ldc_w 910
    //   32: iconst_1
    //   33: anewarray 306	java/lang/String
    //   36: dup
    //   37: iconst_0
    //   38: aload_1
    //   39: aastore
    //   40: aconst_null
    //   41: aconst_null
    //   42: aconst_null
    //   43: invokevirtual 452	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   46: astore 4
    //   48: aload 4
    //   50: astore_3
    //   51: aload 4
    //   53: invokeinterface 221 1 0
    //   58: istore_2
    //   59: iload_2
    //   60: ifne +19 -> 79
    //   63: aload 4
    //   65: ifnull +10 -> 75
    //   68: aload 4
    //   70: invokeinterface 228 1 0
    //   75: aconst_null
    //   76: astore_1
    //   77: aload_1
    //   78: areturn
    //   79: aload 4
    //   81: astore_3
    //   82: aload 4
    //   84: iconst_0
    //   85: invokeinterface 786 2 0
    //   90: astore 5
    //   92: aload 4
    //   94: astore_3
    //   95: aload 4
    //   97: invokeinterface 727 1 0
    //   102: ifeq +23 -> 125
    //   105: aload 4
    //   107: astore_3
    //   108: aload_0
    //   109: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   112: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   115: ldc_w 1494
    //   118: aload_1
    //   119: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   122: invokevirtual 296	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   125: aload 5
    //   127: astore_1
    //   128: aload 4
    //   130: ifnull -53 -> 77
    //   133: aload 4
    //   135: invokeinterface 228 1 0
    //   140: aload 5
    //   142: areturn
    //   143: astore 5
    //   145: aconst_null
    //   146: astore 4
    //   148: aload 4
    //   150: astore_3
    //   151: aload_0
    //   152: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   155: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   158: ldc_w 1496
    //   161: aload_1
    //   162: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   165: aload 5
    //   167: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   170: aload 4
    //   172: ifnull +10 -> 182
    //   175: aload 4
    //   177: invokeinterface 228 1 0
    //   182: aconst_null
    //   183: areturn
    //   184: astore_1
    //   185: aconst_null
    //   186: astore_3
    //   187: aload_3
    //   188: ifnull +9 -> 197
    //   191: aload_3
    //   192: invokeinterface 228 1 0
    //   197: aload_1
    //   198: athrow
    //   199: astore_1
    //   200: goto -13 -> 187
    //   203: astore 5
    //   205: goto -57 -> 148
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	208	0	this	zzatj
    //   0	208	1	paramString	String
    //   58	2	2	bool	boolean
    //   50	142	3	localCursor1	Cursor
    //   46	130	4	localCursor2	Cursor
    //   90	51	5	arrayOfByte	byte[]
    //   143	23	5	localSQLiteException1	SQLiteException
    //   203	1	5	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   13	48	143	android/database/sqlite/SQLiteException
    //   13	48	184	finally
    //   51	59	199	finally
    //   82	92	199	finally
    //   95	105	199	finally
    //   108	125	199	finally
    //   151	170	199	finally
    //   51	59	203	android/database/sqlite/SQLiteException
    //   82	92	203	android/database/sqlite/SQLiteException
    //   95	105	203	android/database/sqlite/SQLiteException
    //   108	125	203	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  void zzfx(String paramString)
  {
    zzob();
    zzmR();
    zzac.zzdr(paramString);
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.delete("property_filters", "app_id=?", new String[] { paramString });
    localSQLiteDatabase.delete("event_filters", "app_id=?", new String[] { paramString });
  }
  
  /* Error */
  Map<Integer, zzauw.zzf> zzfy(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   4: aload_0
    //   5: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   8: aload_1
    //   9: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: astore 4
    //   19: aload 4
    //   21: ldc_w 1064
    //   24: iconst_2
    //   25: anewarray 306	java/lang/String
    //   28: dup
    //   29: iconst_0
    //   30: ldc_w 498
    //   33: aastore
    //   34: dup
    //   35: iconst_1
    //   36: ldc_w 1062
    //   39: aastore
    //   40: ldc_w 910
    //   43: iconst_1
    //   44: anewarray 306	java/lang/String
    //   47: dup
    //   48: iconst_0
    //   49: aload_1
    //   50: aastore
    //   51: aconst_null
    //   52: aconst_null
    //   53: aconst_null
    //   54: invokevirtual 452	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   57: astore 5
    //   59: aload 5
    //   61: astore 4
    //   63: aload 5
    //   65: invokeinterface 221 1 0
    //   70: istore_3
    //   71: iload_3
    //   72: ifne +19 -> 91
    //   75: aload 5
    //   77: ifnull +10 -> 87
    //   80: aload 5
    //   82: invokeinterface 228 1 0
    //   87: aconst_null
    //   88: astore_1
    //   89: aload_1
    //   90: areturn
    //   91: aload 5
    //   93: astore 4
    //   95: new 28	android/support/v4/util/ArrayMap
    //   98: dup
    //   99: invokespecial 818	android/support/v4/util/ArrayMap:<init>	()V
    //   102: astore 6
    //   104: aload 5
    //   106: astore 4
    //   108: aload 5
    //   110: iconst_0
    //   111: invokeinterface 778 2 0
    //   116: istore_2
    //   117: aload 5
    //   119: astore 4
    //   121: aload 5
    //   123: iconst_1
    //   124: invokeinterface 786 2 0
    //   129: invokestatic 829	com/google/android/gms/internal/zzbyb:zzag	([B)Lcom/google/android/gms/internal/zzbyb;
    //   132: astore 7
    //   134: aload 5
    //   136: astore 4
    //   138: new 1058	com/google/android/gms/internal/zzauw$zzf
    //   141: dup
    //   142: invokespecial 1499	com/google/android/gms/internal/zzauw$zzf:<init>	()V
    //   145: astore 8
    //   147: aload 5
    //   149: astore 4
    //   151: aload 8
    //   153: aload 7
    //   155: invokevirtual 1500	com/google/android/gms/internal/zzauw$zzf:zzb	(Lcom/google/android/gms/internal/zzbyb;)Lcom/google/android/gms/internal/zzbyj;
    //   158: pop
    //   159: aload 5
    //   161: astore 4
    //   163: aload 6
    //   165: iload_2
    //   166: invokestatic 468	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   169: aload 8
    //   171: invokeinterface 44 3 0
    //   176: pop
    //   177: aload 5
    //   179: astore 4
    //   181: aload 5
    //   183: invokeinterface 727 1 0
    //   188: istore_3
    //   189: iload_3
    //   190: ifne -86 -> 104
    //   193: aload 6
    //   195: astore_1
    //   196: aload 5
    //   198: ifnull -109 -> 89
    //   201: aload 5
    //   203: invokeinterface 228 1 0
    //   208: aload 6
    //   210: areturn
    //   211: astore 7
    //   213: aload 5
    //   215: astore 4
    //   217: aload_0
    //   218: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   221: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   224: ldc_w 1502
    //   227: aload_1
    //   228: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   231: iload_2
    //   232: invokestatic 468	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   235: aload 7
    //   237: invokevirtual 472	com/google/android/gms/internal/zzatx$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   240: goto -63 -> 177
    //   243: astore 6
    //   245: aload 5
    //   247: astore 4
    //   249: aload_0
    //   250: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   253: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   256: ldc_w 1504
    //   259: aload_1
    //   260: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   263: aload 6
    //   265: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   268: aload 5
    //   270: ifnull +10 -> 280
    //   273: aload 5
    //   275: invokeinterface 228 1 0
    //   280: aconst_null
    //   281: areturn
    //   282: astore_1
    //   283: aconst_null
    //   284: astore 4
    //   286: aload 4
    //   288: ifnull +10 -> 298
    //   291: aload 4
    //   293: invokeinterface 228 1 0
    //   298: aload_1
    //   299: athrow
    //   300: astore_1
    //   301: goto -15 -> 286
    //   304: astore 6
    //   306: aconst_null
    //   307: astore 5
    //   309: goto -64 -> 245
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	312	0	this	zzatj
    //   0	312	1	paramString	String
    //   116	116	2	i	int
    //   70	120	3	bool	boolean
    //   17	275	4	localObject	Object
    //   57	251	5	localCursor	Cursor
    //   102	107	6	localArrayMap	ArrayMap
    //   243	21	6	localSQLiteException1	SQLiteException
    //   304	1	6	localSQLiteException2	SQLiteException
    //   132	22	7	localzzbyb	zzbyb
    //   211	25	7	localIOException	IOException
    //   145	25	8	localzzf	zzauw.zzf
    // Exception table:
    //   from	to	target	type
    //   151	159	211	java/io/IOException
    //   63	71	243	android/database/sqlite/SQLiteException
    //   95	104	243	android/database/sqlite/SQLiteException
    //   108	117	243	android/database/sqlite/SQLiteException
    //   121	134	243	android/database/sqlite/SQLiteException
    //   138	147	243	android/database/sqlite/SQLiteException
    //   151	159	243	android/database/sqlite/SQLiteException
    //   163	177	243	android/database/sqlite/SQLiteException
    //   181	189	243	android/database/sqlite/SQLiteException
    //   217	240	243	android/database/sqlite/SQLiteException
    //   19	59	282	finally
    //   63	71	300	finally
    //   95	104	300	finally
    //   108	117	300	finally
    //   121	134	300	finally
    //   138	147	300	finally
    //   151	159	300	finally
    //   163	177	300	finally
    //   181	189	300	finally
    //   217	240	300	finally
    //   249	268	300	finally
    //   19	59	304	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  void zzfz(String paramString)
  {
    zzob();
    zzmR();
    zzac.zzdr(paramString);
    try
    {
      SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
      String[] arrayOfString = new String[1];
      arrayOfString[0] = paramString;
      int i = localSQLiteDatabase.delete("events", "app_id=?", arrayOfString);
      int j = localSQLiteDatabase.delete("user_attributes", "app_id=?", arrayOfString);
      int k = localSQLiteDatabase.delete("conditional_properties", "app_id=?", arrayOfString);
      int m = localSQLiteDatabase.delete("apps", "app_id=?", arrayOfString);
      int n = localSQLiteDatabase.delete("raw_events", "app_id=?", arrayOfString);
      int i1 = localSQLiteDatabase.delete("raw_events_metadata", "app_id=?", arrayOfString);
      int i2 = localSQLiteDatabase.delete("event_filters", "app_id=?", arrayOfString);
      int i3 = localSQLiteDatabase.delete("property_filters", "app_id=?", arrayOfString);
      i = localSQLiteDatabase.delete("audience_filter_values", "app_id=?", arrayOfString) + (i + 0 + j + k + m + n + i1 + i2 + i3);
      if (i > 0) {
        zzKl().zzMf().zze("Deleted application data. app, records", paramString, Integer.valueOf(i));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzKl().zzLZ().zze("Error deleting application data. appId, error", zzatx.zzfE(paramString), localSQLiteException);
    }
  }
  
  @WorkerThread
  public List<zzatg> zzh(String paramString, long paramLong)
  {
    zzac.zzdr(paramString);
    zzmR();
    zzob();
    if (paramLong < 0L)
    {
      zzKl().zzMb().zze("Invalid time querying timed out conditional properties", zzatx.zzfE(paramString), Long.valueOf(paramLong));
      return Collections.emptyList();
    }
    return zzc("active=0 and app_id=? and abs(? - creation_timestamp) > trigger_timeout", new String[] { paramString, String.valueOf(paramLong) });
  }
  
  @WorkerThread
  public List<zzatg> zzi(String paramString, long paramLong)
  {
    zzac.zzdr(paramString);
    zzmR();
    zzob();
    if (paramLong < 0L)
    {
      zzKl().zzMb().zze("Invalid time querying expired conditional properties", zzatx.zzfE(paramString), Long.valueOf(paramLong));
      return Collections.emptyList();
    }
    return zzc("active<>0 and app_id=? and abs(? - triggered_timestamp) > time_to_live", new String[] { paramString, String.valueOf(paramLong) });
  }
  
  /* Error */
  @WorkerThread
  public List<zzaus> zzk(String paramString1, String paramString2, String paramString3)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 8
    //   3: aload_1
    //   4: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_0
    //   9: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   12: aload_0
    //   13: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   16: new 837	java/util/ArrayList
    //   19: dup
    //   20: invokespecial 838	java/util/ArrayList:<init>	()V
    //   23: astore 9
    //   25: new 837	java/util/ArrayList
    //   28: dup
    //   29: iconst_3
    //   30: invokespecial 1524	java/util/ArrayList:<init>	(I)V
    //   33: astore 10
    //   35: aload 10
    //   37: aload_1
    //   38: invokeinterface 841 2 0
    //   43: pop
    //   44: new 318	java/lang/StringBuilder
    //   47: dup
    //   48: ldc_w 910
    //   51: invokespecial 588	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   54: astore 7
    //   56: aload_2
    //   57: invokestatic 463	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   60: ifne +21 -> 81
    //   63: aload 10
    //   65: aload_2
    //   66: invokeinterface 841 2 0
    //   71: pop
    //   72: aload 7
    //   74: ldc_w 1526
    //   77: invokevirtual 333	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   80: pop
    //   81: aload_3
    //   82: invokestatic 463	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   85: ifne +30 -> 115
    //   88: aload 10
    //   90: aload_3
    //   91: invokestatic 322	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   94: ldc_w 1528
    //   97: invokevirtual 1531	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   100: invokeinterface 841 2 0
    //   105: pop
    //   106: aload 7
    //   108: ldc_w 1533
    //   111: invokevirtual 333	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   114: pop
    //   115: aload 10
    //   117: aload 10
    //   119: invokeinterface 593 1 0
    //   124: anewarray 306	java/lang/String
    //   127: invokeinterface 1537 2 0
    //   132: checkcast 1539	[Ljava/lang/String;
    //   135: astore 10
    //   137: aload_0
    //   138: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   141: astore 11
    //   143: aload 7
    //   145: invokevirtual 338	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   148: astore 7
    //   150: aload_0
    //   151: invokevirtual 658	com/google/android/gms/internal/zzatj:zzKn	()Lcom/google/android/gms/internal/zzati;
    //   154: invokevirtual 1387	com/google/android/gms/internal/zzati:zzKY	()I
    //   157: pop
    //   158: aload 11
    //   160: ldc_w 734
    //   163: iconst_4
    //   164: anewarray 306	java/lang/String
    //   167: dup
    //   168: iconst_0
    //   169: ldc_w 446
    //   172: aastore
    //   173: dup
    //   174: iconst_1
    //   175: ldc_w 742
    //   178: aastore
    //   179: dup
    //   180: iconst_2
    //   181: ldc_w 743
    //   184: aastore
    //   185: dup
    //   186: iconst_3
    //   187: ldc 36
    //   189: aastore
    //   190: aload 7
    //   192: aload 10
    //   194: aconst_null
    //   195: aconst_null
    //   196: ldc_w 1079
    //   199: sipush 1001
    //   202: invokestatic 579	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   205: invokevirtual 1084	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   208: astore 7
    //   210: aload_2
    //   211: astore 8
    //   213: aload 7
    //   215: invokeinterface 221 1 0
    //   220: istore 4
    //   222: iload 4
    //   224: ifne +18 -> 242
    //   227: aload 7
    //   229: ifnull +10 -> 239
    //   232: aload 7
    //   234: invokeinterface 228 1 0
    //   239: aload 9
    //   241: areturn
    //   242: aload_2
    //   243: astore 8
    //   245: aload 9
    //   247: invokeinterface 593 1 0
    //   252: aload_0
    //   253: invokevirtual 658	com/google/android/gms/internal/zzatj:zzKn	()Lcom/google/android/gms/internal/zzati;
    //   256: invokevirtual 1387	com/google/android/gms/internal/zzati:zzKY	()I
    //   259: if_icmplt +44 -> 303
    //   262: aload_2
    //   263: astore 8
    //   265: aload_0
    //   266: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   269: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   272: ldc_w 1541
    //   275: aload_0
    //   276: invokevirtual 658	com/google/android/gms/internal/zzatj:zzKn	()Lcom/google/android/gms/internal/zzati;
    //   279: invokevirtual 1387	com/google/android/gms/internal/zzati:zzKY	()I
    //   282: invokestatic 468	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   285: invokevirtual 296	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   288: aload 7
    //   290: ifnull +10 -> 300
    //   293: aload 7
    //   295: invokeinterface 228 1 0
    //   300: aload 9
    //   302: areturn
    //   303: aload_2
    //   304: astore 8
    //   306: aload 7
    //   308: iconst_0
    //   309: invokeinterface 619 2 0
    //   314: astore 10
    //   316: aload_2
    //   317: astore 8
    //   319: aload 7
    //   321: iconst_1
    //   322: invokeinterface 225 2 0
    //   327: lstore 5
    //   329: aload_2
    //   330: astore 8
    //   332: aload_0
    //   333: aload 7
    //   335: iconst_2
    //   336: invokevirtual 746	com/google/android/gms/internal/zzatj:zzb	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   339: astore 11
    //   341: aload_2
    //   342: astore 8
    //   344: aload 7
    //   346: iconst_3
    //   347: invokeinterface 619 2 0
    //   352: astore_2
    //   353: aload 11
    //   355: ifnonnull +35 -> 390
    //   358: aload_0
    //   359: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   362: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   365: ldc_w 1543
    //   368: aload_1
    //   369: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   372: aload_2
    //   373: aload_3
    //   374: invokevirtual 472	com/google/android/gms/internal/zzatx$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   377: aload 7
    //   379: invokeinterface 727 1 0
    //   384: ifne -142 -> 242
    //   387: goto -99 -> 288
    //   390: aload 9
    //   392: new 748	com/google/android/gms/internal/zzaus
    //   395: dup
    //   396: aload_1
    //   397: aload_2
    //   398: aload 10
    //   400: lload 5
    //   402: aload 11
    //   404: invokespecial 751	com/google/android/gms/internal/zzaus:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   407: invokeinterface 841 2 0
    //   412: pop
    //   413: goto -36 -> 377
    //   416: astore 8
    //   418: aload 7
    //   420: astore_3
    //   421: aload 8
    //   423: astore 7
    //   425: aload_0
    //   426: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   429: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   432: ldc_w 1545
    //   435: aload_1
    //   436: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   439: aload_2
    //   440: aload 7
    //   442: invokevirtual 472	com/google/android/gms/internal/zzatx$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   445: aload_3
    //   446: ifnull +9 -> 455
    //   449: aload_3
    //   450: invokeinterface 228 1 0
    //   455: aconst_null
    //   456: areturn
    //   457: astore_1
    //   458: aload 8
    //   460: astore_2
    //   461: aload_2
    //   462: ifnull +9 -> 471
    //   465: aload_2
    //   466: invokeinterface 228 1 0
    //   471: aload_1
    //   472: athrow
    //   473: astore_1
    //   474: aload 7
    //   476: astore_2
    //   477: goto -16 -> 461
    //   480: astore_1
    //   481: aload_3
    //   482: astore_2
    //   483: goto -22 -> 461
    //   486: astore 7
    //   488: aconst_null
    //   489: astore_3
    //   490: goto -65 -> 425
    //   493: astore_2
    //   494: aload 7
    //   496: astore_3
    //   497: aload_2
    //   498: astore 7
    //   500: aload 8
    //   502: astore_2
    //   503: goto -78 -> 425
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	506	0	this	zzatj
    //   0	506	1	paramString1	String
    //   0	506	2	paramString2	String
    //   0	506	3	paramString3	String
    //   220	3	4	bool	boolean
    //   327	74	5	l	long
    //   54	421	7	localObject1	Object
    //   486	9	7	localSQLiteException1	SQLiteException
    //   498	1	7	str1	String
    //   1	342	8	str2	String
    //   416	85	8	localSQLiteException2	SQLiteException
    //   23	368	9	localArrayList	ArrayList
    //   33	366	10	localObject2	Object
    //   141	262	11	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   358	377	416	android/database/sqlite/SQLiteException
    //   377	387	416	android/database/sqlite/SQLiteException
    //   390	413	416	android/database/sqlite/SQLiteException
    //   25	81	457	finally
    //   81	115	457	finally
    //   115	210	457	finally
    //   213	222	473	finally
    //   245	262	473	finally
    //   265	288	473	finally
    //   306	316	473	finally
    //   319	329	473	finally
    //   332	341	473	finally
    //   344	353	473	finally
    //   358	377	473	finally
    //   377	387	473	finally
    //   390	413	473	finally
    //   425	445	480	finally
    //   25	81	486	android/database/sqlite/SQLiteException
    //   81	115	486	android/database/sqlite/SQLiteException
    //   115	210	486	android/database/sqlite/SQLiteException
    //   213	222	493	android/database/sqlite/SQLiteException
    //   245	262	493	android/database/sqlite/SQLiteException
    //   265	288	493	android/database/sqlite/SQLiteException
    //   306	316	493	android/database/sqlite/SQLiteException
    //   319	329	493	android/database/sqlite/SQLiteException
    //   332	341	493	android/database/sqlite/SQLiteException
    //   344	353	493	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  public List<zzatg> zzl(String paramString1, String paramString2, String paramString3)
  {
    zzac.zzdr(paramString1);
    zzmR();
    zzob();
    ArrayList localArrayList = new ArrayList(3);
    localArrayList.add(paramString1);
    paramString1 = new StringBuilder("app_id=?");
    if (!TextUtils.isEmpty(paramString2))
    {
      localArrayList.add(paramString2);
      paramString1.append(" and origin=?");
    }
    if (!TextUtils.isEmpty(paramString3))
    {
      localArrayList.add(String.valueOf(paramString3).concat("*"));
      paramString1.append(" and name glob ?");
    }
    paramString2 = (String[])localArrayList.toArray(new String[localArrayList.size()]);
    return zzc(paramString1.toString(), paramString2);
  }
  
  protected void zzmS() {}
  
  /* Error */
  @WorkerThread
  public List<android.util.Pair<zzauw.zze, Long>> zzn(String paramString, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore 6
    //   3: aload_0
    //   4: invokevirtual 385	com/google/android/gms/internal/zzatj:zzmR	()V
    //   7: aload_0
    //   8: invokevirtual 382	com/google/android/gms/internal/zzatj:zzob	()V
    //   11: iload_2
    //   12: ifle +112 -> 124
    //   15: iconst_1
    //   16: istore 5
    //   18: iload 5
    //   20: invokestatic 1554	com/google/android/gms/common/internal/zzac:zzaw	(Z)V
    //   23: iload_3
    //   24: ifle +106 -> 130
    //   27: iload 6
    //   29: istore 5
    //   31: iload 5
    //   33: invokestatic 1554	com/google/android/gms/common/internal/zzac:zzaw	(Z)V
    //   36: aload_1
    //   37: invokestatic 391	com/google/android/gms/common/internal/zzac:zzdr	(Ljava/lang/String;)Ljava/lang/String;
    //   40: pop
    //   41: aload_0
    //   42: invokevirtual 212	com/google/android/gms/internal/zzatj:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   45: ldc_w 672
    //   48: iconst_2
    //   49: anewarray 306	java/lang/String
    //   52: dup
    //   53: iconst_0
    //   54: ldc_w 1079
    //   57: aastore
    //   58: dup
    //   59: iconst_1
    //   60: ldc_w 507
    //   63: aastore
    //   64: ldc_w 910
    //   67: iconst_1
    //   68: anewarray 306	java/lang/String
    //   71: dup
    //   72: iconst_0
    //   73: aload_1
    //   74: aastore
    //   75: aconst_null
    //   76: aconst_null
    //   77: ldc_w 1079
    //   80: iload_2
    //   81: invokestatic 579	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   84: invokevirtual 1084	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   87: astore 9
    //   89: aload 9
    //   91: invokeinterface 221 1 0
    //   96: ifne +40 -> 136
    //   99: invokestatic 1337	java/util/Collections:emptyList	()Ljava/util/List;
    //   102: astore 10
    //   104: aload 10
    //   106: astore_1
    //   107: aload 9
    //   109: ifnull +13 -> 122
    //   112: aload 9
    //   114: invokeinterface 228 1 0
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
    //   136: new 837	java/util/ArrayList
    //   139: dup
    //   140: invokespecial 838	java/util/ArrayList:<init>	()V
    //   143: astore 10
    //   145: iconst_0
    //   146: istore_2
    //   147: aload 9
    //   149: iconst_0
    //   150: invokeinterface 225 2 0
    //   155: lstore 7
    //   157: aload 9
    //   159: iconst_1
    //   160: invokeinterface 786 2 0
    //   165: astore 11
    //   167: aload_0
    //   168: invokevirtual 782	com/google/android/gms/internal/zzatj:zzKh	()Lcom/google/android/gms/internal/zzaut;
    //   171: aload 11
    //   173: invokevirtual 1557	com/google/android/gms/internal/zzaut:zzx	([B)[B
    //   176: astore 11
    //   178: aload 10
    //   180: invokeinterface 1558 1 0
    //   185: ifne +73 -> 258
    //   188: aload 11
    //   190: arraylength
    //   191: istore 4
    //   193: iload 4
    //   195: iload_2
    //   196: iadd
    //   197: iload_3
    //   198: if_icmple +60 -> 258
    //   201: aload 10
    //   203: astore_1
    //   204: aload 9
    //   206: ifnull -84 -> 122
    //   209: aload 9
    //   211: invokeinterface 228 1 0
    //   216: aload 10
    //   218: areturn
    //   219: astore 11
    //   221: aload_0
    //   222: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   225: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   228: ldc_w 1560
    //   231: aload_1
    //   232: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   235: aload 11
    //   237: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   240: aload 9
    //   242: invokeinterface 727 1 0
    //   247: ifeq -46 -> 201
    //   250: iload_2
    //   251: iload_3
    //   252: if_icmpgt -51 -> 201
    //   255: goto -108 -> 147
    //   258: aload 11
    //   260: invokestatic 829	com/google/android/gms/internal/zzbyb:zzag	([B)Lcom/google/android/gms/internal/zzbyb;
    //   263: astore 12
    //   265: new 885	com/google/android/gms/internal/zzauw$zze
    //   268: dup
    //   269: invokespecial 1091	com/google/android/gms/internal/zzauw$zze:<init>	()V
    //   272: astore 13
    //   274: aload 13
    //   276: aload 12
    //   278: invokevirtual 1092	com/google/android/gms/internal/zzauw$zze:zzb	(Lcom/google/android/gms/internal/zzbyb;)Lcom/google/android/gms/internal/zzbyj;
    //   281: pop
    //   282: aload 11
    //   284: arraylength
    //   285: iload_2
    //   286: iadd
    //   287: istore_2
    //   288: aload 10
    //   290: aload 13
    //   292: lload 7
    //   294: invokestatic 869	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   297: invokestatic 1566	android/util/Pair:create	(Ljava/lang/Object;Ljava/lang/Object;)Landroid/util/Pair;
    //   300: invokeinterface 841 2 0
    //   305: pop
    //   306: goto -66 -> 240
    //   309: astore 10
    //   311: aload_0
    //   312: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   315: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   318: ldc_w 1568
    //   321: aload_1
    //   322: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   325: aload 10
    //   327: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   330: invokestatic 1337	java/util/Collections:emptyList	()Ljava/util/List;
    //   333: astore 10
    //   335: aload 10
    //   337: astore_1
    //   338: aload 9
    //   340: ifnull -218 -> 122
    //   343: aload 9
    //   345: invokeinterface 228 1 0
    //   350: aload 10
    //   352: areturn
    //   353: astore 11
    //   355: aload_0
    //   356: invokevirtual 232	com/google/android/gms/internal/zzatj:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   359: invokevirtual 238	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   362: ldc_w 1570
    //   365: aload_1
    //   366: invokestatic 415	com/google/android/gms/internal/zzatx:zzfE	(Ljava/lang/String;)Ljava/lang/Object;
    //   369: aload 11
    //   371: invokevirtual 246	com/google/android/gms/internal/zzatx$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   374: goto -134 -> 240
    //   377: astore_1
    //   378: aconst_null
    //   379: astore 9
    //   381: aload 9
    //   383: ifnull +10 -> 393
    //   386: aload 9
    //   388: invokeinterface 228 1 0
    //   393: aload_1
    //   394: athrow
    //   395: astore_1
    //   396: goto -15 -> 381
    //   399: astore_1
    //   400: goto -19 -> 381
    //   403: astore 10
    //   405: aconst_null
    //   406: astore 9
    //   408: goto -97 -> 311
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	411	0	this	zzatj
    //   0	411	1	paramString	String
    //   0	411	2	paramInt1	int
    //   0	411	3	paramInt2	int
    //   191	6	4	i	int
    //   16	116	5	bool1	boolean
    //   1	27	6	bool2	boolean
    //   155	138	7	l	long
    //   87	320	9	localCursor	Cursor
    //   102	187	10	localObject	Object
    //   309	17	10	localSQLiteException1	SQLiteException
    //   333	18	10	localList	List
    //   403	1	10	localSQLiteException2	SQLiteException
    //   165	24	11	arrayOfByte	byte[]
    //   219	64	11	localIOException1	IOException
    //   353	17	11	localIOException2	IOException
    //   263	14	12	localzzbyb	zzbyb
    //   272	19	13	localzze	zzauw.zze
    // Exception table:
    //   from	to	target	type
    //   157	178	219	java/io/IOException
    //   89	104	309	android/database/sqlite/SQLiteException
    //   136	145	309	android/database/sqlite/SQLiteException
    //   147	157	309	android/database/sqlite/SQLiteException
    //   157	178	309	android/database/sqlite/SQLiteException
    //   178	193	309	android/database/sqlite/SQLiteException
    //   221	240	309	android/database/sqlite/SQLiteException
    //   240	250	309	android/database/sqlite/SQLiteException
    //   258	274	309	android/database/sqlite/SQLiteException
    //   274	282	309	android/database/sqlite/SQLiteException
    //   282	306	309	android/database/sqlite/SQLiteException
    //   355	374	309	android/database/sqlite/SQLiteException
    //   274	282	353	java/io/IOException
    //   41	89	377	finally
    //   89	104	395	finally
    //   136	145	395	finally
    //   147	157	395	finally
    //   157	178	395	finally
    //   178	193	395	finally
    //   221	240	395	finally
    //   240	250	395	finally
    //   258	274	395	finally
    //   274	282	395	finally
    //   282	306	395	finally
    //   355	374	395	finally
    //   311	335	399	finally
    //   41	89	403	android/database/sqlite/SQLiteException
  }
  
  String zzow()
  {
    return zzKn().zzpv();
  }
  
  @WorkerThread
  public void zzz(String paramString, int paramInt)
  {
    zzac.zzdr(paramString);
    zzmR();
    zzob();
    try
    {
      getWritableDatabase().execSQL("delete from user_attributes where app_id=? and name in (select name from user_attributes where app_id=? and name like '_ltv_%' order by triggered_timestamp desc limit ?,10);", new String[] { paramString, paramString, String.valueOf(paramInt) });
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzKl().zzLZ().zze("Error pruning currencies. appId", zzatx.zzfE(paramString), localSQLiteException);
    }
  }
  
  public static class zza
  {
    long zzbro;
    long zzbrp;
    long zzbrq;
    long zzbrr;
    long zzbrs;
  }
  
  static abstract interface zzb
  {
    public abstract boolean zza(long paramLong, zzauw.zzb paramzzb);
    
    public abstract void zzb(zzauw.zze paramzze);
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
      if (!zzatj.zza(zzatj.this).zzA(zzatj.this.zzKn().zzLd())) {
        throw new SQLiteException("Database open failed");
      }
      try
      {
        SQLiteDatabase localSQLiteDatabase = super.getWritableDatabase();
        return localSQLiteDatabase;
      }
      catch (SQLiteException localSQLiteException1)
      {
        zzatj.zza(zzatj.this).start();
        zzatj.this.zzKl().zzLZ().log("Opening the database failed, dropping and recreating it");
        Object localObject = zzatj.this.zzow();
        if (!zzatj.this.getContext().getDatabasePath((String)localObject).delete()) {
          zzatj.this.zzKl().zzLZ().zzj("Failed to delete corrupted db file", localObject);
        }
        try
        {
          localObject = super.getWritableDatabase();
          zzatj.zza(zzatj.this).clear();
          return (SQLiteDatabase)localObject;
        }
        catch (SQLiteException localSQLiteException2)
        {
          zzatj.this.zzKl().zzLZ().zzj("Failed to open freshly created database", localSQLiteException2);
          throw localSQLiteException2;
        }
      }
    }
    
    @WorkerThread
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      zzatj.zza(zzatj.this.zzKl(), paramSQLiteDatabase);
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
        zzatj.zza(zzatj.this.zzKl(), paramSQLiteDatabase, "events", "CREATE TABLE IF NOT EXISTS events ( app_id TEXT NOT NULL, name TEXT NOT NULL, lifetime_count INTEGER NOT NULL, current_bundle_count INTEGER NOT NULL, last_fire_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,lifetime_count,current_bundle_count,last_fire_timestamp", null);
        zzatj.zza(zzatj.this.zzKl(), paramSQLiteDatabase, "conditional_properties", "CREATE TABLE IF NOT EXISTS conditional_properties ( app_id TEXT NOT NULL, origin TEXT NOT NULL, name TEXT NOT NULL, value BLOB NOT NULL, creation_timestamp INTEGER NOT NULL, active INTEGER NOT NULL, trigger_event_name TEXT, trigger_timeout INTEGER NOT NULL, timed_out_event BLOB,triggered_event BLOB, triggered_timestamp INTEGER NOT NULL, time_to_live INTEGER NOT NULL, expired_event BLOB, PRIMARY KEY (app_id, name)) ;", "app_id,origin,name,value,active,trigger_event_name,trigger_timeout,creation_timestamp,timed_out_event,triggered_event,triggered_timestamp,time_to_live,expired_event", null);
        zzatj.zza(zzatj.this.zzKl(), paramSQLiteDatabase, "user_attributes", "CREATE TABLE IF NOT EXISTS user_attributes ( app_id TEXT NOT NULL, name TEXT NOT NULL, triggered_timestamp INTEGER NOT NULL, value BLOB NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,triggered_timestamp,value", zzatj.zzLO());
        zzatj.zza(zzatj.this.zzKl(), paramSQLiteDatabase, "apps", "CREATE TABLE IF NOT EXISTS apps ( app_id TEXT NOT NULL, app_instance_id TEXT, gmp_app_id TEXT, resettable_device_id_hash TEXT, last_bundle_index INTEGER NOT NULL, last_bundle_end_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id)) ;", "app_id,app_instance_id,gmp_app_id,resettable_device_id_hash,last_bundle_index,last_bundle_end_timestamp", zzatj.zzbri);
        zzatj.zza(zzatj.this.zzKl(), paramSQLiteDatabase, "queue", "CREATE TABLE IF NOT EXISTS queue ( app_id TEXT NOT NULL, bundle_end_timestamp INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,bundle_end_timestamp,data", zzatj.zzLP());
        zzatj.zza(zzatj.this.zzKl(), paramSQLiteDatabase, "raw_events_metadata", "CREATE TABLE IF NOT EXISTS raw_events_metadata ( app_id TEXT NOT NULL, metadata_fingerprint INTEGER NOT NULL, metadata BLOB NOT NULL, PRIMARY KEY (app_id, metadata_fingerprint));", "app_id,metadata_fingerprint,metadata", null);
        zzatj.zza(zzatj.this.zzKl(), paramSQLiteDatabase, "raw_events", "CREATE TABLE IF NOT EXISTS raw_events ( app_id TEXT NOT NULL, name TEXT NOT NULL, timestamp INTEGER NOT NULL, metadata_fingerprint INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,name,timestamp,metadata_fingerprint,data", zzatj.zzLQ());
        zzatj.zza(zzatj.this.zzKl(), paramSQLiteDatabase, "event_filters", "CREATE TABLE IF NOT EXISTS event_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, event_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, event_name, audience_id, filter_id));", "app_id,audience_id,filter_id,event_name,data", null);
        zzatj.zza(zzatj.this.zzKl(), paramSQLiteDatabase, "property_filters", "CREATE TABLE IF NOT EXISTS property_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, property_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, property_name, audience_id, filter_id));", "app_id,audience_id,filter_id,property_name,data", null);
        zzatj.zza(zzatj.this.zzKl(), paramSQLiteDatabase, "audience_filter_values", "CREATE TABLE IF NOT EXISTS audience_filter_values ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, current_results BLOB, PRIMARY KEY (app_id, audience_id));", "app_id,audience_id,current_results", null);
        zzatj.zza(zzatj.this.zzKl(), paramSQLiteDatabase, "app2", "CREATE TABLE IF NOT EXISTS app2 ( app_id TEXT NOT NULL, first_open_count INTEGER NOT NULL, PRIMARY KEY (app_id));", "app_id,first_open_count", zzatj.zzLR());
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */