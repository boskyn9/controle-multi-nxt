package control

import edu.cmu.sphinx.util.props.ConfigurationManager
import edu.cmu.sphinx.recognizer.Recognizer
import edu.cmu.sphinx.frontend.util.Microphone
import scripts.ControleUtil
import ch.aplu.nxtsim.*
import sun.awt.windows.ThemeReader
import synthesizer.Speaker

/**
 * User: boskyn9
 * Date: 02/12/11
 * Time: 00:54
 * To change this template use File | Settings | File Templates.
 */
class ControlOfVoice {

	Speaker speaker = Speaker.getInstance()
	def actions = new Actions()
	def gears = []
	def recognizer

	ControlOfVoice(gears) {
		this.gears = gears

		def cm = new ConfigurationManager(this.class.getResource("gram/controle.config.xml"));

		recognizer = (Recognizer) cm.lookup("recognizer");
		recognizer.allocate();

		def microphone = (Microphone) cm.lookup("microphone");
		if (!microphone.startRecording()) {
			println("Cannot start microphone.");
			recognizer.deallocate();
			System.exit(1);
		}

		start()

	}

	def start() {
		speaker.say("The system began!", true)
		//println "gears - $gears"

		while (true) {
			def result = recognizer.recognize()
			if (result) {
				String resultText = result.getBestFinalResultNoFiller();
				def listResutl = result.getResultTokens()
				if (resultText) {
					def toRobot = false
					println "Eu acho que é: $resultText \n"

					// limitação atual, o camando deve ser passado inteiramente
					def robotIndex
					def actionIndex

					if (resultText.startsWith("robot")) {
						robotIndex = resultText.indexOf("robot")
					}

					if (resultText.contains("action")) {
						actionIndex = resultText.indexOf("action")
					}

					if (robotIndex != null && actionIndex != null) {
						toRobot = true
						def indexRobotControl = ControleUtil.numberByName(resultText.substring(robotIndex, actionIndex))
						ControleUtil.addAction(resultText.substring(actionIndex), actions)
						println "opa $indexRobotControl -- ${gears.findAll{ it.key == indexRobotControl}}"
						ControleUtil.commands(actions, gears.findAll{ it.key == indexRobotControl})
					}

					if (!toRobot) {
						ControleUtil.addAction(resultText, actions)
						ControleUtil.commands(actions, gears) // chamar a excução de tarefas
					}

					// TODO identificar a palavra "action". Está será a finalização da declaração do robot e o incio das ações para o mesmo.

					println actions.retunrListActions() // exibir a lista de ações a serem executadas
					actions.clear() // limpando tarefas para recomeço.
				} else {
					println "I did not hear what was said.\n ResultText $resultText"
					//speaker.say('I did not hear what was said')
				}
			} else {
				println "I did not hear what was said.\n Result $result"
				//speaker.say('I did not hear what was said')
			}
		}
	}

}
