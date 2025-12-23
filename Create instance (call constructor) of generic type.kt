// The problem:

// You have a function with a generic type:

fun <T : Animal> createAnimal() : T {
   val animal : T = Animal() as T // you mistakenly think, that T (Cat, Dog or whatever) is created...
   animal.sound() // ...and it will miauw or bark, but an Animal is created (you called its constructor, after all!), so there is no sound
   return animal
}

// The solution:

// To enable the solution, mark the generic parameter as reified. 
// That is possible only in inline functions, so mark the function as inline and instantiate it (i.e. call its constructor) this way:

inline fun <reified T : Animal> createAnimal() : T {
   val actualRuntimeClassConstructor : KFunction<T> = T::class.constructors.first() // get pointer to constructor of Cat, Dog or whatever
   val animal : T = actualRuntimeClassConstructor.call() // now Cat, Dog or whatever is really created!
   animal.sound() // miauw if Cat was passed, bark if Dog was passed, silent if Fish was passed :-)
   return animal
}

// BTW, there is another way to do the same:

inline fun <reified T : Animal> createAnimal() : T {
   val actualRuntimeClassName : String = T::class.qualifiedName!!
   val animal : T = Class.forName(actualRuntimeClassName).newInstance() as T // now Cat, Dog or whatever is really created!
   animal.sound() // miauw if Cat was passed, bark if Dog was passed, silent if Fish was passed :-)
   return animal
}