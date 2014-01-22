/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package control

/**
 *
 * @author boskyn9
 */

class RobotsControl {

	public static final GEAR = 'gear'
	public static final MOTOR = 'motor'
	public static final SOUND_SENSOR = 'sound'
	public static final LIGHT_SENSOR = 'light'
	public static final TOUCH_SENSOR = 'touch'
	public static final COLOR_SENSOR = 'color'
	public static final ULTRASONIC_SENSOR = 'ultrasonic'
	public static final GYRO_SENSOR = 'gyro'

	public static final PORT_A = 'PORT_A'
	public static final PORT_B = 'PORT_B'
	public static final PORT_C = 'PORT_C'

	public static final PORT_1 = 'PORT_1'
	public static final PORT_2 = 'PORT_2'
	public static final PORT_3 = 'PORT_3'

	public static final boolean validatePorts(port) {
		return [PORT_A, PORT_B, PORT_C, PORT_1, PORT_2, PORT_3].contains(port)
	}

	public static final boolean validateParts(part) {
		return [GEAR, MOTOR, SOUND_SENSOR, LIGHT_SENSOR, TOUCH_SENSOR, COLOR_SENSOR, ULTRASONIC_SENSOR, GYRO_SENSOR].contains(part)
	}

}
