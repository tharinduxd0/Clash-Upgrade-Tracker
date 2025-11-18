package com.s92064476.samsungnote2;

import android.app.KeyguardManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AlarmTriggerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (km != null) km.requestDismissKeyguard(this, null);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alarm_trigger);

        if (getResources().getDisplayMetrics() != null) {
            getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
        }

        TextView tvDesc = findViewById(R.id.tvAlarmDesc);
        TextView tvAccount = findViewById(R.id.tvAccountName); // NEW
        Button btnStop = findViewById(R.id.btnStop);

        String desc = getIntent().getStringExtra("description");
        String account = getIntent().getStringExtra("accountName"); // GET DATA

        tvDesc.setText(desc != null ? desc : "Timer Finished");
        tvAccount.setText("Account: " + (account != null ? account : "Unknown"));

        playAlarmSound();

        btnStop.setOnClickListener(v -> {
            stopAlarm();
            finish();
        });
    }

    private void playAlarmSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (notification == null) notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (am != null) am.setStreamVolume(AudioManager.STREAM_ALARM, am.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, notification);
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();

            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {0, 1000, 1000};
            if (vibrator != null) vibrator.vibrate(VibrationEffect.createWaveform(pattern, 0));

        } catch (Exception e) { e.printStackTrace(); }
    }

    private void stopAlarm() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception e) { e.printStackTrace(); }
            finally { mediaPlayer = null; }
        }
        if (vibrator != null) {
            try { vibrator.cancel(); } catch (Exception e) { e.printStackTrace(); }
            finally { vibrator = null; }
        }
    }

    @Override
    protected void onDestroy() {
        stopAlarm();
        super.onDestroy();
    }
}