package com.zlcdgroup.mrsei

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.abs

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun   testGcd(){
        println("gcd=${gcd(720,1280)}")
    }

    fun gcd(numerator: Int, denominator: Int): Int {
        /*
         * Non-recursive implementation of Euclid's algorithm:
         *
         *  gcd(a, 0) := a
         *  gcd(a, b) := gcd(b, a mod b)
         *
         */
        var a = numerator
        var b = denominator

        while (b != 0) {
            val oldB = b

            b = a % b
            a = oldB
        }
        testthis2<Int> {
            println(this)
        }
        testthis<Int> {

        }


        test3{
            a,b->
            println(a)
        }
        return abs(a)
    }

    fun  <T :Int> testthis(t:()->Unit){
        println("testthis=$t")

    }
    fun  <T :Int> testthis2(t:T.()->Unit){
        var  a = 1

        println("testthis2=$t")
    }

    fun test3(callback:(a:Int,b:Int) ->Unit){
         callback(1,2)

    }

    data class Plate<T>(var item: T)

    //只需要返回值，不需要传入参数
    interface Source<T>{  fun getSource():Source<T>  }

    open class Food
    open class Fruit : Food()
    class Apple : Fruit()
    class Banana : Fruit()
    class Test1{  fun <T:Fruit> setPlate(plate: Plate<T>){}  }

    //out即java中的<? extends T>
    //意为仅可作为返回值，返回值类型是T或T的父类
    data class Basket<out T>(val item: T)

    //in即java中的<? super T>
    //意为仅可作为参数传入，传入的参数类型是T或T的子类
    class Bowl<in T> {  fun setItem(item: T) {}  }


     @Test
    fun   test2(){
         val plate1 = Plate(Food())
         plate1.item = Fruit()
         val test = Test1()
         //无法通过 因为Food不是Fruit或其子类
       //  test.setPlate( plate1)


         val plate3 = Plate(Apple())
          test.setPlate(plate3)

         val basket: Basket<Fruit> = Basket<Apple>(Apple())
     }
}
