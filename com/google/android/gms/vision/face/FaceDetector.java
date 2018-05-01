package com.google.android.gms.vision.face;

import android.content.Context;
import android.util.SparseArray;
import com.google.android.gms.internal.vision.zzj;
import com.google.android.gms.internal.vision.zzk;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.internal.client.zza;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.concurrent.GuardedBy;

public final class FaceDetector
  extends Detector<Face>
{
  @GuardedBy("mLock")
  private boolean mIsActive = true;
  private final Object mLock = new Object();
  private final com.google.android.gms.vision.zzc zzbm = new com.google.android.gms.vision.zzc();
  @GuardedBy("mLock")
  private final zza zzbn;
  
  private FaceDetector()
  {
    throw new IllegalStateException("Default constructor called");
  }
  
  private FaceDetector(zza paramzza)
  {
    this.zzbn = paramzza;
  }
  
  public final SparseArray<Face> detect(Frame paramFrame)
  {
    if (paramFrame == null) {
      throw new IllegalArgumentException("No frame supplied.");
    }
    ByteBuffer localByteBuffer = paramFrame.getGrayscaleImageData();
    synchronized (this.mLock)
    {
      if (!this.mIsActive)
      {
        paramFrame = new java/lang/RuntimeException;
        paramFrame.<init>("Cannot use detector after release()");
        throw paramFrame;
      }
    }
    paramFrame = this.zzbn.zzb(localByteBuffer, zzk.zzc(paramFrame));
    ??? = new HashSet();
    SparseArray localSparseArray = new SparseArray(paramFrame.length);
    int i = paramFrame.length;
    int j = 0;
    int k = 0;
    int m;
    if (j < i)
    {
      localByteBuffer = paramFrame[j];
      m = localByteBuffer.getId();
      k = Math.max(k, m);
      if (!((Set)???).contains(Integer.valueOf(m))) {
        break label178;
      }
      k++;
      m = k;
    }
    label178:
    for (;;)
    {
      ((Set)???).add(Integer.valueOf(m));
      localSparseArray.append(this.zzbm.zzb(m), localByteBuffer);
      j++;
      break;
      return localSparseArray;
    }
  }
  
  /* Error */
  protected final void finalize()
    throws java.lang.Throwable
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 32	com/google/android/gms/vision/face/FaceDetector:mLock	Ljava/lang/Object;
    //   4: astore_1
    //   5: aload_1
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 34	com/google/android/gms/vision/face/FaceDetector:mIsActive	Z
    //   11: ifeq +15 -> 26
    //   14: ldc 127
    //   16: ldc -127
    //   18: invokestatic 135	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   21: pop
    //   22: aload_0
    //   23: invokevirtual 138	com/google/android/gms/vision/Detector:release	()V
    //   26: aload_1
    //   27: monitorexit
    //   28: aload_0
    //   29: invokespecial 140	java/lang/Object:finalize	()V
    //   32: return
    //   33: astore_2
    //   34: aload_1
    //   35: monitorexit
    //   36: aload_2
    //   37: athrow
    //   38: astore_1
    //   39: aload_0
    //   40: invokespecial 140	java/lang/Object:finalize	()V
    //   43: aload_1
    //   44: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	45	0	this	FaceDetector
    //   38	6	1	localObject2	Object
    //   33	4	2	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   7	26	33	finally
    //   26	28	33	finally
    //   34	36	33	finally
    //   0	7	38	finally
    //   36	38	38	finally
  }
  
  public final boolean isOperational()
  {
    return this.zzbn.isOperational();
  }
  
  public final void release()
  {
    super.release();
    synchronized (this.mLock)
    {
      if (!this.mIsActive) {
        return;
      }
      this.zzbn.zzg();
      this.mIsActive = false;
    }
  }
  
  public static class Builder
  {
    private final Context mContext;
    private int zzbo = 0;
    private boolean zzbp = false;
    private int zzbq = 0;
    private boolean zzbr = true;
    private int zzbs = 0;
    private float zzbt = -1.0F;
    
    public Builder(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public FaceDetector build()
    {
      com.google.android.gms.vision.face.internal.client.zzc localzzc = new com.google.android.gms.vision.face.internal.client.zzc();
      localzzc.mode = this.zzbs;
      localzzc.zzcd = this.zzbo;
      localzzc.zzce = this.zzbq;
      localzzc.zzcf = this.zzbp;
      localzzc.zzcg = this.zzbr;
      localzzc.zzch = this.zzbt;
      return new FaceDetector(new zza(this.mContext, localzzc), null);
    }
    
    public Builder setLandmarkType(int paramInt)
    {
      if ((paramInt != 0) && (paramInt != 1)) {
        throw new IllegalArgumentException(34 + "Invalid landmark type: " + paramInt);
      }
      this.zzbo = paramInt;
      return this;
    }
    
    public Builder setMode(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        throw new IllegalArgumentException(25 + "Invalid mode: " + paramInt);
      }
      this.zzbs = paramInt;
      return this;
    }
    
    public Builder setTrackingEnabled(boolean paramBoolean)
    {
      this.zzbr = paramBoolean;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/face/FaceDetector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */