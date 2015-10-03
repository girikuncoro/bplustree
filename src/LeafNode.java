import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class LeafNode<K extends Comparable<K>, T> extends Node<K, T> {
	protected ArrayList<T> values;
	protected LeafNode<K,T> nextLeaf;
	protected LeafNode<K,T> previousLeaf;

	public LeafNode(K firstKey, T firstValue) {
		isLeafNode = true;
		keys = new ArrayList<K>();
		values = new ArrayList<T>();
		keys.add(firstKey);
		values.add(firstValue);

	}

	public LeafNode(List<K> newKeys, List<T> newValues) {
		isLeafNode = true;
		keys = new ArrayList<K>(newKeys);
		values = new ArrayList<T>(newValues);

	}

	/**
	 * insert key/value into this node so that it still remains sorted
	 * 
	 * @param key
	 * @param value
	 */
	public void insertSorted(K key, T value) {
		if (key.compareTo(keys.get(0)) < 0) {
			keys.add(0, key);
			values.add(0, value);
		} else if (key.compareTo(keys.get(keys.size() - 1)) > 0) {
			keys.add(key);
			values.add(value);
		} else {
			ListIterator<K> iterator = keys.listIterator();
			while (iterator.hasNext()) {
				if (iterator.next().compareTo(key) > 0) {
					int position = iterator.previousIndex();
					keys.add(position, key);
					values.add(position, value);
					break;
				}
			}

		}
	}

}
