# Lenguajes de Consultas de Datos

Proyecto de Persistencia de Datos (U3) que implementa consultas **JPQL** para consultar categorías, productos, clientes, facturas y métodos de pago, utilizando Java, Maven, una interfaz gráfica Swing y persistencia relacional en **PostgreSQL** mediante **Jakarta Persistence (JPA)** y **Hibernate ORM**.

El proyecto aplica el Mapeo Objeto-Relacional (ORM) con entidades JPA y ejecuta consultas mediante `EntityManager`. La interfaz muestra los resultados de las consultas en paneles de texto, con formato y etiquetas resaltadas.

## ⚙️ Características Técnicas

* **Consultas JPQL** para obtener, ordenar y filtrar información de las entidades.
* **Consultas de clientes:** clientes por nombre, por apellido y clientes que tienen facturas.
* **Consultas de facturas:** todas las facturas, ventas totales, máximo y mínimo de ventas, facturas por fecha y facturas por cliente.
* **Mapeo Objeto-Relacional (ORM):** anotaciones JPA (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@ManyToOne`, `@OneToMany` y `@JoinColumn`).
* **Resultados formateados:** los resultados se muestran en `JTextPane` con etiquetas en negrita.
* **Orden ascendente:** las consultas de clientes y facturas se ordenan mediante `ORDER BY ... ASC`.
* **Buscadores:** permiten filtrar productos, facturas, clientes y métodos de pago.
* **Pool de conexiones** configurado mediante Hibernate y HikariCP.

## 🛠️ Requisitos Previos

* **Java Development Kit (JDK)** 21 o superior.
* **Apache Maven**.
* **PostgreSQL** activo en `localhost:5432`.
* **IntelliJ IDEA** (recomendado por el diseñador de formularios Swing).

## 📦 Dependencias principales

| Librería | Versión | Uso |
|---|---|---|
| Jakarta Persistence API | 3.2.0 | Entidades y `EntityManager` |
| Hibernate ORM | 7.4.13.Final | Implementación de JPA |
| PostgreSQL JDBC | 42.7.14 | Conexión con PostgreSQL |
| HikariCP | 7.1.0 | Pool de conexiones |
| Lombok | 1.18.48 | Generación de métodos de las entidades |
| forms_rt | 7.0.3 | Runtime de formularios Swing |

## 📂 Estructura del Proyecto

```text
src/main/
 ├── java/U1/
 │   ├── Main.java                       # Punto de entrada
 │   ├── controlador/
 │   │   ├── CategoriaControlador.java
 │   │   ├── ClientesControlador.java
 │   │   ├── FacturaControlador.java
 │   │   ├── MetodoDePagoControlador.java
 │   │   └── ProductosControlador.java
 │   ├── modelo/
 │   │   ├── Categoria.java
 │   │   ├── Cliente.java
 │   │   ├── Factura.java
 │   │   ├── FacturaDetalle.java
 │   │   ├── MetodoDePago.java
 │   │   └── Productos.java
 │   ├── util/
 │   │   └── ConexionJPA.java            # Fábrica de EntityManager
 │   └── vista/
 │       ├── base/
 │       │   ├── VentanaEmpresaUI.java   # Componentes generados por IntelliJ
 │       │   └── VentanaEmpresa.form     # Diseño visual
 │       └── VentanaEmpresa.java         # Eventos y presentación
 └── resources/
     ├── images/
     │   ├── pestañas/                   # Capturas de las pestañas
     │   └── Diagrama de Clases.png
     ├── META-INF/
     │   └── persistence.xml             # Unidad de persistencia
     ├── sql/                            # Scripts SQL de las tablas
     └── uml/                            # Diagramas UML
```

## 📊 Modelo de Datos

| Tabla | Descripción |
|---|---|
| `categorias` | Categorías de productos |
| `productos` | Productos con precio, cantidad y categoría |
| `clientes` | Clientes registrados |
| `facturas` | Facturas con fecha, subtotal, impuesto y total |
| `factura_detalle` | Detalles de productos de cada factura |
| `metodos_pago` | Métodos de pago disponibles |

Relaciones principales:

* Una categoría tiene muchos productos.
* Un cliente tiene muchas facturas.
* Una factura tiene muchas líneas de detalle.
* Cada detalle de factura pertenece a un producto.

## 🖼️ Evidencia de ejecución

**Pestaña Clientes**

![Pestaña Clientes](src/main/resources/images/pestañas/Pestaña%20Clientes.png)

**Pestaña Categorías**

![Pestaña Categorías](src/main/resources/images/pestañas/Pestaña%20Categorías%20.png)

**Pestaña Productos**

![Pestaña Productos](src/main/resources/images/pestañas/Pestaña%20Productos.png)

**Pestaña Factura**

![Pestaña Factura](src/main/resources/images/pestañas/Pestaña%20Factura.png)

## 🚀 Instrucciones de Uso

### 1. Clonar el proyecto

```bash
git clone https://github.com/20Martin05/Lenguajes-de-Consultas-de-Datos.git
cd Lenguajes-de-Consultas-de-Datos
```

### 2. Preparar PostgreSQL

1. Inicia PostgreSQL en el puerto `5432`.
2. Crea la base de datos que utilizará la aplicación.
3. Ejecuta los scripts de `src/main/resources/sql/` respetando las relaciones entre las tablas.

### 3. Configurar y ejecutar el programa

1. Abre el proyecto en IntelliJ IDEA, Eclipse, NetBeans o VS Code como proyecto Maven.
2. Configura en `src/main/resources/META-INF/persistence.xml` la URL, usuario y contraseña de tu base de datos:

```xml
<property name="jakarta.persistence.jdbc.url"
          value="jdbc:postgresql://localhost:5432/TU_BASE_DE_DATOS"/>
<property name="jakarta.persistence.jdbc.user" value="TU_USUARIO"/>
<property name="jakarta.persistence.jdbc.password" value="TU_CONTRASEÑA"/>
```

3. Recarga las dependencias Maven.
4. Ejecuta la clase principal `U1.Main`.

Para compilar el proyecto:

```bash
mvn clean package
```

### Uso de la interfaz

* **Clientes:** consulta clientes ordenados por ID, por nombre, por apellido o que tienen facturas.
* **Categorías:** consulta las categorías disponibles mediante los botones de consulta.
* **Productos:** consulta productos, categorías, existencias y precios.
* **Facturas:** consulta todas las facturas, ventas totales, valores máximo y mínimo, fechas y clientes.
* **Métodos de pago:** consulta los métodos de pago por tipo y estado.
* **Buscadores:** filtran productos por nombre, facturas por cliente, clientes por nombre y métodos de pago por tipo.

## 👤 Autor

* **Desarrollado por:** Martin Rody Rosero Játiva
* **Materia:** Persistencia de Datos
* **Universidad:** Universidad Politécnica Salesiana
* **Bloque:** Bloque I - Unidad 3 (Lenguajes de Consultas de Datos)
