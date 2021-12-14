package org.telegram.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_changeAuthorizationSettings;
import org.telegram.tgnet.TLRPC$TL_authorization;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.Switch;

public class SessionBottomSheet extends BottomSheet {
    RLottieImageView imageView;
    BaseFragment parentFragment;
    TLRPC$TL_authorization session;

    public interface Callback {
        void onSessionTerminated(TLRPC$TL_authorization tLRPC$TL_authorization);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$uploadSessionSettings$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SessionBottomSheet(BaseFragment baseFragment, TLRPC$TL_authorization tLRPC$TL_authorization, boolean z, Callback callback) {
        super(baseFragment.getParentActivity(), false);
        String str;
        final BaseFragment baseFragment2 = baseFragment;
        final TLRPC$TL_authorization tLRPC$TL_authorization2 = tLRPC$TL_authorization;
        setOpenNoDelay(true);
        Activity parentActivity = baseFragment.getParentActivity();
        this.session = tLRPC$TL_authorization2;
        this.parentFragment = baseFragment2;
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        RLottieImageView rLottieImageView = new RLottieImageView(parentActivity);
        this.imageView = rLottieImageView;
        rLottieImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!SessionBottomSheet.this.imageView.isPlaying() && SessionBottomSheet.this.imageView.getAnimatedDrawable() != null) {
                    SessionBottomSheet.this.imageView.getAnimatedDrawable().setCurrentFrame(40);
                    SessionBottomSheet.this.imageView.playAnimation();
                }
            }
        });
        this.imageView.setScaleType(ImageView.ScaleType.CENTER);
        linearLayout.addView(this.imageView, LayoutHelper.createLinear(70, 70, 1, 0, 16, 0, 0));
        TextView textView = new TextView(parentActivity);
        textView.setTextSize(2, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        textView.setGravity(17);
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 1, 21, 12, 21, 0));
        TextView textView2 = new TextView(parentActivity);
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        textView2.setTextSize(2, 13.0f);
        textView2.setGravity(17);
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 1, 21, 4, 21, 21));
        if ((tLRPC$TL_authorization2.flags & 1) != 0) {
            str = LocaleController.getString("Online", NUM);
        } else {
            str = LocaleController.stringForMessageListDate((long) tLRPC$TL_authorization2.date_active);
        }
        textView2.setText(str);
        StringBuilder sb = new StringBuilder();
        if (tLRPC$TL_authorization2.device_model.length() != 0) {
            sb.append(tLRPC$TL_authorization2.device_model);
        }
        if (sb.length() == 0) {
            if (tLRPC$TL_authorization2.platform.length() != 0) {
                sb.append(tLRPC$TL_authorization2.platform);
            }
            if (tLRPC$TL_authorization2.system_version.length() != 0) {
                if (tLRPC$TL_authorization2.platform.length() != 0) {
                    sb.append(" ");
                }
                sb.append(tLRPC$TL_authorization2.system_version);
            }
        }
        textView.setText(sb);
        setAnimation(tLRPC$TL_authorization2, this.imageView);
        ItemView itemView = new ItemView(parentActivity, false);
        StringBuilder sb2 = new StringBuilder();
        sb2.append(tLRPC$TL_authorization2.app_name);
        sb2.append(" ");
        sb2.append(tLRPC$TL_authorization2.app_version);
        itemView.valueText.setText(sb2);
        Drawable mutate = ContextCompat.getDrawable(parentActivity, NUM).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.SRC_IN));
        itemView.iconView.setImageDrawable(mutate);
        itemView.descriptionText.setText(LocaleController.getString("Application", NUM));
        linearLayout.addView(itemView);
        if (tLRPC$TL_authorization2.country.length() != 0) {
            ItemView itemView2 = new ItemView(parentActivity, false);
            itemView2.valueText.setText(tLRPC$TL_authorization2.country);
            Drawable mutate2 = ContextCompat.getDrawable(parentActivity, NUM).mutate();
            mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.SRC_IN));
            itemView2.iconView.setImageDrawable(mutate2);
            itemView2.descriptionText.setText(LocaleController.getString("Location", NUM));
            itemView2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    SessionBottomSheet.this.copyText(tLRPC$TL_authorization2.country);
                }
            });
            itemView2.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View view) {
                    SessionBottomSheet.this.copyText(tLRPC$TL_authorization2.country);
                    return true;
                }
            });
            itemView2.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 2));
            linearLayout.addView(itemView2);
            itemView.needDivider = true;
            itemView = itemView2;
        }
        if (tLRPC$TL_authorization2.ip.length() != 0) {
            ItemView itemView3 = new ItemView(parentActivity, false);
            itemView3.valueText.setText(tLRPC$TL_authorization2.ip);
            Drawable mutate3 = ContextCompat.getDrawable(parentActivity, NUM).mutate();
            mutate3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.SRC_IN));
            itemView3.iconView.setImageDrawable(mutate3);
            itemView3.descriptionText.setText(LocaleController.getString("IpAddress", NUM));
            itemView3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    SessionBottomSheet.this.copyText(tLRPC$TL_authorization2.ip);
                }
            });
            itemView3.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View view) {
                    SessionBottomSheet.this.copyText(tLRPC$TL_authorization2.country);
                    return true;
                }
            });
            itemView3.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 2));
            linearLayout.addView(itemView3);
            itemView.needDivider = true;
            itemView = itemView3;
        }
        if (secretChatsEnabled(tLRPC$TL_authorization2)) {
            final ItemView itemView4 = new ItemView(parentActivity, true);
            itemView4.valueText.setText(LocaleController.getString("AcceptSecretChats", NUM));
            Drawable mutate4 = ContextCompat.getDrawable(parentActivity, NUM).mutate();
            mutate4.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.SRC_IN));
            itemView4.iconView.setImageDrawable(mutate4);
            itemView4.switchView.setChecked(!tLRPC$TL_authorization2.encrypted_requests_disabled, false);
            itemView4.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 7));
            itemView4.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Switch switchR = itemView4.switchView;
                    switchR.setChecked(!switchR.isChecked(), true);
                    tLRPC$TL_authorization2.encrypted_requests_disabled = !itemView4.switchView.isChecked();
                    SessionBottomSheet.this.uploadSessionSettings();
                }
            });
            itemView.needDivider = true;
            itemView4.descriptionText.setText(LocaleController.getString("AcceptSecretChatsDescription", NUM));
            linearLayout.addView(itemView4);
            itemView = itemView4;
        }
        final ItemView itemView5 = new ItemView(parentActivity, true);
        itemView5.valueText.setText(LocaleController.getString("AcceptCalls", NUM));
        Drawable mutate5 = ContextCompat.getDrawable(parentActivity, NUM).mutate();
        mutate5.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.SRC_IN));
        itemView5.iconView.setImageDrawable(mutate5);
        itemView5.switchView.setChecked(!tLRPC$TL_authorization2.call_requests_disabled, false);
        itemView5.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 7));
        itemView5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Switch switchR = itemView5.switchView;
                switchR.setChecked(!switchR.isChecked(), true);
                tLRPC$TL_authorization2.call_requests_disabled = !itemView5.switchView.isChecked();
                SessionBottomSheet.this.uploadSessionSettings();
            }
        });
        itemView.needDivider = true;
        itemView5.descriptionText.setText(LocaleController.getString("AcceptCallsChatsDescription", NUM));
        linearLayout.addView(itemView5);
        if (!z) {
            TextView textView3 = new TextView(parentActivity);
            textView3.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            textView3.setGravity(17);
            textView3.setTextSize(1, 14.0f);
            textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView3.setText(LocaleController.getString("TerminateSession", NUM));
            textView3.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            textView3.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("chat_attachAudioBackground"), ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhite"), 120)));
            linearLayout.addView(textView3, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 15.0f, 16.0f, 16.0f));
            final Callback callback2 = callback;
            textView3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) SessionBottomSheet.this.parentFragment.getParentActivity());
                    builder.setMessage(LocaleController.getString("TerminateSessionText", NUM));
                    builder.setTitle(LocaleController.getString("AreYouSureSessionTitle", NUM));
                    builder.setPositiveButton(LocaleController.getString("Terminate", NUM), new SessionBottomSheet$8$$ExternalSyntheticLambda0(this, callback2, tLRPC$TL_authorization2));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog create = builder.create();
                    baseFragment2.showDialog(create);
                    TextView textView = (TextView) create.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onClick$0(Callback callback, TLRPC$TL_authorization tLRPC$TL_authorization, DialogInterface dialogInterface, int i) {
                    callback.onSessionTerminated(tLRPC$TL_authorization);
                    SessionBottomSheet.this.dismiss();
                }
            });
        }
        ScrollView scrollView = new ScrollView(parentActivity);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }

    private boolean secretChatsEnabled(TLRPC$TL_authorization tLRPC$TL_authorization) {
        int i = tLRPC$TL_authorization.api_id;
        return (i == 2040 || i == 2496) ? false : true;
    }

    /* access modifiers changed from: private */
    public void uploadSessionSettings() {
        TLRPC$TL_account_changeAuthorizationSettings tLRPC$TL_account_changeAuthorizationSettings = new TLRPC$TL_account_changeAuthorizationSettings();
        TLRPC$TL_authorization tLRPC$TL_authorization = this.session;
        tLRPC$TL_account_changeAuthorizationSettings.encrypted_requests_disabled = tLRPC$TL_authorization.encrypted_requests_disabled;
        tLRPC$TL_account_changeAuthorizationSettings.call_requests_disabled = tLRPC$TL_authorization.call_requests_disabled;
        tLRPC$TL_account_changeAuthorizationSettings.flags = 3;
        tLRPC$TL_account_changeAuthorizationSettings.hash = tLRPC$TL_authorization.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_changeAuthorizationSettings, SessionBottomSheet$$ExternalSyntheticLambda1.INSTANCE);
    }

    /* access modifiers changed from: private */
    public void copyText(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(new CharSequence[]{LocaleController.getString("Copy", NUM)}, new SessionBottomSheet$$ExternalSyntheticLambda0(this, str));
        builder.show();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$copyText$1(String str, DialogInterface dialogInterface, int i) {
        ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", str));
        BulletinFactory.of(getContainer(), (Theme.ResourcesProvider) null).createCopyBulletin(LocaleController.getString("TextCopied", NUM)).show();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00c3, code lost:
        if (r13.app_name.toLowerCase().contains("desktop") != false) goto L_0x009b;
     */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00fb  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x010c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setAnimation(org.telegram.tgnet.TLRPC$TL_authorization r13, org.telegram.ui.Components.RLottieImageView r14) {
        /*
            r12 = this;
            java.lang.String r0 = r13.platform
            java.lang.String r0 = r0.toLowerCase()
            boolean r1 = r0.isEmpty()
            if (r1 == 0) goto L_0x0012
            java.lang.String r0 = r13.system_version
            java.lang.String r0 = r0.toLowerCase()
        L_0x0012:
            java.lang.String r1 = r13.device_model
            java.lang.String r1 = r1.toLowerCase()
            java.lang.String r2 = "safari"
            boolean r2 = r1.contains(r2)
            r3 = 2131558554(0x7f0d009a, float:1.8742427E38)
            java.lang.String r4 = "avatar_backgroundBlue"
            r5 = 2131558424(0x7f0d0018, float:1.8742163E38)
            r6 = 1
            java.lang.String r7 = "avatar_backgroundCyan"
            r8 = 0
            java.lang.String r9 = "avatar_backgroundPink"
            if (r2 == 0) goto L_0x0035
            r3 = 2131558489(0x7f0d0059, float:1.8742295E38)
        L_0x0031:
            r4 = r9
        L_0x0032:
            r13 = 1
            goto L_0x00e8
        L_0x0035:
            java.lang.String r2 = "edge"
            boolean r2 = r1.contains(r2)
            if (r2 == 0) goto L_0x0041
            r3 = 2131558434(0x7f0d0022, float:1.8742184E38)
            goto L_0x0031
        L_0x0041:
            java.lang.String r2 = "chrome"
            boolean r2 = r1.contains(r2)
            if (r2 == 0) goto L_0x0050
        L_0x0049:
            r4 = r9
            r13 = 1
            r3 = 2131558424(0x7f0d0018, float:1.8742163E38)
            goto L_0x00e8
        L_0x0050:
            java.lang.String r2 = "opera"
            boolean r10 = r1.contains(r2)
            java.lang.String r11 = "firefox"
            if (r10 != 0) goto L_0x00c6
            boolean r10 = r1.contains(r11)
            if (r10 != 0) goto L_0x00c6
            java.lang.String r10 = "vivaldi"
            boolean r10 = r1.contains(r10)
            if (r10 == 0) goto L_0x0069
            goto L_0x00c6
        L_0x0069:
            java.lang.String r2 = "ubuntu"
            boolean r2 = r0.contains(r2)
            if (r2 == 0) goto L_0x0075
            r3 = 2131558523(0x7f0d007b, float:1.8742364E38)
            goto L_0x0032
        L_0x0075:
            java.lang.String r2 = "ios"
            boolean r2 = r0.contains(r2)
            if (r2 == 0) goto L_0x0093
            java.lang.String r13 = "ipad"
            boolean r13 = r1.contains(r13)
            if (r13 == 0) goto L_0x008c
            r13 = 2131558467(0x7f0d0043, float:1.874225E38)
            r3 = 2131558467(0x7f0d0043, float:1.874225E38)
            goto L_0x0032
        L_0x008c:
            r13 = 2131558468(0x7f0d0044, float:1.8742253E38)
            r3 = 2131558468(0x7f0d0044, float:1.8742253E38)
            goto L_0x0032
        L_0x0093:
            java.lang.String r1 = "windows"
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x009d
        L_0x009b:
            r4 = r7
            goto L_0x0032
        L_0x009d:
            java.lang.String r1 = "macos"
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x00a9
            r3 = 2131558471(0x7f0d0047, float:1.8742259E38)
            goto L_0x009b
        L_0x00a9:
            java.lang.String r1 = "android"
            boolean r0 = r0.contains(r1)
            if (r0 == 0) goto L_0x00b7
            r3 = 2131558400(0x7f0d0000, float:1.8742115E38)
            java.lang.String r4 = "avatar_backgroundGreen"
            goto L_0x0032
        L_0x00b7:
            java.lang.String r13 = r13.app_name
            java.lang.String r13 = r13.toLowerCase()
            java.lang.String r0 = "desktop"
            boolean r13 = r13.contains(r0)
            if (r13 == 0) goto L_0x0049
            goto L_0x009b
        L_0x00c6:
            boolean r13 = r1.contains(r2)
            if (r13 == 0) goto L_0x00d3
            r13 = 2131165401(0x7var_d9, float:1.7945018E38)
            r3 = 2131165401(0x7var_d9, float:1.7945018E38)
            goto L_0x00e6
        L_0x00d3:
            boolean r13 = r1.contains(r11)
            if (r13 == 0) goto L_0x00e0
            r13 = 2131165400(0x7var_d8, float:1.7945016E38)
            r3 = 2131165400(0x7var_d8, float:1.7945016E38)
            goto L_0x00e6
        L_0x00e0:
            r13 = 2131165402(0x7var_da, float:1.794502E38)
            r3 = 2131165402(0x7var_da, float:1.794502E38)
        L_0x00e6:
            r4 = r9
            r13 = 0
        L_0x00e8:
            r0 = 1109917696(0x42280000, float:42.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            android.graphics.drawable.ShapeDrawable r0 = org.telegram.ui.ActionBar.Theme.createCircleDrawable(r0, r1)
            r14.setBackground(r0)
            if (r13 == 0) goto L_0x010c
            r13 = 2
            int[] r13 = new int[r13]
            r13[r8] = r8
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r13[r6] = r0
            r0 = 50
            r14.setAnimation(r3, r0, r0, r13)
            goto L_0x0117
        L_0x010c:
            android.content.Context r13 = r12.getContext()
            android.graphics.drawable.Drawable r13 = androidx.core.content.ContextCompat.getDrawable(r13, r3)
            r14.setImageDrawable(r13)
        L_0x0117:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SessionBottomSheet.setAnimation(org.telegram.tgnet.TLRPC$TL_authorization, org.telegram.ui.Components.RLottieImageView):void");
    }

    private static class ItemView extends FrameLayout {
        TextView descriptionText;
        ImageView iconView;
        boolean needDivider = false;
        Switch switchView;
        TextView valueText;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public ItemView(Context context, boolean z) {
            super(context);
            Context context2 = context;
            ImageView imageView = new ImageView(context2);
            this.iconView = imageView;
            addView(imageView, LayoutHelper.createFrame(28, 28.0f, 0, 16.0f, 8.0f, 0.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 64.0f, 4.0f, 0.0f, 4.0f));
            TextView textView = new TextView(context2);
            this.valueText = textView;
            textView.setTextSize(2, 16.0f);
            this.valueText.setGravity(3);
            this.valueText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            linearLayout.addView(this.valueText, LayoutHelper.createLinear(-1, -2, 0, 0, 0, z ? 46 : 0, 0));
            TextView textView2 = new TextView(context2);
            this.descriptionText = textView2;
            textView2.setTextSize(2, 13.0f);
            this.descriptionText.setGravity(3);
            this.descriptionText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            linearLayout.addView(this.descriptionText, LayoutHelper.createLinear(-1, -2, 0, 0, 4, z ? 46 : 0, 0));
            setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
            if (z) {
                Switch switchR = new Switch(context2);
                this.switchView = switchR;
                switchR.setDrawIconType(1);
                addView(this.switchView, LayoutHelper.createFrame(37, 40.0f, 21, 21.0f, 0.0f, 21.0f, 0.0f));
            }
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (this.needDivider) {
                canvas.drawRect((float) AndroidUtilities.dp(64.0f), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.dividerPaint);
            }
        }
    }

    public void show() {
        super.show();
        this.imageView.playAnimation();
    }
}
