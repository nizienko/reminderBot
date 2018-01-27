package core.utils

fun <O> cached(seconds: Int, supplier: () -> O) = Cache(seconds, supplier)

class Cache<out O>(private val timeoutS: Int, private val supplier: () -> O) {
    private var updatedTime = System.currentTimeMillis()
    private var obj = supplier()

    fun get(): O {
        if (System.currentTimeMillis() - updatedTime > timeoutS*1000) {
            updatedTime = System.currentTimeMillis()
            obj = supplier()
        }
        return obj
    }
}