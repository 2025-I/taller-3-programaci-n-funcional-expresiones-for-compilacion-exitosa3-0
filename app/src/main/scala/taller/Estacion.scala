package taller

object ManiobrasTrenes {

  type Vagon = Any
  type Tren = List[Vagon]
  type Estado = (Tren, Tren, Tren)

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
      } yield resultado) match {
        case Some(estado) => estado
        case None => e
      }
    }
  }
}
