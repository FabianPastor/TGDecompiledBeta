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
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLObject;
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
    private SharedLinkCellDelegate delegate;
    private int description2Y = AndroidUtilities.dp(27.0f);
    private StaticLayout descriptionLayout;
    private StaticLayout descriptionLayout2;
    private TextPaint descriptionTextPaint;
    private int descriptionY = AndroidUtilities.dp(27.0f);
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
    private int titleY = AndroidUtilities.dp(7.0f);
    private LinkPath urlPath = new LinkPath();

    public interface SharedLinkCellDelegate {
        boolean canPerformActions();

        void needOpenWebView(WebPage webPage);
    }

    public SharedLinkCell(Context context) {
        super(context);
        this.titleTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.descriptionTextPaint = new TextPaint(1);
        this.titleTextPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        this.descriptionTextPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        setWillNotDraw(false);
        this.linkImageView = new ImageReceiver(this);
        this.letterDrawable = new LetterDrawable();
        this.checkBox = new CheckBox(context, C0446R.drawable.round_check2);
        this.checkBox.setVisibility(4);
        this.checkBox.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
        addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 44.0f, 44.0f, LocaleController.isRTL ? 44.0f : 0.0f, 0.0f));
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int i, int i2) {
        String str;
        String str2;
        Object obj;
        boolean z;
        String str3;
        String str4;
        String str5;
        Throwable th;
        Throwable e;
        StaticLayout staticLayout;
        char c;
        int i3;
        String str6;
        CharSequence ellipsize;
        StaticLayout staticLayout2;
        char c2;
        int dp;
        TLObject closestPhotoSizeWithSize;
        PhotoSize closestPhotoSizeWithSize2;
        int i4;
        StaticLayout staticLayout3;
        this.drawLinkImageView = false;
        this.descriptionLayout = null;
        this.titleLayout = null;
        this.descriptionLayout2 = null;
        this.description2Y = this.descriptionY;
        this.linkLayout.clear();
        this.links.clear();
        int size = (MeasureSpec.getSize(i) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - AndroidUtilities.dp(8.0f);
        boolean z2 = true;
        if ((this.message.messageOwner.media instanceof TL_messageMediaWebPage) && (r1.message.messageOwner.media.webpage instanceof TL_webPage)) {
            WebPage webPage = r1.message.messageOwner.media.webpage;
            if (r1.message.photoThumbs == null && webPage.photo != null) {
                r1.message.generateThumbs(true);
            }
            boolean z3 = (webPage.photo == null || r1.message.photoThumbs == null) ? false : true;
            str = webPage.title;
            if (str == null) {
                str = webPage.site_name;
            }
            str2 = webPage.description;
            obj = webPage.url;
            z = z3;
        } else {
            z = false;
            obj = null;
            str = obj;
            str2 = str;
        }
        if (r1.message == null || r1.message.messageOwner.entities.isEmpty()) {
            str3 = str;
            str4 = str2;
            str5 = null;
        } else {
            int i5 = 0;
            String str7 = null;
            while (i5 < r1.message.messageOwner.entities.size()) {
                MessageEntity messageEntity = (MessageEntity) r1.message.messageOwner.entities.get(i5);
                if (messageEntity.length > 0 && messageEntity.offset >= 0) {
                    if (messageEntity.offset < r1.message.messageOwner.message.length()) {
                        if (messageEntity.offset + messageEntity.length > r1.message.messageOwner.message.length()) {
                            messageEntity.length = r1.message.messageOwner.message.length() - messageEntity.offset;
                        }
                        if (!(i5 != 0 || obj == null || (messageEntity.offset == 0 && messageEntity.length == r1.message.messageOwner.message.length()))) {
                            if (r1.message.messageOwner.entities.size() != z2) {
                                str7 = r1.message.messageOwner.message;
                            } else if (str2 == null) {
                                str7 = r1.message.messageOwner.message;
                            }
                        }
                        if (!(messageEntity instanceof TL_messageEntityTextUrl)) {
                            if (!(messageEntity instanceof TL_messageEntityUrl)) {
                                StringBuilder stringBuilder;
                                if ((messageEntity instanceof TL_messageEntityEmail) && (str == null || str.length() == 0)) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("mailto:");
                                    stringBuilder.append(r1.message.messageOwner.message.substring(messageEntity.offset, messageEntity.offset + messageEntity.length));
                                    str3 = stringBuilder.toString();
                                    String substring = r1.message.messageOwner.message.substring(messageEntity.offset, messageEntity.offset + messageEntity.length);
                                    try {
                                        if (!(messageEntity.offset == 0 && messageEntity.length == r1.message.messageOwner.message.length())) {
                                            str2 = r1.message.messageOwner.message;
                                        }
                                        str = substring;
                                        if (str3 != null) {
                                            if (str3.toLowerCase().indexOf("http") != 0) {
                                            }
                                            r1.links.add(str3);
                                        }
                                    } catch (Throwable e2) {
                                        th = e2;
                                        str = substring;
                                        FileLog.m3e(th);
                                        i5++;
                                        z2 = true;
                                    }
                                } else {
                                    str3 = null;
                                    if (str3 != null) {
                                        if (str3.toLowerCase().indexOf("http") != 0 || str3.toLowerCase().indexOf("mailto") == 0) {
                                            r1.links.add(str3);
                                        } else {
                                            ArrayList arrayList = r1.links;
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("http://");
                                            stringBuilder.append(str3);
                                            arrayList.add(stringBuilder.toString());
                                        }
                                    }
                                }
                            }
                        }
                        try {
                            if (messageEntity instanceof TL_messageEntityUrl) {
                                str3 = r1.message.messageOwner.message.substring(messageEntity.offset, messageEntity.offset + messageEntity.length);
                            } else {
                                str3 = messageEntity.url;
                            }
                            if (str == null || str.length() == 0) {
                                try {
                                    str = Uri.parse(str3).getHost();
                                    if (str == null) {
                                        str = str3;
                                    }
                                    if (str != null) {
                                        int lastIndexOf = str.lastIndexOf(46);
                                        if (lastIndexOf >= 0) {
                                            String substring2 = str.substring(0, lastIndexOf);
                                            try {
                                                int lastIndexOf2 = substring2.lastIndexOf(46);
                                                if (lastIndexOf2 >= 0) {
                                                    substring2 = substring2.substring(lastIndexOf2 + 1);
                                                }
                                                StringBuilder stringBuilder2 = new StringBuilder();
                                                stringBuilder2.append(substring2.substring(0, 1).toUpperCase());
                                                stringBuilder2.append(substring2.substring(1));
                                                str = stringBuilder2.toString();
                                            } catch (Throwable e22) {
                                                th = e22;
                                                str = substring2;
                                                FileLog.m3e(th);
                                                i5++;
                                                z2 = true;
                                            }
                                        }
                                    }
                                    if (!(messageEntity.offset == 0 && messageEntity.length == r1.message.messageOwner.message.length())) {
                                        str2 = r1.message.messageOwner.message;
                                    }
                                } catch (Exception e3) {
                                    e22 = e3;
                                    str = str3;
                                    th = e22;
                                    FileLog.m3e(th);
                                    i5++;
                                    z2 = true;
                                }
                            }
                            if (str3 != null) {
                                if (str3.toLowerCase().indexOf("http") != 0) {
                                }
                                r1.links.add(str3);
                            }
                        } catch (Exception e4) {
                            e22 = e4;
                            th = e22;
                            FileLog.m3e(th);
                            i5++;
                            z2 = true;
                        }
                    }
                }
                i5++;
                z2 = true;
            }
            str3 = str;
            str4 = str2;
            str5 = str7;
        }
        if (obj != null && r1.links.isEmpty()) {
            r1.links.add(obj);
        }
        if (str3 != null) {
            try {
                staticLayout = staticLayout;
                StaticLayout staticLayout4 = staticLayout;
                c = '\n';
                try {
                    staticLayout = new StaticLayout(TextUtils.ellipsize(str3.replace('\n', ' '), r1.titleTextPaint, (float) Math.min((int) Math.ceil((double) r1.titleTextPaint.measureText(str3)), size), TruncateAt.END), r1.titleTextPaint, size, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    r1.titleLayout = staticLayout4;
                } catch (Exception e5) {
                    e22 = e5;
                    FileLog.m3e(e22);
                    r1.letterDrawable.setTitle(str3);
                    if (str4 != null) {
                        try {
                            r1.descriptionLayout = ChatMessageCell.generateStaticLayout(str4, r1.descriptionTextPaint, size, size, 0, 3);
                            if (r1.descriptionLayout.getLineCount() > 0) {
                                r1.description2Y = (r1.descriptionY + r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1)) + AndroidUtilities.dp(1.0f);
                            }
                        } catch (Throwable e222) {
                            FileLog.m3e(e222);
                        }
                    }
                    if (str5 != null) {
                        try {
                            r1.descriptionLayout2 = ChatMessageCell.generateStaticLayout(str5, r1.descriptionTextPaint, size, size, 0, 3);
                            r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1);
                            if (r1.descriptionLayout != null) {
                                r1.description2Y += AndroidUtilities.dp(10.0f);
                            }
                        } catch (Throwable e2222) {
                            FileLog.m3e(e2222);
                        }
                    }
                    if (!r1.links.isEmpty()) {
                        i3 = 0;
                        while (i3 < r1.links.size()) {
                            try {
                                str6 = (String) r1.links.get(i3);
                                try {
                                    ellipsize = TextUtils.ellipsize(str6.replace(c, ' '), r1.descriptionTextPaint, (float) Math.min((int) Math.ceil((double) r1.descriptionTextPaint.measureText(str6)), size), TruncateAt.MIDDLE);
                                    staticLayout = staticLayout;
                                    staticLayout2 = staticLayout;
                                    c2 = ' ';
                                    try {
                                        staticLayout = new StaticLayout(ellipsize, r1.descriptionTextPaint, size, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                        r1.linkY = r1.description2Y;
                                        r1.linkY += r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1) + AndroidUtilities.dp(1.0f);
                                        r1.linkLayout.add(staticLayout2);
                                    } catch (Exception e6) {
                                        e2222 = e6;
                                        FileLog.m3e(e2222);
                                        i3++;
                                        c = '\n';
                                    }
                                } catch (Exception e7) {
                                    e2222 = e7;
                                    c2 = ' ';
                                    FileLog.m3e(e2222);
                                    i3++;
                                    c = '\n';
                                }
                            } catch (Exception e8) {
                                e2222 = e8;
                                FileLog.m3e(e2222);
                                i3++;
                                c = '\n';
                            }
                            i3++;
                            c = '\n';
                        }
                    }
                    dp = AndroidUtilities.dp(52.0f);
                    size = LocaleController.isRTL ? (MeasureSpec.getSize(i) - AndroidUtilities.dp(10.0f)) - dp : AndroidUtilities.dp(10.0f);
                    r1.letterDrawable.setBounds(size, AndroidUtilities.dp(10.0f), size + dp, AndroidUtilities.dp(62.0f));
                    if (z) {
                        closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(r1.message.photoThumbs, dp, true);
                        closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(r1.message.photoThumbs, 80);
                        if (closestPhotoSizeWithSize2 == closestPhotoSizeWithSize) {
                            closestPhotoSizeWithSize2 = null;
                        }
                        closestPhotoSizeWithSize.size = -1;
                        if (closestPhotoSizeWithSize2 != null) {
                            closestPhotoSizeWithSize2.size = -1;
                        }
                        r1.linkImageView.setImageCoords(size, AndroidUtilities.dp(10.0f), dp, dp);
                        FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                        r1.linkImageView.setImage(closestPhotoSizeWithSize.location, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(dp), Integer.valueOf(dp)}), closestPhotoSizeWithSize2 != null ? closestPhotoSizeWithSize2.location : null, String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf(dp), Integer.valueOf(dp)}), 0, null, 0);
                        dp = 1;
                        r1.drawLinkImageView = true;
                    } else {
                        dp = 1;
                    }
                    if (r1.titleLayout != null) {
                    }
                    i4 = 0;
                    dp = 0;
                    dp += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                    dp += r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1);
                    if (r1.descriptionLayout != null) {
                        dp += AndroidUtilities.dp(10.0f);
                    }
                    while (i4 < r1.linkLayout.size()) {
                        staticLayout3 = (StaticLayout) r1.linkLayout.get(i4);
                        if (staticLayout3.getLineCount() <= 0) {
                            dp += staticLayout3.getLineBottom(staticLayout3.getLineCount() - 1);
                        }
                        i4++;
                    }
                    if (z) {
                        dp = Math.max(AndroidUtilities.dp(48.0f), dp);
                    }
                    r1.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM));
                    setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(72.0f), dp + AndroidUtilities.dp(16.0f)) + r1.needDivider);
                }
            } catch (Exception e9) {
                e2222 = e9;
                c = '\n';
                FileLog.m3e(e2222);
                r1.letterDrawable.setTitle(str3);
                if (str4 != null) {
                    r1.descriptionLayout = ChatMessageCell.generateStaticLayout(str4, r1.descriptionTextPaint, size, size, 0, 3);
                    if (r1.descriptionLayout.getLineCount() > 0) {
                        r1.description2Y = (r1.descriptionY + r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1)) + AndroidUtilities.dp(1.0f);
                    }
                }
                if (str5 != null) {
                    r1.descriptionLayout2 = ChatMessageCell.generateStaticLayout(str5, r1.descriptionTextPaint, size, size, 0, 3);
                    r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1);
                    if (r1.descriptionLayout != null) {
                        r1.description2Y += AndroidUtilities.dp(10.0f);
                    }
                }
                if (r1.links.isEmpty()) {
                    i3 = 0;
                    while (i3 < r1.links.size()) {
                        str6 = (String) r1.links.get(i3);
                        ellipsize = TextUtils.ellipsize(str6.replace(c, ' '), r1.descriptionTextPaint, (float) Math.min((int) Math.ceil((double) r1.descriptionTextPaint.measureText(str6)), size), TruncateAt.MIDDLE);
                        staticLayout = staticLayout;
                        staticLayout2 = staticLayout;
                        c2 = ' ';
                        staticLayout = new StaticLayout(ellipsize, r1.descriptionTextPaint, size, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        r1.linkY = r1.description2Y;
                        r1.linkY += r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1) + AndroidUtilities.dp(1.0f);
                        r1.linkLayout.add(staticLayout2);
                        i3++;
                        c = '\n';
                    }
                }
                dp = AndroidUtilities.dp(52.0f);
                if (LocaleController.isRTL) {
                }
                r1.letterDrawable.setBounds(size, AndroidUtilities.dp(10.0f), size + dp, AndroidUtilities.dp(62.0f));
                if (z) {
                    dp = 1;
                } else {
                    closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(r1.message.photoThumbs, dp, true);
                    closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(r1.message.photoThumbs, 80);
                    if (closestPhotoSizeWithSize2 == closestPhotoSizeWithSize) {
                        closestPhotoSizeWithSize2 = null;
                    }
                    closestPhotoSizeWithSize.size = -1;
                    if (closestPhotoSizeWithSize2 != null) {
                        closestPhotoSizeWithSize2.size = -1;
                    }
                    r1.linkImageView.setImageCoords(size, AndroidUtilities.dp(10.0f), dp, dp);
                    FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                    if (closestPhotoSizeWithSize2 != null) {
                    }
                    r1.linkImageView.setImage(closestPhotoSizeWithSize.location, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(dp), Integer.valueOf(dp)}), closestPhotoSizeWithSize2 != null ? closestPhotoSizeWithSize2.location : null, String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf(dp), Integer.valueOf(dp)}), 0, null, 0);
                    dp = 1;
                    r1.drawLinkImageView = true;
                }
                if (r1.titleLayout != null) {
                }
                i4 = 0;
                dp = 0;
                dp += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                dp += r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1);
                if (r1.descriptionLayout != null) {
                    dp += AndroidUtilities.dp(10.0f);
                }
                while (i4 < r1.linkLayout.size()) {
                    staticLayout3 = (StaticLayout) r1.linkLayout.get(i4);
                    if (staticLayout3.getLineCount() <= 0) {
                        dp += staticLayout3.getLineBottom(staticLayout3.getLineCount() - 1);
                    }
                    i4++;
                }
                if (z) {
                    dp = Math.max(AndroidUtilities.dp(48.0f), dp);
                }
                r1.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM));
                setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(72.0f), dp + AndroidUtilities.dp(16.0f)) + r1.needDivider);
            }
            r1.letterDrawable.setTitle(str3);
        } else {
            c = '\n';
        }
        if (str4 != null) {
            r1.descriptionLayout = ChatMessageCell.generateStaticLayout(str4, r1.descriptionTextPaint, size, size, 0, 3);
            if (r1.descriptionLayout.getLineCount() > 0) {
                r1.description2Y = (r1.descriptionY + r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1)) + AndroidUtilities.dp(1.0f);
            }
        }
        if (str5 != null) {
            r1.descriptionLayout2 = ChatMessageCell.generateStaticLayout(str5, r1.descriptionTextPaint, size, size, 0, 3);
            r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1);
            if (r1.descriptionLayout != null) {
                r1.description2Y += AndroidUtilities.dp(10.0f);
            }
        }
        if (r1.links.isEmpty()) {
            i3 = 0;
            while (i3 < r1.links.size()) {
                str6 = (String) r1.links.get(i3);
                ellipsize = TextUtils.ellipsize(str6.replace(c, ' '), r1.descriptionTextPaint, (float) Math.min((int) Math.ceil((double) r1.descriptionTextPaint.measureText(str6)), size), TruncateAt.MIDDLE);
                staticLayout = staticLayout;
                staticLayout2 = staticLayout;
                c2 = ' ';
                staticLayout = new StaticLayout(ellipsize, r1.descriptionTextPaint, size, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                r1.linkY = r1.description2Y;
                if (!(r1.descriptionLayout2 == null || r1.descriptionLayout2.getLineCount() == 0)) {
                    r1.linkY += r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1) + AndroidUtilities.dp(1.0f);
                }
                r1.linkLayout.add(staticLayout2);
                i3++;
                c = '\n';
            }
        }
        dp = AndroidUtilities.dp(52.0f);
        if (LocaleController.isRTL) {
        }
        r1.letterDrawable.setBounds(size, AndroidUtilities.dp(10.0f), size + dp, AndroidUtilities.dp(62.0f));
        if (z) {
            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(r1.message.photoThumbs, dp, true);
            closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(r1.message.photoThumbs, 80);
            if (closestPhotoSizeWithSize2 == closestPhotoSizeWithSize) {
                closestPhotoSizeWithSize2 = null;
            }
            closestPhotoSizeWithSize.size = -1;
            if (closestPhotoSizeWithSize2 != null) {
                closestPhotoSizeWithSize2.size = -1;
            }
            r1.linkImageView.setImageCoords(size, AndroidUtilities.dp(10.0f), dp, dp);
            FileLoader.getAttachFileName(closestPhotoSizeWithSize);
            if (closestPhotoSizeWithSize2 != null) {
            }
            r1.linkImageView.setImage(closestPhotoSizeWithSize.location, String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(dp), Integer.valueOf(dp)}), closestPhotoSizeWithSize2 != null ? closestPhotoSizeWithSize2.location : null, String.format(Locale.US, "%d_%d_b", new Object[]{Integer.valueOf(dp), Integer.valueOf(dp)}), 0, null, 0);
            dp = 1;
            r1.drawLinkImageView = true;
        } else {
            dp = 1;
        }
        if (r1.titleLayout != null || r1.titleLayout.getLineCount() == 0) {
            i4 = 0;
            dp = 0;
        } else {
            i4 = 0;
            dp = r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - dp) + 0;
        }
        if (!(r1.descriptionLayout == null || r1.descriptionLayout.getLineCount() == 0)) {
            dp += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
        }
        if (!(r1.descriptionLayout2 == null || r1.descriptionLayout2.getLineCount() == 0)) {
            dp += r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1);
            if (r1.descriptionLayout != null) {
                dp += AndroidUtilities.dp(10.0f);
            }
        }
        while (i4 < r1.linkLayout.size()) {
            staticLayout3 = (StaticLayout) r1.linkLayout.get(i4);
            if (staticLayout3.getLineCount() <= 0) {
                dp += staticLayout3.getLineBottom(staticLayout3.getLineCount() - 1);
            }
            i4++;
        }
        if (z) {
            dp = Math.max(AndroidUtilities.dp(48.0f), dp);
        }
        r1.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM));
        setMeasuredDimension(MeasureSpec.getSize(i), Math.max(AndroidUtilities.dp(72.0f), dp + AndroidUtilities.dp(16.0f)) + r1.needDivider);
    }

    public void setLink(MessageObject messageObject, boolean z) {
        this.needDivider = z;
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

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onAttachedToWindow();
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        int y;
        if (this.message == null || this.linkLayout.isEmpty() || this.delegate == null || !this.delegate.canPerformActions()) {
            resetPressedLink();
        } else {
            boolean z;
            if (motionEvent.getAction() != 0) {
                if (!this.linkPreviewPressed || motionEvent.getAction() != 1) {
                    if (motionEvent.getAction() == 3) {
                        resetPressedLink();
                    }
                }
            }
            int x = (int) motionEvent.getX();
            y = (int) motionEvent.getY();
            int i = 0;
            int i2 = i;
            while (i < this.linkLayout.size()) {
                StaticLayout staticLayout = (StaticLayout) this.linkLayout.get(i);
                if (staticLayout.getLineCount() > 0) {
                    int lineBottom = staticLayout.getLineBottom(staticLayout.getLineCount() - 1);
                    float f = (float) x;
                    float dp = (float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline);
                    if (f < staticLayout.getLineLeft(0) + dp || f > dp + staticLayout.getLineWidth(0) || y < this.linkY + i2 || y > (this.linkY + i2) + lineBottom) {
                        i2 += lineBottom;
                    } else {
                        if (motionEvent.getAction() == 0) {
                            resetPressedLink();
                            this.pressedLink = i;
                            this.linkPreviewPressed = true;
                            try {
                                this.urlPath.setCurrentLayout(staticLayout, 0, 0.0f);
                                staticLayout.getSelectionPath(0, staticLayout.getText().length(), this.urlPath);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        } else if (this.linkPreviewPressed) {
                            try {
                                WebPage webPage = (this.pressedLink != 0 || this.message.messageOwner.media == null) ? null : this.message.messageOwner.media.webpage;
                                if (webPage == null || webPage.embed_url == null || webPage.embed_url.length() == 0) {
                                    Browser.openUrl(getContext(), (String) this.links.get(this.pressedLink));
                                    resetPressedLink();
                                } else {
                                    this.delegate.needOpenWebView(webPage);
                                    resetPressedLink();
                                }
                            } catch (Throwable e2) {
                                FileLog.m3e(e2);
                            }
                        } else {
                            z = true;
                            y = 0;
                            if (!z) {
                                resetPressedLink();
                            }
                            if (y != 0) {
                                return true;
                            }
                            if (super.onTouchEvent(motionEvent) == null) {
                                return true;
                            }
                            return false;
                        }
                        z = true;
                        y = z;
                        if (z) {
                            resetPressedLink();
                        }
                        if (y != 0) {
                            return true;
                        }
                        if (super.onTouchEvent(motionEvent) == null) {
                            return false;
                        }
                        return true;
                    }
                }
                i++;
            }
            z = false;
            y = z;
            if (z) {
                resetPressedLink();
            }
            if (y != 0) {
                return true;
            }
            if (super.onTouchEvent(motionEvent) == null) {
                return true;
            }
            return false;
        }
        y = 0;
        if (y != 0) {
            return true;
        }
        if (super.onTouchEvent(motionEvent) == null) {
            return false;
        }
        return true;
    }

    public String getLink(int i) {
        if (i >= 0) {
            if (i < this.links.size()) {
                return (String) this.links.get(i);
            }
        }
        return 0;
    }

    protected void resetPressedLink() {
        this.pressedLink = -1;
        this.linkPreviewPressed = false;
        invalidate();
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(z, z2);
    }

    protected void onDraw(Canvas canvas) {
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            this.descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout2 != null) {
            this.descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.description2Y);
            this.descriptionLayout2.draw(canvas);
            canvas.restore();
        }
        if (!this.linkLayout.isEmpty()) {
            this.descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
            int i = 0;
            int i2 = 0;
            while (i < this.linkLayout.size()) {
                StaticLayout staticLayout = (StaticLayout) this.linkLayout.get(i);
                if (staticLayout.getLineCount() > 0) {
                    canvas.save();
                    canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) (this.linkY + i2));
                    if (this.pressedLink == i) {
                        canvas.drawPath(this.urlPath, Theme.linkSelectionPaint);
                    }
                    staticLayout.draw(canvas);
                    canvas.restore();
                    i2 += staticLayout.getLineBottom(staticLayout.getLineCount() - 1);
                }
                i++;
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
}
