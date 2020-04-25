package cl.humankind.humankindcounter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private SQLiteOpenHelper sanctuaries;
    private PopupWindow popupPointsWindow;
    private PopupMenu menuSanctuaries;
    private Button structurePoints;
    private Button willPoints;
    private HashMap<Integer, Sanctuary> options;
    private HashMap<Integer, Sanctuary> candidates;
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
        super.onCreate(savedInstanceState);
        setCache();
        setContentView(R.layout.activity_main);
        structurePoints = findViewById(R.id.structure_points);
        willPoints = findViewById(R.id.will_points);
        candidates = new HashMap<>();
        setOptions(5);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private void setOptions(int toShow){
        options = new HashMap<>();
        ImageButton sanctuaryButton = findViewById(R.id.sanctuary);
        menuSanctuaries = new PopupMenu(MainActivity.this, sanctuaryButton);
        menuSanctuaries.getMenuInflater().inflate(R.menu.select_sanctuary,
                menuSanctuaries.getMenu());
        for(int i = 0; i < toShow || !sanctuaryCache.cacheEmpty(); i++) {
            MenuItem item = menuSanctuaries.getMenu().findItem(cache[i]);
            Sanctuary toAdd = sanctuaryCache.getSanctuary();
            options.put(cache[i], toAdd);
            if (toAdd != null) {
                item.setTitle(toAdd.getName());
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
        SQLiteDatabase database = sanctuaries.getWritableDatabase();
        LinearLayout main = findViewById(R.id.main);
        main.setBackgroundResource(sanctuary.getBackground());
        ImageView color = findViewById(R.id.sanctuary_color);
        color.setImageResource(sanctuary.getColor());
        int used = sanctuary.getIndex();
        if (used != -1) {
            Cursor cursor = database.rawQuery(String.format(Locale.US,
                    "SELECT \"index\" FROM user_preference WHERE \"index\" = %d;", used),
                    null);
            String sql;
            double timestamp = System.currentTimeMillis() / 1000.0;
            Log.d(msg, "Timestamp to register: " + timestamp);
            if(cursor.moveToNext()){
                Log.d(msg, "Increasing uses of " + used);
                sql = String.format(Locale.US,
                        "UPDATE user_preference SET uses = uses + 1, timestamp = %f \n" +
                                "WHERE \"index\" = %d;",
                        timestamp, used);
            } else {
                Log.d(msg, "Registering " + used);
                sql = String.format(Locale.US,
                        "INSERT INTO user_preference(\"index\", uses, timestamp) \n" +
                                "VALUES (%d, 1, %f);",
                        used, timestamp);
            }
            database.execSQL(sql);
            cursor.close();
            database.close();
        } else Log.d(msg, "Sanctuary set, not picked up");
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
        setCache();
        setOptions(5);
    }

    private void lookForPopup(final View view){
        if (this.isFinishing()){
            Log.d(msg, "Process is finishing");
            return;
        }
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popup = inflater.inflate(R.layout.look_for, null);
        if (popup == null) return;
        final PopupWindow lookForSanctuary = new PopupWindow(
                popup, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true
        );
        lookForSanctuary.showAtLocation(view, Gravity.CENTER, 0, 0);
        Button ok_button = popup.findViewById(R.id.ok_button);
        final Spinner edition = popup.findViewById(R.id.select_edition);
        final Spinner enterId = popup.findViewById(R.id.select_id);
        final Spinner enterName = popup.findViewById(R.id.select_name);
        final String[] edition_selected = new String[1];
        final HashMap[] coordinates = new HashMap[1];
        ArrayAdapter<CharSequence> adapterEdition = ArrayAdapter.createFromResource(this,
                R.array.edition_arrays, android.R.layout.simple_spinner_item);
        adapterEdition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edition.setAdapter(adapterEdition);
        edition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        edition_selected[0] = "ev";
                        break;
                    case 1:
                        edition_selected[0] = "dv";
                        break;
                    case 2:
                        edition_selected[0] = "su";
                        break;
                    case 3:
                        edition_selected[0] = "ra";
                        break;
                } Log.d(msg, "Edition selected: " + edition_selected[0]);
                coordinates[0] = fillAutocomplete(edition_selected[0], enterId, enterName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        final int[] id_selected = new int[1];
        id_selected[0] = -1;
        final String[] name_selected = new String[1];
        name_selected[0] = "";
        enterId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object got = parent.getItemAtPosition(position);
                if (got != null){
                    id_selected[0] = (int) got;
                    enterName.setSelection(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                id_selected[0] = -1;
                Log.d(msg, "No id selected");
            }
        });
        enterName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object got = parent.getItemAtPosition(position);
                if (got != null){
                    name_selected[0] = (String) got;
                    enterId.setSelection(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                name_selected[0] = "";
                Log.d(msg, "No id selected");
            }
        });
        ok_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer idReceived;
                Log.d(msg, "Sanctuary name selected: " + name_selected[0]);
                if (coordinates[0] == null){
                    Toast.makeText(
                            MainActivity.this,
                            R.string.sanctuary_error,
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }
                idReceived = (Integer) coordinates[0].get(name_selected[0]);
                if (idReceived != null) {
                    Log.d(msg, "Sanctuary id selected: " + idReceived);
                } else if (id_selected[0] != -1) {
                    Log.d(msg, "Sanctuary id selected: " + id_selected[0]);
                    idReceived = id_selected[0];
                } else {
                    Toast.makeText(
                            MainActivity.this,
                            R.string.sanctuary_no_picked_up,
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                } sanctuary = new MainSanctuary(candidates.get(idReceived));
                lookForSanctuary.dismiss();
                settingSanctuary();
            }
        });
    }

    private void setUpPopup(final View view){
        if (this.isFinishing()){
            Log.d(msg, "Process is finishing");
            return;
        }
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popup = inflater.inflate(R.layout.set_up, null);
        if (popup == null) return;
        final PopupWindow setUpSanctuary = new PopupWindow(
                popup, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true
        );
        setUpSanctuary.showAtLocation(view, Gravity.CENTER, 0, 0);
        Button ok_button = popup.findViewById(R.id.ok_button);
        final Spinner faction = popup.findViewById(R.id.select_faction);
        final String[] faction_selected = {"none"};
        ArrayAdapter<CharSequence> adapterFaction = ArrayAdapter.createFromResource(this,
                R.array.faction_arrays, android.R.layout.simple_spinner_item);
        adapterFaction.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        faction.setAdapter(adapterFaction);
        faction.setSelection(4);
        faction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        faction_selected[0] = "chimera";
                        break;
                    case 1:
                        faction_selected[0] = "abysmal";
                        break;
                    case 2:
                        faction_selected[0] = "corpo";
                        break;
                    case 3:
                        faction_selected[0] = "acracia";
                        break;
                    default:
                        faction_selected[0] = "none";
                        break;
                } Log.d(msg, "Faction selected: " + faction_selected[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                faction_selected[0] = "none";
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

    private HashMap<String, Integer> fillAutocomplete(String edition, Spinner enterId,
                                                    Spinner enterName){
        SQLiteDatabase database = sanctuaries.getReadableDatabase();
        String sql = "SELECT \"index\", \"id\", name, faction, structure, will \n" +
                "FROM sanctuaries WHERE edition = \"" + edition + "\";";
        Cursor cursor = database.rawQuery(sql, null);
        List<Integer> ids = new ArrayList<>();
        List<String> names = new ArrayList<>();
        candidates.clear();
        HashMap<String, Integer> coordinate = new HashMap<>();
        coordinate.put("", -1);
        int found = 0;
        while (cursor.moveToNext()){
            found++;
            Integer anId = cursor.getInt(cursor.getColumnIndex("id"));
            String sanctuaryName = cursor.getString(cursor.getColumnIndex("name"));
            ids.add(anId);
            names.add(sanctuaryName);
            coordinate.put(sanctuaryName, anId);
            String faction = cursor.getString(cursor.getColumnIndex("faction"));
            Log.d(msg, "Faction found: " + faction);
            candidates.put(anId, new Sanctuary(
                    cursor.getInt(cursor.getColumnIndex("index")),
                    sanctuaryName,
                    cursor.getInt(cursor.getColumnIndex("structure")),
                    cursor.getInt(cursor.getColumnIndex("will")),
                    faction
            ));
        } cursor.close();
        database.close();
        Log.d(msg, "Database found " + found + " sanctuaries");
        ArrayAdapter<Integer> adapterId = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ids);
        enterId.setAdapter(adapterId);
        ArrayAdapter<String> adapterName = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, names);
        enterName.setAdapter(adapterName);
        return coordinate;
    }

    private void setCache(){
        sanctuaryCache = new SanctuaryCache();
        sanctuaries = new SanctuariesDatabaseHelper(this);
        SQLiteDatabase database = sanctuaries.getReadableDatabase();
        String get_cache_sql = "SELECT user_preference.\"index\", name, structure, will, faction\n" +
                "FROM user_preference JOIN sanctuaries\n" +
                "ON user_preference.\"index\" = sanctuaries.\"index\"\n" +
                "ORDER BY timestamp DESC LIMIT 1;";
        Cursor cursor = database.rawQuery(get_cache_sql, null);
        int index_to_exclude = 1000;
        if (cursor.moveToNext()){
            Log.d(msg, "Sanctuary on cache: " +
                    cursor.getString(cursor.getColumnIndex("name")));
            index_to_exclude = cursor.getInt(cursor.getColumnIndex("index"));
            sanctuaryCache.addSanctuary(
                    index_to_exclude,
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getInt(cursor.getColumnIndex("structure")),
                    cursor.getInt(cursor.getColumnIndex("will")),
                    cursor.getString(cursor.getColumnIndex("faction")));
        }
        get_cache_sql = "SELECT sanctuaries.\"index\", name, structure, will, faction\n" +
                "FROM user_preference JOIN sanctuaries\n " +
                "ON user_preference.\"index\" = sanctuaries.\"index\"\n " +
                "WHERE user_preference.\"index\" != " + index_to_exclude + " \n " +
                "ORDER BY uses DESC, timestamp DESC LIMIT 4;";
        cursor = database.rawQuery(get_cache_sql, null);
        while (cursor.moveToNext()) {
            String name_log = cursor.getString(cursor.getColumnIndex("name"));
            Log.d(msg, "Sanctuary on cache: " + name_log);
            sanctuaryCache.addSanctuary(
                    cursor.getInt(cursor.getColumnIndex("index")),
                    name_log,
                    cursor.getInt(cursor.getColumnIndex("structure")),
                    cursor.getInt(cursor.getColumnIndex("will")),
                    cursor.getString(cursor.getColumnIndex("faction"))
            );
        } cursor.close();
        database.close();
    }

    /*
    * To
     */

}
