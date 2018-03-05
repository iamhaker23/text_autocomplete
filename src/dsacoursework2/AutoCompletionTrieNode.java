package dsacoursework2;

/**
 *
 * @author Hakeem
 */
public class AutoCompletionTrieNode{
    
    private int frequency;
    public AutoCompletionTrieNode[] offspring;
    private boolean isCompleteKey;
    
    
    public AutoCompletionTrieNode(int frequency){
        this.offspring = new AutoCompletionTrieNode[26];
        this.isCompleteKey = (frequency != -1);
        this.frequency = frequency;
    }
    
    public int getFrequency(){
        return this.frequency;
    }
    
    public void setFrequency(int frequency){
        this.isCompleteKey = (frequency >= 0);
        this.frequency = frequency;
    }
    
    
    
    public void setCompleteKey(boolean isCompleteKey){
        this.isCompleteKey = isCompleteKey;
    }
    
    public boolean isCompleteKey(){
        return this.isCompleteKey;
    }
    
}
