package taller

import taller.ManiobrasTrenes._
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import taller.ManiobrasTrenes.Estacion

import scala.util.Random

@RunWith(classOf[JUnitRunner])
class EstacionTest extends AnyFunSuite{

  val objEstacion = new Estacion()


  test("(List('a', 'b', 'c', 'd'), Nil, Nil) -> aplicarMovimiento(e1 ,Uno(2)) devuelve (List(a, b) ,List(c , d) ,List ())") {
    val e1 = (List('a', 'b', 'c', 'd'), Nil, Nil)
    assert(objEstacion.aplicarMovimiento(e1, Uno(2)) == (List('a', 'b'), List('c', 'd'), List()))
  }

  test(" (List () ,List('c' , 'd') ,List('a', 'b')) -> aplicarMovimiento(e1 , Dos(−1)) devuelve (List(a) ,List(c , d) ,List(b))") {
    val e1 = (List () ,List('c' , 'd') ,List('a', 'b'))
    val esperado = (List('a') ,List('c' , 'd') ,List('b'))
    assert(objEstacion.aplicarMovimiento(e1, Dos(-1)) == esperado)
  }

  test("(List('a', 'b'), List('c', 'd'), Nil) -> aplicarMovimiento(e1 ,Uno(-2)) devuelve (List('a', 'b', 'c', 'd'), List(), List())") {
    val e1 = (List('a', 'b'), List('c', 'd'), Nil)
    val esperado = (List('a', 'b', 'c', 'd'), List(), List())
    assert(objEstacion.aplicarMovimiento(e1, Uno(-2)) == esperado)
  }

  test("(List('a', 'b', 'c', 'd'), Nil, Nil) -> aplicarMovimiento(e1 ,Dos(1)) devuelve (List(a, b, c) ,List() ,List(d))") {
    val e1 = (List('a', 'b', 'c', 'd'), Nil, Nil)
    val esperado = (List('a', 'b', 'c'), Nil, List('d'))
    assert(objEstacion.aplicarMovimiento(e1, Dos(1)) == esperado)
  }

  test("(List('a', 'b', 'c', 'd'), Nil, Nil)" +
    "aplicarMoviemiento(e1 ,Uno(2)" +
    "aplicarMovimiento(e2 ,Dos(3))" +
    "aplicarMovimiento(e3 ,Dos(-1))" +
    "aplicarMovimiento(e4 ,Uno(-2))" +
    "devuelve (List(a, b, c, d), List(), List(b))") {
    val e1 = (List('a', 'b', 'c', 'd'), Nil, Nil)
    val e2 = objEstacion.aplicarMovimiento(e1, Uno(2))
    val e3 = objEstacion.aplicarMovimiento(e2, Dos(3))
    val e4 = objEstacion.aplicarMovimiento(e3, Dos(-1))
    val e5 = objEstacion.aplicarMovimiento(e4, Uno(-2))
    val esperado = (List('a', 'c', 'd'), Nil, List('b'))
    assert(e5== esperado)
  }

  def generarVagones(n: Int): List[Char] =
    (1 to n).map(i => ('a' + (i % 26)).toChar).toList

  def generarMovimientos(n: Int): List[Movimiento] = {
    val r = new Random(42)
    (1 to n).map { _ =>
      if (r.nextBoolean()) Uno(r.nextInt(5) - 2)
      else Dos(r.nextInt(5) - 2)
    }.toList
  }

  def aplicarSecuencia(e: Estado, movimientos: List[Movimiento]): Estado = {
    movimientos.foldLeft(e) { (estado, mov) => objEstacion.aplicarMovimiento(estado, mov) }
  }

  test("Prueba de juguete: 10 vagones y 10 movimientos") {
    val tren = generarVagones(10)
    val movimientos = generarMovimientos(10)
    val estadoFinal = aplicarSecuencia((tren, Nil, Nil), movimientos)
    assert(estadoFinal._1.size + estadoFinal._2.size + estadoFinal._3.size == 10)
  }

  test("Prueba pequeña: 100 vagones y 100 movimientos") {
    val tren = generarVagones(100)
    val movimientos = generarMovimientos(100)
    val estadoFinal = aplicarSecuencia((tren, Nil, Nil), movimientos)
    assert(estadoFinal._1.size + estadoFinal._2.size + estadoFinal._3.size == 100)
  }

  test("Prueba mediana: 500 vagones y 500 movimientos") {
    val tren = generarVagones(500)
    val movimientos = generarMovimientos(500)
    val estadoFinal = aplicarSecuencia((tren, Nil, Nil), movimientos)
    assert(estadoFinal._1.size + estadoFinal._2.size + estadoFinal._3.size == 500)
  }

  test("Prueba grande: 1000 vagones y 1000 movimientos") {
    val tren = generarVagones(1000)
    val movimientos = generarMovimientos(1000)
    val estadoFinal = aplicarSecuencia((tren, Nil, Nil), movimientos)
    assert(estadoFinal._1.size + estadoFinal._2.size + estadoFinal._3.size == 1000)
  }


}
