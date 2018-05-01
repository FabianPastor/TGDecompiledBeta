package android.support.v4.print;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.annotation.RequiresApi;

@TargetApi(20)
@RequiresApi(20)
class PrintHelperApi20
  extends PrintHelperKitkat
{
  PrintHelperApi20(Context paramContext)
  {
    super(paramContext);
    this.mPrintActivityRespectsOrientation = false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/print/PrintHelperApi20.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */