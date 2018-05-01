package com.google.android.gms.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

final class zzcen
  extends zzchj
{
  private static final Map<String, String> zzbpn;
  private static final Map<String, String> zzbpo;
  private static final Map<String, String> zzbpp;
  private static final Map<String, String> zzbpq;
  private static final Map<String, String> zzbpr;
  private final zzceq zzbps;
  private final zzcjf zzbpt = new zzcjf(zzkq());
  
  static
  {
    ArrayMap localArrayMap = new ArrayMap(1);
    zzbpn = localArrayMap;
    localArrayMap.put("origin", "ALTER TABLE user_attributes ADD COLUMN origin TEXT;");
    localArrayMap = new ArrayMap(18);
    zzbpo = localArrayMap;
    localArrayMap.put("app_version", "ALTER TABLE apps ADD COLUMN app_version TEXT;");
    zzbpo.put("app_store", "ALTER TABLE apps ADD COLUMN app_store TEXT;");
    zzbpo.put("gmp_version", "ALTER TABLE apps ADD COLUMN gmp_version INTEGER;");
    zzbpo.put("dev_cert_hash", "ALTER TABLE apps ADD COLUMN dev_cert_hash INTEGER;");
    zzbpo.put("measurement_enabled", "ALTER TABLE apps ADD COLUMN measurement_enabled INTEGER;");
    zzbpo.put("last_bundle_start_timestamp", "ALTER TABLE apps ADD COLUMN last_bundle_start_timestamp INTEGER;");
    zzbpo.put("day", "ALTER TABLE apps ADD COLUMN day INTEGER;");
    zzbpo.put("daily_public_events_count", "ALTER TABLE apps ADD COLUMN daily_public_events_count INTEGER;");
    zzbpo.put("daily_events_count", "ALTER TABLE apps ADD COLUMN daily_events_count INTEGER;");
    zzbpo.put("daily_conversions_count", "ALTER TABLE apps ADD COLUMN daily_conversions_count INTEGER;");
    zzbpo.put("remote_config", "ALTER TABLE apps ADD COLUMN remote_config BLOB;");
    zzbpo.put("config_fetched_time", "ALTER TABLE apps ADD COLUMN config_fetched_time INTEGER;");
    zzbpo.put("failed_config_fetch_time", "ALTER TABLE apps ADD COLUMN failed_config_fetch_time INTEGER;");
    zzbpo.put("app_version_int", "ALTER TABLE apps ADD COLUMN app_version_int INTEGER;");
    zzbpo.put("firebase_instance_id", "ALTER TABLE apps ADD COLUMN firebase_instance_id TEXT;");
    zzbpo.put("daily_error_events_count", "ALTER TABLE apps ADD COLUMN daily_error_events_count INTEGER;");
    zzbpo.put("daily_realtime_events_count", "ALTER TABLE apps ADD COLUMN daily_realtime_events_count INTEGER;");
    zzbpo.put("health_monitor_sample", "ALTER TABLE apps ADD COLUMN health_monitor_sample TEXT;");
    zzbpo.put("android_id", "ALTER TABLE apps ADD COLUMN android_id INTEGER;");
    localArrayMap = new ArrayMap(1);
    zzbpp = localArrayMap;
    localArrayMap.put("realtime", "ALTER TABLE raw_events ADD COLUMN realtime INTEGER;");
    localArrayMap = new ArrayMap(1);
    zzbpq = localArrayMap;
    localArrayMap.put("has_realtime", "ALTER TABLE queue ADD COLUMN has_realtime INTEGER;");
    localArrayMap = new ArrayMap(1);
    zzbpr = localArrayMap;
    localArrayMap.put("previous_install_count", "ALTER TABLE app2 ADD COLUMN previous_install_count INTEGER;");
  }
  
  zzcen(zzcgl paramzzcgl)
  {
    super(paramzzcgl);
    paramzzcgl = zzcem.zzxC();
    this.zzbps = new zzceq(this, getContext(), paramzzcgl);
  }
  
  @WorkerThread
  private final long zza(String paramString, String[] paramArrayOfString, long paramLong)
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
      zzwF().zzyx().zze("Database error", paramString, paramArrayOfString);
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
  private final Object zza(Cursor paramCursor, int paramInt)
  {
    int i = paramCursor.getType(paramInt);
    switch (i)
    {
    default: 
      zzwF().zzyx().zzj("Loaded invalid unknown value type, ignoring it", Integer.valueOf(i));
      return null;
    case 0: 
      zzwF().zzyx().log("Loaded invalid null value from database");
      return null;
    case 1: 
      return Long.valueOf(paramCursor.getLong(paramInt));
    case 2: 
      return Double.valueOf(paramCursor.getDouble(paramInt));
    case 3: 
      return paramCursor.getString(paramInt);
    }
    zzwF().zzyx().log("Loaded invalid blob type value, ignoring it");
    return null;
  }
  
  @WorkerThread
  private static void zza(ContentValues paramContentValues, String paramString, Object paramObject)
  {
    zzbo.zzcF(paramString);
    zzbo.zzu(paramObject);
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
  
  static void zza(zzcfl paramzzcfl, SQLiteDatabase paramSQLiteDatabase)
  {
    if (paramzzcfl == null) {
      throw new IllegalArgumentException("Monitor must not be null");
    }
    paramSQLiteDatabase = new File(paramSQLiteDatabase.getPath());
    if (!paramSQLiteDatabase.setReadable(false, false)) {
      paramzzcfl.zzyz().log("Failed to turn off database read permission");
    }
    if (!paramSQLiteDatabase.setWritable(false, false)) {
      paramzzcfl.zzyz().log("Failed to turn off database write permission");
    }
    if (!paramSQLiteDatabase.setReadable(true, true)) {
      paramzzcfl.zzyz().log("Failed to turn on database read permission for owner");
    }
    if (!paramSQLiteDatabase.setWritable(true, true)) {
      paramzzcfl.zzyz().log("Failed to turn on database write permission for owner");
    }
  }
  
  @WorkerThread
  static void zza(zzcfl paramzzcfl, SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, String paramString3, Map<String, String> paramMap)
    throws SQLiteException
  {
    if (paramzzcfl == null) {
      throw new IllegalArgumentException("Monitor must not be null");
    }
    if (!zza(paramzzcfl, paramSQLiteDatabase, paramString1)) {
      paramSQLiteDatabase.execSQL(paramString2);
    }
    try
    {
      zza(paramzzcfl, paramSQLiteDatabase, paramString1, paramString3, paramMap);
      return;
    }
    catch (SQLiteException paramSQLiteDatabase)
    {
      paramzzcfl.zzyx().zzj("Failed to verify columns on table that was just created", paramString1);
      throw paramSQLiteDatabase;
    }
  }
  
  @WorkerThread
  private static void zza(zzcfl paramzzcfl, SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, Map<String, String> paramMap)
    throws SQLiteException
  {
    if (paramzzcfl == null) {
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
      paramzzcfl.zzyz().zze("Table has extra columns. table, columns", paramString1, TextUtils.join(", ", localSet));
    }
  }
  
  /* Error */
  @WorkerThread
  private static boolean zza(zzcfl paramzzcfl, SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: ifnonnull +14 -> 18
    //   7: new 280	java/lang/IllegalArgumentException
    //   10: dup
    //   11: ldc_w 287
    //   14: invokespecial 284	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   17: athrow
    //   18: aload_1
    //   19: ldc_w 405
    //   22: iconst_1
    //   23: anewarray 267	java/lang/String
    //   26: dup
    //   27: iconst_0
    //   28: ldc_w 407
    //   31: aastore
    //   32: ldc_w 409
    //   35: iconst_1
    //   36: anewarray 267	java/lang/String
    //   39: dup
    //   40: iconst_0
    //   41: aload_2
    //   42: aastore
    //   43: aconst_null
    //   44: aconst_null
    //   45: aconst_null
    //   46: invokevirtual 413	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   49: astore_1
    //   50: aload_1
    //   51: astore 4
    //   53: aload 4
    //   55: astore_1
    //   56: aload 4
    //   58: invokeinterface 184 1 0
    //   63: istore_3
    //   64: aload 4
    //   66: ifnull +10 -> 76
    //   69: aload 4
    //   71: invokeinterface 191 1 0
    //   76: iload_3
    //   77: ireturn
    //   78: astore 5
    //   80: aconst_null
    //   81: astore 4
    //   83: aload 4
    //   85: astore_1
    //   86: aload_0
    //   87: invokevirtual 300	com/google/android/gms/internal/zzcfl:zzyz	()Lcom/google/android/gms/internal/zzcfn;
    //   90: ldc_w 415
    //   93: aload_2
    //   94: aload 5
    //   96: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   99: aload 4
    //   101: ifnull +10 -> 111
    //   104: aload 4
    //   106: invokeinterface 191 1 0
    //   111: iconst_0
    //   112: ireturn
    //   113: astore_0
    //   114: aload 4
    //   116: astore_1
    //   117: aload_1
    //   118: ifnull +9 -> 127
    //   121: aload_1
    //   122: invokeinterface 191 1 0
    //   127: aload_0
    //   128: athrow
    //   129: astore_0
    //   130: goto -13 -> 117
    //   133: astore 5
    //   135: goto -52 -> 83
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	138	0	paramzzcfl	zzcfl
    //   0	138	1	paramSQLiteDatabase	SQLiteDatabase
    //   0	138	2	paramString	String
    //   63	14	3	bool	boolean
    //   1	114	4	localSQLiteDatabase	SQLiteDatabase
    //   78	17	5	localSQLiteException1	SQLiteException
    //   133	1	5	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   18	50	78	android/database/sqlite/SQLiteException
    //   18	50	113	finally
    //   56	64	129	finally
    //   86	99	129	finally
    //   56	64	133	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  private final boolean zza(String paramString, int paramInt, zzcjn paramzzcjn)
  {
    zzkD();
    zzjC();
    zzbo.zzcF(paramString);
    zzbo.zzu(paramzzcjn);
    if (TextUtils.isEmpty(paramzzcjn.zzbuN))
    {
      zzwF().zzyz().zzd("Event filter had no event name. Audience definition ignored. appId, audienceId, filterId", zzcfl.zzdZ(paramString), Integer.valueOf(paramInt), String.valueOf(paramzzcjn.zzbuM));
      return false;
    }
    try
    {
      byte[] arrayOfByte = new byte[paramzzcjn.zzLV()];
      Object localObject = adh.zzc(arrayOfByte, 0, arrayOfByte.length);
      paramzzcjn.zza((adh)localObject);
      ((adh)localObject).zzLM();
      localObject = new ContentValues();
      ((ContentValues)localObject).put("app_id", paramString);
      ((ContentValues)localObject).put("audience_id", Integer.valueOf(paramInt));
      ((ContentValues)localObject).put("filter_id", paramzzcjn.zzbuM);
      ((ContentValues)localObject).put("event_name", paramzzcjn.zzbuN);
      ((ContentValues)localObject).put("data", arrayOfByte);
      return false;
    }
    catch (IOException paramzzcjn)
    {
      try
      {
        if (getWritableDatabase().insertWithOnConflict("event_filters", null, (ContentValues)localObject, 5) == -1L) {
          zzwF().zzyx().zzj("Failed to insert event filter (got -1). appId", zzcfl.zzdZ(paramString));
        }
        return true;
      }
      catch (SQLiteException paramzzcjn)
      {
        zzwF().zzyx().zze("Error storing event filter. appId", zzcfl.zzdZ(paramString), paramzzcjn);
      }
      paramzzcjn = paramzzcjn;
      zzwF().zzyx().zze("Configuration loss. Failed to serialize event filter. appId", zzcfl.zzdZ(paramString), paramzzcjn);
      return false;
    }
  }
  
  @WorkerThread
  private final boolean zza(String paramString, int paramInt, zzcjq paramzzcjq)
  {
    zzkD();
    zzjC();
    zzbo.zzcF(paramString);
    zzbo.zzu(paramzzcjq);
    if (TextUtils.isEmpty(paramzzcjq.zzbvc))
    {
      zzwF().zzyz().zzd("Property filter had no property name. Audience definition ignored. appId, audienceId, filterId", zzcfl.zzdZ(paramString), Integer.valueOf(paramInt), String.valueOf(paramzzcjq.zzbuM));
      return false;
    }
    try
    {
      byte[] arrayOfByte = new byte[paramzzcjq.zzLV()];
      Object localObject = adh.zzc(arrayOfByte, 0, arrayOfByte.length);
      paramzzcjq.zza((adh)localObject);
      ((adh)localObject).zzLM();
      localObject = new ContentValues();
      ((ContentValues)localObject).put("app_id", paramString);
      ((ContentValues)localObject).put("audience_id", Integer.valueOf(paramInt));
      ((ContentValues)localObject).put("filter_id", paramzzcjq.zzbuM);
      ((ContentValues)localObject).put("property_name", paramzzcjq.zzbvc);
      ((ContentValues)localObject).put("data", arrayOfByte);
      try
      {
        if (getWritableDatabase().insertWithOnConflict("property_filters", null, (ContentValues)localObject, 5) == -1L)
        {
          zzwF().zzyx().zzj("Failed to insert property filter (got -1). appId", zzcfl.zzdZ(paramString));
          return false;
        }
      }
      catch (SQLiteException paramzzcjq)
      {
        zzwF().zzyx().zze("Error storing property filter. appId", zzcfl.zzdZ(paramString), paramzzcjq);
        return false;
      }
      return true;
    }
    catch (IOException paramzzcjq)
    {
      zzwF().zzyx().zze("Configuration loss. Failed to serialize property filter. appId", zzcfl.zzdZ(paramString), paramzzcjq);
      return false;
    }
  }
  
  @WorkerThread
  private final long zzb(String paramString, String[] paramArrayOfString)
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
      zzwF().zzyx().zze("Database error", paramString, paramArrayOfString);
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
  private static Set<String> zzb(SQLiteDatabase paramSQLiteDatabase, String paramString)
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
  
  private final boolean zzc(String paramString, List<Integer> paramList)
  {
    zzbo.zzcF(paramString);
    zzkD();
    zzjC();
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    int j;
    label160:
    do
    {
      try
      {
        long l = zzb("select count(1) from audience_filter_values where app_id=?", new String[] { paramString });
        j = Math.max(0, Math.min(2000, zzwH().zzb(paramString, zzcfb.zzbqA)));
        if (l <= j) {
          return false;
        }
      }
      catch (SQLiteException paramList)
      {
        zzwF().zzyx().zze("Database error querying filters. appId", zzcfl.zzdZ(paramString), paramList);
        return false;
      }
      ArrayList localArrayList = new ArrayList();
      int i = 0;
      for (;;)
      {
        if (i >= paramList.size()) {
          break label160;
        }
        Integer localInteger = (Integer)paramList.get(i);
        if ((localInteger == null) || (!(localInteger instanceof Integer))) {
          break;
        }
        localArrayList.add(Integer.toString(localInteger.intValue()));
        i += 1;
      }
      paramList = String.valueOf(TextUtils.join(",", localArrayList));
      paramList = String.valueOf(paramList).length() + 2 + "(" + paramList + ")";
    } while (localSQLiteDatabase.delete("audience_filter_values", String.valueOf(paramList).length() + 140 + "audience_id in (select audience_id from audience_filter_values where app_id=? and audience_id not in " + paramList + " order by rowid desc limit -1 offset ?)", new String[] { paramString, Integer.toString(j) }) <= 0);
    return true;
  }
  
  private final boolean zzyk()
  {
    return getContext().getDatabasePath(zzcem.zzxC()).exists();
  }
  
  @WorkerThread
  public final void beginTransaction()
  {
    zzkD();
    getWritableDatabase().beginTransaction();
  }
  
  @WorkerThread
  public final void endTransaction()
  {
    zzkD();
    getWritableDatabase().endTransaction();
  }
  
  @WorkerThread
  final SQLiteDatabase getWritableDatabase()
  {
    zzjC();
    try
    {
      SQLiteDatabase localSQLiteDatabase = this.zzbps.getWritableDatabase();
      return localSQLiteDatabase;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzwF().zzyz().zzj("Error opening database", localSQLiteException);
      throw localSQLiteException;
    }
  }
  
  @WorkerThread
  public final void setTransactionSuccessful()
  {
    zzkD();
    getWritableDatabase().setTransactionSuccessful();
  }
  
  /* Error */
  @WorkerThread
  public final zzcev zzE(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aload_1
    //   4: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_2
    //   9: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 424	com/google/android/gms/internal/zzcen:zzjC	()V
    //   17: aload_0
    //   18: invokevirtual 421	com/google/android/gms/internal/zzcen:zzkD	()V
    //   21: aload_0
    //   22: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   25: ldc_w 632
    //   28: iconst_3
    //   29: anewarray 267	java/lang/String
    //   32: dup
    //   33: iconst_0
    //   34: ldc_w 634
    //   37: aastore
    //   38: dup
    //   39: iconst_1
    //   40: ldc_w 636
    //   43: aastore
    //   44: dup
    //   45: iconst_2
    //   46: ldc_w 638
    //   49: aastore
    //   50: ldc_w 640
    //   53: iconst_2
    //   54: anewarray 267	java/lang/String
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
    //   68: invokevirtual 413	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   71: astore 4
    //   73: aload 4
    //   75: invokeinterface 184 1 0
    //   80: istore_3
    //   81: iload_3
    //   82: ifne +19 -> 101
    //   85: aload 4
    //   87: ifnull +10 -> 97
    //   90: aload 4
    //   92: invokeinterface 191 1 0
    //   97: aconst_null
    //   98: astore_1
    //   99: aload_1
    //   100: areturn
    //   101: new 642	com/google/android/gms/internal/zzcev
    //   104: dup
    //   105: aload_1
    //   106: aload_2
    //   107: aload 4
    //   109: iconst_0
    //   110: invokeinterface 188 2 0
    //   115: aload 4
    //   117: iconst_1
    //   118: invokeinterface 188 2 0
    //   123: aload 4
    //   125: iconst_2
    //   126: invokeinterface 188 2 0
    //   131: invokespecial 645	com/google/android/gms/internal/zzcev:<init>	(Ljava/lang/String;Ljava/lang/String;JJJ)V
    //   134: astore 5
    //   136: aload 4
    //   138: invokeinterface 648 1 0
    //   143: ifeq +20 -> 163
    //   146: aload_0
    //   147: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   150: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   153: ldc_w 650
    //   156: aload_1
    //   157: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   160: invokevirtual 228	com/google/android/gms/internal/zzcfn:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   163: aload 5
    //   165: astore_1
    //   166: aload 4
    //   168: ifnull -69 -> 99
    //   171: aload 4
    //   173: invokeinterface 191 1 0
    //   178: aload 5
    //   180: areturn
    //   181: astore 5
    //   183: aconst_null
    //   184: astore 4
    //   186: aload_0
    //   187: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   190: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   193: ldc_w 652
    //   196: aload_1
    //   197: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   200: aload_0
    //   201: invokevirtual 656	com/google/android/gms/internal/zzcen:zzwA	()Lcom/google/android/gms/internal/zzcfj;
    //   204: aload_2
    //   205: invokevirtual 661	com/google/android/gms/internal/zzcfj:zzdW	(Ljava/lang/String;)Ljava/lang/String;
    //   208: aload 5
    //   210: invokevirtual 447	com/google/android/gms/internal/zzcfn:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   213: aload 4
    //   215: ifnull +10 -> 225
    //   218: aload 4
    //   220: invokeinterface 191 1 0
    //   225: aconst_null
    //   226: areturn
    //   227: astore_1
    //   228: aload 5
    //   230: astore_2
    //   231: aload_2
    //   232: ifnull +9 -> 241
    //   235: aload_2
    //   236: invokeinterface 191 1 0
    //   241: aload_1
    //   242: athrow
    //   243: astore_1
    //   244: aload 4
    //   246: astore_2
    //   247: goto -16 -> 231
    //   250: astore_1
    //   251: aload 4
    //   253: astore_2
    //   254: goto -23 -> 231
    //   257: astore 5
    //   259: goto -73 -> 186
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	262	0	this	zzcen
    //   0	262	1	paramString1	String
    //   0	262	2	paramString2	String
    //   80	2	3	bool	boolean
    //   71	181	4	localCursor	Cursor
    //   1	178	5	localzzcev	zzcev
    //   181	48	5	localSQLiteException1	SQLiteException
    //   257	1	5	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   21	73	181	android/database/sqlite/SQLiteException
    //   21	73	227	finally
    //   73	81	243	finally
    //   101	163	243	finally
    //   186	213	250	finally
    //   73	81	257	android/database/sqlite/SQLiteException
    //   101	163	257	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  public final void zzF(String paramString1, String paramString2)
  {
    zzbo.zzcF(paramString1);
    zzbo.zzcF(paramString2);
    zzjC();
    zzkD();
    try
    {
      int i = getWritableDatabase().delete("user_attributes", "app_id=? and name=?", new String[] { paramString1, paramString2 });
      zzwF().zzyD().zzj("Deleted user attribute rows", Integer.valueOf(i));
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzwF().zzyx().zzd("Error deleting user attribute. appId", zzcfl.zzdZ(paramString1), zzwA().zzdY(paramString2), localSQLiteException);
    }
  }
  
  /* Error */
  @WorkerThread
  public final zzcjk zzG(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aload_1
    //   4: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_2
    //   9: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 424	com/google/android/gms/internal/zzcen:zzjC	()V
    //   17: aload_0
    //   18: invokevirtual 421	com/google/android/gms/internal/zzcen:zzkD	()V
    //   21: aload_0
    //   22: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   25: ldc_w 664
    //   28: iconst_3
    //   29: anewarray 267	java/lang/String
    //   32: dup
    //   33: iconst_0
    //   34: ldc_w 678
    //   37: aastore
    //   38: dup
    //   39: iconst_1
    //   40: ldc_w 680
    //   43: aastore
    //   44: dup
    //   45: iconst_2
    //   46: ldc 27
    //   48: aastore
    //   49: ldc_w 640
    //   52: iconst_2
    //   53: anewarray 267	java/lang/String
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
    //   67: invokevirtual 413	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   70: astore 6
    //   72: aload 6
    //   74: invokeinterface 184 1 0
    //   79: istore_3
    //   80: iload_3
    //   81: ifne +19 -> 100
    //   84: aload 6
    //   86: ifnull +10 -> 96
    //   89: aload 6
    //   91: invokeinterface 191 1 0
    //   96: aconst_null
    //   97: astore_1
    //   98: aload_1
    //   99: areturn
    //   100: aload 6
    //   102: iconst_0
    //   103: invokeinterface 188 2 0
    //   108: lstore 4
    //   110: aload_0
    //   111: aload 6
    //   113: iconst_1
    //   114: invokespecial 682	com/google/android/gms/internal/zzcen:zza	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   117: astore 7
    //   119: new 684	com/google/android/gms/internal/zzcjk
    //   122: dup
    //   123: aload_1
    //   124: aload 6
    //   126: iconst_2
    //   127: invokeinterface 252 2 0
    //   132: aload_2
    //   133: lload 4
    //   135: aload 7
    //   137: invokespecial 687	com/google/android/gms/internal/zzcjk:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   140: astore 7
    //   142: aload 6
    //   144: invokeinterface 648 1 0
    //   149: ifeq +20 -> 169
    //   152: aload_0
    //   153: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   156: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   159: ldc_w 689
    //   162: aload_1
    //   163: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   166: invokevirtual 228	com/google/android/gms/internal/zzcfn:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   169: aload 7
    //   171: astore_1
    //   172: aload 6
    //   174: ifnull -76 -> 98
    //   177: aload 6
    //   179: invokeinterface 191 1 0
    //   184: aload 7
    //   186: areturn
    //   187: astore 7
    //   189: aconst_null
    //   190: astore 6
    //   192: aload_0
    //   193: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   196: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   199: ldc_w 691
    //   202: aload_1
    //   203: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   206: aload_0
    //   207: invokevirtual 656	com/google/android/gms/internal/zzcen:zzwA	()Lcom/google/android/gms/internal/zzcfj;
    //   210: aload_2
    //   211: invokevirtual 674	com/google/android/gms/internal/zzcfj:zzdY	(Ljava/lang/String;)Ljava/lang/String;
    //   214: aload 7
    //   216: invokevirtual 447	com/google/android/gms/internal/zzcfn:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   219: aload 6
    //   221: ifnull +10 -> 231
    //   224: aload 6
    //   226: invokeinterface 191 1 0
    //   231: aconst_null
    //   232: areturn
    //   233: astore_1
    //   234: aload 7
    //   236: astore_2
    //   237: aload_2
    //   238: ifnull +9 -> 247
    //   241: aload_2
    //   242: invokeinterface 191 1 0
    //   247: aload_1
    //   248: athrow
    //   249: astore_1
    //   250: aload 6
    //   252: astore_2
    //   253: goto -16 -> 237
    //   256: astore_1
    //   257: aload 6
    //   259: astore_2
    //   260: goto -23 -> 237
    //   263: astore 7
    //   265: goto -73 -> 192
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	268	0	this	zzcen
    //   0	268	1	paramString1	String
    //   0	268	2	paramString2	String
    //   79	2	3	bool	boolean
    //   108	26	4	l	long
    //   70	188	6	localCursor	Cursor
    //   1	184	7	localObject	Object
    //   187	48	7	localSQLiteException1	SQLiteException
    //   263	1	7	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   21	72	187	android/database/sqlite/SQLiteException
    //   21	72	233	finally
    //   72	80	249	finally
    //   100	169	249	finally
    //   192	219	256	finally
    //   72	80	263	android/database/sqlite/SQLiteException
    //   100	169	263	android/database/sqlite/SQLiteException
  }
  
  public final void zzG(List<Long> paramList)
  {
    zzbo.zzu(paramList);
    zzjC();
    zzkD();
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
      zzwF().zzyx().zze("Deleted fewer rows from raw events table than expected", Integer.valueOf(i), Integer.valueOf(paramList.size()));
    }
  }
  
  /* Error */
  @WorkerThread
  public final zzcek zzH(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_2
    //   6: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   9: pop
    //   10: aload_0
    //   11: invokevirtual 424	com/google/android/gms/internal/zzcen:zzjC	()V
    //   14: aload_0
    //   15: invokevirtual 421	com/google/android/gms/internal/zzcen:zzkD	()V
    //   18: aload_0
    //   19: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   22: ldc_w 711
    //   25: bipush 11
    //   27: anewarray 267	java/lang/String
    //   30: dup
    //   31: iconst_0
    //   32: ldc 27
    //   34: aastore
    //   35: dup
    //   36: iconst_1
    //   37: ldc_w 680
    //   40: aastore
    //   41: dup
    //   42: iconst_2
    //   43: ldc_w 713
    //   46: aastore
    //   47: dup
    //   48: iconst_3
    //   49: ldc_w 715
    //   52: aastore
    //   53: dup
    //   54: iconst_4
    //   55: ldc_w 717
    //   58: aastore
    //   59: dup
    //   60: iconst_5
    //   61: ldc_w 719
    //   64: aastore
    //   65: dup
    //   66: bipush 6
    //   68: ldc_w 721
    //   71: aastore
    //   72: dup
    //   73: bipush 7
    //   75: ldc_w 723
    //   78: aastore
    //   79: dup
    //   80: bipush 8
    //   82: ldc_w 725
    //   85: aastore
    //   86: dup
    //   87: bipush 9
    //   89: ldc_w 727
    //   92: aastore
    //   93: dup
    //   94: bipush 10
    //   96: ldc_w 729
    //   99: aastore
    //   100: ldc_w 640
    //   103: iconst_2
    //   104: anewarray 267	java/lang/String
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
    //   118: invokevirtual 413	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   121: astore 12
    //   123: aload 12
    //   125: invokeinterface 184 1 0
    //   130: istore_3
    //   131: iload_3
    //   132: ifne +19 -> 151
    //   135: aload 12
    //   137: ifnull +10 -> 147
    //   140: aload 12
    //   142: invokeinterface 191 1 0
    //   147: aconst_null
    //   148: astore_1
    //   149: aload_1
    //   150: areturn
    //   151: aload 12
    //   153: iconst_0
    //   154: invokeinterface 252 2 0
    //   159: astore 13
    //   161: aload_0
    //   162: aload 12
    //   164: iconst_1
    //   165: invokespecial 682	com/google/android/gms/internal/zzcen:zza	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   168: astore 14
    //   170: aload 12
    //   172: iconst_2
    //   173: invokeinterface 732 2 0
    //   178: ifeq +223 -> 401
    //   181: iconst_1
    //   182: istore_3
    //   183: aload 12
    //   185: iconst_3
    //   186: invokeinterface 252 2 0
    //   191: astore 15
    //   193: aload 12
    //   195: iconst_4
    //   196: invokeinterface 188 2 0
    //   201: lstore 4
    //   203: aload_0
    //   204: invokevirtual 736	com/google/android/gms/internal/zzcen:zzwB	()Lcom/google/android/gms/internal/zzcjl;
    //   207: aload 12
    //   209: iconst_5
    //   210: invokeinterface 740 2 0
    //   215: getstatic 746	com/google/android/gms/internal/zzcez:CREATOR	Landroid/os/Parcelable$Creator;
    //   218: invokevirtual 751	com/google/android/gms/internal/zzcjl:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   221: checkcast 742	com/google/android/gms/internal/zzcez
    //   224: astore 16
    //   226: aload 12
    //   228: bipush 6
    //   230: invokeinterface 188 2 0
    //   235: lstore 6
    //   237: aload_0
    //   238: invokevirtual 736	com/google/android/gms/internal/zzcen:zzwB	()Lcom/google/android/gms/internal/zzcjl;
    //   241: aload 12
    //   243: bipush 7
    //   245: invokeinterface 740 2 0
    //   250: getstatic 746	com/google/android/gms/internal/zzcez:CREATOR	Landroid/os/Parcelable$Creator;
    //   253: invokevirtual 751	com/google/android/gms/internal/zzcjl:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   256: checkcast 742	com/google/android/gms/internal/zzcez
    //   259: astore 17
    //   261: aload 12
    //   263: bipush 8
    //   265: invokeinterface 188 2 0
    //   270: lstore 8
    //   272: aload 12
    //   274: bipush 9
    //   276: invokeinterface 188 2 0
    //   281: lstore 10
    //   283: aload_0
    //   284: invokevirtual 736	com/google/android/gms/internal/zzcen:zzwB	()Lcom/google/android/gms/internal/zzcjl;
    //   287: aload 12
    //   289: bipush 10
    //   291: invokeinterface 740 2 0
    //   296: getstatic 746	com/google/android/gms/internal/zzcez:CREATOR	Landroid/os/Parcelable$Creator;
    //   299: invokevirtual 751	com/google/android/gms/internal/zzcjl:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   302: checkcast 742	com/google/android/gms/internal/zzcez
    //   305: astore 18
    //   307: new 753	com/google/android/gms/internal/zzcek
    //   310: dup
    //   311: aload_1
    //   312: aload 13
    //   314: new 755	com/google/android/gms/internal/zzcji
    //   317: dup
    //   318: aload_2
    //   319: lload 8
    //   321: aload 14
    //   323: aload 13
    //   325: invokespecial 758	com/google/android/gms/internal/zzcji:<init>	(Ljava/lang/String;JLjava/lang/Object;Ljava/lang/String;)V
    //   328: lload 6
    //   330: iload_3
    //   331: aload 15
    //   333: aload 16
    //   335: lload 4
    //   337: aload 17
    //   339: lload 10
    //   341: aload 18
    //   343: invokespecial 761	com/google/android/gms/internal/zzcek:<init>	(Ljava/lang/String;Ljava/lang/String;Lcom/google/android/gms/internal/zzcji;JZLjava/lang/String;Lcom/google/android/gms/internal/zzcez;JLcom/google/android/gms/internal/zzcez;JLcom/google/android/gms/internal/zzcez;)V
    //   346: astore 13
    //   348: aload 12
    //   350: invokeinterface 648 1 0
    //   355: ifeq +28 -> 383
    //   358: aload_0
    //   359: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   362: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   365: ldc_w 763
    //   368: aload_1
    //   369: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   372: aload_0
    //   373: invokevirtual 656	com/google/android/gms/internal/zzcen:zzwA	()Lcom/google/android/gms/internal/zzcfj;
    //   376: aload_2
    //   377: invokevirtual 674	com/google/android/gms/internal/zzcfj:zzdY	(Ljava/lang/String;)Ljava/lang/String;
    //   380: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   383: aload 13
    //   385: astore_1
    //   386: aload 12
    //   388: ifnull -239 -> 149
    //   391: aload 12
    //   393: invokeinterface 191 1 0
    //   398: aload 13
    //   400: areturn
    //   401: iconst_0
    //   402: istore_3
    //   403: goto -220 -> 183
    //   406: astore 13
    //   408: aconst_null
    //   409: astore 12
    //   411: aload_0
    //   412: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   415: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   418: ldc_w 765
    //   421: aload_1
    //   422: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   425: aload_0
    //   426: invokevirtual 656	com/google/android/gms/internal/zzcen:zzwA	()Lcom/google/android/gms/internal/zzcfj;
    //   429: aload_2
    //   430: invokevirtual 674	com/google/android/gms/internal/zzcfj:zzdY	(Ljava/lang/String;)Ljava/lang/String;
    //   433: aload 13
    //   435: invokevirtual 447	com/google/android/gms/internal/zzcfn:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   438: aload 12
    //   440: ifnull +10 -> 450
    //   443: aload 12
    //   445: invokeinterface 191 1 0
    //   450: aconst_null
    //   451: areturn
    //   452: astore_1
    //   453: aconst_null
    //   454: astore 12
    //   456: aload 12
    //   458: ifnull +10 -> 468
    //   461: aload 12
    //   463: invokeinterface 191 1 0
    //   468: aload_1
    //   469: athrow
    //   470: astore_1
    //   471: goto -15 -> 456
    //   474: astore_1
    //   475: goto -19 -> 456
    //   478: astore 13
    //   480: goto -69 -> 411
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	483	0	this	zzcen
    //   0	483	1	paramString1	String
    //   0	483	2	paramString2	String
    //   130	273	3	bool	boolean
    //   201	135	4	l1	long
    //   235	94	6	l2	long
    //   270	50	8	l3	long
    //   281	59	10	l4	long
    //   121	341	12	localCursor	Cursor
    //   159	240	13	localObject1	Object
    //   406	28	13	localSQLiteException1	SQLiteException
    //   478	1	13	localSQLiteException2	SQLiteException
    //   168	154	14	localObject2	Object
    //   191	141	15	str	String
    //   224	110	16	localzzcez1	zzcez
    //   259	79	17	localzzcez2	zzcez
    //   305	37	18	localzzcez3	zzcez
    // Exception table:
    //   from	to	target	type
    //   18	123	406	android/database/sqlite/SQLiteException
    //   18	123	452	finally
    //   123	131	470	finally
    //   151	181	470	finally
    //   183	383	470	finally
    //   411	438	474	finally
    //   123	131	478	android/database/sqlite/SQLiteException
    //   151	181	478	android/database/sqlite/SQLiteException
    //   183	383	478	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  public final int zzI(String paramString1, String paramString2)
  {
    zzbo.zzcF(paramString1);
    zzbo.zzcF(paramString2);
    zzjC();
    zzkD();
    try
    {
      int i = getWritableDatabase().delete("conditional_properties", "app_id=? and name=?", new String[] { paramString1, paramString2 });
      return i;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzwF().zzyx().zzd("Error deleting conditional property", zzcfl.zzdZ(paramString1), zzwA().zzdY(paramString2), localSQLiteException);
    }
    return 0;
  }
  
  /* Error */
  final Map<Integer, List<zzcjn>> zzJ(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 421	com/google/android/gms/internal/zzcen:zzkD	()V
    //   4: aload_0
    //   5: invokevirtual 424	com/google/android/gms/internal/zzcen:zzjC	()V
    //   8: aload_1
    //   9: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_2
    //   14: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   17: pop
    //   18: new 19	android/support/v4/util/ArrayMap
    //   21: dup
    //   22: invokespecial 772	android/support/v4/util/ArrayMap:<init>	()V
    //   25: astore 8
    //   27: aload_0
    //   28: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   31: astore 5
    //   33: aload 5
    //   35: ldc_w 482
    //   38: iconst_2
    //   39: anewarray 267	java/lang/String
    //   42: dup
    //   43: iconst_0
    //   44: ldc_w 468
    //   47: aastore
    //   48: dup
    //   49: iconst_1
    //   50: ldc_w 477
    //   53: aastore
    //   54: ldc_w 774
    //   57: iconst_2
    //   58: anewarray 267	java/lang/String
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
    //   72: invokevirtual 413	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   75: astore 5
    //   77: aload 5
    //   79: astore_2
    //   80: aload 5
    //   82: invokeinterface 184 1 0
    //   87: ifne +26 -> 113
    //   90: aload 5
    //   92: astore_2
    //   93: invokestatic 777	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   96: astore 6
    //   98: aload 5
    //   100: ifnull +10 -> 110
    //   103: aload 5
    //   105: invokeinterface 191 1 0
    //   110: aload 6
    //   112: areturn
    //   113: aload 5
    //   115: astore_2
    //   116: aload 5
    //   118: iconst_1
    //   119: invokeinterface 740 2 0
    //   124: astore 6
    //   126: aload 5
    //   128: astore_2
    //   129: aload 6
    //   131: iconst_0
    //   132: aload 6
    //   134: arraylength
    //   135: invokestatic 782	com/google/android/gms/internal/adg:zzb	([BII)Lcom/google/android/gms/internal/adg;
    //   138: astore 6
    //   140: aload 5
    //   142: astore_2
    //   143: new 426	com/google/android/gms/internal/zzcjn
    //   146: dup
    //   147: invokespecial 783	com/google/android/gms/internal/zzcjn:<init>	()V
    //   150: astore 9
    //   152: aload 5
    //   154: astore_2
    //   155: aload 9
    //   157: aload 6
    //   159: invokevirtual 786	com/google/android/gms/internal/zzcjn:zza	(Lcom/google/android/gms/internal/adg;)Lcom/google/android/gms/internal/adp;
    //   162: pop
    //   163: aload 5
    //   165: astore_2
    //   166: aload 5
    //   168: iconst_0
    //   169: invokeinterface 732 2 0
    //   174: istore_3
    //   175: aload 5
    //   177: astore_2
    //   178: aload 8
    //   180: iload_3
    //   181: invokestatic 224	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   184: invokeinterface 788 2 0
    //   189: checkcast 570	java/util/List
    //   192: astore 7
    //   194: aload 7
    //   196: astore 6
    //   198: aload 7
    //   200: ifnonnull +32 -> 232
    //   203: aload 5
    //   205: astore_2
    //   206: new 567	java/util/ArrayList
    //   209: dup
    //   210: invokespecial 568	java/util/ArrayList:<init>	()V
    //   213: astore 6
    //   215: aload 5
    //   217: astore_2
    //   218: aload 8
    //   220: iload_3
    //   221: invokestatic 224	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   224: aload 6
    //   226: invokeinterface 35 3 0
    //   231: pop
    //   232: aload 5
    //   234: astore_2
    //   235: aload 6
    //   237: aload 9
    //   239: invokeinterface 585 2 0
    //   244: pop
    //   245: aload 5
    //   247: astore_2
    //   248: aload 5
    //   250: invokeinterface 648 1 0
    //   255: istore 4
    //   257: iload 4
    //   259: ifne -146 -> 113
    //   262: aload 5
    //   264: ifnull +10 -> 274
    //   267: aload 5
    //   269: invokeinterface 191 1 0
    //   274: aload 8
    //   276: areturn
    //   277: astore 6
    //   279: aload 5
    //   281: astore_2
    //   282: aload_0
    //   283: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   286: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   289: ldc_w 790
    //   292: aload_1
    //   293: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   296: aload 6
    //   298: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   301: goto -56 -> 245
    //   304: astore 6
    //   306: aload 5
    //   308: astore_2
    //   309: aload_0
    //   310: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   313: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   316: ldc_w 565
    //   319: aload_1
    //   320: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   323: aload 6
    //   325: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   328: aload 5
    //   330: ifnull +10 -> 340
    //   333: aload 5
    //   335: invokeinterface 191 1 0
    //   340: aconst_null
    //   341: areturn
    //   342: astore_1
    //   343: aconst_null
    //   344: astore_2
    //   345: aload_2
    //   346: ifnull +9 -> 355
    //   349: aload_2
    //   350: invokeinterface 191 1 0
    //   355: aload_1
    //   356: athrow
    //   357: astore_1
    //   358: goto -13 -> 345
    //   361: astore 6
    //   363: aconst_null
    //   364: astore 5
    //   366: goto -60 -> 306
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	369	0	this	zzcen
    //   0	369	1	paramString1	String
    //   0	369	2	paramString2	String
    //   174	47	3	i	int
    //   255	3	4	bool	boolean
    //   31	334	5	localObject1	Object
    //   96	140	6	localObject2	Object
    //   277	20	6	localIOException	IOException
    //   304	20	6	localSQLiteException1	SQLiteException
    //   361	1	6	localSQLiteException2	SQLiteException
    //   192	7	7	localList	List
    //   25	250	8	localArrayMap	ArrayMap
    //   150	88	9	localzzcjn	zzcjn
    // Exception table:
    //   from	to	target	type
    //   155	163	277	java/io/IOException
    //   80	90	304	android/database/sqlite/SQLiteException
    //   93	98	304	android/database/sqlite/SQLiteException
    //   116	126	304	android/database/sqlite/SQLiteException
    //   129	140	304	android/database/sqlite/SQLiteException
    //   143	152	304	android/database/sqlite/SQLiteException
    //   155	163	304	android/database/sqlite/SQLiteException
    //   166	175	304	android/database/sqlite/SQLiteException
    //   178	194	304	android/database/sqlite/SQLiteException
    //   206	215	304	android/database/sqlite/SQLiteException
    //   218	232	304	android/database/sqlite/SQLiteException
    //   235	245	304	android/database/sqlite/SQLiteException
    //   248	257	304	android/database/sqlite/SQLiteException
    //   282	301	304	android/database/sqlite/SQLiteException
    //   33	77	342	finally
    //   80	90	357	finally
    //   93	98	357	finally
    //   116	126	357	finally
    //   129	140	357	finally
    //   143	152	357	finally
    //   155	163	357	finally
    //   166	175	357	finally
    //   178	194	357	finally
    //   206	215	357	finally
    //   218	232	357	finally
    //   235	245	357	finally
    //   248	257	357	finally
    //   282	301	357	finally
    //   309	328	357	finally
    //   33	77	361	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  final Map<Integer, List<zzcjq>> zzK(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 421	com/google/android/gms/internal/zzcen:zzkD	()V
    //   4: aload_0
    //   5: invokevirtual 424	com/google/android/gms/internal/zzcen:zzjC	()V
    //   8: aload_1
    //   9: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_2
    //   14: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   17: pop
    //   18: new 19	android/support/v4/util/ArrayMap
    //   21: dup
    //   22: invokespecial 772	android/support/v4/util/ArrayMap:<init>	()V
    //   25: astore 8
    //   27: aload_0
    //   28: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   31: astore 5
    //   33: aload 5
    //   35: ldc_w 509
    //   38: iconst_2
    //   39: anewarray 267	java/lang/String
    //   42: dup
    //   43: iconst_0
    //   44: ldc_w 468
    //   47: aastore
    //   48: dup
    //   49: iconst_1
    //   50: ldc_w 477
    //   53: aastore
    //   54: ldc_w 794
    //   57: iconst_2
    //   58: anewarray 267	java/lang/String
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
    //   72: invokevirtual 413	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   75: astore 5
    //   77: aload 5
    //   79: astore_2
    //   80: aload 5
    //   82: invokeinterface 184 1 0
    //   87: ifne +26 -> 113
    //   90: aload 5
    //   92: astore_2
    //   93: invokestatic 777	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   96: astore 6
    //   98: aload 5
    //   100: ifnull +10 -> 110
    //   103: aload 5
    //   105: invokeinterface 191 1 0
    //   110: aload 6
    //   112: areturn
    //   113: aload 5
    //   115: astore_2
    //   116: aload 5
    //   118: iconst_1
    //   119: invokeinterface 740 2 0
    //   124: astore 6
    //   126: aload 5
    //   128: astore_2
    //   129: aload 6
    //   131: iconst_0
    //   132: aload 6
    //   134: arraylength
    //   135: invokestatic 782	com/google/android/gms/internal/adg:zzb	([BII)Lcom/google/android/gms/internal/adg;
    //   138: astore 6
    //   140: aload 5
    //   142: astore_2
    //   143: new 497	com/google/android/gms/internal/zzcjq
    //   146: dup
    //   147: invokespecial 795	com/google/android/gms/internal/zzcjq:<init>	()V
    //   150: astore 9
    //   152: aload 5
    //   154: astore_2
    //   155: aload 9
    //   157: aload 6
    //   159: invokevirtual 796	com/google/android/gms/internal/zzcjq:zza	(Lcom/google/android/gms/internal/adg;)Lcom/google/android/gms/internal/adp;
    //   162: pop
    //   163: aload 5
    //   165: astore_2
    //   166: aload 5
    //   168: iconst_0
    //   169: invokeinterface 732 2 0
    //   174: istore_3
    //   175: aload 5
    //   177: astore_2
    //   178: aload 8
    //   180: iload_3
    //   181: invokestatic 224	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   184: invokeinterface 788 2 0
    //   189: checkcast 570	java/util/List
    //   192: astore 7
    //   194: aload 7
    //   196: astore 6
    //   198: aload 7
    //   200: ifnonnull +32 -> 232
    //   203: aload 5
    //   205: astore_2
    //   206: new 567	java/util/ArrayList
    //   209: dup
    //   210: invokespecial 568	java/util/ArrayList:<init>	()V
    //   213: astore 6
    //   215: aload 5
    //   217: astore_2
    //   218: aload 8
    //   220: iload_3
    //   221: invokestatic 224	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   224: aload 6
    //   226: invokeinterface 35 3 0
    //   231: pop
    //   232: aload 5
    //   234: astore_2
    //   235: aload 6
    //   237: aload 9
    //   239: invokeinterface 585 2 0
    //   244: pop
    //   245: aload 5
    //   247: astore_2
    //   248: aload 5
    //   250: invokeinterface 648 1 0
    //   255: istore 4
    //   257: iload 4
    //   259: ifne -146 -> 113
    //   262: aload 5
    //   264: ifnull +10 -> 274
    //   267: aload 5
    //   269: invokeinterface 191 1 0
    //   274: aload 8
    //   276: areturn
    //   277: astore 6
    //   279: aload 5
    //   281: astore_2
    //   282: aload_0
    //   283: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   286: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   289: ldc_w 798
    //   292: aload_1
    //   293: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   296: aload 6
    //   298: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   301: goto -56 -> 245
    //   304: astore 6
    //   306: aload 5
    //   308: astore_2
    //   309: aload_0
    //   310: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   313: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   316: ldc_w 565
    //   319: aload_1
    //   320: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   323: aload 6
    //   325: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   328: aload 5
    //   330: ifnull +10 -> 340
    //   333: aload 5
    //   335: invokeinterface 191 1 0
    //   340: aconst_null
    //   341: areturn
    //   342: astore_1
    //   343: aconst_null
    //   344: astore_2
    //   345: aload_2
    //   346: ifnull +9 -> 355
    //   349: aload_2
    //   350: invokeinterface 191 1 0
    //   355: aload_1
    //   356: athrow
    //   357: astore_1
    //   358: goto -13 -> 345
    //   361: astore 6
    //   363: aconst_null
    //   364: astore 5
    //   366: goto -60 -> 306
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	369	0	this	zzcen
    //   0	369	1	paramString1	String
    //   0	369	2	paramString2	String
    //   174	47	3	i	int
    //   255	3	4	bool	boolean
    //   31	334	5	localObject1	Object
    //   96	140	6	localObject2	Object
    //   277	20	6	localIOException	IOException
    //   304	20	6	localSQLiteException1	SQLiteException
    //   361	1	6	localSQLiteException2	SQLiteException
    //   192	7	7	localList	List
    //   25	250	8	localArrayMap	ArrayMap
    //   150	88	9	localzzcjq	zzcjq
    // Exception table:
    //   from	to	target	type
    //   155	163	277	java/io/IOException
    //   80	90	304	android/database/sqlite/SQLiteException
    //   93	98	304	android/database/sqlite/SQLiteException
    //   116	126	304	android/database/sqlite/SQLiteException
    //   129	140	304	android/database/sqlite/SQLiteException
    //   143	152	304	android/database/sqlite/SQLiteException
    //   155	163	304	android/database/sqlite/SQLiteException
    //   166	175	304	android/database/sqlite/SQLiteException
    //   178	194	304	android/database/sqlite/SQLiteException
    //   206	215	304	android/database/sqlite/SQLiteException
    //   218	232	304	android/database/sqlite/SQLiteException
    //   235	245	304	android/database/sqlite/SQLiteException
    //   248	257	304	android/database/sqlite/SQLiteException
    //   282	301	304	android/database/sqlite/SQLiteException
    //   33	77	342	finally
    //   80	90	357	finally
    //   93	98	357	finally
    //   116	126	357	finally
    //   129	140	357	finally
    //   143	152	357	finally
    //   155	163	357	finally
    //   166	175	357	finally
    //   178	194	357	finally
    //   206	215	357	finally
    //   218	232	357	finally
    //   235	245	357	finally
    //   248	257	357	finally
    //   282	301	357	finally
    //   309	328	357	finally
    //   33	77	361	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  @WorkerThread
  protected final long zzL(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_2
    //   6: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   9: pop
    //   10: aload_0
    //   11: invokevirtual 424	com/google/android/gms/internal/zzcen:zzjC	()V
    //   14: aload_0
    //   15: invokevirtual 421	com/google/android/gms/internal/zzcen:zzkD	()V
    //   18: aload_0
    //   19: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   22: astore 8
    //   24: aload 8
    //   26: invokevirtual 619	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   29: aload_0
    //   30: new 344	java/lang/StringBuilder
    //   33: dup
    //   34: aload_2
    //   35: invokestatic 347	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   38: invokevirtual 351	java/lang/String:length	()I
    //   41: bipush 32
    //   43: iadd
    //   44: invokespecial 352	java/lang/StringBuilder:<init>	(I)V
    //   47: ldc_w 803
    //   50: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: aload_2
    //   54: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: ldc_w 805
    //   60: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   63: invokevirtual 363	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   66: iconst_1
    //   67: anewarray 267	java/lang/String
    //   70: dup
    //   71: iconst_0
    //   72: aload_1
    //   73: aastore
    //   74: ldc2_w 487
    //   77: invokespecial 807	com/google/android/gms/internal/zzcen:zza	(Ljava/lang/String;[Ljava/lang/String;J)J
    //   80: lstore 5
    //   82: lload 5
    //   84: lstore_3
    //   85: lload 5
    //   87: ldc2_w 487
    //   90: lcmp
    //   91: ifne +92 -> 183
    //   94: new 269	android/content/ContentValues
    //   97: dup
    //   98: invokespecial 464	android/content/ContentValues:<init>	()V
    //   101: astore 7
    //   103: aload 7
    //   105: ldc_w 466
    //   108: aload_1
    //   109: invokevirtual 272	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   112: aload 7
    //   114: ldc_w 809
    //   117: iconst_0
    //   118: invokestatic 224	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   121: invokevirtual 471	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   124: aload 7
    //   126: ldc -127
    //   128: iconst_0
    //   129: invokestatic 224	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   132: invokevirtual 471	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   135: aload 8
    //   137: ldc_w 811
    //   140: aconst_null
    //   141: aload 7
    //   143: iconst_5
    //   144: invokevirtual 486	android/database/sqlite/SQLiteDatabase:insertWithOnConflict	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    //   147: ldc2_w 487
    //   150: lcmp
    //   151: ifne +30 -> 181
    //   154: aload_0
    //   155: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   158: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   161: ldc_w 813
    //   164: aload_1
    //   165: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   168: aload_2
    //   169: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   172: aload 8
    //   174: invokevirtual 622	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   177: ldc2_w 487
    //   180: lreturn
    //   181: lconst_0
    //   182: lstore_3
    //   183: new 269	android/content/ContentValues
    //   186: dup
    //   187: invokespecial 464	android/content/ContentValues:<init>	()V
    //   190: astore 7
    //   192: aload 7
    //   194: ldc_w 466
    //   197: aload_1
    //   198: invokevirtual 272	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   201: aload 7
    //   203: aload_2
    //   204: lconst_1
    //   205: lload_3
    //   206: ladd
    //   207: invokestatic 239	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   210: invokevirtual 275	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   213: aload 8
    //   215: ldc_w 811
    //   218: aload 7
    //   220: ldc_w 815
    //   223: iconst_1
    //   224: anewarray 267	java/lang/String
    //   227: dup
    //   228: iconst_0
    //   229: aload_1
    //   230: aastore
    //   231: invokevirtual 819	android/database/sqlite/SQLiteDatabase:update	(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   234: i2l
    //   235: lconst_0
    //   236: lcmp
    //   237: ifne +30 -> 267
    //   240: aload_0
    //   241: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   244: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   247: ldc_w 821
    //   250: aload_1
    //   251: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   254: aload_2
    //   255: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   258: aload 8
    //   260: invokevirtual 622	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   263: ldc2_w 487
    //   266: lreturn
    //   267: aload 8
    //   269: invokevirtual 628	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   272: aload 8
    //   274: invokevirtual 622	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   277: lload_3
    //   278: lreturn
    //   279: astore 7
    //   281: lconst_0
    //   282: lstore_3
    //   283: aload_0
    //   284: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   287: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   290: ldc_w 823
    //   293: aload_1
    //   294: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   297: aload_2
    //   298: aload 7
    //   300: invokevirtual 447	com/google/android/gms/internal/zzcfn:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   303: aload 8
    //   305: invokevirtual 622	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   308: lload_3
    //   309: lreturn
    //   310: astore_1
    //   311: aload 8
    //   313: invokevirtual 622	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   316: aload_1
    //   317: athrow
    //   318: astore 7
    //   320: goto -37 -> 283
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	323	0	this	zzcen
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
  
  public final long zza(zzcjz paramzzcjz)
    throws IOException
  {
    zzjC();
    zzkD();
    zzbo.zzu(paramzzcjz);
    zzbo.zzcF(paramzzcjz.zzaH);
    for (;;)
    {
      try
      {
        byte[] arrayOfByte = new byte[paramzzcjz.zzLV()];
        Object localObject = adh.zzc(arrayOfByte, 0, arrayOfByte.length);
        paramzzcjz.zza((adh)localObject);
        ((adh)localObject).zzLM();
        localObject = zzwB();
        zzbo.zzu(arrayOfByte);
        ((zzcjl)localObject).zzjC();
        MessageDigest localMessageDigest = zzcjl.zzbE("MD5");
        if (localMessageDigest == null)
        {
          ((zzcjl)localObject).zzwF().zzyx().log("Failed to get MD5");
          l = 0L;
          localObject = new ContentValues();
          ((ContentValues)localObject).put("app_id", paramzzcjz.zzaH);
          ((ContentValues)localObject).put("metadata_fingerprint", Long.valueOf(l));
          ((ContentValues)localObject).put("metadata", arrayOfByte);
        }
        long l = zzcjl.zzn(localMessageDigest.digest(localIOException));
      }
      catch (IOException localIOException)
      {
        try
        {
          getWritableDatabase().insertWithOnConflict("raw_events_metadata", null, (ContentValues)localObject, 4);
          return l;
        }
        catch (SQLiteException localSQLiteException)
        {
          zzwF().zzyx().zze("Error storing raw event metadata. appId", zzcfl.zzdZ(paramzzcjz.zzaH), localSQLiteException);
          throw localSQLiteException;
        }
        localIOException = localIOException;
        zzwF().zzyx().zze("Data loss. Failed to serialize event metadata. appId", zzcfl.zzdZ(paramzzcjz.zzaH), localIOException);
        throw localIOException;
      }
    }
  }
  
  /* Error */
  @WorkerThread
  public final zzceo zza(long paramLong, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
  {
    // Byte code:
    //   0: aload_3
    //   1: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 424	com/google/android/gms/internal/zzcen:zzjC	()V
    //   9: aload_0
    //   10: invokevirtual 421	com/google/android/gms/internal/zzcen:zzkD	()V
    //   13: new 864	com/google/android/gms/internal/zzceo
    //   16: dup
    //   17: invokespecial 865	com/google/android/gms/internal/zzceo:<init>	()V
    //   20: astore 12
    //   22: aload_0
    //   23: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   26: astore 11
    //   28: aload 11
    //   30: ldc_w 867
    //   33: bipush 6
    //   35: anewarray 267	java/lang/String
    //   38: dup
    //   39: iconst_0
    //   40: ldc 63
    //   42: aastore
    //   43: dup
    //   44: iconst_1
    //   45: ldc 71
    //   47: aastore
    //   48: dup
    //   49: iconst_2
    //   50: ldc 67
    //   52: aastore
    //   53: dup
    //   54: iconst_3
    //   55: ldc 75
    //   57: aastore
    //   58: dup
    //   59: iconst_4
    //   60: ldc 99
    //   62: aastore
    //   63: dup
    //   64: iconst_5
    //   65: ldc 103
    //   67: aastore
    //   68: ldc_w 869
    //   71: iconst_1
    //   72: anewarray 267	java/lang/String
    //   75: dup
    //   76: iconst_0
    //   77: aload_3
    //   78: aastore
    //   79: aconst_null
    //   80: aconst_null
    //   81: aconst_null
    //   82: invokevirtual 413	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   85: astore 10
    //   87: aload 10
    //   89: astore 9
    //   91: aload 10
    //   93: invokeinterface 184 1 0
    //   98: ifne +39 -> 137
    //   101: aload 10
    //   103: astore 9
    //   105: aload_0
    //   106: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   109: invokevirtual 300	com/google/android/gms/internal/zzcfl:zzyz	()Lcom/google/android/gms/internal/zzcfn;
    //   112: ldc_w 871
    //   115: aload_3
    //   116: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   119: invokevirtual 228	com/google/android/gms/internal/zzcfn:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   122: aload 10
    //   124: ifnull +10 -> 134
    //   127: aload 10
    //   129: invokeinterface 191 1 0
    //   134: aload 12
    //   136: areturn
    //   137: aload 10
    //   139: astore 9
    //   141: aload 10
    //   143: iconst_0
    //   144: invokeinterface 188 2 0
    //   149: lload_1
    //   150: lcmp
    //   151: ifne +88 -> 239
    //   154: aload 10
    //   156: astore 9
    //   158: aload 12
    //   160: aload 10
    //   162: iconst_1
    //   163: invokeinterface 188 2 0
    //   168: putfield 875	com/google/android/gms/internal/zzceo:zzbpv	J
    //   171: aload 10
    //   173: astore 9
    //   175: aload 12
    //   177: aload 10
    //   179: iconst_2
    //   180: invokeinterface 188 2 0
    //   185: putfield 878	com/google/android/gms/internal/zzceo:zzbpu	J
    //   188: aload 10
    //   190: astore 9
    //   192: aload 12
    //   194: aload 10
    //   196: iconst_3
    //   197: invokeinterface 188 2 0
    //   202: putfield 881	com/google/android/gms/internal/zzceo:zzbpw	J
    //   205: aload 10
    //   207: astore 9
    //   209: aload 12
    //   211: aload 10
    //   213: iconst_4
    //   214: invokeinterface 188 2 0
    //   219: putfield 884	com/google/android/gms/internal/zzceo:zzbpx	J
    //   222: aload 10
    //   224: astore 9
    //   226: aload 12
    //   228: aload 10
    //   230: iconst_5
    //   231: invokeinterface 188 2 0
    //   236: putfield 887	com/google/android/gms/internal/zzceo:zzbpy	J
    //   239: iload 4
    //   241: ifeq +19 -> 260
    //   244: aload 10
    //   246: astore 9
    //   248: aload 12
    //   250: aload 12
    //   252: getfield 875	com/google/android/gms/internal/zzceo:zzbpv	J
    //   255: lconst_1
    //   256: ladd
    //   257: putfield 875	com/google/android/gms/internal/zzceo:zzbpv	J
    //   260: iload 5
    //   262: ifeq +19 -> 281
    //   265: aload 10
    //   267: astore 9
    //   269: aload 12
    //   271: aload 12
    //   273: getfield 878	com/google/android/gms/internal/zzceo:zzbpu	J
    //   276: lconst_1
    //   277: ladd
    //   278: putfield 878	com/google/android/gms/internal/zzceo:zzbpu	J
    //   281: iload 6
    //   283: ifeq +19 -> 302
    //   286: aload 10
    //   288: astore 9
    //   290: aload 12
    //   292: aload 12
    //   294: getfield 881	com/google/android/gms/internal/zzceo:zzbpw	J
    //   297: lconst_1
    //   298: ladd
    //   299: putfield 881	com/google/android/gms/internal/zzceo:zzbpw	J
    //   302: iload 7
    //   304: ifeq +19 -> 323
    //   307: aload 10
    //   309: astore 9
    //   311: aload 12
    //   313: aload 12
    //   315: getfield 884	com/google/android/gms/internal/zzceo:zzbpx	J
    //   318: lconst_1
    //   319: ladd
    //   320: putfield 884	com/google/android/gms/internal/zzceo:zzbpx	J
    //   323: iload 8
    //   325: ifeq +19 -> 344
    //   328: aload 10
    //   330: astore 9
    //   332: aload 12
    //   334: aload 12
    //   336: getfield 887	com/google/android/gms/internal/zzceo:zzbpy	J
    //   339: lconst_1
    //   340: ladd
    //   341: putfield 887	com/google/android/gms/internal/zzceo:zzbpy	J
    //   344: aload 10
    //   346: astore 9
    //   348: new 269	android/content/ContentValues
    //   351: dup
    //   352: invokespecial 464	android/content/ContentValues:<init>	()V
    //   355: astore 13
    //   357: aload 10
    //   359: astore 9
    //   361: aload 13
    //   363: ldc 63
    //   365: lload_1
    //   366: invokestatic 239	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   369: invokevirtual 275	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   372: aload 10
    //   374: astore 9
    //   376: aload 13
    //   378: ldc 67
    //   380: aload 12
    //   382: getfield 878	com/google/android/gms/internal/zzceo:zzbpu	J
    //   385: invokestatic 239	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   388: invokevirtual 275	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   391: aload 10
    //   393: astore 9
    //   395: aload 13
    //   397: ldc 71
    //   399: aload 12
    //   401: getfield 875	com/google/android/gms/internal/zzceo:zzbpv	J
    //   404: invokestatic 239	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   407: invokevirtual 275	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   410: aload 10
    //   412: astore 9
    //   414: aload 13
    //   416: ldc 75
    //   418: aload 12
    //   420: getfield 881	com/google/android/gms/internal/zzceo:zzbpw	J
    //   423: invokestatic 239	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   426: invokevirtual 275	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   429: aload 10
    //   431: astore 9
    //   433: aload 13
    //   435: ldc 99
    //   437: aload 12
    //   439: getfield 884	com/google/android/gms/internal/zzceo:zzbpx	J
    //   442: invokestatic 239	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   445: invokevirtual 275	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   448: aload 10
    //   450: astore 9
    //   452: aload 13
    //   454: ldc 103
    //   456: aload 12
    //   458: getfield 887	com/google/android/gms/internal/zzceo:zzbpy	J
    //   461: invokestatic 239	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   464: invokevirtual 275	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   467: aload 10
    //   469: astore 9
    //   471: aload 11
    //   473: ldc_w 867
    //   476: aload 13
    //   478: ldc_w 869
    //   481: iconst_1
    //   482: anewarray 267	java/lang/String
    //   485: dup
    //   486: iconst_0
    //   487: aload_3
    //   488: aastore
    //   489: invokevirtual 819	android/database/sqlite/SQLiteDatabase:update	(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   492: pop
    //   493: aload 10
    //   495: ifnull +10 -> 505
    //   498: aload 10
    //   500: invokeinterface 191 1 0
    //   505: aload 12
    //   507: areturn
    //   508: astore 11
    //   510: aconst_null
    //   511: astore 10
    //   513: aload 10
    //   515: astore 9
    //   517: aload_0
    //   518: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   521: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   524: ldc_w 889
    //   527: aload_3
    //   528: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   531: aload 11
    //   533: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   536: aload 10
    //   538: ifnull +10 -> 548
    //   541: aload 10
    //   543: invokeinterface 191 1 0
    //   548: aload 12
    //   550: areturn
    //   551: astore_3
    //   552: aconst_null
    //   553: astore 9
    //   555: aload 9
    //   557: ifnull +10 -> 567
    //   560: aload 9
    //   562: invokeinterface 191 1 0
    //   567: aload_3
    //   568: athrow
    //   569: astore_3
    //   570: goto -15 -> 555
    //   573: astore 11
    //   575: goto -62 -> 513
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	578	0	this	zzcen
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
    //   20	529	12	localzzceo	zzceo
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
  public final void zza(zzceg paramzzceg)
  {
    zzbo.zzu(paramzzceg);
    zzjC();
    zzkD();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzceg.zzhl());
    localContentValues.put("app_instance_id", paramzzceg.getAppInstanceId());
    localContentValues.put("gmp_app_id", paramzzceg.getGmpAppId());
    localContentValues.put("resettable_device_id_hash", paramzzceg.zzwJ());
    localContentValues.put("last_bundle_index", Long.valueOf(paramzzceg.zzwS()));
    localContentValues.put("last_bundle_start_timestamp", Long.valueOf(paramzzceg.zzwL()));
    localContentValues.put("last_bundle_end_timestamp", Long.valueOf(paramzzceg.zzwM()));
    localContentValues.put("app_version", paramzzceg.zzjH());
    localContentValues.put("app_store", paramzzceg.zzwO());
    localContentValues.put("gmp_version", Long.valueOf(paramzzceg.zzwP()));
    localContentValues.put("dev_cert_hash", Long.valueOf(paramzzceg.zzwQ()));
    localContentValues.put("measurement_enabled", Boolean.valueOf(paramzzceg.zzwR()));
    localContentValues.put("day", Long.valueOf(paramzzceg.zzwW()));
    localContentValues.put("daily_public_events_count", Long.valueOf(paramzzceg.zzwX()));
    localContentValues.put("daily_events_count", Long.valueOf(paramzzceg.zzwY()));
    localContentValues.put("daily_conversions_count", Long.valueOf(paramzzceg.zzwZ()));
    localContentValues.put("config_fetched_time", Long.valueOf(paramzzceg.zzwT()));
    localContentValues.put("failed_config_fetch_time", Long.valueOf(paramzzceg.zzwU()));
    localContentValues.put("app_version_int", Long.valueOf(paramzzceg.zzwN()));
    localContentValues.put("firebase_instance_id", paramzzceg.zzwK());
    localContentValues.put("daily_error_events_count", Long.valueOf(paramzzceg.zzxb()));
    localContentValues.put("daily_realtime_events_count", Long.valueOf(paramzzceg.zzxa()));
    localContentValues.put("health_monitor_sample", paramzzceg.zzxc());
    localContentValues.put("android_id", Long.valueOf(paramzzceg.zzxe()));
    try
    {
      SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
      if ((localSQLiteDatabase.update("apps", localContentValues, "app_id = ?", new String[] { paramzzceg.zzhl() }) == 0L) && (localSQLiteDatabase.insertWithOnConflict("apps", null, localContentValues, 5) == -1L)) {
        zzwF().zzyx().zzj("Failed to insert/update app (got -1). appId", zzcfl.zzdZ(paramzzceg.zzhl()));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzwF().zzyx().zze("Error storing app. appId", zzcfl.zzdZ(paramzzceg.zzhl()), localSQLiteException);
    }
  }
  
  @WorkerThread
  public final void zza(zzcev paramzzcev)
  {
    zzbo.zzu(paramzzcev);
    zzjC();
    zzkD();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzcev.mAppId);
    localContentValues.put("name", paramzzcev.mName);
    localContentValues.put("lifetime_count", Long.valueOf(paramzzcev.zzbpG));
    localContentValues.put("current_bundle_count", Long.valueOf(paramzzcev.zzbpH));
    localContentValues.put("last_fire_timestamp", Long.valueOf(paramzzcev.zzbpI));
    try
    {
      if (getWritableDatabase().insertWithOnConflict("events", null, localContentValues, 5) == -1L) {
        zzwF().zzyx().zzj("Failed to insert/update event aggregates (got -1). appId", zzcfl.zzdZ(paramzzcev.mAppId));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzwF().zzyx().zze("Error storing event aggregates. appId", zzcfl.zzdZ(paramzzcev.mAppId), localSQLiteException);
    }
  }
  
  @WorkerThread
  final void zza(String paramString, zzcjm[] paramArrayOfzzcjm)
  {
    int n = 0;
    zzkD();
    zzjC();
    zzbo.zzcF(paramString);
    zzbo.zzu(paramArrayOfzzcjm);
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    Object localObject1;
    int j;
    int i2;
    for (;;)
    {
      try
      {
        zzkD();
        zzjC();
        zzbo.zzcF(paramString);
        localObject1 = getWritableDatabase();
        ((SQLiteDatabase)localObject1).delete("property_filters", "app_id=?", new String[] { paramString });
        ((SQLiteDatabase)localObject1).delete("event_filters", "app_id=?", new String[] { paramString });
        int i1 = paramArrayOfzzcjm.length;
        j = 0;
        if (j >= i1) {
          break label480;
        }
        localObject1 = paramArrayOfzzcjm[j];
        zzkD();
        zzjC();
        zzbo.zzcF(paramString);
        zzbo.zzu(localObject1);
        zzbo.zzu(((zzcjm)localObject1).zzbuK);
        zzbo.zzu(((zzcjm)localObject1).zzbuJ);
        if (((zzcjm)localObject1).zzbuI == null)
        {
          zzwF().zzyz().zzj("Audience with no ID. appId", zzcfl.zzdZ(paramString));
        }
        else
        {
          i2 = ((zzcjm)localObject1).zzbuI.intValue();
          localObject2 = ((zzcjm)localObject1).zzbuK;
          k = localObject2.length;
          i = 0;
          if (i < k) {
            if (localObject2[i].zzbuM == null) {
              zzwF().zzyz().zze("Event filter with no ID. Audience definition ignored. appId, audienceId", zzcfl.zzdZ(paramString), ((zzcjm)localObject1).zzbuI);
            }
          }
        }
      }
      finally
      {
        localSQLiteDatabase.endTransaction();
      }
      i += 1;
    }
    Object localObject2 = ((zzcjm)localObject1).zzbuJ;
    int k = localObject2.length;
    int i = 0;
    label270:
    label325:
    label347:
    int m;
    int i3;
    if (i < k)
    {
      if (localObject2[i].zzbuM != null) {
        break label556;
      }
      zzwF().zzyz().zze("Property filter with no ID. Audience definition ignored. appId, audienceId", zzcfl.zzdZ(paramString), ((zzcjm)localObject1).zzbuI);
    }
    else
    {
      localObject2 = ((zzcjm)localObject1).zzbuK;
      k = localObject2.length;
      i = 0;
      if (i < k)
      {
        if (zza(paramString, i2, localObject2[i])) {
          break label563;
        }
        i = 0;
        m = i;
        if (i != 0)
        {
          localObject1 = ((zzcjm)localObject1).zzbuJ;
          i3 = localObject1.length;
          k = 0;
        }
      }
    }
    for (;;)
    {
      m = i;
      if (k < i3)
      {
        if (!zza(paramString, i2, localObject1[k])) {
          m = 0;
        }
      }
      else
      {
        if (m == 0)
        {
          zzkD();
          zzjC();
          zzbo.zzcF(paramString);
          localObject1 = getWritableDatabase();
          ((SQLiteDatabase)localObject1).delete("property_filters", "app_id=? and audience_id=?", new String[] { paramString, String.valueOf(i2) });
          ((SQLiteDatabase)localObject1).delete("event_filters", "app_id=? and audience_id=?", new String[] { paramString, String.valueOf(i2) });
          break label547;
          label480:
          localObject1 = new ArrayList();
          j = paramArrayOfzzcjm.length;
          i = n;
          while (i < j)
          {
            ((List)localObject1).add(paramArrayOfzzcjm[i].zzbuI);
            i += 1;
          }
          zzc(paramString, (List)localObject1);
          localSQLiteDatabase.setTransactionSuccessful();
          localSQLiteDatabase.endTransaction();
          return;
          i = 1;
          break label347;
        }
        label547:
        j += 1;
        break;
        label556:
        i += 1;
        break label270;
        label563:
        i += 1;
        break label325;
      }
      k += 1;
    }
  }
  
  @WorkerThread
  public final boolean zza(zzcek paramzzcek)
  {
    zzbo.zzu(paramzzcek);
    zzjC();
    zzkD();
    if (zzG(paramzzcek.packageName, paramzzcek.zzbpd.name) == null)
    {
      long l = zzb("SELECT COUNT(1) FROM conditional_properties WHERE app_id=?", new String[] { paramzzcek.packageName });
      zzcem.zzxv();
      if (l >= 1000L) {
        return false;
      }
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzcek.packageName);
    localContentValues.put("origin", paramzzcek.zzbpc);
    localContentValues.put("name", paramzzcek.zzbpd.name);
    zza(localContentValues, "value", paramzzcek.zzbpd.getValue());
    localContentValues.put("active", Boolean.valueOf(paramzzcek.zzbpf));
    localContentValues.put("trigger_event_name", paramzzcek.zzbpg);
    localContentValues.put("trigger_timeout", Long.valueOf(paramzzcek.zzbpi));
    zzwB();
    localContentValues.put("timed_out_event", zzcjl.zza(paramzzcek.zzbph));
    localContentValues.put("creation_timestamp", Long.valueOf(paramzzcek.zzbpe));
    zzwB();
    localContentValues.put("triggered_event", zzcjl.zza(paramzzcek.zzbpj));
    localContentValues.put("triggered_timestamp", Long.valueOf(paramzzcek.zzbpd.zzbuy));
    localContentValues.put("time_to_live", Long.valueOf(paramzzcek.zzbpk));
    zzwB();
    localContentValues.put("expired_event", zzcjl.zza(paramzzcek.zzbpl));
    try
    {
      if (getWritableDatabase().insertWithOnConflict("conditional_properties", null, localContentValues, 5) == -1L) {
        zzwF().zzyx().zzj("Failed to insert/update conditional user property (got -1)", zzcfl.zzdZ(paramzzcek.packageName));
      }
      return true;
    }
    catch (SQLiteException localSQLiteException)
    {
      for (;;)
      {
        zzwF().zzyx().zze("Error storing conditional user property", zzcfl.zzdZ(paramzzcek.packageName), localSQLiteException);
      }
    }
  }
  
  public final boolean zza(zzceu paramzzceu, long paramLong, boolean paramBoolean)
  {
    zzjC();
    zzkD();
    zzbo.zzu(paramzzceu);
    zzbo.zzcF(paramzzceu.mAppId);
    Object localObject1 = new zzcjw();
    ((zzcjw)localObject1).zzbvy = Long.valueOf(paramzzceu.zzbpE);
    ((zzcjw)localObject1).zzbvw = new zzcjx[paramzzceu.zzbpF.size()];
    Object localObject2 = paramzzceu.zzbpF.iterator();
    int i = 0;
    Object localObject3;
    while (((Iterator)localObject2).hasNext())
    {
      Object localObject4 = (String)((Iterator)localObject2).next();
      localObject3 = new zzcjx();
      ((zzcjw)localObject1).zzbvw[i] = localObject3;
      ((zzcjx)localObject3).name = ((String)localObject4);
      localObject4 = paramzzceu.zzbpF.get((String)localObject4);
      zzwB().zza((zzcjx)localObject3, localObject4);
      i += 1;
    }
    for (;;)
    {
      try
      {
        localObject2 = new byte[((zzcjw)localObject1).zzLV()];
        localObject3 = adh.zzc((byte[])localObject2, 0, localObject2.length);
        ((zzcjw)localObject1).zza((adh)localObject3);
        ((adh)localObject3).zzLM();
        zzwF().zzyD().zze("Saving event, name, data size", zzwA().zzdW(paramzzceu.mName), Integer.valueOf(localObject2.length));
        localObject1 = new ContentValues();
        ((ContentValues)localObject1).put("app_id", paramzzceu.mAppId);
        ((ContentValues)localObject1).put("name", paramzzceu.mName);
        ((ContentValues)localObject1).put("timestamp", Long.valueOf(paramzzceu.zzayS));
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
          zzwF().zzyx().zzj("Failed to insert raw event (got -1). appId", zzcfl.zzdZ(paramzzceu.mAppId));
          return false;
        }
        catch (SQLiteException localSQLiteException)
        {
          zzwF().zzyx().zze("Error storing raw event. appId", zzcfl.zzdZ(paramzzceu.mAppId), localSQLiteException);
          return false;
        }
        localIOException = localIOException;
        zzwF().zzyx().zze("Data loss. Failed to serialize event params/data. appId", zzcfl.zzdZ(paramzzceu.mAppId), localIOException);
        return false;
      }
    }
    return true;
  }
  
  @WorkerThread
  public final boolean zza(zzcjk paramzzcjk)
  {
    zzbo.zzu(paramzzcjk);
    zzjC();
    zzkD();
    if (zzG(paramzzcjk.mAppId, paramzzcjk.mName) == null)
    {
      long l;
      if (zzcjl.zzeo(paramzzcjk.mName))
      {
        l = zzb("select count(1) from user_attributes where app_id=? and name not like '!_%' escape '!'", new String[] { paramzzcjk.mAppId });
        zzcem.zzxs();
        if (l < 25L) {}
      }
      else
      {
        do
        {
          return false;
          l = zzb("select count(1) from user_attributes where app_id=? and origin=? AND name like '!_%' escape '!'", new String[] { paramzzcjk.mAppId, paramzzcjk.mOrigin });
          zzcem.zzxu();
        } while (l >= 25L);
      }
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzcjk.mAppId);
    localContentValues.put("origin", paramzzcjk.mOrigin);
    localContentValues.put("name", paramzzcjk.mName);
    localContentValues.put("set_timestamp", Long.valueOf(paramzzcjk.zzbuC));
    zza(localContentValues, "value", paramzzcjk.mValue);
    try
    {
      if (getWritableDatabase().insertWithOnConflict("user_attributes", null, localContentValues, 5) == -1L) {
        zzwF().zzyx().zzj("Failed to insert/update user property (got -1). appId", zzcfl.zzdZ(paramzzcjk.mAppId));
      }
      return true;
    }
    catch (SQLiteException localSQLiteException)
    {
      for (;;)
      {
        zzwF().zzyx().zze("Error storing user property. appId", zzcfl.zzdZ(paramzzcjk.mAppId), localSQLiteException);
      }
    }
  }
  
  @WorkerThread
  public final boolean zza(zzcjz paramzzcjz, boolean paramBoolean)
  {
    zzjC();
    zzkD();
    zzbo.zzu(paramzzcjz);
    zzbo.zzcF(paramzzcjz.zzaH);
    zzbo.zzu(paramzzcjz.zzbvI);
    zzye();
    long l = zzkq().currentTimeMillis();
    if ((paramzzcjz.zzbvI.longValue() < l - zzcem.zzxG()) || (paramzzcjz.zzbvI.longValue() > zzcem.zzxG() + l)) {
      zzwF().zzyz().zzd("Storing bundle outside of the max uploading time span. appId, now, timestamp", zzcfl.zzdZ(paramzzcjz.zzaH), Long.valueOf(l), paramzzcjz.zzbvI);
    }
    for (;;)
    {
      try
      {
        byte[] arrayOfByte = new byte[paramzzcjz.zzLV()];
        Object localObject = adh.zzc(arrayOfByte, 0, arrayOfByte.length);
        paramzzcjz.zza((adh)localObject);
        ((adh)localObject).zzLM();
        arrayOfByte = zzwB().zzl(arrayOfByte);
        zzwF().zzyD().zzj("Saving bundle, size", Integer.valueOf(arrayOfByte.length));
        localObject = new ContentValues();
        ((ContentValues)localObject).put("app_id", paramzzcjz.zzaH);
        ((ContentValues)localObject).put("bundle_end_timestamp", paramzzcjz.zzbvI);
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
          zzwF().zzyx().zzj("Failed to insert bundle (got -1). appId", zzcfl.zzdZ(paramzzcjz.zzaH));
          return false;
        }
        catch (SQLiteException localSQLiteException)
        {
          zzwF().zzyx().zze("Error storing bundle. appId", zzcfl.zzdZ(paramzzcjz.zzaH), localSQLiteException);
          return false;
        }
        localIOException = localIOException;
        zzwF().zzyx().zze("Data loss. Failed to serialize bundle. appId", zzcfl.zzdZ(paramzzcjz.zzaH), localIOException);
        return false;
      }
    }
    return true;
  }
  
  /* Error */
  public final String zzaa(long paramLong)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aload_0
    //   4: invokevirtual 424	com/google/android/gms/internal/zzcen:zzjC	()V
    //   7: aload_0
    //   8: invokevirtual 421	com/google/android/gms/internal/zzcen:zzkD	()V
    //   11: aload_0
    //   12: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   15: ldc_w 1221
    //   18: iconst_1
    //   19: anewarray 267	java/lang/String
    //   22: dup
    //   23: iconst_0
    //   24: lload_1
    //   25: invokestatic 1223	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   28: aastore
    //   29: invokevirtual 178	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   32: astore_3
    //   33: aload_3
    //   34: astore 4
    //   36: aload_3
    //   37: invokeinterface 184 1 0
    //   42: ifne +40 -> 82
    //   45: aload_3
    //   46: astore 4
    //   48: aload_0
    //   49: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   52: invokevirtual 667	com/google/android/gms/internal/zzcfl:zzyD	()Lcom/google/android/gms/internal/zzcfn;
    //   55: ldc_w 1225
    //   58: invokevirtual 234	com/google/android/gms/internal/zzcfn:log	(Ljava/lang/String;)V
    //   61: aload 6
    //   63: astore 4
    //   65: aload_3
    //   66: ifnull +13 -> 79
    //   69: aload_3
    //   70: invokeinterface 191 1 0
    //   75: aload 6
    //   77: astore 4
    //   79: aload 4
    //   81: areturn
    //   82: aload_3
    //   83: astore 4
    //   85: aload_3
    //   86: iconst_0
    //   87: invokeinterface 252 2 0
    //   92: astore 5
    //   94: aload 5
    //   96: astore 4
    //   98: aload_3
    //   99: ifnull -20 -> 79
    //   102: aload_3
    //   103: invokeinterface 191 1 0
    //   108: aload 5
    //   110: areturn
    //   111: astore 5
    //   113: aconst_null
    //   114: astore_3
    //   115: aload_3
    //   116: astore 4
    //   118: aload_0
    //   119: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   122: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   125: ldc_w 1227
    //   128: aload 5
    //   130: invokevirtual 228	com/google/android/gms/internal/zzcfn:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   133: aload 6
    //   135: astore 4
    //   137: aload_3
    //   138: ifnull -59 -> 79
    //   141: aload_3
    //   142: invokeinterface 191 1 0
    //   147: aconst_null
    //   148: areturn
    //   149: astore_3
    //   150: aconst_null
    //   151: astore 4
    //   153: aload 4
    //   155: ifnull +10 -> 165
    //   158: aload 4
    //   160: invokeinterface 191 1 0
    //   165: aload_3
    //   166: athrow
    //   167: astore_3
    //   168: goto -15 -> 153
    //   171: astore 5
    //   173: goto -58 -> 115
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	176	0	this	zzcen
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
  public final List<zzcek> zzc(String paramString, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 424	com/google/android/gms/internal/zzcen:zzjC	()V
    //   4: aload_0
    //   5: invokevirtual 421	com/google/android/gms/internal/zzcen:zzkD	()V
    //   8: new 567	java/util/ArrayList
    //   11: dup
    //   12: invokespecial 568	java/util/ArrayList:<init>	()V
    //   15: astore 12
    //   17: aload_0
    //   18: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   21: astore 13
    //   23: invokestatic 1053	com/google/android/gms/internal/zzcem:zzxv	()I
    //   26: pop
    //   27: aload 13
    //   29: ldc_w 711
    //   32: bipush 13
    //   34: anewarray 267	java/lang/String
    //   37: dup
    //   38: iconst_0
    //   39: ldc_w 466
    //   42: aastore
    //   43: dup
    //   44: iconst_1
    //   45: ldc 27
    //   47: aastore
    //   48: dup
    //   49: iconst_2
    //   50: ldc_w 407
    //   53: aastore
    //   54: dup
    //   55: iconst_3
    //   56: ldc_w 680
    //   59: aastore
    //   60: dup
    //   61: iconst_4
    //   62: ldc_w 713
    //   65: aastore
    //   66: dup
    //   67: iconst_5
    //   68: ldc_w 715
    //   71: aastore
    //   72: dup
    //   73: bipush 6
    //   75: ldc_w 717
    //   78: aastore
    //   79: dup
    //   80: bipush 7
    //   82: ldc_w 719
    //   85: aastore
    //   86: dup
    //   87: bipush 8
    //   89: ldc_w 721
    //   92: aastore
    //   93: dup
    //   94: bipush 9
    //   96: ldc_w 723
    //   99: aastore
    //   100: dup
    //   101: bipush 10
    //   103: ldc_w 725
    //   106: aastore
    //   107: dup
    //   108: bipush 11
    //   110: ldc_w 727
    //   113: aastore
    //   114: dup
    //   115: bipush 12
    //   117: ldc_w 729
    //   120: aastore
    //   121: aload_1
    //   122: aload_2
    //   123: aconst_null
    //   124: aconst_null
    //   125: ldc_w 1230
    //   128: ldc_w 1232
    //   131: invokevirtual 1235	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   134: astore_1
    //   135: aload_1
    //   136: invokeinterface 184 1 0
    //   141: istore_3
    //   142: iload_3
    //   143: ifne +18 -> 161
    //   146: aload_1
    //   147: ifnull +9 -> 156
    //   150: aload_1
    //   151: invokeinterface 191 1 0
    //   156: aload 12
    //   158: astore_2
    //   159: aload_2
    //   160: areturn
    //   161: aload 12
    //   163: invokeinterface 573 1 0
    //   168: invokestatic 1053	com/google/android/gms/internal/zzcem:zzxv	()I
    //   171: if_icmplt +35 -> 206
    //   174: aload_0
    //   175: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   178: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   181: ldc_w 1237
    //   184: invokestatic 1053	com/google/android/gms/internal/zzcem:zzxv	()I
    //   187: invokestatic 224	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   190: invokevirtual 228	com/google/android/gms/internal/zzcfn:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   193: aload_1
    //   194: ifnull +9 -> 203
    //   197: aload_1
    //   198: invokeinterface 191 1 0
    //   203: aload 12
    //   205: areturn
    //   206: aload_1
    //   207: iconst_0
    //   208: invokeinterface 252 2 0
    //   213: astore_2
    //   214: aload_1
    //   215: iconst_1
    //   216: invokeinterface 252 2 0
    //   221: astore 13
    //   223: aload_1
    //   224: iconst_2
    //   225: invokeinterface 252 2 0
    //   230: astore 14
    //   232: aload_0
    //   233: aload_1
    //   234: iconst_3
    //   235: invokespecial 682	com/google/android/gms/internal/zzcen:zza	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   238: astore 15
    //   240: aload_1
    //   241: iconst_4
    //   242: invokeinterface 732 2 0
    //   247: ifeq +185 -> 432
    //   250: iconst_1
    //   251: istore_3
    //   252: aload_1
    //   253: iconst_5
    //   254: invokeinterface 252 2 0
    //   259: astore 16
    //   261: aload_1
    //   262: bipush 6
    //   264: invokeinterface 188 2 0
    //   269: lstore 4
    //   271: aload_0
    //   272: invokevirtual 736	com/google/android/gms/internal/zzcen:zzwB	()Lcom/google/android/gms/internal/zzcjl;
    //   275: aload_1
    //   276: bipush 7
    //   278: invokeinterface 740 2 0
    //   283: getstatic 746	com/google/android/gms/internal/zzcez:CREATOR	Landroid/os/Parcelable$Creator;
    //   286: invokevirtual 751	com/google/android/gms/internal/zzcjl:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   289: checkcast 742	com/google/android/gms/internal/zzcez
    //   292: astore 17
    //   294: aload_1
    //   295: bipush 8
    //   297: invokeinterface 188 2 0
    //   302: lstore 6
    //   304: aload_0
    //   305: invokevirtual 736	com/google/android/gms/internal/zzcen:zzwB	()Lcom/google/android/gms/internal/zzcjl;
    //   308: aload_1
    //   309: bipush 9
    //   311: invokeinterface 740 2 0
    //   316: getstatic 746	com/google/android/gms/internal/zzcez:CREATOR	Landroid/os/Parcelable$Creator;
    //   319: invokevirtual 751	com/google/android/gms/internal/zzcjl:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   322: checkcast 742	com/google/android/gms/internal/zzcez
    //   325: astore 18
    //   327: aload_1
    //   328: bipush 10
    //   330: invokeinterface 188 2 0
    //   335: lstore 8
    //   337: aload_1
    //   338: bipush 11
    //   340: invokeinterface 188 2 0
    //   345: lstore 10
    //   347: aload_0
    //   348: invokevirtual 736	com/google/android/gms/internal/zzcen:zzwB	()Lcom/google/android/gms/internal/zzcjl;
    //   351: aload_1
    //   352: bipush 12
    //   354: invokeinterface 740 2 0
    //   359: getstatic 746	com/google/android/gms/internal/zzcez:CREATOR	Landroid/os/Parcelable$Creator;
    //   362: invokevirtual 751	com/google/android/gms/internal/zzcjl:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   365: checkcast 742	com/google/android/gms/internal/zzcez
    //   368: astore 19
    //   370: aload 12
    //   372: new 753	com/google/android/gms/internal/zzcek
    //   375: dup
    //   376: aload_2
    //   377: aload 13
    //   379: new 755	com/google/android/gms/internal/zzcji
    //   382: dup
    //   383: aload 14
    //   385: lload 8
    //   387: aload 15
    //   389: aload 13
    //   391: invokespecial 758	com/google/android/gms/internal/zzcji:<init>	(Ljava/lang/String;JLjava/lang/Object;Ljava/lang/String;)V
    //   394: lload 6
    //   396: iload_3
    //   397: aload 16
    //   399: aload 17
    //   401: lload 4
    //   403: aload 18
    //   405: lload 10
    //   407: aload 19
    //   409: invokespecial 761	com/google/android/gms/internal/zzcek:<init>	(Ljava/lang/String;Ljava/lang/String;Lcom/google/android/gms/internal/zzcji;JZLjava/lang/String;Lcom/google/android/gms/internal/zzcez;JLcom/google/android/gms/internal/zzcez;JLcom/google/android/gms/internal/zzcez;)V
    //   412: invokeinterface 585 2 0
    //   417: pop
    //   418: aload_1
    //   419: invokeinterface 648 1 0
    //   424: istore_3
    //   425: iload_3
    //   426: ifne -265 -> 161
    //   429: goto -236 -> 193
    //   432: iconst_0
    //   433: istore_3
    //   434: goto -182 -> 252
    //   437: astore_2
    //   438: aconst_null
    //   439: astore_1
    //   440: aload_0
    //   441: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   444: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   447: ldc_w 1239
    //   450: aload_2
    //   451: invokevirtual 228	com/google/android/gms/internal/zzcfn:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   454: invokestatic 1243	java/util/Collections:emptyList	()Ljava/util/List;
    //   457: astore 12
    //   459: aload 12
    //   461: astore_2
    //   462: aload_1
    //   463: ifnull -304 -> 159
    //   466: aload_1
    //   467: invokeinterface 191 1 0
    //   472: aload 12
    //   474: areturn
    //   475: astore_2
    //   476: aconst_null
    //   477: astore_1
    //   478: aload_1
    //   479: ifnull +9 -> 488
    //   482: aload_1
    //   483: invokeinterface 191 1 0
    //   488: aload_2
    //   489: athrow
    //   490: astore_2
    //   491: goto -13 -> 478
    //   494: astore_2
    //   495: goto -17 -> 478
    //   498: astore_2
    //   499: goto -59 -> 440
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	502	0	this	zzcen
    //   0	502	1	paramString	String
    //   0	502	2	paramArrayOfString	String[]
    //   141	293	3	bool	boolean
    //   269	133	4	l1	long
    //   302	93	6	l2	long
    //   335	51	8	l3	long
    //   345	61	10	l4	long
    //   15	458	12	localObject1	Object
    //   21	369	13	localObject2	Object
    //   230	154	14	str1	String
    //   238	150	15	localObject3	Object
    //   259	139	16	str2	String
    //   292	108	17	localzzcez1	zzcez
    //   325	79	18	localzzcez2	zzcez
    //   368	40	19	localzzcez3	zzcez
    // Exception table:
    //   from	to	target	type
    //   17	135	437	android/database/sqlite/SQLiteException
    //   17	135	475	finally
    //   135	142	490	finally
    //   161	193	490	finally
    //   206	250	490	finally
    //   252	425	490	finally
    //   440	459	494	finally
    //   135	142	498	android/database/sqlite/SQLiteException
    //   161	193	498	android/database/sqlite/SQLiteException
    //   206	250	498	android/database/sqlite/SQLiteException
    //   252	425	498	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  @WorkerThread
  public final List<zzcjk> zzdP(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aload_1
    //   4: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_0
    //   9: invokevirtual 424	com/google/android/gms/internal/zzcen:zzjC	()V
    //   12: aload_0
    //   13: invokevirtual 421	com/google/android/gms/internal/zzcen:zzkD	()V
    //   16: new 567	java/util/ArrayList
    //   19: dup
    //   20: invokespecial 568	java/util/ArrayList:<init>	()V
    //   23: astore 9
    //   25: aload_0
    //   26: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   29: astore 6
    //   31: invokestatic 1249	com/google/android/gms/internal/zzcem:zzxt	()I
    //   34: istore_2
    //   35: aload 6
    //   37: ldc_w 664
    //   40: iconst_4
    //   41: anewarray 267	java/lang/String
    //   44: dup
    //   45: iconst_0
    //   46: ldc_w 407
    //   49: aastore
    //   50: dup
    //   51: iconst_1
    //   52: ldc 27
    //   54: aastore
    //   55: dup
    //   56: iconst_2
    //   57: ldc_w 678
    //   60: aastore
    //   61: dup
    //   62: iconst_3
    //   63: ldc_w 680
    //   66: aastore
    //   67: ldc_w 869
    //   70: iconst_1
    //   71: anewarray 267	java/lang/String
    //   74: dup
    //   75: iconst_0
    //   76: aload_1
    //   77: aastore
    //   78: aconst_null
    //   79: aconst_null
    //   80: ldc_w 1230
    //   83: iload_2
    //   84: invokestatic 1034	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   87: invokevirtual 1235	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   90: astore 6
    //   92: aload 6
    //   94: invokeinterface 184 1 0
    //   99: istore_3
    //   100: iload_3
    //   101: ifne +18 -> 119
    //   104: aload 6
    //   106: ifnull +10 -> 116
    //   109: aload 6
    //   111: invokeinterface 191 1 0
    //   116: aload 9
    //   118: areturn
    //   119: aload 6
    //   121: iconst_0
    //   122: invokeinterface 252 2 0
    //   127: astore 10
    //   129: aload 6
    //   131: iconst_1
    //   132: invokeinterface 252 2 0
    //   137: astore 8
    //   139: aload 8
    //   141: astore 7
    //   143: aload 8
    //   145: ifnonnull +8 -> 153
    //   148: ldc_w 1251
    //   151: astore 7
    //   153: aload 6
    //   155: iconst_2
    //   156: invokeinterface 188 2 0
    //   161: lstore 4
    //   163: aload_0
    //   164: aload 6
    //   166: iconst_3
    //   167: invokespecial 682	com/google/android/gms/internal/zzcen:zza	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   170: astore 8
    //   172: aload 8
    //   174: ifnonnull +47 -> 221
    //   177: aload_0
    //   178: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   181: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   184: ldc_w 1253
    //   187: aload_1
    //   188: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   191: invokevirtual 228	com/google/android/gms/internal/zzcfn:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   194: aload 6
    //   196: invokeinterface 648 1 0
    //   201: istore_3
    //   202: iload_3
    //   203: ifne -84 -> 119
    //   206: aload 6
    //   208: ifnull +10 -> 218
    //   211: aload 6
    //   213: invokeinterface 191 1 0
    //   218: aload 9
    //   220: areturn
    //   221: aload 9
    //   223: new 684	com/google/android/gms/internal/zzcjk
    //   226: dup
    //   227: aload_1
    //   228: aload 7
    //   230: aload 10
    //   232: lload 4
    //   234: aload 8
    //   236: invokespecial 687	com/google/android/gms/internal/zzcjk:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   239: invokeinterface 585 2 0
    //   244: pop
    //   245: goto -51 -> 194
    //   248: astore 7
    //   250: aload_0
    //   251: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   254: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   257: ldc_w 1255
    //   260: aload_1
    //   261: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   264: aload 7
    //   266: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   269: aload 6
    //   271: ifnull +10 -> 281
    //   274: aload 6
    //   276: invokeinterface 191 1 0
    //   281: aconst_null
    //   282: areturn
    //   283: astore_1
    //   284: aload 7
    //   286: astore 6
    //   288: aload 6
    //   290: ifnull +10 -> 300
    //   293: aload 6
    //   295: invokeinterface 191 1 0
    //   300: aload_1
    //   301: athrow
    //   302: astore_1
    //   303: goto -15 -> 288
    //   306: astore_1
    //   307: goto -19 -> 288
    //   310: astore 7
    //   312: aconst_null
    //   313: astore 6
    //   315: goto -65 -> 250
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	318	0	this	zzcen
    //   0	318	1	paramString	String
    //   34	50	2	i	int
    //   99	104	3	bool	boolean
    //   161	72	4	l	long
    //   29	285	6	localObject1	Object
    //   1	228	7	localObject2	Object
    //   248	37	7	localSQLiteException1	SQLiteException
    //   310	1	7	localSQLiteException2	SQLiteException
    //   137	98	8	localObject3	Object
    //   23	199	9	localArrayList	ArrayList
    //   127	104	10	str	String
    // Exception table:
    //   from	to	target	type
    //   92	100	248	android/database/sqlite/SQLiteException
    //   119	139	248	android/database/sqlite/SQLiteException
    //   153	172	248	android/database/sqlite/SQLiteException
    //   177	194	248	android/database/sqlite/SQLiteException
    //   194	202	248	android/database/sqlite/SQLiteException
    //   221	245	248	android/database/sqlite/SQLiteException
    //   25	92	283	finally
    //   92	100	302	finally
    //   119	139	302	finally
    //   153	172	302	finally
    //   177	194	302	finally
    //   194	202	302	finally
    //   221	245	302	finally
    //   250	269	306	finally
    //   25	92	310	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  @WorkerThread
  public final zzceg zzdQ(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 424	com/google/android/gms/internal/zzcen:zzjC	()V
    //   9: aload_0
    //   10: invokevirtual 421	com/google/android/gms/internal/zzcen:zzkD	()V
    //   13: aload_0
    //   14: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: ldc_w 867
    //   20: bipush 23
    //   22: anewarray 267	java/lang/String
    //   25: dup
    //   26: iconst_0
    //   27: ldc_w 897
    //   30: aastore
    //   31: dup
    //   32: iconst_1
    //   33: ldc_w 902
    //   36: aastore
    //   37: dup
    //   38: iconst_2
    //   39: ldc_w 907
    //   42: aastore
    //   43: dup
    //   44: iconst_3
    //   45: ldc_w 912
    //   48: aastore
    //   49: dup
    //   50: iconst_4
    //   51: ldc 59
    //   53: aastore
    //   54: dup
    //   55: iconst_5
    //   56: ldc_w 920
    //   59: aastore
    //   60: dup
    //   61: bipush 6
    //   63: ldc 39
    //   65: aastore
    //   66: dup
    //   67: bipush 7
    //   69: ldc 43
    //   71: aastore
    //   72: dup
    //   73: bipush 8
    //   75: ldc 47
    //   77: aastore
    //   78: dup
    //   79: bipush 9
    //   81: ldc 51
    //   83: aastore
    //   84: dup
    //   85: bipush 10
    //   87: ldc 55
    //   89: aastore
    //   90: dup
    //   91: bipush 11
    //   93: ldc 63
    //   95: aastore
    //   96: dup
    //   97: bipush 12
    //   99: ldc 67
    //   101: aastore
    //   102: dup
    //   103: bipush 13
    //   105: ldc 71
    //   107: aastore
    //   108: dup
    //   109: bipush 14
    //   111: ldc 75
    //   113: aastore
    //   114: dup
    //   115: bipush 15
    //   117: ldc 83
    //   119: aastore
    //   120: dup
    //   121: bipush 16
    //   123: ldc 87
    //   125: aastore
    //   126: dup
    //   127: bipush 17
    //   129: ldc 91
    //   131: aastore
    //   132: dup
    //   133: bipush 18
    //   135: ldc 95
    //   137: aastore
    //   138: dup
    //   139: bipush 19
    //   141: ldc 99
    //   143: aastore
    //   144: dup
    //   145: bipush 20
    //   147: ldc 103
    //   149: aastore
    //   150: dup
    //   151: bipush 21
    //   153: ldc 107
    //   155: aastore
    //   156: dup
    //   157: bipush 22
    //   159: ldc 111
    //   161: aastore
    //   162: ldc_w 869
    //   165: iconst_1
    //   166: anewarray 267	java/lang/String
    //   169: dup
    //   170: iconst_0
    //   171: aload_1
    //   172: aastore
    //   173: aconst_null
    //   174: aconst_null
    //   175: aconst_null
    //   176: invokevirtual 413	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   179: astore 7
    //   181: aload 7
    //   183: astore 6
    //   185: aload 7
    //   187: invokeinterface 184 1 0
    //   192: istore_3
    //   193: iload_3
    //   194: ifne +19 -> 213
    //   197: aload 7
    //   199: ifnull +10 -> 209
    //   202: aload 7
    //   204: invokeinterface 191 1 0
    //   209: aconst_null
    //   210: astore_1
    //   211: aload_1
    //   212: areturn
    //   213: aload 7
    //   215: astore 6
    //   217: new 892	com/google/android/gms/internal/zzceg
    //   220: dup
    //   221: aload_0
    //   222: getfield 1262	com/google/android/gms/internal/zzcen:zzboe	Lcom/google/android/gms/internal/zzcgl;
    //   225: aload_1
    //   226: invokespecial 1265	com/google/android/gms/internal/zzceg:<init>	(Lcom/google/android/gms/internal/zzcgl;Ljava/lang/String;)V
    //   229: astore 8
    //   231: aload 7
    //   233: astore 6
    //   235: aload 8
    //   237: aload 7
    //   239: iconst_0
    //   240: invokeinterface 252 2 0
    //   245: invokevirtual 1268	com/google/android/gms/internal/zzceg:zzdG	(Ljava/lang/String;)V
    //   248: aload 7
    //   250: astore 6
    //   252: aload 8
    //   254: aload 7
    //   256: iconst_1
    //   257: invokeinterface 252 2 0
    //   262: invokevirtual 1271	com/google/android/gms/internal/zzceg:zzdH	(Ljava/lang/String;)V
    //   265: aload 7
    //   267: astore 6
    //   269: aload 8
    //   271: aload 7
    //   273: iconst_2
    //   274: invokeinterface 252 2 0
    //   279: invokevirtual 1274	com/google/android/gms/internal/zzceg:zzdI	(Ljava/lang/String;)V
    //   282: aload 7
    //   284: astore 6
    //   286: aload 8
    //   288: aload 7
    //   290: iconst_3
    //   291: invokeinterface 188 2 0
    //   296: invokevirtual 1278	com/google/android/gms/internal/zzceg:zzQ	(J)V
    //   299: aload 7
    //   301: astore 6
    //   303: aload 8
    //   305: aload 7
    //   307: iconst_4
    //   308: invokeinterface 188 2 0
    //   313: invokevirtual 1280	com/google/android/gms/internal/zzceg:zzL	(J)V
    //   316: aload 7
    //   318: astore 6
    //   320: aload 8
    //   322: aload 7
    //   324: iconst_5
    //   325: invokeinterface 188 2 0
    //   330: invokevirtual 1283	com/google/android/gms/internal/zzceg:zzM	(J)V
    //   333: aload 7
    //   335: astore 6
    //   337: aload 8
    //   339: aload 7
    //   341: bipush 6
    //   343: invokeinterface 252 2 0
    //   348: invokevirtual 1286	com/google/android/gms/internal/zzceg:setAppVersion	(Ljava/lang/String;)V
    //   351: aload 7
    //   353: astore 6
    //   355: aload 8
    //   357: aload 7
    //   359: bipush 7
    //   361: invokeinterface 252 2 0
    //   366: invokevirtual 1289	com/google/android/gms/internal/zzceg:zzdK	(Ljava/lang/String;)V
    //   369: aload 7
    //   371: astore 6
    //   373: aload 8
    //   375: aload 7
    //   377: bipush 8
    //   379: invokeinterface 188 2 0
    //   384: invokevirtual 1292	com/google/android/gms/internal/zzceg:zzO	(J)V
    //   387: aload 7
    //   389: astore 6
    //   391: aload 8
    //   393: aload 7
    //   395: bipush 9
    //   397: invokeinterface 188 2 0
    //   402: invokevirtual 1295	com/google/android/gms/internal/zzceg:zzP	(J)V
    //   405: aload 7
    //   407: astore 6
    //   409: aload 7
    //   411: bipush 10
    //   413: invokeinterface 1299 2 0
    //   418: ifeq +322 -> 740
    //   421: iconst_1
    //   422: istore_2
    //   423: goto +440 -> 863
    //   426: aload 7
    //   428: astore 6
    //   430: aload 8
    //   432: iload_3
    //   433: invokevirtual 1303	com/google/android/gms/internal/zzceg:setMeasurementEnabled	(Z)V
    //   436: aload 7
    //   438: astore 6
    //   440: aload 8
    //   442: aload 7
    //   444: bipush 11
    //   446: invokeinterface 188 2 0
    //   451: invokevirtual 1306	com/google/android/gms/internal/zzceg:zzT	(J)V
    //   454: aload 7
    //   456: astore 6
    //   458: aload 8
    //   460: aload 7
    //   462: bipush 12
    //   464: invokeinterface 188 2 0
    //   469: invokevirtual 1309	com/google/android/gms/internal/zzceg:zzU	(J)V
    //   472: aload 7
    //   474: astore 6
    //   476: aload 8
    //   478: aload 7
    //   480: bipush 13
    //   482: invokeinterface 188 2 0
    //   487: invokevirtual 1312	com/google/android/gms/internal/zzceg:zzV	(J)V
    //   490: aload 7
    //   492: astore 6
    //   494: aload 8
    //   496: aload 7
    //   498: bipush 14
    //   500: invokeinterface 188 2 0
    //   505: invokevirtual 1315	com/google/android/gms/internal/zzceg:zzW	(J)V
    //   508: aload 7
    //   510: astore 6
    //   512: aload 8
    //   514: aload 7
    //   516: bipush 15
    //   518: invokeinterface 188 2 0
    //   523: invokevirtual 1318	com/google/android/gms/internal/zzceg:zzR	(J)V
    //   526: aload 7
    //   528: astore 6
    //   530: aload 8
    //   532: aload 7
    //   534: bipush 16
    //   536: invokeinterface 188 2 0
    //   541: invokevirtual 1321	com/google/android/gms/internal/zzceg:zzS	(J)V
    //   544: aload 7
    //   546: astore 6
    //   548: aload 7
    //   550: bipush 17
    //   552: invokeinterface 1299 2 0
    //   557: ifeq +200 -> 757
    //   560: ldc2_w 1322
    //   563: lstore 4
    //   565: aload 7
    //   567: astore 6
    //   569: aload 8
    //   571: lload 4
    //   573: invokevirtual 1326	com/google/android/gms/internal/zzceg:zzN	(J)V
    //   576: aload 7
    //   578: astore 6
    //   580: aload 8
    //   582: aload 7
    //   584: bipush 18
    //   586: invokeinterface 252 2 0
    //   591: invokevirtual 1329	com/google/android/gms/internal/zzceg:zzdJ	(Ljava/lang/String;)V
    //   594: aload 7
    //   596: astore 6
    //   598: aload 8
    //   600: aload 7
    //   602: bipush 19
    //   604: invokeinterface 188 2 0
    //   609: invokevirtual 1332	com/google/android/gms/internal/zzceg:zzY	(J)V
    //   612: aload 7
    //   614: astore 6
    //   616: aload 8
    //   618: aload 7
    //   620: bipush 20
    //   622: invokeinterface 188 2 0
    //   627: invokevirtual 1335	com/google/android/gms/internal/zzceg:zzX	(J)V
    //   630: aload 7
    //   632: astore 6
    //   634: aload 8
    //   636: aload 7
    //   638: bipush 21
    //   640: invokeinterface 252 2 0
    //   645: invokevirtual 1338	com/google/android/gms/internal/zzceg:zzdL	(Ljava/lang/String;)V
    //   648: aload 7
    //   650: astore 6
    //   652: aload 7
    //   654: bipush 22
    //   656: invokeinterface 1299 2 0
    //   661: ifeq +115 -> 776
    //   664: lconst_0
    //   665: lstore 4
    //   667: aload 7
    //   669: astore 6
    //   671: aload 8
    //   673: lload 4
    //   675: invokevirtual 1341	com/google/android/gms/internal/zzceg:zzZ	(J)V
    //   678: aload 7
    //   680: astore 6
    //   682: aload 8
    //   684: invokevirtual 1344	com/google/android/gms/internal/zzceg:zzwI	()V
    //   687: aload 7
    //   689: astore 6
    //   691: aload 7
    //   693: invokeinterface 648 1 0
    //   698: ifeq +24 -> 722
    //   701: aload 7
    //   703: astore 6
    //   705: aload_0
    //   706: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   709: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   712: ldc_w 1346
    //   715: aload_1
    //   716: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   719: invokevirtual 228	com/google/android/gms/internal/zzcfn:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   722: aload 8
    //   724: astore_1
    //   725: aload 7
    //   727: ifnull -516 -> 211
    //   730: aload 7
    //   732: invokeinterface 191 1 0
    //   737: aload 8
    //   739: areturn
    //   740: aload 7
    //   742: astore 6
    //   744: aload 7
    //   746: bipush 10
    //   748: invokeinterface 732 2 0
    //   753: istore_2
    //   754: goto +109 -> 863
    //   757: aload 7
    //   759: astore 6
    //   761: aload 7
    //   763: bipush 17
    //   765: invokeinterface 732 2 0
    //   770: i2l
    //   771: lstore 4
    //   773: goto -208 -> 565
    //   776: aload 7
    //   778: astore 6
    //   780: aload 7
    //   782: bipush 22
    //   784: invokeinterface 188 2 0
    //   789: lstore 4
    //   791: goto -124 -> 667
    //   794: astore 8
    //   796: aconst_null
    //   797: astore 7
    //   799: aload 7
    //   801: astore 6
    //   803: aload_0
    //   804: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   807: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   810: ldc_w 1348
    //   813: aload_1
    //   814: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   817: aload 8
    //   819: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   822: aload 7
    //   824: ifnull +10 -> 834
    //   827: aload 7
    //   829: invokeinterface 191 1 0
    //   834: aconst_null
    //   835: areturn
    //   836: astore_1
    //   837: aconst_null
    //   838: astore 6
    //   840: aload 6
    //   842: ifnull +10 -> 852
    //   845: aload 6
    //   847: invokeinterface 191 1 0
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
    //   0	877	0	this	zzcen
    //   0	877	1	paramString	String
    //   422	442	2	i	int
    //   192	682	3	bool	boolean
    //   563	227	4	l	long
    //   183	663	6	localCursor1	Cursor
    //   179	649	7	localCursor2	Cursor
    //   229	509	8	localzzceg	zzceg
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
  
  public final long zzdR(String paramString)
  {
    zzbo.zzcF(paramString);
    zzjC();
    zzkD();
    try
    {
      int i = getWritableDatabase().delete("raw_events", "rowid in (select rowid from raw_events where app_id=? order by rowid desc limit -1 offset ?)", new String[] { paramString, String.valueOf(Math.max(0, Math.min(1000000, zzwH().zzb(paramString, zzcfb.zzbqk)))) });
      return i;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzwF().zzyx().zze("Error deleting over the limit events. appId", zzcfl.zzdZ(paramString), localSQLiteException);
    }
    return 0L;
  }
  
  /* Error */
  @WorkerThread
  public final byte[] zzdS(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 424	com/google/android/gms/internal/zzcen:zzjC	()V
    //   9: aload_0
    //   10: invokevirtual 421	com/google/android/gms/internal/zzcen:zzkD	()V
    //   13: aload_0
    //   14: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: ldc_w 867
    //   20: iconst_1
    //   21: anewarray 267	java/lang/String
    //   24: dup
    //   25: iconst_0
    //   26: ldc 79
    //   28: aastore
    //   29: ldc_w 869
    //   32: iconst_1
    //   33: anewarray 267	java/lang/String
    //   36: dup
    //   37: iconst_0
    //   38: aload_1
    //   39: aastore
    //   40: aconst_null
    //   41: aconst_null
    //   42: aconst_null
    //   43: invokevirtual 413	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   46: astore 4
    //   48: aload 4
    //   50: astore_3
    //   51: aload 4
    //   53: invokeinterface 184 1 0
    //   58: istore_2
    //   59: iload_2
    //   60: ifne +19 -> 79
    //   63: aload 4
    //   65: ifnull +10 -> 75
    //   68: aload 4
    //   70: invokeinterface 191 1 0
    //   75: aconst_null
    //   76: astore_1
    //   77: aload_1
    //   78: areturn
    //   79: aload 4
    //   81: astore_3
    //   82: aload 4
    //   84: iconst_0
    //   85: invokeinterface 740 2 0
    //   90: astore 5
    //   92: aload 4
    //   94: astore_3
    //   95: aload 4
    //   97: invokeinterface 648 1 0
    //   102: ifeq +23 -> 125
    //   105: aload 4
    //   107: astore_3
    //   108: aload_0
    //   109: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   112: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   115: ldc_w 1362
    //   118: aload_1
    //   119: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   122: invokevirtual 228	com/google/android/gms/internal/zzcfn:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   125: aload 5
    //   127: astore_1
    //   128: aload 4
    //   130: ifnull -53 -> 77
    //   133: aload 4
    //   135: invokeinterface 191 1 0
    //   140: aload 5
    //   142: areturn
    //   143: astore 5
    //   145: aconst_null
    //   146: astore 4
    //   148: aload 4
    //   150: astore_3
    //   151: aload_0
    //   152: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   155: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   158: ldc_w 1364
    //   161: aload_1
    //   162: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   165: aload 5
    //   167: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   170: aload 4
    //   172: ifnull +10 -> 182
    //   175: aload 4
    //   177: invokeinterface 191 1 0
    //   182: aconst_null
    //   183: areturn
    //   184: astore_1
    //   185: aconst_null
    //   186: astore_3
    //   187: aload_3
    //   188: ifnull +9 -> 197
    //   191: aload_3
    //   192: invokeinterface 191 1 0
    //   197: aload_1
    //   198: athrow
    //   199: astore_1
    //   200: goto -13 -> 187
    //   203: astore 5
    //   205: goto -57 -> 148
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	208	0	this	zzcen
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
  
  /* Error */
  final Map<Integer, zzcka> zzdT(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 421	com/google/android/gms/internal/zzcen:zzkD	()V
    //   4: aload_0
    //   5: invokevirtual 424	com/google/android/gms/internal/zzcen:zzjC	()V
    //   8: aload_1
    //   9: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: astore 4
    //   19: aload 4
    //   21: ldc_w 591
    //   24: iconst_2
    //   25: anewarray 267	java/lang/String
    //   28: dup
    //   29: iconst_0
    //   30: ldc_w 468
    //   33: aastore
    //   34: dup
    //   35: iconst_1
    //   36: ldc_w 1368
    //   39: aastore
    //   40: ldc_w 869
    //   43: iconst_1
    //   44: anewarray 267	java/lang/String
    //   47: dup
    //   48: iconst_0
    //   49: aload_1
    //   50: aastore
    //   51: aconst_null
    //   52: aconst_null
    //   53: aconst_null
    //   54: invokevirtual 413	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   57: astore 5
    //   59: aload 5
    //   61: astore 4
    //   63: aload 5
    //   65: invokeinterface 184 1 0
    //   70: istore_3
    //   71: iload_3
    //   72: ifne +19 -> 91
    //   75: aload 5
    //   77: ifnull +10 -> 87
    //   80: aload 5
    //   82: invokeinterface 191 1 0
    //   87: aconst_null
    //   88: astore_1
    //   89: aload_1
    //   90: areturn
    //   91: aload 5
    //   93: astore 4
    //   95: new 19	android/support/v4/util/ArrayMap
    //   98: dup
    //   99: invokespecial 772	android/support/v4/util/ArrayMap:<init>	()V
    //   102: astore 6
    //   104: aload 5
    //   106: astore 4
    //   108: aload 5
    //   110: iconst_0
    //   111: invokeinterface 732 2 0
    //   116: istore_2
    //   117: aload 5
    //   119: astore 4
    //   121: aload 5
    //   123: iconst_1
    //   124: invokeinterface 740 2 0
    //   129: astore 7
    //   131: aload 5
    //   133: astore 4
    //   135: aload 7
    //   137: iconst_0
    //   138: aload 7
    //   140: arraylength
    //   141: invokestatic 782	com/google/android/gms/internal/adg:zzb	([BII)Lcom/google/android/gms/internal/adg;
    //   144: astore 7
    //   146: aload 5
    //   148: astore 4
    //   150: new 1370	com/google/android/gms/internal/zzcka
    //   153: dup
    //   154: invokespecial 1371	com/google/android/gms/internal/zzcka:<init>	()V
    //   157: astore 8
    //   159: aload 5
    //   161: astore 4
    //   163: aload 8
    //   165: aload 7
    //   167: invokevirtual 1372	com/google/android/gms/internal/zzcka:zza	(Lcom/google/android/gms/internal/adg;)Lcom/google/android/gms/internal/adp;
    //   170: pop
    //   171: aload 5
    //   173: astore 4
    //   175: aload 6
    //   177: iload_2
    //   178: invokestatic 224	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   181: aload 8
    //   183: invokeinterface 35 3 0
    //   188: pop
    //   189: aload 5
    //   191: astore 4
    //   193: aload 5
    //   195: invokeinterface 648 1 0
    //   200: istore_3
    //   201: iload_3
    //   202: ifne -98 -> 104
    //   205: aload 6
    //   207: astore_1
    //   208: aload 5
    //   210: ifnull -121 -> 89
    //   213: aload 5
    //   215: invokeinterface 191 1 0
    //   220: aload 6
    //   222: areturn
    //   223: astore 7
    //   225: aload 5
    //   227: astore 4
    //   229: aload_0
    //   230: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   233: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   236: ldc_w 1374
    //   239: aload_1
    //   240: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   243: iload_2
    //   244: invokestatic 224	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   247: aload 7
    //   249: invokevirtual 447	com/google/android/gms/internal/zzcfn:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   252: goto -63 -> 189
    //   255: astore 6
    //   257: aload 5
    //   259: astore 4
    //   261: aload_0
    //   262: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   265: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   268: ldc_w 1376
    //   271: aload_1
    //   272: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   275: aload 6
    //   277: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   280: aload 5
    //   282: ifnull +10 -> 292
    //   285: aload 5
    //   287: invokeinterface 191 1 0
    //   292: aconst_null
    //   293: areturn
    //   294: astore_1
    //   295: aconst_null
    //   296: astore 4
    //   298: aload 4
    //   300: ifnull +10 -> 310
    //   303: aload 4
    //   305: invokeinterface 191 1 0
    //   310: aload_1
    //   311: athrow
    //   312: astore_1
    //   313: goto -15 -> 298
    //   316: astore 6
    //   318: aconst_null
    //   319: astore 5
    //   321: goto -64 -> 257
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	324	0	this	zzcen
    //   0	324	1	paramString	String
    //   116	128	2	i	int
    //   70	132	3	bool	boolean
    //   17	287	4	localObject1	Object
    //   57	263	5	localCursor	Cursor
    //   102	119	6	localArrayMap	ArrayMap
    //   255	21	6	localSQLiteException1	SQLiteException
    //   316	1	6	localSQLiteException2	SQLiteException
    //   129	37	7	localObject2	Object
    //   223	25	7	localIOException	IOException
    //   157	25	8	localzzcka	zzcka
    // Exception table:
    //   from	to	target	type
    //   163	171	223	java/io/IOException
    //   63	71	255	android/database/sqlite/SQLiteException
    //   95	104	255	android/database/sqlite/SQLiteException
    //   108	117	255	android/database/sqlite/SQLiteException
    //   121	131	255	android/database/sqlite/SQLiteException
    //   135	146	255	android/database/sqlite/SQLiteException
    //   150	159	255	android/database/sqlite/SQLiteException
    //   163	171	255	android/database/sqlite/SQLiteException
    //   175	189	255	android/database/sqlite/SQLiteException
    //   193	201	255	android/database/sqlite/SQLiteException
    //   229	252	255	android/database/sqlite/SQLiteException
    //   19	59	294	finally
    //   63	71	312	finally
    //   95	104	312	finally
    //   108	117	312	finally
    //   121	131	312	finally
    //   135	146	312	finally
    //   150	159	312	finally
    //   163	171	312	finally
    //   175	189	312	finally
    //   193	201	312	finally
    //   229	252	312	finally
    //   261	280	312	finally
    //   19	59	316	android/database/sqlite/SQLiteException
  }
  
  public final long zzdU(String paramString)
  {
    zzbo.zzcF(paramString);
    return zza("select count(1) from events where app_id=? and name not like '!_%' escape '!'", new String[] { paramString }, 0L);
  }
  
  /* Error */
  @WorkerThread
  public final List<zzcjk> zzh(String paramString1, String paramString2, String paramString3)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 8
    //   3: aload_1
    //   4: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_0
    //   9: invokevirtual 424	com/google/android/gms/internal/zzcen:zzjC	()V
    //   12: aload_0
    //   13: invokevirtual 421	com/google/android/gms/internal/zzcen:zzkD	()V
    //   16: new 567	java/util/ArrayList
    //   19: dup
    //   20: invokespecial 568	java/util/ArrayList:<init>	()V
    //   23: astore 9
    //   25: new 567	java/util/ArrayList
    //   28: dup
    //   29: iconst_3
    //   30: invokespecial 1383	java/util/ArrayList:<init>	(I)V
    //   33: astore 10
    //   35: aload 10
    //   37: aload_1
    //   38: invokeinterface 585 2 0
    //   43: pop
    //   44: new 344	java/lang/StringBuilder
    //   47: dup
    //   48: ldc_w 869
    //   51: invokespecial 695	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   54: astore 7
    //   56: aload_2
    //   57: invokestatic 433	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   60: ifne +21 -> 81
    //   63: aload 10
    //   65: aload_2
    //   66: invokeinterface 585 2 0
    //   71: pop
    //   72: aload 7
    //   74: ldc_w 1385
    //   77: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   80: pop
    //   81: aload_3
    //   82: invokestatic 433	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   85: ifne +30 -> 115
    //   88: aload 10
    //   90: aload_3
    //   91: invokestatic 347	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   94: ldc_w 1387
    //   97: invokevirtual 1390	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   100: invokeinterface 585 2 0
    //   105: pop
    //   106: aload 7
    //   108: ldc_w 1392
    //   111: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   114: pop
    //   115: aload 10
    //   117: aload 10
    //   119: invokeinterface 573 1 0
    //   124: anewarray 267	java/lang/String
    //   127: invokeinterface 1396 2 0
    //   132: checkcast 1398	[Ljava/lang/String;
    //   135: astore 10
    //   137: aload_0
    //   138: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   141: astore 11
    //   143: aload 7
    //   145: invokevirtual 363	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   148: astore 7
    //   150: invokestatic 1249	com/google/android/gms/internal/zzcem:zzxt	()I
    //   153: pop
    //   154: aload 11
    //   156: ldc_w 664
    //   159: iconst_4
    //   160: anewarray 267	java/lang/String
    //   163: dup
    //   164: iconst_0
    //   165: ldc_w 407
    //   168: aastore
    //   169: dup
    //   170: iconst_1
    //   171: ldc_w 678
    //   174: aastore
    //   175: dup
    //   176: iconst_2
    //   177: ldc_w 680
    //   180: aastore
    //   181: dup
    //   182: iconst_3
    //   183: ldc 27
    //   185: aastore
    //   186: aload 7
    //   188: aload 10
    //   190: aconst_null
    //   191: aconst_null
    //   192: ldc_w 1230
    //   195: ldc_w 1232
    //   198: invokevirtual 1235	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   201: astore 7
    //   203: aload_2
    //   204: astore 8
    //   206: aload 7
    //   208: invokeinterface 184 1 0
    //   213: istore 4
    //   215: iload 4
    //   217: ifne +18 -> 235
    //   220: aload 7
    //   222: ifnull +10 -> 232
    //   225: aload 7
    //   227: invokeinterface 191 1 0
    //   232: aload 9
    //   234: areturn
    //   235: aload_2
    //   236: astore 8
    //   238: aload 9
    //   240: invokeinterface 573 1 0
    //   245: invokestatic 1249	com/google/android/gms/internal/zzcem:zzxt	()I
    //   248: if_icmplt +40 -> 288
    //   251: aload_2
    //   252: astore 8
    //   254: aload_0
    //   255: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   258: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   261: ldc_w 1400
    //   264: invokestatic 1249	com/google/android/gms/internal/zzcem:zzxt	()I
    //   267: invokestatic 224	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   270: invokevirtual 228	com/google/android/gms/internal/zzcfn:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   273: aload 7
    //   275: ifnull +10 -> 285
    //   278: aload 7
    //   280: invokeinterface 191 1 0
    //   285: aload 9
    //   287: areturn
    //   288: aload_2
    //   289: astore 8
    //   291: aload 7
    //   293: iconst_0
    //   294: invokeinterface 252 2 0
    //   299: astore 10
    //   301: aload_2
    //   302: astore 8
    //   304: aload 7
    //   306: iconst_1
    //   307: invokeinterface 188 2 0
    //   312: lstore 5
    //   314: aload_2
    //   315: astore 8
    //   317: aload_0
    //   318: aload 7
    //   320: iconst_2
    //   321: invokespecial 682	com/google/android/gms/internal/zzcen:zza	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   324: astore 11
    //   326: aload_2
    //   327: astore 8
    //   329: aload 7
    //   331: iconst_3
    //   332: invokeinterface 252 2 0
    //   337: astore_2
    //   338: aload 11
    //   340: ifnonnull +35 -> 375
    //   343: aload_0
    //   344: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   347: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   350: ldc_w 1402
    //   353: aload_1
    //   354: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   357: aload_2
    //   358: aload_3
    //   359: invokevirtual 447	com/google/android/gms/internal/zzcfn:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   362: aload 7
    //   364: invokeinterface 648 1 0
    //   369: ifne -134 -> 235
    //   372: goto -99 -> 273
    //   375: aload 9
    //   377: new 684	com/google/android/gms/internal/zzcjk
    //   380: dup
    //   381: aload_1
    //   382: aload_2
    //   383: aload 10
    //   385: lload 5
    //   387: aload 11
    //   389: invokespecial 687	com/google/android/gms/internal/zzcjk:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   392: invokeinterface 585 2 0
    //   397: pop
    //   398: goto -36 -> 362
    //   401: astore 8
    //   403: aload 7
    //   405: astore_3
    //   406: aload 8
    //   408: astore 7
    //   410: aload_0
    //   411: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   414: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   417: ldc_w 1404
    //   420: aload_1
    //   421: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   424: aload_2
    //   425: aload 7
    //   427: invokevirtual 447	com/google/android/gms/internal/zzcfn:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   430: aload_3
    //   431: ifnull +9 -> 440
    //   434: aload_3
    //   435: invokeinterface 191 1 0
    //   440: aconst_null
    //   441: areturn
    //   442: astore_1
    //   443: aload 8
    //   445: astore_2
    //   446: aload_2
    //   447: ifnull +9 -> 456
    //   450: aload_2
    //   451: invokeinterface 191 1 0
    //   456: aload_1
    //   457: athrow
    //   458: astore_1
    //   459: aload 7
    //   461: astore_2
    //   462: goto -16 -> 446
    //   465: astore_1
    //   466: aload_3
    //   467: astore_2
    //   468: goto -22 -> 446
    //   471: astore 7
    //   473: aconst_null
    //   474: astore_3
    //   475: goto -65 -> 410
    //   478: astore_2
    //   479: aload 7
    //   481: astore_3
    //   482: aload_2
    //   483: astore 7
    //   485: aload 8
    //   487: astore_2
    //   488: goto -78 -> 410
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	491	0	this	zzcen
    //   0	491	1	paramString1	String
    //   0	491	2	paramString2	String
    //   0	491	3	paramString3	String
    //   213	3	4	bool	boolean
    //   312	74	5	l	long
    //   54	406	7	localObject1	Object
    //   471	9	7	localSQLiteException1	SQLiteException
    //   483	1	7	str1	String
    //   1	327	8	str2	String
    //   401	85	8	localSQLiteException2	SQLiteException
    //   23	353	9	localArrayList	ArrayList
    //   33	351	10	localObject2	Object
    //   141	247	11	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   343	362	401	android/database/sqlite/SQLiteException
    //   362	372	401	android/database/sqlite/SQLiteException
    //   375	398	401	android/database/sqlite/SQLiteException
    //   25	81	442	finally
    //   81	115	442	finally
    //   115	203	442	finally
    //   206	215	458	finally
    //   238	251	458	finally
    //   254	273	458	finally
    //   291	301	458	finally
    //   304	314	458	finally
    //   317	326	458	finally
    //   329	338	458	finally
    //   343	362	458	finally
    //   362	372	458	finally
    //   375	398	458	finally
    //   410	430	465	finally
    //   25	81	471	android/database/sqlite/SQLiteException
    //   81	115	471	android/database/sqlite/SQLiteException
    //   115	203	471	android/database/sqlite/SQLiteException
    //   206	215	478	android/database/sqlite/SQLiteException
    //   238	251	478	android/database/sqlite/SQLiteException
    //   254	273	478	android/database/sqlite/SQLiteException
    //   291	301	478	android/database/sqlite/SQLiteException
    //   304	314	478	android/database/sqlite/SQLiteException
    //   317	326	478	android/database/sqlite/SQLiteException
    //   329	338	478	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  public final List<zzcek> zzi(String paramString1, String paramString2, String paramString3)
  {
    zzbo.zzcF(paramString1);
    zzjC();
    zzkD();
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
  
  protected final void zzjD() {}
  
  /* Error */
  @WorkerThread
  public final List<android.util.Pair<zzcjz, Long>> zzl(String paramString, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore 5
    //   3: aload_0
    //   4: invokevirtual 424	com/google/android/gms/internal/zzcen:zzjC	()V
    //   7: aload_0
    //   8: invokevirtual 421	com/google/android/gms/internal/zzcen:zzkD	()V
    //   11: iload_2
    //   12: ifle +112 -> 124
    //   15: iconst_1
    //   16: istore 4
    //   18: iload 4
    //   20: invokestatic 1414	com/google/android/gms/common/internal/zzbo:zzaf	(Z)V
    //   23: iload_3
    //   24: ifle +106 -> 130
    //   27: iload 5
    //   29: istore 4
    //   31: iload 4
    //   33: invokestatic 1414	com/google/android/gms/common/internal/zzbo:zzaf	(Z)V
    //   36: aload_1
    //   37: invokestatic 261	com/google/android/gms/common/internal/zzbo:zzcF	(Ljava/lang/String;)Ljava/lang/String;
    //   40: pop
    //   41: aload_0
    //   42: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   45: ldc_w 1211
    //   48: iconst_2
    //   49: anewarray 267	java/lang/String
    //   52: dup
    //   53: iconst_0
    //   54: ldc_w 1230
    //   57: aastore
    //   58: dup
    //   59: iconst_1
    //   60: ldc_w 477
    //   63: aastore
    //   64: ldc_w 869
    //   67: iconst_1
    //   68: anewarray 267	java/lang/String
    //   71: dup
    //   72: iconst_0
    //   73: aload_1
    //   74: aastore
    //   75: aconst_null
    //   76: aconst_null
    //   77: ldc_w 1230
    //   80: iload_2
    //   81: invokestatic 1034	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   84: invokevirtual 1235	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   87: astore 8
    //   89: aload 8
    //   91: invokeinterface 184 1 0
    //   96: ifne +40 -> 136
    //   99: invokestatic 1243	java/util/Collections:emptyList	()Ljava/util/List;
    //   102: astore 9
    //   104: aload 9
    //   106: astore_1
    //   107: aload 8
    //   109: ifnull +13 -> 122
    //   112: aload 8
    //   114: invokeinterface 191 1 0
    //   119: aload 9
    //   121: astore_1
    //   122: aload_1
    //   123: areturn
    //   124: iconst_0
    //   125: istore 4
    //   127: goto -109 -> 18
    //   130: iconst_0
    //   131: istore 4
    //   133: goto -102 -> 31
    //   136: new 567	java/util/ArrayList
    //   139: dup
    //   140: invokespecial 568	java/util/ArrayList:<init>	()V
    //   143: astore 9
    //   145: iconst_0
    //   146: istore_2
    //   147: aload 8
    //   149: iconst_0
    //   150: invokeinterface 188 2 0
    //   155: lstore 6
    //   157: aload 8
    //   159: iconst_1
    //   160: invokeinterface 740 2 0
    //   165: astore 10
    //   167: aload_0
    //   168: invokevirtual 736	com/google/android/gms/internal/zzcen:zzwB	()Lcom/google/android/gms/internal/zzcjl;
    //   171: aload 10
    //   173: invokevirtual 1417	com/google/android/gms/internal/zzcjl:zzm	([B)[B
    //   176: astore 10
    //   178: aload 9
    //   180: invokeinterface 1418 1 0
    //   185: ifne +12 -> 197
    //   188: aload 10
    //   190: arraylength
    //   191: iload_2
    //   192: iadd
    //   193: iload_3
    //   194: if_icmpgt +74 -> 268
    //   197: aload 10
    //   199: iconst_0
    //   200: aload 10
    //   202: arraylength
    //   203: invokestatic 782	com/google/android/gms/internal/adg:zzb	([BII)Lcom/google/android/gms/internal/adg;
    //   206: astore 11
    //   208: new 826	com/google/android/gms/internal/zzcjz
    //   211: dup
    //   212: invokespecial 1419	com/google/android/gms/internal/zzcjz:<init>	()V
    //   215: astore 12
    //   217: aload 12
    //   219: aload 11
    //   221: invokevirtual 1420	com/google/android/gms/internal/zzcjz:zza	(Lcom/google/android/gms/internal/adg;)Lcom/google/android/gms/internal/adp;
    //   224: pop
    //   225: aload 10
    //   227: arraylength
    //   228: iload_2
    //   229: iadd
    //   230: istore_2
    //   231: aload 9
    //   233: aload 12
    //   235: lload 6
    //   237: invokestatic 239	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   240: invokestatic 1426	android/util/Pair:create	(Ljava/lang/Object;Ljava/lang/Object;)Landroid/util/Pair;
    //   243: invokeinterface 585 2 0
    //   248: pop
    //   249: aload 8
    //   251: invokeinterface 648 1 0
    //   256: istore 4
    //   258: iload 4
    //   260: ifeq +8 -> 268
    //   263: iload_2
    //   264: iload_3
    //   265: if_icmple +147 -> 412
    //   268: aload 9
    //   270: astore_1
    //   271: aload 8
    //   273: ifnull -151 -> 122
    //   276: aload 8
    //   278: invokeinterface 191 1 0
    //   283: aload 9
    //   285: areturn
    //   286: astore 10
    //   288: aload_0
    //   289: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   292: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   295: ldc_w 1428
    //   298: aload_1
    //   299: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   302: aload 10
    //   304: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   307: goto -58 -> 249
    //   310: astore 10
    //   312: aload_0
    //   313: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   316: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   319: ldc_w 1430
    //   322: aload_1
    //   323: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   326: aload 10
    //   328: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   331: goto -82 -> 249
    //   334: astore 9
    //   336: aconst_null
    //   337: astore 8
    //   339: aload_0
    //   340: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   343: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   346: ldc_w 1432
    //   349: aload_1
    //   350: invokestatic 439	com/google/android/gms/internal/zzcfl:zzdZ	(Ljava/lang/String;)Ljava/lang/Object;
    //   353: aload 9
    //   355: invokevirtual 209	com/google/android/gms/internal/zzcfn:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   358: invokestatic 1243	java/util/Collections:emptyList	()Ljava/util/List;
    //   361: astore 9
    //   363: aload 9
    //   365: astore_1
    //   366: aload 8
    //   368: ifnull -246 -> 122
    //   371: aload 8
    //   373: invokeinterface 191 1 0
    //   378: aload 9
    //   380: areturn
    //   381: astore_1
    //   382: aconst_null
    //   383: astore 8
    //   385: aload 8
    //   387: ifnull +10 -> 397
    //   390: aload 8
    //   392: invokeinterface 191 1 0
    //   397: aload_1
    //   398: athrow
    //   399: astore_1
    //   400: goto -15 -> 385
    //   403: astore_1
    //   404: goto -19 -> 385
    //   407: astore 9
    //   409: goto -70 -> 339
    //   412: goto -265 -> 147
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	415	0	this	zzcen
    //   0	415	1	paramString	String
    //   0	415	2	paramInt1	int
    //   0	415	3	paramInt2	int
    //   16	243	4	bool1	boolean
    //   1	27	5	bool2	boolean
    //   155	81	6	l	long
    //   87	304	8	localCursor	Cursor
    //   102	182	9	localObject	Object
    //   334	20	9	localSQLiteException1	SQLiteException
    //   361	18	9	localList	List
    //   407	1	9	localSQLiteException2	SQLiteException
    //   165	61	10	arrayOfByte	byte[]
    //   286	17	10	localIOException1	IOException
    //   310	17	10	localIOException2	IOException
    //   206	14	11	localadg	adg
    //   215	19	12	localzzcjz	zzcjz
    // Exception table:
    //   from	to	target	type
    //   157	178	286	java/io/IOException
    //   217	225	310	java/io/IOException
    //   41	89	334	android/database/sqlite/SQLiteException
    //   41	89	381	finally
    //   89	104	399	finally
    //   136	145	399	finally
    //   147	157	399	finally
    //   157	178	399	finally
    //   178	197	399	finally
    //   197	217	399	finally
    //   217	225	399	finally
    //   225	249	399	finally
    //   249	258	399	finally
    //   288	307	399	finally
    //   312	331	399	finally
    //   339	363	403	finally
    //   89	104	407	android/database/sqlite/SQLiteException
    //   136	145	407	android/database/sqlite/SQLiteException
    //   147	157	407	android/database/sqlite/SQLiteException
    //   157	178	407	android/database/sqlite/SQLiteException
    //   178	197	407	android/database/sqlite/SQLiteException
    //   197	217	407	android/database/sqlite/SQLiteException
    //   217	225	407	android/database/sqlite/SQLiteException
    //   225	249	407	android/database/sqlite/SQLiteException
    //   249	258	407	android/database/sqlite/SQLiteException
    //   288	307	407	android/database/sqlite/SQLiteException
    //   312	331	407	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  @WorkerThread
  public final String zzyc()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: invokevirtual 172	com/google/android/gms/internal/zzcen:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   7: astore_1
    //   8: aload_1
    //   9: ldc_w 1436
    //   12: aconst_null
    //   13: invokevirtual 178	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   16: astore_1
    //   17: aload_1
    //   18: astore_2
    //   19: aload_1
    //   20: invokeinterface 184 1 0
    //   25: ifeq +29 -> 54
    //   28: aload_1
    //   29: astore_2
    //   30: aload_1
    //   31: iconst_0
    //   32: invokeinterface 252 2 0
    //   37: astore_3
    //   38: aload_3
    //   39: astore_2
    //   40: aload_1
    //   41: ifnull +11 -> 52
    //   44: aload_1
    //   45: invokeinterface 191 1 0
    //   50: aload_3
    //   51: astore_2
    //   52: aload_2
    //   53: areturn
    //   54: aload 4
    //   56: astore_2
    //   57: aload_1
    //   58: ifnull -6 -> 52
    //   61: aload_1
    //   62: invokeinterface 191 1 0
    //   67: aconst_null
    //   68: areturn
    //   69: astore_3
    //   70: aconst_null
    //   71: astore_1
    //   72: aload_1
    //   73: astore_2
    //   74: aload_0
    //   75: invokevirtual 195	com/google/android/gms/internal/zzcen:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   78: invokevirtual 201	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   81: ldc_w 1438
    //   84: aload_3
    //   85: invokevirtual 228	com/google/android/gms/internal/zzcfn:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   88: aload 4
    //   90: astore_2
    //   91: aload_1
    //   92: ifnull -40 -> 52
    //   95: aload_1
    //   96: invokeinterface 191 1 0
    //   101: aconst_null
    //   102: areturn
    //   103: astore_1
    //   104: aconst_null
    //   105: astore_2
    //   106: aload_2
    //   107: ifnull +9 -> 116
    //   110: aload_2
    //   111: invokeinterface 191 1 0
    //   116: aload_1
    //   117: athrow
    //   118: astore_1
    //   119: goto -13 -> 106
    //   122: astore_3
    //   123: goto -51 -> 72
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	this	zzcen
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
  
  public final boolean zzyd()
  {
    return zzb("select count(1) > 0 from queue where has_realtime = 1", null) != 0L;
  }
  
  @WorkerThread
  final void zzye()
  {
    zzjC();
    zzkD();
    if (!zzyk()) {}
    int i;
    do
    {
      do
      {
        long l1;
        long l2;
        do
        {
          return;
          l1 = zzwG().zzbrn.get();
          l2 = zzkq().elapsedRealtime();
        } while (Math.abs(l2 - l1) <= zzcem.zzxH());
        zzwG().zzbrn.set(l2);
        zzjC();
        zzkD();
      } while (!zzyk());
      i = getWritableDatabase().delete("queue", "abs(bundle_end_timestamp - ?) > cast(? as integer)", new String[] { String.valueOf(zzkq().currentTimeMillis()), String.valueOf(zzcem.zzxG()) });
    } while (i <= 0);
    zzwF().zzyD().zzj("Deleted stale rows. rowsDeleted", Integer.valueOf(i));
  }
  
  @WorkerThread
  public final long zzyf()
  {
    return zza("select max(bundle_end_timestamp) from queue", null, 0L);
  }
  
  @WorkerThread
  public final long zzyg()
  {
    return zza("select max(timestamp) from raw_events", null, 0L);
  }
  
  public final boolean zzyh()
  {
    return zzb("select count(1) > 0 from raw_events", null) != 0L;
  }
  
  public final boolean zzyi()
  {
    return zzb("select count(1) > 0 from raw_events where realtime = 1", null) != 0L;
  }
  
  public final long zzyj()
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
        zzwF().zzyx().zzj("Error querying raw events", localSQLiteException);
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcen.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */