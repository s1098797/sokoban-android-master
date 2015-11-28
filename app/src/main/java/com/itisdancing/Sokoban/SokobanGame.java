package com.itisdancing.Sokoban;

/* ---- this class is the game page, generates SokobanView and menu---- */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.util.Log;

public class SokobanGame extends Activity
{
    SokobanView sokoban_view;
    TextView status_bar;

    private static final String PREF_SAVED_GAME = "saved_game";
    private static final String PREF_CURRENT_LEVEL = "current_level";
    protected static final String KEY_LEVEL = "level";
    protected static final int LEVEL_CONTINUE = -1;
    public static final String SHARE_PREF_NAME = "pref";  /* -- 21 nov -- */
    public static final String PASSED_LEVEL = "passed_level";  /* -- 21 nov -- */
    public static final String PERF_CURRENT_MOVE = "current_move";  /* -- 28 nov -- */
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        ViewGroup frame;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        frame = (ViewGroup) findViewById(R.id.game_frame);
        status_bar = (TextView) findViewById(R.id.game_status_bar);
        sokoban_view = new SokobanView(this, getIntent().getIntExtra(KEY_LEVEL, 0));
        sokoban_view.setFocusable(true);
        sokoban_view.setFocusableInTouchMode(true);
        frame.addView(sokoban_view);
    }
    
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
        case R.id.skip_level:
          sokoban_view.skipLevel();
          return true;
        case R.id.win_level:
          sokoban_view.instantWin();
          return true;
      }
      return false;
    }

    public String getSavedGame() {
      return getPreferences(MODE_PRIVATE).getString(PREF_SAVED_GAME, null);
    }

    public int getSavedLevel() {
      return getPreferences(MODE_PRIVATE).getInt(PREF_CURRENT_LEVEL, 0);
    }

    public int getSavedMoves() { /* 28-nov */
        return getPreferences(MODE_PRIVATE).getInt(PERF_CURRENT_MOVE, 0);
    }

    public void setStatusBar(String message) {
      status_bar.setText(message);
    }

    protected void saveGame() {
      d("Saving game");
      getPreferences(MODE_PRIVATE).edit().
        putString(PREF_SAVED_GAME, sokoban_view.getArena().serialize()).
        putInt(PREF_CURRENT_LEVEL, sokoban_view.getCurrentLevel()).
        putInt(PERF_CURRENT_MOVE, sokoban_view.getCurrentMoves()).
        commit();
        /*SharedPreferences pref = getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_SAVED_GAME, sokoban_view.getArena().serialize());
        editor.putInt(PREF_CURRENT_LEVEL, sokoban_view.getCurrentLevel());
        editor.putInt(PERF_CURRENT_MOVE, sokoban_view.getCurrentMoves());
        editor.commit();*/
    }

    public void putPassedLevel() {     /* 21-nov */
        SharedPreferences pref = getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PASSED_LEVEL, sokoban_view.getCurrentLevel());
        editor.commit();
    }

    public int getPassedLevel() {     /* 21-nov */
        SharedPreferences pref = getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(PASSED_LEVEL, 0);
    }

    protected void d(String message) {
      Log.d("SOKO", message);
    }

}
