package taller

import taller.ManiobrasTrenes._
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AplicarMovimientosTest extends AnyFunSuite {

  val objEstacion = new Estacion()

  test("Prueba juguete: 10 vagones y 10 movimientos") {
    val tren = ('a' to 'j').toList
    val movimientos = List.fill(5)(Uno(1)) ++ List.fill(5)(Uno(-1))
    val resultado = objEstacion.aplicarMovimientos((tren, Nil, Nil), movimientos)
    assert(resultado.length == 11)
  }

  test("Prueba pequeña: 100 vagones y 100 movimientos") {
    val tren = (1 to 100).toList
    val movimientos = List.fill(50)(Uno(1)) ++ List.fill(50)(Uno(-1))
    val resultado = objEstacion.aplicarMovimientos((tren, Nil, Nil), movimientos)
    assert(resultado.length == 101)
  }

  test("Prueba mediana: 500 vagones y 500 movimientos") {
    val tren = (1 to 500).toList
    val movimientos = List.fill(250)(Dos(1)) ++ List.fill(250)(Dos(-1))
    val resultado = objEstacion.aplicarMovimientos((tren, Nil, Nil), movimientos)
    assert(resultado.length == 501)
  }

  test("Prueba grande: 1000 vagones y 1000 movimientos") {
    val tren = (1 to 1000).toList
    val movimientos = List.fill(500)(Uno(2)) ++ List.fill(500)(Uno(-2))
    val resultado = objEstacion.aplicarMovimientos((tren, Nil, Nil), movimientos)
    assert(resultado.length == 1001)
  }
}