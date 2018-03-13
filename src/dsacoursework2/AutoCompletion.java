package dsacoursework2;

import static dsacoursework2.DictionaryFinder.getLocalResourcesPath;
import static dsacoursework2.DictionaryFinder.readWordsFromCSV;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

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
                
                if (current != null && ((best[i] == null) || (current.compareTo(allMatches.get(best[i]))) > 0) && (Arrays.asList(best).indexOf(key) == -1)){
                    
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
            
            if (word != null && !word.equals("")) T.add(word, df.dictionary.get(word), false);
            
        }
        
        ArrayList<String> queries =  readWordsFromCSV(queryFileName, '\n');
        
        HashMap<String, HashMap<String, Float>> autoCompleteMatches = new HashMap<>();
        
        for (String query : queries){
            
            if (query != null && !query.equals("")){
                
                query = query.trim();

                /*for (String val : T.getSubTrie(query).getAllWords()){
                    System.out.println(val);
                }
                System.out.println("\n" + query + "\n");*/

                HashMap<String, Float> matches = T.getBestMatches(query);

                LinkedHashMap<String, Float> sortedMatches = sortMatches(matches);

                if (sortedMatches != null){

                    autoCompleteMatches.put(query, sortedMatches);
                    System.out.println("Given:" + query);
                    String[] sortedKeys = sortedMatches.keySet().toArray(new String[0]);
                    for (int i = 0; i < 3; i++){
                        if (sortedKeys.length > i) printMatch(sortedKeys[i], sortedMatches.get(sortedKeys[i]), query);
                    }

                }
                else System.out.println("Could not autocomplete word: '"+query+"'");
            
            //TODO: perhaps System.err.println()
            } else System.out.println("Skipping invalid query value: '"+query+"'");
        }

            DictionaryFinder.saveMapToFile(autoCompleteMatches, getLocalResourcesPath("lotrMatches.csv"));

            //df.saveToFile();
    }
    
    private static void printMatch(String match, Float value, String query){
        System.out.println("\t" + ((query.equals(match))?"" : query) + match + "(" + value + ")");
    }
    
    //helper function to sort matches in order of highest probability (and alphabetically in the case of a tie)
    private static LinkedHashMap<String, Float> sortMatches(HashMap<String, Float> matches){
        
        LinkedHashMap<Float, ArrayList<String>> probsToStrings = new LinkedHashMap<>();
        
        LinkedHashMap<String, Float> sortedMatches = new LinkedHashMap<>();
        
        //TODO: sort
        
        for (String key : matches.keySet()){
            
            Float probability = matches.get(key);
            
            if (probability !=null){
                if (probsToStrings.get(probability) != null){
                    probsToStrings.get(probability).add(key);
                }else probsToStrings.put(probability, new ArrayList<>(Arrays.asList(new String[]{key})));
            }
            
        }
        
        ArrayList<Float> sortedKeys = new ArrayList<>(Arrays.asList(probsToStrings.keySet().toArray(new Float[0])));
        
        //TODO: refactor this to use comparators or something nicer
        //Sort in descending order
        Collections.sort(sortedKeys);
        Collections.reverse(sortedKeys);
        
        for (Float key : sortedKeys){
            
            //alphabetically sort the matches for this probability
            List<String> words = probsToStrings.get(key);
            Collections.sort(words);
            
            for (String word : words){
                sortedMatches.put(word, key);
            }
        }
        
        return sortedMatches;
    }
    
    
}
