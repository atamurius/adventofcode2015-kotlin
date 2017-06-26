
// List = Nil | (head, List)
sealed class List {
    data class Pair(val head: Int, val tail: List) : List()

    class EmptyList : List()
}

val Nil = List.EmptyList()

operator fun Int.plus(tail: List) = List.Pair(this, tail)

// O(n)
operator fun List.plus(last: Int) = reverse(last + reverse(this))

// O(n)
fun reverse(list: List): List {

    tailrec fun reverseTail(list: List, reversed: List): List = when(list) {
        is List.EmptyList -> reversed
        is List.Pair -> reverseTail(list.tail, list.head + reversed)
    }
    return reverseTail(list, Nil)
}



// (1 + 2 + 3 + Nil) + 4 =
// 1 ((2 + 3 + Nil) + 4) =
// 1 + 2 + ((3 + Nil) + 4) =
// 1 + 2 + 3 + (Nil + 4) =
// 1 + 2 + 3 + 4 + Nil

fun printList(list: List): Unit = when (list) {
    is List.EmptyList -> println(".")
    is List.Pair -> {
        print("${list.head} ")
        printList(list.tail)
    }
}

fun <T> foldRight(list: List, initial: T, f: (Int, T) -> T): T = when(list) {
    is List.EmptyList -> initial
    is List.Pair -> f(list.head, foldRight(list.tail, initial, f))
}
// fold([1, 2, 3], 0, +) =
// 1 + fold([2, 3], 0, +) =
// 1 + (2 + fold([3], 0, +)) =
// 1 + (2 + (3 + fold([], 0, +))) =
// 1 + (2 + (3 + initial)))

// fold = ((initial + 1) + 2) + 3
tailrec fun <T> fold(list: List, acc: T, f: (T, Int) -> T): T = when(list) {
    is List.EmptyList -> acc
    is List.Pair -> fold(list.tail, f(acc, list.head), f)
}
// fold([1,2,3], 0) =
// fold([2,3], 1) =
// fold([3], 3) =
// fold([], 6) =
// 6


fun filter(list: List, predicate: (Int) -> Boolean): List = when (list) {
    is List.EmptyList -> Nil
    is List.Pair ->
            if (predicate(list.head)) list.head + filter(list.tail, predicate)
            else filter(list.tail, predicate)
}

fun filter2(list: List, predicate: (Int) -> Boolean): List =
        foldRight(list, Nil as List) { x, acc ->
            if (predicate(x)) x + acc
            else acc
        }

val numbers = 1 + (2 + (3 + (4 + (5 + Nil))))

fun main(vararg args: String) {
    fold(numbers, Unit) { _, x ->
        print("$x ")
    }
    println(".")
    println(fold(numbers, 0) { a,b -> a + b })
    printList(filter2(numbers, { it % 2 == 0 }))
}