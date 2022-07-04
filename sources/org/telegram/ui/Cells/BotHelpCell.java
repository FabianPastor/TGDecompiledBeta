package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FileRefController;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class BotHelpCell extends View {
    private boolean animating;
    private String currentPhotoKey;
    private BotHelpCellDelegate delegate;
    private int height;
    private int imagePadding = AndroidUtilities.dp(4.0f);
    private ImageReceiver imageReceiver;
    private boolean isPhotoVisible;
    private boolean isTextVisible;
    private String oldText;
    private int photoHeight;
    private ClickableSpan pressedLink;
    private Theme.ResourcesProvider resourcesProvider;
    private StaticLayout textLayout;
    private int textX;
    private int textY;
    private LinkPath urlPath = new LinkPath();
    public boolean wasDraw;
    private int width;

    public interface BotHelpCellDelegate {
        void didPressUrl(String str);
    }

    public BotHelpCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        ImageReceiver imageReceiver2 = new ImageReceiver(this);
        this.imageReceiver = imageReceiver2;
        imageReceiver2.setInvalidateAll(true);
        this.imageReceiver.setCrossfadeWithOldImage(true);
        this.imageReceiver.setCrossfadeDuration(300);
    }

    public void setDelegate(BotHelpCellDelegate botHelpCellDelegate) {
        this.delegate = botHelpCellDelegate;
    }

    private void resetPressedLink() {
        if (this.pressedLink != null) {
            this.pressedLink = null;
        }
        invalidate();
    }

    public void setText(boolean bot, String text) {
        setText(bot, text, (TLObject) null, (TLRPC.BotInfo) null);
    }

    public void setText(boolean bot, String text, TLObject imageOrAnimation, TLRPC.BotInfo botInfo) {
        String text2;
        int maxWidth;
        TLObject tLObject = imageOrAnimation;
        boolean photoVisible = tLObject != null;
        boolean textVisible = !TextUtils.isEmpty(text);
        if ((text == null || text.length() == 0) && !photoVisible) {
            setVisibility(8);
            return;
        }
        if (text == null) {
            text2 = "";
        } else {
            text2 = text;
        }
        if (text2 == null || !text2.equals(this.oldText) || this.isPhotoVisible != photoVisible) {
            this.isPhotoVisible = photoVisible;
            this.isTextVisible = textVisible;
            if (photoVisible) {
                String photoKey = FileRefController.getKeyForParentObject(botInfo);
                if (!ColorUtils$$ExternalSyntheticBackport0.m(this.currentPhotoKey, photoKey)) {
                    this.currentPhotoKey = photoKey;
                    if (tLObject instanceof TLRPC.TL_photo) {
                        TLRPC.Photo photo = (TLRPC.Photo) tLObject;
                        this.imageReceiver.setImage(ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 400), photo), "400_400", (Drawable) null, "jpg", botInfo, 0);
                    } else if (tLObject instanceof TLRPC.Document) {
                        TLRPC.Document doc = (TLRPC.Document) tLObject;
                        TLRPC.PhotoSize photoThumb = FileLoader.getClosestPhotoSizeWithSize(doc.thumbs, 400);
                        BitmapDrawable strippedThumb = null;
                        if (SharedConfig.getDevicePerformanceClass() != 0) {
                            Iterator<TLRPC.PhotoSize> it = doc.thumbs.iterator();
                            while (it.hasNext()) {
                                TLRPC.PhotoSize photoSize = it.next();
                                if (photoSize instanceof TLRPC.TL_photoStrippedSize) {
                                    strippedThumb = new BitmapDrawable(getResources(), ImageLoader.getStrippedPhotoBitmap(photoSize.bytes, "b"));
                                }
                            }
                        }
                        this.imageReceiver.setImage(ImageLocation.getForDocument(doc), "g", ImageLocation.getForDocument(MessageObject.getDocumentVideoThumb(doc), doc), (String) null, ImageLocation.getForDocument(photoThumb, doc), "86_86_b", strippedThumb, doc.size, "mp4", botInfo, 0);
                    }
                    int topRadius = AndroidUtilities.dp((float) SharedConfig.bubbleRadius) - AndroidUtilities.dp(2.0f);
                    int bottomRadius = AndroidUtilities.dp(4.0f);
                    if (!this.isTextVisible) {
                        bottomRadius = topRadius;
                    }
                    this.imageReceiver.setRoundRadius(topRadius, topRadius, bottomRadius, bottomRadius);
                }
            }
            this.oldText = AndroidUtilities.getSafeString(text2);
            setVisibility(0);
            if (AndroidUtilities.isTablet()) {
                maxWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.7f);
            } else {
                maxWidth = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
            }
            if (this.isTextVisible) {
                String[] lines = text2.split("\n");
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                String help = LocaleController.getString(NUM);
                if (bot) {
                    stringBuilder.append(help);
                    stringBuilder.append("\n\n");
                }
                for (int a = 0; a < lines.length; a++) {
                    stringBuilder.append(lines[a].trim());
                    if (a != lines.length - 1) {
                        stringBuilder.append("\n");
                    }
                }
                MessageObject.addLinks(false, stringBuilder);
                if (bot) {
                    stringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0, help.length(), 33);
                }
                Emoji.replaceEmoji(stringBuilder, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                try {
                    StaticLayout staticLayout = new StaticLayout(stringBuilder, Theme.chat_msgTextPaint, maxWidth - (this.isPhotoVisible ? AndroidUtilities.dp(5.0f) : 0), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.textLayout = staticLayout;
                    this.width = 0;
                    this.height = staticLayout.getHeight() + AndroidUtilities.dp(22.0f);
                    int count = this.textLayout.getLineCount();
                    for (int a2 = 0; a2 < count; a2++) {
                        this.width = (int) Math.ceil((double) Math.max((float) this.width, this.textLayout.getLineWidth(a2) + this.textLayout.getLineLeft(a2)));
                    }
                    if (this.width > maxWidth || this.isPhotoVisible) {
                        this.width = maxWidth;
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (this.isPhotoVisible) {
                this.width = maxWidth;
            }
            int dp = this.width + AndroidUtilities.dp(22.0f);
            this.width = dp;
            if (this.isPhotoVisible) {
                int i = this.height;
                double d = (double) dp;
                Double.isNaN(d);
                int i2 = (int) (d * 0.5625d);
                this.photoHeight = i2;
                this.height = i + i2 + AndroidUtilities.dp(4.0f);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        BotHelpCellDelegate botHelpCellDelegate;
        float x = event.getX();
        float y = event.getY();
        boolean result = false;
        if (this.textLayout != null) {
            if (event.getAction() == 0 || (this.pressedLink != null && event.getAction() == 1)) {
                if (event.getAction() == 0) {
                    resetPressedLink();
                    try {
                        int x2 = (int) (x - ((float) this.textX));
                        int line = this.textLayout.getLineForVertical((int) (y - ((float) this.textY)));
                        int off = this.textLayout.getOffsetForHorizontal(line, (float) x2);
                        float left = this.textLayout.getLineLeft(line);
                        if (left > ((float) x2) || this.textLayout.getLineWidth(line) + left < ((float) x2)) {
                            resetPressedLink();
                        } else {
                            Spannable buffer = (Spannable) this.textLayout.getText();
                            ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                            if (link.length != 0) {
                                resetPressedLink();
                                ClickableSpan clickableSpan = link[0];
                                this.pressedLink = clickableSpan;
                                result = true;
                                try {
                                    int start = buffer.getSpanStart(clickableSpan);
                                    this.urlPath.setCurrentLayout(this.textLayout, start, 0.0f);
                                    this.textLayout.getSelectionPath(start, buffer.getSpanEnd(this.pressedLink), this.urlPath);
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                            } else {
                                resetPressedLink();
                            }
                        }
                    } catch (Exception e2) {
                        resetPressedLink();
                        FileLog.e((Throwable) e2);
                    }
                } else {
                    ClickableSpan clickableSpan2 = this.pressedLink;
                    if (clickableSpan2 != null) {
                        try {
                            if (clickableSpan2 instanceof URLSpanNoUnderline) {
                                String url = ((URLSpanNoUnderline) clickableSpan2).getURL();
                                if ((url.startsWith("@") || url.startsWith("#") || url.startsWith("/")) && (botHelpCellDelegate = this.delegate) != null) {
                                    botHelpCellDelegate.didPressUrl(url);
                                }
                            } else if (clickableSpan2 instanceof URLSpan) {
                                BotHelpCellDelegate botHelpCellDelegate2 = this.delegate;
                                if (botHelpCellDelegate2 != null) {
                                    botHelpCellDelegate2.didPressUrl(((URLSpan) clickableSpan2).getURL());
                                }
                            } else {
                                clickableSpan2.onClick(this);
                            }
                        } catch (Exception e3) {
                            FileLog.e((Throwable) e3);
                        }
                        resetPressedLink();
                        result = true;
                    }
                }
            } else if (event.getAction() == 3) {
                resetPressedLink();
            }
        }
        return result || super.onTouchEvent(event);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), this.height + AndroidUtilities.dp(8.0f));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int x = (getWidth() - this.width) / 2;
        int y = this.photoHeight + AndroidUtilities.dp(2.0f);
        Drawable shadowDrawable = Theme.chat_msgInMediaDrawable.getShadowDrawable();
        if (shadowDrawable != null) {
            shadowDrawable.setBounds(x, y, this.width + x, this.height + y);
            shadowDrawable.draw(canvas);
        }
        int w = AndroidUtilities.displaySize.x;
        int h = AndroidUtilities.displaySize.y;
        if (getParent() instanceof View) {
            View view = (View) getParent();
            w = view.getMeasuredWidth();
            h = view.getMeasuredHeight();
        }
        Theme.MessageDrawable drawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInMedia");
        drawable.setTop((int) getY(), w, h, false, false);
        drawable.setBounds(x, 0, this.width + x, this.height);
        drawable.draw(canvas);
        ImageReceiver imageReceiver2 = this.imageReceiver;
        int i = this.imagePadding;
        imageReceiver2.setImageCoords((float) (x + i), (float) i, (float) (this.width - (i * 2)), (float) (this.photoHeight - i));
        this.imageReceiver.draw(canvas);
        Theme.chat_msgTextPaint.setColor(getThemedColor("chat_messageTextIn"));
        Theme.chat_msgTextPaint.linkColor = getThemedColor("chat_messageLinkIn");
        canvas.save();
        int dp = AndroidUtilities.dp(this.isPhotoVisible ? 14.0f : 11.0f) + x;
        this.textX = dp;
        int dp2 = AndroidUtilities.dp(11.0f) + y;
        this.textY = dp2;
        canvas.translate((float) dp, (float) dp2);
        if (this.pressedLink != null) {
            canvas.drawPath(this.urlPath, Theme.chat_urlPaint);
        }
        StaticLayout staticLayout = this.textLayout;
        if (staticLayout != null) {
            staticLayout.draw(canvas);
        }
        canvas.restore();
        this.wasDraw = true;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.imageReceiver.onDetachedFromWindow();
        this.wasDraw = false;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setText(this.textLayout.getText());
    }

    public boolean animating() {
        return this.animating;
    }

    public void setAnimating(boolean animating2) {
        this.animating = animating2;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private Drawable getThemedDrawable(String drawableKey) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Drawable drawable = resourcesProvider2 != null ? resourcesProvider2.getDrawable(drawableKey) : null;
        return drawable != null ? drawable : Theme.getThemeDrawable(drawableKey);
    }
}
