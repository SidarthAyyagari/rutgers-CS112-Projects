package structures;

import javax.swing.text.html.HTML;
import javax.xml.soap.Node;
import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {

	/**
	 * Root node
	 */
	TagNode root = null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;

	/**
	 * Initializes this tree object with scanner for input HTML file
	 *
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	/**
	 * Builds the DOM tree from input HTML file. The root of the
	 * tree is stored in the root field.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/

		Stack<TagNode> treestack = new Stack<TagNode>();
		String line = null;
		TagNode node = null;
		TagNode siblingchecker = null;


		//check if it is a valid html file
		if (sc.hasNextLine()) {
			line = sc.nextLine();
			if (line.equalsIgnoreCase("<html>")) {
				//strip angle brackets from tag
				line = line.replace("<", "");
				line = line.replace(">", "");


				root = new TagNode(line, null, null);
				treestack.push(root);
			} else {
				System.out.println("not valid html");
				return;
			}
		}

		//read rest of file
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			node = new TagNode(line, null, null);

			//if line is a closing tag
			if (line.startsWith("</")) {
				//strip angle brackets from tag
				line = line.replace("</", "");
				line = line.replace(">", "");

				if (treestack.peek().tag.equals(line)) {
					treestack.pop();
				}

				//should not reach
				else {
					System.out.print("CLOSING TAG DOESNT MATCH****************");
				}
			}

			//if line is opening tag or text
			else {
				//if first child is null, then set node as first child of the top of the stack
				if (treestack.peek().firstChild == null) {
					treestack.peek().firstChild = node;
				}

				//if first child is NOT null, then set node as the last sibling of the child of the top of the stack
				else {
					siblingchecker = treestack.peek().firstChild;
					while (siblingchecker.sibling != null) {
						siblingchecker = siblingchecker.sibling;
					}
					siblingchecker.sibling = node;
				}

				//push only tags
				if (line.startsWith("<")) {
					line = line.replace("<", "");
					line = line.replace(">", "");
					node.tag = line;
					treestack.push(node);
				}
			}
		}
	}


	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 *
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		replaceTagWorker(oldTag, newTag, root);
	}

	private void replaceTagWorker(String oldTag, String newTag, TagNode ptr) {
		//condition for the last node
		if (ptr == null) {
			return;
		}

		//if you get a hit
		if (ptr.tag.equals(oldTag) && ptr.firstChild != null) {
			ptr.tag = newTag;
		}

		//for every node, repeat logic for its child and sibling
		replaceTagWorker(oldTag, newTag, ptr.firstChild);
		replaceTagWorker(oldTag, newTag, ptr.sibling);
	}

	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 *
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		TagNode ptr = boldRowWorker(root);

		if(ptr == null){
			return;
		}

		if(ptr.firstChild != null) {
			ptr = ptr.firstChild;

			//at end of loop, ptr points to the row that has tobe bolded
			for (int i = 1; i < row; i++) {
				ptr = ptr.sibling;

				//if the row that has to be bolded is greater than the total amount of rows in the table, then no-op
				if(ptr == null){
					return;
				}
			}

			//drop down to cell
			ptr = ptr.firstChild;

			//bold each cell in row
			while (ptr != null) {
				TagNode boldtag = new TagNode("b", ptr.firstChild, null);
				ptr.firstChild = boldtag;
				ptr = ptr.sibling;
			}
		}

		else{
			return;
		}


	}

	private TagNode boldRowWorker(TagNode ptr) {
		//last node in recursion
		if (ptr == null) {
			return null;
		}

		//when u get a hit
		if (ptr.tag.equals("table") && ptr.firstChild != null) {
			return ptr;
		}

		//recursion for every node
		TagNode x = boldRowWorker(ptr.firstChild);
		TagNode y = boldRowWorker(ptr.sibling);

		if(x!= null){
			return x;
		}

		if (y!= null){
			return y;
		}

		return null;

	}

	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		if (tag.equals("p") || tag.equals("em") || tag.equals("b")) {
			removeTagWorker1(root, tag);
		}

		else if (tag.equals("ul") || tag.equals("ol")) {
			removeTagWorker2(root, tag);
		}
	}

	private void removeTagWorker1(TagNode ptr, String tag) {
		//reach a dead end
		if (ptr == null || (ptr.firstChild == null && ptr.sibling == null) ){
			return;
		}

		//when u get a hit

		//if the tag is a child
		if( (ptr.firstChild != null) && (ptr.firstChild.tag.equals(tag)) ){
			TagNode toBeRemoved = ptr.firstChild;
			TagNode possSibling = null;
			TagNode newChild = toBeRemoved.firstChild;

			if(toBeRemoved.sibling != null){
				possSibling = toBeRemoved.sibling;
			}

			ptr.firstChild = newChild;
			TagNode current = ptr.firstChild;

			while(current.sibling != null){
				current = current.sibling;
			}

			if(possSibling != null){
				current.sibling = possSibling;
			}
		}

		//if the tag is a sibling
		else if ( (ptr.sibling != null) && (ptr.sibling.tag.equals(tag)) ){
			TagNode toBeRemoved = ptr.sibling;
			TagNode possSibling = null;

			if(toBeRemoved.sibling != null){
				possSibling = toBeRemoved.sibling;
			}

			ptr.sibling = toBeRemoved.firstChild;
			TagNode current = toBeRemoved.firstChild;

			if(possSibling != null){
				while(current.sibling != null){
					current = current.sibling;
				}
				current.sibling = possSibling;
			}
		}


		//how to step thru recursively
		removeTagWorker1(ptr.firstChild, tag);
		removeTagWorker1(ptr.sibling, tag);

		}

	private void removeTagWorker2(TagNode ptr, String tag) {
		if (ptr == null || (ptr.firstChild == null && ptr.sibling == null)) {
			return;
		}

		if ((ptr.firstChild != null) && (ptr.firstChild.tag.equals(tag))) {
			TagNode toBeRemoved = ptr.firstChild;
			TagNode possSibling = null;
			TagNode newChild = toBeRemoved.firstChild;

			if (toBeRemoved.sibling != null) {
				possSibling = toBeRemoved.sibling;
			}

			ptr.firstChild = newChild;
			TagNode current = ptr.firstChild;

			while (current.sibling != null) {
				if(current.tag.equals("li")){
					current.tag = "p";
				}
				current = current.sibling;
			}
			if(current.tag.equals("li")){
				current.tag = "p";
			}

			if (possSibling != null) {
				current.sibling = possSibling;
			}

		} else if ((ptr.sibling != null) && (ptr.sibling.tag.equals(tag))) {
			TagNode toBeRemoved = ptr.sibling;
			TagNode possSibling = null;

			if (toBeRemoved.sibling != null) {
				possSibling = toBeRemoved.sibling;
			}

			ptr.sibling = toBeRemoved.firstChild;
			TagNode current = toBeRemoved.firstChild;

			while (current.sibling != null) {
				current.tag = "p";
				current = current.sibling;
			}

			current.tag = "p";

			if (possSibling != null) {
				current.sibling = possSibling;
			}

		}

		removeTagWorker2(ptr.firstChild, tag);
		removeTagWorker2(ptr.sibling, tag);
	}

	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		/** COMPLETE THIS METHOD **/
		addTagWorker(root, word, tag);
	}

	private void addTagWorker(TagNode ptr, String word, String tag) {
		String textbuilder = "";
		TagNode targetsibling = null;

		if (ptr == null) {
			return;
		}

		if(ptr.sibling != null){
			targetsibling = ptr.sibling;
		}

		//if true, ptr is a text node
		if (ptr.firstChild == null) {

			//if its not there, then no point searching
			if (!ptr.tag.toLowerCase().contains(word.toLowerCase())) {
				return;
			}
			//break up text into individual words
			String[] bits = ptr.tag.split("\\s+");
			boolean isFirstNode = true;
			TagNode wordnode = null;
			TagNode tagnode = null;
			TagNode textnode = null;

			for (String text : bits) {
				if (	text.equalsIgnoreCase(word) ||
						text.equalsIgnoreCase(word + ".") ||
						text.equalsIgnoreCase(word + "!") ||
						text.equalsIgnoreCase(word + ",") ||
						text.equalsIgnoreCase(word + "?") ||
						text.equalsIgnoreCase(word + ":") ||
						text.equalsIgnoreCase(word + ";")) {

					//do not lose ptr
					if(isFirstNode){
						//hit on the first word
						if(textbuilder.equals("")){
							ptr.tag = tag;
							wordnode = new TagNode(text, null, null);
							ptr.firstChild = wordnode;

							isFirstNode = false;
							continue;
						}

						//didnt get a hit on the first word
						else{
							wordnode = new TagNode(text, null,null);
							tagnode = new TagNode(tag, wordnode, null);
							ptr.tag = textbuilder + " ";
							ptr.sibling = tagnode;

							ptr = tagnode;
							textbuilder = "";
							isFirstNode = false;
							continue;
						}
					}
					//hit, later in the text
					if(!textbuilder.equals("")){
						textnode = new TagNode(textbuilder + " ", null, null);
						textbuilder = "";
						ptr.sibling = textnode;
						wordnode = new TagNode(text, null, null);
						tagnode = new TagNode(tag, wordnode, null);
						textnode.sibling = tagnode;
						ptr = tagnode;
						continue;
					}
					else{
						wordnode = new TagNode(text, null, null);
						tagnode = new TagNode(tag, wordnode, null);

						ptr.sibling = tagnode;
						ptr = tagnode;
						continue;
					}


				}
				else{
					if(textbuilder.equals("")){
						textbuilder = text;
					}
					else{
						textbuilder = textbuilder + " " + text;
					}

					continue;
				}

			}

			if(!textbuilder.equals("")){
				textnode = new TagNode(textbuilder, null, null);
				ptr.sibling = textnode;
				ptr = textnode;
			}

			ptr.sibling = targetsibling;
			}

			addTagWorker(ptr.firstChild, word, tag);
			addTagWorker(ptr.sibling, word, tag);
		}

	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
}
