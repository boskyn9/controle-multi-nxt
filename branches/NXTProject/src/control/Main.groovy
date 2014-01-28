package control

import ch.aplu.nxtsim.*
import com.esotericsoftware.yamlbeans.YamlReader
import scripts.RobotUtil
/**
 *
 * @author boskyn9
 */
class Main {

	static void main(args) {

		def robots = []
		try {
			//YamlReader reader = new YamlReader(new FileReader("model/robot.yml"));
			def reader = new YamlReader(new File("src/control/model/robot.yaml").text)

			while (true) {
				Map objeto = (Map) reader.read()
				if (objeto == null) break
				NxtRobot robot = new NxtRobot(objeto.name)
				def parts = objeto.nxt_robot
				parts.each { part ->
					String partTemp = ""
					String portTemp = ""
					String numberOrLetter = ""
					try {
						partTemp = part.type?.toString()?.toLowerCase()
						if (!RobotsControl.validateParts(partTemp)) throw new Exception("Motor desconhecido")

						portTemp = part.port?.toString()?.toUpperCase()
						if (!RobotsControl.validatePorts(portTemp)) throw new Exception("Porta desconhecida")

						int last_ = portTemp.lastIndexOf('_')
						numberOrLetter = portTemp.substring(last_ + 1)
					} catch(e) { println e.message }

					def port = null
					switch (portTemp) {
						case RobotsControl.PORT_A:
						case RobotsControl.PORT_B:
						case RobotsControl.PORT_C:
							port = MotorPort."$numberOrLetter"
							break
						case RobotsControl.PORT_1:
						case RobotsControl.PORT_2:
						case RobotsControl.PORT_3:
							port = SensorPort."S$numberOrLetter"
							break
						default:
							break
					}

					switch (partTemp) {
						case RobotsControl.GEAR:
							def gear = new Gear()
							robot.addPart(gear)
							break;
						case RobotsControl.MOTOR:
							def motor = new Motor(port as MotorPort)
							robot.addPart(motor)
							break;
						case RobotsControl.SOUND_SENSOR:
							def soundSendor = new SoundSensor(port as SensorPort)
							robot.addPart(soundSendor)
							break
						case RobotsControl.LIGHT_SENSOR:
							def lightSensor = new LightSensor(port as SensorPort)
							robot.addPart(lightSensor)
							break
						case RobotsControl.TOUCH_SENSOR:
							def touchSensor = new TouchSensor(port as SensorPort)
							robot.addPart(touchSensor)
							break
						case RobotsControl.COLOR_SENSOR:
							//def colorSensor = new ColorSensor(port as SensorPort)
							robot.addPart(colorSensor)
							break
						case RobotsControl.ULTRASONIC_SENSOR:
							//def ultrasonicSensor = new UltrasonicSensor(port as SensorPort)
							//robot.addPart(ultrasonicSensor)
							break
						case RobotsControl.GYRO_SENSOR://nao suportado no simulador
							//def gyroSensor = new GyroSensor(port as SensorPort)
							//robot.addPart(gyroSensor)
							break
						default:
							break
					}
				}

				robots.add(robot)
			}
		} catch (e) {
			e.printStackTrace()
		}

		//Speaker speaker = Speaker.getInstance()

		/*NxtRobot robot = new NxtRobot('nxt1')
		Gear gear = new Gear()
		robot.addPart(gear)*/
		/*TouchSensor ts = new TouchSensor(SensorPort.S3)
		robot.addPart(ts)*/

		/*NxtRobot robot2 = new NxtRobot('nxt2')
		Gear gear2 = new Gear()
		robot2.addPart(gear2)

		robots.add(robot)
		robots.add(robot2)*/

		/*NxtRobot robot3 = new NxtRobot('3')
		Gear gear3 = new Gear()
		robot3.addPart(gear3)
		TouchSensor ts2 = new TouchSensor(SensorPort.S3)
		robot2.addPart(ts2);

		Thread.start {
			while(true) {
				if (ts.isPressed()) {
					speaker.say('stop!')
					gear.stop()
					Thread.sleep(10000)
				}
			}
		}*/

		// get gear privates
		/*Field parts = NxtRobot.class.getDeclaredField('parts')
		parts.setAccessible(true)

		GameGridManager.robotList.each(){ nxt ->
			(parts.get(nxt)as List).each(){
				println it
			}
		}*/

		def control = new ControlOfVoice(RobotUtil.gearsFromRobotList(robots, Gear.class, NxtRobot.class))

	}

}

