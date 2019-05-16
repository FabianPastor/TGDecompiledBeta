package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate.-CC;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.FastScrollAdapter;
import org.telegram.ui.Components.RecyclerListView.Holder;

public class GroupCreateActivity extends BaseFragment implements NotificationCenterDelegate, OnClickListener {
    private static final int done_button = 1;
    private GroupCreateAdapter adapter;
    private ArrayList<GroupCreateSpan> allSpans = new ArrayList();
    private int chatId;
    private int chatType = 0;
    private int containerHeight;
    private GroupCreateSpan currentDeletingSpan;
    private AnimatorSet currentDoneButtonAnimation;
    private GroupCreateActivityDelegate delegate;
    private View doneButton;
    private boolean doneButtonVisible;
    private EditTextBoldCursor editText;
    private EmptyTextProgressView emptyView;
    private int fieldY;
    private ImageView floatingButton;
    private boolean ignoreScrollEvent;
    private boolean isAlwaysShare;
    private boolean isGroup;
    private boolean isNeverShare;
    private GroupCreateDividerItemDecoration itemDecoration;
    private RecyclerListView listView;
    private int maxCount = MessagesController.getInstance(this.currentAccount).maxMegagroupCount;
    private ScrollView scrollView;
    private boolean searchWas;
    private boolean searching;
    private SparseArray<GroupCreateSpan> selectedContacts = new SparseArray();
    private boolean showFabButton;
    private SpansContainer spansContainer;

    public interface GroupCreateActivityDelegate {
        void didSelectUsers(ArrayList<Integer> arrayList);
    }

    private class SpansContainer extends ViewGroup {
        private View addingSpan;
        private boolean animationStarted;
        private ArrayList<Animator> animators = new ArrayList();
        private AnimatorSet currentAnimation;
        private View removingSpan;

        public SpansContainer(Context context) {
            super(context);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            String str;
            String str2;
            int childCount = getChildCount();
            int size = MeasureSpec.getSize(i);
            float f = 32.0f;
            int dp = size - AndroidUtilities.dp(32.0f);
            int dp2 = AndroidUtilities.dp(12.0f);
            int dp3 = AndroidUtilities.dp(12.0f);
            int i3 = 0;
            int i4 = 0;
            int i5 = 0;
            while (true) {
                str = "translationY";
                str2 = "translationX";
                if (i3 >= childCount) {
                    break;
                }
                View childAt = getChildAt(i3);
                if (childAt instanceof GroupCreateSpan) {
                    float f2;
                    float f3;
                    childAt.measure(MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(f), NUM));
                    if (childAt == this.removingSpan || childAt.getMeasuredWidth() + i4 <= dp) {
                        f2 = 12.0f;
                    } else {
                        f2 = 12.0f;
                        dp2 += childAt.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                        i4 = 0;
                    }
                    if (childAt.getMeasuredWidth() + i5 > dp) {
                        dp3 += childAt.getMeasuredHeight() + AndroidUtilities.dp(f2);
                        f3 = 16.0f;
                        i5 = 0;
                    } else {
                        f3 = 16.0f;
                    }
                    int dp4 = AndroidUtilities.dp(f3) + i4;
                    if (!this.animationStarted) {
                        View view = this.removingSpan;
                        if (childAt == view) {
                            childAt.setTranslationX((float) (AndroidUtilities.dp(f3) + i5));
                            childAt.setTranslationY((float) dp3);
                        } else if (view != null) {
                            int i6;
                            f2 = (float) dp4;
                            if (childAt.getTranslationX() != f2) {
                                ArrayList arrayList = this.animators;
                                dp4 = 1;
                                float[] fArr = new float[1];
                                i6 = 0;
                                fArr[0] = f2;
                                arrayList.add(ObjectAnimator.ofFloat(childAt, str2, fArr));
                            } else {
                                dp4 = 1;
                                i6 = 0;
                            }
                            f3 = (float) dp2;
                            if (childAt.getTranslationY() != f3) {
                                ArrayList arrayList2 = this.animators;
                                float[] fArr2 = new float[dp4];
                                fArr2[i6] = f3;
                                arrayList2.add(ObjectAnimator.ofFloat(childAt, str, fArr2));
                            }
                        } else {
                            childAt.setTranslationX((float) dp4);
                            childAt.setTranslationY((float) dp2);
                        }
                    }
                    if (childAt != this.removingSpan) {
                        i4 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    i5 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
                i3++;
                f = 32.0f;
            }
            if (AndroidUtilities.isTablet()) {
                childCount = AndroidUtilities.dp(366.0f) / 3;
            } else {
                Point point = AndroidUtilities.displaySize;
                childCount = (Math.min(point.x, point.y) - AndroidUtilities.dp(164.0f)) / 3;
            }
            if (dp - i4 < childCount) {
                dp2 += AndroidUtilities.dp(44.0f);
                i4 = 0;
            }
            if (dp - i5 < childCount) {
                dp3 += AndroidUtilities.dp(44.0f);
            }
            GroupCreateActivity.this.editText.measure(MeasureSpec.makeMeasureSpec(dp - i4, NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), NUM));
            if (!this.animationStarted) {
                dp3 += AndroidUtilities.dp(44.0f);
                i4 += AndroidUtilities.dp(16.0f);
                GroupCreateActivity.this.fieldY = dp2;
                if (this.currentAnimation != null) {
                    boolean z;
                    if (GroupCreateActivity.this.containerHeight != dp2 + AndroidUtilities.dp(44.0f)) {
                        this.animators.add(ObjectAnimator.ofInt(GroupCreateActivity.this, "containerHeight", new int[]{dp2}));
                    }
                    if (GroupCreateActivity.this.editText.getTranslationX() != ((float) i4)) {
                        this.animators.add(ObjectAnimator.ofFloat(GroupCreateActivity.this.editText, str2, new float[]{f}));
                    }
                    if (GroupCreateActivity.this.editText.getTranslationY() != ((float) GroupCreateActivity.this.fieldY)) {
                        ArrayList arrayList3 = this.animators;
                        EditTextBoldCursor access$000 = GroupCreateActivity.this.editText;
                        float[] fArr3 = new float[1];
                        z = false;
                        fArr3[0] = (float) GroupCreateActivity.this.fieldY;
                        arrayList3.add(ObjectAnimator.ofFloat(access$000, str, fArr3));
                    } else {
                        z = false;
                    }
                    GroupCreateActivity.this.editText.setAllowDrawCursor(z);
                    this.currentAnimation.playTogether(this.animators);
                    this.currentAnimation.start();
                    this.animationStarted = true;
                } else {
                    GroupCreateActivity.this.containerHeight = dp3;
                    GroupCreateActivity.this.editText.setTranslationX((float) i4);
                    GroupCreateActivity.this.editText.setTranslationY((float) GroupCreateActivity.this.fieldY);
                }
            } else if (!(this.currentAnimation == null || GroupCreateActivity.this.ignoreScrollEvent || this.removingSpan != null)) {
                GroupCreateActivity.this.editText.bringPointIntoView(GroupCreateActivity.this.editText.getSelectionStart());
            }
            setMeasuredDimension(size, GroupCreateActivity.this.containerHeight);
        }

        /* Access modifiers changed, original: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int childCount = getChildCount();
            for (i2 = 0; i2 < childCount; i2++) {
                View childAt = getChildAt(i2);
                childAt.layout(0, 0, childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
            }
        }

        public void addSpan(GroupCreateSpan groupCreateSpan) {
            GroupCreateActivity.this.allSpans.add(groupCreateSpan);
            GroupCreateActivity.this.selectedContacts.put(groupCreateSpan.getUid(), groupCreateSpan);
            GroupCreateActivity.this.editText.setHintVisible(false);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.addingSpan = null;
                    SpansContainer.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    GroupCreateActivity.this.editText.setAllowDrawCursor(true);
                }
            });
            this.currentAnimation.setDuration(150);
            this.addingSpan = groupCreateSpan;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleX", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleY", new float[]{0.01f, 1.0f}));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "alpha", new float[]{0.0f, 1.0f}));
            addView(groupCreateSpan);
        }

        public void removeSpan(final GroupCreateSpan groupCreateSpan) {
            GroupCreateActivity.this.ignoreScrollEvent = true;
            GroupCreateActivity.this.selectedContacts.remove(groupCreateSpan.getUid());
            GroupCreateActivity.this.allSpans.remove(groupCreateSpan);
            groupCreateSpan.setOnClickListener(null);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            this.currentAnimation = new AnimatorSet();
            this.currentAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(groupCreateSpan);
                    SpansContainer.this.removingSpan = null;
                    SpansContainer.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    GroupCreateActivity.this.editText.setAllowDrawCursor(true);
                    if (GroupCreateActivity.this.allSpans.isEmpty()) {
                        GroupCreateActivity.this.editText.setHintVisible(true);
                    }
                }
            });
            this.currentAnimation.setDuration(150);
            this.removingSpan = groupCreateSpan;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleX", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleY", new float[]{1.0f, 0.01f}));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "alpha", new float[]{1.0f, 0.0f}));
            requestLayout();
        }
    }

    public class GroupCreateAdapter extends FastScrollAdapter {
        private ArrayList<User> contacts = new ArrayList();
        private Context context;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<User> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;
        private boolean searching;

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public GroupCreateAdapter(Context context) {
            this.context = context;
            ArrayList arrayList = ContactsController.getInstance(GroupCreateActivity.this.currentAccount).contacts;
            for (int i = 0; i < arrayList.size(); i++) {
                User user = MessagesController.getInstance(GroupCreateActivity.this.currentAccount).getUser(Integer.valueOf(((TL_contact) arrayList.get(i)).user_id));
                if (!(user == null || user.self || user.deleted)) {
                    this.contacts.add(user);
                }
            }
            this.searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate(GroupCreateActivity.this) {
                public /* synthetic */ SparseArray<User> getExcludeUsers() {
                    return -CC.$default$getExcludeUsers(this);
                }

                public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
                }

                public void onDataSetChanged() {
                    GroupCreateAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public void setSearching(boolean z) {
            if (this.searching != z) {
                this.searching = z;
                notifyDataSetChanged();
            }
        }

        public String getLetter(int i) {
            if (i < 0 || i >= this.contacts.size()) {
                return null;
            }
            User user = (User) this.contacts.get(i);
            if (user == null) {
                return null;
            }
            if (LocaleController.nameDisplayOrder == 1) {
                if (!TextUtils.isEmpty(user.first_name)) {
                    return user.first_name.substring(0, 1).toUpperCase();
                }
                if (!TextUtils.isEmpty(user.last_name)) {
                    return user.last_name.substring(0, 1).toUpperCase();
                }
            } else if (!TextUtils.isEmpty(user.last_name)) {
                return user.last_name.substring(0, 1).toUpperCase();
            } else {
                if (!TextUtils.isEmpty(user.first_name)) {
                    return user.first_name.substring(0, 1).toUpperCase();
                }
            }
            return "";
        }

        public int getItemCount() {
            if (!this.searching) {
                return this.contacts.size();
            }
            int size = this.searchResult.size();
            int size2 = this.searchAdapterHelper.getGlobalSearch().size();
            if (size2 != 0) {
                size += size2 + 1;
            }
            return size;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View groupCreateUserCell;
            if (i != 0) {
                groupCreateUserCell = new GroupCreateUserCell(this.context, true, 0);
            } else {
                groupCreateUserCell = new GroupCreateSectionCell(this.context);
            }
            return new Holder(groupCreateUserCell);
        }

        /* JADX WARNING: Removed duplicated region for block: B:44:0x00ed  */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x00ed  */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x00ed  */
        /* JADX WARNING: Missing block: B:21:0x0074, code skipped:
            if (r0.startsWith(r5.toString()) != false) goto L_0x0076;
     */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r9, int r10) {
            /*
            r8 = this;
            r0 = r9.getItemViewType();
            if (r0 == 0) goto L_0x00f2;
        L_0x0006:
            r9 = r9.itemView;
            r9 = (org.telegram.ui.Cells.GroupCreateUserCell) r9;
            r0 = r8.searching;
            r1 = 0;
            r2 = 1;
            if (r0 == 0) goto L_0x00d0;
        L_0x0010:
            r0 = r8.searchResult;
            r0 = r0.size();
            r3 = r8.searchAdapterHelper;
            r3 = r3.getGlobalSearch();
            r3 = r3.size();
            if (r10 < 0) goto L_0x002d;
        L_0x0022:
            if (r10 >= r0) goto L_0x002d;
        L_0x0024:
            r3 = r8.searchResult;
            r3 = r3.get(r10);
            r3 = (org.telegram.tgnet.TLRPC.User) r3;
            goto L_0x0043;
        L_0x002d:
            if (r10 <= r0) goto L_0x0042;
        L_0x002f:
            r3 = r3 + r0;
            if (r10 > r3) goto L_0x0042;
        L_0x0032:
            r3 = r8.searchAdapterHelper;
            r3 = r3.getGlobalSearch();
            r4 = r10 - r0;
            r4 = r4 - r2;
            r3 = r3.get(r4);
            r3 = (org.telegram.tgnet.TLRPC.User) r3;
            goto L_0x0043;
        L_0x0042:
            r3 = r1;
        L_0x0043:
            if (r3 == 0) goto L_0x00d9;
        L_0x0045:
            r4 = "@";
            if (r10 >= r0) goto L_0x007a;
        L_0x0049:
            r0 = r8.searchResultNames;
            r10 = r0.get(r10);
            r10 = (java.lang.CharSequence) r10;
            if (r10 == 0) goto L_0x00da;
        L_0x0053:
            r0 = r3.username;
            r0 = android.text.TextUtils.isEmpty(r0);
            if (r0 != 0) goto L_0x00da;
        L_0x005b:
            r0 = r10.toString();
            r5 = new java.lang.StringBuilder;
            r5.<init>();
            r5.append(r4);
            r4 = r3.username;
            r5.append(r4);
            r4 = r5.toString();
            r0 = r0.startsWith(r4);
            if (r0 == 0) goto L_0x00da;
        L_0x0076:
            r7 = r1;
            r1 = r10;
            r10 = r7;
            goto L_0x00da;
        L_0x007a:
            if (r10 <= r0) goto L_0x00d9;
        L_0x007c:
            r10 = r3.username;
            r10 = android.text.TextUtils.isEmpty(r10);
            if (r10 != 0) goto L_0x00d9;
        L_0x0084:
            r10 = r8.searchAdapterHelper;
            r10 = r10.getLastFoundUsername();
            r0 = r10.startsWith(r4);
            if (r0 == 0) goto L_0x0094;
        L_0x0090:
            r10 = r10.substring(r2);
        L_0x0094:
            r0 = new android.text.SpannableStringBuilder;	 Catch:{ Exception -> 0x00cd }
            r0.<init>();	 Catch:{ Exception -> 0x00cd }
            r0.append(r4);	 Catch:{ Exception -> 0x00cd }
            r4 = r3.username;	 Catch:{ Exception -> 0x00cd }
            r0.append(r4);	 Catch:{ Exception -> 0x00cd }
            r4 = r3.username;	 Catch:{ Exception -> 0x00cd }
            r4 = r4.toLowerCase();	 Catch:{ Exception -> 0x00cd }
            r4 = r4.indexOf(r10);	 Catch:{ Exception -> 0x00cd }
            r5 = -1;
            if (r4 == r5) goto L_0x00ca;
        L_0x00ae:
            r10 = r10.length();	 Catch:{ Exception -> 0x00cd }
            if (r4 != 0) goto L_0x00b7;
        L_0x00b4:
            r10 = r10 + 1;
            goto L_0x00b9;
        L_0x00b7:
            r4 = r4 + 1;
        L_0x00b9:
            r5 = new android.text.style.ForegroundColorSpan;	 Catch:{ Exception -> 0x00cd }
            r6 = "windowBackgroundWhiteBlueText4";
            r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);	 Catch:{ Exception -> 0x00cd }
            r5.<init>(r6);	 Catch:{ Exception -> 0x00cd }
            r10 = r10 + r4;
            r6 = 33;
            r0.setSpan(r5, r4, r10, r6);	 Catch:{ Exception -> 0x00cd }
        L_0x00ca:
            r10 = r1;
            r1 = r0;
            goto L_0x00da;
        L_0x00cd:
            r10 = r3.username;
            goto L_0x0076;
        L_0x00d0:
            r0 = r8.contacts;
            r10 = r0.get(r10);
            r3 = r10;
            r3 = (org.telegram.tgnet.TLRPC.User) r3;
        L_0x00d9:
            r10 = r1;
        L_0x00da:
            r9.setUser(r3, r10, r1);
            r10 = org.telegram.ui.GroupCreateActivity.this;
            r10 = r10.selectedContacts;
            r0 = r3.id;
            r10 = r10.indexOfKey(r0);
            r0 = 0;
            if (r10 < 0) goto L_0x00ed;
        L_0x00ec:
            goto L_0x00ee;
        L_0x00ed:
            r2 = 0;
        L_0x00ee:
            r9.setChecked(r2, r0);
            goto L_0x0106;
        L_0x00f2:
            r9 = r9.itemView;
            r9 = (org.telegram.ui.Cells.GroupCreateSectionCell) r9;
            r10 = r8.searching;
            if (r10 == 0) goto L_0x0106;
        L_0x00fa:
            r10 = NUM; // 0x7f0d047b float:1.8744441E38 double:1.053130344E-314;
            r0 = "GlobalSearch";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setText(r10);
        L_0x0106:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCreateActivity$GroupCreateAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public int getItemViewType(int i) {
            if (this.searching && i == this.searchResult.size()) {
                return 0;
            }
            return 1;
        }

        public int getPositionForScrollProgress(float f) {
            return (int) (((float) getItemCount()) * f);
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof GroupCreateUserCell) {
                ((GroupCreateUserCell) view).recycle();
            }
        }

        public void searchDialogs(final String str) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.queryServerSearch(null, true, false, false, false, 0, 0);
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {
                public void run() {
                    try {
                        GroupCreateAdapter.this.searchTimer.cancel();
                        GroupCreateAdapter.this.searchTimer = null;
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    AndroidUtilities.runOnUIThread(new -$$Lambda$GroupCreateActivity$GroupCreateAdapter$2$ST9GxCzXCSKWacsG9anovYkXh0A(this, str));
                }

                public /* synthetic */ void lambda$run$1$GroupCreateActivity$GroupCreateAdapter$2(String str) {
                    GroupCreateAdapter.this.searchAdapterHelper.queryServerSearch(str, true, false, true, false, 0, 0);
                    Utilities.searchQueue.postRunnable(new -$$Lambda$GroupCreateActivity$GroupCreateAdapter$2$0zSLqCotZ8GFr33wUzqa2QasDYY(this, str));
                }

                /* JADX WARNING: Removed duplicated region for block: B:44:0x0111 A:{LOOP_END, LOOP:1: B:23:0x0087->B:44:0x0111} */
                /* JADX WARNING: Removed duplicated region for block: B:51:0x00d6 A:{SYNTHETIC} */
                /* JADX WARNING: Missing block: B:32:0x00c3, code skipped:
            if (r10.contains(r15.toString()) != false) goto L_0x00d3;
     */
                public /* synthetic */ void lambda$null$0$GroupCreateActivity$GroupCreateAdapter$2(java.lang.String r17) {
                    /*
                    r16 = this;
                    r0 = r16;
                    r1 = r17.trim();
                    r1 = r1.toLowerCase();
                    r2 = r1.length();
                    if (r2 != 0) goto L_0x0020;
                L_0x0010:
                    r1 = org.telegram.ui.GroupCreateActivity.GroupCreateAdapter.this;
                    r2 = new java.util.ArrayList;
                    r2.<init>();
                    r3 = new java.util.ArrayList;
                    r3.<init>();
                    r1.updateSearchResults(r2, r3);
                    return;
                L_0x0020:
                    r2 = org.telegram.messenger.LocaleController.getInstance();
                    r2 = r2.getTranslitString(r1);
                    r3 = r1.equals(r2);
                    r4 = 0;
                    if (r3 != 0) goto L_0x0035;
                L_0x002f:
                    r3 = r2.length();
                    if (r3 != 0) goto L_0x0036;
                L_0x0035:
                    r2 = r4;
                L_0x0036:
                    r3 = 0;
                    r5 = 1;
                    if (r2 == 0) goto L_0x003c;
                L_0x003a:
                    r6 = 1;
                    goto L_0x003d;
                L_0x003c:
                    r6 = 0;
                L_0x003d:
                    r6 = r6 + r5;
                    r6 = new java.lang.String[r6];
                    r6[r3] = r1;
                    if (r2 == 0) goto L_0x0046;
                L_0x0044:
                    r6[r5] = r2;
                L_0x0046:
                    r1 = new java.util.ArrayList;
                    r1.<init>();
                    r2 = new java.util.ArrayList;
                    r2.<init>();
                    r7 = 0;
                L_0x0051:
                    r8 = org.telegram.ui.GroupCreateActivity.GroupCreateAdapter.this;
                    r8 = r8.contacts;
                    r8 = r8.size();
                    if (r7 >= r8) goto L_0x011b;
                L_0x005d:
                    r8 = org.telegram.ui.GroupCreateActivity.GroupCreateAdapter.this;
                    r8 = r8.contacts;
                    r8 = r8.get(r7);
                    r8 = (org.telegram.tgnet.TLRPC.User) r8;
                    r9 = r8.first_name;
                    r10 = r8.last_name;
                    r9 = org.telegram.messenger.ContactsController.formatName(r9, r10);
                    r9 = r9.toLowerCase();
                    r10 = org.telegram.messenger.LocaleController.getInstance();
                    r10 = r10.getTranslitString(r9);
                    r11 = r9.equals(r10);
                    if (r11 == 0) goto L_0x0084;
                L_0x0083:
                    r10 = r4;
                L_0x0084:
                    r11 = r6.length;
                    r12 = 0;
                    r13 = 0;
                L_0x0087:
                    if (r12 >= r11) goto L_0x0116;
                L_0x0089:
                    r14 = r6[r12];
                    r15 = r9.startsWith(r14);
                    if (r15 != 0) goto L_0x00d3;
                L_0x0091:
                    r15 = new java.lang.StringBuilder;
                    r15.<init>();
                    r3 = " ";
                    r15.append(r3);
                    r15.append(r14);
                    r15 = r15.toString();
                    r15 = r9.contains(r15);
                    if (r15 != 0) goto L_0x00d3;
                L_0x00a8:
                    if (r10 == 0) goto L_0x00c6;
                L_0x00aa:
                    r15 = r10.startsWith(r14);
                    if (r15 != 0) goto L_0x00d3;
                L_0x00b0:
                    r15 = new java.lang.StringBuilder;
                    r15.<init>();
                    r15.append(r3);
                    r15.append(r14);
                    r3 = r15.toString();
                    r3 = r10.contains(r3);
                    if (r3 == 0) goto L_0x00c6;
                L_0x00c5:
                    goto L_0x00d3;
                L_0x00c6:
                    r3 = r8.username;
                    if (r3 == 0) goto L_0x00d4;
                L_0x00ca:
                    r3 = r3.startsWith(r14);
                    if (r3 == 0) goto L_0x00d4;
                L_0x00d0:
                    r3 = 2;
                    r13 = 2;
                    goto L_0x00d4;
                L_0x00d3:
                    r13 = 1;
                L_0x00d4:
                    if (r13 == 0) goto L_0x0111;
                L_0x00d6:
                    if (r13 != r5) goto L_0x00e4;
                L_0x00d8:
                    r3 = r8.first_name;
                    r9 = r8.last_name;
                    r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r9, r14);
                    r2.add(r3);
                    goto L_0x010d;
                L_0x00e4:
                    r3 = new java.lang.StringBuilder;
                    r3.<init>();
                    r9 = "@";
                    r3.append(r9);
                    r10 = r8.username;
                    r3.append(r10);
                    r3 = r3.toString();
                    r10 = new java.lang.StringBuilder;
                    r10.<init>();
                    r10.append(r9);
                    r10.append(r14);
                    r9 = r10.toString();
                    r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r4, r9);
                    r2.add(r3);
                L_0x010d:
                    r1.add(r8);
                    goto L_0x0116;
                L_0x0111:
                    r12 = r12 + 1;
                    r3 = 0;
                    goto L_0x0087;
                L_0x0116:
                    r7 = r7 + 1;
                    r3 = 0;
                    goto L_0x0051;
                L_0x011b:
                    r3 = org.telegram.ui.GroupCreateActivity.GroupCreateAdapter.this;
                    r3.updateSearchResults(r1, r2);
                    return;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCreateActivity$GroupCreateAdapter$AnonymousClass2.lambda$null$0$GroupCreateActivity$GroupCreateAdapter$2(java.lang.String):void");
                }
            }, 200, 300);
        }

        private void updateSearchResults(ArrayList<User> arrayList, ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$GroupCreateActivity$GroupCreateAdapter$C0i_zDLg66DiKGKME02xepSowZQ(this, arrayList, arrayList2));
        }

        public /* synthetic */ void lambda$updateSearchResults$0$GroupCreateActivity$GroupCreateAdapter(ArrayList arrayList, ArrayList arrayList2) {
            this.searchResult = arrayList;
            this.searchResultNames = arrayList2;
            notifyDataSetChanged();
        }
    }

    public GroupCreateActivity(Bundle bundle) {
        super(bundle);
        this.chatType = bundle.getInt("chatType", 0);
        this.isAlwaysShare = bundle.getBoolean("isAlwaysShare", false);
        this.isNeverShare = bundle.getBoolean("isNeverShare", false);
        this.isGroup = bundle.getBoolean("isGroup", false);
        this.chatId = bundle.getInt("chatId");
        this.showFabButton = bundle.getBoolean("showFabButton", false);
        this.maxCount = this.chatType == 0 ? MessagesController.getInstance(this.currentAccount).maxMegagroupCount : MessagesController.getInstance(this.currentAccount).maxBroadcastCount;
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onClick(View view) {
        GroupCreateSpan groupCreateSpan = (GroupCreateSpan) view;
        if (groupCreateSpan.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(groupCreateSpan);
            updateHint();
            checkVisibleRows();
            return;
        }
        GroupCreateSpan groupCreateSpan2 = this.currentDeletingSpan;
        if (groupCreateSpan2 != null) {
            groupCreateSpan2.cancelDeleteAnimation();
        }
        this.currentDeletingSpan = groupCreateSpan;
        groupCreateSpan.startDeleteAnimation();
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        this.doneButtonVisible = this.chatType == 2;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        int i = this.chatType;
        if (i == 2) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAddMembers", NUM));
        } else if (this.isAlwaysShare) {
            if (this.isGroup) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", NUM));
            } else {
                this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", NUM));
            }
        } else if (!this.isNeverShare) {
            String str;
            ActionBar actionBar = this.actionBar;
            if (i == 0) {
                i = NUM;
                str = "NewGroup";
            } else {
                i = NUM;
                str = "NewBroadcastList";
            }
            actionBar.setTitle(LocaleController.getString(str, i));
        } else if (this.isGroup) {
            this.actionBar.setTitle(LocaleController.getString("NeverAllow", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    GroupCreateActivity.this.finishFragment();
                } else if (i == 1) {
                    GroupCreateActivity.this.onDonePressed();
                }
            }
        });
        this.fragmentView = new ViewGroup(context) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                int dp;
                i = MeasureSpec.getSize(i);
                i2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(i, i2);
                float f = 56.0f;
                if (AndroidUtilities.isTablet() || i2 > i) {
                    dp = AndroidUtilities.dp(144.0f);
                } else {
                    dp = AndroidUtilities.dp(56.0f);
                }
                GroupCreateActivity.this.scrollView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
                GroupCreateActivity.this.listView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2 - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                GroupCreateActivity.this.emptyView.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i2 - GroupCreateActivity.this.scrollView.getMeasuredHeight(), NUM));
                if (GroupCreateActivity.this.floatingButton != null) {
                    if (VERSION.SDK_INT < 21) {
                        f = 60.0f;
                    }
                    i = AndroidUtilities.dp(f);
                    GroupCreateActivity.this.floatingButton.measure(MeasureSpec.makeMeasureSpec(i, NUM), MeasureSpec.makeMeasureSpec(i, NUM));
                }
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                GroupCreateActivity.this.scrollView.layout(0, 0, GroupCreateActivity.this.scrollView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight());
                GroupCreateActivity.this.listView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.listView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.listView.getMeasuredHeight());
                GroupCreateActivity.this.emptyView.layout(0, GroupCreateActivity.this.scrollView.getMeasuredHeight(), GroupCreateActivity.this.emptyView.getMeasuredWidth(), GroupCreateActivity.this.scrollView.getMeasuredHeight() + GroupCreateActivity.this.emptyView.getMeasuredHeight());
                if (GroupCreateActivity.this.floatingButton != null) {
                    int dp = LocaleController.isRTL ? AndroidUtilities.dp(14.0f) : ((i3 - i) - AndroidUtilities.dp(14.0f)) - GroupCreateActivity.this.floatingButton.getMeasuredWidth();
                    i4 = ((i4 - i2) - AndroidUtilities.dp(14.0f)) - GroupCreateActivity.this.floatingButton.getMeasuredHeight();
                    GroupCreateActivity.this.floatingButton.layout(dp, i4, GroupCreateActivity.this.floatingButton.getMeasuredWidth() + dp, GroupCreateActivity.this.floatingButton.getMeasuredHeight() + i4);
                }
            }

            /* Access modifiers changed, original: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == GroupCreateActivity.this.listView || view == GroupCreateActivity.this.emptyView) {
                    GroupCreateActivity.this.parentLayout.drawHeaderShadow(canvas, GroupCreateActivity.this.scrollView.getMeasuredHeight());
                }
                return drawChild;
            }
        };
        ViewGroup viewGroup = (ViewGroup) this.fragmentView;
        this.scrollView = new ScrollView(context) {
            public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                if (GroupCreateActivity.this.ignoreScrollEvent) {
                    GroupCreateActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rect.offset(view.getLeft() - view.getScrollX(), view.getTop() - view.getScrollY());
                rect.top += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rect.bottom += GroupCreateActivity.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(view, rect, z);
            }
        };
        this.scrollView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("windowBackgroundWhite"));
        viewGroup.addView(this.scrollView);
        this.spansContainer = new SpansContainer(context);
        this.scrollView.addView(this.spansContainer, LayoutHelper.createFrame(-1, -2.0f));
        this.spansContainer.setOnClickListener(new -$$Lambda$GroupCreateActivity$eUWXrWcPgL8v-iB3AHPMNXDrIzs(this));
        this.editText = new EditTextBoldCursor(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (GroupCreateActivity.this.currentDeletingSpan != null) {
                    GroupCreateActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    GroupCreateActivity.this.currentDeletingSpan = null;
                }
                if (motionEvent.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintColor(Theme.getColor("groupcreate_hintText"));
        this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.editText.setCursorColor(Theme.getColor("groupcreate_cursor"));
        this.editText.setCursorWidth(1.5f);
        this.editText.setInputType(655536);
        this.editText.setSingleLine(true);
        this.editText.setBackgroundDrawable(null);
        this.editText.setVerticalScrollBarEnabled(false);
        this.editText.setHorizontalScrollBarEnabled(false);
        this.editText.setTextIsSelectable(false);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setImeOptions(NUM);
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.spansContainer.addView(this.editText);
        if (this.chatType == 2) {
            this.editText.setHintText(LocaleController.getString("AddMutual", NUM));
        } else if (this.isAlwaysShare) {
            if (this.isGroup) {
                this.editText.setHintText(LocaleController.getString("AlwaysAllowPlaceholder", NUM));
            } else {
                this.editText.setHintText(LocaleController.getString("AlwaysShareWithPlaceholder", NUM));
            }
        } else if (!this.isNeverShare) {
            this.editText.setHintText(LocaleController.getString("SendMessageTo", NUM));
        } else if (this.isGroup) {
            this.editText.setHintText(LocaleController.getString("NeverAllowPlaceholder", NUM));
        } else {
            this.editText.setHintText(LocaleController.getString("NeverShareWithPlaceholder", NUM));
        }
        this.editText.setCustomSelectionActionModeCallback(new Callback() {
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }
        });
        this.editText.setOnEditorActionListener(new -$$Lambda$GroupCreateActivity$gD5dQDpB0G04wuqUX611txNyFto(this));
        this.editText.setOnKeyListener(new OnKeyListener() {
            private boolean wasEmpty;

            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == 67) {
                    boolean z = true;
                    if (keyEvent.getAction() == 0) {
                        if (GroupCreateActivity.this.editText.length() != 0) {
                            z = false;
                        }
                        this.wasEmpty = z;
                    } else if (keyEvent.getAction() == 1 && this.wasEmpty && !GroupCreateActivity.this.allSpans.isEmpty()) {
                        GroupCreateActivity.this.spansContainer.removeSpan((GroupCreateSpan) GroupCreateActivity.this.allSpans.get(GroupCreateActivity.this.allSpans.size() - 1));
                        GroupCreateActivity.this.updateHint();
                        GroupCreateActivity.this.checkVisibleRows();
                        return true;
                    }
                }
                return false;
            }
        });
        this.editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                if (GroupCreateActivity.this.editText.length() != 0) {
                    GroupCreateActivity.this.searching = true;
                    GroupCreateActivity.this.searchWas = true;
                    GroupCreateActivity.this.adapter.setSearching(true);
                    GroupCreateActivity.this.itemDecoration.setSearching(true);
                    GroupCreateActivity.this.adapter.searchDialogs(GroupCreateActivity.this.editText.getText().toString());
                    GroupCreateActivity.this.listView.setFastScrollVisible(false);
                    GroupCreateActivity.this.listView.setVerticalScrollBarEnabled(true);
                    GroupCreateActivity.this.emptyView.setText(LocaleController.getString("NoResult", NUM));
                    return;
                }
                GroupCreateActivity.this.closeSearch();
            }
        });
        this.emptyView = new EmptyTextProgressView(context);
        if (ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
        viewGroup.addView(this.emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.listView = new RecyclerListView(context);
        this.listView.setFastScrollEnabled();
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView = this.listView;
        GroupCreateAdapter groupCreateAdapter = new GroupCreateAdapter(context);
        this.adapter = groupCreateAdapter;
        recyclerListView.setAdapter(groupCreateAdapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView2 = this.listView;
        GroupCreateDividerItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        this.itemDecoration = groupCreateDividerItemDecoration;
        recyclerListView2.addItemDecoration(groupCreateDividerItemDecoration);
        viewGroup.addView(this.listView);
        this.listView.setOnItemClickListener(new -$$Lambda$GroupCreateActivity$Z0-oiBdOv7dKnHHu2tWm78V07EE(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(GroupCreateActivity.this.editText);
                }
            }
        });
        if (this.showFabButton) {
            this.floatingButton = new ImageView(context);
            this.floatingButton.setScaleType(ScaleType.CENTER);
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
            if (VERSION.SDK_INT < 21) {
                Drawable mutate = context.getResources().getDrawable(NUM).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
                Drawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                createSimpleSelectorCircleDrawable = combinedDrawable;
            }
            this.floatingButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
            this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), Mode.MULTIPLY));
            BackDrawable backDrawable = new BackDrawable(false);
            backDrawable.setArrowRotation(180);
            this.floatingButton.setImageDrawable(backDrawable);
            if (VERSION.SDK_INT >= 21) {
                StateListAnimator stateListAnimator = new StateListAnimator();
                String str2 = "translationZ";
                stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, str2, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, str2, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.floatingButton.setStateListAnimator(stateListAnimator);
                this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            viewGroup.addView(this.floatingButton);
            this.floatingButton.setOnClickListener(new -$$Lambda$GroupCreateActivity$aI4t_wNzC9v5vuIA5pFlwNuyQ7Y(this));
            if (this.chatType != 2) {
                this.floatingButton.setVisibility(4);
                this.floatingButton.setScaleX(0.0f);
                this.floatingButton.setScaleY(0.0f);
                this.floatingButton.setAlpha(0.0f);
            }
            this.floatingButton.setContentDescription(LocaleController.getString("Next", NUM));
        } else {
            this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
            if (this.chatType != 2) {
                this.doneButton.setScaleX(0.0f);
                this.doneButton.setScaleY(0.0f);
                this.doneButton.setAlpha(0.0f);
            }
        }
        updateHint();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$GroupCreateActivity(View view) {
        this.editText.clearFocus();
        this.editText.requestFocus();
        AndroidUtilities.showKeyboard(this.editText);
    }

    public /* synthetic */ boolean lambda$createView$1$GroupCreateActivity(TextView textView, int i, KeyEvent keyEvent) {
        return i == 6 && onDonePressed();
    }

    public /* synthetic */ void lambda$createView$2$GroupCreateActivity(View view, int i) {
        if (view instanceof GroupCreateUserCell) {
            GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) view;
            User user = groupCreateUserCell.getUser();
            if (user != null) {
                int i2 = this.selectedContacts.indexOfKey(user.id) >= 0 ? 1 : 0;
                if (i2 != 0) {
                    this.spansContainer.removeSpan((GroupCreateSpan) this.selectedContacts.get(user.id));
                } else if (this.maxCount != 0 && this.selectedContacts.size() == this.maxCount) {
                    return;
                } else {
                    if (this.chatType == 0 && this.selectedContacts.size() == MessagesController.getInstance(this.currentAccount).maxGroupCount) {
                        Builder builder = new Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setMessage(LocaleController.getString("SoftUserLimitAlert", NUM));
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                        showDialog(builder.create());
                        return;
                    }
                    MessagesController.getInstance(this.currentAccount).putUser(user, this.searching ^ 1);
                    GroupCreateSpan groupCreateSpan = new GroupCreateSpan(this.editText.getContext(), user);
                    this.spansContainer.addSpan(groupCreateSpan);
                    groupCreateSpan.setOnClickListener(this);
                }
                updateHint();
                if (this.searching || this.searchWas) {
                    AndroidUtilities.showKeyboard(this.editText);
                } else {
                    groupCreateUserCell.setChecked(i2 ^ 1, true);
                }
                if (this.editText.length() > 0) {
                    this.editText.setText(null);
                }
            }
        }
    }

    public /* synthetic */ void lambda$createView$3$GroupCreateActivity(View view) {
        onDonePressed();
    }

    public void onResume() {
        super.onResume();
        EditTextBoldCursor editTextBoldCursor = this.editText;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.contactsDidLoad) {
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showTextView();
            }
            GroupCreateAdapter groupCreateAdapter = this.adapter;
            if (groupCreateAdapter != null) {
                groupCreateAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            if (this.listView != null) {
                i = 0;
                i2 = ((Integer) objArr[0]).intValue();
                int childCount = this.listView.getChildCount();
                if ((i2 & 2) != 0 || (i2 & 1) != 0 || (i2 & 4) != 0) {
                    while (i < childCount) {
                        View childAt = this.listView.getChildAt(i);
                        if (childAt instanceof GroupCreateUserCell) {
                            ((GroupCreateUserCell) childAt).update(i2);
                        }
                        i++;
                    }
                }
            }
        } else if (i == NotificationCenter.chatDidCreated) {
            removeSelfFromStack();
        }
    }

    @Keep
    public void setContainerHeight(int i) {
        this.containerHeight = i;
        SpansContainer spansContainer = this.spansContainer;
        if (spansContainer != null) {
            spansContainer.requestLayout();
        }
    }

    public int getContainerHeight() {
        return this.containerHeight;
    }

    private void checkVisibleRows() {
        int childCount = this.listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof GroupCreateUserCell) {
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) childAt;
                User user = groupCreateUserCell.getUser();
                if (user != null) {
                    groupCreateUserCell.setChecked(this.selectedContacts.indexOfKey(user.id) >= 0, true);
                }
            }
        }
    }

    private boolean onDonePressed() {
        int i = 0;
        ArrayList arrayList;
        if (this.chatType == 2) {
            arrayList = new ArrayList();
            for (int i2 = 0; i2 < this.selectedContacts.size(); i2++) {
                InputUser inputUser = MessagesController.getInstance(this.currentAccount).getInputUser(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedContacts.keyAt(i2))));
                if (inputUser != null) {
                    arrayList.add(inputUser);
                }
            }
            MessagesController.getInstance(this.currentAccount).addUsersToChannel(this.chatId, arrayList, null);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            Bundle bundle = new Bundle();
            bundle.putInt("chat_id", this.chatId);
            presentFragment(new ChatActivity(bundle), true);
        } else if (!this.doneButtonVisible || this.selectedContacts.size() == 0) {
            return false;
        } else {
            arrayList = new ArrayList();
            while (i < this.selectedContacts.size()) {
                arrayList.add(Integer.valueOf(this.selectedContacts.keyAt(i)));
                i++;
            }
            if (this.isAlwaysShare || this.isNeverShare) {
                GroupCreateActivityDelegate groupCreateActivityDelegate = this.delegate;
                if (groupCreateActivityDelegate != null) {
                    groupCreateActivityDelegate.didSelectUsers(arrayList);
                }
                finishFragment();
            } else {
                Bundle bundle2 = new Bundle();
                bundle2.putIntegerArrayList("result", arrayList);
                bundle2.putInt("chatType", this.chatType);
                presentFragment(new GroupCreateFinalActivity(bundle2));
            }
        }
        return true;
    }

    private void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.itemDecoration.setSearching(false);
        this.adapter.setSearching(false);
        this.adapter.searchDialogs(null);
        this.listView.setFastScrollVisible(true);
        this.listView.setVerticalScrollBarEnabled(false);
        this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
    }

    private void updateHint() {
        if (!(this.isAlwaysShare || this.isNeverShare)) {
            String str = "Members";
            if (this.chatType == 2) {
                this.actionBar.setSubtitle(LocaleController.formatPluralString(str, this.selectedContacts.size()));
            } else if (this.selectedContacts.size() == 0) {
                this.actionBar.setSubtitle(LocaleController.formatString("MembersCountZero", NUM, LocaleController.formatPluralString(str, this.maxCount)));
            } else {
                this.actionBar.setSubtitle(LocaleController.formatString("MembersCount", NUM, Integer.valueOf(this.selectedContacts.size()), Integer.valueOf(this.maxCount)));
            }
        }
        if (this.chatType != 2) {
            String str2 = "alpha";
            String str3 = "scaleY";
            String str4 = "scaleX";
            AnimatorSet animatorSet;
            View view;
            AnimatorSet animatorSet2;
            Animator[] animatorArr;
            ImageView imageView;
            if (this.doneButtonVisible && this.allSpans.isEmpty()) {
                animatorSet = this.currentDoneButtonAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                this.currentDoneButtonAnimation = new AnimatorSet();
                view = this.doneButton;
                if (view != null) {
                    animatorSet2 = this.currentDoneButtonAnimation;
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofFloat(view, str4, new float[]{0.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.doneButton, str3, new float[]{0.0f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.doneButton, str2, new float[]{0.0f});
                    animatorSet2.playTogether(animatorArr);
                } else {
                    imageView = this.floatingButton;
                    if (imageView != null) {
                        animatorSet2 = this.currentDoneButtonAnimation;
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(imageView, str4, new float[]{0.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.floatingButton, str3, new float[]{0.0f});
                        animatorArr[2] = ObjectAnimator.ofFloat(this.floatingButton, str2, new float[]{0.0f});
                        animatorSet2.playTogether(animatorArr);
                        this.currentDoneButtonAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                GroupCreateActivity.this.floatingButton.setVisibility(4);
                            }
                        });
                    }
                }
                this.currentDoneButtonAnimation.setDuration(180);
                this.currentDoneButtonAnimation.start();
                this.doneButtonVisible = false;
            } else if (!this.doneButtonVisible && !this.allSpans.isEmpty()) {
                animatorSet = this.currentDoneButtonAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                this.currentDoneButtonAnimation = new AnimatorSet();
                view = this.doneButton;
                if (view != null) {
                    animatorSet2 = this.currentDoneButtonAnimation;
                    animatorArr = new Animator[3];
                    animatorArr[0] = ObjectAnimator.ofFloat(view, str4, new float[]{1.0f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.doneButton, str3, new float[]{1.0f});
                    animatorArr[2] = ObjectAnimator.ofFloat(this.doneButton, str2, new float[]{1.0f});
                    animatorSet2.playTogether(animatorArr);
                } else {
                    imageView = this.floatingButton;
                    if (imageView != null) {
                        imageView.setVisibility(0);
                        animatorSet = this.currentDoneButtonAnimation;
                        animatorArr = new Animator[3];
                        animatorArr[0] = ObjectAnimator.ofFloat(this.floatingButton, str4, new float[]{1.0f});
                        animatorArr[1] = ObjectAnimator.ofFloat(this.floatingButton, str3, new float[]{1.0f});
                        animatorArr[2] = ObjectAnimator.ofFloat(this.floatingButton, str2, new float[]{1.0f});
                        animatorSet.playTogether(animatorArr);
                    }
                }
                this.currentDoneButtonAnimation.setDuration(180);
                this.currentDoneButtonAnimation.start();
                this.doneButtonVisible = true;
            }
        }
    }

    public void setDelegate(GroupCreateActivityDelegate groupCreateActivityDelegate) {
        this.delegate = groupCreateActivityDelegate;
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$GroupCreateActivity$dtR367GWfcBqvsix6KxTjm2i-4Y -__lambda_groupcreateactivity_dtr367gwfcbqvsix6kxtjm2i-4y = new -$$Lambda$GroupCreateActivity$dtR367GWfcBqvsix6KxTjm2i-4Y(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[38];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[6] = new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollActive");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollInactive");
        themeDescriptionArr[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollText");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[12] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        themeDescriptionArr[13] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        themeDescriptionArr[14] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[15] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "groupcreate_hintText");
        themeDescriptionArr[16] = new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "groupcreate_cursor");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, null, null, null, "graySection");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, null, null, null, "groupcreate_sectionShadow");
        View view = this.listView;
        int i = ThemeDescription.FLAG_TEXTCOLOR;
        Class[] clsArr = new Class[]{GroupCreateSectionCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[19] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "groupcreate_sectionText");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, null, null, null, "groupcreate_sectionText");
        view = this.listView;
        i = ThemeDescription.FLAG_TEXTCOLOR;
        clsArr = new Class[]{GroupCreateUserCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        themeDescriptionArr[21] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "groupcreate_checkbox");
        themeDescriptionArr[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, null, null, null, "groupcreate_checkboxCheck");
        view = this.listView;
        i = ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{GroupCreateUserCell.class};
        strArr = new String[1];
        strArr[0] = "statusTextView";
        themeDescriptionArr[23] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueText");
        themeDescriptionArr[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, null, null, null, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$GroupCreateActivity$dtR367GWfcBqvsix6KxTjm2i-4Y -__lambda_groupcreateactivity_dtr367gwfcbqvsix6kxtjm2i-4y2 = -__lambda_groupcreateactivity_dtr367gwfcbqvsix6kxtjm2i-4y;
        themeDescriptionArr[26] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreateactivity_dtr367gwfcbqvsix6kxtjm2i-4y2, "avatar_backgroundRed");
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreateactivity_dtr367gwfcbqvsix6kxtjm2i-4y2, "avatar_backgroundOrange");
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreateactivity_dtr367gwfcbqvsix6kxtjm2i-4y2, "avatar_backgroundViolet");
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreateactivity_dtr367gwfcbqvsix6kxtjm2i-4y2, "avatar_backgroundGreen");
        themeDescriptionArr[30] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreateactivity_dtr367gwfcbqvsix6kxtjm2i-4y2, "avatar_backgroundCyan");
        themeDescriptionArr[31] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreateactivity_dtr367gwfcbqvsix6kxtjm2i-4y2, "avatar_backgroundBlue");
        themeDescriptionArr[32] = new ThemeDescription(null, 0, null, null, null, -__lambda_groupcreateactivity_dtr367gwfcbqvsix6kxtjm2i-4y2, "avatar_backgroundPink");
        themeDescriptionArr[33] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "avatar_backgroundGroupCreateSpanBlue");
        themeDescriptionArr[34] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "groupcreate_spanBackground");
        themeDescriptionArr[35] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "groupcreate_spanText");
        themeDescriptionArr[36] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "groupcreate_spanDelete");
        themeDescriptionArr[37] = new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, "avatar_backgroundBlue");
        return themeDescriptionArr;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$4$GroupCreateActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) childAt).update(0);
                }
            }
        }
    }
}
