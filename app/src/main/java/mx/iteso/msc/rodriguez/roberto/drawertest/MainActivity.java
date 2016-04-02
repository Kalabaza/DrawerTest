package mx.iteso.msc.rodriguez.roberto.drawertest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private Button popUpButton;
    private Button dialogButton;
    private Button alertButton;
    private CharSequence title;
    private CharSequence drawerTitle;
    private String[] planets;
    // Image of the planets
    private static Bitmap planetSrc;
    // Coordinates of each planet in the image (kind of a sprite map)
    private static int[][] coordinates = {{148, 25},   {624, 25},   {1104, 25},
                                          {148, 520},  {624, 520},  {1104, 520},
                                          {148, 1005}, {624, 1005}, {1104, 1005}};
    // Size of the rectangle that will contain a planet
    private static int rectWidth = 400;
    private static int rectHeight = 560;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load the planet names from the strings resources
        planets = getResources().getStringArray(R.array.planets);
        // Get the DrawerLayout
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        // Get the listview that holds the elements on the drawer panel
        drawerList = (ListView)findViewById(R.id.left_drawer);
        title = drawerTitle = getTitle();

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // Send the planet strings to the drawer list
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, planets));
        // Add a listener to catch the click event on the drawer list
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        // Load the planets image file
        planetSrc = BitmapFactory.decodeResource(getResources(), R.drawable.planets);
        // Add a drawer toggle button
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            // Drawer is closed
            public void onDrawerClosed(View view) {
                setTitle(title);
                invalidateOptionsMenu();
            }
            // Drawer is opened
            public void onDrawerOpened(View drawerView) {
                setTitle(drawerTitle);
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.addDrawerListener(drawerToggle);

        // Enable the drawer layout button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        popUpButton = (Button)findViewById(R.id.popup_button);
        popUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer text = new StringBuffer();
                text.append(getResources().getString(R.string.toast_message));
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
                toast.show();
            }
        });
        dialogButton = (Button)findViewById(R.id.dialog_button);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new FireMissilesDialogFragment();
                dialog.show(getFragmentManager(), getResources().getString(R.string.dialog_fire));
            }
        });
        alertButton = (Button)findViewById(R.id.alert_button);
        alertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                // Add a title to the alert dialog
                .setTitle(getResources().getString(R.string.alert_title))
                // Add the message to the dialog
                .setMessage(getResources().getString(R.string.alert_body))
                // Set the listener for the positive button
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // In case no is pressed don't do anything
                    }
                })
                // Set the listener for the negative button
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // In case no is pressed don't do anything
                    }
                })
                // Create a dialog with the previous characteristics
                .create();
                // Add a listener to change the color of the buttons in the dialog
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        // Change the positive button to green color with white text
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(0xFF609000);
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.WHITE);
                        // Change the negative button to red color with white text
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(0xFFC00000);
                        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.WHITE);
                    }
                });
                // Show the dialog on the screen
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add a menu to the activity
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        menu.findItem(R.id.action_websearch).setVisible(!drawerLayout.isDrawerOpen(drawerList));
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_websearch:
                // Create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getSupportActionBar().getTitle());
                // Catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        // Add an argument with the planet number
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment in the menu_main activity
        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();

        // Update the action bar and close the drawer
        drawerList.setItemChecked(position, true);
        setTitle(planets[position]);
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public static class FireMissilesDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for conversation dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // Chain together various setter methods to set the dialog characteristics
            builder.setMessage(R.string.dialog_fire)
                    .setTitle(R.string.dialog_title)
                    .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // In case yes is pressed don't do anything
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener () {
                        public void onClick(DialogInterface dialog, int id) {
                            // In case no is pressed don't do anything
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public static class PlanetFragment extends Fragment {
        public static final String ARG_PLANET_NUMBER = "planet_number";

        public PlanetFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_planet, container, false);

            int position = getArguments().getInt(ARG_PLANET_NUMBER);
            String planet = getResources().getStringArray(R.array.planets)[position];
            Bitmap planetDst = Bitmap.createBitmap(drawerLayout.getWidth(), drawerLayout.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(planetDst);
            float density = getResources().getDisplayMetrics().density;
            Rect srcClip = new Rect((int)(coordinates[position][0] * density),                  // Left
                                    (int)(coordinates[position][1] * density),                  // Top
                                   (int)((coordinates[position][0] + rectWidth) * density),     // Right
                                   (int)((coordinates[position][1] + rectHeight) * density));   // Bottom
            // THe resulting image will cover the available space on screen
            Rect dstClip = new Rect(0, 0, drawerLayout.getWidth(), drawerLayout.getHeight());
            // Draw only a portion of the original bitmap into a new bitmap
            canvas.drawBitmap(planetSrc, srcClip, dstClip, null);
            ((ImageView)rootView.findViewById(R.id.image)).setImageBitmap(planetDst);
            return rootView;
        }
    }
}
