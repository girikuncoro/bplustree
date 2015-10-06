import static org.junit.Assert.*;

import org.junit.Test;

public class TestsThreeD {

	// Change D = 3 on BPlusTree.java before running this test case
	@Test
	public void testSimpleHybrid() {
		System.out.println("\n testSimpleHybrid");
		Character alphabet[] = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f'};
		String alphabetStrings[] = new String[alphabet.length];
		for (int i = 0; i < alphabet.length; i++) {
			alphabetStrings[i] = (alphabet[i]).toString();
		}
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);

		String test = Utils.outputTree(tree);
		String correct = "[(a,a);(b,b);(c,c);(d,d);(e,e);(f,f);]$%%";

		assertEquals(correct, test);
		
		for(int i=0; i<alphabet.length; i++) {
			assertEquals(""+alphabet[i], tree.search(alphabet[i]));
		}
		
		tree.insert(new Character('g'), "g");
		test = Utils.outputTree(tree);
		correct = "@d/@%%[(a,a);(b,b);(c,c);]#[(d,d);(e,e);(f,f);(g,g);]$%%"; 

		// Delete from left
		tree.delete('a');
		assertEquals(null, tree.search('a'));

		test = Utils.outputTree(tree);
		correct = "@e/@%%[(b,b);(c,c);(d,d);]#[(e,e);(f,f);(g,g);]$%%";
		assertEquals(correct, test);
		
		tree.delete('b');
		test = Utils.outputTree(tree);
		correct = "[(c,c);(d,d);(e,e);(f,f);(g,g);]$%%";
		assertEquals(correct, test);
	}

	public <K extends Comparable<K>, T> void testTreeInvariants(
			BPlusTree<K, T> tree) {
		for (Node<K, T> child : ((IndexNode<K, T>) (tree.root)).children)
			testNodeInvariants(child);
	}

	public <K extends Comparable<K>, T> void testNodeInvariants(Node<K, T> node) {
		assertFalse(node.keys.size() > 2 * BPlusTree.D);
		assertFalse(node.keys.size() < BPlusTree.D);
		if (!(node.isLeafNode))
			for (Node<K, T> child : ((IndexNode<K, T>) node).children)
				testNodeInvariants(child);
	}

	public <K extends Comparable<K>, T> int treeDepth(Node<K, T> node) {
		if (node.isLeafNode)
			return 1;
		int childDepth = 0;
		int maxDepth = 0;
		for (Node<K, T> child : ((IndexNode<K, T>) node).children) {
			childDepth = treeDepth(child);
			if (childDepth > maxDepth)
				maxDepth = childDepth;
		}
		return (1 + maxDepth);
	}
}
