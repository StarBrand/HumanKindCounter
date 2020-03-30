package cl.humankind.humankindcounter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
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
    private int[] display_faction = new int[]{
            R.id.faction_box_1, R.id.faction_box_2, R.id.faction_box_3,
            R.id.faction_box_4, R.id.faction_box_5
    };
    private int current_display_faction = 0;
    private int[] display_numerical = new int[]{
            R.id.numerical_box_1, R.id.numerical_box_2, R.id.numerical_box_3,
            R.id.numerical_box_4, R.id.numerical_box_5
    };
    private int current_display_numerical = 0;

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
        String get_cache_sql = "SELECT user_preference.\"index\", name, structure, will, faction\n" +
                "FROM user_preference JOIN sanctuaries\n" +
                "ON user_preference.\"index\" = sanctuaries.\"index\"\n" +
                "ORDER BY timestamp DESC LIMIT 1;";
        Cursor cursor = sanctuaries.rawQuery(get_cache_sql,null);
        int index_to_exclude = 1000;
        if (cursor.moveToNext()){
            Log.d(msg, "Sanctuary on cache: " +
                    cursor.getString(cursor.getColumnIndex("name")));
            index_to_exclude = cursor.getInt(cursor.getColumnIndex("index"));
            sanctuaryCache.addSanctuary(
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getInt(cursor.getColumnIndex("structure")),
                    cursor.getInt(cursor.getColumnIndex("will")),
                    cursor.getString(cursor.getColumnIndex("faction")));
        }
        get_cache_sql = "SELECT name, structure, will, faction\n" +
                "FROM user_preference JOIN sanctuaries\n" +
                "ON user_preference.\"index\" = sanctuaries.\"index\"\n" +
                "WHERE user_preference.\"index\" != " + index_to_exclude + " \n" +
                "ORDER BY uses DESC, timestamp DESC LIMIT 4;";
        cursor = sanctuaries.rawQuery(get_cache_sql,null);
        while (cursor.moveToNext()) {
            Log.d(msg, "Sanctuary on cache: " +
                    cursor.getString(cursor.getColumnIndex("name")));
            sanctuaryCache.addSanctuary(
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getInt(cursor.getColumnIndex("structure")),
                    cursor.getInt(cursor.getColumnIndex("will")),
                    cursor.getString(cursor.getColumnIndex("faction"))
            );
        } cursor.close();
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
                    lookForPopup(view);
                } else if (selected == R.id.set_up) {
                    setUpPopup(view);
                } else {
                    sanctuary = new MainSanctuary(options.get(selected));
                    settingSanctuary();
                } return true;
            }
        }); menuSanctuaries.show();
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

    private void settingSanctuary(){
        Log.d(msg, "Setting sanctuary");
        LinearLayout main = findViewById(R.id.main);
        main.setBackgroundResource(sanctuary.getBackground());
        ImageView color = findViewById(R.id.sanctuary_color);
        color.setImageResource(sanctuary.getColor());
        gameStatus = new GameStatus(
                sanctuary.getStructurePoints(),
                sanctuary.getWillPoints()
        );
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
    }

    private void lookForPopup(final View view){
        final View popup = View.inflate(this, R.layout.look_for, null);
        final PopupWindow lookForSanctuary = new PopupWindow(
                popup, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true
        );
        lookForSanctuary.showAtLocation(view, Gravity.CENTER, 0, 0);
        final String[] edition_selected = {"ev"};
        Button ok_button = popup.findViewById(R.id.ok_button);
        final Button edition = popup.findViewById(R.id.select_edition);
        edition.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(MainActivity.this,
                        R.style.PopupMenuTheme);
                PopupMenu editionMenu = new PopupMenu(wrapper, edition);
                editionMenu.getMenuInflater().inflate(R.menu.edition_menu,
                        editionMenu.getMenu());
                editionMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.ev:
                                edition_selected[0] = "ev";
                                edition.setText(R.string.ev);
                                break;
                            case R.id.dv:
                                edition_selected[0] = "dv";
                                edition.setText(R.string.dv);
                                break;
                            case R.id.su:
                                edition_selected[0] = "su";
                                edition.setText(R.string.su);
                                break;
                            case R.id.ra:
                                edition_selected[0] = "ra";
                                edition.setText(R.string.ra);
                                break;
                        }
                        return true;
                    }
                }); editionMenu.show();
            }
        });
        ok_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lookForSanctuary.dismiss();
            }
        });
    }

    private void setUpPopup(final View view){
        final View popup = View.inflate(this, R.layout.set_up,null);
        final PopupWindow setUpSanctuary = new PopupWindow(
                popup, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true
        );
        setUpSanctuary.showAtLocation(view, Gravity.CENTER, 0, 0);
        Button ok_button = popup.findViewById(R.id.ok_button);
        final Button faction = popup.findViewById(R.id.select_faction);
        final String[] faction_selected = {"none"};
        faction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(MainActivity.this,
                        R.style.PopupMenuTheme);
                PopupMenu factionMenu = new PopupMenu(wrapper, faction);
                factionMenu.getMenuInflater().inflate(R.menu.faction_menu,
                        factionMenu.getMenu());
                factionMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.chimera:
                                faction_selected[0] = "chimera";
                                faction.setText(R.string.chimera);
                                break;
                            case R.id.abysmal:
                                faction_selected[0] = "abysmal";
                                faction.setText(R.string.abysmal);
                                break;
                            case R.id.corpo:
                                faction_selected[0] = "corpo";
                                faction.setText(R.string.corpo);
                                break;
                            case R.id.acracia:
                                faction_selected[0] = "acracia";
                                faction.setText(R.string.acracia);
                                break;
                            case R.id.none:
                                faction_selected[0] = "none";
                                faction.setText(R.string.no_faction);
                                break;
                        }
                        return true;
                    }
                }); factionMenu.show();
            }
        });
        ok_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(msg, faction_selected[0]);
                int structure;
                int will;
                try {
                    structure = Integer.parseInt(String.valueOf(
                            ((EditText) popup.findViewById(R.id.set_structure))
                                    .getText()));
                } catch (NumberFormatException e) {
                    structure = 12;
                }
                try {
                    will = Integer.parseInt(String.valueOf(
                            ((EditText) popup.findViewById(R.id.set_will))
                                    .getText()));
                } catch (NumberFormatException e) {
                    will = 4;
                }
                sanctuary = new MainSanctuary(structure, will, faction_selected[0]);
                setUpSanctuary.dismiss();
                settingSanctuary();
            }
        });
    }

    private View popupWindow(View view, int questionId, int hintId, int hitItId){
        final View popup = View.inflate(this, R.layout.popup_points, null);
        popupPointsWindow = new PopupWindow(
                popup, LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true
        );
        popupPointsWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
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
        nextCard(R.id.card_numerical, numerical, display_numerical, false);
    }

    public void nextFactionCard(View view){
        nextCard(R.id.card_faction, faction, display_faction, true);
    }

    private void nextCard(int cardId, VirtueCard virtue, int[] display, boolean faction){
        try{
            CardPair chosen = virtue.nextVirtue();
            Log.d(msg, "Virtue at random: " + chosen.getDisplay());
            ImageButton card = findViewById(cardId);
            card.setImageResource(chosen.getCardImage());
            ImageView mini_view;
            if (faction) {
                mini_view = findViewById(display[current_display_faction]);
                current_display_faction += 1;
            } else {
                mini_view = findViewById(display[current_display_numerical]);
                current_display_numerical += 1;
            }
            mini_view.setVisibility(View.VISIBLE);
            mini_view.setImageResource(chosen.getDisplay());
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
        for(int display:display_numerical){
            ImageView display_view = findViewById(display);
            display_view.setVisibility(View.INVISIBLE);
        }
        current_display_numerical = 0;
        Toast.makeText(MainActivity.this,
                R.string.shuffled,
                Toast.LENGTH_SHORT).show();
    }

    public void shuffleFactionCard(View view){
        faction.shuffleCards();
        ImageButton card = findViewById(R.id.card_faction);
        card.setImageResource(R.drawable.faction_back);
        for(int display:display_faction){
            ImageView display_view = findViewById(display);
            display_view.setVisibility(View.INVISIBLE);
        }
        current_display_faction = 0;
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
