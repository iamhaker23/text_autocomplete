
package dsacoursework2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 *
 * @author ajb
 * @author hdb
 * 
 */
public class DictionaryFinder {

    HashMap<String, Integer> dictionary;

    public DictionaryFinder(){
    }
    /**
     * Reads all the words in a comma separated text document into an Array
     * @param f 
     * 
     */   
    public static ArrayList<String> readWordsFromCSV(String file, char delimiter) throws FileNotFoundException {
        Scanner sc=new Scanner(new File(file));
        sc.useDelimiter(" |"+delimiter);
        ArrayList<String> words=new ArrayList<>();
        String str;
        while(sc.hasNext()){
            str=sc.next();
            str=str.trim();
            str=str.toLowerCase();
            words.add(str);
        }
        return words;
    }
    
    public static ArrayList<String> readWordsFromCSV(String file) throws FileNotFoundException {
        return readWordsFromCSV(file, ',');
    }
    
    
    public static void saveCollectionToFile(Collection<?> c,String file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter printWriter = new PrintWriter(fileWriter);
         for(Object w: c){
            printWriter.println(w.toString());
         }
        printWriter.close();
     }
    
    public static void saveMapToFile(HashMap<String, HashMap<String, Float>> c,String file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        PrintWriter printWriter = new PrintWriter(fileWriter);
         for(String w : c.keySet()){
            printWriter.print("\n" + w.toString() + ",");
            HashMap<String, Float> h = c.get(w);
            for (String word : h.keySet()){
                
                //TODO: will output incorrectly for any repeating words such as 'haha'
                String display = ((word.equals(w))? word : w + word);
                
                printWriter.print(display + "," + h.get(word) + ",");
            }
            
         }
        printWriter.close();
     }

     public void formDictionary(ArrayList<String> words){

         dictionary = new HashMap();

         for (int i = 0; i < words.size(); i++){
             Integer currentCount = dictionary.get(words.get(i));
             if (currentCount == null) currentCount = 0;
             dictionary.put(words.get(i), currentCount+1);
         }
     }
     
    public static HashMap<String, Integer> formDictionary(HashMap<String, Integer> wordFrequencies, String prefix){

         HashMap<String, Integer> dict = new HashMap();

         for (String key : wordFrequencies.keySet()){
             
             if (key != null){
                
                Integer currentCount = wordFrequencies.get(key);
                Integer curr = dict.get(key);

                if (currentCount == null) currentCount = 0;
                if (curr == null) curr = 0;
               
                dict.put(key, currentCount + curr);
                
             }
         }
         
         return dict;
     }
    
     public static HashMap<String, Integer> formDictionary(String[] words){
         DictionaryFinder df = new DictionaryFinder();
         df.formDictionary(new ArrayList<>(Arrays.asList(words)));
         return df.dictionary;
     }

     public void saveToFile(){

         if (dictionary != null){

             List<String> keys = Arrays.asList(dictionary.keySet().toArray(new String[0]));

             Comparator<String> c = new Comparator<String>() {
                 @Override
                 public int compare(String a, String b) {

                     //negative, zero, or a positive 
                     //as the first argument is
                     //less than, equal to, or greater 
                     //than the second.

                     String smallest = (a.length() <= b.length()) ? a : b;
                     String longest = (smallest.equals(a)) ? b : a;

                     for (int i = 0; i < longest.length(); i++){
                         
                         if (smallest.length() <= i){
                             //equal to this point, but longest contains more letters so should appear later
                             return (longest.equals(b)) ? -1 : 1 ;
                         }
                         
                         if (longest.charAt(i) > smallest.charAt(i)){
                             //longest is bigger
                             return (longest.equals(b)) ? -1 : 1 ;
                             
                         }else if (longest.charAt(i) < smallest.charAt(i)){
                             //longest is smaller
                             return (longest.equals(a)) ? -1 : 1 ;
                         }
                     }
                     return 0;
                 }
             };

             keys.sort(c);

             ArrayList<WordFrequency> wordFreqs = new ArrayList<WordFrequency>();
             
             for (String key : keys){
                 wordFreqs.add(new WordFrequency(key, dictionary.get(key)));
             }
             
             String filePath = getLocalResourcesPath("lotrWordFrequencies.csv");
             try{
                 
                 saveCollectionToFile(wordFreqs, filePath);
             }catch(IOException e){
                 System.out.println("FATAL ERROR: Could not save to " + filePath);
             }

         }

     }

    private class WordFrequency{
        String word = null;
        int frequency = -1;
        
        public WordFrequency(String word, int frequency){
            this.word = word;
            this.frequency = frequency;
        }
        
        @Override
        public String toString(){
            if (word != null && frequency != -1){
                return word + ",\t"+Integer.toString(frequency);
            }else return "Invalid Word Frequency:"+word+", "+frequency;
        }
        
    }
    
    public static String getLocalResourcesPath(String file){
        return (new File("")).getAbsolutePath() +  File.separatorChar + "src" + File.separatorChar + "resources" +  File.separatorChar + file;
    }

    public static void main(String[] args) throws Exception {

        DictionaryFinder df=new DictionaryFinder();

        String inFileName = getLocalResourcesPath("lotr.csv");

        ArrayList<String> words = readWordsFromCSV(inFileName);

        df.formDictionary(words);

        df.saveToFile();

        

    }

}
