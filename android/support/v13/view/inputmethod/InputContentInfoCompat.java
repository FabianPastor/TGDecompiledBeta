package android.support.v13.view.inputmethod;

import android.content.ClipDescription;
import android.net.Uri;
import android.os.Build.VERSION;
import android.view.inputmethod.InputContentInfo;

public final class InputContentInfoCompat
{
  private final InputContentInfoCompatImpl mImpl;
  
  public InputContentInfoCompat(Uri paramUri1, ClipDescription paramClipDescription, Uri paramUri2)
  {
    if (Build.VERSION.SDK_INT >= 25) {}
    for (this.mImpl = new InputContentInfoCompatApi25Impl(paramUri1, paramClipDescription, paramUri2);; this.mImpl = new InputContentInfoCompatBaseImpl(paramUri1, paramClipDescription, paramUri2)) {
      return;
    }
  }
  
  private InputContentInfoCompat(InputContentInfoCompatImpl paramInputContentInfoCompatImpl)
  {
    this.mImpl = paramInputContentInfoCompatImpl;
  }
  
  public static InputContentInfoCompat wrap(Object paramObject)
  {
    InputContentInfoCompat localInputContentInfoCompat = null;
    if (paramObject == null) {}
    for (;;)
    {
      return localInputContentInfoCompat;
      if (Build.VERSION.SDK_INT >= 25) {
        localInputContentInfoCompat = new InputContentInfoCompat(new InputContentInfoCompatApi25Impl(paramObject));
      }
    }
  }
  
  public Uri getContentUri()
  {
    return this.mImpl.getContentUri();
  }
  
  public ClipDescription getDescription()
  {
    return this.mImpl.getDescription();
  }
  
  public void releasePermission()
  {
    this.mImpl.releasePermission();
  }
  
  public void requestPermission()
  {
    this.mImpl.requestPermission();
  }
  
  private static final class InputContentInfoCompatApi25Impl
    implements InputContentInfoCompat.InputContentInfoCompatImpl
  {
    final InputContentInfo mObject;
    
    InputContentInfoCompatApi25Impl(Uri paramUri1, ClipDescription paramClipDescription, Uri paramUri2)
    {
      this.mObject = new InputContentInfo(paramUri1, paramClipDescription, paramUri2);
    }
    
    InputContentInfoCompatApi25Impl(Object paramObject)
    {
      this.mObject = ((InputContentInfo)paramObject);
    }
    
    public Uri getContentUri()
    {
      return this.mObject.getContentUri();
    }
    
    public ClipDescription getDescription()
    {
      return this.mObject.getDescription();
    }
    
    public void releasePermission()
    {
      this.mObject.releasePermission();
    }
    
    public void requestPermission()
    {
      this.mObject.requestPermission();
    }
  }
  
  private static final class InputContentInfoCompatBaseImpl
    implements InputContentInfoCompat.InputContentInfoCompatImpl
  {
    private final Uri mContentUri;
    private final ClipDescription mDescription;
    private final Uri mLinkUri;
    
    InputContentInfoCompatBaseImpl(Uri paramUri1, ClipDescription paramClipDescription, Uri paramUri2)
    {
      this.mContentUri = paramUri1;
      this.mDescription = paramClipDescription;
      this.mLinkUri = paramUri2;
    }
    
    public Uri getContentUri()
    {
      return this.mContentUri;
    }
    
    public ClipDescription getDescription()
    {
      return this.mDescription;
    }
    
    public void releasePermission() {}
    
    public void requestPermission() {}
  }
  
  private static abstract interface InputContentInfoCompatImpl
  {
    public abstract Uri getContentUri();
    
    public abstract ClipDescription getDescription();
    
    public abstract void releasePermission();
    
    public abstract void requestPermission();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v13/view/inputmethod/InputContentInfoCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */