package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_messageEntityEmail;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.LinkPath;

public class SharedLinkCell
  extends FrameLayout
{
  private static TextPaint descriptionTextPaint;
  private static Paint paint;
  private static TextPaint titleTextPaint;
  private static Paint urlPaint;
  private CheckBox checkBox;
  private SharedLinkCellDelegate delegate;
  private int description2Y = AndroidUtilities.dp(27.0F);
  private StaticLayout descriptionLayout;
  private StaticLayout descriptionLayout2;
  private int descriptionY = AndroidUtilities.dp(27.0F);
  private boolean drawLinkImageView;
  private LetterDrawable letterDrawable;
  private ImageReceiver linkImageView;
  private ArrayList<StaticLayout> linkLayout = new ArrayList();
  private boolean linkPreviewPressed;
  private int linkY;
  ArrayList<String> links = new ArrayList();
  private MessageObject message;
  private boolean needDivider;
  private int pressedLink;
  private StaticLayout titleLayout;
  private int titleY = AndroidUtilities.dp(7.0F);
  private LinkPath urlPath = new LinkPath();
  
  public SharedLinkCell(Context paramContext)
  {
    super(paramContext);
    if (titleTextPaint == null)
    {
      titleTextPaint = new TextPaint(1);
      titleTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      titleTextPaint.setColor(-14606047);
      descriptionTextPaint = new TextPaint(1);
      paint = new Paint();
      paint.setColor(-2500135);
      paint.setStrokeWidth(1.0F);
      urlPaint = new Paint();
      urlPaint.setColor(862104035);
    }
    titleTextPaint.setTextSize(AndroidUtilities.dp(16.0F));
    descriptionTextPaint.setTextSize(AndroidUtilities.dp(16.0F));
    setWillNotDraw(false);
    this.linkImageView = new ImageReceiver(this);
    this.letterDrawable = new LetterDrawable();
    this.checkBox = new CheckBox(paramContext, 2130837959);
    this.checkBox.setVisibility(4);
    paramContext = this.checkBox;
    int i;
    float f1;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label290;
      }
      f1 = 0.0F;
      label252:
      if (!LocaleController.isRTL) {
        break label296;
      }
    }
    label290:
    label296:
    for (float f2 = 44.0F;; f2 = 0.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f1, 44.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      f1 = 44.0F;
      break label252;
    }
  }
  
  public String getLink(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.links.size())) {
      return null;
    }
    return (String)this.links.get(paramInt);
  }
  
  public MessageObject getMessage()
  {
    return this.message;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.drawLinkImageView) {
      this.linkImageView.onAttachedToWindow();
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.drawLinkImageView) {
      this.linkImageView.onDetachedFromWindow();
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    label76:
    label131:
    int j;
    int i;
    label180:
    StaticLayout localStaticLayout;
    int k;
    if (this.titleLayout != null)
    {
      paramCanvas.save();
      if (LocaleController.isRTL)
      {
        f = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f), this.titleY);
        this.titleLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
    }
    else
    {
      if (this.descriptionLayout != null)
      {
        descriptionTextPaint.setColor(-14606047);
        paramCanvas.save();
        if (!LocaleController.isRTL) {
          break label312;
        }
        f = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f), this.descriptionY);
        this.descriptionLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.descriptionLayout2 != null)
      {
        descriptionTextPaint.setColor(-14606047);
        paramCanvas.save();
        if (!LocaleController.isRTL) {
          break label320;
        }
        f = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f), this.description2Y);
        this.descriptionLayout2.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.linkLayout.isEmpty()) {
        break label336;
      }
      descriptionTextPaint.setColor(-14255946);
      j = 0;
      i = 0;
      if (i >= this.linkLayout.size()) {
        break label336;
      }
      localStaticLayout = (StaticLayout)this.linkLayout.get(i);
      k = j;
      if (localStaticLayout.getLineCount() > 0)
      {
        paramCanvas.save();
        if (!LocaleController.isRTL) {
          break label328;
        }
      }
    }
    label312:
    label320:
    label328:
    for (float f = 8.0F;; f = AndroidUtilities.leftBaseline)
    {
      paramCanvas.translate(AndroidUtilities.dp(f), this.linkY + j);
      if (this.pressedLink == i) {
        paramCanvas.drawPath(this.urlPath, urlPaint);
      }
      localStaticLayout.draw(paramCanvas);
      paramCanvas.restore();
      k = j + localStaticLayout.getLineBottom(localStaticLayout.getLineCount() - 1);
      i += 1;
      j = k;
      break label180;
      f = AndroidUtilities.leftBaseline;
      break;
      f = AndroidUtilities.leftBaseline;
      break label76;
      f = AndroidUtilities.leftBaseline;
      break label131;
    }
    label336:
    this.letterDrawable.draw(paramCanvas);
    if (this.drawLinkImageView) {
      this.linkImageView.draw(paramCanvas);
    }
    if (this.needDivider)
    {
      if (LocaleController.isRTL) {
        paramCanvas.drawLine(0.0F, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, paint);
      }
    }
    else {
      return;
    }
    paramCanvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, paint);
  }
  
  @SuppressLint({"DrawAllocation"})
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    this.drawLinkImageView = false;
    this.descriptionLayout = null;
    this.titleLayout = null;
    this.descriptionLayout2 = null;
    this.description2Y = this.descriptionY;
    this.linkLayout.clear();
    this.links.clear();
    int i = View.MeasureSpec.getSize(paramInt1) - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - AndroidUtilities.dp(8.0F);
    Object localObject6 = null;
    Object localObject7 = null;
    Object localObject9 = null;
    Object localObject1 = null;
    Object localObject8 = null;
    paramInt2 = 0;
    Object localObject3 = localObject7;
    int j = paramInt2;
    Object localObject5 = localObject6;
    Object localObject12 = localObject8;
    if ((this.message.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))
    {
      localObject3 = localObject7;
      j = paramInt2;
      localObject5 = localObject6;
      localObject12 = localObject8;
      if ((this.message.messageOwner.media.webpage instanceof TLRPC.TL_webPage))
      {
        localObject6 = this.message.messageOwner.media.webpage;
        if ((this.message.photoThumbs == null) && (((TLRPC.WebPage)localObject6).photo != null)) {
          this.message.generateThumbs(true);
        }
        if ((((TLRPC.WebPage)localObject6).photo == null) || (this.message.photoThumbs == null)) {
          break label433;
        }
        paramInt2 = 1;
        localObject3 = ((TLRPC.WebPage)localObject6).title;
        localObject5 = localObject3;
        if (localObject3 == null) {
          localObject5 = ((TLRPC.WebPage)localObject6).site_name;
        }
        localObject3 = ((TLRPC.WebPage)localObject6).description;
        localObject12 = ((TLRPC.WebPage)localObject6).url;
        j = paramInt2;
      }
    }
    localObject7 = localObject3;
    localObject6 = localObject9;
    localObject8 = localObject5;
    label293:
    label433:
    label438:
    label577:
    int k;
    if (this.message != null)
    {
      localObject7 = localObject3;
      localObject6 = localObject9;
      localObject8 = localObject5;
      if (!this.message.messageOwner.entities.isEmpty())
      {
        paramInt2 = 0;
        localObject7 = localObject3;
        localObject6 = localObject1;
        localObject8 = localObject5;
        if (paramInt2 < this.message.messageOwner.entities.size())
        {
          TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)this.message.messageOwner.entities.get(paramInt2);
          Object localObject10 = localObject3;
          Object localObject13 = localObject1;
          localObject6 = localObject5;
          if (localMessageEntity.length > 0)
          {
            localObject10 = localObject3;
            localObject13 = localObject1;
            localObject6 = localObject5;
            if (localMessageEntity.offset >= 0)
            {
              if (localMessageEntity.offset < this.message.messageOwner.message.length()) {
                break label438;
              }
              localObject6 = localObject5;
              localObject13 = localObject1;
              localObject10 = localObject3;
            }
          }
          for (;;)
          {
            paramInt2 += 1;
            localObject3 = localObject10;
            localObject1 = localObject13;
            localObject5 = localObject6;
            break label293;
            paramInt2 = 0;
            break;
            if (localMessageEntity.offset + localMessageEntity.length > this.message.messageOwner.message.length()) {
              localMessageEntity.length = (this.message.messageOwner.message.length() - localMessageEntity.offset);
            }
            localObject9 = localObject1;
            if (paramInt2 == 0)
            {
              localObject9 = localObject1;
              if (localObject12 != null) {
                if (localMessageEntity.offset == 0)
                {
                  localObject9 = localObject1;
                  if (localMessageEntity.length == this.message.messageOwner.message.length()) {}
                }
                else
                {
                  if (this.message.messageOwner.entities.size() != 1) {
                    break label1094;
                  }
                  localObject9 = localObject1;
                  if (localObject3 == null) {
                    localObject9 = this.message.messageOwner.message;
                  }
                }
              }
            }
            localObject6 = null;
            localObject10 = localObject3;
            localObject1 = localObject5;
            Object localObject11;
            for (;;)
            {
              try
              {
                if (!(localMessageEntity instanceof TLRPC.TL_messageEntityTextUrl))
                {
                  localObject10 = localObject3;
                  localObject1 = localObject5;
                  if (!(localMessageEntity instanceof TLRPC.TL_messageEntityUrl)) {
                    break label1127;
                  }
                }
                localObject10 = localObject3;
                localObject1 = localObject5;
                if (!(localMessageEntity instanceof TLRPC.TL_messageEntityUrl)) {
                  break label1109;
                }
                localObject10 = localObject3;
                localObject1 = localObject5;
                localObject6 = this.message.messageOwner.message.substring(localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length);
                if (localObject5 != null)
                {
                  localObject8 = localObject3;
                  localObject11 = localObject6;
                  localObject7 = localObject5;
                  localObject10 = localObject3;
                  localObject1 = localObject5;
                  if (((String)localObject5).length() != 0) {}
                }
                else
                {
                  localObject5 = localObject6;
                  localObject10 = localObject3;
                  localObject1 = localObject5;
                  localObject5 = Uri.parse((String)localObject5).getHost();
                  localObject7 = localObject5;
                  if (localObject5 == null) {
                    localObject7 = localObject6;
                  }
                  localObject5 = localObject7;
                  if (localObject7 != null)
                  {
                    localObject10 = localObject3;
                    localObject1 = localObject7;
                    k = ((String)localObject7).lastIndexOf('.');
                    localObject5 = localObject7;
                    if (k >= 0)
                    {
                      localObject10 = localObject3;
                      localObject1 = localObject7;
                      localObject7 = ((String)localObject7).substring(0, k);
                      localObject10 = localObject3;
                      localObject1 = localObject7;
                      k = ((String)localObject7).lastIndexOf('.');
                      localObject5 = localObject7;
                      if (k >= 0)
                      {
                        localObject10 = localObject3;
                        localObject1 = localObject7;
                        localObject5 = ((String)localObject7).substring(k + 1);
                      }
                      localObject10 = localObject3;
                      localObject1 = localObject5;
                      localObject5 = ((String)localObject5).substring(0, 1).toUpperCase() + ((String)localObject5).substring(1);
                    }
                  }
                  localObject10 = localObject3;
                  localObject1 = localObject5;
                  if (localMessageEntity.offset == 0)
                  {
                    localObject8 = localObject3;
                    localObject11 = localObject6;
                    localObject7 = localObject5;
                    localObject10 = localObject3;
                    localObject1 = localObject5;
                    if (localMessageEntity.length == this.message.messageOwner.message.length()) {}
                  }
                  else
                  {
                    localObject10 = localObject3;
                    localObject1 = localObject5;
                    localObject8 = this.message.messageOwner.message;
                    localObject7 = localObject5;
                    localObject11 = localObject6;
                  }
                }
                localObject10 = localObject8;
                localObject13 = localObject9;
                localObject6 = localObject7;
                if (localObject11 == null) {
                  break;
                }
                localObject10 = localObject8;
                localObject1 = localObject7;
                if (((String)localObject11).toLowerCase().indexOf("http") == 0) {
                  break label1373;
                }
                localObject10 = localObject8;
                localObject1 = localObject7;
                if (((String)localObject11).toLowerCase().indexOf("mailto") == 0) {
                  break label1373;
                }
                localObject10 = localObject8;
                localObject1 = localObject7;
                this.links.add("http://" + (String)localObject11);
                localObject10 = localObject8;
                localObject13 = localObject9;
                localObject6 = localObject7;
              }
              catch (Exception localException5)
              {
                FileLog.e("tmessages", localException5);
                localObject13 = localObject9;
                localObject6 = localObject1;
              }
              break;
              label1094:
              localObject9 = this.message.messageOwner.message;
              break label577;
              label1109:
              localObject10 = localException5;
              localObject1 = localObject5;
              localObject6 = localMessageEntity.url;
              continue;
              label1127:
              localObject8 = localException5;
              localObject11 = localObject6;
              localObject7 = localObject5;
              localObject10 = localException5;
              localObject1 = localObject5;
              if ((localMessageEntity instanceof TLRPC.TL_messageEntityEmail)) {
                if (localObject5 != null)
                {
                  localObject8 = localException5;
                  localObject11 = localObject6;
                  localObject7 = localObject5;
                  localObject10 = localException5;
                  localObject1 = localObject5;
                  if (((String)localObject5).length() != 0) {}
                }
                else
                {
                  localObject10 = localException5;
                  localObject1 = localObject5;
                  localObject6 = "mailto:" + this.message.messageOwner.message.substring(localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length);
                  localObject10 = localException5;
                  localObject1 = localObject5;
                  localObject5 = this.message.messageOwner.message.substring(localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length);
                  localObject10 = localException5;
                  localObject1 = localObject5;
                  if (localMessageEntity.offset == 0)
                  {
                    localObject8 = localException5;
                    localObject11 = localObject6;
                    localObject7 = localObject5;
                    localObject10 = localException5;
                    localObject1 = localObject5;
                    if (localMessageEntity.length == this.message.messageOwner.message.length()) {}
                  }
                  else
                  {
                    localObject10 = localException5;
                    localObject1 = localObject5;
                    localObject8 = this.message.messageOwner.message;
                    localObject11 = localObject6;
                    localObject7 = localObject5;
                  }
                }
              }
            }
            label1373:
            localObject10 = localObject8;
            localObject1 = localObject7;
            this.links.add(localObject11);
            localObject10 = localObject8;
            localObject13 = localObject9;
            localObject6 = localObject7;
          }
        }
      }
    }
    if ((localObject12 != null) && (this.links.isEmpty())) {
      this.links.add(localObject12);
    }
    if (localObject8 != null) {}
    try
    {
      paramInt2 = (int)Math.ceil(titleTextPaint.measureText((String)localObject8));
      this.titleLayout = new StaticLayout(TextUtils.ellipsize(((String)localObject8).replace('\n', ' '), titleTextPaint, Math.min(paramInt2, i), TextUtils.TruncateAt.END), titleTextPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      this.letterDrawable.setTitle((String)localObject8);
      if (localObject7 == null) {}
    }
    catch (Exception localException2)
    {
      try
      {
        this.descriptionLayout = ChatMessageCell.generateStaticLayout((CharSequence)localObject7, descriptionTextPaint, i, i, 0, 3);
        if (this.descriptionLayout.getLineCount() > 0) {
          this.description2Y = (this.descriptionY + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1) + AndroidUtilities.dp(1.0F));
        }
        if (localObject6 == null) {}
      }
      catch (Exception localException2)
      {
        try
        {
          for (;;)
          {
            this.descriptionLayout2 = ChatMessageCell.generateStaticLayout((CharSequence)localObject6, descriptionTextPaint, i, i, 0, 3);
            this.descriptionLayout2.getLineBottom(this.descriptionLayout2.getLineCount() - 1);
            if (this.descriptionLayout != null) {
              this.description2Y += AndroidUtilities.dp(10.0F);
            }
            if (this.links.isEmpty()) {
              break label1844;
            }
            paramInt2 = 0;
            for (;;)
            {
              if (paramInt2 >= this.links.size()) {
                break label1844;
              }
              try
              {
                localObject1 = (String)this.links.get(paramInt2);
                k = (int)Math.ceil(descriptionTextPaint.measureText((String)localObject1));
                localObject1 = new StaticLayout(TextUtils.ellipsize(((String)localObject1).replace('\n', ' '), descriptionTextPaint, Math.min(k, i), TextUtils.TruncateAt.MIDDLE), descriptionTextPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
                this.linkY = this.description2Y;
                if ((this.descriptionLayout2 != null) && (this.descriptionLayout2.getLineCount() != 0)) {
                  this.linkY += this.descriptionLayout2.getLineBottom(this.descriptionLayout2.getLineCount() - 1) + AndroidUtilities.dp(1.0F);
                }
                this.linkLayout.add(localObject1);
              }
              catch (Exception localException4)
              {
                for (;;)
                {
                  FileLog.e("tmessages", localException4);
                }
              }
              paramInt2 += 1;
            }
            localException1 = localException1;
            FileLog.e("tmessages", localException1);
          }
          localException2 = localException2;
          FileLog.e("tmessages", localException2);
        }
        catch (Exception localException3)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException3);
          }
          label1844:
          i = AndroidUtilities.dp(52.0F);
          Object localObject2;
          if (LocaleController.isRTL)
          {
            paramInt2 = View.MeasureSpec.getSize(paramInt1) - AndroidUtilities.dp(10.0F) - i;
            this.letterDrawable.setBounds(paramInt2, AndroidUtilities.dp(10.0F), paramInt2 + i, AndroidUtilities.dp(62.0F));
            if (j != 0)
            {
              localObject6 = FileLoader.getClosestPhotoSizeWithSize(this.message.photoThumbs, i, true);
              Object localObject4 = FileLoader.getClosestPhotoSizeWithSize(this.message.photoThumbs, 80);
              localObject2 = localObject4;
              if (localObject4 == localObject6) {
                localObject2 = null;
              }
              ((TLRPC.PhotoSize)localObject6).size = -1;
              if (localObject2 != null) {
                ((TLRPC.PhotoSize)localObject2).size = -1;
              }
              this.linkImageView.setImageCoords(paramInt2, AndroidUtilities.dp(10.0F), i, i);
              localObject5 = FileLoader.getAttachFileName((TLObject)localObject6);
              paramInt2 = 1;
              if (!FileLoader.getPathToAttach((TLObject)localObject6, true).exists()) {
                paramInt2 = 0;
              }
              localObject4 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(i), Integer.valueOf(i) });
              if ((paramInt2 == 0) && (!MediaController.getInstance().canDownloadMedia(1)) && (!FileLoader.getInstance().isLoadingFile((String)localObject5))) {
                break label2341;
              }
              localObject5 = this.linkImageView;
              localObject6 = ((TLRPC.PhotoSize)localObject6).location;
              if (localObject2 == null) {
                break label2335;
              }
              localObject2 = ((TLRPC.PhotoSize)localObject2).location;
              label2076:
              ((ImageReceiver)localObject5).setImage((TLObject)localObject6, (String)localObject4, (TLRPC.FileLocation)localObject2, String.format(Locale.US, "%d_%d_b", new Object[] { Integer.valueOf(i), Integer.valueOf(i) }), 0, null, false);
            }
          }
          for (;;)
          {
            this.drawLinkImageView = true;
            i = 0;
            paramInt2 = i;
            if (this.titleLayout != null)
            {
              paramInt2 = i;
              if (this.titleLayout.getLineCount() != 0) {
                paramInt2 = 0 + this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
              }
            }
            i = paramInt2;
            if (this.descriptionLayout != null)
            {
              i = paramInt2;
              if (this.descriptionLayout.getLineCount() != 0) {
                i = paramInt2 + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
              }
            }
            paramInt2 = i;
            if (this.descriptionLayout2 != null)
            {
              paramInt2 = i;
              if (this.descriptionLayout2.getLineCount() != 0)
              {
                i += this.descriptionLayout2.getLineBottom(this.descriptionLayout2.getLineCount() - 1);
                paramInt2 = i;
                if (this.descriptionLayout != null) {
                  paramInt2 = i + AndroidUtilities.dp(10.0F);
                }
              }
            }
            i = 0;
            while (i < this.linkLayout.size())
            {
              localObject2 = (StaticLayout)this.linkLayout.get(i);
              k = paramInt2;
              if (((StaticLayout)localObject2).getLineCount() > 0) {
                k = paramInt2 + ((StaticLayout)localObject2).getLineBottom(((StaticLayout)localObject2).getLineCount() - 1);
              }
              i += 1;
              paramInt2 = k;
            }
            paramInt2 = AndroidUtilities.dp(10.0F);
            break;
            label2335:
            localObject2 = null;
            break label2076;
            label2341:
            if (localObject2 != null) {
              this.linkImageView.setImage(null, null, ((TLRPC.PhotoSize)localObject2).location, String.format(Locale.US, "%d_%d_b", new Object[] { Integer.valueOf(i), Integer.valueOf(i) }), 0, null, false);
            } else {
              this.linkImageView.setImageBitmap((Drawable)null);
            }
          }
          i = paramInt2;
          if (j != 0) {
            i = Math.max(AndroidUtilities.dp(48.0F), paramInt2);
          }
          this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0F), 1073741824));
          paramInt2 = View.MeasureSpec.getSize(paramInt1);
          i = Math.max(AndroidUtilities.dp(72.0F), AndroidUtilities.dp(16.0F) + i);
          if (!this.needDivider) {}
        }
      }
    }
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      setMeasuredDimension(paramInt2, paramInt1 + i);
      return;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = 0;
    int n = 0;
    int k;
    int j;
    int m;
    StaticLayout localStaticLayout;
    float f;
    if ((this.message != null) && (!this.linkLayout.isEmpty()) && (this.delegate != null) && (this.delegate.canPerformActions())) {
      if ((paramMotionEvent.getAction() == 0) || ((this.linkPreviewPressed) && (paramMotionEvent.getAction() == 1)))
      {
        int i2 = (int)paramMotionEvent.getX();
        int i3 = (int)paramMotionEvent.getY();
        k = 0;
        int i1 = 0;
        j = 0;
        m = i1;
        i = n;
        if (j < this.linkLayout.size())
        {
          localStaticLayout = (StaticLayout)this.linkLayout.get(j);
          i = k;
          if (localStaticLayout.getLineCount() <= 0) {
            break label484;
          }
          i = localStaticLayout.getLineBottom(localStaticLayout.getLineCount() - 1);
          if (!LocaleController.isRTL) {
            break label310;
          }
          f = 8.0F;
          m = AndroidUtilities.dp(f);
          if ((i2 < m + localStaticLayout.getLineLeft(0)) || (i2 > m + localStaticLayout.getLineWidth(0)) || (i3 < this.linkY + k) || (i3 > this.linkY + k + i)) {
            break label479;
          }
          k = 1;
          if (paramMotionEvent.getAction() != 0) {
            break label331;
          }
          resetPressedLink();
          this.pressedLink = j;
          this.linkPreviewPressed = true;
        }
      }
    }
    for (;;)
    {
      try
      {
        this.urlPath.setCurrentLayout(localStaticLayout, 0, 0.0F);
        localStaticLayout.getSelectionPath(0, localStaticLayout.getText().length(), this.urlPath);
        i = 1;
        m = k;
        j = i;
        if (m == 0)
        {
          resetPressedLink();
          j = i;
        }
        if ((j == 0) && (!super.onTouchEvent(paramMotionEvent))) {
          break label527;
        }
        return true;
        label310:
        f = AndroidUtilities.leftBaseline;
      }
      catch (Exception localException1)
      {
        FileLog.e("tmessages", localException1);
        continue;
      }
      label331:
      m = k;
      i = n;
      if (this.linkPreviewPressed)
      {
        for (;;)
        {
          try
          {
            if ((this.pressedLink != 0) || (this.message.messageOwner.media == null)) {
              continue;
            }
            localWebPage = this.message.messageOwner.media.webpage;
            if ((localWebPage == null) || (Build.VERSION.SDK_INT < 16) || (localWebPage.embed_url == null) || (localWebPage.embed_url.length() == 0)) {
              continue;
            }
            this.delegate.needOpenWebView(localWebPage);
          }
          catch (Exception localException2)
          {
            TLRPC.WebPage localWebPage;
            FileLog.e("tmessages", localException2);
            continue;
          }
          resetPressedLink();
          i = 1;
          m = k;
          break;
          localWebPage = null;
          continue;
          Browser.openUrl(getContext(), (String)this.links.get(this.pressedLink));
        }
        label479:
        i = k + i;
        label484:
        j += 1;
        k = i;
        break;
        j = i;
        if (paramMotionEvent.getAction() == 3)
        {
          resetPressedLink();
          j = i;
          continue;
          resetPressedLink();
          j = i;
        }
      }
    }
    label527:
    return false;
  }
  
  protected void resetPressedLink()
  {
    this.pressedLink = -1;
    this.linkPreviewPressed = false;
    invalidate();
  }
  
  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.checkBox.getVisibility() != 0) {
      this.checkBox.setVisibility(0);
    }
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
  }
  
  public void setDelegate(SharedLinkCellDelegate paramSharedLinkCellDelegate)
  {
    this.delegate = paramSharedLinkCellDelegate;
  }
  
  public void setLink(MessageObject paramMessageObject, boolean paramBoolean)
  {
    this.needDivider = paramBoolean;
    resetPressedLink();
    this.message = paramMessageObject;
    requestLayout();
  }
  
  public static abstract interface SharedLinkCellDelegate
  {
    public abstract boolean canPerformActions();
    
    public abstract void needOpenWebView(TLRPC.WebPage paramWebPage);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/SharedLinkCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */