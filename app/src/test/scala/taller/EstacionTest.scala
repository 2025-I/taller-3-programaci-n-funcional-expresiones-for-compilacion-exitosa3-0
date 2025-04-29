package taller

import taller.ManiobrasTrenes._

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import taller.ManiobrasTrenes.Estacion

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


}
