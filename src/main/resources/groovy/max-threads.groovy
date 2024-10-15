
def threadPools = server.'thread-pools'.'thread-pool'.findAll {it.@'max-threads'.toFloat() > 100 }
if (threadPools.size() > 0) {
    threadPools.forEach { threadPool ->
        println "${threadPool.@name}"
    }
} else {
    println "List has 0 or fewer elements."
}
