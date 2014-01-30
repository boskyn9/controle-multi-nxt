package control

class Actions {

	private Queue listOfAction = new LinkedList()
	private def lastAction = null

	def offer = { action ->
		action == Action.CLEAR ? listOfAction.clear() : listOfAction.offer(action)
	}

	def poll = {
		if (listOfAction.element() != Action.AGAIN) {
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

