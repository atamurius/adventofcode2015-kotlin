import java.io.File

object Day2: Puzzle() {

    data class Box(val width: Int,
                   val height: Int,
                   val length: Int) {

        companion object {
            fun from(str: String): Box {
                val (w, h, l) = str.split('x')
                return Box(w.toInt(), h.toInt(), l.toInt())
            }
        }

        val area get() = 2 * (topArea + leftArea + rightArea)
        val topArea get() = width * length
        val leftArea get() = width * height
        val rightArea get() = height * length
        val topPerimeter get() = 2 * (width + length)
        val leftPerimeter get() = 2 * (width + height)
        val rightPerimeter get() = 2 * (height + length)
        val volume get() = width * length * height

//        fun copy(width: Int = this.width, height: Int = this.height, length: Int = this.length): Box = Box(width, height, length)

        fun wrappingPaperArea() = area + minOf(topArea, leftArea, rightArea)

        fun ribbonLength() = minOf(topPerimeter, leftPerimeter, rightPerimeter) + volume
    }

    fun testPaper1() = Box.from("2x3x4").wrappingPaperArea() shouldBe 58
    fun testPaper2() = Box.from("1x1x10").wrappingPaperArea() shouldBe 43

    fun testRibbon1() = Box.from("2x3x4").ribbonLength() shouldBe 34
    fun testRibbon2() = Box.from("1x1x10").ribbonLength() shouldBe 14

    val boxes = File("src/day2.txt").readLines().map { Box.from(it) }

    override fun part1() = boxes.sumBy { it.wrappingPaperArea() }
    override fun part2() = boxes.sumBy { it.ribbonLength() }

    @JvmStatic
    fun main(vararg args: String) = solve()
}

