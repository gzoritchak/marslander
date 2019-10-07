package org.gzk.marslander

val GRAVITY     = acceleration(0.0, -3.711)
val maxX        = 6999
val minX        = 0

/**
 * power : 0, 1, 2, 3, 4
 * angle : -90, -75, ... , 0, +15, +30, ..., +75, +90
 */
data class ControlCmd(val power: Int = 0, val angle:Int = 0)

data class State(val fuel: Int, val power: Int, val angle: Int, val particule: Particule){
    val position: Vector
        get() = particule.position

    val speed: Speed
        get() = particule.speed
}

enum class FlyState {
    Landed, Crashed, Flying
}

val Line.landingZone: Pair<Vector, Vector>
    get() = points.withIndex()
            .first { it.value.y == points[it.index + 1].y }
            .let { it.value to points[it.index + 1] }

/**
 * Handle the physics (acceleration, speed, fuel, ...)
 */
class Lander(initState: State, val cmds:List<ControlCmd>, val ground: Line) {

    val trajectory = mutableListOf(initState)
    var flystate = FlyState.Flying

    init {
        computeTrajectory()
    }

    fun computeTrajectory() {
        for ((i, cmd) in cmds.withIndex()) {
            val nextState = trajectory[i].computeNextState(cmd)
            trajectory.add(nextState)

            if (evaluateOutside(nextState))         return
            if (evaluateHitTheGround(nextState))    return
            if (evaluateNoFuel(nextState))          return
        }
    }

    fun evaluateOutside(state: State): Boolean {
        if (state.position.x > maxX || state.position.x < minX) {
            flystate = FlyState.Crashed
            return true
        }
        return false
    }

    fun evaluateHitTheGround(nextState: State): Boolean {
        if (ground > nextState.position) {
            if (nextState.angle == 0
                    && nextState.speed.ySpeed > -40
                    && nextState.speed.xSpeed.abs() <= 20
                    && ground.isHorizontalAtX(nextState.position.x))
                flystate = FlyState.Landed
            else
                flystate = FlyState.Crashed
            return true
        }
        return false
    }

    fun evaluateNoFuel(nextState: State): Boolean {
        if (nextState.fuel <= 0) {
            flystate = FlyState.Crashed
            return true
        }
        return false
    }

    fun State.computeNextState(cmd: ControlCmd, time: Time = 1.0.sec): State {

        val newAngle = angle + (cmd.angle - angle).coerceAtLeast(-15).coerceAtMost(15)
        val newPower = power + (cmd.power - power).coerceAtMost(1).coerceAtLeast(-1)

        val thrustAcceleration  = Acceleration((Vector(0.0, 1.0) * newPower.toDouble()).rotate(newAngle.deg()))
        val acceleration        = GRAVITY + thrustAcceleration
        val newParticule        = particule.accelerate(acceleration, time)
        val newFuel             = fuel - newPower

        return State(newFuel, newPower, newAngle, newParticule)
    }
}

fun Double.abs(): Double  = Math.abs(this)
