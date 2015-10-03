import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class contains methods assisting coding and testing
 * 
 */
public class Utils {

	/**
	 * Bulk Insert test data
	 * 
	 * @param b
	 * @param tests
	 */
	public static <K extends Comparable<K>, T> void 
	    bulkInsert(BPlusTree<K,T> b, K[] tests, T[] testValues) {
		for (int i = 0; i < tests.length; i++) {
			b.insert(tests[i], testValues[i]);
		}

	}

	
	public static <K extends Comparable<K>,T> String 
    outputTree(BPlusTree<K,T> tree) {
  /* Temporary queue. */
  LinkedBlockingQueue<Node<K,T>> queue;
  
  /* Create a queue to hold node pointers. */
  queue = new LinkedBlockingQueue<Node<K,T>>();
  String result = "";
  
  int nodesInCurrentLevel = 1;
  int nodesInNextLevel = 0;
  ArrayList<Integer> childrenPerIndex = new ArrayList<Integer>();
  queue.add(tree.root);
  while (!queue.isEmpty()) {
    Node<K,T> target = queue.poll();
    nodesInCurrentLevel--;
    if (target.isLeafNode) {
      LeafNode<K,T> leaf = (LeafNode<K,T>) target;
      result += "[";
      for (int i = 0; i < leaf.keys.size(); i++) {
        result += "(" + leaf.keys.get(i) + "," + leaf.values.get(i)
            + ");";
      }
      if (childrenPerIndex.isEmpty()) {
        result += "]$";
      } else {
        childrenPerIndex.set(0, childrenPerIndex.get(0) - 1);
        if (childrenPerIndex.get(0) == 0) {
          result += "]$";
          childrenPerIndex.remove(0);
        } else {
          result += "]#";
        }
  
      }
    } else {
      IndexNode<K,T> index = ((IndexNode<K,T>) target);
      result += "@";
      for (int i = 0; i < index.keys.size(); i++) {
        result += "" + index.keys.get(i) + "/";
      }
      result += "@";
      queue.addAll(index.children);
      if (index.children.get(0).isLeafNode) {
        childrenPerIndex.add(index.children.size());
      }
      nodesInNextLevel += index.children.size();
    }
  
    if (nodesInCurrentLevel == 0) {
      result += "%%";
      nodesInCurrentLevel = nodesInNextLevel;
      nodesInNextLevel = 0;
    }
  
  }
  
  return result;

}
	
	
	/**
	 * print the current tree to console
	 * 
	 * @param root
	 */
	public static <K extends Comparable<K>,T> void printTree(BPlusTree<K,T> tree){
		System.out.println(outputTree(tree));
	}

}
