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
import android.view.accessibility.AccessibilityNodeInfo;
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
import org.telegram.messenger.R;
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
        fixNavigationBar();
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
            str = LocaleController.getString("Online", R.string.Online);
        } else {
            str = LocaleController.formatDateTime((long) tLRPC$TL_authorization2.date_active);
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
        Drawable mutate = ContextCompat.getDrawable(parentActivity, R.drawable.menu_devices).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.SRC_IN));
        itemView.iconView.setImageDrawable(mutate);
        itemView.descriptionText.setText(LocaleController.getString("Application", R.string.Application));
        linearLayout.addView(itemView);
        if (tLRPC$TL_authorization2.country.length() != 0) {
            ItemView itemView2 = new ItemView(parentActivity, false);
            itemView2.valueText.setText(tLRPC$TL_authorization2.country);
            Drawable mutate2 = ContextCompat.getDrawable(parentActivity, R.drawable.msg_location).mutate();
            mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.SRC_IN));
            itemView2.iconView.setImageDrawable(mutate2);
            itemView2.descriptionText.setText(LocaleController.getString("Location", R.string.Location));
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
            Drawable mutate3 = ContextCompat.getDrawable(parentActivity, R.drawable.msg_language).mutate();
            mutate3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.SRC_IN));
            itemView3.iconView.setImageDrawable(mutate3);
            itemView3.descriptionText.setText(LocaleController.getString("IpAddress", R.string.IpAddress));
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
            itemView4.valueText.setText(LocaleController.getString("AcceptSecretChats", R.string.AcceptSecretChats));
            Drawable mutate4 = ContextCompat.getDrawable(parentActivity, R.drawable.msg_secret).mutate();
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
            itemView4.descriptionText.setText(LocaleController.getString("AcceptSecretChatsDescription", R.string.AcceptSecretChatsDescription));
            linearLayout.addView(itemView4);
            itemView = itemView4;
        }
        final ItemView itemView5 = new ItemView(parentActivity, true);
        itemView5.valueText.setText(LocaleController.getString("AcceptCalls", R.string.AcceptCalls));
        Drawable mutate5 = ContextCompat.getDrawable(parentActivity, R.drawable.msg_calls).mutate();
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
        itemView5.descriptionText.setText(LocaleController.getString("AcceptCallsChatsDescription", R.string.AcceptCallsChatsDescription));
        linearLayout.addView(itemView5);
        if (!z) {
            TextView textView3 = new TextView(parentActivity);
            textView3.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            textView3.setGravity(17);
            textView3.setTextSize(1, 14.0f);
            textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView3.setText(LocaleController.getString("TerminateSession", R.string.TerminateSession));
            textView3.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            textView3.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("chat_attachAudioBackground"), ColorUtils.setAlphaComponent(Theme.getColor("windowBackgroundWhite"), 120)));
            linearLayout.addView(textView3, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 15.0f, 16.0f, 16.0f));
            final Callback callback2 = callback;
            textView3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) SessionBottomSheet.this.parentFragment.getParentActivity());
                    builder.setMessage(LocaleController.getString("TerminateSessionText", R.string.TerminateSessionText));
                    builder.setTitle(LocaleController.getString("AreYouSureSessionTitle", R.string.AreYouSureSessionTitle));
                    builder.setPositiveButton(LocaleController.getString("Terminate", R.string.Terminate), new SessionBottomSheet$8$$ExternalSyntheticLambda0(this, callback2, tLRPC$TL_authorization2));
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
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
        builder.setItems(new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, new SessionBottomSheet$$ExternalSyntheticLambda0(this, str));
        builder.show();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$copyText$1(String str, DialogInterface dialogInterface, int i) {
        ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", str));
        BulletinFactory.of(getContainer(), (Theme.ResourcesProvider) null).createCopyBulletin(LocaleController.getString("TextCopied", R.string.TextCopied)).show();
    }

    /* JADX WARNING: Removed duplicated region for block: B:53:0x00e0  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00f1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setAnimation(org.telegram.tgnet.TLRPC$TL_authorization r11, org.telegram.ui.Components.RLottieImageView r12) {
        /*
            r10 = this;
            java.lang.String r0 = r11.platform
            java.lang.String r0 = r0.toLowerCase()
            boolean r1 = r0.isEmpty()
            if (r1 == 0) goto L_0x0012
            java.lang.String r0 = r11.system_version
            java.lang.String r0 = r0.toLowerCase()
        L_0x0012:
            java.lang.String r1 = r11.device_model
            java.lang.String r1 = r1.toLowerCase()
            java.lang.String r2 = "safari"
            boolean r2 = r1.contains(r2)
            java.lang.String r3 = "avatar_backgroundBlue"
            r4 = 1
            java.lang.String r5 = "avatar_backgroundCyan"
            r6 = 0
            java.lang.String r7 = "avatar_backgroundPink"
            if (r2 == 0) goto L_0x002e
            int r11 = org.telegram.messenger.R.raw.safari_30
        L_0x002a:
            r3 = r7
        L_0x002b:
            r0 = 1
            goto L_0x00cd
        L_0x002e:
            java.lang.String r2 = "edge"
            boolean r2 = r1.contains(r2)
            if (r2 == 0) goto L_0x0039
            int r11 = org.telegram.messenger.R.raw.edge_30
            goto L_0x002a
        L_0x0039:
            java.lang.String r2 = "chrome"
            boolean r2 = r1.contains(r2)
            if (r2 == 0) goto L_0x0044
            int r11 = org.telegram.messenger.R.raw.chrome_30
            goto L_0x002a
        L_0x0044:
            java.lang.String r2 = "opera"
            boolean r8 = r1.contains(r2)
            java.lang.String r9 = "firefox"
            if (r8 != 0) goto L_0x00b7
            boolean r8 = r1.contains(r9)
            if (r8 != 0) goto L_0x00b7
            java.lang.String r8 = "vivaldi"
            boolean r8 = r1.contains(r8)
            if (r8 == 0) goto L_0x005d
            goto L_0x00b7
        L_0x005d:
            java.lang.String r2 = "ubuntu"
            boolean r2 = r0.contains(r2)
            if (r2 == 0) goto L_0x0068
            int r11 = org.telegram.messenger.R.raw.ubuntu_30
            goto L_0x002b
        L_0x0068:
            java.lang.String r2 = "ios"
            boolean r2 = r0.contains(r2)
            if (r2 == 0) goto L_0x007e
            java.lang.String r11 = "ipad"
            boolean r11 = r1.contains(r11)
            if (r11 == 0) goto L_0x007b
            int r11 = org.telegram.messenger.R.raw.ipad_30
            goto L_0x002b
        L_0x007b:
            int r11 = org.telegram.messenger.R.raw.iphone_30
            goto L_0x002b
        L_0x007e:
            java.lang.String r1 = "windows"
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x008a
            int r11 = org.telegram.messenger.R.raw.windows_30
        L_0x0088:
            r3 = r5
            goto L_0x002b
        L_0x008a:
            java.lang.String r1 = "macos"
            boolean r1 = r0.contains(r1)
            if (r1 == 0) goto L_0x0095
            int r11 = org.telegram.messenger.R.raw.mac_30
            goto L_0x0088
        L_0x0095:
            java.lang.String r1 = "android"
            boolean r0 = r0.contains(r1)
            if (r0 == 0) goto L_0x00a2
            int r11 = org.telegram.messenger.R.raw.android_30
            java.lang.String r3 = "avatar_backgroundGreen"
            goto L_0x002b
        L_0x00a2:
            java.lang.String r11 = r11.app_name
            java.lang.String r11 = r11.toLowerCase()
            java.lang.String r0 = "desktop"
            boolean r11 = r11.contains(r0)
            if (r11 == 0) goto L_0x00b3
            int r11 = org.telegram.messenger.R.raw.windows_30
            goto L_0x0088
        L_0x00b3:
            int r11 = org.telegram.messenger.R.raw.chrome_30
            goto L_0x002a
        L_0x00b7:
            boolean r11 = r1.contains(r2)
            if (r11 == 0) goto L_0x00c0
            int r11 = org.telegram.messenger.R.drawable.device_web_opera
            goto L_0x00cb
        L_0x00c0:
            boolean r11 = r1.contains(r9)
            if (r11 == 0) goto L_0x00c9
            int r11 = org.telegram.messenger.R.drawable.device_web_firefox
            goto L_0x00cb
        L_0x00c9:
            int r11 = org.telegram.messenger.R.drawable.device_web_other
        L_0x00cb:
            r3 = r7
            r0 = 0
        L_0x00cd:
            r1 = 1109917696(0x42280000, float:42.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            android.graphics.drawable.ShapeDrawable r1 = org.telegram.ui.ActionBar.Theme.createCircleDrawable(r1, r2)
            r12.setBackground(r1)
            if (r0 == 0) goto L_0x00f1
            r0 = 2
            int[] r0 = new int[r0]
            r0[r6] = r6
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0[r4] = r1
            r1 = 50
            r12.setAnimation(r11, r1, r1, r0)
            goto L_0x00fc
        L_0x00f1:
            android.content.Context r0 = r10.getContext()
            android.graphics.drawable.Drawable r11 = androidx.core.content.ContextCompat.getDrawable(r0, r11)
            r12.setImageDrawable(r11)
        L_0x00fc:
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

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            String str;
            int i;
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            if (this.switchView != null) {
                accessibilityNodeInfo.setClassName("android.widget.Switch");
                accessibilityNodeInfo.setCheckable(true);
                accessibilityNodeInfo.setChecked(this.switchView.isChecked());
                StringBuilder sb = new StringBuilder();
                sb.append(this.valueText.getText());
                sb.append("\n");
                sb.append(this.descriptionText.getText());
                sb.append("\n");
                if (this.switchView.isChecked()) {
                    i = R.string.NotificationsOn;
                    str = "NotificationsOn";
                } else {
                    i = R.string.NotificationsOff;
                    str = "NotificationsOff";
                }
                sb.append(LocaleController.getString(str, i));
                accessibilityNodeInfo.setText(sb.toString());
            }
        }
    }

    public void show() {
        super.show();
        this.imageView.playAnimation();
    }
}
