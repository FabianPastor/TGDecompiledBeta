package org.telegram.ui.Cells;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class PhotoPickerSearchCell
  extends LinearLayout
{
  private PhotoPickerSearchCellDelegate delegate;
  
  public PhotoPickerSearchCell(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    setOrientation(0);
    Object localObject = new SearchButton(paramContext);
    ((SearchButton)localObject).textView1.setText(LocaleController.getString("SearchImages", 2131166209));
    ((SearchButton)localObject).textView2.setText(LocaleController.getString("SearchImagesInfo", 2131166210));
    ((SearchButton)localObject).imageView.setImageResource(2130837977);
    addView((View)localObject);
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)((SearchButton)localObject).getLayoutParams();
    localLayoutParams.weight = 0.5F;
    localLayoutParams.topMargin = AndroidUtilities.dp(4.0F);
    localLayoutParams.height = AndroidUtilities.dp(48.0F);
    localLayoutParams.width = 0;
    ((SearchButton)localObject).setLayoutParams(localLayoutParams);
    ((SearchButton)localObject).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (PhotoPickerSearchCell.this.delegate != null) {
          PhotoPickerSearchCell.this.delegate.didPressedSearchButton(0);
        }
      }
    });
    localObject = new FrameLayout(paramContext);
    ((FrameLayout)localObject).setBackgroundColor(0);
    addView((View)localObject);
    localLayoutParams = (LinearLayout.LayoutParams)((FrameLayout)localObject).getLayoutParams();
    localLayoutParams.topMargin = AndroidUtilities.dp(4.0F);
    localLayoutParams.height = AndroidUtilities.dp(48.0F);
    localLayoutParams.width = AndroidUtilities.dp(4.0F);
    ((FrameLayout)localObject).setLayoutParams(localLayoutParams);
    paramContext = new SearchButton(paramContext);
    paramContext.textView1.setText(LocaleController.getString("SearchGifs", 2131166207));
    paramContext.textView2.setText("GIPHY");
    paramContext.imageView.setImageResource(2130837972);
    addView(paramContext);
    localObject = (LinearLayout.LayoutParams)paramContext.getLayoutParams();
    ((LinearLayout.LayoutParams)localObject).weight = 0.5F;
    ((LinearLayout.LayoutParams)localObject).topMargin = AndroidUtilities.dp(4.0F);
    ((LinearLayout.LayoutParams)localObject).height = AndroidUtilities.dp(48.0F);
    ((LinearLayout.LayoutParams)localObject).width = 0;
    paramContext.setLayoutParams((ViewGroup.LayoutParams)localObject);
    if (paramBoolean)
    {
      paramContext.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (PhotoPickerSearchCell.this.delegate != null) {
            PhotoPickerSearchCell.this.delegate.didPressedSearchButton(1);
          }
        }
      });
      return;
    }
    paramContext.setAlpha(0.5F);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(52.0F), 1073741824));
  }
  
  public void setDelegate(PhotoPickerSearchCellDelegate paramPhotoPickerSearchCellDelegate)
  {
    this.delegate = paramPhotoPickerSearchCellDelegate;
  }
  
  public static abstract interface PhotoPickerSearchCellDelegate
  {
    public abstract void didPressedSearchButton(int paramInt);
  }
  
  private class SearchButton
    extends FrameLayout
  {
    private ImageView imageView;
    private View selector;
    private TextView textView1;
    private TextView textView2;
    
    public SearchButton(Context paramContext)
    {
      super();
      setBackgroundColor(-15066598);
      this.selector = new View(paramContext);
      this.selector.setBackgroundResource(2130837796);
      addView(this.selector, LayoutHelper.createFrame(-1, -1.0F));
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      addView(this.imageView, LayoutHelper.createFrame(48, 48, 51));
      this.textView1 = new TextView(paramContext);
      this.textView1.setGravity(16);
      this.textView1.setTextSize(1, 14.0F);
      this.textView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.textView1.setTextColor(-1);
      this.textView1.setSingleLine(true);
      this.textView1.setEllipsize(TextUtils.TruncateAt.END);
      addView(this.textView1, LayoutHelper.createFrame(-1, -2.0F, 51, 51.0F, 8.0F, 4.0F, 0.0F));
      this.textView2 = new TextView(paramContext);
      this.textView2.setGravity(16);
      this.textView2.setTextSize(1, 10.0F);
      this.textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.textView2.setTextColor(-10066330);
      this.textView2.setSingleLine(true);
      this.textView2.setEllipsize(TextUtils.TruncateAt.END);
      addView(this.textView2, LayoutHelper.createFrame(-1, -2.0F, 51, 51.0F, 26.0F, 4.0F, 0.0F));
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      if (Build.VERSION.SDK_INT >= 21) {
        this.selector.drawableHotspotChanged(paramMotionEvent.getX(), paramMotionEvent.getY());
      }
      return super.onTouchEvent(paramMotionEvent);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/PhotoPickerSearchCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */