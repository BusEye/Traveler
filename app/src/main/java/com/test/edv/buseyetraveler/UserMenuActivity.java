package com.test.edv.buseyetraveler;

import android.app.Dialog;
//import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class UserMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    public String id;
    TravelerMapFragment gmapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (googleServiceAvailable()) {

            setContentView(R.layout.activity_user_menu);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

          //  fragmentManager = getFragmentManager();
            //fragmentManager.beginTransaction().replace(R.id.mainLayout,setValue()).commit();

            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.mainLayout, setValue());
            tx.commit();
        }
        else {

        }
    }

    public boolean googleServiceAvailable() {

        Log.d(TAG, "GoogleServices: Checking services");
        int avalability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(UserMenuActivity.this);

        if (avalability == ConnectionResult.SUCCESS) {
            //all is fine
            Log.d(TAG, "GoogleServices: All is fine");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(avalability)) {
            //error that can resolve
            Log.d(TAG, "GoogleServices: Erroe can solve");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(UserMenuActivity.this, avalability, ERROR_DIALOG_REQUEST);
            dialog.show();

        } else {
            Toast.makeText(this, "You cant Make map request", Toast.LENGTH_LONG).show();
        }
        return false;

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        fragmentManager = getFragmentManager();

        int id = item.getItemId();

        if (id == R.id.nav_map) {
         //   int commit= fragmentManager.beginTransaction().replace(R.id.mainLayout, setValue()).commit();

            TravelerMapFragment gmapFragment =new TravelerMapFragment();

            FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            tx.replace(R.id.mainLayout, setValue());
            tx.commit();

        } else if (id == R.id.nav_tickets) {
              PaymentFragment paymentFragment = new PaymentFragment();
              FragmentTransaction pyment = getSupportFragmentManager().beginTransaction();
              pyment.replace(R.id.mainLayout,paymentFragment);
              pyment.commit();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Fragment setValue() {

        Bundle bundle = new Bundle();
        bundle.putBoolean("value", false);
        bundle.putString("Id", id);


        gmapFragment = new TravelerMapFragment();
        gmapFragment.setArguments(bundle);


        return gmapFragment;
    }
}
