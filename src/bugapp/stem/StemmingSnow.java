/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bugapp.stem;

import java.util.HashSet;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

/**
 *
 * @author Henrique
 */
public class StemmingSnow extends BugAppStemmingFilter {
    
    protected static HashSet<String> lstStopWords = null;
    protected SnowballStemmer Stemmer;
    
    public StemmingSnow(){
        Stemmer = new englishStemmer();
        if(lstStopWords==null){
            makeStopWordList();
        }
    }
    
    private void makeStopWordList() {
        lstStopWords = new HashSet<String>();
        //Adaptado de: http://www.ranks.nl/resources/stopwords.html
        lstStopWords.add("about");
        lstStopWords.add("above");
        lstStopWords.add("after");
        lstStopWords.add("again");
        lstStopWords.add("against");
        lstStopWords.add("all");
        lstStopWords.add("and");
        lstStopWords.add("any");
        lstStopWords.add("are");
        lstStopWords.add("aren");
        lstStopWords.add("because");
        lstStopWords.add("been");
        lstStopWords.add("before");
        lstStopWords.add("being");
        lstStopWords.add("below");
        lstStopWords.add("between");
        lstStopWords.add("both");
        lstStopWords.add("but");
        lstStopWords.add("can");
        lstStopWords.add("cannot");
        lstStopWords.add("could");
        lstStopWords.add("couldn");
        lstStopWords.add("did");
        lstStopWords.add("didn");
        lstStopWords.add("does");
        lstStopWords.add("doesn");
        lstStopWords.add("doing");
        lstStopWords.add("don");
        lstStopWords.add("down");
        lstStopWords.add("during");
        lstStopWords.add("each");
        lstStopWords.add("few");
        lstStopWords.add("for");
        lstStopWords.add("from");
        lstStopWords.add("further");
        lstStopWords.add("had");
        lstStopWords.add("hadn");
        lstStopWords.add("has");
        lstStopWords.add("hasn");
        lstStopWords.add("have");
        lstStopWords.add("haven");
        lstStopWords.add("having");
        lstStopWords.add("her");
        lstStopWords.add("here");
        lstStopWords.add("hers");
        lstStopWords.add("herself");
        lstStopWords.add("him");
        lstStopWords.add("himself");
        lstStopWords.add("his");
        lstStopWords.add("how");
        lstStopWords.add("into");
        lstStopWords.add("isn");
        lstStopWords.add("its");
        lstStopWords.add("itself");
        lstStopWords.add("let");
        lstStopWords.add("more");
        lstStopWords.add("most");
        lstStopWords.add("mustn");
        lstStopWords.add("myself");
        lstStopWords.add("nor");
        lstStopWords.add("not");
        lstStopWords.add("off");
        lstStopWords.add("once");
        lstStopWords.add("only");
        lstStopWords.add("other");
        lstStopWords.add("ought");
        lstStopWords.add("ourselves");
        lstStopWords.add("out");
        lstStopWords.add("over");
        lstStopWords.add("own");
        lstStopWords.add("same");
        lstStopWords.add("shan");
        lstStopWords.add("she");
        lstStopWords.add("should");
        lstStopWords.add("shouldn");
        lstStopWords.add("so");
        lstStopWords.add("some");
        lstStopWords.add("such");
        lstStopWords.add("than");
        lstStopWords.add("that");
        lstStopWords.add("the");
        lstStopWords.add("their");
        lstStopWords.add("theirs");
        lstStopWords.add("them");
        lstStopWords.add("themselves");
        lstStopWords.add("then");
        lstStopWords.add("there");
        lstStopWords.add("these");
        lstStopWords.add("they");
        lstStopWords.add("this");
        lstStopWords.add("those");
        lstStopWords.add("through");
        lstStopWords.add("too");
        lstStopWords.add("under");
        lstStopWords.add("until");
        lstStopWords.add("very");
        lstStopWords.add("was");
        lstStopWords.add("wasn");
        lstStopWords.add("were");
        lstStopWords.add("weren");
        lstStopWords.add("what");
        lstStopWords.add("when");
        lstStopWords.add("where");
        lstStopWords.add("which");
        lstStopWords.add("while");
        lstStopWords.add("who");
        lstStopWords.add("whom");
        lstStopWords.add("why");
        lstStopWords.add("with");
        lstStopWords.add("won");
        lstStopWords.add("would");
        lstStopWords.add("wouldn");
        lstStopWords.add("you");
        lstStopWords.add("your");
        lstStopWords.add("yours");
        lstStopWords.add("yourself");
        lstStopWords.add("yourselves");

    }
    
    @Override
    public String processWord(String Word){
        Word = removeExtraPoint(Word);
        if(Word.length() <= 2 || isStopWord(Word)){
            return null;
        }
        else{
            Stemmer.setCurrent(Word);
            Stemmer.stem();
            return Stemmer.getCurrent();
        }
    }
    
    private boolean isStopWord(String Word){
        return lstStopWords.contains(Word);
    }
    
    /**
     * Remove a point at the end of the word
     * @param Word
     * @return 
     */
    private String removeExtraPoint(String Word){
        while(Word.endsWith("=") || Word.endsWith("!") || Word.endsWith("?") || Word.endsWith(".") || Word.endsWith(":") || Word.endsWith("/") || Word.endsWith("-") || Word.endsWith("_")){
            //O ponto era pontuação de fim de frase mesmo
            //então remover o ponto do final da palavra
            Word = Word.substring(0, Word.length()-1);
        }
        
        while(Word.startsWith("=") || Word.startsWith("!") || Word.startsWith("?") || Word.startsWith(".") || Word.startsWith(":") || Word.startsWith("/") || Word.startsWith("-") || Word.startsWith("_")){
            //O ponto era pontuação no começo de uma frase (ou lixo mesmo)
            //então remover o ponto do começo
            Word = Word.substring(1);
        }

        return Word.trim();
        
    }
    
    /*
     * Test para ver se o Stemming funciona
     */
    public static void main(String[] args){
        SnowballStemmer S = new englishStemmer();
        S.setCurrent("objects");
        System.out.println(S.stem());
        System.out.println(S.getCurrent());

        S.setCurrent("org.process.objects");
        System.out.println(S.stem());
        System.out.println(S.getCurrent());
    }
}
