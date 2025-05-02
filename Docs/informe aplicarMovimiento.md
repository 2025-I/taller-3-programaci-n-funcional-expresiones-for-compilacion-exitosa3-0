# informe funcion aplicarMovimiento

### la funcion aplicarMovimiento sirve para simular el movimiento de vagones entre tres vías de un tren, según un tipo de operación dada (del tipo Movimiento), y devuelve Un nuevo Estado (una nueva tupla de tres listas), reflejando el movimiento de los vagones según lo solicitado.

```
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
```
## tabla de ejecucion y estado de la pila
### val e1 = (List(’a’ , ’b’ , ’c’ , ’d’) , Nil , Nil) --> aplicarMovimiento(e1 ,Uno(2))

| Paso | Estado Principal (P)     | Estado Uno (U) | Estado Dos (D) | Movimiento | Descripción                                              | Estado de la Pila                                       |
|------|---------------------------|----------------|----------------|------------|----------------------------------------------------------|----------------------------------------------------------|
| 1    | List('a','b','c','d')     | Nil            | Nil            | Uno(2)     | Inicio de `aplicarMovimiento` con `Uno(2)`              | `aplicarMovimiento((List(a,b,c,d), Nil, Nil), Uno(2))`   |
| 2    | List('a','b','c','d')     | Nil            | Nil            | Uno(2)     | Se hace split: quedan = List('a','b'), seVan = List('c','d') | `splitAt(4 - 2) = (List(a,b), List(c,d))`                |
| 3    | List('a','b')             | List('c','d')  | Nil            | —          | Se actualiza la vía uno con `uno ++ seVan`              | Retorna `Some((List(a,b), List(c,d), Nil))`             |
| 4    | List('a','b')             | List('c','d')  | Nil            | —          | Resultado final después del movimiento                  | Valor final asignado a `e2`                             |
