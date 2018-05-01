package android.support.v13.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.os.BuildCompat;
import android.view.DragEvent;

@TargetApi(13)
@RequiresApi(13)
public final class DragAndDropPermissionsCompat
{
  private static DragAndDropPermissionsCompatImpl IMPL = new BaseDragAndDropPermissionsCompatImpl();
  private Object mDragAndDropPermissions;
  
  static
  {
    if (BuildCompat.isAtLeastN())
    {
      IMPL = new Api24DragAndDropPermissionsCompatImpl();
      return;
    }
  }
  
  private DragAndDropPermissionsCompat(Object paramObject)
  {
    this.mDragAndDropPermissions = paramObject;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static DragAndDropPermissionsCompat request(Activity paramActivity, DragEvent paramDragEvent)
  {
    paramActivity = IMPL.request(paramActivity, paramDragEvent);
    if (paramActivity != null) {
      return new DragAndDropPermissionsCompat(paramActivity);
    }
    return null;
  }
  
  public void release()
  {
    IMPL.release(this.mDragAndDropPermissions);
  }
  
  static class Api24DragAndDropPermissionsCompatImpl
    extends DragAndDropPermissionsCompat.BaseDragAndDropPermissionsCompatImpl
  {
    public void release(Object paramObject)
    {
      DragAndDropPermissionsCompatApi24.release(paramObject);
    }
    
    public Object request(Activity paramActivity, DragEvent paramDragEvent)
    {
      return DragAndDropPermissionsCompatApi24.request(paramActivity, paramDragEvent);
    }
  }
  
  static class BaseDragAndDropPermissionsCompatImpl
    implements DragAndDropPermissionsCompat.DragAndDropPermissionsCompatImpl
  {
    public void release(Object paramObject) {}
    
    public Object request(Activity paramActivity, DragEvent paramDragEvent)
    {
      return null;
    }
  }
  
  static abstract interface DragAndDropPermissionsCompatImpl
  {
    public abstract void release(Object paramObject);
    
    public abstract Object request(Activity paramActivity, DragEvent paramDragEvent);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v13/view/DragAndDropPermissionsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */