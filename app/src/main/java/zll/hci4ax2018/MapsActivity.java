package zll.hci4ax2018;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;

    //User's location
    final LatLng self = new LatLng(1.441284, 103.786199);

    //Dining locations at RP
    final LatLng dining1 = new LatLng(1.443828, 103.785277);
    final LatLng dining2 = new LatLng(1.444590, 103.784799);
    final LatLng dining3 = new LatLng(1.445317, 103.784728);

    //Dining locations at Woodlands
    final LatLng dining4 = new LatLng(1.439325, 103.783402);
    final LatLng dining5 = new LatLng(1.437672, 103.784367);
    final LatLng dining6 = new LatLng(1.437039, 103.786674);
    final LatLng dining7 = new LatLng(1.442013, 103.788056);
    final LatLng dining8 = new LatLng(1.439942, 103.787892);

    //Activity locations at Woodlands
    final LatLng activity1 = new LatLng(1.442413, 103.784524);
    final LatLng activity2 = new LatLng(1.440839, 103.789235);
    final LatLng activity3 = new LatLng(1.444302, 103.783639);

    String setName = "";
    String setPriceRange = "";
    String setAddress = "";
    String setOpeningHours = "";
    String setGroup = "";
    String setDate = "";
    String setDay = "";
    int setCategory;

    DiaryRoomDatabase db;

    NavigationView navView;
    Menu menu;
    SubMenu subMenu;
    SubMenu subMenu2;
    SubMenu subMenu3;

    int confirmation = 0;
    int activityCount = 0;
    int activityCount2 = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_overlay);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Catch Up");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "October 2018 71kg 165cm\nNovember 2018 70.2kg 165cm", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        db = Room.databaseBuilder(getApplicationContext(), DiaryRoomDatabase.class, "diary_table").allowMainThreadQueries().build();
        viewHistory();

        if(getIntent().getExtras() != null)
            secondTimeUser();
    }

    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        //mMap.getUiSettings().setTiltGesturesEnabled(false);
        //mMap.getUiSettings().setZoomGesturesEnabled(false);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        mMap.setMinZoomPreference(16.25f);
        mMap.setMaxZoomPreference(17.00f);

        mMap.addMarker(new MarkerOptions().position(self).title("You are here").icon(BitmapDescriptorFactory.fromResource(R.drawable.icona))).setTag(1);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(self, 16.5f));

        mMap.addMarker(new MarkerOptions().position(dining1).title("John's Meat and Salad Shack (800m)").snippet("\nPrice range: $\nClick here to track diet").icon(BitmapDescriptorFactory.fromResource(R.drawable.iconb))).setTag(2);
        mMap.addMarker(new MarkerOptions().position(dining2).title("Ah Tan's Healthy Western Meals (1km)").snippet("\nPrice range: $$\nClick here to track diet").icon(BitmapDescriptorFactory.fromResource(R.drawable.iconb))).setTag(3);
        mMap.addMarker(new MarkerOptions().position(dining3).title("Pete's Organic Kebab (1.2km)").snippet("\nPrice range: $\nClick here to track diet").icon(BitmapDescriptorFactory.fromResource(R.drawable.icone))).setTag(4);

        mMap.addMarker(new MarkerOptions().position(dining4).title("Ah Tan's Fish Soup Cuisines (800m)").snippet("\nPrice range: $\nClick here to track diet").icon(BitmapDescriptorFactory.fromResource(R.drawable.iconb))).setTag(5);
        mMap.addMarker(new MarkerOptions().position(dining5).title("Mark's Chicken Rice (1km)").snippet("\nPrice range: $\nClick here to track diet").icon(BitmapDescriptorFactory.fromResource(R.drawable.iconb))).setTag(6);
        mMap.addMarker(new MarkerOptions().position(dining6).title("Organic Chicken and Salad Stop (1.2km)").snippet("\nPrice range: $$$\nClick here to track diet").icon(BitmapDescriptorFactory.fromResource(R.drawable.icone))).setTag(7);
        mMap.addMarker(new MarkerOptions().position(dining7).title("Daniel's Organic Vegan Alternatives (350m)").snippet("\nPrice range: $\nClick here to track diet").icon(BitmapDescriptorFactory.fromResource(R.drawable.icone))).setTag(8);
        mMap.addMarker(new MarkerOptions().position(dining8).title("Woodlands Subway (350m)").snippet("\nPrice range: $$\nClick here to track diet").icon(BitmapDescriptorFactory.fromResource(R.drawable.iconb))).setTag(9);

        mMap.addMarker(new MarkerOptions().position(activity1).title("Traditional Chinese Dance Practice (400m)").snippet("\nPrice range: Free \nSolo \nClick here to track activity").icon(BitmapDescriptorFactory.fromResource(R.drawable.icond))).setTag(10);
        mMap.addMarker(new MarkerOptions().position(activity2).title("Adventure Group Trekking (600m)").snippet("\nPrice range: Paid\nGroup \nClick here to track activity").icon(BitmapDescriptorFactory.fromResource(R.drawable.iconc))).setTag(11);
        mMap.addMarker(new MarkerOptions().position(activity3).title("Friendly Soccer Practice (800m)").snippet("\nPrice range: Free\nGroup \nClick here to track activity").icon(BitmapDescriptorFactory.fromResource(R.drawable.icond))).setTag(12);

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker marker) {
                getTrackDiet(marker);
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public void getTrackDiet(Marker marker) {

        switch (Integer.parseInt(marker.getTag().toString())) {
            case 2:
                setName = "John's Meat and Salad Shack";
                setPriceRange = "$";
                setAddress = "9 Woodlands Ave 9, Singapore 738964";
                setOpeningHours = "Opening hours: 7.30am to 9.00pm";
                setCategory = 0;
                break;

            case 3:
                setName = "Ah Tan's Healthy Western Meals";
                setPriceRange = "$$";
                setAddress = "9 Woodlands Ave 9, Singapore 738964";
                setOpeningHours = "Opening hours: 7.30am to 9.00pm";
                setCategory = 0;
                break;

            case 4:
                setName = "Pete's Organic Kebab";
                setPriceRange = "$";
                setAddress = "9 Woodlands Ave 9, Singapore 738964";
                setOpeningHours = "Opening hours: 7.30am to 9.00pm";
                setCategory = 0;
                break;

            case 5:
                setName = "Ah Tan's Fish Soup Cuisines";
                setPriceRange = "$";
                setAddress = "35 Marsiling Ind Est Rd 3, #01-11, Singapore 739257";
                setOpeningHours = "Opening hours: 9.30am to 11.00pm";
                setCategory = 1;
                break;

            case 6:
                setName = "Mark's Chicken Rice";
                setPriceRange = "$";
                setAddress = "35 Marsiling Ind Est Rd 3, #01-11, Singapore 739257";
                setOpeningHours = "Opening hours: 9.30am to 11.00pm";
                setCategory = 1;
                break;

            case 7:
                setName = "Organic Chicken and Salad Stop";
                setPriceRange = "$$$";
                setAddress = "35 Marsiling Ind Est Rd 3, #01-11, Singapore 739257";
                setOpeningHours = "Opening hours: 9.30am to 11.00pm";
                setCategory = 1;
                break;

            case 8:
                setName = "Daniel's Organic Vegan Alternatives";
                setPriceRange = "$";
                setAddress = "81 Woodlands Street 81, Singapore 730827";
                setOpeningHours = "Opening hours: 9.30am to 11.00pm";
                setCategory = 2;
                break;

            case 9:
                setName = "Woodlands Subway";
                setPriceRange = "$$";
                setAddress = "81 Woodlands Street 81, Singapore 730827";
                setOpeningHours = "Opening hours: 9.30am to 11.00pm";
                setCategory = 2;
                break;

            case 10:
                setName = "Traditional Chinese Dance Practice";
                setPriceRange = "Free";
                setGroup = "Solo";
                setAddress = "43 Woodlands Avenue 9, Singapore 737729";
                setDate = "9.30pm on 28 November 2018";
                setDay = "(Friday)";
                setCategory = 3;
                break;

            case 11:
                setName = "Adventure Group Trekking";
                setPriceRange = "$3.00 per pax";
                setGroup = "Group";
                setAddress = "43 Woodlands Avenue 9, Singapore 737729";
                setDate = "9.30pm on 30 November 2018";
                setDay = "(Saturday)";
                setCategory = 4;
                break;

            case 12:
                setName = "Friendly Soccer Practice";
                setPriceRange = "Free";
                setGroup = "Group";
                setAddress = "43 Woodlands Avenue 9, Singapore 737729";
                setDate = "9.30pm on 21 November 2018";
                setDay = "(Monday)";
                setCategory = 5;
                break;

            default:
                setAddress = "Invalid";
                break;
        }

        if(Integer.parseInt(marker.getTag().toString()) == 1) {
            if(confirmation < 2)
                confirmation++;
            else
                clearDatabase();
        }

        if (Integer.parseInt(marker.getTag().toString()) >= 2 && Integer.parseInt(marker.getTag().toString()) <= 9) {
            String message = "<big><b>" + setName + "</b></big>" + "<br><br>" + "Price range: " +
                    "<font color=\"red\">" + setPriceRange + "</font><br>" + setAddress + "<br>" + setOpeningHours;

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater factory = LayoutInflater.from(this);
            final View view;
            if(setCategory == 0)
                view = factory.inflate(R.layout.alertdialogimagea, null);
            else if(setCategory == 1)
                view = factory.inflate(R.layout.alertdialogimageaa, null);
            else if(setCategory == 2)
                view = factory.inflate(R.layout.alertdialogimageaaa, null);
            else
                view = factory.inflate(R.layout.alertdialogimagea, null);
            builder.setView(view);
            builder.setMessage(Html.fromHtml(message));
            builder.setCancelable(true);
            builder.setPositiveButton(
                    "I'm dining here",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Toast toast = Toast.makeText(getApplicationContext(), "Dining diary updated", Toast.LENGTH_SHORT);
                            //Vibrator v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
                            //v.vibrate(400);
                            //toast.show();
                            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(400,255));
                            showToast("✓ Your dining diary has been updated", true);
                            insertDiningHistory();
                            activityCount++;
                            activityCount2++;
                        }
                    });

            builder.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        if (Integer.parseInt(marker.getTag().toString()) >= 10 && Integer.parseInt(marker.getTag().toString()) <= 12) {
            String message = "<big><b>" + setName + "</b></big>" + "<br><br>" + "Price range: " +
                    "<font color=\"red\">" + setPriceRange + "</font><br>" + setGroup + "<br>" + setAddress + "<br>" +
                    setDate + " " + "<font color=\"red\">" + setDay + "</font> ";

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater factory = LayoutInflater.from(this);
            final View view;
            if(setCategory == 3)
                view = factory.inflate(R.layout.alertdialogimageb, null);
            else if(setCategory == 4)
                view = factory.inflate(R.layout.alertdialogimagebb, null);
            else if(setCategory == 5)
                view = factory.inflate(R.layout.alertdialogimagebbb, null);
            else
                view = factory.inflate(R.layout.alertdialogimageb, null);
            builder.setView(view);
            builder.setMessage(Html.fromHtml(message));
            builder.setCancelable(true);
            builder.setPositiveButton(
                    "I'm joining",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Toast toast = Toast.makeText(getApplicationContext(), "Activity diary updated", Toast.LENGTH_SHORT);
                            //Vibrator v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
                            //.vibrate(400);
                            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(400,255));
                            //toast.show();
                            showToast("✓ Your activity diary has been updated", true);
                            insertActivitiesHistory();
                            activityCount++;
                            activityCount2++;
                        }
                    });

            builder.setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void getNotification(String title, String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CHANNEL_ID";
            String description = "CHANNEL_ID";
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(getApplicationContext(), MapsActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notificationIntent.putExtra("Prompt", 1);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Notification buildNotification = mBuilder.build();
        NotificationManager mNotifyMgr = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1, buildNotification);
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(400,255));
    }

    public void firsTimeUser() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        TextView setTitle = new TextView(this);
        TextView setMessage = new TextView(this);

        setTitle.setText(Html.fromHtml("<br><big><big><b>Hi there new user :)</b></big></big><br>"));
        setTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        setMessage.setText(Html.fromHtml("<big>Please filled in the required information</big>"));
        setMessage.setGravity(Gravity.CENTER_HORIZONTAL);

        layout.addView(setTitle);
        layout.addView(setMessage);

        params.setMargins(200, 10, 200, 10);

        final EditText age = new EditText(this);
        age.setLayoutParams(params);
        age.setGravity(Gravity.CENTER_HORIZONTAL);
        age.setHint("Age");
        age.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        age.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        layout.addView(age);

        final EditText weight = new EditText(this);
        weight.setLayoutParams(params);
        weight.setGravity(Gravity.CENTER_HORIZONTAL);
        weight.setHint("Weight(kg)");
        weight.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        weight.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        layout.addView(weight);

        final EditText height = new EditText(this);
        height.setLayoutParams(params);
        height.setGravity(Gravity.CENTER_HORIZONTAL);
        height.setHint("Height(cm)");
        height.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        height.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        layout.addView(height);

        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Dining cost preference");
        spinnerArray.add("$");
        spinnerArray.add("$$");
        spinnerArray.add("$$$");
        spinnerArray.add("Any");

        ArrayList<String> spinnerArray2 = new ArrayList<String>();
        spinnerArray2.add("Activity cost preference");
        spinnerArray2.add("Free");
        spinnerArray2.add("Paid");
        spinnerArray2.add("Both");

        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spinner.setAdapter(spinnerArrayAdapter);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params2.setMargins(200, 0, 150, 0);
        spinner.setLayoutParams(params2);
        spinner.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        layout.addView(spinner);

        Spinner spinner2 = new Spinner(this);
        ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray2);
        spinner2.setAdapter(spinnerArrayAdapter2);
        spinner2.setLayoutParams(params2);
        spinner2.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        layout.addView(spinner2);

        alert.setView(layout);
        alert.setCancelable(false);
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.setNegativeButton("Opt out", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    public void secondTimeUser(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        TextView setMessage = new TextView(this);

        setMessage.setText(Html.fromHtml("<br><big>Please filled in the required information</big>"));
        setMessage.setGravity(Gravity.CENTER_HORIZONTAL);

        layout.addView(setMessage);

        params.setMargins(200, 10, 200, 10);

        final EditText age = new EditText(this);
        age.setLayoutParams(params);
        age.setGravity(Gravity.CENTER_HORIZONTAL);
        age.setHint("Age");
        age.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        age.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        layout.addView(age);

        final EditText weight = new EditText(this);
        weight.setLayoutParams(params);
        weight.setGravity(Gravity.CENTER_HORIZONTAL);
        weight.setHint("Weight(kg)");
        weight.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        weight.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        layout.addView(weight);

        final EditText height = new EditText(this);
        height.setLayoutParams(params);
        height.setGravity(Gravity.CENTER_HORIZONTAL);
        height.setHint("Height(cm)");
        height.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        height.getBackground().mutate().setColorFilter(getResources().getColor(android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
        layout.addView(height);

        alert.setView(layout);
        alert.setCancelable(false);
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.setNegativeButton("Next time", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    public void viewHistory() {
        List<Diary> diaryList = db.diaryDao().getAllDiaries();

        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        menu = navView.getMenu();

        menu.clear();

        subMenu = menu.addSubMenu("Upcoming Activites");

        int position1 = 0;
        int position2 = 0;

        for (Diary diary : diaryList) {
            if(diary.getType().equals("Activities")) {
                subMenu.add(diary.getName());
                subMenu.getItem(position1).setIcon(ContextCompat.getDrawable(this, R.drawable.iconc));
                position1++;
            }
        }

        subMenu2 = menu.addSubMenu("Weekly Dining Journal");

        for (Diary diary : diaryList) {
            if(diary.getType().equals("Dining")) {
                subMenu2.add(diary.getName2());
                subMenu2.getItem(position2).setIcon(ContextCompat.getDrawable(this, R.drawable.iconf));
                position2++;
            }
        }

        subMenu3 = menu.addSubMenu("Map Legend");
        subMenu3.add("You");
        subMenu3.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.icona));
        subMenu3.add("Healthier Food");
        subMenu3.getItem(1).setIcon(ContextCompat.getDrawable(this, R.drawable.iconb));
        subMenu3.add("Organic Food");
        subMenu3.getItem(2).setIcon(ContextCompat.getDrawable(this, R.drawable.icone));
        subMenu3.add("Free Activities");
        subMenu3.getItem(3).setIcon(ContextCompat.getDrawable(this, R.drawable.icond));
        subMenu3.add("Paid Activities");
        subMenu3.getItem(4).setIcon(ContextCompat.getDrawable(this, R.drawable.iconc));

        if(position1 == 0 && position2 == 0)
            firsTimeUser();

        if(activityCount > 3){
            getNotification("Upcoming Event","You have friendly soccer practice this Monday at 9.30pm.");
            activityCount = 0;
        }

        if(activityCount2 > 5){
            getNotification("Notification","Hi you are required to update your bio.");
            activityCount2 = 0;
        }
    }

    public void insertActivitiesHistory() {
        Diary diary = new Diary();
        diary.setName(setName);
        diary.setName2(null);
        diary.setDate(setDate);
        diary.setType("Activities");
        try {
            db.diaryDao().insert(diary);
        } catch (Exception e) {
            //Toast toast = Toast.makeText(getApplicationContext(), "Sorry you are already attending this activity", Toast.LENGTH_SHORT);
            //toast.show();
            showToast("X Sorry this activity is already in your diary", false);
        }
        viewHistory();
    }

    public void insertDiningHistory() {
        String uuid = UUID.randomUUID().toString();

        Diary diary = new Diary();
        diary.setName(uuid);
        diary.setName2(setName);
        diary.setDate(null);
        diary.setType("Dining");
        try {
            db.diaryDao().insert(diary);
        } catch (Exception e) {
            //Toast toast = Toast.makeText(getApplicationContext(), "Sorry you are already attending this activity", Toast.LENGTH_SHORT);
            //toast.show();
            showToast("X Sorry this activity is already in your diary", false);
        }
        viewHistory();
    }

    public void clearDatabase(){
        db.diaryDao().deleteAll();
        Toast toast = Toast.makeText(getApplicationContext(), "Database cleared", Toast.LENGTH_SHORT);
        toast.show();
        confirmation = 0;
        viewHistory();
    }

    public void showToast(String message, boolean colour){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        View view = toast.getView();
        if(colour) {
            view.getBackground().setColorFilter(getResources().getColor(android.R.color.holo_blue_dark), PorterDuff.Mode.SRC_IN);
        }
        else{
            view.getBackground().setColorFilter(getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_IN);
        }
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        toast.show();
    }

}

