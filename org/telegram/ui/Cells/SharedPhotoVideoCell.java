package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.PhotoViewer;

public class SharedPhotoVideoCell
  extends FrameLayout
{
  private SharedPhotoVideoCellDelegate delegate;
  private int[] indeces = new int[6];
  private boolean isFirst;
  private int itemsCount;
  private MessageObject[] messageObjects = new MessageObject[6];
  private PhotoVideoView[] photoVideoViews = new PhotoVideoView[6];
  
  public SharedPhotoVideoCell(Context paramContext)
  {
    super(paramContext);
    for (int i = 0; i < 6; i++)
    {
      this.photoVideoViews[i] = new PhotoVideoView(paramContext);
      addView(this.photoVideoViews[i]);
      this.photoVideoViews[i].setVisibility(4);
      this.photoVideoViews[i].setTag(Integer.valueOf(i));
      this.photoVideoViews[i].setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (SharedPhotoVideoCell.this.delegate != null)
          {
            int i = ((Integer)paramAnonymousView.getTag()).intValue();
            SharedPhotoVideoCell.this.delegate.didClickItem(SharedPhotoVideoCell.this, SharedPhotoVideoCell.this.indeces[i], SharedPhotoVideoCell.this.messageObjects[i], i);
          }
        }
      });
      this.photoVideoViews[i].setOnLongClickListener(new View.OnLongClickListener()
      {
        public boolean onLongClick(View paramAnonymousView)
        {
          int i;
          if (SharedPhotoVideoCell.this.delegate != null) {
            i = ((Integer)paramAnonymousView.getTag()).intValue();
          }
          for (boolean bool = SharedPhotoVideoCell.this.delegate.didLongClickItem(SharedPhotoVideoCell.this, SharedPhotoVideoCell.this.indeces[i], SharedPhotoVideoCell.this.messageObjects[i], i);; bool = false) {
            return bool;
          }
        }
      });
    }
  }
  
  public BackupImageView getImageView(int paramInt)
  {
    if (paramInt >= this.itemsCount) {}
    for (BackupImageView localBackupImageView = null;; localBackupImageView = this.photoVideoViews[paramInt].imageView) {
      return localBackupImageView;
    }
  }
  
  public MessageObject getMessageObject(int paramInt)
  {
    if (paramInt >= this.itemsCount) {}
    for (MessageObject localMessageObject = null;; localMessageObject = this.messageObjects[paramInt]) {
      return localMessageObject;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = 0;
    label35:
    FrameLayout.LayoutParams localLayoutParams;
    if (AndroidUtilities.isTablet())
    {
      paramInt2 = (AndroidUtilities.dp(490.0F) - (this.itemsCount + 1) * AndroidUtilities.dp(4.0F)) / this.itemsCount;
      j = 0;
      if (j >= this.itemsCount) {
        break label172;
      }
      localLayoutParams = (FrameLayout.LayoutParams)this.photoVideoViews[j].getLayoutParams();
      if (!this.isFirst) {
        break label162;
      }
    }
    label162:
    for (int k = 0;; k = AndroidUtilities.dp(4.0F))
    {
      localLayoutParams.topMargin = k;
      localLayoutParams.leftMargin = ((AndroidUtilities.dp(4.0F) + paramInt2) * j + AndroidUtilities.dp(4.0F));
      localLayoutParams.width = paramInt2;
      localLayoutParams.height = paramInt2;
      localLayoutParams.gravity = 51;
      this.photoVideoViews[j].setLayoutParams(localLayoutParams);
      j++;
      break label35;
      paramInt2 = (AndroidUtilities.displaySize.x - (this.itemsCount + 1) * AndroidUtilities.dp(4.0F)) / this.itemsCount;
      break;
    }
    label172:
    if (this.isFirst) {}
    for (int j = i;; j = AndroidUtilities.dp(4.0F))
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(j + paramInt2, NUM));
      return;
    }
  }
  
  public void setChecked(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.photoVideoViews[paramInt].setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setDelegate(SharedPhotoVideoCellDelegate paramSharedPhotoVideoCellDelegate)
  {
    this.delegate = paramSharedPhotoVideoCellDelegate;
  }
  
  public void setIsFirst(boolean paramBoolean)
  {
    this.isFirst = paramBoolean;
  }
  
  public void setItem(int paramInt1, int paramInt2, MessageObject paramMessageObject)
  {
    this.messageObjects[paramInt1] = paramMessageObject;
    this.indeces[paramInt1] = paramInt2;
    PhotoVideoView localPhotoVideoView;
    boolean bool;
    if (paramMessageObject != null)
    {
      this.photoVideoViews[paramInt1].setVisibility(0);
      localPhotoVideoView = this.photoVideoViews[paramInt1];
      localPhotoVideoView.imageView.getImageReceiver().setParentMessageObject(paramMessageObject);
      Object localObject = localPhotoVideoView.imageView.getImageReceiver();
      if (!PhotoViewer.isShowingImage(paramMessageObject))
      {
        bool = true;
        ((ImageReceiver)localObject).setVisible(bool, false);
        if (!paramMessageObject.isVideo()) {
          break label263;
        }
        localPhotoVideoView.videoInfoContainer.setVisibility(0);
        int i = 0;
        paramInt2 = 0;
        label97:
        paramInt1 = i;
        if (paramInt2 < paramMessageObject.getDocument().attributes.size())
        {
          localObject = (TLRPC.DocumentAttribute)paramMessageObject.getDocument().attributes.get(paramInt2);
          if (!(localObject instanceof TLRPC.TL_documentAttributeVideo)) {
            break label243;
          }
          paramInt1 = ((TLRPC.DocumentAttribute)localObject).duration;
        }
        paramInt2 = paramInt1 / 60;
        localPhotoVideoView.videoTextView.setText(String.format("%d:%02d", new Object[] { Integer.valueOf(paramInt2), Integer.valueOf(paramInt1 - paramInt2 * 60) }));
        if (paramMessageObject.getDocument().thumb == null) {
          break label249;
        }
        paramMessageObject = paramMessageObject.getDocument().thumb.location;
        localPhotoVideoView.imageView.setImage(null, null, null, ApplicationLoader.applicationContext.getResources().getDrawable(NUM), null, paramMessageObject, "b", null, 0);
      }
    }
    for (;;)
    {
      return;
      bool = false;
      break;
      label243:
      paramInt2++;
      break label97;
      label249:
      localPhotoVideoView.imageView.setImageResource(NUM);
      continue;
      label263:
      if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) && (paramMessageObject.messageOwner.media.photo != null) && (!paramMessageObject.photoThumbs.isEmpty()))
      {
        localPhotoVideoView.videoInfoContainer.setVisibility(4);
        paramMessageObject = FileLoader.getClosestPhotoSizeWithSize(paramMessageObject.photoThumbs, 80);
        localPhotoVideoView.imageView.setImage(null, null, null, ApplicationLoader.applicationContext.getResources().getDrawable(NUM), null, paramMessageObject.location, "b", null, 0);
      }
      else
      {
        localPhotoVideoView.videoInfoContainer.setVisibility(4);
        localPhotoVideoView.imageView.setImageResource(NUM);
        continue;
        this.photoVideoViews[paramInt1].clearAnimation();
        this.photoVideoViews[paramInt1].setVisibility(4);
        this.messageObjects[paramInt1] = null;
      }
    }
  }
  
  public void setItemsCount(int paramInt)
  {
    int i = 0;
    if (i < this.photoVideoViews.length)
    {
      this.photoVideoViews[i].clearAnimation();
      PhotoVideoView localPhotoVideoView = this.photoVideoViews[i];
      if (i < paramInt) {}
      for (int j = 0;; j = 4)
      {
        localPhotoVideoView.setVisibility(j);
        i++;
        break;
      }
    }
    this.itemsCount = paramInt;
  }
  
  public void updateCheckboxColor()
  {
    for (int i = 0; i < 6; i++) {
      this.photoVideoViews[i].checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
    }
  }
  
  private class PhotoVideoView
    extends FrameLayout
  {
    private AnimatorSet animator;
    private CheckBox checkBox;
    private FrameLayout container;
    private BackupImageView imageView;
    private View selector;
    private FrameLayout videoInfoContainer;
    private TextView videoTextView;
    
    public PhotoVideoView(Context paramContext)
    {
      super();
      this.container = new FrameLayout(paramContext);
      addView(this.container, LayoutHelper.createFrame(-1, -1.0F));
      this.imageView = new BackupImageView(paramContext);
      this.imageView.getImageReceiver().setNeedsQualityThumb(true);
      this.imageView.getImageReceiver().setShouldGenerateQualityThumb(true);
      this.container.addView(this.imageView, LayoutHelper.createFrame(-1, -1.0F));
      this.videoInfoContainer = new FrameLayout(paramContext);
      this.videoInfoContainer.setBackgroundResource(NUM);
      this.videoInfoContainer.setPadding(AndroidUtilities.dp(3.0F), 0, AndroidUtilities.dp(3.0F), 0);
      this.container.addView(this.videoInfoContainer, LayoutHelper.createFrame(-1, 16, 83));
      this$1 = new ImageView(paramContext);
      SharedPhotoVideoCell.this.setImageResource(NUM);
      this.videoInfoContainer.addView(SharedPhotoVideoCell.this, LayoutHelper.createFrame(-2, -2, 19));
      this.videoTextView = new TextView(paramContext);
      this.videoTextView.setTextColor(-1);
      this.videoTextView.setTextSize(1, 12.0F);
      this.videoInfoContainer.addView(this.videoTextView, LayoutHelper.createFrame(-2, -2.0F, 19, 18.0F, -0.7F, 0.0F, 0.0F));
      this.selector = new View(paramContext);
      this.selector.setBackgroundDrawable(Theme.getSelectorDrawable(false));
      addView(this.selector, LayoutHelper.createFrame(-1, -1.0F));
      this.checkBox = new CheckBox(paramContext, NUM);
      this.checkBox.setVisibility(4);
      this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
      addView(this.checkBox, LayoutHelper.createFrame(22, 22.0F, 53, 0.0F, 2.0F, 2.0F, 0.0F));
    }
    
    public void clearAnimation()
    {
      super.clearAnimation();
      if (this.animator != null)
      {
        this.animator.cancel();
        this.animator = null;
      }
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      if (Build.VERSION.SDK_INT >= 21) {
        this.selector.drawableHotspotChanged(paramMotionEvent.getX(), paramMotionEvent.getY());
      }
      return super.onTouchEvent(paramMotionEvent);
    }
    
    public void setChecked(final boolean paramBoolean1, boolean paramBoolean2)
    {
      int i = -657931;
      float f1 = 0.85F;
      if (this.checkBox.getVisibility() != 0) {
        this.checkBox.setVisibility(0);
      }
      this.checkBox.setChecked(paramBoolean1, paramBoolean2);
      if (this.animator != null)
      {
        this.animator.cancel();
        this.animator = null;
      }
      Object localObject;
      float f2;
      if (paramBoolean2)
      {
        if (paramBoolean1) {
          setBackgroundColor(-657931);
        }
        this.animator = new AnimatorSet();
        localObject = this.animator;
        FrameLayout localFrameLayout = this.container;
        ObjectAnimator localObjectAnimator;
        if (paramBoolean1)
        {
          f2 = 0.85F;
          localObjectAnimator = ObjectAnimator.ofFloat(localFrameLayout, "scaleX", new float[] { f2 });
          localFrameLayout = this.container;
          if (!paramBoolean1) {
            break label198;
          }
        }
        for (;;)
        {
          ((AnimatorSet)localObject).playTogether(new Animator[] { localObjectAnimator, ObjectAnimator.ofFloat(localFrameLayout, "scaleY", new float[] { f1 }) });
          this.animator.setDuration(200L);
          this.animator.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationCancel(Animator paramAnonymousAnimator)
            {
              if ((SharedPhotoVideoCell.PhotoVideoView.this.animator != null) && (SharedPhotoVideoCell.PhotoVideoView.this.animator.equals(paramAnonymousAnimator))) {
                SharedPhotoVideoCell.PhotoVideoView.access$002(SharedPhotoVideoCell.PhotoVideoView.this, null);
              }
            }
            
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if ((SharedPhotoVideoCell.PhotoVideoView.this.animator != null) && (SharedPhotoVideoCell.PhotoVideoView.this.animator.equals(paramAnonymousAnimator)))
              {
                SharedPhotoVideoCell.PhotoVideoView.access$002(SharedPhotoVideoCell.PhotoVideoView.this, null);
                if (!paramBoolean1) {
                  SharedPhotoVideoCell.PhotoVideoView.this.setBackgroundColor(0);
                }
              }
            }
          });
          this.animator.start();
          return;
          f2 = 1.0F;
          break;
          label198:
          f1 = 1.0F;
        }
      }
      if (paramBoolean1)
      {
        label208:
        setBackgroundColor(i);
        localObject = this.container;
        if (!paramBoolean1) {
          break label259;
        }
        f2 = 0.85F;
        label227:
        ((FrameLayout)localObject).setScaleX(f2);
        localObject = this.container;
        if (!paramBoolean1) {
          break label265;
        }
      }
      for (;;)
      {
        ((FrameLayout)localObject).setScaleY(f1);
        break;
        i = 0;
        break label208;
        label259:
        f2 = 1.0F;
        break label227;
        label265:
        f1 = 1.0F;
      }
    }
  }
  
  public static abstract interface SharedPhotoVideoCellDelegate
  {
    public abstract void didClickItem(SharedPhotoVideoCell paramSharedPhotoVideoCell, int paramInt1, MessageObject paramMessageObject, int paramInt2);
    
    public abstract boolean didLongClickItem(SharedPhotoVideoCell paramSharedPhotoVideoCell, int paramInt1, MessageObject paramMessageObject, int paramInt2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/SharedPhotoVideoCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */