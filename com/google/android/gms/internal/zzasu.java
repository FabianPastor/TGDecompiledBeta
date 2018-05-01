package com.google.android.gms.internal;

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

class zzasu
  extends zzats
{
  private static final Map<String, String> zzbqp = new ArrayMap(18);
  private static final Map<String, String> zzbqq;
  private static final Map<String, String> zzbqr;
  private static final Map<String, String> zzbqs;
  private final zzc zzbqt;
  private final zzatz zzbqu = new zzatz(zznq());
  
  static
  {
    zzbqp.put("app_version", "ALTER TABLE apps ADD COLUMN app_version TEXT;");
    zzbqp.put("app_store", "ALTER TABLE apps ADD COLUMN app_store TEXT;");
    zzbqp.put("gmp_version", "ALTER TABLE apps ADD COLUMN gmp_version INTEGER;");
    zzbqp.put("dev_cert_hash", "ALTER TABLE apps ADD COLUMN dev_cert_hash INTEGER;");
    zzbqp.put("measurement_enabled", "ALTER TABLE apps ADD COLUMN measurement_enabled INTEGER;");
    zzbqp.put("last_bundle_start_timestamp", "ALTER TABLE apps ADD COLUMN last_bundle_start_timestamp INTEGER;");
    zzbqp.put("day", "ALTER TABLE apps ADD COLUMN day INTEGER;");
    zzbqp.put("daily_public_events_count", "ALTER TABLE apps ADD COLUMN daily_public_events_count INTEGER;");
    zzbqp.put("daily_events_count", "ALTER TABLE apps ADD COLUMN daily_events_count INTEGER;");
    zzbqp.put("daily_conversions_count", "ALTER TABLE apps ADD COLUMN daily_conversions_count INTEGER;");
    zzbqp.put("remote_config", "ALTER TABLE apps ADD COLUMN remote_config BLOB;");
    zzbqp.put("config_fetched_time", "ALTER TABLE apps ADD COLUMN config_fetched_time INTEGER;");
    zzbqp.put("failed_config_fetch_time", "ALTER TABLE apps ADD COLUMN failed_config_fetch_time INTEGER;");
    zzbqp.put("app_version_int", "ALTER TABLE apps ADD COLUMN app_version_int INTEGER;");
    zzbqp.put("firebase_instance_id", "ALTER TABLE apps ADD COLUMN firebase_instance_id TEXT;");
    zzbqp.put("daily_error_events_count", "ALTER TABLE apps ADD COLUMN daily_error_events_count INTEGER;");
    zzbqp.put("daily_realtime_events_count", "ALTER TABLE apps ADD COLUMN daily_realtime_events_count INTEGER;");
    zzbqp.put("health_monitor_sample", "ALTER TABLE apps ADD COLUMN health_monitor_sample TEXT;");
    zzbqq = new ArrayMap(1);
    zzbqq.put("realtime", "ALTER TABLE raw_events ADD COLUMN realtime INTEGER;");
    zzbqr = new ArrayMap(1);
    zzbqr.put("has_realtime", "ALTER TABLE queue ADD COLUMN has_realtime INTEGER;");
    zzbqs = new ArrayMap(1);
    zzbqs.put("previous_install_count", "ALTER TABLE app2 ADD COLUMN previous_install_count INTEGER;");
  }
  
  zzasu(zzatp paramzzatp)
  {
    super(paramzzatp);
    paramzzatp = zznV();
    this.zzbqt = new zzc(getContext(), paramzzatp);
  }
  
  private boolean zzKP()
  {
    return getContext().getDatabasePath(zznV()).exists();
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
      zzJt().zzLa().zze("Database error", paramString, paramArrayOfString);
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
  
  static void zza(zzati paramzzati, SQLiteDatabase paramSQLiteDatabase)
  {
    if (paramzzati == null) {
      throw new IllegalArgumentException("Monitor must not be null");
    }
    if (Build.VERSION.SDK_INT >= 9)
    {
      paramSQLiteDatabase = new File(paramSQLiteDatabase.getPath());
      if (!paramSQLiteDatabase.setReadable(false, false)) {
        paramzzati.zzLc().log("Failed to turn off database read permission");
      }
      if (!paramSQLiteDatabase.setWritable(false, false)) {
        paramzzati.zzLc().log("Failed to turn off database write permission");
      }
      if (!paramSQLiteDatabase.setReadable(true, true)) {
        paramzzati.zzLc().log("Failed to turn on database read permission for owner");
      }
      if (!paramSQLiteDatabase.setWritable(true, true)) {
        paramzzati.zzLc().log("Failed to turn on database write permission for owner");
      }
    }
  }
  
  @WorkerThread
  static void zza(zzati paramzzati, SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, String paramString3, Map<String, String> paramMap)
    throws SQLiteException
  {
    if (paramzzati == null) {
      throw new IllegalArgumentException("Monitor must not be null");
    }
    if (!zza(paramzzati, paramSQLiteDatabase, paramString1)) {
      paramSQLiteDatabase.execSQL(paramString2);
    }
    try
    {
      zza(paramzzati, paramSQLiteDatabase, paramString1, paramString3, paramMap);
      return;
    }
    catch (SQLiteException paramSQLiteDatabase)
    {
      paramzzati.zzLa().zzj("Failed to verify columns on table that was just created", paramString1);
      throw paramSQLiteDatabase;
    }
  }
  
  @WorkerThread
  static void zza(zzati paramzzati, SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, Map<String, String> paramMap)
    throws SQLiteException
  {
    if (paramzzati == null) {
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
      paramzzati.zzLc().zze("Table has extra columns. table, columns", paramString1, TextUtils.join(", ", localSet));
    }
  }
  
  @WorkerThread
  private void zza(String paramString, zzauf.zza paramzza)
  {
    int k = 0;
    zznA();
    zzmq();
    zzac.zzdv(paramString);
    zzac.zzw(paramzza);
    zzac.zzw(paramzza.zzbvj);
    zzac.zzw(paramzza.zzbvi);
    if (paramzza.zzbvh == null) {
      zzJt().zzLc().zzj("Audience with no ID. appId", zzati.zzfI(paramString));
    }
    label247:
    label292:
    label301:
    label302:
    for (;;)
    {
      return;
      int n = paramzza.zzbvh.intValue();
      Object localObject = paramzza.zzbvj;
      int j = localObject.length;
      int i = 0;
      while (i < j)
      {
        if (localObject[i].zzbvl == null)
        {
          zzJt().zzLc().zze("Event filter with no ID. Audience definition ignored. appId, audienceId", zzati.zzfI(paramString), paramzza.zzbvh);
          return;
        }
        i += 1;
      }
      localObject = paramzza.zzbvi;
      j = localObject.length;
      i = 0;
      while (i < j)
      {
        if (localObject[i].zzbvl == null)
        {
          zzJt().zzLc().zze("Property filter with no ID. Audience definition ignored. appId, audienceId", zzati.zzfI(paramString), paramzza.zzbvh);
          return;
        }
        i += 1;
      }
      int m = 1;
      localObject = paramzza.zzbvj;
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
        paramzza = paramzza.zzbvi;
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
  static boolean zza(zzati paramzzati, SQLiteDatabase paramSQLiteDatabase, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: ifnonnull +14 -> 18
    //   7: new 266	java/lang/IllegalArgumentException
    //   10: dup
    //   11: ldc_w 268
    //   14: invokespecial 271	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   17: athrow
    //   18: aload_1
    //   19: ldc_w 459
    //   22: iconst_1
    //   23: anewarray 322	java/lang/String
    //   26: dup
    //   27: iconst_0
    //   28: ldc_w 461
    //   31: aastore
    //   32: ldc_w 463
    //   35: iconst_1
    //   36: anewarray 322	java/lang/String
    //   39: dup
    //   40: iconst_0
    //   41: aload_2
    //   42: aastore
    //   43: aconst_null
    //   44: aconst_null
    //   45: aconst_null
    //   46: invokevirtual 467	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   49: astore_1
    //   50: aload_1
    //   51: astore 4
    //   53: aload 4
    //   55: astore_1
    //   56: aload 4
    //   58: invokeinterface 237 1 0
    //   63: istore_3
    //   64: aload 4
    //   66: ifnull +10 -> 76
    //   69: aload 4
    //   71: invokeinterface 244 1 0
    //   76: iload_3
    //   77: ireturn
    //   78: astore 5
    //   80: aconst_null
    //   81: astore 4
    //   83: aload 4
    //   85: astore_1
    //   86: aload_0
    //   87: invokevirtual 282	com/google/android/gms/internal/zzati:zzLc	()Lcom/google/android/gms/internal/zzati$zza;
    //   90: ldc_w 469
    //   93: aload_2
    //   94: aload 5
    //   96: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   99: aload 4
    //   101: ifnull +10 -> 111
    //   104: aload 4
    //   106: invokeinterface 244 1 0
    //   111: iconst_0
    //   112: ireturn
    //   113: astore_0
    //   114: aload 4
    //   116: astore_1
    //   117: aload_1
    //   118: ifnull +9 -> 127
    //   121: aload_1
    //   122: invokeinterface 244 1 0
    //   127: aload_0
    //   128: athrow
    //   129: astore_0
    //   130: goto -13 -> 117
    //   133: astore 5
    //   135: goto -52 -> 83
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	138	0	paramzzati	zzati
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
  private boolean zza(String paramString, int paramInt, zzauf.zzb paramzzb)
  {
    zznA();
    zzmq();
    zzac.zzdv(paramString);
    zzac.zzw(paramzzb);
    if (TextUtils.isEmpty(paramzzb.zzbvm))
    {
      zzJt().zzLc().zzd("Event filter had no event name. Audience definition ignored. appId, audienceId, filterId", zzati.zzfI(paramString), Integer.valueOf(paramInt), String.valueOf(paramzzb.zzbvl));
      return false;
    }
    try
    {
      byte[] arrayOfByte = new byte[paramzzb.zzacZ()];
      Object localObject = zzbum.zzae(arrayOfByte);
      paramzzb.zza((zzbum)localObject);
      ((zzbum)localObject).zzacM();
      localObject = new ContentValues();
      ((ContentValues)localObject).put("app_id", paramString);
      ((ContentValues)localObject).put("audience_id", Integer.valueOf(paramInt));
      ((ContentValues)localObject).put("filter_id", paramzzb.zzbvl);
      ((ContentValues)localObject).put("event_name", paramzzb.zzbvm);
      ((ContentValues)localObject).put("data", arrayOfByte);
      return false;
    }
    catch (IOException paramzzb)
    {
      try
      {
        if (getWritableDatabase().insertWithOnConflict("event_filters", null, (ContentValues)localObject, 5) == -1L) {
          zzJt().zzLa().zzj("Failed to insert event filter (got -1). appId", zzati.zzfI(paramString));
        }
        return true;
      }
      catch (SQLiteException paramzzb)
      {
        zzJt().zzLa().zze("Error storing event filter. appId", zzati.zzfI(paramString), paramzzb);
      }
      paramzzb = paramzzb;
      zzJt().zzLa().zze("Configuration loss. Failed to serialize event filter. appId", zzati.zzfI(paramString), paramzzb);
      return false;
    }
  }
  
  @WorkerThread
  private boolean zza(String paramString, int paramInt, zzauf.zze paramzze)
  {
    zznA();
    zzmq();
    zzac.zzdv(paramString);
    zzac.zzw(paramzze);
    if (TextUtils.isEmpty(paramzze.zzbvB))
    {
      zzJt().zzLc().zzd("Property filter had no property name. Audience definition ignored. appId, audienceId, filterId", zzati.zzfI(paramString), Integer.valueOf(paramInt), String.valueOf(paramzze.zzbvl));
      return false;
    }
    try
    {
      byte[] arrayOfByte = new byte[paramzze.zzacZ()];
      Object localObject = zzbum.zzae(arrayOfByte);
      paramzze.zza((zzbum)localObject);
      ((zzbum)localObject).zzacM();
      localObject = new ContentValues();
      ((ContentValues)localObject).put("app_id", paramString);
      ((ContentValues)localObject).put("audience_id", Integer.valueOf(paramInt));
      ((ContentValues)localObject).put("filter_id", paramzze.zzbvl);
      ((ContentValues)localObject).put("property_name", paramzze.zzbvB);
      ((ContentValues)localObject).put("data", arrayOfByte);
      try
      {
        if (getWritableDatabase().insertWithOnConflict("property_filters", null, (ContentValues)localObject, 5) == -1L)
        {
          zzJt().zzLa().zzj("Failed to insert property filter (got -1). appId", zzati.zzfI(paramString));
          return false;
        }
      }
      catch (SQLiteException paramzze)
      {
        zzJt().zzLa().zze("Error storing property filter. appId", zzati.zzfI(paramString), paramzze);
        return false;
      }
      return true;
    }
    catch (IOException paramzze)
    {
      zzJt().zzLa().zze("Configuration loss. Failed to serialize property filter. appId", zzati.zzfI(paramString), paramzze);
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
      zzJt().zzLa().zze("Database error", paramString, paramArrayOfString);
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
    zznA();
    getWritableDatabase().beginTransaction();
  }
  
  @WorkerThread
  public void endTransaction()
  {
    zznA();
    getWritableDatabase().endTransaction();
  }
  
  @WorkerThread
  SQLiteDatabase getWritableDatabase()
  {
    zzmq();
    try
    {
      SQLiteDatabase localSQLiteDatabase = this.zzbqt.getWritableDatabase();
      return localSQLiteDatabase;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzJt().zzLc().zzj("Error opening database", localSQLiteException);
      throw localSQLiteException;
    }
  }
  
  @WorkerThread
  public void setTransactionSuccessful()
  {
    zznA();
    getWritableDatabase().setTransactionSuccessful();
  }
  
  @WorkerThread
  void zzA(String paramString, int paramInt)
  {
    zznA();
    zzmq();
    zzac.zzdv(paramString);
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.delete("property_filters", "app_id=? and audience_id=?", new String[] { paramString, String.valueOf(paramInt) });
    localSQLiteDatabase.delete("event_filters", "app_id=? and audience_id=?", new String[] { paramString, String.valueOf(paramInt) });
  }
  
  public void zzG(List<Long> paramList)
  {
    zzac.zzw(paramList);
    zzmq();
    zznA();
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
      zzJt().zzLa().zze("Deleted fewer rows from raw events table than expected", Integer.valueOf(i), Integer.valueOf(paramList.size()));
    }
  }
  
  /* Error */
  @WorkerThread
  public String zzKG()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   7: astore_1
    //   8: aload_1
    //   9: ldc_w 631
    //   12: aconst_null
    //   13: invokevirtual 234	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   16: astore_1
    //   17: aload_1
    //   18: astore_2
    //   19: aload_1
    //   20: invokeinterface 237 1 0
    //   25: ifeq +29 -> 54
    //   28: aload_1
    //   29: astore_2
    //   30: aload_1
    //   31: iconst_0
    //   32: invokeinterface 634 2 0
    //   37: astore_3
    //   38: aload_3
    //   39: astore_2
    //   40: aload_1
    //   41: ifnull +11 -> 52
    //   44: aload_1
    //   45: invokeinterface 244 1 0
    //   50: aload_3
    //   51: astore_2
    //   52: aload_2
    //   53: areturn
    //   54: aload 4
    //   56: astore_2
    //   57: aload_1
    //   58: ifnull -6 -> 52
    //   61: aload_1
    //   62: invokeinterface 244 1 0
    //   67: aconst_null
    //   68: areturn
    //   69: astore_3
    //   70: aconst_null
    //   71: astore_1
    //   72: aload_1
    //   73: astore_2
    //   74: aload_0
    //   75: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   78: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   81: ldc_w 636
    //   84: aload_3
    //   85: invokevirtual 312	com/google/android/gms/internal/zzati$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   88: aload 4
    //   90: astore_2
    //   91: aload_1
    //   92: ifnull -40 -> 52
    //   95: aload_1
    //   96: invokeinterface 244 1 0
    //   101: aconst_null
    //   102: areturn
    //   103: astore_1
    //   104: aconst_null
    //   105: astore_2
    //   106: aload_2
    //   107: ifnull +9 -> 116
    //   110: aload_2
    //   111: invokeinterface 244 1 0
    //   116: aload_1
    //   117: athrow
    //   118: astore_1
    //   119: goto -13 -> 106
    //   122: astore_3
    //   123: goto -51 -> 72
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	126	0	this	zzasu
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
  
  public boolean zzKH()
  {
    return zzb("select count(1) > 0 from queue where has_realtime = 1", null) != 0L;
  }
  
  @WorkerThread
  void zzKI()
  {
    zzmq();
    zznA();
    if (!zzKP()) {}
    long l1;
    long l2;
    do
    {
      return;
      l1 = zzJu().zzbsj.get();
      l2 = zznq().elapsedRealtime();
    } while (Math.abs(l2 - l1) <= zzJv().zzKo());
    zzJu().zzbsj.set(l2);
    zzKJ();
  }
  
  @WorkerThread
  void zzKJ()
  {
    zzmq();
    zznA();
    if (!zzKP()) {}
    int i;
    do
    {
      return;
      i = getWritableDatabase().delete("queue", "abs(bundle_end_timestamp - ?) > cast(? as integer)", new String[] { String.valueOf(zznq().currentTimeMillis()), String.valueOf(zzJv().zzKn()) });
    } while (i <= 0);
    zzJt().zzLg().zzj("Deleted stale rows. rowsDeleted", Integer.valueOf(i));
  }
  
  @WorkerThread
  public long zzKK()
  {
    return zza("select max(bundle_end_timestamp) from queue", null, 0L);
  }
  
  @WorkerThread
  public long zzKL()
  {
    return zza("select max(timestamp) from raw_events", null, 0L);
  }
  
  public boolean zzKM()
  {
    return zzb("select count(1) > 0 from raw_events", null) != 0L;
  }
  
  public boolean zzKN()
  {
    return zzb("select count(1) > 0 from raw_events where realtime = 1", null) != 0L;
  }
  
  public long zzKO()
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
        zzJt().zzLa().zzj("Error querying raw events", localSQLiteException);
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
  public zzasy zzP(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aload_1
    //   4: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_2
    //   9: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   17: aload_0
    //   18: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
    //   21: aload_0
    //   22: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   25: ldc_w 726
    //   28: iconst_3
    //   29: anewarray 322	java/lang/String
    //   32: dup
    //   33: iconst_0
    //   34: ldc_w 728
    //   37: aastore
    //   38: dup
    //   39: iconst_1
    //   40: ldc_w 730
    //   43: aastore
    //   44: dup
    //   45: iconst_2
    //   46: ldc_w 732
    //   49: aastore
    //   50: ldc_w 734
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
    //   68: invokevirtual 467	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   71: astore 4
    //   73: aload 4
    //   75: invokeinterface 237 1 0
    //   80: istore_3
    //   81: iload_3
    //   82: ifne +19 -> 101
    //   85: aload 4
    //   87: ifnull +10 -> 97
    //   90: aload 4
    //   92: invokeinterface 244 1 0
    //   97: aconst_null
    //   98: astore_1
    //   99: aload_1
    //   100: areturn
    //   101: new 736	com/google/android/gms/internal/zzasy
    //   104: dup
    //   105: aload_1
    //   106: aload_2
    //   107: aload 4
    //   109: iconst_0
    //   110: invokeinterface 241 2 0
    //   115: aload 4
    //   117: iconst_1
    //   118: invokeinterface 241 2 0
    //   123: aload 4
    //   125: iconst_2
    //   126: invokeinterface 241 2 0
    //   131: invokespecial 739	com/google/android/gms/internal/zzasy:<init>	(Ljava/lang/String;Ljava/lang/String;JJJ)V
    //   134: astore 5
    //   136: aload 4
    //   138: invokeinterface 742 1 0
    //   143: ifeq +20 -> 163
    //   146: aload_0
    //   147: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   150: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   153: ldc_w 744
    //   156: aload_1
    //   157: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   160: invokevirtual 312	com/google/android/gms/internal/zzati$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   163: aload 5
    //   165: astore_1
    //   166: aload 4
    //   168: ifnull -69 -> 99
    //   171: aload 4
    //   173: invokeinterface 244 1 0
    //   178: aload 5
    //   180: areturn
    //   181: astore 5
    //   183: aconst_null
    //   184: astore 4
    //   186: aload_0
    //   187: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   190: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   193: ldc_w 746
    //   196: aload_1
    //   197: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   200: aload_2
    //   201: aload 5
    //   203: invokevirtual 487	com/google/android/gms/internal/zzati$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   206: aload 4
    //   208: ifnull +10 -> 218
    //   211: aload 4
    //   213: invokeinterface 244 1 0
    //   218: aconst_null
    //   219: areturn
    //   220: astore_1
    //   221: aload 5
    //   223: astore_2
    //   224: aload_2
    //   225: ifnull +9 -> 234
    //   228: aload_2
    //   229: invokeinterface 244 1 0
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
    //   0	255	0	this	zzasu
    //   0	255	1	paramString1	String
    //   0	255	2	paramString2	String
    //   80	2	3	bool	boolean
    //   71	174	4	localCursor	Cursor
    //   1	178	5	localzzasy	zzasy
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
  public void zzQ(String paramString1, String paramString2)
  {
    zzac.zzdv(paramString1);
    zzac.zzdv(paramString2);
    zzmq();
    zznA();
    try
    {
      int i = getWritableDatabase().delete("user_attributes", "app_id=? and name=?", new String[] { paramString1, paramString2 });
      zzJt().zzLg().zzj("Deleted user attribute rows:", Integer.valueOf(i));
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzJt().zzLa().zzd("Error deleting user attribute. appId", zzati.zzfI(paramString1), paramString2, localSQLiteException);
    }
  }
  
  /* Error */
  @WorkerThread
  public zzaud zzR(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aload_1
    //   4: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_2
    //   9: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   17: aload_0
    //   18: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
    //   21: aload_0
    //   22: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   25: ldc_w 749
    //   28: iconst_2
    //   29: anewarray 322	java/lang/String
    //   32: dup
    //   33: iconst_0
    //   34: ldc_w 757
    //   37: aastore
    //   38: dup
    //   39: iconst_1
    //   40: ldc_w 758
    //   43: aastore
    //   44: ldc_w 734
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
    //   62: invokevirtual 467	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   65: astore 4
    //   67: aload 4
    //   69: invokeinterface 237 1 0
    //   74: istore_3
    //   75: iload_3
    //   76: ifne +19 -> 95
    //   79: aload 4
    //   81: ifnull +10 -> 91
    //   84: aload 4
    //   86: invokeinterface 244 1 0
    //   91: aconst_null
    //   92: astore_1
    //   93: aload_1
    //   94: areturn
    //   95: new 760	com/google/android/gms/internal/zzaud
    //   98: dup
    //   99: aload_1
    //   100: aload_2
    //   101: aload 4
    //   103: iconst_0
    //   104: invokeinterface 241 2 0
    //   109: aload_0
    //   110: aload 4
    //   112: iconst_1
    //   113: invokevirtual 763	com/google/android/gms/internal/zzasu:zzb	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   116: invokespecial 766	com/google/android/gms/internal/zzaud:<init>	(Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   119: astore 5
    //   121: aload 4
    //   123: invokeinterface 742 1 0
    //   128: ifeq +20 -> 148
    //   131: aload_0
    //   132: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   135: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   138: ldc_w 768
    //   141: aload_1
    //   142: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   145: invokevirtual 312	com/google/android/gms/internal/zzati$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   148: aload 5
    //   150: astore_1
    //   151: aload 4
    //   153: ifnull -60 -> 93
    //   156: aload 4
    //   158: invokeinterface 244 1 0
    //   163: aload 5
    //   165: areturn
    //   166: astore 5
    //   168: aconst_null
    //   169: astore 4
    //   171: aload_0
    //   172: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   175: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   178: ldc_w 770
    //   181: aload_1
    //   182: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   185: aload_2
    //   186: aload 5
    //   188: invokevirtual 487	com/google/android/gms/internal/zzati$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   191: aload 4
    //   193: ifnull +10 -> 203
    //   196: aload 4
    //   198: invokeinterface 244 1 0
    //   203: aconst_null
    //   204: areturn
    //   205: astore_1
    //   206: aload 5
    //   208: astore_2
    //   209: aload_2
    //   210: ifnull +9 -> 219
    //   213: aload_2
    //   214: invokeinterface 244 1 0
    //   219: aload_1
    //   220: athrow
    //   221: astore_1
    //   222: aload 4
    //   224: astore_2
    //   225: goto -16 -> 209
    //   228: astore_1
    //   229: aload 4
    //   231: astore_2
    //   232: goto -23 -> 209
    //   235: astore 5
    //   237: goto -66 -> 171
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	240	0	this	zzasu
    //   0	240	1	paramString1	String
    //   0	240	2	paramString2	String
    //   74	2	3	bool	boolean
    //   65	165	4	localCursor	Cursor
    //   1	163	5	localzzaud	zzaud
    //   166	41	5	localSQLiteException1	SQLiteException
    //   235	1	5	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   21	67	166	android/database/sqlite/SQLiteException
    //   21	67	205	finally
    //   67	75	221	finally
    //   95	148	221	finally
    //   171	191	228	finally
    //   67	75	235	android/database/sqlite/SQLiteException
    //   95	148	235	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  Map<Integer, List<zzauf.zzb>> zzS(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
    //   4: aload_0
    //   5: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   8: aload_1
    //   9: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_2
    //   14: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   17: pop
    //   18: new 27	android/support/v4/util/ArrayMap
    //   21: dup
    //   22: invokespecial 773	android/support/v4/util/ArrayMap:<init>	()V
    //   25: astore 8
    //   27: aload_0
    //   28: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   31: astore 5
    //   33: aload 5
    //   35: ldc_w 527
    //   38: iconst_2
    //   39: anewarray 322	java/lang/String
    //   42: dup
    //   43: iconst_0
    //   44: ldc_w 513
    //   47: aastore
    //   48: dup
    //   49: iconst_1
    //   50: ldc_w 522
    //   53: aastore
    //   54: ldc_w 775
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
    //   72: invokevirtual 467	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   75: astore 5
    //   77: aload 5
    //   79: astore_2
    //   80: aload 5
    //   82: invokeinterface 237 1 0
    //   87: ifne +26 -> 113
    //   90: aload 5
    //   92: astore_2
    //   93: invokestatic 778	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   96: astore 6
    //   98: aload 5
    //   100: ifnull +10 -> 110
    //   103: aload 5
    //   105: invokeinterface 244 1 0
    //   110: aload 6
    //   112: areturn
    //   113: aload 5
    //   115: astore_2
    //   116: aload 5
    //   118: iconst_1
    //   119: invokeinterface 782 2 0
    //   124: invokestatic 788	com/google/android/gms/internal/zzbul:zzad	([B)Lcom/google/android/gms/internal/zzbul;
    //   127: astore 6
    //   129: aload 5
    //   131: astore_2
    //   132: new 437	com/google/android/gms/internal/zzauf$zzb
    //   135: dup
    //   136: invokespecial 789	com/google/android/gms/internal/zzauf$zzb:<init>	()V
    //   139: astore 9
    //   141: aload 5
    //   143: astore_2
    //   144: aload 9
    //   146: aload 6
    //   148: invokevirtual 792	com/google/android/gms/internal/zzauf$zzb:zzb	(Lcom/google/android/gms/internal/zzbul;)Lcom/google/android/gms/internal/zzbut;
    //   151: pop
    //   152: aload 5
    //   154: astore_2
    //   155: aload 5
    //   157: iconst_0
    //   158: invokeinterface 795 2 0
    //   163: istore_3
    //   164: aload 5
    //   166: astore_2
    //   167: aload 8
    //   169: iload_3
    //   170: invokestatic 483	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   173: invokeinterface 797 2 0
    //   178: checkcast 605	java/util/List
    //   181: astore 7
    //   183: aload 7
    //   185: astore 6
    //   187: aload 7
    //   189: ifnonnull +32 -> 221
    //   192: aload 5
    //   194: astore_2
    //   195: new 799	java/util/ArrayList
    //   198: dup
    //   199: invokespecial 800	java/util/ArrayList:<init>	()V
    //   202: astore 6
    //   204: aload 5
    //   206: astore_2
    //   207: aload 8
    //   209: iload_3
    //   210: invokestatic 483	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   213: aload 6
    //   215: invokeinterface 43 3 0
    //   220: pop
    //   221: aload 5
    //   223: astore_2
    //   224: aload 6
    //   226: aload 9
    //   228: invokeinterface 803 2 0
    //   233: pop
    //   234: aload 5
    //   236: astore_2
    //   237: aload 5
    //   239: invokeinterface 742 1 0
    //   244: istore 4
    //   246: iload 4
    //   248: ifne -135 -> 113
    //   251: aload 5
    //   253: ifnull +10 -> 263
    //   256: aload 5
    //   258: invokeinterface 244 1 0
    //   263: aload 8
    //   265: areturn
    //   266: astore 6
    //   268: aload 5
    //   270: astore_2
    //   271: aload_0
    //   272: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   275: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   278: ldc_w 805
    //   281: aload_1
    //   282: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   285: aload 6
    //   287: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   290: goto -56 -> 234
    //   293: astore 6
    //   295: aload 5
    //   297: astore_2
    //   298: aload_0
    //   299: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   302: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   305: ldc_w 807
    //   308: aload_1
    //   309: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   312: aload 6
    //   314: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   317: aload 5
    //   319: ifnull +10 -> 329
    //   322: aload 5
    //   324: invokeinterface 244 1 0
    //   329: aconst_null
    //   330: areturn
    //   331: astore_1
    //   332: aconst_null
    //   333: astore_2
    //   334: aload_2
    //   335: ifnull +9 -> 344
    //   338: aload_2
    //   339: invokeinterface 244 1 0
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
    //   0	358	0	this	zzasu
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
    //   139	88	9	localzzb	zzauf.zzb
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
  Map<Integer, List<zzauf.zze>> zzT(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
    //   4: aload_0
    //   5: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   8: aload_1
    //   9: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_2
    //   14: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   17: pop
    //   18: new 27	android/support/v4/util/ArrayMap
    //   21: dup
    //   22: invokespecial 773	android/support/v4/util/ArrayMap:<init>	()V
    //   25: astore 8
    //   27: aload_0
    //   28: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   31: astore 5
    //   33: aload 5
    //   35: ldc_w 550
    //   38: iconst_2
    //   39: anewarray 322	java/lang/String
    //   42: dup
    //   43: iconst_0
    //   44: ldc_w 513
    //   47: aastore
    //   48: dup
    //   49: iconst_1
    //   50: ldc_w 522
    //   53: aastore
    //   54: ldc_w 811
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
    //   72: invokevirtual 467	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   75: astore 5
    //   77: aload 5
    //   79: astore_2
    //   80: aload 5
    //   82: invokeinterface 237 1 0
    //   87: ifne +26 -> 113
    //   90: aload 5
    //   92: astore_2
    //   93: invokestatic 778	java/util/Collections:emptyMap	()Ljava/util/Map;
    //   96: astore 6
    //   98: aload 5
    //   100: ifnull +10 -> 110
    //   103: aload 5
    //   105: invokeinterface 244 1 0
    //   110: aload 6
    //   112: areturn
    //   113: aload 5
    //   115: astore_2
    //   116: aload 5
    //   118: iconst_1
    //   119: invokeinterface 782 2 0
    //   124: invokestatic 788	com/google/android/gms/internal/zzbul:zzad	([B)Lcom/google/android/gms/internal/zzbul;
    //   127: astore 6
    //   129: aload 5
    //   131: astore_2
    //   132: new 444	com/google/android/gms/internal/zzauf$zze
    //   135: dup
    //   136: invokespecial 812	com/google/android/gms/internal/zzauf$zze:<init>	()V
    //   139: astore 9
    //   141: aload 5
    //   143: astore_2
    //   144: aload 9
    //   146: aload 6
    //   148: invokevirtual 813	com/google/android/gms/internal/zzauf$zze:zzb	(Lcom/google/android/gms/internal/zzbul;)Lcom/google/android/gms/internal/zzbut;
    //   151: pop
    //   152: aload 5
    //   154: astore_2
    //   155: aload 5
    //   157: iconst_0
    //   158: invokeinterface 795 2 0
    //   163: istore_3
    //   164: aload 5
    //   166: astore_2
    //   167: aload 8
    //   169: iload_3
    //   170: invokestatic 483	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   173: invokeinterface 797 2 0
    //   178: checkcast 605	java/util/List
    //   181: astore 7
    //   183: aload 7
    //   185: astore 6
    //   187: aload 7
    //   189: ifnonnull +32 -> 221
    //   192: aload 5
    //   194: astore_2
    //   195: new 799	java/util/ArrayList
    //   198: dup
    //   199: invokespecial 800	java/util/ArrayList:<init>	()V
    //   202: astore 6
    //   204: aload 5
    //   206: astore_2
    //   207: aload 8
    //   209: iload_3
    //   210: invokestatic 483	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   213: aload 6
    //   215: invokeinterface 43 3 0
    //   220: pop
    //   221: aload 5
    //   223: astore_2
    //   224: aload 6
    //   226: aload 9
    //   228: invokeinterface 803 2 0
    //   233: pop
    //   234: aload 5
    //   236: astore_2
    //   237: aload 5
    //   239: invokeinterface 742 1 0
    //   244: istore 4
    //   246: iload 4
    //   248: ifne -135 -> 113
    //   251: aload 5
    //   253: ifnull +10 -> 263
    //   256: aload 5
    //   258: invokeinterface 244 1 0
    //   263: aload 8
    //   265: areturn
    //   266: astore 6
    //   268: aload 5
    //   270: astore_2
    //   271: aload_0
    //   272: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   275: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   278: ldc_w 815
    //   281: aload_1
    //   282: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   285: aload 6
    //   287: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   290: goto -56 -> 234
    //   293: astore 6
    //   295: aload 5
    //   297: astore_2
    //   298: aload_0
    //   299: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   302: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   305: ldc_w 807
    //   308: aload_1
    //   309: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   312: aload 6
    //   314: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   317: aload 5
    //   319: ifnull +10 -> 329
    //   322: aload 5
    //   324: invokeinterface 244 1 0
    //   329: aconst_null
    //   330: areturn
    //   331: astore_1
    //   332: aconst_null
    //   333: astore_2
    //   334: aload_2
    //   335: ifnull +9 -> 344
    //   338: aload_2
    //   339: invokeinterface 244 1 0
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
    //   0	358	0	this	zzasu
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
    //   139	88	9	localzze	zzauf.zze
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
  protected long zzU(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_2
    //   6: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   9: pop
    //   10: aload_0
    //   11: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   14: aload_0
    //   15: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
    //   18: aload_0
    //   19: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   22: astore 8
    //   24: aload 8
    //   26: invokevirtual 580	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   29: aload_0
    //   30: new 334	java/lang/StringBuilder
    //   33: dup
    //   34: aload_2
    //   35: invokestatic 338	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   38: invokevirtual 341	java/lang/String:length	()I
    //   41: bipush 32
    //   43: iadd
    //   44: invokespecial 342	java/lang/StringBuilder:<init>	(I)V
    //   47: ldc_w 820
    //   50: invokevirtual 348	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: aload_2
    //   54: invokevirtual 348	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: ldc_w 822
    //   60: invokevirtual 348	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   63: invokevirtual 353	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   66: iconst_1
    //   67: anewarray 322	java/lang/String
    //   70: dup
    //   71: iconst_0
    //   72: aload_1
    //   73: aastore
    //   74: ldc2_w 532
    //   77: invokespecial 708	com/google/android/gms/internal/zzasu:zza	(Ljava/lang/String;[Ljava/lang/String;J)J
    //   80: lstore 5
    //   82: lload 5
    //   84: lstore_3
    //   85: lload 5
    //   87: ldc2_w 532
    //   90: lcmp
    //   91: ifne +92 -> 183
    //   94: new 504	android/content/ContentValues
    //   97: dup
    //   98: invokespecial 506	android/content/ContentValues:<init>	()V
    //   101: astore 7
    //   103: aload 7
    //   105: ldc_w 508
    //   108: aload_1
    //   109: invokevirtual 511	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   112: aload 7
    //   114: ldc_w 824
    //   117: iconst_0
    //   118: invokestatic 483	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   121: invokevirtual 516	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   124: aload 7
    //   126: ldc 127
    //   128: iconst_0
    //   129: invokestatic 483	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   132: invokevirtual 516	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   135: aload 8
    //   137: ldc_w 826
    //   140: aconst_null
    //   141: aload 7
    //   143: iconst_5
    //   144: invokevirtual 531	android/database/sqlite/SQLiteDatabase:insertWithOnConflict	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    //   147: ldc2_w 532
    //   150: lcmp
    //   151: ifne +30 -> 181
    //   154: aload_0
    //   155: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   158: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   161: ldc_w 828
    //   164: aload_1
    //   165: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   168: aload_2
    //   169: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   172: aload 8
    //   174: invokevirtual 583	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   177: ldc2_w 532
    //   180: lreturn
    //   181: lconst_0
    //   182: lstore_3
    //   183: new 504	android/content/ContentValues
    //   186: dup
    //   187: invokespecial 506	android/content/ContentValues:<init>	()V
    //   190: astore 7
    //   192: aload 7
    //   194: ldc_w 508
    //   197: aload_1
    //   198: invokevirtual 511	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   201: aload 7
    //   203: aload_2
    //   204: lconst_1
    //   205: lload_3
    //   206: ladd
    //   207: invokestatic 831	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   210: invokevirtual 834	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   213: aload 8
    //   215: ldc_w 826
    //   218: aload 7
    //   220: ldc_w 836
    //   223: iconst_1
    //   224: anewarray 322	java/lang/String
    //   227: dup
    //   228: iconst_0
    //   229: aload_1
    //   230: aastore
    //   231: invokevirtual 840	android/database/sqlite/SQLiteDatabase:update	(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   234: i2l
    //   235: lconst_0
    //   236: lcmp
    //   237: ifne +30 -> 267
    //   240: aload_0
    //   241: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   244: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   247: ldc_w 842
    //   250: aload_1
    //   251: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   254: aload_2
    //   255: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   258: aload 8
    //   260: invokevirtual 583	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   263: ldc2_w 532
    //   266: lreturn
    //   267: aload 8
    //   269: invokevirtual 589	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   272: aload 8
    //   274: invokevirtual 583	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   277: lload_3
    //   278: lreturn
    //   279: astore 7
    //   281: lconst_0
    //   282: lstore_3
    //   283: aload_0
    //   284: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   287: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   290: ldc_w 844
    //   293: aload_1
    //   294: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   297: aload_2
    //   298: aload 7
    //   300: invokevirtual 487	com/google/android/gms/internal/zzati$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   303: aload 8
    //   305: invokevirtual 583	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   308: lload_3
    //   309: lreturn
    //   310: astore_1
    //   311: aload 8
    //   313: invokevirtual 583	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   316: aload_1
    //   317: athrow
    //   318: astore 7
    //   320: goto -37 -> 283
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	323	0	this	zzasu
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
  public long zza(zzauh.zze paramzze)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   4: aload_0
    //   5: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
    //   8: aload_1
    //   9: invokestatic 410	com/google/android/gms/common/internal/zzac:zzw	(Ljava/lang/Object;)Ljava/lang/Object;
    //   12: pop
    //   13: aload_1
    //   14: getfield 850	com/google/android/gms/internal/zzauh$zze:zzaR	Ljava/lang/String;
    //   17: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   20: pop
    //   21: aload_1
    //   22: invokevirtual 851	com/google/android/gms/internal/zzauh$zze:zzacZ	()I
    //   25: newarray <illegal type>
    //   27: astore 4
    //   29: aload 4
    //   31: invokestatic 496	com/google/android/gms/internal/zzbum:zzae	([B)Lcom/google/android/gms/internal/zzbum;
    //   34: astore 5
    //   36: aload_1
    //   37: aload 5
    //   39: invokevirtual 852	com/google/android/gms/internal/zzauh$zze:zza	(Lcom/google/android/gms/internal/zzbum;)V
    //   42: aload 5
    //   44: invokevirtual 502	com/google/android/gms/internal/zzbum:zzacM	()V
    //   47: aload_0
    //   48: invokevirtual 856	com/google/android/gms/internal/zzasu:zzJp	()Lcom/google/android/gms/internal/zzaue;
    //   51: aload 4
    //   53: invokevirtual 862	com/google/android/gms/internal/zzaue:zzz	([B)J
    //   56: lstore_2
    //   57: new 504	android/content/ContentValues
    //   60: dup
    //   61: invokespecial 506	android/content/ContentValues:<init>	()V
    //   64: astore 5
    //   66: aload 5
    //   68: ldc_w 508
    //   71: aload_1
    //   72: getfield 850	com/google/android/gms/internal/zzauh$zze:zzaR	Ljava/lang/String;
    //   75: invokevirtual 511	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   78: aload 5
    //   80: ldc_w 864
    //   83: lload_2
    //   84: invokestatic 831	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   87: invokevirtual 834	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   90: aload 5
    //   92: ldc_w 866
    //   95: aload 4
    //   97: invokevirtual 525	android/content/ContentValues:put	(Ljava/lang/String;[B)V
    //   100: aload_0
    //   101: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   104: ldc_w 868
    //   107: aconst_null
    //   108: aload 5
    //   110: iconst_4
    //   111: invokevirtual 531	android/database/sqlite/SQLiteDatabase:insertWithOnConflict	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    //   114: pop2
    //   115: lload_2
    //   116: lreturn
    //   117: astore 4
    //   119: aload_0
    //   120: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   123: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   126: ldc_w 870
    //   129: aload_1
    //   130: getfield 850	com/google/android/gms/internal/zzauh$zze:zzaR	Ljava/lang/String;
    //   133: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   136: aload 4
    //   138: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   141: aload 4
    //   143: athrow
    //   144: astore 4
    //   146: aload_0
    //   147: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   150: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   153: ldc_w 872
    //   156: aload_1
    //   157: getfield 850	com/google/android/gms/internal/zzauh$zze:zzaR	Ljava/lang/String;
    //   160: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   163: aload 4
    //   165: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   168: aload 4
    //   170: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	171	0	this	zzasu
    //   0	171	1	paramzze	zzauh.zze
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
    //   1: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   9: aload_0
    //   10: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
    //   13: new 6	com/google/android/gms/internal/zzasu$zza
    //   16: dup
    //   17: invokespecial 874	com/google/android/gms/internal/zzasu$zza:<init>	()V
    //   20: astore 12
    //   22: aload_0
    //   23: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   26: astore 11
    //   28: aload 11
    //   30: ldc_w 876
    //   33: bipush 6
    //   35: anewarray 322	java/lang/String
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
    //   68: ldc_w 878
    //   71: iconst_1
    //   72: anewarray 322	java/lang/String
    //   75: dup
    //   76: iconst_0
    //   77: aload_3
    //   78: aastore
    //   79: aconst_null
    //   80: aconst_null
    //   81: aconst_null
    //   82: invokevirtual 467	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   85: astore 10
    //   87: aload 10
    //   89: astore 9
    //   91: aload 10
    //   93: invokeinterface 237 1 0
    //   98: ifne +39 -> 137
    //   101: aload 10
    //   103: astore 9
    //   105: aload_0
    //   106: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   109: invokevirtual 282	com/google/android/gms/internal/zzati:zzLc	()Lcom/google/android/gms/internal/zzati$zza;
    //   112: ldc_w 880
    //   115: aload_3
    //   116: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   119: invokevirtual 312	com/google/android/gms/internal/zzati$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   122: aload 10
    //   124: ifnull +10 -> 134
    //   127: aload 10
    //   129: invokeinterface 244 1 0
    //   134: aload 12
    //   136: areturn
    //   137: aload 10
    //   139: astore 9
    //   141: aload 10
    //   143: iconst_0
    //   144: invokeinterface 241 2 0
    //   149: lload_1
    //   150: lcmp
    //   151: ifne +88 -> 239
    //   154: aload 10
    //   156: astore 9
    //   158: aload 12
    //   160: aload 10
    //   162: iconst_1
    //   163: invokeinterface 241 2 0
    //   168: putfield 884	com/google/android/gms/internal/zzasu$zza:zzbqw	J
    //   171: aload 10
    //   173: astore 9
    //   175: aload 12
    //   177: aload 10
    //   179: iconst_2
    //   180: invokeinterface 241 2 0
    //   185: putfield 887	com/google/android/gms/internal/zzasu$zza:zzbqv	J
    //   188: aload 10
    //   190: astore 9
    //   192: aload 12
    //   194: aload 10
    //   196: iconst_3
    //   197: invokeinterface 241 2 0
    //   202: putfield 890	com/google/android/gms/internal/zzasu$zza:zzbqx	J
    //   205: aload 10
    //   207: astore 9
    //   209: aload 12
    //   211: aload 10
    //   213: iconst_4
    //   214: invokeinterface 241 2 0
    //   219: putfield 893	com/google/android/gms/internal/zzasu$zza:zzbqy	J
    //   222: aload 10
    //   224: astore 9
    //   226: aload 12
    //   228: aload 10
    //   230: iconst_5
    //   231: invokeinterface 241 2 0
    //   236: putfield 896	com/google/android/gms/internal/zzasu$zza:zzbqz	J
    //   239: iload 4
    //   241: ifeq +19 -> 260
    //   244: aload 10
    //   246: astore 9
    //   248: aload 12
    //   250: aload 12
    //   252: getfield 884	com/google/android/gms/internal/zzasu$zza:zzbqw	J
    //   255: lconst_1
    //   256: ladd
    //   257: putfield 884	com/google/android/gms/internal/zzasu$zza:zzbqw	J
    //   260: iload 5
    //   262: ifeq +19 -> 281
    //   265: aload 10
    //   267: astore 9
    //   269: aload 12
    //   271: aload 12
    //   273: getfield 887	com/google/android/gms/internal/zzasu$zza:zzbqv	J
    //   276: lconst_1
    //   277: ladd
    //   278: putfield 887	com/google/android/gms/internal/zzasu$zza:zzbqv	J
    //   281: iload 6
    //   283: ifeq +19 -> 302
    //   286: aload 10
    //   288: astore 9
    //   290: aload 12
    //   292: aload 12
    //   294: getfield 890	com/google/android/gms/internal/zzasu$zza:zzbqx	J
    //   297: lconst_1
    //   298: ladd
    //   299: putfield 890	com/google/android/gms/internal/zzasu$zza:zzbqx	J
    //   302: iload 7
    //   304: ifeq +19 -> 323
    //   307: aload 10
    //   309: astore 9
    //   311: aload 12
    //   313: aload 12
    //   315: getfield 893	com/google/android/gms/internal/zzasu$zza:zzbqy	J
    //   318: lconst_1
    //   319: ladd
    //   320: putfield 893	com/google/android/gms/internal/zzasu$zza:zzbqy	J
    //   323: iload 8
    //   325: ifeq +19 -> 344
    //   328: aload 10
    //   330: astore 9
    //   332: aload 12
    //   334: aload 12
    //   336: getfield 896	com/google/android/gms/internal/zzasu$zza:zzbqz	J
    //   339: lconst_1
    //   340: ladd
    //   341: putfield 896	com/google/android/gms/internal/zzasu$zza:zzbqz	J
    //   344: aload 10
    //   346: astore 9
    //   348: new 504	android/content/ContentValues
    //   351: dup
    //   352: invokespecial 506	android/content/ContentValues:<init>	()V
    //   355: astore 13
    //   357: aload 10
    //   359: astore 9
    //   361: aload 13
    //   363: ldc 65
    //   365: lload_1
    //   366: invokestatic 831	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   369: invokevirtual 834	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   372: aload 10
    //   374: astore 9
    //   376: aload 13
    //   378: ldc 69
    //   380: aload 12
    //   382: getfield 887	com/google/android/gms/internal/zzasu$zza:zzbqv	J
    //   385: invokestatic 831	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   388: invokevirtual 834	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   391: aload 10
    //   393: astore 9
    //   395: aload 13
    //   397: ldc 73
    //   399: aload 12
    //   401: getfield 884	com/google/android/gms/internal/zzasu$zza:zzbqw	J
    //   404: invokestatic 831	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   407: invokevirtual 834	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   410: aload 10
    //   412: astore 9
    //   414: aload 13
    //   416: ldc 77
    //   418: aload 12
    //   420: getfield 890	com/google/android/gms/internal/zzasu$zza:zzbqx	J
    //   423: invokestatic 831	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   426: invokevirtual 834	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   429: aload 10
    //   431: astore 9
    //   433: aload 13
    //   435: ldc 101
    //   437: aload 12
    //   439: getfield 893	com/google/android/gms/internal/zzasu$zza:zzbqy	J
    //   442: invokestatic 831	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   445: invokevirtual 834	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   448: aload 10
    //   450: astore 9
    //   452: aload 13
    //   454: ldc 105
    //   456: aload 12
    //   458: getfield 896	com/google/android/gms/internal/zzasu$zza:zzbqz	J
    //   461: invokestatic 831	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   464: invokevirtual 834	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   467: aload 10
    //   469: astore 9
    //   471: aload 11
    //   473: ldc_w 876
    //   476: aload 13
    //   478: ldc_w 878
    //   481: iconst_1
    //   482: anewarray 322	java/lang/String
    //   485: dup
    //   486: iconst_0
    //   487: aload_3
    //   488: aastore
    //   489: invokevirtual 840	android/database/sqlite/SQLiteDatabase:update	(Ljava/lang/String;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   492: pop
    //   493: aload 10
    //   495: ifnull +10 -> 505
    //   498: aload 10
    //   500: invokeinterface 244 1 0
    //   505: aload 12
    //   507: areturn
    //   508: astore 11
    //   510: aconst_null
    //   511: astore 10
    //   513: aload 10
    //   515: astore 9
    //   517: aload_0
    //   518: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   521: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   524: ldc_w 898
    //   527: aload_3
    //   528: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   531: aload 11
    //   533: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   536: aload 10
    //   538: ifnull +10 -> 548
    //   541: aload 10
    //   543: invokeinterface 244 1 0
    //   548: aload 12
    //   550: areturn
    //   551: astore_3
    //   552: aconst_null
    //   553: astore 9
    //   555: aload 9
    //   557: ifnull +10 -> 567
    //   560: aload 9
    //   562: invokeinterface 244 1 0
    //   567: aload_3
    //   568: athrow
    //   569: astore_3
    //   570: goto -15 -> 555
    //   573: astore 11
    //   575: goto -62 -> 513
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	578	0	this	zzasu
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
    zzac.zzdv(paramString);
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
  public void zza(zzasp paramzzasp)
  {
    zzac.zzw(paramzzasp);
    zzmq();
    zznA();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzasp.zzjI());
    localContentValues.put("app_instance_id", paramzzasp.getAppInstanceId());
    localContentValues.put("gmp_app_id", paramzzasp.getGmpAppId());
    localContentValues.put("resettable_device_id_hash", paramzzasp.zzJx());
    localContentValues.put("last_bundle_index", Long.valueOf(paramzzasp.zzJG()));
    localContentValues.put("last_bundle_start_timestamp", Long.valueOf(paramzzasp.zzJz()));
    localContentValues.put("last_bundle_end_timestamp", Long.valueOf(paramzzasp.zzJA()));
    localContentValues.put("app_version", paramzzasp.zzmy());
    localContentValues.put("app_store", paramzzasp.zzJC());
    localContentValues.put("gmp_version", Long.valueOf(paramzzasp.zzJD()));
    localContentValues.put("dev_cert_hash", Long.valueOf(paramzzasp.zzJE()));
    localContentValues.put("measurement_enabled", Boolean.valueOf(paramzzasp.zzJF()));
    localContentValues.put("day", Long.valueOf(paramzzasp.zzJK()));
    localContentValues.put("daily_public_events_count", Long.valueOf(paramzzasp.zzJL()));
    localContentValues.put("daily_events_count", Long.valueOf(paramzzasp.zzJM()));
    localContentValues.put("daily_conversions_count", Long.valueOf(paramzzasp.zzJN()));
    localContentValues.put("config_fetched_time", Long.valueOf(paramzzasp.zzJH()));
    localContentValues.put("failed_config_fetch_time", Long.valueOf(paramzzasp.zzJI()));
    localContentValues.put("app_version_int", Long.valueOf(paramzzasp.zzJB()));
    localContentValues.put("firebase_instance_id", paramzzasp.zzJy());
    localContentValues.put("daily_error_events_count", Long.valueOf(paramzzasp.zzJP()));
    localContentValues.put("daily_realtime_events_count", Long.valueOf(paramzzasp.zzJO()));
    localContentValues.put("health_monitor_sample", paramzzasp.zzJQ());
    try
    {
      if (getWritableDatabase().insertWithOnConflict("apps", null, localContentValues, 5) == -1L) {
        zzJt().zzLa().zzj("Failed to insert/update app (got -1). appId", zzati.zzfI(paramzzasp.zzjI()));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzJt().zzLa().zze("Error storing app. appId", zzati.zzfI(paramzzasp.zzjI()), localSQLiteException);
    }
  }
  
  /* Error */
  public void zza(zzasx paramzzasx, long paramLong, boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 6
    //   3: aload_0
    //   4: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   7: aload_0
    //   8: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
    //   11: aload_1
    //   12: invokestatic 410	com/google/android/gms/common/internal/zzac:zzw	(Ljava/lang/Object;)Ljava/lang/Object;
    //   15: pop
    //   16: aload_1
    //   17: getfield 1006	com/google/android/gms/internal/zzasx:zzVQ	Ljava/lang/String;
    //   20: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   23: pop
    //   24: new 1008	com/google/android/gms/internal/zzauh$zzb
    //   27: dup
    //   28: invokespecial 1009	com/google/android/gms/internal/zzauh$zzb:<init>	()V
    //   31: astore 7
    //   33: aload 7
    //   35: aload_1
    //   36: getfield 1012	com/google/android/gms/internal/zzasx:zzbqH	J
    //   39: invokestatic 831	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   42: putfield 1016	com/google/android/gms/internal/zzauh$zzb:zzbvX	Ljava/lang/Long;
    //   45: aload 7
    //   47: aload_1
    //   48: getfield 1020	com/google/android/gms/internal/zzasx:zzbqI	Lcom/google/android/gms/internal/zzasz;
    //   51: invokevirtual 1023	com/google/android/gms/internal/zzasz:size	()I
    //   54: anewarray 1025	com/google/android/gms/internal/zzauh$zzc
    //   57: putfield 1029	com/google/android/gms/internal/zzauh$zzb:zzbvV	[Lcom/google/android/gms/internal/zzauh$zzc;
    //   60: aload_1
    //   61: getfield 1020	com/google/android/gms/internal/zzasx:zzbqI	Lcom/google/android/gms/internal/zzasz;
    //   64: invokevirtual 1030	com/google/android/gms/internal/zzasz:iterator	()Ljava/util/Iterator;
    //   67: astore 8
    //   69: iconst_0
    //   70: istore 5
    //   72: aload 8
    //   74: invokeinterface 367 1 0
    //   79: ifeq +72 -> 151
    //   82: aload 8
    //   84: invokeinterface 371 1 0
    //   89: checkcast 322	java/lang/String
    //   92: astore 10
    //   94: new 1025	com/google/android/gms/internal/zzauh$zzc
    //   97: dup
    //   98: invokespecial 1031	com/google/android/gms/internal/zzauh$zzc:<init>	()V
    //   101: astore 9
    //   103: aload 7
    //   105: getfield 1029	com/google/android/gms/internal/zzauh$zzb:zzbvV	[Lcom/google/android/gms/internal/zzauh$zzc;
    //   108: iload 5
    //   110: aload 9
    //   112: aastore
    //   113: aload 9
    //   115: aload 10
    //   117: putfield 1033	com/google/android/gms/internal/zzauh$zzc:name	Ljava/lang/String;
    //   120: aload_1
    //   121: getfield 1020	com/google/android/gms/internal/zzasx:zzbqI	Lcom/google/android/gms/internal/zzasz;
    //   124: aload 10
    //   126: invokevirtual 1035	com/google/android/gms/internal/zzasz:get	(Ljava/lang/String;)Ljava/lang/Object;
    //   129: astore 10
    //   131: aload_0
    //   132: invokevirtual 856	com/google/android/gms/internal/zzasu:zzJp	()Lcom/google/android/gms/internal/zzaue;
    //   135: aload 9
    //   137: aload 10
    //   139: invokevirtual 1038	com/google/android/gms/internal/zzaue:zza	(Lcom/google/android/gms/internal/zzauh$zzc;Ljava/lang/Object;)V
    //   142: iload 5
    //   144: iconst_1
    //   145: iadd
    //   146: istore 5
    //   148: goto -76 -> 72
    //   151: aload 7
    //   153: invokevirtual 1039	com/google/android/gms/internal/zzauh$zzb:zzacZ	()I
    //   156: newarray <illegal type>
    //   158: astore 8
    //   160: aload 8
    //   162: invokestatic 496	com/google/android/gms/internal/zzbum:zzae	([B)Lcom/google/android/gms/internal/zzbum;
    //   165: astore 9
    //   167: aload 7
    //   169: aload 9
    //   171: invokevirtual 1040	com/google/android/gms/internal/zzauh$zzb:zza	(Lcom/google/android/gms/internal/zzbum;)V
    //   174: aload 9
    //   176: invokevirtual 502	com/google/android/gms/internal/zzbum:zzacM	()V
    //   179: aload_0
    //   180: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   183: invokevirtual 701	com/google/android/gms/internal/zzati:zzLg	()Lcom/google/android/gms/internal/zzati$zza;
    //   186: ldc_w 1042
    //   189: aload_1
    //   190: getfield 1045	com/google/android/gms/internal/zzasx:mName	Ljava/lang/String;
    //   193: aload 8
    //   195: arraylength
    //   196: invokestatic 483	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   199: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   202: new 504	android/content/ContentValues
    //   205: dup
    //   206: invokespecial 506	android/content/ContentValues:<init>	()V
    //   209: astore 7
    //   211: aload 7
    //   213: ldc_w 508
    //   216: aload_1
    //   217: getfield 1006	com/google/android/gms/internal/zzasx:zzVQ	Ljava/lang/String;
    //   220: invokevirtual 511	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   223: aload 7
    //   225: ldc_w 461
    //   228: aload_1
    //   229: getfield 1045	com/google/android/gms/internal/zzasx:mName	Ljava/lang/String;
    //   232: invokevirtual 511	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   235: aload 7
    //   237: ldc_w 1047
    //   240: aload_1
    //   241: getfield 1050	com/google/android/gms/internal/zzasx:zzavX	J
    //   244: invokestatic 831	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   247: invokevirtual 834	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   250: aload 7
    //   252: ldc_w 864
    //   255: lload_2
    //   256: invokestatic 831	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   259: invokevirtual 834	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   262: aload 7
    //   264: ldc_w 522
    //   267: aload 8
    //   269: invokevirtual 525	android/content/ContentValues:put	(Ljava/lang/String;[B)V
    //   272: iload 6
    //   274: istore 5
    //   276: iload 4
    //   278: ifeq +6 -> 284
    //   281: iconst_1
    //   282: istore 5
    //   284: aload 7
    //   286: ldc 115
    //   288: iload 5
    //   290: invokestatic 483	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   293: invokevirtual 516	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   296: aload_0
    //   297: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   300: ldc_w 625
    //   303: aconst_null
    //   304: aload 7
    //   306: invokevirtual 1054	android/database/sqlite/SQLiteDatabase:insert	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;)J
    //   309: ldc2_w 532
    //   312: lcmp
    //   313: ifne +23 -> 336
    //   316: aload_0
    //   317: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   320: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   323: ldc_w 1056
    //   326: aload_1
    //   327: getfield 1006	com/google/android/gms/internal/zzasx:zzVQ	Ljava/lang/String;
    //   330: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   333: invokevirtual 312	com/google/android/gms/internal/zzati$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   336: return
    //   337: astore 7
    //   339: aload_0
    //   340: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   343: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   346: ldc_w 1058
    //   349: aload_1
    //   350: getfield 1006	com/google/android/gms/internal/zzasx:zzVQ	Ljava/lang/String;
    //   353: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   356: aload 7
    //   358: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   361: return
    //   362: astore 7
    //   364: aload_0
    //   365: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   368: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   371: ldc_w 1060
    //   374: aload_1
    //   375: getfield 1006	com/google/android/gms/internal/zzasx:zzVQ	Ljava/lang/String;
    //   378: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   381: aload 7
    //   383: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   386: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	387	0	this	zzasu
    //   0	387	1	paramzzasx	zzasx
    //   0	387	2	paramLong	long
    //   0	387	4	paramBoolean	boolean
    //   70	219	5	i	int
    //   1	272	6	j	int
    //   31	274	7	localObject1	Object
    //   337	20	7	localIOException	IOException
    //   362	20	7	localSQLiteException	SQLiteException
    //   67	201	8	localObject2	Object
    //   101	74	9	localObject3	Object
    //   92	46	10	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   151	179	337	java/io/IOException
    //   296	336	362	android/database/sqlite/SQLiteException
  }
  
  @WorkerThread
  public void zza(zzasy paramzzasy)
  {
    zzac.zzw(paramzzasy);
    zzmq();
    zznA();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzasy.zzVQ);
    localContentValues.put("name", paramzzasy.mName);
    localContentValues.put("lifetime_count", Long.valueOf(paramzzasy.zzbqJ));
    localContentValues.put("current_bundle_count", Long.valueOf(paramzzasy.zzbqK));
    localContentValues.put("last_fire_timestamp", Long.valueOf(paramzzasy.zzbqL));
    try
    {
      if (getWritableDatabase().insertWithOnConflict("events", null, localContentValues, 5) == -1L) {
        zzJt().zzLa().zzj("Failed to insert/update event aggregates (got -1). appId", zzati.zzfI(paramzzasy.zzVQ));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzJt().zzLa().zze("Error storing event aggregates. appId", zzati.zzfI(paramzzasy.zzVQ), localSQLiteException);
    }
  }
  
  @WorkerThread
  public void zza(zzauh.zze paramzze, boolean paramBoolean)
  {
    zzmq();
    zznA();
    zzac.zzw(paramzze);
    zzac.zzdv(paramzze.zzaR);
    zzac.zzw(paramzze.zzbwh);
    zzKI();
    long l = zznq().currentTimeMillis();
    if ((paramzze.zzbwh.longValue() < l - zzJv().zzKn()) || (paramzze.zzbwh.longValue() > zzJv().zzKn() + l)) {
      zzJt().zzLc().zzd("Storing bundle outside of the max uploading time span. appId, now, timestamp", zzati.zzfI(paramzze.zzaR), Long.valueOf(l), paramzze.zzbwh);
    }
    for (;;)
    {
      try
      {
        byte[] arrayOfByte = new byte[paramzze.zzacZ()];
        Object localObject = zzbum.zzae(arrayOfByte);
        paramzze.zza((zzbum)localObject);
        ((zzbum)localObject).zzacM();
        arrayOfByte = zzJp().zzk(arrayOfByte);
        zzJt().zzLg().zzj("Saving bundle, size", Integer.valueOf(arrayOfByte.length));
        localObject = new ContentValues();
        ((ContentValues)localObject).put("app_id", paramzze.zzaR);
        ((ContentValues)localObject).put("bundle_end_timestamp", paramzze.zzbwh);
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
          if (getWritableDatabase().insert("queue", null, (ContentValues)localObject) == -1L) {
            zzJt().zzLa().zzj("Failed to insert bundle (got -1). appId", zzati.zzfI(paramzze.zzaR));
          }
          return;
        }
        catch (SQLiteException localSQLiteException)
        {
          zzJt().zzLa().zze("Error storing bundle. appId", zzati.zzfI(paramzze.zzaR), localSQLiteException);
        }
        localIOException = localIOException;
        zzJt().zzLa().zze("Data loss. Failed to serialize bundle. appId", zzati.zzfI(paramzze.zzaR), localIOException);
        return;
      }
    }
  }
  
  /* Error */
  void zza(String paramString, int paramInt, zzauh.zzf paramzzf)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
    //   4: aload_0
    //   5: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   8: aload_1
    //   9: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_3
    //   14: invokestatic 410	com/google/android/gms/common/internal/zzac:zzw	(Ljava/lang/Object;)Ljava/lang/Object;
    //   17: pop
    //   18: aload_3
    //   19: invokevirtual 1102	com/google/android/gms/internal/zzauh$zzf:zzacZ	()I
    //   22: newarray <illegal type>
    //   24: astore 4
    //   26: aload 4
    //   28: invokestatic 496	com/google/android/gms/internal/zzbum:zzae	([B)Lcom/google/android/gms/internal/zzbum;
    //   31: astore 5
    //   33: aload_3
    //   34: aload 5
    //   36: invokevirtual 1103	com/google/android/gms/internal/zzauh$zzf:zza	(Lcom/google/android/gms/internal/zzbum;)V
    //   39: aload 5
    //   41: invokevirtual 502	com/google/android/gms/internal/zzbum:zzacM	()V
    //   44: new 504	android/content/ContentValues
    //   47: dup
    //   48: invokespecial 506	android/content/ContentValues:<init>	()V
    //   51: astore_3
    //   52: aload_3
    //   53: ldc_w 508
    //   56: aload_1
    //   57: invokevirtual 511	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   60: aload_3
    //   61: ldc_w 513
    //   64: iload_2
    //   65: invokestatic 483	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   68: invokevirtual 516	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   71: aload_3
    //   72: ldc_w 1105
    //   75: aload 4
    //   77: invokevirtual 525	android/content/ContentValues:put	(Ljava/lang/String;[B)V
    //   80: aload_0
    //   81: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   84: ldc_w 1107
    //   87: aconst_null
    //   88: aload_3
    //   89: iconst_5
    //   90: invokevirtual 531	android/database/sqlite/SQLiteDatabase:insertWithOnConflict	(Ljava/lang/String;Ljava/lang/String;Landroid/content/ContentValues;I)J
    //   93: ldc2_w 532
    //   96: lcmp
    //   97: ifne +20 -> 117
    //   100: aload_0
    //   101: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   104: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   107: ldc_w 1109
    //   110: aload_1
    //   111: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   114: invokevirtual 312	com/google/android/gms/internal/zzati$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   117: return
    //   118: astore_3
    //   119: aload_0
    //   120: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   123: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   126: ldc_w 1111
    //   129: aload_1
    //   130: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   133: aload_3
    //   134: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   137: return
    //   138: astore_3
    //   139: aload_0
    //   140: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   143: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   146: ldc_w 1113
    //   149: aload_1
    //   150: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   153: aload_3
    //   154: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   157: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	158	0	this	zzasu
    //   0	158	1	paramString	String
    //   0	158	2	paramInt	int
    //   0	158	3	paramzzf	zzauh.zzf
    //   24	52	4	arrayOfByte	byte[]
    //   31	9	5	localzzbum	zzbum
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
    //   2: invokestatic 410	com/google/android/gms/common/internal/zzac:zzw	(Ljava/lang/Object;)Ljava/lang/Object;
    //   5: pop
    //   6: aload_0
    //   7: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   10: aload_0
    //   11: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
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
    //   32: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   35: astore 15
    //   37: aload 13
    //   39: astore 8
    //   41: aload 14
    //   43: astore 9
    //   45: aload_1
    //   46: astore 10
    //   48: aload_1
    //   49: invokestatic 478	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   52: ifeq +342 -> 394
    //   55: lload 4
    //   57: ldc2_w 532
    //   60: lcmp
    //   61: ifeq +135 -> 196
    //   64: aload 13
    //   66: astore 8
    //   68: aload 14
    //   70: astore 9
    //   72: aload_1
    //   73: astore 10
    //   75: iconst_2
    //   76: anewarray 322	java/lang/String
    //   79: dup
    //   80: iconst_0
    //   81: lload 4
    //   83: invokestatic 695	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   86: aastore
    //   87: dup
    //   88: iconst_1
    //   89: lload_2
    //   90: invokestatic 695	java/lang/String:valueOf	(J)Ljava/lang/String;
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
    //   112: new 334	java/lang/StringBuilder
    //   115: dup
    //   116: aload 12
    //   118: invokestatic 338	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   121: invokevirtual 341	java/lang/String:length	()I
    //   124: sipush 148
    //   127: iadd
    //   128: invokespecial 342	java/lang/StringBuilder:<init>	(I)V
    //   131: ldc_w 1116
    //   134: invokevirtual 348	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   137: aload 12
    //   139: invokevirtual 348	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   142: ldc_w 1118
    //   145: invokevirtual 348	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   148: invokevirtual 353	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   151: aload 11
    //   153: invokevirtual 234	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   156: astore 11
    //   158: aload 11
    //   160: astore 8
    //   162: aload 11
    //   164: astore 9
    //   166: aload_1
    //   167: astore 10
    //   169: aload 11
    //   171: invokeinterface 237 1 0
    //   176: istore 7
    //   178: iload 7
    //   180: ifne +43 -> 223
    //   183: aload 11
    //   185: ifnull +10 -> 195
    //   188: aload 11
    //   190: invokeinterface 244 1 0
    //   195: return
    //   196: aload 13
    //   198: astore 8
    //   200: aload 14
    //   202: astore 9
    //   204: aload_1
    //   205: astore 10
    //   207: iconst_1
    //   208: anewarray 322	java/lang/String
    //   211: dup
    //   212: iconst_0
    //   213: lload_2
    //   214: invokestatic 695	java/lang/String:valueOf	(J)Ljava/lang/String;
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
    //   237: invokeinterface 634 2 0
    //   242: astore_1
    //   243: aload 11
    //   245: astore 8
    //   247: aload 11
    //   249: astore 9
    //   251: aload_1
    //   252: astore 10
    //   254: aload 11
    //   256: iconst_1
    //   257: invokeinterface 634 2 0
    //   262: astore 12
    //   264: aload 11
    //   266: astore 8
    //   268: aload 11
    //   270: astore 9
    //   272: aload_1
    //   273: astore 10
    //   275: aload 11
    //   277: invokeinterface 244 1 0
    //   282: aload 12
    //   284: astore 10
    //   286: aload 11
    //   288: astore 8
    //   290: aload 8
    //   292: astore 9
    //   294: aload 15
    //   296: ldc_w 868
    //   299: iconst_1
    //   300: anewarray 322	java/lang/String
    //   303: dup
    //   304: iconst_0
    //   305: ldc_w 866
    //   308: aastore
    //   309: ldc_w 1120
    //   312: iconst_2
    //   313: anewarray 322	java/lang/String
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
    //   327: ldc_w 1122
    //   330: ldc_w 1124
    //   333: invokevirtual 1127	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   336: astore 11
    //   338: aload 11
    //   340: astore 9
    //   342: aload 11
    //   344: astore 8
    //   346: aload 11
    //   348: invokeinterface 237 1 0
    //   353: ifne +252 -> 605
    //   356: aload 11
    //   358: astore 9
    //   360: aload 11
    //   362: astore 8
    //   364: aload_0
    //   365: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   368: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   371: ldc_w 1129
    //   374: aload_1
    //   375: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   378: invokevirtual 312	com/google/android/gms/internal/zzati$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   381: aload 11
    //   383: ifnull -188 -> 195
    //   386: aload 11
    //   388: invokeinterface 244 1 0
    //   393: return
    //   394: lload 4
    //   396: ldc2_w 532
    //   399: lcmp
    //   400: ifeq +131 -> 531
    //   403: aload 13
    //   405: astore 8
    //   407: aload 14
    //   409: astore 9
    //   411: aload_1
    //   412: astore 10
    //   414: iconst_2
    //   415: anewarray 322	java/lang/String
    //   418: dup
    //   419: iconst_0
    //   420: aload_1
    //   421: aastore
    //   422: dup
    //   423: iconst_1
    //   424: lload 4
    //   426: invokestatic 695	java/lang/String:valueOf	(J)Ljava/lang/String;
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
    //   448: new 334	java/lang/StringBuilder
    //   451: dup
    //   452: aload 12
    //   454: invokestatic 338	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   457: invokevirtual 341	java/lang/String:length	()I
    //   460: bipush 84
    //   462: iadd
    //   463: invokespecial 342	java/lang/StringBuilder:<init>	(I)V
    //   466: ldc_w 1131
    //   469: invokevirtual 348	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   472: aload 12
    //   474: invokevirtual 348	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   477: ldc_w 1133
    //   480: invokevirtual 348	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   483: invokevirtual 353	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   486: aload 11
    //   488: invokevirtual 234	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   491: astore 11
    //   493: aload 11
    //   495: astore 8
    //   497: aload 11
    //   499: astore 9
    //   501: aload_1
    //   502: astore 10
    //   504: aload 11
    //   506: invokeinterface 237 1 0
    //   511: istore 7
    //   513: iload 7
    //   515: ifne +40 -> 555
    //   518: aload 11
    //   520: ifnull -325 -> 195
    //   523: aload 11
    //   525: invokeinterface 244 1 0
    //   530: return
    //   531: aload 13
    //   533: astore 8
    //   535: aload 14
    //   537: astore 9
    //   539: aload_1
    //   540: astore 10
    //   542: iconst_1
    //   543: anewarray 322	java/lang/String
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
    //   569: invokeinterface 634 2 0
    //   574: astore 12
    //   576: aload 11
    //   578: astore 8
    //   580: aload 11
    //   582: astore 9
    //   584: aload_1
    //   585: astore 10
    //   587: aload 11
    //   589: invokeinterface 244 1 0
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
    //   616: invokeinterface 782 2 0
    //   621: invokestatic 788	com/google/android/gms/internal/zzbul:zzad	([B)Lcom/google/android/gms/internal/zzbul;
    //   624: astore 12
    //   626: aload 11
    //   628: astore 9
    //   630: aload 11
    //   632: astore 8
    //   634: new 847	com/google/android/gms/internal/zzauh$zze
    //   637: dup
    //   638: invokespecial 1134	com/google/android/gms/internal/zzauh$zze:<init>	()V
    //   641: astore 13
    //   643: aload 11
    //   645: astore 9
    //   647: aload 11
    //   649: astore 8
    //   651: aload 13
    //   653: aload 12
    //   655: invokevirtual 1135	com/google/android/gms/internal/zzauh$zze:zzb	(Lcom/google/android/gms/internal/zzbul;)Lcom/google/android/gms/internal/zzbut;
    //   658: pop
    //   659: aload 11
    //   661: astore 9
    //   663: aload 11
    //   665: astore 8
    //   667: aload 11
    //   669: invokeinterface 742 1 0
    //   674: ifeq +28 -> 702
    //   677: aload 11
    //   679: astore 9
    //   681: aload 11
    //   683: astore 8
    //   685: aload_0
    //   686: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   689: invokevirtual 282	com/google/android/gms/internal/zzati:zzLc	()Lcom/google/android/gms/internal/zzati$zza;
    //   692: ldc_w 1137
    //   695: aload_1
    //   696: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   699: invokevirtual 312	com/google/android/gms/internal/zzati$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   702: aload 11
    //   704: astore 9
    //   706: aload 11
    //   708: astore 8
    //   710: aload 11
    //   712: invokeinterface 244 1 0
    //   717: aload 11
    //   719: astore 9
    //   721: aload 11
    //   723: astore 8
    //   725: aload 6
    //   727: aload 13
    //   729: invokeinterface 1140 2 0
    //   734: lload 4
    //   736: ldc2_w 532
    //   739: lcmp
    //   740: ifeq +214 -> 954
    //   743: ldc_w 1142
    //   746: astore 13
    //   748: aload 11
    //   750: astore 9
    //   752: aload 11
    //   754: astore 8
    //   756: iconst_3
    //   757: anewarray 322	java/lang/String
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
    //   786: invokestatic 695	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   789: aastore
    //   790: aload 13
    //   792: astore 10
    //   794: aload 11
    //   796: astore 9
    //   798: aload 11
    //   800: astore 8
    //   802: aload 15
    //   804: ldc_w 625
    //   807: iconst_4
    //   808: anewarray 322	java/lang/String
    //   811: dup
    //   812: iconst_0
    //   813: ldc_w 1122
    //   816: aastore
    //   817: dup
    //   818: iconst_1
    //   819: ldc_w 461
    //   822: aastore
    //   823: dup
    //   824: iconst_2
    //   825: ldc_w 1047
    //   828: aastore
    //   829: dup
    //   830: iconst_3
    //   831: ldc_w 522
    //   834: aastore
    //   835: aload 10
    //   837: aload 12
    //   839: aconst_null
    //   840: aconst_null
    //   841: ldc_w 1122
    //   844: aconst_null
    //   845: invokevirtual 1127	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   848: astore 11
    //   850: aload 11
    //   852: astore 8
    //   854: aload 11
    //   856: astore 9
    //   858: aload_1
    //   859: astore 10
    //   861: aload 11
    //   863: invokeinterface 237 1 0
    //   868: ifne +167 -> 1035
    //   871: aload 11
    //   873: astore 8
    //   875: aload 11
    //   877: astore 9
    //   879: aload_1
    //   880: astore 10
    //   882: aload_0
    //   883: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   886: invokevirtual 282	com/google/android/gms/internal/zzati:zzLc	()Lcom/google/android/gms/internal/zzati$zza;
    //   889: ldc_w 1144
    //   892: aload_1
    //   893: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   896: invokevirtual 312	com/google/android/gms/internal/zzati$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   899: aload 11
    //   901: ifnull -706 -> 195
    //   904: aload 11
    //   906: invokeinterface 244 1 0
    //   911: return
    //   912: astore 6
    //   914: aload 11
    //   916: astore 9
    //   918: aload 11
    //   920: astore 8
    //   922: aload_0
    //   923: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   926: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   929: ldc_w 1146
    //   932: aload_1
    //   933: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   936: aload 6
    //   938: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   941: aload 11
    //   943: ifnull -748 -> 195
    //   946: aload 11
    //   948: invokeinterface 244 1 0
    //   953: return
    //   954: ldc_w 1120
    //   957: astore 13
    //   959: aload 11
    //   961: astore 9
    //   963: aload 11
    //   965: astore 8
    //   967: iconst_2
    //   968: anewarray 322	java/lang/String
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
    //   1004: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   1007: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   1010: ldc_w 1148
    //   1013: aload 10
    //   1015: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   1018: aload_1
    //   1019: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   1022: aload 9
    //   1024: ifnull -829 -> 195
    //   1027: aload 9
    //   1029: invokeinterface 244 1 0
    //   1034: return
    //   1035: aload 11
    //   1037: astore 8
    //   1039: aload 11
    //   1041: astore 9
    //   1043: aload_1
    //   1044: astore 10
    //   1046: aload 11
    //   1048: iconst_0
    //   1049: invokeinterface 241 2 0
    //   1054: lstore_2
    //   1055: aload 11
    //   1057: astore 8
    //   1059: aload 11
    //   1061: astore 9
    //   1063: aload_1
    //   1064: astore 10
    //   1066: aload 11
    //   1068: iconst_3
    //   1069: invokeinterface 782 2 0
    //   1074: invokestatic 788	com/google/android/gms/internal/zzbul:zzad	([B)Lcom/google/android/gms/internal/zzbul;
    //   1077: astore 12
    //   1079: aload 11
    //   1081: astore 8
    //   1083: aload 11
    //   1085: astore 9
    //   1087: aload_1
    //   1088: astore 10
    //   1090: new 1008	com/google/android/gms/internal/zzauh$zzb
    //   1093: dup
    //   1094: invokespecial 1009	com/google/android/gms/internal/zzauh$zzb:<init>	()V
    //   1097: astore 13
    //   1099: aload 11
    //   1101: astore 8
    //   1103: aload 11
    //   1105: astore 9
    //   1107: aload_1
    //   1108: astore 10
    //   1110: aload 13
    //   1112: aload 12
    //   1114: invokevirtual 1149	com/google/android/gms/internal/zzauh$zzb:zzb	(Lcom/google/android/gms/internal/zzbul;)Lcom/google/android/gms/internal/zzbut;
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
    //   1134: invokeinterface 634 2 0
    //   1139: putfield 1150	com/google/android/gms/internal/zzauh$zzb:name	Ljava/lang/String;
    //   1142: aload 11
    //   1144: astore 8
    //   1146: aload 11
    //   1148: astore 9
    //   1150: aload_1
    //   1151: astore 10
    //   1153: aload 13
    //   1155: aload 11
    //   1157: iconst_2
    //   1158: invokeinterface 241 2 0
    //   1163: invokestatic 831	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   1166: putfield 1153	com/google/android/gms/internal/zzauh$zzb:zzbvW	Ljava/lang/Long;
    //   1169: aload 11
    //   1171: astore 8
    //   1173: aload 11
    //   1175: astore 9
    //   1177: aload_1
    //   1178: astore 10
    //   1180: aload 6
    //   1182: lload_2
    //   1183: aload 13
    //   1185: invokeinterface 1156 4 0
    //   1190: istore 7
    //   1192: iload 7
    //   1194: ifne +48 -> 1242
    //   1197: aload 11
    //   1199: ifnull -1004 -> 195
    //   1202: aload 11
    //   1204: invokeinterface 244 1 0
    //   1209: return
    //   1210: astore 12
    //   1212: aload 11
    //   1214: astore 8
    //   1216: aload 11
    //   1218: astore 9
    //   1220: aload_1
    //   1221: astore 10
    //   1223: aload_0
    //   1224: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   1227: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   1230: ldc_w 1158
    //   1233: aload_1
    //   1234: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   1237: aload 12
    //   1239: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   1242: aload 11
    //   1244: astore 8
    //   1246: aload 11
    //   1248: astore 9
    //   1250: aload_1
    //   1251: astore 10
    //   1253: aload 11
    //   1255: invokeinterface 742 1 0
    //   1260: istore 7
    //   1262: iload 7
    //   1264: ifne -229 -> 1035
    //   1267: aload 11
    //   1269: ifnull -1074 -> 195
    //   1272: aload 11
    //   1274: invokeinterface 244 1 0
    //   1279: return
    //   1280: astore_1
    //   1281: aload 8
    //   1283: ifnull +10 -> 1293
    //   1286: aload 8
    //   1288: invokeinterface 244 1 0
    //   1293: aload_1
    //   1294: athrow
    //   1295: astore_1
    //   1296: goto -15 -> 1281
    //   1299: astore_1
    //   1300: goto -301 -> 999
    //   1303: lload 4
    //   1305: ldc2_w 532
    //   1308: lcmp
    //   1309: ifeq +11 -> 1320
    //   1312: ldc_w 1160
    //   1315: astore 12
    //   1317: goto -1218 -> 99
    //   1320: ldc_w 1162
    //   1323: astore 12
    //   1325: goto -1226 -> 99
    //   1328: lload 4
    //   1330: ldc2_w 532
    //   1333: lcmp
    //   1334: ifeq +11 -> 1345
    //   1337: ldc_w 1164
    //   1340: astore 12
    //   1342: goto -907 -> 435
    //   1345: ldc_w 1162
    //   1348: astore 12
    //   1350: goto -915 -> 435
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1353	0	this	zzasu
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
  public boolean zza(zzaud paramzzaud)
  {
    zzac.zzw(paramzzaud);
    zzmq();
    zznA();
    if (zzR(paramzzaud.zzVQ, paramzzaud.mName) == null)
    {
      long l;
      if (zzaue.zzfW(paramzzaud.mName))
      {
        l = zzb("select count(1) from user_attributes where app_id=? and name not like '!_%' escape '!'", new String[] { paramzzaud.zzVQ });
        zzJv().zzKe();
        if (l < 25L) {}
      }
      else
      {
        do
        {
          return false;
          l = zzb("select count(1) from user_attributes where app_id=?", new String[] { paramzzaud.zzVQ });
          zzJv().zzKf();
        } while (l >= 50L);
      }
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("app_id", paramzzaud.zzVQ);
    localContentValues.put("name", paramzzaud.mName);
    localContentValues.put("set_timestamp", Long.valueOf(paramzzaud.zzbvd));
    zza(localContentValues, "value", paramzzaud.zzYe);
    try
    {
      if (getWritableDatabase().insertWithOnConflict("user_attributes", null, localContentValues, 5) == -1L) {
        zzJt().zzLa().zzj("Failed to insert/update user property (got -1). appId", zzati.zzfI(paramzzaud.zzVQ));
      }
      return true;
    }
    catch (SQLiteException localSQLiteException)
    {
      for (;;)
      {
        zzJt().zzLa().zze("Error storing user property. appId", zzati.zzfI(paramzzaud.zzVQ), localSQLiteException);
      }
    }
  }
  
  @WorkerThread
  public void zzal(long paramLong)
  {
    zzmq();
    zznA();
    if (getWritableDatabase().delete("queue", "rowid=?", new String[] { String.valueOf(paramLong) }) != 1) {
      zzJt().zzLa().log("Deleted fewer rows from queue than expected");
    }
  }
  
  /* Error */
  public String zzam(long paramLong)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 6
    //   3: aload_0
    //   4: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   7: aload_0
    //   8: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
    //   11: aload_0
    //   12: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   15: ldc_w 1208
    //   18: iconst_1
    //   19: anewarray 322	java/lang/String
    //   22: dup
    //   23: iconst_0
    //   24: lload_1
    //   25: invokestatic 695	java/lang/String:valueOf	(J)Ljava/lang/String;
    //   28: aastore
    //   29: invokevirtual 234	android/database/sqlite/SQLiteDatabase:rawQuery	(Ljava/lang/String;[Ljava/lang/String;)Landroid/database/Cursor;
    //   32: astore_3
    //   33: aload_3
    //   34: astore 4
    //   36: aload_3
    //   37: invokeinterface 237 1 0
    //   42: ifne +40 -> 82
    //   45: aload_3
    //   46: astore 4
    //   48: aload_0
    //   49: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   52: invokevirtual 701	com/google/android/gms/internal/zzati:zzLg	()Lcom/google/android/gms/internal/zzati$zza;
    //   55: ldc_w 1210
    //   58: invokevirtual 287	com/google/android/gms/internal/zzati$zza:log	(Ljava/lang/String;)V
    //   61: aload 6
    //   63: astore 4
    //   65: aload_3
    //   66: ifnull +13 -> 79
    //   69: aload_3
    //   70: invokeinterface 244 1 0
    //   75: aload 6
    //   77: astore 4
    //   79: aload 4
    //   81: areturn
    //   82: aload_3
    //   83: astore 4
    //   85: aload_3
    //   86: iconst_0
    //   87: invokeinterface 634 2 0
    //   92: astore 5
    //   94: aload 5
    //   96: astore 4
    //   98: aload_3
    //   99: ifnull -20 -> 79
    //   102: aload_3
    //   103: invokeinterface 244 1 0
    //   108: aload 5
    //   110: areturn
    //   111: astore 5
    //   113: aconst_null
    //   114: astore_3
    //   115: aload_3
    //   116: astore 4
    //   118: aload_0
    //   119: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   122: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   125: ldc_w 1212
    //   128: aload 5
    //   130: invokevirtual 312	com/google/android/gms/internal/zzati$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   133: aload 6
    //   135: astore 4
    //   137: aload_3
    //   138: ifnull -59 -> 79
    //   141: aload_3
    //   142: invokeinterface 244 1 0
    //   147: aconst_null
    //   148: areturn
    //   149: astore_3
    //   150: aconst_null
    //   151: astore 4
    //   153: aload 4
    //   155: ifnull +10 -> 165
    //   158: aload 4
    //   160: invokeinterface 244 1 0
    //   165: aload_3
    //   166: athrow
    //   167: astore_3
    //   168: goto -15 -> 153
    //   171: astore 5
    //   173: goto -58 -> 115
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	176	0	this	zzasu
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
      zzJt().zzLa().zzj("Loaded invalid unknown value type, ignoring it", Integer.valueOf(i));
      return null;
    case 0: 
      zzJt().zzLa().log("Loaded invalid null value from database");
      return null;
    case 1: 
      return Long.valueOf(paramCursor.getLong(paramInt));
    case 2: 
      return Double.valueOf(paramCursor.getDouble(paramInt));
    case 3: 
      return paramCursor.getString(paramInt);
    }
    zzJt().zzLa().log("Loaded invalid blob type value, ignoring it");
    return null;
  }
  
  @WorkerThread
  void zzb(String paramString, zzauf.zza[] paramArrayOfzza)
  {
    int j = 0;
    zznA();
    zzmq();
    zzac.zzdv(paramString);
    zzac.zzw(paramArrayOfzza);
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.beginTransaction();
    try
    {
      zzfB(paramString);
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
        localArrayList.add(paramArrayOfzza[i].zzbvh);
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
  
  boolean zzc(String paramString, List<Integer> paramList)
  {
    zzac.zzdv(paramString);
    zznA();
    zzmq();
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    int j;
    label151:
    do
    {
      try
      {
        long l = zzb("select count(1) from audience_filter_values where app_id=?", new String[] { paramString });
        j = zzJv().zzfs(paramString);
        if (l <= j) {
          return false;
        }
      }
      catch (SQLiteException paramList)
      {
        zzJt().zzLa().zze("Database error querying filters. appId", zzati.zzfI(paramString), paramList);
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
  public void zzd(String paramString, byte[] paramArrayOfByte)
  {
    zzac.zzdv(paramString);
    zzmq();
    zznA();
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("remote_config", paramArrayOfByte);
    try
    {
      if (getWritableDatabase().update("apps", localContentValues, "app_id = ?", new String[] { paramString }) == 0L) {
        zzJt().zzLa().zzj("Failed to update remote config (got 0). appId", zzati.zzfI(paramString));
      }
      return;
    }
    catch (SQLiteException paramArrayOfByte)
    {
      zzJt().zzLa().zze("Error storing remote config. appId", zzati.zzfI(paramString), paramArrayOfByte);
    }
  }
  
  /* Error */
  @WorkerThread
  public byte[] zzfA(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   9: aload_0
    //   10: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
    //   13: aload_0
    //   14: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: ldc_w 876
    //   20: iconst_1
    //   21: anewarray 322	java/lang/String
    //   24: dup
    //   25: iconst_0
    //   26: ldc 81
    //   28: aastore
    //   29: ldc_w 878
    //   32: iconst_1
    //   33: anewarray 322	java/lang/String
    //   36: dup
    //   37: iconst_0
    //   38: aload_1
    //   39: aastore
    //   40: aconst_null
    //   41: aconst_null
    //   42: aconst_null
    //   43: invokevirtual 467	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   46: astore 4
    //   48: aload 4
    //   50: astore_3
    //   51: aload 4
    //   53: invokeinterface 237 1 0
    //   58: istore_2
    //   59: iload_2
    //   60: ifne +19 -> 79
    //   63: aload 4
    //   65: ifnull +10 -> 75
    //   68: aload 4
    //   70: invokeinterface 244 1 0
    //   75: aconst_null
    //   76: astore_1
    //   77: aload_1
    //   78: areturn
    //   79: aload 4
    //   81: astore_3
    //   82: aload 4
    //   84: iconst_0
    //   85: invokeinterface 782 2 0
    //   90: astore 5
    //   92: aload 4
    //   94: astore_3
    //   95: aload 4
    //   97: invokeinterface 742 1 0
    //   102: ifeq +23 -> 125
    //   105: aload 4
    //   107: astore_3
    //   108: aload_0
    //   109: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   112: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   115: ldc_w 1259
    //   118: aload_1
    //   119: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   122: invokevirtual 312	com/google/android/gms/internal/zzati$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   125: aload 5
    //   127: astore_1
    //   128: aload 4
    //   130: ifnull -53 -> 77
    //   133: aload 4
    //   135: invokeinterface 244 1 0
    //   140: aload 5
    //   142: areturn
    //   143: astore 5
    //   145: aconst_null
    //   146: astore 4
    //   148: aload 4
    //   150: astore_3
    //   151: aload_0
    //   152: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   155: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   158: ldc_w 1261
    //   161: aload_1
    //   162: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   165: aload 5
    //   167: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   170: aload 4
    //   172: ifnull +10 -> 182
    //   175: aload 4
    //   177: invokeinterface 244 1 0
    //   182: aconst_null
    //   183: areturn
    //   184: astore_1
    //   185: aconst_null
    //   186: astore_3
    //   187: aload_3
    //   188: ifnull +9 -> 197
    //   191: aload_3
    //   192: invokeinterface 244 1 0
    //   197: aload_1
    //   198: athrow
    //   199: astore_1
    //   200: goto -13 -> 187
    //   203: astore 5
    //   205: goto -57 -> 148
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	208	0	this	zzasu
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
  void zzfB(String paramString)
  {
    zznA();
    zzmq();
    zzac.zzdv(paramString);
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    localSQLiteDatabase.delete("property_filters", "app_id=?", new String[] { paramString });
    localSQLiteDatabase.delete("event_filters", "app_id=?", new String[] { paramString });
  }
  
  /* Error */
  Map<Integer, zzauh.zzf> zzfC(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
    //   4: aload_0
    //   5: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   8: aload_1
    //   9: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   12: pop
    //   13: aload_0
    //   14: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: astore 4
    //   19: aload 4
    //   21: ldc_w 1107
    //   24: iconst_2
    //   25: anewarray 322	java/lang/String
    //   28: dup
    //   29: iconst_0
    //   30: ldc_w 513
    //   33: aastore
    //   34: dup
    //   35: iconst_1
    //   36: ldc_w 1105
    //   39: aastore
    //   40: ldc_w 878
    //   43: iconst_1
    //   44: anewarray 322	java/lang/String
    //   47: dup
    //   48: iconst_0
    //   49: aload_1
    //   50: aastore
    //   51: aconst_null
    //   52: aconst_null
    //   53: aconst_null
    //   54: invokevirtual 467	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   57: astore 5
    //   59: aload 5
    //   61: astore 4
    //   63: aload 5
    //   65: invokeinterface 237 1 0
    //   70: istore_3
    //   71: iload_3
    //   72: ifne +19 -> 91
    //   75: aload 5
    //   77: ifnull +10 -> 87
    //   80: aload 5
    //   82: invokeinterface 244 1 0
    //   87: aconst_null
    //   88: astore_1
    //   89: aload_1
    //   90: areturn
    //   91: aload 5
    //   93: astore 4
    //   95: new 27	android/support/v4/util/ArrayMap
    //   98: dup
    //   99: invokespecial 773	android/support/v4/util/ArrayMap:<init>	()V
    //   102: astore 6
    //   104: aload 5
    //   106: astore 4
    //   108: aload 5
    //   110: iconst_0
    //   111: invokeinterface 795 2 0
    //   116: istore_2
    //   117: aload 5
    //   119: astore 4
    //   121: aload 5
    //   123: iconst_1
    //   124: invokeinterface 782 2 0
    //   129: invokestatic 788	com/google/android/gms/internal/zzbul:zzad	([B)Lcom/google/android/gms/internal/zzbul;
    //   132: astore 7
    //   134: aload 5
    //   136: astore 4
    //   138: new 1101	com/google/android/gms/internal/zzauh$zzf
    //   141: dup
    //   142: invokespecial 1264	com/google/android/gms/internal/zzauh$zzf:<init>	()V
    //   145: astore 8
    //   147: aload 5
    //   149: astore 4
    //   151: aload 8
    //   153: aload 7
    //   155: invokevirtual 1265	com/google/android/gms/internal/zzauh$zzf:zzb	(Lcom/google/android/gms/internal/zzbul;)Lcom/google/android/gms/internal/zzbut;
    //   158: pop
    //   159: aload 5
    //   161: astore 4
    //   163: aload 6
    //   165: iload_2
    //   166: invokestatic 483	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   169: aload 8
    //   171: invokeinterface 43 3 0
    //   176: pop
    //   177: aload 5
    //   179: astore 4
    //   181: aload 5
    //   183: invokeinterface 742 1 0
    //   188: istore_3
    //   189: iload_3
    //   190: ifne -86 -> 104
    //   193: aload 6
    //   195: astore_1
    //   196: aload 5
    //   198: ifnull -109 -> 89
    //   201: aload 5
    //   203: invokeinterface 244 1 0
    //   208: aload 6
    //   210: areturn
    //   211: astore 7
    //   213: aload 5
    //   215: astore 4
    //   217: aload_0
    //   218: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   221: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   224: ldc_w 1267
    //   227: aload_1
    //   228: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   231: iload_2
    //   232: invokestatic 483	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   235: aload 7
    //   237: invokevirtual 487	com/google/android/gms/internal/zzati$zza:zzd	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   240: goto -63 -> 177
    //   243: astore 6
    //   245: aload 5
    //   247: astore 4
    //   249: aload_0
    //   250: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   253: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   256: ldc_w 1269
    //   259: aload_1
    //   260: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   263: aload 6
    //   265: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   268: aload 5
    //   270: ifnull +10 -> 280
    //   273: aload 5
    //   275: invokeinterface 244 1 0
    //   280: aconst_null
    //   281: areturn
    //   282: astore_1
    //   283: aconst_null
    //   284: astore 4
    //   286: aload 4
    //   288: ifnull +10 -> 298
    //   291: aload 4
    //   293: invokeinterface 244 1 0
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
    //   0	312	0	this	zzasu
    //   0	312	1	paramString	String
    //   116	116	2	i	int
    //   70	120	3	bool	boolean
    //   17	275	4	localObject	Object
    //   57	251	5	localCursor	Cursor
    //   102	107	6	localArrayMap	ArrayMap
    //   243	21	6	localSQLiteException1	SQLiteException
    //   304	1	6	localSQLiteException2	SQLiteException
    //   132	22	7	localzzbul	zzbul
    //   211	25	7	localIOException	IOException
    //   145	25	8	localzzf	zzauh.zzf
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
  void zzfD(String paramString)
  {
    zznA();
    zzmq();
    zzac.zzdv(paramString);
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
        zzJt().zzLg().zze("Deleted application data. app, records", paramString, Integer.valueOf(i));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzJt().zzLa().zze("Error deleting application data. appId, error", zzati.zzfI(paramString), localSQLiteException);
    }
  }
  
  @WorkerThread
  public long zzfE(String paramString)
  {
    zzac.zzdv(paramString);
    zzmq();
    zznA();
    return zzU(paramString, "first_open_count");
  }
  
  public void zzfF(String paramString)
  {
    SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
    try
    {
      localSQLiteDatabase.execSQL("delete from raw_events_metadata where app_id=? and metadata_fingerprint not in (select distinct metadata_fingerprint from raw_events where app_id=?)", new String[] { paramString, paramString });
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzJt().zzLa().zze("Failed to remove unused event metadata. appId", zzati.zzfI(paramString), localSQLiteException);
    }
  }
  
  public long zzfG(String paramString)
  {
    zzac.zzdv(paramString);
    return zza("select count(1) from events where app_id=? and name not like '!_%' escape '!'", new String[] { paramString }, 0L);
  }
  
  /* Error */
  @WorkerThread
  public List<zzaud> zzfx(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aload_1
    //   4: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   7: pop
    //   8: aload_0
    //   9: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   12: aload_0
    //   13: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
    //   16: new 799	java/util/ArrayList
    //   19: dup
    //   20: invokespecial 800	java/util/ArrayList:<init>	()V
    //   23: astore 8
    //   25: aload_0
    //   26: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   29: astore 6
    //   31: aload_0
    //   32: invokevirtual 673	com/google/android/gms/internal/zzasu:zzJv	()Lcom/google/android/gms/internal/zzast;
    //   35: invokevirtual 1185	com/google/android/gms/internal/zzast:zzKf	()I
    //   38: istore_2
    //   39: aload 6
    //   41: ldc_w 749
    //   44: iconst_3
    //   45: anewarray 322	java/lang/String
    //   48: dup
    //   49: iconst_0
    //   50: ldc_w 461
    //   53: aastore
    //   54: dup
    //   55: iconst_1
    //   56: ldc_w 757
    //   59: aastore
    //   60: dup
    //   61: iconst_2
    //   62: ldc_w 758
    //   65: aastore
    //   66: ldc_w 878
    //   69: iconst_1
    //   70: anewarray 322	java/lang/String
    //   73: dup
    //   74: iconst_0
    //   75: aload_1
    //   76: aastore
    //   77: aconst_null
    //   78: aconst_null
    //   79: ldc_w 1122
    //   82: iload_2
    //   83: invokestatic 594	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   86: invokevirtual 1127	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   89: astore 6
    //   91: aload 6
    //   93: invokeinterface 237 1 0
    //   98: istore_3
    //   99: iload_3
    //   100: ifne +18 -> 118
    //   103: aload 6
    //   105: ifnull +10 -> 115
    //   108: aload 6
    //   110: invokeinterface 244 1 0
    //   115: aload 8
    //   117: areturn
    //   118: aload 6
    //   120: iconst_0
    //   121: invokeinterface 634 2 0
    //   126: astore 7
    //   128: aload 6
    //   130: iconst_1
    //   131: invokeinterface 241 2 0
    //   136: lstore 4
    //   138: aload_0
    //   139: aload 6
    //   141: iconst_2
    //   142: invokevirtual 763	com/google/android/gms/internal/zzasu:zzb	(Landroid/database/Cursor;I)Ljava/lang/Object;
    //   145: astore 9
    //   147: aload 9
    //   149: ifnonnull +47 -> 196
    //   152: aload_0
    //   153: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   156: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   159: ldc_w 1294
    //   162: aload_1
    //   163: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   166: invokevirtual 312	com/google/android/gms/internal/zzati$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   169: aload 6
    //   171: invokeinterface 742 1 0
    //   176: istore_3
    //   177: iload_3
    //   178: ifne -60 -> 118
    //   181: aload 6
    //   183: ifnull +10 -> 193
    //   186: aload 6
    //   188: invokeinterface 244 1 0
    //   193: aload 8
    //   195: areturn
    //   196: aload 8
    //   198: new 760	com/google/android/gms/internal/zzaud
    //   201: dup
    //   202: aload_1
    //   203: aload 7
    //   205: lload 4
    //   207: aload 9
    //   209: invokespecial 766	com/google/android/gms/internal/zzaud:<init>	(Ljava/lang/String;Ljava/lang/String;JLjava/lang/Object;)V
    //   212: invokeinterface 803 2 0
    //   217: pop
    //   218: goto -49 -> 169
    //   221: astore 7
    //   223: aload_0
    //   224: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   227: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   230: ldc_w 1296
    //   233: aload_1
    //   234: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   237: aload 7
    //   239: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   242: aload 6
    //   244: ifnull +10 -> 254
    //   247: aload 6
    //   249: invokeinterface 244 1 0
    //   254: aconst_null
    //   255: areturn
    //   256: astore_1
    //   257: aload 7
    //   259: astore 6
    //   261: aload 6
    //   263: ifnull +10 -> 273
    //   266: aload 6
    //   268: invokeinterface 244 1 0
    //   273: aload_1
    //   274: athrow
    //   275: astore_1
    //   276: goto -15 -> 261
    //   279: astore_1
    //   280: goto -19 -> 261
    //   283: astore 7
    //   285: aconst_null
    //   286: astore 6
    //   288: goto -65 -> 223
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	291	0	this	zzasu
    //   0	291	1	paramString	String
    //   38	45	2	i	int
    //   98	80	3	bool	boolean
    //   136	70	4	l	long
    //   29	258	6	localObject1	Object
    //   1	203	7	str	String
    //   221	37	7	localSQLiteException1	SQLiteException
    //   283	1	7	localSQLiteException2	SQLiteException
    //   23	174	8	localArrayList	ArrayList
    //   145	63	9	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   91	99	221	android/database/sqlite/SQLiteException
    //   118	147	221	android/database/sqlite/SQLiteException
    //   152	169	221	android/database/sqlite/SQLiteException
    //   169	177	221	android/database/sqlite/SQLiteException
    //   196	218	221	android/database/sqlite/SQLiteException
    //   25	91	256	finally
    //   91	99	275	finally
    //   118	147	275	finally
    //   152	169	275	finally
    //   169	177	275	finally
    //   196	218	275	finally
    //   223	242	279	finally
    //   25	91	283	android/database/sqlite/SQLiteException
  }
  
  /* Error */
  @WorkerThread
  public zzasp zzfy(String paramString)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   4: pop
    //   5: aload_0
    //   6: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   9: aload_0
    //   10: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
    //   13: aload_0
    //   14: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   17: ldc_w 876
    //   20: bipush 22
    //   22: anewarray 322	java/lang/String
    //   25: dup
    //   26: iconst_0
    //   27: ldc_w 914
    //   30: aastore
    //   31: dup
    //   32: iconst_1
    //   33: ldc_w 919
    //   36: aastore
    //   37: dup
    //   38: iconst_2
    //   39: ldc_w 924
    //   42: aastore
    //   43: dup
    //   44: iconst_3
    //   45: ldc_w 929
    //   48: aastore
    //   49: dup
    //   50: iconst_4
    //   51: ldc 61
    //   53: aastore
    //   54: dup
    //   55: iconst_5
    //   56: ldc_w 937
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
    //   150: dup
    //   151: bipush 21
    //   153: ldc 109
    //   155: aastore
    //   156: ldc_w 878
    //   159: iconst_1
    //   160: anewarray 322	java/lang/String
    //   163: dup
    //   164: iconst_0
    //   165: aload_1
    //   166: aastore
    //   167: aconst_null
    //   168: aconst_null
    //   169: aconst_null
    //   170: invokevirtual 467	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   173: astore 7
    //   175: aload 7
    //   177: astore 6
    //   179: aload 7
    //   181: invokeinterface 237 1 0
    //   186: istore_3
    //   187: iload_3
    //   188: ifne +19 -> 207
    //   191: aload 7
    //   193: ifnull +10 -> 203
    //   196: aload 7
    //   198: invokeinterface 244 1 0
    //   203: aconst_null
    //   204: astore_1
    //   205: aload_1
    //   206: areturn
    //   207: aload 7
    //   209: astore 6
    //   211: new 909	com/google/android/gms/internal/zzasp
    //   214: dup
    //   215: aload_0
    //   216: getfield 1303	com/google/android/gms/internal/zzasu:zzbpw	Lcom/google/android/gms/internal/zzatp;
    //   219: aload_1
    //   220: invokespecial 1306	com/google/android/gms/internal/zzasp:<init>	(Lcom/google/android/gms/internal/zzatp;Ljava/lang/String;)V
    //   223: astore 8
    //   225: aload 7
    //   227: astore 6
    //   229: aload 8
    //   231: aload 7
    //   233: iconst_0
    //   234: invokeinterface 634 2 0
    //   239: invokevirtual 1309	com/google/android/gms/internal/zzasp:zzfh	(Ljava/lang/String;)V
    //   242: aload 7
    //   244: astore 6
    //   246: aload 8
    //   248: aload 7
    //   250: iconst_1
    //   251: invokeinterface 634 2 0
    //   256: invokevirtual 1312	com/google/android/gms/internal/zzasp:zzfi	(Ljava/lang/String;)V
    //   259: aload 7
    //   261: astore 6
    //   263: aload 8
    //   265: aload 7
    //   267: iconst_2
    //   268: invokeinterface 634 2 0
    //   273: invokevirtual 1315	com/google/android/gms/internal/zzasp:zzfj	(Ljava/lang/String;)V
    //   276: aload 7
    //   278: astore 6
    //   280: aload 8
    //   282: aload 7
    //   284: iconst_3
    //   285: invokeinterface 241 2 0
    //   290: invokevirtual 1318	com/google/android/gms/internal/zzasp:zzac	(J)V
    //   293: aload 7
    //   295: astore 6
    //   297: aload 8
    //   299: aload 7
    //   301: iconst_4
    //   302: invokeinterface 241 2 0
    //   307: invokevirtual 1321	com/google/android/gms/internal/zzasp:zzX	(J)V
    //   310: aload 7
    //   312: astore 6
    //   314: aload 8
    //   316: aload 7
    //   318: iconst_5
    //   319: invokeinterface 241 2 0
    //   324: invokevirtual 1324	com/google/android/gms/internal/zzasp:zzY	(J)V
    //   327: aload 7
    //   329: astore 6
    //   331: aload 8
    //   333: aload 7
    //   335: bipush 6
    //   337: invokeinterface 634 2 0
    //   342: invokevirtual 1327	com/google/android/gms/internal/zzasp:setAppVersion	(Ljava/lang/String;)V
    //   345: aload 7
    //   347: astore 6
    //   349: aload 8
    //   351: aload 7
    //   353: bipush 7
    //   355: invokeinterface 634 2 0
    //   360: invokevirtual 1330	com/google/android/gms/internal/zzasp:zzfl	(Ljava/lang/String;)V
    //   363: aload 7
    //   365: astore 6
    //   367: aload 8
    //   369: aload 7
    //   371: bipush 8
    //   373: invokeinterface 241 2 0
    //   378: invokevirtual 1333	com/google/android/gms/internal/zzasp:zzaa	(J)V
    //   381: aload 7
    //   383: astore 6
    //   385: aload 8
    //   387: aload 7
    //   389: bipush 9
    //   391: invokeinterface 241 2 0
    //   396: invokevirtual 1336	com/google/android/gms/internal/zzasp:zzab	(J)V
    //   399: aload 7
    //   401: astore 6
    //   403: aload 7
    //   405: bipush 10
    //   407: invokeinterface 1339 2 0
    //   412: ifeq +292 -> 704
    //   415: iconst_1
    //   416: istore_2
    //   417: goto +394 -> 811
    //   420: aload 7
    //   422: astore 6
    //   424: aload 8
    //   426: iload_3
    //   427: invokevirtual 1343	com/google/android/gms/internal/zzasp:setMeasurementEnabled	(Z)V
    //   430: aload 7
    //   432: astore 6
    //   434: aload 8
    //   436: aload 7
    //   438: bipush 11
    //   440: invokeinterface 241 2 0
    //   445: invokevirtual 1346	com/google/android/gms/internal/zzasp:zzaf	(J)V
    //   448: aload 7
    //   450: astore 6
    //   452: aload 8
    //   454: aload 7
    //   456: bipush 12
    //   458: invokeinterface 241 2 0
    //   463: invokevirtual 1349	com/google/android/gms/internal/zzasp:zzag	(J)V
    //   466: aload 7
    //   468: astore 6
    //   470: aload 8
    //   472: aload 7
    //   474: bipush 13
    //   476: invokeinterface 241 2 0
    //   481: invokevirtual 1352	com/google/android/gms/internal/zzasp:zzah	(J)V
    //   484: aload 7
    //   486: astore 6
    //   488: aload 8
    //   490: aload 7
    //   492: bipush 14
    //   494: invokeinterface 241 2 0
    //   499: invokevirtual 1355	com/google/android/gms/internal/zzasp:zzai	(J)V
    //   502: aload 7
    //   504: astore 6
    //   506: aload 8
    //   508: aload 7
    //   510: bipush 15
    //   512: invokeinterface 241 2 0
    //   517: invokevirtual 1357	com/google/android/gms/internal/zzasp:zzad	(J)V
    //   520: aload 7
    //   522: astore 6
    //   524: aload 8
    //   526: aload 7
    //   528: bipush 16
    //   530: invokeinterface 241 2 0
    //   535: invokevirtual 1359	com/google/android/gms/internal/zzasp:zzae	(J)V
    //   538: aload 7
    //   540: astore 6
    //   542: aload 7
    //   544: bipush 17
    //   546: invokeinterface 1339 2 0
    //   551: ifeq +170 -> 721
    //   554: ldc2_w 1360
    //   557: lstore 4
    //   559: aload 7
    //   561: astore 6
    //   563: aload 8
    //   565: lload 4
    //   567: invokevirtual 1364	com/google/android/gms/internal/zzasp:zzZ	(J)V
    //   570: aload 7
    //   572: astore 6
    //   574: aload 8
    //   576: aload 7
    //   578: bipush 18
    //   580: invokeinterface 634 2 0
    //   585: invokevirtual 1367	com/google/android/gms/internal/zzasp:zzfk	(Ljava/lang/String;)V
    //   588: aload 7
    //   590: astore 6
    //   592: aload 8
    //   594: aload 7
    //   596: bipush 19
    //   598: invokeinterface 241 2 0
    //   603: invokevirtual 1370	com/google/android/gms/internal/zzasp:zzak	(J)V
    //   606: aload 7
    //   608: astore 6
    //   610: aload 8
    //   612: aload 7
    //   614: bipush 20
    //   616: invokeinterface 241 2 0
    //   621: invokevirtual 1373	com/google/android/gms/internal/zzasp:zzaj	(J)V
    //   624: aload 7
    //   626: astore 6
    //   628: aload 8
    //   630: aload 7
    //   632: bipush 21
    //   634: invokeinterface 634 2 0
    //   639: invokevirtual 1376	com/google/android/gms/internal/zzasp:zzfm	(Ljava/lang/String;)V
    //   642: aload 7
    //   644: astore 6
    //   646: aload 8
    //   648: invokevirtual 1379	com/google/android/gms/internal/zzasp:zzJw	()V
    //   651: aload 7
    //   653: astore 6
    //   655: aload 7
    //   657: invokeinterface 742 1 0
    //   662: ifeq +24 -> 686
    //   665: aload 7
    //   667: astore 6
    //   669: aload_0
    //   670: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   673: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   676: ldc_w 1381
    //   679: aload_1
    //   680: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   683: invokevirtual 312	com/google/android/gms/internal/zzati$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   686: aload 8
    //   688: astore_1
    //   689: aload 7
    //   691: ifnull -486 -> 205
    //   694: aload 7
    //   696: invokeinterface 244 1 0
    //   701: aload 8
    //   703: areturn
    //   704: aload 7
    //   706: astore 6
    //   708: aload 7
    //   710: bipush 10
    //   712: invokeinterface 795 2 0
    //   717: istore_2
    //   718: goto +93 -> 811
    //   721: aload 7
    //   723: astore 6
    //   725: aload 7
    //   727: bipush 17
    //   729: invokeinterface 795 2 0
    //   734: istore_2
    //   735: iload_2
    //   736: i2l
    //   737: lstore 4
    //   739: goto -180 -> 559
    //   742: astore 8
    //   744: aconst_null
    //   745: astore 7
    //   747: aload 7
    //   749: astore 6
    //   751: aload_0
    //   752: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   755: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   758: ldc_w 1383
    //   761: aload_1
    //   762: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   765: aload 8
    //   767: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   770: aload 7
    //   772: ifnull +10 -> 782
    //   775: aload 7
    //   777: invokeinterface 244 1 0
    //   782: aconst_null
    //   783: areturn
    //   784: astore_1
    //   785: aconst_null
    //   786: astore 6
    //   788: aload 6
    //   790: ifnull +10 -> 800
    //   793: aload 6
    //   795: invokeinterface 244 1 0
    //   800: aload_1
    //   801: athrow
    //   802: astore_1
    //   803: goto -15 -> 788
    //   806: astore 8
    //   808: goto -61 -> 747
    //   811: iload_2
    //   812: ifeq +8 -> 820
    //   815: iconst_1
    //   816: istore_3
    //   817: goto -397 -> 420
    //   820: iconst_0
    //   821: istore_3
    //   822: goto -402 -> 420
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	825	0	this	zzasu
    //   0	825	1	paramString	String
    //   416	396	2	i	int
    //   186	636	3	bool	boolean
    //   557	181	4	l	long
    //   177	617	6	localCursor1	Cursor
    //   173	603	7	localCursor2	Cursor
    //   223	479	8	localzzasp	zzasp
    //   742	24	8	localSQLiteException1	SQLiteException
    //   806	1	8	localSQLiteException2	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   13	175	742	android/database/sqlite/SQLiteException
    //   13	175	784	finally
    //   179	187	802	finally
    //   211	225	802	finally
    //   229	242	802	finally
    //   246	259	802	finally
    //   263	276	802	finally
    //   280	293	802	finally
    //   297	310	802	finally
    //   314	327	802	finally
    //   331	345	802	finally
    //   349	363	802	finally
    //   367	381	802	finally
    //   385	399	802	finally
    //   403	415	802	finally
    //   424	430	802	finally
    //   434	448	802	finally
    //   452	466	802	finally
    //   470	484	802	finally
    //   488	502	802	finally
    //   506	520	802	finally
    //   524	538	802	finally
    //   542	554	802	finally
    //   563	570	802	finally
    //   574	588	802	finally
    //   592	606	802	finally
    //   610	624	802	finally
    //   628	642	802	finally
    //   646	651	802	finally
    //   655	665	802	finally
    //   669	686	802	finally
    //   708	718	802	finally
    //   725	735	802	finally
    //   751	770	802	finally
    //   179	187	806	android/database/sqlite/SQLiteException
    //   211	225	806	android/database/sqlite/SQLiteException
    //   229	242	806	android/database/sqlite/SQLiteException
    //   246	259	806	android/database/sqlite/SQLiteException
    //   263	276	806	android/database/sqlite/SQLiteException
    //   280	293	806	android/database/sqlite/SQLiteException
    //   297	310	806	android/database/sqlite/SQLiteException
    //   314	327	806	android/database/sqlite/SQLiteException
    //   331	345	806	android/database/sqlite/SQLiteException
    //   349	363	806	android/database/sqlite/SQLiteException
    //   367	381	806	android/database/sqlite/SQLiteException
    //   385	399	806	android/database/sqlite/SQLiteException
    //   403	415	806	android/database/sqlite/SQLiteException
    //   424	430	806	android/database/sqlite/SQLiteException
    //   434	448	806	android/database/sqlite/SQLiteException
    //   452	466	806	android/database/sqlite/SQLiteException
    //   470	484	806	android/database/sqlite/SQLiteException
    //   488	502	806	android/database/sqlite/SQLiteException
    //   506	520	806	android/database/sqlite/SQLiteException
    //   524	538	806	android/database/sqlite/SQLiteException
    //   542	554	806	android/database/sqlite/SQLiteException
    //   563	570	806	android/database/sqlite/SQLiteException
    //   574	588	806	android/database/sqlite/SQLiteException
    //   592	606	806	android/database/sqlite/SQLiteException
    //   610	624	806	android/database/sqlite/SQLiteException
    //   628	642	806	android/database/sqlite/SQLiteException
    //   646	651	806	android/database/sqlite/SQLiteException
    //   655	665	806	android/database/sqlite/SQLiteException
    //   669	686	806	android/database/sqlite/SQLiteException
    //   708	718	806	android/database/sqlite/SQLiteException
    //   725	735	806	android/database/sqlite/SQLiteException
  }
  
  public long zzfz(String paramString)
  {
    zzac.zzdv(paramString);
    zzmq();
    zznA();
    try
    {
      int i = getWritableDatabase().delete("raw_events", "rowid in (select rowid from raw_events where app_id=? order by rowid desc limit -1 offset ?)", new String[] { paramString, String.valueOf(zzJv().zzfw(paramString)) });
      return i;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzJt().zzLa().zze("Error deleting over the limit events. appId", zzati.zzfI(paramString), localSQLiteException);
    }
    return 0L;
  }
  
  protected void zzmr() {}
  
  /* Error */
  @WorkerThread
  public List<android.util.Pair<zzauh.zze, Long>> zzn(String paramString, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore 6
    //   3: aload_0
    //   4: invokevirtual 400	com/google/android/gms/internal/zzasu:zzmq	()V
    //   7: aload_0
    //   8: invokevirtual 397	com/google/android/gms/internal/zzasu:zznA	()V
    //   11: iload_2
    //   12: ifle +112 -> 124
    //   15: iconst_1
    //   16: istore 5
    //   18: iload 5
    //   20: invokestatic 1397	com/google/android/gms/common/internal/zzac:zzas	(Z)V
    //   23: iload_3
    //   24: ifle +106 -> 130
    //   27: iload 6
    //   29: istore 5
    //   31: iload 5
    //   33: invokestatic 1397	com/google/android/gms/common/internal/zzac:zzas	(Z)V
    //   36: aload_1
    //   37: invokestatic 406	com/google/android/gms/common/internal/zzac:zzdv	(Ljava/lang/String;)Ljava/lang/String;
    //   40: pop
    //   41: aload_0
    //   42: invokevirtual 228	com/google/android/gms/internal/zzasu:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   45: ldc_w 687
    //   48: iconst_2
    //   49: anewarray 322	java/lang/String
    //   52: dup
    //   53: iconst_0
    //   54: ldc_w 1122
    //   57: aastore
    //   58: dup
    //   59: iconst_1
    //   60: ldc_w 522
    //   63: aastore
    //   64: ldc_w 878
    //   67: iconst_1
    //   68: anewarray 322	java/lang/String
    //   71: dup
    //   72: iconst_0
    //   73: aload_1
    //   74: aastore
    //   75: aconst_null
    //   76: aconst_null
    //   77: ldc_w 1122
    //   80: iload_2
    //   81: invokestatic 594	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   84: invokevirtual 1127	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   87: astore 9
    //   89: aload 9
    //   91: invokeinterface 237 1 0
    //   96: ifne +40 -> 136
    //   99: invokestatic 1401	java/util/Collections:emptyList	()Ljava/util/List;
    //   102: astore 10
    //   104: aload 10
    //   106: astore_1
    //   107: aload 9
    //   109: ifnull +13 -> 122
    //   112: aload 9
    //   114: invokeinterface 244 1 0
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
    //   136: new 799	java/util/ArrayList
    //   139: dup
    //   140: invokespecial 800	java/util/ArrayList:<init>	()V
    //   143: astore 10
    //   145: iconst_0
    //   146: istore_2
    //   147: aload 9
    //   149: iconst_0
    //   150: invokeinterface 241 2 0
    //   155: lstore 7
    //   157: aload 9
    //   159: iconst_1
    //   160: invokeinterface 782 2 0
    //   165: astore 11
    //   167: aload_0
    //   168: invokevirtual 856	com/google/android/gms/internal/zzasu:zzJp	()Lcom/google/android/gms/internal/zzaue;
    //   171: aload 11
    //   173: invokevirtual 1404	com/google/android/gms/internal/zzaue:zzx	([B)[B
    //   176: astore 11
    //   178: aload 10
    //   180: invokeinterface 1405 1 0
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
    //   211: invokeinterface 244 1 0
    //   216: aload 10
    //   218: areturn
    //   219: astore 11
    //   221: aload_0
    //   222: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   225: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   228: ldc_w 1407
    //   231: aload_1
    //   232: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   235: aload 11
    //   237: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   240: aload 9
    //   242: invokeinterface 742 1 0
    //   247: ifeq -46 -> 201
    //   250: iload_2
    //   251: iload_3
    //   252: if_icmpgt -51 -> 201
    //   255: goto -108 -> 147
    //   258: aload 11
    //   260: invokestatic 788	com/google/android/gms/internal/zzbul:zzad	([B)Lcom/google/android/gms/internal/zzbul;
    //   263: astore 12
    //   265: new 847	com/google/android/gms/internal/zzauh$zze
    //   268: dup
    //   269: invokespecial 1134	com/google/android/gms/internal/zzauh$zze:<init>	()V
    //   272: astore 13
    //   274: aload 13
    //   276: aload 12
    //   278: invokevirtual 1135	com/google/android/gms/internal/zzauh$zze:zzb	(Lcom/google/android/gms/internal/zzbul;)Lcom/google/android/gms/internal/zzbut;
    //   281: pop
    //   282: aload 11
    //   284: arraylength
    //   285: iload_2
    //   286: iadd
    //   287: istore_2
    //   288: aload 10
    //   290: aload 13
    //   292: lload 7
    //   294: invokestatic 831	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   297: invokestatic 1413	android/util/Pair:create	(Ljava/lang/Object;Ljava/lang/Object;)Landroid/util/Pair;
    //   300: invokeinterface 803 2 0
    //   305: pop
    //   306: goto -66 -> 240
    //   309: astore 10
    //   311: aload_0
    //   312: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   315: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   318: ldc_w 1415
    //   321: aload_1
    //   322: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   325: aload 10
    //   327: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   330: invokestatic 1401	java/util/Collections:emptyList	()Ljava/util/List;
    //   333: astore 10
    //   335: aload 10
    //   337: astore_1
    //   338: aload 9
    //   340: ifnull -218 -> 122
    //   343: aload 9
    //   345: invokeinterface 244 1 0
    //   350: aload 10
    //   352: areturn
    //   353: astore 11
    //   355: aload_0
    //   356: invokevirtual 248	com/google/android/gms/internal/zzasu:zzJt	()Lcom/google/android/gms/internal/zzati;
    //   359: invokevirtual 254	com/google/android/gms/internal/zzati:zzLa	()Lcom/google/android/gms/internal/zzati$zza;
    //   362: ldc_w 1417
    //   365: aload_1
    //   366: invokestatic 430	com/google/android/gms/internal/zzati:zzfI	(Ljava/lang/String;)Ljava/lang/Object;
    //   369: aload 11
    //   371: invokevirtual 262	com/google/android/gms/internal/zzati$zza:zze	(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   374: goto -134 -> 240
    //   377: astore_1
    //   378: aconst_null
    //   379: astore 9
    //   381: aload 9
    //   383: ifnull +10 -> 393
    //   386: aload 9
    //   388: invokeinterface 244 1 0
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
    //   0	411	0	this	zzasu
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
    //   263	14	12	localzzbul	zzbul
    //   272	19	13	localzze	zzauh.zze
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
  
  String zznV()
  {
    return zzJv().zzoV();
  }
  
  @WorkerThread
  public void zzz(String paramString, int paramInt)
  {
    zzac.zzdv(paramString);
    zzmq();
    zznA();
    try
    {
      getWritableDatabase().execSQL("delete from user_attributes where app_id=? and name in (select name from user_attributes where app_id=? and name like '_ltv_%' order by set_timestamp desc limit ?,10);", new String[] { paramString, paramString, String.valueOf(paramInt) });
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzJt().zzLa().zze("Error pruning currencies. appId", zzati.zzfI(paramString), localSQLiteException);
    }
  }
  
  public static class zza
  {
    long zzbqv;
    long zzbqw;
    long zzbqx;
    long zzbqy;
    long zzbqz;
  }
  
  static abstract interface zzb
  {
    public abstract boolean zza(long paramLong, zzauh.zzb paramzzb);
    
    public abstract void zzb(zzauh.zze paramzze);
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
      if (!zzasu.zza(zzasu.this).zzz(zzasu.this.zzJv().zzKg())) {
        throw new SQLiteException("Database open failed");
      }
      try
      {
        SQLiteDatabase localSQLiteDatabase = super.getWritableDatabase();
        return localSQLiteDatabase;
      }
      catch (SQLiteException localSQLiteException1)
      {
        zzasu.zza(zzasu.this).start();
        zzasu.this.zzJt().zzLa().log("Opening the database failed, dropping and recreating it");
        Object localObject = zzasu.this.zznV();
        if (!zzasu.this.getContext().getDatabasePath((String)localObject).delete()) {
          zzasu.this.zzJt().zzLa().zzj("Failed to delete corrupted db file", localObject);
        }
        try
        {
          localObject = super.getWritableDatabase();
          zzasu.zza(zzasu.this).clear();
          return (SQLiteDatabase)localObject;
        }
        catch (SQLiteException localSQLiteException2)
        {
          zzasu.this.zzJt().zzLa().zzj("Failed to open freshly created database", localSQLiteException2);
          throw localSQLiteException2;
        }
      }
    }
    
    @WorkerThread
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      zzasu.zza(zzasu.this.zzJt(), paramSQLiteDatabase);
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
        zzasu.zza(zzasu.this.zzJt(), paramSQLiteDatabase, "events", "CREATE TABLE IF NOT EXISTS events ( app_id TEXT NOT NULL, name TEXT NOT NULL, lifetime_count INTEGER NOT NULL, current_bundle_count INTEGER NOT NULL, last_fire_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,lifetime_count,current_bundle_count,last_fire_timestamp", null);
        zzasu.zza(zzasu.this.zzJt(), paramSQLiteDatabase, "user_attributes", "CREATE TABLE IF NOT EXISTS user_attributes ( app_id TEXT NOT NULL, name TEXT NOT NULL, set_timestamp INTEGER NOT NULL, value BLOB NOT NULL, PRIMARY KEY (app_id, name)) ;", "app_id,name,set_timestamp,value", null);
        zzasu.zza(zzasu.this.zzJt(), paramSQLiteDatabase, "apps", "CREATE TABLE IF NOT EXISTS apps ( app_id TEXT NOT NULL, app_instance_id TEXT, gmp_app_id TEXT, resettable_device_id_hash TEXT, last_bundle_index INTEGER NOT NULL, last_bundle_end_timestamp INTEGER NOT NULL, PRIMARY KEY (app_id)) ;", "app_id,app_instance_id,gmp_app_id,resettable_device_id_hash,last_bundle_index,last_bundle_end_timestamp", zzasu.zzKQ());
        zzasu.zza(zzasu.this.zzJt(), paramSQLiteDatabase, "queue", "CREATE TABLE IF NOT EXISTS queue ( app_id TEXT NOT NULL, bundle_end_timestamp INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,bundle_end_timestamp,data", zzasu.zzKR());
        zzasu.zza(zzasu.this.zzJt(), paramSQLiteDatabase, "raw_events_metadata", "CREATE TABLE IF NOT EXISTS raw_events_metadata ( app_id TEXT NOT NULL, metadata_fingerprint INTEGER NOT NULL, metadata BLOB NOT NULL, PRIMARY KEY (app_id, metadata_fingerprint));", "app_id,metadata_fingerprint,metadata", null);
        zzasu.zza(zzasu.this.zzJt(), paramSQLiteDatabase, "raw_events", "CREATE TABLE IF NOT EXISTS raw_events ( app_id TEXT NOT NULL, name TEXT NOT NULL, timestamp INTEGER NOT NULL, metadata_fingerprint INTEGER NOT NULL, data BLOB NOT NULL);", "app_id,name,timestamp,metadata_fingerprint,data", zzasu.zzKS());
        zzasu.zza(zzasu.this.zzJt(), paramSQLiteDatabase, "event_filters", "CREATE TABLE IF NOT EXISTS event_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, event_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, event_name, audience_id, filter_id));", "app_id,audience_id,filter_id,event_name,data", null);
        zzasu.zza(zzasu.this.zzJt(), paramSQLiteDatabase, "property_filters", "CREATE TABLE IF NOT EXISTS property_filters ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, filter_id INTEGER NOT NULL, property_name TEXT NOT NULL, data BLOB NOT NULL, PRIMARY KEY (app_id, property_name, audience_id, filter_id));", "app_id,audience_id,filter_id,property_name,data", null);
        zzasu.zza(zzasu.this.zzJt(), paramSQLiteDatabase, "audience_filter_values", "CREATE TABLE IF NOT EXISTS audience_filter_values ( app_id TEXT NOT NULL, audience_id INTEGER NOT NULL, current_results BLOB, PRIMARY KEY (app_id, audience_id));", "app_id,audience_id,current_results", null);
        zzasu.zza(zzasu.this.zzJt(), paramSQLiteDatabase, "app2", "CREATE TABLE IF NOT EXISTS app2 ( app_id TEXT NOT NULL, first_open_count INTEGER NOT NULL, PRIMARY KEY (app_id));", "app_id,first_open_count", zzasu.zzKT());
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzasu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */