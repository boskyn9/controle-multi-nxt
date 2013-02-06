package scripts

import control.Actions
import ch.aplu.nxtsim.NxtRobot
import ch.aplu.nxtsim.Gear
import ch.aplu.nxtsim.Tools
import synthesizer.Speaker

/**
 * (one | two | three | four | five | six | seven | eight | nine | zero | oh | nil )
 *
 */

static def numberByName(number){
    switch (number) {
        case ~/.*zero$/:
        case ~/.*oh$/:
        case ~/.*nil$/: '0'
            break
        case ~/.*one$/: '1'
            break
        case ~/.*two$/: '2'
            break
        case ~/.*three$/: '3'
            break
        case ~/.*four$/: '4'
            break
        case ~/.*five$/: '5'
            break
        case ~/.*six$/: '6'
            break
        case ~/.*seven$/: '7'
            break
        case ~/.*eight$/: '8'
            break
        case ~/.*nine$/: '9'
            break
        default: null
            break
    }
}

static def addAction(opt, actions) {
    Speaker speaker = Speaker.getInstance()
    speaker.say("you said it: $opt ?", true)
    
    switch (opt) {
        case ~/^forward.*$/:
            println 'vou andar para frente'
            actions.offer(Actions.FORWARD)
            break
        case ~/^back.*$/:
            println 'vou andar para tras'
            actions.offer(Actions.BACK)
            break
        case ~/^stop.*$/:
            println 'vou parar'
            actions.offer(Actions.STOP)
            break
        case ~/^turn left.*$/:
            println 'vou virar para esquerda'
            actions.offer(Actions.TURN_LEFT)
            break
        case ~/^turn right.*$/:
            println 'vou virar para a direita'
            actions.offer(Actions.TURN_RIGHT)
            break
        case ~/^left.*$/:
            println 'esquerda'
            actions.offer(Actions.LEFT)
            break
        case ~/^right.*$/:
            println 'direita'
            actions.offer(Actions.RIGHT)
            break
        case ~/^speed plus.*$/:
            println 'aumentar velocidade'
            actions.offer(Actions.SPEED_PLUS)
            break
        case ~/^speed less.*$/:
            println 'diminuir velocidade'
            actions.offer(Actions.SPEED_LESS)
            break
        case ~/^exit now.*$/:
            println 'xau'
            actions.offer(Actions.EXIT_NOW)
            break
        case ~/^clear.*$/:
            println 'limpar tarefas'
            actions.offer(Actions.CLEAR)
            break
        default:
            def number = numberByName(opt)
            if(number){
                actions.offer(number)
                println number
            }else{
                println "I don't understand"
                speaker.say("I don't understand")
            }
            break
    }
}


static void commands (Actions actions, gear) {
    actions.toArray.call().each { action ->
        println gear
        if(gear instanceof Gear)
            make(action, gear)
        else{
            gear.each(){
                make(action, it)
            }
        }
    }
}


private static void make(action,Gear gear){
    if(action){
        println action
        Thread.start {
            switch (action) {
                case Actions.BACK:
                    gear.backward()
                    break
                case Actions.FORWARD:
                    gear.forward()
                    break
                case Actions.STOP:
                    gear.stop()
                    break
                case Actions.TURN_LEFT:
                    gear.leftArc(0.45)
                    break
                case Actions.TURN_RIGHT:
                    gear.rightArc(0.45)
                    break
                case Actions.LEFT:
                    gear.left(100)
                    break
                case Actions.RIGHT:
                    gear.right(100)
                    break
                case Actions.SPEED_PLUS:
                    println "velocidade $gear.speed"
                    gear.speed += 10
                    println "nova velocidade $gear.speed"
                    break
                case Actions.SPEED_LESS:
                    println "velocidade $gear.speed"
                    gear.speed -= 10
                    println "nova velocidade $gear.speed"
                    break
                case Actions.CLEAR:
                    actions.clear.call()
                    break
                case Actions.EXIT_NOW:
                    System.exit(0)
                    break
                default:
                    break
            }
        }
    }
}