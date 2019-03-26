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
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.MessageEntity;
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

public class SharedLinkCell extends FrameLayout {
    private CheckBox checkBox;
    private boolean checkingForLongPress = false;
    private SharedLinkCellDelegate delegate;
    private int description2Y = AndroidUtilities.dp(30.0f);
    private StaticLayout descriptionLayout;
    private StaticLayout descriptionLayout2;
    private TextPaint descriptionTextPaint;
    private int descriptionY = AndroidUtilities.dp(30.0f);
    private boolean drawLinkImageView;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private ArrayList<StaticLayout> linkLayout = new ArrayList();
    private boolean linkPreviewPressed;
    private int linkY;
    ArrayList<String> links = new ArrayList();
    private MessageObject message;
    private boolean needDivider;
    private CheckForLongPress pendingCheckForLongPress = null;
    private CheckForTap pendingCheckForTap = null;
    private int pressCount = 0;
    private int pressedLink;
    private StaticLayout titleLayout;
    private TextPaint titleTextPaint;
    private int titleY = AndroidUtilities.dp(10.0f);
    private LinkPath urlPath = new LinkPath();

    class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
        }

        public void run() {
            if (SharedLinkCell.this.checkingForLongPress && SharedLinkCell.this.getParent() != null && this.currentPressCount == SharedLinkCell.this.pressCount) {
                SharedLinkCell.this.checkingForLongPress = false;
                SharedLinkCell.this.performHapticFeedback(0);
                if (SharedLinkCell.this.pressedLink >= 0) {
                    SharedLinkCell.this.delegate.onLinkLongPress((String) SharedLinkCell.this.links.get(SharedLinkCell.this.pressedLink));
                }
                MotionEvent event = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                SharedLinkCell.this.onTouchEvent(event);
                event.recycle();
            }
        }
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        public void run() {
            if (SharedLinkCell.this.pendingCheckForLongPress == null) {
                SharedLinkCell.this.pendingCheckForLongPress = new CheckForLongPress();
            }
            SharedLinkCell.this.pendingCheckForLongPress.currentPressCount = SharedLinkCell.access$104(SharedLinkCell.this);
            SharedLinkCell.this.postDelayed(SharedLinkCell.this.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
        }
    }

    public interface SharedLinkCellDelegate {
        boolean canPerformActions();

        void needOpenWebView(WebPage webPage);

        void onLinkLongPress(String str);
    }

    static /* synthetic */ int access$104(SharedLinkCell x0) {
        int i = x0.pressCount + 1;
        x0.pressCount = i;
        return i;
    }

    /* Access modifiers changed, original: protected */
    public void startCheckLongPress() {
        if (!this.checkingForLongPress) {
            this.checkingForLongPress = true;
            if (this.pendingCheckForTap == null) {
                this.pendingCheckForTap = new CheckForTap();
            }
            postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
        }
    }

    /* Access modifiers changed, original: protected */
    public void cancelCheckLongPress() {
        this.checkingForLongPress = false;
        if (this.pendingCheckForLongPress != null) {
            removeCallbacks(this.pendingCheckForLongPress);
        }
        if (this.pendingCheckForTap != null) {
            removeCallbacks(this.pendingCheckForTap);
        }
    }

    public SharedLinkCell(Context context) {
        super(context);
        this.urlPath.setUseRoundRect(true);
        this.titleTextPaint = new TextPaint(1);
        this.titleTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.descriptionTextPaint = new TextPaint(1);
        this.titleTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.descriptionTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        setWillNotDraw(false);
        this.linkImageView = new ImageReceiver(this);
        this.linkImageView.setRoundRadius(AndroidUtilities.dp(4.0f));
        this.letterDrawable = new LetterDrawable();
        this.checkBox = new CheckBox(context, NUM);
        this.checkBox.setVisibility(4);
        this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
        addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 44.0f, 44.0f, LocaleController.isRTL ? 44.0f : 0.0f, 0.0f));
        setFocusable(true);
    }

    /* Access modifiers changed, original: protected */
    @SuppressLint({"DrawAllocation"})
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int a;
        String link;
        StaticLayout layout;
        int x;
        this.drawLinkImageView = false;
        this.descriptionLayout = null;
        this.titleLayout = null;
        this.descriptionLayout2 = null;
        this.linkLayout.clear();
        this.links.clear();
        int maxWidth = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - AndroidUtilities.dp(8.0f);
        String title = null;
        String description = null;
        String description2 = null;
        String webPageLink = null;
        boolean hasPhoto = false;
        if ((this.message.messageOwner.media instanceof TL_messageMediaWebPage) && (this.message.messageOwner.media.webpage instanceof TL_webPage)) {
            WebPage webPage = this.message.messageOwner.media.webpage;
            if (this.message.photoThumbs == null && webPage.photo != null) {
                this.message.generateThumbs(true);
            }
            hasPhoto = (webPage.photo == null || this.message.photoThumbs == null) ? false : true;
            title = webPage.title;
            if (title == null) {
                title = webPage.site_name;
            }
            description = webPage.description;
            webPageLink = webPage.url;
        }
        if (!(this.message == null || this.message.messageOwner.entities.isEmpty())) {
            for (a = 0; a < this.message.messageOwner.entities.size(); a++) {
                MessageEntity entity = (MessageEntity) this.message.messageOwner.entities.get(a);
                if (entity.length > 0 && entity.offset >= 0 && entity.offset < this.message.messageOwner.message.length()) {
                    if (entity.offset + entity.length > this.message.messageOwner.message.length()) {
                        entity.length = this.message.messageOwner.message.length() - entity.offset;
                    }
                    if (!(a != 0 || webPageLink == null || (entity.offset == 0 && entity.length == this.message.messageOwner.message.length()))) {
                        if (this.message.messageOwner.entities.size() != 1) {
                            description2 = this.message.messageOwner.message;
                        } else if (description == null) {
                            description2 = this.message.messageOwner.message;
                        }
                    }
                    link = null;
                    try {
                        if ((entity instanceof TL_messageEntityTextUrl) || (entity instanceof TL_messageEntityUrl)) {
                            if (entity instanceof TL_messageEntityUrl) {
                                link = this.message.messageOwner.message.substring(entity.offset, entity.offset + entity.length);
                            } else {
                                link = entity.url;
                            }
                            if (title == null || title.length() == 0) {
                                title = Uri.parse(link).getHost();
                                if (title == null) {
                                    title = link;
                                }
                                if (title != null) {
                                    int index = title.lastIndexOf(46);
                                    if (index >= 0) {
                                        title = title.substring(0, index);
                                        index = title.lastIndexOf(46);
                                        if (index >= 0) {
                                            title = title.substring(index + 1);
                                        }
                                        title = title.substring(0, 1).toUpperCase() + title.substring(1);
                                    }
                                }
                                if (!(entity.offset == 0 && entity.length == this.message.messageOwner.message.length())) {
                                    description = this.message.messageOwner.message;
                                }
                            }
                        } else if ((entity instanceof TL_messageEntityEmail) && (title == null || title.length() == 0)) {
                            link = "mailto:" + this.message.messageOwner.message.substring(entity.offset, entity.offset + entity.length);
                            title = this.message.messageOwner.message.substring(entity.offset, entity.offset + entity.length);
                            if (!(entity.offset == 0 && entity.length == this.message.messageOwner.message.length())) {
                                description = this.message.messageOwner.message;
                            }
                        }
                        if (link != null) {
                            if (link.toLowerCase().indexOf("http") == 0 || link.toLowerCase().indexOf("mailto") == 0) {
                                this.links.add(link);
                            } else {
                                this.links.add("http://" + link);
                            }
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
        }
        if (webPageLink != null && this.links.isEmpty()) {
            this.links.add(webPageLink);
        }
        if (title != null) {
            try {
                this.titleLayout = ChatMessageCell.generateStaticLayout(title, this.titleTextPaint, maxWidth, maxWidth, 0, 3);
                if (this.titleLayout.getLineCount() > 0) {
                    this.descriptionY = (this.titleY + this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1)) + AndroidUtilities.dp(4.0f);
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            this.letterDrawable.setTitle(title);
        }
        this.description2Y = this.descriptionY;
        int desctiptionLines = Math.max(1, 4 - (this.titleLayout != null ? this.titleLayout.getLineCount() : 0));
        if (description != null) {
            try {
                this.descriptionLayout = ChatMessageCell.generateStaticLayout(description, this.descriptionTextPaint, maxWidth, maxWidth, 0, desctiptionLines);
                if (this.descriptionLayout.getLineCount() > 0) {
                    this.description2Y = (this.descriptionY + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1)) + AndroidUtilities.dp(5.0f);
                }
            } catch (Exception e22) {
                FileLog.e(e22);
            }
        }
        if (description2 != null) {
            try {
                this.descriptionLayout2 = ChatMessageCell.generateStaticLayout(description2, this.descriptionTextPaint, maxWidth, maxWidth, 0, desctiptionLines);
                if (this.descriptionLayout != null) {
                    this.description2Y += AndroidUtilities.dp(10.0f);
                }
            } catch (Exception e222) {
                FileLog.e(e222);
            }
        }
        if (!this.links.isEmpty()) {
            for (a = 0; a < this.links.size(); a++) {
                try {
                    link = (String) this.links.get(a);
                    layout = new StaticLayout(TextUtils.ellipsize(link.replace(10, ' '), this.descriptionTextPaint, (float) Math.min((int) Math.ceil((double) this.descriptionTextPaint.measureText(link)), maxWidth), TruncateAt.MIDDLE), this.descriptionTextPaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.linkY = this.description2Y;
                    if (!(this.descriptionLayout2 == null || this.descriptionLayout2.getLineCount() == 0)) {
                        this.linkY += this.descriptionLayout2.getLineBottom(this.descriptionLayout2.getLineCount() - 1) + AndroidUtilities.dp(5.0f);
                    }
                    this.linkLayout.add(layout);
                } catch (Exception e2222) {
                    FileLog.e(e2222);
                }
            }
        }
        int maxPhotoWidth = AndroidUtilities.dp(52.0f);
        if (LocaleController.isRTL) {
            x = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(10.0f)) - maxPhotoWidth;
        } else {
            x = AndroidUtilities.dp(10.0f);
        }
        this.letterDrawable.setBounds(x, AndroidUtilities.dp(11.0f), x + maxPhotoWidth, AndroidUtilities.dp(63.0f));
        if (hasPhoto) {
            PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.message.photoThumbs, maxPhotoWidth, true);
            PhotoSize currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(this.message.photoThumbs, 80);
            if (currentPhotoObjectThumb == currentPhotoObject) {
                currentPhotoObjectThumb = null;
            }
            currentPhotoObject.size = -1;
            if (currentPhotoObjectThumb != null) {
                currentPhotoObjectThumb.size = -1;
            }
            this.linkImageView.setImageCoords(x, AndroidUtilities.dp(11.0f), maxPhotoWidth, maxPhotoWidth);
            String fileName = FileLoader.getAttachFileName(currentPhotoObject);
            this.linkImageView.setImage(currentPhotoObject, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(maxPhotoWidth), Integer.valueOf(maxPhotoWidth)}), currentPhotoObjectThumb, String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf(maxPhotoWidth), Integer.valueOf(maxPhotoWidth)}), 0, null, this.message, 0);
            this.drawLinkImageView = true;
        }
        int height = 0;
        if (!(this.titleLayout == null || this.titleLayout.getLineCount() == 0)) {
            height = 0 + (this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1) + AndroidUtilities.dp(4.0f));
        }
        if (!(this.descriptionLayout == null || this.descriptionLayout.getLineCount() == 0)) {
            height += this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1) + AndroidUtilities.dp(5.0f);
        }
        if (!(this.descriptionLayout2 == null || this.descriptionLayout2.getLineCount() == 0)) {
            height += this.descriptionLayout2.getLineBottom(this.descriptionLayout2.getLineCount() - 1) + AndroidUtilities.dp(5.0f);
            if (this.descriptionLayout != null) {
                height += AndroidUtilities.dp(10.0f);
            }
        }
        for (a = 0; a < this.linkLayout.size(); a++) {
            layout = (StaticLayout) this.linkLayout.get(a);
            if (layout.getLineCount() > 0) {
                height += layout.getLineBottom(layout.getLineCount() - 1);
            }
        }
        this.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM));
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (this.needDivider ? 1 : 0) + Math.max(AndroidUtilities.dp(76.0f), AndroidUtilities.dp(17.0f) + height));
    }

    public void setLink(MessageObject messageObject, boolean divider) {
        this.needDivider = divider;
        resetPressedLink();
        this.message = messageObject;
        requestLayout();
    }

    public void setDelegate(SharedLinkCellDelegate sharedLinkCellDelegate) {
        this.delegate = sharedLinkCellDelegate;
    }

    public MessageObject getMessage() {
        return this.message;
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onAttachedToWindow();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:37:0x00ae  */
    public boolean onTouchEvent(android.view.MotionEvent r15) {
        /*
        r14 = this;
        r7 = 0;
        r11 = r14.message;
        if (r11 == 0) goto L_0x011d;
    L_0x0005:
        r11 = r14.linkLayout;
        r11 = r11.isEmpty();
        if (r11 != 0) goto L_0x011d;
    L_0x000d:
        r11 = r14.delegate;
        if (r11 == 0) goto L_0x011d;
    L_0x0011:
        r11 = r14.delegate;
        r11 = r11.canPerformActions();
        if (r11 == 0) goto L_0x011d;
    L_0x0019:
        r11 = r15.getAction();
        if (r11 == 0) goto L_0x002a;
    L_0x001f:
        r11 = r14.linkPreviewPressed;
        if (r11 == 0) goto L_0x0112;
    L_0x0023:
        r11 = r15.getAction();
        r12 = 1;
        if (r11 != r12) goto L_0x0112;
    L_0x002a:
        r11 = r15.getX();
        r9 = (int) r11;
        r11 = r15.getY();
        r10 = (int) r11;
        r5 = 0;
        r6 = 0;
        r0 = 0;
    L_0x0037:
        r11 = r14.linkLayout;
        r11 = r11.size();
        if (r0 >= r11) goto L_0x00ac;
    L_0x003f:
        r11 = r14.linkLayout;
        r3 = r11.get(r0);
        r3 = (android.text.StaticLayout) r3;
        r11 = r3.getLineCount();
        if (r11 <= 0) goto L_0x010e;
    L_0x004d:
        r11 = r3.getLineCount();
        r11 = r11 + -1;
        r2 = r3.getLineBottom(r11);
        r11 = org.telegram.messenger.LocaleController.isRTL;
        if (r11 == 0) goto L_0x00bb;
    L_0x005b:
        r11 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
    L_0x005d:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = (float) r9;
        r12 = (float) r4;
        r13 = 0;
        r13 = r3.getLineLeft(r13);
        r12 = r12 + r13;
        r11 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1));
        if (r11 < 0) goto L_0x010d;
    L_0x006d:
        r11 = (float) r9;
        r12 = (float) r4;
        r13 = 0;
        r13 = r3.getLineWidth(r13);
        r12 = r12 + r13;
        r11 = (r11 > r12 ? 1 : (r11 == r12 ? 0 : -1));
        if (r11 > 0) goto L_0x010d;
    L_0x0079:
        r11 = r14.linkY;
        r11 = r11 + r5;
        if (r10 < r11) goto L_0x010d;
    L_0x007e:
        r11 = r14.linkY;
        r11 = r11 + r5;
        r11 = r11 + r2;
        if (r10 > r11) goto L_0x010d;
    L_0x0084:
        r6 = 1;
        r11 = r15.getAction();
        if (r11 != 0) goto L_0x00c4;
    L_0x008b:
        r14.resetPressedLink();
        r14.pressedLink = r0;
        r11 = 1;
        r14.linkPreviewPressed = r11;
        r14.startCheckLongPress();
        r11 = r14.urlPath;	 Catch:{ Exception -> 0x00bf }
        r12 = 0;
        r13 = 0;
        r11.setCurrentLayout(r3, r12, r13);	 Catch:{ Exception -> 0x00bf }
        r11 = 0;
        r12 = r3.getText();	 Catch:{ Exception -> 0x00bf }
        r12 = r12.length();	 Catch:{ Exception -> 0x00bf }
        r13 = r14.urlPath;	 Catch:{ Exception -> 0x00bf }
        r3.getSelectionPath(r11, r12, r13);	 Catch:{ Exception -> 0x00bf }
    L_0x00ab:
        r7 = 1;
    L_0x00ac:
        if (r6 != 0) goto L_0x00b1;
    L_0x00ae:
        r14.resetPressedLink();
    L_0x00b1:
        if (r7 != 0) goto L_0x00b9;
    L_0x00b3:
        r11 = super.onTouchEvent(r15);
        if (r11 == 0) goto L_0x0121;
    L_0x00b9:
        r11 = 1;
    L_0x00ba:
        return r11;
    L_0x00bb:
        r11 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r11 = (float) r11;
        goto L_0x005d;
    L_0x00bf:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x00ab;
    L_0x00c4:
        r11 = r14.linkPreviewPressed;
        if (r11 == 0) goto L_0x00ac;
    L_0x00c8:
        r11 = r14.pressedLink;	 Catch:{ Exception -> 0x0108 }
        if (r11 != 0) goto L_0x00f4;
    L_0x00cc:
        r11 = r14.message;	 Catch:{ Exception -> 0x0108 }
        r11 = r11.messageOwner;	 Catch:{ Exception -> 0x0108 }
        r11 = r11.media;	 Catch:{ Exception -> 0x0108 }
        if (r11 == 0) goto L_0x00f4;
    L_0x00d4:
        r11 = r14.message;	 Catch:{ Exception -> 0x0108 }
        r11 = r11.messageOwner;	 Catch:{ Exception -> 0x0108 }
        r11 = r11.media;	 Catch:{ Exception -> 0x0108 }
        r8 = r11.webpage;	 Catch:{ Exception -> 0x0108 }
    L_0x00dc:
        if (r8 == 0) goto L_0x00f6;
    L_0x00de:
        r11 = r8.embed_url;	 Catch:{ Exception -> 0x0108 }
        if (r11 == 0) goto L_0x00f6;
    L_0x00e2:
        r11 = r8.embed_url;	 Catch:{ Exception -> 0x0108 }
        r11 = r11.length();	 Catch:{ Exception -> 0x0108 }
        if (r11 == 0) goto L_0x00f6;
    L_0x00ea:
        r11 = r14.delegate;	 Catch:{ Exception -> 0x0108 }
        r11.needOpenWebView(r8);	 Catch:{ Exception -> 0x0108 }
    L_0x00ef:
        r14.resetPressedLink();
        r7 = 1;
        goto L_0x00ac;
    L_0x00f4:
        r8 = 0;
        goto L_0x00dc;
    L_0x00f6:
        r12 = r14.getContext();	 Catch:{ Exception -> 0x0108 }
        r11 = r14.links;	 Catch:{ Exception -> 0x0108 }
        r13 = r14.pressedLink;	 Catch:{ Exception -> 0x0108 }
        r11 = r11.get(r13);	 Catch:{ Exception -> 0x0108 }
        r11 = (java.lang.String) r11;	 Catch:{ Exception -> 0x0108 }
        org.telegram.messenger.browser.Browser.openUrl(r12, r11);	 Catch:{ Exception -> 0x0108 }
        goto L_0x00ef;
    L_0x0108:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
        goto L_0x00ef;
    L_0x010d:
        r5 = r5 + r2;
    L_0x010e:
        r0 = r0 + 1;
        goto L_0x0037;
    L_0x0112:
        r11 = r15.getAction();
        r12 = 3;
        if (r11 != r12) goto L_0x00b1;
    L_0x0119:
        r14.resetPressedLink();
        goto L_0x00b1;
    L_0x011d:
        r14.resetPressedLink();
        goto L_0x00b1;
    L_0x0121:
        r11 = 0;
        goto L_0x00ba;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedLinkCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public String getLink(int num) {
        if (num < 0 || num >= this.links.size()) {
            return null;
        }
        return (String) this.links.get(num);
    }

    /* Access modifiers changed, original: protected */
    public void resetPressedLink() {
        this.pressedLink = -1;
        this.linkPreviewPressed = false;
        cancelCheckLongPress();
        invalidate();
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(checked, animated);
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        float f;
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout2 != null) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.save();
            if (LocaleController.isRTL) {
                f = 8.0f;
            } else {
                f = (float) AndroidUtilities.leftBaseline;
            }
            canvas.translate((float) AndroidUtilities.dp(f), (float) this.description2Y);
            this.descriptionLayout2.draw(canvas);
            canvas.restore();
        }
        if (!this.linkLayout.isEmpty()) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkText"));
            int offset = 0;
            for (int a = 0; a < this.linkLayout.size(); a++) {
                StaticLayout layout = (StaticLayout) this.linkLayout.get(a);
                if (layout.getLineCount() > 0) {
                    canvas.save();
                    if (LocaleController.isRTL) {
                        f = 8.0f;
                    } else {
                        f = (float) AndroidUtilities.leftBaseline;
                    }
                    canvas.translate((float) AndroidUtilities.dp(f), (float) (this.linkY + offset));
                    if (this.pressedLink == a) {
                        canvas.drawPath(this.urlPath, Theme.linkSelectionPaint);
                    }
                    layout.draw(canvas);
                    canvas.restore();
                    offset += layout.getLineBottom(layout.getLineCount() - 1);
                }
            }
        }
        this.letterDrawable.draw(canvas);
        if (this.drawLinkImageView) {
            this.linkImageView.draw(canvas);
        }
        if (!this.needDivider) {
            return;
        }
        if (LocaleController.isRTL) {
            canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        } else {
            canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        StringBuilder sb = new StringBuilder();
        if (this.titleLayout != null) {
            sb.append(this.titleLayout.getText());
        }
        if (this.descriptionLayout != null) {
            sb.append(", ");
            sb.append(this.descriptionLayout.getText());
        }
        if (this.descriptionLayout2 != null) {
            sb.append(", ");
            sb.append(this.descriptionLayout2.getText());
        }
        if (this.checkBox.isChecked()) {
            info.setChecked(true);
            info.setCheckable(true);
        }
    }
}
