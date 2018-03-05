package dsacoursework2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Hakeem
 */
public class AutoCompletionTrie{

    
    public AutoCompletionTrieNode root;
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
    
    
    public AutoCompletionTrie(){
        this.casing = Casing.LOWER;
        this.root = new AutoCompletionTrieNode(-1);
    }
    
    
    private AutoCompletionTrieNode getLastNodeOfWord(String word){
        int indexOfLastLetter = getTrieNodeIndexOfLetter(word.substring(word.length()-1, word.length()).charAt(0));
        
        AutoCompletionTrie sub = this.getSubTrie(word.substring(0, word.length()-1));
        
        
        return sub.root.offspring[indexOfLastLetter];
    }
    
    
    public HashMap<String, Float> getBestMatches(String p){
        
        if (p.equals("")) return null;
        
        AutoCompletionTrie sub;
        HashMap<String, Integer> wFreqOrig = new HashMap<>();
        
        try{
            
            if (this.contains(p)){
                //contains p as a complete key
                int lastCharIndex = getTrieNodeIndexOfLetter(p.charAt(p.length()-1));
                wFreqOrig.put(p, this.getLastNodeOfWord(p).getFrequency());
            }
            
            sub = this.getSubTrie(p);
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Failed to located query in AutoCompleteTrie:\n" + p + ".\n");
            return null;
        }
        
        if (sub != null){
            
            HashMap<String, Integer> wFreq = DictionaryFinder.formDictionary(sub.getAllWordFrequencies(), p);
            
            
            //If the original prefix is a word, include that in the auto-completion
            if (wFreqOrig.size() > 0){
                String key = wFreqOrig.keySet().toArray(new String[0])[0];
                Integer val = wFreqOrig.get(key);
                wFreq.put(key, val);
            }
            
            HashMap<String, Float> probabilities = new HashMap();
            
            float totalOccurences = 0.0f;
            
            for (String key : wFreq.keySet()){
                float freq = (float)wFreq.get(key);
                
                //System.out.println(key + " : " + freq);
                
                totalOccurences += freq;
                probabilities.put(key, freq);
            }
            
            for(String key : wFreq.keySet()){
                
                //System.out.println(key + ":" + probabilities.get(key));
                probabilities.put(key, probabilities.get(key)/totalOccurences);
            }
            
            return probabilities;
        }else{
            return null;
        }
        
    }
    
    public HashMap<String, Integer> getAllWordFrequencies(){
        String[] keys = this.getAllWords();
        HashMap<String, Integer> output = new HashMap();
        
        for (String key : keys){
            AutoCompletionTrieNode tmp = this.getLastNodeOfWord(key);
            output.put(key, tmp.getFrequency());
        }
        
        return output;
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
    
    public boolean add(String key, int frequency, boolean additive){
        //true if added, false if exists
        boolean wasAdded = false;
        
        
        AutoCompletionTrieNode currentParent = this.root;
        
        for (int i = 0; i < key.length(); i++){
            char currentLetter = key.charAt(i);
            
                       
            int index = getTrieNodeIndexOfLetter(currentLetter);
            boolean isLastLetter = (i == key.length()-1);
            
            AutoCompletionTrieNode nextParent = currentParent.offspring[index];
            
            if (nextParent == null){
                nextParent = new AutoCompletionTrieNode((isLastLetter)?frequency : -1);
                currentParent.offspring[index] = nextParent;
                wasAdded = true;
            }else if (isLastLetter && nextParent.isCompleteKey() != true){
                if (additive){
                    nextParent.setFrequency(frequency+nextParent.getFrequency());
                }else nextParent.setFrequency(frequency);
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
    
    private Character[][] breadthFirstSearch(AutoCompletionTrieNode Tn, int j){
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
    
    private String depthFirstSearch(AutoCompletionTrieNode Tn){
        String output = "";
        
        for (int i = 0; i < 26; i++){
            if (Tn.offspring[i] != null){
                output += depthFirstSearch(Tn.offspring[i]) + getLetterFromTrieNodeIndex(i);
            }
        }
        
        return output;
    }
    
    public AutoCompletionTrie getSubTrie(String p) throws ArrayIndexOutOfBoundsException{
        
        //if (p.equals("")) throw new ArrayIndexOutOfBoundsException("SubTrie with prefix empty '' is the same as original Trie.");
        
        AutoCompletionTrie S;
        
        AutoCompletionTrieNode currentNode = this.root;
        
        for(int i = 0; i < p.length(); i++){
            
            int index = getTrieNodeIndexOfLetter(p.charAt(i));
            
            if (currentNode.offspring[index] != null){
                currentNode = currentNode.offspring[index];
            }else{
                return null;
            }
        }
        
        S = new AutoCompletionTrie();
        S.root = currentNode;
        
        return S;
    }
    
    public String[] getAllWords(){
        return this.getAllWords(this.root, null);
    }
    
    public String[] getAllWords(AutoCompletionTrieNode Tn, String p){
        
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
        
        AutoCompletionTrieNode currentParent = this.root;
        
        for (int i = 0; i < key.length(); i++){
            
            char currentLetter = key.charAt(i);
            int index = getTrieNodeIndexOfLetter(currentLetter);
            boolean isLastLetter = (i == key.length()-1);
            
            AutoCompletionTrieNode nextParent = currentParent.offspring[index];
            
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
    
    
}

