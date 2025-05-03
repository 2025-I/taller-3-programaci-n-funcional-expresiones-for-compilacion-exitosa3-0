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
  }
}