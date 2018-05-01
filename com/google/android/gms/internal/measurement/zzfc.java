package com.google.android.gms.internal.measurement;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.os.Parcel;
import android.os.SystemClock;

public final class zzfc
  extends zzhk
{
  private final zzfd zzaig = new zzfd(this, getContext(), "google_app_measurement_local.db");
  private boolean zzaih;
  
  zzfc(zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  private final SQLiteDatabase getWritableDatabase()
    throws SQLiteException
  {
    Object localObject = null;
    if (this.zzaih) {}
    for (;;)
    {
      return (SQLiteDatabase)localObject;
      SQLiteDatabase localSQLiteDatabase = this.zzaig.getWritableDatabase();
      if (localSQLiteDatabase == null) {
        this.zzaih = true;
      } else {
        localObject = localSQLiteDatabase;
      }
    }
  }
  
  private final boolean zza(int paramInt, byte[] paramArrayOfByte)
  {
    zzab();
    if (this.zzaih) {}
    for (boolean bool = false;; bool = false)
    {
      for (;;)
      {
        return bool;
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("type", Integer.valueOf(paramInt));
        localContentValues.put("entry", paramArrayOfByte);
        int i = 5;
        paramInt = 0;
        label48:
        if (paramInt < 5)
        {
          Object localObject1 = null;
          Object localObject2 = null;
          Object localObject3 = null;
          Object localObject4 = null;
          Object localObject5 = null;
          Object localObject6 = null;
          Object localObject7 = null;
          Cursor localCursor1 = null;
          Cursor localCursor2 = localCursor1;
          Object localObject8 = localObject5;
          Object localObject9 = localObject6;
          paramArrayOfByte = (byte[])localObject7;
          try
          {
            SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
            if (localSQLiteDatabase == null)
            {
              localCursor2 = localCursor1;
              localObject4 = localSQLiteDatabase;
              localObject8 = localObject5;
              localObject1 = localSQLiteDatabase;
              localObject9 = localObject6;
              localObject2 = localSQLiteDatabase;
              paramArrayOfByte = (byte[])localObject7;
              localObject3 = localSQLiteDatabase;
              this.zzaih = true;
              if (localSQLiteDatabase != null) {
                localSQLiteDatabase.close();
              }
              bool = false;
            }
            else
            {
              localCursor2 = localCursor1;
              localObject4 = localSQLiteDatabase;
              localObject8 = localObject5;
              localObject1 = localSQLiteDatabase;
              localObject9 = localObject6;
              localObject2 = localSQLiteDatabase;
              paramArrayOfByte = (byte[])localObject7;
              localObject3 = localSQLiteDatabase;
              localSQLiteDatabase.beginTransaction();
              long l1 = 0L;
              localCursor2 = localCursor1;
              localObject4 = localSQLiteDatabase;
              localObject8 = localObject5;
              localObject1 = localSQLiteDatabase;
              localObject9 = localObject6;
              localObject2 = localSQLiteDatabase;
              paramArrayOfByte = (byte[])localObject7;
              localObject3 = localSQLiteDatabase;
              localCursor1 = localSQLiteDatabase.rawQuery("select count(1) from messages", null);
              l2 = l1;
              if (localCursor1 != null)
              {
                l2 = l1;
                localCursor2 = localCursor1;
                localObject4 = localSQLiteDatabase;
                localObject8 = localCursor1;
                localObject1 = localSQLiteDatabase;
                localObject9 = localCursor1;
                localObject2 = localSQLiteDatabase;
                paramArrayOfByte = localCursor1;
                localObject3 = localSQLiteDatabase;
                if (localCursor1.moveToFirst())
                {
                  localCursor2 = localCursor1;
                  localObject4 = localSQLiteDatabase;
                  localObject8 = localCursor1;
                  localObject1 = localSQLiteDatabase;
                  localObject9 = localCursor1;
                  localObject2 = localSQLiteDatabase;
                  paramArrayOfByte = localCursor1;
                  localObject3 = localSQLiteDatabase;
                  l2 = localCursor1.getLong(0);
                }
              }
              if (l2 >= 100000L)
              {
                localCursor2 = localCursor1;
                localObject4 = localSQLiteDatabase;
                localObject8 = localCursor1;
                localObject1 = localSQLiteDatabase;
                localObject9 = localCursor1;
                localObject2 = localSQLiteDatabase;
                paramArrayOfByte = localCursor1;
                localObject3 = localSQLiteDatabase;
                zzgg().zzil().log("Data loss, local db full");
                l2 = 100000L - l2 + 1L;
                localCursor2 = localCursor1;
                localObject4 = localSQLiteDatabase;
                localObject8 = localCursor1;
                localObject1 = localSQLiteDatabase;
                localObject9 = localCursor1;
                localObject2 = localSQLiteDatabase;
                paramArrayOfByte = localCursor1;
                localObject3 = localSQLiteDatabase;
                l1 = localSQLiteDatabase.delete("messages", "rowid in (select rowid from messages order by rowid asc limit ?)", new String[] { Long.toString(l2) });
                if (l1 != l2)
                {
                  localCursor2 = localCursor1;
                  localObject4 = localSQLiteDatabase;
                  localObject8 = localCursor1;
                  localObject1 = localSQLiteDatabase;
                  localObject9 = localCursor1;
                  localObject2 = localSQLiteDatabase;
                  paramArrayOfByte = localCursor1;
                  localObject3 = localSQLiteDatabase;
                  zzgg().zzil().zzd("Different delete count than expected in local db. expected, received, difference", Long.valueOf(l2), Long.valueOf(l1), Long.valueOf(l2 - l1));
                }
              }
              localCursor2 = localCursor1;
              localObject4 = localSQLiteDatabase;
              localObject8 = localCursor1;
              localObject1 = localSQLiteDatabase;
              localObject9 = localCursor1;
              localObject2 = localSQLiteDatabase;
              paramArrayOfByte = localCursor1;
              localObject3 = localSQLiteDatabase;
              localSQLiteDatabase.insertOrThrow("messages", null, localContentValues);
              localCursor2 = localCursor1;
              localObject4 = localSQLiteDatabase;
              localObject8 = localCursor1;
              localObject1 = localSQLiteDatabase;
              localObject9 = localCursor1;
              localObject2 = localSQLiteDatabase;
              paramArrayOfByte = localCursor1;
              localObject3 = localSQLiteDatabase;
              localSQLiteDatabase.setTransactionSuccessful();
              localCursor2 = localCursor1;
              localObject4 = localSQLiteDatabase;
              localObject8 = localCursor1;
              localObject1 = localSQLiteDatabase;
              localObject9 = localCursor1;
              localObject2 = localSQLiteDatabase;
              paramArrayOfByte = localCursor1;
              localObject3 = localSQLiteDatabase;
              localSQLiteDatabase.endTransaction();
              if (localCursor1 != null) {
                localCursor1.close();
              }
              if (localSQLiteDatabase != null) {
                localSQLiteDatabase.close();
              }
              bool = true;
            }
          }
          catch (SQLiteFullException localSQLiteFullException)
          {
            paramArrayOfByte = localCursor2;
            localObject3 = localObject4;
            zzgg().zzil().zzg("Error writing entry to local database", localSQLiteFullException);
            paramArrayOfByte = localCursor2;
            localObject3 = localObject4;
            this.zzaih = true;
            if (localCursor2 != null) {
              localCursor2.close();
            }
            j = i;
            if (localObject4 != null)
            {
              ((SQLiteDatabase)localObject4).close();
              j = i;
            }
            paramInt++;
            i = j;
            break label48;
          }
          catch (SQLiteDatabaseLockedException paramArrayOfByte)
          {
            for (;;)
            {
              long l2 = i;
              paramArrayOfByte = (byte[])localObject8;
              localObject3 = localObject1;
              SystemClock.sleep(l2);
              i += 20;
              if (localObject8 != null) {
                ((Cursor)localObject8).close();
              }
              j = i;
              if (localObject1 != null)
              {
                ((SQLiteDatabase)localObject1).close();
                j = i;
              }
            }
          }
          catch (SQLiteException localSQLiteException)
          {
            for (;;)
            {
              if (localObject2 != null)
              {
                paramArrayOfByte = (byte[])localObject9;
                localObject3 = localObject2;
                if (((SQLiteDatabase)localObject2).inTransaction())
                {
                  paramArrayOfByte = (byte[])localObject9;
                  localObject3 = localObject2;
                  ((SQLiteDatabase)localObject2).endTransaction();
                }
              }
              paramArrayOfByte = (byte[])localObject9;
              localObject3 = localObject2;
              zzgg().zzil().zzg("Error writing entry to local database", localSQLiteException);
              paramArrayOfByte = (byte[])localObject9;
              localObject3 = localObject2;
              this.zzaih = true;
              if (localObject9 != null) {
                ((Cursor)localObject9).close();
              }
              int j = i;
              if (localObject2 != null)
              {
                ((SQLiteDatabase)localObject2).close();
                j = i;
              }
            }
          }
          finally
          {
            if (paramArrayOfByte != null) {
              paramArrayOfByte.close();
            }
            if (localObject3 != null) {
              ((SQLiteDatabase)localObject3).close();
            }
          }
        }
      }
      zzgg().zzin().log("Failed to write entry to local database");
    }
  }
  
  public final void resetAnalyticsData()
  {
    zzab();
    try
    {
      int i = getWritableDatabase().delete("messages", null, null) + 0;
      if (i > 0) {
        zzgg().zzir().zzg("Reset local analytics data. records", Integer.valueOf(i));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      for (;;)
      {
        zzgg().zzil().zzg("Error resetting local analytics data. error", localSQLiteException);
      }
    }
  }
  
  public final boolean zza(zzeu paramzzeu)
  {
    boolean bool = false;
    Parcel localParcel = Parcel.obtain();
    paramzzeu.writeToParcel(localParcel, 0);
    paramzzeu = localParcel.marshall();
    localParcel.recycle();
    if (paramzzeu.length > 131072) {
      zzgg().zzin().log("Event is too long for local database. Sending event directly to service");
    }
    for (;;)
    {
      return bool;
      bool = zza(0, paramzzeu);
    }
  }
  
  public final boolean zza(zzjs paramzzjs)
  {
    boolean bool = false;
    Parcel localParcel = Parcel.obtain();
    paramzzjs.writeToParcel(localParcel, 0);
    paramzzjs = localParcel.marshall();
    localParcel.recycle();
    if (paramzzjs.length > 131072) {
      zzgg().zzin().log("User property too long for local database. Sending directly to service");
    }
    for (;;)
    {
      return bool;
      bool = zza(1, paramzzjs);
    }
  }
  
  public final boolean zzc(zzef paramzzef)
  {
    zzgc();
    paramzzef = zzjv.zza(paramzzef);
    if (paramzzef.length > 131072) {
      zzgg().zzin().log("Conditional user property too long for local database. Sending directly to service");
    }
    for (boolean bool = false;; bool = zza(2, paramzzef)) {
      return bool;
    }
  }
  
  protected final boolean zzhh()
  {
    return false;
  }
  
  /* Error */
  public final java.util.List<com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable> zzp(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 47	com/google/android/gms/internal/measurement/zzhj:zzab	()V
    //   4: aload_0
    //   5: getfield 34	com/google/android/gms/internal/measurement/zzfc:zzaih	Z
    //   8: ifeq +7 -> 15
    //   11: aconst_null
    //   12: astore_2
    //   13: aload_2
    //   14: areturn
    //   15: new 302	java/util/ArrayList
    //   18: dup
    //   19: invokespecial 303	java/util/ArrayList:<init>	()V
    //   22: astore_3
    //   23: aload_0
    //   24: invokevirtual 20	com/google/android/gms/internal/measurement/zzhj:getContext	()Landroid/content/Context;
    //   27: ldc 22
    //   29: invokevirtual 309	android/content/Context:getDatabasePath	(Ljava/lang/String;)Ljava/io/File;
    //   32: invokevirtual 314	java/io/File:exists	()Z
    //   35: ifne +8 -> 43
    //   38: aload_3
    //   39: astore_2
    //   40: goto -27 -> 13
    //   43: iconst_5
    //   44: istore_1
    //   45: iconst_0
    //   46: istore 4
    //   48: iload 4
    //   50: iconst_5
    //   51: if_icmpge +816 -> 867
    //   54: aconst_null
    //   55: astore 5
    //   57: aconst_null
    //   58: astore 6
    //   60: aconst_null
    //   61: astore 7
    //   63: aconst_null
    //   64: astore 8
    //   66: aload_0
    //   67: invokespecial 69	com/google/android/gms/internal/measurement/zzfc:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   70: astore_2
    //   71: aload_2
    //   72: ifnonnull +21 -> 93
    //   75: aload_0
    //   76: iconst_1
    //   77: putfield 34	com/google/android/gms/internal/measurement/zzfc:zzaih	Z
    //   80: aload_2
    //   81: ifnull +7 -> 88
    //   84: aload_2
    //   85: invokevirtual 74	android/database/sqlite/SQLiteDatabase:close	()V
    //   88: aconst_null
    //   89: astore_2
    //   90: goto -77 -> 13
    //   93: aload_2
    //   94: invokevirtual 77	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   97: bipush 100
    //   99: invokestatic 317	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   102: astore 6
    //   104: aload_2
    //   105: ldc 115
    //   107: iconst_3
    //   108: anewarray 119	java/lang/String
    //   111: dup
    //   112: iconst_0
    //   113: ldc_w 319
    //   116: aastore
    //   117: dup
    //   118: iconst_1
    //   119: ldc 53
    //   121: aastore
    //   122: dup
    //   123: iconst_2
    //   124: ldc 65
    //   126: aastore
    //   127: aconst_null
    //   128: aconst_null
    //   129: aconst_null
    //   130: aconst_null
    //   131: ldc_w 321
    //   134: aload 6
    //   136: invokevirtual 325	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   139: astore 6
    //   141: ldc2_w 326
    //   144: lstore 9
    //   146: aload 6
    //   148: invokeinterface 330 1 0
    //   153: ifeq +638 -> 791
    //   156: aload 6
    //   158: iconst_0
    //   159: invokeinterface 93 2 0
    //   164: lstore 11
    //   166: aload 6
    //   168: iconst_1
    //   169: invokeinterface 334 2 0
    //   174: istore 13
    //   176: aload 6
    //   178: iconst_2
    //   179: invokeinterface 338 2 0
    //   184: astore 7
    //   186: iload 13
    //   188: ifne +340 -> 528
    //   191: invokestatic 185	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   194: astore 14
    //   196: aload 14
    //   198: aload 7
    //   200: iconst_0
    //   201: aload 7
    //   203: arraylength
    //   204: invokevirtual 342	android/os/Parcel:unmarshall	([BII)V
    //   207: aload 14
    //   209: iconst_0
    //   210: invokevirtual 346	android/os/Parcel:setDataPosition	(I)V
    //   213: getstatic 350	com/google/android/gms/internal/measurement/zzeu:CREATOR	Landroid/os/Parcelable$Creator;
    //   216: aload 14
    //   218: invokeinterface 356 2 0
    //   223: checkcast 187	com/google/android/gms/internal/measurement/zzeu
    //   226: astore 8
    //   228: aload 14
    //   230: invokevirtual 198	android/os/Parcel:recycle	()V
    //   233: lload 11
    //   235: lstore 9
    //   237: aload 8
    //   239: ifnull -93 -> 146
    //   242: aload_3
    //   243: aload 8
    //   245: invokeinterface 362 2 0
    //   250: pop
    //   251: lload 11
    //   253: lstore 9
    //   255: goto -109 -> 146
    //   258: astore 7
    //   260: aload_2
    //   261: astore 8
    //   263: aload 6
    //   265: astore 14
    //   267: aload 14
    //   269: astore 6
    //   271: aload 8
    //   273: astore_2
    //   274: aload_0
    //   275: invokevirtual 99	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   278: invokevirtual 105	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   281: ldc_w 364
    //   284: aload 7
    //   286: invokevirtual 155	com/google/android/gms/internal/measurement/zzfi:zzg	(Ljava/lang/String;Ljava/lang/Object;)V
    //   289: aload 14
    //   291: astore 6
    //   293: aload 8
    //   295: astore_2
    //   296: aload_0
    //   297: iconst_1
    //   298: putfield 34	com/google/android/gms/internal/measurement/zzfc:zzaih	Z
    //   301: aload 14
    //   303: ifnull +10 -> 313
    //   306: aload 14
    //   308: invokeinterface 149 1 0
    //   313: aload 8
    //   315: ifnull +658 -> 973
    //   318: aload 8
    //   320: invokevirtual 74	android/database/sqlite/SQLiteDatabase:close	()V
    //   323: iinc 4 1
    //   326: goto -278 -> 48
    //   329: astore 8
    //   331: aload_0
    //   332: invokevirtual 99	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   335: invokevirtual 105	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   338: ldc_w 366
    //   341: invokevirtual 113	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
    //   344: aload 14
    //   346: invokevirtual 198	android/os/Parcel:recycle	()V
    //   349: lload 11
    //   351: lstore 9
    //   353: goto -207 -> 146
    //   356: astore 14
    //   358: aload_2
    //   359: astore 8
    //   361: aload 6
    //   363: astore 14
    //   365: iload_1
    //   366: i2l
    //   367: lstore 9
    //   369: aload 14
    //   371: astore 6
    //   373: aload 8
    //   375: astore_2
    //   376: lload 9
    //   378: invokestatic 161	android/os/SystemClock:sleep	(J)V
    //   381: iload_1
    //   382: bipush 20
    //   384: iadd
    //   385: istore 13
    //   387: aload 14
    //   389: ifnull +10 -> 399
    //   392: aload 14
    //   394: invokeinterface 149 1 0
    //   399: iload 13
    //   401: istore_1
    //   402: aload 8
    //   404: ifnull -81 -> 323
    //   407: aload 8
    //   409: invokevirtual 74	android/database/sqlite/SQLiteDatabase:close	()V
    //   412: iload 13
    //   414: istore_1
    //   415: goto -92 -> 323
    //   418: astore 8
    //   420: aload 14
    //   422: invokevirtual 198	android/os/Parcel:recycle	()V
    //   425: aload 8
    //   427: athrow
    //   428: astore 7
    //   430: aload_2
    //   431: astore 8
    //   433: aload 6
    //   435: astore 14
    //   437: aload 8
    //   439: ifnull +30 -> 469
    //   442: aload 14
    //   444: astore 6
    //   446: aload 8
    //   448: astore_2
    //   449: aload 8
    //   451: invokevirtual 164	android/database/sqlite/SQLiteDatabase:inTransaction	()Z
    //   454: ifeq +15 -> 469
    //   457: aload 14
    //   459: astore 6
    //   461: aload 8
    //   463: astore_2
    //   464: aload 8
    //   466: invokevirtual 148	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   469: aload 14
    //   471: astore 6
    //   473: aload 8
    //   475: astore_2
    //   476: aload_0
    //   477: invokevirtual 99	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   480: invokevirtual 105	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   483: ldc_w 364
    //   486: aload 7
    //   488: invokevirtual 155	com/google/android/gms/internal/measurement/zzfi:zzg	(Ljava/lang/String;Ljava/lang/Object;)V
    //   491: aload 14
    //   493: astore 6
    //   495: aload 8
    //   497: astore_2
    //   498: aload_0
    //   499: iconst_1
    //   500: putfield 34	com/google/android/gms/internal/measurement/zzfc:zzaih	Z
    //   503: aload 14
    //   505: ifnull +10 -> 515
    //   508: aload 14
    //   510: invokeinterface 149 1 0
    //   515: aload 8
    //   517: ifnull +456 -> 973
    //   520: aload 8
    //   522: invokevirtual 74	android/database/sqlite/SQLiteDatabase:close	()V
    //   525: goto -202 -> 323
    //   528: iload 13
    //   530: iconst_1
    //   531: if_icmpne +131 -> 662
    //   534: invokestatic 185	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   537: astore 8
    //   539: aload 8
    //   541: aload 7
    //   543: iconst_0
    //   544: aload 7
    //   546: arraylength
    //   547: invokevirtual 342	android/os/Parcel:unmarshall	([BII)V
    //   550: aload 8
    //   552: iconst_0
    //   553: invokevirtual 346	android/os/Parcel:setDataPosition	(I)V
    //   556: getstatic 367	com/google/android/gms/internal/measurement/zzjs:CREATOR	Landroid/os/Parcelable$Creator;
    //   559: aload 8
    //   561: invokeinterface 356 2 0
    //   566: checkcast 206	com/google/android/gms/internal/measurement/zzjs
    //   569: astore 14
    //   571: aload 8
    //   573: invokevirtual 198	android/os/Parcel:recycle	()V
    //   576: lload 11
    //   578: lstore 9
    //   580: aload 14
    //   582: ifnull -436 -> 146
    //   585: aload_3
    //   586: aload 14
    //   588: invokeinterface 362 2 0
    //   593: pop
    //   594: lload 11
    //   596: lstore 9
    //   598: goto -452 -> 146
    //   601: astore 14
    //   603: aload 6
    //   605: ifnull +10 -> 615
    //   608: aload 6
    //   610: invokeinterface 149 1 0
    //   615: aload_2
    //   616: ifnull +7 -> 623
    //   619: aload_2
    //   620: invokevirtual 74	android/database/sqlite/SQLiteDatabase:close	()V
    //   623: aload 14
    //   625: athrow
    //   626: astore 14
    //   628: aload_0
    //   629: invokevirtual 99	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   632: invokevirtual 105	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   635: ldc_w 369
    //   638: invokevirtual 113	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
    //   641: aload 8
    //   643: invokevirtual 198	android/os/Parcel:recycle	()V
    //   646: aconst_null
    //   647: astore 14
    //   649: goto -73 -> 576
    //   652: astore 14
    //   654: aload 8
    //   656: invokevirtual 198	android/os/Parcel:recycle	()V
    //   659: aload 14
    //   661: athrow
    //   662: iload 13
    //   664: iconst_2
    //   665: if_icmpne +106 -> 771
    //   668: invokestatic 185	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   671: astore 8
    //   673: aload 8
    //   675: aload 7
    //   677: iconst_0
    //   678: aload 7
    //   680: arraylength
    //   681: invokevirtual 342	android/os/Parcel:unmarshall	([BII)V
    //   684: aload 8
    //   686: iconst_0
    //   687: invokevirtual 346	android/os/Parcel:setDataPosition	(I)V
    //   690: getstatic 372	com/google/android/gms/internal/measurement/zzef:CREATOR	Landroid/os/Parcelable$Creator;
    //   693: aload 8
    //   695: invokeinterface 356 2 0
    //   700: checkcast 371	com/google/android/gms/internal/measurement/zzef
    //   703: astore 14
    //   705: aload 8
    //   707: invokevirtual 198	android/os/Parcel:recycle	()V
    //   710: lload 11
    //   712: lstore 9
    //   714: aload 14
    //   716: ifnull -570 -> 146
    //   719: aload_3
    //   720: aload 14
    //   722: invokeinterface 362 2 0
    //   727: pop
    //   728: lload 11
    //   730: lstore 9
    //   732: goto -586 -> 146
    //   735: astore 14
    //   737: aload_0
    //   738: invokevirtual 99	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   741: invokevirtual 105	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   744: ldc_w 369
    //   747: invokevirtual 113	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
    //   750: aload 8
    //   752: invokevirtual 198	android/os/Parcel:recycle	()V
    //   755: aconst_null
    //   756: astore 14
    //   758: goto -48 -> 710
    //   761: astore 14
    //   763: aload 8
    //   765: invokevirtual 198	android/os/Parcel:recycle	()V
    //   768: aload 14
    //   770: athrow
    //   771: aload_0
    //   772: invokevirtual 99	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   775: invokevirtual 105	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   778: ldc_w 374
    //   781: invokevirtual 113	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
    //   784: lload 11
    //   786: lstore 9
    //   788: goto -642 -> 146
    //   791: aload_2
    //   792: ldc 115
    //   794: ldc_w 376
    //   797: iconst_1
    //   798: anewarray 119	java/lang/String
    //   801: dup
    //   802: iconst_0
    //   803: lload 9
    //   805: invokestatic 125	java/lang/Long:toString	(J)Ljava/lang/String;
    //   808: aastore
    //   809: invokevirtual 129	android/database/sqlite/SQLiteDatabase:delete	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
    //   812: aload_3
    //   813: invokeinterface 380 1 0
    //   818: if_icmpge +16 -> 834
    //   821: aload_0
    //   822: invokevirtual 99	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   825: invokevirtual 105	com/google/android/gms/internal/measurement/zzfg:zzil	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   828: ldc_w 382
    //   831: invokevirtual 113	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
    //   834: aload_2
    //   835: invokevirtual 145	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   838: aload_2
    //   839: invokevirtual 148	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   842: aload 6
    //   844: ifnull +10 -> 854
    //   847: aload 6
    //   849: invokeinterface 149 1 0
    //   854: aload_2
    //   855: ifnull +7 -> 862
    //   858: aload_2
    //   859: invokevirtual 74	android/database/sqlite/SQLiteDatabase:close	()V
    //   862: aload_3
    //   863: astore_2
    //   864: goto -851 -> 13
    //   867: aload_0
    //   868: invokevirtual 99	com/google/android/gms/internal/measurement/zzhj:zzgg	()Lcom/google/android/gms/internal/measurement/zzfg;
    //   871: invokevirtual 167	com/google/android/gms/internal/measurement/zzfg:zzin	()Lcom/google/android/gms/internal/measurement/zzfi;
    //   874: ldc_w 384
    //   877: invokevirtual 113	com/google/android/gms/internal/measurement/zzfi:log	(Ljava/lang/String;)V
    //   880: aconst_null
    //   881: astore_2
    //   882: goto -869 -> 13
    //   885: astore 14
    //   887: aconst_null
    //   888: astore 6
    //   890: aload 7
    //   892: astore_2
    //   893: goto -290 -> 603
    //   896: astore 14
    //   898: aconst_null
    //   899: astore 6
    //   901: goto -298 -> 603
    //   904: astore 14
    //   906: goto -303 -> 603
    //   909: astore 7
    //   911: aconst_null
    //   912: astore 14
    //   914: aload 6
    //   916: astore 8
    //   918: goto -481 -> 437
    //   921: astore 7
    //   923: aconst_null
    //   924: astore 14
    //   926: aload_2
    //   927: astore 8
    //   929: goto -492 -> 437
    //   932: astore_2
    //   933: aconst_null
    //   934: astore 14
    //   936: aload 5
    //   938: astore 8
    //   940: goto -575 -> 365
    //   943: astore 6
    //   945: aconst_null
    //   946: astore 14
    //   948: aload_2
    //   949: astore 8
    //   951: goto -586 -> 365
    //   954: astore 7
    //   956: aconst_null
    //   957: astore 14
    //   959: goto -692 -> 267
    //   962: astore 7
    //   964: aconst_null
    //   965: astore 14
    //   967: aload_2
    //   968: astore 8
    //   970: goto -703 -> 267
    //   973: goto -650 -> 323
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	976	0	this	zzfc
    //   0	976	1	paramInt	int
    //   12	915	2	localObject1	Object
    //   932	36	2	localSQLiteDatabaseLockedException1	SQLiteDatabaseLockedException
    //   22	841	3	localArrayList	java.util.ArrayList
    //   46	278	4	i	int
    //   55	882	5	localObject2	Object
    //   58	857	6	localObject3	Object
    //   943	1	6	localSQLiteDatabaseLockedException2	SQLiteDatabaseLockedException
    //   61	141	7	arrayOfByte	byte[]
    //   258	27	7	localSQLiteFullException1	SQLiteFullException
    //   428	463	7	localSQLiteException1	SQLiteException
    //   909	1	7	localSQLiteException2	SQLiteException
    //   921	1	7	localSQLiteException3	SQLiteException
    //   954	1	7	localSQLiteFullException2	SQLiteFullException
    //   962	1	7	localSQLiteFullException3	SQLiteFullException
    //   64	255	8	localObject4	Object
    //   329	1	8	localParseException1	com.google.android.gms.common.internal.safeparcel.SafeParcelReader.ParseException
    //   359	49	8	localObject5	Object
    //   418	8	8	localObject6	Object
    //   431	538	8	localObject7	Object
    //   144	660	9	l1	long
    //   164	621	11	l2	long
    //   174	492	13	j	int
    //   194	151	14	localObject8	Object
    //   356	1	14	localSQLiteDatabaseLockedException3	SQLiteDatabaseLockedException
    //   363	224	14	localObject9	Object
    //   601	23	14	localObject10	Object
    //   626	1	14	localParseException2	com.google.android.gms.common.internal.safeparcel.SafeParcelReader.ParseException
    //   647	1	14	localObject11	Object
    //   652	8	14	localObject12	Object
    //   703	18	14	localzzef	zzef
    //   735	1	14	localParseException3	com.google.android.gms.common.internal.safeparcel.SafeParcelReader.ParseException
    //   756	1	14	localObject13	Object
    //   761	8	14	localObject14	Object
    //   885	1	14	localObject15	Object
    //   896	1	14	localObject16	Object
    //   904	1	14	localObject17	Object
    //   912	54	14	localObject18	Object
    // Exception table:
    //   from	to	target	type
    //   146	186	258	android/database/sqlite/SQLiteFullException
    //   191	196	258	android/database/sqlite/SQLiteFullException
    //   228	233	258	android/database/sqlite/SQLiteFullException
    //   242	251	258	android/database/sqlite/SQLiteFullException
    //   344	349	258	android/database/sqlite/SQLiteFullException
    //   420	428	258	android/database/sqlite/SQLiteFullException
    //   534	539	258	android/database/sqlite/SQLiteFullException
    //   571	576	258	android/database/sqlite/SQLiteFullException
    //   585	594	258	android/database/sqlite/SQLiteFullException
    //   641	646	258	android/database/sqlite/SQLiteFullException
    //   654	662	258	android/database/sqlite/SQLiteFullException
    //   668	673	258	android/database/sqlite/SQLiteFullException
    //   705	710	258	android/database/sqlite/SQLiteFullException
    //   719	728	258	android/database/sqlite/SQLiteFullException
    //   750	755	258	android/database/sqlite/SQLiteFullException
    //   763	771	258	android/database/sqlite/SQLiteFullException
    //   771	784	258	android/database/sqlite/SQLiteFullException
    //   791	834	258	android/database/sqlite/SQLiteFullException
    //   834	842	258	android/database/sqlite/SQLiteFullException
    //   196	228	329	com/google/android/gms/common/internal/safeparcel/SafeParcelReader$ParseException
    //   146	186	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   191	196	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   228	233	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   242	251	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   344	349	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   420	428	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   534	539	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   571	576	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   585	594	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   641	646	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   654	662	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   668	673	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   705	710	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   719	728	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   750	755	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   763	771	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   771	784	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   791	834	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   834	842	356	android/database/sqlite/SQLiteDatabaseLockedException
    //   196	228	418	finally
    //   331	344	418	finally
    //   146	186	428	android/database/sqlite/SQLiteException
    //   191	196	428	android/database/sqlite/SQLiteException
    //   228	233	428	android/database/sqlite/SQLiteException
    //   242	251	428	android/database/sqlite/SQLiteException
    //   344	349	428	android/database/sqlite/SQLiteException
    //   420	428	428	android/database/sqlite/SQLiteException
    //   534	539	428	android/database/sqlite/SQLiteException
    //   571	576	428	android/database/sqlite/SQLiteException
    //   585	594	428	android/database/sqlite/SQLiteException
    //   641	646	428	android/database/sqlite/SQLiteException
    //   654	662	428	android/database/sqlite/SQLiteException
    //   668	673	428	android/database/sqlite/SQLiteException
    //   705	710	428	android/database/sqlite/SQLiteException
    //   719	728	428	android/database/sqlite/SQLiteException
    //   750	755	428	android/database/sqlite/SQLiteException
    //   763	771	428	android/database/sqlite/SQLiteException
    //   771	784	428	android/database/sqlite/SQLiteException
    //   791	834	428	android/database/sqlite/SQLiteException
    //   834	842	428	android/database/sqlite/SQLiteException
    //   146	186	601	finally
    //   191	196	601	finally
    //   228	233	601	finally
    //   242	251	601	finally
    //   344	349	601	finally
    //   420	428	601	finally
    //   534	539	601	finally
    //   571	576	601	finally
    //   585	594	601	finally
    //   641	646	601	finally
    //   654	662	601	finally
    //   668	673	601	finally
    //   705	710	601	finally
    //   719	728	601	finally
    //   750	755	601	finally
    //   763	771	601	finally
    //   771	784	601	finally
    //   791	834	601	finally
    //   834	842	601	finally
    //   539	571	626	com/google/android/gms/common/internal/safeparcel/SafeParcelReader$ParseException
    //   539	571	652	finally
    //   628	641	652	finally
    //   673	705	735	com/google/android/gms/common/internal/safeparcel/SafeParcelReader$ParseException
    //   673	705	761	finally
    //   737	750	761	finally
    //   66	71	885	finally
    //   75	80	896	finally
    //   93	141	896	finally
    //   274	289	904	finally
    //   296	301	904	finally
    //   376	381	904	finally
    //   449	457	904	finally
    //   464	469	904	finally
    //   476	491	904	finally
    //   498	503	904	finally
    //   66	71	909	android/database/sqlite/SQLiteException
    //   75	80	921	android/database/sqlite/SQLiteException
    //   93	141	921	android/database/sqlite/SQLiteException
    //   66	71	932	android/database/sqlite/SQLiteDatabaseLockedException
    //   75	80	943	android/database/sqlite/SQLiteDatabaseLockedException
    //   93	141	943	android/database/sqlite/SQLiteDatabaseLockedException
    //   66	71	954	android/database/sqlite/SQLiteFullException
    //   75	80	962	android/database/sqlite/SQLiteFullException
    //   93	141	962	android/database/sqlite/SQLiteFullException
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzfc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */