Blog con Javalin, JPA, Hibernate y CockroachDB

Este proyecto es una aplicación web para la gestión de un blog, desarrollada con la librería Javalin e implementando los conceptos del protocolo HTTP, sesiones, cookies, ORM con JPA y persistencia en bases de datos en la nube con JDBC.

-Funcionalidades Implementadas

1. Persistencia con H2 y JPA

Se utiliza la base de datos H2 en modo servidor con Hibernate como proveedor de persistencia.

2. Gestión de Artículos

Creación y modificación de artículos con título, cuerpo y etiquetas asociadas.

Evita la duplicación de artículos en la base de datos.

3. Paginación en la Página de Inicio

Se muestra un máximo de 5 publicaciones por página.

Carga dinámica de artículos según la página seleccionada.

4. Filtrado por Etiquetas

Posibilidad de visualizar todos los artículos relacionados con una etiqueta específica.

5. Autenticación con Recuerdo de Usuario

Uso de cookies encriptadas con una duración de una semana para recordar al usuario.

Eliminación de la cookie en caso de logout.

6. Gestión de Usuarios con Imágenes

Registro y almacenamiento de fotos en base64 para visualización en el perfil.

7. Registro de Inicio de Sesión con CockroachDB

Almacena la fecha, hora y usuario en una tabla externa mediante JDBC en CockroachDB Serverless.

Uso de la variable de entorno JDBC_DATABASE_URL para la configuración de conexión.