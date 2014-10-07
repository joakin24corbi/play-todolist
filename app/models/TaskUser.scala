package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class TaskUser(id: Long, name: String)

object TaskUser
{  
  /**
   * Atributos de la clase tarea
   */
  val taskUser = {
    get[Long]("id") ~ 
    get[String]("name") map {
       case id~name => TaskUser(id, name)
    }
  }

  /**
   * Devuelve una tarea desde su id.
   * @param id El id de la tarea
   */
  def findByName(name: String) : Boolean = {
    var user : Option[TaskUser] = null

    DB.withConnection { implicit connection =>
       user = SQL("select * from taskUser where name = {name}").on('name -> name).as(taskUser.singleOpt)
    }

    user match {
      case None => false
      case Some(t) => true
    }
  }
}
