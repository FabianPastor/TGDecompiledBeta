package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DialogCell;

public class LinkActionView extends LinearLayout {
    /* access modifiers changed from: private */
    public ActionBarPopupWindow actionBarPopupWindow;
    private final AvatarsContainer avatarsContainer;
    private boolean canEdit = true;
    /* access modifiers changed from: private */
    public final TextView copyView;
    private Delegate delegate;
    BaseFragment fragment;
    /* access modifiers changed from: private */
    public final FrameLayout frameLayout;
    private boolean hideRevokeOption;
    private boolean isChannel;
    String link;
    TextView linkView;
    boolean loadingImporters;
    ImageView optionsView;
    private boolean permanent;
    float[] point = new float[2];
    /* access modifiers changed from: private */
    public QRCodeBottomSheet qrCodeBottomSheet;
    private final TextView removeView;
    private boolean revoked;
    private final TextView shareView;
    /* access modifiers changed from: private */
    public int usersCount;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public LinkActionView(Context context, BaseFragment fragment2, BottomSheet bottomSheet, long chatId, boolean permanent2, boolean isChannel2) {
        super(context);
        Context context2 = context;
        BaseFragment baseFragment = fragment2;
        BottomSheet bottomSheet2 = bottomSheet;
        this.fragment = baseFragment;
        this.permanent = permanent2;
        this.isChannel = isChannel2;
        setOrientation(1);
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.frameLayout = frameLayout2;
        TextView textView = new TextView(context2);
        this.linkView = textView;
        textView.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(18.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(18.0f));
        this.linkView.setTextSize(1, 16.0f);
        this.linkView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        this.linkView.setSingleLine(true);
        frameLayout2.addView(this.linkView);
        ImageView imageView = new ImageView(context2);
        this.optionsView = imageView;
        imageView.setImageDrawable(ContextCompat.getDrawable(context2, NUM));
        this.optionsView.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.optionsView.setScaleType(ImageView.ScaleType.CENTER);
        frameLayout2.addView(this.optionsView, LayoutHelper.createFrame(40, 48, 21));
        addView(frameLayout2, LayoutHelper.createLinear(-1, -2, 0, 4, 0, 4, 0));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(0);
        TextView textView2 = new TextView(context2);
        this.copyView = textView2;
        textView2.setGravity(1);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append("..").setSpan(new ColoredImageSpan(ContextCompat.getDrawable(context2, NUM)), 0, 1, 0);
        spannableStringBuilder.setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(8.0f)), 1, 2, 0);
        spannableStringBuilder.append(LocaleController.getString("LinkActionCopy", NUM));
        FrameLayout frameLayout3 = frameLayout2;
        spannableStringBuilder.append(".").setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(5.0f)), spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 0);
        textView2.setText(spannableStringBuilder);
        textView2.setContentDescription(LocaleController.getString("LinkActionCopy", NUM));
        textView2.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        textView2.setTextSize(1, 14.0f);
        textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView2.setSingleLine(true);
        linearLayout.addView(textView2, LayoutHelper.createLinear(0, 40, 1.0f, 0, 4, 0, 4, 0));
        TextView textView3 = new TextView(context2);
        this.shareView = textView3;
        textView3.setGravity(1);
        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder();
        spannableStringBuilder2.append("..").setSpan(new ColoredImageSpan(ContextCompat.getDrawable(context2, NUM)), 0, 1, 0);
        spannableStringBuilder2.setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(8.0f)), 1, 2, 0);
        spannableStringBuilder2.append(LocaleController.getString("LinkActionShare", NUM));
        spannableStringBuilder2.append(".").setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(5.0f)), spannableStringBuilder2.length() - 1, spannableStringBuilder2.length(), 0);
        textView3.setText(spannableStringBuilder2);
        textView3.setContentDescription(LocaleController.getString("LinkActionShare", NUM));
        textView3.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        textView3.setTextSize(1, 14.0f);
        textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView3.setSingleLine(true);
        linearLayout.addView(textView3, LayoutHelper.createLinear(0, 40, 1.0f, 4, 0, 4, 0));
        TextView textView4 = new TextView(context2);
        this.removeView = textView4;
        textView4.setGravity(1);
        SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder();
        spannableStringBuilder3.append("..").setSpan(new ColoredImageSpan(ContextCompat.getDrawable(context2, NUM)), 0, 1, 0);
        spannableStringBuilder3.setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(8.0f)), 1, 2, 0);
        spannableStringBuilder3.append(LocaleController.getString("DeleteLink", NUM));
        spannableStringBuilder3.append(".").setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(5.0f)), spannableStringBuilder3.length() - 1, spannableStringBuilder3.length(), 0);
        textView4.setText(spannableStringBuilder3);
        textView4.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        textView4.setTextSize(1, 14.0f);
        textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView4.setSingleLine(true);
        linearLayout.addView(textView4, LayoutHelper.createLinear(0, -2, 1.0f, 4, 0, 4, 0));
        textView4.setVisibility(8);
        addView(linearLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 20.0f, 0.0f, 0.0f));
        AvatarsContainer avatarsContainer2 = new AvatarsContainer(context2);
        this.avatarsContainer = avatarsContainer2;
        addView(avatarsContainer2, LayoutHelper.createLinear(-1, 44, 0.0f, 12.0f, 0.0f, 0.0f));
        textView2.setOnClickListener(new LinkActionView$$ExternalSyntheticLambda12(this, bottomSheet2, baseFragment));
        if (permanent2) {
            avatarsContainer2.setOnClickListener(new LinkActionView$$ExternalSyntheticLambda5(this));
        }
        textView3.setOnClickListener(new LinkActionView$$ExternalSyntheticLambda10(this, baseFragment));
        textView4.setOnClickListener(new LinkActionView$$ExternalSyntheticLambda11(this, baseFragment));
        this.optionsView.setOnClickListener(new LinkActionView$$ExternalSyntheticLambda9(this, context2, bottomSheet2, baseFragment));
        frameLayout3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                LinkActionView.this.copyView.callOnClick();
            }
        });
        updateColors();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-LinkActionView  reason: not valid java name */
    public /* synthetic */ void m1097lambda$new$0$orgtelegramuiComponentsLinkActionView(BottomSheet bottomSheet, BaseFragment fragment2, View view) {
        try {
            if (this.link != null) {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.link));
                if (bottomSheet == null || bottomSheet.getContainer() == null) {
                    BulletinFactory.createCopyLinkBulletin(fragment2).show();
                } else {
                    BulletinFactory.createCopyLinkBulletin(bottomSheet.getContainer()).show();
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-LinkActionView  reason: not valid java name */
    public /* synthetic */ void m1098lambda$new$1$orgtelegramuiComponentsLinkActionView(View view) {
        this.delegate.showUsersForPermanentLink();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-LinkActionView  reason: not valid java name */
    public /* synthetic */ void m1099lambda$new$2$orgtelegramuiComponentsLinkActionView(BaseFragment fragment2, View view) {
        try {
            if (this.link != null) {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.TEXT", this.link);
                fragment2.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteToGroupByLink", NUM)), 500);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-LinkActionView  reason: not valid java name */
    public /* synthetic */ void m1101lambda$new$4$orgtelegramuiComponentsLinkActionView(BaseFragment fragment2, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) fragment2.getParentActivity());
        builder.setTitle(LocaleController.getString("DeleteLink", NUM));
        builder.setMessage(LocaleController.getString("DeleteLinkHelp", NUM));
        builder.setPositiveButton(LocaleController.getString("Delete", NUM), new LinkActionView$$ExternalSyntheticLambda0(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        fragment2.showDialog(builder.create());
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-LinkActionView  reason: not valid java name */
    public /* synthetic */ void m1100lambda$new$3$orgtelegramuiComponentsLinkActionView(DialogInterface dialogInterface2, int i2) {
        Delegate delegate2 = this.delegate;
        if (delegate2 != null) {
            delegate2.removeLink();
        }
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-LinkActionView  reason: not valid java name */
    public /* synthetic */ void m1106lambda$new$9$orgtelegramuiComponentsLinkActionView(Context context, BottomSheet bottomSheet, BaseFragment fragment2, View view) {
        FrameLayout container;
        Context context2 = context;
        if (this.actionBarPopupWindow == null) {
            ActionBarPopupWindow.ActionBarPopupWindowLayout layout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context2);
            if (!this.permanent && this.canEdit) {
                ActionBarMenuSubItem subItem = new ActionBarMenuSubItem(context2, true, false);
                subItem.setTextAndIcon(LocaleController.getString("Edit", NUM), NUM);
                layout.addView(subItem, LayoutHelper.createLinear(-1, 48));
                subItem.setOnClickListener(new LinkActionView$$ExternalSyntheticLambda6(this));
            }
            ActionBarMenuSubItem subItem2 = new ActionBarMenuSubItem(context2, true, false);
            subItem2.setTextAndIcon(LocaleController.getString("GetQRCode", NUM), NUM);
            layout.addView(subItem2, LayoutHelper.createLinear(-1, 48));
            subItem2.setOnClickListener(new LinkActionView$$ExternalSyntheticLambda7(this));
            if (!this.hideRevokeOption) {
                ActionBarMenuSubItem subItem3 = new ActionBarMenuSubItem(context2, false, true);
                subItem3.setTextAndIcon(LocaleController.getString("RevokeLink", NUM), NUM);
                subItem3.setColors(Theme.getColor("windowBackgroundWhiteRedText"), Theme.getColor("windowBackgroundWhiteRedText"));
                subItem3.setOnClickListener(new LinkActionView$$ExternalSyntheticLambda8(this));
                layout.addView(subItem3, LayoutHelper.createLinear(-1, 48));
            }
            if (bottomSheet == null) {
                container = fragment2.getParentLayout();
            } else {
                container = bottomSheet.getContainer();
            }
            if (container != null) {
                float x = 0.0f;
                getPointOnScreen(this.frameLayout, container, this.point);
                float y = this.point[1];
                final FrameLayout finalContainer = container;
                final View dimView = new View(context2) {
                    /* access modifiers changed from: protected */
                    public void onDraw(Canvas canvas) {
                        canvas.drawColor(NUM);
                        LinkActionView linkActionView = LinkActionView.this;
                        linkActionView.getPointOnScreen(linkActionView.frameLayout, finalContainer, LinkActionView.this.point);
                        canvas.save();
                        float clipTop = ((View) LinkActionView.this.frameLayout.getParent()).getY() + LinkActionView.this.frameLayout.getY();
                        if (clipTop < 1.0f) {
                            canvas.clipRect(0.0f, (LinkActionView.this.point[1] - clipTop) + 1.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
                        }
                        canvas.translate(LinkActionView.this.point[0], LinkActionView.this.point[1]);
                        LinkActionView.this.frameLayout.draw(canvas);
                        canvas.restore();
                    }
                };
                final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        dimView.invalidate();
                        return true;
                    }
                };
                finalContainer.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
                container.addView(dimView, LayoutHelper.createFrame(-1, -1.0f));
                dimView.setAlpha(0.0f);
                dimView.animate().alpha(1.0f).setDuration(150);
                layout.measure(View.MeasureSpec.makeMeasureSpec(container.getMeasuredWidth(), 0), View.MeasureSpec.makeMeasureSpec(container.getMeasuredHeight(), 0));
                ActionBarPopupWindow actionBarPopupWindow2 = new ActionBarPopupWindow(layout, -2, -2);
                this.actionBarPopupWindow = actionBarPopupWindow2;
                actionBarPopupWindow2.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    public void onDismiss() {
                        ActionBarPopupWindow unused = LinkActionView.this.actionBarPopupWindow = null;
                        dimView.animate().cancel();
                        dimView.animate().alpha(0.0f).setDuration(150).setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (dimView.getParent() != null) {
                                    finalContainer.removeView(dimView);
                                }
                                finalContainer.getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
                            }
                        });
                    }
                });
                this.actionBarPopupWindow.setOutsideTouchable(true);
                this.actionBarPopupWindow.setFocusable(true);
                this.actionBarPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
                this.actionBarPopupWindow.setAnimationStyle(NUM);
                this.actionBarPopupWindow.setInputMethodMode(2);
                this.actionBarPopupWindow.setSoftInputMode(0);
                layout.setDispatchKeyEventListener(new LinkActionView$$ExternalSyntheticLambda3(this));
                if (AndroidUtilities.isTablet()) {
                    y += (float) container.getPaddingTop();
                    x = 0.0f - ((float) container.getPaddingLeft());
                }
                this.actionBarPopupWindow.showAtLocation(container, 0, (int) (((float) ((container.getMeasuredWidth() - layout.getMeasuredWidth()) - AndroidUtilities.dp(16.0f))) + container.getX() + x), (int) (((float) this.frameLayout.getMeasuredHeight()) + y + container.getY()));
            }
        }
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-LinkActionView  reason: not valid java name */
    public /* synthetic */ void m1102lambda$new$5$orgtelegramuiComponentsLinkActionView(View view12) {
        ActionBarPopupWindow actionBarPopupWindow2 = this.actionBarPopupWindow;
        if (actionBarPopupWindow2 != null) {
            actionBarPopupWindow2.dismiss();
        }
        this.delegate.editLink();
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-LinkActionView  reason: not valid java name */
    public /* synthetic */ void m1103lambda$new$6$orgtelegramuiComponentsLinkActionView(View view12) {
        showQrCode();
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-LinkActionView  reason: not valid java name */
    public /* synthetic */ void m1104lambda$new$7$orgtelegramuiComponentsLinkActionView(View view1) {
        ActionBarPopupWindow actionBarPopupWindow2 = this.actionBarPopupWindow;
        if (actionBarPopupWindow2 != null) {
            actionBarPopupWindow2.dismiss();
        }
        revokeLink();
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-LinkActionView  reason: not valid java name */
    public /* synthetic */ void m1105lambda$new$8$orgtelegramuiComponentsLinkActionView(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && this.actionBarPopupWindow.isShowing()) {
            this.actionBarPopupWindow.dismiss(true);
        }
    }

    /* JADX WARNING: type inference failed for: r3v9, types: [android.view.ViewParent] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void getPointOnScreen(android.widget.FrameLayout r5, android.widget.FrameLayout r6, float[] r7) {
        /*
            r4 = this;
            r0 = 0
            r1 = 0
            r2 = r5
        L_0x0003:
            if (r2 == r6) goto L_0x0025
            float r3 = r2.getY()
            float r1 = r1 + r3
            float r3 = r2.getX()
            float r0 = r0 + r3
            boolean r3 = r2 instanceof android.widget.ScrollView
            if (r3 == 0) goto L_0x0019
            int r3 = r2.getScrollY()
            float r3 = (float) r3
            float r1 = r1 - r3
        L_0x0019:
            android.view.ViewParent r3 = r2.getParent()
            r2 = r3
            android.view.View r2 = (android.view.View) r2
            boolean r3 = r2 instanceof android.view.ViewGroup
            if (r3 != 0) goto L_0x0003
            return
        L_0x0025:
            int r3 = r6.getPaddingLeft()
            float r3 = (float) r3
            float r0 = r0 - r3
            int r3 = r6.getPaddingTop()
            float r3 = (float) r3
            float r1 = r1 - r3
            r3 = 0
            r7[r3] = r0
            r3 = 1
            r7[r3] = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.LinkActionView.getPointOnScreen(android.widget.FrameLayout, android.widget.FrameLayout, float[]):void");
    }

    private void showQrCode() {
        String str;
        int i;
        Context context = getContext();
        String str2 = this.link;
        if (this.isChannel) {
            i = NUM;
            str = "QRCodeLinkHelpChannel";
        } else {
            i = NUM;
            str = "QRCodeLinkHelpGroup";
        }
        AnonymousClass5 r0 = new QRCodeBottomSheet(context, str2, LocaleController.getString(str, i)) {
            public void dismiss() {
                super.dismiss();
                QRCodeBottomSheet unused = LinkActionView.this.qrCodeBottomSheet = null;
            }
        };
        this.qrCodeBottomSheet = r0;
        r0.show();
        ActionBarPopupWindow actionBarPopupWindow2 = this.actionBarPopupWindow;
        if (actionBarPopupWindow2 != null) {
            actionBarPopupWindow2.dismiss();
        }
    }

    public void updateColors() {
        this.copyView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.shareView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.removeView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        this.copyView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.shareView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        this.removeView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("chat_attachAudioBackground"), ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhite"), 120)));
        this.frameLayout.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("graySection"), ColorUtils.setAlphaComponent(Theme.getColor("listSelectorSDK21"), 76)));
        this.linkView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.optionsView.setColorFilter(Theme.getColor("dialogTextGray3"));
        this.avatarsContainer.countTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText"));
        this.avatarsContainer.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhiteBlueText"), 76)));
        QRCodeBottomSheet qRCodeBottomSheet = this.qrCodeBottomSheet;
        if (qRCodeBottomSheet != null) {
            qRCodeBottomSheet.updateColors();
        }
    }

    public void setLink(String link2) {
        this.link = link2;
        if (link2 == null) {
            this.linkView.setText(LocaleController.getString("Loading", NUM));
        } else if (link2.startsWith("https://")) {
            this.linkView.setText(link2.substring("https://".length()));
        } else {
            this.linkView.setText(link2);
        }
    }

    public void setRevoke(boolean revoked2) {
        this.revoked = revoked2;
        if (revoked2) {
            this.optionsView.setVisibility(8);
            this.shareView.setVisibility(8);
            this.copyView.setVisibility(8);
            this.removeView.setVisibility(0);
            return;
        }
        this.optionsView.setVisibility(0);
        this.shareView.setVisibility(0);
        this.copyView.setVisibility(0);
        this.removeView.setVisibility(8);
    }

    public void showOptions(boolean b) {
        this.optionsView.setVisibility(b ? 0 : 8);
    }

    public void hideRevokeOption(boolean b) {
        if (this.hideRevokeOption != b) {
            this.hideRevokeOption = b;
            this.optionsView.setVisibility(0);
            ImageView imageView = this.optionsView;
            imageView.setImageDrawable(ContextCompat.getDrawable(imageView.getContext(), NUM));
        }
    }

    private class AvatarsContainer extends FrameLayout {
        AvatarsImageView avatarsImageView;
        TextView countTextView;

        public AvatarsContainer(Context context) {
            super(context);
            this.avatarsImageView = new AvatarsImageView(context, false, LinkActionView.this) {
                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int N = Math.min(3, LinkActionView.this.usersCount);
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (N == 0 ? 0 : ((N - 1) * 20) + 24 + 8)), NUM), heightMeasureSpec);
                }
            };
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createFrame(-2, -1, 1));
            TextView textView = new TextView(context);
            this.countTextView = textView;
            textView.setTextSize(1, 14.0f);
            this.countTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            linearLayout.addView(this.avatarsImageView, LayoutHelper.createLinear(-2, -1));
            linearLayout.addView(this.countTextView, LayoutHelper.createLinear(-2, -2, 16));
            setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            this.avatarsImageView.commitTransition(false);
        }
    }

    private void revokeLink() {
        if (this.fragment.getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.fragment.getParentActivity());
            builder.setMessage(LocaleController.getString("RevokeAlert", NUM));
            builder.setTitle(LocaleController.getString("RevokeLink", NUM));
            builder.setPositiveButton(LocaleController.getString("RevokeButton", NUM), new LinkActionView$$ExternalSyntheticLambda4(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.show();
        }
    }

    /* renamed from: lambda$revokeLink$10$org-telegram-ui-Components-LinkActionView  reason: not valid java name */
    public /* synthetic */ void m1107lambda$revokeLink$10$orgtelegramuiComponentsLinkActionView(DialogInterface dialogInterface, int i) {
        Delegate delegate2 = this.delegate;
        if (delegate2 != null) {
            delegate2.revokeLink();
        }
    }

    public void setDelegate(Delegate delegate2) {
        this.delegate = delegate2;
    }

    public void setUsers(int usersCount2, ArrayList<TLRPC.User> importers) {
        this.usersCount = usersCount2;
        if (usersCount2 == 0) {
            this.avatarsContainer.setVisibility(8);
            setPadding(AndroidUtilities.dp(19.0f), AndroidUtilities.dp(18.0f), AndroidUtilities.dp(19.0f), AndroidUtilities.dp(18.0f));
        } else {
            this.avatarsContainer.setVisibility(0);
            setPadding(AndroidUtilities.dp(19.0f), AndroidUtilities.dp(18.0f), AndroidUtilities.dp(19.0f), AndroidUtilities.dp(10.0f));
            this.avatarsContainer.countTextView.setText(LocaleController.formatPluralString("PeopleJoined", usersCount2, new Object[0]));
            this.avatarsContainer.requestLayout();
        }
        if (importers != null) {
            for (int i = 0; i < 3; i++) {
                if (i < importers.size()) {
                    MessagesController.getInstance(UserConfig.selectedAccount).putUser(importers.get(i), false);
                    this.avatarsContainer.avatarsImageView.setObject(i, UserConfig.selectedAccount, importers.get(i));
                } else {
                    this.avatarsContainer.avatarsImageView.setObject(i, UserConfig.selectedAccount, (TLObject) null);
                }
            }
            this.avatarsContainer.avatarsImageView.commitTransition(false);
        }
    }

    public void loadUsers(TLRPC.TL_chatInviteExported invite, long chatId) {
        if (invite == null) {
            setUsers(0, (ArrayList<TLRPC.User>) null);
            return;
        }
        setUsers(invite.usage, invite.importers);
        if (invite.usage > 0 && invite.importers == null && !this.loadingImporters) {
            TLRPC.TL_messages_getChatInviteImporters req = new TLRPC.TL_messages_getChatInviteImporters();
            req.link = invite.link;
            req.peer = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer(-chatId);
            req.offset_user = new TLRPC.TL_inputUserEmpty();
            req.limit = Math.min(invite.usage, 3);
            this.loadingImporters = true;
            ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, new LinkActionView$$ExternalSyntheticLambda2(this, invite));
        }
    }

    /* renamed from: lambda$loadUsers$12$org-telegram-ui-Components-LinkActionView  reason: not valid java name */
    public /* synthetic */ void m1096lambda$loadUsers$12$orgtelegramuiComponentsLinkActionView(TLRPC.TL_chatInviteExported invite, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new LinkActionView$$ExternalSyntheticLambda1(this, error, response, invite));
    }

    /* renamed from: lambda$loadUsers$11$org-telegram-ui-Components-LinkActionView  reason: not valid java name */
    public /* synthetic */ void m1095lambda$loadUsers$11$orgtelegramuiComponentsLinkActionView(TLRPC.TL_error error, TLObject response, TLRPC.TL_chatInviteExported invite) {
        this.loadingImporters = false;
        if (error == null) {
            TLRPC.TL_messages_chatInviteImporters inviteImporters = (TLRPC.TL_messages_chatInviteImporters) response;
            if (invite.importers == null) {
                invite.importers = new ArrayList<>(3);
            }
            invite.importers.clear();
            for (int i = 0; i < inviteImporters.users.size(); i++) {
                invite.importers.addAll(inviteImporters.users);
            }
            setUsers(invite.usage, invite.importers);
        }
    }

    public interface Delegate {
        void editLink();

        void removeLink();

        void revokeLink();

        void showUsersForPermanentLink();

        /* renamed from: org.telegram.ui.Components.LinkActionView$Delegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static void $default$editLink(Delegate _this) {
            }

            public static void $default$removeLink(Delegate _this) {
            }

            public static void $default$showUsersForPermanentLink(Delegate _this) {
            }
        }
    }

    public void setPermanent(boolean permanent2) {
        this.permanent = permanent2;
    }

    public void setCanEdit(boolean canEdit2) {
        this.canEdit = canEdit2;
    }
}
