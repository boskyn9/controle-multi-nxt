package synthesizer;

import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;

import de.dfki.lt.freetts.en.us.MbrolaVoice;
import de.dfki.lt.freetts.mbrola.MbrolaAudioOutput;
import java.util.Locale;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.speech.EngineCreate;
import javax.speech.EngineList;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

public class Speaker {

    private static Synthesizer synthesizer =null;
    private static SynthesizerModeDesc desc =null;
    private static Speaker instance = null;

    
    private Speaker() {
        try {
            desc = new SynthesizerModeDesc(
                    null,          // engine name
                    "general",     // mode name
                    Locale.US,     // locale
                    null,          // running
                    null);         // voice
                
//            System.setProperty("mbrola.base", ""); 
                
                //Synthesizer synthesizer = Central.createSynthesizer(desc);
                FreeTTSEngineCentral central = new FreeTTSEngineCentral();
                EngineList list = central.createEngineList(desc); 
                if (list) { 
                    EngineCreate creator = (EngineCreate) list.getAt(0); 
                    synthesizer = (Synthesizer) creator.createEngine(); 
                }
        } catch (ex) {
            Logger.getLogger(Speaker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static Speaker getInstance() {
       if(!instance)
           instance = new Speaker();
       
       String voiceName = "kevin16";       
        
	try {
	    
	    if (!synthesizer) {
		System.err.println("Cannot create synthesizer");
		System.exit(1);
	    }

	    synthesizer.allocate();
	    synthesizer.resume();            
            
            desc = (SynthesizerModeDesc) synthesizer.getEngineModeDesc();
            Voice[] voices = desc.getVoices();
            Voice voice = null;
            
            println voices
            
            voice = voices.find { it.name.equals(voiceName) }
            
            if (!voice) {
                System.err.println(
                    "Synthesizer does not have a voice named "
                    + voiceName + ".");
                System.exit(1);
            }
            synthesizer.getSynthesizerProperties().setVoice(voice);
            synthesizer.getSynthesizerProperties().setVolume(1f);
            synthesizer.getSynthesizerProperties().setSpeakingRate(150);
            synthesizer.getSynthesizerProperties().setPitchRange(200);
            synthesizer.getSynthesizerProperties().setPitch(15);

	} catch (e) {
	    e.printStackTrace();
	}
       
       return instance;       
    }
     
    public void say(String text){
        instance.say(text,null,false);
    }
    
    public void say(String text,boolean log){
        instance.say(text,null,log);
    }
    
    public void say(String text, Boolean plain, boolean log){
        try {
            if(plain!=null && plain){
                synthesizer.speakPlainText(text, null);
            }else{
                synthesizer.speak(text,null);
            }
            if(log)
                println text
//            synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);            
        } catch (ex) {
            Logger.getLogger(Speaker.class.getName()).log(Level.SEVERE, null, ex);
            this.instance = Speaker.getInstance();
        }
    }
    
    public static void main(String[] args) {
        Speaker s = Speaker.getInstance();
        s.say("The system began",true);
//        Speaker s1 = Speaker.getInstance();
        s.say("speed more bosco",true);
//        s1.say("now, i don't", null);
        
//        Speaker s2 = new Speaker();
//        s2.say("it don't working");
        
//        s.say("but, i work");
//        s1.say("i work to");
//        s2.say("and,i...");
        
    }
}