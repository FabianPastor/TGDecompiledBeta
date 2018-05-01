package com.google.android.gms.internal.measurement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.text.TextUtils;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.Clock;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class zzei
  extends zzhk
{
  private static final String[] zzaeu = { "last_bundled_timestamp", "ALTER TABLE events ADD COLUMN last_bundled_timestamp INTEGER;", "last_sampled_complex_event_id", "ALTER TABLE events ADD COLUMN last_sampled_complex_event_id INTEGER;", "last_sampling_rate", "ALTER TABLE events ADD COLUMN last_sampling_rate INTEGER;", "last_exempt_from_sampling", "ALTER TABLE events ADD COLUMN last_exempt_from_sampling INTEGER;" };
  private static final String[] zzaev = { "origin", "ALTER TABLE user_attributes ADD COLUMN origin TEXT;" };
  private static final String[] zzaew = { "app_version", "ALTER TABLE apps ADD COLUMN app_version TEXT;", "app_store", "ALTER TABLE apps ADD COLUMN app_store TEXT;", "gmp_version", "ALTER TABLE apps ADD COLUMN gmp_version INTEGER;", "dev_cert_hash", "ALTER TABLE apps ADD COLUMN dev_cert_hash INTEGER;", "measurement_enabled", "ALTER TABLE apps ADD COLUMN measurement_enabled INTEGER;", "last_bundle_start_timestamp", "ALTER TABLE apps ADD COLUMN last_bundle_start_timestamp INTEGER;", "day", "ALTER TABLE apps ADD COLUMN day INTEGER;", "daily_public_events_count", "ALTER TABLE apps ADD COLUMN daily_public_events_count INTEGER;", "daily_events_count", "ALTER TABLE apps ADD COLUMN daily_events_count INTEGER;", "daily_conversions_count", "ALTER TABLE apps ADD COLUMN daily_conversions_count INTEGER;", "remote_config", "ALTER TABLE apps ADD COLUMN remote_config BLOB;", "config_fetched_time", "ALTER TABLE apps ADD COLUMN config_fetched_time INTEGER;", "failed_config_fetch_time", "ALTER TABLE apps ADD COLUMN failed_config_fetch_time INTEGER;", "app_version_int", "ALTER TABLE apps ADD COLUMN app_version_int INTEGER;", "firebase_instance_id", "ALTER TABLE apps ADD COLUMN firebase_instance_id TEXT;", "daily_error_events_count", "ALTER TABLE apps ADD COLUMN daily_error_events_count INTEGER;", "daily_realtime_events_count", "ALTER TABLE apps ADD COLUMN daily_realtime_events_count INTEGER;", "health_monitor_sample", "ALTER TABLE apps ADD COLUMN health_monitor_sample TEXT;", "android_id", "ALTER TABLE apps ADD COLUMN android_id INTEGER;", "adid_reporting_enabled", "ALTER TABLE apps ADD COLUMN adid_reporting_enabled INTEGER;", "ssaid_reporting_enabled", "ALTER TABLE apps ADD COLUMN ssaid_reporting_enabled INTEGER;" };
  private static final String[] zzaex = { "realtime", "ALTER TABLE raw_events ADD COLUMN realtime INTEGER;" };
  private static final String[] zzaey = { "has_realtime", "ALTER TABLE queue ADD COLUMN has_realtime INTEGER;", "retry_count", "ALTER TABLE queue ADD COLUMN retry_count INTEGER;" };
  private static final String[] zzaez = { "previous_install_count", "ALTER TABLE app2 ADD COLUMN previous_install_count INTEGER;" };
  private final zzel zzafa = new zzel(this, getContext(), "google_app_measurement.db");
  private final zzjp zzafb = new zzjp(zzbt());
  
  zzei(zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  private final long zza(String paramString, String[] paramArrayOfString)
  {
    Object localObject1 = getWritableDatabase();
    Object localObject2 = null;
    String[] arrayOfString = null;
    try
    {
      paramArrayOfString = ((SQLiteDatabase)localObject1).rawQuery(paramString, paramArrayOfString);
      arrayOfString = paramArrayOfString;
      localObject2 = paramArrayOfString;
      if (paramArrayOfString.moveToFirst())
      {
        arrayOfString = paramArrayOfString;
        localObject2 = paramArrayOfString;
        long l = paramArrayOfString.getLong(0);
        if (paramArrayOfString != null) {
          paramArrayOfString.close();
        }
        return l;
      }
      arrayOfString = paramArrayOfString;
      localObject2 = paramArrayOfString;
      localObject1 = new android/database/sqlite/SQLiteException;
      arrayOfString = paramArrayOfString;
      localObject2 = paramArrayOfString;
      ((SQLiteException)localObject1).<init>("Database returned empty set");
      arrayOfString = paramArrayOfString;
      localObject2 = paramArrayOfString;
      throw ((Throwable)localObject1);
    }
    catch (SQLiteException paramArrayOfString)
    {
      localObject2 = arrayOfString;
      zzgg().zzil().zze("Database error", paramString, paramArrayOfString);
      localObject2 = arrayOfString;
      throw paramArrayOfString;
    }
    finally
    {
      if (localObject2 != null) {
        ((Cursor)localObject2).close();
      }
    }
  }
  
  /* Error */
  private final long zza(String paramString, String[] paramArrayOfString, long paramLong)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 190	com/google/android/gms/internal/measurement/zzei:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   4: astore 5
    //   6: aconst_null
    //   7: astore 6
    //   9: aconst_null
    //   10: astore 7
    //   12: aload 5
    //   14: aload_1
    //   15: aload_2
    //   16: invokevirtual 196	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   19: astore_2
    //   20: aload_2
    //   21: astore 7
    //   23: aload_2
    //   24: astore 6
    //   26: aload_2
    //   27: invokeinterface 202 1 0
    //   32: ifeq +36 -> 68
    //   35: aload_2
    //   36: astore 7
    //   38: aload_2
    //   39: astore 6
    //   41: aload_2
    //   42: iconst_0
    //   43: invokeinterface 206 2 0
    //   48: lstore_3
    //   49: lload_3
    //   50: lstore 8
    //   52: aload_2
    //   53: ifnull +12 -> 65
    //   56: aload_2
    //   57: invokeinterface 209 1 0
    //   62: lload_3
    //   63: lstore 8
    //   65: lload 8
    //   67: lreturn
    //   68: lload_3
    //   69: lstore 8
    //   71: aload_2
    //   72: ifnull -7 -> 65
    //   75: aload_2
    //   76: invokeinterface 209 1 0
    //   81: lload_3
    //   82: lstore 8
    //   84: goto -19 -> 65
    //   87: astore_2
    //   88: aload 7
    //   90: astore 6
    //   92: aload_0
    //   93: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   96: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   99: ldc -30
    //   101: aload_1
    //   102: aload_2
    //   103: invokevirtual 232	com/google/android/gms/internal/measurement/zzfi:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   106: aload 7
    //   108: astore 6
    //   110: aload_2
    //   111: athrow
    //   112: astore_1
    //   113: aload 6
    //   115: ifnull +10 -> 125
    //   118: aload 6
    //   120: invokeinterface 209 1 0
    //   125: aload_1
    //   126: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	127	0	this	zzei
    //   0	127	1	paramString	String
    //   0	127	2	paramArrayOfString	String[]
    //   0	127	3	paramLong	long
    //   4	9	5	localSQLiteDatabase	SQLiteDatabase
    //   7	112	6	localObject	Object
    //   10	97	7	arrayOfString	String[]
    //   50	33	8	l	long
    // Exception table:
    //   from	to	target	type
    //   12	20	87	android/database/sqlite/SQLiteException
    //   26	35	87	android/database/sqlite/SQLiteException
    //   41	49	87	android/database/sqlite/SQLiteException
    //   12	20	112	finally
    //   26	35	112	finally
    //   41	49	112	finally
    //   92	106	112	finally
    //   110	112	112	finally
  }
  
  private final Object zza(Cursor paramCursor, int paramInt)
  {
    Object localObject = null;
    int i = paramCursor.getType(paramInt);
    switch (i)
    {
    default: 
      zzgg().zzil().zzg("Loaded invalid unknown value type, ignoring it", Integer.valueOf(i));
      paramCursor = (Cursor)localObject;
    }
    for (;;)
    {
      return paramCursor;
      zzgg().zzil().log("Loaded invalid null value from database");
      paramCursor = (Cursor)localObject;
      continue;
      paramCursor = Long.valueOf(paramCursor.getLong(paramInt));
      continue;
      paramCursor = Double.valueOf(paramCursor.getDouble(paramInt));
      continue;
      paramCursor = paramCursor.getString(paramInt);
      continue;
      zzgg().zzil().log("Loaded invalid blob type value, ignoring it");
      paramCursor = (Cursor)localObject;
    }
  }
  
  private static void zza(ContentValues paramContentValues, String paramString, Object paramObject)
  {
    Preconditions.checkNotEmpty(paramString);
    Preconditions.checkNotNull(paramObject);
    if ((paramObject instanceof String)) {
      paramContentValues.put(paramString, (String)paramObject);
    }
    for (;;)
    {
      return;
      if ((paramObject instanceof Long))
      {
        paramContentValues.put(paramString, (Long)paramObject);
      }
      else
      {
        if (!(paramObject instanceof Double)) {
          break;
        }
        paramContentValues.put(paramString, (Double)paramObject);
      }
    }
    throw new IllegalArgumentException("Invalid value type");
  }
  
  static void zza(zzfg paramzzfg, SQLiteDatabase paramSQLiteDatabase)
  {
    if (paramzzfg == null) {
      throw new IllegalArgumentException("Monitor must not be null");
    }
    paramSQLiteDatabase = new File(paramSQLiteDatabase.getPath());
    if (!paramSQLiteDatabase.setReadable(false, false)) {
      paramzzfg.zzin().log("Failed to turn off database read permission");
    }
    if (!paramSQLiteDatabase.setWritable(false, false)) {
      paramzzfg.zzin().log("Failed to turn off database write permission");
    }
    if (!paramSQLiteDatabase.setReadable(true, true)) {
      paramzzfg.zzin().log("Failed to turn on database read permission for owner");
    }
    if (!paramSQLiteDatabase.setWritable(true, true)) {
      paramzzfg.zzin().log("Failed to turn on database write permission for owner");
    }
  }
  
  static void zza(zzfg paramzzfg, SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
    throws SQLiteException
  {
    int i = 0;
    if (paramzzfg == null) {
      throw new IllegalArgumentException("Monitor must not be null");
    }
    if (!zza(paramzzfg, paramSQLiteDatabase, paramString1)) {
      paramSQLiteDatabase.execSQL(paramString2);
    }
    if (paramzzfg == null) {
      try
      {
        paramSQLiteDatabase = new java/lang/IllegalArgumentException;
        paramSQLiteDatabase.<init>("Monitor must not be null");
        throw paramSQLiteDatabase;
      }
      catch (SQLiteException paramSQLiteDatabase)
      {
        paramzzfg.zzil().zzg("Failed to verify columns on table that was just created", paramString1);
        throw paramSQLiteDatabase;
      }
    }
    Set localSet = zzb(paramSQLiteDatabase, paramString1);
    for (paramString2 : paramString3.split(",")) {
      if (!localSet.remove(paramString2))
      {
        paramSQLiteDatabase = new android/database/sqlite/SQLiteException;
        i = String.valueOf(paramString1).length();
        ??? = String.valueOf(paramString2).length();
        paramString3 = new java/lang/StringBuilder;
        paramString3.<init>(i + 35 + ???);
        paramSQLiteDatabase.<init>("Table " + paramString1 + " is missing required column: " + paramString2);
        throw paramSQLiteDatabase;
      }
    }
    if (paramArrayOfString != null) {
      for (??? = i; ??? < paramArrayOfString.length; ??? += 2) {
        if (!localSet.remove(paramArrayOfString[???])) {
          paramSQLiteDatabase.execSQL(paramArrayOfString[(??? + 1)]);
        }
      }
    }
    if (!localSet.isEmpty()) {
      paramzzfg.zzin().zze("Table has extra columns. table, columns", paramString1, TextUtils.join(", ", localSet));
    }
  }
  
  /* Error */
  private static boolean zza(zzfg paramzzfg, SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aload_0
    //   3: ifnonnull +14 -> 17
    //   6: new 301	java/lang/IllegalArgumentException
    //   9: dup
    //   10: ldc_w 307
    //   13: invokespecial 304	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   16: athrow
    //   17: aload_1
    //   18: ldc_w 396
    //   21: iconst_1
    //   22: anewarray 19	java/lang/String
    //   25: dup
    //   26: iconst_0
    //   27: ldc_w 398
    //   30: aastore
    //   31: ldc_w 400
    //   34: iconst_1
    //   35: anewarray 19	java/lang/String
    //   38: dup
    //   39: iconst_0
    //   40: aload_2
    //   41: aastore
    //   42: aconst_null
    //   43: aconst_null
    //   44: aconst_null
    //   45: invokevirtual 404	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   48: astore_1
    //   49: aload_1
    //   50: astore_3
    //   51: aload_3
    //   52: astore_1
    //   53: aload_3
    //   54: invokeinterface 202 1 0
    //   59: istore 4
    //   61: iload 4
    //   63: istore 5
    //   65: aload_3
    //   66: ifnull +13 -> 79
    //   69: aload_3
    //   70: invokeinterface 209 1 0
    //   75: iload 4
    //   77: istore 5
    //   79: iload 5
    //   81: ireturn
    //   82: astore 6
    //   84: aconst_null
    //   85: astore_3
    //   86: aload_3
    //   87: astore_1
    //   88: aload_0
    //   89: invokevirtual 321	com/google/android/gms/internal/measurement/zzfg:zzin	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   92: ldc_w 406
    //   95: aload_2
    //   96: aload 6
    //   98: invokevirtual 232	com/google/android/gms/internal/measurement/zzfi:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   101: aload_3
    //   102: ifnull +9 -> 111
    //   105: aload_3
    //   106: invokeinterface 209 1 0
    //   111: iconst_0
    //   112: istore 5
    //   114: goto -35 -> 79
    //   117: astore_2
    //   118: aload_3
    //   119: astore_0
    //   120: aload_0
    //   121: ifnull +9 -> 130
    //   124: aload_0
    //   125: invokeinterface 209 1 0
    //   130: aload_2
    //   131: athrow
    //   132: astore_2
    //   133: aload_1
    //   134: astore_0
    //   135: goto -15 -> 120
    //   138: astore 6
    //   140: goto -54 -> 86
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	143	0	paramzzfg	zzfg
    //   0	143	1	paramSQLiteDatabase	SQLiteDatabase
    //   0	143	2	paramString	String
    //   1	118	3	localSQLiteDatabase	SQLiteDatabase
    //   59	17	4	bool1	boolean
    //   63	50	5	bool2	boolean
    //   82	15	6	localSQLiteException1	SQLiteException
    //   138	1	6	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   17	49	82	android/database/sqlite/SQLiteException
    //   17	49	117	finally
    //   53	61	132	finally
    //   88	101	132	finally
    //   53	61	138	android/database/sqlite/SQLiteException
  }
  
  private final boolean zza(String paramString, int paramInt, zzjz paramzzjz)
  {
    boolean bool = false;
    zzch();
    zzab();
    Preconditions.checkNotEmpty(paramString);
    Preconditions.checkNotNull(paramzzjz);
    if (TextUtils.isEmpty(paramzzjz.zzarl)) {
      zzgg().zzin().zzd("Event filter had no event name. Audience definition ignored. appId, audienceId, filterId", zzfg.zzbh(paramString), Integer.valueOf(paramInt), String.valueOf(paramzzjz.zzark));
    }
    for (;;)
    {
      return bool;
      try
      {
        byte[] arrayOfByte = new byte[paramzzjz.zzwg()];
        localObject = zzabb.zzb(arrayOfByte, 0, arrayOfByte.length);
        paramzzjz.zza((zzabb)localObject);
        ((zzabb)localObject).zzvy();
        localObject = new ContentValues();
        ((ContentValues)localObject).put("app_id", paramString);
        ((ContentValues)localObject).put("audience_id", Integer.valueOf(paramInt));
        ((ContentValues)localObject).put("filter_id", paramzzjz.zzark);
        ((ContentValues)localObject).put("event_name", paramzzjz.zzarl);
        ((ContentValues)localObject).put("data", arrayOfByte);
      }
      catch (IOException paramzzjz)
      {
        try
        {
          Object localObject;
          if (getWritableDatabase().insertWithOnConflict("event_filters", null, (ContentValues)localObject, 5) == -1L) {
            zzgg().zzil().zzg("Failed to insert event filter (got -1). appId", zzfg.zzbh(paramString));
          }
          bool = true;
        }
        catch (SQLiteException paramzzjz)
        {
          zzgg().zzil().zze("Error storing event filter. appId", zzfg.zzbh(paramString), paramzzjz);
        }
        paramzzjz = paramzzjz;
        zzgg().zzil().zze("Configuration loss. Failed to serialize event filter. appId", zzfg.zzbh(paramString), paramzzjz);
      }
    }
  }
  
  private final boolean zza(String paramString, int paramInt, zzkc paramzzkc)
  {
    boolean bool = false;
    zzch();
    zzab();
    Preconditions.checkNotEmpty(paramString);
    Preconditions.checkNotNull(paramzzkc);
    if (TextUtils.isEmpty(paramzzkc.zzasa)) {
      zzgg().zzin().zzd("Property filter had no property name. Audience definition ignored. appId, audienceId, filterId", zzfg.zzbh(paramString), Integer.valueOf(paramInt), String.valueOf(paramzzkc.zzark));
    }
    for (;;)
    {
      return bool;
      try
      {
        byte[] arrayOfByte = new byte[paramzzkc.zzwg()];
        Object localObject = zzabb.zzb(arrayOfByte, 0, arrayOfByte.length);
        paramzzkc.zza((zzabb)localObject);
        ((zzabb)localObject).zzvy();
        localObject = new ContentValues();
        ((ContentValues)localObject).put("app_id", paramString);
        ((ContentValues)localObject).put("audience_id", Integer.valueOf(paramInt));
        ((ContentValues)localObject).put("filter_id", paramzzkc.zzark);
        ((ContentValues)localObject).put("property_name", paramzzkc.zzasa);
        ((ContentValues)localObject).put("data", arrayOfByte);
        try
        {
          if (getWritableDatabase().insertWithOnConflict("property_filters", null, (ContentValues)localObject, 5) != -1L) {
            break label241;
          }
          zzgg().zzil().zzg("Failed to insert property filter (got -1). appId", zzfg.zzbh(paramString));
        }
        catch (SQLiteException paramzzkc)
        {
          zzgg().zzil().zze("Error storing property filter. appId", zzfg.zzbh(paramString), paramzzkc);
        }
      }
      catch (IOException paramzzkc)
      {
        zzgg().zzil().zze("Configuration loss. Failed to serialize property filter. appId", zzfg.zzbh(paramString), paramzzkc);
      }
      continue;
      label241:
      bool = true;
    }
  }
  
  private final boolean zza(String paramString, List<Integer> paramList)
  {
    bool1 = false;
    Preconditions.checkNotEmpty(paramString);
    zzch();
    zzab();
    localSQLiteDatabase = getWritableDatabase();
    try
    {
      long l = zza("select count(1) from audience_filter_values where app_id=?", new String[] { paramString });
      i = Math.max(0, Math.min(2000, zzgi().zzb(paramString, zzew.zzahq)));
      if (l > i) {
        break label101;
      }
      bool2 = bool1;
    }
    catch (SQLiteException paramList)
    {
      for (;;)
      {
        int i;
        zzgg().zzil().zze("Database error querying filters. appId", zzfg.zzbh(paramString), paramList);
        boolean bool2 = bool1;
        continue;
        ArrayList localArrayList = new ArrayList();
        for (int j = 0;; j++)
        {
          if (j >= paramList.size()) {
            break label178;
          }
          Integer localInteger = (Integer)paramList.get(j);
          bool2 = bool1;
          if (localInteger == null) {
            break;
          }
          bool2 = bool1;
          if (!(localInteger instanceof Integer)) {
            break;
          }
          localArrayList.add(Integer.toString(localInteger.intValue()));
        }
        paramList = TextUtils.join(",", localArrayList);
        paramList = String.valueOf(paramList).length() + 2 + "(" + paramList + ")";
        bool2 = bool1;
        if (localSQLiteDatabase.delete("audience_filter_values", String.valueOf(paramList).length() + 140 + "audience_id in (select audience_id from audience_filter_values where app_id=? and audience_id not in " + paramList + " order by rowid desc limit -1 offset ?)", new String[] { paramString, Integer.toString(i) }) > 0) {
          bool2 = true;
        }
      }
    }
    return bool2;
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
  
  private final boolean zzhv()
  {
    return getContext().getDatabasePath("google_app_measurement.db").exists();
  }
  
  public final void beginTransaction()
  {
    zzch();
    getWritableDatabase().beginTransaction();
  }
  
  public final void endTransaction()
  {
    zzch();
    getWritableDatabase().endTransaction();
  }
  
  final SQLiteDatabase getWritableDatabase()
  {
    zzab();
    try
    {
      SQLiteDatabase localSQLiteDatabase = this.zzafa.getWritableDatabase();
      return localSQLiteDatabase;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzgg().zzin().zzg("Error opening database", localSQLiteException);
      throw localSQLiteException;
    }
  }
  
  public final void setTransactionSuccessful()
  {
    zzch();
    getWritableDatabase().setTransactionSuccessful();
  }
  
  /* Error */
  public final android.util.Pair<zzki, Long> zza(String paramString, Long paramLong)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aload_0
    //   3: invokevirtual 415	com/google/android/gms/internal/measurement/zzhj:zzab	()V
    //   6: aload_0
    //   7: invokevirtual 412	com/google/android/gms/internal/measurement/zzhk:zzch	()V
    //   10: aload_0
    //   11: invokevirtual 190	com/google/android/gms/internal/measurement/zzei:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   14: ldc_w 621
    //   17: iconst_2
    //   18: anewarray 19	java/lang/String
    //   21: dup
    //   22: iconst_0
    //   23: aload_1
    //   24: aastore
    //   25: dup
    //   26: iconst_1
    //   27: aload_2
    //   28: invokestatic 360	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   31: aastore
    //   32: invokevirtual 196	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   35: astore 4
    //   37: aload 4
    //   39: astore 5
    //   41: aload 4
    //   43: invokeinterface 202 1 0
    //   48: ifne +38 -> 86
    //   51: aload 4
    //   53: astore 5
    //   55: aload_0
    //   56: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   59: invokevirtual 624	com/google/android/gms/internal/measurement/zzfg:zzir	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   62: ldc_w 626
    //   65: invokevirtual 256	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
    //   68: aload_3
    //   69: astore_1
    //   70: aload 4
    //   72: ifnull +12 -> 84
    //   75: aload 4
    //   77: invokeinterface 209 1 0
    //   82: aload_3
    //   83: astore_1
    //   84: aload_1
    //   85: areturn
    //   86: aload 4
    //   88: astore 5
    //   90: aload 4
    //   92: iconst_0
    //   93: invokeinterface 630 2 0
    //   98: astore 6
    //   100: aload 4
    //   102: astore 5
    //   104: aload 4
    //   106: iconst_1
    //   107: invokeinterface 206 2 0
    //   112: lstore 7
    //   114: aload 4
    //   116: astore 5
    //   118: aload 6
    //   120: iconst_0
    //   121: aload 6
    //   123: arraylength
    //   124: invokestatic 635	com/google/android/gms/internal/measurement/zzaba:zza	([BII)Lcom/google/android/gms/internal/measurement/zzaba;
    //   127: astore 6
    //   129: aload 4
    //   131: astore 5
    //   133: new 637	com/google/android/gms/internal/measurement/zzki
    //   136: astore 9
    //   138: aload 4
    //   140: astore 5
    //   142: aload 9
    //   144: invokespecial 638	com/google/android/gms/internal/measurement/zzki:<init>	()V
    //   147: aload 4
    //   149: astore 5
    //   151: aload 9
    //   153: aload 6
    //   155: invokevirtual 641	com/google/android/gms/internal/measurement/zzabj:zzb	(Lcom/google/android/gms/internal/measurement/zzaba;)Lcom/google/android/gms/internal/measurement/zzabj;
    //   158: pop
    //   159: aload 4
    //   161: astore 5
    //   163: aload 9
    //   165: lload 7
    //   167: invokestatic 261	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   170: invokestatic 647	android/util/Pair:create	(Ljava/lang/Object;Ljava/lang/Object;)Landroid/util/Pair;
    //   173: astore_2
    //   174: aload_2
    //   175: astore_1
    //   176: aload 4
    //   178: ifnull -94 -> 84
    //   181: aload 4
    //   183: invokeinterface 209 1 0
    //   188: aload_2
    //   189: astore_1
    //   190: goto -106 -> 84
    //   193: astore 6
    //   195: aload 4
    //   197: astore 5
    //   199: aload_0
    //   200: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   203: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   206: ldc_w 649
    //   209: aload_1
    //   210: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   213: aload_2
    //   214: aload 6
    //   216: invokevirtual 438	com/google/android/gms/internal/measurement/zzfi:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   219: aload_3
    //   220: astore_1
    //   221: aload 4
    //   223: ifnull -139 -> 84
    //   226: aload 4
    //   228: invokeinterface 209 1 0
    //   233: aload_3
    //   234: astore_1
    //   235: goto -151 -> 84
    //   238: astore_1
    //   239: aconst_null
    //   240: astore 4
    //   242: aload 4
    //   244: astore 5
    //   246: aload_0
    //   247: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   250: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   253: ldc_w 651
    //   256: aload_1
    //   257: invokevirtual 251	com/google/android/gms/internal/measurement/zzfi:zzg	(Ljava/lang/String;Ljava/lang/Object;)V
    //   260: aload_3
    //   261: astore_1
    //   262: aload 4
    //   264: ifnull -180 -> 84
    //   267: aload 4
    //   269: invokeinterface 209 1 0
    //   274: aload_3
    //   275: astore_1
    //   276: goto -192 -> 84
    //   279: astore_1
    //   280: aconst_null
    //   281: astore 5
    //   283: aload 5
    //   285: ifnull +10 -> 295
    //   288: aload 5
    //   290: invokeinterface 209 1 0
    //   295: aload_1
    //   296: athrow
    //   297: astore_1
    //   298: goto -15 -> 283
    //   301: astore_1
    //   302: goto -60 -> 242
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	305	0	this	zzei
    //   0	305	1	paramString	String
    //   0	305	2	paramLong	Long
    //   1	274	3	localObject1	Object
    //   35	233	4	localCursor1	Cursor
    //   39	250	5	localCursor2	Cursor
    //   98	56	6	localObject2	Object
    //   193	22	6	localIOException	IOException
    //   112	54	7	l	long
    //   136	28	9	localzzki	zzki
    // Exception table:
    //   from	to	target	type
    //   151	159	193	java/io/IOException
    //   10	37	238	android/database/sqlite/SQLiteException
    //   10	37	279	finally
    //   41	51	297	finally
    //   55	68	297	finally
    //   90	100	297	finally
    //   104	114	297	finally
    //   118	129	297	finally
    //   133	138	297	finally
    //   142	147	297	finally
    //   151	159	297	finally
    //   163	174	297	finally
    //   199	219	297	finally
    //   246	260	297	finally
    //   41	51	301	android/database/sqlite/SQLiteException
    //   55	68	301	android/database/sqlite/SQLiteException
    //   90	100	301	android/database/sqlite/SQLiteException
    //   104	114	301	android/database/sqlite/SQLiteException
    //   118	129	301	android/database/sqlite/SQLiteException
    //   133	138	301	android/database/sqlite/SQLiteException
    //   142	147	301	android/database/sqlite/SQLiteException
    //   151	159	301	android/database/sqlite/SQLiteException
    //   163	174	301	android/database/sqlite/SQLiteException
    //   199	219	301	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  public final zzej zza(long paramLong, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5)
  {
    // Byte code:
    //   0: aload_3
    //   1: invokestatic 283	com/google/android/gms/common/internal/Preconditions:checkNotEmpty	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 415	com/google/android/gms/internal/measurement/zzhj:zzab	()V
    //   9: aload_0
    //   10: invokevirtual 412	com/google/android/gms/internal/measurement/zzhk:zzch	()V
    //   13: new 655	com/google/android/gms/internal/measurement/zzej
    //   16: dup
    //   17: invokespecial 656	com/google/android/gms/internal/measurement/zzej:<init>	()V
    //   20: astore 9
    //   22: aload_0
    //   23: invokevirtual 190	com/google/android/gms/internal/measurement/zzei:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   26: astore 10
    //   28: aload 10
    //   30: ldc_w 658
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
    //   68: ldc_w 660
    //   71: iconst_1
    //   72: anewarray 19	java/lang/String
    //   75: dup
    //   76: iconst_0
    //   77: aload_3
    //   78: aastore
    //   79: aconst_null
    //   80: aconst_null
    //   81: aconst_null
    //   82: invokevirtual 404	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   85: astore 11
    //   87: aload 11
    //   89: astore 12
    //   91: aload 11
    //   93: invokeinterface 202 1 0
    //   98: ifne +39 -> 137
    //   101: aload 11
    //   103: astore 12
    //   105: aload_0
    //   106: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   109: invokevirtual 321	com/google/android/gms/internal/measurement/zzfg:zzin	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   112: ldc_w 662
    //   115: aload_3
    //   116: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   119: invokevirtual 251	com/google/android/gms/internal/measurement/zzfi:zzg	(Ljava/lang/String;Ljava/lang/Object;)V
    //   122: aload 11
    //   124: ifnull +10 -> 134
    //   127: aload 11
    //   129: invokeinterface 209 1 0
    //   134: aload 9
    //   136: areturn
    //   137: aload 11
    //   139: astore 12
    //   141: aload 11
    //   143: iconst_0
    //   144: invokeinterface 206 2 0
    //   149: lload_1
    //   150: lcmp
    //   151: ifne +88 -> 239
    //   154: aload 11
    //   156: astore 12
    //   158: aload 9
    //   160: aload 11
    //   162: iconst_1
    //   163: invokeinterface 206 2 0
    //   168: putfield 666	com/google/android/gms/internal/measurement/zzej:zzafd	J
    //   171: aload 11
    //   173: astore 12
    //   175: aload 9
    //   177: aload 11
    //   179: iconst_2
    //   180: invokeinterface 206 2 0
    //   185: putfield 669	com/google/android/gms/internal/measurement/zzej:zzafc	J
    //   188: aload 11
    //   190: astore 12
    //   192: aload 9
    //   194: aload 11
    //   196: iconst_3
    //   197: invokeinterface 206 2 0
    //   202: putfield 672	com/google/android/gms/internal/measurement/zzej:zzafe	J
    //   205: aload 11
    //   207: astore 12
    //   209: aload 9
    //   211: aload 11
    //   213: iconst_4
    //   214: invokeinterface 206 2 0
    //   219: putfield 675	com/google/android/gms/internal/measurement/zzej:zzaff	J
    //   222: aload 11
    //   224: astore 12
    //   226: aload 9
    //   228: aload 11
    //   230: iconst_5
    //   231: invokeinterface 206 2 0
    //   236: putfield 678	com/google/android/gms/internal/measurement/zzej:zzafg	J
    //   239: iload 4
    //   241: ifeq +19 -> 260
    //   244: aload 11
    //   246: astore 12
    //   248: aload 9
    //   250: aload 9
    //   252: getfield 666	com/google/android/gms/internal/measurement/zzej:zzafd	J
    //   255: lconst_1
    //   256: ladd
    //   257: putfield 666	com/google/android/gms/internal/measurement/zzej:zzafd	J
    //   260: iload 5
    //   262: ifeq +19 -> 281
    //   265: aload 11
    //   267: astore 12
    //   269: aload 9
    //   271: aload 9
    //   273: getfield 669	com/google/android/gms/internal/measurement/zzej:zzafc	J
    //   276: lconst_1
    //   277: ladd
    //   278: putfield 669	com/google/android/gms/internal/measurement/zzej:zzafc	J
    //   281: iload 6
    //   283: ifeq +19 -> 302
    //   286: aload 11
    //   288: astore 12
    //   290: aload 9
    //   292: aload 9
    //   294: getfield 672	com/google/android/gms/internal/measurement/zzej:zzafe	J
    //   297: lconst_1
    //   298: ladd
    //   299: putfield 672	com/google/android/gms/internal/measurement/zzej:zzafe	J
    //   302: iload 7
    //   304: ifeq +19 -> 323
    //   307: aload 11
    //   309: astore 12
    //   311: aload 9
    //   313: aload 9
    //   315: getfield 675	com/google/android/gms/internal/measurement/zzej:zzaff	J
    //   318: lconst_1
    //   319: ladd
    //   320: putfield 675	com/google/android/gms/internal/measurement/zzej:zzaff	J
    //   323: iload 8
    //   325: ifeq +19 -> 344
    //   328: aload 11
    //   330: astore 12
    //   332: aload 9
    //   334: aload 9
    //   336: getfield 678	com/google/android/gms/internal/measurement/zzej:zzafg	J
    //   339: lconst_1
    //   340: ladd
    //   341: putfield 678	com/google/android/gms/internal/measurement/zzej:zzafg	J
    //   344: aload 11
    //   346: astore 12
    //   348: new 289	android/content/ContentValues
    //   351: astore 13
    //   353: aload 11
    //   355: astore 12
    //   357: aload 13
    //   359: invokespecial 456	android/content/ContentValues:<init>	()V
    //   362: aload 11
    //   364: astore 12
    //   366: aload 13
    //   368: ldc 69
    //   370: lload_1
    //   371: invokestatic 261	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   374: invokevirtual 296	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   377: aload 11
    //   379: astore 12
    //   381: aload 13
    //   383: ldc 73
    //   385: aload 9
    //   387: getfield 669	com/google/android/gms/internal/measurement/zzej:zzafc	J
    //   390: invokestatic 261	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   393: invokevirtual 296	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   396: aload 11
    //   398: astore 12
    //   400: aload 13
    //   402: ldc 77
    //   404: aload 9
    //   406: getfield 666	com/google/android/gms/internal/measurement/zzej:zzafd	J
    //   409: invokestatic 261	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   412: invokevirtual 296	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   415: aload 11
    //   417: astore 12
    //   419: aload 13
    //   421: ldc 81
    //   423: aload 9
    //   425: getfield 672	com/google/android/gms/internal/measurement/zzej:zzafe	J
    //   428: invokestatic 261	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   431: invokevirtual 296	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   434: aload 11
    //   436: astore 12
    //   438: aload 13
    //   440: ldc 105
    //   442: aload 9
    //   444: getfield 675	com/google/android/gms/internal/measurement/zzej:zzaff	J
    //   447: invokestatic 261	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   450: invokevirtual 296	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   453: aload 11
    //   455: astore 12
    //   457: aload 13
    //   459: ldc 109
    //   461: aload 9
    //   463: getfield 678	com/google/android/gms/internal/measurement/zzej:zzafg	J
    //   466: invokestatic 261	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   469: invokevirtual 296	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   472: aload 11
    //   474: astore 12
    //   476: aload 10
    //   478: ldc_w 658
    //   481: aload 13
    //   483: ldc_w 660
    //   486: iconst_1
    //   487: anewarray 19	java/lang/String
    //   490: dup
    //   491: iconst_0
    //   492: aload_3
    //   493: aastore
    //   494: invokevirtual 682	android/database/sqlite/SQLiteDatabase:update	(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   497: pop
    //   498: aload 11
    //   500: ifnull +10 -> 510
    //   503: aload 11
    //   505: invokeinterface 209 1 0
    //   510: goto -376 -> 134
    //   513: astore 10
    //   515: aconst_null
    //   516: astore 11
    //   518: aload 11
    //   520: astore 12
    //   522: aload_0
    //   523: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   526: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   529: ldc_w 684
    //   532: aload_3
    //   533: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   536: aload 10
    //   538: invokevirtual 232	com/google/android/gms/internal/measurement/zzfi:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   541: aload 11
    //   543: ifnull +10 -> 553
    //   546: aload 11
    //   548: invokeinterface 209 1 0
    //   553: goto -419 -> 134
    //   556: astore_3
    //   557: aconst_null
    //   558: astore 12
    //   560: aload 12
    //   562: ifnull +10 -> 572
    //   565: aload 12
    //   567: invokeinterface 209 1 0
    //   572: aload_3
    //   573: athrow
    //   574: astore_3
    //   575: goto -15 -> 560
    //   578: astore 10
    //   580: goto -62 -> 518
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	583	0	this	zzei
    //   0	583	1	paramLong	long
    //   0	583	3	paramString	String
    //   0	583	4	paramBoolean1	boolean
    //   0	583	5	paramBoolean2	boolean
    //   0	583	6	paramBoolean3	boolean
    //   0	583	7	paramBoolean4	boolean
    //   0	583	8	paramBoolean5	boolean
    //   20	442	9	localzzej	zzej
    //   26	451	10	localSQLiteDatabase	SQLiteDatabase
    //   513	24	10	localSQLiteException1	SQLiteException
    //   578	1	10	localSQLiteException2	SQLiteException
    //   85	462	11	localCursor1	Cursor
    //   89	477	12	localCursor2	Cursor
    //   351	131	13	localContentValues	ContentValues
    // Exception table:
    //   from	to	target	type
    //   22	87	513	android/database/sqlite/SQLiteException
    //   22	87	556	finally
    //   91	101	574	finally
    //   105	122	574	finally
    //   141	154	574	finally
    //   158	171	574	finally
    //   175	188	574	finally
    //   192	205	574	finally
    //   209	222	574	finally
    //   226	239	574	finally
    //   248	260	574	finally
    //   269	281	574	finally
    //   290	302	574	finally
    //   311	323	574	finally
    //   332	344	574	finally
    //   348	353	574	finally
    //   357	362	574	finally
    //   366	377	574	finally
    //   381	396	574	finally
    //   400	415	574	finally
    //   419	434	574	finally
    //   438	453	574	finally
    //   457	472	574	finally
    //   476	498	574	finally
    //   522	541	574	finally
    //   91	101	578	android/database/sqlite/SQLiteException
    //   105	122	578	android/database/sqlite/SQLiteException
    //   141	154	578	android/database/sqlite/SQLiteException
    //   158	171	578	android/database/sqlite/SQLiteException
    //   175	188	578	android/database/sqlite/SQLiteException
    //   192	205	578	android/database/sqlite/SQLiteException
    //   209	222	578	android/database/sqlite/SQLiteException
    //   226	239	578	android/database/sqlite/SQLiteException
    //   248	260	578	android/database/sqlite/SQLiteException
    //   269	281	578	android/database/sqlite/SQLiteException
    //   290	302	578	android/database/sqlite/SQLiteException
    //   311	323	578	android/database/sqlite/SQLiteException
    //   332	344	578	android/database/sqlite/SQLiteException
    //   348	353	578	android/database/sqlite/SQLiteException
    //   357	362	578	android/database/sqlite/SQLiteException
    //   366	377	578	android/database/sqlite/SQLiteException
    //   381	396	578	android/database/sqlite/SQLiteException
    //   400	415	578	android/database/sqlite/SQLiteException
    //   419	434	578	android/database/sqlite/SQLiteException
    //   438	453	578	android/database/sqlite/SQLiteException
    //   457	472	578	android/database/sqlite/SQLiteException
    //   476	498	578	android/database/sqlite/SQLiteException
  }
  
  public final void zza(zzeb paramzzeb)
  {
    Preconditions.checkNotNull(paramzzeb);
    zzab();
    zzch();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzeb.zzah());
    localContentValues.put("app_instance_id", paramzzeb.getAppInstanceId());
    localContentValues.put("gmp_app_id", paramzzeb.getGmpAppId());
    localContentValues.put("resettable_device_id_hash", paramzzeb.zzgk());
    localContentValues.put("last_bundle_index", Long.valueOf(paramzzeb.zzgs()));
    localContentValues.put("last_bundle_start_timestamp", Long.valueOf(paramzzeb.zzgm()));
    localContentValues.put("last_bundle_end_timestamp", Long.valueOf(paramzzeb.zzgn()));
    localContentValues.put("app_version", paramzzeb.zzag());
    localContentValues.put("app_store", paramzzeb.zzgp());
    localContentValues.put("gmp_version", Long.valueOf(paramzzeb.zzgq()));
    localContentValues.put("dev_cert_hash", Long.valueOf(paramzzeb.zzgr()));
    localContentValues.put("measurement_enabled", Boolean.valueOf(paramzzeb.isMeasurementEnabled()));
    localContentValues.put("day", Long.valueOf(paramzzeb.zzgw()));
    localContentValues.put("daily_public_events_count", Long.valueOf(paramzzeb.zzgx()));
    localContentValues.put("daily_events_count", Long.valueOf(paramzzeb.zzgy()));
    localContentValues.put("daily_conversions_count", Long.valueOf(paramzzeb.zzgz()));
    localContentValues.put("config_fetched_time", Long.valueOf(paramzzeb.zzgt()));
    localContentValues.put("failed_config_fetch_time", Long.valueOf(paramzzeb.zzgu()));
    localContentValues.put("app_version_int", Long.valueOf(paramzzeb.zzgo()));
    localContentValues.put("firebase_instance_id", paramzzeb.zzgl());
    localContentValues.put("daily_error_events_count", Long.valueOf(paramzzeb.zzhb()));
    localContentValues.put("daily_realtime_events_count", Long.valueOf(paramzzeb.zzha()));
    localContentValues.put("health_monitor_sample", paramzzeb.zzhc());
    localContentValues.put("android_id", Long.valueOf(paramzzeb.zzhe()));
    localContentValues.put("adid_reporting_enabled", Boolean.valueOf(paramzzeb.zzhf()));
    localContentValues.put("ssaid_reporting_enabled", Boolean.valueOf(paramzzeb.zzhg()));
    try
    {
      SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
      if ((localSQLiteDatabase.update("apps", localContentValues, "app_id = ?", new String[] { paramzzeb.zzah() }) == 0L) && (localSQLiteDatabase.insertWithOnConflict("apps", null, localContentValues, 5) == -1L)) {
        zzgg().zzil().zzg("Failed to insert/update app (got -1). appId", zzfg.zzbh(paramzzeb.zzah()));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      for (;;)
      {
        zzgg().zzil().zze("Error storing app. appId", zzfg.zzbh(paramzzeb.zzah()), localSQLiteException);
      }
    }
  }
  
  public final void zza(zzeq paramzzeq)
  {
    Object localObject1 = null;
    Preconditions.checkNotNull(paramzzeq);
    zzab();
    zzch();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzeq.zztd);
    localContentValues.put("name", paramzzeq.name);
    localContentValues.put("lifetime_count", Long.valueOf(paramzzeq.zzafp));
    localContentValues.put("current_bundle_count", Long.valueOf(paramzzeq.zzafq));
    localContentValues.put("last_fire_timestamp", Long.valueOf(paramzzeq.zzafr));
    localContentValues.put("last_bundled_timestamp", Long.valueOf(paramzzeq.zzafs));
    localContentValues.put("last_sampled_complex_event_id", paramzzeq.zzaft);
    localContentValues.put("last_sampling_rate", paramzzeq.zzafu);
    Object localObject2 = localObject1;
    if (paramzzeq.zzafv != null)
    {
      localObject2 = localObject1;
      if (paramzzeq.zzafv.booleanValue()) {
        localObject2 = Long.valueOf(1L);
      }
    }
    localContentValues.put("last_exempt_from_sampling", (Long)localObject2);
    try
    {
      if (getWritableDatabase().insertWithOnConflict("events", null, localContentValues, 5) == -1L) {
        zzgg().zzil().zzg("Failed to insert/update event aggregates (got -1). appId", zzfg.zzbh(paramzzeq.zztd));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      for (;;)
      {
        zzgg().zzil().zze("Error storing event aggregates. appId", zzfg.zzbh(paramzzeq.zztd), localSQLiteException);
      }
    }
  }
  
  final void zza(String paramString, zzjy[] paramArrayOfzzjy)
  {
    int i = 0;
    zzch();
    zzab();
    Preconditions.checkNotEmpty(paramString);
    Preconditions.checkNotNull(paramArrayOfzzjy);
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    Object localObject1;
    int k;
    int m;
    for (;;)
    {
      try
      {
        zzch();
        zzab();
        Preconditions.checkNotEmpty(paramString);
        localObject1 = getWritableDatabase();
        ((SQLiteDatabase)localObject1).delete("property_filters", "app_id=?", new String[] { paramString });
        ((SQLiteDatabase)localObject1).delete("event_filters", "app_id=?", new String[] { paramString });
        int j = paramArrayOfzzjy.length;
        k = 0;
        if (k >= j) {
          break label512;
        }
        localObject1 = paramArrayOfzzjy[k];
        zzch();
        zzab();
        Preconditions.checkNotEmpty(paramString);
        Preconditions.checkNotNull(localObject1);
        Preconditions.checkNotNull(((zzjy)localObject1).zzari);
        Preconditions.checkNotNull(((zzjy)localObject1).zzarh);
        if (((zzjy)localObject1).zzarg == null)
        {
          zzgg().zzin().zzg("Audience with no ID. appId", zzfg.zzbh(paramString));
          k++;
        }
        else
        {
          m = ((zzjy)localObject1).zzarg.intValue();
          localObject2 = ((zzjy)localObject1).zzari;
          n = localObject2.length;
          i1 = 0;
          if (i1 >= n) {
            break;
          }
          if (localObject2[i1].zzark == null) {
            zzgg().zzin().zze("Event filter with no ID. Audience definition ignored. appId, audienceId", zzfg.zzbh(paramString), ((zzjy)localObject1).zzarg);
          } else {
            i1++;
          }
        }
      }
      finally
      {
        localSQLiteDatabase.endTransaction();
      }
    }
    Object localObject2 = ((zzjy)localObject1).zzarh;
    int n = localObject2.length;
    for (int i1 = 0;; i1++)
    {
      if (i1 >= n) {
        break label324;
      }
      if (localObject2[i1].zzark == null)
      {
        zzgg().zzin().zze("Property filter with no ID. Audience definition ignored. appId, audienceId", zzfg.zzbh(paramString), ((zzjy)localObject1).zzarg);
        break;
      }
    }
    label324:
    localObject2 = ((zzjy)localObject1).zzari;
    n = localObject2.length;
    i1 = 0;
    label339:
    if (i1 < n) {
      if (zza(paramString, m, localObject2[i1])) {}
    }
    for (i1 = 0;; i1 = 1)
    {
      int i2 = i1;
      int i3;
      if (i1 != 0)
      {
        localObject1 = ((zzjy)localObject1).zzarh;
        i3 = localObject1.length;
      }
      for (n = 0;; n++)
      {
        i2 = i1;
        if (n < i3)
        {
          if (!zza(paramString, m, localObject1[n])) {
            i2 = 0;
          }
        }
        else
        {
          if (i2 != 0) {
            break;
          }
          zzch();
          zzab();
          Preconditions.checkNotEmpty(paramString);
          localObject1 = getWritableDatabase();
          ((SQLiteDatabase)localObject1).delete("property_filters", "app_id=? and audience_id=?", new String[] { paramString, String.valueOf(m) });
          ((SQLiteDatabase)localObject1).delete("event_filters", "app_id=? and audience_id=?", new String[] { paramString, String.valueOf(m) });
          break;
          i1++;
          break label339;
        }
      }
      label512:
      localObject1 = new java/util/ArrayList;
      ((ArrayList)localObject1).<init>();
      k = paramArrayOfzzjy.length;
      for (i1 = i; i1 < k; i1++) {
        ((List)localObject1).add(paramArrayOfzzjy[i1].zzarg);
      }
      zza(paramString, (List)localObject1);
      localSQLiteDatabase.setTransactionSuccessful();
      localSQLiteDatabase.endTransaction();
      return;
    }
  }
  
  public final boolean zza(zzju paramzzju)
  {
    boolean bool = false;
    Preconditions.checkNotNull(paramzzju);
    zzab();
    zzch();
    if (zzg(paramzzju.zztd, paramzzju.name) == null) {
      if (zzjv.zzbv(paramzzju.name)) {
        if (zza("select count(1) from user_attributes where app_id=? and name not like '!_%' escape '!'", new String[] { paramzzju.zztd }) < 25L) {
          break label99;
        }
      }
    }
    for (;;)
    {
      return bool;
      if (zza("select count(1) from user_attributes where app_id=? and origin=? AND name like '!_%' escape '!'", new String[] { paramzzju.zztd, paramzzju.zzaek }) >= 25L) {
        continue;
      }
      label99:
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("app_id", paramzzju.zztd);
      localContentValues.put("origin", paramzzju.zzaek);
      localContentValues.put("name", paramzzju.name);
      localContentValues.put("set_timestamp", Long.valueOf(paramzzju.zzaqu));
      zza(localContentValues, "value", paramzzju.value);
      try
      {
        if (getWritableDatabase().insertWithOnConflict("user_attributes", null, localContentValues, 5) == -1L) {
          zzgg().zzil().zzg("Failed to insert/update user property (got -1). appId", zzfg.zzbh(paramzzju.zztd));
        }
        bool = true;
      }
      catch (SQLiteException localSQLiteException)
      {
        for (;;)
        {
          zzgg().zzil().zze("Error storing user property. appId", zzfg.zzbh(paramzzju.zztd), localSQLiteException);
        }
      }
    }
  }
  
  public final boolean zza(zzkl paramzzkl, boolean paramBoolean)
  {
    bool = false;
    zzab();
    zzch();
    Preconditions.checkNotNull(paramzzkl);
    Preconditions.checkNotEmpty(paramzzkl.zztd);
    Preconditions.checkNotNull(paramzzkl.zzath);
    zzhp();
    long l = zzbt().currentTimeMillis();
    if ((paramzzkl.zzath.longValue() < l - zzeh.zzhj()) || (paramzzkl.zzath.longValue() > zzeh.zzhj() + l)) {
      zzgg().zzin().zzd("Storing bundle outside of the max uploading time span. appId, now, timestamp", zzfg.zzbh(paramzzkl.zztd), Long.valueOf(l), paramzzkl.zzath);
    }
    try
    {
      localObject1 = new byte[paramzzkl.zzwg()];
      Object localObject2 = zzabb.zzb((byte[])localObject1, 0, localObject1.length);
      paramzzkl.zza((zzabb)localObject2);
      ((zzabb)localObject2).zzvy();
      localObject2 = zzgc().zza((byte[])localObject1);
      zzgg().zzir().zzg("Saving bundle, size", Integer.valueOf(localObject2.length));
      localObject1 = new ContentValues();
      ((ContentValues)localObject1).put("app_id", paramzzkl.zztd);
      ((ContentValues)localObject1).put("bundle_end_timestamp", paramzzkl.zzath);
      ((ContentValues)localObject1).put("data", (byte[])localObject2);
      if (!paramBoolean) {
        break label322;
      }
      i = 1;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        Object localObject1;
        zzgg().zzil().zze("Data loss. Failed to serialize bundle. appId", zzfg.zzbh(paramzzkl.zztd), localIOException);
        paramBoolean = bool;
        continue;
        int i = 0;
      }
    }
    ((ContentValues)localObject1).put("has_realtime", Integer.valueOf(i));
    if (paramzzkl.zzaue != null) {
      ((ContentValues)localObject1).put("retry_count", paramzzkl.zzaue);
    }
    try
    {
      if (getWritableDatabase().insert("queue", null, (ContentValues)localObject1) != -1L) {
        break label357;
      }
      zzgg().zzil().zzg("Failed to insert bundle (got -1). appId", zzfg.zzbh(paramzzkl.zztd));
      paramBoolean = bool;
    }
    catch (SQLiteException localSQLiteException)
    {
      for (;;)
      {
        zzgg().zzil().zze("Error storing bundle. appId", zzfg.zzbh(paramzzkl.zztd), localSQLiteException);
        paramBoolean = bool;
        continue;
        paramBoolean = true;
      }
    }
    return paramBoolean;
  }
  
  /* Error */
  public final boolean zza(String paramString, Long paramLong, long paramLong1, zzki paramzzki)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 6
    //   3: aload_0
    //   4: invokevirtual 415	com/google/android/gms/internal/measurement/zzhj:zzab	()V
    //   7: aload_0
    //   8: invokevirtual 412	com/google/android/gms/internal/measurement/zzhk:zzch	()V
    //   11: aload 5
    //   13: invokestatic 287	com/google/android/gms/common/internal/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   16: pop
    //   17: aload_1
    //   18: invokestatic 283	com/google/android/gms/common/internal/Preconditions:checkNotEmpty	(Ljava/lang/String;)Ljava/lang/String;
    //   21: pop
    //   22: aload_2
    //   23: invokestatic 287	com/google/android/gms/common/internal/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   26: pop
    //   27: aload 5
    //   29: invokevirtual 443	com/google/android/gms/internal/measurement/zzabj:zzwg	()I
    //   32: newarray <illegal type>
    //   34: astore 7
    //   36: aload 7
    //   38: iconst_0
    //   39: aload 7
    //   41: arraylength
    //   42: invokestatic 448	com/google/android/gms/internal/measurement/zzabb:zzb	([BII)Lcom/google/android/gms/internal/measurement/zzabb;
    //   45: astore 8
    //   47: aload 5
    //   49: aload 8
    //   51: invokevirtual 451	com/google/android/gms/internal/measurement/zzabj:zza	(Lcom/google/android/gms/internal/measurement/zzabb;)V
    //   54: aload 8
    //   56: invokevirtual 454	com/google/android/gms/internal/measurement/zzabb:zzvy	()V
    //   59: aload_0
    //   60: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   63: invokevirtual 624	com/google/android/gms/internal/measurement/zzfg:zzir	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   66: ldc_w 959
    //   69: aload_0
    //   70: invokevirtual 963	com/google/android/gms/internal/measurement/zzhj:zzgb	()Lcom/google/android/gms/internal/measurement/zzfe;
    //   73: aload_1
    //   74: invokevirtual 968	com/google/android/gms/internal/measurement/zzfe:zzbe	(Ljava/lang/String;)Ljava/lang/String;
    //   77: aload 7
    //   79: arraylength
    //   80: invokestatic 247	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   83: invokevirtual 232	com/google/android/gms/internal/measurement/zzfi:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   86: new 289	android/content/ContentValues
    //   89: dup
    //   90: invokespecial 456	android/content/ContentValues:<init>	()V
    //   93: astore 5
    //   95: aload 5
    //   97: ldc_w 458
    //   100: aload_1
    //   101: invokevirtual 293	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   104: aload 5
    //   106: ldc_w 970
    //   109: aload_2
    //   110: invokevirtual 296	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   113: aload 5
    //   115: ldc_w 972
    //   118: lload_3
    //   119: invokestatic 261	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   122: invokevirtual 296	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   125: aload 5
    //   127: ldc_w 974
    //   130: aload 7
    //   132: invokevirtual 472	android/content/ContentValues:put	(Ljava/lang/String;[B)V
    //   135: aload_0
    //   136: invokevirtual 190	com/google/android/gms/internal/measurement/zzei:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   139: ldc_w 976
    //   142: aconst_null
    //   143: aload 5
    //   145: iconst_5
    //   146: invokevirtual 478	android/database/sqlite/SQLiteDatabase:insertWithOnConflict	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    //   149: ldc2_w 479
    //   152: lcmp
    //   153: ifne +70 -> 223
    //   156: aload_0
    //   157: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   160: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   163: ldc_w 978
    //   166: aload_1
    //   167: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   170: invokevirtual 251	com/google/android/gms/internal/measurement/zzfi:zzg	(Ljava/lang/String;Ljava/lang/Object;)V
    //   173: iload 6
    //   175: ireturn
    //   176: astore 5
    //   178: aload_0
    //   179: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   182: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   185: ldc_w 980
    //   188: aload_1
    //   189: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   192: aload_2
    //   193: aload 5
    //   195: invokevirtual 438	com/google/android/gms/internal/measurement/zzfi:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   198: goto -25 -> 173
    //   201: astore_2
    //   202: aload_0
    //   203: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   206: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   209: ldc_w 982
    //   212: aload_1
    //   213: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   216: aload_2
    //   217: invokevirtual 232	com/google/android/gms/internal/measurement/zzfi:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   220: goto -47 -> 173
    //   223: iconst_1
    //   224: istore 6
    //   226: goto -53 -> 173
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	229	0	this	zzei
    //   0	229	1	paramString	String
    //   0	229	2	paramLong	Long
    //   0	229	3	paramLong1	long
    //   0	229	5	paramzzki	zzki
    //   1	224	6	bool	boolean
    //   34	97	7	arrayOfByte	byte[]
    //   45	10	8	localzzabb	zzabb
    // Exception table:
    //   from	to	target	type
    //   27	59	176	java/io/IOException
    //   135	173	201	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  public final String zzab(long paramLong)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aload_0
    //   3: invokevirtual 415	com/google/android/gms/internal/measurement/zzhj:zzab	()V
    //   6: aload_0
    //   7: invokevirtual 412	com/google/android/gms/internal/measurement/zzhk:zzch	()V
    //   10: aload_0
    //   11: invokevirtual 190	com/google/android/gms/internal/measurement/zzei:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   14: ldc_w 985
    //   17: iconst_1
    //   18: anewarray 19	java/lang/String
    //   21: dup
    //   22: iconst_0
    //   23: lload_1
    //   24: invokestatic 987	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   27: aastore
    //   28: invokevirtual 196	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   31: astore 4
    //   33: aload 4
    //   35: astore 5
    //   37: aload 4
    //   39: invokeinterface 202 1 0
    //   44: ifne +41 -> 85
    //   47: aload 4
    //   49: astore 5
    //   51: aload_0
    //   52: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   55: invokevirtual 624	com/google/android/gms/internal/measurement/zzfg:zzir	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   58: ldc_w 989
    //   61: invokevirtual 256	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
    //   64: aload_3
    //   65: astore 5
    //   67: aload 4
    //   69: ifnull +13 -> 82
    //   72: aload 4
    //   74: invokeinterface 209 1 0
    //   79: aload_3
    //   80: astore 5
    //   82: aload 5
    //   84: areturn
    //   85: aload 4
    //   87: astore 5
    //   89: aload 4
    //   91: iconst_0
    //   92: invokeinterface 274 2 0
    //   97: astore 6
    //   99: aload 6
    //   101: astore 5
    //   103: aload 4
    //   105: ifnull -23 -> 82
    //   108: aload 4
    //   110: invokeinterface 209 1 0
    //   115: aload 6
    //   117: astore 5
    //   119: goto -37 -> 82
    //   122: astore 6
    //   124: aconst_null
    //   125: astore 4
    //   127: aload 4
    //   129: astore 5
    //   131: aload_0
    //   132: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   135: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   138: ldc_w 991
    //   141: aload 6
    //   143: invokevirtual 251	com/google/android/gms/internal/measurement/zzfi:zzg	(Ljava/lang/String;Ljava/lang/Object;)V
    //   146: aload_3
    //   147: astore 5
    //   149: aload 4
    //   151: ifnull -69 -> 82
    //   154: aload 4
    //   156: invokeinterface 209 1 0
    //   161: aload_3
    //   162: astore 5
    //   164: goto -82 -> 82
    //   167: astore 4
    //   169: aconst_null
    //   170: astore 5
    //   172: aload 5
    //   174: ifnull +10 -> 184
    //   177: aload 5
    //   179: invokeinterface 209 1 0
    //   184: aload 4
    //   186: athrow
    //   187: astore 4
    //   189: goto -17 -> 172
    //   192: astore 6
    //   194: goto -67 -> 127
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	197	0	this	zzei
    //   0	197	1	paramLong	long
    //   1	161	3	localObject1	Object
    //   31	124	4	localCursor	Cursor
    //   167	18	4	localObject2	Object
    //   187	1	4	localObject3	Object
    //   35	143	5	localObject4	Object
    //   97	19	6	str	String
    //   122	20	6	localSQLiteException1	SQLiteException
    //   192	1	6	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   10	33	122	android/database/sqlite/SQLiteException
    //   10	33	167	finally
    //   37	47	187	finally
    //   51	64	187	finally
    //   89	99	187	finally
    //   131	146	187	finally
    //   37	47	192	android/database/sqlite/SQLiteException
    //   51	64	192	android/database/sqlite/SQLiteException
    //   89	99	192	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  public final zzeb zzax(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 283	com/google/android/gms/common/internal/Preconditions:checkNotEmpty	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 415	com/google/android/gms/internal/measurement/zzhj:zzab	()V
    //   9: aload_0
    //   10: invokevirtual 412	com/google/android/gms/internal/measurement/zzhk:zzch	()V
    //   13: aload_0
    //   14: invokevirtual 190	com/google/android/gms/internal/measurement/zzei:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: ldc_w 658
    //   20: bipush 25
    //   22: anewarray 19	java/lang/String
    //   25: dup
    //   26: iconst_0
    //   27: ldc_w 692
    //   30: aastore
    //   31: dup
    //   32: iconst_1
    //   33: ldc_w 697
    //   36: aastore
    //   37: dup
    //   38: iconst_2
    //   39: ldc_w 702
    //   42: aastore
    //   43: dup
    //   44: iconst_3
    //   45: ldc_w 707
    //   48: aastore
    //   49: dup
    //   50: iconst_4
    //   51: ldc 65
    //   53: aastore
    //   54: dup
    //   55: iconst_5
    //   56: ldc_w 716
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
    //   168: dup
    //   169: bipush 24
    //   171: ldc 125
    //   173: aastore
    //   174: ldc_w 660
    //   177: iconst_1
    //   178: anewarray 19	java/lang/String
    //   181: dup
    //   182: iconst_0
    //   183: aload_1
    //   184: aastore
    //   185: aconst_null
    //   186: aconst_null
    //   187: aconst_null
    //   188: invokevirtual 404	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   191: astore_2
    //   192: aload_2
    //   193: astore_3
    //   194: aload_2
    //   195: invokeinterface 202 1 0
    //   200: istore 4
    //   202: iload 4
    //   204: ifne +17 -> 221
    //   207: aload_2
    //   208: ifnull +9 -> 217
    //   211: aload_2
    //   212: invokeinterface 209 1 0
    //   217: aconst_null
    //   218: astore_1
    //   219: aload_1
    //   220: areturn
    //   221: aload_2
    //   222: astore_3
    //   223: new 687	com/google/android/gms/internal/measurement/zzeb
    //   226: astore 5
    //   228: aload_2
    //   229: astore_3
    //   230: aload 5
    //   232: aload_0
    //   233: getfield 997	com/google/android/gms/internal/measurement/zzei:zzacr	Lcom/google/android/gms/internal/measurement/zzgl;
    //   236: aload_1
    //   237: invokespecial 1000	com/google/android/gms/internal/measurement/zzeb:<init>	(Lcom/google/android/gms/internal/measurement/zzgl;Ljava/lang/String;)V
    //   240: aload_2
    //   241: astore_3
    //   242: aload 5
    //   244: aload_2
    //   245: iconst_0
    //   246: invokeinterface 274 2 0
    //   251: invokevirtual 1003	com/google/android/gms/internal/measurement/zzeb:zzal	(Ljava/lang/String;)V
    //   254: aload_2
    //   255: astore_3
    //   256: aload 5
    //   258: aload_2
    //   259: iconst_1
    //   260: invokeinterface 274 2 0
    //   265: invokevirtual 1006	com/google/android/gms/internal/measurement/zzeb:zzam	(Ljava/lang/String;)V
    //   268: aload_2
    //   269: astore_3
    //   270: aload 5
    //   272: aload_2
    //   273: iconst_2
    //   274: invokeinterface 274 2 0
    //   279: invokevirtual 1009	com/google/android/gms/internal/measurement/zzeb:zzan	(Ljava/lang/String;)V
    //   282: aload_2
    //   283: astore_3
    //   284: aload 5
    //   286: aload_2
    //   287: iconst_3
    //   288: invokeinterface 206 2 0
    //   293: invokevirtual 1013	com/google/android/gms/internal/measurement/zzeb:zzr	(J)V
    //   296: aload_2
    //   297: astore_3
    //   298: aload 5
    //   300: aload_2
    //   301: iconst_4
    //   302: invokeinterface 206 2 0
    //   307: invokevirtual 1016	com/google/android/gms/internal/measurement/zzeb:zzm	(J)V
    //   310: aload_2
    //   311: astore_3
    //   312: aload 5
    //   314: aload_2
    //   315: iconst_5
    //   316: invokeinterface 206 2 0
    //   321: invokevirtual 1019	com/google/android/gms/internal/measurement/zzeb:zzn	(J)V
    //   324: aload_2
    //   325: astore_3
    //   326: aload 5
    //   328: aload_2
    //   329: bipush 6
    //   331: invokeinterface 274 2 0
    //   336: invokevirtual 1022	com/google/android/gms/internal/measurement/zzeb:setAppVersion	(Ljava/lang/String;)V
    //   339: aload_2
    //   340: astore_3
    //   341: aload 5
    //   343: aload_2
    //   344: bipush 7
    //   346: invokeinterface 274 2 0
    //   351: invokevirtual 1025	com/google/android/gms/internal/measurement/zzeb:zzap	(Ljava/lang/String;)V
    //   354: aload_2
    //   355: astore_3
    //   356: aload 5
    //   358: aload_2
    //   359: bipush 8
    //   361: invokeinterface 206 2 0
    //   366: invokevirtual 1028	com/google/android/gms/internal/measurement/zzeb:zzp	(J)V
    //   369: aload_2
    //   370: astore_3
    //   371: aload 5
    //   373: aload_2
    //   374: bipush 9
    //   376: invokeinterface 206 2 0
    //   381: invokevirtual 1031	com/google/android/gms/internal/measurement/zzeb:zzq	(J)V
    //   384: aload_2
    //   385: astore_3
    //   386: aload_2
    //   387: bipush 10
    //   389: invokeinterface 1035 2 0
    //   394: ifne +16 -> 410
    //   397: aload_2
    //   398: astore_3
    //   399: aload_2
    //   400: bipush 10
    //   402: invokeinterface 1038 2 0
    //   407: ifeq +349 -> 756
    //   410: iconst_1
    //   411: istore 4
    //   413: aload_2
    //   414: astore_3
    //   415: aload 5
    //   417: iload 4
    //   419: invokevirtual 1042	com/google/android/gms/internal/measurement/zzeb:setMeasurementEnabled	(Z)V
    //   422: aload_2
    //   423: astore_3
    //   424: aload 5
    //   426: aload_2
    //   427: bipush 11
    //   429: invokeinterface 206 2 0
    //   434: invokevirtual 1045	com/google/android/gms/internal/measurement/zzeb:zzu	(J)V
    //   437: aload_2
    //   438: astore_3
    //   439: aload 5
    //   441: aload_2
    //   442: bipush 12
    //   444: invokeinterface 206 2 0
    //   449: invokevirtual 1048	com/google/android/gms/internal/measurement/zzeb:zzv	(J)V
    //   452: aload_2
    //   453: astore_3
    //   454: aload 5
    //   456: aload_2
    //   457: bipush 13
    //   459: invokeinterface 206 2 0
    //   464: invokevirtual 1051	com/google/android/gms/internal/measurement/zzeb:zzw	(J)V
    //   467: aload_2
    //   468: astore_3
    //   469: aload 5
    //   471: aload_2
    //   472: bipush 14
    //   474: invokeinterface 206 2 0
    //   479: invokevirtual 1054	com/google/android/gms/internal/measurement/zzeb:zzx	(J)V
    //   482: aload_2
    //   483: astore_3
    //   484: aload 5
    //   486: aload_2
    //   487: bipush 15
    //   489: invokeinterface 206 2 0
    //   494: invokevirtual 1057	com/google/android/gms/internal/measurement/zzeb:zzs	(J)V
    //   497: aload_2
    //   498: astore_3
    //   499: aload 5
    //   501: aload_2
    //   502: bipush 16
    //   504: invokeinterface 206 2 0
    //   509: invokevirtual 1060	com/google/android/gms/internal/measurement/zzeb:zzt	(J)V
    //   512: aload_2
    //   513: astore_3
    //   514: aload_2
    //   515: bipush 17
    //   517: invokeinterface 1035 2 0
    //   522: ifeq +240 -> 762
    //   525: ldc2_w 1061
    //   528: lstore 6
    //   530: aload_2
    //   531: astore_3
    //   532: aload 5
    //   534: lload 6
    //   536: invokevirtual 1065	com/google/android/gms/internal/measurement/zzeb:zzo	(J)V
    //   539: aload_2
    //   540: astore_3
    //   541: aload 5
    //   543: aload_2
    //   544: bipush 18
    //   546: invokeinterface 274 2 0
    //   551: invokevirtual 1068	com/google/android/gms/internal/measurement/zzeb:zzao	(Ljava/lang/String;)V
    //   554: aload_2
    //   555: astore_3
    //   556: aload 5
    //   558: aload_2
    //   559: bipush 19
    //   561: invokeinterface 206 2 0
    //   566: invokevirtual 1071	com/google/android/gms/internal/measurement/zzeb:zzz	(J)V
    //   569: aload_2
    //   570: astore_3
    //   571: aload 5
    //   573: aload_2
    //   574: bipush 20
    //   576: invokeinterface 206 2 0
    //   581: invokevirtual 1074	com/google/android/gms/internal/measurement/zzeb:zzy	(J)V
    //   584: aload_2
    //   585: astore_3
    //   586: aload 5
    //   588: aload_2
    //   589: bipush 21
    //   591: invokeinterface 274 2 0
    //   596: invokevirtual 1077	com/google/android/gms/internal/measurement/zzeb:zzaq	(Ljava/lang/String;)V
    //   599: aload_2
    //   600: astore_3
    //   601: aload_2
    //   602: bipush 22
    //   604: invokeinterface 1035 2 0
    //   609: ifeq +169 -> 778
    //   612: lconst_0
    //   613: lstore 6
    //   615: aload_2
    //   616: astore_3
    //   617: aload 5
    //   619: lload 6
    //   621: invokevirtual 1080	com/google/android/gms/internal/measurement/zzeb:zzaa	(J)V
    //   624: aload_2
    //   625: astore_3
    //   626: aload_2
    //   627: bipush 23
    //   629: invokeinterface 1035 2 0
    //   634: ifne +16 -> 650
    //   637: aload_2
    //   638: astore_3
    //   639: aload_2
    //   640: bipush 23
    //   642: invokeinterface 1038 2 0
    //   647: ifeq +146 -> 793
    //   650: iconst_1
    //   651: istore 4
    //   653: aload_2
    //   654: astore_3
    //   655: aload 5
    //   657: iload 4
    //   659: invokevirtual 1082	com/google/android/gms/internal/measurement/zzeb:zzd	(Z)V
    //   662: aload_2
    //   663: astore_3
    //   664: aload_2
    //   665: bipush 24
    //   667: invokeinterface 1035 2 0
    //   672: ifne +16 -> 688
    //   675: aload_2
    //   676: astore_3
    //   677: aload_2
    //   678: bipush 24
    //   680: invokeinterface 1038 2 0
    //   685: ifeq +114 -> 799
    //   688: iconst_1
    //   689: istore 4
    //   691: aload_2
    //   692: astore_3
    //   693: aload 5
    //   695: iload 4
    //   697: invokevirtual 1084	com/google/android/gms/internal/measurement/zzeb:zze	(Z)V
    //   700: aload_2
    //   701: astore_3
    //   702: aload 5
    //   704: invokevirtual 1087	com/google/android/gms/internal/measurement/zzeb:zzgj	()V
    //   707: aload_2
    //   708: astore_3
    //   709: aload_2
    //   710: invokeinterface 1090 1 0
    //   715: ifeq +22 -> 737
    //   718: aload_2
    //   719: astore_3
    //   720: aload_0
    //   721: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   724: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   727: ldc_w 1092
    //   730: aload_1
    //   731: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   734: invokevirtual 251	com/google/android/gms/internal/measurement/zzfi:zzg	(Ljava/lang/String;Ljava/lang/Object;)V
    //   737: aload 5
    //   739: astore_1
    //   740: aload_2
    //   741: ifnull -522 -> 219
    //   744: aload_2
    //   745: invokeinterface 209 1 0
    //   750: aload 5
    //   752: astore_1
    //   753: goto -534 -> 219
    //   756: iconst_0
    //   757: istore 4
    //   759: goto -346 -> 413
    //   762: aload_2
    //   763: astore_3
    //   764: aload_2
    //   765: bipush 17
    //   767: invokeinterface 1038 2 0
    //   772: i2l
    //   773: lstore 6
    //   775: goto -245 -> 530
    //   778: aload_2
    //   779: astore_3
    //   780: aload_2
    //   781: bipush 22
    //   783: invokeinterface 206 2 0
    //   788: lstore 6
    //   790: goto -175 -> 615
    //   793: iconst_0
    //   794: istore 4
    //   796: goto -143 -> 653
    //   799: iconst_0
    //   800: istore 4
    //   802: goto -111 -> 691
    //   805: astore 5
    //   807: aconst_null
    //   808: astore_2
    //   809: aload_2
    //   810: astore_3
    //   811: aload_0
    //   812: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   815: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   818: ldc_w 1094
    //   821: aload_1
    //   822: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   825: aload 5
    //   827: invokevirtual 232	com/google/android/gms/internal/measurement/zzfi:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   830: aload_2
    //   831: ifnull +9 -> 840
    //   834: aload_2
    //   835: invokeinterface 209 1 0
    //   840: aconst_null
    //   841: astore_1
    //   842: goto -623 -> 219
    //   845: astore_1
    //   846: aconst_null
    //   847: astore_3
    //   848: aload_3
    //   849: ifnull +9 -> 858
    //   852: aload_3
    //   853: invokeinterface 209 1 0
    //   858: aload_1
    //   859: athrow
    //   860: astore_1
    //   861: goto -13 -> 848
    //   864: astore 5
    //   866: goto -57 -> 809
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	869	0	this	zzei
    //   0	869	1	paramString	String
    //   191	644	2	localCursor1	Cursor
    //   193	660	3	localCursor2	Cursor
    //   200	601	4	bool	boolean
    //   226	525	5	localzzeb	zzeb
    //   805	21	5	localSQLiteException1	SQLiteException
    //   864	1	5	localSQLiteException2	SQLiteException
    //   528	261	6	l	long
    // Exception table:
    //   from	to	target	type
    //   13	192	805	android/database/sqlite/SQLiteException
    //   13	192	845	finally
    //   194	202	860	finally
    //   223	228	860	finally
    //   230	240	860	finally
    //   242	254	860	finally
    //   256	268	860	finally
    //   270	282	860	finally
    //   284	296	860	finally
    //   298	310	860	finally
    //   312	324	860	finally
    //   326	339	860	finally
    //   341	354	860	finally
    //   356	369	860	finally
    //   371	384	860	finally
    //   386	397	860	finally
    //   399	410	860	finally
    //   415	422	860	finally
    //   424	437	860	finally
    //   439	452	860	finally
    //   454	467	860	finally
    //   469	482	860	finally
    //   484	497	860	finally
    //   499	512	860	finally
    //   514	525	860	finally
    //   532	539	860	finally
    //   541	554	860	finally
    //   556	569	860	finally
    //   571	584	860	finally
    //   586	599	860	finally
    //   601	612	860	finally
    //   617	624	860	finally
    //   626	637	860	finally
    //   639	650	860	finally
    //   655	662	860	finally
    //   664	675	860	finally
    //   677	688	860	finally
    //   693	700	860	finally
    //   702	707	860	finally
    //   709	718	860	finally
    //   720	737	860	finally
    //   764	775	860	finally
    //   780	790	860	finally
    //   811	830	860	finally
    //   194	202	864	android/database/sqlite/SQLiteException
    //   223	228	864	android/database/sqlite/SQLiteException
    //   230	240	864	android/database/sqlite/SQLiteException
    //   242	254	864	android/database/sqlite/SQLiteException
    //   256	268	864	android/database/sqlite/SQLiteException
    //   270	282	864	android/database/sqlite/SQLiteException
    //   284	296	864	android/database/sqlite/SQLiteException
    //   298	310	864	android/database/sqlite/SQLiteException
    //   312	324	864	android/database/sqlite/SQLiteException
    //   326	339	864	android/database/sqlite/SQLiteException
    //   341	354	864	android/database/sqlite/SQLiteException
    //   356	369	864	android/database/sqlite/SQLiteException
    //   371	384	864	android/database/sqlite/SQLiteException
    //   386	397	864	android/database/sqlite/SQLiteException
    //   399	410	864	android/database/sqlite/SQLiteException
    //   415	422	864	android/database/sqlite/SQLiteException
    //   424	437	864	android/database/sqlite/SQLiteException
    //   439	452	864	android/database/sqlite/SQLiteException
    //   454	467	864	android/database/sqlite/SQLiteException
    //   469	482	864	android/database/sqlite/SQLiteException
    //   484	497	864	android/database/sqlite/SQLiteException
    //   499	512	864	android/database/sqlite/SQLiteException
    //   514	525	864	android/database/sqlite/SQLiteException
    //   532	539	864	android/database/sqlite/SQLiteException
    //   541	554	864	android/database/sqlite/SQLiteException
    //   556	569	864	android/database/sqlite/SQLiteException
    //   571	584	864	android/database/sqlite/SQLiteException
    //   586	599	864	android/database/sqlite/SQLiteException
    //   601	612	864	android/database/sqlite/SQLiteException
    //   617	624	864	android/database/sqlite/SQLiteException
    //   626	637	864	android/database/sqlite/SQLiteException
    //   639	650	864	android/database/sqlite/SQLiteException
    //   655	662	864	android/database/sqlite/SQLiteException
    //   664	675	864	android/database/sqlite/SQLiteException
    //   677	688	864	android/database/sqlite/SQLiteException
    //   693	700	864	android/database/sqlite/SQLiteException
    //   702	707	864	android/database/sqlite/SQLiteException
    //   709	718	864	android/database/sqlite/SQLiteException
    //   720	737	864	android/database/sqlite/SQLiteException
    //   764	775	864	android/database/sqlite/SQLiteException
    //   780	790	864	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  public final byte[] zzaz(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 283	com/google/android/gms/common/internal/Preconditions:checkNotEmpty	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 415	com/google/android/gms/internal/measurement/zzhj:zzab	()V
    //   9: aload_0
    //   10: invokevirtual 412	com/google/android/gms/internal/measurement/zzhk:zzch	()V
    //   13: aload_0
    //   14: invokevirtual 190	com/google/android/gms/internal/measurement/zzei:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: ldc_w 658
    //   20: iconst_1
    //   21: anewarray 19	java/lang/String
    //   24: dup
    //   25: iconst_0
    //   26: ldc 85
    //   28: aastore
    //   29: ldc_w 660
    //   32: iconst_1
    //   33: anewarray 19	java/lang/String
    //   36: dup
    //   37: iconst_0
    //   38: aload_1
    //   39: aastore
    //   40: aconst_null
    //   41: aconst_null
    //   42: aconst_null
    //   43: invokevirtual 404	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   46: astore_2
    //   47: aload_2
    //   48: astore_3
    //   49: aload_2
    //   50: invokeinterface 202 1 0
    //   55: istore 4
    //   57: iload 4
    //   59: ifne +17 -> 76
    //   62: aload_2
    //   63: ifnull +9 -> 72
    //   66: aload_2
    //   67: invokeinterface 209 1 0
    //   72: aconst_null
    //   73: astore_1
    //   74: aload_1
    //   75: areturn
    //   76: aload_2
    //   77: astore_3
    //   78: aload_2
    //   79: iconst_0
    //   80: invokeinterface 630 2 0
    //   85: astore 5
    //   87: aload_2
    //   88: astore_3
    //   89: aload_2
    //   90: invokeinterface 1090 1 0
    //   95: ifeq +22 -> 117
    //   98: aload_2
    //   99: astore_3
    //   100: aload_0
    //   101: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   104: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   107: ldc_w 1098
    //   110: aload_1
    //   111: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   114: invokevirtual 251	com/google/android/gms/internal/measurement/zzfi:zzg	(Ljava/lang/String;Ljava/lang/Object;)V
    //   117: aload 5
    //   119: astore_1
    //   120: aload_2
    //   121: ifnull -47 -> 74
    //   124: aload_2
    //   125: invokeinterface 209 1 0
    //   130: aload 5
    //   132: astore_1
    //   133: goto -59 -> 74
    //   136: astore 5
    //   138: aconst_null
    //   139: astore_2
    //   140: aload_2
    //   141: astore_3
    //   142: aload_0
    //   143: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   146: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   149: ldc_w 1100
    //   152: aload_1
    //   153: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   156: aload 5
    //   158: invokevirtual 232	com/google/android/gms/internal/measurement/zzfi:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   161: aload_2
    //   162: ifnull +9 -> 171
    //   165: aload_2
    //   166: invokeinterface 209 1 0
    //   171: aconst_null
    //   172: astore_1
    //   173: goto -99 -> 74
    //   176: astore_1
    //   177: aconst_null
    //   178: astore_3
    //   179: aload_3
    //   180: ifnull +9 -> 189
    //   183: aload_3
    //   184: invokeinterface 209 1 0
    //   189: aload_1
    //   190: athrow
    //   191: astore_1
    //   192: goto -13 -> 179
    //   195: astore 5
    //   197: goto -57 -> 140
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	200	0	this	zzei
    //   0	200	1	paramString	String
    //   46	120	2	localCursor1	Cursor
    //   48	136	3	localCursor2	Cursor
    //   55	3	4	bool	boolean
    //   85	46	5	arrayOfByte	byte[]
    //   136	21	5	localSQLiteException1	SQLiteException
    //   195	1	5	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   13	47	136	android/database/sqlite/SQLiteException
    //   13	47	176	finally
    //   49	57	191	finally
    //   78	87	191	finally
    //   89	98	191	finally
    //   100	117	191	finally
    //   142	161	191	finally
    //   49	57	195	android/database/sqlite/SQLiteException
    //   78	87	195	android/database/sqlite/SQLiteException
    //   89	98	195	android/database/sqlite/SQLiteException
    //   100	117	195	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  public final List<android.util.Pair<zzkl, Long>> zzb(String paramString, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore 4
    //   3: aload_0
    //   4: invokevirtual 415	com/google/android/gms/internal/measurement/zzhj:zzab	()V
    //   7: aload_0
    //   8: invokevirtual 412	com/google/android/gms/internal/measurement/zzhk:zzch	()V
    //   11: iload_2
    //   12: ifle +117 -> 129
    //   15: iconst_1
    //   16: istore 5
    //   18: iload 5
    //   20: invokestatic 1104	com/google/android/gms/common/internal/Preconditions:checkArgument	(Z)V
    //   23: iload_3
    //   24: ifle +111 -> 135
    //   27: iload 4
    //   29: istore 5
    //   31: iload 5
    //   33: invokestatic 1104	com/google/android/gms/common/internal/Preconditions:checkArgument	(Z)V
    //   36: aload_1
    //   37: invokestatic 283	com/google/android/gms/common/internal/Preconditions:checkNotEmpty	(Ljava/lang/String;)Ljava/lang/String;
    //   40: pop
    //   41: aload_0
    //   42: invokevirtual 190	com/google/android/gms/internal/measurement/zzei:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   45: ldc_w 946
    //   48: iconst_3
    //   49: anewarray 19	java/lang/String
    //   52: dup
    //   53: iconst_0
    //   54: ldc_w 1106
    //   57: aastore
    //   58: dup
    //   59: iconst_1
    //   60: ldc_w 469
    //   63: aastore
    //   64: dup
    //   65: iconst_2
    //   66: ldc -115
    //   68: aastore
    //   69: ldc_w 660
    //   72: iconst_1
    //   73: anewarray 19	java/lang/String
    //   76: dup
    //   77: iconst_0
    //   78: aload_1
    //   79: aastore
    //   80: aconst_null
    //   81: aconst_null
    //   82: ldc_w 1106
    //   85: iload_2
    //   86: invokestatic 864	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   89: invokevirtual 1109	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   92: astore 6
    //   94: aload 6
    //   96: invokeinterface 202 1 0
    //   101: ifne +40 -> 141
    //   104: invokestatic 1113	java/util/Collections:emptyList	()Ljava/util/List;
    //   107: astore 7
    //   109: aload 7
    //   111: astore_1
    //   112: aload 6
    //   114: ifnull +13 -> 127
    //   117: aload 6
    //   119: invokeinterface 209 1 0
    //   124: aload 7
    //   126: astore_1
    //   127: aload_1
    //   128: areturn
    //   129: iconst_0
    //   130: istore 5
    //   132: goto -114 -> 18
    //   135: iconst_0
    //   136: istore 5
    //   138: goto -107 -> 31
    //   141: new 538	java/util/ArrayList
    //   144: astore 7
    //   146: aload 7
    //   148: invokespecial 539	java/util/ArrayList:<init>	()V
    //   151: iconst_0
    //   152: istore_2
    //   153: aload 6
    //   155: iconst_0
    //   156: invokeinterface 206 2 0
    //   161: lstore 8
    //   163: aload 6
    //   165: iconst_1
    //   166: invokeinterface 630 2 0
    //   171: astore 10
    //   173: aload_0
    //   174: invokevirtual 934	com/google/android/gms/internal/measurement/zzhj:zzgc	()Lcom/google/android/gms/internal/measurement/zzjv;
    //   177: aload 10
    //   179: invokevirtual 1115	com/google/android/gms/internal/measurement/zzjv:zzb	([B)[B
    //   182: astore 11
    //   184: aload 7
    //   186: invokeinterface 1116 1 0
    //   191: ifne +12 -> 203
    //   194: aload 11
    //   196: arraylength
    //   197: iload_2
    //   198: iadd
    //   199: iload_3
    //   200: if_icmpgt +102 -> 302
    //   203: aload 11
    //   205: iconst_0
    //   206: aload 11
    //   208: arraylength
    //   209: invokestatic 635	com/google/android/gms/internal/measurement/zzaba:zza	([BII)Lcom/google/android/gms/internal/measurement/zzaba;
    //   212: astore 12
    //   214: new 910	com/google/android/gms/internal/measurement/zzkl
    //   217: astore 10
    //   219: aload 10
    //   221: invokespecial 1117	com/google/android/gms/internal/measurement/zzkl:<init>	()V
    //   224: aload 10
    //   226: aload 12
    //   228: invokevirtual 641	com/google/android/gms/internal/measurement/zzabj:zzb	(Lcom/google/android/gms/internal/measurement/zzaba;)Lcom/google/android/gms/internal/measurement/zzabj;
    //   231: pop
    //   232: aload 6
    //   234: iconst_2
    //   235: invokeinterface 1035 2 0
    //   240: ifne +19 -> 259
    //   243: aload 10
    //   245: aload 6
    //   247: iconst_2
    //   248: invokeinterface 1038 2 0
    //   253: invokestatic 247	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   256: putfield 944	com/google/android/gms/internal/measurement/zzkl:zzaue	Ljava/lang/Integer;
    //   259: aload 11
    //   261: arraylength
    //   262: iload_2
    //   263: iadd
    //   264: istore_2
    //   265: aload 7
    //   267: aload 10
    //   269: lload 8
    //   271: invokestatic 261	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   274: invokestatic 647	android/util/Pair:create	(Ljava/lang/Object;Ljava/lang/Object;)Landroid/util/Pair;
    //   277: invokeinterface 556 2 0
    //   282: pop
    //   283: aload 6
    //   285: invokeinterface 1090 1 0
    //   290: istore 5
    //   292: iload 5
    //   294: ifeq +8 -> 302
    //   297: iload_2
    //   298: iload_3
    //   299: if_icmple +153 -> 452
    //   302: aload 7
    //   304: astore_1
    //   305: aload 6
    //   307: ifnull -180 -> 127
    //   310: aload 6
    //   312: invokeinterface 209 1 0
    //   317: aload 7
    //   319: astore_1
    //   320: goto -193 -> 127
    //   323: astore 10
    //   325: aload_0
    //   326: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   329: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   332: ldc_w 1119
    //   335: aload_1
    //   336: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   339: aload 10
    //   341: invokevirtual 232	com/google/android/gms/internal/measurement/zzfi:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   344: goto -61 -> 283
    //   347: astore 10
    //   349: aload_0
    //   350: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   353: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   356: ldc_w 1121
    //   359: aload_1
    //   360: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   363: aload 10
    //   365: invokevirtual 232	com/google/android/gms/internal/measurement/zzfi:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   368: goto -85 -> 283
    //   371: astore 7
    //   373: aconst_null
    //   374: astore 6
    //   376: aload_0
    //   377: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   380: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   383: ldc_w 1123
    //   386: aload_1
    //   387: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   390: aload 7
    //   392: invokevirtual 232	com/google/android/gms/internal/measurement/zzfi:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   395: invokestatic 1113	java/util/Collections:emptyList	()Ljava/util/List;
    //   398: astore 7
    //   400: aload 7
    //   402: astore_1
    //   403: aload 6
    //   405: ifnull -278 -> 127
    //   408: aload 6
    //   410: invokeinterface 209 1 0
    //   415: aload 7
    //   417: astore_1
    //   418: goto -291 -> 127
    //   421: astore_1
    //   422: aconst_null
    //   423: astore 6
    //   425: aload 6
    //   427: ifnull +10 -> 437
    //   430: aload 6
    //   432: invokeinterface 209 1 0
    //   437: aload_1
    //   438: athrow
    //   439: astore_1
    //   440: goto -15 -> 425
    //   443: astore_1
    //   444: goto -19 -> 425
    //   447: astore 7
    //   449: goto -73 -> 376
    //   452: goto -299 -> 153
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	455	0	this	zzei
    //   0	455	1	paramString	String
    //   0	455	2	paramInt1	int
    //   0	455	3	paramInt2	int
    //   1	27	4	bool1	boolean
    //   16	277	5	bool2	boolean
    //   92	339	6	localCursor	Cursor
    //   107	211	7	localObject1	Object
    //   371	20	7	localSQLiteException1	SQLiteException
    //   398	18	7	localList	List
    //   447	1	7	localSQLiteException2	SQLiteException
    //   161	109	8	l	long
    //   171	97	10	localObject2	Object
    //   323	17	10	localIOException1	IOException
    //   347	17	10	localIOException2	IOException
    //   182	78	11	arrayOfByte	byte[]
    //   212	15	12	localzzaba	zzaba
    // Exception table:
    //   from	to	target	type
    //   163	184	323	java/io/IOException
    //   224	232	347	java/io/IOException
    //   41	94	371	android/database/sqlite/SQLiteException
    //   41	94	421	finally
    //   94	109	439	finally
    //   141	151	439	finally
    //   153	163	439	finally
    //   163	184	439	finally
    //   184	203	439	finally
    //   203	224	439	finally
    //   224	232	439	finally
    //   232	259	439	finally
    //   259	283	439	finally
    //   283	292	439	finally
    //   325	344	439	finally
    //   349	368	439	finally
    //   376	400	443	finally
    //   94	109	447	android/database/sqlite/SQLiteException
    //   141	151	447	android/database/sqlite/SQLiteException
    //   153	163	447	android/database/sqlite/SQLiteException
    //   163	184	447	android/database/sqlite/SQLiteException
    //   184	203	447	android/database/sqlite/SQLiteException
    //   203	224	447	android/database/sqlite/SQLiteException
    //   224	232	447	android/database/sqlite/SQLiteException
    //   232	259	447	android/database/sqlite/SQLiteException
    //   259	283	447	android/database/sqlite/SQLiteException
    //   283	292	447	android/database/sqlite/SQLiteException
    //   325	344	447	android/database/sqlite/SQLiteException
    //   349	368	447	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  final java.util.Map<Integer, zzkm> zzba(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 412	com/google/android/gms/internal/measurement/zzhk:zzch	()V
    //   4: aload_0
    //   5: invokevirtual 415	com/google/android/gms/internal/measurement/zzhj:zzab	()V
    //   8: aload_1
    //   9: invokestatic 283	com/google/android/gms/common/internal/Preconditions:checkNotEmpty	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 190	com/google/android/gms/internal/measurement/zzei:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: astore_2
    //   18: aload_2
    //   19: ldc_w 562
    //   22: iconst_2
    //   23: anewarray 19	java/lang/String
    //   26: dup
    //   27: iconst_0
    //   28: ldc_w 460
    //   31: aastore
    //   32: dup
    //   33: iconst_1
    //   34: ldc_w 1128
    //   37: aastore
    //   38: ldc_w 660
    //   41: iconst_1
    //   42: anewarray 19	java/lang/String
    //   45: dup
    //   46: iconst_0
    //   47: aload_1
    //   48: aastore
    //   49: aconst_null
    //   50: aconst_null
    //   51: aconst_null
    //   52: invokevirtual 404	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   55: astore_3
    //   56: aload_3
    //   57: astore_2
    //   58: aload_3
    //   59: invokeinterface 202 1 0
    //   64: istore 4
    //   66: iload 4
    //   68: ifne +17 -> 85
    //   71: aload_3
    //   72: ifnull +9 -> 81
    //   75: aload_3
    //   76: invokeinterface 209 1 0
    //   81: aconst_null
    //   82: astore_1
    //   83: aload_1
    //   84: areturn
    //   85: aload_3
    //   86: astore_2
    //   87: new 1130	android/support/v4/util/ArrayMap
    //   90: astore 5
    //   92: aload_3
    //   93: astore_2
    //   94: aload 5
    //   96: invokespecial 1131	android/support/v4/util/ArrayMap:<init>	()V
    //   99: aload_3
    //   100: astore_2
    //   101: aload_3
    //   102: iconst_0
    //   103: invokeinterface 1038 2 0
    //   108: istore 6
    //   110: aload_3
    //   111: astore_2
    //   112: aload_3
    //   113: iconst_1
    //   114: invokeinterface 630 2 0
    //   119: astore 7
    //   121: aload_3
    //   122: astore_2
    //   123: aload 7
    //   125: iconst_0
    //   126: aload 7
    //   128: arraylength
    //   129: invokestatic 635	com/google/android/gms/internal/measurement/zzaba:zza	([BII)Lcom/google/android/gms/internal/measurement/zzaba;
    //   132: astore 8
    //   134: aload_3
    //   135: astore_2
    //   136: new 1133	com/google/android/gms/internal/measurement/zzkm
    //   139: astore 7
    //   141: aload_3
    //   142: astore_2
    //   143: aload 7
    //   145: invokespecial 1134	com/google/android/gms/internal/measurement/zzkm:<init>	()V
    //   148: aload_3
    //   149: astore_2
    //   150: aload 7
    //   152: aload 8
    //   154: invokevirtual 641	com/google/android/gms/internal/measurement/zzabj:zzb	(Lcom/google/android/gms/internal/measurement/zzaba;)Lcom/google/android/gms/internal/measurement/zzabj;
    //   157: pop
    //   158: aload_3
    //   159: astore_2
    //   160: aload 5
    //   162: iload 6
    //   164: invokestatic 247	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   167: aload 7
    //   169: invokeinterface 1139 3 0
    //   174: pop
    //   175: aload_3
    //   176: astore_2
    //   177: aload_3
    //   178: invokeinterface 1090 1 0
    //   183: istore 4
    //   185: iload 4
    //   187: ifne -88 -> 99
    //   190: aload 5
    //   192: astore_1
    //   193: aload_3
    //   194: ifnull -111 -> 83
    //   197: aload_3
    //   198: invokeinterface 209 1 0
    //   203: aload 5
    //   205: astore_1
    //   206: goto -123 -> 83
    //   209: astore 7
    //   211: aload_3
    //   212: astore_2
    //   213: aload_0
    //   214: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   217: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   220: ldc_w 1141
    //   223: aload_1
    //   224: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   227: iload 6
    //   229: invokestatic 247	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   232: aload 7
    //   234: invokevirtual 438	com/google/android/gms/internal/measurement/zzfi:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   237: goto -62 -> 175
    //   240: astore 5
    //   242: aload_3
    //   243: astore_2
    //   244: aload_0
    //   245: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   248: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   251: ldc_w 1143
    //   254: aload_1
    //   255: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   258: aload 5
    //   260: invokevirtual 232	com/google/android/gms/internal/measurement/zzfi:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   263: aload_3
    //   264: ifnull +9 -> 273
    //   267: aload_3
    //   268: invokeinterface 209 1 0
    //   273: aconst_null
    //   274: astore_1
    //   275: goto -192 -> 83
    //   278: astore_1
    //   279: aconst_null
    //   280: astore_2
    //   281: aload_2
    //   282: ifnull +9 -> 291
    //   285: aload_2
    //   286: invokeinterface 209 1 0
    //   291: aload_1
    //   292: athrow
    //   293: astore_1
    //   294: goto -13 -> 281
    //   297: astore 5
    //   299: aconst_null
    //   300: astore_3
    //   301: goto -59 -> 242
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	304	0	this	zzei
    //   0	304	1	paramString	String
    //   17	269	2	localObject1	Object
    //   55	246	3	localCursor	Cursor
    //   64	122	4	bool	boolean
    //   90	114	5	localArrayMap	android.support.v4.util.ArrayMap
    //   240	19	5	localSQLiteException1	SQLiteException
    //   297	1	5	localSQLiteException2	SQLiteException
    //   108	120	6	i	int
    //   119	49	7	localObject2	Object
    //   209	24	7	localIOException	IOException
    //   132	21	8	localzzaba	zzaba
    // Exception table:
    //   from	to	target	type
    //   150	158	209	java/io/IOException
    //   58	66	240	android/database/sqlite/SQLiteException
    //   87	92	240	android/database/sqlite/SQLiteException
    //   94	99	240	android/database/sqlite/SQLiteException
    //   101	110	240	android/database/sqlite/SQLiteException
    //   112	121	240	android/database/sqlite/SQLiteException
    //   123	134	240	android/database/sqlite/SQLiteException
    //   136	141	240	android/database/sqlite/SQLiteException
    //   143	148	240	android/database/sqlite/SQLiteException
    //   150	158	240	android/database/sqlite/SQLiteException
    //   160	175	240	android/database/sqlite/SQLiteException
    //   177	185	240	android/database/sqlite/SQLiteException
    //   213	237	240	android/database/sqlite/SQLiteException
    //   18	56	278	finally
    //   58	66	293	finally
    //   87	92	293	finally
    //   94	99	293	finally
    //   101	110	293	finally
    //   112	121	293	finally
    //   123	134	293	finally
    //   136	141	293	finally
    //   143	148	293	finally
    //   150	158	293	finally
    //   160	175	293	finally
    //   177	185	293	finally
    //   213	237	293	finally
    //   244	263	293	finally
    //   18	56	297	android/database/sqlite/SQLiteException
  }
  
  final void zzc(List<Long> paramList)
  {
    zzab();
    zzch();
    Preconditions.checkNotNull(paramList);
    Preconditions.checkNotZero(paramList.size());
    if (!zzhv()) {}
    for (;;)
    {
      return;
      paramList = TextUtils.join(",", paramList);
      paramList = String.valueOf(paramList).length() + 2 + "(" + paramList + ")";
      if (zza(String.valueOf(paramList).length() + 80 + "SELECT COUNT(1) FROM queue WHERE rowid IN " + paramList + " AND retry_count =  NUM LIMIT 1", null) > 0L) {
        zzgg().zzin().log("The number of upload retries exceeds the limit. Will remain unchanged.");
      }
      try
      {
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        int i = String.valueOf(paramList).length();
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>(i + 127);
        localSQLiteDatabase.execSQL("UPDATE queue SET retry_count = IFNULL(retry_count, 0) + 1 WHERE rowid IN " + paramList + " AND (retry_count IS NULL OR retry_count < NUM)");
      }
      catch (SQLiteException paramList)
      {
        zzgg().zzil().zzg("Error incrementing retry count. error", paramList);
      }
    }
  }
  
  /* Error */
  public final zzeq zze(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 283	com/google/android/gms/common/internal/Preconditions:checkNotEmpty	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_2
    //   6: invokestatic 283	com/google/android/gms/common/internal/Preconditions:checkNotEmpty	(Ljava/lang/String;)Ljava/lang/String;
    //   9: pop
    //   10: aload_0
    //   11: invokevirtual 415	com/google/android/gms/internal/measurement/zzhj:zzab	()V
    //   14: aload_0
    //   15: invokevirtual 412	com/google/android/gms/internal/measurement/zzhk:zzch	()V
    //   18: aload_0
    //   19: invokevirtual 190	com/google/android/gms/internal/measurement/zzei:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
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
    //   69: ldc_w 1167
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
    //   87: invokevirtual 404	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   90: astore_3
    //   91: aload_3
    //   92: invokeinterface 202 1 0
    //   97: istore 4
    //   99: iload 4
    //   101: ifne +17 -> 118
    //   104: aload_3
    //   105: ifnull +9 -> 114
    //   108: aload_3
    //   109: invokeinterface 209 1 0
    //   114: aconst_null
    //   115: astore_1
    //   116: aload_1
    //   117: areturn
    //   118: aload_3
    //   119: iconst_0
    //   120: invokeinterface 206 2 0
    //   125: lstore 5
    //   127: aload_3
    //   128: iconst_1
    //   129: invokeinterface 206 2 0
    //   134: lstore 7
    //   136: aload_3
    //   137: iconst_2
    //   138: invokeinterface 206 2 0
    //   143: lstore 9
    //   145: aload_3
    //   146: iconst_3
    //   147: invokeinterface 1035 2 0
    //   152: ifeq +140 -> 292
    //   155: lconst_0
    //   156: lstore 11
    //   158: aload_3
    //   159: iconst_4
    //   160: invokeinterface 1035 2 0
    //   165: ifeq +139 -> 304
    //   168: aconst_null
    //   169: astore 13
    //   171: aload_3
    //   172: iconst_5
    //   173: invokeinterface 1035 2 0
    //   178: ifeq +141 -> 319
    //   181: aconst_null
    //   182: astore 14
    //   184: aconst_null
    //   185: astore 15
    //   187: aload_3
    //   188: bipush 6
    //   190: invokeinterface 1035 2 0
    //   195: ifne +26 -> 221
    //   198: aload_3
    //   199: bipush 6
    //   201: invokeinterface 206 2 0
    //   206: lconst_1
    //   207: lcmp
    //   208: ifne +130 -> 338
    //   211: iconst_1
    //   212: istore 4
    //   214: iload 4
    //   216: invokestatic 739	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   219: astore 15
    //   221: new 793	com/google/android/gms/internal/measurement/zzeq
    //   224: astore 16
    //   226: aload 16
    //   228: aload_1
    //   229: aload_2
    //   230: lload 5
    //   232: lload 7
    //   234: lload 9
    //   236: lload 11
    //   238: aload 13
    //   240: aload 14
    //   242: aload 15
    //   244: invokespecial 1170	com/google/android/gms/internal/measurement/zzeq:<init>	(Ljava/lang/String;Ljava/lang/String;JJJJLjava/lang/Long;Ljava/lang/Long;Ljava/lang/Boolean;)V
    //   247: aload_3
    //   248: invokeinterface 1090 1 0
    //   253: ifeq +20 -> 273
    //   256: aload_0
    //   257: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   260: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   263: ldc_w 1172
    //   266: aload_1
    //   267: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   270: invokevirtual 251	com/google/android/gms/internal/measurement/zzfi:zzg	(Ljava/lang/String;Ljava/lang/Object;)V
    //   273: aload 16
    //   275: astore_1
    //   276: aload_3
    //   277: ifnull -161 -> 116
    //   280: aload_3
    //   281: invokeinterface 209 1 0
    //   286: aload 16
    //   288: astore_1
    //   289: goto -173 -> 116
    //   292: aload_3
    //   293: iconst_3
    //   294: invokeinterface 206 2 0
    //   299: lstore 11
    //   301: goto -143 -> 158
    //   304: aload_3
    //   305: iconst_4
    //   306: invokeinterface 206 2 0
    //   311: invokestatic 261	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   314: astore 13
    //   316: goto -145 -> 171
    //   319: aload_3
    //   320: iconst_5
    //   321: invokeinterface 206 2 0
    //   326: lstore 17
    //   328: lload 17
    //   330: invokestatic 261	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   333: astore 14
    //   335: goto -151 -> 184
    //   338: iconst_0
    //   339: istore 4
    //   341: goto -127 -> 214
    //   344: astore 13
    //   346: aconst_null
    //   347: astore_3
    //   348: aload_0
    //   349: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   352: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   355: ldc_w 1174
    //   358: aload_1
    //   359: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   362: aload_0
    //   363: invokevirtual 963	com/google/android/gms/internal/measurement/zzhj:zzgb	()Lcom/google/android/gms/internal/measurement/zzfe;
    //   366: aload_2
    //   367: invokevirtual 968	com/google/android/gms/internal/measurement/zzfe:zzbe	(Ljava/lang/String;)Ljava/lang/String;
    //   370: aload 13
    //   372: invokevirtual 438	com/google/android/gms/internal/measurement/zzfi:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   375: aload_3
    //   376: ifnull +9 -> 385
    //   379: aload_3
    //   380: invokeinterface 209 1 0
    //   385: aconst_null
    //   386: astore_1
    //   387: goto -271 -> 116
    //   390: astore_1
    //   391: aconst_null
    //   392: astore_3
    //   393: aload_3
    //   394: ifnull +9 -> 403
    //   397: aload_3
    //   398: invokeinterface 209 1 0
    //   403: aload_1
    //   404: athrow
    //   405: astore_1
    //   406: goto -13 -> 393
    //   409: astore_1
    //   410: goto -17 -> 393
    //   413: astore 13
    //   415: goto -67 -> 348
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	418	0	this	zzei
    //   0	418	1	paramString1	String
    //   0	418	2	paramString2	String
    //   90	308	3	localCursor	Cursor
    //   97	243	4	bool	boolean
    //   125	106	5	l1	long
    //   134	99	7	l2	long
    //   143	92	9	l3	long
    //   156	144	11	l4	long
    //   169	146	13	localLong1	Long
    //   344	27	13	localSQLiteException1	SQLiteException
    //   413	1	13	localSQLiteException2	SQLiteException
    //   182	152	14	localLong2	Long
    //   185	58	15	localBoolean	Boolean
    //   224	63	16	localzzeq	zzeq
    //   326	3	17	l5	long
    // Exception table:
    //   from	to	target	type
    //   18	91	344	android/database/sqlite/SQLiteException
    //   18	91	390	finally
    //   91	99	405	finally
    //   118	155	405	finally
    //   158	168	405	finally
    //   171	181	405	finally
    //   187	211	405	finally
    //   214	221	405	finally
    //   221	273	405	finally
    //   292	301	405	finally
    //   304	316	405	finally
    //   319	328	405	finally
    //   348	375	409	finally
    //   91	99	413	android/database/sqlite/SQLiteException
    //   118	155	413	android/database/sqlite/SQLiteException
    //   158	168	413	android/database/sqlite/SQLiteException
    //   171	181	413	android/database/sqlite/SQLiteException
    //   187	211	413	android/database/sqlite/SQLiteException
    //   214	221	413	android/database/sqlite/SQLiteException
    //   221	273	413	android/database/sqlite/SQLiteException
    //   292	301	413	android/database/sqlite/SQLiteException
    //   304	316	413	android/database/sqlite/SQLiteException
    //   319	328	413	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  public final zzju zzg(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aload_1
    //   3: invokestatic 283	com/google/android/gms/common/internal/Preconditions:checkNotEmpty	(Ljava/lang/String;)Ljava/lang/String;
    //   6: pop
    //   7: aload_2
    //   8: invokestatic 283	com/google/android/gms/common/internal/Preconditions:checkNotEmpty	(Ljava/lang/String;)Ljava/lang/String;
    //   11: pop
    //   12: aload_0
    //   13: invokevirtual 415	com/google/android/gms/internal/measurement/zzhj:zzab	()V
    //   16: aload_0
    //   17: invokevirtual 412	com/google/android/gms/internal/measurement/zzhk:zzch	()V
    //   20: aload_0
    //   21: invokevirtual 190	com/google/android/gms/internal/measurement/zzei:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   24: ldc_w 903
    //   27: iconst_3
    //   28: anewarray 19	java/lang/String
    //   31: dup
    //   32: iconst_0
    //   33: ldc_w 891
    //   36: aastore
    //   37: dup
    //   38: iconst_1
    //   39: ldc_w 896
    //   42: aastore
    //   43: dup
    //   44: iconst_2
    //   45: ldc 39
    //   47: aastore
    //   48: ldc_w 1167
    //   51: iconst_2
    //   52: anewarray 19	java/lang/String
    //   55: dup
    //   56: iconst_0
    //   57: aload_1
    //   58: aastore
    //   59: dup
    //   60: iconst_1
    //   61: aload_2
    //   62: aastore
    //   63: aconst_null
    //   64: aconst_null
    //   65: aconst_null
    //   66: invokevirtual 404	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   69: astore 4
    //   71: aload 4
    //   73: invokeinterface 202 1 0
    //   78: istore 5
    //   80: iload 5
    //   82: ifne +19 -> 101
    //   85: aload 4
    //   87: ifnull +10 -> 97
    //   90: aload 4
    //   92: invokeinterface 209 1 0
    //   97: aconst_null
    //   98: astore_1
    //   99: aload_1
    //   100: areturn
    //   101: aload 4
    //   103: iconst_0
    //   104: invokeinterface 206 2 0
    //   109: lstore 6
    //   111: aload_0
    //   112: aload 4
    //   114: iconst_1
    //   115: invokespecial 1176	com/google/android/gms/internal/measurement/zzei:zza	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   118: astore 8
    //   120: aload 4
    //   122: iconst_2
    //   123: invokeinterface 274 2 0
    //   128: astore 9
    //   130: new 869	com/google/android/gms/internal/measurement/zzju
    //   133: astore_3
    //   134: aload_3
    //   135: aload_1
    //   136: aload 9
    //   138: aload_2
    //   139: lload 6
    //   141: aload 8
    //   143: invokespecial 1179	com/google/android/gms/internal/measurement/zzju:<init>	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   146: aload 4
    //   148: invokeinterface 1090 1 0
    //   153: ifeq +20 -> 173
    //   156: aload_0
    //   157: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   160: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   163: ldc_w 1181
    //   166: aload_1
    //   167: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   170: invokevirtual 251	com/google/android/gms/internal/measurement/zzfi:zzg	(Ljava/lang/String;Ljava/lang/Object;)V
    //   173: aload_3
    //   174: astore_1
    //   175: aload 4
    //   177: ifnull -78 -> 99
    //   180: aload 4
    //   182: invokeinterface 209 1 0
    //   187: aload_3
    //   188: astore_1
    //   189: goto -90 -> 99
    //   192: astore_3
    //   193: aconst_null
    //   194: astore 4
    //   196: aload_0
    //   197: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   200: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   203: ldc_w 1183
    //   206: aload_1
    //   207: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   210: aload_0
    //   211: invokevirtual 963	com/google/android/gms/internal/measurement/zzhj:zzgb	()Lcom/google/android/gms/internal/measurement/zzfe;
    //   214: aload_2
    //   215: invokevirtual 1186	com/google/android/gms/internal/measurement/zzfe:zzbg	(Ljava/lang/String;)Ljava/lang/String;
    //   218: aload_3
    //   219: invokevirtual 438	com/google/android/gms/internal/measurement/zzfi:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   222: aload 4
    //   224: ifnull +10 -> 234
    //   227: aload 4
    //   229: invokeinterface 209 1 0
    //   234: aconst_null
    //   235: astore_1
    //   236: goto -137 -> 99
    //   239: astore_1
    //   240: aload_3
    //   241: astore_2
    //   242: aload_2
    //   243: ifnull +9 -> 252
    //   246: aload_2
    //   247: invokeinterface 209 1 0
    //   252: aload_1
    //   253: athrow
    //   254: astore_1
    //   255: aload 4
    //   257: astore_2
    //   258: goto -16 -> 242
    //   261: astore_1
    //   262: aload 4
    //   264: astore_2
    //   265: goto -23 -> 242
    //   268: astore_3
    //   269: goto -73 -> 196
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	272	0	this	zzei
    //   0	272	1	paramString1	String
    //   0	272	2	paramString2	String
    //   1	187	3	localzzju	zzju
    //   192	49	3	localSQLiteException1	SQLiteException
    //   268	1	3	localSQLiteException2	SQLiteException
    //   69	194	4	localCursor	Cursor
    //   78	3	5	bool	boolean
    //   109	31	6	l	long
    //   118	24	8	localObject	Object
    //   128	9	9	str	String
    // Exception table:
    //   from	to	target	type
    //   20	71	192	android/database/sqlite/SQLiteException
    //   20	71	239	finally
    //   71	80	254	finally
    //   101	173	254	finally
    //   196	222	261	finally
    //   71	80	268	android/database/sqlite/SQLiteException
    //   101	173	268	android/database/sqlite/SQLiteException
  }
  
  protected final boolean zzhh()
  {
    return false;
  }
  
  /* Error */
  public final String zzhn()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: aload_0
    //   3: invokevirtual 190	com/google/android/gms/internal/measurement/zzei:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   6: astore_2
    //   7: aload_2
    //   8: ldc_w 1190
    //   11: aconst_null
    //   12: invokevirtual 196	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   15: astore_3
    //   16: aload_3
    //   17: astore_2
    //   18: aload_3
    //   19: invokeinterface 202 1 0
    //   24: ifeq +32 -> 56
    //   27: aload_3
    //   28: astore_2
    //   29: aload_3
    //   30: iconst_0
    //   31: invokeinterface 274 2 0
    //   36: astore 4
    //   38: aload 4
    //   40: astore_2
    //   41: aload_3
    //   42: ifnull +12 -> 54
    //   45: aload_3
    //   46: invokeinterface 209 1 0
    //   51: aload 4
    //   53: astore_2
    //   54: aload_2
    //   55: areturn
    //   56: aload_1
    //   57: astore_2
    //   58: aload_3
    //   59: ifnull -5 -> 54
    //   62: aload_3
    //   63: invokeinterface 209 1 0
    //   68: aload_1
    //   69: astore_2
    //   70: goto -16 -> 54
    //   73: astore 4
    //   75: aconst_null
    //   76: astore_3
    //   77: aload_3
    //   78: astore_2
    //   79: aload_0
    //   80: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   83: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   86: ldc_w 1192
    //   89: aload 4
    //   91: invokevirtual 251	com/google/android/gms/internal/measurement/zzfi:zzg	(Ljava/lang/String;Ljava/lang/Object;)V
    //   94: aload_1
    //   95: astore_2
    //   96: aload_3
    //   97: ifnull -43 -> 54
    //   100: aload_3
    //   101: invokeinterface 209 1 0
    //   106: aload_1
    //   107: astore_2
    //   108: goto -54 -> 54
    //   111: astore_3
    //   112: aconst_null
    //   113: astore_2
    //   114: aload_2
    //   115: ifnull +9 -> 124
    //   118: aload_2
    //   119: invokeinterface 209 1 0
    //   124: aload_3
    //   125: athrow
    //   126: astore_3
    //   127: goto -13 -> 114
    //   130: astore 4
    //   132: goto -55 -> 77
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	135	0	this	zzei
    //   1	106	1	localObject1	Object
    //   6	113	2	localObject2	Object
    //   15	86	3	localCursor	Cursor
    //   111	14	3	localObject3	Object
    //   126	1	3	localObject4	Object
    //   36	16	4	str	String
    //   73	17	4	localSQLiteException1	SQLiteException
    //   130	1	4	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   7	16	73	android/database/sqlite/SQLiteException
    //   7	16	111	finally
    //   18	27	126	finally
    //   29	38	126	finally
    //   79	94	126	finally
    //   18	27	130	android/database/sqlite/SQLiteException
    //   29	38	130	android/database/sqlite/SQLiteException
  }
  
  public final boolean zzho()
  {
    if (zza("select count(1) > 0 from queue where has_realtime = 1", null) != 0L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  final void zzhp()
  {
    zzab();
    zzch();
    if (!zzhv()) {}
    for (;;)
    {
      return;
      long l1 = zzgh().zzajw.get();
      long l2 = zzbt().elapsedRealtime();
      if (Math.abs(l2 - l1) > ((Long)zzew.zzahj.get()).longValue())
      {
        zzgh().zzajw.set(l2);
        zzab();
        zzch();
        if (zzhv())
        {
          int i = getWritableDatabase().delete("queue", "abs(bundle_end_timestamp - ?) > cast(? as integer)", new String[] { String.valueOf(zzbt().currentTimeMillis()), String.valueOf(zzeh.zzhj()) });
          if (i > 0) {
            zzgg().zzir().zzg("Deleted stale rows. rowsDeleted", Integer.valueOf(i));
          }
        }
      }
    }
  }
  
  public final long zzhq()
  {
    return zza("select max(bundle_end_timestamp) from queue", null, 0L);
  }
  
  public final long zzhr()
  {
    return zza("select max(timestamp) from raw_events", null, 0L);
  }
  
  public final boolean zzhs()
  {
    if (zza("select count(1) > 0 from raw_events", null) != 0L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final boolean zzht()
  {
    if (zza("select count(1) > 0 from raw_events where realtime = 1", null) != 0L) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final long zzhu()
  {
    l1 = -1L;
    localObject1 = null;
    localObject2 = null;
    for (;;)
    {
      try
      {
        localCursor = getWritableDatabase().rawQuery("select rowid from raw_events order by rowid desc limit 1;", null);
        localObject2 = localCursor;
        localObject1 = localCursor;
        boolean bool = localCursor.moveToFirst();
        if (bool) {
          continue;
        }
        l2 = l1;
        if (localCursor != null)
        {
          localCursor.close();
          l2 = l1;
        }
      }
      catch (SQLiteException localSQLiteException)
      {
        Cursor localCursor;
        localObject1 = localObject2;
        zzgg().zzil().zzg("Error querying raw events", localSQLiteException);
        long l2 = l1;
        if (localObject2 == null) {
          continue;
        }
        ((Cursor)localObject2).close();
        l2 = l1;
        continue;
      }
      finally
      {
        if (localObject1 == null) {
          continue;
        }
        ((Cursor)localObject1).close();
      }
      return l2;
      localObject2 = localCursor;
      localObject1 = localCursor;
      l2 = localCursor.getLong(0);
      l1 = l2;
      l2 = l1;
      if (localCursor != null)
      {
        localCursor.close();
        l2 = l1;
      }
    }
  }
  
  /* Error */
  final java.util.Map<Integer, List<zzjz>> zzj(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 412	com/google/android/gms/internal/measurement/zzhk:zzch	()V
    //   4: aload_0
    //   5: invokevirtual 415	com/google/android/gms/internal/measurement/zzhj:zzab	()V
    //   8: aload_1
    //   9: invokestatic 283	com/google/android/gms/common/internal/Preconditions:checkNotEmpty	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_2
    //   14: invokestatic 283	com/google/android/gms/common/internal/Preconditions:checkNotEmpty	(Ljava/lang/String;)Ljava/lang/String;
    //   17: pop
    //   18: new 1130	android/support/v4/util/ArrayMap
    //   21: dup
    //   22: invokespecial 1131	android/support/v4/util/ArrayMap:<init>	()V
    //   25: astore_3
    //   26: aload_0
    //   27: invokevirtual 190	com/google/android/gms/internal/measurement/zzei:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   30: astore 4
    //   32: aload 4
    //   34: ldc_w 474
    //   37: iconst_2
    //   38: anewarray 19	java/lang/String
    //   41: dup
    //   42: iconst_0
    //   43: ldc_w 460
    //   46: aastore
    //   47: dup
    //   48: iconst_1
    //   49: ldc_w 469
    //   52: aastore
    //   53: ldc_w 1254
    //   56: iconst_2
    //   57: anewarray 19	java/lang/String
    //   60: dup
    //   61: iconst_0
    //   62: aload_1
    //   63: aastore
    //   64: dup
    //   65: iconst_1
    //   66: aload_2
    //   67: aastore
    //   68: aconst_null
    //   69: aconst_null
    //   70: aconst_null
    //   71: invokevirtual 404	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   74: astore 4
    //   76: aload 4
    //   78: astore_2
    //   79: aload 4
    //   81: invokeinterface 202 1 0
    //   86: ifne +32 -> 118
    //   89: aload 4
    //   91: astore_2
    //   92: invokestatic 1258	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   95: astore 5
    //   97: aload 5
    //   99: astore_2
    //   100: aload_2
    //   101: astore_1
    //   102: aload 4
    //   104: ifnull +12 -> 116
    //   107: aload 4
    //   109: invokeinterface 209 1 0
    //   114: aload_2
    //   115: astore_1
    //   116: aload_1
    //   117: areturn
    //   118: aload 4
    //   120: astore_2
    //   121: aload 4
    //   123: iconst_1
    //   124: invokeinterface 630 2 0
    //   129: astore 5
    //   131: aload 4
    //   133: astore_2
    //   134: aload 5
    //   136: iconst_0
    //   137: aload 5
    //   139: arraylength
    //   140: invokestatic 635	com/google/android/gms/internal/measurement/zzaba:zza	([BII)Lcom/google/android/gms/internal/measurement/zzaba;
    //   143: astore 5
    //   145: aload 4
    //   147: astore_2
    //   148: new 417	com/google/android/gms/internal/measurement/zzjz
    //   151: astore 6
    //   153: aload 4
    //   155: astore_2
    //   156: aload 6
    //   158: invokespecial 1259	com/google/android/gms/internal/measurement/zzjz:<init>	()V
    //   161: aload 4
    //   163: astore_2
    //   164: aload 6
    //   166: aload 5
    //   168: invokevirtual 641	com/google/android/gms/internal/measurement/zzabj:zzb	(Lcom/google/android/gms/internal/measurement/zzaba;)Lcom/google/android/gms/internal/measurement/zzabj;
    //   171: pop
    //   172: aload 4
    //   174: astore_2
    //   175: aload 4
    //   177: iconst_0
    //   178: invokeinterface 1038 2 0
    //   183: istore 7
    //   185: aload 4
    //   187: astore_2
    //   188: aload_3
    //   189: iload 7
    //   191: invokestatic 247	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   194: invokeinterface 1261 2 0
    //   199: checkcast 541	java/util/List
    //   202: astore 8
    //   204: aload 8
    //   206: astore 5
    //   208: aload 8
    //   210: ifnonnull +36 -> 246
    //   213: aload 4
    //   215: astore_2
    //   216: new 538	java/util/ArrayList
    //   219: astore 5
    //   221: aload 4
    //   223: astore_2
    //   224: aload 5
    //   226: invokespecial 539	java/util/ArrayList:<init>	()V
    //   229: aload 4
    //   231: astore_2
    //   232: aload_3
    //   233: iload 7
    //   235: invokestatic 247	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   238: aload 5
    //   240: invokeinterface 1139 3 0
    //   245: pop
    //   246: aload 4
    //   248: astore_2
    //   249: aload 5
    //   251: aload 6
    //   253: invokeinterface 556 2 0
    //   258: pop
    //   259: aload 4
    //   261: astore_2
    //   262: aload 4
    //   264: invokeinterface 1090 1 0
    //   269: istore 9
    //   271: iload 9
    //   273: ifne -155 -> 118
    //   276: aload 4
    //   278: ifnull +10 -> 288
    //   281: aload 4
    //   283: invokeinterface 209 1 0
    //   288: aload_3
    //   289: astore_1
    //   290: goto -174 -> 116
    //   293: astore 5
    //   295: aload 4
    //   297: astore_2
    //   298: aload_0
    //   299: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   302: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   305: ldc_w 1263
    //   308: aload_1
    //   309: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   312: aload 5
    //   314: invokevirtual 232	com/google/android/gms/internal/measurement/zzfi:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   317: goto -58 -> 259
    //   320: astore 5
    //   322: aload 4
    //   324: astore_2
    //   325: aload_0
    //   326: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   329: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   332: ldc_w 536
    //   335: aload_1
    //   336: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   339: aload 5
    //   341: invokevirtual 232	com/google/android/gms/internal/measurement/zzfi:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   344: aload 4
    //   346: ifnull +10 -> 356
    //   349: aload 4
    //   351: invokeinterface 209 1 0
    //   356: aconst_null
    //   357: astore_1
    //   358: goto -242 -> 116
    //   361: astore_1
    //   362: aconst_null
    //   363: astore_2
    //   364: aload_2
    //   365: ifnull +9 -> 374
    //   368: aload_2
    //   369: invokeinterface 209 1 0
    //   374: aload_1
    //   375: athrow
    //   376: astore_1
    //   377: goto -13 -> 364
    //   380: astore 5
    //   382: aconst_null
    //   383: astore 4
    //   385: goto -63 -> 322
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	388	0	this	zzei
    //   0	388	1	paramString1	String
    //   0	388	2	paramString2	String
    //   25	264	3	localArrayMap	android.support.v4.util.ArrayMap
    //   30	354	4	localObject1	Object
    //   95	155	5	localObject2	Object
    //   293	20	5	localIOException	IOException
    //   320	20	5	localSQLiteException1	SQLiteException
    //   380	1	5	localSQLiteException2	SQLiteException
    //   151	101	6	localzzjz	zzjz
    //   183	51	7	i	int
    //   202	7	8	localList	List
    //   269	3	9	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   164	172	293	java/io/IOException
    //   79	89	320	android/database/sqlite/SQLiteException
    //   92	97	320	android/database/sqlite/SQLiteException
    //   121	131	320	android/database/sqlite/SQLiteException
    //   134	145	320	android/database/sqlite/SQLiteException
    //   148	153	320	android/database/sqlite/SQLiteException
    //   156	161	320	android/database/sqlite/SQLiteException
    //   164	172	320	android/database/sqlite/SQLiteException
    //   175	185	320	android/database/sqlite/SQLiteException
    //   188	204	320	android/database/sqlite/SQLiteException
    //   216	221	320	android/database/sqlite/SQLiteException
    //   224	229	320	android/database/sqlite/SQLiteException
    //   232	246	320	android/database/sqlite/SQLiteException
    //   249	259	320	android/database/sqlite/SQLiteException
    //   262	271	320	android/database/sqlite/SQLiteException
    //   298	317	320	android/database/sqlite/SQLiteException
    //   32	76	361	finally
    //   79	89	376	finally
    //   92	97	376	finally
    //   121	131	376	finally
    //   134	145	376	finally
    //   148	153	376	finally
    //   156	161	376	finally
    //   164	172	376	finally
    //   175	185	376	finally
    //   188	204	376	finally
    //   216	221	376	finally
    //   224	229	376	finally
    //   232	246	376	finally
    //   249	259	376	finally
    //   262	271	376	finally
    //   298	317	376	finally
    //   325	344	376	finally
    //   32	76	380	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  final java.util.Map<Integer, List<zzkc>> zzk(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 412	com/google/android/gms/internal/measurement/zzhk:zzch	()V
    //   4: aload_0
    //   5: invokevirtual 415	com/google/android/gms/internal/measurement/zzhj:zzab	()V
    //   8: aload_1
    //   9: invokestatic 283	com/google/android/gms/common/internal/Preconditions:checkNotEmpty	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_2
    //   14: invokestatic 283	com/google/android/gms/common/internal/Preconditions:checkNotEmpty	(Ljava/lang/String;)Ljava/lang/String;
    //   17: pop
    //   18: new 1130	android/support/v4/util/ArrayMap
    //   21: dup
    //   22: invokespecial 1131	android/support/v4/util/ArrayMap:<init>	()V
    //   25: astore_3
    //   26: aload_0
    //   27: invokevirtual 190	com/google/android/gms/internal/measurement/zzei:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   30: astore 4
    //   32: aload 4
    //   34: ldc_w 499
    //   37: iconst_2
    //   38: anewarray 19	java/lang/String
    //   41: dup
    //   42: iconst_0
    //   43: ldc_w 460
    //   46: aastore
    //   47: dup
    //   48: iconst_1
    //   49: ldc_w 469
    //   52: aastore
    //   53: ldc_w 1267
    //   56: iconst_2
    //   57: anewarray 19	java/lang/String
    //   60: dup
    //   61: iconst_0
    //   62: aload_1
    //   63: aastore
    //   64: dup
    //   65: iconst_1
    //   66: aload_2
    //   67: aastore
    //   68: aconst_null
    //   69: aconst_null
    //   70: aconst_null
    //   71: invokevirtual 404	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   74: astore 4
    //   76: aload 4
    //   78: astore_2
    //   79: aload 4
    //   81: invokeinterface 202 1 0
    //   86: ifne +32 -> 118
    //   89: aload 4
    //   91: astore_2
    //   92: invokestatic 1258	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   95: astore 5
    //   97: aload 5
    //   99: astore_2
    //   100: aload_2
    //   101: astore_1
    //   102: aload 4
    //   104: ifnull +12 -> 116
    //   107: aload 4
    //   109: invokeinterface 209 1 0
    //   114: aload_2
    //   115: astore_1
    //   116: aload_1
    //   117: areturn
    //   118: aload 4
    //   120: astore_2
    //   121: aload 4
    //   123: iconst_1
    //   124: invokeinterface 630 2 0
    //   129: astore 5
    //   131: aload 4
    //   133: astore_2
    //   134: aload 5
    //   136: iconst_0
    //   137: aload 5
    //   139: arraylength
    //   140: invokestatic 635	com/google/android/gms/internal/measurement/zzaba:zza	([BII)Lcom/google/android/gms/internal/measurement/zzaba;
    //   143: astore 5
    //   145: aload 4
    //   147: astore_2
    //   148: new 489	com/google/android/gms/internal/measurement/zzkc
    //   151: astore 6
    //   153: aload 4
    //   155: astore_2
    //   156: aload 6
    //   158: invokespecial 1268	com/google/android/gms/internal/measurement/zzkc:<init>	()V
    //   161: aload 4
    //   163: astore_2
    //   164: aload 6
    //   166: aload 5
    //   168: invokevirtual 641	com/google/android/gms/internal/measurement/zzabj:zzb	(Lcom/google/android/gms/internal/measurement/zzaba;)Lcom/google/android/gms/internal/measurement/zzabj;
    //   171: pop
    //   172: aload 4
    //   174: astore_2
    //   175: aload 4
    //   177: iconst_0
    //   178: invokeinterface 1038 2 0
    //   183: istore 7
    //   185: aload 4
    //   187: astore_2
    //   188: aload_3
    //   189: iload 7
    //   191: invokestatic 247	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   194: invokeinterface 1261 2 0
    //   199: checkcast 541	java/util/List
    //   202: astore 8
    //   204: aload 8
    //   206: astore 5
    //   208: aload 8
    //   210: ifnonnull +36 -> 246
    //   213: aload 4
    //   215: astore_2
    //   216: new 538	java/util/ArrayList
    //   219: astore 5
    //   221: aload 4
    //   223: astore_2
    //   224: aload 5
    //   226: invokespecial 539	java/util/ArrayList:<init>	()V
    //   229: aload 4
    //   231: astore_2
    //   232: aload_3
    //   233: iload 7
    //   235: invokestatic 247	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   238: aload 5
    //   240: invokeinterface 1139 3 0
    //   245: pop
    //   246: aload 4
    //   248: astore_2
    //   249: aload 5
    //   251: aload 6
    //   253: invokeinterface 556 2 0
    //   258: pop
    //   259: aload 4
    //   261: astore_2
    //   262: aload 4
    //   264: invokeinterface 1090 1 0
    //   269: istore 9
    //   271: iload 9
    //   273: ifne -155 -> 118
    //   276: aload 4
    //   278: ifnull +10 -> 288
    //   281: aload 4
    //   283: invokeinterface 209 1 0
    //   288: aload_3
    //   289: astore_1
    //   290: goto -174 -> 116
    //   293: astore 5
    //   295: aload 4
    //   297: astore_2
    //   298: aload_0
    //   299: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   302: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   305: ldc_w 1270
    //   308: aload_1
    //   309: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   312: aload 5
    //   314: invokevirtual 232	com/google/android/gms/internal/measurement/zzfi:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   317: goto -58 -> 259
    //   320: astore 5
    //   322: aload 4
    //   324: astore_2
    //   325: aload_0
    //   326: invokevirtual 218	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   329: invokevirtual 224	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   332: ldc_w 536
    //   335: aload_1
    //   336: invokestatic 430	com/google/android/gms/internal/measurement/zzfg:zzbh	(Ljava/lang/String;)Ljava/lang/Object;
    //   339: aload 5
    //   341: invokevirtual 232	com/google/android/gms/internal/measurement/zzfi:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   344: aload 4
    //   346: ifnull +10 -> 356
    //   349: aload 4
    //   351: invokeinterface 209 1 0
    //   356: aconst_null
    //   357: astore_1
    //   358: goto -242 -> 116
    //   361: astore_1
    //   362: aconst_null
    //   363: astore_2
    //   364: aload_2
    //   365: ifnull +9 -> 374
    //   368: aload_2
    //   369: invokeinterface 209 1 0
    //   374: aload_1
    //   375: athrow
    //   376: astore_1
    //   377: goto -13 -> 364
    //   380: astore 5
    //   382: aconst_null
    //   383: astore 4
    //   385: goto -63 -> 322
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	388	0	this	zzei
    //   0	388	1	paramString1	String
    //   0	388	2	paramString2	String
    //   25	264	3	localArrayMap	android.support.v4.util.ArrayMap
    //   30	354	4	localObject1	Object
    //   95	155	5	localObject2	Object
    //   293	20	5	localIOException	IOException
    //   320	20	5	localSQLiteException1	SQLiteException
    //   380	1	5	localSQLiteException2	SQLiteException
    //   151	101	6	localzzkc	zzkc
    //   183	51	7	i	int
    //   202	7	8	localList	List
    //   269	3	9	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   164	172	293	java/io/IOException
    //   79	89	320	android/database/sqlite/SQLiteException
    //   92	97	320	android/database/sqlite/SQLiteException
    //   121	131	320	android/database/sqlite/SQLiteException
    //   134	145	320	android/database/sqlite/SQLiteException
    //   148	153	320	android/database/sqlite/SQLiteException
    //   156	161	320	android/database/sqlite/SQLiteException
    //   164	172	320	android/database/sqlite/SQLiteException
    //   175	185	320	android/database/sqlite/SQLiteException
    //   188	204	320	android/database/sqlite/SQLiteException
    //   216	221	320	android/database/sqlite/SQLiteException
    //   224	229	320	android/database/sqlite/SQLiteException
    //   232	246	320	android/database/sqlite/SQLiteException
    //   249	259	320	android/database/sqlite/SQLiteException
    //   262	271	320	android/database/sqlite/SQLiteException
    //   298	317	320	android/database/sqlite/SQLiteException
    //   32	76	361	finally
    //   79	89	376	finally
    //   92	97	376	finally
    //   121	131	376	finally
    //   134	145	376	finally
    //   148	153	376	finally
    //   156	161	376	finally
    //   164	172	376	finally
    //   175	185	376	finally
    //   188	204	376	finally
    //   216	221	376	finally
    //   224	229	376	finally
    //   232	246	376	finally
    //   249	259	376	finally
    //   262	271	376	finally
    //   298	317	376	finally
    //   325	344	376	finally
    //   32	76	380	android/database/sqlite/SQLiteException
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzei.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */