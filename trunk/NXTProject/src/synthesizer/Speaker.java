package synthesizer;
/**
 * Copyright 2003 Sun Microsystems, Inc.
 * 
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL 
 * WARRANTIES.
 */
import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;

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
                
                
                //Synthesizer synthesizer = Central.createSynthesizer(desc);
                FreeTTSEngineCentral central = new FreeTTSEngineCentral();
                EngineList list = central.createEngineList(desc); 
                if (list.size() > 0) { 
                    EngineCreate creator = (EngineCreate) list.get(0); 
                    synthesizer = (Synthesizer) creator.createEngine(); 
                }
        } catch (Exception ex) {
            Logger.getLogger(Speaker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static Speaker getInstance() {
       if(instance==null)
           instance = new Speaker();
       
       String voiceName = "kevin16";       
        
	try {
	    
	    if (synthesizer == null) {
		System.err.println("Cannot create synthesizer");
		System.exit(1);
	    }

	    synthesizer.allocate();
	    synthesizer.resume();

            desc = (SynthesizerModeDesc) synthesizer.getEngineModeDesc();
            Voice[] voices = desc.getVoices();
            Voice voice = null;
            for (int i = 0; i < voices.length; i++) {
                if (voices[i].getName().equals(voiceName)) {
                    voice = voices[i];
                    break;
                }
            }
            if (voice == null) {
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

	} catch (Exception e) {
	    e.printStackTrace();
	}
       
       return instance;       
    }
        
    public void say(String text){
        instance.say(text,null);
    }
    
    public void say(String text, Boolean plain){
        try {
            if(plain!=null && plain){
                System.out.println("if");
                synthesizer.speakPlainText(text, null);
            }else{
                System.out.println("else");
                synthesizer.speak(text,null);
            }
//            synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);            
        } catch (Exception ex) {
            Logger.getLogger(Speaker.class.getName()).log(Level.SEVERE, null, ex);
            this.instance = Speaker.getInstance();
        }
    }
    
    public static void main(String[] args) {
        Speaker s = Speaker.getInstance();
        s.say("The system began", null);
        Speaker s1 = Speaker.getInstance();
        s.say("now", null);
        s1.say("now, i don't", null);
        
        Speaker s2 = new Speaker();
        s2.say("it don't working");
        
        s.say("but, i work");
        s1.say("i work to");
        s2.say("and,i...");
        
    }
}