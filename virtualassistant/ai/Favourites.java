package virtualassistant.ai;

import java.util.LinkedHashMap;
import java.util.Map;

import virtualassistant.misc.Pair;

public class Favourites<K, V> extends LinkedHashMap<K, V> {

  private final int maxFavourites = 30;

  @Override
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    if (this.size() > maxFavourites) {
      return true;
    }
    return false;
  }

  public void addToBegining(K key, V value) {
    if (this.containsKey(key)) {
      this.remove(key);
    }
    this.put(key, value);
  }

}
