package dsacoursework2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Hakeem
 */
public class Trie {

    public TrieNode root;
    private Casing casing;
    
    private enum Casing{
        UPPER(65),
        LOWER(97);
        
        int start;
        Casing(int start){
            this.start = start;
        }
        
        public int alphabetStart(){
            return this.start;
        }
    }
    
    
    public Trie(){
        this.casing = Casing.LOWER;
        this.root = new TrieNode(false);
    }
    
    public int getTrieNodeIndexOfLetter(char c){
        
        if (this.casing.equals(Casing.LOWER)){
            c = Character.toLowerCase(c);
        }else if (this.casing.equals(Casing.UPPER)){
            c = Character.toUpperCase(c);
        }
        
        //System.out.println(c + " : " + (int)c + " : "+this.casing.alphabetStart());
        
        return ((int)c) - this.casing.alphabetStart();
    }
    
    public char getLetterFromTrieNodeIndex(int index){
        return (char)(index+this.casing.alphabetStart());
    }
    
    public boolean add(String key){
        //true if added, false if exists
        boolean wasAdded = false;
        
        
        TrieNode currentParent = this.root;
        
        for (int i = 0; i < key.length(); i++){
            char currentLetter = key.charAt(i);
                        
            int index = getTrieNodeIndexOfLetter(currentLetter);
            boolean isLastLetter = (i == key.length()-1);
            
            TrieNode nextParent = currentParent.offspring[index];
            
            if (nextParent == null){
                nextParent = new TrieNode(isLastLetter);
                currentParent.offspring[index] = nextParent;
                wasAdded = true;
            }else if (isLastLetter && nextParent.isCompleteKey() != true){
                nextParent.setCompleteKey(true);
                wasAdded = true;
            }
            currentParent = nextParent;
        }        
        
        return wasAdded;
    }
    
    public String outputBreadthFirstSearch(){
        
        StringBuilder output = new StringBuilder();
        
        Character[][] bfs = breadthFirstSearch(this.root, 0);
        
        for (Character[] bf : bfs) {
            for (Character c : bf) {
                if (c != null) output.append(c);
            }
        }
               
        return output.toString();
    }
    
    private Character[][] breadthFirstSearch(TrieNode Tn, int j){
        Character[][] output = new Character[j+1][];
        output[j] = new Character[1];
        
        for (int i = 0; i < 26; i++){
            if (Tn.offspring[i] != null){
                char letter = getLetterFromTrieNodeIndex(i);
                
                
                //append
                output[j] = (Character[])appendToArray(letter, output[j]);
                
            }
        }
        
        for (int k = 0; k < 26; k++){
            if (Tn.offspring[k] != null){
                output = (Character[][])merge2DArrays(output, breadthFirstSearch(Tn.offspring[k], j+1));
            }
        }
        
        
        return output;
    }
    
    private Character[][] merge2DArrays(Character[][] ina, Character[][] inb){
        
        //Copy ina into output, and ensure enough space for inb
        //Character[][] output = ina; <--- due to static typing causes a nullreference if inb is larger, not a problem in e.g. javascript
        //START JAVA SPECIFIC MODIFICATION TO ALGORITHM
        int length = (ina.length > inb.length) ? ina.length : inb.length;
        Character[][] output = new Character[length][];
        
        int index=0;
        for (Character[] tmp : ina){
            output[index++] = tmp;
        }
        //END JAVA SPECIFIC MODIFICATION TO ALGORITHM
        
        for (int itemKey = 0; itemKey < inb.length; itemKey++){
            if (inb[itemKey] != null){
                
                Character[] merged = (output[itemKey] != null) ? output[itemKey] : new Character[0];
                
                for (Character item : inb[itemKey]) {
                    merged = appendToArray(item, merged);
                }
                
                output[itemKey] = merged;
            }
        }
        
        return output;
    }
    
    private Character[] appendToArray(Character toAdd, Character[] array){
        Character[] output = new Character[array.length + 1];
        int index = 0;
        for (Character o : array){
            output[index++] = o;
        }
        output[index] = toAdd;
        return output;
        
    }
    
    private String[] appendToArray(String toAdd, String[] array){
        String[] output = new String[array.length + 1];
        int index = 0;
        for (String o : array){
            output[index++] = o;
        }
        output[index] = toAdd;
        return output;
        
    }
    
    public String outputDepthFirstSearch(){
        
        return depthFirstSearch(this.root);
    }
    
    private String depthFirstSearch(TrieNode Tn){
        String output = "";
        
        for (int i = 0; i < 26; i++){
            if (Tn.offspring[i] != null){
                output += depthFirstSearch(Tn.offspring[i]) + getLetterFromTrieNodeIndex(i);
            }
        }
        
        return output;
    }
    
    public Trie getSubTrie(String p) throws ArrayIndexOutOfBoundsException{
        
        //if (p.equals("")) throw new ArrayIndexOutOfBoundsException("SubTrie with prefix empty '' is the same as original Trie.");
        
        Trie S;
        
        TrieNode currentNode = this.root;
        
        for(int i = 0; i < p.length(); i++){
            
            int index = getTrieNodeIndexOfLetter(p.charAt(i));
            
            if (currentNode.offspring[index] != null){
                currentNode = currentNode.offspring[index];
            }else{
                return null;
            }
        }
        
        S = new Trie();
        S.root = currentNode;
        
        return S;
    }
    
    public String[] getAllWords(){
        return this.getAllWords(this.root, null);
    }
    
    public String[] getAllWords(TrieNode Tn, String p){
        
        String[] output = new String[0];
        
        String prefix = (p != null) ? p : "";
        
        for (int i = 0; i < 26; i++){
            if (Tn.offspring[i] != null){
                String current = prefix + getLetterFromTrieNodeIndex(i);
                if (Tn.offspring[i].isCompleteKey()){
                    output = appendToArray(current, output);
                }
                output = appendList(getAllWords(Tn.offspring[i], current), output);
            }
        }
        
        return output;
    }
    
    private String[] appendList(String[] ina, String[] inb){
        String[] merged = new String[0];
        for (String tmp : ina){
            
            merged = appendToArray(tmp, merged);
        }
        for (String tmp : inb){
            merged = appendToArray(tmp, merged);
        }
        return merged;
    }
    
    public boolean contains(String key){
        
        TrieNode currentParent = this.root;
        
        for (int i = 0; i < key.length(); i++){
            
            char currentLetter = key.charAt(i);
            int index = getTrieNodeIndexOfLetter(currentLetter);
            boolean isLastLetter = (i == key.length()-1);
            
            TrieNode nextParent = currentParent.offspring[index];
            
            if ((nextParent == null) || (isLastLetter && !nextParent.isCompleteKey())){
                return false;
            }
            currentParent = nextParent;
        }
        return true;
    }
    
    @Override
    public String toString(){
        StringBuilder output = new StringBuilder();
        
        int index = 0;
        String[] allWords = this.getAllWords();
        if (allWords != null && allWords.length > 0){
            for (String s : allWords){
                output.append(index++).append(":\t").append(s).append("\n");
            }
        }else{
            output.append("No words represented in Trie.");
        }
        
        return output.toString();
    }
    
    
    public static void main(String[] args){
        //Demonstrate all functionality
        
        Trie T = new Trie();
        
        
        T.add("bat");
        T.add("cat");
        T.add("chat");
        T.add("cheers");
        T.add("cheese");
        
        
        System.out.println("All words:\n" + T.toString());
        System.out.println();
        System.out.println("Breadth First Search: " + T.outputBreadthFirstSearch());
        System.out.println();
        System.out.println("Depth First Search: " + T.outputDepthFirstSearch());
        System.out.println();
        System.out.println("Does Trie contain 'cat'? " + T.contains("cat"));
        System.out.println();
        System.out.println("Does Trie contain 'chat'? " + T.contains("chat"));
        System.out.println();
        System.out.println("Does Trie contain 'hat'? " + T.contains("hat"));
        System.out.println();
        System.out.println("Does Trie contain 'cheese'? " + T.contains("cheese"));
        
        System.out.println("\n\n\nGetSubTrie()\n");
        
        Trie sub = T.getSubTrie("zamboni");
        System.out.println("Is there a sub trie for 'zamboni'?" + (sub!=null));
        sub = T.getSubTrie("ch");
        System.out.println("Is there a sub trie for 'ch'?" + (sub!=null));
        System.out.println("\n"+sub.toString());
        
        //System.out.println(T);
        
        
    }
        
    
}
