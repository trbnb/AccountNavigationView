package de.trbnb.accountnavigationview;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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

        accountNavigationView.addNavigationItemId(R.id.navigation_secondary);
        accountNavigationView.addAccountItemId(R.id.accounts_secondary);

        accountNavigationView.addAccount("Jeff", 0);
        MenuItem item = accountNavigationView.addAccount("Mathias", 0);
        accountNavigationView.addAccount("Alexander", 0);

        accountNavigationView.selectItem(item);
        accountNavigationView.setHeaderProfilePicture(new ColorDrawable(Color.YELLOW));
    }
}
