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
import android.support.annotation.WorkerThread;

public final class zzcfh
  extends zzchj
{
  private final zzcfi zzbqF = new zzcfi(this, super.getContext(), zzcem.zzxD());
  private boolean zzbqG;
  
  zzcfh(zzcgl paramzzcgl)
  {
    super(paramzzcgl);
  }
  
  @WorkerThread
  private final SQLiteDatabase getWritableDatabase()
  {
    if (this.zzbqG) {
      return null;
    }
    SQLiteDatabase localSQLiteDatabase = this.zzbqF.getWritableDatabase();
    if (localSQLiteDatabase == null)
    {
      this.zzbqG = true;
      return null;
    }
    return localSQLiteDatabase;
  }
  
  @TargetApi(11)
  @WorkerThread
  private final boolean zza(int paramInt, byte[] paramArrayOfByte)
  {
    super.zzwp();
    super.zzjC();
    if (this.zzbqG) {
      return false;
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("type", Integer.valueOf(paramInt));
    localContentValues.put("entry", paramArrayOfByte);
    zzcem.zzxN();
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
          this.zzbqG = true;
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
          super.zzwF().zzyx().log("Data loss, local db full");
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
            super.zzwF().zzyx().zzd("Different delete count than expected in local db. expected, received, difference", Long.valueOf(l1), Long.valueOf(l2), Long.valueOf(l1 - l2));
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
        super.zzwF().zzyx().zzj("Error writing entry to local database", localSQLiteFullException);
        paramArrayOfByte = localCursor1;
        localObject1 = localObject3;
        this.zzbqG = true;
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
          super.zzwF().zzyx().zzj("Error writing entry to local database", localSQLiteException);
          paramArrayOfByte = localSQLiteFullException;
          localObject1 = localObject5;
          this.zzbqG = true;
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
    super.zzwF().zzyz().log("Failed to write entry to local database");
    return false;
  }
  
  public final boolean zza(zzcez paramzzcez)
  {
    Parcel localParcel = Parcel.obtain();
    paramzzcez.writeToParcel(localParcel, 0);
    paramzzcez = localParcel.marshall();
    localParcel.recycle();
    if (paramzzcez.length > 131072)
    {
      super.zzwF().zzyz().log("Event is too long for local database. Sending event directly to service");
      return false;
    }
    return zza(0, paramzzcez);
  }
  
  public final boolean zza(zzcji paramzzcji)
  {
    Parcel localParcel = Parcel.obtain();
    paramzzcji.writeToParcel(localParcel, 0);
    paramzzcji = localParcel.marshall();
    localParcel.recycle();
    if (paramzzcji.length > 131072)
    {
      super.zzwF().zzyz().log("User property too long for local database. Sending directly to service");
      return false;
    }
    return zza(1, paramzzcji);
  }
  
  /* Error */
  @TargetApi(11)
  public final java.util.List<com.google.android.gms.common.internal.safeparcel.zza> zzbp(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 54	com/google/android/gms/internal/zzchj:zzjC	()V
    //   4: aload_0
    //   5: invokespecial 51	com/google/android/gms/internal/zzchj:zzwp	()V
    //   8: aload_0
    //   9: getfield 35	com/google/android/gms/internal/zzcfh:zzbqG	Z
    //   12: ifeq +5 -> 17
    //   15: aconst_null
    //   16: areturn
    //   17: new 225	java/util/ArrayList
    //   20: dup
    //   21: invokespecial 226	java/util/ArrayList:<init>	()V
    //   24: astore 12
    //   26: aload_0
    //   27: invokespecial 18	com/google/android/gms/internal/zzchj:getContext	()Landroid/content/Context;
    //   30: invokestatic 24	com/google/android/gms/internal/zzcem:zzxD	()Ljava/lang/String;
    //   33: invokevirtual 232	android/content/Context:getDatabasePath	(Ljava/lang/String;)Ljava/io/File;
    //   36: invokevirtual 237	java/io/File:exists	()Z
    //   39: ifne +6 -> 45
    //   42: aload 12
    //   44: areturn
    //   45: iconst_5
    //   46: istore_1
    //   47: iconst_0
    //   48: istore_3
    //   49: iload_3
    //   50: iconst_5
    //   51: if_icmpge +762 -> 813
    //   54: aconst_null
    //   55: astore 10
    //   57: aconst_null
    //   58: astore 9
    //   60: aload_0
    //   61: invokespecial 80	com/google/android/gms/internal/zzcfh:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   64: astore 8
    //   66: aload 8
    //   68: ifnonnull +20 -> 88
    //   71: aload_0
    //   72: iconst_1
    //   73: putfield 35	com/google/android/gms/internal/zzcfh:zzbqG	Z
    //   76: aload 8
    //   78: ifnull +8 -> 86
    //   81: aload 8
    //   83: invokevirtual 85	android/database/sqlite/SQLiteDatabase:close	()V
    //   86: aconst_null
    //   87: areturn
    //   88: aload 8
    //   90: invokevirtual 88	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   93: bipush 100
    //   95: invokestatic 240	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   98: astore 9
    //   100: aload 8
    //   102: ldc 126
    //   104: iconst_3
    //   105: anewarray 130	java/lang/String
    //   108: dup
    //   109: iconst_0
    //   110: ldc -14
    //   112: aastore
    //   113: dup
    //   114: iconst_1
    //   115: ldc 60
    //   117: aastore
    //   118: dup
    //   119: iconst_2
    //   120: ldc 72
    //   122: aastore
    //   123: aconst_null
    //   124: aconst_null
    //   125: aconst_null
    //   126: aconst_null
    //   127: ldc -12
    //   129: aload 9
    //   131: invokevirtual 248	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   134: astore 10
    //   136: ldc2_w 249
    //   139: lstore 4
    //   141: aload 10
    //   143: invokeinterface 253 1 0
    //   148: ifeq +542 -> 690
    //   151: aload 10
    //   153: iconst_0
    //   154: invokeinterface 104 2 0
    //   159: lstore 6
    //   161: aload 10
    //   163: iconst_1
    //   164: invokeinterface 257 2 0
    //   169: istore_2
    //   170: aload 10
    //   172: iconst_2
    //   173: invokeinterface 261 2 0
    //   178: astore 13
    //   180: iload_2
    //   181: ifne +271 -> 452
    //   184: invokestatic 195	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   187: astore 9
    //   189: aload 9
    //   191: aload 13
    //   193: iconst_0
    //   194: aload 13
    //   196: arraylength
    //   197: invokevirtual 265	android/os/Parcel:unmarshall	([BII)V
    //   200: aload 9
    //   202: iconst_0
    //   203: invokevirtual 269	android/os/Parcel:setDataPosition	(I)V
    //   206: getstatic 273	com/google/android/gms/internal/zzcez:CREATOR	Landroid/os/Parcelable$Creator;
    //   209: aload 9
    //   211: invokeinterface 279 2 0
    //   216: checkcast 197	com/google/android/gms/internal/zzcez
    //   219: astore 11
    //   221: aload 9
    //   223: invokevirtual 208	android/os/Parcel:recycle	()V
    //   226: lload 6
    //   228: lstore 4
    //   230: aload 11
    //   232: ifnull -91 -> 141
    //   235: aload 12
    //   237: aload 11
    //   239: invokeinterface 285 2 0
    //   244: pop
    //   245: lload 6
    //   247: lstore 4
    //   249: goto -108 -> 141
    //   252: astore 11
    //   254: aload 8
    //   256: astore 9
    //   258: aload 10
    //   260: astore 8
    //   262: aload 11
    //   264: astore 10
    //   266: aload_0
    //   267: invokespecial 110	com/google/android/gms/internal/zzchj:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   270: invokevirtual 116	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   273: ldc_w 287
    //   276: aload 10
    //   278: invokevirtual 166	com/google/android/gms/internal/zzcfn:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   281: aload_0
    //   282: iconst_1
    //   283: putfield 35	com/google/android/gms/internal/zzcfh:zzbqG	Z
    //   286: aload 8
    //   288: ifnull +10 -> 298
    //   291: aload 8
    //   293: invokeinterface 160 1 0
    //   298: aload 9
    //   300: ifnull +625 -> 925
    //   303: aload 9
    //   305: invokevirtual 85	android/database/sqlite/SQLiteDatabase:close	()V
    //   308: iload_3
    //   309: iconst_1
    //   310: iadd
    //   311: istore_3
    //   312: goto -263 -> 49
    //   315: astore 11
    //   317: aload_0
    //   318: invokespecial 110	com/google/android/gms/internal/zzchj:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   321: invokevirtual 116	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   324: ldc_w 289
    //   327: invokevirtual 124	com/google/android/gms/internal/zzcfn:log	(Ljava/lang/String;)V
    //   330: aload 9
    //   332: invokevirtual 208	android/os/Parcel:recycle	()V
    //   335: lload 6
    //   337: lstore 4
    //   339: goto -198 -> 141
    //   342: astore 11
    //   344: aload 8
    //   346: astore 9
    //   348: aload 11
    //   350: astore 8
    //   352: getstatic 172	android/os/Build$VERSION:SDK_INT	I
    //   355: bipush 11
    //   357: if_icmplt +413 -> 770
    //   360: aload 8
    //   362: instanceof 174
    //   365: ifeq +405 -> 770
    //   368: iload_1
    //   369: i2l
    //   370: invokestatic 180	android/os/SystemClock:sleep	(J)V
    //   373: iload_1
    //   374: bipush 20
    //   376: iadd
    //   377: istore_2
    //   378: aload 10
    //   380: ifnull +10 -> 390
    //   383: aload 10
    //   385: invokeinterface 160 1 0
    //   390: iload_2
    //   391: istore_1
    //   392: aload 9
    //   394: ifnull -86 -> 308
    //   397: aload 9
    //   399: invokevirtual 85	android/database/sqlite/SQLiteDatabase:close	()V
    //   402: iload_2
    //   403: istore_1
    //   404: goto -96 -> 308
    //   407: astore 11
    //   409: aload 9
    //   411: invokevirtual 208	android/os/Parcel:recycle	()V
    //   414: aload 11
    //   416: athrow
    //   417: astore 11
    //   419: aload 8
    //   421: astore 9
    //   423: aload 11
    //   425: astore 8
    //   427: aload 10
    //   429: ifnull +10 -> 439
    //   432: aload 10
    //   434: invokeinterface 160 1 0
    //   439: aload 9
    //   441: ifnull +8 -> 449
    //   444: aload 9
    //   446: invokevirtual 85	android/database/sqlite/SQLiteDatabase:close	()V
    //   449: aload 8
    //   451: athrow
    //   452: iload_2
    //   453: iconst_1
    //   454: if_icmpne +107 -> 561
    //   457: invokestatic 195	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   460: astore 11
    //   462: aload 11
    //   464: aload 13
    //   466: iconst_0
    //   467: aload 13
    //   469: arraylength
    //   470: invokevirtual 265	android/os/Parcel:unmarshall	([BII)V
    //   473: aload 11
    //   475: iconst_0
    //   476: invokevirtual 269	android/os/Parcel:setDataPosition	(I)V
    //   479: getstatic 290	com/google/android/gms/internal/zzcji:CREATOR	Landroid/os/Parcelable$Creator;
    //   482: aload 11
    //   484: invokeinterface 279 2 0
    //   489: checkcast 216	com/google/android/gms/internal/zzcji
    //   492: astore 9
    //   494: aload 11
    //   496: invokevirtual 208	android/os/Parcel:recycle	()V
    //   499: lload 6
    //   501: lstore 4
    //   503: aload 9
    //   505: ifnull -364 -> 141
    //   508: aload 12
    //   510: aload 9
    //   512: invokeinterface 285 2 0
    //   517: pop
    //   518: lload 6
    //   520: lstore 4
    //   522: goto -381 -> 141
    //   525: astore 9
    //   527: aload_0
    //   528: invokespecial 110	com/google/android/gms/internal/zzchj:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   531: invokevirtual 116	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   534: ldc_w 292
    //   537: invokevirtual 124	com/google/android/gms/internal/zzcfn:log	(Ljava/lang/String;)V
    //   540: aload 11
    //   542: invokevirtual 208	android/os/Parcel:recycle	()V
    //   545: aconst_null
    //   546: astore 9
    //   548: goto -49 -> 499
    //   551: astore 9
    //   553: aload 11
    //   555: invokevirtual 208	android/os/Parcel:recycle	()V
    //   558: aload 9
    //   560: athrow
    //   561: iload_2
    //   562: iconst_2
    //   563: if_icmpne +107 -> 670
    //   566: invokestatic 195	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   569: astore 11
    //   571: aload 11
    //   573: aload 13
    //   575: iconst_0
    //   576: aload 13
    //   578: arraylength
    //   579: invokevirtual 265	android/os/Parcel:unmarshall	([BII)V
    //   582: aload 11
    //   584: iconst_0
    //   585: invokevirtual 269	android/os/Parcel:setDataPosition	(I)V
    //   588: getstatic 295	com/google/android/gms/internal/zzcek:CREATOR	Landroid/os/Parcelable$Creator;
    //   591: aload 11
    //   593: invokeinterface 279 2 0
    //   598: checkcast 294	com/google/android/gms/internal/zzcek
    //   601: astore 9
    //   603: aload 11
    //   605: invokevirtual 208	android/os/Parcel:recycle	()V
    //   608: lload 6
    //   610: lstore 4
    //   612: aload 9
    //   614: ifnull -473 -> 141
    //   617: aload 12
    //   619: aload 9
    //   621: invokeinterface 285 2 0
    //   626: pop
    //   627: lload 6
    //   629: lstore 4
    //   631: goto -490 -> 141
    //   634: astore 9
    //   636: aload_0
    //   637: invokespecial 110	com/google/android/gms/internal/zzchj:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   640: invokevirtual 116	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   643: ldc_w 292
    //   646: invokevirtual 124	com/google/android/gms/internal/zzcfn:log	(Ljava/lang/String;)V
    //   649: aload 11
    //   651: invokevirtual 208	android/os/Parcel:recycle	()V
    //   654: aconst_null
    //   655: astore 9
    //   657: goto -49 -> 608
    //   660: astore 9
    //   662: aload 11
    //   664: invokevirtual 208	android/os/Parcel:recycle	()V
    //   667: aload 9
    //   669: athrow
    //   670: aload_0
    //   671: invokespecial 110	com/google/android/gms/internal/zzchj:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   674: invokevirtual 116	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   677: ldc_w 297
    //   680: invokevirtual 124	com/google/android/gms/internal/zzcfn:log	(Ljava/lang/String;)V
    //   683: lload 6
    //   685: lstore 4
    //   687: goto -546 -> 141
    //   690: aload 8
    //   692: ldc 126
    //   694: ldc_w 299
    //   697: iconst_1
    //   698: anewarray 130	java/lang/String
    //   701: dup
    //   702: iconst_0
    //   703: lload 4
    //   705: invokestatic 136	java/lang/Long:toString	(J)Ljava/lang/String;
    //   708: aastore
    //   709: invokevirtual 140	android/database/sqlite/SQLiteDatabase:delete	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
    //   712: aload 12
    //   714: invokeinterface 302 1 0
    //   719: if_icmpge +16 -> 735
    //   722: aload_0
    //   723: invokespecial 110	com/google/android/gms/internal/zzchj:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   726: invokevirtual 116	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   729: ldc_w 304
    //   732: invokevirtual 124	com/google/android/gms/internal/zzcfn:log	(Ljava/lang/String;)V
    //   735: aload 8
    //   737: invokevirtual 156	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   740: aload 8
    //   742: invokevirtual 159	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   745: aload 10
    //   747: ifnull +10 -> 757
    //   750: aload 10
    //   752: invokeinterface 160 1 0
    //   757: aload 8
    //   759: ifnull +8 -> 767
    //   762: aload 8
    //   764: invokevirtual 85	android/database/sqlite/SQLiteDatabase:close	()V
    //   767: aload 12
    //   769: areturn
    //   770: aload 9
    //   772: ifnull +16 -> 788
    //   775: aload 9
    //   777: invokevirtual 183	android/database/sqlite/SQLiteDatabase:inTransaction	()Z
    //   780: ifeq +8 -> 788
    //   783: aload 9
    //   785: invokevirtual 159	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   788: aload_0
    //   789: invokespecial 110	com/google/android/gms/internal/zzchj:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   792: invokevirtual 116	com/google/android/gms/internal/zzcfl:zzyx	()Lcom/google/android/gms/internal/zzcfn;
    //   795: ldc_w 287
    //   798: aload 8
    //   800: invokevirtual 166	com/google/android/gms/internal/zzcfn:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   803: aload_0
    //   804: iconst_1
    //   805: putfield 35	com/google/android/gms/internal/zzcfh:zzbqG	Z
    //   808: iload_1
    //   809: istore_2
    //   810: goto -432 -> 378
    //   813: aload_0
    //   814: invokespecial 110	com/google/android/gms/internal/zzchj:zzwF	()Lcom/google/android/gms/internal/zzcfl;
    //   817: invokevirtual 186	com/google/android/gms/internal/zzcfl:zzyz	()Lcom/google/android/gms/internal/zzcfn;
    //   820: ldc_w 306
    //   823: invokevirtual 124	com/google/android/gms/internal/zzcfn:log	(Ljava/lang/String;)V
    //   826: aconst_null
    //   827: areturn
    //   828: astore 8
    //   830: aconst_null
    //   831: astore 11
    //   833: aload 10
    //   835: astore 9
    //   837: aload 11
    //   839: astore 10
    //   841: goto -414 -> 427
    //   844: astore 11
    //   846: aconst_null
    //   847: astore 10
    //   849: aload 8
    //   851: astore 9
    //   853: aload 11
    //   855: astore 8
    //   857: goto -430 -> 427
    //   860: astore 11
    //   862: aload 8
    //   864: astore 10
    //   866: aload 11
    //   868: astore 8
    //   870: goto -443 -> 427
    //   873: astore 8
    //   875: goto -448 -> 427
    //   878: astore 8
    //   880: aconst_null
    //   881: astore 10
    //   883: goto -531 -> 352
    //   886: astore 11
    //   888: aconst_null
    //   889: astore 10
    //   891: aload 8
    //   893: astore 9
    //   895: aload 11
    //   897: astore 8
    //   899: goto -547 -> 352
    //   902: astore 10
    //   904: aconst_null
    //   905: astore 9
    //   907: aconst_null
    //   908: astore 8
    //   910: goto -644 -> 266
    //   913: astore 10
    //   915: aload 8
    //   917: astore 9
    //   919: aconst_null
    //   920: astore 8
    //   922: goto -656 -> 266
    //   925: goto -617 -> 308
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	928	0	this	zzcfh
    //   0	928	1	paramInt	int
    //   169	641	2	i	int
    //   48	264	3	j	int
    //   139	565	4	l1	long
    //   159	525	6	l2	long
    //   64	735	8	localObject1	Object
    //   828	22	8	localObject2	Object
    //   855	14	8	localObject3	Object
    //   873	1	8	localObject4	Object
    //   878	14	8	localSQLiteException1	SQLiteException
    //   897	24	8	localObject5	Object
    //   58	453	9	localObject6	Object
    //   525	1	9	localzzc1	com.google.android.gms.common.internal.safeparcel.zzc
    //   546	1	9	localObject7	Object
    //   551	8	9	localObject8	Object
    //   601	19	9	localzzcek	zzcek
    //   634	1	9	localzzc2	com.google.android.gms.common.internal.safeparcel.zzc
    //   655	1	9	localObject9	Object
    //   660	124	9	localObject10	Object
    //   835	83	9	localObject11	Object
    //   55	835	10	localObject12	Object
    //   902	1	10	localSQLiteFullException1	SQLiteFullException
    //   913	1	10	localSQLiteFullException2	SQLiteFullException
    //   219	19	11	localzzcez	zzcez
    //   252	11	11	localSQLiteFullException3	SQLiteFullException
    //   315	1	11	localzzc3	com.google.android.gms.common.internal.safeparcel.zzc
    //   342	7	11	localSQLiteException2	SQLiteException
    //   407	8	11	localObject13	Object
    //   417	7	11	localObject14	Object
    //   460	378	11	localParcel	Parcel
    //   844	10	11	localObject15	Object
    //   860	7	11	localObject16	Object
    //   886	10	11	localSQLiteException3	SQLiteException
    //   24	744	12	localArrayList	java.util.ArrayList
    //   178	399	13	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   141	180	252	android/database/sqlite/SQLiteFullException
    //   184	189	252	android/database/sqlite/SQLiteFullException
    //   221	226	252	android/database/sqlite/SQLiteFullException
    //   235	245	252	android/database/sqlite/SQLiteFullException
    //   330	335	252	android/database/sqlite/SQLiteFullException
    //   409	417	252	android/database/sqlite/SQLiteFullException
    //   457	462	252	android/database/sqlite/SQLiteFullException
    //   494	499	252	android/database/sqlite/SQLiteFullException
    //   508	518	252	android/database/sqlite/SQLiteFullException
    //   540	545	252	android/database/sqlite/SQLiteFullException
    //   553	561	252	android/database/sqlite/SQLiteFullException
    //   566	571	252	android/database/sqlite/SQLiteFullException
    //   603	608	252	android/database/sqlite/SQLiteFullException
    //   617	627	252	android/database/sqlite/SQLiteFullException
    //   649	654	252	android/database/sqlite/SQLiteFullException
    //   662	670	252	android/database/sqlite/SQLiteFullException
    //   670	683	252	android/database/sqlite/SQLiteFullException
    //   690	735	252	android/database/sqlite/SQLiteFullException
    //   735	745	252	android/database/sqlite/SQLiteFullException
    //   189	221	315	com/google/android/gms/common/internal/safeparcel/zzc
    //   141	180	342	android/database/sqlite/SQLiteException
    //   184	189	342	android/database/sqlite/SQLiteException
    //   221	226	342	android/database/sqlite/SQLiteException
    //   235	245	342	android/database/sqlite/SQLiteException
    //   330	335	342	android/database/sqlite/SQLiteException
    //   409	417	342	android/database/sqlite/SQLiteException
    //   457	462	342	android/database/sqlite/SQLiteException
    //   494	499	342	android/database/sqlite/SQLiteException
    //   508	518	342	android/database/sqlite/SQLiteException
    //   540	545	342	android/database/sqlite/SQLiteException
    //   553	561	342	android/database/sqlite/SQLiteException
    //   566	571	342	android/database/sqlite/SQLiteException
    //   603	608	342	android/database/sqlite/SQLiteException
    //   617	627	342	android/database/sqlite/SQLiteException
    //   649	654	342	android/database/sqlite/SQLiteException
    //   662	670	342	android/database/sqlite/SQLiteException
    //   670	683	342	android/database/sqlite/SQLiteException
    //   690	735	342	android/database/sqlite/SQLiteException
    //   735	745	342	android/database/sqlite/SQLiteException
    //   189	221	407	finally
    //   317	330	407	finally
    //   141	180	417	finally
    //   184	189	417	finally
    //   221	226	417	finally
    //   235	245	417	finally
    //   330	335	417	finally
    //   409	417	417	finally
    //   457	462	417	finally
    //   494	499	417	finally
    //   508	518	417	finally
    //   540	545	417	finally
    //   553	561	417	finally
    //   566	571	417	finally
    //   603	608	417	finally
    //   617	627	417	finally
    //   649	654	417	finally
    //   662	670	417	finally
    //   670	683	417	finally
    //   690	735	417	finally
    //   735	745	417	finally
    //   462	494	525	com/google/android/gms/common/internal/safeparcel/zzc
    //   462	494	551	finally
    //   527	540	551	finally
    //   571	603	634	com/google/android/gms/common/internal/safeparcel/zzc
    //   571	603	660	finally
    //   636	649	660	finally
    //   60	66	828	finally
    //   71	76	844	finally
    //   88	136	844	finally
    //   266	286	860	finally
    //   352	373	873	finally
    //   775	788	873	finally
    //   788	808	873	finally
    //   60	66	878	android/database/sqlite/SQLiteException
    //   71	76	886	android/database/sqlite/SQLiteException
    //   88	136	886	android/database/sqlite/SQLiteException
    //   60	66	902	android/database/sqlite/SQLiteFullException
    //   71	76	913	android/database/sqlite/SQLiteFullException
    //   88	136	913	android/database/sqlite/SQLiteFullException
  }
  
  public final boolean zzc(zzcek paramzzcek)
  {
    super.zzwB();
    paramzzcek = zzcjl.zza(paramzzcek);
    if (paramzzcek.length > 131072)
    {
      super.zzwF().zzyz().log("Conditional user property too long for local database. Sending directly to service");
      return false;
    }
    return zza(2, paramzzcek);
  }
  
  protected final void zzjD() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcfh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */