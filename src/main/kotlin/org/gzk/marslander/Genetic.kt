package org.gzk.marslander

import java.lang.Math.random

val log                 = true
val elitism             = true
val generationsCount    = 10
val populationSize      = 20
val genomeSize          = 1200

var uniformRate         = .5
var mutationRate        = .06
var selectionRatio      = .4


data class Gene(val random: Double = random()){
    /**
     * Random int from 0 to max (inclusive)
     */
    fun asInt(max:Int) = (random * (max + 1)).toInt()
}

class GenomeAndResult<T>(val genome: Array<Gene>, create: (Array<Gene>) -> T) {
    val result = create(genome)
}

fun <T> findBestChimp(create: (Array<Gene>) ->T, fitness: (T) -> Double): GenomeAndResult<T> {

    fun Array<GenomeAndResult<T>>.sortByFitness() =
            this.apply {
                sortBy { fitness (it.result) }
                reverse()
                .apply { if(log) println(joinToString (separator = " ", transform = { g -> fitness(g.result).toInt().toString().padStart(5) })) }
            }

    val population = Array(populationSize, { GenomeAndResult(buildGenome(genomeSize), create) }).sortByFitness()

    val chimpsFewGenerationsLater = (1..generationsCount)
            .fold(population) {
                pop, _ ->
                buildNextGeneration(pop, create).sortByFitness()
            }

    return chimpsFewGenerationsLater.first()
}

fun buildGenome(size: Int) = Array(size, { Gene() })

fun <T> buildNextGeneration(population: Array<GenomeAndResult<T>>,
                            create: (Array<Gene>) -> T): Array<GenomeAndResult<T>> {

    val newPop = population.copyOf()

    val elitismOffset = if (elitism) 1 else 0

    (elitismOffset..population.size - 1).forEach {
        val genome1 = select(population).genome
        val genome2 = select(population).genome
        val genome = crossover (genome1, genome2)

        mutate(genome)

        newPop[it] = GenomeAndResult(genome, create)
    }
    return newPop
}

fun <T> select(population: Array<T>): T {
    for ((i, chimp) in population.withIndex()) {
        if (random() <= selectionRatio * (population.size - i) / population.size) {
            return chimp
        }
    }
    return population.first()
}

fun crossover(genome1: Array<Gene>, genome2: Array<Gene>) =
        Array(genome1.size, {
            if (random() <= uniformRate)
                genome1.get(it)
            else
                genome2.get(it)
        })

fun mutate(genome: Array<Gene>) {
    for ((i, _) in genome.withIndex()) {
        if (random() <= mutationRate) {
            genome[i] = Gene()
        }
    }
}
