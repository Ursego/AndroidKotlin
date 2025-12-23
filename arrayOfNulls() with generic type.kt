// If you write

val arr = arrayOfNulls<T>(5)

// you get a compilation error "Cannot use 'T' as reified typealias parameter. Use a class instead.".

// Solution: In your "util" package, create the global function genericArrayOfNulls():

@Suppress("UNCHECKED_CAST")
fun <T> genericArrayOfNulls(size: Int): Array<T?> = arrayOfNulls<Any?>(size) as Array<T?>

// Now, you can use it instead of arrayOfNulls():

val arr = genericArrayOfNulls(5)