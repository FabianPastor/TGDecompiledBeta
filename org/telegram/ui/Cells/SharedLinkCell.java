package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.LinkPath;

public class SharedLinkCell
  extends FrameLayout
{
  private CheckBox checkBox;
  private SharedLinkCellDelegate delegate;
  private int description2Y = AndroidUtilities.dp(27.0F);
  private StaticLayout descriptionLayout;
  private StaticLayout descriptionLayout2;
  private TextPaint descriptionTextPaint;
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
  private TextPaint titleTextPaint = new TextPaint(1);
  private int titleY = AndroidUtilities.dp(7.0F);
  private LinkPath urlPath = new LinkPath();
  
  public SharedLinkCell(Context paramContext)
  {
    super(paramContext);
    this.titleTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.titleTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.descriptionTextPaint = new TextPaint(1);
    this.titleTextPaint.setTextSize(AndroidUtilities.dp(16.0F));
    this.descriptionTextPaint.setTextSize(AndroidUtilities.dp(16.0F));
    setWillNotDraw(false);
    this.linkImageView = new ImageReceiver(this);
    this.letterDrawable = new LetterDrawable();
    this.checkBox = new CheckBox(paramContext, NUM);
    this.checkBox.setVisibility(4);
    this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
    paramContext = this.checkBox;
    int i;
    float f1;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL) {
        break label266;
      }
      f1 = 0.0F;
      label228:
      if (!LocaleController.isRTL) {
        break label272;
      }
    }
    label266:
    label272:
    for (float f2 = 44.0F;; f2 = 0.0F)
    {
      addView(paramContext, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f1, 44.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      f1 = 44.0F;
      break label228;
    }
  }
  
  public String getLink(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.links.size())) {}
    for (String str = null;; str = (String)this.links.get(paramInt)) {
      return str;
    }
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
    label80:
    label139:
    int i;
    int j;
    label192:
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
        this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        paramCanvas.save();
        if (!LocaleController.isRTL) {
          break label322;
        }
        f = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f), this.descriptionY);
        this.descriptionLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.descriptionLayout2 != null)
      {
        this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        paramCanvas.save();
        if (!LocaleController.isRTL) {
          break label330;
        }
        f = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f), this.description2Y);
        this.descriptionLayout2.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.linkLayout.isEmpty()) {
        break label346;
      }
      this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkText"));
      i = 0;
      j = 0;
      if (j >= this.linkLayout.size()) {
        break label346;
      }
      localStaticLayout = (StaticLayout)this.linkLayout.get(j);
      k = i;
      if (localStaticLayout.getLineCount() > 0)
      {
        paramCanvas.save();
        if (!LocaleController.isRTL) {
          break label338;
        }
      }
    }
    label322:
    label330:
    label338:
    for (float f = 8.0F;; f = AndroidUtilities.leftBaseline)
    {
      paramCanvas.translate(AndroidUtilities.dp(f), this.linkY + i);
      if (this.pressedLink == j) {
        paramCanvas.drawPath(this.urlPath, Theme.linkSelectionPaint);
      }
      localStaticLayout.draw(paramCanvas);
      paramCanvas.restore();
      k = i + localStaticLayout.getLineBottom(localStaticLayout.getLineCount() - 1);
      j++;
      i = k;
      break label192;
      f = AndroidUtilities.leftBaseline;
      break;
      f = AndroidUtilities.leftBaseline;
      break label80;
      f = AndroidUtilities.leftBaseline;
      break label139;
    }
    label346:
    this.letterDrawable.draw(paramCanvas);
    if (this.drawLinkImageView) {
      this.linkImageView.draw(paramCanvas);
    }
    if (this.needDivider)
    {
      if (!LocaleController.isRTL) {
        break label419;
      }
      paramCanvas.drawLine(0.0F, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, Theme.dividerPaint);
    }
    for (;;)
    {
      return;
      label419:
      paramCanvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
    }
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
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject4 = null;
    Object localObject6 = null;
    paramInt2 = 0;
    Object localObject7 = localObject2;
    int j = paramInt2;
    Object localObject9 = localObject1;
    Object localObject10 = localObject6;
    if ((this.message.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))
    {
      localObject7 = localObject2;
      j = paramInt2;
      localObject9 = localObject1;
      localObject10 = localObject6;
      if ((this.message.messageOwner.media.webpage instanceof TLRPC.TL_webPage))
      {
        localObject2 = this.message.messageOwner.media.webpage;
        if ((this.message.photoThumbs == null) && (((TLRPC.WebPage)localObject2).photo != null)) {
          this.message.generateThumbs(true);
        }
        if ((((TLRPC.WebPage)localObject2).photo == null) || (this.message.photoThumbs == null)) {
          break label432;
        }
        paramInt2 = 1;
        localObject7 = ((TLRPC.WebPage)localObject2).title;
        localObject9 = localObject7;
        if (localObject7 == null) {
          localObject9 = ((TLRPC.WebPage)localObject2).site_name;
        }
        localObject7 = ((TLRPC.WebPage)localObject2).description;
        localObject10 = ((TLRPC.WebPage)localObject2).url;
        j = paramInt2;
      }
    }
    localObject6 = localObject7;
    localObject2 = localObject3;
    localObject1 = localObject9;
    label293:
    label432:
    label437:
    label576:
    int k;
    if (this.message != null)
    {
      localObject6 = localObject7;
      localObject2 = localObject3;
      localObject1 = localObject9;
      if (!this.message.messageOwner.entities.isEmpty())
      {
        paramInt2 = 0;
        localObject6 = localObject7;
        localObject2 = localObject4;
        localObject1 = localObject9;
        if (paramInt2 < this.message.messageOwner.entities.size())
        {
          TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)this.message.messageOwner.entities.get(paramInt2);
          Object localObject11 = localObject7;
          Object localObject12 = localObject4;
          localObject2 = localObject9;
          if (localMessageEntity.length > 0)
          {
            localObject11 = localObject7;
            localObject12 = localObject4;
            localObject2 = localObject9;
            if (localMessageEntity.offset >= 0)
            {
              if (localMessageEntity.offset < this.message.messageOwner.message.length()) {
                break label437;
              }
              localObject2 = localObject9;
              localObject12 = localObject4;
              localObject11 = localObject7;
            }
          }
          for (;;)
          {
            paramInt2++;
            localObject7 = localObject11;
            localObject4 = localObject12;
            localObject9 = localObject2;
            break label293;
            paramInt2 = 0;
            break;
            if (localMessageEntity.offset + localMessageEntity.length > this.message.messageOwner.message.length()) {
              localMessageEntity.length = (this.message.messageOwner.message.length() - localMessageEntity.offset);
            }
            localObject3 = localObject4;
            if (paramInt2 == 0)
            {
              localObject3 = localObject4;
              if (localObject10 != null) {
                if (localMessageEntity.offset == 0)
                {
                  localObject3 = localObject4;
                  if (localMessageEntity.length == this.message.messageOwner.message.length()) {}
                }
                else
                {
                  if (this.message.messageOwner.entities.size() != 1) {
                    break label1144;
                  }
                  localObject3 = localObject4;
                  if (localObject7 == null) {
                    localObject3 = this.message.messageOwner.message;
                  }
                }
              }
            }
            localObject2 = null;
            localObject11 = localObject7;
            localObject4 = localObject9;
            Object localObject13;
            for (;;)
            {
              try
              {
                if (!(localMessageEntity instanceof TLRPC.TL_messageEntityTextUrl))
                {
                  localObject11 = localObject7;
                  localObject4 = localObject9;
                  if (!(localMessageEntity instanceof TLRPC.TL_messageEntityUrl)) {
                    break label1177;
                  }
                }
                localObject11 = localObject7;
                localObject4 = localObject9;
                if (!(localMessageEntity instanceof TLRPC.TL_messageEntityUrl)) {
                  break label1159;
                }
                localObject11 = localObject7;
                localObject4 = localObject9;
                localObject2 = this.message.messageOwner.message.substring(localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length);
                if (localObject9 != null)
                {
                  localObject1 = localObject7;
                  localObject13 = localObject2;
                  localObject6 = localObject9;
                  localObject11 = localObject7;
                  localObject4 = localObject9;
                  if (((String)localObject9).length() != 0) {}
                }
                else
                {
                  localObject9 = localObject2;
                  localObject11 = localObject7;
                  localObject4 = localObject9;
                  localObject9 = Uri.parse((String)localObject9).getHost();
                  localObject6 = localObject9;
                  if (localObject9 == null) {
                    localObject6 = localObject2;
                  }
                  localObject9 = localObject6;
                  if (localObject6 != null)
                  {
                    localObject11 = localObject7;
                    localObject4 = localObject6;
                    k = ((String)localObject6).lastIndexOf('.');
                    localObject9 = localObject6;
                    if (k >= 0)
                    {
                      localObject11 = localObject7;
                      localObject4 = localObject6;
                      localObject6 = ((String)localObject6).substring(0, k);
                      localObject11 = localObject7;
                      localObject4 = localObject6;
                      k = ((String)localObject6).lastIndexOf('.');
                      localObject9 = localObject6;
                      if (k >= 0)
                      {
                        localObject11 = localObject7;
                        localObject4 = localObject6;
                        localObject9 = ((String)localObject6).substring(k + 1);
                      }
                      localObject11 = localObject7;
                      localObject4 = localObject9;
                      localObject6 = new java/lang/StringBuilder;
                      localObject11 = localObject7;
                      localObject4 = localObject9;
                      ((StringBuilder)localObject6).<init>();
                      localObject11 = localObject7;
                      localObject4 = localObject9;
                      localObject9 = ((String)localObject9).substring(0, 1).toUpperCase() + ((String)localObject9).substring(1);
                    }
                  }
                  localObject11 = localObject7;
                  localObject4 = localObject9;
                  if (localMessageEntity.offset == 0)
                  {
                    localObject1 = localObject7;
                    localObject13 = localObject2;
                    localObject6 = localObject9;
                    localObject11 = localObject7;
                    localObject4 = localObject9;
                    if (localMessageEntity.length == this.message.messageOwner.message.length()) {}
                  }
                  else
                  {
                    localObject11 = localObject7;
                    localObject4 = localObject9;
                    localObject1 = this.message.messageOwner.message;
                    localObject6 = localObject9;
                    localObject13 = localObject2;
                  }
                }
                localObject11 = localObject1;
                localObject12 = localObject3;
                localObject2 = localObject6;
                if (localObject13 == null) {
                  break;
                }
                localObject11 = localObject1;
                localObject4 = localObject6;
                if (((String)localObject13).toLowerCase().indexOf("http") == 0) {
                  break label1444;
                }
                localObject11 = localObject1;
                localObject4 = localObject6;
                if (((String)localObject13).toLowerCase().indexOf("mailto") == 0) {
                  break label1444;
                }
                localObject11 = localObject1;
                localObject4 = localObject6;
                localObject9 = this.links;
                localObject11 = localObject1;
                localObject4 = localObject6;
                localObject7 = new java/lang/StringBuilder;
                localObject11 = localObject1;
                localObject4 = localObject6;
                ((StringBuilder)localObject7).<init>();
                localObject11 = localObject1;
                localObject4 = localObject6;
                ((ArrayList)localObject9).add("http://" + (String)localObject13);
                localObject11 = localObject1;
                localObject12 = localObject3;
                localObject2 = localObject6;
              }
              catch (Exception localException5)
              {
                FileLog.e(localException5);
                localObject12 = localObject3;
                localObject2 = localObject4;
              }
              break;
              label1144:
              localObject3 = this.message.messageOwner.message;
              break label576;
              label1159:
              localObject11 = localException5;
              localObject4 = localObject9;
              localObject2 = localMessageEntity.url;
              continue;
              label1177:
              localObject1 = localException5;
              localObject13 = localObject2;
              localObject6 = localObject9;
              localObject11 = localException5;
              localObject4 = localObject9;
              if ((localMessageEntity instanceof TLRPC.TL_messageEntityEmail)) {
                if (localObject9 != null)
                {
                  localObject1 = localException5;
                  localObject13 = localObject2;
                  localObject6 = localObject9;
                  localObject11 = localException5;
                  localObject4 = localObject9;
                  if (((String)localObject9).length() != 0) {}
                }
                else
                {
                  localObject11 = localException5;
                  localObject4 = localObject9;
                  localObject2 = new java/lang/StringBuilder;
                  localObject11 = localException5;
                  localObject4 = localObject9;
                  ((StringBuilder)localObject2).<init>();
                  localObject11 = localException5;
                  localObject4 = localObject9;
                  localObject2 = "mailto:" + this.message.messageOwner.message.substring(localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length);
                  localObject11 = localException5;
                  localObject4 = localObject9;
                  localObject9 = this.message.messageOwner.message.substring(localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length);
                  localObject11 = localException5;
                  localObject4 = localObject9;
                  if (localMessageEntity.offset == 0)
                  {
                    localObject1 = localException5;
                    localObject13 = localObject2;
                    localObject6 = localObject9;
                    localObject11 = localException5;
                    localObject4 = localObject9;
                    if (localMessageEntity.length == this.message.messageOwner.message.length()) {}
                  }
                  else
                  {
                    localObject11 = localException5;
                    localObject4 = localObject9;
                    localObject1 = this.message.messageOwner.message;
                    localObject13 = localObject2;
                    localObject6 = localObject9;
                  }
                }
              }
            }
            label1444:
            localObject11 = localObject1;
            localObject4 = localObject6;
            this.links.add(localObject13);
            localObject11 = localObject1;
            localObject12 = localObject3;
            localObject2 = localObject6;
          }
        }
      }
    }
    if ((localObject10 != null) && (this.links.isEmpty())) {
      this.links.add(localObject10);
    }
    if (localObject1 != null) {}
    try
    {
      paramInt2 = (int)Math.ceil(this.titleTextPaint.measureText((String)localObject1));
      localObject8 = TextUtils.ellipsize(((String)localObject1).replace('\n', ' '), this.titleTextPaint, Math.min(paramInt2, i), TextUtils.TruncateAt.END);
      localObject4 = new android/text/StaticLayout;
      ((StaticLayout)localObject4).<init>((CharSequence)localObject8, this.titleTextPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      this.titleLayout = ((StaticLayout)localObject4);
      this.letterDrawable.setTitle((String)localObject1);
      if (localObject6 == null) {}
    }
    catch (Exception localException2)
    {
      try
      {
        this.descriptionLayout = ChatMessageCell.generateStaticLayout((CharSequence)localObject6, this.descriptionTextPaint, i, i, 0, 3);
        if (this.descriptionLayout.getLineCount() > 0) {
          this.description2Y = (this.descriptionY + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1) + AndroidUtilities.dp(1.0F));
        }
        if (localObject2 == null) {}
      }
      catch (Exception localException2)
      {
        try
        {
          for (;;)
          {
            this.descriptionLayout2 = ChatMessageCell.generateStaticLayout((CharSequence)localObject2, this.descriptionTextPaint, i, i, 0, 3);
            this.descriptionLayout2.getLineBottom(this.descriptionLayout2.getLineCount() - 1);
            if (this.descriptionLayout != null) {
              this.description2Y += AndroidUtilities.dp(10.0F);
            }
            if (this.links.isEmpty()) {
              break label1924;
            }
            paramInt2 = 0;
            for (;;)
            {
              if (paramInt2 >= this.links.size()) {
                break label1924;
              }
              try
              {
                localObject4 = (String)this.links.get(paramInt2);
                k = (int)Math.ceil(this.descriptionTextPaint.measureText((String)localObject4));
                localObject8 = TextUtils.ellipsize(((String)localObject4).replace('\n', ' '), this.descriptionTextPaint, Math.min(k, i), TextUtils.TruncateAt.MIDDLE);
                localObject4 = new android/text/StaticLayout;
                ((StaticLayout)localObject4).<init>((CharSequence)localObject8, this.descriptionTextPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
                this.linkY = this.description2Y;
                if ((this.descriptionLayout2 != null) && (this.descriptionLayout2.getLineCount() != 0)) {
                  this.linkY += this.descriptionLayout2.getLineBottom(this.descriptionLayout2.getLineCount() - 1) + AndroidUtilities.dp(1.0F);
                }
                this.linkLayout.add(localObject4);
                paramInt2++;
              }
              catch (Exception localException4)
              {
                for (;;)
                {
                  FileLog.e(localException4);
                }
              }
            }
            localException1 = localException1;
            FileLog.e(localException1);
          }
          localException2 = localException2;
          FileLog.e(localException2);
        }
        catch (Exception localException3)
        {
          Object localObject8;
          for (;;)
          {
            FileLog.e(localException3);
          }
          label1924:
          i = AndroidUtilities.dp(52.0F);
          if (LocaleController.isRTL)
          {
            paramInt2 = View.MeasureSpec.getSize(paramInt1) - AndroidUtilities.dp(10.0F) - i;
            this.letterDrawable.setBounds(paramInt2, AndroidUtilities.dp(10.0F), paramInt2 + i, AndroidUtilities.dp(62.0F));
            if (j != 0)
            {
              localObject2 = FileLoader.getClosestPhotoSizeWithSize(this.message.photoThumbs, i, true);
              localObject8 = FileLoader.getClosestPhotoSizeWithSize(this.message.photoThumbs, 80);
              localObject5 = localObject8;
              if (localObject8 == localObject2) {
                localObject5 = null;
              }
              ((TLRPC.PhotoSize)localObject2).size = -1;
              if (localObject5 != null) {
                ((TLRPC.PhotoSize)localObject5).size = -1;
              }
              this.linkImageView.setImageCoords(paramInt2, AndroidUtilities.dp(10.0F), i, i);
              FileLoader.getAttachFileName((TLObject)localObject2);
              localObject9 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(i), Integer.valueOf(i) });
              localObject8 = this.linkImageView;
              localObject2 = ((TLRPC.PhotoSize)localObject2).location;
              if (localObject5 == null) {
                break label2372;
              }
            }
          }
          label2372:
          for (Object localObject5 = ((TLRPC.PhotoSize)localObject5).location;; localObject5 = null)
          {
            ((ImageReceiver)localObject8).setImage((TLObject)localObject2, (String)localObject9, (TLRPC.FileLocation)localObject5, String.format(Locale.US, "%d_%d_b", new Object[] { Integer.valueOf(i), Integer.valueOf(i) }), 0, null, 0);
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
            k = 0;
            while (k < this.linkLayout.size())
            {
              localObject5 = (StaticLayout)this.linkLayout.get(k);
              i = paramInt2;
              if (((StaticLayout)localObject5).getLineCount() > 0) {
                i = paramInt2 + ((StaticLayout)localObject5).getLineBottom(((StaticLayout)localObject5).getLineCount() - 1);
              }
              k++;
              paramInt2 = i;
            }
            paramInt2 = AndroidUtilities.dp(10.0F);
            break;
          }
          i = paramInt2;
          if (j != 0) {
            i = Math.max(AndroidUtilities.dp(48.0F), paramInt2);
          }
          this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0F), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0F), NUM));
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
    int j = 0;
    int n;
    int i2;
    int i3;
    StaticLayout localStaticLayout;
    float f;
    if ((this.message != null) && (!this.linkLayout.isEmpty()) && (this.delegate != null) && (this.delegate.canPerformActions())) {
      if ((paramMotionEvent.getAction() == 0) || ((this.linkPreviewPressed) && (paramMotionEvent.getAction() == 1)))
      {
        int k = (int)paramMotionEvent.getX();
        int m = (int)paramMotionEvent.getY();
        n = 0;
        int i1 = 0;
        i2 = 0;
        i3 = i1;
        i = j;
        if (i2 < this.linkLayout.size())
        {
          localStaticLayout = (StaticLayout)this.linkLayout.get(i2);
          i = n;
          if (localStaticLayout.getLineCount() <= 0) {
            break label474;
          }
          i = localStaticLayout.getLineBottom(localStaticLayout.getLineCount() - 1);
          if (!LocaleController.isRTL) {
            break label314;
          }
          f = 8.0F;
          i3 = AndroidUtilities.dp(f);
          if ((k < i3 + localStaticLayout.getLineLeft(0)) || (k > i3 + localStaticLayout.getLineWidth(0)) || (m < this.linkY + n) || (m > this.linkY + n + i)) {
            break label469;
          }
          n = 1;
          if (paramMotionEvent.getAction() != 0) {
            break label333;
          }
          resetPressedLink();
          this.pressedLink = i2;
          this.linkPreviewPressed = true;
        }
      }
    }
    for (;;)
    {
      boolean bool;
      try
      {
        this.urlPath.setCurrentLayout(localStaticLayout, 0, 0.0F);
        localStaticLayout.getSelectionPath(0, localStaticLayout.getText().length(), this.urlPath);
        i = 1;
        i3 = n;
        i2 = i;
        if (i3 == 0)
        {
          resetPressedLink();
          i2 = i;
        }
        if ((i2 == 0) && (!super.onTouchEvent(paramMotionEvent))) {
          break label514;
        }
        bool = true;
        return bool;
        label314:
        f = AndroidUtilities.leftBaseline;
      }
      catch (Exception localException1)
      {
        FileLog.e(localException1);
        continue;
      }
      label333:
      i3 = n;
      i = j;
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
            if ((localWebPage == null) || (localWebPage.embed_url == null) || (localWebPage.embed_url.length() == 0)) {
              continue;
            }
            this.delegate.needOpenWebView(localWebPage);
          }
          catch (Exception localException2)
          {
            TLRPC.WebPage localWebPage;
            FileLog.e(localException2);
            continue;
          }
          resetPressedLink();
          i = 1;
          i3 = n;
          break;
          localWebPage = null;
          continue;
          Browser.openUrl(getContext(), (String)this.links.get(this.pressedLink));
        }
        label469:
        i = n + i;
        label474:
        i2++;
        n = i;
        break;
        i2 = i;
        if (paramMotionEvent.getAction() == 3)
        {
          resetPressedLink();
          i2 = i;
          continue;
          resetPressedLink();
          i2 = i;
          continue;
          label514:
          bool = false;
        }
      }
    }
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