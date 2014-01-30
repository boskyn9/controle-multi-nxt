package control

/**
 * Created with IntelliJ IDEA.
 * User: Will
 * Date: 30/01/14
 * Time: 00:40
 * To change this template use File | Settings | File Templates.
 */
class Action {

	public static final BACK = 'back'
	public static final FORWARD = 'forward'
	public static final STOP = 'stop'
	public static final AGAIN = 'again'
	public static final TURN_LEFT = 'turn_left'
	public static final TURN_RIGHT = 'turn_right'
	public static final LEFT = 'left'
	public static final RIGHT = 'right'
	public static final SPEED_LESS = 'speed less'
	public static final SPEED_PLUS = 'speed plus'
	public static final CLEAR = 'clear'
	public static final EXIT_NOW = 'exit now'

	String command
	Integer duration = null //in ms
	Double radius = null
	Integer speed = null //0..100

	void setSpeed(String speed) {
		Integer veloc = speed ? new Integer(speed) : null
		this.speed = veloc ? ([0..100].contains(veloc) ? veloc : null) : null
	}

	void setRadius(String radius) {
		this.radius = radius ? new Double(radius) : null
	}

	void setDuration(String duration) {
		this.duration = duration ? new Integer(duration) : null
	}

	String toString() {
		"(Comando: $command, Duração: $duration, Raio: $radius, Velocidade: $speed)"
	}
}
