package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;

public class ChannelIntroActivity
  extends BaseFragment
{
  private TextView createChannelText;
  private TextView descriptionText;
  private ImageView imageView;
  private TextView whatIsChannelText;
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackgroundColor(-1);
    this.actionBar.setBackButtonImage(2130837916);
    this.actionBar.setItemsBackgroundColor(788529152);
    this.actionBar.setCastShadows(false);
    if (!AndroidUtilities.isTablet()) {
      this.actionBar.showActionModeTop();
    }
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ChannelIntroActivity.this.finishFragment();
        }
      }
    });
    this.fragmentView = new ViewGroup(paramContext)
    {
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        int i = paramAnonymousInt3 - paramAnonymousInt1;
        paramAnonymousInt1 = paramAnonymousInt4 - paramAnonymousInt2;
        if (paramAnonymousInt3 > paramAnonymousInt4)
        {
          paramAnonymousInt2 = (int)(paramAnonymousInt1 * 0.05F);
          ChannelIntroActivity.this.imageView.layout(0, paramAnonymousInt2, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + paramAnonymousInt2);
          paramAnonymousInt2 = (int)(i * 0.4F);
          paramAnonymousInt3 = (int)(paramAnonymousInt1 * 0.14F);
          ChannelIntroActivity.this.whatIsChannelText.layout(paramAnonymousInt2, paramAnonymousInt3, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth() + paramAnonymousInt2, ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + paramAnonymousInt3);
          paramAnonymousInt3 = (int)(paramAnonymousInt1 * 0.61F);
          ChannelIntroActivity.this.createChannelText.layout(paramAnonymousInt2, paramAnonymousInt3, ChannelIntroActivity.this.createChannelText.getMeasuredWidth() + paramAnonymousInt2, ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + paramAnonymousInt3);
          paramAnonymousInt2 = (int)(i * 0.45F);
          paramAnonymousInt1 = (int)(paramAnonymousInt1 * 0.31F);
          ChannelIntroActivity.this.descriptionText.layout(paramAnonymousInt2, paramAnonymousInt1, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + paramAnonymousInt2, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + paramAnonymousInt1);
          return;
        }
        paramAnonymousInt2 = (int)(paramAnonymousInt1 * 0.05F);
        ChannelIntroActivity.this.imageView.layout(0, paramAnonymousInt2, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + paramAnonymousInt2);
        paramAnonymousInt2 = (int)(paramAnonymousInt1 * 0.59F);
        ChannelIntroActivity.this.whatIsChannelText.layout(0, paramAnonymousInt2, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth(), ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + paramAnonymousInt2);
        paramAnonymousInt2 = (int)(paramAnonymousInt1 * 0.68F);
        paramAnonymousInt3 = (int)(i * 0.05F);
        ChannelIntroActivity.this.descriptionText.layout(paramAnonymousInt3, paramAnonymousInt2, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + paramAnonymousInt3, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + paramAnonymousInt2);
        paramAnonymousInt1 = (int)(paramAnonymousInt1 * 0.86F);
        ChannelIntroActivity.this.createChannelText.layout(0, paramAnonymousInt1, ChannelIntroActivity.this.createChannelText.getMeasuredWidth(), ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + paramAnonymousInt1);
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        paramAnonymousInt1 = View.MeasureSpec.getSize(paramAnonymousInt1);
        paramAnonymousInt2 = View.MeasureSpec.getSize(paramAnonymousInt2);
        if (paramAnonymousInt1 > paramAnonymousInt2)
        {
          ChannelIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec((int)(paramAnonymousInt1 * 0.45F), 1073741824), View.MeasureSpec.makeMeasureSpec((int)(paramAnonymousInt2 * 0.78F), 1073741824));
          ChannelIntroActivity.this.whatIsChannelText.measure(View.MeasureSpec.makeMeasureSpec((int)(paramAnonymousInt1 * 0.6F), 1073741824), View.MeasureSpec.makeMeasureSpec(paramAnonymousInt2, 0));
          ChannelIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec((int)(paramAnonymousInt1 * 0.5F), 1073741824), View.MeasureSpec.makeMeasureSpec(paramAnonymousInt2, 0));
          ChannelIntroActivity.this.createChannelText.measure(View.MeasureSpec.makeMeasureSpec((int)(paramAnonymousInt1 * 0.6F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0F), 1073741824));
        }
        for (;;)
        {
          setMeasuredDimension(paramAnonymousInt1, paramAnonymousInt2);
          return;
          ChannelIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(paramAnonymousInt1, 1073741824), View.MeasureSpec.makeMeasureSpec((int)(paramAnonymousInt2 * 0.44F), 1073741824));
          ChannelIntroActivity.this.whatIsChannelText.measure(View.MeasureSpec.makeMeasureSpec(paramAnonymousInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(paramAnonymousInt2, 0));
          ChannelIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec((int)(paramAnonymousInt1 * 0.9F), 1073741824), View.MeasureSpec.makeMeasureSpec(paramAnonymousInt2, 0));
          ChannelIntroActivity.this.createChannelText.measure(View.MeasureSpec.makeMeasureSpec(paramAnonymousInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0F), 1073741824));
        }
      }
    };
    this.fragmentView.setBackgroundColor(-1);
    ViewGroup localViewGroup = (ViewGroup)this.fragmentView;
    localViewGroup.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.imageView = new ImageView(paramContext);
    this.imageView.setImageResource(2130837589);
    this.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    localViewGroup.addView(this.imageView);
    this.whatIsChannelText = new TextView(paramContext);
    this.whatIsChannelText.setTextColor(-14606047);
    this.whatIsChannelText.setGravity(1);
    this.whatIsChannelText.setTextSize(1, 24.0F);
    this.whatIsChannelText.setText(LocaleController.getString("ChannelAlertTitle", 2131165413));
    localViewGroup.addView(this.whatIsChannelText);
    this.descriptionText = new TextView(paramContext);
    this.descriptionText.setTextColor(-8882056);
    this.descriptionText.setGravity(1);
    this.descriptionText.setTextSize(1, 16.0F);
    this.descriptionText.setText(LocaleController.getString("ChannelAlertText", 2131165412));
    localViewGroup.addView(this.descriptionText);
    this.createChannelText = new TextView(paramContext);
    this.createChannelText.setTextColor(-11759926);
    this.createChannelText.setGravity(17);
    this.createChannelText.setTextSize(1, 16.0F);
    this.createChannelText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.createChannelText.setText(LocaleController.getString("ChannelAlertCreate", 2131165411));
    localViewGroup.addView(this.createChannelText);
    this.createChannelText.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        paramAnonymousView = new Bundle();
        paramAnonymousView.putInt("step", 0);
        ChannelIntroActivity.this.presentFragment(new ChannelCreateActivity(paramAnonymousView), true);
      }
    });
    return this.fragmentView;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChannelIntroActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */