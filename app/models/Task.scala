package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Task(id: Long, label: String)

object Task
{  
   /**
    * Atributos de la clase tarea
    */
	val task = {
		get[Long]("id") ~ 
		get[String]("label") map {
			case id~label => Task(id, label)
  		}
	}

  	/**
    * Devuelve una tarea desde su id.
    * @param id El id de la tarea
    */
  	def findById(id: Long) : Option[Task] = {
   	DB.withConnection { implicit connection =>
			SQL("select * from task where id = {id}").on('id -> id).as(task.singleOpt)
		}
  	}

	/**
	 *	Devuelve una lista de todas las tareas
	 */
	def all(): List[Task] = DB.withConnection { implicit c =>
		SQL("select * from task").as(task *)
 	}

 	/**
 	 * Inserta una nueva tarea
 	 * @param label La nueva tarea a insertar
 	 */
	def create(label: String) : Long = {
      var idNuevo = 0L
		DB.withConnection { implicit c =>
          idNuevo = SQL("insert into task (label) values ({label})").on('label -> label).executeInsert().get
		}

      return idNuevo
	}

	/**
	 * Elimina una tarea de la base de datos
	  * @param id Identificador de la tarea a eliminar
	 */
	def delete(id: Long) {
		DB.withConnection { implicit c =>
			SQL("delete from task where id = {id}").on('id -> id).executeUpdate()
		}
 	}
}