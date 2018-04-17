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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.FileLocation;
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
        this.checkBox = new CheckBox(context, R.drawable.round_check2);
        this.checkBox.setVisibility(4);
        this.checkBox.setColor(Theme.getColor(Theme.key_checkbox), Theme.getColor(Theme.key_checkboxCheck));
        addView(this.checkBox, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 44.0f, 44.0f, LocaleController.isRTL ? 44.0f : 0.0f, 0.0f));
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        String title;
        String description2;
        String description;
        String description22;
        Throwable e;
        int width;
        StaticLayout staticLayout;
        char c;
        Throwable e2;
        float f;
        int height;
        String link;
        int width2;
        int a;
        String title2;
        float f2;
        Throwable e3;
        int maxPhotoWidth;
        float f3;
        int x;
        PhotoSize currentPhotoObject;
        PhotoSize currentPhotoObjectThumb;
        ImageReceiver imageReceiver;
        TLObject tLObject;
        FileLocation fileLocation;
        Object[] objArr;
        int i;
        int height2;
        StaticLayout layout;
        this.drawLinkImageView = false;
        this.descriptionLayout = null;
        this.titleLayout = null;
        this.descriptionLayout2 = null;
        this.description2Y = this.descriptionY;
        this.linkLayout.clear();
        this.links.clear();
        int maxWidth = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - AndroidUtilities.dp(8.0f);
        String webPageLink = null;
        String title3 = null;
        String description3 = null;
        boolean hasPhoto = false;
        boolean z = true;
        if ((this.message.messageOwner.media instanceof TL_messageMediaWebPage) && (r1.message.messageOwner.media.webpage instanceof TL_webPage)) {
            WebPage webPage = r1.message.messageOwner.media.webpage;
            if (r1.message.photoThumbs == null && webPage.photo != null) {
                r1.message.generateThumbs(true);
            }
            boolean z2 = (webPage.photo == null || r1.message.photoThumbs == null) ? false : true;
            hasPhoto = z2;
            webPageLink = webPage.title;
            if (webPageLink == null) {
                webPageLink = webPage.site_name;
            }
            title3 = webPage.description;
            description3 = webPage.url;
        }
        boolean hasPhoto2 = hasPhoto;
        String str = title3;
        title3 = webPageLink;
        webPageLink = description3;
        description3 = str;
        if (r1.message == null || r1.message.messageOwner.entities.isEmpty()) {
            title = title3;
            description2 = null;
            description = description3;
        } else {
            description22 = null;
            String description23 = title3;
            title3 = null;
            while (title3 < r1.message.messageOwner.entities.size()) {
                MessageEntity entity = (MessageEntity) r1.message.messageOwner.entities.get(title3);
                if (entity.length > 0 && entity.offset >= 0) {
                    if (entity.offset < r1.message.messageOwner.message.length()) {
                        if (entity.offset + entity.length > r1.message.messageOwner.message.length()) {
                            entity.length = r1.message.messageOwner.message.length() - entity.offset;
                        }
                        if (!(title3 != null || webPageLink == null || (entity.offset == 0 && entity.length == r1.message.messageOwner.message.length()))) {
                            if (r1.message.messageOwner.entities.size() != z) {
                                description22 = r1.message.messageOwner.message;
                            } else if (description3 == null) {
                                description22 = r1.message.messageOwner.message;
                            }
                        }
                        String link2 = null;
                        try {
                            if (!(entity instanceof TL_messageEntityTextUrl)) {
                                if (!(entity instanceof TL_messageEntityUrl)) {
                                    if ((entity instanceof TL_messageEntityEmail) && (description23 == null || description23.length() == 0)) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append("mailto:");
                                        stringBuilder.append(r1.message.messageOwner.message.substring(entity.offset, entity.offset + entity.length));
                                        link2 = stringBuilder.toString();
                                        title = r1.message.messageOwner.message.substring(entity.offset, entity.offset + entity.length);
                                        try {
                                            if (!(entity.offset == 0 && entity.length == r1.message.messageOwner.message.length())) {
                                                description23 = title;
                                                description3 = r1.message.messageOwner.message;
                                            }
                                            description23 = title;
                                        } catch (Exception e4) {
                                            e = e4;
                                            description23 = title;
                                            FileLog.m3e(e);
                                            title3++;
                                            z = true;
                                        }
                                    }
                                    if (link2 != null) {
                                        if (link2.toLowerCase().indexOf("http") != 0 || link2.toLowerCase().indexOf("mailto") == 0) {
                                            r1.links.add(link2);
                                        } else {
                                            ArrayList arrayList = r1.links;
                                            StringBuilder stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append("http://");
                                            stringBuilder2.append(link2);
                                            arrayList.add(stringBuilder2.toString());
                                        }
                                    }
                                }
                            }
                            if (entity instanceof TL_messageEntityUrl) {
                                title = r1.message.messageOwner.message.substring(entity.offset, entity.offset + entity.length);
                            } else {
                                title = entity.url;
                            }
                            link2 = title;
                            if (description23 == null || description23.length() == 0) {
                                title = link2;
                                Uri uri = Uri.parse(title);
                                title = uri.getHost();
                                if (title == null) {
                                    title = link2;
                                }
                                if (title != null) {
                                    int lastIndexOf = title.lastIndexOf(46);
                                    int index = lastIndexOf;
                                    if (lastIndexOf >= 0) {
                                        title = title.substring(0, index);
                                        description23 = title.lastIndexOf(46);
                                        lastIndexOf = description23;
                                        if (description23 >= null) {
                                            title = title.substring(lastIndexOf + 1);
                                        }
                                        description23 = new StringBuilder();
                                        description23.append(title.substring(0, 1).toUpperCase());
                                        description23.append(title.substring(1));
                                        title = description23.toString();
                                        if (!(entity.offset == 0 && entity.length == r1.message.messageOwner.message.length())) {
                                            description3 = r1.message.messageOwner.message;
                                        }
                                        description23 = title;
                                    }
                                }
                                description3 = r1.message.messageOwner.message;
                                description23 = title;
                            }
                            if (link2 != null) {
                                if (link2.toLowerCase().indexOf("http") != 0) {
                                }
                                r1.links.add(link2);
                            }
                        } catch (Exception e5) {
                            e = e5;
                            FileLog.m3e(e);
                            title3++;
                            z = true;
                        }
                    }
                }
                title3++;
                z = true;
            }
            title = description23;
            description = description3;
            description2 = description22;
        }
        if (webPageLink != null && r1.links.isEmpty()) {
            r1.links.add(webPageLink);
        }
        if (title != null) {
            try {
                width = (int) Math.ceil((double) r1.titleTextPaint.measureText(title));
                staticLayout = staticLayout;
                StaticLayout staticLayout2 = staticLayout;
                c = 32;
                try {
                    staticLayout = new StaticLayout(TextUtils.ellipsize(title.replace('\n', ' '), r1.titleTextPaint, (float) Math.min(width, maxWidth), TruncateAt.END), r1.titleTextPaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    r1.titleLayout = staticLayout2;
                } catch (Throwable e6) {
                    e2 = e6;
                    FileLog.m3e(e2);
                    r1.letterDrawable.setTitle(title);
                    f = 1.0f;
                    if (description != null) {
                        try {
                            r1.descriptionLayout = ChatMessageCell.generateStaticLayout(description, r1.descriptionTextPaint, maxWidth, maxWidth, 0, 3);
                            if (r1.descriptionLayout.getLineCount() > 0) {
                                r1.description2Y = (r1.descriptionY + r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1)) + AndroidUtilities.dp(1.0f);
                            }
                        } catch (Throwable e62) {
                            FileLog.m3e(e62);
                        }
                    }
                    if (description2 != null) {
                        try {
                            r1.descriptionLayout2 = ChatMessageCell.generateStaticLayout(description2, r1.descriptionTextPaint, maxWidth, maxWidth, 0, 3);
                            height = r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1);
                            if (r1.descriptionLayout != null) {
                                r1.description2Y += AndroidUtilities.dp(10.0f);
                            }
                        } catch (Throwable e622) {
                            FileLog.m3e(e622);
                        }
                    }
                    if (!r1.links.isEmpty()) {
                        height = 0;
                        while (true) {
                            width = height;
                            if (width < r1.links.size()) {
                                break;
                            }
                            try {
                                link = (String) r1.links.get(width);
                                width2 = (int) Math.ceil((double) r1.descriptionTextPaint.measureText(link));
                                try {
                                    staticLayout = staticLayout;
                                    a = width;
                                    title2 = title;
                                    f2 = f;
                                } catch (Exception e7) {
                                    e622 = e7;
                                    title2 = title;
                                    char c2 = '\n';
                                    a = width;
                                    f2 = f;
                                    e3 = e622;
                                    FileLog.m3e(e3);
                                    height = a + 1;
                                    f = f2;
                                    title = title2;
                                    c = ' ';
                                }
                                try {
                                    staticLayout = new StaticLayout(TextUtils.ellipsize(link.replace('\n', c), r1.descriptionTextPaint, (float) Math.min(width2, maxWidth), TruncateAt.MIDDLE), r1.descriptionTextPaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                    r1.linkY = r1.description2Y;
                                    r1.linkY += r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1) + AndroidUtilities.dp(f2);
                                    r1.linkLayout.add(staticLayout);
                                } catch (Throwable e6222) {
                                    e3 = e6222;
                                    FileLog.m3e(e3);
                                    height = a + 1;
                                    f = f2;
                                    title = title2;
                                    c = ' ';
                                }
                            } catch (Exception e8) {
                                e6222 = e8;
                                title2 = title;
                                a = width;
                                f2 = f;
                                e3 = e6222;
                                FileLog.m3e(e3);
                                height = a + 1;
                                f = f2;
                                title = title2;
                                c = ' ';
                            }
                            height = a + 1;
                            f = f2;
                            title = title2;
                            c = ' ';
                        }
                    }
                    maxPhotoWidth = AndroidUtilities.dp(NUM);
                    if (LocaleController.isRTL) {
                        f3 = 10.0f;
                        x = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(10.0f)) - maxPhotoWidth;
                    } else {
                        f3 = 10.0f;
                        x = AndroidUtilities.dp(10.0f);
                    }
                    r1.letterDrawable.setBounds(x, AndroidUtilities.dp(f3), x + maxPhotoWidth, AndroidUtilities.dp(62.0f));
                    if (hasPhoto2) {
                        currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(r1.message.photoThumbs, maxPhotoWidth, true);
                        currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(r1.message.photoThumbs, 80);
                        if (currentPhotoObjectThumb == currentPhotoObject) {
                            currentPhotoObjectThumb = null;
                        }
                        currentPhotoObject.size = -1;
                        if (currentPhotoObjectThumb != null) {
                            currentPhotoObjectThumb.size = -1;
                        }
                        r1.linkImageView.setImageCoords(x, AndroidUtilities.dp(f3), maxPhotoWidth, maxPhotoWidth);
                        description22 = FileLoader.getAttachFileName(currentPhotoObject);
                        link = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(maxPhotoWidth), Integer.valueOf(maxPhotoWidth)});
                        imageReceiver = r1.linkImageView;
                        tLObject = currentPhotoObject.location;
                        fileLocation = currentPhotoObjectThumb != null ? currentPhotoObjectThumb.location : null;
                        objArr = new Object[2];
                        i = 0;
                        objArr[0] = Integer.valueOf(maxPhotoWidth);
                        objArr[1] = Integer.valueOf(maxPhotoWidth);
                        imageReceiver.setImage(tLObject, link, fileLocation, String.format(Locale.US, "%d_%d_b", objArr), 0, null, 0);
                        r1.drawLinkImageView = true;
                    } else {
                        i = 0;
                    }
                    height2 = 0;
                    height2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                    height2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                    height2 += r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1);
                    if (r1.descriptionLayout != null) {
                        height2 += AndroidUtilities.dp(10.0f);
                    }
                    while (true) {
                        height = i;
                        if (height < r1.linkLayout.size()) {
                            break;
                        }
                        layout = (StaticLayout) r1.linkLayout.get(height);
                        if (layout.getLineCount() <= 0) {
                            height2 += layout.getLineBottom(layout.getLineCount() - 1);
                        }
                        i = height + 1;
                    }
                    if (hasPhoto2) {
                        height2 = Math.max(AndroidUtilities.dp(48.0f), height2);
                    }
                    r1.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM));
                    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(72.0f), AndroidUtilities.dp(16.0f) + height2) + r1.needDivider);
                }
            } catch (Throwable e62222) {
                String str2 = webPageLink;
                c = 32;
                e2 = e62222;
                FileLog.m3e(e2);
                r1.letterDrawable.setTitle(title);
                f = 1.0f;
                if (description != null) {
                    r1.descriptionLayout = ChatMessageCell.generateStaticLayout(description, r1.descriptionTextPaint, maxWidth, maxWidth, 0, 3);
                    if (r1.descriptionLayout.getLineCount() > 0) {
                        r1.description2Y = (r1.descriptionY + r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1)) + AndroidUtilities.dp(1.0f);
                    }
                }
                if (description2 != null) {
                    r1.descriptionLayout2 = ChatMessageCell.generateStaticLayout(description2, r1.descriptionTextPaint, maxWidth, maxWidth, 0, 3);
                    height = r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1);
                    if (r1.descriptionLayout != null) {
                        r1.description2Y += AndroidUtilities.dp(10.0f);
                    }
                }
                if (!r1.links.isEmpty()) {
                    height = 0;
                    while (true) {
                        width = height;
                        if (width < r1.links.size()) {
                            break;
                        }
                        link = (String) r1.links.get(width);
                        width2 = (int) Math.ceil((double) r1.descriptionTextPaint.measureText(link));
                        staticLayout = staticLayout;
                        a = width;
                        title2 = title;
                        f2 = f;
                        staticLayout = new StaticLayout(TextUtils.ellipsize(link.replace('\n', c), r1.descriptionTextPaint, (float) Math.min(width2, maxWidth), TruncateAt.MIDDLE), r1.descriptionTextPaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        r1.linkY = r1.description2Y;
                        r1.linkY += r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1) + AndroidUtilities.dp(f2);
                        r1.linkLayout.add(staticLayout);
                        height = a + 1;
                        f = f2;
                        title = title2;
                        c = ' ';
                    }
                }
                maxPhotoWidth = AndroidUtilities.dp(NUM);
                if (LocaleController.isRTL) {
                    f3 = 10.0f;
                    x = AndroidUtilities.dp(10.0f);
                } else {
                    f3 = 10.0f;
                    x = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(10.0f)) - maxPhotoWidth;
                }
                r1.letterDrawable.setBounds(x, AndroidUtilities.dp(f3), x + maxPhotoWidth, AndroidUtilities.dp(62.0f));
                if (hasPhoto2) {
                    i = 0;
                } else {
                    currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(r1.message.photoThumbs, maxPhotoWidth, true);
                    currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(r1.message.photoThumbs, 80);
                    if (currentPhotoObjectThumb == currentPhotoObject) {
                        currentPhotoObjectThumb = null;
                    }
                    currentPhotoObject.size = -1;
                    if (currentPhotoObjectThumb != null) {
                        currentPhotoObjectThumb.size = -1;
                    }
                    r1.linkImageView.setImageCoords(x, AndroidUtilities.dp(f3), maxPhotoWidth, maxPhotoWidth);
                    description22 = FileLoader.getAttachFileName(currentPhotoObject);
                    link = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(maxPhotoWidth), Integer.valueOf(maxPhotoWidth)});
                    imageReceiver = r1.linkImageView;
                    tLObject = currentPhotoObject.location;
                    if (currentPhotoObjectThumb != null) {
                    }
                    objArr = new Object[2];
                    i = 0;
                    objArr[0] = Integer.valueOf(maxPhotoWidth);
                    objArr[1] = Integer.valueOf(maxPhotoWidth);
                    imageReceiver.setImage(tLObject, link, fileLocation, String.format(Locale.US, "%d_%d_b", objArr), 0, null, 0);
                    r1.drawLinkImageView = true;
                }
                height2 = 0;
                height2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
                height2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
                height2 += r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1);
                if (r1.descriptionLayout != null) {
                    height2 += AndroidUtilities.dp(10.0f);
                }
                while (true) {
                    height = i;
                    if (height < r1.linkLayout.size()) {
                        break;
                    }
                    layout = (StaticLayout) r1.linkLayout.get(height);
                    if (layout.getLineCount() <= 0) {
                        height2 += layout.getLineBottom(layout.getLineCount() - 1);
                    }
                    i = height + 1;
                }
                if (hasPhoto2) {
                    height2 = Math.max(AndroidUtilities.dp(48.0f), height2);
                }
                r1.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM));
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(72.0f), AndroidUtilities.dp(16.0f) + height2) + r1.needDivider);
            }
            r1.letterDrawable.setTitle(title);
        } else {
            c = 32;
        }
        f = 1.0f;
        if (description != null) {
            r1.descriptionLayout = ChatMessageCell.generateStaticLayout(description, r1.descriptionTextPaint, maxWidth, maxWidth, 0, 3);
            if (r1.descriptionLayout.getLineCount() > 0) {
                r1.description2Y = (r1.descriptionY + r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1)) + AndroidUtilities.dp(1.0f);
            }
        }
        if (description2 != null) {
            r1.descriptionLayout2 = ChatMessageCell.generateStaticLayout(description2, r1.descriptionTextPaint, maxWidth, maxWidth, 0, 3);
            height = r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1);
            if (r1.descriptionLayout != null) {
                r1.description2Y += AndroidUtilities.dp(10.0f);
            }
        }
        if (!r1.links.isEmpty()) {
            height = 0;
            while (true) {
                width = height;
                if (width < r1.links.size()) {
                    break;
                }
                link = (String) r1.links.get(width);
                width2 = (int) Math.ceil((double) r1.descriptionTextPaint.measureText(link));
                staticLayout = staticLayout;
                a = width;
                title2 = title;
                f2 = f;
                staticLayout = new StaticLayout(TextUtils.ellipsize(link.replace('\n', c), r1.descriptionTextPaint, (float) Math.min(width2, maxWidth), TruncateAt.MIDDLE), r1.descriptionTextPaint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                r1.linkY = r1.description2Y;
                if (!(r1.descriptionLayout2 == null || r1.descriptionLayout2.getLineCount() == 0)) {
                    r1.linkY += r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1) + AndroidUtilities.dp(f2);
                }
                r1.linkLayout.add(staticLayout);
                height = a + 1;
                f = f2;
                title = title2;
                c = ' ';
            }
        }
        maxPhotoWidth = AndroidUtilities.dp(NUM);
        if (LocaleController.isRTL) {
            f3 = 10.0f;
            x = (MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(10.0f)) - maxPhotoWidth;
        } else {
            f3 = 10.0f;
            x = AndroidUtilities.dp(10.0f);
        }
        r1.letterDrawable.setBounds(x, AndroidUtilities.dp(f3), x + maxPhotoWidth, AndroidUtilities.dp(62.0f));
        if (hasPhoto2) {
            currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(r1.message.photoThumbs, maxPhotoWidth, true);
            currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(r1.message.photoThumbs, 80);
            if (currentPhotoObjectThumb == currentPhotoObject) {
                currentPhotoObjectThumb = null;
            }
            currentPhotoObject.size = -1;
            if (currentPhotoObjectThumb != null) {
                currentPhotoObjectThumb.size = -1;
            }
            r1.linkImageView.setImageCoords(x, AndroidUtilities.dp(f3), maxPhotoWidth, maxPhotoWidth);
            description22 = FileLoader.getAttachFileName(currentPhotoObject);
            link = String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf(maxPhotoWidth), Integer.valueOf(maxPhotoWidth)});
            imageReceiver = r1.linkImageView;
            tLObject = currentPhotoObject.location;
            if (currentPhotoObjectThumb != null) {
            }
            objArr = new Object[2];
            i = 0;
            objArr[0] = Integer.valueOf(maxPhotoWidth);
            objArr[1] = Integer.valueOf(maxPhotoWidth);
            imageReceiver.setImage(tLObject, link, fileLocation, String.format(Locale.US, "%d_%d_b", objArr), 0, null, 0);
            r1.drawLinkImageView = true;
        } else {
            i = 0;
        }
        height2 = 0;
        if (!(r1.titleLayout == null || r1.titleLayout.getLineCount() == 0)) {
            height2 = 0 + r1.titleLayout.getLineBottom(r1.titleLayout.getLineCount() - 1);
        }
        if (!(r1.descriptionLayout == null || r1.descriptionLayout.getLineCount() == 0)) {
            height2 += r1.descriptionLayout.getLineBottom(r1.descriptionLayout.getLineCount() - 1);
        }
        if (!(r1.descriptionLayout2 == null || r1.descriptionLayout2.getLineCount() == 0)) {
            height2 += r1.descriptionLayout2.getLineBottom(r1.descriptionLayout2.getLineCount() - 1);
            if (r1.descriptionLayout != null) {
                height2 += AndroidUtilities.dp(10.0f);
            }
        }
        while (true) {
            height = i;
            if (height < r1.linkLayout.size()) {
                break;
            }
            layout = (StaticLayout) r1.linkLayout.get(height);
            if (layout.getLineCount() <= 0) {
                height2 += layout.getLineBottom(layout.getLineCount() - 1);
            }
            i = height + 1;
        }
        if (hasPhoto2) {
            height2 = Math.max(AndroidUtilities.dp(48.0f), height2);
        }
        r1.checkBox.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0f), NUM));
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), Math.max(AndroidUtilities.dp(72.0f), AndroidUtilities.dp(16.0f) + height2) + r1.needDivider);
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

    public boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        boolean z = true;
        if (this.message == null || r1.linkLayout.isEmpty() || r1.delegate == null || !r1.delegate.canPerformActions()) {
            resetPressedLink();
        } else {
            if (event.getAction() != 0) {
                if (!r1.linkPreviewPressed || event.getAction() != 1) {
                    if (event.getAction() == 3) {
                        resetPressedLink();
                    }
                }
            }
            int x = (int) event.getX();
            int y = (int) event.getY();
            boolean ok = false;
            int offset = 0;
            int a = 0;
            while (a < r1.linkLayout.size()) {
                StaticLayout layout = (StaticLayout) r1.linkLayout.get(a);
                if (layout.getLineCount() > 0) {
                    int height = layout.getLineBottom(layout.getLineCount() - z);
                    int linkPosX = AndroidUtilities.dp(LocaleController.isRTL ? NUM : (float) AndroidUtilities.leftBaseline);
                    if (((float) x) < ((float) linkPosX) + layout.getLineLeft(0) || ((float) x) > ((float) linkPosX) + layout.getLineWidth(0) || y < r1.linkY + offset || y > (r1.linkY + offset) + height) {
                        offset += height;
                    } else {
                        ok = true;
                        if (event.getAction() == 0) {
                            resetPressedLink();
                            r1.pressedLink = a;
                            r1.linkPreviewPressed = z;
                            try {
                                r1.urlPath.setCurrentLayout(layout, 0, 0.0f);
                                layout.getSelectionPath(0, layout.getText().length(), r1.urlPath);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                            result = true;
                        } else if (r1.linkPreviewPressed) {
                            try {
                                WebPage webPage = (r1.pressedLink != 0 || r1.message.messageOwner.media == null) ? null : r1.message.messageOwner.media.webpage;
                                if (webPage == null || webPage.embed_url == null || webPage.embed_url.length() == 0) {
                                    Browser.openUrl(getContext(), (String) r1.links.get(r1.pressedLink));
                                } else {
                                    r1.delegate.needOpenWebView(webPage);
                                }
                            } catch (Throwable e2) {
                                FileLog.m3e(e2);
                            }
                            resetPressedLink();
                            result = true;
                        }
                        if (!ok) {
                            resetPressedLink();
                        }
                    }
                }
                a++;
                z = true;
            }
            if (ok) {
                resetPressedLink();
            }
        }
        if (!result) {
            if (!super.onTouchEvent(event)) {
                return false;
            }
        }
        return true;
    }

    public String getLink(int num) {
        if (num >= 0) {
            if (num < this.links.size()) {
                return (String) this.links.get(num);
            }
        }
        return null;
    }

    protected void resetPressedLink() {
        this.pressedLink = -1;
        this.linkPreviewPressed = false;
        invalidate();
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(checked, animated);
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
            int offset = 0;
            for (int a = 0; a < this.linkLayout.size(); a++) {
                StaticLayout layout = (StaticLayout) this.linkLayout.get(a);
                if (layout.getLineCount() > 0) {
                    canvas.save();
                    canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) (this.linkY + offset));
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
}
