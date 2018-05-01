package android.support.v13.view.inputmethod;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.view.inputmethod.EditorInfo;

@TargetApi(25)
@RequiresApi(25)
final class EditorInfoCompatApi25
{
  public static String[] getContentMimeTypes(EditorInfo paramEditorInfo)
  {
    return paramEditorInfo.contentMimeTypes;
  }
  
  public static void setContentMimeTypes(EditorInfo paramEditorInfo, String[] paramArrayOfString)
  {
    paramEditorInfo.contentMimeTypes = paramArrayOfString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v13/view/inputmethod/EditorInfoCompatApi25.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */