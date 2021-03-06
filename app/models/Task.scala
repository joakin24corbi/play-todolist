package models

import play.api.libs.json._

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

import java.util.{Date}
import java.util.Calendar
import java.text.SimpleDateFormat

case class Task(id: Long, label: String, usuario: String, fecha: Option[Date])

object Task
{  
  /**
   * Atributos de la clase tarea
   */
  val task = {
    get[Long]("id") ~ 
    get[String]("label") ~
    get[String]("usuario") ~
    get[Option[Date]]("fecha") map {
      case id~label~usuario~fecha => Task(id, label, usuario, fecha)
    }
  }

  /**
   * Variable para parsear una tarea a JSON
   */
  implicit val taskWrites = new Writes[Task] {
    def writes(task: Task) = Json.obj(
      "id" -> task.id,
      "label" -> task.label,
      "usuario" -> task.usuario,
      "fecha" -> formatDate(task.fecha)
    )
  }

  def formatDate(date: Option[Date]) : Option[String] = {
    val formatter = new SimpleDateFormat("yyyy.MM.dd")

    date match {
      case None => None
      case Some(f) => Option(formatter.format(f))
    }
  }

   /**
    * Inserta una nueva tarea
    * @param login Identificador del usuario para el que se crea la tarea
    * @param label El texto de la nueva tarea a insertar
    * @param fecha La fecha en la que la tarea caduca
    * @return El nuevo id de la tarea insertada
    */
   def create(login: String, label: String, fecha: Option[Date]) : Long = {
      var idNuevo = 0L

      DB.withConnection { implicit c =>
          idNuevo = SQL("insert into task (label, usuario, fecha) values ({label}, {login}, {fecha})")
                    .on("label" -> label, "login" -> login, "fecha" -> fecha)
                    .executeInsert().get
      }

      return idNuevo
   }

   /**
    * Elimina una tarea de la base de datos
     * @param id Identificador de la tarea a eliminar
     * @return Cantidad de lineas modificadas despues de la actualizacion
    */
   def delete(id: Long) : Int = {
      var cantidadModificado = 0

      DB.withConnection { implicit c =>
         cantidadModificado = SQL("delete from task where id = {id}").on("id" -> id).executeUpdate()
      }

      return cantidadModificado
   }

  	/**
    * Devuelve una tarea desde su id.
    * @param id El id de la tarea
    * @return La tarea que se busca
    */
  	def findById(id: Long) : Option[Task] = {
   	DB.withConnection { implicit connection =>
			SQL("select * from task where id = {id}").on("id" -> id).as(task.singleOpt)
      }
  	}

	/**
	 *	Devuelve una lista de todas las tareas
    * @param login Identificador del usuario del que se listan las tareas
    * @return Las tareas del usuario en concreto
	 */
	def allByUser(login: String): List[Task] = {
      DB.withConnection { implicit c =>
		    SQL("select * from task where usuario = {login}").on("login" -> login).as(task *)
      }
 	}

   /**
    * Busca las tareas de un determinado usuario en un dia concreto
    * @param login Usuario del que se buscan las tareas
    * @param date Fecha en la que se buscan las tareas
    * @return Lista de tareas del usuario en el dia especificado
    */
   def allByDate(login: String, date: Date) : List[Task] = {
      DB.withConnection { implicit c =>
          SQL("select * from task where usuario = {login} and fecha = {date}")
          .on("login" -> login, "date" -> date).as(task *)
      }
   }

   /**
    * Busca las tareas de un usuario posteriores a hoy
    * @param login Usuario del que se buscan las tareas
    * @return Lista de tareas posteriores a hoy
    */
   def pendentTasks(login: String) : List[Task] = {
      val today = Calendar.getInstance.getTime
      val formatter = new SimpleDateFormat("yyyy.MM.dd")
      val formattedDay = formatter.format(today)
      val formattedDate = formatter.parse(formattedDay)

      DB.withConnection { implicit c =>
          SQL("select * from task where usuario = {login} and (fecha >= {date} or fecha is null)")
          .on("login" -> login, 'date -> formattedDate).as(task *)
      }
   }
}