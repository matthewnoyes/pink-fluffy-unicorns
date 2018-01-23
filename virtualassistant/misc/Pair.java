package virtualassistant.misc;

public class Pair<K, V> {
  
  private K first;
  private V second;
  
  public Pair(K first, V second) {
    this.first = first;
    this.second = second;
  }
  
  // Getters
  public K getFirst(){
    return first;
  }
  
  public V getSecond(){
    return second;
  }
  
  // Setters
  public void setFirst(K first) {
    this.first = first;
  }
  
  public void setSecond(V second) {
    this.second = second;
  }
}
