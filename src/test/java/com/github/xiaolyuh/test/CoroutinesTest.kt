package com.github.xiaolyuh.test

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.junit.Test
import kotlin.system.measureTimeMillis

class CoroutinesTest {
    @Test
    fun test1() {
        fun main() = runBlocking { // this: CoroutineScope
            launch { // launch a new coroutine and continue
                delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
                println("World!") // print after delay
            }
            println("Hello") // main coroutine continues while a previous one is delayed
        }

        main()
    }

    @Test
    fun test2() {
        fun main() = runBlocking { // this: CoroutineScope
            launch { doWorld() }
            println("Hello")
        }

        main()
    }

    // this is your first suspending function
    private suspend fun doWorld() {
        delay(1000L)
        println("World!")
    }


    @Test
    fun test3() {
        suspend fun doWorld() = coroutineScope {  // this: CoroutineScope
            launch {
                delay(1000L)
                println("World!")
            }
            println("Hello")
        }

        fun main() = runBlocking {
            doWorld()
        }

        main()
    }

    @Test
    fun test4() {
        // Concurrently executes both sections
        suspend fun doWorld() = coroutineScope { // this: CoroutineScope
            launch {
                delay(2000L)
                println("World 2")
            }
            launch {
                delay(1000L)
                println("World 1")
            }
            println("Hello")
        }

        fun main() = runBlocking {
            doWorld()
            println("Done")
        }

        main()
    }

    @Test
    fun test5() {
        fun main() = runBlocking {
            val job = launch { // launch a new coroutine and keep a reference to its Job
                delay(1000L)
                println("World!")
            }
            println("Hello")
            job.join() // wait until child coroutine completes
            println("Done")

        }

        main()
    }

    @Test
    fun test6() {
        fun main() = runBlocking {
            repeat(10_000) { // launch a lot of coroutines
                launch {
                    delay(5000L)
                    print(".")
                }
            }
        }

        main()
    }

    @Test
    fun test7() {
        fun main() = runBlocking {
            val deferred: Deferred<Int> = async {
                loadData()
            }
            println("waiting...")
            println(deferred.await())
        }

        main()
    }

    suspend fun loadData(): Int {
        println("loading...")
        delay(1000L)
        println("loaded!")
        return 42
    }

    @Test
    fun test8() {
        fun main() = runBlocking {
            val deferreds: List<Deferred<Int>> = (1..3).map {
                async {
                    delay(1000L * it)
                    println("Loading $it")
                    it
                }
            }
            val sum = deferreds.awaitAll().sum()
            println("$sum")
        }

        main()
    }

    @Test
    fun test9() {
        fun log(message: Any?) {
            println("[${Thread.currentThread().name}] $message")
        }

        fun main() = runBlocking<Unit> {
            val channel = Channel<String>()
            launch {
                channel.send("A1")
                channel.send("A2")
                log("A done")
            }
            launch {
                channel.send("B1")
                log("B done")
            }
            launch {
                repeat(3) {
                    val x = channel.receive()
                    log(x)
                }
            }
        }

        main()
    }

    @Test
    fun test10() {
        fun main() = runBlocking {

            val job = launch {
                repeat(1000) { i ->
                    println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            }
            delay(1300L) // delay a bit
            println("main: I'm tired of waiting!")
            job.cancel() // cancels the job
            job.join() // waits for job's completion
            println("main: Now I can quit.")

        }

        main()
    }

    @Test
    fun test11() {
        fun main() = runBlocking {
            val startTime = System.currentTimeMillis()
            val job = launch(Dispatchers.Default) {
                var nextPrintTime = startTime
                var i = 0
                while (i < 5) { // computation loop, just wastes CPU
                    // print a message twice a second
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        println("job: I'm sleeping ${i++} ...")
                        nextPrintTime += 500L
                    }
                }
            }
            delay(1300L) // delay a bit
            println("main: I'm tired of waiting!")
            job.cancelAndJoin() // cancels the job and waits for its completion
            println("main: Now I can quit.")
        }

        main()
    }

    @Test
    fun test12() {
        fun main() = runBlocking {
            val job = launch(Dispatchers.Default) {
                repeat(5) { i ->
                    try {
                        // print a message twice a second
                        println("job: I'm sleeping $i ...")
                        delay(500)
                    } catch (e: Exception) {
                        // log the exception
                        println(e)
                    }
                }
            }
            delay(1300L) // delay a bit
            println("main: I'm tired of waiting!")
            job.cancelAndJoin() // cancels the job and waits for its completion
            println("main: Now I can quit.")
        }

        main()
    }

    @Test
    fun test13() {
        fun main() = runBlocking {
            val startTime = System.currentTimeMillis()
            val job = launch(Dispatchers.Default) {
                var nextPrintTime = startTime
                var i = 0
                while (isActive) { // cancellable computation loop
                    // print a message twice a second
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        println("job: I'm sleeping ${i++} ...")
                        nextPrintTime += 500L
                    }
                }
            }
            delay(1300L) // delay a bit
            println("main: I'm tired of waiting!")
            job.cancelAndJoin() // cancels the job and waits for its completion
            println("main: Now I can quit.")
        }

        main()
    }

    @Test
    fun test14() {
        fun main() = runBlocking {

            val job = launch {
                try {
                    repeat(1000) { i ->
                        println("job: I'm sleeping $i ...")
                        delay(500L)
                    }
                } finally {
                    withContext(NonCancellable) {
                        println("job: I'm running finally")
                        delay(1000L)
                        println("job: And I've just delayed for 1 sec because I'm non-cancellable")
                    }
                }
            }
            delay(1300L) // delay a bit
            println("main: I'm tired of waiting!")
            job.cancelAndJoin() // cancels the job and waits for its completion
            println("main: Now I can quit.")

        }

        main()
    }

    @Test
    fun test15() {
        fun main() = runBlocking {

            withTimeout(1300L) {
                repeat(1000) { i ->
                    println("I'm sleeping $i ...")
                    delay(500L)
                }
            }

        }

        main()
    }

    @Test
    fun test16() {
        fun main() = runBlocking {

            val result = withTimeoutOrNull(1300L) {
                repeat(1000) { i ->
                    println("I'm sleeping $i ...")
                    delay(500L)
                }
                "Done" // will get cancelled before it produces this result
            }
            println("Result is $result")

        }

        main()
    }

    @Test
    fun test17() {
        fun main() = runBlocking {

            val time = measureTimeMillis {
                val one = doSomethingUsefulOne()
                val two = doSomethingUsefulTwo()
                println("The answer is ${one + two}")
            }
            println("Completed in $time ms")

        }

        main()

        fun main1() = runBlocking {

            val time = measureTimeMillis {
                val one = async { doSomethingUsefulOne() }
                val two = async { doSomethingUsefulTwo() }
                println("The answer is ${one.await() + two.await()}")
            }
            println("Completed in $time ms")

        }

        main1()

        fun main3() = runBlocking {

            val time = measureTimeMillis {
                val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
                val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
                // some computation
                one.start() // start the first one
                two.start() // start the second one
                println("The answer is ${one.await() + two.await()}")
            }
            println("Completed in $time ms")

        }

        main3()
    }

    suspend fun doSomethingUsefulOne(): Int {
        delay(1000L) // pretend we are doing something useful here
        return 13
    }

    suspend fun doSomethingUsefulTwo(): Int {
        delay(1000L) // pretend we are doing something useful here, too
        return 29
    }
}