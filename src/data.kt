
// Arrays
// - add element = 0:M
// - remove element = O(n)
// - indexed access = O(1)

// Linked Lists
// - add element = O(n)
// - remove element = O(n)
// - indexed access = O(n)

// Hash Map
// - add element = O(1)
// - remove element = O(1)
// - keyed access = O(1)
/*

(key: value)
i = hash(key) % n

[0] -> () -> () -> ()
[1] ->
[i] -> (key1:other) -> (key2:things) -> (key:value)
[3]
 .
 .
 .
[n]

 */

// (key1:value),(key2:value2),...
// random ~ O(n) ~ 500
// binary ~ O(log2n) ~ 10

// -----------------A-----------------
// --------B-------- --------X--------
// ---Y---- ----C--- -----------------
// -------- --D- -Z- -----------------

// Trees
// Tree = Nil | (Value, left: Tree, right: Tree)
//
//fun sqrt(n: Double): Double =
//        if (n >= 0) Math.sqrt(n)
//        else throw ArithmeticException("$n is not positive or zero")

sealed class Tree<out T> {

    object Empty : Tree<Nothing>()

    data class Node<out T>(val value: T,
                           val left: Tree<T> = Empty,
                           val right: Tree<T> = Empty)
        : Tree<T>()
}

val tree: Tree<Int> = Tree.Node(5, right = Tree.Node(8))


fun <T> find(tree: Tree<T>, compare: (T) -> Int): T? = when (tree) {
    is Tree.Empty -> null
    is Tree.Node -> compare(tree.value).let {
        if (it == 0) tree.value
        else if (it > 0) find(tree.right, compare)
        else find(tree.left, compare)
    }
}

fun <T> put(tree: Tree<T>, value: T, compare: (T, T) -> Int): Tree<T> = when (tree) {
    is Tree.Empty -> Tree.Node(value)
    is Tree.Node -> compare(tree.value, value).let {
        if (it == 0) tree.copy(value)
        else if (it > 0) tree.copy(left = put(tree.left, value, compare))
        else tree.copy(right = put(tree.right, value, compare))
    }
}


val fruits = Tree.Node(
        value = Pair("apple", 35),
        left = Tree.Node(Pair("potato", 10)),
        right = Tree.Node(Pair("banana", 40))
)

fun findByPrice(tree: Tree<Pair<String, Int>>, target: Int) = find(tree) {
    (_, price) -> if (price == target) 0 else if (price > target) -1 else 1
}

val fruitFor40 = findByPrice(fruits, 40)

fun main(vararg args: String) {
    val res = listOf("potato" to 10, "apple" to 35, "banana" to 40)
            .fold(Tree.Empty as Tree<Pair<String, Int>>) { tree, el ->
                put(tree, el) { (_, a), (_, b) -> a.compareTo(b) }
            }

    println(res)
    println(findByPrice(res, 40))
}








