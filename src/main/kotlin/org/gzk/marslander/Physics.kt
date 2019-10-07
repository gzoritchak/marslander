package org.gzk.marslander

data class Time(val sec:Double)
val Double.sec: Time
    get() = Time(this)


/**
 * Speed is internally using a vector, allowing to compose it. speed12 = speed1 + speed2
 */
data class Speed(val direction: Vector) {

    infix operator fun plus(speed: Speed) = Speed(direction + speed.direction)

    val xSpeed:Double
        get() = direction.x

    val ySpeed:Double
        get() = direction.y

    override fun toString() = "(${xSpeed.format(2)}, ${ySpeed.format(2)})"
}

fun speed(xSpeed: Double, ySpeed: Double) = Speed(Vector(xSpeed, ySpeed))


/**
 * Acceleration is internally using a vector, allowing to compose it. acc12 = acc1 + acc2
 * Acceleration * Time -> Speed
 */
data class Acceleration(val vector: Vector) {

    infix operator fun times( time: Time) = Speed(vector * time.sec)

    infix operator fun plus(acceleration: Acceleration) = Acceleration(vector + acceleration.vector)
}
fun acceleration(xAcc:Double, yAcc:Double) = Acceleration(Vector(xAcc, yAcc))


/**
 * Represent something with an initial point and speed and on which an acceleration can be applied.
 */
data class Particule(val position: Vector, val speed: Speed){

    fun accelerate(acceleration: Acceleration, time: Time): Particule {
        val newSpeed = speed + acceleration * time

        val newPosition = position +
                speed.direction * time.sec +
                acceleration.vector * time.sec * time.sec * 0.5

        return Particule(newPosition, newSpeed)
    }

    override fun toString() = " x=${position.x.format(2)} y=${position.y.format(2)} speed= $speed"
}

fun particule(x:Double, y:Double, s: Speed) = Particule(Vector(x, y), s)

fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)
