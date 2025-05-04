package taller

import taller.ManiobrasTrenes._
import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class definirManiobraTest extends AnyFunSuite {

  val estacion = new Estacion() // Necesario porque los métodos están dentro de esta clase

  // Función para verificar que la maniobra transforma t1 en t2
  def verificarManiobra(t1: Tren, t2: Tren): Boolean = {
    val maniobra = estacion.definirManiobra(t1, t2)

    // Si la maniobra está vacía y los trenes son iguales, es válido
    if (maniobra.isEmpty && t1 == t2) {
      true
    } else {
      // Aplicamos la maniobra y verificamos el resultado final
      val estados = estacion.aplicarMovimientos((t1, Nil, Nil), maniobra)
      val estadoFinal = estados.last

      // Verificamos que el estado final sea el esperado
      val (principal, uno, dos) = estadoFinal
      principal.isEmpty && uno.isEmpty && dos.toSet == t2.toSet && dos == t2
    }
  }

  // Prueba de juguete: 10 elementos
  test("Prueba de juguete: 10 elementos") {
    val t1 = (1 to 10).toList
    val t2 = t1.reverse
    assert(verificarManiobra(t1, t2))
  }

  // Prueba pequeña: 100 elementos
  test("Prueba pequeña: 100 elementos") {
    val t1 = (1 to 100).toList
    val t2 = t1.reverse
    assert(verificarManiobra(t1, t2))
  }

  // Prueba mediana: 500 elementos
  test("Prueba mediana: 500 elementos") {
    val t1 = (1 to 500).toList
    val t2 = t1.reverse
    assert(verificarManiobra(t1, t2))
  }

  // Prueba grande: 1000 elementos
  test("Prueba grande: 1000 elementos") {
    val t1 = (1 to 1000).toList
    val t2 = t1.reverse
    assert(verificarManiobra(t1, t2))
  }

  // Caso base específico
  test("Caso base específico: List('a','b','c','d') a List('d','b','c','a')") {
    val t1 = List('a', 'b', 'c', 'd')
    val t2 = List('d', 'b', 'c', 'a')
    assert(verificarManiobra(t1, t2))
  }

  // Caso trivial: misma secuencia
  test("Caso trivial: mismo orden") {
    val t1 = List('a', 'b', 'c', 'd')
    val t2 = List('a', 'b', 'c', 'd')
    assert(verificarManiobra(t1, t2))
  }

  // Caso simple con reordenamiento menor
  test("Caso con reordenamiento menor") {
    val t1 = List('a', 'b', 'c', 'd')
    val t2 = List('a', 'c', 'b', 'd')
    assert(verificarManiobra(t1, t2))
  }

  // Caso de error: verificar que se lanza una excepción si un vagón no se encuentra
  test("Caso de error: vagón no encontrado") {
    val t1 = List('a', 'b', 'c', 'd')
    val t2 = List('e', 'b', 'c', 'a') // 'e' no está en t1
    assertThrows[IllegalArgumentException] {
      estacion.definirManiobra(t1, t2)
    }
  }
}