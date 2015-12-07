package com.itisdancing.Sokoban;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

public class SokobanGame extends Activity implements View.OnClickListener
{
    SokobanView sokoban_view;
    TextView status_bar;
    Button undo_button;
    boolean soundOn;
    MediaPlayer mp;
    //MenuItem soundB;

    private static final String PREF_SAVED_GAME = "saved_game";
    private static final String PREF_CURRENT_LEVEL = "current_level";
    protected static final String KEY_LEVEL = "level";
    protected static final int LEVEL_CONTINUE = -1;
    public static final String SHARE_PREF_NAME = "pref";
    public static final String PASSED_LEVEL = "passed_level";
    public static final String PERF_CURRENT_MOVE = "current_move";
    public static final String PREF_SOUND = "sound";
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        ViewGroup frame;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        frame = (ViewGroup) findViewById(R.id.game_frame);
        status_bar = (TextView) findViewById(R.id.game_status_bar);
        undo_button = (Button) findViewById(R.id.undo_button);
        undo_button.setOnClickListener(this);
        soundOn = getSavedSound();
        mp = MediaPlayer.create(this, R.raw.sample14);
        sokoban_view = new SokobanView(this, getIntent().getIntExtra(KEY_LEVEL, 0));
        sokoban_view.setFocusable(true);
        sokoban_view.setFocusableInTouchMode(true);
        frame.addView(sokoban_view);
        /*soundB = (MenuItem)findViewById(R.id.sound);
        soundB.setTitle("dfggd");*/
    }


    public void setSoundOn(boolean on) {
        this.soundOn = on;
        getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(PREF_SOUND, on).commit();
    }

    public boolean getSoundOn() {return soundOn;}

    public boolean getSavedSound() {
        return getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).getBoolean(PREF_SOUND, true);
    }

    /*public void setString(MenuItem mi) {
        //MenuItem mi = (MenuItem)findViewById(R.id.sound);
        if (getSavedSound())  mi.setTitle("Sound On");
        else  mi.setTitle("Sound Off");
    }*/

    @Override
    protected void onPause() {
      super.onPause();
      saveGame();
    }

   /*@Override
    protected void onResume() {
      super.onResume();
      sokoban_view.loadGame(-1);
    }*/

    @Override
    protected void onDestroy() {
      super.onDestroy();
      saveGame();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.undo_button) {
            sokoban_view.undo();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
      MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
      return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
        case R.id.retry_level:
          sokoban_view.retryLevel();
          return true;
        case R.id.sound:
            setSoundOn(!getSoundOn());
            d("sound: " + getSoundOn());
            if (getSoundOn())
                item.setTitle("Sound On");
            else
                item.setTitle("Sound Off");
           // setString((MenuItem)findViewById(R.id.sound));
          return true;
      }
      return false;
    }

    public String getSavedGame() {
      return getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).getString(PREF_SAVED_GAME, null);
    }

    public int getSavedLevel() {
      return getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).getInt(PREF_CURRENT_LEVEL, 0);
    }

    public int getSavedMoves() {
        return getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE).getInt(PERF_CURRENT_MOVE, 0);
    }

    public void setStatusBar(String message) {
      status_bar.setText(message);
    }

    protected void saveGame() {
      d("Saving game");
        SharedPreferences pref = getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_SAVED_GAME, sokoban_view.getArena().serialize());
        editor.putInt(PREF_CURRENT_LEVEL, sokoban_view.getCurrentLevel());
        editor.putInt(PERF_CURRENT_MOVE, sokoban_view.getCurrentMoves());
        editor.commit();
    }

    public void putPassedLevel() {
        SharedPreferences pref = getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PASSED_LEVEL, sokoban_view.getCurrentLevel());
        editor.commit();
    }

    public int getPassedLevel() {
        SharedPreferences pref = getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(PASSED_LEVEL, 0);
    }

    protected void d(String message) {
      Log.d("SOKO", message);
    }

    public void playSound() {
        //mp.stop();
        if (soundOn) {
            mp.start();
        }
    }
}
