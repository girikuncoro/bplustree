# Bplustree
Project in Cornell CS4320 to implement B+ Trees, a dynamic index structure for database system. 

# Authors
Giri Kuncoro (gk256@cornell.edu), Batu Inal (bi49@cornell.edu), Shubham Shukla (ss3469@cornell.edu)

# Description
**1. Search**

Search key value in a given tree, starts from root, done recursively with helper function to search throughout the tree. Linear searching is used when searching for entry.

**2. Insert**

The insertion takes an entry, finds the leaf node where it belongs and inserts it there. Recursively insert the entry by calling the insert algorithm on the appropriate child node. Going down to the leaf node where entry belongs, placing the entry there and returning all the way back to the root node. Sometimes a node is full and it must be split. When the node is split, an entry pointing to the node created by the split must be inserted into its parent. This entry is pointed by the `newChildEntry` (pointer variable). If root is split, new root node is created and the height of tree increases by 1. Splitting for both leaf and index node are done by the helper function `splitIndexNode` and `splitLeafNode`.

**3. Delete**

The deletion takes an entry, finds the leaf node where it belongs and deletes it. Recursively delete the entry by calling the delete algorithm on the appropriate child node. Going down to the leaf node where entry belongs, remove the entry from there and return all the way back to root node. Sometimes, node is at minimum occupancy before deletion, and deletion causes underflow. When this happens, either redistribute entries from adjacent sibling or merge the node with siblings. However, redistribution has higher priority. If entries are redistributed, their parent node must be updated to reflect this: the key value in the index entry pointing to the second node must be changed to be the lowest search key in the second node. If nodes are merged, parnet must be updated by deleting the index entry for second node. The index entry is pointed by `oldChildEntry` (pointer variable). If the last entry in root node is deleted because one of its children was deleted, the height of tree decreases by 1.

**Tests**

The code is tested for D=1, D=2 and D=3 for search, insert, and delete functions.

# Acknowledgments
The code is implemented based on the algorithm provided in Ramakrishnan and Gehrke's Database Management System book (3rd edition) page 347-356. The pseudocode written there is translated into Java code can be found in this repo.
