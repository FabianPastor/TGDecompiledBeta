package com.google.android.gms.internal.config;

import android.content.Context;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class zzan
  implements Runnable
{
  private final Context mContext;
  private final zzar zzaj;
  private final zzao zzat;
  private final zzao zzau;
  private final zzao zzav;
  
  public zzan(Context paramContext, zzao paramzzao1, zzao paramzzao2, zzao paramzzao3, zzar paramzzar)
  {
    this.mContext = paramContext;
    this.zzat = paramzzao1;
    this.zzau = paramzzao2;
    this.zzav = paramzzao3;
    this.zzaj = paramzzar;
  }
  
  private static zzas zza(zzao paramzzao)
  {
    zzas localzzas = new zzas();
    Object localObject1;
    if (paramzzao.zzp() != null)
    {
      Map localMap1 = paramzzao.zzp();
      localObject1 = new ArrayList();
      if (localMap1 != null)
      {
        Iterator localIterator = localMap1.keySet().iterator();
        while (localIterator.hasNext())
        {
          String str1 = (String)localIterator.next();
          ArrayList localArrayList = new ArrayList();
          Map localMap2 = (Map)localMap1.get(str1);
          if (localMap2 != null)
          {
            localObject2 = localMap2.keySet().iterator();
            while (((Iterator)localObject2).hasNext())
            {
              String str2 = (String)((Iterator)localObject2).next();
              zzat localzzat = new zzat();
              localzzat.zzbj = str2;
              localzzat.zzbk = ((byte[])localMap2.get(str2));
              localArrayList.add(localzzat);
            }
          }
          Object localObject2 = new zzav();
          ((zzav)localObject2).namespace = str1;
          ((zzav)localObject2).zzbp = ((zzat[])localArrayList.toArray(new zzat[localArrayList.size()]));
          ((List)localObject1).add(localObject2);
        }
      }
      localzzas.zzbg = ((zzav[])((List)localObject1).toArray(new zzav[((List)localObject1).size()]));
    }
    if (paramzzao.zzg() != null)
    {
      localObject1 = paramzzao.zzg();
      localzzas.zzbh = ((byte[][])((List)localObject1).toArray(new byte[((List)localObject1).size()][]));
    }
    localzzas.timestamp = paramzzao.getTimestamp();
    return localzzas;
  }
  
  /* Error */
  public final void run()
  {
    // Byte code:
    //   0: new 136	com/google/android/gms/internal/config/zzaw
    //   3: dup
    //   4: invokespecial 137	com/google/android/gms/internal/config/zzaw:<init>	()V
    //   7: astore_1
    //   8: aload_0
    //   9: getfield 23	com/google/android/gms/internal/config/zzan:zzat	Lcom/google/android/gms/internal/config/zzao;
    //   12: ifnull +14 -> 26
    //   15: aload_1
    //   16: aload_0
    //   17: getfield 23	com/google/android/gms/internal/config/zzan:zzat	Lcom/google/android/gms/internal/config/zzao;
    //   20: invokestatic 139	com/google/android/gms/internal/config/zzan:zza	(Lcom/google/android/gms/internal/config/zzao;)Lcom/google/android/gms/internal/config/zzas;
    //   23: putfield 143	com/google/android/gms/internal/config/zzaw:zzbq	Lcom/google/android/gms/internal/config/zzas;
    //   26: aload_0
    //   27: getfield 25	com/google/android/gms/internal/config/zzan:zzau	Lcom/google/android/gms/internal/config/zzao;
    //   30: ifnull +14 -> 44
    //   33: aload_1
    //   34: aload_0
    //   35: getfield 25	com/google/android/gms/internal/config/zzan:zzau	Lcom/google/android/gms/internal/config/zzao;
    //   38: invokestatic 139	com/google/android/gms/internal/config/zzan:zza	(Lcom/google/android/gms/internal/config/zzao;)Lcom/google/android/gms/internal/config/zzas;
    //   41: putfield 146	com/google/android/gms/internal/config/zzaw:zzbr	Lcom/google/android/gms/internal/config/zzas;
    //   44: aload_0
    //   45: getfield 27	com/google/android/gms/internal/config/zzan:zzav	Lcom/google/android/gms/internal/config/zzao;
    //   48: ifnull +14 -> 62
    //   51: aload_1
    //   52: aload_0
    //   53: getfield 27	com/google/android/gms/internal/config/zzan:zzav	Lcom/google/android/gms/internal/config/zzao;
    //   56: invokestatic 139	com/google/android/gms/internal/config/zzan:zza	(Lcom/google/android/gms/internal/config/zzao;)Lcom/google/android/gms/internal/config/zzas;
    //   59: putfield 149	com/google/android/gms/internal/config/zzaw:zzbs	Lcom/google/android/gms/internal/config/zzas;
    //   62: aload_0
    //   63: getfield 29	com/google/android/gms/internal/config/zzan:zzaj	Lcom/google/android/gms/internal/config/zzar;
    //   66: ifnull +49 -> 115
    //   69: new 151	com/google/android/gms/internal/config/zzau
    //   72: dup
    //   73: invokespecial 152	com/google/android/gms/internal/config/zzau:<init>	()V
    //   76: astore_2
    //   77: aload_2
    //   78: aload_0
    //   79: getfield 29	com/google/android/gms/internal/config/zzan:zzaj	Lcom/google/android/gms/internal/config/zzar;
    //   82: invokevirtual 157	com/google/android/gms/internal/config/zzar:getLastFetchStatus	()I
    //   85: putfield 161	com/google/android/gms/internal/config/zzau:zzbl	I
    //   88: aload_2
    //   89: aload_0
    //   90: getfield 29	com/google/android/gms/internal/config/zzan:zzaj	Lcom/google/android/gms/internal/config/zzar;
    //   93: invokevirtual 164	com/google/android/gms/internal/config/zzar:isDeveloperModeEnabled	()Z
    //   96: putfield 168	com/google/android/gms/internal/config/zzau:zzbm	Z
    //   99: aload_2
    //   100: aload_0
    //   101: getfield 29	com/google/android/gms/internal/config/zzan:zzaj	Lcom/google/android/gms/internal/config/zzar;
    //   104: invokevirtual 171	com/google/android/gms/internal/config/zzar:zzt	()J
    //   107: putfield 174	com/google/android/gms/internal/config/zzau:zzbn	J
    //   110: aload_1
    //   111: aload_2
    //   112: putfield 178	com/google/android/gms/internal/config/zzaw:zzbt	Lcom/google/android/gms/internal/config/zzau;
    //   115: aload_0
    //   116: getfield 29	com/google/android/gms/internal/config/zzan:zzaj	Lcom/google/android/gms/internal/config/zzar;
    //   119: ifnull +163 -> 282
    //   122: aload_0
    //   123: getfield 29	com/google/android/gms/internal/config/zzan:zzaj	Lcom/google/android/gms/internal/config/zzar;
    //   126: invokevirtual 181	com/google/android/gms/internal/config/zzar:zzr	()Ljava/util/Map;
    //   129: ifnull +153 -> 282
    //   132: new 43	java/util/ArrayList
    //   135: dup
    //   136: invokespecial 44	java/util/ArrayList:<init>	()V
    //   139: astore_3
    //   140: aload_0
    //   141: getfield 29	com/google/android/gms/internal/config/zzan:zzaj	Lcom/google/android/gms/internal/config/zzar;
    //   144: invokevirtual 181	com/google/android/gms/internal/config/zzar:zzr	()Ljava/util/Map;
    //   147: astore 4
    //   149: aload 4
    //   151: invokeinterface 50 1 0
    //   156: invokeinterface 56 1 0
    //   161: astore 5
    //   163: aload 5
    //   165: invokeinterface 62 1 0
    //   170: ifeq +90 -> 260
    //   173: aload 5
    //   175: invokeinterface 66 1 0
    //   180: checkcast 68	java/lang/String
    //   183: astore_2
    //   184: aload 4
    //   186: aload_2
    //   187: invokeinterface 72 2 0
    //   192: ifnull -29 -> 163
    //   195: new 183	com/google/android/gms/internal/config/zzax
    //   198: dup
    //   199: invokespecial 184	com/google/android/gms/internal/config/zzax:<init>	()V
    //   202: astore 6
    //   204: aload 6
    //   206: aload_2
    //   207: putfield 185	com/google/android/gms/internal/config/zzax:namespace	Ljava/lang/String;
    //   210: aload 6
    //   212: aload 4
    //   214: aload_2
    //   215: invokeinterface 72 2 0
    //   220: checkcast 187	com/google/android/gms/internal/config/zzal
    //   223: invokevirtual 190	com/google/android/gms/internal/config/zzal:zzo	()J
    //   226: putfield 193	com/google/android/gms/internal/config/zzax:zzbw	J
    //   229: aload 6
    //   231: aload 4
    //   233: aload_2
    //   234: invokeinterface 72 2 0
    //   239: checkcast 187	com/google/android/gms/internal/config/zzal
    //   242: invokevirtual 196	com/google/android/gms/internal/config/zzal:getResourceId	()I
    //   245: putfield 199	com/google/android/gms/internal/config/zzax:resourceId	I
    //   248: aload_3
    //   249: aload 6
    //   251: invokeinterface 90 2 0
    //   256: pop
    //   257: goto -94 -> 163
    //   260: aload_1
    //   261: aload_3
    //   262: aload_3
    //   263: invokeinterface 100 1 0
    //   268: anewarray 183	com/google/android/gms/internal/config/zzax
    //   271: invokeinterface 104 2 0
    //   276: checkcast 201	[Lcom/google/android/gms/internal/config/zzax;
    //   279: putfield 204	com/google/android/gms/internal/config/zzaw:zzbu	[Lcom/google/android/gms/internal/config/zzax;
    //   282: aload_1
    //   283: invokevirtual 209	com/google/android/gms/internal/config/zzbh:zzai	()I
    //   286: newarray <illegal type>
    //   288: astore_2
    //   289: aload_2
    //   290: arraylength
    //   291: istore 7
    //   293: aload_2
    //   294: iconst_0
    //   295: iload 7
    //   297: invokestatic 215	com/google/android/gms/internal/config/zzaz:zzb	([BII)Lcom/google/android/gms/internal/config/zzaz;
    //   300: astore 4
    //   302: aload_1
    //   303: aload 4
    //   305: invokevirtual 218	com/google/android/gms/internal/config/zzbh:zza	(Lcom/google/android/gms/internal/config/zzaz;)V
    //   308: aload 4
    //   310: invokevirtual 221	com/google/android/gms/internal/config/zzaz:zzad	()V
    //   313: aload_0
    //   314: getfield 21	com/google/android/gms/internal/config/zzan:mContext	Landroid/content/Context;
    //   317: ldc -33
    //   319: iconst_0
    //   320: invokevirtual 229	android/content/Context:openFileOutput	(Ljava/lang/String;I)Ljava/io/FileOutputStream;
    //   323: astore_1
    //   324: aload_1
    //   325: aload_2
    //   326: invokevirtual 235	java/io/FileOutputStream:write	([B)V
    //   329: aload_1
    //   330: invokevirtual 238	java/io/FileOutputStream:close	()V
    //   333: return
    //   334: astore_1
    //   335: new 240	java/lang/RuntimeException
    //   338: dup
    //   339: ldc -14
    //   341: aload_1
    //   342: invokespecial 245	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   345: athrow
    //   346: astore_1
    //   347: ldc -9
    //   349: ldc -7
    //   351: aload_1
    //   352: invokestatic 255	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   355: pop
    //   356: goto -23 -> 333
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	359	0	this	zzan
    //   7	323	1	localObject1	Object
    //   334	8	1	localIOException1	java.io.IOException
    //   346	6	1	localIOException2	java.io.IOException
    //   76	250	2	localObject2	Object
    //   139	124	3	localArrayList	ArrayList
    //   147	162	4	localObject3	Object
    //   161	13	5	localIterator	Iterator
    //   202	48	6	localzzax	zzax
    //   291	5	7	i	int
    // Exception table:
    //   from	to	target	type
    //   293	313	334	java/io/IOException
    //   313	333	346	java/io/IOException
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzan.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */