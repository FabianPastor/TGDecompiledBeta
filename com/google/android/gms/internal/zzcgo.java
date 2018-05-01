package com.google.android.gms.internal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.util.zzd;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

final class zzcgo
  extends zzcjl
{
  private static final String[] zziyp = { "last_bundled_timestamp", "ALTER TABLE events ADD COLUMN last_bundled_timestamp INTEGER;", "last_sampled_complex_event_id", "ALTER TABLE events ADD COLUMN last_sampled_complex_event_id INTEGER;", "last_sampling_rate", "ALTER TABLE events ADD COLUMN last_sampling_rate INTEGER;", "last_exempt_from_sampling", "ALTER TABLE events ADD COLUMN last_exempt_from_sampling INTEGER;" };
  private static final String[] zziyq = { "origin", "ALTER TABLE user_attributes ADD COLUMN origin TEXT;" };
  private static final String[] zziyr = { "app_version", "ALTER TABLE apps ADD COLUMN app_version TEXT;", "app_store", "ALTER TABLE apps ADD COLUMN app_store TEXT;", "gmp_version", "ALTER TABLE apps ADD COLUMN gmp_version INTEGER;", "dev_cert_hash", "ALTER TABLE apps ADD COLUMN dev_cert_hash INTEGER;", "measurement_enabled", "ALTER TABLE apps ADD COLUMN measurement_enabled INTEGER;", "last_bundle_start_timestamp", "ALTER TABLE apps ADD COLUMN last_bundle_start_timestamp INTEGER;", "day", "ALTER TABLE apps ADD COLUMN day INTEGER;", "daily_public_events_count", "ALTER TABLE apps ADD COLUMN daily_public_events_count INTEGER;", "daily_events_count", "ALTER TABLE apps ADD COLUMN daily_events_count INTEGER;", "daily_conversions_count", "ALTER TABLE apps ADD COLUMN daily_conversions_count INTEGER;", "remote_config", "ALTER TABLE apps ADD COLUMN remote_config BLOB;", "config_fetched_time", "ALTER TABLE apps ADD COLUMN config_fetched_time INTEGER;", "failed_config_fetch_time", "ALTER TABLE apps ADD COLUMN failed_config_fetch_time INTEGER;", "app_version_int", "ALTER TABLE apps ADD COLUMN app_version_int INTEGER;", "firebase_instance_id", "ALTER TABLE apps ADD COLUMN firebase_instance_id TEXT;", "daily_error_events_count", "ALTER TABLE apps ADD COLUMN daily_error_events_count INTEGER;", "daily_realtime_events_count", "ALTER TABLE apps ADD COLUMN daily_realtime_events_count INTEGER;", "health_monitor_sample", "ALTER TABLE apps ADD COLUMN health_monitor_sample TEXT;", "android_id", "ALTER TABLE apps ADD COLUMN android_id INTEGER;", "adid_reporting_enabled", "ALTER TABLE apps ADD COLUMN adid_reporting_enabled INTEGER;" };
  private static final String[] zziys = { "realtime", "ALTER TABLE raw_events ADD COLUMN realtime INTEGER;" };
  private static final String[] zziyt = { "has_realtime", "ALTER TABLE queue ADD COLUMN has_realtime INTEGER;" };
  private static final String[] zziyu = { "previous_install_count", "ALTER TABLE app2 ADD COLUMN previous_install_count INTEGER;" };
  private final zzcgr zziyv = new zzcgr(this, getContext(), "google_app_measurement.db");
  private final zzclk zziyw = new zzclk(zzws());
  
  zzcgo(zzcim paramzzcim)
  {
    super(paramzzcim);
  }
  
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
      zzawy().zzazd().zze("Database error", paramString, paramArrayOfString);
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
  
  private final Object zza(Cursor paramCursor, int paramInt)
  {
    int i = paramCursor.getType(paramInt);
    switch (i)
    {
    default: 
      zzawy().zzazd().zzj("Loaded invalid unknown value type, ignoring it", Integer.valueOf(i));
      return null;
    case 0: 
      zzawy().zzazd().log("Loaded invalid null value from database");
      return null;
    case 1: 
      return Long.valueOf(paramCursor.getLong(paramInt));
    case 2: 
      return Double.valueOf(paramCursor.getDouble(paramInt));
    case 3: 
      return paramCursor.getString(paramInt);
    }
    zzawy().zzazd().log("Loaded invalid blob type value, ignoring it");
    return null;
  }
  
  private static void zza(ContentValues paramContentValues, String paramString, Object paramObject)
  {
    zzbq.zzgm(paramString);
    zzbq.checkNotNull(paramObject);
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
  
  static void zza(zzchm paramzzchm, SQLiteDatabase paramSQLiteDatabase)
  {
    if (paramzzchm == null) {
      throw new IllegalArgumentException("Monitor must not be null");
    }
    paramSQLiteDatabase = new File(paramSQLiteDatabase.getPath());
    if (!paramSQLiteDatabase.setReadable(false, false)) {
      paramzzchm.zzazf().log("Failed to turn off database read permission");
    }
    if (!paramSQLiteDatabase.setWritable(false, false)) {
      paramzzchm.zzazf().log("Failed to turn off database write permission");
    }
    if (!paramSQLiteDatabase.setReadable(true, true)) {
      paramzzchm.zzazf().log("Failed to turn on database read permission for owner");
    }
    if (!paramSQLiteDatabase.setWritable(true, true)) {
      paramzzchm.zzazf().log("Failed to turn on database write permission for owner");
    }
  }
  
  static void zza(zzchm paramzzchm, SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
    throws SQLiteException
  {
    if (paramzzchm == null) {
      throw new IllegalArgumentException("Monitor must not be null");
    }
    if (!zza(paramzzchm, paramSQLiteDatabase, paramString1)) {
      paramSQLiteDatabase.execSQL(paramString2);
    }
    try
    {
      zza(paramzzchm, paramSQLiteDatabase, paramString1, paramString3, paramArrayOfString);
      return;
    }
    catch (SQLiteException paramSQLiteDatabase)
    {
      paramzzchm.zzazd().zzj("Failed to verify columns on table that was just created", paramString1);
      throw paramSQLiteDatabase;
    }
  }
  
  private static void zza(zzchm paramzzchm, SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, String[] paramArrayOfString)
    throws SQLiteException
  {
    int j = 0;
    if (paramzzchm == null) {
      throw new IllegalArgumentException("Monitor must not be null");
    }
    Set localSet = zzb(paramSQLiteDatabase, paramString1);
    paramString2 = paramString2.split(",");
    int k = paramString2.length;
    int i = 0;
    while (i < k)
    {
      Object localObject = paramString2[i];
      if (!localSet.remove(localObject)) {
        throw new SQLiteException(String.valueOf(paramString1).length() + 35 + String.valueOf(localObject).length() + "Table " + paramString1 + " is missing required column: " + (String)localObject);
      }
      i += 1;
    }
    if (paramArrayOfString != null)
    {
      i = j;
      while (i < paramArrayOfString.length)
      {
        if (!localSet.remove(paramArrayOfString[i])) {
          paramSQLiteDatabase.execSQL(paramArrayOfString[(i + 1)]);
        }
        i += 2;
      }
    }
    if (!localSet.isEmpty()) {
      paramzzchm.zzazf().zze("Table has extra columns. table, columns", paramString1, TextUtils.join(", ", localSet));
    }
  }
  
  /* Error */
  private static boolean zza(zzchm paramzzchm, SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: ifnonnull +14 -> 18
    //   7: new 288	java/lang/IllegalArgumentException
    //   10: dup
    //   11: ldc_w 295
    //   14: invokespecial 292	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   17: athrow
    //   18: aload_1
    //   19: ldc_w 388
    //   22: iconst_1
    //   23: anewarray 19	java/lang/String
    //   26: dup
    //   27: iconst_0
    //   28: ldc_w 390
    //   31: aastore
    //   32: ldc_w 392
    //   35: iconst_1
    //   36: anewarray 19	java/lang/String
    //   39: dup
    //   40: iconst_0
    //   41: aload_2
    //   42: aastore
    //   43: aconst_null
    //   44: aconst_null
    //   45: aconst_null
    //   46: invokevirtual 396	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   49: astore_1
    //   50: aload_1
    //   51: astore 4
    //   53: aload 4
    //   55: astore_1
    //   56: aload 4
    //   58: invokeinterface 194 1 0
    //   63: istore_3
    //   64: aload 4
    //   66: ifnull +10 -> 76
    //   69: aload 4
    //   71: invokeinterface 201 1 0
    //   76: iload_3
    //   77: ireturn
    //   78: astore 5
    //   80: aconst_null
    //   81: astore 4
    //   83: aload 4
    //   85: astore_1
    //   86: aload_0
    //   87: invokevirtual 309	com/google/android/gms/internal/zzchm:zzazf	()Lcom/google/android/gms/internal/zzcho;
    //   90: ldc_w 398
    //   93: aload_2
    //   94: aload 5
    //   96: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   99: aload 4
    //   101: ifnull +10 -> 111
    //   104: aload 4
    //   106: invokeinterface 201 1 0
    //   111: iconst_0
    //   112: ireturn
    //   113: astore_0
    //   114: aload 4
    //   116: astore_1
    //   117: aload_1
    //   118: ifnull +9 -> 127
    //   121: aload_1
    //   122: invokeinterface 201 1 0
    //   127: aload_0
    //   128: athrow
    //   129: astore_0
    //   130: goto -13 -> 117
    //   133: astore 5
    //   135: goto -52 -> 83
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	138	0	paramzzchm	zzchm
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
  
  private final boolean zza(String paramString, int paramInt, zzcls paramzzcls)
  {
    zzxf();
    zzve();
    zzbq.zzgm(paramString);
    zzbq.checkNotNull(paramzzcls);
    if (TextUtils.isEmpty(paramzzcls.zzjjx))
    {
      zzawy().zzazf().zzd("Event filter had no event name. Audience definition ignored. appId, audienceId, filterId", zzchm.zzjk(paramString), Integer.valueOf(paramInt), String.valueOf(paramzzcls.zzjjw));
      return false;
    }
    try
    {
      byte[] arrayOfByte = new byte[paramzzcls.zzho()];
      Object localObject = zzfjk.zzo(arrayOfByte, 0, arrayOfByte.length);
      paramzzcls.zza((zzfjk)localObject);
      ((zzfjk)localObject).zzcwt();
      localObject = new ContentValues();
      ((ContentValues)localObject).put("app_id", paramString);
      ((ContentValues)localObject).put("audience_id", Integer.valueOf(paramInt));
      ((ContentValues)localObject).put("filter_id", paramzzcls.zzjjw);
      ((ContentValues)localObject).put("event_name", paramzzcls.zzjjx);
      ((ContentValues)localObject).put("data", arrayOfByte);
      return false;
    }
    catch (IOException paramzzcls)
    {
      try
      {
        if (getWritableDatabase().insertWithOnConflict("event_filters", null, (ContentValues)localObject, 5) == -1L) {
          zzawy().zzazd().zzj("Failed to insert event filter (got -1). appId", zzchm.zzjk(paramString));
        }
        return true;
      }
      catch (SQLiteException paramzzcls)
      {
        zzawy().zzazd().zze("Error storing event filter. appId", zzchm.zzjk(paramString), paramzzcls);
      }
      paramzzcls = paramzzcls;
      zzawy().zzazd().zze("Configuration loss. Failed to serialize event filter. appId", zzchm.zzjk(paramString), paramzzcls);
      return false;
    }
  }
  
  private final boolean zza(String paramString, int paramInt, zzclv paramzzclv)
  {
    zzxf();
    zzve();
    zzbq.zzgm(paramString);
    zzbq.checkNotNull(paramzzclv);
    if (TextUtils.isEmpty(paramzzclv.zzjkm))
    {
      zzawy().zzazf().zzd("Property filter had no property name. Audience definition ignored. appId, audienceId, filterId", zzchm.zzjk(paramString), Integer.valueOf(paramInt), String.valueOf(paramzzclv.zzjjw));
      return false;
    }
    try
    {
      byte[] arrayOfByte = new byte[paramzzclv.zzho()];
      Object localObject = zzfjk.zzo(arrayOfByte, 0, arrayOfByte.length);
      paramzzclv.zza((zzfjk)localObject);
      ((zzfjk)localObject).zzcwt();
      localObject = new ContentValues();
      ((ContentValues)localObject).put("app_id", paramString);
      ((ContentValues)localObject).put("audience_id", Integer.valueOf(paramInt));
      ((ContentValues)localObject).put("filter_id", paramzzclv.zzjjw);
      ((ContentValues)localObject).put("property_name", paramzzclv.zzjkm);
      ((ContentValues)localObject).put("data", arrayOfByte);
      try
      {
        if (getWritableDatabase().insertWithOnConflict("property_filters", null, (ContentValues)localObject, 5) == -1L)
        {
          zzawy().zzazd().zzj("Failed to insert property filter (got -1). appId", zzchm.zzjk(paramString));
          return false;
        }
      }
      catch (SQLiteException paramzzclv)
      {
        zzawy().zzazd().zze("Error storing property filter. appId", zzchm.zzjk(paramString), paramzzclv);
        return false;
      }
      return true;
    }
    catch (IOException paramzzclv)
    {
      zzawy().zzazd().zze("Configuration loss. Failed to serialize property filter. appId", zzchm.zzjk(paramString), paramzzclv);
      return false;
    }
  }
  
  private final boolean zzayn()
  {
    return getContext().getDatabasePath("google_app_measurement.db").exists();
  }
  
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
      zzawy().zzazd().zze("Database error", paramString, paramArrayOfString);
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
  
  private final boolean zze(String paramString, List<Integer> paramList)
  {
    zzbq.zzgm(paramString);
    zzxf();
    zzve();
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    int j;
    label160:
    do
    {
      try
      {
        long l = zzb("select count(1) from audience_filter_values where app_id=?", new String[] { paramString });
        j = Math.max(0, Math.min(2000, zzaxa().zzb(paramString, zzchc.zzjbi)));
        if (l <= j) {
          return false;
        }
      }
      catch (SQLiteException paramList)
      {
        zzawy().zzazd().zze("Database error querying filters. appId", zzchm.zzjk(paramString), paramList);
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
      paramList = TextUtils.join(",", localArrayList);
      paramList = String.valueOf(paramList).length() + 2 + "(" + paramList + ")";
    } while (localSQLiteDatabase.delete("audience_filter_values", String.valueOf(paramList).length() + 140 + "audience_id in (select audience_id from audience_filter_values where app_id=? and audience_id not in " + paramList + " order by rowid desc limit -1 offset ?)", new String[] { paramString, Integer.toString(j) }) <= 0);
    return true;
  }
  
  public final void beginTransaction()
  {
    zzxf();
    getWritableDatabase().beginTransaction();
  }
  
  public final void endTransaction()
  {
    zzxf();
    getWritableDatabase().endTransaction();
  }
  
  final SQLiteDatabase getWritableDatabase()
  {
    zzve();
    try
    {
      SQLiteDatabase localSQLiteDatabase = this.zziyv.getWritableDatabase();
      return localSQLiteDatabase;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzawy().zzazf().zzj("Error opening database", localSQLiteException);
      throw localSQLiteException;
    }
  }
  
  public final void setTransactionSuccessful()
  {
    zzxf();
    getWritableDatabase().setTransactionSuccessful();
  }
  
  public final long zza(zzcme paramzzcme)
    throws IOException
  {
    zzve();
    zzxf();
    zzbq.checkNotNull(paramzzcme);
    zzbq.zzgm(paramzzcme.zzcn);
    for (;;)
    {
      try
      {
        byte[] arrayOfByte = new byte[paramzzcme.zzho()];
        Object localObject = zzfjk.zzo(arrayOfByte, 0, arrayOfByte.length);
        paramzzcme.zza((zzfjk)localObject);
        ((zzfjk)localObject).zzcwt();
        localObject = zzawu();
        zzbq.checkNotNull(arrayOfByte);
        ((zzcjk)localObject).zzve();
        MessageDigest localMessageDigest = zzclq.zzek("MD5");
        if (localMessageDigest == null)
        {
          ((zzcjk)localObject).zzawy().zzazd().log("Failed to get MD5");
          l = 0L;
          localObject = new ContentValues();
          ((ContentValues)localObject).put("app_id", paramzzcme.zzcn);
          ((ContentValues)localObject).put("metadata_fingerprint", Long.valueOf(l));
          ((ContentValues)localObject).put("metadata", arrayOfByte);
        }
        long l = zzclq.zzs(localMessageDigest.digest(localIOException));
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
          zzawy().zzazd().zze("Error storing raw event metadata. appId", zzchm.zzjk(paramzzcme.zzcn), localSQLiteException);
          throw localSQLiteException;
        }
        localIOException = localIOException;
        zzawy().zzazd().zze("Data loss. Failed to serialize event metadata. appId", zzchm.zzjk(paramzzcme.zzcn), localIOException);
        throw localIOException;
      }
    }
  }
  
  /* Error */
  public final zzcgp zza(long paramLong, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
  {
    // Byte code:
    //   0: aload_3
    //   1: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 407	com/google/android/gms/internal/zzcjk:zzve	()V
    //   9: aload_0
    //   10: invokevirtual 404	com/google/android/gms/internal/zzcjl:zzxf	()V
    //   13: new 657	com/google/android/gms/internal/zzcgp
    //   16: dup
    //   17: invokespecial 658	com/google/android/gms/internal/zzcgp:<init>	()V
    //   20: astore 12
    //   22: aload_0
    //   23: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   26: astore 11
    //   28: aload 11
    //   30: ldc_w 660
    //   33: bipush 6
    //   35: anewarray 19	java/lang/String
    //   38: dup
    //   39: iconst_0
    //   40: ldc 69
    //   42: aastore
    //   43: dup
    //   44: iconst_1
    //   45: ldc 77
    //   47: aastore
    //   48: dup
    //   49: iconst_2
    //   50: ldc 73
    //   52: aastore
    //   53: dup
    //   54: iconst_3
    //   55: ldc 81
    //   57: aastore
    //   58: dup
    //   59: iconst_4
    //   60: ldc 105
    //   62: aastore
    //   63: dup
    //   64: iconst_5
    //   65: ldc 109
    //   67: aastore
    //   68: ldc_w 662
    //   71: iconst_1
    //   72: anewarray 19	java/lang/String
    //   75: dup
    //   76: iconst_0
    //   77: aload_3
    //   78: aastore
    //   79: aconst_null
    //   80: aconst_null
    //   81: aconst_null
    //   82: invokevirtual 396	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   85: astore 10
    //   87: aload 10
    //   89: astore 9
    //   91: aload 10
    //   93: invokeinterface 194 1 0
    //   98: ifne +39 -> 137
    //   101: aload 10
    //   103: astore 9
    //   105: aload_0
    //   106: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   109: invokevirtual 309	com/google/android/gms/internal/zzchm:zzazf	()Lcom/google/android/gms/internal/zzcho;
    //   112: ldc_w 664
    //   115: aload_3
    //   116: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   119: invokevirtual 237	com/google/android/gms/internal/zzcho:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   122: aload 10
    //   124: ifnull +10 -> 134
    //   127: aload 10
    //   129: invokeinterface 201 1 0
    //   134: aload 12
    //   136: areturn
    //   137: aload 10
    //   139: astore 9
    //   141: aload 10
    //   143: iconst_0
    //   144: invokeinterface 198 2 0
    //   149: lload_1
    //   150: lcmp
    //   151: ifne +88 -> 239
    //   154: aload 10
    //   156: astore 9
    //   158: aload 12
    //   160: aload 10
    //   162: iconst_1
    //   163: invokeinterface 198 2 0
    //   168: putfield 668	com/google/android/gms/internal/zzcgp:zziyy	J
    //   171: aload 10
    //   173: astore 9
    //   175: aload 12
    //   177: aload 10
    //   179: iconst_2
    //   180: invokeinterface 198 2 0
    //   185: putfield 671	com/google/android/gms/internal/zzcgp:zziyx	J
    //   188: aload 10
    //   190: astore 9
    //   192: aload 12
    //   194: aload 10
    //   196: iconst_3
    //   197: invokeinterface 198 2 0
    //   202: putfield 674	com/google/android/gms/internal/zzcgp:zziyz	J
    //   205: aload 10
    //   207: astore 9
    //   209: aload 12
    //   211: aload 10
    //   213: iconst_4
    //   214: invokeinterface 198 2 0
    //   219: putfield 677	com/google/android/gms/internal/zzcgp:zziza	J
    //   222: aload 10
    //   224: astore 9
    //   226: aload 12
    //   228: aload 10
    //   230: iconst_5
    //   231: invokeinterface 198 2 0
    //   236: putfield 680	com/google/android/gms/internal/zzcgp:zzizb	J
    //   239: iload 4
    //   241: ifeq +19 -> 260
    //   244: aload 10
    //   246: astore 9
    //   248: aload 12
    //   250: aload 12
    //   252: getfield 668	com/google/android/gms/internal/zzcgp:zziyy	J
    //   255: lconst_1
    //   256: ladd
    //   257: putfield 668	com/google/android/gms/internal/zzcgp:zziyy	J
    //   260: iload 5
    //   262: ifeq +19 -> 281
    //   265: aload 10
    //   267: astore 9
    //   269: aload 12
    //   271: aload 12
    //   273: getfield 671	com/google/android/gms/internal/zzcgp:zziyx	J
    //   276: lconst_1
    //   277: ladd
    //   278: putfield 671	com/google/android/gms/internal/zzcgp:zziyx	J
    //   281: iload 6
    //   283: ifeq +19 -> 302
    //   286: aload 10
    //   288: astore 9
    //   290: aload 12
    //   292: aload 12
    //   294: getfield 674	com/google/android/gms/internal/zzcgp:zziyz	J
    //   297: lconst_1
    //   298: ladd
    //   299: putfield 674	com/google/android/gms/internal/zzcgp:zziyz	J
    //   302: iload 7
    //   304: ifeq +19 -> 323
    //   307: aload 10
    //   309: astore 9
    //   311: aload 12
    //   313: aload 12
    //   315: getfield 677	com/google/android/gms/internal/zzcgp:zziza	J
    //   318: lconst_1
    //   319: ladd
    //   320: putfield 677	com/google/android/gms/internal/zzcgp:zziza	J
    //   323: iload 8
    //   325: ifeq +19 -> 344
    //   328: aload 10
    //   330: astore 9
    //   332: aload 12
    //   334: aload 12
    //   336: getfield 680	com/google/android/gms/internal/zzcgp:zzizb	J
    //   339: lconst_1
    //   340: ladd
    //   341: putfield 680	com/google/android/gms/internal/zzcgp:zzizb	J
    //   344: aload 10
    //   346: astore 9
    //   348: new 276	android/content/ContentValues
    //   351: dup
    //   352: invokespecial 449	android/content/ContentValues:<init>	()V
    //   355: astore 13
    //   357: aload 10
    //   359: astore 9
    //   361: aload 13
    //   363: ldc 69
    //   365: lload_1
    //   366: invokestatic 248	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   369: invokevirtual 283	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   372: aload 10
    //   374: astore 9
    //   376: aload 13
    //   378: ldc 73
    //   380: aload 12
    //   382: getfield 671	com/google/android/gms/internal/zzcgp:zziyx	J
    //   385: invokestatic 248	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   388: invokevirtual 283	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   391: aload 10
    //   393: astore 9
    //   395: aload 13
    //   397: ldc 77
    //   399: aload 12
    //   401: getfield 668	com/google/android/gms/internal/zzcgp:zziyy	J
    //   404: invokestatic 248	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   407: invokevirtual 283	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   410: aload 10
    //   412: astore 9
    //   414: aload 13
    //   416: ldc 81
    //   418: aload 12
    //   420: getfield 674	com/google/android/gms/internal/zzcgp:zziyz	J
    //   423: invokestatic 248	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   426: invokevirtual 283	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   429: aload 10
    //   431: astore 9
    //   433: aload 13
    //   435: ldc 105
    //   437: aload 12
    //   439: getfield 677	com/google/android/gms/internal/zzcgp:zziza	J
    //   442: invokestatic 248	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   445: invokevirtual 283	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   448: aload 10
    //   450: astore 9
    //   452: aload 13
    //   454: ldc 109
    //   456: aload 12
    //   458: getfield 680	com/google/android/gms/internal/zzcgp:zzizb	J
    //   461: invokestatic 248	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   464: invokevirtual 283	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   467: aload 10
    //   469: astore 9
    //   471: aload 11
    //   473: ldc_w 660
    //   476: aload 13
    //   478: ldc_w 662
    //   481: iconst_1
    //   482: anewarray 19	java/lang/String
    //   485: dup
    //   486: iconst_0
    //   487: aload_3
    //   488: aastore
    //   489: invokevirtual 684	android/database/sqlite/SQLiteDatabase:update	(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   492: pop
    //   493: aload 10
    //   495: ifnull +10 -> 505
    //   498: aload 10
    //   500: invokeinterface 201 1 0
    //   505: aload 12
    //   507: areturn
    //   508: astore 11
    //   510: aconst_null
    //   511: astore 10
    //   513: aload 10
    //   515: astore 9
    //   517: aload_0
    //   518: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   521: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   524: ldc_w 686
    //   527: aload_3
    //   528: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   531: aload 11
    //   533: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   536: aload 10
    //   538: ifnull +10 -> 548
    //   541: aload 10
    //   543: invokeinterface 201 1 0
    //   548: aload 12
    //   550: areturn
    //   551: astore_3
    //   552: aconst_null
    //   553: astore 9
    //   555: aload 9
    //   557: ifnull +10 -> 567
    //   560: aload 9
    //   562: invokeinterface 201 1 0
    //   567: aload_3
    //   568: athrow
    //   569: astore_3
    //   570: goto -15 -> 555
    //   573: astore 11
    //   575: goto -62 -> 513
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	578	0	this	zzcgo
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
    //   20	529	12	localzzcgp	zzcgp
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
  
  public final void zza(zzcgh paramzzcgh)
  {
    zzbq.checkNotNull(paramzzcgh);
    zzve();
    zzxf();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzcgh.getAppId());
    localContentValues.put("app_instance_id", paramzzcgh.getAppInstanceId());
    localContentValues.put("gmp_app_id", paramzzcgh.getGmpAppId());
    localContentValues.put("resettable_device_id_hash", paramzzcgh.zzaxc());
    localContentValues.put("last_bundle_index", Long.valueOf(paramzzcgh.zzaxl()));
    localContentValues.put("last_bundle_start_timestamp", Long.valueOf(paramzzcgh.zzaxe()));
    localContentValues.put("last_bundle_end_timestamp", Long.valueOf(paramzzcgh.zzaxf()));
    localContentValues.put("app_version", paramzzcgh.zzvj());
    localContentValues.put("app_store", paramzzcgh.zzaxh());
    localContentValues.put("gmp_version", Long.valueOf(paramzzcgh.zzaxi()));
    localContentValues.put("dev_cert_hash", Long.valueOf(paramzzcgh.zzaxj()));
    localContentValues.put("measurement_enabled", Boolean.valueOf(paramzzcgh.zzaxk()));
    localContentValues.put("day", Long.valueOf(paramzzcgh.zzaxp()));
    localContentValues.put("daily_public_events_count", Long.valueOf(paramzzcgh.zzaxq()));
    localContentValues.put("daily_events_count", Long.valueOf(paramzzcgh.zzaxr()));
    localContentValues.put("daily_conversions_count", Long.valueOf(paramzzcgh.zzaxs()));
    localContentValues.put("config_fetched_time", Long.valueOf(paramzzcgh.zzaxm()));
    localContentValues.put("failed_config_fetch_time", Long.valueOf(paramzzcgh.zzaxn()));
    localContentValues.put("app_version_int", Long.valueOf(paramzzcgh.zzaxg()));
    localContentValues.put("firebase_instance_id", paramzzcgh.zzaxd());
    localContentValues.put("daily_error_events_count", Long.valueOf(paramzzcgh.zzaxu()));
    localContentValues.put("daily_realtime_events_count", Long.valueOf(paramzzcgh.zzaxt()));
    localContentValues.put("health_monitor_sample", paramzzcgh.zzaxv());
    localContentValues.put("android_id", Long.valueOf(paramzzcgh.zzaxx()));
    localContentValues.put("adid_reporting_enabled", Boolean.valueOf(paramzzcgh.zzaxy()));
    try
    {
      SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
      if ((localSQLiteDatabase.update("apps", localContentValues, "app_id = ?", new String[] { paramzzcgh.getAppId() }) == 0L) && (localSQLiteDatabase.insertWithOnConflict("apps", null, localContentValues, 5) == -1L)) {
        zzawy().zzazd().zzj("Failed to insert/update app (got -1). appId", zzchm.zzjk(paramzzcgh.getAppId()));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzawy().zzazd().zze("Error storing app. appId", zzchm.zzjk(paramzzcgh.getAppId()), localSQLiteException);
    }
  }
  
  public final void zza(zzcgw paramzzcgw)
  {
    Object localObject2 = null;
    zzbq.checkNotNull(paramzzcgw);
    zzve();
    zzxf();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzcgw.mAppId);
    localContentValues.put("name", paramzzcgw.mName);
    localContentValues.put("lifetime_count", Long.valueOf(paramzzcgw.zzizk));
    localContentValues.put("current_bundle_count", Long.valueOf(paramzzcgw.zzizl));
    localContentValues.put("last_fire_timestamp", Long.valueOf(paramzzcgw.zzizm));
    localContentValues.put("last_bundled_timestamp", Long.valueOf(paramzzcgw.zzizn));
    localContentValues.put("last_sampled_complex_event_id", paramzzcgw.zzizo);
    localContentValues.put("last_sampling_rate", paramzzcgw.zzizp);
    Object localObject1 = localObject2;
    if (paramzzcgw.zzizq != null)
    {
      localObject1 = localObject2;
      if (paramzzcgw.zzizq.booleanValue()) {
        localObject1 = Long.valueOf(1L);
      }
    }
    localContentValues.put("last_exempt_from_sampling", (Long)localObject1);
    try
    {
      if (getWritableDatabase().insertWithOnConflict("events", null, localContentValues, 5) == -1L) {
        zzawy().zzazd().zzj("Failed to insert/update event aggregates (got -1). appId", zzchm.zzjk(paramzzcgw.mAppId));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzawy().zzazd().zze("Error storing event aggregates. appId", zzchm.zzjk(paramzzcgw.mAppId), localSQLiteException);
    }
  }
  
  final void zza(String paramString, zzclr[] paramArrayOfzzclr)
  {
    int n = 0;
    zzxf();
    zzve();
    zzbq.zzgm(paramString);
    zzbq.checkNotNull(paramArrayOfzzclr);
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    Object localObject1;
    int j;
    int i2;
    for (;;)
    {
      try
      {
        zzxf();
        zzve();
        zzbq.zzgm(paramString);
        localObject1 = getWritableDatabase();
        ((SQLiteDatabase)localObject1).delete("property_filters", "app_id=?", new String[] { paramString });
        ((SQLiteDatabase)localObject1).delete("event_filters", "app_id=?", new String[] { paramString });
        int i1 = paramArrayOfzzclr.length;
        j = 0;
        if (j >= i1) {
          break label480;
        }
        localObject1 = paramArrayOfzzclr[j];
        zzxf();
        zzve();
        zzbq.zzgm(paramString);
        zzbq.checkNotNull(localObject1);
        zzbq.checkNotNull(((zzclr)localObject1).zzjju);
        zzbq.checkNotNull(((zzclr)localObject1).zzjjt);
        if (((zzclr)localObject1).zzjjs == null)
        {
          zzawy().zzazf().zzj("Audience with no ID. appId", zzchm.zzjk(paramString));
        }
        else
        {
          i2 = ((zzclr)localObject1).zzjjs.intValue();
          localObject2 = ((zzclr)localObject1).zzjju;
          k = localObject2.length;
          i = 0;
          if (i < k) {
            if (localObject2[i].zzjjw == null) {
              zzawy().zzazf().zze("Event filter with no ID. Audience definition ignored. appId, audienceId", zzchm.zzjk(paramString), ((zzclr)localObject1).zzjjs);
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
    Object localObject2 = ((zzclr)localObject1).zzjjt;
    int k = localObject2.length;
    int i = 0;
    label270:
    label325:
    label347:
    int m;
    int i3;
    if (i < k)
    {
      if (localObject2[i].zzjjw != null) {
        break label556;
      }
      zzawy().zzazf().zze("Property filter with no ID. Audience definition ignored. appId, audienceId", zzchm.zzjk(paramString), ((zzclr)localObject1).zzjjs);
    }
    else
    {
      localObject2 = ((zzclr)localObject1).zzjju;
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
          localObject1 = ((zzclr)localObject1).zzjjt;
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
          zzxf();
          zzve();
          zzbq.zzgm(paramString);
          localObject1 = getWritableDatabase();
          ((SQLiteDatabase)localObject1).delete("property_filters", "app_id=? and audience_id=?", new String[] { paramString, String.valueOf(i2) });
          ((SQLiteDatabase)localObject1).delete("event_filters", "app_id=? and audience_id=?", new String[] { paramString, String.valueOf(i2) });
          break label547;
          label480:
          localObject1 = new ArrayList();
          j = paramArrayOfzzclr.length;
          i = n;
          while (i < j)
          {
            ((List)localObject1).add(paramArrayOfzzclr[i].zzjjs);
            i += 1;
          }
          zze(paramString, (List)localObject1);
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
  
  public final boolean zza(zzcgl paramzzcgl)
  {
    zzbq.checkNotNull(paramzzcgl);
    zzve();
    zzxf();
    if (zzag(paramzzcgl.packageName, paramzzcgl.zziyg.name) == null) {
      if (zzb("SELECT COUNT(1) FROM conditional_properties WHERE app_id=?", new String[] { paramzzcgl.packageName }) >= 1000L) {
        return false;
      }
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzcgl.packageName);
    localContentValues.put("origin", paramzzcgl.zziyf);
    localContentValues.put("name", paramzzcgl.zziyg.name);
    zza(localContentValues, "value", paramzzcgl.zziyg.getValue());
    localContentValues.put("active", Boolean.valueOf(paramzzcgl.zziyi));
    localContentValues.put("trigger_event_name", paramzzcgl.zziyj);
    localContentValues.put("trigger_timeout", Long.valueOf(paramzzcgl.zziyl));
    zzawu();
    localContentValues.put("timed_out_event", zzclq.zza(paramzzcgl.zziyk));
    localContentValues.put("creation_timestamp", Long.valueOf(paramzzcgl.zziyh));
    zzawu();
    localContentValues.put("triggered_event", zzclq.zza(paramzzcgl.zziym));
    localContentValues.put("triggered_timestamp", Long.valueOf(paramzzcgl.zziyg.zzjji));
    localContentValues.put("time_to_live", Long.valueOf(paramzzcgl.zziyn));
    zzawu();
    localContentValues.put("expired_event", zzclq.zza(paramzzcgl.zziyo));
    try
    {
      if (getWritableDatabase().insertWithOnConflict("conditional_properties", null, localContentValues, 5) == -1L) {
        zzawy().zzazd().zzj("Failed to insert/update conditional user property (got -1)", zzchm.zzjk(paramzzcgl.packageName));
      }
      return true;
    }
    catch (SQLiteException localSQLiteException)
    {
      for (;;)
      {
        zzawy().zzazd().zze("Error storing conditional user property", zzchm.zzjk(paramzzcgl.packageName), localSQLiteException);
      }
    }
  }
  
  public final boolean zza(zzcgv paramzzcgv, long paramLong, boolean paramBoolean)
  {
    zzve();
    zzxf();
    zzbq.checkNotNull(paramzzcgv);
    zzbq.zzgm(paramzzcgv.mAppId);
    Object localObject1 = new zzcmb();
    ((zzcmb)localObject1).zzjlj = Long.valueOf(paramzzcgv.zzizi);
    ((zzcmb)localObject1).zzjlh = new zzcmc[paramzzcgv.zzizj.size()];
    Object localObject2 = paramzzcgv.zzizj.iterator();
    int i = 0;
    Object localObject3;
    while (((Iterator)localObject2).hasNext())
    {
      Object localObject4 = (String)((Iterator)localObject2).next();
      localObject3 = new zzcmc();
      ((zzcmb)localObject1).zzjlh[i] = localObject3;
      ((zzcmc)localObject3).name = ((String)localObject4);
      localObject4 = paramzzcgv.zzizj.get((String)localObject4);
      zzawu().zza((zzcmc)localObject3, localObject4);
      i += 1;
    }
    for (;;)
    {
      try
      {
        localObject2 = new byte[((zzfjs)localObject1).zzho()];
        localObject3 = zzfjk.zzo((byte[])localObject2, 0, localObject2.length);
        ((zzfjs)localObject1).zza((zzfjk)localObject3);
        ((zzfjk)localObject3).zzcwt();
        zzawy().zzazj().zze("Saving event, name, data size", zzawt().zzjh(paramzzcgv.mName), Integer.valueOf(localObject2.length));
        localObject1 = new ContentValues();
        ((ContentValues)localObject1).put("app_id", paramzzcgv.mAppId);
        ((ContentValues)localObject1).put("name", paramzzcgv.mName);
        ((ContentValues)localObject1).put("timestamp", Long.valueOf(paramzzcgv.zzfij));
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
          zzawy().zzazd().zzj("Failed to insert raw event (got -1). appId", zzchm.zzjk(paramzzcgv.mAppId));
          return false;
        }
        catch (SQLiteException localSQLiteException)
        {
          zzawy().zzazd().zze("Error storing raw event. appId", zzchm.zzjk(paramzzcgv.mAppId), localSQLiteException);
          return false;
        }
        localIOException = localIOException;
        zzawy().zzazd().zze("Data loss. Failed to serialize event params/data. appId", zzchm.zzjk(paramzzcgv.mAppId), localIOException);
        return false;
      }
    }
    return true;
  }
  
  public final boolean zza(zzclp paramzzclp)
  {
    zzbq.checkNotNull(paramzzclp);
    zzve();
    zzxf();
    if (zzag(paramzzclp.mAppId, paramzzclp.mName) == null) {
      if (zzclq.zzjz(paramzzclp.mName))
      {
        if (zzb("select count(1) from user_attributes where app_id=? and name not like '!_%' escape '!'", new String[] { paramzzclp.mAppId }) < 25L) {}
      }
      else {
        while (zzb("select count(1) from user_attributes where app_id=? and origin=? AND name like '!_%' escape '!'", new String[] { paramzzclp.mAppId, paramzzclp.mOrigin }) >= 25L) {
          return false;
        }
      }
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzclp.mAppId);
    localContentValues.put("origin", paramzzclp.mOrigin);
    localContentValues.put("name", paramzzclp.mName);
    localContentValues.put("set_timestamp", Long.valueOf(paramzzclp.zzjjm));
    zza(localContentValues, "value", paramzzclp.mValue);
    try
    {
      if (getWritableDatabase().insertWithOnConflict("user_attributes", null, localContentValues, 5) == -1L) {
        zzawy().zzazd().zzj("Failed to insert/update user property (got -1). appId", zzchm.zzjk(paramzzclp.mAppId));
      }
      return true;
    }
    catch (SQLiteException localSQLiteException)
    {
      for (;;)
      {
        zzawy().zzazd().zze("Error storing user property. appId", zzchm.zzjk(paramzzclp.mAppId), localSQLiteException);
      }
    }
  }
  
  public final boolean zza(zzcme paramzzcme, boolean paramBoolean)
  {
    zzve();
    zzxf();
    zzbq.checkNotNull(paramzzcme);
    zzbq.zzgm(paramzzcme.zzcn);
    zzbq.checkNotNull(paramzzcme.zzjlt);
    zzayh();
    long l = zzws().currentTimeMillis();
    if ((paramzzcme.zzjlt.longValue() < l - zzcgn.zzayb()) || (paramzzcme.zzjlt.longValue() > zzcgn.zzayb() + l)) {
      zzawy().zzazf().zzd("Storing bundle outside of the max uploading time span. appId, now, timestamp", zzchm.zzjk(paramzzcme.zzcn), Long.valueOf(l), paramzzcme.zzjlt);
    }
    for (;;)
    {
      try
      {
        byte[] arrayOfByte = new byte[paramzzcme.zzho()];
        Object localObject = zzfjk.zzo(arrayOfByte, 0, arrayOfByte.length);
        paramzzcme.zza((zzfjk)localObject);
        ((zzfjk)localObject).zzcwt();
        arrayOfByte = zzawu().zzq(arrayOfByte);
        zzawy().zzazj().zzj("Saving bundle, size", Integer.valueOf(arrayOfByte.length));
        localObject = new ContentValues();
        ((ContentValues)localObject).put("app_id", paramzzcme.zzcn);
        ((ContentValues)localObject).put("bundle_end_timestamp", paramzzcme.zzjlt);
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
          zzawy().zzazd().zzj("Failed to insert bundle (got -1). appId", zzchm.zzjk(paramzzcme.zzcn));
          return false;
        }
        catch (SQLiteException localSQLiteException)
        {
          zzawy().zzazd().zze("Error storing bundle. appId", zzchm.zzjk(paramzzcme.zzcn), localSQLiteException);
          return false;
        }
        localIOException = localIOException;
        zzawy().zzazd().zze("Data loss. Failed to serialize bundle. appId", zzchm.zzjk(paramzzcme.zzcn), localIOException);
        return false;
      }
    }
    return true;
  }
  
  /* Error */
  public final zzcgw zzae(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_2
    //   6: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   9: pop
    //   10: aload_0
    //   11: invokevirtual 407	com/google/android/gms/internal/zzcjk:zzve	()V
    //   14: aload_0
    //   15: invokevirtual 404	com/google/android/gms/internal/zzcjl:zzxf	()V
    //   18: aload_0
    //   19: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   22: ldc_w 832
    //   25: bipush 7
    //   27: anewarray 19	java/lang/String
    //   30: dup
    //   31: iconst_0
    //   32: ldc_w 800
    //   35: aastore
    //   36: dup
    //   37: iconst_1
    //   38: ldc_w 805
    //   41: aastore
    //   42: dup
    //   43: iconst_2
    //   44: ldc_w 810
    //   47: aastore
    //   48: dup
    //   49: iconst_3
    //   50: ldc 21
    //   52: aastore
    //   53: dup
    //   54: iconst_4
    //   55: ldc 25
    //   57: aastore
    //   58: dup
    //   59: iconst_5
    //   60: ldc 29
    //   62: aastore
    //   63: dup
    //   64: bipush 6
    //   66: ldc 33
    //   68: aastore
    //   69: ldc_w 1104
    //   72: iconst_2
    //   73: anewarray 19	java/lang/String
    //   76: dup
    //   77: iconst_0
    //   78: aload_1
    //   79: aastore
    //   80: dup
    //   81: iconst_1
    //   82: aload_2
    //   83: aastore
    //   84: aconst_null
    //   85: aconst_null
    //   86: aconst_null
    //   87: invokevirtual 396	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   90: astore 14
    //   92: aload 14
    //   94: invokeinterface 194 1 0
    //   99: istore_3
    //   100: iload_3
    //   101: ifne +19 -> 120
    //   104: aload 14
    //   106: ifnull +10 -> 116
    //   109: aload 14
    //   111: invokeinterface 201 1 0
    //   116: aconst_null
    //   117: astore_1
    //   118: aload_1
    //   119: areturn
    //   120: aload 14
    //   122: iconst_0
    //   123: invokeinterface 198 2 0
    //   128: lstore 6
    //   130: aload 14
    //   132: iconst_1
    //   133: invokeinterface 198 2 0
    //   138: lstore 8
    //   140: aload 14
    //   142: iconst_2
    //   143: invokeinterface 198 2 0
    //   148: lstore 10
    //   150: aload 14
    //   152: iconst_3
    //   153: invokeinterface 1108 2 0
    //   158: ifeq +141 -> 299
    //   161: lconst_0
    //   162: lstore 4
    //   164: aload 14
    //   166: iconst_4
    //   167: invokeinterface 1108 2 0
    //   172: ifeq +140 -> 312
    //   175: aconst_null
    //   176: astore 15
    //   178: aload 14
    //   180: iconst_5
    //   181: invokeinterface 1108 2 0
    //   186: ifeq +142 -> 328
    //   189: aconst_null
    //   190: astore 16
    //   192: aconst_null
    //   193: astore 17
    //   195: aload 14
    //   197: bipush 6
    //   199: invokeinterface 1108 2 0
    //   204: ifne +25 -> 229
    //   207: aload 14
    //   209: bipush 6
    //   211: invokeinterface 198 2 0
    //   216: lconst_1
    //   217: lcmp
    //   218: ifne +130 -> 348
    //   221: iconst_1
    //   222: istore_3
    //   223: iload_3
    //   224: invokestatic 741	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   227: astore 17
    //   229: new 792	com/google/android/gms/internal/zzcgw
    //   232: dup
    //   233: aload_1
    //   234: aload_2
    //   235: lload 6
    //   237: lload 8
    //   239: lload 10
    //   241: lload 4
    //   243: aload 15
    //   245: aload 16
    //   247: aload 17
    //   249: invokespecial 1111	com/google/android/gms/internal/zzcgw:<init>	(Ljava/lang/String;Ljava/lang/String;JJJJLjava/lang/Long;Ljava/lang/Long;Ljava/lang/Boolean;)V
    //   252: astore 15
    //   254: aload 14
    //   256: invokeinterface 1114 1 0
    //   261: ifeq +20 -> 281
    //   264: aload_0
    //   265: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   268: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   271: ldc_w 1116
    //   274: aload_1
    //   275: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   278: invokevirtual 237	com/google/android/gms/internal/zzcho:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   281: aload 15
    //   283: astore_1
    //   284: aload 14
    //   286: ifnull -168 -> 118
    //   289: aload 14
    //   291: invokeinterface 201 1 0
    //   296: aload 15
    //   298: areturn
    //   299: aload 14
    //   301: iconst_3
    //   302: invokeinterface 198 2 0
    //   307: lstore 4
    //   309: goto -145 -> 164
    //   312: aload 14
    //   314: iconst_4
    //   315: invokeinterface 198 2 0
    //   320: invokestatic 248	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   323: astore 15
    //   325: goto -147 -> 178
    //   328: aload 14
    //   330: iconst_5
    //   331: invokeinterface 198 2 0
    //   336: lstore 12
    //   338: lload 12
    //   340: invokestatic 248	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   343: astore 16
    //   345: goto -153 -> 192
    //   348: iconst_0
    //   349: istore_3
    //   350: goto -127 -> 223
    //   353: astore 15
    //   355: aconst_null
    //   356: astore 14
    //   358: aload_0
    //   359: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   362: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   365: ldc_w 1118
    //   368: aload_1
    //   369: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   372: aload_0
    //   373: invokevirtual 1009	com/google/android/gms/internal/zzcjk:zzawt	()Lcom/google/android/gms/internal/zzchk;
    //   376: aload_2
    //   377: invokevirtual 1015	com/google/android/gms/internal/zzchk:zzjh	(Ljava/lang/String;)Ljava/lang/String;
    //   380: aload 15
    //   382: invokevirtual 430	com/google/android/gms/internal/zzcho:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   385: aload 14
    //   387: ifnull +10 -> 397
    //   390: aload 14
    //   392: invokeinterface 201 1 0
    //   397: aconst_null
    //   398: areturn
    //   399: astore_1
    //   400: aconst_null
    //   401: astore 14
    //   403: aload 14
    //   405: ifnull +10 -> 415
    //   408: aload 14
    //   410: invokeinterface 201 1 0
    //   415: aload_1
    //   416: athrow
    //   417: astore_1
    //   418: goto -15 -> 403
    //   421: astore_1
    //   422: goto -19 -> 403
    //   425: astore 15
    //   427: goto -69 -> 358
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	430	0	this	zzcgo
    //   0	430	1	paramString1	String
    //   0	430	2	paramString2	String
    //   99	251	3	bool	boolean
    //   162	146	4	l1	long
    //   128	108	6	l2	long
    //   138	100	8	l3	long
    //   148	92	10	l4	long
    //   336	3	12	l5	long
    //   90	319	14	localCursor	Cursor
    //   176	148	15	localObject	Object
    //   353	28	15	localSQLiteException1	SQLiteException
    //   425	1	15	localSQLiteException2	SQLiteException
    //   190	154	16	localLong	Long
    //   193	55	17	localBoolean	Boolean
    // Exception table:
    //   from	to	target	type
    //   18	92	353	android/database/sqlite/SQLiteException
    //   18	92	399	finally
    //   92	100	417	finally
    //   120	161	417	finally
    //   164	175	417	finally
    //   178	189	417	finally
    //   195	221	417	finally
    //   223	229	417	finally
    //   229	281	417	finally
    //   299	309	417	finally
    //   312	325	417	finally
    //   328	338	417	finally
    //   358	385	421	finally
    //   92	100	425	android/database/sqlite/SQLiteException
    //   120	161	425	android/database/sqlite/SQLiteException
    //   164	175	425	android/database/sqlite/SQLiteException
    //   178	189	425	android/database/sqlite/SQLiteException
    //   195	221	425	android/database/sqlite/SQLiteException
    //   223	229	425	android/database/sqlite/SQLiteException
    //   229	281	425	android/database/sqlite/SQLiteException
    //   299	309	425	android/database/sqlite/SQLiteException
    //   312	325	425	android/database/sqlite/SQLiteException
    //   328	338	425	android/database/sqlite/SQLiteException
  }
  
  public final void zzaf(String paramString1, String paramString2)
  {
    zzbq.zzgm(paramString1);
    zzbq.zzgm(paramString2);
    zzve();
    zzxf();
    try
    {
      int i = getWritableDatabase().delete("user_attributes", "app_id=? and name=?", new String[] { paramString1, paramString2 });
      zzawy().zzazj().zzj("Deleted user attribute rows", Integer.valueOf(i));
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzawy().zzazd().zzd("Error deleting user attribute. appId", zzchm.zzjk(paramString1), zzawt().zzjj(paramString2), localSQLiteException);
    }
  }
  
  /* Error */
  public final zzclp zzag(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aload_1
    //   4: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_2
    //   9: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 407	com/google/android/gms/internal/zzcjk:zzve	()V
    //   17: aload_0
    //   18: invokevirtual 404	com/google/android/gms/internal/zzcjl:zzxf	()V
    //   21: aload_0
    //   22: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   25: ldc_w 1061
    //   28: iconst_3
    //   29: anewarray 19	java/lang/String
    //   32: dup
    //   33: iconst_0
    //   34: ldc_w 1052
    //   37: aastore
    //   38: dup
    //   39: iconst_1
    //   40: ldc_w 893
    //   43: aastore
    //   44: dup
    //   45: iconst_2
    //   46: ldc 39
    //   48: aastore
    //   49: ldc_w 1104
    //   52: iconst_2
    //   53: anewarray 19	java/lang/String
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
    //   67: invokevirtual 396	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   70: astore 6
    //   72: aload 6
    //   74: invokeinterface 194 1 0
    //   79: istore_3
    //   80: iload_3
    //   81: ifne +19 -> 100
    //   84: aload 6
    //   86: ifnull +10 -> 96
    //   89: aload 6
    //   91: invokeinterface 201 1 0
    //   96: aconst_null
    //   97: astore_1
    //   98: aload_1
    //   99: areturn
    //   100: aload 6
    //   102: iconst_0
    //   103: invokeinterface 198 2 0
    //   108: lstore 4
    //   110: aload_0
    //   111: aload 6
    //   113: iconst_1
    //   114: invokespecial 1128	com/google/android/gms/internal/zzcgo:zza	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   117: astore 7
    //   119: new 1035	com/google/android/gms/internal/zzclp
    //   122: dup
    //   123: aload_1
    //   124: aload 6
    //   126: iconst_2
    //   127: invokeinterface 261 2 0
    //   132: aload_2
    //   133: lload 4
    //   135: aload 7
    //   137: invokespecial 1131	com/google/android/gms/internal/zzclp:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   140: astore 7
    //   142: aload 6
    //   144: invokeinterface 1114 1 0
    //   149: ifeq +20 -> 169
    //   152: aload_0
    //   153: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   156: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   159: ldc_w 1133
    //   162: aload_1
    //   163: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   166: invokevirtual 237	com/google/android/gms/internal/zzcho:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   169: aload 7
    //   171: astore_1
    //   172: aload 6
    //   174: ifnull -76 -> 98
    //   177: aload 6
    //   179: invokeinterface 201 1 0
    //   184: aload 7
    //   186: areturn
    //   187: astore 7
    //   189: aconst_null
    //   190: astore 6
    //   192: aload_0
    //   193: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   196: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   199: ldc_w 1135
    //   202: aload_1
    //   203: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   206: aload_0
    //   207: invokevirtual 1009	com/google/android/gms/internal/zzcjk:zzawt	()Lcom/google/android/gms/internal/zzchk;
    //   210: aload_2
    //   211: invokevirtual 1126	com/google/android/gms/internal/zzchk:zzjj	(Ljava/lang/String;)Ljava/lang/String;
    //   214: aload 7
    //   216: invokevirtual 430	com/google/android/gms/internal/zzcho:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   219: aload 6
    //   221: ifnull +10 -> 231
    //   224: aload 6
    //   226: invokeinterface 201 1 0
    //   231: aconst_null
    //   232: areturn
    //   233: astore_1
    //   234: aload 7
    //   236: astore_2
    //   237: aload_2
    //   238: ifnull +9 -> 247
    //   241: aload_2
    //   242: invokeinterface 201 1 0
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
    //   0	268	0	this	zzcgo
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
  
  /* Error */
  public final zzcgl zzah(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_2
    //   6: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   9: pop
    //   10: aload_0
    //   11: invokevirtual 407	com/google/android/gms/internal/zzcjk:zzve	()V
    //   14: aload_0
    //   15: invokevirtual 404	com/google/android/gms/internal/zzcjl:zzxf	()V
    //   18: aload_0
    //   19: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   22: ldc_w 951
    //   25: bipush 11
    //   27: anewarray 19	java/lang/String
    //   30: dup
    //   31: iconst_0
    //   32: ldc 39
    //   34: aastore
    //   35: dup
    //   36: iconst_1
    //   37: ldc_w 893
    //   40: aastore
    //   41: dup
    //   42: iconst_2
    //   43: ldc_w 901
    //   46: aastore
    //   47: dup
    //   48: iconst_3
    //   49: ldc_w 907
    //   52: aastore
    //   53: dup
    //   54: iconst_4
    //   55: ldc_w 912
    //   58: aastore
    //   59: dup
    //   60: iconst_5
    //   61: ldc_w 917
    //   64: aastore
    //   65: dup
    //   66: bipush 6
    //   68: ldc_w 926
    //   71: aastore
    //   72: dup
    //   73: bipush 7
    //   75: ldc_w 931
    //   78: aastore
    //   79: dup
    //   80: bipush 8
    //   82: ldc_w 936
    //   85: aastore
    //   86: dup
    //   87: bipush 9
    //   89: ldc_w 941
    //   92: aastore
    //   93: dup
    //   94: bipush 10
    //   96: ldc_w 946
    //   99: aastore
    //   100: ldc_w 1104
    //   103: iconst_2
    //   104: anewarray 19	java/lang/String
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
    //   118: invokevirtual 396	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   121: astore 12
    //   123: aload 12
    //   125: invokeinterface 194 1 0
    //   130: istore_3
    //   131: iload_3
    //   132: ifne +19 -> 151
    //   135: aload 12
    //   137: ifnull +10 -> 147
    //   140: aload 12
    //   142: invokeinterface 201 1 0
    //   147: aconst_null
    //   148: astore_1
    //   149: aload_1
    //   150: areturn
    //   151: aload 12
    //   153: iconst_0
    //   154: invokeinterface 261 2 0
    //   159: astore 13
    //   161: aload_0
    //   162: aload 12
    //   164: iconst_1
    //   165: invokespecial 1128	com/google/android/gms/internal/zzcgo:zza	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   168: astore 14
    //   170: aload 12
    //   172: iconst_2
    //   173: invokeinterface 1140 2 0
    //   178: ifeq +223 -> 401
    //   181: iconst_1
    //   182: istore_3
    //   183: aload 12
    //   185: iconst_3
    //   186: invokeinterface 261 2 0
    //   191: astore 15
    //   193: aload 12
    //   195: iconst_4
    //   196: invokeinterface 198 2 0
    //   201: lstore 4
    //   203: aload_0
    //   204: invokevirtual 624	com/google/android/gms/internal/zzcjk:zzawu	()Lcom/google/android/gms/internal/zzclq;
    //   207: aload 12
    //   209: iconst_5
    //   210: invokeinterface 1144 2 0
    //   215: getstatic 1150	com/google/android/gms/internal/zzcha:CREATOR	Landroid/os/Parcelable$Creator;
    //   218: invokevirtual 1153	com/google/android/gms/internal/zzclq:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   221: checkcast 1146	com/google/android/gms/internal/zzcha
    //   224: astore 16
    //   226: aload 12
    //   228: bipush 6
    //   230: invokeinterface 198 2 0
    //   235: lstore 6
    //   237: aload_0
    //   238: invokevirtual 624	com/google/android/gms/internal/zzcjk:zzawu	()Lcom/google/android/gms/internal/zzclq;
    //   241: aload 12
    //   243: bipush 7
    //   245: invokeinterface 1144 2 0
    //   250: getstatic 1150	com/google/android/gms/internal/zzcha:CREATOR	Landroid/os/Parcelable$Creator;
    //   253: invokevirtual 1153	com/google/android/gms/internal/zzclq:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   256: checkcast 1146	com/google/android/gms/internal/zzcha
    //   259: astore 17
    //   261: aload 12
    //   263: bipush 8
    //   265: invokeinterface 198 2 0
    //   270: lstore 8
    //   272: aload 12
    //   274: bipush 9
    //   276: invokeinterface 198 2 0
    //   281: lstore 10
    //   283: aload_0
    //   284: invokevirtual 624	com/google/android/gms/internal/zzcjk:zzawu	()Lcom/google/android/gms/internal/zzclq;
    //   287: aload 12
    //   289: bipush 10
    //   291: invokeinterface 1144 2 0
    //   296: getstatic 1150	com/google/android/gms/internal/zzcha:CREATOR	Landroid/os/Parcelable$Creator;
    //   299: invokevirtual 1153	com/google/android/gms/internal/zzclq:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   302: checkcast 1146	com/google/android/gms/internal/zzcha
    //   305: astore 18
    //   307: new 869	com/google/android/gms/internal/zzcgl
    //   310: dup
    //   311: aload_1
    //   312: aload 13
    //   314: new 878	com/google/android/gms/internal/zzcln
    //   317: dup
    //   318: aload_2
    //   319: lload 8
    //   321: aload 14
    //   323: aload 13
    //   325: invokespecial 1156	com/google/android/gms/internal/zzcln:<init>	(Ljava/lang/String;JLjava/lang/Object;Ljava/lang/String;)V
    //   328: lload 6
    //   330: iload_3
    //   331: aload 15
    //   333: aload 16
    //   335: lload 4
    //   337: aload 17
    //   339: lload 10
    //   341: aload 18
    //   343: invokespecial 1159	com/google/android/gms/internal/zzcgl:<init>	(Ljava/lang/String;Ljava/lang/String;Lcom/google/android/gms/internal/zzcln;JZLjava/lang/String;Lcom/google/android/gms/internal/zzcha;JLcom/google/android/gms/internal/zzcha;JLcom/google/android/gms/internal/zzcha;)V
    //   346: astore 13
    //   348: aload 12
    //   350: invokeinterface 1114 1 0
    //   355: ifeq +28 -> 383
    //   358: aload_0
    //   359: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   362: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   365: ldc_w 1161
    //   368: aload_1
    //   369: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   372: aload_0
    //   373: invokevirtual 1009	com/google/android/gms/internal/zzcjk:zzawt	()Lcom/google/android/gms/internal/zzchk;
    //   376: aload_2
    //   377: invokevirtual 1126	com/google/android/gms/internal/zzchk:zzjj	(Ljava/lang/String;)Ljava/lang/String;
    //   380: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   383: aload 13
    //   385: astore_1
    //   386: aload 12
    //   388: ifnull -239 -> 149
    //   391: aload 12
    //   393: invokeinterface 201 1 0
    //   398: aload 13
    //   400: areturn
    //   401: iconst_0
    //   402: istore_3
    //   403: goto -220 -> 183
    //   406: astore 13
    //   408: aconst_null
    //   409: astore 12
    //   411: aload_0
    //   412: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   415: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   418: ldc_w 1163
    //   421: aload_1
    //   422: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   425: aload_0
    //   426: invokevirtual 1009	com/google/android/gms/internal/zzcjk:zzawt	()Lcom/google/android/gms/internal/zzchk;
    //   429: aload_2
    //   430: invokevirtual 1126	com/google/android/gms/internal/zzchk:zzjj	(Ljava/lang/String;)Ljava/lang/String;
    //   433: aload 13
    //   435: invokevirtual 430	com/google/android/gms/internal/zzcho:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   438: aload 12
    //   440: ifnull +10 -> 450
    //   443: aload 12
    //   445: invokeinterface 201 1 0
    //   450: aconst_null
    //   451: areturn
    //   452: astore_1
    //   453: aconst_null
    //   454: astore 12
    //   456: aload 12
    //   458: ifnull +10 -> 468
    //   461: aload 12
    //   463: invokeinterface 201 1 0
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
    //   0	483	0	this	zzcgo
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
    //   224	110	16	localzzcha1	zzcha
    //   259	79	17	localzzcha2	zzcha
    //   305	37	18	localzzcha3	zzcha
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
  
  public final void zzah(List<Long> paramList)
  {
    zzbq.checkNotNull(paramList);
    zzve();
    zzxf();
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
      zzawy().zzazd().zze("Deleted fewer rows from raw events table than expected", Integer.valueOf(i), Integer.valueOf(paramList.size()));
    }
  }
  
  public final int zzai(String paramString1, String paramString2)
  {
    zzbq.zzgm(paramString1);
    zzbq.zzgm(paramString2);
    zzve();
    zzxf();
    try
    {
      int i = getWritableDatabase().delete("conditional_properties", "app_id=? and name=?", new String[] { paramString1, paramString2 });
      return i;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzawy().zzazd().zzd("Error deleting conditional property", zzchm.zzjk(paramString1), zzawt().zzjj(paramString2), localSQLiteException);
    }
    return 0;
  }
  
  /* Error */
  final java.util.Map<Integer, List<zzcls>> zzaj(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 404	com/google/android/gms/internal/zzcjl:zzxf	()V
    //   4: aload_0
    //   5: invokevirtual 407	com/google/android/gms/internal/zzcjk:zzve	()V
    //   8: aload_1
    //   9: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_2
    //   14: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   17: pop
    //   18: new 1181	android/support/v4/util/ArrayMap
    //   21: dup
    //   22: invokespecial 1182	android/support/v4/util/ArrayMap:<init>	()V
    //   25: astore 8
    //   27: aload_0
    //   28: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   31: astore 5
    //   33: aload 5
    //   35: ldc_w 467
    //   38: iconst_2
    //   39: anewarray 19	java/lang/String
    //   42: dup
    //   43: iconst_0
    //   44: ldc_w 453
    //   47: aastore
    //   48: dup
    //   49: iconst_1
    //   50: ldc_w 462
    //   53: aastore
    //   54: ldc_w 1184
    //   57: iconst_2
    //   58: anewarray 19	java/lang/String
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
    //   72: invokevirtual 396	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   75: astore 5
    //   77: aload 5
    //   79: astore_2
    //   80: aload 5
    //   82: invokeinterface 194 1 0
    //   87: ifne +26 -> 113
    //   90: aload 5
    //   92: astore_2
    //   93: invokestatic 1188	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   96: astore 6
    //   98: aload 5
    //   100: ifnull +10 -> 110
    //   103: aload 5
    //   105: invokeinterface 201 1 0
    //   110: aload 6
    //   112: areturn
    //   113: aload 5
    //   115: astore_2
    //   116: aload 5
    //   118: iconst_1
    //   119: invokeinterface 1144 2 0
    //   124: astore 6
    //   126: aload 5
    //   128: astore_2
    //   129: aload 6
    //   131: iconst_0
    //   132: aload 6
    //   134: arraylength
    //   135: invokestatic 1194	com/google/android/gms/internal/zzfjj:zzn	([BII)Lcom/google/android/gms/internal/zzfjj;
    //   138: astore 6
    //   140: aload 5
    //   142: astore_2
    //   143: new 409	com/google/android/gms/internal/zzcls
    //   146: dup
    //   147: invokespecial 1195	com/google/android/gms/internal/zzcls:<init>	()V
    //   150: astore 9
    //   152: aload 5
    //   154: astore_2
    //   155: aload 9
    //   157: aload 6
    //   159: invokevirtual 1198	com/google/android/gms/internal/zzfjs:zza	(Lcom/google/android/gms/internal/zzfjj;)Lcom/google/android/gms/internal/zzfjs;
    //   162: pop
    //   163: aload 5
    //   165: astore_2
    //   166: aload 5
    //   168: iconst_0
    //   169: invokeinterface 1140 2 0
    //   174: istore_3
    //   175: aload 5
    //   177: astore_2
    //   178: aload 8
    //   180: iload_3
    //   181: invokestatic 233	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   184: invokeinterface 1202 2 0
    //   189: checkcast 572	java/util/List
    //   192: astore 7
    //   194: aload 7
    //   196: astore 6
    //   198: aload 7
    //   200: ifnonnull +32 -> 232
    //   203: aload 5
    //   205: astore_2
    //   206: new 569	java/util/ArrayList
    //   209: dup
    //   210: invokespecial 570	java/util/ArrayList:<init>	()V
    //   213: astore 6
    //   215: aload 5
    //   217: astore_2
    //   218: aload 8
    //   220: iload_3
    //   221: invokestatic 233	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   224: aload 6
    //   226: invokeinterface 1205 3 0
    //   231: pop
    //   232: aload 5
    //   234: astore_2
    //   235: aload 6
    //   237: aload 9
    //   239: invokeinterface 587 2 0
    //   244: pop
    //   245: aload 5
    //   247: astore_2
    //   248: aload 5
    //   250: invokeinterface 1114 1 0
    //   255: istore 4
    //   257: iload 4
    //   259: ifne -146 -> 113
    //   262: aload 5
    //   264: ifnull +10 -> 274
    //   267: aload 5
    //   269: invokeinterface 201 1 0
    //   274: aload 8
    //   276: areturn
    //   277: astore 6
    //   279: aload 5
    //   281: astore_2
    //   282: aload_0
    //   283: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   286: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   289: ldc_w 1207
    //   292: aload_1
    //   293: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   296: aload 6
    //   298: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   301: goto -56 -> 245
    //   304: astore 6
    //   306: aload 5
    //   308: astore_2
    //   309: aload_0
    //   310: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   313: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   316: ldc_w 567
    //   319: aload_1
    //   320: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   323: aload 6
    //   325: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   328: aload 5
    //   330: ifnull +10 -> 340
    //   333: aload 5
    //   335: invokeinterface 201 1 0
    //   340: aconst_null
    //   341: areturn
    //   342: astore_1
    //   343: aconst_null
    //   344: astore_2
    //   345: aload_2
    //   346: ifnull +9 -> 355
    //   349: aload_2
    //   350: invokeinterface 201 1 0
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
    //   0	369	0	this	zzcgo
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
    //   25	250	8	localArrayMap	android.support.v4.util.ArrayMap
    //   150	88	9	localzzcls	zzcls
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
  final java.util.Map<Integer, List<zzclv>> zzak(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 404	com/google/android/gms/internal/zzcjl:zzxf	()V
    //   4: aload_0
    //   5: invokevirtual 407	com/google/android/gms/internal/zzcjk:zzve	()V
    //   8: aload_1
    //   9: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_2
    //   14: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   17: pop
    //   18: new 1181	android/support/v4/util/ArrayMap
    //   21: dup
    //   22: invokespecial 1182	android/support/v4/util/ArrayMap:<init>	()V
    //   25: astore 8
    //   27: aload_0
    //   28: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   31: astore 5
    //   33: aload 5
    //   35: ldc_w 492
    //   38: iconst_2
    //   39: anewarray 19	java/lang/String
    //   42: dup
    //   43: iconst_0
    //   44: ldc_w 453
    //   47: aastore
    //   48: dup
    //   49: iconst_1
    //   50: ldc_w 462
    //   53: aastore
    //   54: ldc_w 1211
    //   57: iconst_2
    //   58: anewarray 19	java/lang/String
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
    //   72: invokevirtual 396	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   75: astore 5
    //   77: aload 5
    //   79: astore_2
    //   80: aload 5
    //   82: invokeinterface 194 1 0
    //   87: ifne +26 -> 113
    //   90: aload 5
    //   92: astore_2
    //   93: invokestatic 1188	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   96: astore 6
    //   98: aload 5
    //   100: ifnull +10 -> 110
    //   103: aload 5
    //   105: invokeinterface 201 1 0
    //   110: aload 6
    //   112: areturn
    //   113: aload 5
    //   115: astore_2
    //   116: aload 5
    //   118: iconst_1
    //   119: invokeinterface 1144 2 0
    //   124: astore 6
    //   126: aload 5
    //   128: astore_2
    //   129: aload 6
    //   131: iconst_0
    //   132: aload 6
    //   134: arraylength
    //   135: invokestatic 1194	com/google/android/gms/internal/zzfjj:zzn	([BII)Lcom/google/android/gms/internal/zzfjj;
    //   138: astore 6
    //   140: aload 5
    //   142: astore_2
    //   143: new 482	com/google/android/gms/internal/zzclv
    //   146: dup
    //   147: invokespecial 1212	com/google/android/gms/internal/zzclv:<init>	()V
    //   150: astore 9
    //   152: aload 5
    //   154: astore_2
    //   155: aload 9
    //   157: aload 6
    //   159: invokevirtual 1198	com/google/android/gms/internal/zzfjs:zza	(Lcom/google/android/gms/internal/zzfjj;)Lcom/google/android/gms/internal/zzfjs;
    //   162: pop
    //   163: aload 5
    //   165: astore_2
    //   166: aload 5
    //   168: iconst_0
    //   169: invokeinterface 1140 2 0
    //   174: istore_3
    //   175: aload 5
    //   177: astore_2
    //   178: aload 8
    //   180: iload_3
    //   181: invokestatic 233	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   184: invokeinterface 1202 2 0
    //   189: checkcast 572	java/util/List
    //   192: astore 7
    //   194: aload 7
    //   196: astore 6
    //   198: aload 7
    //   200: ifnonnull +32 -> 232
    //   203: aload 5
    //   205: astore_2
    //   206: new 569	java/util/ArrayList
    //   209: dup
    //   210: invokespecial 570	java/util/ArrayList:<init>	()V
    //   213: astore 6
    //   215: aload 5
    //   217: astore_2
    //   218: aload 8
    //   220: iload_3
    //   221: invokestatic 233	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   224: aload 6
    //   226: invokeinterface 1205 3 0
    //   231: pop
    //   232: aload 5
    //   234: astore_2
    //   235: aload 6
    //   237: aload 9
    //   239: invokeinterface 587 2 0
    //   244: pop
    //   245: aload 5
    //   247: astore_2
    //   248: aload 5
    //   250: invokeinterface 1114 1 0
    //   255: istore 4
    //   257: iload 4
    //   259: ifne -146 -> 113
    //   262: aload 5
    //   264: ifnull +10 -> 274
    //   267: aload 5
    //   269: invokeinterface 201 1 0
    //   274: aload 8
    //   276: areturn
    //   277: astore 6
    //   279: aload 5
    //   281: astore_2
    //   282: aload_0
    //   283: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   286: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   289: ldc_w 1214
    //   292: aload_1
    //   293: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   296: aload 6
    //   298: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   301: goto -56 -> 245
    //   304: astore 6
    //   306: aload 5
    //   308: astore_2
    //   309: aload_0
    //   310: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   313: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   316: ldc_w 567
    //   319: aload_1
    //   320: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   323: aload 6
    //   325: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   328: aload 5
    //   330: ifnull +10 -> 340
    //   333: aload 5
    //   335: invokeinterface 201 1 0
    //   340: aconst_null
    //   341: areturn
    //   342: astore_1
    //   343: aconst_null
    //   344: astore_2
    //   345: aload_2
    //   346: ifnull +9 -> 355
    //   349: aload_2
    //   350: invokeinterface 201 1 0
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
    //   0	369	0	this	zzcgo
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
    //   25	250	8	localArrayMap	android.support.v4.util.ArrayMap
    //   150	88	9	localzzclv	zzclv
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
  protected final long zzal(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_2
    //   6: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   9: pop
    //   10: aload_0
    //   11: invokevirtual 407	com/google/android/gms/internal/zzcjk:zzve	()V
    //   14: aload_0
    //   15: invokevirtual 404	com/google/android/gms/internal/zzcjl:zzxf	()V
    //   18: aload_0
    //   19: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   22: astore 8
    //   24: aload 8
    //   26: invokevirtual 605	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   29: aload_0
    //   30: new 351	java/lang/StringBuilder
    //   33: dup
    //   34: aload_2
    //   35: invokestatic 354	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   38: invokevirtual 358	java/lang/String:length	()I
    //   41: bipush 32
    //   43: iadd
    //   44: invokespecial 361	java/lang/StringBuilder:<init>	(I)V
    //   47: ldc_w 1219
    //   50: invokevirtual 367	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: aload_2
    //   54: invokevirtual 367	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: ldc_w 1221
    //   60: invokevirtual 367	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   63: invokevirtual 372	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   66: iconst_1
    //   67: anewarray 19	java/lang/String
    //   70: dup
    //   71: iconst_0
    //   72: aload_1
    //   73: aastore
    //   74: ldc2_w 472
    //   77: invokespecial 1223	com/google/android/gms/internal/zzcgo:zza	(Ljava/lang/String;[Ljava/lang/String;J)J
    //   80: lstore 5
    //   82: lload 5
    //   84: lstore_3
    //   85: lload 5
    //   87: ldc2_w 472
    //   90: lcmp
    //   91: ifne +92 -> 183
    //   94: new 276	android/content/ContentValues
    //   97: dup
    //   98: invokespecial 449	android/content/ContentValues:<init>	()V
    //   101: astore 7
    //   103: aload 7
    //   105: ldc_w 451
    //   108: aload_1
    //   109: invokevirtual 280	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   112: aload 7
    //   114: ldc_w 1225
    //   117: iconst_0
    //   118: invokestatic 233	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   121: invokevirtual 456	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   124: aload 7
    //   126: ldc -117
    //   128: iconst_0
    //   129: invokestatic 233	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   132: invokevirtual 456	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   135: aload 8
    //   137: ldc_w 1227
    //   140: aconst_null
    //   141: aload 7
    //   143: iconst_5
    //   144: invokevirtual 471	android/database/sqlite/SQLiteDatabase:insertWithOnConflict	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    //   147: ldc2_w 472
    //   150: lcmp
    //   151: ifne +30 -> 181
    //   154: aload_0
    //   155: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   158: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   161: ldc_w 1229
    //   164: aload_1
    //   165: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   168: aload_2
    //   169: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   172: aload 8
    //   174: invokevirtual 608	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   177: ldc2_w 472
    //   180: lreturn
    //   181: lconst_0
    //   182: lstore_3
    //   183: new 276	android/content/ContentValues
    //   186: dup
    //   187: invokespecial 449	android/content/ContentValues:<init>	()V
    //   190: astore 7
    //   192: aload 7
    //   194: ldc_w 451
    //   197: aload_1
    //   198: invokevirtual 280	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   201: aload 7
    //   203: aload_2
    //   204: lconst_1
    //   205: lload_3
    //   206: ladd
    //   207: invokestatic 248	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   210: invokevirtual 283	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   213: aload 8
    //   215: ldc_w 1227
    //   218: aload 7
    //   220: ldc_w 785
    //   223: iconst_1
    //   224: anewarray 19	java/lang/String
    //   227: dup
    //   228: iconst_0
    //   229: aload_1
    //   230: aastore
    //   231: invokevirtual 684	android/database/sqlite/SQLiteDatabase:update	(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   234: i2l
    //   235: lconst_0
    //   236: lcmp
    //   237: ifne +30 -> 267
    //   240: aload_0
    //   241: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   244: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   247: ldc_w 1231
    //   250: aload_1
    //   251: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   254: aload_2
    //   255: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   258: aload 8
    //   260: invokevirtual 608	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   263: ldc2_w 472
    //   266: lreturn
    //   267: aload 8
    //   269: invokevirtual 614	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   272: aload 8
    //   274: invokevirtual 608	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   277: lload_3
    //   278: lreturn
    //   279: astore 7
    //   281: lconst_0
    //   282: lstore_3
    //   283: aload_0
    //   284: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   287: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   290: ldc_w 1233
    //   293: aload_1
    //   294: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   297: aload_2
    //   298: aload 7
    //   300: invokevirtual 430	com/google/android/gms/internal/zzcho:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   303: aload 8
    //   305: invokevirtual 608	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   308: lload_3
    //   309: lreturn
    //   310: astore_1
    //   311: aload 8
    //   313: invokevirtual 608	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   316: aload_1
    //   317: athrow
    //   318: astore 7
    //   320: goto -37 -> 283
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	323	0	this	zzcgo
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
  
  protected final boolean zzaxz()
  {
    return false;
  }
  
  /* Error */
  public final String zzayf()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   7: astore_1
    //   8: aload_1
    //   9: ldc_w 1237
    //   12: aconst_null
    //   13: invokevirtual 188	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   16: astore_1
    //   17: aload_1
    //   18: astore_2
    //   19: aload_1
    //   20: invokeinterface 194 1 0
    //   25: ifeq +29 -> 54
    //   28: aload_1
    //   29: astore_2
    //   30: aload_1
    //   31: iconst_0
    //   32: invokeinterface 261 2 0
    //   37: astore_3
    //   38: aload_3
    //   39: astore_2
    //   40: aload_1
    //   41: ifnull +11 -> 52
    //   44: aload_1
    //   45: invokeinterface 201 1 0
    //   50: aload_3
    //   51: astore_2
    //   52: aload_2
    //   53: areturn
    //   54: aload 4
    //   56: astore_2
    //   57: aload_1
    //   58: ifnull -6 -> 52
    //   61: aload_1
    //   62: invokeinterface 201 1 0
    //   67: aconst_null
    //   68: areturn
    //   69: astore_3
    //   70: aconst_null
    //   71: astore_1
    //   72: aload_1
    //   73: astore_2
    //   74: aload_0
    //   75: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   78: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   81: ldc_w 1239
    //   84: aload_3
    //   85: invokevirtual 237	com/google/android/gms/internal/zzcho:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   88: aload 4
    //   90: astore_2
    //   91: aload_1
    //   92: ifnull -40 -> 52
    //   95: aload_1
    //   96: invokeinterface 201 1 0
    //   101: aconst_null
    //   102: areturn
    //   103: astore_1
    //   104: aconst_null
    //   105: astore_2
    //   106: aload_2
    //   107: ifnull +9 -> 116
    //   110: aload_2
    //   111: invokeinterface 201 1 0
    //   116: aload_1
    //   117: athrow
    //   118: astore_1
    //   119: goto -13 -> 106
    //   122: astore_3
    //   123: goto -51 -> 72
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	this	zzcgo
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
  
  public final boolean zzayg()
  {
    return zzb("select count(1) > 0 from queue where has_realtime = 1", null) != 0L;
  }
  
  final void zzayh()
  {
    zzve();
    zzxf();
    if (!zzayn()) {}
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
          l1 = zzawz().zzjcu.get();
          l2 = zzws().elapsedRealtime();
        } while (Math.abs(l2 - l1) <= ((Long)zzchc.zzjbb.get()).longValue());
        zzawz().zzjcu.set(l2);
        zzve();
        zzxf();
      } while (!zzayn());
      i = getWritableDatabase().delete("queue", "abs(bundle_end_timestamp - ?) > cast(? as integer)", new String[] { String.valueOf(zzws().currentTimeMillis()), String.valueOf(zzcgn.zzayb()) });
    } while (i <= 0);
    zzawy().zzazj().zzj("Deleted stale rows. rowsDeleted", Integer.valueOf(i));
  }
  
  public final long zzayi()
  {
    return zza("select max(bundle_end_timestamp) from queue", null, 0L);
  }
  
  public final long zzayj()
  {
    return zza("select max(timestamp) from raw_events", null, 0L);
  }
  
  public final boolean zzayk()
  {
    return zzb("select count(1) > 0 from raw_events", null) != 0L;
  }
  
  public final boolean zzayl()
  {
    return zzb("select count(1) > 0 from raw_events where realtime = 1", null) != 0L;
  }
  
  public final long zzaym()
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
        zzawy().zzazd().zzj("Error querying raw events", localSQLiteException);
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
  public final String zzba(long paramLong)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aload_0
    //   4: invokevirtual 407	com/google/android/gms/internal/zzcjk:zzve	()V
    //   7: aload_0
    //   8: invokevirtual 404	com/google/android/gms/internal/zzcjl:zzxf	()V
    //   11: aload_0
    //   12: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   15: ldc_w 1303
    //   18: iconst_1
    //   19: anewarray 19	java/lang/String
    //   22: dup
    //   23: iconst_0
    //   24: lload_1
    //   25: invokestatic 1281	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   28: aastore
    //   29: invokevirtual 188	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   32: astore_3
    //   33: aload_3
    //   34: astore 4
    //   36: aload_3
    //   37: invokeinterface 194 1 0
    //   42: ifne +40 -> 82
    //   45: aload_3
    //   46: astore 4
    //   48: aload_0
    //   49: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   52: invokevirtual 1003	com/google/android/gms/internal/zzchm:zzazj	()Lcom/google/android/gms/internal/zzcho;
    //   55: ldc_w 1305
    //   58: invokevirtual 243	com/google/android/gms/internal/zzcho:log	(Ljava/lang/String;)V
    //   61: aload 6
    //   63: astore 4
    //   65: aload_3
    //   66: ifnull +13 -> 79
    //   69: aload_3
    //   70: invokeinterface 201 1 0
    //   75: aload 6
    //   77: astore 4
    //   79: aload 4
    //   81: areturn
    //   82: aload_3
    //   83: astore 4
    //   85: aload_3
    //   86: iconst_0
    //   87: invokeinterface 261 2 0
    //   92: astore 5
    //   94: aload 5
    //   96: astore 4
    //   98: aload_3
    //   99: ifnull -20 -> 79
    //   102: aload_3
    //   103: invokeinterface 201 1 0
    //   108: aload 5
    //   110: areturn
    //   111: astore 5
    //   113: aconst_null
    //   114: astore_3
    //   115: aload_3
    //   116: astore 4
    //   118: aload_0
    //   119: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   122: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   125: ldc_w 1307
    //   128: aload 5
    //   130: invokevirtual 237	com/google/android/gms/internal/zzcho:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   133: aload 6
    //   135: astore 4
    //   137: aload_3
    //   138: ifnull -59 -> 79
    //   141: aload_3
    //   142: invokeinterface 201 1 0
    //   147: aconst_null
    //   148: areturn
    //   149: astore_3
    //   150: aconst_null
    //   151: astore 4
    //   153: aload 4
    //   155: ifnull +10 -> 165
    //   158: aload 4
    //   160: invokeinterface 201 1 0
    //   165: aload_3
    //   166: athrow
    //   167: astore_3
    //   168: goto -15 -> 153
    //   171: astore 5
    //   173: goto -58 -> 115
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	176	0	this	zzcgo
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
  public final List<zzcgl> zzc(String paramString, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 407	com/google/android/gms/internal/zzcjk:zzve	()V
    //   4: aload_0
    //   5: invokevirtual 404	com/google/android/gms/internal/zzcjl:zzxf	()V
    //   8: new 569	java/util/ArrayList
    //   11: dup
    //   12: invokespecial 570	java/util/ArrayList:<init>	()V
    //   15: astore 12
    //   17: aload_0
    //   18: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   21: ldc_w 951
    //   24: bipush 13
    //   26: anewarray 19	java/lang/String
    //   29: dup
    //   30: iconst_0
    //   31: ldc_w 451
    //   34: aastore
    //   35: dup
    //   36: iconst_1
    //   37: ldc 39
    //   39: aastore
    //   40: dup
    //   41: iconst_2
    //   42: ldc_w 390
    //   45: aastore
    //   46: dup
    //   47: iconst_3
    //   48: ldc_w 893
    //   51: aastore
    //   52: dup
    //   53: iconst_4
    //   54: ldc_w 901
    //   57: aastore
    //   58: dup
    //   59: iconst_5
    //   60: ldc_w 907
    //   63: aastore
    //   64: dup
    //   65: bipush 6
    //   67: ldc_w 912
    //   70: aastore
    //   71: dup
    //   72: bipush 7
    //   74: ldc_w 917
    //   77: aastore
    //   78: dup
    //   79: bipush 8
    //   81: ldc_w 926
    //   84: aastore
    //   85: dup
    //   86: bipush 9
    //   88: ldc_w 931
    //   91: aastore
    //   92: dup
    //   93: bipush 10
    //   95: ldc_w 936
    //   98: aastore
    //   99: dup
    //   100: bipush 11
    //   102: ldc_w 941
    //   105: aastore
    //   106: dup
    //   107: bipush 12
    //   109: ldc_w 946
    //   112: aastore
    //   113: aload_1
    //   114: aload_2
    //   115: aconst_null
    //   116: aconst_null
    //   117: ldc_w 1311
    //   120: ldc_w 1313
    //   123: invokevirtual 1316	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   126: astore_1
    //   127: aload_1
    //   128: invokeinterface 194 1 0
    //   133: istore_3
    //   134: iload_3
    //   135: ifne +18 -> 153
    //   138: aload_1
    //   139: ifnull +9 -> 148
    //   142: aload_1
    //   143: invokeinterface 201 1 0
    //   148: aload 12
    //   150: astore_2
    //   151: aload_2
    //   152: areturn
    //   153: aload 12
    //   155: invokeinterface 575 1 0
    //   160: sipush 1000
    //   163: if_icmplt +35 -> 198
    //   166: aload_0
    //   167: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   170: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   173: ldc_w 1318
    //   176: sipush 1000
    //   179: invokestatic 233	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   182: invokevirtual 237	com/google/android/gms/internal/zzcho:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   185: aload_1
    //   186: ifnull +9 -> 195
    //   189: aload_1
    //   190: invokeinterface 201 1 0
    //   195: aload 12
    //   197: areturn
    //   198: aload_1
    //   199: iconst_0
    //   200: invokeinterface 261 2 0
    //   205: astore_2
    //   206: aload_1
    //   207: iconst_1
    //   208: invokeinterface 261 2 0
    //   213: astore 13
    //   215: aload_1
    //   216: iconst_2
    //   217: invokeinterface 261 2 0
    //   222: astore 14
    //   224: aload_0
    //   225: aload_1
    //   226: iconst_3
    //   227: invokespecial 1128	com/google/android/gms/internal/zzcgo:zza	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   230: astore 15
    //   232: aload_1
    //   233: iconst_4
    //   234: invokeinterface 1140 2 0
    //   239: ifeq +185 -> 424
    //   242: iconst_1
    //   243: istore_3
    //   244: aload_1
    //   245: iconst_5
    //   246: invokeinterface 261 2 0
    //   251: astore 16
    //   253: aload_1
    //   254: bipush 6
    //   256: invokeinterface 198 2 0
    //   261: lstore 4
    //   263: aload_0
    //   264: invokevirtual 624	com/google/android/gms/internal/zzcjk:zzawu	()Lcom/google/android/gms/internal/zzclq;
    //   267: aload_1
    //   268: bipush 7
    //   270: invokeinterface 1144 2 0
    //   275: getstatic 1150	com/google/android/gms/internal/zzcha:CREATOR	Landroid/os/Parcelable$Creator;
    //   278: invokevirtual 1153	com/google/android/gms/internal/zzclq:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   281: checkcast 1146	com/google/android/gms/internal/zzcha
    //   284: astore 17
    //   286: aload_1
    //   287: bipush 8
    //   289: invokeinterface 198 2 0
    //   294: lstore 6
    //   296: aload_0
    //   297: invokevirtual 624	com/google/android/gms/internal/zzcjk:zzawu	()Lcom/google/android/gms/internal/zzclq;
    //   300: aload_1
    //   301: bipush 9
    //   303: invokeinterface 1144 2 0
    //   308: getstatic 1150	com/google/android/gms/internal/zzcha:CREATOR	Landroid/os/Parcelable$Creator;
    //   311: invokevirtual 1153	com/google/android/gms/internal/zzclq:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   314: checkcast 1146	com/google/android/gms/internal/zzcha
    //   317: astore 18
    //   319: aload_1
    //   320: bipush 10
    //   322: invokeinterface 198 2 0
    //   327: lstore 8
    //   329: aload_1
    //   330: bipush 11
    //   332: invokeinterface 198 2 0
    //   337: lstore 10
    //   339: aload_0
    //   340: invokevirtual 624	com/google/android/gms/internal/zzcjk:zzawu	()Lcom/google/android/gms/internal/zzclq;
    //   343: aload_1
    //   344: bipush 12
    //   346: invokeinterface 1144 2 0
    //   351: getstatic 1150	com/google/android/gms/internal/zzcha:CREATOR	Landroid/os/Parcelable$Creator;
    //   354: invokevirtual 1153	com/google/android/gms/internal/zzclq:zzb	([BLandroid/os/Parcelable$Creator;)Landroid/os/Parcelable;
    //   357: checkcast 1146	com/google/android/gms/internal/zzcha
    //   360: astore 19
    //   362: aload 12
    //   364: new 869	com/google/android/gms/internal/zzcgl
    //   367: dup
    //   368: aload_2
    //   369: aload 13
    //   371: new 878	com/google/android/gms/internal/zzcln
    //   374: dup
    //   375: aload 14
    //   377: lload 8
    //   379: aload 15
    //   381: aload 13
    //   383: invokespecial 1156	com/google/android/gms/internal/zzcln:<init>	(Ljava/lang/String;JLjava/lang/Object;Ljava/lang/String;)V
    //   386: lload 6
    //   388: iload_3
    //   389: aload 16
    //   391: aload 17
    //   393: lload 4
    //   395: aload 18
    //   397: lload 10
    //   399: aload 19
    //   401: invokespecial 1159	com/google/android/gms/internal/zzcgl:<init>	(Ljava/lang/String;Ljava/lang/String;Lcom/google/android/gms/internal/zzcln;JZLjava/lang/String;Lcom/google/android/gms/internal/zzcha;JLcom/google/android/gms/internal/zzcha;JLcom/google/android/gms/internal/zzcha;)V
    //   404: invokeinterface 587 2 0
    //   409: pop
    //   410: aload_1
    //   411: invokeinterface 1114 1 0
    //   416: istore_3
    //   417: iload_3
    //   418: ifne -265 -> 153
    //   421: goto -236 -> 185
    //   424: iconst_0
    //   425: istore_3
    //   426: goto -182 -> 244
    //   429: astore_2
    //   430: aconst_null
    //   431: astore_1
    //   432: aload_0
    //   433: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   436: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   439: ldc_w 1320
    //   442: aload_2
    //   443: invokevirtual 237	com/google/android/gms/internal/zzcho:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   446: invokestatic 1324	java/util/Collections:emptyList	()Ljava/util/List;
    //   449: astore 12
    //   451: aload 12
    //   453: astore_2
    //   454: aload_1
    //   455: ifnull -304 -> 151
    //   458: aload_1
    //   459: invokeinterface 201 1 0
    //   464: aload 12
    //   466: areturn
    //   467: astore_2
    //   468: aconst_null
    //   469: astore_1
    //   470: aload_1
    //   471: ifnull +9 -> 480
    //   474: aload_1
    //   475: invokeinterface 201 1 0
    //   480: aload_2
    //   481: athrow
    //   482: astore_2
    //   483: goto -13 -> 470
    //   486: astore_2
    //   487: goto -17 -> 470
    //   490: astore_2
    //   491: goto -59 -> 432
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	494	0	this	zzcgo
    //   0	494	1	paramString	String
    //   0	494	2	paramArrayOfString	String[]
    //   133	293	3	bool	boolean
    //   261	133	4	l1	long
    //   294	93	6	l2	long
    //   327	51	8	l3	long
    //   337	61	10	l4	long
    //   15	450	12	localObject1	Object
    //   213	169	13	str1	String
    //   222	154	14	str2	String
    //   230	150	15	localObject2	Object
    //   251	139	16	str3	String
    //   284	108	17	localzzcha1	zzcha
    //   317	79	18	localzzcha2	zzcha
    //   360	40	19	localzzcha3	zzcha
    // Exception table:
    //   from	to	target	type
    //   17	127	429	android/database/sqlite/SQLiteException
    //   17	127	467	finally
    //   127	134	482	finally
    //   153	185	482	finally
    //   198	242	482	finally
    //   244	417	482	finally
    //   432	451	486	finally
    //   127	134	490	android/database/sqlite/SQLiteException
    //   153	185	490	android/database/sqlite/SQLiteException
    //   198	242	490	android/database/sqlite/SQLiteException
    //   244	417	490	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  public final List<zzclp> zzg(String paramString1, String paramString2, String paramString3)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 8
    //   3: aload_1
    //   4: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_0
    //   9: invokevirtual 407	com/google/android/gms/internal/zzcjk:zzve	()V
    //   12: aload_0
    //   13: invokevirtual 404	com/google/android/gms/internal/zzcjl:zzxf	()V
    //   16: new 569	java/util/ArrayList
    //   19: dup
    //   20: invokespecial 570	java/util/ArrayList:<init>	()V
    //   23: astore 9
    //   25: new 569	java/util/ArrayList
    //   28: dup
    //   29: iconst_3
    //   30: invokespecial 1328	java/util/ArrayList:<init>	(I)V
    //   33: astore 10
    //   35: aload 10
    //   37: aload_1
    //   38: invokeinterface 587 2 0
    //   43: pop
    //   44: new 351	java/lang/StringBuilder
    //   47: dup
    //   48: ldc_w 662
    //   51: invokespecial 1167	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   54: astore 7
    //   56: aload_2
    //   57: invokestatic 416	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   60: ifne +21 -> 81
    //   63: aload 10
    //   65: aload_2
    //   66: invokeinterface 587 2 0
    //   71: pop
    //   72: aload 7
    //   74: ldc_w 1330
    //   77: invokevirtual 367	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   80: pop
    //   81: aload_3
    //   82: invokestatic 416	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   85: ifne +30 -> 115
    //   88: aload 10
    //   90: aload_3
    //   91: invokestatic 354	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   94: ldc_w 1332
    //   97: invokevirtual 1335	java/lang/String:concat	(Ljava/lang/String;)Ljava/lang/String;
    //   100: invokeinterface 587 2 0
    //   105: pop
    //   106: aload 7
    //   108: ldc_w 1337
    //   111: invokevirtual 367	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   114: pop
    //   115: aload 10
    //   117: aload 10
    //   119: invokeinterface 575 1 0
    //   124: anewarray 19	java/lang/String
    //   127: invokeinterface 1341 2 0
    //   132: checkcast 1342	[Ljava/lang/String;
    //   135: astore 10
    //   137: aload_0
    //   138: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   141: astore 11
    //   143: aload 7
    //   145: invokevirtual 372	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   148: astore 7
    //   150: aload 11
    //   152: ldc_w 1061
    //   155: iconst_4
    //   156: anewarray 19	java/lang/String
    //   159: dup
    //   160: iconst_0
    //   161: ldc_w 390
    //   164: aastore
    //   165: dup
    //   166: iconst_1
    //   167: ldc_w 1052
    //   170: aastore
    //   171: dup
    //   172: iconst_2
    //   173: ldc_w 893
    //   176: aastore
    //   177: dup
    //   178: iconst_3
    //   179: ldc 39
    //   181: aastore
    //   182: aload 7
    //   184: aload 10
    //   186: aconst_null
    //   187: aconst_null
    //   188: ldc_w 1311
    //   191: ldc_w 1313
    //   194: invokevirtual 1316	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   197: astore 7
    //   199: aload_2
    //   200: astore 8
    //   202: aload 7
    //   204: invokeinterface 194 1 0
    //   209: istore 4
    //   211: iload 4
    //   213: ifne +18 -> 231
    //   216: aload 7
    //   218: ifnull +10 -> 228
    //   221: aload 7
    //   223: invokeinterface 201 1 0
    //   228: aload 9
    //   230: areturn
    //   231: aload_2
    //   232: astore 8
    //   234: aload 9
    //   236: invokeinterface 575 1 0
    //   241: sipush 1000
    //   244: if_icmplt +40 -> 284
    //   247: aload_2
    //   248: astore 8
    //   250: aload_0
    //   251: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   254: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   257: ldc_w 1344
    //   260: sipush 1000
    //   263: invokestatic 233	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   266: invokevirtual 237	com/google/android/gms/internal/zzcho:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   269: aload 7
    //   271: ifnull +10 -> 281
    //   274: aload 7
    //   276: invokeinterface 201 1 0
    //   281: aload 9
    //   283: areturn
    //   284: aload_2
    //   285: astore 8
    //   287: aload 7
    //   289: iconst_0
    //   290: invokeinterface 261 2 0
    //   295: astore 10
    //   297: aload_2
    //   298: astore 8
    //   300: aload 7
    //   302: iconst_1
    //   303: invokeinterface 198 2 0
    //   308: lstore 5
    //   310: aload_2
    //   311: astore 8
    //   313: aload_0
    //   314: aload 7
    //   316: iconst_2
    //   317: invokespecial 1128	com/google/android/gms/internal/zzcgo:zza	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   320: astore 11
    //   322: aload_2
    //   323: astore 8
    //   325: aload 7
    //   327: iconst_3
    //   328: invokeinterface 261 2 0
    //   333: astore_2
    //   334: aload 11
    //   336: ifnonnull +35 -> 371
    //   339: aload_0
    //   340: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   343: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   346: ldc_w 1346
    //   349: aload_1
    //   350: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   353: aload_2
    //   354: aload_3
    //   355: invokevirtual 430	com/google/android/gms/internal/zzcho:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   358: aload 7
    //   360: invokeinterface 1114 1 0
    //   365: ifne -134 -> 231
    //   368: goto -99 -> 269
    //   371: aload 9
    //   373: new 1035	com/google/android/gms/internal/zzclp
    //   376: dup
    //   377: aload_1
    //   378: aload_2
    //   379: aload 10
    //   381: lload 5
    //   383: aload 11
    //   385: invokespecial 1131	com/google/android/gms/internal/zzclp:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   388: invokeinterface 587 2 0
    //   393: pop
    //   394: goto -36 -> 358
    //   397: astore 8
    //   399: aload 7
    //   401: astore_3
    //   402: aload 8
    //   404: astore 7
    //   406: aload_0
    //   407: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   410: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   413: ldc_w 1348
    //   416: aload_1
    //   417: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   420: aload_2
    //   421: aload 7
    //   423: invokevirtual 430	com/google/android/gms/internal/zzcho:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   426: aload_3
    //   427: ifnull +9 -> 436
    //   430: aload_3
    //   431: invokeinterface 201 1 0
    //   436: aconst_null
    //   437: areturn
    //   438: astore_1
    //   439: aload 8
    //   441: astore_2
    //   442: aload_2
    //   443: ifnull +9 -> 452
    //   446: aload_2
    //   447: invokeinterface 201 1 0
    //   452: aload_1
    //   453: athrow
    //   454: astore_1
    //   455: aload 7
    //   457: astore_2
    //   458: goto -16 -> 442
    //   461: astore_1
    //   462: aload_3
    //   463: astore_2
    //   464: goto -22 -> 442
    //   467: astore 7
    //   469: aconst_null
    //   470: astore_3
    //   471: goto -65 -> 406
    //   474: astore_2
    //   475: aload 7
    //   477: astore_3
    //   478: aload_2
    //   479: astore 7
    //   481: aload 8
    //   483: astore_2
    //   484: goto -78 -> 406
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	487	0	this	zzcgo
    //   0	487	1	paramString1	String
    //   0	487	2	paramString2	String
    //   0	487	3	paramString3	String
    //   209	3	4	bool	boolean
    //   308	74	5	l	long
    //   54	402	7	localObject1	Object
    //   467	9	7	localSQLiteException1	SQLiteException
    //   479	1	7	str1	String
    //   1	323	8	str2	String
    //   397	85	8	localSQLiteException2	SQLiteException
    //   23	349	9	localArrayList	ArrayList
    //   33	347	10	localObject2	Object
    //   141	243	11	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   339	358	397	android/database/sqlite/SQLiteException
    //   358	368	397	android/database/sqlite/SQLiteException
    //   371	394	397	android/database/sqlite/SQLiteException
    //   25	81	438	finally
    //   81	115	438	finally
    //   115	199	438	finally
    //   202	211	454	finally
    //   234	247	454	finally
    //   250	269	454	finally
    //   287	297	454	finally
    //   300	310	454	finally
    //   313	322	454	finally
    //   325	334	454	finally
    //   339	358	454	finally
    //   358	368	454	finally
    //   371	394	454	finally
    //   406	426	461	finally
    //   25	81	467	android/database/sqlite/SQLiteException
    //   81	115	467	android/database/sqlite/SQLiteException
    //   115	199	467	android/database/sqlite/SQLiteException
    //   202	211	474	android/database/sqlite/SQLiteException
    //   234	247	474	android/database/sqlite/SQLiteException
    //   250	269	474	android/database/sqlite/SQLiteException
    //   287	297	474	android/database/sqlite/SQLiteException
    //   300	310	474	android/database/sqlite/SQLiteException
    //   313	322	474	android/database/sqlite/SQLiteException
    //   325	334	474	android/database/sqlite/SQLiteException
  }
  
  public final List<zzcgl> zzh(String paramString1, String paramString2, String paramString3)
  {
    zzbq.zzgm(paramString1);
    zzve();
    zzxf();
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
  
  /* Error */
  public final List<zzclp> zzja(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aload_1
    //   4: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_0
    //   9: invokevirtual 407	com/google/android/gms/internal/zzcjk:zzve	()V
    //   12: aload_0
    //   13: invokevirtual 404	com/google/android/gms/internal/zzcjl:zzxf	()V
    //   16: new 569	java/util/ArrayList
    //   19: dup
    //   20: invokespecial 570	java/util/ArrayList:<init>	()V
    //   23: astore 8
    //   25: aload_0
    //   26: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   29: ldc_w 1061
    //   32: iconst_4
    //   33: anewarray 19	java/lang/String
    //   36: dup
    //   37: iconst_0
    //   38: ldc_w 390
    //   41: aastore
    //   42: dup
    //   43: iconst_1
    //   44: ldc 39
    //   46: aastore
    //   47: dup
    //   48: iconst_2
    //   49: ldc_w 1052
    //   52: aastore
    //   53: dup
    //   54: iconst_3
    //   55: ldc_w 893
    //   58: aastore
    //   59: ldc_w 662
    //   62: iconst_1
    //   63: anewarray 19	java/lang/String
    //   66: dup
    //   67: iconst_0
    //   68: aload_1
    //   69: aastore
    //   70: aconst_null
    //   71: aconst_null
    //   72: ldc_w 1311
    //   75: ldc_w 1357
    //   78: invokevirtual 1316	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   81: astore 5
    //   83: aload 5
    //   85: invokeinterface 194 1 0
    //   90: istore_2
    //   91: iload_2
    //   92: ifne +18 -> 110
    //   95: aload 5
    //   97: ifnull +10 -> 107
    //   100: aload 5
    //   102: invokeinterface 201 1 0
    //   107: aload 8
    //   109: areturn
    //   110: aload 5
    //   112: iconst_0
    //   113: invokeinterface 261 2 0
    //   118: astore 9
    //   120: aload 5
    //   122: iconst_1
    //   123: invokeinterface 261 2 0
    //   128: astore 7
    //   130: aload 7
    //   132: astore 6
    //   134: aload 7
    //   136: ifnonnull +8 -> 144
    //   139: ldc_w 1359
    //   142: astore 6
    //   144: aload 5
    //   146: iconst_2
    //   147: invokeinterface 198 2 0
    //   152: lstore_3
    //   153: aload_0
    //   154: aload 5
    //   156: iconst_3
    //   157: invokespecial 1128	com/google/android/gms/internal/zzcgo:zza	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   160: astore 7
    //   162: aload 7
    //   164: ifnonnull +47 -> 211
    //   167: aload_0
    //   168: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   171: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   174: ldc_w 1361
    //   177: aload_1
    //   178: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   181: invokevirtual 237	com/google/android/gms/internal/zzcho:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   184: aload 5
    //   186: invokeinterface 1114 1 0
    //   191: istore_2
    //   192: iload_2
    //   193: ifne -83 -> 110
    //   196: aload 5
    //   198: ifnull +10 -> 208
    //   201: aload 5
    //   203: invokeinterface 201 1 0
    //   208: aload 8
    //   210: areturn
    //   211: aload 8
    //   213: new 1035	com/google/android/gms/internal/zzclp
    //   216: dup
    //   217: aload_1
    //   218: aload 6
    //   220: aload 9
    //   222: lload_3
    //   223: aload 7
    //   225: invokespecial 1131	com/google/android/gms/internal/zzclp:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   228: invokeinterface 587 2 0
    //   233: pop
    //   234: goto -50 -> 184
    //   237: astore 6
    //   239: aload_0
    //   240: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   243: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   246: ldc_w 1363
    //   249: aload_1
    //   250: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   253: aload 6
    //   255: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   258: aload 5
    //   260: ifnull +10 -> 270
    //   263: aload 5
    //   265: invokeinterface 201 1 0
    //   270: aconst_null
    //   271: areturn
    //   272: astore_1
    //   273: aload 6
    //   275: astore 5
    //   277: aload 5
    //   279: ifnull +10 -> 289
    //   282: aload 5
    //   284: invokeinterface 201 1 0
    //   289: aload_1
    //   290: athrow
    //   291: astore_1
    //   292: goto -15 -> 277
    //   295: astore_1
    //   296: goto -19 -> 277
    //   299: astore 6
    //   301: aconst_null
    //   302: astore 5
    //   304: goto -65 -> 239
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	307	0	this	zzcgo
    //   0	307	1	paramString	String
    //   90	103	2	bool	boolean
    //   152	71	3	l	long
    //   81	222	5	localObject1	Object
    //   1	218	6	localObject2	Object
    //   237	37	6	localSQLiteException1	SQLiteException
    //   299	1	6	localSQLiteException2	SQLiteException
    //   128	96	7	localObject3	Object
    //   23	189	8	localArrayList	ArrayList
    //   118	103	9	str	String
    // Exception table:
    //   from	to	target	type
    //   83	91	237	android/database/sqlite/SQLiteException
    //   110	130	237	android/database/sqlite/SQLiteException
    //   144	162	237	android/database/sqlite/SQLiteException
    //   167	184	237	android/database/sqlite/SQLiteException
    //   184	192	237	android/database/sqlite/SQLiteException
    //   211	234	237	android/database/sqlite/SQLiteException
    //   25	83	272	finally
    //   83	91	291	finally
    //   110	130	291	finally
    //   144	162	291	finally
    //   167	184	291	finally
    //   184	192	291	finally
    //   211	234	291	finally
    //   239	258	295	finally
    //   25	83	299	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  public final zzcgh zzjb(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 407	com/google/android/gms/internal/zzcjk:zzve	()V
    //   9: aload_0
    //   10: invokevirtual 404	com/google/android/gms/internal/zzcjl:zzxf	()V
    //   13: aload_0
    //   14: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: ldc_w 660
    //   20: bipush 24
    //   22: anewarray 19	java/lang/String
    //   25: dup
    //   26: iconst_0
    //   27: ldc_w 694
    //   30: aastore
    //   31: dup
    //   32: iconst_1
    //   33: ldc_w 699
    //   36: aastore
    //   37: dup
    //   38: iconst_2
    //   39: ldc_w 704
    //   42: aastore
    //   43: dup
    //   44: iconst_3
    //   45: ldc_w 709
    //   48: aastore
    //   49: dup
    //   50: iconst_4
    //   51: ldc 65
    //   53: aastore
    //   54: dup
    //   55: iconst_5
    //   56: ldc_w 718
    //   59: aastore
    //   60: dup
    //   61: bipush 6
    //   63: ldc 45
    //   65: aastore
    //   66: dup
    //   67: bipush 7
    //   69: ldc 49
    //   71: aastore
    //   72: dup
    //   73: bipush 8
    //   75: ldc 53
    //   77: aastore
    //   78: dup
    //   79: bipush 9
    //   81: ldc 57
    //   83: aastore
    //   84: dup
    //   85: bipush 10
    //   87: ldc 61
    //   89: aastore
    //   90: dup
    //   91: bipush 11
    //   93: ldc 69
    //   95: aastore
    //   96: dup
    //   97: bipush 12
    //   99: ldc 73
    //   101: aastore
    //   102: dup
    //   103: bipush 13
    //   105: ldc 77
    //   107: aastore
    //   108: dup
    //   109: bipush 14
    //   111: ldc 81
    //   113: aastore
    //   114: dup
    //   115: bipush 15
    //   117: ldc 89
    //   119: aastore
    //   120: dup
    //   121: bipush 16
    //   123: ldc 93
    //   125: aastore
    //   126: dup
    //   127: bipush 17
    //   129: ldc 97
    //   131: aastore
    //   132: dup
    //   133: bipush 18
    //   135: ldc 101
    //   137: aastore
    //   138: dup
    //   139: bipush 19
    //   141: ldc 105
    //   143: aastore
    //   144: dup
    //   145: bipush 20
    //   147: ldc 109
    //   149: aastore
    //   150: dup
    //   151: bipush 21
    //   153: ldc 113
    //   155: aastore
    //   156: dup
    //   157: bipush 22
    //   159: ldc 117
    //   161: aastore
    //   162: dup
    //   163: bipush 23
    //   165: ldc 121
    //   167: aastore
    //   168: ldc_w 662
    //   171: iconst_1
    //   172: anewarray 19	java/lang/String
    //   175: dup
    //   176: iconst_0
    //   177: aload_1
    //   178: aastore
    //   179: aconst_null
    //   180: aconst_null
    //   181: aconst_null
    //   182: invokevirtual 396	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   185: astore 6
    //   187: aload 6
    //   189: astore 5
    //   191: aload 6
    //   193: invokeinterface 194 1 0
    //   198: istore_2
    //   199: iload_2
    //   200: ifne +19 -> 219
    //   203: aload 6
    //   205: ifnull +10 -> 215
    //   208: aload 6
    //   210: invokeinterface 201 1 0
    //   215: aconst_null
    //   216: astore_1
    //   217: aload_1
    //   218: areturn
    //   219: aload 6
    //   221: astore 5
    //   223: new 689	com/google/android/gms/internal/zzcgh
    //   226: dup
    //   227: aload_0
    //   228: getfield 1370	com/google/android/gms/internal/zzcgo:zziwf	Lcom/google/android/gms/internal/zzcim;
    //   231: aload_1
    //   232: invokespecial 1373	com/google/android/gms/internal/zzcgh:<init>	(Lcom/google/android/gms/internal/zzcim;Ljava/lang/String;)V
    //   235: astore 7
    //   237: aload 6
    //   239: astore 5
    //   241: aload 7
    //   243: aload 6
    //   245: iconst_0
    //   246: invokeinterface 261 2 0
    //   251: invokevirtual 1376	com/google/android/gms/internal/zzcgh:zzir	(Ljava/lang/String;)V
    //   254: aload 6
    //   256: astore 5
    //   258: aload 7
    //   260: aload 6
    //   262: iconst_1
    //   263: invokeinterface 261 2 0
    //   268: invokevirtual 1379	com/google/android/gms/internal/zzcgh:zzis	(Ljava/lang/String;)V
    //   271: aload 6
    //   273: astore 5
    //   275: aload 7
    //   277: aload 6
    //   279: iconst_2
    //   280: invokeinterface 261 2 0
    //   285: invokevirtual 1382	com/google/android/gms/internal/zzcgh:zzit	(Ljava/lang/String;)V
    //   288: aload 6
    //   290: astore 5
    //   292: aload 7
    //   294: aload 6
    //   296: iconst_3
    //   297: invokeinterface 198 2 0
    //   302: invokevirtual 1385	com/google/android/gms/internal/zzcgh:zzaq	(J)V
    //   305: aload 6
    //   307: astore 5
    //   309: aload 7
    //   311: aload 6
    //   313: iconst_4
    //   314: invokeinterface 198 2 0
    //   319: invokevirtual 1387	com/google/android/gms/internal/zzcgh:zzal	(J)V
    //   322: aload 6
    //   324: astore 5
    //   326: aload 7
    //   328: aload 6
    //   330: iconst_5
    //   331: invokeinterface 198 2 0
    //   336: invokevirtual 1390	com/google/android/gms/internal/zzcgh:zzam	(J)V
    //   339: aload 6
    //   341: astore 5
    //   343: aload 7
    //   345: aload 6
    //   347: bipush 6
    //   349: invokeinterface 261 2 0
    //   354: invokevirtual 1393	com/google/android/gms/internal/zzcgh:setAppVersion	(Ljava/lang/String;)V
    //   357: aload 6
    //   359: astore 5
    //   361: aload 7
    //   363: aload 6
    //   365: bipush 7
    //   367: invokeinterface 261 2 0
    //   372: invokevirtual 1396	com/google/android/gms/internal/zzcgh:zziv	(Ljava/lang/String;)V
    //   375: aload 6
    //   377: astore 5
    //   379: aload 7
    //   381: aload 6
    //   383: bipush 8
    //   385: invokeinterface 198 2 0
    //   390: invokevirtual 1399	com/google/android/gms/internal/zzcgh:zzao	(J)V
    //   393: aload 6
    //   395: astore 5
    //   397: aload 7
    //   399: aload 6
    //   401: bipush 9
    //   403: invokeinterface 198 2 0
    //   408: invokevirtual 1402	com/google/android/gms/internal/zzcgh:zzap	(J)V
    //   411: aload 6
    //   413: astore 5
    //   415: aload 6
    //   417: bipush 10
    //   419: invokeinterface 1108 2 0
    //   424: ifne +491 -> 915
    //   427: aload 6
    //   429: astore 5
    //   431: aload 6
    //   433: bipush 10
    //   435: invokeinterface 1140 2 0
    //   440: ifeq +361 -> 801
    //   443: goto +472 -> 915
    //   446: aload 6
    //   448: astore 5
    //   450: aload 7
    //   452: iload_2
    //   453: invokevirtual 1406	com/google/android/gms/internal/zzcgh:setMeasurementEnabled	(Z)V
    //   456: aload 6
    //   458: astore 5
    //   460: aload 7
    //   462: aload 6
    //   464: bipush 11
    //   466: invokeinterface 198 2 0
    //   471: invokevirtual 1409	com/google/android/gms/internal/zzcgh:zzat	(J)V
    //   474: aload 6
    //   476: astore 5
    //   478: aload 7
    //   480: aload 6
    //   482: bipush 12
    //   484: invokeinterface 198 2 0
    //   489: invokevirtual 1412	com/google/android/gms/internal/zzcgh:zzau	(J)V
    //   492: aload 6
    //   494: astore 5
    //   496: aload 7
    //   498: aload 6
    //   500: bipush 13
    //   502: invokeinterface 198 2 0
    //   507: invokevirtual 1415	com/google/android/gms/internal/zzcgh:zzav	(J)V
    //   510: aload 6
    //   512: astore 5
    //   514: aload 7
    //   516: aload 6
    //   518: bipush 14
    //   520: invokeinterface 198 2 0
    //   525: invokevirtual 1418	com/google/android/gms/internal/zzcgh:zzaw	(J)V
    //   528: aload 6
    //   530: astore 5
    //   532: aload 7
    //   534: aload 6
    //   536: bipush 15
    //   538: invokeinterface 198 2 0
    //   543: invokevirtual 1421	com/google/android/gms/internal/zzcgh:zzar	(J)V
    //   546: aload 6
    //   548: astore 5
    //   550: aload 7
    //   552: aload 6
    //   554: bipush 16
    //   556: invokeinterface 198 2 0
    //   561: invokevirtual 1424	com/google/android/gms/internal/zzcgh:zzas	(J)V
    //   564: aload 6
    //   566: astore 5
    //   568: aload 6
    //   570: bipush 17
    //   572: invokeinterface 1108 2 0
    //   577: ifeq +229 -> 806
    //   580: ldc2_w 1425
    //   583: lstore_3
    //   584: aload 6
    //   586: astore 5
    //   588: aload 7
    //   590: lload_3
    //   591: invokevirtual 1429	com/google/android/gms/internal/zzcgh:zzan	(J)V
    //   594: aload 6
    //   596: astore 5
    //   598: aload 7
    //   600: aload 6
    //   602: bipush 18
    //   604: invokeinterface 261 2 0
    //   609: invokevirtual 1432	com/google/android/gms/internal/zzcgh:zziu	(Ljava/lang/String;)V
    //   612: aload 6
    //   614: astore 5
    //   616: aload 7
    //   618: aload 6
    //   620: bipush 19
    //   622: invokeinterface 198 2 0
    //   627: invokevirtual 1435	com/google/android/gms/internal/zzcgh:zzay	(J)V
    //   630: aload 6
    //   632: astore 5
    //   634: aload 7
    //   636: aload 6
    //   638: bipush 20
    //   640: invokeinterface 198 2 0
    //   645: invokevirtual 1438	com/google/android/gms/internal/zzcgh:zzax	(J)V
    //   648: aload 6
    //   650: astore 5
    //   652: aload 7
    //   654: aload 6
    //   656: bipush 21
    //   658: invokeinterface 261 2 0
    //   663: invokevirtual 1441	com/google/android/gms/internal/zzcgh:zziw	(Ljava/lang/String;)V
    //   666: aload 6
    //   668: astore 5
    //   670: aload 6
    //   672: bipush 22
    //   674: invokeinterface 1108 2 0
    //   679: ifeq +145 -> 824
    //   682: lconst_0
    //   683: lstore_3
    //   684: aload 6
    //   686: astore 5
    //   688: aload 7
    //   690: lload_3
    //   691: invokevirtual 1444	com/google/android/gms/internal/zzcgh:zzaz	(J)V
    //   694: aload 6
    //   696: astore 5
    //   698: aload 6
    //   700: bipush 23
    //   702: invokeinterface 1108 2 0
    //   707: ifne +213 -> 920
    //   710: aload 6
    //   712: astore 5
    //   714: aload 6
    //   716: bipush 23
    //   718: invokeinterface 1140 2 0
    //   723: ifeq +118 -> 841
    //   726: goto +194 -> 920
    //   729: aload 6
    //   731: astore 5
    //   733: aload 7
    //   735: iload_2
    //   736: invokevirtual 1447	com/google/android/gms/internal/zzcgh:zzbl	(Z)V
    //   739: aload 6
    //   741: astore 5
    //   743: aload 7
    //   745: invokevirtual 1450	com/google/android/gms/internal/zzcgh:zzaxb	()V
    //   748: aload 6
    //   750: astore 5
    //   752: aload 6
    //   754: invokeinterface 1114 1 0
    //   759: ifeq +24 -> 783
    //   762: aload 6
    //   764: astore 5
    //   766: aload_0
    //   767: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   770: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   773: ldc_w 1452
    //   776: aload_1
    //   777: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   780: invokevirtual 237	com/google/android/gms/internal/zzcho:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   783: aload 7
    //   785: astore_1
    //   786: aload 6
    //   788: ifnull -571 -> 217
    //   791: aload 6
    //   793: invokeinterface 201 1 0
    //   798: aload 7
    //   800: areturn
    //   801: iconst_0
    //   802: istore_2
    //   803: goto -357 -> 446
    //   806: aload 6
    //   808: astore 5
    //   810: aload 6
    //   812: bipush 17
    //   814: invokeinterface 1140 2 0
    //   819: i2l
    //   820: lstore_3
    //   821: goto -237 -> 584
    //   824: aload 6
    //   826: astore 5
    //   828: aload 6
    //   830: bipush 22
    //   832: invokeinterface 198 2 0
    //   837: lstore_3
    //   838: goto -154 -> 684
    //   841: iconst_0
    //   842: istore_2
    //   843: goto -114 -> 729
    //   846: astore 7
    //   848: aconst_null
    //   849: astore 6
    //   851: aload 6
    //   853: astore 5
    //   855: aload_0
    //   856: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   859: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   862: ldc_w 1454
    //   865: aload_1
    //   866: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   869: aload 7
    //   871: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   874: aload 6
    //   876: ifnull +10 -> 886
    //   879: aload 6
    //   881: invokeinterface 201 1 0
    //   886: aconst_null
    //   887: areturn
    //   888: astore_1
    //   889: aconst_null
    //   890: astore 5
    //   892: aload 5
    //   894: ifnull +10 -> 904
    //   897: aload 5
    //   899: invokeinterface 201 1 0
    //   904: aload_1
    //   905: athrow
    //   906: astore_1
    //   907: goto -15 -> 892
    //   910: astore 7
    //   912: goto -61 -> 851
    //   915: iconst_1
    //   916: istore_2
    //   917: goto -471 -> 446
    //   920: iconst_1
    //   921: istore_2
    //   922: goto -193 -> 729
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	925	0	this	zzcgo
    //   0	925	1	paramString	String
    //   198	724	2	bool	boolean
    //   583	255	3	l	long
    //   189	709	5	localCursor1	Cursor
    //   185	695	6	localCursor2	Cursor
    //   235	564	7	localzzcgh	zzcgh
    //   846	24	7	localSQLiteException1	SQLiteException
    //   910	1	7	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   13	187	846	android/database/sqlite/SQLiteException
    //   13	187	888	finally
    //   191	199	906	finally
    //   223	237	906	finally
    //   241	254	906	finally
    //   258	271	906	finally
    //   275	288	906	finally
    //   292	305	906	finally
    //   309	322	906	finally
    //   326	339	906	finally
    //   343	357	906	finally
    //   361	375	906	finally
    //   379	393	906	finally
    //   397	411	906	finally
    //   415	427	906	finally
    //   431	443	906	finally
    //   450	456	906	finally
    //   460	474	906	finally
    //   478	492	906	finally
    //   496	510	906	finally
    //   514	528	906	finally
    //   532	546	906	finally
    //   550	564	906	finally
    //   568	580	906	finally
    //   588	594	906	finally
    //   598	612	906	finally
    //   616	630	906	finally
    //   634	648	906	finally
    //   652	666	906	finally
    //   670	682	906	finally
    //   688	694	906	finally
    //   698	710	906	finally
    //   714	726	906	finally
    //   733	739	906	finally
    //   743	748	906	finally
    //   752	762	906	finally
    //   766	783	906	finally
    //   810	821	906	finally
    //   828	838	906	finally
    //   855	874	906	finally
    //   191	199	910	android/database/sqlite/SQLiteException
    //   223	237	910	android/database/sqlite/SQLiteException
    //   241	254	910	android/database/sqlite/SQLiteException
    //   258	271	910	android/database/sqlite/SQLiteException
    //   275	288	910	android/database/sqlite/SQLiteException
    //   292	305	910	android/database/sqlite/SQLiteException
    //   309	322	910	android/database/sqlite/SQLiteException
    //   326	339	910	android/database/sqlite/SQLiteException
    //   343	357	910	android/database/sqlite/SQLiteException
    //   361	375	910	android/database/sqlite/SQLiteException
    //   379	393	910	android/database/sqlite/SQLiteException
    //   397	411	910	android/database/sqlite/SQLiteException
    //   415	427	910	android/database/sqlite/SQLiteException
    //   431	443	910	android/database/sqlite/SQLiteException
    //   450	456	910	android/database/sqlite/SQLiteException
    //   460	474	910	android/database/sqlite/SQLiteException
    //   478	492	910	android/database/sqlite/SQLiteException
    //   496	510	910	android/database/sqlite/SQLiteException
    //   514	528	910	android/database/sqlite/SQLiteException
    //   532	546	910	android/database/sqlite/SQLiteException
    //   550	564	910	android/database/sqlite/SQLiteException
    //   568	580	910	android/database/sqlite/SQLiteException
    //   588	594	910	android/database/sqlite/SQLiteException
    //   598	612	910	android/database/sqlite/SQLiteException
    //   616	630	910	android/database/sqlite/SQLiteException
    //   634	648	910	android/database/sqlite/SQLiteException
    //   652	666	910	android/database/sqlite/SQLiteException
    //   670	682	910	android/database/sqlite/SQLiteException
    //   688	694	910	android/database/sqlite/SQLiteException
    //   698	710	910	android/database/sqlite/SQLiteException
    //   714	726	910	android/database/sqlite/SQLiteException
    //   733	739	910	android/database/sqlite/SQLiteException
    //   743	748	910	android/database/sqlite/SQLiteException
    //   752	762	910	android/database/sqlite/SQLiteException
    //   766	783	910	android/database/sqlite/SQLiteException
    //   810	821	910	android/database/sqlite/SQLiteException
    //   828	838	910	android/database/sqlite/SQLiteException
  }
  
  public final long zzjc(String paramString)
  {
    zzbq.zzgm(paramString);
    zzve();
    zzxf();
    try
    {
      int i = getWritableDatabase().delete("raw_events", "rowid in (select rowid from raw_events where app_id=? order by rowid desc limit -1 offset ?)", new String[] { paramString, String.valueOf(Math.max(0, Math.min(1000000, zzaxa().zzb(paramString, zzchc.zzjas)))) });
      return i;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzawy().zzazd().zze("Error deleting over the limit events. appId", zzchm.zzjk(paramString), localSQLiteException);
    }
    return 0L;
  }
  
  /* Error */
  public final byte[] zzjd(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 407	com/google/android/gms/internal/zzcjk:zzve	()V
    //   9: aload_0
    //   10: invokevirtual 404	com/google/android/gms/internal/zzcjl:zzxf	()V
    //   13: aload_0
    //   14: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: ldc_w 660
    //   20: iconst_1
    //   21: anewarray 19	java/lang/String
    //   24: dup
    //   25: iconst_0
    //   26: ldc 85
    //   28: aastore
    //   29: ldc_w 662
    //   32: iconst_1
    //   33: anewarray 19	java/lang/String
    //   36: dup
    //   37: iconst_0
    //   38: aload_1
    //   39: aastore
    //   40: aconst_null
    //   41: aconst_null
    //   42: aconst_null
    //   43: invokevirtual 396	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   46: astore 4
    //   48: aload 4
    //   50: astore_3
    //   51: aload 4
    //   53: invokeinterface 194 1 0
    //   58: istore_2
    //   59: iload_2
    //   60: ifne +19 -> 79
    //   63: aload 4
    //   65: ifnull +10 -> 75
    //   68: aload 4
    //   70: invokeinterface 201 1 0
    //   75: aconst_null
    //   76: astore_1
    //   77: aload_1
    //   78: areturn
    //   79: aload 4
    //   81: astore_3
    //   82: aload 4
    //   84: iconst_0
    //   85: invokeinterface 1144 2 0
    //   90: astore 5
    //   92: aload 4
    //   94: astore_3
    //   95: aload 4
    //   97: invokeinterface 1114 1 0
    //   102: ifeq +23 -> 125
    //   105: aload 4
    //   107: astore_3
    //   108: aload_0
    //   109: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   112: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   115: ldc_w 1468
    //   118: aload_1
    //   119: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   122: invokevirtual 237	com/google/android/gms/internal/zzcho:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   125: aload 5
    //   127: astore_1
    //   128: aload 4
    //   130: ifnull -53 -> 77
    //   133: aload 4
    //   135: invokeinterface 201 1 0
    //   140: aload 5
    //   142: areturn
    //   143: astore 5
    //   145: aconst_null
    //   146: astore 4
    //   148: aload 4
    //   150: astore_3
    //   151: aload_0
    //   152: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   155: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   158: ldc_w 1470
    //   161: aload_1
    //   162: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   165: aload 5
    //   167: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   170: aload 4
    //   172: ifnull +10 -> 182
    //   175: aload 4
    //   177: invokeinterface 201 1 0
    //   182: aconst_null
    //   183: areturn
    //   184: astore_1
    //   185: aconst_null
    //   186: astore_3
    //   187: aload_3
    //   188: ifnull +9 -> 197
    //   191: aload_3
    //   192: invokeinterface 201 1 0
    //   197: aload_1
    //   198: athrow
    //   199: astore_1
    //   200: goto -13 -> 187
    //   203: astore 5
    //   205: goto -57 -> 148
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	208	0	this	zzcgo
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
  final java.util.Map<Integer, zzcmf> zzje(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 404	com/google/android/gms/internal/zzcjl:zzxf	()V
    //   4: aload_0
    //   5: invokevirtual 407	com/google/android/gms/internal/zzcjk:zzve	()V
    //   8: aload_1
    //   9: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: astore 4
    //   19: aload 4
    //   21: ldc_w 593
    //   24: iconst_2
    //   25: anewarray 19	java/lang/String
    //   28: dup
    //   29: iconst_0
    //   30: ldc_w 453
    //   33: aastore
    //   34: dup
    //   35: iconst_1
    //   36: ldc_w 1474
    //   39: aastore
    //   40: ldc_w 662
    //   43: iconst_1
    //   44: anewarray 19	java/lang/String
    //   47: dup
    //   48: iconst_0
    //   49: aload_1
    //   50: aastore
    //   51: aconst_null
    //   52: aconst_null
    //   53: aconst_null
    //   54: invokevirtual 396	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   57: astore 5
    //   59: aload 5
    //   61: astore 4
    //   63: aload 5
    //   65: invokeinterface 194 1 0
    //   70: istore_3
    //   71: iload_3
    //   72: ifne +19 -> 91
    //   75: aload 5
    //   77: ifnull +10 -> 87
    //   80: aload 5
    //   82: invokeinterface 201 1 0
    //   87: aconst_null
    //   88: astore_1
    //   89: aload_1
    //   90: areturn
    //   91: aload 5
    //   93: astore 4
    //   95: new 1181	android/support/v4/util/ArrayMap
    //   98: dup
    //   99: invokespecial 1182	android/support/v4/util/ArrayMap:<init>	()V
    //   102: astore 6
    //   104: aload 5
    //   106: astore 4
    //   108: aload 5
    //   110: iconst_0
    //   111: invokeinterface 1140 2 0
    //   116: istore_2
    //   117: aload 5
    //   119: astore 4
    //   121: aload 5
    //   123: iconst_1
    //   124: invokeinterface 1144 2 0
    //   129: astore 7
    //   131: aload 5
    //   133: astore 4
    //   135: aload 7
    //   137: iconst_0
    //   138: aload 7
    //   140: arraylength
    //   141: invokestatic 1194	com/google/android/gms/internal/zzfjj:zzn	([BII)Lcom/google/android/gms/internal/zzfjj;
    //   144: astore 7
    //   146: aload 5
    //   148: astore 4
    //   150: new 1476	com/google/android/gms/internal/zzcmf
    //   153: dup
    //   154: invokespecial 1477	com/google/android/gms/internal/zzcmf:<init>	()V
    //   157: astore 8
    //   159: aload 5
    //   161: astore 4
    //   163: aload 8
    //   165: aload 7
    //   167: invokevirtual 1198	com/google/android/gms/internal/zzfjs:zza	(Lcom/google/android/gms/internal/zzfjj;)Lcom/google/android/gms/internal/zzfjs;
    //   170: pop
    //   171: aload 5
    //   173: astore 4
    //   175: aload 6
    //   177: iload_2
    //   178: invokestatic 233	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   181: aload 8
    //   183: invokeinterface 1205 3 0
    //   188: pop
    //   189: aload 5
    //   191: astore 4
    //   193: aload 5
    //   195: invokeinterface 1114 1 0
    //   200: istore_3
    //   201: iload_3
    //   202: ifne -98 -> 104
    //   205: aload 6
    //   207: astore_1
    //   208: aload 5
    //   210: ifnull -121 -> 89
    //   213: aload 5
    //   215: invokeinterface 201 1 0
    //   220: aload 6
    //   222: areturn
    //   223: astore 7
    //   225: aload 5
    //   227: astore 4
    //   229: aload_0
    //   230: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   233: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   236: ldc_w 1479
    //   239: aload_1
    //   240: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   243: iload_2
    //   244: invokestatic 233	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   247: aload 7
    //   249: invokevirtual 430	com/google/android/gms/internal/zzcho:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   252: goto -63 -> 189
    //   255: astore 6
    //   257: aload 5
    //   259: astore 4
    //   261: aload_0
    //   262: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   265: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   268: ldc_w 1481
    //   271: aload_1
    //   272: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   275: aload 6
    //   277: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   280: aload 5
    //   282: ifnull +10 -> 292
    //   285: aload 5
    //   287: invokeinterface 201 1 0
    //   292: aconst_null
    //   293: areturn
    //   294: astore_1
    //   295: aconst_null
    //   296: astore 4
    //   298: aload 4
    //   300: ifnull +10 -> 310
    //   303: aload 4
    //   305: invokeinterface 201 1 0
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
    //   0	324	0	this	zzcgo
    //   0	324	1	paramString	String
    //   116	128	2	i	int
    //   70	132	3	bool	boolean
    //   17	287	4	localObject1	Object
    //   57	263	5	localCursor	Cursor
    //   102	119	6	localArrayMap	android.support.v4.util.ArrayMap
    //   255	21	6	localSQLiteException1	SQLiteException
    //   316	1	6	localSQLiteException2	SQLiteException
    //   129	37	7	localObject2	Object
    //   223	25	7	localIOException	IOException
    //   157	25	8	localzzcmf	zzcmf
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
  
  public final long zzjf(String paramString)
  {
    zzbq.zzgm(paramString);
    return zza("select count(1) from events where app_id=? and name not like '!_%' escape '!'", new String[] { paramString }, 0L);
  }
  
  /* Error */
  public final List<android.util.Pair<zzcme, Long>> zzl(String paramString, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore 5
    //   3: aload_0
    //   4: invokevirtual 407	com/google/android/gms/internal/zzcjk:zzve	()V
    //   7: aload_0
    //   8: invokevirtual 404	com/google/android/gms/internal/zzcjl:zzxf	()V
    //   11: iload_2
    //   12: ifle +112 -> 124
    //   15: iconst_1
    //   16: istore 4
    //   18: iload 4
    //   20: invokestatic 1490	com/google/android/gms/common/internal/zzbq:checkArgument	(Z)V
    //   23: iload_3
    //   24: ifle +106 -> 130
    //   27: iload 5
    //   29: istore 4
    //   31: iload 4
    //   33: invokestatic 1490	com/google/android/gms/common/internal/zzbq:checkArgument	(Z)V
    //   36: aload_1
    //   37: invokestatic 270	com/google/android/gms/common/internal/zzbq:zzgm	(Ljava/lang/String;)Ljava/lang/String;
    //   40: pop
    //   41: aload_0
    //   42: invokevirtual 182	com/google/android/gms/internal/zzcgo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   45: ldc_w 1094
    //   48: iconst_2
    //   49: anewarray 19	java/lang/String
    //   52: dup
    //   53: iconst_0
    //   54: ldc_w 1311
    //   57: aastore
    //   58: dup
    //   59: iconst_1
    //   60: ldc_w 462
    //   63: aastore
    //   64: ldc_w 662
    //   67: iconst_1
    //   68: anewarray 19	java/lang/String
    //   71: dup
    //   72: iconst_0
    //   73: aload_1
    //   74: aastore
    //   75: aconst_null
    //   76: aconst_null
    //   77: ldc_w 1311
    //   80: iload_2
    //   81: invokestatic 864	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   84: invokevirtual 1316	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   87: astore 8
    //   89: aload 8
    //   91: invokeinterface 194 1 0
    //   96: ifne +40 -> 136
    //   99: invokestatic 1324	java/util/Collections:emptyList	()Ljava/util/List;
    //   102: astore 9
    //   104: aload 9
    //   106: astore_1
    //   107: aload 8
    //   109: ifnull +13 -> 122
    //   112: aload 8
    //   114: invokeinterface 201 1 0
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
    //   136: new 569	java/util/ArrayList
    //   139: dup
    //   140: invokespecial 570	java/util/ArrayList:<init>	()V
    //   143: astore 9
    //   145: iconst_0
    //   146: istore_2
    //   147: aload 8
    //   149: iconst_0
    //   150: invokeinterface 198 2 0
    //   155: lstore 6
    //   157: aload 8
    //   159: iconst_1
    //   160: invokeinterface 1144 2 0
    //   165: astore 10
    //   167: aload_0
    //   168: invokevirtual 624	com/google/android/gms/internal/zzcjk:zzawu	()Lcom/google/android/gms/internal/zzclq;
    //   171: aload 10
    //   173: invokevirtual 1493	com/google/android/gms/internal/zzclq:zzr	([B)[B
    //   176: astore 10
    //   178: aload 9
    //   180: invokeinterface 1494 1 0
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
    //   203: invokestatic 1194	com/google/android/gms/internal/zzfjj:zzn	([BII)Lcom/google/android/gms/internal/zzfjj;
    //   206: astore 11
    //   208: new 617	com/google/android/gms/internal/zzcme
    //   211: dup
    //   212: invokespecial 1495	com/google/android/gms/internal/zzcme:<init>	()V
    //   215: astore 12
    //   217: aload 12
    //   219: aload 11
    //   221: invokevirtual 1198	com/google/android/gms/internal/zzfjs:zza	(Lcom/google/android/gms/internal/zzfjj;)Lcom/google/android/gms/internal/zzfjs;
    //   224: pop
    //   225: aload 10
    //   227: arraylength
    //   228: iload_2
    //   229: iadd
    //   230: istore_2
    //   231: aload 9
    //   233: aload 12
    //   235: lload 6
    //   237: invokestatic 248	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   240: invokestatic 1501	android/util/Pair:create	(Ljava/lang/Object;Ljava/lang/Object;)Landroid/util/Pair;
    //   243: invokeinterface 587 2 0
    //   248: pop
    //   249: aload 8
    //   251: invokeinterface 1114 1 0
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
    //   278: invokeinterface 201 1 0
    //   283: aload 9
    //   285: areturn
    //   286: astore 10
    //   288: aload_0
    //   289: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   292: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   295: ldc_w 1503
    //   298: aload_1
    //   299: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   302: aload 10
    //   304: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   307: goto -58 -> 249
    //   310: astore 10
    //   312: aload_0
    //   313: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   316: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   319: ldc_w 1505
    //   322: aload_1
    //   323: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   326: aload 10
    //   328: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   331: goto -82 -> 249
    //   334: astore 9
    //   336: aconst_null
    //   337: astore 8
    //   339: aload_0
    //   340: invokevirtual 205	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   343: invokevirtual 211	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   346: ldc_w 1507
    //   349: aload_1
    //   350: invokestatic 422	com/google/android/gms/internal/zzchm:zzjk	(Ljava/lang/String;)Ljava/lang/Object;
    //   353: aload 9
    //   355: invokevirtual 219	com/google/android/gms/internal/zzcho:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   358: invokestatic 1324	java/util/Collections:emptyList	()Ljava/util/List;
    //   361: astore 9
    //   363: aload 9
    //   365: astore_1
    //   366: aload 8
    //   368: ifnull -246 -> 122
    //   371: aload 8
    //   373: invokeinterface 201 1 0
    //   378: aload 9
    //   380: areturn
    //   381: astore_1
    //   382: aconst_null
    //   383: astore 8
    //   385: aload 8
    //   387: ifnull +10 -> 397
    //   390: aload 8
    //   392: invokeinterface 201 1 0
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
    //   0	415	0	this	zzcgo
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
    //   206	14	11	localzzfjj	zzfjj
    //   215	19	12	localzzcme	zzcme
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcgo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */