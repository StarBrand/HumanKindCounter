package cl.humankind.humankindcounter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Locale;

import cl.humankind.humankindcounter.cards.CardPair;
import cl.humankind.humankindcounter.cards.FactionVirtue;
import cl.humankind.humankindcounter.cards.NumericalVirtue;
import cl.humankind.humankindcounter.cards.VirtueCard;
import cl.humankind.humankindcounter.points.GameStatus;
import cl.humankind.humankindcounter.points.MainSanctuary;
import cl.humankind.humankindcounter.points.Sanctuary;
import cl.humankind.humankindcounter.points.SanctuaryCache;

public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    String msg = "Android : ";
    private VirtueCard numerical = new NumericalVirtue();
    private VirtueCard faction = new FactionVirtue();
    private GameStatus gameStatus;
    private SanctuaryCache sanctuaryCache;
    private MainSanctuary sanctuary;
    private SQLiteDatabase sanctuaries;
    private HashMap<Integer, Sanctuary> options;
    private PopupWindow popupPointsWindow;
    private PopupMenu menuSanctuaries;
    private Button structurePoints;
    private Button willPoints;
    private int[] cache = new int[] {
            R.id.cache_1, R.id.cache_2, R.id.cache_3,
            R.id.cache_4, R.id.cache_5
    };

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
        sanctuaryCache = new SanctuaryCache();
        super.onCreate(savedInstanceState);
        SanctuariesDatabaseHelper sanctuariesHelper = new SanctuariesDatabaseHelper(this);
        sanctuaries = sanctuariesHelper.getReadableDatabase();
        String get_cache_sql = "SELECT name, structure, will, faction\n" +
                "FROM user_preference JOIN sanctuaries\n" +
                "ON user_preference.\"index\" = sanctuaries.\"index\"\n" +
                "ORDER BY uses DESC, timestamp DESC;";
        Cursor cursor = sanctuaries.rawQuery(get_cache_sql,null);
        while (cursor.moveToNext()){
            Log.d(msg, "Sanctuary on cache: " +
                    cursor.getString(cursor.getColumnIndex("name")));
            sanctuaryCache.addSanctuary(
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getInt(cursor.getColumnIndex("structure")),
                    cursor.getInt(cursor.getColumnIndex("will")),
                    cursor.getString(cursor.getColumnIndex("faction"))
            );
        }
        cursor.close();
        sanctuaries.close();
        setContentView(R.layout.activity_main);
        options = new HashMap<>();
        structurePoints = findViewById(R.id.structure_points);
        willPoints = findViewById(R.id.will_points);
        setOptions(5);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void setOptions(int toShow){
        ImageButton sanctuaryButton = findViewById(R.id.sanctuary);
        menuSanctuaries = new PopupMenu(MainActivity.this, sanctuaryButton);
        menuSanctuaries.getMenuInflater().inflate(R.menu.select_sanctuary,
                menuSanctuaries.getMenu());
        for(int i = 0; i < toShow || !sanctuaryCache.cacheEmpty(); i++) {
            MenuItem item = menuSanctuaries.getMenu().findItem(cache[i]);
            if (options.size() <= i) {
                Sanctuary toAdd = sanctuaryCache.getSanctuary();
                options.put(cache[i], toAdd);
            } else Log.d(msg, "Options already set");
            Sanctuary aSanctuary = options.get(cache[i]);
            if (aSanctuary != null) {
                item.setTitle(aSanctuary.getName());
                item.setVisible(true);
            }
        }
    }

    public void startGame(final View view) {
        menuSanctuaries.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                int selected = item.getItemId();
                if (selected == R.id.look_for){
                    // TODO: implement looking for
                    return true;
                } else if (selected == R.id.set_up) {
                    // TODO: implement set up santuary
                    return true;
                } else {
                    sanctuary = new MainSanctuary(options.get(selected));
                }
                LinearLayout main = findViewById(R.id.main);
                main.setBackgroundResource(sanctuary.getBackground());
                ImageView color = findViewById(R.id.sanctuary_color);
                color.setImageResource(sanctuary.getColor());
                structurePoints.setText(String.valueOf(sanctuary.getStructurePoints()));
                willPoints.setText(String.valueOf(sanctuary.getWillPoints()));
                return true;
            }
        }); menuSanctuaries.show();
        gameStatus = new GameStatus();
        structurePoints.setText(gameStatus.getStructurePoints());
        structurePoints.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                popupStructure(v);
            }
        });
        willPoints.setText(gameStatus.getWillPoints());
        willPoints.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWill(v);
            }
        });
        ImageButton minus_structure = findViewById(R.id.minus_structure);
        minus_structure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                minusStructure(v);
            }
        });
        ImageButton plus_structure = findViewById(R.id.plus_structure);
        plus_structure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                plusStructure(v);
            }
        });
        plus_structure.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                addStructure(v);
                return false;
            }
        });
        ImageButton minus_will = findViewById(R.id.minus_will);
        minus_will.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                minusWill(v);
            }
        });
        ImageButton plus_will = findViewById(R.id.plus_will);
        plus_will.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                plusWill(v);
            }
        });
        plus_will.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                addWill(v);
                return false;
            }
        });
    }

    private View popupWindow(View view, int questionId, int hintId, int hitItId)
            throws NullPointerException{
        final View popup;
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        if (inflater != null)
            popup = inflater.inflate(R.layout.popup_points,null);
        else {
            Log.w(msg, "Inflater get null");
            throw new NullPointerException();
        } popupPointsWindow = new PopupWindow(
                popup, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true
        );
        popupPointsWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        popupPointsWindow.setFocusable(true);
        ((TextView) popup.findViewById(R.id.question)).setText(questionId);
        ((EditText) popup.findViewById(R.id.hits)).setHint(hintId);
        ((Button) popup.findViewById(R.id.hit_it)).setText(hitItId);
        return popup;
    }

    public void popupStructure(final View view){
        final View structure_popup;
        try {
            structure_popup = popupWindow(view, R.string.structure_question,
                    R.string.passed, R.string.hit_it);
        } catch (NullPointerException e){return;}
        final Button hits = structure_popup.findViewById(R.id.hit_it);
        hits.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(msg, "Here inside");
                EditText hits = structure_popup.findViewById(R.id.hits);
                try {
                    int hit_points = Integer.parseInt(String.valueOf(hits.getText()));
                    Log.d(msg, "Hits: " + hit_points);
                    gameStatus.delStructure(hit_points);
                    structurePoints.setText(gameStatus.getStructurePoints());
                    if (!gameStatus.getStatus()){
                        Toast.makeText(
                                MainActivity.this,
                                R.string.no_structure,
                                Toast.LENGTH_LONG).show();
                        endGame();}
                } catch (NumberFormatException e){
                    Log.w(msg, "Error getting hits");
                } hits.setText(null);
                popupPointsWindow.dismiss();
            }
        });
    }

    public void minusStructure(final View view){
        gameStatus.delStructure(1);
        structurePoints.setText(gameStatus.getStructurePoints());
        if (! gameStatus.getStatus()){
            Toast.makeText(MainActivity.this, R.string.no_structure, Toast.LENGTH_LONG).show();
            endGame();
        }
    }

    public void plusStructure(final View view){
        gameStatus.addStructure(1);
        structurePoints.setText(gameStatus.getStructurePoints());
    }

    public void addStructure(final View view){
        final View structure_popup;
        try {
            structure_popup = popupWindow(view, R.string.add_structure_question,
                    R.string.add_structure, R.string.add_it);
        } catch (NullPointerException e){return;}
        final Button hit_it = structure_popup.findViewById(R.id.hit_it);
        hit_it.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText hits = structure_popup.findViewById(R.id.hits);
                try {
                    int hit_points = Integer.parseInt(String.valueOf(hits.getText()));
                    Log.d(msg, "Recover: " + hit_points);
                    gameStatus.addStructure(hit_points);
                    structurePoints.setText(gameStatus.getStructurePoints());
                } catch (NumberFormatException e){
                    Log.w(msg, "Error getting hits");
                } hits.setText(null);
                popupPointsWindow.dismiss();
            }
        });
    }

    public void popupWill(final View view){
        final View will_points;
        try {
            will_points = popupWindow(view, R.string.will_question,
                    R.string.will_spent, R.string.spend);
        } catch (NullPointerException e){return;}
        final Button hits = will_points.findViewById(R.id.hit_it);
        hits.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText hits = will_points.findViewById(R.id.hits);
                try {
                    int hit_points = Integer.parseInt(String.valueOf(hits.getText()));
                    Log.d(msg, "Spends: " + hit_points);
                    try {
                        gameStatus.delWill(hit_points);
                        willPoints.setText(gameStatus.getWillPoints());
                    } catch (Exception e){
                        Toast.makeText(MainActivity.this, R.string.no_will,
                                Toast.LENGTH_LONG).show();}
                } catch (NumberFormatException e){
                    Log.w(msg, "Error getting hits");
                } hits.setText(null);
                popupPointsWindow.dismiss();
            }
        });
    }

    public void minusWill(final View view){
        try{
            gameStatus.delWill(1);
            Button will_points = MainActivity.this.findViewById(R.id.will_points);
            will_points.setText(gameStatus.getWillPoints());
        } catch (Exception e){
            Toast.makeText(MainActivity.this, R.string.no_will, Toast.LENGTH_LONG).show();
        }
    }

    public void plusWill(final View view){
        gameStatus.addWill(1);
        Button will_points = MainActivity.this.findViewById(R.id.will_points);
        will_points.setText(gameStatus.getWillPoints());
    }

    public void addWill(final View view){
        final View will_pop;
        try {
            will_pop = popupWindow(view, R.string.add_will_question,
                    R.string.add_will, R.string.add_it);
        } catch (NullPointerException e){return;}
        final Button hit_it = will_pop.findViewById(R.id.hit_it);
        hit_it.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText hits = will_pop.findViewById(R.id.hits);
                try {
                    int hit_points = Integer.parseInt(String.valueOf(hits.getText()));
                    Log.d(msg, "Add: " + hit_points);
                    gameStatus.addWill(hit_points);
                    willPoints.setText(gameStatus.getWillPoints());
                } catch (NumberFormatException e){
                    Log.w(msg, "Error getting hits");
                } hits.setText(null);
                popupPointsWindow.dismiss();
            }
        });
    }

    public void endGame(){
        ImageButton minus_structure = findViewById(R.id.minus_structure);
        minus_structure.setOnClickListener(null);
        structurePoints.setText(null);
        structurePoints.setOnClickListener(null);
        ImageButton plus_structure = findViewById(R.id.plus_structure);
        plus_structure.setOnClickListener(null);
        plus_structure.setOnLongClickListener(null);
        ImageButton minus_will = findViewById(R.id.minus_will);
        minus_will.setOnClickListener(null);
        willPoints.setText(null);
        willPoints.setOnClickListener(null);
        ImageButton plus_will = findViewById(R.id.plus_will);
        plus_will.setOnClickListener(null);
        plus_will.setOnLongClickListener(null);
    }

    public void nextNumericalCard(View view){
        nextCard(R.id.card_numerical, numerical);
    }

    public void nextFactionCard(View view){
        nextCard(R.id.card_faction, faction);
    }

    private void nextCard(int cardId, VirtueCard virtue){
        try{
            CardPair chosen = virtue.nextVirtue();
            Log.d(msg, "Virtue at random: " + chosen.getDisplay());
            ImageButton card = findViewById(cardId);
            card.setImageResource(chosen.getCardImage());
            ImageView mini_view = findViewById(chosen.getDisplay());
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
