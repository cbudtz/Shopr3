package com.nexb.shopr3;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseListAdapter;
import com.nexb.shopr3.dataModel.InstantAutoCompleteTextView;
import com.nexb.shopr3.dataModel.ListItem;
import com.nexb.shopr3.dataModel.ShopList;
import com.nexb.shopr3.dataModel.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int FirebaseId = 0;
    //DB fields
    private Firebase fireBaseRoot;

    private String activeList = "Shoplist1";
    private String testList = "Shoplist2";
    private Firebase fireBaseActiveList;

    public static final String USERS = "Users";
    private Firebase fireBaseUserList;

    private Firebase firebaseUser;

        //UI elements
    private Toolbar toolbar;
    private EditText searchField;
    private AutoCompleteTextView autoBox;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private NavigationView navigationView;

    private ListView mainActivityListView;
    private FirebaseListAdapter<ListItem> mainListAdapter;


    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Setup Toolbar:
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Setup Search Field (EditText)

        //Setup AutoBox
        autoBox = (InstantAutoCompleteTextView) findViewById(R.id.mainAutoCompleteBox);
        final ArrayAdapter<String> autoAdaptor = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, new String[]{"Bananer","Ananas","Citroner","Pærer","Æg"});
        autoBox.setAdapter(autoAdaptor);
        autoBox.setHint("Write new item here");
        autoBox.setVisibility(View.INVISIBLE);
        autoBox.setThreshold(0);
        autoBox.setDropDownBackgroundDrawable(getResources().getDrawable(android.R.drawable.alert_light_frame));
        //Setup actionbutton
        setupFloatingActionButton();
        //Setup layout
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //setup Navigation view
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //setup ListView
        mainActivityListView = (ListView) findViewById(R.id.content_main_listView);
        //TODO: Replace layout with custom layout and subclass ArrayAdaptor (peek at/Steal from androidelementer)

        //TODO: Extract Firebase functionality!
        //setup Firebase connection:
        Firebase.setAndroidContext(this);
        fireBaseRoot= new Firebase("https://shop-r.firebaseio.com/");
        fireBaseUserList = fireBaseRoot.child(USERS);
        //Find user in android accounts
        //resolve User
        user = new User();
        AccountManager manager = (AccountManager) this.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] list = manager.getAccountsByType("com.google");
        if (list!=null && list.length>0 && list[0]!=null) {
            String id = list[0].name;
            id = id.replace('.','_');
            System.out.println(id);
            user.setUserID(id);
        }
        else {

            user.setUserID("" + Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));

        }
        System.out.println(user.getUserID());
        //ShopList testShopList = new ShopList();
        //Firebase newlistRef = fireBaseRoot.push();
        //testShopList.setCreatedByID(user.getUserID());
        firebaseUser = fireBaseUserList.child(user.getUserID());
       // newlistRef.setValue(testShopList);

        firebaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User foreignUser = dataSnapshot.getValue(User.class);
                if (foreignUser==null ||foreignUser.getUserName()==null) {
                    firebaseUser.setValue(user);
                    System.out.println("User created");
                } else {
                    user = foreignUser;
                    System.out.println("User already Exists");

                }

                //Test syso's
                System.out.println("DataChanged");
                System.out.println("Username: " + user.getUserName() + " userID: " + user.getUserID() + "\nOwnLists: " + user.getOwnLists() + " foreignUsers: " + user.getForeignLists());
                navigationView.getMenu().removeGroup(1);
                int i = 0;
                for (String s : user.getOwnLists()) {
                    navigationView.getMenu().add(1, i, i, s);
                    i++;
                }
                if(user.getUserID()!=null)((TextView)findViewById(R.id.userMail)).setText(user.getUserID());
                if(user.getUserName()!=null)((TextView)findViewById(R.id.userName)).setText(user.getUserName());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        autoBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Firebase newItem = fireBaseActiveList.push();
                        newItem.setValue(new ListItem(newItem.getKey(),1, " ", (parent.getItemAtPosition(position)).toString()));
                autoBox.setText("");
                autoBox.showDropDown();
            }
        });
        autoBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Firebase newItem;
                if (event.getAction()==KeyEvent.ACTION_DOWN){
                    newItem = fireBaseActiveList.push();
                    newItem.setValue(new ListItem(newItem.getKey(),1, " ", v.getText().toString()));
                }
                System.out.println(v.getText());
                v.setText("");
                return true;
            }
        });


        setActiveList(user.getActiveList());

    }

    private void setActiveList(String listID){
        //Keep reference to old adaptor
        FirebaseListAdapter<ListItem> oldAdaptor = mainListAdapter;
        //resolve FB ref
        fireBaseActiveList = fireBaseRoot.child(listID);
        Query orderedActiveList = fireBaseActiveList.orderByValue();
        mainListAdapter = new ShopListFirebaseAdapter(orderedActiveList);
        mainActivityListView.setAdapter(mainListAdapter);
        if (oldAdaptor!=null)oldAdaptor.cleanup();//Cleanup Adaptor!
    }


    private void setupFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.openTextBox ){
            if(autoBox.getVisibility() == View.INVISIBLE){
                autoBox.setVisibility(View.VISIBLE);
            }
            else{
                autoBox.setVisibility(View.INVISIBLE);
            }
        }
        else if(id == R.id.mode){
            autoBox.setVisibility(View.INVISIBLE);

        }
        return super.onOptionsItemSelected(item);
    }

    //Navigation drawer clicks
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        System.out.println("buttonPressed: " + id);

        if (id == R.id.nav_newList) {
            //make a new list
        } else if (id == R.id.nav_share) {
            setActiveList(activeList);
        } else {
            setActiveList(user.getOwnLists().get(id));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class ShopListFirebaseAdapter extends FirebaseListAdapter<ListItem> {
        public ShopListFirebaseAdapter(Query orderedActiveList) {
            super(MainActivity.this, ListItem.class, R.layout.list_item, orderedActiveList);
        }

        @Override
        protected void populateView(final View view, final ListItem s) {
            ((TextView)view.findViewById(R.id.itemName)).setText(s.getName());
            ((TextView)view.findViewById(R.id.itemAmount)).setText(String.valueOf(s.getAmount()));
            ((TextView)view.findViewById(R.id.itemType)).setText(s.getUnit());
            view.setTag(s.getItemID());

            // on click methods for the views items
            // DELETE
            view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //view.setVisibility(View.INVISIBLE);
                    System.out.println(view.getTag());
                    fireBaseActiveList.child(s.getItemID()).removeValue();
                }
            });
            // MINUS ONE BUTTON
            view.findViewById(R.id.minusOne).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    double oldvalue = s.getAmount();
                    if(oldvalue > 1) {
                        s.setAmount(oldvalue - 1);
                        // push new value to firebase here
                        fireBaseActiveList.child(s.getItemID()).setValue(s);
                    }
                }
            });
            // PLUS ONE BUTTON
            view.findViewById(R.id.plusOne).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double oldvalue = s.getAmount();
                    s.setAmount(oldvalue + 1);
                    // push new value to firebase here
                    fireBaseActiveList.child(s.getItemID()).setValue(s);

                }
            });
            // item type edit text
            view.findViewById(R.id.itemType).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final InstantAutoCompleteTextView typeBox = (InstantAutoCompleteTextView) findViewById(R.id.itemType);
                    final ArrayAdapter<String> adaptorForItemType = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, new String[]{"Stk", "Kilo", "Gram", "Liter", "Gallon",});
                    typeBox.setAdapter(adaptorForItemType);
                    Toast.makeText(MainActivity.this,
                            "Her skal laves en listener der ændre type på vare ved action enter",
                            Toast.LENGTH_LONG).show();
                }
            });
            // item amount edit text
            view.findViewById(R.id.itemAmount).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this,
                            "Her skal laves en key listener der ændre antal på vare ved action enter",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
