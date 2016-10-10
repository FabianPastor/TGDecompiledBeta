package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.AudioEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class AudioCell
  extends FrameLayout
{
  private static Paint paint;
  private MediaController.AudioEntry audioEntry;
  private TextView authorTextView;
  private CheckBox checkBox;
  private AudioCellDelegate delegate;
  private TextView genreTextView;
  private boolean needDivider;
  private ImageView playButton;
  private TextView timeTextView;
  private TextView titleTextView;
  
  public AudioCell(Context paramContext)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint();
      paint.setColor(-2500135);
      paint.setStrokeWidth(1.0F);
    }
    this.playButton = new ImageView(paramContext);
    this.playButton.setScaleType(ImageView.ScaleType.CENTER);
    Object localObject = this.playButton;
    int i;
    float f1;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label815;
      }
      f1 = 0.0F;
      label84:
      if (!LocaleController.isRTL) {
        break label821;
      }
      f2 = 13.0F;
      label93:
      addView((View)localObject, LayoutHelper.createFrame(46, 46.0F, i | 0x30, f1, 13.0F, f2, 0.0F));
      this.playButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (AudioCell.this.audioEntry != null)
          {
            if ((!MediaController.getInstance().isPlayingAudio(AudioCell.this.audioEntry.messageObject)) || (MediaController.getInstance().isAudioPaused())) {
              break label68;
            }
            MediaController.getInstance().pauseAudio(AudioCell.this.audioEntry.messageObject);
            AudioCell.this.playButton.setImageResource(2130837544);
          }
          label68:
          do
          {
            do
            {
              return;
              paramAnonymousView = new ArrayList();
              paramAnonymousView.add(AudioCell.this.audioEntry.messageObject);
            } while (!MediaController.getInstance().setPlaylist(paramAnonymousView, AudioCell.this.audioEntry.messageObject));
            AudioCell.this.playButton.setImageResource(2130837543);
          } while (AudioCell.this.delegate == null);
          AudioCell.this.delegate.startedPlayingAudio(AudioCell.this.audioEntry.messageObject);
        }
      });
      this.titleTextView = new TextView(paramContext);
      this.titleTextView.setTextColor(-14606047);
      this.titleTextView.setTextSize(1, 16.0F);
      this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.titleTextView.setLines(1);
      this.titleTextView.setMaxLines(1);
      this.titleTextView.setSingleLine(true);
      this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.titleTextView;
      if (!LocaleController.isRTL) {
        break label826;
      }
      i = 5;
      label223:
      ((TextView)localObject).setGravity(i | 0x30);
      localObject = this.titleTextView;
      if (!LocaleController.isRTL) {
        break label832;
      }
      i = 5;
      label248:
      if (!LocaleController.isRTL) {
        break label838;
      }
      f1 = 50.0F;
      label257:
      if (!LocaleController.isRTL) {
        break label844;
      }
      f2 = 72.0F;
      label266:
      addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 7.0F, f2, 0.0F));
      this.genreTextView = new TextView(paramContext);
      this.genreTextView.setTextColor(-7697782);
      this.genreTextView.setTextSize(1, 14.0F);
      this.genreTextView.setLines(1);
      this.genreTextView.setMaxLines(1);
      this.genreTextView.setSingleLine(true);
      this.genreTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.genreTextView;
      if (!LocaleController.isRTL) {
        break label850;
      }
      i = 5;
      label368:
      ((TextView)localObject).setGravity(i | 0x30);
      localObject = this.genreTextView;
      if (!LocaleController.isRTL) {
        break label856;
      }
      i = 5;
      label393:
      if (!LocaleController.isRTL) {
        break label862;
      }
      f1 = 50.0F;
      label402:
      if (!LocaleController.isRTL) {
        break label868;
      }
      f2 = 72.0F;
      label411:
      addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 28.0F, f2, 0.0F));
      this.authorTextView = new TextView(paramContext);
      this.authorTextView.setTextColor(-7697782);
      this.authorTextView.setTextSize(1, 14.0F);
      this.authorTextView.setLines(1);
      this.authorTextView.setMaxLines(1);
      this.authorTextView.setSingleLine(true);
      this.authorTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.authorTextView;
      if (!LocaleController.isRTL) {
        break label874;
      }
      i = 5;
      label513:
      ((TextView)localObject).setGravity(i | 0x30);
      localObject = this.authorTextView;
      if (!LocaleController.isRTL) {
        break label880;
      }
      i = 5;
      label538:
      if (!LocaleController.isRTL) {
        break label886;
      }
      f1 = 50.0F;
      label547:
      if (!LocaleController.isRTL) {
        break label892;
      }
      f2 = 72.0F;
      label556:
      addView((View)localObject, LayoutHelper.createFrame(-1, -2.0F, i | 0x30, f1, 44.0F, f2, 0.0F));
      this.timeTextView = new TextView(paramContext);
      this.timeTextView.setTextColor(-6710887);
      this.timeTextView.setTextSize(1, 13.0F);
      this.timeTextView.setLines(1);
      this.timeTextView.setMaxLines(1);
      this.timeTextView.setSingleLine(true);
      this.timeTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.timeTextView;
      if (!LocaleController.isRTL) {
        break label898;
      }
      i = 3;
      label658:
      ((TextView)localObject).setGravity(i | 0x30);
      localObject = this.timeTextView;
      if (!LocaleController.isRTL) {
        break label904;
      }
      i = 3;
      label683:
      if (!LocaleController.isRTL) {
        break label910;
      }
      f1 = 18.0F;
      label692:
      if (!LocaleController.isRTL) {
        break label915;
      }
      f2 = 0.0F;
      label700:
      addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 11.0F, f2, 0.0F));
      this.checkBox = new CheckBox(paramContext, 2130837959);
      this.checkBox.setVisibility(0);
      this.checkBox.setColor(-14043401);
      paramContext = this.checkBox;
      if (!LocaleController.isRTL) {
        break label921;
      }
      i = j;
      label769:
      if (!LocaleController.isRTL) {
        break label927;
      }
      f1 = 18.0F;
      label778:
      if (!LocaleController.isRTL) {
        break label932;
      }
    }
    label815:
    label821:
    label826:
    label832:
    label838:
    label844:
    label850:
    label856:
    label862:
    label868:
    label874:
    label880:
    label886:
    label892:
    label898:
    label904:
    label910:
    label915:
    label921:
    label927:
    label932:
    for (float f2 = 0.0F;; f2 = 18.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f1, 39.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      f1 = 13.0F;
      break label84;
      f2 = 0.0F;
      break label93;
      i = 3;
      break label223;
      i = 3;
      break label248;
      f1 = 72.0F;
      break label257;
      f2 = 50.0F;
      break label266;
      i = 3;
      break label368;
      i = 3;
      break label393;
      f1 = 72.0F;
      break label402;
      f2 = 50.0F;
      break label411;
      i = 3;
      break label513;
      i = 3;
      break label538;
      f1 = 72.0F;
      break label547;
      f2 = 50.0F;
      break label556;
      i = 5;
      break label658;
      i = 5;
      break label683;
      f1 = 0.0F;
      break label692;
      f2 = 18.0F;
      break label700;
      i = 5;
      break label769;
      f1 = 0.0F;
      break label778;
    }
  }
  
  public MediaController.AudioEntry getAudioEntry()
  {
    return this.audioEntry;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(AndroidUtilities.dp(72.0F), getHeight() - 1, getWidth(), getHeight() - 1, paint);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = AndroidUtilities.dp(72.0F);
    if (this.needDivider) {}
    for (paramInt2 = 1;; paramInt2 = 0)
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(paramInt2 + i, 1073741824));
      return;
    }
  }
  
  public void setAudio(MediaController.AudioEntry paramAudioEntry, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.audioEntry = paramAudioEntry;
    this.titleTextView.setText(this.audioEntry.title);
    this.genreTextView.setText(this.audioEntry.genre);
    this.authorTextView.setText(this.audioEntry.author);
    this.timeTextView.setText(String.format("%d:%02d", new Object[] { Integer.valueOf(this.audioEntry.duration / 60), Integer.valueOf(this.audioEntry.duration % 60) }));
    paramAudioEntry = this.playButton;
    int i;
    if ((MediaController.getInstance().isPlayingAudio(this.audioEntry.messageObject)) && (!MediaController.getInstance().isAudioPaused()))
    {
      i = 2130837543;
      paramAudioEntry.setImageResource(i);
      this.needDivider = paramBoolean1;
      if (paramBoolean1) {
        break label170;
      }
    }
    label170:
    for (paramBoolean1 = true;; paramBoolean1 = false)
    {
      setWillNotDraw(paramBoolean1);
      this.checkBox.setChecked(paramBoolean2, false);
      return;
      i = 2130837544;
      break;
    }
  }
  
  public void setChecked(boolean paramBoolean)
  {
    this.checkBox.setChecked(paramBoolean, true);
  }
  
  public void setDelegate(AudioCellDelegate paramAudioCellDelegate)
  {
    this.delegate = paramAudioCellDelegate;
  }
  
  public static abstract interface AudioCellDelegate
  {
    public abstract void startedPlayingAudio(MessageObject paramMessageObject);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/AudioCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */