package com.google.android.gms.internal;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class zzaob
{
  private final ThreadLocal<Map<zzapx<?>, zza<?>>> bkJ = new ThreadLocal();
  private final Map<zzapx<?>, zzaot<?>> bkK = Collections.synchronizedMap(new HashMap());
  private final List<zzaou> bkL;
  private final zzapb bkM;
  private final boolean bkN;
  private final boolean bkO;
  private final boolean bkP;
  private final boolean bkQ;
  final zzaof bkR = new zzaof() {};
  final zzaoo bkS = new zzaoo() {};
  
  public zzaob()
  {
    this(zzapc.blF, zzanz.bkD, Collections.emptyMap(), false, false, false, true, false, false, zzaor.blg, Collections.emptyList());
  }
  
  zzaob(zzapc paramzzapc, zzaoa paramzzaoa, Map<Type, zzaod<?>> paramMap, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6, zzaor paramzzaor, List<zzaou> paramList)
  {
    this.bkM = new zzapb(paramMap);
    this.bkN = paramBoolean1;
    this.bkP = paramBoolean3;
    this.bkO = paramBoolean4;
    this.bkQ = paramBoolean5;
    paramMap = new ArrayList();
    paramMap.add(zzapw.bnI);
    paramMap.add(zzapr.bmp);
    paramMap.add(paramzzapc);
    paramMap.addAll(paramList);
    paramMap.add(zzapw.bnp);
    paramMap.add(zzapw.bne);
    paramMap.add(zzapw.bmY);
    paramMap.add(zzapw.bna);
    paramMap.add(zzapw.bnc);
    paramMap.add(zzapw.zza(Long.TYPE, Long.class, zza(paramzzaor)));
    paramMap.add(zzapw.zza(Double.TYPE, Double.class, zzdd(paramBoolean6)));
    paramMap.add(zzapw.zza(Float.TYPE, Float.class, zzde(paramBoolean6)));
    paramMap.add(zzapw.bnj);
    paramMap.add(zzapw.bnl);
    paramMap.add(zzapw.bnr);
    paramMap.add(zzapw.bnt);
    paramMap.add(zzapw.zza(BigDecimal.class, zzapw.bnn));
    paramMap.add(zzapw.zza(BigInteger.class, zzapw.bno));
    paramMap.add(zzapw.bnv);
    paramMap.add(zzapw.bnx);
    paramMap.add(zzapw.bnB);
    paramMap.add(zzapw.bnG);
    paramMap.add(zzapw.bnz);
    paramMap.add(zzapw.bmV);
    paramMap.add(zzapm.bmp);
    paramMap.add(zzapw.bnE);
    paramMap.add(zzapu.bmp);
    paramMap.add(zzapt.bmp);
    paramMap.add(zzapw.bnC);
    paramMap.add(zzapk.bmp);
    paramMap.add(zzapw.bmT);
    paramMap.add(new zzapl(this.bkM));
    paramMap.add(new zzapq(this.bkM, paramBoolean2));
    paramMap.add(new zzapn(this.bkM));
    paramMap.add(zzapw.bnJ);
    paramMap.add(new zzaps(this.bkM, paramzzaoa, paramzzapc));
    this.bkL = Collections.unmodifiableList(paramMap);
  }
  
  private zzaot<Number> zza(zzaor paramzzaor)
  {
    if (paramzzaor == zzaor.blg) {
      return zzapw.bnf;
    }
    new zzaot()
    {
      public void zza(zzaqa paramAnonymouszzaqa, Number paramAnonymousNumber)
        throws IOException
      {
        if (paramAnonymousNumber == null)
        {
          paramAnonymouszzaqa.bx();
          return;
        }
        paramAnonymouszzaqa.zzut(paramAnonymousNumber.toString());
      }
      
      public Number zzg(zzapy paramAnonymouszzapy)
        throws IOException
      {
        if (paramAnonymouszzapy.bn() == zzapz.bos)
        {
          paramAnonymouszzapy.nextNull();
          return null;
        }
        return Long.valueOf(paramAnonymouszzapy.nextLong());
      }
    };
  }
  
  private static void zza(Object paramObject, zzapy paramzzapy)
  {
    if (paramObject != null) {
      try
      {
        if (paramzzapy.bn() != zzapz.bot) {
          throw new zzaoi("JSON document was not fully consumed.");
        }
      }
      catch (zzaqb paramObject)
      {
        throw new zzaoq((Throwable)paramObject);
      }
      catch (IOException paramObject)
      {
        throw new zzaoi((Throwable)paramObject);
      }
    }
  }
  
  private zzaot<Number> zzdd(boolean paramBoolean)
  {
    if (paramBoolean) {
      return zzapw.bnh;
    }
    new zzaot()
    {
      public void zza(zzaqa paramAnonymouszzaqa, Number paramAnonymousNumber)
        throws IOException
      {
        if (paramAnonymousNumber == null)
        {
          paramAnonymouszzaqa.bx();
          return;
        }
        double d = paramAnonymousNumber.doubleValue();
        zzaob.zza(zzaob.this, d);
        paramAnonymouszzaqa.zza(paramAnonymousNumber);
      }
      
      public Double zze(zzapy paramAnonymouszzapy)
        throws IOException
      {
        if (paramAnonymouszzapy.bn() == zzapz.bos)
        {
          paramAnonymouszzapy.nextNull();
          return null;
        }
        return Double.valueOf(paramAnonymouszzapy.nextDouble());
      }
    };
  }
  
  private zzaot<Number> zzde(boolean paramBoolean)
  {
    if (paramBoolean) {
      return zzapw.bng;
    }
    new zzaot()
    {
      public void zza(zzaqa paramAnonymouszzaqa, Number paramAnonymousNumber)
        throws IOException
      {
        if (paramAnonymousNumber == null)
        {
          paramAnonymouszzaqa.bx();
          return;
        }
        float f = paramAnonymousNumber.floatValue();
        zzaob.zza(zzaob.this, f);
        paramAnonymouszzaqa.zza(paramAnonymousNumber);
      }
      
      public Float zzf(zzapy paramAnonymouszzapy)
        throws IOException
      {
        if (paramAnonymouszzapy.bn() == zzapz.bos)
        {
          paramAnonymouszzapy.nextNull();
          return null;
        }
        return Float.valueOf((float)paramAnonymouszzapy.nextDouble());
      }
    };
  }
  
  private void zzn(double paramDouble)
  {
    if ((Double.isNaN(paramDouble)) || (Double.isInfinite(paramDouble))) {
      throw new IllegalArgumentException(168 + paramDouble + " is not a valid double value as per JSON specification. To override this" + " behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method.");
    }
  }
  
  public String toString()
  {
    return "{serializeNulls:" + this.bkN + "factories:" + this.bkL + ",instanceCreators:" + this.bkM + "}";
  }
  
  public <T> zzaot<T> zza(zzaou paramzzaou, zzapx<T> paramzzapx)
  {
    int i = 0;
    if (!this.bkL.contains(paramzzaou)) {
      i = 1;
    }
    Iterator localIterator = this.bkL.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (zzaou)localIterator.next();
      if (i == 0)
      {
        if (localObject == paramzzaou) {
          i = 1;
        }
      }
      else
      {
        localObject = ((zzaou)localObject).zza(this, paramzzapx);
        if (localObject != null) {
          return (zzaot<T>)localObject;
        }
      }
    }
    paramzzaou = String.valueOf(paramzzapx);
    throw new IllegalArgumentException(String.valueOf(paramzzaou).length() + 22 + "GSON cannot serialize " + paramzzaou);
  }
  
  public <T> zzaot<T> zza(zzapx<T> paramzzapx)
  {
    Object localObject1 = (zzaot)this.bkK.get(paramzzapx);
    if (localObject1 != null) {
      return (zzaot<T>)localObject1;
    }
    Object localObject3 = (Map)this.bkJ.get();
    int i = 0;
    if (localObject3 == null)
    {
      localObject3 = new HashMap();
      this.bkJ.set(localObject3);
      i = 1;
    }
    for (;;)
    {
      Object localObject4 = (zza)((Map)localObject3).get(paramzzapx);
      localObject1 = localObject4;
      if (localObject4 != null) {
        break;
      }
      try
      {
        localObject1 = new zza();
        ((Map)localObject3).put(paramzzapx, localObject1);
        Iterator localIterator = this.bkL.iterator();
        for (;;)
        {
          if (localIterator.hasNext())
          {
            localObject4 = ((zzaou)localIterator.next()).zza(this, paramzzapx);
            if (localObject4 != null)
            {
              ((zza)localObject1).zza((zzaot)localObject4);
              this.bkK.put(paramzzapx, localObject4);
              ((Map)localObject3).remove(paramzzapx);
              localObject1 = localObject4;
              if (i == 0) {
                break;
              }
              this.bkJ.remove();
              return (zzaot<T>)localObject4;
            }
          }
        }
        localObject1 = String.valueOf(paramzzapx);
        throw new IllegalArgumentException(String.valueOf(localObject1).length() + 19 + "GSON cannot handle " + (String)localObject1);
      }
      finally
      {
        ((Map)localObject3).remove(paramzzapx);
        if (i != 0) {
          this.bkJ.remove();
        }
      }
    }
  }
  
  public zzaqa zza(Writer paramWriter)
    throws IOException
  {
    if (this.bkP) {
      paramWriter.write(")]}'\n");
    }
    paramWriter = new zzaqa(paramWriter);
    if (this.bkQ) {
      paramWriter.setIndent("  ");
    }
    paramWriter.zzdi(this.bkN);
    return paramWriter;
  }
  
  public <T> T zza(zzaoh paramzzaoh, Class<T> paramClass)
    throws zzaoq
  {
    paramzzaoh = zza(paramzzaoh, paramClass);
    return (T)zzaph.zzp(paramClass).cast(paramzzaoh);
  }
  
  public <T> T zza(zzaoh paramzzaoh, Type paramType)
    throws zzaoq
  {
    if (paramzzaoh == null) {
      return null;
    }
    return (T)zza(new zzapo(paramzzaoh), paramType);
  }
  
  /* Error */
  public <T> T zza(zzapy paramzzapy, Type paramType)
    throws zzaoi, zzaoq
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: aload_1
    //   3: invokevirtual 484	com/google/android/gms/internal/zzapy:isLenient	()Z
    //   6: istore 4
    //   8: aload_1
    //   9: iconst_1
    //   10: invokevirtual 487	com/google/android/gms/internal/zzapy:setLenient	(Z)V
    //   13: aload_1
    //   14: invokevirtual 289	com/google/android/gms/internal/zzapy:bn	()Lcom/google/android/gms/internal/zzapz;
    //   17: pop
    //   18: iconst_0
    //   19: istore_3
    //   20: aload_0
    //   21: aload_2
    //   22: invokestatic 493	com/google/android/gms/internal/zzapx:zzl	(Ljava/lang/reflect/Type;)Lcom/google/android/gms/internal/zzapx;
    //   25: invokevirtual 495	com/google/android/gms/internal/zzaob:zza	(Lcom/google/android/gms/internal/zzapx;)Lcom/google/android/gms/internal/zzaot;
    //   28: aload_1
    //   29: invokevirtual 499	com/google/android/gms/internal/zzaot:zzb	(Lcom/google/android/gms/internal/zzapy;)Ljava/lang/Object;
    //   32: astore_2
    //   33: aload_1
    //   34: iload 4
    //   36: invokevirtual 487	com/google/android/gms/internal/zzapy:setLenient	(Z)V
    //   39: aload_2
    //   40: areturn
    //   41: astore_2
    //   42: iload_3
    //   43: ifeq +11 -> 54
    //   46: aload_1
    //   47: iload 4
    //   49: invokevirtual 487	com/google/android/gms/internal/zzapy:setLenient	(Z)V
    //   52: aconst_null
    //   53: areturn
    //   54: new 304	com/google/android/gms/internal/zzaoq
    //   57: dup
    //   58: aload_2
    //   59: invokespecial 307	com/google/android/gms/internal/zzaoq:<init>	(Ljava/lang/Throwable;)V
    //   62: athrow
    //   63: astore_2
    //   64: aload_1
    //   65: iload 4
    //   67: invokevirtual 487	com/google/android/gms/internal/zzapy:setLenient	(Z)V
    //   70: aload_2
    //   71: athrow
    //   72: astore_2
    //   73: new 304	com/google/android/gms/internal/zzaoq
    //   76: dup
    //   77: aload_2
    //   78: invokespecial 307	com/google/android/gms/internal/zzaoq:<init>	(Ljava/lang/Throwable;)V
    //   81: athrow
    //   82: astore_2
    //   83: new 304	com/google/android/gms/internal/zzaoq
    //   86: dup
    //   87: aload_2
    //   88: invokespecial 307	com/google/android/gms/internal/zzaoq:<init>	(Ljava/lang/Throwable;)V
    //   91: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	92	0	this	zzaob
    //   0	92	1	paramzzapy	zzapy
    //   0	92	2	paramType	Type
    //   1	42	3	i	int
    //   6	60	4	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   13	18	41	java/io/EOFException
    //   20	33	41	java/io/EOFException
    //   13	18	63	finally
    //   20	33	63	finally
    //   54	63	63	finally
    //   73	82	63	finally
    //   83	92	63	finally
    //   13	18	72	java/lang/IllegalStateException
    //   20	33	72	java/lang/IllegalStateException
    //   13	18	82	java/io/IOException
    //   20	33	82	java/io/IOException
  }
  
  public <T> T zza(Reader paramReader, Type paramType)
    throws zzaoi, zzaoq
  {
    paramReader = new zzapy(paramReader);
    paramType = zza(paramReader, paramType);
    zza(paramType, paramReader);
    return paramType;
  }
  
  public <T> T zza(String paramString, Type paramType)
    throws zzaoq
  {
    if (paramString == null) {
      return null;
    }
    return (T)zza(new StringReader(paramString), paramType);
  }
  
  public void zza(zzaoh paramzzaoh, zzaqa paramzzaqa)
    throws zzaoi
  {
    boolean bool1 = paramzzaqa.isLenient();
    paramzzaqa.setLenient(true);
    boolean bool2 = paramzzaqa.bJ();
    paramzzaqa.zzdh(this.bkO);
    boolean bool3 = paramzzaqa.bK();
    paramzzaqa.zzdi(this.bkN);
    try
    {
      zzapi.zzb(paramzzaoh, paramzzaqa);
      return;
    }
    catch (IOException paramzzaoh)
    {
      throw new zzaoi(paramzzaoh);
    }
    finally
    {
      paramzzaqa.setLenient(bool1);
      paramzzaqa.zzdh(bool2);
      paramzzaqa.zzdi(bool3);
    }
  }
  
  public void zza(zzaoh paramzzaoh, Appendable paramAppendable)
    throws zzaoi
  {
    try
    {
      zza(paramzzaoh, zza(zzapi.zza(paramAppendable)));
      return;
    }
    catch (IOException paramzzaoh)
    {
      throw new RuntimeException(paramzzaoh);
    }
  }
  
  public void zza(Object paramObject, Type paramType, zzaqa paramzzaqa)
    throws zzaoi
  {
    paramType = zza(zzapx.zzl(paramType));
    boolean bool1 = paramzzaqa.isLenient();
    paramzzaqa.setLenient(true);
    boolean bool2 = paramzzaqa.bJ();
    paramzzaqa.zzdh(this.bkO);
    boolean bool3 = paramzzaqa.bK();
    paramzzaqa.zzdi(this.bkN);
    try
    {
      paramType.zza(paramzzaqa, paramObject);
      return;
    }
    catch (IOException paramObject)
    {
      throw new zzaoi((Throwable)paramObject);
    }
    finally
    {
      paramzzaqa.setLenient(bool1);
      paramzzaqa.zzdh(bool2);
      paramzzaqa.zzdi(bool3);
    }
  }
  
  public void zza(Object paramObject, Type paramType, Appendable paramAppendable)
    throws zzaoi
  {
    try
    {
      zza(paramObject, paramType, zza(zzapi.zza(paramAppendable)));
      return;
    }
    catch (IOException paramObject)
    {
      throw new zzaoi((Throwable)paramObject);
    }
  }
  
  public String zzb(zzaoh paramzzaoh)
  {
    StringWriter localStringWriter = new StringWriter();
    zza(paramzzaoh, localStringWriter);
    return localStringWriter.toString();
  }
  
  public String zzc(Object paramObject, Type paramType)
  {
    StringWriter localStringWriter = new StringWriter();
    zza(paramObject, paramType, localStringWriter);
    return localStringWriter.toString();
  }
  
  public String zzcl(Object paramObject)
  {
    if (paramObject == null) {
      return zzb(zzaoj.bld);
    }
    return zzc(paramObject, paramObject.getClass());
  }
  
  public <T> T zzf(String paramString, Class<T> paramClass)
    throws zzaoq
  {
    paramString = zza(paramString, paramClass);
    return (T)zzaph.zzp(paramClass).cast(paramString);
  }
  
  public <T> zzaot<T> zzk(Class<T> paramClass)
  {
    return zza(zzapx.zzr(paramClass));
  }
  
  static class zza<T>
    extends zzaot<T>
  {
    private zzaot<T> bkU;
    
    public void zza(zzaot<T> paramzzaot)
    {
      if (this.bkU != null) {
        throw new AssertionError();
      }
      this.bkU = paramzzaot;
    }
    
    public void zza(zzaqa paramzzaqa, T paramT)
      throws IOException
    {
      if (this.bkU == null) {
        throw new IllegalStateException();
      }
      this.bkU.zza(paramzzaqa, paramT);
    }
    
    public T zzb(zzapy paramzzapy)
      throws IOException
    {
      if (this.bkU == null) {
        throw new IllegalStateException();
      }
      return (T)this.bkU.zzb(paramzzapy);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaob.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */