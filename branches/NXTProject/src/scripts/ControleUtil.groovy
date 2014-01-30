package scripts

import ch.aplu.nxtsim.Gear
import com.esotericsoftware.yamlbeans.YamlReader
import control.Action

//import ch.aplu.nxt.Gear
import control.Actions
import synthesizer.Speaker

/**
 * (one | two | three | four | five | six | seven | eight | nine | zero | oh | nil )
 *
 */

static def numberByName(String number) {
	number = number.replaceFirst("robot ", "")
	println "numero - $number"

	switch (number.trim()) {
	case ~/.*zero$/:
	case ~/.*oh$/:
	case ~/.*nil$/: return '0'
		break
	case ~/.*one$/: return '1'
		break
	case ~/.*two$/: return '2'
		break
	case ~/.*three$/: return '3'
		break
	case ~/.*four$/: return '4'
		break
	case ~/.*five$/: return '5'
		break
	case ~/.*six$/: return '6'
		break
	case ~/.*seven$/: return '7'
		break
	case ~/.*eight$/: return '8'
		break
	case ~/.*nine$/: return '9'
		break
	default: return null
		break
	}
}

static def addAction(Action action, Actions actions) {
	//Speaker speaker = Speaker.getInstance()
	//speaker.say("you said it: $opt ?", true)
	action.command = action?.command?.replaceFirst("action ", "")
	switch (action.command) {
	case ~/^forward.*$/:
		//println 'vou andar para frente'
		action.command = Action.FORWARD
		break
	case ~/^back.*$/:
		//println 'vou andar para tras'
		action.command = Action.BACK
		break
	case ~/^stop.*$/:
		//println 'vou parar'
		action.command = Action.STOP
		break
	case ~/^turn left.*$/:
		//println 'vou virar para esquerda'
		action.command = Action.TURN_LEFT
		break
	case ~/^turn right.*$/:
		//println 'vou virar para a direita'
		action.command = Action.TURN_RIGHT
		break
	case ~/^left.*$/:
		//println 'esquerda'
		action.command = Action.LEFT
		break
	case ~/^right.*$/:
		//println 'direita'
		action.command = Action.RIGHT
		break
	case ~/^more.*$/:
		//println 'aumentar velocidade'
		action.command = Action.SPEED_PLUS
		break
	case ~/^less.*$/:
		//println 'diminuir velocidade'
		action.command = Action.SPEED_LESS
		break
	case ~/^exit now.*$/:
		//println 'xau'
		action.command = Action.EXIT_NOW
		break
	case ~/^clear.*$/:
		//println 'limpar tarefas'
		action.command = Action.CLEAR
		break
	default:
		def reader = new YamlReader(new File("src/control/command/comandos.yaml").text)
		boolean achou = false
		while (true) {
			Map command = (Map) reader.read()
			if (command == null) break
			//println 'Complexo: ' + command.complexo
			if (action.command == command.complexo) {
				achou = true
				def listSimples = command.simples
				listSimples?.each { simples ->
					//println 'Simples: ' + simples?.comando
					//println 'Duração: ' + simples?.duracao
					//println 'Raio: ' + simples?.raio
					//println 'Velocidade: ' + simples?.velocidade //0..100
					if (simples?.comando) {
						action = new Action()
						action.command = simples?.comando
						action.setDuration(simples?.duracao ?: null)
						action.setRadius(simples?.raio ?: null)
						action.setSpeed(simples?.velocidade ?: null)
						addAction(action, actions)
					}
				}
			}
		}
		if (achou) {
			return
		}
		def number = numberByName(action.command)
		if (number) {
			//actions.offer(number)
			println number
		} else {
			//speaker.say("I don't understand", true)
		}
		break
	}

	actions.offer(action)
}

static void commands (Actions actions, gears) {
	actions.toArray().each { action ->
		gears.values().each(){ gear ->
			make(action as Action, actions, gear)
		}
		Thread.sleep(1000L)
	}
}

private static void make(Action action, Actions actions, gear) {
	if (action) {
		println 'Action: ' + action
		//pensar como colocar o action.speed... se vai no forward e backward apenas ou geral tipo fora do switch mesmo
		Thread.start {
			switch (action.command) {
			case Action.BACK:
				action.duration ? gear.backward(action.duration) : gear.backward()
				break
			case Action.FORWARD:
				action.duration ? gear.forward(action.duration) : gear.forward()
				break
			case Action.STOP:
				gear.stop()
				break
			case Action.TURN_LEFT:
				action.radius ? gear.leftArc(action.radius) : gear.leftArc(0.45)
				break
			case Action.TURN_RIGHT:
				action.radius ? gear.rightArc(action.radius) : gear.rightArc(0.45)
				break
			case Action.LEFT:
				action.duration ? gear.left(action.duration) : gear.left(100)
				break
			case Action.RIGHT:
				action.duration ? gear.right(action.duration) : gear.right(100)
				break
			case Action.SPEED_PLUS:
				println "velocidade $gear.speed"
				gear.speed += 10
				println "nova velocidade $gear.speed"
				break
			case Action.SPEED_LESS:
				println "velocidade $gear.speed"
				gear.speed -= 10
				println "nova velocidade $gear.speed"
				break
			case Action.CLEAR:
				actions.clear()
				break
			case Action.EXIT_NOW:
				System.exit(0)
				break
			default:
				break
			}
		}
	}
}