package cl.humankind.humankindcounter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NumericalVirtud extends Service implements OnClickListener {

    private int current_card = 10;
    private boolean[] to_display = {false, false, false, false, false};
    private List<Integer> available = new ArrayList<>(Arrays.asList(-2, -1, 0, 1, 2));

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(this, "Service Destroy", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        Random rand = new Random();
        int chosen = rand.nextInt(available.size());
        current_card = available.get(chosen);
        to_display[chosen] = true;
        available.remove(chosen);
    }
}

