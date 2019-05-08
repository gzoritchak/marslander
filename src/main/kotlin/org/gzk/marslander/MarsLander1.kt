package org.gzk.marslander

import java.io.StringReader
import java.util.*

fun main(args: Array<String>) {
    val bestChimp = findBestChimp(::createMarsLander1FromGenome, ::marsLander1Fitness)
    println(
            bestChimp.result.trajectory.drop(1).joinToString (transform = { state -> "${state.angle}, ${state.power}" })
    )
}

val marsLander1InitState = State(500, 0, 0, particule(2500.0, 2500.0, speed(0.0, 0.0)))


val mars1Ground by lazy {
    codingGameGroundInputsToLine(
"""
0 100
6999 100""")
}

fun codingGameGroundInputsToLine(groundAsString:String): Line {
    val points = mutableListOf<Point>()
    val sc = Scanner(StringReader(groundAsString))
    while (sc.hasNextLine()) {
        points.add(Point(sc.nextDouble(), sc.nextDouble()))
    }
    return Line(points)
}


fun createMarsLander1FromGenome(genome: Array<Gene>):Lander {
    var previousPower = 0

    val ret = mutableListOf<ControlCmd>()
    (1..genomeSize).forEach {
        val power = (previousPower + 1 - genome[it-1].asInt(2)).coerceAtMost(4).coerceAtLeast(0)
        ret.add(ControlCmd(power, 0))
        previousPower = power
    }
    return Lander(marsLander1InitState, ret, mars1Ground)
}

fun marsLander1Fitness(lander: Lander)= when (lander.flystate) {
    FlyState.Landed ->  lander.trajectory.last().fuel.toDouble()
    FlyState.Flying ->  - lander.trajectory.last().position.y + mars1Ground.landingZone.first.y
    FlyState.Crashed -> with(lander.trajectory.last()) {
        mars1Ground.landingZone.first.y - position.y + speed.ySpeed + 40
    }
}
