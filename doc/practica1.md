# Práctica 1

Esta primera práctica ha sido desarrollada en tres fases las cuales han ido añadiendo funcionalidad a la misma. Las fases han sido las siguientes: 

  - Feature1 - Funcionalidades para usuario por defecto
  - Feature2 - Funcionalidades con diferenciacion de usuarios
  - Feature3 - Funcionalidades con diferenciacion de fecha

Voy a introducir a continuación el funcionamiento de las funcionalidades y una pequeña explicación de la implementación de cada uno de ellas.

Antes de nada he de comentar que el formato **JSON** de una tarea representa los siguientes datos de la tarea:

```sh
{
    "id": {id},
    "label": {Descripción de la tarea},
    "usuario": {Nombre del usuario},
    "fecha": {Fecha de finalización}
}
```

### Consulta de una tarea

Devuelve la representación **JSON** de la tarea cuyo identificador se pasa en la URI.

```sh
GET /tasks/{id}
```

En el caso de que la tarea que se busque no exista en la base de datos se devolverá un error HTTP 404 (NOT FOUND) con el texto *"Error: No se encuentra la tarea"*.


### Creación de una nueva tarea

Los datos para crear una tarea nueva ("label" y "fecha") se obtienen de un formulario. El label es obligatorio pero la fecha es opcional. Si no introdujesemos fecha en el formulario no habría inconveniente ya que, se controla este caso para que cumpla con la especificación iniciandola a *null*.

```sh
POST /tasks
```

Para realizar esta funcionalidad tecleamos la anterior URI. Las tareas que devuelve son las del usuario por defecto, llamado internamente "anonimo". Esto se ha implementado así para guardar la coherencia con las funcionalidades de la *feature2* sin tener que ampliar el modelo. Si quisieramos obtener las tareas de un usuario concreto teclearimos la URI:

```sh
POST /:login/tasks
```
Para controlar que el usuario exista he creado una clase ``TaskUser`` que simplemente guarda el nombre del usuario e implementa un unico método que es buscar el usuario mediante el nombre. De esta forma podemos saber si un usuario existe o no en la base de datos mediante una única y rapida búsqueda en la base de datos.

Se devuelve un **JSON** con los datos de la nueva tarea creada y el código HTTP 201 (CREATED). Si el usuario no existe se devuelve el error HTTP 404 (NOTFOUND) con el texto "Error: No existe el usuario".


### Listado de tareas

Devuelve una colección **JSON** con la lista de tareas. Se accede a la funcionalidad con la siguiente URI:

```sh
GET /tasks
```

Del mismo modo que en la funcionalidad anterior, existen dos variantes, cada una de ellas implementada para diferente *feature*. La primera, nombrada ya, devuelve todas las tareas del usuario "anonimo" pasando este dato como parametro por defecto al método ``def allByUser(login: String): List[Task]`` implementado en el modelo ``app/models/Task.scala``.

La segunda variante, que instanciamos mediante la siguiente URI, pasa al método ``allByUser`` el nombre del usuario que introduzcamos en el lugar de *:login*.

```sh
GET /:login/tasks
```

Si el usuario del que se buscan las tareas no existe se devuelve el error HTTP 404 (NOTFOUND) con el texto "Error: No existe el usuario".


### Borrado de una tarea

Borra una tarea cuyo identificador se pasa en la URI. Si la tarea no existe se devuelve un código HTTP 404 (NOT FOUND) con el texto "Error: No se encuentra la tarea". En el caso de que la tarea sea borrada satisfactoriamente se devuelve un código HTTP 200 (OK) con el texto ""La tarea ha sido borrada".


### Busqueda de tareas segun la fecha

Busca todas las tareas de un usuario en dia determinado. Devuelve un **JSON** con una lista de tareas que cumplen las restricciones. Para ejecutarlo hay que introducir la URI:

```sh
GET /:login/tasks/:date
```

Se puede observar que obtenermos el usuario y el día a traves de la URI. La fecha que he elijido para la fecha es la siguiente: ``"yyyy.MM.dd"`` donde **y** indica el año con 4 cifras, **M** indica el mes con dos cifras y **d** hace referencia al día indicado con dos cifras.

Si lo introducir despues de */tasks/* (sea una fecha o no) no cumple con el formato indicado saltará un error HTTP 400 (BADREQUEST) con el texto "Error: Formato de fecha".

Si el usuario no existe, como en los casos anteriores se devuelve el error HTTP 404 (NOTFOUND) con el texto "Error: No existe el usuario".


### Tareas no acabadas

Realiza la busqueda de las tareas que contengan la fecha de hoy, una fecha posterior o que no tengan fecha y las devuelve en formato **JSON**.

La URI para utilizar esta funcionalidad es:

```sh
GET /tasks/unfinished
```

para el usuario por defecto o

```sh
GET /:login/tasks/unfinished
```

para un usuario diferente.

Si el usuario del que se buscan las tareas no existe se devuelve el error HTTP 404 (NOTFOUND) con el texto "Error: No existe el usuario".