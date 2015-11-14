package com.itisdancing.Sokoban;

/* ---- this class generates the map from the raw data file "sokoban"  ---- */

import java.io.*;
import java.util.*;
import android.util.Log;

public class MapList
{
  private BufferedReader data;
  private List<MapRecord> map_list;

  public MapList(InputStream input) {
    this(new InputStreamReader(input));
  }

  public MapList(Reader reader) {
    data = new BufferedReader(reader); 
    map_list = new ArrayList<MapRecord>();
    readData();
  }

  public SokobanArena selectMap(int map) {
    MapRecord record = map_list.get(map);
    SokobanArena arena = new SokobanArena(record.getWidth(), record.getHeight());
    char[] data = record.getData().toCharArray();
    int length = record.getData().length();
    int x = 0;
    int y = 0;

    for(int i = 0; i < length; i++) {
      if(data[i] == '\n') {
        x = 0;
        y += 1;
      } else {
        arena.setTile(x, y, tileType(data[i]));
        x += 1;
      }
    }
    
    return arena;
  }

  protected int tileType(char tile) {
    switch(tile) {
      case '#': return SokobanArena.WALL;
      case '.': return SokobanArena.GOAL;
      case '$': return SokobanArena.CRATE;
      case '@': return SokobanArena.SOKOBAN;
      case '!': return SokobanArena.PLACED_CRATE;
      case '?': return SokobanArena.SOKOBAN_ON_GOAL;
      default:  return SokobanArena.FLOOR;
    }
  }

  protected void readData() {
    String line;
    String newMap;
    MapRecord record = new MapRecord();

    try {
      line = data.readLine();
      while(line != null) {
        if(line.charAt(0) == ';') {
          newMap = record.getData();
          if(newMap.length() > 0) {
            map_list.add(record);
          }
          record = new MapRecord();
        } else {
          record.addLine(line);
        }
        line = data.readLine();
      }
      newMap = record.getData();
      if(newMap.length() > 0) {
        map_list.add(record);
      }
    } catch(IOException except) {
      // TODO
    }
  }

  private class MapRecord {
    private int width;
    private int height;
    private String data;
    private StringBuilder data_builder;

    public MapRecord() {
      width = 0;
      height = 0;
      data = null;
      data_builder = new StringBuilder();
    }

    public String getData() {
      finalize();
      return data;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void addLine(String line) {
      data_builder.append(line + "\n");
      if (line.length() > width) {
        width = line.length();
      }
      height += 1;
    }

    protected void finalize() {
      if (data == null) {
        data = data_builder.toString();
      }
    }
  }
}
