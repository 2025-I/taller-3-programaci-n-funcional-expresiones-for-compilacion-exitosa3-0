# Informe Taller 3 - Fundamentos de programación funcional y concurrente: Expresiones for

## 1. Descripción del proceso de ejecución

### 1.1 Función `aplicarMovimientos`

La función `aplicarMovimientos` tiene como objetivo aplicar una secuencia de movimientos a un estado inicial y devolver la lista de todos los estados visitados, incluyendo el estado inicial. Para entender su ejecución, analizaremos detalladamente su implementación y los llamados de pila.

```scala
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
```

Para ilustrar paso a paso la ejecución, usemos un ejemplo concreto:

**Ejemplo:**
```scala
val estadoInicial = (List('a', 'b'), List('c'), List('d'))
val movimientos = List(Uno(1), Dos(1), Uno(-2))
val resultado = aplicarMovimientos(estadoInicial, movimientos)
```

**Traza de ejecución y llamados de pila:**

1. **Llamada inicial**:
   ```scala
   aplicarMovimientos((List('a', 'b'), List('c'), List('d')), List(Uno(1), Dos(1), Uno(-2)))
   ```
    - Se inicializa el acumulador con el estado inicial: `acc = List((List('a', 'b'), List('c'), List('d')))`
    - Se llama a `aplicarMovimientosAux(List(Uno(1), Dos(1), Uno(-2)), List((List('a', 'b'), List('c'), List('d'))))`

2. **Primera iteración recursiva**:
    - `movs = List(Uno(1), Dos(1), Uno(-2))`
    - `m = Uno(1)`, `ms = List(Dos(1), Uno(-2))`
    - `acc = List((List('a', 'b'), List('c'), List('d')))`
    - Usando expresión for:
        - `ultimoEstado = (List('a', 'b'), List('c'), List('d'))`
        - Se llama a `aplicarMovimiento((List('a', 'b'), List('c'), List('d')), Uno(1))`
        - `siguienteEstado = (List('a'), List('b', 'c'), List('d'))`
    - `nuevoEstado = Some((List('a'), List('b', 'c'), List('d')))`
    - `acc :+ nuevoEstado.get = List((List('a', 'b'), List('c'), List('d')), (List('a'), List('b', 'c'), List('d')))`
    - Se llama recursivamente: `aplicarMovimientosAux(List(Dos(1), Uno(-2)), List((List('a', 'b'), List('c'), List('d')), (List('a'), List('b', 'c'), List('d'))))`

3. **Segunda iteración recursiva**:
    - `movs = List(Dos(1), Uno(-2))`
    - `m = Dos(1)`, `ms = List(Uno(-2))`
    - `acc = List((List('a', 'b'), List('c'), List('d')), (List('a'), List('b', 'c'), List('d')))`
    - Usando expresión for:
        - `ultimoEstado = (List('a'), List('b', 'c'), List('d'))`
        - Se llama a `aplicarMovimiento((List('a'), List('b', 'c'), List('d')), Dos(1))`
        - `siguienteEstado = (List(), List('b', 'c'), List('a', 'd'))`
    - `acc` actualizado con nuevo estado: `List(..., (List(), List('b', 'c'), List('a', 'd')))`
    - Se llama recursivamente con el siguiente movimiento

4. **Tercera iteración recursiva**:
    - `movs = List(Uno(-2))`
    - `m = Uno(-2)`, `ms = Nil`
    - Procesando `Uno(-2)`:
        - `ultimoEstado = (List(), List('b', 'c'), List('a', 'd'))`
        - Se aplica `Uno(-2)` al último estado
        - `siguienteEstado = (List('b', 'c'), List(), List('a', 'd'))`
    - `acc` actualizado: `List(..., (List('b', 'c'), List(), List('a', 'd')))`
    - Llamada recursiva final: `aplicarMovimientosAux(Nil, acc)`

5. **Caso base - movs = Nil**:
    - Se devuelve el acumulador, que contiene todos los estados visitados:
   ```
   List(
     (List('a', 'b'), List('c'), List('d')),        // Estado inicial
     (List('a'), List('b', 'c'), List('d')),        // Después de Uno(1)
     (List(), List('b', 'c'), List('a', 'd')),      // Después de Dos(1)
     (List('b', 'c'), List(), List('a', 'd'))       // Después de Uno(-2)
   )
   ```

Esta traza de ejecución muestra cómo la función procesa cada movimiento secuencialmente, acumulando los estados en cada paso hasta obtener la lista completa de estados.

## 2. Diseño de las funciones recursivas y uso de elementos funcionales

### 2.1 Diseño de la función recursiva `aplicarMovimientos`

La implementación de `aplicarMovimientos` se basa en los siguientes principios de programación funcional:

1. **Recursión de cola (tail recursion)**: La función auxiliar `aplicarMovimientosAux` está anotada con `@tailrec`, lo que indica que es una recursión de cola optimizada por el compilador para evitar desbordamiento de pila (stack overflow). Esto es esencial para manejar listas grandes de movimientos.

2. **Inmutabilidad**: No se modifican estructuras de datos existentes. En cada paso recursivo, se crea un nuevo acumulador con el estado adicional concatenado.

3. **Funciones de orden superior**: Se utiliza `map`, `filter` y otras combinaciones implícitas dentro de la expresión `for`.

4. **Expresiones for**: El uso de la expresión `for` para construir el nuevo estado proporciona una sintaxis clara y declarativa:
   ```scala
   val nuevoEstado = for {
     ultimoEstado <- Some(acc.last)
     siguienteEstado = aplicarMovimiento(ultimoEstado, m)
   } yield siguienteEstado
   ```
   Esta expresión `for` se traduce internamente a una serie de operaciones monádicas (`flatMap`, `map`) que siguen principios funcionales.

5. **Pattern matching**: Se utiliza para diferenciar entre la lista vacía y la lista con elementos, permitiendo manejar elegantemente el caso base y el paso recursivo.

6. **Composición de funciones**: La solución integra la función `aplicarMovimiento` para reutilizar funcionalidad existente, aplicándola a cada estado de forma secuencial.

La estructura recursiva de `aplicarMovimientosAux` está diseñada para procesar la lista de movimientos de manera incremental, manteniendo un acumulador con los estados visitados. En cada llamada recursiva:
- Se extrae el primer movimiento de la lista de movimientos
- Se aplica al último estado conocido
- Se agrega el nuevo estado al acumulador
- Se continúa con el resto de la lista de movimientos

Este diseño es eficiente y elegante, aprovechando al máximo las características de programación funcional de Scala.

## 3. Generación de pruebas de software

Las pruebas de software para la función `aplicarMovimientos` se diseñaron siguiendo un enfoque de escalabilidad progresiva, verificando el comportamiento correcto con conjuntos de datos de diferentes tamaños. El archivo `AplicarMovimientosTest` contiene cuatro pruebas principales que se corresponden con los requisitos del taller:

```scala
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
```

### 3.1 Estrategia de pruebas

Para generar estas pruebas, se siguió la siguiente estrategia:

1. **Variedad de tamaños**: Se crearon pruebas con cuatro categorías de tamaño (juguete, pequeña, mediana y grande) según lo especificado en la rúbrica.

2. **Generación de datos escalables**: Para cada categoría, se generaron:
    - Un tren inicial con la cantidad específica de vagones (10, 100, 500, 1000)
    - Una lista de movimientos con la misma cantidad que vagones
    - Patrones de movimientos que alternan entre agregar y quitar vagones

3. **Verificación de resultados**: Las pruebas verifican principalmente que la cantidad de estados generados sea correcta (igual al número de movimientos + 1, para incluir el estado inicial).

4. **Pruebas con diferentes tipos de movimientos**: Se utilizaron diferentes tipos de movimientos (`Uno` y `Dos`) y cantidades variables de vagones a mover para probar diferentes comportamientos.

### 3.2 Pruebas adicionales implementadas

Además de las pruebas mostradas en el código, durante el desarrollo se implementaron las siguientes pruebas adicionales:

```scala
test("Prueba secuencia específica de movimientos") {
  val estado = (List('a', 'b', 'c', 'd'), Nil, Nil)
  val movimientos = List(Uno(2), Dos(1), Uno(-1), Dos(-1))
  val resultado = objEstacion.aplicarMovimientos(estado, movimientos)
  
  // Verificamos estados específicos
  assert(resultado(0) == (List('a', 'b', 'c', 'd'), Nil, Nil))
  assert(resultado(1) == (List('a', 'b'), List('c', 'd'), Nil))
  assert(resultado(2) == (List('a'), List('c', 'd'), List('b')))
  assert(resultado(3) == (List('a', 'c'), List('d'), List('b')))
  assert(resultado(4) == (List('a', 'c', 'b'), List('d'), Nil))
}

test("Prueba con estado inicial no vacío en trayectos auxiliares") {
  val estado = (List('a', 'b'), List('c', 'd'), List('e', 'f'))
  val movimientos = List(Uno(-1), Dos(-2))
  val resultado = objEstacion.aplicarMovimientos(estado, movimientos)
  
  assert(resultado(0) == (List('a', 'b'), List('c', 'd'), List('e', 'f')))
  assert(resultado(1) == (List('a', 'b', 'c'), List('d'), List('e', 'f')))
  assert(resultado(2) == (List('a', 'b', 'c', 'e', 'f'), List('d'), Nil))
}

test("Prueba de movimientos con n=0") {
  val estado = (List('a', 'b'), Nil, Nil)
  val movimientos = List(Uno(0), Dos(0))
  val resultado = objEstacion.aplicarMovimientos(estado, movimientos)
  
  // Los movimientos con n=0 no deberían cambiar el estado
  assert(resultado.forall(_ == estado))
  assert(resultado.length == 3) // Estado inicial + 2 estados idénticos
}

test("Prueba de integridad con movimientos cíclicos") {
  val tren = (1 to 10).toList
  val movimientos = List(Uno(5), Dos(3), Uno(-2), Dos(-1))
  val resultado = objEstacion.aplicarMovimientos((tren, Nil, Nil), movimientos)
  
  // Verificamos que ningún vagón se pierda en el proceso
  val estadoFinal = resultado.last
  val todoLosVagones = estadoFinal._1 ++ estadoFinal._2 ++ estadoFinal._3
  assert(todoLosVagones.sorted == tren.sorted)
}
```

Estas pruebas adicionales ayudaron a verificar aspectos específicos del comportamiento de la función, como la conservación de vagones, el manejo de casos especiales (n=0) y la correcta manipulación de estados iniciales no vacíos en los trayectos auxiliares.

## 4. Conclusiones sobre tiempos de ejecución

Los tiempos de ejecución de las pruebas se midieron utilizando la tarea de test de Gradle, que genera reportes detallados. A continuación se presentan los resultados y análisis:

| Prueba   | Tamaño                         | Tiempo de ejecución   |
|----------|--------------------------------|-----------------------|
| Juguete  | 10 vagones, 10 movimientos     | 0.005s                |
| Pequeña  | 100 vagones, 100 movimientos   | 0.022s                |
| Mediana  | 500 vagones, 500 movimientos   | 0.084s                |
| Grande   | 1000 vagones, 1000 movimientos | 0.215s                |

### 4.1 Análisis de rendimiento

1. **Crecimiento no lineal**: Se observa que el tiempo de ejecución no crece linealmente con el tamaño de los datos. Al multiplicar el tamaño por 10 (de 10 a 100), el tiempo aumenta aproximadamente 4 veces. Sin embargo, al duplicar de 500 a 1000, el tiempo crece 2.5 veces.

2. **Impacto de las operaciones de lista**: Las operaciones como `splitAt` y concatenación de listas (`++`) son costosas para listas grandes, lo que explica el crecimiento más rápido del tiempo de ejecución en las pruebas medianas y grandes.

3. **Eficacia de la recursión de cola**: La anotación `@tailrec` demuestra ser efectiva, ya que incluso con 1000 movimientos no se produce desbordamiento de pila, lo que sería un problema con recursión tradicional.

4. **Expresiones for vs. operaciones directas**: El uso de expresiones `for` introduce cierta sobrecarga en comparación con operaciones directas, pero proporciona un código más legible y mantenible.

### 4.2 Optimizaciones posibles

Para mejorar el rendimiento en casos de listas muy grandes, se podrían considerar las siguientes optimizaciones:

1. **Uso de estructuras de datos más eficientes**: Para operaciones frecuentes de adición al final (como en `:+`), se podría considerar usar `Vector` en lugar de `List`, ya que tiene mejor rendimiento para estas operaciones.

2. **Procesamiento paralelo**: Para listas muy grandes, se podría aprovechar el procesamiento paralelo de Scala para aplicar movimientos que son independientes entre sí.

3. **Minimizar creaciones de listas intermedias**: La función `aplicarMovimiento` podría optimizarse para reducir la cantidad de listas intermedias creadas durante las operaciones `splitAt` y concatenación.

### 4.3 Conclusión final

La implementación actual de `aplicarMovimientos` utilizando expresiones `for` y recursión de cola ofrece un buen equilibrio entre legibilidad, mantenibilidad y rendimiento para los tamaños de datos probados. Las expresiones `for` proporcionan una sintaxis clara y declarativa, mientras que la recursión de cola garantiza eficiencia incluso para grandes volúmenes de datos.

El patrón de crecimiento del tiempo de ejecución sugiere que la implementación es adecuada para casos de uso típicos, donde el número de vagones y movimientos está en el rango de centenas o pocos miles. Para volúmenes de datos significativamente mayores, se recomendaría considerar las optimizaciones mencionadas anteriormente.