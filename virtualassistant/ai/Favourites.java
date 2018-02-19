package virtualassistant.ai;

import java.util.ArrayList;

import virtualassistant.misc.Pair;

public class Favourites {

  private final int maxFavourites = 5;

  private ArrayList<Pair<String, Integer>> store;
  private int high;

  private int iterateValue;

  public Favourites() {
    store = new ArrayList<Pair<String, Integer>>();

    for (int i = 0; i < maxFavourites; i++) {
      store.add(null);
    }

    high = 0;
  }

  /**
   * Adds the ticker to favourites, or if already contained, renews it
   */
  public void addToFavourites(String ticker) {

    int lowest = 1000000;
    int lowestPos = -1;

    for (int i = 0; i < maxFavourites; i++) {
      if (store.get(i) == null) {
        lowestPos = i;
        lowest = 0;
        continue;
      }

      //If in the list, update
      if (ticker == store.get(i).getFirst()) {
        store.get(i).setSecond(++high);
        return;
      }

      if (store.get(i).getSecond() < lowest) {
        lowest = store.get(i).getSecond();
        lowestPos = i;
      }
    }

    //Not in list
    //Swap for oldest in list
    Pair<String, Integer> newTicker = new Pair<String, Integer>(ticker, ++high);
    store.set(lowestPos, newTicker);

  }

  /**
   * Adds a new ticker, that cannot be remove naturally
   */
  public void addBookmark(String ticker) {

  }

  /**
   * Removes the ticker from the list
   */
  public void remove(String ticker) {

  }

  public void startIterating() {
    iterateValue = 1000000;
  }

  public String nextIterate() {
    int highest = 0;
    int highestPos = -1;
    for (int i = 0; i < maxFavourites; i++) {

      if (store.get(i).getSecond() < iterateValue && store.get(i).getSecond() > highest) {
        highest = store.get(i).getSecond();
        highestPos = i;
      }

    }

    if (highestPos == -1) {
      return "";
    }


    iterateValue = highest;

    return (store.get(highestPos).getFirst());
  }

}
