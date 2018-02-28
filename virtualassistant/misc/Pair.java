package virtualassistant.misc;

import virtualassistant.data.news.NewsObj;
import java.util.LinkedList;

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

    public static Pair<String, LinkedList<NewsObj>> merge(Pair<String, LinkedList<NewsObj>> p1, Pair<String, LinkedList<NewsObj>> p2) {
        
        if(p2 == null) return p1;
        
        if(p2.second == null) {
            return new Pair(p1.first + p2.first, p1.second);
        }
        
        if(p2.first == null){
            p1.second.addAll(p2.second);
            return new Pair(p1.first, p1.second);
        }
        p1.second.addAll(p2.second);
        return new Pair(p1.first + p2.first, p1.second );
    }
}
