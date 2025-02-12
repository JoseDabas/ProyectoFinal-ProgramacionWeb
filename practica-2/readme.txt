Practica 2 - Creacion de Blog en Sesion
Grupo: 
Jose Ariel Martinez Dabas - 10144270
Hiroshy Luna - 1014-5127

Objetivo:

-Manejo de la librería Javalin para peticiones, respuestas y plantillas.
-Uso del contexto de sesión en aplicaciones web.
-Implementación de seguridad en recursos web.

Descripción del Proyecto:

-Crear artículos, etiquetarlos y asociarles comentarios.
-Solo los usuarios autenticados podrán comentar.

El modelo de datos incluye:

-Artículo: Contiene título, cuerpo y etiquetas.
-Usuario: Incluye roles (Administrador y Autor).
-Comentario: Asociado a artículos.
-Etiqueta: Relacionada con los artículos.

Funcionalidades:

Persistencia en Memoria:

-Se utilizan colecciones para almacenar las entidades.

Usuario por Defecto:

-Se crea un usuario administrador al inicializar la aplicación.
-El administrador puede crear nuevos usuarios.
-El autor puede crear artículos.

Vista de Inicio:

-Muestra los artículos del más reciente al más antiguo.
-Se visualizan las etiquetas y un resumen de los primeros 70 caracteres del artículo.

Vista de Artículo:

-Muestra el contenido completo del artículo y sus comentarios.
-Solo los usuarios autenticados pueden comentar.
-El administrador o autor puede borrar artículos inapropiados.

Gestión de Artículos:

-El administrador o autor puede crear o modificar artículos.
-Se incluyen título, cuerpo y etiquetas separadas por comas.

Interfaz Visual:

-Se utiliza Bootstrap u otro framework CSS para el diseño.

Filtros de Seguridad:

-Se implementan filtros para controlar accesos no autorizados.
