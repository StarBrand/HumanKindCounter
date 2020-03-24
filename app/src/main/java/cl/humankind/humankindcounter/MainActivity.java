package cl.humankind.humankindcounter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Locale;

import cl.humankind.humankindcounter.cards.FactionVirtue;
import cl.humankind.humankindcounter.cards.NumericalVirtue;
import cl.humankind.humankindcounter.cards.VirtueCard;
import cl.humankind.humankindcounter.points.GameStatus;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    String msg = "Android : ";
    private HashMap<Object, Integer> new_card;
    private HashMap<Object, Integer> card_to_show;
    private VirtueCard numerical = new NumericalVirtue();
    private VirtueCard faction = new FactionVirtue();
    private GameStatus gameStatus;
    private HashMap<Integer, Integer> sanctuary_color;
    private HashMap<Integer, Integer> background;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        new_card = new HashMap<>();
        card_to_show = new HashMap<>();
        sanctuary_color = new HashMap<>();
        background = new HashMap<>();
        addSanctuaryColors();
        addBackground();
        addNewCardNumber();
        addNewCardFaction();
        addCardToShowNumber();
        addCardToShowFaction();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Log.d(msg, "The onCreate event");
    }

    private void addSanctuaryColors(){
        sanctuary_color.put(R.id.select_chimera, R.drawable.color_blue);
        sanctuary_color.put(R.id.select_abysmal, R.drawable.color_green);
        sanctuary_color.put(R.id.select_corpo, R.drawable.color_red);
        sanctuary_color.put(R.id.select_acracia, R.drawable.color_yellow);
        sanctuary_color.put(R.id.select_white, R.drawable.color_white);
    }

    private void addBackground(){
        background.put(R.id.select_chimera, R.drawable.back_chimera);
        background.put(R.id.select_abysmal, R.drawable.back_abysmal);
        background.put(R.id.select_corpo, R.drawable.back_corpo);
        background.put(R.id.select_acracia, R.drawable.back_acracia);
        background.put(R.id.select_white, R.color.colorBack);
    }

    private void addNewCardNumber() {
        new_card.put(-2, R.id.minus_two);
        new_card.put(-1, R.id.minus_one);
        new_card.put(0, R.id.zero);
        new_card.put(1, R.id.plus_one);
        new_card.put(2, R.id.plus_two);
    }

    private void addNewCardFaction() {
        new_card.put("qm", R.id.chimera);
        new_card.put("ab", R.id.abysmal);
        new_card.put("co", R.id.corpo);
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
        card_to_show.put("qm", R.drawable.card_chimera);
        card_to_show.put("ab", R.drawable.card_abysmal);
        card_to_show.put("co", R.drawable.card_corpo);
        card_to_show.put("ac", R.drawable.card_acracia);
        card_to_show.put("none", R.drawable.card_blank);
    }

    public void startGame(final View view) {
        final ImageButton sanctuary = findViewById(R.id.sanctuary);
        PopupMenu popup = new PopupMenu(MainActivity.this, sanctuary);
        popup.getMenuInflater().inflate(R.menu.select_sanctuary, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int selected = item.getItemId();
                LinearLayout main = findViewById(R.id.main);
                main.setBackgroundResource(background.get(selected));
                ImageView color = findViewById(R.id.sanctuary_color);
                color.setImageResource(sanctuary_color.get(selected));
                color.setVisibility(View.VISIBLE);
                return true;
            }
        });
        popup.show();
        gameStatus = new GameStatus();
        final Button structure_points = this.findViewById(R.id.structure_points);
        structure_points.setText(gameStatus.getStructurePoints());
        final Button will_points = this.findViewById(R.id.will_points);
        will_points.setText(gameStatus.getWillPoints());
        ImageButton minus_structure = findViewById(R.id.minus_structure);
        LayoutInflater struct_inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        LayoutInflater will_inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View structure_popup = struct_inflater.inflate(R.layout.structure_points, null);
        final View will_popup = will_inflater.inflate(R.layout.will_points, null);
        final PopupWindow popupStructureWindow = new PopupWindow(
                structure_popup, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true
        );
        final PopupWindow popupWillWindow = new PopupWindow(
                will_popup, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true
        );
        structure_points.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupStructureWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            }
        });
        will_points.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWillWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            }
        });
        Button hits = structure_popup.findViewById(R.id.hit_it);
        hits.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText hits = structure_popup.findViewById(R.id.hits);
                try{
                    int hit_points = Integer.parseInt(String.valueOf(hits.getText()));
                    gameStatus.delStructure(hit_points);
                    structure_points.setText(gameStatus.getStructurePoints());
                    if (!gameStatus.getStatus()){
                        Toast.makeText(MainActivity.this, R.string.no_structure, Toast.LENGTH_LONG).show();
                        endGame();
                    } hits.setText(null);
                } catch (NumberFormatException ignored){
                } popupStructureWindow.dismiss();
            }
        });
        Button spent_will = will_popup.findViewById(R.id.hit_it);
        spent_will.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText hits = will_popup.findViewById(R.id.hits);
                try{
                    int hit_points = Integer.parseInt(String.valueOf(hits.getText()));
                    try {
                        gameStatus.delWill(hit_points);
                        will_points.setText(gameStatus.getWillPoints());
                    } catch (Exception e){
                        Toast.makeText(MainActivity.this, R.string.no_will, Toast.LENGTH_LONG).show();
                    } hits.setText(null);
                } catch (NumberFormatException ignored){
                } popupWillWindow.dismiss();
            }
        });
        minus_structure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gameStatus.delStructure(1);
                structure_points.setText(gameStatus.getStructurePoints());
                if (! gameStatus.getStatus()){
                    Toast.makeText(MainActivity.this, R.string.no_structure, Toast.LENGTH_LONG).show();
                    endGame();
                }
            }
        });
        ImageButton plus_structure = findViewById(R.id.plus_structure);
        plus_structure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gameStatus.addStructure(1);
                structure_points.setText(gameStatus.getStructurePoints());
            }
        });
        ImageButton minus_will = findViewById(R.id.minus_will);
        minus_will.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    gameStatus.delWill(1);
                    Button will_points = MainActivity.this.findViewById(R.id.will_points);
                    will_points.setText(gameStatus.getWillPoints());
                } catch (Exception e){
                    Toast.makeText(MainActivity.this, R.string.no_will, Toast.LENGTH_LONG).show();
                }
            }
        });
        ImageButton plus_will = findViewById(R.id.plus_will);
        plus_will.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gameStatus.addWill(1);
                Button will_points = MainActivity.this.findViewById(R.id.will_points);
                will_points.setText(gameStatus.getWillPoints());
            }
        });
    }


    public void endGame(){
        ImageButton minus_structure = findViewById(R.id.minus_structure);
        minus_structure.setOnClickListener(null);
        Button structure_points = this.findViewById(R.id.structure_points);
        structure_points.setText(null);
    }

    public void nextNumericalCard(View view){
        try{
            Object chosen = numerical.nextVirtue();
            Log.d(msg, "Numerical virtue at random: " + chosen);
            ImageButton card = findViewById(R.id.card_numerical);
            card.setImageResource(card_to_show.get(chosen));
            ImageView mini_view = findViewById(new_card.get(chosen));
            mini_view.setVisibility(View.VISIBLE);
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

    public void nextFactionCard(View view){
        try{
            Object chosen = faction.nextVirtue();
            Log.d(msg, "Faction virtue at random: " + chosen);
            ImageButton card = findViewById(R.id.card_faction);
            card.setImageResource(card_to_show.get(chosen));
            ImageView mini_view = findViewById(new_card.get(chosen));
            mini_view.setVisibility(View.VISIBLE);
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

    public void shuffleNumericalCard(View view){
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

    public void shuffleFactionCard(View view){
        faction.shuffleCards();
        ImageButton card = findViewById(R.id.card_faction);
        card.setImageResource(R.drawable.faction_back);
        for(int display:new int[]{
                R.id.chimera, R.id.abysmal, R.id.corpo,
                R.id.acracia, R.id.none
        }){
            ImageView display_view = findViewById(display);
            display_view.setVisibility(View.INVISIBLE);
        }
        Toast.makeText(MainActivity.this,
                R.string.shuffled,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String language_selected = sharedPreferences.getString("language", "null");
        Configuration configuration = new Configuration(getResources().getConfiguration());
        Locale locale;
        Log.d(msg, "Selected language: " + language_selected);
        if (language_selected.equals("null")) {
            language_selected = Locale.getDefault().getDisplayLanguage();
        } Log.d(msg, "Selected language: " + language_selected);
        locale = new Locale(language_selected);
        Locale.setDefault(locale);
        configuration.locale = locale;
        getResources().updateConfiguration(configuration, null);
    }

}
