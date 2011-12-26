package control

import edu.cmu.sphinx.util.props.ConfigurationManager
import edu.cmu.sphinx.recognizer.Recognizer
import edu.cmu.sphinx.frontend.util.Microphone
import scripts.ControleUtil
import ch.aplu.nxtsim.*
import sun.awt.windows.ThemeReader
import synthesizer.Speaker

/**
 * Created by IntelliJ IDEA.
 * User: boskyn9
 * Date: 02/12/11
 * Time: 00:54
 * To change this template use File | Settings | File Templates.
 */
class ControlOfVoice{

    Speaker speaker = Speaker.getInstance()
    
    static void main(def args){
        // TODO adicionar criação de cenario.
        
        //        NxtContext.setLocation(10, 10);
        //        NxtContext.setStartDirection(5);
        //        NxtContext.setStartPosition(100, 240);
        //        NxtContext.useObstacle(NxtContext.box);
        
        new ControlOfVoice()
    }

    def actions = new Actions()

    ControlOfVoice(){
        def cm = new ConfigurationManager(this.class.getResource("gram/controle.config.xml"));


        def recognizer = (Recognizer) cm.lookup("recognizer");
        recognizer.allocate();

        def microphone = (Microphone) cm.lookup("microphone");
        if (!microphone.startRecording()) {
            println("Cannot start microphone.");
            recognizer.deallocate();
            System.exit(1);
        }

        println "The system began!"
        start(recognizer)
    }

    def start(recognizer){
        NxtRobot robot = new NxtRobot()
        Gear gear = new Gear()
        robot.addPart(gear)
        
        speaker.say("The system began!",null)
        
        // TODO criar o robo adicionando sensores...

        while (true) {
            def result = recognizer.recognize()
            if (result) {
                def resultText = result.getBestFinalResultNoFiller();
                def listResutl = result.getResultTokens()
                if (resultText) {
                    //println "Eu acho que é: $resultText \n"
                    ControleUtil.addAction(resultText, actions)
                    ControleUtil.commands(actions, robot, gear); // chamar a excução de tarefas
                    println actions.retunrListActions.call() // exibir a lista de ações a serem executadas
                    actions.clear.call() // limpando tarefas para recomeço.
                } else
                println "I did not hear what was said.\n"
               // speaker.say('I did not hear what was said')

            } else {
                println "I did not hear what was said.\n"
                //speaker.say('I did not hear what was said')
            }
        }
    }
}
