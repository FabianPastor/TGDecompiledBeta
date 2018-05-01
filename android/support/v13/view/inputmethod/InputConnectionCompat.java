package android.support.v13.view.inputmethod;

import android.content.ClipDescription;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputContentInfo;

public final class InputConnectionCompat
{
  public static int INPUT_CONTENT_GRANT_READ_URI_PERMISSION = 1;
  
  public static InputConnection createWrapper(InputConnection paramInputConnection, EditorInfo paramEditorInfo, final OnCommitContentListener paramOnCommitContentListener)
  {
    if (paramInputConnection == null) {
      throw new IllegalArgumentException("inputConnection must be non-null");
    }
    if (paramEditorInfo == null) {
      throw new IllegalArgumentException("editorInfo must be non-null");
    }
    if (paramOnCommitContentListener == null) {
      throw new IllegalArgumentException("onCommitContentListener must be non-null");
    }
    Object localObject;
    if (Build.VERSION.SDK_INT >= 25) {
      localObject = new InputConnectionWrapper(paramInputConnection, false)
      {
        public boolean commitContent(InputContentInfo paramAnonymousInputContentInfo, int paramAnonymousInt, Bundle paramAnonymousBundle)
        {
          if (paramOnCommitContentListener.onCommitContent(InputContentInfoCompat.wrap(paramAnonymousInputContentInfo), paramAnonymousInt, paramAnonymousBundle)) {}
          for (boolean bool = true;; bool = super.commitContent(paramAnonymousInputContentInfo, paramAnonymousInt, paramAnonymousBundle)) {
            return bool;
          }
        }
      };
    }
    for (;;)
    {
      return (InputConnection)localObject;
      localObject = paramInputConnection;
      if (EditorInfoCompat.getContentMimeTypes(paramEditorInfo).length != 0) {
        localObject = new InputConnectionWrapper(paramInputConnection, false)
        {
          public boolean performPrivateCommand(String paramAnonymousString, Bundle paramAnonymousBundle)
          {
            if (InputConnectionCompat.handlePerformPrivateCommand(paramAnonymousString, paramAnonymousBundle, paramOnCommitContentListener)) {}
            for (boolean bool = true;; bool = super.performPrivateCommand(paramAnonymousString, paramAnonymousBundle)) {
              return bool;
            }
          }
        };
      }
    }
  }
  
  static boolean handlePerformPrivateCommand(String paramString, Bundle paramBundle, OnCommitContentListener paramOnCommitContentListener)
  {
    int i = 1;
    boolean bool = false;
    if (!TextUtils.equals("android.support.v13.view.inputmethod.InputConnectionCompat.COMMIT_CONTENT", paramString)) {}
    for (;;)
    {
      return bool;
      if (paramBundle != null)
      {
        paramString = null;
        try
        {
          ResultReceiver localResultReceiver = (ResultReceiver)paramBundle.getParcelable("android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_RESULT_RECEIVER");
          paramString = localResultReceiver;
          Uri localUri1 = (Uri)paramBundle.getParcelable("android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_URI");
          paramString = localResultReceiver;
          ClipDescription localClipDescription = (ClipDescription)paramBundle.getParcelable("android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_DESCRIPTION");
          paramString = localResultReceiver;
          Uri localUri2 = (Uri)paramBundle.getParcelable("android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_LINK_URI");
          paramString = localResultReceiver;
          int j = paramBundle.getInt("android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_FLAGS");
          paramString = localResultReceiver;
          Bundle localBundle = (Bundle)paramBundle.getParcelable("android.support.v13.view.inputmethod.InputConnectionCompat.CONTENT_OPTS");
          paramString = localResultReceiver;
          paramBundle = new android/support/v13/view/inputmethod/InputContentInfoCompat;
          paramString = localResultReceiver;
          paramBundle.<init>(localUri1, localClipDescription, localUri2);
          paramString = localResultReceiver;
          bool = paramOnCommitContentListener.onCommitContent(paramBundle, j, localBundle);
          if (localResultReceiver != null) {
            if (!bool) {
              break label159;
            }
          }
          label159:
          for (i = 1;; i = 0)
          {
            localResultReceiver.send(i, null);
            break;
          }
          if (0 == 0) {}
        }
        finally
        {
          if (paramString == null) {}
        }
      }
    }
    for (;;)
    {
      paramString.send(i, null);
      throw paramBundle;
      i = 0;
    }
  }
  
  public static abstract interface OnCommitContentListener
  {
    public abstract boolean onCommitContent(InputContentInfoCompat paramInputContentInfoCompat, int paramInt, Bundle paramBundle);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v13/view/inputmethod/InputConnectionCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */