package android.support.v13.view.inputmethod;

import android.annotation.TargetApi;
import android.content.ClipDescription;
import android.net.Uri;
import android.support.annotation.RequiresApi;
import android.view.inputmethod.InputContentInfo;

@TargetApi(25)
@RequiresApi(25)
final class InputContentInfoCompatApi25
{
  public static Object create(Uri paramUri1, ClipDescription paramClipDescription, Uri paramUri2)
  {
    return new InputContentInfo(paramUri1, paramClipDescription, paramUri2);
  }
  
  public static Uri getContentUri(Object paramObject)
  {
    return ((InputContentInfo)paramObject).getContentUri();
  }
  
  public static ClipDescription getDescription(Object paramObject)
  {
    return ((InputContentInfo)paramObject).getDescription();
  }
  
  public static Uri getLinkUri(Object paramObject)
  {
    return ((InputContentInfo)paramObject).getLinkUri();
  }
  
  public static void releasePermission(Object paramObject)
  {
    ((InputContentInfo)paramObject).releasePermission();
  }
  
  public static void requestPermission(Object paramObject)
  {
    ((InputContentInfo)paramObject).requestPermission();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v13/view/inputmethod/InputContentInfoCompatApi25.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */