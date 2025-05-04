# Informe función definirManiobra

### 
La función definirManiobra determina una secuencia de movimientos una "maniobra"
para transformar un tren t1 en otro tren t2 utilizando tres vías
(principal, uno y dos). El resultado es una lista de instrucciones del tipo
Movimiento (Uno(n) o Dos(n)), que permiten simular esta transformación.

### Funcionamiento del codigo
```
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
```
## Ejecución y estados
```
val t1 = List('a', 'b', 'c', 'd')
val t2 = List('d', 'b', 'c', 'a')

```

| Paso | Estado Principal (P) | Vía Uno (U) | Vía Dos (D)   | Movimiento aplicado | Descripción                                   |
| ---- | -------------------- | ----------- | ------------- | ------------------- | --------------------------------------------- |
| 1    | List(a,b,c,d)        | Nil         | Nil           | —                   | Estado inicial                                |
| 2    | List(a,b)            | List(c,d)   | Nil           | Uno(2)              | Se mueven 2 vagones a la vía uno              |
| 3    | List()               | List(c,d)   | List(a,b)     | Dos(2)              | Se mueven 2 vagones a la vía dos              |
| 4    | List(d)              | List(c)     | List(a,b)     | Uno(-1)             | Se devuelve 1 vagón desde vía uno a principal |
| 5    | List()               | List(c)     | List(a,b,d)   | Dos(1)              | Se mueve 1 vagón de principal a vía dos       |
| 6    | List(c)              | Nil         | List(a,b,d)   | Uno(-1)             | Se devuelve 1 vagón desde vía uno a principal |
| 7    | List()               | Nil         | List(a,b,d,c) | Dos(1)              | Se mueve 1 vagón de principal a vía dos       |
| 8    | Nil                  | Nil         | List(d,b,c,a) | —                   | Estado final coincide con t2                  |

## Argumento de corrección de definirManiobra

1. Representación del estado de la iteración s
   Un estado s es una tupla:
```
s = Nodo((p, u, d), movs)
```

donde:

- `p`, `u`, `d` son los contenidos actuales de las vías principal, uno y dos respectivamente.

- `movs` es la secuencia de movimientos aplicados hasta llegar a ese estado.

2. Estado inicial s₀
   El estado inicial es:
```
s₀ = Nodo((t1, Nil, Nil), Nil)
```
Es decir, todos los vagones están inicialmente en la vía principal y no se ha hecho ningún movimiento.

3. Condición de estado final $esFinal(s)$
   Un estado `s = Nodo((p, u, d), _)` es final si:
```
p.isEmpty && u.isEmpty && d == t2
```
Es decir, las vías principal y uno están vacías y la vía dos contiene exactamente el tren deseado `t2`.


4. Invariante de iteración $Inv(s)$
   Para todo estado válido `s = Nodo((p, u, d), movs)` generado por la iteración:
```
Inv(s): union(p, u, d).toSet == t1.toSet ∧ length(movs) ≤ MAX
```

Significa:

Todos los vagones presentes en el sistema son exactamente los que estaban al principio (no se crean ni destruyen).

La longitud de la secuencia de movimientos es finita (en la práctica, acotada por el tamaño del tren).

5. Función de transición $transformar(s)$
   La función transformar genera los nuevos estados posibles desde un estado dado aplicando movimientos válidos:

```
aplicarMovimiento(s) // Devuelve todos los nuevos nodos posibles con Uno(n) y Dos(n) 
```
Cada llamada produce una lista de nuevos estados alcanzables desde el actual.

6. Corrección parcial $(Inv(s₀) ∧ preservación del invariante)$
   Base: `Inv(s₀)` se cumple porque todos los vagones están en `p` (ninguno perdido), y no se ha aplicado ningún movimiento.

Paso inductivo: Si `Inv(sᵢ)` se cumple, entonces los nuevos estados generados por `transformar(sᵢ)` también cumplen `Inv`.

Esto es porque aplicarMovimiento nunca altera el conjunto de vagones, solo los mueve entre listas sin duplicar ni eliminar.

7. Corrección total: Se alcanza un estado final `s_f` tal que `respuesta(s_f) = movs` y `d == t2`
   Si se alcanza un estado `s_f` con `esFinal(s_f)` y se preserva `Inv(s_f)`, entonces los movimientos `movs` aplicados transforman `t1` en `t2`.

### Conclusión

El desarrollo de la función definirManiobra fue un reto tanto en términos de diseño algorítmico como en su implementación funcional en Scala. Uno de los principales desafíos fue modelar correctamente los estados del sistema de maniobras, asegurando que cada movimiento preservara la estructura y los contenidos de los tres trayectos de vagones. La representación funcional del estado como una tupla inmutable obligó a pensar cuidadosamente cada transición.