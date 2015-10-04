import java.util.AbstractMap;
import java.util.Map.Entry;
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
		LeafNode<K,T> newLeaf = new LeafNode<K,T>(key, value);
		Entry<K, Node<K,T>> entry = new AbstractMap.SimpleEntry<K, Node<K,T>>(key, newLeaf);
		
		// Insert entry into subtree with root node pointer
		if(root == null) {
			root = entry.getValue();
		}
		
		// newChildEntry null initially, and null on return unless child is split
		Entry<K, Node<K,T>> newChildEntry = getChildEntry(root, entry, null);
		
		if(newChildEntry == null) {
			return;
		} else {
			IndexNode<K,T> newRoot = new IndexNode<K,T>(newChildEntry.getKey(), root, newChildEntry.getValue());
			root = newRoot;
			return;
		}
	}
	
	private Entry<K, Node<K,T>> getChildEntry(Node<K,T> node, Entry<K, Node<K,T>> entry, Entry<K, Node<K,T>> newChildEntry) {
		if(!node.isLeafNode) {
			// Choose subtree, find i such that Ki <= entry's key value < J(i+1)
			IndexNode<K,T> index = (IndexNode<K,T>) node;
			int i = 0;
			while(i < node.keys.size()) {
				if(entry.getKey().compareTo(node.keys.get(i)) < 0) {
					break;
				}
				i++;
			}
			// Recursively, insert entry
			newChildEntry = getChildEntry((Node<K,T>) index.children.get(i), entry, newChildEntry);
			// Usual case, didn't split child
			if(newChildEntry == null) {
				return newChildEntry;
			} 
			// Split child, must insert newChildEntry in node
			else {
				int j = 0;
				while (j < index.keys.size()) {
					if(newChildEntry.getKey().compareTo(node.keys.get(j)) < 0) {
						break;
					}
					j++;
				}
				
				index.insertSorted(newChildEntry, j);
				
				// Usual case, put newChildEntry on it, set newChildEntry to null, return
				if(!index.isOverflowed()) {
					newChildEntry = null;
					return newChildEntry;
				} 
				// Note difference with splitting leaf page
				else{
					newChildEntry = splitIndexNode(index);
					// Root was just split
					if(index == root) {
						// Create new node and make tree's root-node pointer point to newRoot
						IndexNode<K,T> newRoot = new IndexNode<K,T>(newChildEntry.getKey(), root, newChildEntry.getValue());
						root = newRoot;
						newChildEntry = null;
						return newChildEntry;
					}
					return newChildEntry;
				}
			}
		}
		// Node pointer is a leaf node
		else {
			LeafNode<K,T> leaf = (LeafNode<K,T>)node;
			LeafNode<K,T> newLeaf = (LeafNode<K,T>)entry.getValue();
			
			leaf.insertSorted(entry.getKey(), newLeaf.values.get(0));
			
			// Usual case: leaf has space, put entry and set newChildEntry to null and return
			if(!leaf.isOverflowed()) {
				newChildEntry = null;
				return newChildEntry;
			}
			// Once in a while, the leaf is full
			else {
				newChildEntry = splitLeafNode(leaf);
				if(leaf == root) {
					IndexNode<K,T> newRoot = new IndexNode<K,T>(newChildEntry.getKey(), leaf, newChildEntry.getValue());
					root = newRoot;
					newChildEntry = null;
					return newChildEntry;
				}
				return newChildEntry;
			}
		}
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
		
		// The rest D entries move to brand new node
		for(int i=D; i<=2*D; i++) {
			newKeys.add(leaf.keys.get(i));
			newValues.add(leaf.values.get(i));
		}
		
		// First D entries stay
		for(int i=D; i<=2*D; i++) {
			leaf.keys.remove(leaf.keys.size()-1);
			leaf.values.remove(leaf.values.size()-1);
		}
		
		K splitKey = newKeys.get(0);
		LeafNode<K,T> rightNode = new LeafNode<K,T>(newKeys, newValues);
		
		// Set sibling pointers
        LeafNode<K,T> tmp = leaf.nextLeaf;
        leaf.nextLeaf = rightNode;
        leaf.nextLeaf.previousLeaf = rightNode;
        rightNode.previousLeaf = leaf;
        rightNode.nextLeaf = tmp;
        
		Entry<K, Node<K,T>> entry = new AbstractMap.SimpleEntry<K, Node<K,T>>(splitKey, rightNode);
		
		return entry;
	}

	/**
	 * TODO split an indexNode and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param index, any other relevant data
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index) {
		K splittingKey = index.keys.get(D);
        index.keys.remove(D);

        ArrayList<K> RightKey = new ArrayList<K>();
        ArrayList<Node<K,T>> RightChildren = new ArrayList<Node<K, T>>();

        RightChildren.add(index.children.get(D+1));
        index.children.remove(D+1);

        while (index.keys.size() > D){
            RightKey.add(index.keys.get(D));
            index.keys.remove(D);
            RightChildren.add(index.children.get(D + 1));
            index.children.remove(D + 1);
        }

        IndexNode Right = new IndexNode(RightKey, RightChildren);
        Entry<K,Node<K,T>> entry = new AbstractMap.SimpleEntry<K,Node<K,T>>(splittingKey, Right);
		return entry;
	}


	/**
	 * Split a leaf node and return the new right node and the splitting
	 * key as an Entry<splittingKey, RightNode>
	 * 
	 * @param leaf
	 * @return the key/node pair as an Entry
	 */
//	public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {
//
//        ArrayList<K> rightKeys = new ArrayList<K>();
//        ArrayList<T> rightValues = new ArrayList<T>();
//        K splittingKey = leaf.keys.get(D);
//
//        while (leaf.keys.size() > D){
//            rightKeys.add(leaf.keys.get(D));
//            leaf.keys.remove(D);
//            rightValues.add(leaf.values.get(D));
//            leaf.values.remove(D);
//        }
//
//        LeafNode rightNode = new LeafNode(rightKeys, rightValues);
//        LeafNode Tmp = leaf.nextLeaf;
//        leaf.nextLeaf = rightNode;
//        leaf.nextLeaf.previousLeaf = rightNode;
//        rightNode.previousLeaf = leaf;
//        rightNode.nextLeaf = Tmp;
//
//
//        Entry<K,Node<K,T>> entry = new AbstractMap.SimpleEntry<K,Node<K,T>>(splittingKey, rightNode);
//		return entry;
//	}
	
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
