package core.image_filters.filter_utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapMerge<K, V> {
	
	private final Map <K, V> merged = new HashMap <K, V>();
	private final List <Map<K, V>> maps;
	
    public MapMerge(List <Map<K, V>> maps){
    	 this.maps = maps;
    }
    public Map<K, V> getMerged(){
    	if(merged.size() != 0 )return merged;
    	else { mergeMaps(); return merged; }
    }
    private void mergeMaps(){
    	for(Map<K, V> m : maps){
    		for(K k : m.keySet()){
    			merged.put(k, m.get(k));
    		}
    	}
    }
}
