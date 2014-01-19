package control

class Actions {

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

	private Queue listOfAction = new LinkedList()
	private def lastAction = null


	def offer = { action ->
		action == CLEAR ? listOfAction.clear() : listOfAction.offer(action)
	}

	def poll = {
		if (listOfAction.element() != AGAIN) {
			lastAction = listOfAction.element()
		}
		return listOfAction.poll()
	}

	def clear() {
		listOfAction.clear()
	}

	def retunrListActions() {
		listOfAction.toListString()
	}

	def again = {
		lastAction
	}

	def toArray() {
		listOfAction.toArray()
	}
}

