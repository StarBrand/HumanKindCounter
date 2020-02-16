package cl.humankind.humankindcounter;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    String msg = "Android : ";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(msg, "The onCreate event");
    }

    public void startService(View view) {
        startService(new Intent(getBaseContext(), FirstService.class));
    }

    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), FirstService.class));
    }
}
