import java.io.File

typealias Circuit = List<Pair<String, Day7.Instr>>

object Day7 : Puzzle() {

    data class Circuit(val instruction: Map<String, Instr>) {
        val cachedValues = mutableMapOf<String, Int>()
        operator fun get(wire: String) = cachedValues[wire] ?:
                (instruction[wire]?.evaluate(this) ?: error("$wire is undefined")).apply {
                    cachedValues.put(wire, this)
                }
        fun override(wire: String, value: Int) {
            cachedValues.put(wire, value)
        }
    }

    interface Evalable {
        fun evaluate(instr: Circuit): Int
    }

    interface Source : Evalable
    data class Signal(val value: Int): Source {
        override fun evaluate(instr: Circuit) = value
        override fun toString() = "$value"
    }
    data class Wire(val name: String): Source {
        override fun evaluate(instr: Circuit) = instr[name]
        override fun toString() = name
    }

    interface Instr : Evalable
    data class Connection(val value: Source) : Instr {
        override fun evaluate(instr: Circuit) = value.evaluate(instr).apply {
            println("$value -> $this")
        }

        override fun toString() = "$value"
    }
    data class BiGate(val left: Source, val right: Source, val op: BinaryOp) : Instr {
        override fun evaluate(instr: Circuit): Int = op(left.evaluate(instr), right.evaluate(instr)).apply {
            println("$left $op $right -> $this")
        }

        override fun toString() = "$left $op $right"
    }

    data class UnGate(val arg: Source, val op: UnaryOp) : Instr {
        override fun evaluate(instr: Circuit) = op(arg.evaluate(instr)).apply {
            println("$op $arg -> $this")
        }

        override fun toString() = "$op $arg"
    }

    class UnaryOp(val name: String, val op: (Int) -> Int) {
        override fun toString() = name
        operator fun invoke(value: Int) = op(value)
    }

    class BinaryOp(val name: String, val op: (Int, Int) -> Int) {
        override fun toString() = name
        operator fun invoke(a: Int, b: Int) = op(a, b)
    }

    val binaryOps = mapOf(
            "AND" to BinaryOp("AND", { a, b -> a and b }),
            "OR" to BinaryOp("OR", { a, b -> a or b }),
            "LSHIFT" to BinaryOp("LSHIFT", { a, b -> a shl b }),
            "RSHIFT" to BinaryOp("RSHIFT", { a, b -> a shr b }))

    val unaryOps = mapOf("NOT" to UnaryOp("NOT", { x -> x.inv() and 0xffff }))

    infix fun Source.connectedTo(wire: String): Pair<String, Instr> = wire to Connection(this)
    infix fun Instr.connectedTo(wire: String): Pair<String, Instr> = wire to this
    infix fun Source.and(right: Source) = BiGate(this, right, binaryOps["AND"]!!)
    infix fun Source.or(right: Source) = BiGate(this, right, binaryOps["OR"]!!)
    infix fun Source.lsh(right: Source) = BiGate(this, right, binaryOps["LSHIFT"]!!)
    infix fun Source.rsh(right: Source) = BiGate(this, right, binaryOps["RSHIFT"]!!)
    operator fun Source.not() = UnGate(this, unaryOps["NOT"]!!)

    fun parseSource(str: String) = if (Character.isDigit(str[0])) Signal(str.toInt()) else Wire(str)

    fun parse(str: String,
              unOps: Map<String, UnaryOp> = unaryOps,
              biOps: Map<String, BinaryOp> = binaryOps): Pair<String, Instr> =

            Regex("""(\S+) -> (\w+)""").matchEntire(str)?.destructured?.let { (source, target) ->
                parseSource(source) connectedTo target
            } ?:
            Regex("""(\S+) (\w+) (\S+) -> (\w+)""").matchEntire(str)?.destructured?.let { (left, op, right, target) ->
                BiGate(parseSource(left), parseSource(right), biOps[op] ?: error("Operation $op is unknown")) connectedTo target
            } ?:
            Regex("""(\w+) (\S+) -> (\w+)""").matchEntire(str)?.destructured?.let { (op, arg, target) ->
                UnGate(parseSource(arg), unOps[op] ?: error("Undefined unary operation $op")) connectedTo target
            } ?: error("Unknown instruction $str")

    fun toString(instr: Pair<String, Instr>) = "${instr.second} -> ${instr.first}"

    val exampleText = """|123 -> x
           |456 -> y
           |x AND y -> d
           |x OR y -> e
           |x LSHIFT 2 -> f
           |y RSHIFT 2 -> g
           |NOT x -> h
           |NOT y -> i"""
            .trimMargin()

    val example = exampleText
                .lines()
                .map { parse(it) }
                .toMap()

    fun testParse() = example shouldBe mapOf(
            Signal(123) connectedTo "x",
            Signal(456) connectedTo "y",
            Wire("x") and Wire("y") connectedTo "d",
            Wire("x") or Wire("y") connectedTo "e",
            Wire("x") lsh Signal(2) connectedTo "f",
            Wire("y") rsh Signal(2) connectedTo "g",
            ! Wire("x") connectedTo "h",
            ! Wire("y") connectedTo "i")

    fun testEvaluate() = example.mapValues { it.value.evaluate(Circuit(example)) } shouldBe mapOf(
            "d" to 72,
            "e" to 507,
            "f" to 492,
            "g" to 114,
            "h" to 65412,
            "i" to 65079,
            "x" to 123,
            "y" to 456)

    fun testToString() = example.toList().joinToString("\n") { toString(it) } shouldBe exampleText

    val input = File("src/day7.txt").readLines().map { parse(it) }.toMap()

    fun testInputToString() = File("src/day7.txt").readLines().forEach {
        parse(it).let { toString(it) } shouldBe it
    }

    override fun part1() = Circuit(input)["a"]
    override fun part2() = Circuit(input).apply { override("b", 3176) }["a"]

    @JvmStatic
    fun main(vararg args: String) = solve()
}
