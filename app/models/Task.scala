package models

import play.api.libs.json._
import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Task(id: Long, label: String, usuario: String)

object Task
{  
   /**
    * Atributos de la clase tarea
    */
	val task = {
		get[Long]("id") ~ 
		get[String]("label") ~
      get[String]("usuario") map {
			case id~label~usuario => Task(id, label, usuario)
  		}
	}

   /**
    * Variable para parsear una tarea a JSON
    */
   implicit val taskWrites = new Writes[Task] {
      def writes(task: Task) = Json.obj(
         "id" -> task.id,
         "label" -> task.label,
         "usuario" -> task.usuario
      )
   }

  	/**
    * Devuelve una tarea desde su id.
    * @param id El id de la tarea
    */
  	def findById(id: Long) : Option[Task] = {
   	DB.withConnection { implicit connection =>
			SQL("select * from task where id = {id}").on("id" -> id).as(task.singleOpt)
      }
  	}

	/**
	 *	Devuelve una lista de todas las tareas
	 */
	def allByUser(login: String): List[Task] = {
      DB.withConnection { implicit c =>
		    SQL("select * from task where usuario = {login}").on("login" -> login).as(task *)
      }
 	}

 	/**
 	 * Inserta una nueva tarea
 	 * @param label La nueva tarea a insertar
 	 */
	def create(login: String, label: String) : Long = {
      var idNuevo = 0L
		DB.withConnection { implicit c =>
          idNuevo = SQL("insert into task (label, usuario) values ({label}, {login})").on("label" -> label, "login" -> login).executeInsert().get
		}

      return idNuevo//Task(idNuevo, label)
	}

	/**
	 * Elimina una tarea de la base de datos
	  * @param id Identificador de la tarea a eliminar
	 */
	def delete(id: Long) : Int = {
      var cantidadModificado = 0
		DB.withConnection { implicit c =>
			cantidadModificado = SQL("delete from task where id = {id}").on("id" -> id).executeUpdate()
		}

      return cantidadModificado
 	}
}