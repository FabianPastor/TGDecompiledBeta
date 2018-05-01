package android.support.v13.view;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.View.DragShadowBuilder;

@TargetApi(24)
@RequiresApi(24)
class ViewCompatApi24
{
  public static void cancelDragAndDrop(View paramView)
  {
    paramView.cancelDragAndDrop();
  }
  
  public static boolean startDragAndDrop(View paramView, ClipData paramClipData, View.DragShadowBuilder paramDragShadowBuilder, Object paramObject, int paramInt)
  {
    return paramView.startDragAndDrop(paramClipData, paramDragShadowBuilder, paramObject, paramInt);
  }
  
  public static void updateDragShadow(View paramView, View.DragShadowBuilder paramDragShadowBuilder)
  {
    paramView.updateDragShadow(paramDragShadowBuilder);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v13/view/ViewCompatApi24.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */