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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ManageLinksActivity;

public class PermanentLinkBottomSheet extends BottomSheet {
    private int chatId;
    private BaseFragment fragment;
    private final RLottieImageView imageView;
    TLRPC$TL_chatInviteExported invite;
    private final LinkActionView linkActionView;
    boolean linkGenerating;
    RLottieDrawable linkIcon;
    private final TextView manage;
    private final TextView subtitle;
    private final TextView titleView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PermanentLinkBottomSheet(Context context, boolean z, BaseFragment baseFragment, TLRPC$ChatFull tLRPC$ChatFull, int i, boolean z2) {
        super(context, z);
        String str;
        int i2;
        TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported;
        Context context2 = context;
        TLRPC$ChatFull tLRPC$ChatFull2 = tLRPC$ChatFull;
        this.chatId = i;
        setAllowNestedScroll(true);
        setApplyBottomPadding(false);
        LinkActionView linkActionView2 = new LinkActionView(context, baseFragment, this, i, true, z2);
        this.linkActionView = linkActionView2;
        linkActionView2.setPermanent(true);
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.imageView = rLottieImageView;
        RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(90.0f), AndroidUtilities.dp(90.0f), false, (int[]) null);
        this.linkIcon = rLottieDrawable;
        rLottieDrawable.setCustomEndFrame(42);
        rLottieImageView.setAnimation(this.linkIcon);
        linkActionView2.setUsers(0, (ArrayList<TLRPC$User>) null);
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
        if (z2) {
            i2 = NUM;
            str = "LinkInfoChannel";
        } else {
            i2 = NUM;
            str = "LinkInfo";
        }
        textView2.setText(LocaleController.getString(str, i2));
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
        textView3.setOnClickListener(new PermanentLinkBottomSheet$$ExternalSyntheticLambda0(this, tLRPC$ChatFull2, baseFragment));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        linearLayout.addView(rLottieImageView, LayoutHelper.createLinear(90, 90, 1, 0, 24, 0, 0));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 1, 60, 16, 60, 0));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 1, 60, 16, 60, 0));
        linearLayout.addView(linkActionView2, LayoutHelper.createLinear(-1, -2));
        linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 1, 60, 26, 60, 26));
        NestedScrollView nestedScrollView = new NestedScrollView(context2);
        nestedScrollView.setVerticalScrollBarEnabled(false);
        nestedScrollView.addView(linearLayout);
        setCustomView(nestedScrollView);
        TLRPC$Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Integer.valueOf(i));
        if (chat != null && chat.username != null) {
            linkActionView2.setLink("https://t.me/" + chat.username);
            textView3.setVisibility(8);
        } else if (tLRPC$ChatFull2 == null || (tLRPC$TL_chatInviteExported = tLRPC$ChatFull2.exported_invite) == null) {
            generateLink(false);
        } else {
            linkActionView2.setLink(tLRPC$TL_chatInviteExported.link);
        }
        lambda$getThemeDescriptions$5();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        generateLink(true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(TLRPC$ChatFull tLRPC$ChatFull, BaseFragment baseFragment, View view) {
        ManageLinksActivity manageLinksActivity = new ManageLinksActivity(tLRPC$ChatFull.id, 0, 0);
        manageLinksActivity.setInfo(tLRPC$ChatFull, tLRPC$ChatFull.exported_invite);
        baseFragment.presentFragment(manageLinksActivity);
        dismiss();
    }

    private void generateLink(boolean z) {
        if (!this.linkGenerating) {
            this.linkGenerating = true;
            TLRPC$TL_messages_exportChatInvite tLRPC$TL_messages_exportChatInvite = new TLRPC$TL_messages_exportChatInvite();
            tLRPC$TL_messages_exportChatInvite.legacy_revoke_permanent = true;
            tLRPC$TL_messages_exportChatInvite.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chatId);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_exportChatInvite, new PermanentLinkBottomSheet$$ExternalSyntheticLambda3(this, z));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$generateLink$3(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new PermanentLinkBottomSheet$$ExternalSyntheticLambda2(this, tLRPC$TL_error, tLObject, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$generateLink$2(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        if (tLRPC$TL_error == null) {
            this.invite = (TLRPC$TL_chatInviteExported) tLObject;
            TLRPC$ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.chatId);
            if (chatFull != null) {
                chatFull.exported_invite = this.invite;
            }
            this.linkActionView.setLink(this.invite.link);
            if (z && this.fragment != null) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$show$4() {
        this.linkIcon.start();
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        PermanentLinkBottomSheet$$ExternalSyntheticLambda4 permanentLinkBottomSheet$$ExternalSyntheticLambda4 = new PermanentLinkBottomSheet$$ExternalSyntheticLambda4(this);
        arrayList.add(new ThemeDescription(this.titleView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.subtitle, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.manage, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText"));
        PermanentLinkBottomSheet$$ExternalSyntheticLambda4 permanentLinkBottomSheet$$ExternalSyntheticLambda42 = permanentLinkBottomSheet$$ExternalSyntheticLambda4;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, permanentLinkBottomSheet$$ExternalSyntheticLambda42, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, permanentLinkBottomSheet$$ExternalSyntheticLambda42, "featuredStickers_buttonText"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, permanentLinkBottomSheet$$ExternalSyntheticLambda42, "windowBackgroundWhiteBlueText"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: updateColors */
    public void lambda$getThemeDescriptions$5() {
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
