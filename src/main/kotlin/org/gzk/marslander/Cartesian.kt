package org.gzk.marslander

import java.lang.Math.*

/**
 * Vector * Double -> Vector
 * Vector + Vector -> Vector
 */
data class Vector(val x:Double, val y:Double) {

    infix operator fun plus(vector: Vector) = Vector (x + vector.x, y + vector.y)
    infix operator fun times(times: Double) = Vector (x * times, y * times)

    fun rotate(angle: Angle) =
            Vector(
                    x * cos(angle.rad) - y * sin(angle.rad),
                    x * sin(angle.rad) + y * cos(angle.rad)
            )

    val length: Double
        get() = sqrt(x * x + y * y)
}

/**
 * An angle that internally uses radian but can easily convert to degrees.
 */
data class Angle(val rad:Double) {
    val deg:Double
        get() = toDegrees(rad)
}

/**
 * Extension on Int to create angle :  15.deg()
 */
fun Int.deg(): Angle = Angle(toRadians(this.toDouble()))


/**
 * A line is a List of points.
 * It is comparable to point:   Line > Point
 */
data class Line (val points:List<Vector>){
    init {
        if (points.size < 2)
            error("Should have 2 points at minimum.")
    }

    infix operator fun compareTo(point: Vector) = (getYforX(point.x) - point.y).toInt()

    fun isHorizontalAtX(x: Double) = getSegmentFor(x).let {
        it.first.y == it.second.y
    }

    private fun getSegmentFor (x:Double) =
            (1 until points.size).first {
                points[it -1].x <= x && x <= points[it].x
            }.let {
                Pair(points[it - 1], points[it])
            }

    private fun getYforX(x:Double) = getSegmentFor(x).let {
        it.first.y + (x - it.first.x) * (it.second.y - it.first.y)/(it.second.x - it.first.x)
    }

}
