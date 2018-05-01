package android.support.v13.view.inputmethod;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputContentInfo;

@TargetApi(25)
@RequiresApi(25)
final class InputConnectionCompatApi25
{
  public static boolean commitContent(InputConnection paramInputConnection, Object paramObject, int paramInt, Bundle paramBundle)
  {
    return paramInputConnection.commitContent((InputContentInfo)paramObject, paramInt, paramBundle);
  }
  
  public static InputConnection createWrapper(InputConnection paramInputConnection, final OnCommitContentListener paramOnCommitContentListener)
  {
    new InputConnectionWrapper(paramInputConnection, false)
    {
      public boolean commitContent(InputContentInfo paramAnonymousInputContentInfo, int paramAnonymousInt, Bundle paramAnonymousBundle)
      {
        if (paramOnCommitContentListener.onCommitContent(paramAnonymousInputContentInfo, paramAnonymousInt, paramAnonymousBundle)) {
          return true;
        }
        return super.commitContent(paramAnonymousInputContentInfo, paramAnonymousInt, paramAnonymousBundle);
      }
    };
  }
  
  public static abstract interface OnCommitContentListener
  {
    public abstract boolean onCommitContent(Object paramObject, int paramInt, Bundle paramBundle);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v13/view/inputmethod/InputConnectionCompatApi25.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */