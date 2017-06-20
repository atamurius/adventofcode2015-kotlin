import java.io.File

fun floors(s: String) = s.fold(0) { floor, c ->
    if (c == '(') floor + 1
    else if (c == ')') floor - 1
    else floor
}

fun firstTimeInBasement(s: String): Int? {
    s.foldIndexed(0) { index, floor, c ->
        (if (c == '(') floor + 1
        else if (c == ')') floor - 1
        else floor)
                .apply {
                    if (this < 0) return index
                }
    }
    return null
}

fun assert(x: Any, y: Any): Unit {
    if (x != y) throw Error("$x expected to be $y")
}

fun main(vararg arg: String) {
    assert(floors("( (  )     )"), 0)
    assert(floors("()()"), 0)
    assert(floors("((("), 3)
    assert(floors("(()(()("), 3)
    assert(floors("))((((("), 3)
    assert(floors("())"), -1)
    assert(floors(")())())"), -3)

    val input = File("src/day1.txt").readText()
    println(floors(input))
    println(firstTimeInBasement(input)?.let { arg -> arg + 1 })

    ("asdasd" as Int) xor 1
}
















