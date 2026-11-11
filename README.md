# FideBank - Avance 2

Proyecto del curso **Programación Cliente/Servidor Concurrente** de Universidad Fidélitas.

FideBank es un sistema bancario sencillo desarrollado en Java. Permite registrar clientes, iniciar sesión con un PIN, consultar el saldo y el historial, realizar retiros y depósitos.

## Funciones implementadas

- HU-01: Crear cuenta bancaria.
- HU-02: Iniciar sesión con número de cuenta y PIN.
- HU-03: Consultar saldo e historial.
- HU-04: Retirar dinero.
- HU-05: Depositar dinero.

Son 5 de las 7 historias del Avance 1. Las transferencias y los comprobantes se reservan para la siguiente etapa.

## Conceptos de Java

- Clases y objetos
- Herencia y polimorfismo
- Excepciones y colecciones
- Interfaz gráfica Java Swing
- Serialización de cuentas en `fidebank.dat`
- Multihilos para aceptar conexiones de varios clientes
- Comunicación por sockets

## Cómo ejecutarlo

1. Abrir el proyecto en NetBeans con JDK 17 o superior.
2. Ejecutar primero `src/fidebank/Servidor.java`.
3. Ejecutar después `src/fidebank/Ventana.java`.
4. Registrar una cuenta, iniciar sesión y probar las operaciones.

**Nota:** El archivo `fidebank.dat` se genera al usar el sistema y no debe subirse al repositorio.

Este proyecto es una práctica académica y no una aplicación bancaria real.
