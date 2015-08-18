package de.trbnb.library;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * NavigationView that is build to be used with multiple accounts.
 */
public class AccountNavigationView extends NavigationView {

    public static final int ACCOUNT_MENU_ANIMATION_DURATION = 400;

    private FrameLayout header;
    private ImageView headerImage;
    private TextView headerText;
    private ImageView headerArrow;

    private boolean accountsAreShown = false;

    private List<Integer> navigationItemIds;
    private List<Integer> navigationGroupIds;

    private List<Integer> accountItemIds;
    private List<Integer> accountGroupIds;

    private DisplayListener listener;

    public AccountNavigationView(Context context) {
        super(context);
        init();

        setStandardHeaderBackground();
    }

    public AccountNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

        TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.AccountNavigationView, 0, 0);
        initAttrs(array);
    }

    public AccountNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

        TypedArray array = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.AccountNavigationView, defStyleAttr, 0);
        initAttrs(array);
    }

    private void init(){
        navigationItemIds = new ArrayList<>();
        navigationGroupIds = new ArrayList<>();

        accountItemIds = new ArrayList<>();
        accountGroupIds = new ArrayList<>();

        inflateHeaderView(R.layout.nav_header);

        header = (FrameLayout) findViewById(R.id.nav_header);
        headerImage = (ImageView) findViewById(R.id.nav_header_img);
        headerText = (TextView) findViewById(R.id.nav_header_text);
        headerArrow = (ImageView) findViewById(R.id.nav_header_arrow);

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                headerArrow.animate()
                        .setDuration(ACCOUNT_MENU_ANIMATION_DURATION)
                        .rotation(!accountsAreShown ? 180 : 0)
                        .start();

                if (!accountsAreShown) {
                    showAccountsAnimated();
                } else {
                    showNavigationMenuAnimated();
                }
            }
        });

        post(new Runnable() {
            @Override
            public void run() {
                showNavigationMenu();
            }
        });
    }

    /**
     * Just reading the attributes from the xml here.
     */
    private void initAttrs(TypedArray arr){
        Drawable headerBackground = arr.getDrawable(R.styleable.AccountNavigationView_headerBackground);

        if(headerBackground == null){
            setStandardHeaderBackground();
        } else {
            headerImage.setImageDrawable(headerBackground);
        }

        arr.recycle();
    }

    /**
     * Sets the text that will be shown in the header.
     * @param stringRes string resource of the text
     */
    public void setHeaderText(@StringRes int stringRes){
        headerText.setText(stringRes);
    }

    /**
     * Sets the text that will be shown in the header.
     * @param text the to be shown text
     */
    public void setHeaderText(String text){
        headerText.setText(text);
    }

    public void addNavigationItemId(@IdRes int id){
        navigationItemIds.add(id);
    }

    public void addNavigationGroupId(@IdRes int id){
        navigationGroupIds.add(id);
    }

    public void addAccountItemId(@IdRes int id){
        accountItemIds.add(id);
    }

    public void addAccountGroupId(@IdRes int id){
        accountGroupIds.add(id);
    }

    public void setDisplayListener(@Nullable DisplayListener listener) {
        this.listener = listener;
    }

    /**
     * Sets the standard background for the header if none was given as the same as the Wallpaper.
     *
     * In edit mode this will just be black.
     */
    private void setStandardHeaderBackground(){
        if(isInEditMode()){
            headerImage.setImageDrawable(new ColorDrawable(Color.BLACK));
        } else {
            headerImage.setImageDrawable(WallpaperManager.getInstance(getContext()).getFastDrawable());
        }
    }

    public void setHeaderBackground(Drawable drawable){
        if(drawable == null){
            setStandardHeaderBackground();
        } else {
            headerImage.setImageDrawable(drawable);
        }
    }

    /**
     * Animates the all the views in the view below the header.
     * @param callback A callback that will be fired when the animation has finished.
     * @param show If true the views will be fade-in. Otherwise they will fade-out.
     */
    @SuppressWarnings("SimplifiableConditionalExpression")
    private void animateMenuItemsVisibility(final Callback callback, boolean show){
        final List<View> views = new ArrayList<>();
        ViewGroup menuView = (ViewGroup) getChildAt(0);

        for(int i = 1; i < menuView.getChildCount(); i++){
            View item = menuView.getChildAt(i);
            views.add(item);
        }

        ValueAnimator animator = ValueAnimator.ofFloat(show ? 0 : 1, show ? 1 : 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                for (int i = 0; i < views.size(); i++) {
                    views.get(i).setAlpha((Float) animation.getAnimatedValue());
                }
            }
        });
        animator.setDuration(ACCOUNT_MENU_ANIMATION_DURATION / 2);
        animator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (callback != null) {
                    callback.onFinished();
                }
            }
        });
        animator.start();
    }

    /**
     * Sets the visibilty of the navigation and account menu groups.
     * @param showAccounts If true the account menu groups will be set as visible.
     *                     Otherwise the navigation menu groups will be set as visible.
     */
    private void makeAccountListVisible(boolean showAccounts){
        accountsAreShown = showAccounts;

        makeGroupListVisible(navigationGroupIds, !showAccounts);
        makeItemListVisible(navigationItemIds, !showAccounts);
        makeGroupListVisible(accountGroupIds, showAccounts);
        makeItemListVisible(accountItemIds, showAccounts);
    }

    /**
     * Makes all items with the given ids visible or invisible.
     * @param ids list of items ids
     * @param visible If true the items will be made visible, otherwise invisible.
     */
    private void makeItemListVisible(List<Integer> ids, boolean visible){
        for (int i = 0; i < ids.size(); i++) {
            Integer id = ids.get(i);
            getMenu().findItem(id).setVisible(visible);
        }
    }

    /**
     * Makes all groups with the given ids visible or invisible.
     * @param ids list of group ids
     * @param visible If true the groups will be made visible, otherwise invisible.
     */
    private void makeGroupListVisible(List<Integer> ids, boolean visible){
        for (int i = 0; i < ids.size(); i++) {
            Integer id = ids.get(i);
            getMenu().setGroupVisible(id, visible);
        }
    }

    /**
     * Show the navigation menu groups without an animation.
     */
    public void showNavigationMenu(){
        makeAccountListVisible(false);

        if(listener != null){
            listener.onNavigationMenuShown();
        }
    }

    /**
     * Show the account menu groups without an animation.
     */
    public void showAccounts(){
         makeAccountListVisible(true);

        if(listener != null){
            listener.onAccountsShown();
        }
    }

    /**
     * Show the navigation menu groups with an animation.
     */
    public void showNavigationMenuAnimated(){
        animateMenuItemsVisibility(new Callback() {
            @Override
            public void onFinished() {
                if(listener != null){
                    listener.onNavigationMenuShown();
                }

                makeAccountListVisible(false);
                animateMenuItemsVisibility(null, true);
            }
        }, false);
    }

    /**
     * Show the account menu groups with an animation.
     */
    public void showAccountsAnimated(){
        animateMenuItemsVisibility(new Callback() {
            @Override
            public void onFinished() {
                if(listener != null){
                    listener.onAccountsShown();
                }

                showAccounts();
                animateMenuItemsVisibility(null, true);
            }
        }, false);
    }

    /**
     * converts dp into pixels
     */
    private float dpToPx(int dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getContext().getResources().getDisplayMetrics()
        );
    }

    /**
     * Callback for the menu item animation
     */
    private interface Callback{
        /**
         * called when the animation is finished
         */
        void onFinished();
    }

    public interface DisplayListener {
        void onNavigationMenuShown();
        void onAccountsShown();
    }

    public static class ItemClickHelper implements OnNavigationItemSelectedListener, DisplayListener {

        private AccountNavigationView view;

        private int navigationGroup;
        private int accountGroup;

        private MenuItem selectedNavigationItem;
        private MenuItem selectedAccountItem;

        private OnNavigationItemSelectedListener navigationItemSelectedListener;
        private DisplayListener displayListener;

        public ItemClickHelper(AccountNavigationView view,
                               @IdRes int navigationGroup, @IdRes int firstNavigationSelection,
                               @IdRes int accountGroup,    @IdRes int firstAccountSelection){
            this.view = view;

            this.navigationGroup = navigationGroup;
            this.accountGroup = accountGroup;

            this.selectedNavigationItem = view.getMenu().findItem(firstNavigationSelection);
            this.selectedAccountItem = view.getMenu().findItem(firstAccountSelection);

            view.setNavigationItemSelectedListener(this);
            view.setDisplayListener(this);
        }

        public void setNavigationItemSelectedListener(OnNavigationItemSelectedListener externalListener) {
            this.navigationItemSelectedListener = externalListener;
        }

        public void setDisplayListener(DisplayListener displayListener) {
            this.displayListener = displayListener;
        }

        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            if(menuItem.getGroupId() == navigationGroup){
                selectedNavigationItem = menuItem;
            } else if(menuItem.getGroupId() == accountGroup){
                selectedAccountItem = menuItem;
            }

            if(navigationItemSelectedListener != null){
                navigationItemSelectedListener.onNavigationItemSelected(menuItem);
            }

            return true;
        }

        @Override
        public void onNavigationMenuShown() {
            selectedNavigationItem.setChecked(true);
        }

        @Override
        public void onAccountsShown() {
            selectedAccountItem.setChecked(true);
        }
    }
}
