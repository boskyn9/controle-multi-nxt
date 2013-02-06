package scripts

import java.lang.reflect.Field

static def gearsFromRobotList(robots, clazzGear, clazzRobot) {
    Field parts = clazzRobot.getDeclaredField('parts')
    parts.setAccessible(true)
        
    def gears = [:]
        
    robots.each(){ nxt ->            
        gears."$nxt.btName" = (parts.get(nxt)as List).find { clazzGear.isInstance(it) }
    }
        
    gears
}
