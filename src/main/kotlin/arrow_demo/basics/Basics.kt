package arrow_demo.basics

import kotlin.random.Random

object Basics {

    fun getValue(): Int? {
        return Random.nextInt().takeIf { it % 2 == 0 }
    }

    val x: Int? = getValue()

    @JvmStatic
    fun main(args: Array<String>) {
        val value = getValue()
        println(value)
    }
}