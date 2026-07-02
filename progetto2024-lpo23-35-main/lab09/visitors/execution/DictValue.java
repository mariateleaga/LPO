package lab09.visitors.execution;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.util.TreeMap;

/* import static java.util.Objects.hash; */

public class DictValue implements Value {

	private final TreeMap <Integer , Value> map;

	public DictValue(int key, Value value) {
		map=new TreeMap<>(); 
		map.put(requireNonNull(key),requireNonNull(value));
	}

	private DictValue(TreeMap <Integer , Value> m) {
		requireNonNull(m);
		map = new TreeMap<>(m);
	}

	
	public TreeMap <Integer , Value> getMap() {
		return this.map;
	}

	
	public DictValue remove (int key){
		if (!this.map.containsKey(key)) throw new InterpreterException(String.format("Missing key %s",key));
		this.map.remove(key);
		return this;
	}

	
	public Value get (int key){
		if (!this.map.containsKey(key)) throw new InterpreterException(String.format("Missing key %s",key));
		return this.map.get(key);
	}

	public DictValue put (DictValue dict, int key, Value val){
		DictValue d = new DictValue(dict.map);
		d.map.put(key,val);
		return d;
	}
	
	@Override
	public DictValue toMap() {
		return (this);
	}

	@Override
	public String toString(){
		String s="";
		int i=0;
		for (int k : this.map.keySet()){
			if(i<this.map.size()-1)
				s+=(k+":"+this.map.get(k).toString()+",");
			else s+=(k+":"+this.map.get(k).toString());
			i++;
		}

		return String.format("[%s]",s);
	}
	
	@Override
	public int hashCode() {
		return hash(map);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DictValue dv) return this.map.equals(dv.map);
		return false;
	}

}
