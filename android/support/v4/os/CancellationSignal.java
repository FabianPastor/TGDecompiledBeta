package android.support.v4.os;

import android.os.Build.VERSION;

public final class CancellationSignal
{
  private boolean mCancelInProgress;
  private Object mCancellationSignalObj;
  private boolean mIsCanceled;
  private OnCancelListener mOnCancelListener;
  
  /* Error */
  public void cancel()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 22	android/support/v4/os/CancellationSignal:mIsCanceled	Z
    //   6: ifeq +6 -> 12
    //   9: aload_0
    //   10: monitorexit
    //   11: return
    //   12: aload_0
    //   13: iconst_1
    //   14: putfield 22	android/support/v4/os/CancellationSignal:mIsCanceled	Z
    //   17: aload_0
    //   18: iconst_1
    //   19: putfield 24	android/support/v4/os/CancellationSignal:mCancelInProgress	Z
    //   22: aload_0
    //   23: getfield 26	android/support/v4/os/CancellationSignal:mOnCancelListener	Landroid/support/v4/os/CancellationSignal$OnCancelListener;
    //   26: astore_1
    //   27: aload_0
    //   28: getfield 28	android/support/v4/os/CancellationSignal:mCancellationSignalObj	Ljava/lang/Object;
    //   31: astore_2
    //   32: aload_0
    //   33: monitorexit
    //   34: aload_1
    //   35: ifnull +9 -> 44
    //   38: aload_1
    //   39: invokeinterface 31 1 0
    //   44: aload_2
    //   45: ifnull +18 -> 63
    //   48: getstatic 37	android/os/Build$VERSION:SDK_INT	I
    //   51: bipush 16
    //   53: if_icmplt +10 -> 63
    //   56: aload_2
    //   57: checkcast 39	android/os/CancellationSignal
    //   60: invokevirtual 41	android/os/CancellationSignal:cancel	()V
    //   63: aload_0
    //   64: monitorenter
    //   65: aload_0
    //   66: iconst_0
    //   67: putfield 24	android/support/v4/os/CancellationSignal:mCancelInProgress	Z
    //   70: aload_0
    //   71: invokevirtual 44	java/lang/Object:notifyAll	()V
    //   74: aload_0
    //   75: monitorexit
    //   76: goto -65 -> 11
    //   79: astore_2
    //   80: aload_0
    //   81: monitorexit
    //   82: aload_2
    //   83: athrow
    //   84: astore_2
    //   85: aload_0
    //   86: monitorexit
    //   87: aload_2
    //   88: athrow
    //   89: astore_2
    //   90: aload_0
    //   91: monitorenter
    //   92: aload_0
    //   93: iconst_0
    //   94: putfield 24	android/support/v4/os/CancellationSignal:mCancelInProgress	Z
    //   97: aload_0
    //   98: invokevirtual 44	java/lang/Object:notifyAll	()V
    //   101: aload_0
    //   102: monitorexit
    //   103: aload_2
    //   104: athrow
    //   105: astore_2
    //   106: aload_0
    //   107: monitorexit
    //   108: aload_2
    //   109: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	110	0	this	CancellationSignal
    //   26	13	1	localOnCancelListener	OnCancelListener
    //   31	26	2	localObject1	Object
    //   79	4	2	localObject2	Object
    //   84	4	2	localObject3	Object
    //   89	15	2	localObject4	Object
    //   105	4	2	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   65	76	79	finally
    //   80	82	79	finally
    //   2	11	84	finally
    //   12	34	84	finally
    //   85	87	84	finally
    //   38	44	89	finally
    //   48	63	89	finally
    //   92	103	105	finally
    //   106	108	105	finally
  }
  
  public Object getCancellationSignalObject()
  {
    Object localObject1;
    if (Build.VERSION.SDK_INT < 16) {
      localObject1 = null;
    }
    for (;;)
    {
      return localObject1;
      try
      {
        if (this.mCancellationSignalObj == null)
        {
          localObject1 = new android/os/CancellationSignal;
          ((android.os.CancellationSignal)localObject1).<init>();
          this.mCancellationSignalObj = localObject1;
          if (this.mIsCanceled) {
            ((android.os.CancellationSignal)this.mCancellationSignalObj).cancel();
          }
        }
        localObject1 = this.mCancellationSignalObj;
      }
      finally {}
    }
  }
  
  public static abstract interface OnCancelListener
  {
    public abstract void onCancel();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/os/CancellationSignal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */