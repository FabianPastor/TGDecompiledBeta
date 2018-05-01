package com.google.android.gms.measurement.internal;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.SystemClock;
import android.support.annotation.WorkerThread;
import java.io.File;

public class zzo
  extends zzaa
{
  private final zza asx = new zza(getContext(), zzade());
  private boolean asy;
  
  zzo(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  @TargetApi(11)
  @WorkerThread
  private boolean zza(int paramInt, byte[] paramArrayOfByte)
  {
    zzaby();
    zzzx();
    if (this.asy) {
      return false;
    }
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("type", Integer.valueOf(paramInt));
    localContentValues.put("entry", paramArrayOfByte);
    zzbwd().zzbvb();
    int i = 0;
    paramInt = 5;
    while (i < 5)
    {
      Object localObject3 = null;
      paramArrayOfByte = null;
      Object localObject1 = null;
      try
      {
        SQLiteDatabase localSQLiteDatabase = getWritableDatabase();
        if (localSQLiteDatabase == null)
        {
          localObject1 = localSQLiteDatabase;
          localObject3 = localSQLiteDatabase;
          paramArrayOfByte = localSQLiteDatabase;
          this.asy = true;
          return false;
        }
        localObject1 = localSQLiteDatabase;
        localObject3 = localSQLiteDatabase;
        paramArrayOfByte = localSQLiteDatabase;
        localSQLiteDatabase.beginTransaction();
        long l2 = 0L;
        localObject1 = localSQLiteDatabase;
        localObject3 = localSQLiteDatabase;
        paramArrayOfByte = localSQLiteDatabase;
        Cursor localCursor = localSQLiteDatabase.rawQuery("select count(1) from messages", null);
        long l1 = l2;
        if (localCursor != null)
        {
          l1 = l2;
          localObject1 = localSQLiteDatabase;
          localObject3 = localSQLiteDatabase;
          paramArrayOfByte = localSQLiteDatabase;
          if (localCursor.moveToFirst())
          {
            localObject1 = localSQLiteDatabase;
            localObject3 = localSQLiteDatabase;
            paramArrayOfByte = localSQLiteDatabase;
            l1 = localCursor.getLong(0);
          }
        }
        if (l1 >= 100000L)
        {
          localObject1 = localSQLiteDatabase;
          localObject3 = localSQLiteDatabase;
          paramArrayOfByte = localSQLiteDatabase;
          zzbwb().zzbwy().log("Data loss, local db full");
          l1 = 100000L - l1 + 1L;
          localObject1 = localSQLiteDatabase;
          localObject3 = localSQLiteDatabase;
          paramArrayOfByte = localSQLiteDatabase;
          l2 = localSQLiteDatabase.delete("messages", "rowid in (select rowid from messages order by rowid asc limit ?)", new String[] { Long.toString(l1) });
          if (l2 != l1)
          {
            localObject1 = localSQLiteDatabase;
            localObject3 = localSQLiteDatabase;
            paramArrayOfByte = localSQLiteDatabase;
            zzbwb().zzbwy().zzd("Different delete count than expected in local db. expected, received, difference", Long.valueOf(l1), Long.valueOf(l2), Long.valueOf(l1 - l2));
          }
        }
        localObject1 = localSQLiteDatabase;
        localObject3 = localSQLiteDatabase;
        paramArrayOfByte = localSQLiteDatabase;
        localSQLiteDatabase.insertOrThrow("messages", null, localContentValues);
        localObject1 = localSQLiteDatabase;
        localObject3 = localSQLiteDatabase;
        paramArrayOfByte = localSQLiteDatabase;
        localSQLiteDatabase.setTransactionSuccessful();
        localObject1 = localSQLiteDatabase;
        localObject3 = localSQLiteDatabase;
        paramArrayOfByte = localSQLiteDatabase;
        localSQLiteDatabase.endTransaction();
        return true;
      }
      catch (SQLiteFullException localSQLiteFullException)
      {
        paramArrayOfByte = (byte[])localObject1;
        zzbwb().zzbwy().zzj("Error writing entry to local database", localSQLiteFullException);
        paramArrayOfByte = (byte[])localObject1;
        this.asy = true;
        j = paramInt;
        if (localObject1 != null)
        {
          ((SQLiteDatabase)localObject1).close();
          j = paramInt;
        }
        i += 1;
        paramInt = j;
      }
      catch (SQLiteException localSQLiteException)
      {
        int j;
        paramArrayOfByte = localSQLiteFullException;
        if (Build.VERSION.SDK_INT >= 11)
        {
          paramArrayOfByte = localSQLiteFullException;
          if ((localSQLiteException instanceof SQLiteDatabaseLockedException))
          {
            paramArrayOfByte = localSQLiteFullException;
            SystemClock.sleep(paramInt);
            paramInt += 20;
          }
        }
        for (;;)
        {
          j = paramInt;
          if (localSQLiteFullException == null) {
            break;
          }
          localSQLiteFullException.close();
          j = paramInt;
          break;
          if (localSQLiteFullException != null)
          {
            paramArrayOfByte = localSQLiteFullException;
            if (localSQLiteFullException.inTransaction())
            {
              paramArrayOfByte = localSQLiteFullException;
              localSQLiteFullException.endTransaction();
            }
          }
          paramArrayOfByte = localSQLiteFullException;
          zzbwb().zzbwy().zzj("Error writing entry to local database", localSQLiteException);
          paramArrayOfByte = localSQLiteFullException;
          this.asy = true;
        }
      }
      finally
      {
        if (paramArrayOfByte != null) {
          paramArrayOfByte.close();
        }
      }
    }
    zzbwb().zzbxa().log("Failed to write entry to local database");
    return false;
  }
  
  @WorkerThread
  SQLiteDatabase getWritableDatabase()
  {
    if (this.asy) {
      return null;
    }
    SQLiteDatabase localSQLiteDatabase = this.asx.getWritableDatabase();
    if (localSQLiteDatabase == null)
    {
      this.asy = true;
      return null;
    }
    return localSQLiteDatabase;
  }
  
  public boolean zza(EventParcel paramEventParcel)
  {
    Parcel localParcel = Parcel.obtain();
    paramEventParcel.writeToParcel(localParcel, 0);
    paramEventParcel = localParcel.marshall();
    localParcel.recycle();
    if (paramEventParcel.length > 131072)
    {
      zzbwb().zzbxa().log("Event is too long for local database. Sending event directly to service");
      return false;
    }
    return zza(0, paramEventParcel);
  }
  
  public boolean zza(UserAttributeParcel paramUserAttributeParcel)
  {
    Parcel localParcel = Parcel.obtain();
    paramUserAttributeParcel.writeToParcel(localParcel, 0);
    paramUserAttributeParcel = localParcel.marshall();
    localParcel.recycle();
    if (paramUserAttributeParcel.length > 131072)
    {
      zzbwb().zzbxa().log("User property too long for local database. Sending directly to service");
      return false;
    }
    return zza(1, paramUserAttributeParcel);
  }
  
  String zzade()
  {
    return zzbwd().zzbus();
  }
  
  boolean zzbwn()
  {
    return getContext().getDatabasePath(zzade()).exists();
  }
  
  /* Error */
  @TargetApi(11)
  public java.util.List<com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable> zzxe(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 45	com/google/android/gms/measurement/internal/zzo:zzzx	()V
    //   4: aload_0
    //   5: invokevirtual 42	com/google/android/gms/measurement/internal/zzo:zzaby	()V
    //   8: aload_0
    //   9: getfield 47	com/google/android/gms/measurement/internal/zzo:asy	Z
    //   12: ifeq +5 -> 17
    //   15: aconst_null
    //   16: areturn
    //   17: new 306	java/util/ArrayList
    //   20: dup
    //   21: invokespecial 307	java/util/ArrayList:<init>	()V
    //   24: astore 12
    //   26: aload_0
    //   27: invokevirtual 309	com/google/android/gms/measurement/internal/zzo:zzbwn	()Z
    //   30: ifne +6 -> 36
    //   33: aload 12
    //   35: areturn
    //   36: iconst_5
    //   37: istore_2
    //   38: iconst_0
    //   39: istore 4
    //   41: iload 4
    //   43: iconst_5
    //   44: if_icmpge +610 -> 654
    //   47: aconst_null
    //   48: astore 11
    //   50: aconst_null
    //   51: astore 7
    //   53: aconst_null
    //   54: astore 10
    //   56: aload_0
    //   57: invokevirtual 82	com/google/android/gms/measurement/internal/zzo:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   60: astore 8
    //   62: aload 8
    //   64: ifnonnull +20 -> 84
    //   67: aload_0
    //   68: iconst_1
    //   69: putfield 47	com/google/android/gms/measurement/internal/zzo:asy	Z
    //   72: aload 8
    //   74: ifnull +8 -> 82
    //   77: aload 8
    //   79: invokevirtual 87	android/database/sqlite/SQLiteDatabase:close	()V
    //   82: aconst_null
    //   83: areturn
    //   84: aload 8
    //   86: invokevirtual 90	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   89: iload_1
    //   90: invokestatic 312	java/lang/Integer:toString	(I)Ljava/lang/String;
    //   93: astore 7
    //   95: aload 8
    //   97: ldc -128
    //   99: iconst_3
    //   100: anewarray 132	java/lang/String
    //   103: dup
    //   104: iconst_0
    //   105: ldc_w 314
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
    //   123: ldc_w 316
    //   126: aload 7
    //   128: invokevirtual 320	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   131: astore 9
    //   133: ldc2_w 321
    //   136: lstore 5
    //   138: aload 9
    //   140: invokeinterface 325 1 0
    //   145: ifeq +375 -> 520
    //   148: aload 9
    //   150: iconst_0
    //   151: invokeinterface 106 2 0
    //   156: lstore 5
    //   158: aload 9
    //   160: iconst_1
    //   161: invokeinterface 329 2 0
    //   166: istore_3
    //   167: aload 9
    //   169: iconst_2
    //   170: invokeinterface 333 2 0
    //   175: astore 11
    //   177: iload_3
    //   178: ifne +145 -> 323
    //   181: invokestatic 199	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   184: astore 7
    //   186: aload 7
    //   188: aload 11
    //   190: iconst_0
    //   191: aload 11
    //   193: arraylength
    //   194: invokevirtual 337	android/os/Parcel:unmarshall	([BII)V
    //   197: aload 7
    //   199: iconst_0
    //   200: invokevirtual 341	android/os/Parcel:setDataPosition	(I)V
    //   203: getstatic 345	com/google/android/gms/measurement/internal/EventParcel:CREATOR	Landroid/os/Parcelable$Creator;
    //   206: aload 7
    //   208: invokeinterface 351 2 0
    //   213: checkcast 201	com/google/android/gms/measurement/internal/EventParcel
    //   216: astore 10
    //   218: aload 7
    //   220: invokevirtual 212	android/os/Parcel:recycle	()V
    //   223: aload 10
    //   225: ifnull +13 -> 238
    //   228: aload 12
    //   230: aload 10
    //   232: invokeinterface 357 2 0
    //   237: pop
    //   238: goto -100 -> 138
    //   241: astore 10
    //   243: aload_0
    //   244: invokevirtual 112	com/google/android/gms/measurement/internal/zzo:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   247: invokevirtual 118	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   250: ldc_w 359
    //   253: invokevirtual 126	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   256: aload 7
    //   258: invokevirtual 212	android/os/Parcel:recycle	()V
    //   261: goto -123 -> 138
    //   264: astore 9
    //   266: aload 7
    //   268: invokevirtual 212	android/os/Parcel:recycle	()V
    //   271: aload 9
    //   273: athrow
    //   274: astore 9
    //   276: aload 8
    //   278: astore 7
    //   280: aload_0
    //   281: invokevirtual 112	com/google/android/gms/measurement/internal/zzo:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   284: invokevirtual 118	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   287: ldc_w 361
    //   290: aload 9
    //   292: invokevirtual 167	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   295: aload 8
    //   297: astore 7
    //   299: aload_0
    //   300: iconst_1
    //   301: putfield 47	com/google/android/gms/measurement/internal/zzo:asy	Z
    //   304: aload 8
    //   306: ifnull +390 -> 696
    //   309: aload 8
    //   311: invokevirtual 87	android/database/sqlite/SQLiteDatabase:close	()V
    //   314: iload 4
    //   316: iconst_1
    //   317: iadd
    //   318: istore 4
    //   320: goto -279 -> 41
    //   323: iload_3
    //   324: iconst_1
    //   325: if_icmpne +179 -> 504
    //   328: invokestatic 199	android/os/Parcel:obtain	()Landroid/os/Parcel;
    //   331: astore 10
    //   333: aload 10
    //   335: aload 11
    //   337: iconst_0
    //   338: aload 11
    //   340: arraylength
    //   341: invokevirtual 337	android/os/Parcel:unmarshall	([BII)V
    //   344: aload 10
    //   346: iconst_0
    //   347: invokevirtual 341	android/os/Parcel:setDataPosition	(I)V
    //   350: getstatic 362	com/google/android/gms/measurement/internal/UserAttributeParcel:CREATOR	Landroid/os/Parcelable$Creator;
    //   353: aload 10
    //   355: invokeinterface 351 2 0
    //   360: checkcast 220	com/google/android/gms/measurement/internal/UserAttributeParcel
    //   363: astore 7
    //   365: aload 10
    //   367: invokevirtual 212	android/os/Parcel:recycle	()V
    //   370: aload 7
    //   372: ifnull -134 -> 238
    //   375: aload 12
    //   377: aload 7
    //   379: invokeinterface 357 2 0
    //   384: pop
    //   385: goto -147 -> 238
    //   388: astore 9
    //   390: aload 8
    //   392: astore 7
    //   394: getstatic 173	android/os/Build$VERSION:SDK_INT	I
    //   397: bipush 11
    //   399: if_icmplt +196 -> 595
    //   402: aload 8
    //   404: astore 7
    //   406: aload 9
    //   408: instanceof 175
    //   411: ifeq +184 -> 595
    //   414: aload 8
    //   416: astore 7
    //   418: iload_2
    //   419: i2l
    //   420: invokestatic 181	android/os/SystemClock:sleep	(J)V
    //   423: iload_2
    //   424: bipush 20
    //   426: iadd
    //   427: istore_3
    //   428: iload_3
    //   429: istore_2
    //   430: aload 8
    //   432: ifnull -118 -> 314
    //   435: aload 8
    //   437: invokevirtual 87	android/database/sqlite/SQLiteDatabase:close	()V
    //   440: iload_3
    //   441: istore_2
    //   442: goto -128 -> 314
    //   445: astore 7
    //   447: aload_0
    //   448: invokevirtual 112	com/google/android/gms/measurement/internal/zzo:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   451: invokevirtual 118	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   454: ldc_w 364
    //   457: invokevirtual 126	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   460: aload 10
    //   462: invokevirtual 212	android/os/Parcel:recycle	()V
    //   465: aconst_null
    //   466: astore 7
    //   468: goto -98 -> 370
    //   471: astore 7
    //   473: aload 10
    //   475: invokevirtual 212	android/os/Parcel:recycle	()V
    //   478: aload 7
    //   480: athrow
    //   481: astore 7
    //   483: aload 8
    //   485: astore 9
    //   487: aload 7
    //   489: astore 8
    //   491: aload 9
    //   493: ifnull +8 -> 501
    //   496: aload 9
    //   498: invokevirtual 87	android/database/sqlite/SQLiteDatabase:close	()V
    //   501: aload 8
    //   503: athrow
    //   504: aload_0
    //   505: invokevirtual 112	com/google/android/gms/measurement/internal/zzo:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   508: invokevirtual 118	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   511: ldc_w 366
    //   514: invokevirtual 126	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   517: goto -279 -> 238
    //   520: aload 9
    //   522: invokeinterface 367 1 0
    //   527: aload 8
    //   529: ldc -128
    //   531: ldc_w 369
    //   534: iconst_1
    //   535: anewarray 132	java/lang/String
    //   538: dup
    //   539: iconst_0
    //   540: lload 5
    //   542: invokestatic 138	java/lang/Long:toString	(J)Ljava/lang/String;
    //   545: aastore
    //   546: invokevirtual 142	android/database/sqlite/SQLiteDatabase:delete	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
    //   549: aload 12
    //   551: invokeinterface 372 1 0
    //   556: if_icmpge +16 -> 572
    //   559: aload_0
    //   560: invokevirtual 112	com/google/android/gms/measurement/internal/zzo:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   563: invokevirtual 118	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   566: ldc_w 374
    //   569: invokevirtual 126	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   572: aload 8
    //   574: invokevirtual 158	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   577: aload 8
    //   579: invokevirtual 161	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   582: aload 8
    //   584: ifnull +8 -> 592
    //   587: aload 8
    //   589: invokevirtual 87	android/database/sqlite/SQLiteDatabase:close	()V
    //   592: aload 12
    //   594: areturn
    //   595: aload 8
    //   597: ifnull +24 -> 621
    //   600: aload 8
    //   602: astore 7
    //   604: aload 8
    //   606: invokevirtual 184	android/database/sqlite/SQLiteDatabase:inTransaction	()Z
    //   609: ifeq +12 -> 621
    //   612: aload 8
    //   614: astore 7
    //   616: aload 8
    //   618: invokevirtual 161	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   621: aload 8
    //   623: astore 7
    //   625: aload_0
    //   626: invokevirtual 112	com/google/android/gms/measurement/internal/zzo:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   629: invokevirtual 118	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   632: ldc_w 361
    //   635: aload 9
    //   637: invokevirtual 167	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   640: aload 8
    //   642: astore 7
    //   644: aload_0
    //   645: iconst_1
    //   646: putfield 47	com/google/android/gms/measurement/internal/zzo:asy	Z
    //   649: iload_2
    //   650: istore_3
    //   651: goto -223 -> 428
    //   654: aload_0
    //   655: invokevirtual 112	com/google/android/gms/measurement/internal/zzo:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   658: invokevirtual 187	com/google/android/gms/measurement/internal/zzq:zzbxa	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   661: ldc_w 376
    //   664: invokevirtual 126	com/google/android/gms/measurement/internal/zzq$zza:log	(Ljava/lang/String;)V
    //   667: aconst_null
    //   668: areturn
    //   669: astore 8
    //   671: aload 7
    //   673: astore 9
    //   675: goto -184 -> 491
    //   678: astore 9
    //   680: aload 11
    //   682: astore 8
    //   684: goto -294 -> 390
    //   687: astore 9
    //   689: aload 10
    //   691: astore 8
    //   693: goto -417 -> 276
    //   696: goto -382 -> 314
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	699	0	this	zzo
    //   0	699	1	paramInt	int
    //   37	613	2	i	int
    //   166	485	3	j	int
    //   39	280	4	k	int
    //   136	405	5	l	long
    //   51	366	7	localObject1	Object
    //   445	1	7	localzza1	com.google.android.gms.common.internal.safeparcel.zza.zza
    //   466	1	7	localObject2	Object
    //   471	8	7	localObject3	Object
    //   481	7	7	localObject4	Object
    //   602	70	7	localObject5	Object
    //   60	581	8	localObject6	Object
    //   669	1	8	localObject7	Object
    //   682	10	8	localObject8	Object
    //   131	37	9	localCursor	Cursor
    //   264	8	9	localObject9	Object
    //   274	17	9	localSQLiteFullException1	SQLiteFullException
    //   388	19	9	localSQLiteException1	SQLiteException
    //   485	189	9	localObject10	Object
    //   678	1	9	localSQLiteException2	SQLiteException
    //   687	1	9	localSQLiteFullException2	SQLiteFullException
    //   54	177	10	localEventParcel	EventParcel
    //   241	1	10	localzza2	com.google.android.gms.common.internal.safeparcel.zza.zza
    //   331	359	10	localParcel	Parcel
    //   48	633	11	arrayOfByte	byte[]
    //   24	569	12	localArrayList	java.util.ArrayList
    // Exception table:
    //   from	to	target	type
    //   186	218	241	com/google/android/gms/common/internal/safeparcel/zza$zza
    //   186	218	264	finally
    //   243	256	264	finally
    //   67	72	274	android/database/sqlite/SQLiteFullException
    //   84	133	274	android/database/sqlite/SQLiteFullException
    //   138	177	274	android/database/sqlite/SQLiteFullException
    //   181	186	274	android/database/sqlite/SQLiteFullException
    //   218	223	274	android/database/sqlite/SQLiteFullException
    //   228	238	274	android/database/sqlite/SQLiteFullException
    //   256	261	274	android/database/sqlite/SQLiteFullException
    //   266	274	274	android/database/sqlite/SQLiteFullException
    //   328	333	274	android/database/sqlite/SQLiteFullException
    //   365	370	274	android/database/sqlite/SQLiteFullException
    //   375	385	274	android/database/sqlite/SQLiteFullException
    //   460	465	274	android/database/sqlite/SQLiteFullException
    //   473	481	274	android/database/sqlite/SQLiteFullException
    //   504	517	274	android/database/sqlite/SQLiteFullException
    //   520	572	274	android/database/sqlite/SQLiteFullException
    //   572	582	274	android/database/sqlite/SQLiteFullException
    //   67	72	388	android/database/sqlite/SQLiteException
    //   84	133	388	android/database/sqlite/SQLiteException
    //   138	177	388	android/database/sqlite/SQLiteException
    //   181	186	388	android/database/sqlite/SQLiteException
    //   218	223	388	android/database/sqlite/SQLiteException
    //   228	238	388	android/database/sqlite/SQLiteException
    //   256	261	388	android/database/sqlite/SQLiteException
    //   266	274	388	android/database/sqlite/SQLiteException
    //   328	333	388	android/database/sqlite/SQLiteException
    //   365	370	388	android/database/sqlite/SQLiteException
    //   375	385	388	android/database/sqlite/SQLiteException
    //   460	465	388	android/database/sqlite/SQLiteException
    //   473	481	388	android/database/sqlite/SQLiteException
    //   504	517	388	android/database/sqlite/SQLiteException
    //   520	572	388	android/database/sqlite/SQLiteException
    //   572	582	388	android/database/sqlite/SQLiteException
    //   333	365	445	com/google/android/gms/common/internal/safeparcel/zza$zza
    //   333	365	471	finally
    //   447	460	471	finally
    //   67	72	481	finally
    //   84	133	481	finally
    //   138	177	481	finally
    //   181	186	481	finally
    //   218	223	481	finally
    //   228	238	481	finally
    //   256	261	481	finally
    //   266	274	481	finally
    //   328	333	481	finally
    //   365	370	481	finally
    //   375	385	481	finally
    //   460	465	481	finally
    //   473	481	481	finally
    //   504	517	481	finally
    //   520	572	481	finally
    //   572	582	481	finally
    //   56	62	669	finally
    //   280	295	669	finally
    //   299	304	669	finally
    //   394	402	669	finally
    //   406	414	669	finally
    //   418	423	669	finally
    //   604	612	669	finally
    //   616	621	669	finally
    //   625	640	669	finally
    //   644	649	669	finally
    //   56	62	678	android/database/sqlite/SQLiteException
    //   56	62	687	android/database/sqlite/SQLiteFullException
  }
  
  protected void zzzy() {}
  
  @TargetApi(11)
  private class zza
    extends SQLiteOpenHelper
  {
    zza(Context paramContext, String paramString)
    {
      super(paramString, null, 1);
    }
    
    @WorkerThread
    public SQLiteDatabase getWritableDatabase()
    {
      try
      {
        SQLiteDatabase localSQLiteDatabase = super.getWritableDatabase();
        return localSQLiteDatabase;
      }
      catch (SQLiteException localSQLiteException1)
      {
        if ((Build.VERSION.SDK_INT >= 11) && ((localSQLiteException1 instanceof SQLiteDatabaseLockedException))) {
          throw localSQLiteException1;
        }
        zzo.this.zzbwb().zzbwy().log("Opening the local database failed, dropping and recreating it");
        Object localObject = zzo.this.zzade();
        if (!zzo.this.getContext().getDatabasePath((String)localObject).delete()) {
          zzo.this.zzbwb().zzbwy().zzj("Failed to delete corrupted local db file", localObject);
        }
        try
        {
          localObject = super.getWritableDatabase();
          return (SQLiteDatabase)localObject;
        }
        catch (SQLiteException localSQLiteException2)
        {
          zzo.this.zzbwb().zzbwy().zzj("Failed to open local database. Events will bypass local storage", localSQLiteException2);
        }
      }
      return null;
    }
    
    @WorkerThread
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      zze.zza(zzo.this.zzbwb(), paramSQLiteDatabase);
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
        zze.zza(zzo.this.zzbwb(), paramSQLiteDatabase, "messages", "create table if not exists messages ( type INTEGER NOT NULL, entry BLOB NOT NULL)", "type,entry", null);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */