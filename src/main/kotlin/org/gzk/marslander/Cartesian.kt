package org.gzk.marslander

import java.lang.Math.*

/**
 * A point in a 2 dimensions cartesian system.
 * Point + Vector -> Point
 * Point - Point  -> Vector
 */
data class Point(val x:Double, val y: Double) {

    infix operator fun plus(vector: Vector) = Point (x + vector.dx, y + vector.dy)
    infix operator fun minus(point: Point)  = Vector(x - point.x, y - point.y)

    fun distanceTo(point: Point) =  (point - this).length
}

/**
 * Vector * Double -> Vector
 * Vector + Vector -> Vector
 */
data class Vector(val dx:Double, val dy:Double) {

    infix operator fun plus(vector: Vector) = Vector (dx + vector.dx, dy + vector.dy)
    infix operator fun times(times: Double) = Vector (dx * times, dy * times)

    fun rotate(angle: Angle) =
            Vector(
                    dx * cos(angle.rad) - dy * sin(angle.rad),
                    dx * sin(angle.rad) + dy * cos(angle.rad)
            )

    val length: Double
        get() = sqrt(dx * dx + dy * dy)
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
 * A line is a List of point.
 * It is comparable to point:   Line > Point
 */
data class Line (val points:List<Point>){
    init {
        if (points.size < 2)
            error("Should have 2 points at minimum.")
    }

    infix operator fun compareTo(point: Point) = (getYforX(point.x) - point.y ).toInt()

    fun isHorizontalAtX(x: Double) = getSegmentFor(x).let {
        it.first.y == it.second.y
    }

    private fun getSegmentFor (x:Double) =
            (1..points.size-1).first {
                points[it -1].x <= x && x <= points[it].x
            }.let {
                Pair(points[it - 1], points[it])
            }

    private fun getYforX(x:Double) = getSegmentFor(x).let {
        it.first.y + (x - it.first.x) * (it.second.y - it.first.y)/(it.second.x - it.first.x)
    }

}
