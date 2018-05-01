package android.support.v13.view.inputmethod;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;

public final class EditorInfoCompat
{
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
  
  public static String[] getContentMimeTypes(EditorInfo paramEditorInfo)
  {
    if (Build.VERSION.SDK_INT >= 25)
    {
      paramEditorInfo = paramEditorInfo.contentMimeTypes;
      if (paramEditorInfo == null) {}
    }
    for (;;)
    {
      return paramEditorInfo;
      paramEditorInfo = EMPTY_STRING_ARRAY;
      continue;
      if (paramEditorInfo.extras == null)
      {
        paramEditorInfo = EMPTY_STRING_ARRAY;
      }
      else
      {
        String[] arrayOfString = paramEditorInfo.extras.getStringArray("android.support.v13.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES");
        paramEditorInfo = arrayOfString;
        if (arrayOfString == null) {
          paramEditorInfo = EMPTY_STRING_ARRAY;
        }
      }
    }
  }
  
  public static void setContentMimeTypes(EditorInfo paramEditorInfo, String[] paramArrayOfString)
  {
    if (Build.VERSION.SDK_INT >= 25) {
      paramEditorInfo.contentMimeTypes = paramArrayOfString;
    }
    for (;;)
    {
      return;
      if (paramEditorInfo.extras == null) {
        paramEditorInfo.extras = new Bundle();
      }
      paramEditorInfo.extras.putStringArray("android.support.v13.view.inputmethod.EditorInfoCompat.CONTENT_MIME_TYPES", paramArrayOfString);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v13/view/inputmethod/EditorInfoCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */