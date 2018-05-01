package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.SystemClock;

public final class zzchi
  extends zzcjl
{
  private final zzchj zzjbn = new zzchj(this, getContext(), "google_app_measurement_local.db");
  private boolean zzjbo;
  
  zzchi(zzcim paramzzcim)
  {
    super(paramzzcim);
  }
  
  private final SQLiteDatabase getWritableDatabase()
  {
    if (this.zzjbo) {
      return null;
    }
    SQLiteDatabase localSQLiteDatabase = this.zzjbn.getWritableDatabase();
    if (localSQLiteDatabase == null)
    {
      this.zzjbo = true;
      return null;
    }
    return localSQLiteDatabase;
  }
  
  @TargetApi(11)
  private final boolean zzb(int paramInt, byte[] paramArrayOfByte)
  {
    zzve();
    if (this.zzjbo) {
      return false;
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("type", Integer.valueOf(paramInt));
    localContentValues.put("entry", paramArrayOfByte);
    int i = 0;
    paramInt = 5;
    while (i < 5)
    {
      Object localObject5 = null;
      Object localObject1 = null;
      Object localObject3 = null;
      Object localObject6 = null;
      Object localObject7 = null;
      Cursor localCursor2 = null;
      Cursor localCursor1 = localCursor2;
      Object localObject4 = localObject6;
      paramArrayOfByte = (byte[])localObject7;
      try
      {
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        if (localSQLiteDatabase == null)
        {
          localCursor1 = localCursor2;
          localObject3 = localSQLiteDatabase;
          localObject4 = localObject6;
          localObject5 = localSQLiteDatabase;
          paramArrayOfByte = (byte[])localObject7;
          localObject1 = localSQLiteDatabase;
          this.zzjbo = true;
          if (localSQLiteDatabase != null) {
            localSQLiteDatabase.close();
          }
          return false;
        }
        localCursor1 = localCursor2;
        localObject3 = localSQLiteDatabase;
        localObject4 = localObject6;
        localObject5 = localSQLiteDatabase;
        paramArrayOfByte = (byte[])localObject7;
        localObject1 = localSQLiteDatabase;
        localSQLiteDatabase.beginTransaction();
        long l2 = 0L;
        localCursor1 = localCursor2;
        localObject3 = localSQLiteDatabase;
        localObject4 = localObject6;
        localObject5 = localSQLiteDatabase;
        paramArrayOfByte = (byte[])localObject7;
        localObject1 = localSQLiteDatabase;
        localCursor2 = localSQLiteDatabase.rawQuery("select count(1) from messages", null);
        long l1 = l2;
        if (localCursor2 != null)
        {
          l1 = l2;
          localCursor1 = localCursor2;
          localObject3 = localSQLiteDatabase;
          localObject4 = localCursor2;
          localObject5 = localSQLiteDatabase;
          paramArrayOfByte = localCursor2;
          localObject1 = localSQLiteDatabase;
          if (localCursor2.moveToFirst())
          {
            localCursor1 = localCursor2;
            localObject3 = localSQLiteDatabase;
            localObject4 = localCursor2;
            localObject5 = localSQLiteDatabase;
            paramArrayOfByte = localCursor2;
            localObject1 = localSQLiteDatabase;
            l1 = localCursor2.getLong(0);
          }
        }
        if (l1 >= 100000L)
        {
          localCursor1 = localCursor2;
          localObject3 = localSQLiteDatabase;
          localObject4 = localCursor2;
          localObject5 = localSQLiteDatabase;
          paramArrayOfByte = localCursor2;
          localObject1 = localSQLiteDatabase;
          zzawy().zzazd().log("Data loss, local db full");
          l1 = 100000L - l1 + 1L;
          localCursor1 = localCursor2;
          localObject3 = localSQLiteDatabase;
          localObject4 = localCursor2;
          localObject5 = localSQLiteDatabase;
          paramArrayOfByte = localCursor2;
          localObject1 = localSQLiteDatabase;
          l2 = localSQLiteDatabase.delete("messages", "rowid in (select rowid from messages order by rowid asc limit ?)", new String[] { Long.toString(l1) });
          if (l2 != l1)
          {
            localCursor1 = localCursor2;
            localObject3 = localSQLiteDatabase;
            localObject4 = localCursor2;
            localObject5 = localSQLiteDatabase;
            paramArrayOfByte = localCursor2;
            localObject1 = localSQLiteDatabase;
            zzawy().zzazd().zzd("Different delete count than expected in local db. expected, received, difference", Long.valueOf(l1), Long.valueOf(l2), Long.valueOf(l1 - l2));
          }
        }
        localCursor1 = localCursor2;
        localObject3 = localSQLiteDatabase;
        localObject4 = localCursor2;
        localObject5 = localSQLiteDatabase;
        paramArrayOfByte = localCursor2;
        localObject1 = localSQLiteDatabase;
        localSQLiteDatabase.insertOrThrow("messages", null, localContentValues);
        localCursor1 = localCursor2;
        localObject3 = localSQLiteDatabase;
        localObject4 = localCursor2;
        localObject5 = localSQLiteDatabase;
        paramArrayOfByte = localCursor2;
        localObject1 = localSQLiteDatabase;
        localSQLiteDatabase.setTransactionSuccessful();
        localCursor1 = localCursor2;
        localObject3 = localSQLiteDatabase;
        localObject4 = localCursor2;
        localObject5 = localSQLiteDatabase;
        paramArrayOfByte = localCursor2;
        localObject1 = localSQLiteDatabase;
        localSQLiteDatabase.endTransaction();
        if (localCursor2 != null) {
          localCursor2.close();
        }
        if (localSQLiteDatabase != null) {
          localSQLiteDatabase.close();
        }
        return true;
      }
      catch (SQLiteFullException localSQLiteFullException)
      {
        paramArrayOfByte = localCursor1;
        localObject1 = localObject3;
        zzawy().zzazd().zzj("Error writing entry to local database", localSQLiteFullException);
        paramArrayOfByte = localCursor1;
        localObject1 = localObject3;
        this.zzjbo = true;
        if (localCursor1 != null) {
          localCursor1.close();
        }
        j = paramInt;
        if (localObject3 != null)
        {
          ((SQLiteDatabase)localObject3).close();
          j = paramInt;
        }
        i += 1;
        paramInt = j;
      }
      catch (SQLiteException localSQLiteException)
      {
        int j;
        paramArrayOfByte = localSQLiteFullException;
        localObject1 = localObject5;
        if (Build.VERSION.SDK_INT >= 11)
        {
          paramArrayOfByte = localSQLiteFullException;
          localObject1 = localObject5;
          if ((localSQLiteException instanceof SQLiteDatabaseLockedException))
          {
            paramArrayOfByte = localSQLiteFullException;
            localObject1 = localObject5;
            SystemClock.sleep(paramInt);
            paramInt += 20;
          }
        }
        for (;;)
        {
          if (localSQLiteFullException != null) {
            localSQLiteFullException.close();
          }
          j = paramInt;
          if (localObject5 == null) {
            break;
          }
          ((SQLiteDatabase)localObject5).close();
          j = paramInt;
          break;
          if (localObject5 != null)
          {
            paramArrayOfByte = localSQLiteFullException;
            localObject1 = localObject5;
            if (((SQLiteDatabase)localObject5).inTransaction())
            {
              paramArrayOfByte = localSQLiteFullException;
              localObject1 = localObject5;
              ((SQLiteDatabase)localObject5).endTransaction();
            }
          }
          paramArrayOfByte = localSQLiteFullException;
          localObject1 = localObject5;
          zzawy().zzazd().zzj("Error writing entry to local database", localSQLiteException);
          paramArrayOfByte = localSQLiteFullException;
          localObject1 = localObject5;
          this.zzjbo = true;
        }
      }
      finally
      {
        if (paramArrayOfByte != null) {
          paramArrayOfByte.close();
        }
        if (localObject1 != null) {
          ((SQLiteDatabase)localObject1).close();
        }
      }
    }
    zzawy().zzazf().log("Failed to write entry to local database");
    return false;
  }
  
  public final void resetAnalyticsData()
  {
    zzve();
    try
    {
      int i = getWritableDatabase().delete("messages", null, null) + 0;
      if (i > 0) {
        zzawy().zzazj().zzj("Reset local analytics data. records", Integer.valueOf(i));
      }
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      zzawy().zzazd().zzj("Error resetting local analytics data. error", localSQLiteException);
    }
  }
  
  public final boolean zza(zzcha paramzzcha)
  {
    Parcel localParcel = Parcel.obtain();
    paramzzcha.writeToParcel(localParcel, 0);
    paramzzcha = localParcel.marshall();
    localParcel.recycle();
    if (paramzzcha.length > 131072)
    {
      zzawy().zzazf().log("Event is too long for local database. Sending event directly to service");
      return false;
    }
    return zzb(0, paramzzcha);
  }
  
  public final boolean zza(zzcln paramzzcln)
  {
    Parcel localParcel = Parcel.obtain();
    paramzzcln.writeToParcel(localParcel, 0);
    paramzzcln = localParcel.marshall();
    localParcel.recycle();
    if (paramzzcln.length > 131072)
    {
      zzawy().zzazf().log("User property too long for local database. Sending directly to service");
      return false;
    }
    return zzb(1, paramzzcln);
  }
  
  protected final boolean zzaxz()
  {
    return false;
  }
  
  public final boolean zzc(zzcgl paramzzcgl)
  {
    zzawu();
    paramzzcgl = zzclq.zza(paramzzcgl);
    if (paramzzcgl.length > 131072)
    {
      zzawy().zzazf().log("Conditional user property too long for local database. Sending directly to service");
      return false;
    }
    return zzb(2, paramzzcgl);
  }
  
  /* Error */
  @TargetApi(11)
  public final java.util.List<zzbfm> zzeb(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 47	com/google/android/gms/internal/zzcjk:zzve	()V
    //   4: aload_0
    //   5: getfield 32	com/google/android/gms/internal/zzchi:zzjbo	Z
    //   8: ifeq +5 -> 13
    //   11: aconst_null
    //   12: areturn
    //   13: new 307	java/util/ArrayList
    //   16: dup
    //   17: invokespecial 308	java/util/ArrayList:<init>	()V
    //   20: astore 12
    //   22: aload_0
    //   23: invokevirtual 20	com/google/android/gms/internal/zzcjk:getContext	()Landroid/content/Context;
    //   26: ldc 22
    //   28: invokevirtual 314	android/content/Context:getDatabasePath	(Ljava/lang/String;)Ljava/io/File;
    //   31: invokevirtual 319	java/io/File:exists	()Z
    //   34: ifne +6 -> 40
    //   37: aload 12
    //   39: areturn
    //   40: iconst_5
    //   41: istore_1
    //   42: iconst_0
    //   43: istore_3
    //   44: iload_3
    //   45: iconst_5
    //   46: if_icmpge +764 -> 810
    //   49: aconst_null
    //   50: astore 10
    //   52: aconst_null
    //   53: astore 9
    //   55: aload_0
    //   56: invokespecial 69	com/google/android/gms/internal/zzchi:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   59: astore 8
    //   61: aload 8
    //   63: ifnonnull +20 -> 83
    //   66: aload_0
    //   67: iconst_1
    //   68: putfield 32	com/google/android/gms/internal/zzchi:zzjbo	Z
    //   71: aload 8
    //   73: ifnull +8 -> 81
    //   76: aload 8
    //   78: invokevirtual 74	android/database/sqlite/SQLiteDatabase:close	()V
    //   81: aconst_null
    //   82: areturn
    //   83: aload 8
    //   85: invokevirtual 77	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   88: bipush 100
    //   90: invokestatic 322	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   93: astore 9
    //   95: aload 8
    //   97: ldc 115
    //   99: iconst_3
    //   100: anewarray 119	java/lang/String
    //   103: dup
    //   104: iconst_0
    //   105: ldc_w 324
    //   108: aastore
    //   109: dup
    //   110: iconst_1
    //   111: ldc 53
    //   113: aastore
    //   114: dup
    //   115: iconst_2
    //   116: ldc 65
    //   118: aastore
    //   119: aconst_null
    //   120: aconst_null
    //   121: aconst_null
    //   122: aconst_null
    //   123: ldc_w 326
    //   126: aload 9
    //   128: invokevirtual 330	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   131: astore 10
    //   133: ldc2_w 331
    //   136: lstore 4
    //   138: aload 10
    //   140: invokeinterface 335 1 0
    //   145: ifeq +542 -> 687
    //   148: aload 10
    //   150: iconst_0
    //   151: invokeinterface 93 2 0
    //   156: lstore 6
    //   158: aload 10
    //   160: iconst_1
    //   161: invokeinterface 339 2 0
    //   166: istore_2
    //   167: aload 10
    //   169: iconst_2
    //   170: invokeinterface 343 2 0
    //   175: astore 13
    //   177: iload_2
    //   178: ifne +271 -> 449
    //   181: invokestatic 195	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   184: astore 9
    //   186: aload 9
    //   188: aload 13
    //   190: iconst_0
    //   191: aload 13
    //   193: arraylength
    //   194: invokevirtual 347	android/os/Parcel:unmarshall	([BII)V
    //   197: aload 9
    //   199: iconst_0
    //   200: invokevirtual 351	android/os/Parcel:setDataPosition	(I)V
    //   203: getstatic 355	com/google/android/gms/internal/zzcha:CREATOR	Landroid/os/Parcelable$Creator;
    //   206: aload 9
    //   208: invokeinterface 361 2 0
    //   213: checkcast 197	com/google/android/gms/internal/zzcha
    //   216: astore 11
    //   218: aload 9
    //   220: invokevirtual 208	android/os/Parcel:recycle	()V
    //   223: lload 6
    //   225: lstore 4
    //   227: aload 11
    //   229: ifnull -91 -> 138
    //   232: aload 12
    //   234: aload 11
    //   236: invokeinterface 367 2 0
    //   241: pop
    //   242: lload 6
    //   244: lstore 4
    //   246: goto -108 -> 138
    //   249: astore 11
    //   251: aload 8
    //   253: astore 9
    //   255: aload 10
    //   257: astore 8
    //   259: aload 11
    //   261: astore 10
    //   263: aload_0
    //   264: invokevirtual 99	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   267: invokevirtual 105	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   270: ldc_w 369
    //   273: aload 10
    //   275: invokevirtual 155	com/google/android/gms/internal/zzcho:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   278: aload_0
    //   279: iconst_1
    //   280: putfield 32	com/google/android/gms/internal/zzchi:zzjbo	Z
    //   283: aload 8
    //   285: ifnull +10 -> 295
    //   288: aload 8
    //   290: invokeinterface 149 1 0
    //   295: aload 9
    //   297: ifnull +625 -> 922
    //   300: aload 9
    //   302: invokevirtual 74	android/database/sqlite/SQLiteDatabase:close	()V
    //   305: iload_3
    //   306: iconst_1
    //   307: iadd
    //   308: istore_3
    //   309: goto -265 -> 44
    //   312: astore 11
    //   314: aload_0
    //   315: invokevirtual 99	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   318: invokevirtual 105	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   321: ldc_w 371
    //   324: invokevirtual 113	com/google/android/gms/internal/zzcho:log	(Ljava/lang/String;)V
    //   327: aload 9
    //   329: invokevirtual 208	android/os/Parcel:recycle	()V
    //   332: lload 6
    //   334: lstore 4
    //   336: goto -198 -> 138
    //   339: astore 11
    //   341: aload 8
    //   343: astore 9
    //   345: aload 11
    //   347: astore 8
    //   349: getstatic 161	android/os/Build$VERSION:SDK_INT	I
    //   352: bipush 11
    //   354: if_icmplt +413 -> 767
    //   357: aload 8
    //   359: instanceof 163
    //   362: ifeq +405 -> 767
    //   365: iload_1
    //   366: i2l
    //   367: invokestatic 169	android/os/SystemClock:sleep	(J)V
    //   370: iload_1
    //   371: bipush 20
    //   373: iadd
    //   374: istore_2
    //   375: aload 10
    //   377: ifnull +10 -> 387
    //   380: aload 10
    //   382: invokeinterface 149 1 0
    //   387: iload_2
    //   388: istore_1
    //   389: aload 9
    //   391: ifnull -86 -> 305
    //   394: aload 9
    //   396: invokevirtual 74	android/database/sqlite/SQLiteDatabase:close	()V
    //   399: iload_2
    //   400: istore_1
    //   401: goto -96 -> 305
    //   404: astore 11
    //   406: aload 9
    //   408: invokevirtual 208	android/os/Parcel:recycle	()V
    //   411: aload 11
    //   413: athrow
    //   414: astore 11
    //   416: aload 8
    //   418: astore 9
    //   420: aload 11
    //   422: astore 8
    //   424: aload 10
    //   426: ifnull +10 -> 436
    //   429: aload 10
    //   431: invokeinterface 149 1 0
    //   436: aload 9
    //   438: ifnull +8 -> 446
    //   441: aload 9
    //   443: invokevirtual 74	android/database/sqlite/SQLiteDatabase:close	()V
    //   446: aload 8
    //   448: athrow
    //   449: iload_2
    //   450: iconst_1
    //   451: if_icmpne +107 -> 558
    //   454: invokestatic 195	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   457: astore 11
    //   459: aload 11
    //   461: aload 13
    //   463: iconst_0
    //   464: aload 13
    //   466: arraylength
    //   467: invokevirtual 347	android/os/Parcel:unmarshall	([BII)V
    //   470: aload 11
    //   472: iconst_0
    //   473: invokevirtual 351	android/os/Parcel:setDataPosition	(I)V
    //   476: getstatic 372	com/google/android/gms/internal/zzcln:CREATOR	Landroid/os/Parcelable$Creator;
    //   479: aload 11
    //   481: invokeinterface 361 2 0
    //   486: checkcast 216	com/google/android/gms/internal/zzcln
    //   489: astore 9
    //   491: aload 11
    //   493: invokevirtual 208	android/os/Parcel:recycle	()V
    //   496: lload 6
    //   498: lstore 4
    //   500: aload 9
    //   502: ifnull -364 -> 138
    //   505: aload 12
    //   507: aload 9
    //   509: invokeinterface 367 2 0
    //   514: pop
    //   515: lload 6
    //   517: lstore 4
    //   519: goto -381 -> 138
    //   522: astore 9
    //   524: aload_0
    //   525: invokevirtual 99	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   528: invokevirtual 105	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   531: ldc_w 374
    //   534: invokevirtual 113	com/google/android/gms/internal/zzcho:log	(Ljava/lang/String;)V
    //   537: aload 11
    //   539: invokevirtual 208	android/os/Parcel:recycle	()V
    //   542: aconst_null
    //   543: astore 9
    //   545: goto -49 -> 496
    //   548: astore 9
    //   550: aload 11
    //   552: invokevirtual 208	android/os/Parcel:recycle	()V
    //   555: aload 9
    //   557: athrow
    //   558: iload_2
    //   559: iconst_2
    //   560: if_icmpne +107 -> 667
    //   563: invokestatic 195	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   566: astore 11
    //   568: aload 11
    //   570: aload 13
    //   572: iconst_0
    //   573: aload 13
    //   575: arraylength
    //   576: invokevirtual 347	android/os/Parcel:unmarshall	([BII)V
    //   579: aload 11
    //   581: iconst_0
    //   582: invokevirtual 351	android/os/Parcel:setDataPosition	(I)V
    //   585: getstatic 377	com/google/android/gms/internal/zzcgl:CREATOR	Landroid/os/Parcelable$Creator;
    //   588: aload 11
    //   590: invokeinterface 361 2 0
    //   595: checkcast 376	com/google/android/gms/internal/zzcgl
    //   598: astore 9
    //   600: aload 11
    //   602: invokevirtual 208	android/os/Parcel:recycle	()V
    //   605: lload 6
    //   607: lstore 4
    //   609: aload 9
    //   611: ifnull -473 -> 138
    //   614: aload 12
    //   616: aload 9
    //   618: invokeinterface 367 2 0
    //   623: pop
    //   624: lload 6
    //   626: lstore 4
    //   628: goto -490 -> 138
    //   631: astore 9
    //   633: aload_0
    //   634: invokevirtual 99	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   637: invokevirtual 105	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   640: ldc_w 374
    //   643: invokevirtual 113	com/google/android/gms/internal/zzcho:log	(Ljava/lang/String;)V
    //   646: aload 11
    //   648: invokevirtual 208	android/os/Parcel:recycle	()V
    //   651: aconst_null
    //   652: astore 9
    //   654: goto -49 -> 605
    //   657: astore 9
    //   659: aload 11
    //   661: invokevirtual 208	android/os/Parcel:recycle	()V
    //   664: aload 9
    //   666: athrow
    //   667: aload_0
    //   668: invokevirtual 99	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   671: invokevirtual 105	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   674: ldc_w 379
    //   677: invokevirtual 113	com/google/android/gms/internal/zzcho:log	(Ljava/lang/String;)V
    //   680: lload 6
    //   682: lstore 4
    //   684: goto -546 -> 138
    //   687: aload 8
    //   689: ldc 115
    //   691: ldc_w 381
    //   694: iconst_1
    //   695: anewarray 119	java/lang/String
    //   698: dup
    //   699: iconst_0
    //   700: lload 4
    //   702: invokestatic 125	java/lang/Long:toString	(J)Ljava/lang/String;
    //   705: aastore
    //   706: invokevirtual 129	android/database/sqlite/SQLiteDatabase:delete	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
    //   709: aload 12
    //   711: invokeinterface 385 1 0
    //   716: if_icmpge +16 -> 732
    //   719: aload_0
    //   720: invokevirtual 99	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   723: invokevirtual 105	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   726: ldc_w 387
    //   729: invokevirtual 113	com/google/android/gms/internal/zzcho:log	(Ljava/lang/String;)V
    //   732: aload 8
    //   734: invokevirtual 145	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   737: aload 8
    //   739: invokevirtual 148	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   742: aload 10
    //   744: ifnull +10 -> 754
    //   747: aload 10
    //   749: invokeinterface 149 1 0
    //   754: aload 8
    //   756: ifnull +8 -> 764
    //   759: aload 8
    //   761: invokevirtual 74	android/database/sqlite/SQLiteDatabase:close	()V
    //   764: aload 12
    //   766: areturn
    //   767: aload 9
    //   769: ifnull +16 -> 785
    //   772: aload 9
    //   774: invokevirtual 172	android/database/sqlite/SQLiteDatabase:inTransaction	()Z
    //   777: ifeq +8 -> 785
    //   780: aload 9
    //   782: invokevirtual 148	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   785: aload_0
    //   786: invokevirtual 99	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   789: invokevirtual 105	com/google/android/gms/internal/zzchm:zzazd	()Lcom/google/android/gms/internal/zzcho;
    //   792: ldc_w 369
    //   795: aload 8
    //   797: invokevirtual 155	com/google/android/gms/internal/zzcho:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   800: aload_0
    //   801: iconst_1
    //   802: putfield 32	com/google/android/gms/internal/zzchi:zzjbo	Z
    //   805: iload_1
    //   806: istore_2
    //   807: goto -432 -> 375
    //   810: aload_0
    //   811: invokevirtual 99	com/google/android/gms/internal/zzcjk:zzawy	()Lcom/google/android/gms/internal/zzchm;
    //   814: invokevirtual 175	com/google/android/gms/internal/zzchm:zzazf	()Lcom/google/android/gms/internal/zzcho;
    //   817: ldc_w 389
    //   820: invokevirtual 113	com/google/android/gms/internal/zzcho:log	(Ljava/lang/String;)V
    //   823: aconst_null
    //   824: areturn
    //   825: astore 8
    //   827: aconst_null
    //   828: astore 11
    //   830: aload 10
    //   832: astore 9
    //   834: aload 11
    //   836: astore 10
    //   838: goto -414 -> 424
    //   841: astore 11
    //   843: aconst_null
    //   844: astore 10
    //   846: aload 8
    //   848: astore 9
    //   850: aload 11
    //   852: astore 8
    //   854: goto -430 -> 424
    //   857: astore 11
    //   859: aload 8
    //   861: astore 10
    //   863: aload 11
    //   865: astore 8
    //   867: goto -443 -> 424
    //   870: astore 8
    //   872: goto -448 -> 424
    //   875: astore 8
    //   877: aconst_null
    //   878: astore 10
    //   880: goto -531 -> 349
    //   883: astore 11
    //   885: aconst_null
    //   886: astore 10
    //   888: aload 8
    //   890: astore 9
    //   892: aload 11
    //   894: astore 8
    //   896: goto -547 -> 349
    //   899: astore 10
    //   901: aconst_null
    //   902: astore 9
    //   904: aconst_null
    //   905: astore 8
    //   907: goto -644 -> 263
    //   910: astore 10
    //   912: aload 8
    //   914: astore 9
    //   916: aconst_null
    //   917: astore 8
    //   919: goto -656 -> 263
    //   922: goto -617 -> 305
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	925	0	this	zzchi
    //   0	925	1	paramInt	int
    //   166	641	2	i	int
    //   43	266	3	j	int
    //   136	565	4	l1	long
    //   156	525	6	l2	long
    //   59	737	8	localObject1	Object
    //   825	22	8	localObject2	Object
    //   852	14	8	localObject3	Object
    //   870	1	8	localObject4	Object
    //   875	14	8	localSQLiteException1	SQLiteException
    //   894	24	8	localObject5	Object
    //   53	455	9	localObject6	Object
    //   522	1	9	localzzbfo1	zzbfo
    //   543	1	9	localObject7	Object
    //   548	8	9	localObject8	Object
    //   598	19	9	localzzcgl	zzcgl
    //   631	1	9	localzzbfo2	zzbfo
    //   652	1	9	localObject9	Object
    //   657	124	9	localObject10	Object
    //   832	83	9	localObject11	Object
    //   50	837	10	localObject12	Object
    //   899	1	10	localSQLiteFullException1	SQLiteFullException
    //   910	1	10	localSQLiteFullException2	SQLiteFullException
    //   216	19	11	localzzcha	zzcha
    //   249	11	11	localSQLiteFullException3	SQLiteFullException
    //   312	1	11	localzzbfo3	zzbfo
    //   339	7	11	localSQLiteException2	SQLiteException
    //   404	8	11	localObject13	Object
    //   414	7	11	localObject14	Object
    //   457	378	11	localParcel	Parcel
    //   841	10	11	localObject15	Object
    //   857	7	11	localObject16	Object
    //   883	10	11	localSQLiteException3	SQLiteException
    //   20	745	12	localArrayList	java.util.ArrayList
    //   175	399	13	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   138	177	249	android/database/sqlite/SQLiteFullException
    //   181	186	249	android/database/sqlite/SQLiteFullException
    //   218	223	249	android/database/sqlite/SQLiteFullException
    //   232	242	249	android/database/sqlite/SQLiteFullException
    //   327	332	249	android/database/sqlite/SQLiteFullException
    //   406	414	249	android/database/sqlite/SQLiteFullException
    //   454	459	249	android/database/sqlite/SQLiteFullException
    //   491	496	249	android/database/sqlite/SQLiteFullException
    //   505	515	249	android/database/sqlite/SQLiteFullException
    //   537	542	249	android/database/sqlite/SQLiteFullException
    //   550	558	249	android/database/sqlite/SQLiteFullException
    //   563	568	249	android/database/sqlite/SQLiteFullException
    //   600	605	249	android/database/sqlite/SQLiteFullException
    //   614	624	249	android/database/sqlite/SQLiteFullException
    //   646	651	249	android/database/sqlite/SQLiteFullException
    //   659	667	249	android/database/sqlite/SQLiteFullException
    //   667	680	249	android/database/sqlite/SQLiteFullException
    //   687	732	249	android/database/sqlite/SQLiteFullException
    //   732	742	249	android/database/sqlite/SQLiteFullException
    //   186	218	312	com/google/android/gms/internal/zzbfo
    //   138	177	339	android/database/sqlite/SQLiteException
    //   181	186	339	android/database/sqlite/SQLiteException
    //   218	223	339	android/database/sqlite/SQLiteException
    //   232	242	339	android/database/sqlite/SQLiteException
    //   327	332	339	android/database/sqlite/SQLiteException
    //   406	414	339	android/database/sqlite/SQLiteException
    //   454	459	339	android/database/sqlite/SQLiteException
    //   491	496	339	android/database/sqlite/SQLiteException
    //   505	515	339	android/database/sqlite/SQLiteException
    //   537	542	339	android/database/sqlite/SQLiteException
    //   550	558	339	android/database/sqlite/SQLiteException
    //   563	568	339	android/database/sqlite/SQLiteException
    //   600	605	339	android/database/sqlite/SQLiteException
    //   614	624	339	android/database/sqlite/SQLiteException
    //   646	651	339	android/database/sqlite/SQLiteException
    //   659	667	339	android/database/sqlite/SQLiteException
    //   667	680	339	android/database/sqlite/SQLiteException
    //   687	732	339	android/database/sqlite/SQLiteException
    //   732	742	339	android/database/sqlite/SQLiteException
    //   186	218	404	finally
    //   314	327	404	finally
    //   138	177	414	finally
    //   181	186	414	finally
    //   218	223	414	finally
    //   232	242	414	finally
    //   327	332	414	finally
    //   406	414	414	finally
    //   454	459	414	finally
    //   491	496	414	finally
    //   505	515	414	finally
    //   537	542	414	finally
    //   550	558	414	finally
    //   563	568	414	finally
    //   600	605	414	finally
    //   614	624	414	finally
    //   646	651	414	finally
    //   659	667	414	finally
    //   667	680	414	finally
    //   687	732	414	finally
    //   732	742	414	finally
    //   459	491	522	com/google/android/gms/internal/zzbfo
    //   459	491	548	finally
    //   524	537	548	finally
    //   568	600	631	com/google/android/gms/internal/zzbfo
    //   568	600	657	finally
    //   633	646	657	finally
    //   55	61	825	finally
    //   66	71	841	finally
    //   83	133	841	finally
    //   263	283	857	finally
    //   349	370	870	finally
    //   772	785	870	finally
    //   785	805	870	finally
    //   55	61	875	android/database/sqlite/SQLiteException
    //   66	71	883	android/database/sqlite/SQLiteException
    //   83	133	883	android/database/sqlite/SQLiteException
    //   55	61	899	android/database/sqlite/SQLiteFullException
    //   66	71	910	android/database/sqlite/SQLiteFullException
    //   83	133	910	android/database/sqlite/SQLiteFullException
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzchi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */