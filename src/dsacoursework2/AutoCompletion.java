package dsacoursework2;

import static dsacoursework2.DictionaryFinder.getLocalResourcesPath;
import static dsacoursework2.DictionaryFinder.readWordsFromCSV;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Hakeem
 */
public class AutoCompletion {
    
    public static HashMap<String, Float> getBestMatches(HashMap<String, Float> allMatches, int maxBest){
             
        HashMap<String, Float> output = new HashMap<>();
        String[] best = new String[maxBest];
        
        for (String key : allMatches.keySet()){
            
            Float current = allMatches.get(key);
                    
            for (int i = 0; i < best.length; i++){
                if (current != null && ((best[i] == null) || (allMatches.get(best[i]) < current))){
                    best[i] = key;
                    i = best.length;
                }
            }
        }
        
        for (String bestMatch : best){
            output.put(bestMatch, allMatches.get(bestMatch));
        }
        
        return output;
    }
    
    public static void printBestThreeMatches(HashMap<String, Float> matches, String query){
            HashMap<String, Float> bestMatches = getBestMatches(matches, 3);
            
            System.out.println("Given: "+query);
            for (String key : bestMatches.keySet()){
                if (key != null){
                    
                    //TODO: will output incorrectly for any repeating words such as 'haha'
                    String display = (key.equals(query)) ? key : query + key;
                    
                    if (key != null) System.out.println("\t"+display+"("+bestMatches.get(key)+")");
                }
            }
    }
    
    public static void main(String[] args) throws Exception{
        
        DictionaryFinder df=new DictionaryFinder();
        
        String inFileName = getLocalResourcesPath("lotr.csv");
        String queryFileName = getLocalResourcesPath("lotrQueries.csv");

        ArrayList<String> words = readWordsFromCSV(inFileName);

        df.formDictionary(words);

        AutoCompletionTrie T = new AutoCompletionTrie();
        
        for (String word : df.dictionary.keySet()){
            
            T.add(word, df.dictionary.get(word), false);
            
        }
        
        ArrayList<String> queries =  readWordsFromCSV(queryFileName, (char)13);
        
        HashMap<String, HashMap<String, Float>> autoCompleteMatches = new HashMap<>();
        
        for (String query : queries){
            
            /*for (String val : T.getSubTrie(query).getAllWords()){
                System.out.println(val);
            }
            System.out.println("\n" + query + "\n");*/
            
            HashMap<String, Float> matches = T.getBestMatches(query);
            
            if (matches != null){
                
                autoCompleteMatches.put(query, matches);

                printBestThreeMatches(matches, query);
                
            }
            else System.out.println("Could not autocomplete word: '"+query+"'");
            
        }
        
        DictionaryFinder.saveMapToFile(autoCompleteMatches, getLocalResourcesPath("lotrMatches.csv"));
        
        //df.saveToFile();
        
    }
    
    
}
