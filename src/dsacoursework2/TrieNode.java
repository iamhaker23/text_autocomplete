package dsacoursework2;

/**
 *
 * @author Hakeem
 */
public class TrieNode {
    
    public TrieNode[] offspring;
    private boolean isCompleteKey;
    
    public TrieNode(boolean isCompleteKey){
        this.offspring = new TrieNode[26];
        this.isCompleteKey = isCompleteKey;
    }
    
    public void setCompleteKey(boolean isCompleteKey){
        this.isCompleteKey = isCompleteKey;
    }
    
    public boolean isCompleteKey(){
        return this.isCompleteKey;
    }
}
