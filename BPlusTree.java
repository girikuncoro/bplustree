import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.ArrayList;


/**
 * BPlusTree Class Assumptions: 1. No duplicate keys inserted 2. Order D:
 * D<=number of keys in a node <=2*D 3. All keys are non-negative
 * TODO: Rename to BPlusTree
 */
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<K,T> root;
	public static final int D = 2;

	/**
	 * TODO Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public T search(K key) {
		return null;
	}
	

	/**
	 * TODO Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(K key, T value) {
		// Empty tree
		if(root == null) {
			LeafNode<K,T> newLeaf = new LeafNode<K,T>(key, value);
			root = newLeaf;
		}
		
		// Usual case
		Entry<K, Node<K,T>> overflow = getOverflowNode(root, key, value);
		if(overflow != null) {
			// overflow node moves to root
			root = new IndexNode(overflow.getKey(), root, overflow.getValue());
		}
	}
	
	private Entry<K, Node<K,T>> getOverflowNode (Node<K,T> node, K key, T value) {
		Entry<K, Node<K,T>> overflow = null;
		
		if(node.isLeafNode) {
			LeafNode<K,T> leaf = (LeafNode<K,T>) node;
			leaf.insertSorted(key, value);
			
			if(leaf.isOverflowed()) {
				Entry<K, Node<K,T>> rSplit = splitLeafNode(leaf); 
				return rSplit;
			}
			
		} else {
			IndexNode idx = (IndexNode) node;
			
			if(key.compareTo(node.keys.get(0)) < 0) {
				Node<K,T> firstChild = (Node<K,T>) idx.children.get(0);
				overflow = getOverflowNode(firstChild, key, value);
			} else if(key.compareTo(node.keys.get(idx.keys.size()-1)) >= 0) {
				Node<K,T> lastChild = (Node<K,T>) idx.children.get(idx.children.size()-1);
				overflow = getOverflowNode(lastChild, key, value);
			} else {
				// Insert into one of the middle child
				for(int i=0; i < idx.children.size(); i++) {
					if(key.compareTo((K) idx.keys.get(i)) < 0) {
						Node<K,T> midChild = (Node<K,T>) idx.children.get(i);
						overflow = getOverflowNode(midChild, key, value);
						break;
					}
				}
			}
		}
		
		// No overflow found
		if(overflow != null) {
			IndexNode idxNode = (IndexNode) node;
			int idxParent = idxNode.keys.size();
			
			K splitKey = overflow.getKey();
			K firstKey = (K) idxNode.keys.get(0);
			K lastKey = (K) idxNode.keys.get(idxNode.keys.size()-1);
			
			if(splitKey.compareTo(firstKey) < 0) {
				idxParent = 0;
			} else if(splitKey.compareTo(lastKey) > 0) {
				idxParent = idxNode.children.size();
			} else {
				for(int i=0; i < idxNode.keys.size(); i++) {
					K midKey = (K) idxNode.keys.get(i);
					if((Integer) midKey > i) {
						idxParent = i;
					}
				}
			}
			
			idxNode.insertSorted(overflow, idxParent);
			if(idxNode.isOverflowed()) {
				Entry<K, Node<K,T>> rSplit = splitIndexNode(idxNode);
				return rSplit;
			}
			return null;
		}
		
		return overflow;
	}

	/**
	 * TODO Split a leaf node and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param leaf, any other relevant data
	 * @return the key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {
		ArrayList<K> newKeys = new ArrayList<K>();
		ArrayList<T> newValues = new ArrayList<T>();
		
		for(int i=D; i<=2*D; i++) {
			newKeys.add(leaf.keys.get(i));
			newValues.add(leaf.values.get(i));
		}
		
		// Delete the right most
		for(int i=D; i<=2*D; i++) {
			leaf.keys.remove(leaf.keys.size()-1);
			leaf.values.remove(leaf.values.size()-1);
		}
		
		K splitKey = newKeys.get(0);
		LeafNode<K,T> rNode = new LeafNode<K,T>(newKeys, newValues);
		
		// Arrange sibling pointers
		if(leaf.nextLeaf != null) {
			rNode.nextLeaf = leaf.nextLeaf;
		}
		leaf.nextLeaf = rNode;
		
		Entry<K, Node<K,T>> ret = new AbstractMap.SimpleEntry<K, Node<K,T>>(splitKey, rNode);
		
		return ret;
	}

	/**
	 * TODO split an indexNode and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param index, any other relevant data
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index) {
		ArrayList<K> newKeys = new ArrayList<K>();
		ArrayList<Node<K,T>> newChildren = new ArrayList<Node<K,T>>();
		
		for(int i=D; i<=2*D; i++) {
			newKeys.add(index.keys.get(i));
			newChildren.add(index.children.get(i));
		}
		
		// Delete the right most
		for(int i=D; i<=2*D; i++) {
			index.keys.remove(index.keys.size()-1);
			index.children.remove(index.children.size()-1);
		}
		
		// Push up the new index
		K splitKey = newKeys.get(0);
		IndexNode<K,T> rNode = new IndexNode<K,T>(newKeys, newChildren);
		Entry<K, Node<K,T>> ret = new AbstractMap.SimpleEntry<K, Node<K,T>>(splitKey, rNode);
		
		return ret;
	}

	/**
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(K key) {

	}

	/**
	 * TODO Handle LeafNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleLeafNodeUnderflow(LeafNode<K,T> left, LeafNode<K,T> right,
			IndexNode<K,T> parent) {
		return -1;

	}

	/**
	 * TODO Handle IndexNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleIndexNodeUnderflow(IndexNode<K,T> leftIndex,
			IndexNode<K,T> rightIndex, IndexNode<K,T> parent) {
		return -1;
	}

}
