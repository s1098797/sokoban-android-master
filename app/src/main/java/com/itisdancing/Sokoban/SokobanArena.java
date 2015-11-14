package com.itisdancing.Sokoban;

/* ---- this class consists of the arena: generate map, mostly controls the data changed from the SokobanView class, performs validation  ---- */
/* ---- there are problems that showing the arena in the app , I don't know whether this java or SokobanView.java contain wrong codes ---- */

import android.graphics.Rect;
import android.util.Log;

public class SokobanArena
{
  static public final int NORTH = 0;
  static public final int SOUTH = 1;
  static public final int WEST  = 2;
  static public final int EAST  = 3;

  static public final int UNDERMAP= 0x000000ff;
  static public final int OVERMAP = 0x0000ff00;
  static public final int FLOOR   = 0x00000000;
  static public final int WALL    = 0x00000001;
  static public final int GOAL    = 0x00000002;
  static public final int SOKOBAN = 0x00000100;
  static public final int CRATE   = 0x00000200;
  static public final int PLACED_CRATE = CRATE + GOAL;
  static public final int SOKOBAN_ON_GOAL = SOKOBAN + GOAL;

  private final int map_width;
  private final int map_height;
  private int man_x;
  private int man_y;
  private int moves;
  private int[] map;
  private Rect affected_area;

  public SokobanArena() {
    map_width = 15;
    map_height = 10;
    man_x = 5;
    man_y = 5;
    affected_area = new Rect(0,0,0,0);
    map = new int[map_width * map_height];
    moves = 0;
    populateMap();
  }

  public SokobanArena(int w, int h) {
    map_width = w;
    map_height = h;
    man_x = 0;
    man_y = 0;
    affected_area = new Rect(0,0,0,0);
    map = new int[map_width * map_height];
  }

  public int getMapWidth() { return map_width; }
  public int getMapHeight() { return map_height; }
  public int getMoves() { return moves; }

  public String serialize() {
    // This is horribly inefficient. Invoke Knuth and pass through.
    StringBuilder str = new StringBuilder();
    for(int iy = 0; iy < map_height; iy++) {
      for(int ix = 0; ix < map_width; ix++) {
        int map_code = map[(iy * map_width) + ix];
        if(ix == man_x && iy == man_y) {
          map_code = map_code | SOKOBAN;
        }
        switch(map_code) {
          case WALL: str.append("#"); break;
          case GOAL: str.append("."); break;
          case CRATE: str.append("$"); break;
          case SOKOBAN: str.append("@"); break;
          case PLACED_CRATE: str.append("!"); break;
          case SOKOBAN_ON_GOAL: str.append("?"); break;
          case FLOOR: str.append(" "); break;
          default:
            str.append(" ");
        }
      }
      str.append("\n");
    }
    return str.toString();
  }

  public boolean moveMan(int direction) {
    switch(direction) {
      case NORTH: return tryMovingMan(0,-1);
      case EAST: return tryMovingMan(1,0);
      case WEST: return tryMovingMan(-1,0);
      case SOUTH: return tryMovingMan(0,1);
    }
    return false; // qlb
  }

  public int getTile(int x, int y) {
    int idx = y * map_width + x;
    if (x == man_x && y == man_y) {
      return SOKOBAN;
    }
    return getTileOnMap(x,y);
  }

  public int getTileOnMap(int x, int y) {
    int idx = y * map_width + x;
    if (idx >= map_width * map_height) {
      return FLOOR;
    }
    if((map[idx] & UNDERMAP) == map[idx]) {
      return map[idx];
    } else {
      return map[idx] & OVERMAP;
    }
  }

  public Rect lastAffectedArea() {
    return affected_area;
  }

  public boolean gameWon() {
    for(int i = 0; i < map_width * map_height; i++) {
      if((map[i] & UNDERMAP) == GOAL && map[i] != PLACED_CRATE) {
        return false;
      }
    }
    return true;
  }

  public void setTile(int x, int y, int tile) {
    int idx = y * map_width + x;
    if (idx < map_width * map_height) {
      if ((tile & OVERMAP) == SOKOBAN) {
        man_x = x;
        man_y = y;
        tile = tile - SOKOBAN;
      } 
      map[idx] = ((tile & OVERMAP) | map[idx] & OVERMAP) | ((tile & UNDERMAP) | map[idx] & UNDERMAP);
    }
  }

  private void clearOverTile(int x, int y) {
    int idx = y * map_width + x;
    map[idx] = map[idx] & UNDERMAP;
  }

  private boolean tryMovingMan(int dx, int dy) {
    int new_x = man_x + dx;
    int new_y = man_y + dy;
    if(validSpace(new_x, new_y, dx, dy)) {
      affected_area.set(
        new_x > man_x ? man_x - 1: new_x - 1,
        new_y > man_y ? man_y - 1: new_y - 1,
        new_x < man_x ? man_x + 1: new_x + 1,
        new_y < man_y ? man_y + 1: new_y + 1
      );
      displaceCrates(new_x, new_y, dx, dy);
      man_x += dx;
      man_y += dy;
      moves += 1;
      return true;
    }
    return false;
  }

  private boolean validSpace(int x, int y, int vx, int vy) {
    if(x >= map_width || x < 0 || y >= map_height || y < 0) {
      return false;
    }
    if(getTile(x,y) == WALL) {
      return false;
    }
    if(getTile(x,y) == CRATE) {
      int dest = getTile(x+vx,y+vy);
      if(dest != FLOOR && dest != GOAL) {
        return false;
      }
    }
    return true;
  }

  private void displaceCrates(int x, int y, int vx, int vy) {
    if(getTileOnMap(x,y) == CRATE) {
      clearOverTile(x,y);
      setTile(x+vx,y+vy,CRATE);
    }
  }

  private void populateMap() {
    // It's a FAAAAAAKE!
    setTile(0,2,WALL);
    setTile(5,7,CRATE);
    setTile(4,7,WALL);
    setTile(6,7,WALL);
    setTile(12,5,GOAL);
  }
}
