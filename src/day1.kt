import java.io.File

object Day1: Puzzle() {

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

    fun testFloors1() = floors("( (  )     )") shouldBe 0
    fun testFloors2() = floors("(((") shouldBe 3
    fun testFloors3() = floors("(()(()(") shouldBe 3
    fun testFloors4() = floors("))(((((") shouldBe 3
    fun testFloors5() = floors("())") shouldBe -1
    fun testFloors6() = floors(")())())") shouldBe -3

    val input = File("src/day1.txt").readText()

    override fun part1() = floors(input)
    override fun part2() = firstTimeInBasement(input)?.let { arg -> arg + 1 }

    @JvmStatic
    fun main(vararg arg: String) = solve()
}














