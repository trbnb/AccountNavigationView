package de.trbnb.accountnavigationview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import de.trbnb.library.AccountNavigationView;


public class MainActivity extends AppCompatActivity {

    private AccountNavigationView accountNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        accountNavigationView = (AccountNavigationView) findViewById(R.id.nav);
        accountNavigationView.setHeaderText(R.string.not_logged_in);

        accountNavigationView.addNavigationGroupId(R.id.navigation_primary);
        accountNavigationView.addNavigationItemId(R.id.navigation_secondary);
        accountNavigationView.addAccountGroupId(R.id.accounts_primary);
        accountNavigationView.addAccountItemId(R.id.accounts_secondary);

        AccountNavigationView.ItemClickHelper helper = new AccountNavigationView.ItemClickHelper(
                accountNavigationView,
                R.id.navigation_primary,
                R.id.nav_first,
                R.id.accounts_primary,
                R.id.acc_first
        );

    }
}
