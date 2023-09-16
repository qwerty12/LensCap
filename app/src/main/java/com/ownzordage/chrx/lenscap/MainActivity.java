package com.ownzordage.chrx.lenscap;

import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import static android.view.View.GONE;

import com.rosan.dhizuku.api.Dhizuku;
import com.rosan.dhizuku.api.DhizukuRequestPermissionListener;


/**
 * The primary Activity
 */
public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    Context mContext;

    View.OnClickListener clickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        updateUI();

        Button setAdminButton = (Button) findViewById(R.id.enable_device_admin);
        Button lensCapOnButton = (Button) findViewById(R.id.lensCapOnButton);
        Button lensCapOffButton = (Button) findViewById(R.id.lensCapOffButton);
        ImageButton imageButton = (ImageButton) findViewById(R.id.mainToggleButton);

        setAdminButton.setOnClickListener(view -> {
            if (Dhizuku.init(mContext) && !Dhizuku.isPermissionGranted()) {
                Dhizuku.requestPermission(new DhizukuRequestPermissionListener() {
                    @Override
                    public void onRequestPermission(int grantResult) throws RemoteException {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
                            updateUI();
                        }
                    }
                });
            }
        });

        clickListener = view -> {
            LensCapActivator.toggleLensCap(mContext);
            updateUI();
        };

        lensCapOnButton.setOnClickListener(clickListener);
        lensCapOffButton.setOnClickListener(clickListener);
        imageButton.setOnClickListener(clickListener);

        // Show Quick Settings promo card or hide it depending on version
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            findViewById(R.id.quick_settings_card).setVisibility(View.VISIBLE);
            findViewById(R.id.quick_settings_show_me).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    watchYoutubeVideo("ZdsKdM-IMiQ");
                }
            });
        } else {
            findViewById(R.id.quick_settings_card).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            menu.findItem(R.id.action_qs_youtube).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_qs_youtube:
                watchYoutubeVideo("ZdsKdM-IMiQ");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        Button setAdminButton = (Button) findViewById(R.id.enable_device_admin);
        Button lensCapOnButton = (Button) findViewById(R.id.lensCapOnButton);
        Button lensCapOffButton = (Button) findViewById(R.id.lensCapOffButton);
        ImageButton imageButton = (ImageButton) findViewById(R.id.mainToggleButton);
        TextView lensCapStatus = (TextView) findViewById(R.id.lensCapStatus);
        TextView lensCapStatus2 = (TextView) findViewById(R.id.lensCapStatus2);

        LensCapActivator.Status cameraStatus = LensCapActivator.getStatus(mContext);

        switch (cameraStatus) {
            case CAMERA_DISABLED:
                setAdminButton.setVisibility(GONE);
                imageButton.setEnabled(true);

                lensCapOffButton.setEnabled(true);
                lensCapOnButton.setEnabled(false);

                lensCapStatus.setText(getText(R.string.lens_cap_status_on));
                lensCapStatus2.setText(getText(R.string.lens_cap_status_on2));

                imageButton.setImageResource(R.drawable.lenscap);
                break;
            case CAMERA_ENABLED:
                setAdminButton.setVisibility(GONE);
                imageButton.setEnabled(true);

                lensCapOnButton.setEnabled(true);
                lensCapOffButton.setEnabled(false);

                lensCapStatus.setText(getText(R.string.lens_cap_status_off));
                lensCapStatus2.setText(getText(R.string.lens_cap_status_off2));

                imageButton.setImageResource(R.drawable.lens);
                break;
            default:
                setAdminButton.setVisibility(View.VISIBLE);

                lensCapOnButton.setEnabled(false);
                lensCapOffButton.setEnabled(false);

                imageButton.setEnabled(false);
                imageButton.setImageResource(R.drawable.lens);
                break;
        }

        updateWidget();
    }

    private void updateWidget() {
        // Register an onClickListener
        Log.v("updateWidget", "START");
        Intent intent = new Intent(this, LensCapWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        sendBroadcast(intent);
    }

}