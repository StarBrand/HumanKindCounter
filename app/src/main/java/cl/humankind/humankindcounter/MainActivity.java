package cl.humankind.humankindcounter;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Random;

import cl.humankind.humankindcounter.cards.FactionVirtue;
import cl.humankind.humankindcounter.cards.NumericalVirtue;
import cl.humankind.humankindcounter.cards.VirtueCard;

public class MainActivity extends AppCompatActivity {

    String msg = "Android : ";
    private HashMap<Object, Integer> new_card;
    private HashMap<Object, Integer> card_to_show;
    private VirtueCard numerical = new NumericalVirtue();
    private VirtueCard faction = new FactionVirtue();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        new_card = new HashMap<>();
        card_to_show = new HashMap<>();
        addNewCardNumber();
        addNewCardFaction();
        addCardToShowNumber();
        addCardToShowFaction();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton numerical_virtue = findViewById(R.id.card_numerical);
        ImageButton faction_virtue = findViewById(R.id.card_faction);
        numerical_virtue.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                try{
                    Object chosen = numerical.nextVirtue();
                    Log.d(msg, "Numerical virtue at random: " + chosen);
                    ImageButton card = findViewById(R.id.card_numerical);
                    card.setImageResource(card_to_show.get(chosen));
                    ImageView view = findViewById(new_card.get(chosen));
                    view.setVisibility(View.VISIBLE);
                } catch (IndexOutOfBoundsException e){
                    Toast.makeText(MainActivity.this,
                            R.string.shuffle,
                            Toast.LENGTH_LONG).show();
                } catch (NullPointerException e){
                    Toast.makeText(MainActivity.this,
                            R.string.error,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        faction_virtue.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                try{
                    Object chosen = faction.nextVirtue();
                    Log.d(msg, "Numerical virtue at random: " + chosen);
                    ImageButton card = findViewById(R.id.card_faction);
                    card.setImageResource(card_to_show.get(chosen));
                    ImageView view = findViewById(new_card.get(chosen));
                    view.setVisibility(View.VISIBLE);
                } catch (IndexOutOfBoundsException e){
                    Toast.makeText(MainActivity.this,
                            R.string.shuffle,
                            Toast.LENGTH_LONG).show();
                } catch (NullPointerException e){
                    Toast.makeText(MainActivity.this,
                            R.string.error,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        ImageButton shuffle_numerical = findViewById(R.id.restart_numerical);
        ImageButton shuffle_faction = findViewById(R.id.restart_faction);
        shuffle_numerical.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                numerical.shuffleCards();
                ImageButton card = findViewById(R.id.card_numerical);
                card.setImageResource(R.drawable.numerical_back);
                for(int display:new int[]{
                        R.id.minus_two, R.id.minus_one, R.id.zero,
                        R.id.plus_one, R.id.plus_two
                }){
                    ImageView display_view = findViewById(display);
                    display_view.setVisibility(View.INVISIBLE);
                }
                Toast.makeText(MainActivity.this,
                        R.string.shuffled,
                        Toast.LENGTH_SHORT).show();
            }
        });
        shuffle_faction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                faction.shuffleCards();
                ImageButton card = findViewById(R.id.card_faction);
                card.setImageResource(R.drawable.faction_back);
                for(int display:new int[]{
                        R.id.quimera, R.id.abismal, R.id.corporacion,
                        R.id.acracia, R.id.none
                }){
                    ImageView display_view = findViewById(display);
                    display_view.setVisibility(View.INVISIBLE);
                }
                Toast.makeText(MainActivity.this,
                        R.string.shuffled,
                        Toast.LENGTH_SHORT).show();
            }
        });
        Log.d(msg, "The onCreate event");
    }

    private void addNewCardNumber() {
        new_card.put(-2, R.id.minus_two);
        new_card.put(-1, R.id.minus_one);
        new_card.put(0, R.id.zero);
        new_card.put(1, R.id.plus_one);
        new_card.put(2, R.id.plus_two);
    }

    private void addNewCardFaction() {
        new_card.put("qm", R.id.quimera);
        new_card.put("ab", R.id.abismal);
        new_card.put("co", R.id.corporacion);
        new_card.put("ac", R.id.acracia);
        new_card.put("none", R.id.none);
    }

    private void addCardToShowNumber() {
        card_to_show.put(-2, R.drawable.card_minus_two);
        card_to_show.put(-1, R.drawable.card_minus_one);
        card_to_show.put(0, R.drawable.card_zero);
        card_to_show.put(1, R.drawable.card_plus_one);
        card_to_show.put(2, R.drawable.card_plus_two);
    }

    private void addCardToShowFaction() {
        card_to_show.put("qm", R.drawable.card_quimera);
        card_to_show.put("ab", R.drawable.card_abismal);
        card_to_show.put("co", R.drawable.card_corporacion);
        card_to_show.put("ac", R.drawable.card_acracia);
        card_to_show.put("none", R.drawable.card_blank);
    }

    public void startService(View view) {
        startService(new Intent(getBaseContext(), FirstService.class));
    }

    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), FirstService.class));
    }
}
