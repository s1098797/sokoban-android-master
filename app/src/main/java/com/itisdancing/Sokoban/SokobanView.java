package com.itisdancing.Sokoban;

/* ----  this java produces the view: draw maps, set codes for user to control(play) and make reactions,   ---- */
/* ---- there are problems that showing the arena in the app , I don't know whether this java or SokobanArena.java contain wrong codes ---- */
/* ----  the app cannot perform level select, I don't know whether the codes in loadGame() are the main problem  ---- */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import java.io.StringReader;

public class SokobanView extends View {
  private static final int TILE_SIZE = 20;
  private Drawable sokoban;
  private Drawable wall;
  private Drawable crate;
  private Drawable goal;
  private Drawable floor;
  private int tiles_wide;
  private int tiles_high;
  private boolean tall;
  private int side_border_width;
  private int side_border_height;
  private int arena_x_lower_bound;
  private int arena_y_lower_bound;
  private int current_level;
  private Point drag_start;
  private Point drag_stop;
  private MapList map_list;
  private SokobanArena arena;
  private PersistentStore store;

  public SokobanView(SokobanGame context) {
    super(context);
    store = new PersistentStore(context);
    floor = getResources().getDrawable(R.drawable.floor);
    sokoban = getResources().getDrawable(R.drawable.sokoban);
    wall = getResources().getDrawable(R.drawable.wall);
    crate = getResources().getDrawable(R.drawable.crate);
    goal = getResources().getDrawable(R.drawable.goal);
    map_list = new MapList(getResources().openRawResource(R.raw.sokoban));
    drag_start = new Point();
    drag_stop = new Point();
  }

  public SokobanView(SokobanGame context, int level) {
    this(context);
    current_level = level;
    loadGame();
  }

  public SokobanView(Context context, AttributeSet attributes) {
    this((SokobanGame) context);
  }

  public SokobanArena getArena() { return arena; } 
  public int getCurrentLevel() { return current_level; }

  public void retryLevel() {
    selectMap(current_level);
  }
  public void skipLevel() {
    nextLevel();
  }
  public void instantWin() { // for debugging
    levelWon();
    nextLevel();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    updateStatusBar();
    int a_wid    = arena.getMapWidth();
    int a_height = arena.getMapHeight();
    Drawable tile;

    for(int x = 0; x < a_wid; x++) {
      for(int y = 0; y < a_height; y++) {
        int ix; int iy;
        if(tall) {
          ix = (arena_x_lower_bound + arena.getMapHeight()) - y;
          iy = arena_y_lower_bound + x;
        } else {
          ix = x + arena_x_lower_bound;
          iy = y + arena_y_lower_bound;
        }
        tile = tileForLocation(x, y);
        tile.setBounds(ix * TILE_SIZE, iy * TILE_SIZE, ix * TILE_SIZE + TILE_SIZE, iy * TILE_SIZE + TILE_SIZE);
        tile.draw(canvas);
      }
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    tiles_wide = w / TILE_SIZE;
    tiles_high = h / TILE_SIZE;
    tall = tiles_high > tiles_wide;
    if (tall) {
      side_border_width = (tiles_wide - arena.getMapHeight()) / 2;
      side_border_height = (tiles_high - arena.getMapWidth()) / 2;
      arena_x_lower_bound = side_border_width;
      arena_y_lower_bound = side_border_height;
    } else {
      side_border_width = (tiles_wide - arena.getMapWidth()) / 2;
      side_border_height = (tiles_high - arena.getMapHeight()) / 2;
      arena_x_lower_bound = side_border_width;
      arena_y_lower_bound = side_border_height;
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch(event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        drag_start.set((int) event.getX(), (int) event.getY());
        break;
      case MotionEvent.ACTION_UP:
        drag_stop.set((int) event.getX(), (int) event.getY());
        touchMove();
        break;
    }
    return true;
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    Rect invalid;
    switch (keyCode) {
    case KeyEvent.KEYCODE_DPAD_UP:
      doMove(SokobanArena.NORTH);
      break;
    case KeyEvent.KEYCODE_DPAD_DOWN:
      doMove(SokobanArena.SOUTH);
      break;
    case KeyEvent.KEYCODE_DPAD_RIGHT:
      doMove(SokobanArena.EAST);
      break;
    case KeyEvent.KEYCODE_DPAD_LEFT:
      doMove(SokobanArena.WEST);
      break;
    default:
      return super.onKeyDown(keyCode, event);
    }
    return true;
  }

  protected void touchMove() {
    int delta_x = drag_stop.x - drag_start.x;
    int delta_y = drag_stop.y - drag_start.y;
    if (Math.abs(delta_x) < 10 && Math.abs(delta_y) < 10) {
      return; // not enough of a move
    }
    if(Math.abs(delta_x) > Math.abs(delta_y)) { // x move
      if(delta_x < 0) {
        doMove(SokobanArena.WEST);
      } else {
        doMove(SokobanArena.EAST);
      }
    } else { // y move
      if(delta_y < 0) {
        doMove(SokobanArena.NORTH);
      } else {
        doMove(SokobanArena.SOUTH);
      }
    }
  }

  protected void doMove(int direction) {
    Rect invalid;

    if (tall) {
      switch(direction) {
        case SokobanArena.SOUTH: direction = SokobanArena.EAST; break;
        case SokobanArena.NORTH: direction = SokobanArena.WEST; break;
        case SokobanArena.EAST: direction = SokobanArena.NORTH; break;
        case SokobanArena.WEST: direction = SokobanArena.SOUTH; break;
      }
    }

    arena.moveMan(direction);
    /*invalid = arena.lastAffectedArea();
    invalid.set(
      (invalid.left   + arena_x_lower_bound) * TILE_SIZE,
      (invalid.top    + arena_y_lower_bound) * TILE_SIZE,
      (invalid.right  + arena_x_lower_bound + 1) * TILE_SIZE,
      (invalid.bottom + arena_y_lower_bound + 1) * TILE_SIZE
    );
    invalidate(invalid); */
    invalidate();
    updateStatusBar();

    if(arena.gameWon()) {
      levelWon();
      if (current_level > ((SokobanGame) getContext()).getSaveLevel())  /* 21-nov */
          ((SokobanGame) getContext()).putSaveLevel();   /* 21-nov */
      AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
      alert.setCancelable(false);
      alert.setMessage("Next: go to next level; Back: back to homepage");
      alert.setTitle("Congraduation!");
      alert.setPositiveButton("Next", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          nextLevel();
        }
      });
      alert.setNegativeButton("Back", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
          d("" + ((SokobanGame) getContext()).getSavedLevel() + 1);
          selectMapLoad(current_level + 1); /* -- codes for loading next level for continue 19-nov -- */
          ((SokobanGame) getContext()).finish();
        }
      });
      alert.show();
      //nextLevel();
    }
  }

  protected Drawable tileForLocation(int x, int y) {
    switch(arena.getTile(x,y)) {
      case SokobanArena.FLOOR: return floor;
      case SokobanArena.SOKOBAN: return sokoban;
      case SokobanArena.WALL: return wall;
      case SokobanArena.GOAL: return goal;
      case SokobanArena.CRATE: return crate;
    }
    return floor;
  }

  public void loadGame(int level) {
    current_level = level;
    loadGame();
  }

  protected void loadGame() {
    d("Loading game.");
    if (current_level == -1) {
      String saved_game = ((SokobanGame) getContext()).getSavedGame();
      if (saved_game == null) {
        d("No saved game found. Starting from first level.");
        current_level = 0;
        loadGame();
      } else {
        current_level = ((SokobanGame) getContext()).getSavedLevel();
        d("Saved game found for level #" + current_level);
        arena = new MapList(new StringReader(saved_game)).selectMap(0);
      }
    } else {
      d("Loading level #" + current_level);
      selectMap(current_level);
    }
  }

  protected void selectMap(int level) {
    arena = map_list.selectMap(level);
    current_level = level;
    updateStatusBar();
    invalidate();
  }

  /* -- selectMapLoad() is new created for loading next level info. for user to continue 19-nov -- */
  protected void selectMapLoad(int level) {
    d(""+level);
    arena = map_list.selectMap(level);
    current_level = level;
    d(""+current_level);
    updateStatusBar();
    invalidate();
    ((SokobanGame) getContext()).saveGame();
  }

  protected void levelWon() {
    store.addScore("main", current_level, arena.getMoves());
  }

  protected void nextLevel() {
    selectMap(current_level + 1);
  }

  protected void d(String message) {
    Log.d("SOKO", message);
  }

  protected void updateStatusBar() {
    ((SokobanGame) getContext()).setStatusBar(
      "Level: " + (current_level + 1) +
      " | Moves: " + arena.getMoves()
    );
  }
}

