package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SectionsAdapter;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class PhonebookSelectShareAlert extends BottomSheet implements NotificationCenterDelegate {
    private ChatActivity chatActivity;
    private PhonebookShareAlertDelegate delegate;
    private FrameLayout frameLayout;
    private LinearLayoutManager layoutManager;
    private ShareAdapter listAdapter;
    private RecyclerListView listView;
    private int scrollOffsetY;
    private ShareSearchAdapter searchAdapter;
    private EmptyTextProgressView searchEmptyView;
    private View shadow;
    private AnimatorSet shadowAnimation;
    private Drawable shadowDrawable;
    private int topBeforeSwitch;

    public interface PhonebookShareAlertDelegate {
        void didSelectContact(User user, boolean z, int i);
    }

    private class SearchField extends FrameLayout {
        private View backgroundView;
        private ImageView clearSearchImageView;
        private CloseProgressDrawable2 progressDrawable;
        private View searchBackground;
        private EditTextBoldCursor searchEditText;
        private ImageView searchIconImageView;

        public SearchField(Context context) {
            super(context);
            this.searchBackground = new View(context);
            this.searchBackground.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), Theme.getColor("dialogSearchBackground")));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
            this.searchIconImageView = new ImageView(context);
            this.searchIconImageView.setScaleType(ScaleType.CENTER);
            this.searchIconImageView.setImageResource(NUM);
            String str = "dialogSearchIcon";
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
            this.clearSearchImageView = new ImageView(context);
            this.clearSearchImageView.setScaleType(ScaleType.CENTER);
            ImageView imageView = this.clearSearchImageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2();
            this.progressDrawable = closeProgressDrawable2;
            imageView.setImageDrawable(closeProgressDrawable2);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            this.clearSearchImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new -$$Lambda$PhonebookSelectShareAlert$SearchField$PLYYwCicXQVgyBz1a99rUPWqnhE(this));
            this.searchEditText = new EditTextBoldCursor(context, PhonebookSelectShareAlert.this) {
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    obtain.setLocation(obtain.getRawX(), obtain.getRawY() - PhonebookSelectShareAlert.this.containerView.getTranslationY());
                    PhonebookSelectShareAlert.this.listView.dispatchTouchEvent(obtain);
                    obtain.recycle();
                    return super.dispatchTouchEvent(motionEvent);
                }
            };
            this.searchEditText.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(Theme.getColor("dialogSearchHint"));
            this.searchEditText.setTextColor(Theme.getColor("dialogSearchText"));
            this.searchEditText.setBackgroundDrawable(null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(NUM);
            this.searchEditText.setHint(LocaleController.getString("SearchFriends", NUM));
            this.searchEditText.setCursorColor(Theme.getColor("featuredStickers_addedIcon"));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher(PhonebookSelectShareAlert.this) {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    Object obj = 1;
                    Object obj2 = SearchField.this.searchEditText.length() > 0 ? 1 : null;
                    float f = 0.0f;
                    if (SearchField.this.clearSearchImageView.getAlpha() == 0.0f) {
                        obj = null;
                    }
                    if (obj2 != obj) {
                        ViewPropertyAnimator animate = SearchField.this.clearSearchImageView.animate();
                        float f2 = 1.0f;
                        if (obj2 != null) {
                            f = 1.0f;
                        }
                        animate = animate.alpha(f).setDuration(150).scaleX(obj2 != null ? 1.0f : 0.1f);
                        if (obj2 == null) {
                            f2 = 0.1f;
                        }
                        animate.scaleY(f2).start();
                    }
                    String obj3 = SearchField.this.searchEditText.getText().toString();
                    if (obj3.length() != 0) {
                        if (PhonebookSelectShareAlert.this.searchEmptyView != null) {
                            PhonebookSelectShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", NUM));
                        }
                    } else if (PhonebookSelectShareAlert.this.listView.getAdapter() != PhonebookSelectShareAlert.this.listAdapter) {
                        int access$600 = PhonebookSelectShareAlert.this.getCurrentTop();
                        PhonebookSelectShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoContacts", NUM));
                        PhonebookSelectShareAlert.this.searchEmptyView.showTextView();
                        PhonebookSelectShareAlert.this.listView.setAdapter(PhonebookSelectShareAlert.this.listAdapter);
                        PhonebookSelectShareAlert.this.listAdapter.notifyDataSetChanged();
                        if (access$600 > 0) {
                            PhonebookSelectShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -access$600);
                        }
                    }
                    if (PhonebookSelectShareAlert.this.searchAdapter != null) {
                        PhonebookSelectShareAlert.this.searchAdapter.search(obj3);
                    }
                }
            });
            this.searchEditText.setOnEditorActionListener(new -$$Lambda$PhonebookSelectShareAlert$SearchField$qXSUwjIJjKw2Ydl-Q5GAD_aVQUU(this));
        }

        public /* synthetic */ void lambda$new$0$PhonebookSelectShareAlert$SearchField(View view) {
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public /* synthetic */ boolean lambda$new$1$PhonebookSelectShareAlert$SearchField(TextView textView, int i, KeyEvent keyEvent) {
            if (keyEvent != null && ((keyEvent.getAction() == 1 && keyEvent.getKeyCode() == 84) || (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == 66))) {
                AndroidUtilities.hideKeyboard(this.searchEditText);
            }
            return false;
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        public void requestDisallowInterceptTouchEvent(boolean z) {
            super.requestDisallowInterceptTouchEvent(z);
        }
    }

    public class UserCell extends FrameLayout {
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        private BackupImageView avatarImageView;
        private int currentAccount = UserConfig.selectedAccount;
        private int currentId;
        private CharSequence currentName;
        private CharSequence currentStatus;
        private User currentUser;
        private FileLocation lastAvatar;
        private String lastName;
        private int lastStatus;
        private SimpleTextView nameTextView;
        private boolean needDivider;
        private SimpleTextView statusTextView;

        public boolean hasOverlappingRendering() {
            return false;
        }

        public UserCell(Context context) {
            super(context);
            this.avatarImageView = new BackupImageView(context);
            this.avatarImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
            int i = 5;
            addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 14.0f, 9.0f, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f));
            this.nameTextView = new SimpleTextView(context);
            this.nameTextView.setTextColor(Theme.getColor("dialogTextBlack"));
            this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.nameTextView.setTextSize(16);
            this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 28.0f : 72.0f, 12.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
            this.statusTextView = new SimpleTextView(context);
            this.statusTextView.setTextSize(13);
            this.statusTextView.setTextColor(Theme.getColor("dialogTextGray2"));
            this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            SimpleTextView simpleTextView = this.statusTextView;
            if (!LocaleController.isRTL) {
                i = 3;
            }
            addView(simpleTextView, LayoutHelper.createFrame(-1, 20.0f, i | 48, LocaleController.isRTL ? 28.0f : 72.0f, 36.0f, LocaleController.isRTL ? 72.0f : 28.0f, 0.0f));
        }

        public void setCurrentId(int i) {
            this.currentId = i;
        }

        public void setData(User user, CharSequence charSequence, CharSequence charSequence2, boolean z) {
            if (user == null && charSequence == null && charSequence2 == null) {
                this.currentStatus = null;
                this.currentName = null;
                String str = "";
                this.nameTextView.setText(str);
                this.statusTextView.setText(str);
                this.avatarImageView.setImageDrawable(null);
                return;
            }
            this.currentStatus = charSequence2;
            this.currentName = charSequence;
            this.currentUser = user;
            this.needDivider = z;
            setWillNotDraw(this.needDivider ^ 1);
            update(0);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f) + this.needDivider, NUM));
        }

        /* JADX WARNING: Removed duplicated region for block: B:33:0x0049  */
        /* JADX WARNING: Removed duplicated region for block: B:32:0x0046  */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x004e  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x0073 A:{RETURN} */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x0074  */
        /* JADX WARNING: Removed duplicated region for block: B:8:0x0010  */
        /* JADX WARNING: Removed duplicated region for block: B:61:0x008c  */
        /* JADX WARNING: Removed duplicated region for block: B:57:0x0079  */
        /* JADX WARNING: Removed duplicated region for block: B:68:0x00b1  */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x00a9  */
        /* JADX WARNING: Removed duplicated region for block: B:78:0x00d3  */
        /* JADX WARNING: Removed duplicated region for block: B:77:0x00cd  */
        /* JADX WARNING: Removed duplicated region for block: B:87:0x0126  */
        /* JADX WARNING: Removed duplicated region for block: B:86:0x0116  */
        /* JADX WARNING: Missing block: B:22:0x0033, code skipped:
            if (r3.local_id != r0.local_id) goto L_0x0035;
     */
        public void update(int r11) {
            /*
            r10 = this;
            r0 = r10.currentUser;
            r1 = 0;
            if (r0 == 0) goto L_0x000c;
        L_0x0005:
            r0 = r0.photo;
            if (r0 == 0) goto L_0x000c;
        L_0x0009:
            r0 = r0.photo_small;
            goto L_0x000d;
        L_0x000c:
            r0 = r1;
        L_0x000d:
            r2 = 0;
            if (r11 == 0) goto L_0x0074;
        L_0x0010:
            r3 = r11 & 2;
            r4 = 1;
            if (r3 == 0) goto L_0x0037;
        L_0x0015:
            r3 = r10.lastAvatar;
            if (r3 == 0) goto L_0x001b;
        L_0x0019:
            if (r0 == 0) goto L_0x0035;
        L_0x001b:
            r3 = r10.lastAvatar;
            if (r3 != 0) goto L_0x0021;
        L_0x001f:
            if (r0 != 0) goto L_0x0035;
        L_0x0021:
            r3 = r10.lastAvatar;
            if (r3 == 0) goto L_0x0037;
        L_0x0025:
            if (r0 == 0) goto L_0x0037;
        L_0x0027:
            r5 = r3.volume_id;
            r7 = r0.volume_id;
            r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
            if (r9 != 0) goto L_0x0035;
        L_0x002f:
            r3 = r3.local_id;
            r5 = r0.local_id;
            if (r3 == r5) goto L_0x0037;
        L_0x0035:
            r3 = 1;
            goto L_0x0038;
        L_0x0037:
            r3 = 0;
        L_0x0038:
            r5 = r10.currentUser;
            if (r5 == 0) goto L_0x004f;
        L_0x003c:
            if (r3 != 0) goto L_0x004f;
        L_0x003e:
            r6 = r11 & 4;
            if (r6 == 0) goto L_0x004f;
        L_0x0042:
            r5 = r5.status;
            if (r5 == 0) goto L_0x0049;
        L_0x0046:
            r5 = r5.expires;
            goto L_0x004a;
        L_0x0049:
            r5 = 0;
        L_0x004a:
            r6 = r10.lastStatus;
            if (r5 == r6) goto L_0x004f;
        L_0x004e:
            r3 = 1;
        L_0x004f:
            if (r3 != 0) goto L_0x0070;
        L_0x0051:
            r5 = r10.currentName;
            if (r5 != 0) goto L_0x0070;
        L_0x0055:
            r5 = r10.lastName;
            if (r5 == 0) goto L_0x0070;
        L_0x0059:
            r11 = r11 & r4;
            if (r11 == 0) goto L_0x0070;
        L_0x005c:
            r11 = r10.currentUser;
            if (r11 == 0) goto L_0x0065;
        L_0x0060:
            r11 = org.telegram.messenger.UserObject.getUserName(r11);
            goto L_0x0066;
        L_0x0065:
            r11 = r1;
        L_0x0066:
            r5 = r10.lastName;
            r5 = r11.equals(r5);
            if (r5 != 0) goto L_0x0071;
        L_0x006e:
            r3 = 1;
            goto L_0x0071;
        L_0x0070:
            r11 = r1;
        L_0x0071:
            if (r3 != 0) goto L_0x0075;
        L_0x0073:
            return;
        L_0x0074:
            r11 = r1;
        L_0x0075:
            r3 = r10.currentUser;
            if (r3 == 0) goto L_0x008c;
        L_0x0079:
            r4 = r10.avatarDrawable;
            r4.setInfo(r3);
            r3 = r10.currentUser;
            r3 = r3.status;
            if (r3 == 0) goto L_0x0089;
        L_0x0084:
            r3 = r3.expires;
            r10.lastStatus = r3;
            goto L_0x00a5;
        L_0x0089:
            r10.lastStatus = r2;
            goto L_0x00a5;
        L_0x008c:
            r3 = r10.currentName;
            if (r3 == 0) goto L_0x009c;
        L_0x0090:
            r4 = r10.avatarDrawable;
            r5 = r10.currentId;
            r3 = r3.toString();
            r4.setInfo(r5, r3, r1);
            goto L_0x00a5;
        L_0x009c:
            r3 = r10.avatarDrawable;
            r4 = r10.currentId;
            r5 = "#";
            r3.setInfo(r4, r5, r1);
        L_0x00a5:
            r3 = r10.currentName;
            if (r3 == 0) goto L_0x00b1;
        L_0x00a9:
            r10.lastName = r1;
            r11 = r10.nameTextView;
            r11.setText(r3);
            goto L_0x00c9;
        L_0x00b1:
            r1 = r10.currentUser;
            if (r1 == 0) goto L_0x00be;
        L_0x00b5:
            if (r11 != 0) goto L_0x00bb;
        L_0x00b7:
            r11 = org.telegram.messenger.UserObject.getUserName(r1);
        L_0x00bb:
            r10.lastName = r11;
            goto L_0x00c2;
        L_0x00be:
            r11 = "";
            r10.lastName = r11;
        L_0x00c2:
            r11 = r10.nameTextView;
            r1 = r10.lastName;
            r11.setText(r1);
        L_0x00c9:
            r11 = r10.currentStatus;
            if (r11 == 0) goto L_0x00d3;
        L_0x00cd:
            r1 = r10.statusTextView;
            r1.setText(r11);
            goto L_0x0110;
        L_0x00d3:
            r11 = r10.currentUser;
            if (r11 == 0) goto L_0x0110;
        L_0x00d7:
            r11 = r11.phone;
            r11 = android.text.TextUtils.isEmpty(r11);
            if (r11 == 0) goto L_0x00ee;
        L_0x00df:
            r11 = r10.statusTextView;
            r1 = NUM; // 0x7f0e076c float:1.8878891E38 double:1.0531630954E-314;
            r3 = "NumberUnknown";
            r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
            r11.setText(r1);
            goto L_0x0110;
        L_0x00ee:
            r11 = r10.statusTextView;
            r1 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r4 = "+";
            r3.append(r4);
            r4 = r10.currentUser;
            r4 = r4.phone;
            r3.append(r4);
            r3 = r3.toString();
            r1 = r1.format(r3);
            r11.setText(r1);
        L_0x0110:
            r10.lastAvatar = r0;
            r11 = r10.currentUser;
            if (r11 == 0) goto L_0x0126;
        L_0x0116:
            r0 = r10.avatarImageView;
            r11 = org.telegram.messenger.ImageLocation.getForUser(r11, r2);
            r1 = r10.avatarDrawable;
            r2 = r10.currentUser;
            r3 = "50_50";
            r0.setImage(r11, r3, r1, r2);
            goto L_0x012d;
        L_0x0126:
            r11 = r10.avatarImageView;
            r0 = r10.avatarDrawable;
            r11.setImageDrawable(r0);
        L_0x012d:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookSelectShareAlert$UserCell.update(int):void");
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(70.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(70.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    public class ShareSearchAdapter extends SelectionAdapter {
        private int lastSearchId;
        private Context mContext;
        private ArrayList<Object> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Runnable searchRunnable;

        public int getItemViewType(int i) {
            return i == 0 ? 1 : 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public ShareSearchAdapter(Context context) {
            this.mContext = context;
        }

        public void search(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
                return;
            }
            int i = this.lastSearchId + 1;
            this.lastSearchId = i;
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            -$$Lambda$PhonebookSelectShareAlert$ShareSearchAdapter$_WGUIHYttDkAPmlZI1eNbtmVN5Q -__lambda_phonebookselectsharealert_sharesearchadapter__wguihyttdkapmlzi1enbtmvn5q = new -$$Lambda$PhonebookSelectShareAlert$ShareSearchAdapter$_WGUIHYttDkAPmlZI1eNbtmVN5Q(this, str, i);
            this.searchRunnable = -__lambda_phonebookselectsharealert_sharesearchadapter__wguihyttdkapmlzi1enbtmvn5q;
            dispatchQueue.postRunnable(-__lambda_phonebookselectsharealert_sharesearchadapter__wguihyttdkapmlzi1enbtmvn5q, 300);
        }

        public /* synthetic */ void lambda$search$0$PhonebookSelectShareAlert$ShareSearchAdapter(String str, int i) {
            processSearch(str, i);
        }

        private void processSearch(String str, int i) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$PhonebookSelectShareAlert$ShareSearchAdapter$S9CfVH5Tn8TTVACIMejHlILcnok(this, str, i));
        }

        public /* synthetic */ void lambda$processSearch$2$PhonebookSelectShareAlert$ShareSearchAdapter(String str, int i) {
            int i2 = UserConfig.selectedAccount;
            Utilities.searchQueue.postRunnable(new -$$Lambda$PhonebookSelectShareAlert$ShareSearchAdapter$hUXHNkNqax5V4fUX4qBJSo3_5Vc(this, str, new ArrayList(ContactsController.getInstance(i2).contactsBook.values()), new ArrayList(ContactsController.getInstance(i2).contacts), i2, i));
        }

        /* JADX WARNING: Removed duplicated region for block: B:70:0x018f A:{LOOP_END, LOOP:1: B:27:0x00a6->B:70:0x018f} */
        /* JADX WARNING: Removed duplicated region for block: B:111:0x0137 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:111:0x0137 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x018f A:{LOOP_END, LOOP:1: B:27:0x00a6->B:70:0x018f} */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x018f A:{LOOP_END, LOOP:1: B:27:0x00a6->B:70:0x018f} */
        /* JADX WARNING: Removed duplicated region for block: B:111:0x0137 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:111:0x0137 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x018f A:{LOOP_END, LOOP:1: B:27:0x00a6->B:70:0x018f} */
        /* JADX WARNING: Removed duplicated region for block: B:104:0x0272 A:{LOOP_END, LOOP:3: B:82:0x01ea->B:104:0x0272} */
        /* JADX WARNING: Removed duplicated region for block: B:116:0x0236 A:{SYNTHETIC} */
        /* JADX WARNING: Missing block: B:33:0x00c7, code skipped:
            if (r5.contains(r0.toString()) == false) goto L_0x00c9;
     */
        /* JADX WARNING: Missing block: B:38:0x00e4, code skipped:
            if (r11.contains(r0.toString()) != false) goto L_0x00e6;
     */
        /* JADX WARNING: Missing block: B:55:0x012e, code skipped:
            if (r15.contains(r0.toString()) != false) goto L_0x0134;
     */
        /* JADX WARNING: Missing block: B:91:0x0224, code skipped:
            if (r9.contains(r4.toString()) != false) goto L_0x0233;
     */
        public /* synthetic */ void lambda$null$1$PhonebookSelectShareAlert$ShareSearchAdapter(java.lang.String r21, java.util.ArrayList r22, java.util.ArrayList r23, int r24, int r25) {
            /*
            r20 = this;
            r0 = r20;
            r1 = r21;
            r2 = r21.trim();
            r2 = r2.toLowerCase();
            r3 = r2.length();
            if (r3 != 0) goto L_0x0025;
        L_0x0012:
            r2 = -1;
            r0.lastSearchId = r2;
            r2 = new java.util.ArrayList;
            r2.<init>();
            r3 = new java.util.ArrayList;
            r3.<init>();
            r4 = r0.lastSearchId;
            r0.updateSearchResults(r1, r2, r3, r4);
            return;
        L_0x0025:
            r3 = org.telegram.messenger.LocaleController.getInstance();
            r3 = r3.getTranslitString(r2);
            r4 = r2.equals(r3);
            if (r4 != 0) goto L_0x0039;
        L_0x0033:
            r4 = r3.length();
            if (r4 != 0) goto L_0x003a;
        L_0x0039:
            r3 = 0;
        L_0x003a:
            r4 = 0;
            r6 = 1;
            if (r3 == 0) goto L_0x0040;
        L_0x003e:
            r7 = 1;
            goto L_0x0041;
        L_0x0040:
            r7 = 0;
        L_0x0041:
            r7 = r7 + r6;
            r7 = new java.lang.String[r7];
            r7[r4] = r2;
            if (r3 == 0) goto L_0x004a;
        L_0x0048:
            r7[r6] = r3;
        L_0x004a:
            r2 = new java.util.ArrayList;
            r2.<init>();
            r3 = new java.util.ArrayList;
            r3.<init>();
            r8 = new android.util.SparseBooleanArray;
            r8.<init>();
            r9 = 0;
        L_0x005a:
            r10 = r22.size();
            r12 = "@";
            r13 = " ";
            if (r9 >= r10) goto L_0x01a3;
        L_0x0064:
            r10 = r22;
            r14 = r10.get(r9);
            r14 = (org.telegram.messenger.ContactsController.Contact) r14;
            r15 = r14.first_name;
            r4 = r14.last_name;
            r4 = org.telegram.messenger.ContactsController.formatName(r15, r4);
            r4 = r4.toLowerCase();
            r15 = org.telegram.messenger.LocaleController.getInstance();
            r15 = r15.getTranslitString(r4);
            r11 = r14.user;
            if (r11 == 0) goto L_0x0099;
        L_0x0084:
            r5 = r11.first_name;
            r11 = r11.last_name;
            r5 = org.telegram.messenger.ContactsController.formatName(r5, r11);
            r5 = r5.toLowerCase();
            r11 = org.telegram.messenger.LocaleController.getInstance();
            r11 = r11.getTranslitString(r4);
            goto L_0x009b;
        L_0x0099:
            r5 = 0;
            r11 = 0;
        L_0x009b:
            r16 = r4.equals(r15);
            if (r16 == 0) goto L_0x00a2;
        L_0x00a1:
            r15 = 0;
        L_0x00a2:
            r6 = r7.length;
            r10 = 0;
            r17 = 0;
        L_0x00a6:
            if (r10 >= r6) goto L_0x019b;
        L_0x00a8:
            r18 = r6;
            r6 = r7[r10];
            if (r5 == 0) goto L_0x00c9;
        L_0x00ae:
            r19 = r5.startsWith(r6);
            if (r19 != 0) goto L_0x00e6;
        L_0x00b4:
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r0.append(r13);
            r0.append(r6);
            r0 = r0.toString();
            r0 = r5.contains(r0);
            if (r0 != 0) goto L_0x00e6;
        L_0x00c9:
            if (r11 == 0) goto L_0x00e8;
        L_0x00cb:
            r0 = r11.startsWith(r6);
            if (r0 != 0) goto L_0x00e6;
        L_0x00d1:
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r0.append(r13);
            r0.append(r6);
            r0 = r0.toString();
            r0 = r11.contains(r0);
            if (r0 == 0) goto L_0x00e8;
        L_0x00e6:
            r0 = 1;
            goto L_0x0135;
        L_0x00e8:
            r0 = r14.user;
            if (r0 == 0) goto L_0x00f8;
        L_0x00ec:
            r0 = r0.username;
            if (r0 == 0) goto L_0x00f8;
        L_0x00f0:
            r0 = r0.startsWith(r6);
            if (r0 == 0) goto L_0x00f8;
        L_0x00f6:
            r0 = 2;
            goto L_0x0135;
        L_0x00f8:
            r0 = r4.startsWith(r6);
            if (r0 != 0) goto L_0x0134;
        L_0x00fe:
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r0.append(r13);
            r0.append(r6);
            r0 = r0.toString();
            r0 = r4.contains(r0);
            if (r0 != 0) goto L_0x0134;
        L_0x0113:
            if (r15 == 0) goto L_0x0131;
        L_0x0115:
            r0 = r15.startsWith(r6);
            if (r0 != 0) goto L_0x0134;
        L_0x011b:
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r0.append(r13);
            r0.append(r6);
            r0 = r0.toString();
            r0 = r15.contains(r0);
            if (r0 == 0) goto L_0x0131;
        L_0x0130:
            goto L_0x0134;
        L_0x0131:
            r0 = r17;
            goto L_0x0135;
        L_0x0134:
            r0 = 3;
        L_0x0135:
            if (r0 == 0) goto L_0x018f;
        L_0x0137:
            r4 = 3;
            if (r0 != r4) goto L_0x0146;
        L_0x013a:
            r0 = r14.first_name;
            r4 = r14.last_name;
            r0 = org.telegram.messenger.AndroidUtilities.generateSearchName(r0, r4, r6);
            r3.add(r0);
            goto L_0x0181;
        L_0x0146:
            r4 = 1;
            if (r0 != r4) goto L_0x0157;
        L_0x0149:
            r0 = r14.user;
            r4 = r0.first_name;
            r0 = r0.last_name;
            r0 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r0, r6);
            r3.add(r0);
            goto L_0x0181;
        L_0x0157:
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r0.append(r12);
            r4 = r14.user;
            r4 = r4.username;
            r0.append(r4);
            r0 = r0.toString();
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r4.append(r12);
            r4.append(r6);
            r4 = r4.toString();
            r5 = 0;
            r0 = org.telegram.messenger.AndroidUtilities.generateSearchName(r0, r5, r4);
            r3.add(r0);
        L_0x0181:
            r0 = r14.user;
            if (r0 == 0) goto L_0x018b;
        L_0x0185:
            r0 = r0.id;
            r4 = 1;
            r8.put(r0, r4);
        L_0x018b:
            r2.add(r14);
            goto L_0x019b;
        L_0x018f:
            r17 = r4;
            r10 = r10 + 1;
            r6 = r18;
            r17 = r0;
            r0 = r20;
            goto L_0x00a6;
        L_0x019b:
            r9 = r9 + 1;
            r4 = 0;
            r6 = 1;
            r0 = r20;
            goto L_0x005a;
        L_0x01a3:
            r0 = 0;
        L_0x01a4:
            r4 = r23.size();
            if (r0 >= r4) goto L_0x027e;
        L_0x01aa:
            r4 = r23;
            r5 = r4.get(r0);
            r5 = (org.telegram.tgnet.TLRPC.TL_contact) r5;
            r6 = r5.user_id;
            r6 = r8.indexOfKey(r6);
            if (r6 < 0) goto L_0x01be;
        L_0x01ba:
            r4 = 1;
            r15 = 0;
            goto L_0x027a;
        L_0x01be:
            r6 = org.telegram.messenger.MessagesController.getInstance(r24);
            r5 = r5.user_id;
            r5 = java.lang.Integer.valueOf(r5);
            r5 = r6.getUser(r5);
            r6 = r5.first_name;
            r9 = r5.last_name;
            r6 = org.telegram.messenger.ContactsController.formatName(r6, r9);
            r6 = r6.toLowerCase();
            r9 = org.telegram.messenger.LocaleController.getInstance();
            r9 = r9.getTranslitString(r6);
            r10 = r6.equals(r9);
            if (r10 == 0) goto L_0x01e7;
        L_0x01e6:
            r9 = 0;
        L_0x01e7:
            r10 = r7.length;
            r11 = 0;
            r14 = 0;
        L_0x01ea:
            if (r11 >= r10) goto L_0x01ba;
        L_0x01ec:
            r15 = r7[r11];
            r17 = r6.startsWith(r15);
            if (r17 != 0) goto L_0x0233;
        L_0x01f4:
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r4.append(r13);
            r4.append(r15);
            r4 = r4.toString();
            r4 = r6.contains(r4);
            if (r4 != 0) goto L_0x0233;
        L_0x0209:
            if (r9 == 0) goto L_0x0227;
        L_0x020b:
            r4 = r9.startsWith(r15);
            if (r4 != 0) goto L_0x0233;
        L_0x0211:
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r4.append(r13);
            r4.append(r15);
            r4 = r4.toString();
            r4 = r9.contains(r4);
            if (r4 == 0) goto L_0x0227;
        L_0x0226:
            goto L_0x0233;
        L_0x0227:
            r4 = r5.username;
            if (r4 == 0) goto L_0x0234;
        L_0x022b:
            r4 = r4.startsWith(r15);
            if (r4 == 0) goto L_0x0234;
        L_0x0231:
            r14 = 2;
            goto L_0x0234;
        L_0x0233:
            r14 = 1;
        L_0x0234:
            if (r14 == 0) goto L_0x0272;
        L_0x0236:
            r4 = 1;
            if (r14 != r4) goto L_0x0246;
        L_0x0239:
            r6 = r5.first_name;
            r9 = r5.last_name;
            r6 = org.telegram.messenger.AndroidUtilities.generateSearchName(r6, r9, r15);
            r3.add(r6);
            r15 = 0;
            goto L_0x026e;
        L_0x0246:
            r6 = new java.lang.StringBuilder;
            r6.<init>();
            r6.append(r12);
            r9 = r5.username;
            r6.append(r9);
            r6 = r6.toString();
            r9 = new java.lang.StringBuilder;
            r9.<init>();
            r9.append(r12);
            r9.append(r15);
            r9 = r9.toString();
            r15 = 0;
            r6 = org.telegram.messenger.AndroidUtilities.generateSearchName(r6, r15, r9);
            r3.add(r6);
        L_0x026e:
            r2.add(r5);
            goto L_0x027a;
        L_0x0272:
            r4 = 1;
            r15 = 0;
            r11 = r11 + 1;
            r4 = r23;
            goto L_0x01ea;
        L_0x027a:
            r0 = r0 + 1;
            goto L_0x01a4;
        L_0x027e:
            r0 = r20;
            r5 = r25;
            r0.updateSearchResults(r1, r2, r3, r5);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookSelectShareAlert$ShareSearchAdapter.lambda$null$1$PhonebookSelectShareAlert$ShareSearchAdapter(java.lang.String, java.util.ArrayList, java.util.ArrayList, int, int):void");
        }

        private void updateSearchResults(String str, ArrayList<Object> arrayList, ArrayList<CharSequence> arrayList2, int i) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$PhonebookSelectShareAlert$ShareSearchAdapter$wrJnOU5kk5l2N2hm6SkXE2ibf8A(this, i, arrayList, arrayList2));
        }

        public /* synthetic */ void lambda$updateSearchResults$3$PhonebookSelectShareAlert$ShareSearchAdapter(int i, ArrayList arrayList, ArrayList arrayList2) {
            if (i == this.lastSearchId) {
                if (!(i == -1 || PhonebookSelectShareAlert.this.listView.getAdapter() == PhonebookSelectShareAlert.this.searchAdapter)) {
                    PhonebookSelectShareAlert phonebookSelectShareAlert = PhonebookSelectShareAlert.this;
                    phonebookSelectShareAlert.topBeforeSwitch = phonebookSelectShareAlert.getCurrentTop();
                    PhonebookSelectShareAlert.this.listView.setAdapter(PhonebookSelectShareAlert.this.searchAdapter);
                }
                Object obj = 1;
                Object obj2 = (this.searchResult.isEmpty() || !arrayList.isEmpty()) ? null : 1;
                if (!(this.searchResult.isEmpty() && arrayList.isEmpty())) {
                    obj = null;
                }
                if (obj2 != null) {
                    PhonebookSelectShareAlert phonebookSelectShareAlert2 = PhonebookSelectShareAlert.this;
                    phonebookSelectShareAlert2.topBeforeSwitch = phonebookSelectShareAlert2.getCurrentTop();
                }
                this.searchResult = arrayList;
                this.searchResultNames = arrayList2;
                notifyDataSetChanged();
                if (obj == null && obj2 == null && PhonebookSelectShareAlert.this.topBeforeSwitch > 0) {
                    PhonebookSelectShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -PhonebookSelectShareAlert.this.topBeforeSwitch);
                    PhonebookSelectShareAlert.this.topBeforeSwitch = -1000;
                }
            }
        }

        public int getItemCount() {
            return this.searchResult.isEmpty() ? 0 : this.searchResult.size() + 1;
        }

        public Object getItem(int i) {
            return i == 0 ? null : this.searchResult.get(i - 1);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new View(this.mContext);
                view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            } else {
                view = new UserCell(this.mContext);
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                User user;
                UserCell userCell = (UserCell) viewHolder.itemView;
                boolean z = i != getItemCount() - 1;
                Object item = getItem(i);
                if (item instanceof Contact) {
                    Contact contact = (Contact) item;
                    user = contact.user;
                    if (user == null) {
                        userCell.setCurrentId(contact.contact_id);
                        userCell.setData(null, (CharSequence) this.searchResultNames.get(i - 1), contact.phones.isEmpty() ? "" : PhoneFormat.getInstance().format((String) contact.phones.get(0)), z);
                        user = null;
                    }
                } else {
                    user = (User) item;
                }
                if (user != null) {
                    CharSequence charSequence = (CharSequence) this.searchResultNames.get(i - 1);
                    PhoneFormat instance = PhoneFormat.getInstance();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(user.phone);
                    userCell.setData(user, charSequence, instance.format(stringBuilder.toString()), z);
                }
            }
        }
    }

    public class ShareAdapter extends SectionsAdapter {
        private int currentAccount = UserConfig.selectedAccount;
        private Context mContext;

        public int getItemViewType(int i, int i2) {
            return i == 0 ? 1 : 0;
        }

        public String getLetter(int i) {
            return null;
        }

        public int getPositionForScrollProgress(float f) {
            return 0;
        }

        public View getSectionHeaderView(int i, View view) {
            return null;
        }

        public ShareAdapter(Context context) {
            this.mContext = context;
        }

        public Object getItem(int i, int i2) {
            if (i == 0) {
                return null;
            }
            i--;
            HashMap hashMap = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
            ArrayList arrayList = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
            if (i < arrayList.size()) {
                ArrayList arrayList2 = (ArrayList) hashMap.get(arrayList.get(i));
                if (i2 < arrayList2.size()) {
                    return arrayList2.get(i2);
                }
            }
            return null;
        }

        public boolean isEnabled(int i, int i2) {
            boolean z = false;
            if (i == 0) {
                return false;
            }
            if (i2 < ((ArrayList) ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict.get(ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.get(i - 1))).size()) {
                z = true;
            }
            return z;
        }

        public int getSectionCount() {
            return ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.size() + 1;
        }

        public int getCountForSection(int i) {
            if (i == 0) {
                return 1;
            }
            i--;
            HashMap hashMap = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
            ArrayList arrayList = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
            return i < arrayList.size() ? ((ArrayList) hashMap.get(arrayList.get(i))).size() : 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new View(this.mContext);
                view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            } else {
                view = new UserCell(this.mContext);
            }
            return new Holder(view);
        }

        public void onBindViewHolder(int i, int i2, ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 0) {
                User user;
                UserCell userCell = (UserCell) viewHolder.itemView;
                Object item = getItem(i, i2);
                boolean z = true;
                if (i == getSectionCount() - 1 && i2 == getCountForSection(i) - 1) {
                    z = false;
                }
                if (item instanceof Contact) {
                    Contact contact = (Contact) item;
                    user = contact.user;
                    if (user == null) {
                        userCell.setCurrentId(contact.contact_id);
                        userCell.setData(null, ContactsController.formatName(contact.first_name, contact.last_name), contact.phones.isEmpty() ? "" : PhoneFormat.getInstance().format((String) contact.phones.get(0)), z);
                        user = null;
                    }
                } else {
                    user = (User) item;
                }
                if (user != null) {
                    PhoneFormat instance = PhoneFormat.getInstance();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(user.phone);
                    userCell.setData(user, null, instance.format(stringBuilder.toString()), z);
                }
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public PhonebookSelectShareAlert(ChatActivity chatActivity) {
        super(chatActivity.getParentActivity(), true);
        this.chatActivity = chatActivity;
        Activity parentActivity = chatActivity.getParentActivity();
        this.shadowDrawable = parentActivity.getResources().getDrawable(NUM).mutate();
        String str = "dialogBackground";
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), Mode.MULTIPLY));
        this.searchAdapter = new ShareSearchAdapter(parentActivity);
        this.containerView = new FrameLayout(parentActivity) {
            private boolean fullHeight;
            private boolean ignoreLayout = false;
            private RectF rect1 = new RectF();

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                i2 = MeasureSpec.getSize(i2);
                boolean z = true;
                if (VERSION.SDK_INT >= 21 && !PhonebookSelectShareAlert.this.isFullscreen) {
                    this.ignoreLayout = true;
                    setPadding(PhonebookSelectShareAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, PhonebookSelectShareAlert.this.backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
                int paddingTop = i2 - getPaddingTop();
                int max = Math.max(PhonebookSelectShareAlert.this.searchAdapter.getItemCount(), PhonebookSelectShareAlert.this.listAdapter.getItemCount());
                if (max > 0) {
                    max--;
                }
                int dp = (((AndroidUtilities.dp(91.0f) + (AndroidUtilities.dp(64.0f) * max)) + max) - 1) + PhonebookSelectShareAlert.this.backgroundPaddingTop;
                if (dp < paddingTop) {
                    paddingTop = 0;
                } else {
                    paddingTop -= (paddingTop / 5) * 3;
                }
                paddingTop += AndroidUtilities.dp(8.0f);
                if (PhonebookSelectShareAlert.this.listView.getPaddingTop() != paddingTop) {
                    this.ignoreLayout = true;
                    PhonebookSelectShareAlert.this.listView.setPadding(0, paddingTop, 0, 0);
                    this.ignoreLayout = false;
                }
                if (dp < i2) {
                    z = false;
                }
                this.fullHeight = z;
                super.onMeasure(i, MeasureSpec.makeMeasureSpec(Math.min(dp, i2), NUM));
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                PhonebookSelectShareAlert.this.updateLayout();
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0 || PhonebookSelectShareAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ((float) (PhonebookSelectShareAlert.this.scrollOffsetY - AndroidUtilities.dp(30.0f)))) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                PhonebookSelectShareAlert.this.dismiss();
                return true;
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !PhonebookSelectShareAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }

            /* Access modifiers changed, original: protected */
            /* JADX WARNING: Removed duplicated region for block: B:17:0x00b9  */
            /* JADX WARNING: Removed duplicated region for block: B:22:? A:{SYNTHETIC, RETURN} */
            /* JADX WARNING: Removed duplicated region for block: B:20:0x014c  */
            /* JADX WARNING: Removed duplicated region for block: B:17:0x00b9  */
            /* JADX WARNING: Removed duplicated region for block: B:20:0x014c  */
            /* JADX WARNING: Removed duplicated region for block: B:22:? A:{SYNTHETIC, RETURN} */
            public void onDraw(android.graphics.Canvas r14) {
                /*
                r13 = this;
                r0 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r0 = r0.scrollOffsetY;
                r1 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r1 = r1.backgroundPaddingTop;
                r0 = r0 - r1;
                r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r0 = r0 + r1;
                r1 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r1 = r1.scrollOffsetY;
                r2 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r2 = r2.backgroundPaddingTop;
                r1 = r1 - r2;
                r2 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
                r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
                r1 = r1 - r2;
                r2 = r13.getMeasuredHeight();
                r3 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
                r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
                r2 = r2 + r3;
                r3 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r3 = r3.backgroundPaddingTop;
                r2 = r2 + r3;
                r3 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r3 = r3.isFullscreen;
                r4 = 0;
                r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                if (r3 != 0) goto L_0x009a;
            L_0x0045:
                r3 = android.os.Build.VERSION.SDK_INT;
                r6 = 21;
                if (r3 < r6) goto L_0x009a;
            L_0x004b:
                r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r1 = r1 + r3;
                r0 = r0 + r3;
                r2 = r2 - r3;
                r3 = r13.fullHeight;
                if (r3 == 0) goto L_0x009a;
            L_0x0054:
                r3 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r3 = r3.backgroundPaddingTop;
                r3 = r3 + r1;
                r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r7 = r6 * 2;
                if (r3 >= r7) goto L_0x007f;
            L_0x0061:
                r3 = r6 * 2;
                r3 = r3 - r1;
                r7 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r7 = r7.backgroundPaddingTop;
                r3 = r3 - r7;
                r3 = java.lang.Math.min(r6, r3);
                r1 = r1 - r3;
                r2 = r2 + r3;
                r3 = r3 * 2;
                r3 = (float) r3;
                r6 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r6 = (float) r6;
                r3 = r3 / r6;
                r3 = java.lang.Math.min(r5, r3);
                r3 = r5 - r3;
                goto L_0x0081;
            L_0x007f:
                r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            L_0x0081:
                r6 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r6 = r6.backgroundPaddingTop;
                r6 = r6 + r1;
                r7 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                if (r6 >= r7) goto L_0x009c;
            L_0x008c:
                r6 = r7 - r1;
                r8 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r8 = r8.backgroundPaddingTop;
                r6 = r6 - r8;
                r6 = java.lang.Math.min(r7, r6);
                goto L_0x009d;
            L_0x009a:
                r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            L_0x009c:
                r6 = 0;
            L_0x009d:
                r7 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r7 = r7.shadowDrawable;
                r8 = r13.getMeasuredWidth();
                r7.setBounds(r4, r1, r8, r2);
                r2 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r2 = r2.shadowDrawable;
                r2.draw(r14);
                r2 = "dialogBackground";
                r4 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
                if (r4 == 0) goto L_0x0108;
            L_0x00b9:
                r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r5 = org.telegram.ui.ActionBar.Theme.getColor(r2);
                r4.setColor(r5);
                r4 = r13.rect1;
                r5 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r5 = r5.backgroundPaddingLeft;
                r5 = (float) r5;
                r7 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r7 = r7.backgroundPaddingTop;
                r7 = r7 + r1;
                r7 = (float) r7;
                r8 = r13.getMeasuredWidth();
                r9 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r9 = r9.backgroundPaddingLeft;
                r8 = r8 - r9;
                r8 = (float) r8;
                r9 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r9 = r9.backgroundPaddingTop;
                r9 = r9 + r1;
                r1 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r9 = r9 + r1;
                r1 = (float) r9;
                r4.set(r5, r7, r8, r1);
                r1 = r13.rect1;
                r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
                r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r5 = (float) r5;
                r5 = r5 * r3;
                r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
                r4 = (float) r4;
                r4 = r4 * r3;
                r3 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r14.drawRoundRect(r1, r5, r4, r3);
            L_0x0108:
                r1 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r3 = r13.rect1;
                r4 = r13.getMeasuredWidth();
                r4 = r4 - r1;
                r4 = r4 / 2;
                r4 = (float) r4;
                r5 = (float) r0;
                r7 = r13.getMeasuredWidth();
                r7 = r7 + r1;
                r7 = r7 / 2;
                r1 = (float) r7;
                r7 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
                r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
                r0 = r0 + r7;
                r0 = (float) r0;
                r3.set(r4, r5, r1, r0);
                r0 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r1 = "key_sheet_scrollUp";
                r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
                r0.setColor(r1);
                r0 = r13.rect1;
                r1 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
                r3 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r3 = (float) r3;
                r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
                r1 = (float) r1;
                r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r14.drawRoundRect(r0, r3, r1, r4);
                if (r6 <= 0) goto L_0x0196;
            L_0x014c:
                r0 = org.telegram.ui.ActionBar.Theme.getColor(r2);
                r1 = 255; // 0xff float:3.57E-43 double:1.26E-321;
                r2 = android.graphics.Color.red(r0);
                r2 = (float) r2;
                r3 = NUM; // 0x3f4ccccd float:0.8 double:5.246966156E-315;
                r2 = r2 * r3;
                r2 = (int) r2;
                r4 = android.graphics.Color.green(r0);
                r4 = (float) r4;
                r4 = r4 * r3;
                r4 = (int) r4;
                r0 = android.graphics.Color.blue(r0);
                r0 = (float) r0;
                r0 = r0 * r3;
                r0 = (int) r0;
                r0 = android.graphics.Color.argb(r1, r2, r4, r0);
                r1 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r1.setColor(r0);
                r0 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r0 = r0.backgroundPaddingLeft;
                r8 = (float) r0;
                r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r0 = r0 - r6;
                r9 = (float) r0;
                r0 = r13.getMeasuredWidth();
                r1 = org.telegram.ui.Components.PhonebookSelectShareAlert.this;
                r1 = r1.backgroundPaddingLeft;
                r0 = r0 - r1;
                r10 = (float) r0;
                r0 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
                r11 = (float) r0;
                r12 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint;
                r7 = r14;
                r7.drawRect(r8, r9, r10, r11, r12);
            L_0x0196:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PhonebookSelectShareAlert$AnonymousClass1.onDraw(android.graphics.Canvas):void");
            }
        };
        this.containerView.setWillNotDraw(false);
        ViewGroup viewGroup = this.containerView;
        int i = this.backgroundPaddingLeft;
        viewGroup.setPadding(i, 0, i, 0);
        this.frameLayout = new FrameLayout(parentActivity);
        this.frameLayout.setBackgroundColor(Theme.getColor(str));
        this.frameLayout.addView(new SearchField(parentActivity), LayoutHelper.createFrame(-1, -1, 51));
        this.listView = new RecyclerListView(parentActivity) {
            /* Access modifiers changed, original: protected */
            public boolean allowSelectChildAtPosition(float f, float f2) {
                return f2 >= ((float) ((PhonebookSelectShareAlert.this.scrollOffsetY + AndroidUtilities.dp(48.0f)) + (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }
        };
        this.listView.setClipToPadding(false);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        recyclerListView = this.listView;
        ShareAdapter shareAdapter = new ShareAdapter(parentActivity);
        this.listAdapter = shareAdapter;
        recyclerListView.setAdapter(shareAdapter);
        this.listView.setGlowColor(Theme.getColor("dialogScrollGlow"));
        this.listView.setOnItemClickListener(new -$$Lambda$PhonebookSelectShareAlert$bUrPncc8Ri4Mi84xxZUi9Xydi3o(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                PhonebookSelectShareAlert.this.updateLayout();
            }
        });
        this.searchEmptyView = new EmptyTextProgressView(parentActivity);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.showTextView();
        this.searchEmptyView.setText(LocaleController.getString("NoContacts", NUM));
        this.listView.setEmptyView(this.searchEmptyView);
        this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 52.0f, 0.0f, 0.0f));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(58.0f);
        this.shadow = new View(parentActivity);
        this.shadow.setBackgroundColor(Theme.getColor("dialogShadowLine"));
        this.shadow.setAlpha(0.0f);
        this.shadow.setTag(Integer.valueOf(1));
        this.containerView.addView(this.shadow, layoutParams);
        this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 58, 51));
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
    }

    public /* synthetic */ void lambda$new$1$PhonebookSelectShareAlert(View view, int i) {
        Object item;
        Adapter adapter = this.listView.getAdapter();
        Adapter adapter2 = this.searchAdapter;
        if (adapter == adapter2) {
            item = adapter2.getItem(i);
        } else {
            int sectionForPosition = this.listAdapter.getSectionForPosition(i);
            i = this.listAdapter.getPositionInSectionForPosition(i);
            if (i >= 0 && sectionForPosition >= 0) {
                item = this.listAdapter.getItem(sectionForPosition, i);
            } else {
                return;
            }
        }
        if (item != null) {
            Contact contact;
            String formatName;
            if (item instanceof Contact) {
                Contact contact2 = (Contact) item;
                User user = contact2.user;
                contact = contact2;
                formatName = user != null ? ContactsController.formatName(user.first_name, user.last_name) : "";
            } else {
                User user2 = (User) item;
                Contact contact3 = new Contact();
                contact3.first_name = user2.first_name;
                contact3.last_name = user2.last_name;
                contact3.phones.add(user2.phone);
                contact3.user = user2;
                formatName = ContactsController.formatName(contact3.first_name, contact3.last_name);
                contact = contact3;
            }
            PhonebookShareAlert phonebookShareAlert = new PhonebookShareAlert(this.chatActivity, contact, null, null, null, formatName);
            phonebookShareAlert.setDelegate(new -$$Lambda$PhonebookSelectShareAlert$s8Ge4bVdSAGz3-QOO4upUWstpTA(this));
            phonebookShareAlert.show();
        }
    }

    public /* synthetic */ void lambda$null$0$PhonebookSelectShareAlert(User user, boolean z, int i) {
        dismiss();
        this.delegate.didSelectContact(user, z, i);
    }

    private int getCurrentTop() {
        if (this.listView.getChildCount() != 0) {
            int i = 0;
            View childAt = this.listView.getChildAt(0);
            Holder holder = (Holder) this.listView.findContainingViewHolder(childAt);
            if (holder != null) {
                int paddingTop = this.listView.getPaddingTop();
                if (holder.getAdapterPosition() == 0 && childAt.getTop() >= 0) {
                    i = childAt.getTop();
                }
                return paddingTop - i;
            }
        }
        return -1000;
    }

    public void setDelegate(PhonebookShareAlertDelegate phonebookShareAlertDelegate) {
        this.delegate = phonebookShareAlertDelegate;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.contactsDidLoad) {
            ShareAdapter shareAdapter = this.listAdapter;
            if (shareAdapter != null) {
                shareAdapter.notifyDataSetChanged();
            }
        }
    }

    @SuppressLint({"NewApi"})
    private void updateLayout() {
        if (this.listView.getChildCount() > 0) {
            View childAt = this.listView.getChildAt(0);
            Holder holder = (Holder) this.listView.findContainingViewHolder(childAt);
            int top = childAt.getTop() - AndroidUtilities.dp(8.0f);
            int i = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
            if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
                runShadowAnimation(true);
                top = i;
            } else {
                runShadowAnimation(false);
            }
            if (this.scrollOffsetY != top) {
                RecyclerListView recyclerListView = this.listView;
                this.scrollOffsetY = top;
                recyclerListView.setTopGlowOffset(top);
                this.frameLayout.setTranslationY((float) this.scrollOffsetY);
                this.searchEmptyView.setTranslationY((float) this.scrollOffsetY);
                this.containerView.invalidate();
            }
        }
    }

    private void runShadowAnimation(final boolean z) {
        if ((z && this.shadow.getTag() != null) || (!z && this.shadow.getTag() == null)) {
            this.shadow.setTag(z ? null : Integer.valueOf(1));
            if (z) {
                this.shadow.setVisibility(0);
            }
            AnimatorSet animatorSet = this.shadowAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.shadowAnimation = new AnimatorSet();
            animatorSet = this.shadowAnimation;
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (PhonebookSelectShareAlert.this.shadowAnimation != null && PhonebookSelectShareAlert.this.shadowAnimation.equals(animator)) {
                        if (!z) {
                            PhonebookSelectShareAlert.this.shadow.setVisibility(4);
                        }
                        PhonebookSelectShareAlert.this.shadowAnimation = null;
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    if (PhonebookSelectShareAlert.this.shadowAnimation != null && PhonebookSelectShareAlert.this.shadowAnimation.equals(animator)) {
                        PhonebookSelectShareAlert.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
    }
}
