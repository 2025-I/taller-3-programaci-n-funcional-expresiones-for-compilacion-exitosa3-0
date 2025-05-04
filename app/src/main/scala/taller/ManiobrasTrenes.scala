package taller

import scala.annotation.tailrec
import scala.collection.parallel.CollectionConverters._
import taller.ManiobrasTrenes._

object ManiobrasTrenes {

  type Vagon = Any
  type Tren = List[Vagon]
  type Estado = (Tren, Tren, Tren)
  type Maniobra = List[Movimiento]

  trait Movimiento
  case class Uno(n: Int) extends Movimiento
  case class Dos(n: Int) extends Movimiento

  case class Estacion() {

    def aplicarMovimiento(e: Estado, m: Movimiento): Estado = {
      val (principal, uno, dos) = e

      (for {
        resultado <- m match {
          case Uno(n) if n > 0 =>
            val (quedan, seVan) = principal.splitAt(principal.length - (n min principal.length))
            Some((quedan, uno ++ seVan, dos))

          case Uno(n) if n < 0 =>
            val (vuelven, quedan) = uno.splitAt((-n) min uno.length)
            Some((principal ++ vuelven, quedan, dos))

          case Dos(n) if n > 0 =>
            val (quedan, seVan) = principal.splitAt(principal.length - (n min principal.length))
            Some((quedan, uno, dos ++ seVan))

          case Dos(n) if n < 0 =>
            val (vuelven, quedan) = dos.splitAt((-n) min dos.length)
            Some((principal ++ vuelven, uno, quedan))

          case _ =>
            Some(e)
        }
      } yield resultado).getOrElse(e)
    }

    def aplicarMovimientos(e: Estado, movs: Maniobra): List[Estado] = {
      @tailrec
      def aplicarMovimientosAux(movs: Maniobra, acc: List[Estado]): List[Estado] = {
        // Usamos expresiones for para construir el próximo estado y agregarlo a la acumulación
        movs match {
          case Nil => acc
          case m :: ms =>
            val nuevoEstado = for {
              ultimoEstado <- Some(acc.last)
              siguienteEstado = aplicarMovimiento(ultimoEstado, m)
            } yield siguienteEstado

            aplicarMovimientosAux(ms, acc :+ nuevoEstado.getOrElse(acc.last))
        }
      }

      aplicarMovimientosAux(movs, List(e))
    }
    def definirManiobra(t1: Tren, t2: Tren): Maniobra = {
      require(t1.toSet == t2.toSet)

      if (t1 == t2) { Nil }
      else if (t1.reverse == t2) {
        (for (_ <- 1 to t1.size) yield Dos(1)).toList
      }
      else {
        case class Nodo(estado: Estado, movimientos: List[Movimiento])
        @tailrec
        def bfs(queue: Vector[Nodo], visited: Set[Estado]): Maniobra = {
          if (queue.isEmpty) Nil
          else {
            val nodo = queue.head
            val (principal, uno, dos) = nodo.estado
            if (principal.isEmpty && uno.isEmpty && dos == t2) {
              nodo.movimientos.reverse
            } else if (visited.contains(nodo.estado)) {
              bfs(queue.tail, visited)
            } else {
              val posiblesMovimientos = {
                val movimientosPrincipal = if (principal.nonEmpty) {
                  if (principal.length > 100) {
                    List(Uno(1), Dos(1))
                  } else {
                    val movimientosUno = for (n <- 1 to principal.length) yield Uno(n)
                    val movimientosDos = for (n <- 1 to principal.length) yield Dos(n)
                    movimientosUno.toList ++ movimientosDos.toList
                  }
                } else Nil

                val movimientosUno = if (uno.nonEmpty) {
                  for (n <- 1 to uno.length) yield Uno(-n)
                } else Nil

                val movimientosDos = if (dos.nonEmpty) {
                  for (n <- 1 to dos.length) yield Dos(-n)
                } else Nil

                movimientosPrincipal ++ movimientosUno.toList ++ movimientosDos.toList
              }

              val nuevosNodos = for {
                m <- posiblesMovimientos
                nuevoEstado = aplicarMovimiento(nodo.estado, m)
                if !visited.contains(nuevoEstado)
              } yield Nodo(nuevoEstado, m :: nodo.movimientos)

              bfs(queue.tail ++ nuevosNodos.toVector, visited + nodo.estado)
            }
          }
        }

        val resultado = bfs(Vector(Nodo((t1, Nil, Nil), Nil)), Set.empty)

        if (resultado.isEmpty) {
          val moverATodos = (for (_ <- 1 to t1.length) yield Dos(1)).toList
          val devolverEnOrden = (for (_ <- t2.reverse) yield Dos(-1)).toList
          moverATodos ++ devolverEnOrden
        } else {
          resultado
        }
      }
    }

  }
}