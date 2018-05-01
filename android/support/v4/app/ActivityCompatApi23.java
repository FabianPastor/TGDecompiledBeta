package android.support.v4.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.SharedElementCallback;
import android.app.SharedElementCallback.OnSharedElementsReadyListener;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.view.View;
import java.util.List;
import java.util.Map;

@TargetApi(23)
@RequiresApi(23)
class ActivityCompatApi23
{
  private static SharedElementCallback createCallback(SharedElementCallback23 paramSharedElementCallback23)
  {
    SharedElementCallbackImpl localSharedElementCallbackImpl = null;
    if (paramSharedElementCallback23 != null) {
      localSharedElementCallbackImpl = new SharedElementCallbackImpl(paramSharedElementCallback23);
    }
    return localSharedElementCallbackImpl;
  }
  
  public static void requestPermissions(Activity paramActivity, String[] paramArrayOfString, int paramInt)
  {
    if ((paramActivity instanceof RequestPermissionsRequestCodeValidator)) {
      ((RequestPermissionsRequestCodeValidator)paramActivity).validateRequestPermissionsRequestCode(paramInt);
    }
    paramActivity.requestPermissions(paramArrayOfString, paramInt);
  }
  
  public static void setEnterSharedElementCallback(Activity paramActivity, SharedElementCallback23 paramSharedElementCallback23)
  {
    paramActivity.setEnterSharedElementCallback(createCallback(paramSharedElementCallback23));
  }
  
  public static void setExitSharedElementCallback(Activity paramActivity, SharedElementCallback23 paramSharedElementCallback23)
  {
    paramActivity.setExitSharedElementCallback(createCallback(paramSharedElementCallback23));
  }
  
  public static boolean shouldShowRequestPermissionRationale(Activity paramActivity, String paramString)
  {
    return paramActivity.shouldShowRequestPermissionRationale(paramString);
  }
  
  public static abstract interface OnSharedElementsReadyListenerBridge
  {
    public abstract void onSharedElementsReady();
  }
  
  public static abstract interface RequestPermissionsRequestCodeValidator
  {
    public abstract void validateRequestPermissionsRequestCode(int paramInt);
  }
  
  public static abstract class SharedElementCallback23
    extends ActivityCompatApi21.SharedElementCallback21
  {
    public abstract void onSharedElementsArrived(List<String> paramList, List<View> paramList1, ActivityCompatApi23.OnSharedElementsReadyListenerBridge paramOnSharedElementsReadyListenerBridge);
  }
  
  private static class SharedElementCallbackImpl
    extends SharedElementCallback
  {
    private ActivityCompatApi23.SharedElementCallback23 mCallback;
    
    public SharedElementCallbackImpl(ActivityCompatApi23.SharedElementCallback23 paramSharedElementCallback23)
    {
      this.mCallback = paramSharedElementCallback23;
    }
    
    public Parcelable onCaptureSharedElementSnapshot(View paramView, Matrix paramMatrix, RectF paramRectF)
    {
      return this.mCallback.onCaptureSharedElementSnapshot(paramView, paramMatrix, paramRectF);
    }
    
    public View onCreateSnapshotView(Context paramContext, Parcelable paramParcelable)
    {
      return this.mCallback.onCreateSnapshotView(paramContext, paramParcelable);
    }
    
    public void onMapSharedElements(List<String> paramList, Map<String, View> paramMap)
    {
      this.mCallback.onMapSharedElements(paramList, paramMap);
    }
    
    public void onRejectSharedElements(List<View> paramList)
    {
      this.mCallback.onRejectSharedElements(paramList);
    }
    
    public void onSharedElementEnd(List<String> paramList, List<View> paramList1, List<View> paramList2)
    {
      this.mCallback.onSharedElementEnd(paramList, paramList1, paramList2);
    }
    
    public void onSharedElementStart(List<String> paramList, List<View> paramList1, List<View> paramList2)
    {
      this.mCallback.onSharedElementStart(paramList, paramList1, paramList2);
    }
    
    public void onSharedElementsArrived(List<String> paramList, List<View> paramList1, final SharedElementCallback.OnSharedElementsReadyListener paramOnSharedElementsReadyListener)
    {
      this.mCallback.onSharedElementsArrived(paramList, paramList1, new ActivityCompatApi23.OnSharedElementsReadyListenerBridge()
      {
        public void onSharedElementsReady()
        {
          paramOnSharedElementsReadyListener.onSharedElementsReady();
        }
      });
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/app/ActivityCompatApi23.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */