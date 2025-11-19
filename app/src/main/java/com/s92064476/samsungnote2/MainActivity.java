package com.s92064476.samsungnote2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView; // NEW IMPORT
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etDays, etHours, etMinutes;
    private AutoCompleteTextView etDescription; // CHANGED TYPE
    private Spinner spinnerAccount;
    private RecyclerView recyclerView;
    private AlarmAdapter adapter;
    private List<AlarmModel> alarmList;
    private TextView tvCurrentTime;
    private Handler handler = new Handler(Looper.getMainLooper());

    private List<String> accountNames;
    private String currentAccountKey = "tharinduxd";
    private static final int STICKY_NOTIF_ID = 7777;

    // MASTER LIST OF COC ITEMS (Updated with TH17 & TH18 Content)
    private static final String[] COC_ITEMS = {
            // --- TOWN HALL 18 NEW (Nov 2025) ---
            "Guardian: Smasher", "Guardian: Longshot", "Super Wizard Tower", "Meteor Golem", "Totem Spell",
            "Meteorite Builder Skin", "Fancy Shop",

            // --- TOWN HALL 17 NEW (Late 2024) ---
            "Town Hall 17 (Inferno Artillery)", "Firespitter", "Hero Hall", "Helper Hut",
            "Minion Prince", "Thrower", "Revive Spell", "Giga Bomb",
            "Ricochet Cannon", "Multi-Archer Tower", "Multi-Gear Tower",

            // --- CORE BUILDINGS ---
            "Town Hall", "Clan Castle", "Laboratory", "Pet House", "Blacksmith", "Spell Factory", "Dark Spell Factory", "Workshop",

            // --- DEFENSES ---
            "Eagle Artillery", "Monolith", "Scattershot", "Inferno Tower", "X-Bow",
            "Air Defense", "Hidden Tesla", "Bomb Tower", "Wizard Tower", "Air Sweeper", "Builder's Hut", "Spell Tower",
            "Cannon", "Archer Tower", "Mortar", "Wall",

            // --- HEROES ---
            "Barbarian King", "Archer Queen", "Grand Warden", "Royal Champion", "Battle Copter", "Battle Machine",

            // --- PETS ---
            "Spirit Fox", "Angry Jelly", "Phoenix", "Diggy", "Frosty", "Poison Lizard",
            "L.A.S.S.I", "Electro Owl", "Mighty Yak", "Unicorn",

            // --- ELIXIR TROOPS ---
            "Root Rider", "Electro Titan", "Dragon Rider", "Yeti", "Electro Dragon", "Miner", "Baby Dragon",
            "Dragon", "Healer", "P.E.K.K.A", "Wizard", "Balloon", "Wall Breaker", "Goblin", "Giant", "Archer", "Barbarian",

            // --- DARK ELIXIR TROOPS ---
            "Druid", "Apprentice Warden", "Headhunter", "Ice Golem", "Bowler", "Lava Hound", "Witch", "Golem", "Valkyrie", "Hog Rider", "Minion",

            // --- SUPER TROOPS ---
            "Super Dragon", "Super Bowler", "Super Witch", "Super Minion", "Super Hog Rider", "Super Wall Breaker", "Sneaky Goblin", "Super Barbarian", "Super Archer", "Super Giant", "Super Wizard", "Ice Hound", "Inferno Dragon", "Rocket Balloon",

            // --- SIEGE MACHINES ---
            "Battle Drill", "Flame Flinger", "Log Launcher", "Siege Barracks", "Stone Slammer", "Battle Blimp", "Wall Wrecker",

            // --- SPELLS ---
            "Overgrowth Spell", "Recall Spell", "Invisibility Spell", "Clone Spell", "Poison Spell", "Bat Spell", "Skeleton Spell",
            "Earthquake Spell", "Haste Spell", "Freeze Spell", "Jump Spell", "Rage Spell", "Healing Spell", "Lightning Spell",

            // --- TRAPS ---
            "Tornado Trap", "Skeleton Trap", "Seeking Air Mine", "Air Bomb", "Giant Bomb", "Spring Trap", "Bomb",

            // --- RESOURCES ---
            "Dark Elixir Storage", "Elixir Storage", "Gold Storage", "Dark Elixir Drill", "Elixir Collector", "Gold Mine"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        etDays = findViewById(R.id.etDays);
        etHours = findViewById(R.id.etHours);
        etMinutes = findViewById(R.id.etMinutes);
        etDescription = findViewById(R.id.etDescription); // Now finds AutoCompleteTextView
        spinnerAccount = findViewById(R.id.spinnerAccount);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        Button btnAdd = findViewById(R.id.btnAddAlarm);
        Button btnManage = findViewById(R.id.btnManageAccounts);
        recyclerView = findViewById(R.id.recyclerView);

        // --- NEW: SETUP AUTOCOMPLETE ---
        // We reuse 'spinner_item' layout so the suggestions are also Orange & Bold
        ArrayAdapter<String> autoFillAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, COC_ITEMS);
        etDescription.setAdapter(autoFillAdapter);
        // -------------------------------

        // Setup Accounts
        loadAccountList();
        setupSpinner();

        // Lists
        alarmList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Listeners
        btnAdd.setOnClickListener(v -> createNewAlarm());
        btnManage.setOnClickListener(v -> showManageAccountsDialog());

        startClock();
        checkExactAlarmPermission();
    }

    // ... (The rest of the code remains EXACTLY the same as before) ...
    // ... Copy everything below createNewAlarm() from the previous code ...

    private void loadAccountList() {
        SharedPreferences prefs = getSharedPreferences("CocTimer_Settings", MODE_PRIVATE);
        String json = prefs.getString("accounts", null);
        if (json == null) {
            accountNames = new ArrayList<>();
            accountNames.add("tharinduxd");
            accountNames.add("KOTA");
            saveAccountList();
        } else {
            accountNames = new Gson().fromJson(json, new TypeToken<ArrayList<String>>(){}.getType());
        }
    }

    private void saveAccountList() {
        SharedPreferences prefs = getSharedPreferences("CocTimer_Settings", MODE_PRIVATE);
        prefs.edit().putString("accounts", new Gson().toJson(accountNames)).apply();
    }

    private void setupSpinner() {
        ArrayAdapter<String> accAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, accountNames);
        accAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerAccount.setAdapter(accAdapter);

        int index = accountNames.indexOf(currentAccountKey);
        if (index >= 0) spinnerAccount.setSelection(index);

        spinnerAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentAccountKey = accountNames.get(position);
                loadAlarms();
                updateStickyNotification();
                adapter = new AlarmAdapter(alarmList, pos -> deleteAlarm(pos));
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showManageAccountsDialog() {
        String[] options = {"Add New Account", "Rename Current Account", "Delete Current Account"};
        new AlertDialog.Builder(this)
                .setTitle("Manage Accounts")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) showTextInputDialog("Add Account", "", this::addNewAccount);
                    else if (which == 1) showTextInputDialog("Rename Account", currentAccountKey, this::renameCurrentAccount);
                    else if (which == 2) deleteCurrentAccount();
                })
                .show();
    }

    private void addNewAccount(String name) {
        if (name.isEmpty() || accountNames.contains(name)) return;
        accountNames.add(name);
        saveAccountList();
        setupSpinner();
        spinnerAccount.setSelection(accountNames.size() - 1);
        Toast.makeText(this, "Account Added", Toast.LENGTH_SHORT).show();
    }

    private void renameCurrentAccount(String newName) {
        if (newName.isEmpty() || accountNames.contains(newName)) return;
        SharedPreferences oldPrefs = getSharedPreferences("CocTimer_" + currentAccountKey, MODE_PRIVATE);
        SharedPreferences newPrefs = getSharedPreferences("CocTimer_" + newName, MODE_PRIVATE);
        String data = oldPrefs.getString("list", null);
        if (data != null) newPrefs.edit().putString("list", data).apply();
        oldPrefs.edit().clear().apply();
        int index = accountNames.indexOf(currentAccountKey);
        accountNames.set(index, newName);
        saveAccountList();
        currentAccountKey = newName;
        setupSpinner();
        spinnerAccount.setSelection(index);
        Toast.makeText(this, "Account Renamed", Toast.LENGTH_SHORT).show();
    }

    private void deleteCurrentAccount() {
        if (accountNames.size() <= 1) {
            Toast.makeText(this, "Cannot delete the only account", Toast.LENGTH_SHORT).show();
            return;
        }
        getSharedPreferences("CocTimer_" + currentAccountKey, MODE_PRIVATE).edit().clear().apply();
        accountNames.remove(currentAccountKey);
        saveAccountList();
        currentAccountKey = accountNames.get(0);
        setupSpinner();
        Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show();
    }

    interface InputCallback { void onInput(String text); }

    private void showTextInputDialog(String title, String prefill, InputCallback callback) {
        EditText input = new EditText(this);
        input.setText(prefill);
        new AlertDialog.Builder(this).setTitle(title).setView(input)
                .setPositiveButton("OK", (d, w) -> callback.onInput(input.getText().toString().trim()))
                .setNegativeButton("Cancel", null).show();
    }

    private void createNewAlarm() {
        int d = parse(etDays);
        int h = parse(etHours);
        int m = parse(etMinutes);

        if (d == 0 && h == 0 && m == 0) {
            Toast.makeText(this, "Enter time", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar cal = Calendar.getInstance();
        long startTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, d);
        cal.add(Calendar.HOUR_OF_DAY, h);
        cal.add(Calendar.MINUTE, m);

        long triggerTime = cal.getTimeInMillis();
        long uniqueId = System.currentTimeMillis();
        String desc = etDescription.getText().toString();
        if (desc.isEmpty()) desc = "Upgrade";

        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault());
        String formatted = "Due: " + sdf.format(cal.getTime());

        AlarmModel newAlarm = new AlarmModel(uniqueId, desc, triggerTime, startTime, formatted);

        alarmList.add(newAlarm);
        saveAlarms();
        updateStickyNotification();
        adapter.notifyDataSetChanged();

        setSystemAlarm(uniqueId, triggerTime, desc);

        etDays.setText(""); etHours.setText(""); etMinutes.setText(""); etDescription.setText("");
        Toast.makeText(this, "Timer Started!", Toast.LENGTH_SHORT).show();
    }

    private void setSystemAlarm(long id, long timeInMillis, String desc) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intentFinal = new Intent(this, AlarmReceiver.class);
        intentFinal.putExtra("description", desc);
        intentFinal.putExtra("accountName", currentAccountKey);
        intentFinal.putExtra("isPreAlert", false);
        PendingIntent piFinal = PendingIntent.getBroadcast(this, (int)id, intentFinal, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(timeInMillis, piFinal);
        am.setAlarmClock(info, piFinal);

        long fiveMinsBefore = timeInMillis - (5 * 60 * 1000);
        if (fiveMinsBefore > System.currentTimeMillis()) {
            Intent intentPre = new Intent(this, AlarmReceiver.class);
            intentPre.putExtra("description", desc);
            intentPre.putExtra("accountName", currentAccountKey);
            intentPre.putExtra("isPreAlert", true);
            PendingIntent piPre = PendingIntent.getBroadcast(this, (int)id + 1, intentPre, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, fiveMinsBefore, piPre);
        }
    }

    private void deleteAlarm(int position) {
        if (position < 0 || position >= alarmList.size()) return;
        AlarmModel item = alarmList.get(position);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);

        // Cancel Final
        PendingIntent pi1 = PendingIntent.getBroadcast(this, (int)item.getId(), intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE);
        if (pi1 != null) { am.cancel(pi1); pi1.cancel(); }

        // Cancel Pre-Alert
        PendingIntent pi2 = PendingIntent.getBroadcast(this, (int)item.getId() + 1, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE);
        if (pi2 != null) { am.cancel(pi2); pi2.cancel(); }

        alarmList.remove(position);
        saveAlarms();
        updateStickyNotification();

        // --- SAFETY FIX ---
        adapter.notifyItemRemoved(position);
        // This line ensures items below the deleted one update their index immediately
        adapter.notifyItemRangeChanged(position, alarmList.size());
    }

    private void updateStickyNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (alarmList.isEmpty()) { nm.cancel(STICKY_NOTIF_ID); return; }

        AlarmModel nextAlarm = null;
        long minTime = Long.MAX_VALUE;
        long now = System.currentTimeMillis();

        for (AlarmModel m : alarmList) {
            if (m.getTargetTimeInMillis() > now && m.getTargetTimeInMillis() < minTime) {
                minTime = m.getTargetTimeInMillis();
                nextAlarm = m;
            }
        }

        if (nextAlarm == null) { nm.cancel(STICKY_NOTIF_ID); return; }

        String channelId = "sticky_status";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Next Upgrade Status", NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(channel);
        }

        Intent openApp = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, openApp, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
                .setContentTitle("Next (" + currentAccountKey + "): " + nextAlarm.getDescription())
                .setContentText("Finishes at: " + nextAlarm.getFormattedTime())
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pi);

        nm.notify(STICKY_NOTIF_ID, builder.build());
    }

    private void saveAlarms() {
        SharedPreferences prefs = getSharedPreferences("CocTimer_" + currentAccountKey, MODE_PRIVATE);
        prefs.edit().putString("list", new Gson().toJson(alarmList)).apply();
    }

    private void loadAlarms() {
        SharedPreferences prefs = getSharedPreferences("CocTimer_" + currentAccountKey, MODE_PRIVATE);
        String json = prefs.getString("list", null);
        if (json == null) alarmList = new ArrayList<>();
        else alarmList = new Gson().fromJson(json, new TypeToken<ArrayList<AlarmModel>>() {}.getType());
    }

    private int parse(EditText et) {
        try { return Integer.parseInt(et.getText().toString()); }
        catch (Exception e) { return 0; }
    }

    private void startClock() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d - hh:mm:ss a", Locale.getDefault());
                tvCurrentTime.setText(sdf.format(new Date()));
                if (adapter != null) adapter.notifyDataSetChanged();
                handler.postDelayed(this, 60000);
            }
        });
    }

    private void checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!am.canScheduleExactAlarms()) {
                startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
            }
        }
    }
}