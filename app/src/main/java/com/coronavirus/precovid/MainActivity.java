package com.coronavirus.precovid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends Activity {
    private SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView btn = (TextView) findViewById(R.id.text1);
        btn.setText("PreCovid'19");
        sharedpreferences=getApplicationContext().getSharedPreferences("Preferences", 0);
        Thread TimerThread=new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (
                        InterruptedException e) {
                    e.printStackTrace();
                }
                finally
                {
                    String login = sharedpreferences.getString("LOGIN", null);
                    if (login != null) {
                        Log.d("msg","greatly");
                        Intent intent = new Intent(getApplicationContext(), MapsActivityCurrentPlace.class);
                        intent.putExtra("key",login);
                        startActivityForResult(intent,0);
                    }
                    else {

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };
        TimerThread.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}