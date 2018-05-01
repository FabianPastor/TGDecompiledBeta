package android.support.v13.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.support.annotation.RequiresApi;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;

@TargetApi(24)
@RequiresApi(24)
class DragAndDropPermissionsCompatApi24
{
  public static void release(Object paramObject)
  {
    ((DragAndDropPermissions)paramObject).release();
  }
  
  public static Object request(Activity paramActivity, DragEvent paramDragEvent)
  {
    return paramActivity.requestDragAndDropPermissions(paramDragEvent);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v13/view/DragAndDropPermissionsCompatApi24.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */