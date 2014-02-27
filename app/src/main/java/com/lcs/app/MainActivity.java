package com.lcs.app;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.Window;
import android.widget.TextView;

import java.security.Provider;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment(this))
                    .commit();
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
        if (id == R.id.exit) {
            this.finish();
        }else if( id == R.id.about ){
            this.showAbout();

        }
        return super.onOptionsItemSelected(item);
    }
    private void showAbout(){
        try {

            String d  =  this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            new AlertDialog.Builder(this).setTitle("about").setMessage("version:" + d ).setPositiveButton("ok", null).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private TextView loginName = null;
        private TextView host = null;
        private TextView name = null;
        private TextView pass = null;
        private Context context = null;
        private TextView number = null;
        private SensorManager sensorManager =  null ;
        public PlaceholderFragment( Context context ) {
            this.context = context;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            loginName = (TextView)rootView.findViewById(R.id.loginName);
            host = (TextView)rootView.findViewById(R.id.host);
            name = (TextView)rootView.findViewById(R.id.name);
            pass = (TextView)rootView.findViewById(R.id.pass);
            number = (TextView)rootView.findViewById(R.id.number);

            this.initInputData();

            rootView.findViewById(R.id.login).setOnClickListener(clickListener);
           // rootView.findViewById(R.menu.main)
            sensorManager =  (SensorManager)this.context.getSystemService(SENSOR_SERVICE);
            return rootView;
        }
        @Override
        public void onResume(){
            super.onResume();
            if( sensorManager == null )return;
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        }

        @Override
        public void onStop(){
            super.onStop();
            if( sensorManager == null )return;
            sensorManager.unregisterListener(sensorEventListener);
        }

        private void saveInputData(){
            SharedPreferences data = this.context.getSharedPreferences("data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = data.edit();
            editor.putString("host",host.getText().toString());
            editor.putString("name",name.getText().toString());
            editor.putString("pass",pass.getText().toString());
            editor.commit();
        }
        private void initInputData(){
            SharedPreferences data = this.context.getSharedPreferences("data", Context.MODE_PRIVATE);
            host.setText( data.getString("host", host.getText().toString() ) );
            name.setText( data.getString("name", name.getText().toString() ) );
            pass.setText( data.getString("pass", pass.getText().toString() ) );
        }

        private void rock(){
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            //v.vibrate(new long[]{100, 10, 1000}, -1);
            v.vibrate(300);
        }

        /**
         * not done
         */
        private void sound(){
        /*
            AudioManager audioService = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
               return;
            }
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            */
        }

        private void login(){
            saveInputData();
            loginName.setText("login...");
            new Thread(runnable).start();
        }

        View.OnClickListener clickListener = new View.OnClickListener(){
            private int i = 0;
            @Override
            public void onClick(View view) {
               // Log.i("lcs","click" + view.getId()+view.toString());
                login();
                return ;
            }
        };
        SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] values = event.values;
                float x = values[0]; // x轴方向的重力加速度，向右为正
                float y = values[1]; // y轴方向的重力加速度，向前为正
                float z = values[2]; // z轴方向的重力加速度，向上为正

                float max =  Math.max(Math.max(Math.abs(x) , Math.abs(y)),Math.abs(z)-10);
                Log.i("lcs","MAX:" +max+ " x轴方向的重力加速度" + x +  "；y轴方向的重力加速度" + y +  "；z轴方向的重力加速度" + z);

               /* int input = 3 ;
                try{
                    input =  new Integer(number.getText()+"");
                }catch (Exception e){}
                number.setText(input+"");
                */
                if (max > 15 ) {
                    login();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String val = data.getString("value");
                Log.i("lcs","请求结果-->" + val);
                rock();
                loginName.setText(new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date()) + " : " + val);
            }
        };

        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                Looper.prepare();
                String string = "n";
                Login g = new Login( host.getText().toString() );
                string  = g.f( name.getText().toString() , pass.getText().toString() );
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value",string);
                msg.setData(data);
                handler.sendMessage(msg);
            }
        };
    }
}
