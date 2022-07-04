package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.widget.NestedScrollView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ManageLinksActivity;

public class PermanentLinkBottomSheet extends BottomSheet {
    private long chatId;
    private BaseFragment fragment;
    private final RLottieImageView imageView;
    TLRPC.ChatFull info;
    TLRPC.TL_chatInviteExported invite;
    private boolean isChannel;
    private final LinkActionView linkActionView;
    boolean linkGenerating;
    RLottieDrawable linkIcon;
    private final TextView manage;
    private final TextView subtitle;
    private final TextView titleView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PermanentLinkBottomSheet(Context context, boolean needFocus, BaseFragment fragment2, TLRPC.ChatFull info2, long chatId2, boolean isChannel2) {
        super(context, needFocus);
        String str;
        int i;
        Context context2 = context;
        TLRPC.ChatFull chatFull = info2;
        boolean z = isChannel2;
        this.info = chatFull;
        this.chatId = chatId2;
        this.isChannel = z;
        setAllowNestedScroll(true);
        setApplyBottomPadding(false);
        LinkActionView linkActionView2 = r0;
        LinkActionView linkActionView3 = new LinkActionView(context, fragment2, this, chatId2, true, isChannel2);
        this.linkActionView = linkActionView2;
        linkActionView2.setPermanent(true);
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.imageView = rLottieImageView;
        RLottieDrawable rLottieDrawable = r1;
        RLottieDrawable rLottieDrawable2 = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(90.0f), AndroidUtilities.dp(90.0f), false, (int[]) null);
        this.linkIcon = rLottieDrawable;
        rLottieDrawable.setCustomEndFrame(42);
        rLottieImageView.setAnimation(this.linkIcon);
        linkActionView2.setUsers(0, (ArrayList<TLRPC.User>) null);
        linkActionView2.hideRevokeOption(true);
        linkActionView2.setDelegate(new PermanentLinkBottomSheet$$ExternalSyntheticLambda5(this));
        TextView textView = new TextView(context2);
        this.titleView = textView;
        textView.setText(LocaleController.getString("InviteLink", NUM));
        textView.setTextSize(24.0f);
        textView.setGravity(1);
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        TextView textView2 = new TextView(context2);
        this.subtitle = textView2;
        if (z) {
            i = NUM;
            str = "LinkInfoChannel";
        } else {
            i = NUM;
            str = "LinkInfo";
        }
        textView2.setText(LocaleController.getString(str, i));
        textView2.setTextSize(14.0f);
        textView2.setGravity(1);
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        TextView textView3 = new TextView(context2);
        this.manage = textView3;
        textView3.setText(LocaleController.getString("ManageInviteLinks", NUM));
        textView3.setTextSize(14.0f);
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText"));
        textView3.setBackground(Theme.createRadSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhiteBlueText"), 76), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f)));
        textView3.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f));
        textView3.setOnClickListener(new PermanentLinkBottomSheet$$ExternalSyntheticLambda0(this, chatFull, fragment2));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        linearLayout.addView(rLottieImageView, LayoutHelper.createLinear(90, 90, 1, 0, 24, 0, 0));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 1, 60, 16, 60, 0));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 1, 60, 16, 60, 0));
        linearLayout.addView(linkActionView2, LayoutHelper.createLinear(-1, -2));
        linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 1, 60, 26, 60, 26));
        NestedScrollView scrollView = new NestedScrollView(context2);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
        TLRPC.Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(chatId2));
        if (chat != null && chat.username != null) {
            linkActionView2.setLink("https://t.me/" + chat.username);
            textView3.setVisibility(8);
        } else if (chatFull == null || chatFull.exported_invite == null) {
            generateLink(false);
        } else {
            linkActionView2.setLink(chatFull.exported_invite.link);
        }
        updateColors();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PermanentLinkBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1155lambda$new$0$orgtelegramuiComponentsPermanentLinkBottomSheet() {
        generateLink(true);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PermanentLinkBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1156lambda$new$1$orgtelegramuiComponentsPermanentLinkBottomSheet(TLRPC.ChatFull info2, BaseFragment fragment2, View view) {
        ManageLinksActivity manageFragment = new ManageLinksActivity(info2.id, 0, 0);
        manageFragment.setInfo(info2, info2.exported_invite);
        fragment2.presentFragment(manageFragment);
        dismiss();
    }

    private void generateLink(boolean showDialog) {
        if (!this.linkGenerating) {
            this.linkGenerating = true;
            TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
            req.legacy_revoke_permanent = true;
            req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chatId);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new PermanentLinkBottomSheet$$ExternalSyntheticLambda3(this, showDialog));
        }
    }

    /* renamed from: lambda$generateLink$3$org-telegram-ui-Components-PermanentLinkBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1154xdCLASSNAMEc4(boolean showDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PermanentLinkBottomSheet$$ExternalSyntheticLambda2(this, error, response, showDialog));
    }

    /* renamed from: lambda$generateLink$2$org-telegram-ui-Components-PermanentLinkBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1153x4ec9a343(TLRPC.TL_error error, TLObject response, boolean showDialog) {
        if (error == null) {
            this.invite = (TLRPC.TL_chatInviteExported) response;
            TLRPC.ChatFull chatInfo = MessagesController.getInstance(this.currentAccount).getChatFull(this.chatId);
            if (chatInfo != null) {
                chatInfo.exported_invite = this.invite;
            }
            this.linkActionView.setLink(this.invite.link);
            if (showDialog && this.fragment != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(LocaleController.getString("RevokeAlertNewLink", NUM));
                builder.setTitle(LocaleController.getString("RevokeLink", NUM));
                builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                this.fragment.showDialog(builder.create());
            }
        }
        this.linkGenerating = false;
    }

    public void show() {
        super.show();
        AndroidUtilities.runOnUIThread(new PermanentLinkBottomSheet$$ExternalSyntheticLambda1(this), 50);
    }

    /* renamed from: lambda$show$4$org-telegram-ui-Components-PermanentLinkBottomSheet  reason: not valid java name */
    public /* synthetic */ void m1157x3915b8b3() {
        this.linkIcon.start();
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = new PermanentLinkBottomSheet$$ExternalSyntheticLambda4(this);
        arrayList.add(new ThemeDescription(this.titleView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.subtitle, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.manage, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, descriptionDelegate, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, descriptionDelegate, "featuredStickers_buttonText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, descriptionDelegate, "windowBackgroundWhiteBlueText"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public void updateColors() {
        this.imageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(90.0f), Theme.getColor("featuredStickers_addButton")));
        this.manage.setBackground(Theme.createRadSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhiteBlueText"), 76), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f)));
        int color = Theme.getColor("featuredStickers_buttonText");
        this.linkIcon.setLayerColor("Top.**", color);
        this.linkIcon.setLayerColor("Bottom.**", color);
        this.linkIcon.setLayerColor("Center.**", color);
        this.linkActionView.updateColors();
        setBackgroundColor(Theme.getColor("dialogBackground"));
    }

    public void dismissInternal() {
        super.dismissInternal();
    }

    public void dismiss() {
        super.dismiss();
    }
}
