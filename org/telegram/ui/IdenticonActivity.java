package org.telegram.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.URLSpanReplacement;

public class IdenticonActivity
  extends BaseFragment
{
  private int chat_id;
  
  public IdenticonActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  private void fixLayout()
  {
    this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        if (IdenticonActivity.this.fragmentView == null) {
          return true;
        }
        IdenticonActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
        LinearLayout localLinearLayout = (LinearLayout)IdenticonActivity.this.fragmentView;
        int i = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
        if ((i == 3) || (i == 1)) {
          localLinearLayout.setOrientation(0);
        }
        for (;;)
        {
          IdenticonActivity.this.fragmentView.setPadding(IdenticonActivity.this.fragmentView.getPaddingLeft(), 0, IdenticonActivity.this.fragmentView.getPaddingRight(), IdenticonActivity.this.fragmentView.getPaddingBottom());
          return true;
          localLinearLayout.setOrientation(1);
        }
      }
    });
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("EncryptionKey", 2131165613));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          IdenticonActivity.this.finishFragment();
        }
      }
    });
    this.fragmentView = new LinearLayout(paramContext);
    Object localObject2 = (LinearLayout)this.fragmentView;
    ((LinearLayout)localObject2).setOrientation(1);
    ((LinearLayout)localObject2).setWeightSum(100.0F);
    ((LinearLayout)localObject2).setBackgroundColor(-986896);
    this.fragmentView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    Object localObject3 = new FrameLayout(paramContext);
    ((FrameLayout)localObject3).setPadding(AndroidUtilities.dp(20.0F), AndroidUtilities.dp(20.0F), AndroidUtilities.dp(20.0F), AndroidUtilities.dp(20.0F));
    ((LinearLayout)localObject2).addView((View)localObject3, LayoutHelper.createLinear(-1, -1, 50.0F));
    Object localObject1 = new ImageView(paramContext);
    ((ImageView)localObject1).setScaleType(ImageView.ScaleType.FIT_XY);
    ((FrameLayout)localObject3).addView((View)localObject1, LayoutHelper.createFrame(-1, -1.0F));
    localObject3 = new FrameLayout(paramContext);
    ((FrameLayout)localObject3).setBackgroundColor(-1);
    ((FrameLayout)localObject3).setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F));
    ((LinearLayout)localObject2).addView((View)localObject3, LayoutHelper.createLinear(-1, -1, 50.0F));
    paramContext = new TextView(paramContext);
    paramContext.setTextColor(-8421505);
    paramContext.setTextSize(1, 16.0F);
    paramContext.setLinksClickable(true);
    paramContext.setClickable(true);
    paramContext.setMovementMethod(new LinkMovementMethodMy(null));
    paramContext.setLinkTextColor(-14255946);
    paramContext.setGravity(17);
    ((FrameLayout)localObject3).addView(paramContext, LayoutHelper.createFrame(-1, -1.0F));
    localObject2 = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(this.chat_id));
    if (localObject2 != null)
    {
      localObject3 = new IdenticonDrawable();
      ((ImageView)localObject1).setImageDrawable((Drawable)localObject3);
      ((IdenticonDrawable)localObject3).setEncryptedChat((TLRPC.EncryptedChat)localObject2);
      localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.EncryptedChat)localObject2).user_id));
      localObject3 = new SpannableStringBuilder();
      if (((TLRPC.EncryptedChat)localObject2).key_hash.length > 16)
      {
        localObject2 = Utilities.bytesToHex(((TLRPC.EncryptedChat)localObject2).key_hash);
        i = 0;
        if (i < 32)
        {
          if (i != 0)
          {
            if (i % 8 != 0) {
              break label446;
            }
            ((SpannableStringBuilder)localObject3).append('\n');
          }
          for (;;)
          {
            ((SpannableStringBuilder)localObject3).append(((String)localObject2).substring(i * 2, i * 2 + 2));
            ((SpannableStringBuilder)localObject3).append(' ');
            i += 1;
            break;
            label446:
            if (i % 4 == 0) {
              ((SpannableStringBuilder)localObject3).append(' ');
            }
          }
        }
        ((SpannableStringBuilder)localObject3).append("\n\n");
      }
      ((SpannableStringBuilder)localObject3).append(AndroidUtilities.replaceTags(LocaleController.formatString("EncryptionKeyDescription", 2131165614, new Object[] { ((TLRPC.User)localObject1).first_name, ((TLRPC.User)localObject1).first_name })));
      int i = ((SpannableStringBuilder)localObject3).toString().indexOf("telegram.org");
      if (i != -1) {
        ((SpannableStringBuilder)localObject3).setSpan(new URLSpanReplacement(LocaleController.getString("EncryptionKeyLink", 2131165615)), i, "telegram.org".length() + i, 33);
      }
      paramContext.setText((CharSequence)localObject3);
    }
    return this.fragmentView;
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    fixLayout();
  }
  
  public boolean onFragmentCreate()
  {
    this.chat_id = getArguments().getInt("chat_id");
    return super.onFragmentCreate();
  }
  
  public void onResume()
  {
    super.onResume();
    fixLayout();
  }
  
  private static class LinkMovementMethodMy
    extends LinkMovementMethod
  {
    public boolean onTouchEvent(@NonNull TextView paramTextView, @NonNull Spannable paramSpannable, @NonNull MotionEvent paramMotionEvent)
    {
      try
      {
        boolean bool = super.onTouchEvent(paramTextView, paramSpannable, paramMotionEvent);
        return bool;
      }
      catch (Exception paramTextView)
      {
        FileLog.e("tmessages", paramTextView);
      }
      return false;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/IdenticonActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */