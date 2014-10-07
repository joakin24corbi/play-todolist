package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.Task
import models.TaskUser

/**
 * Maneja el control de la base de datos de tareas
 */
object Application extends Controller {


	/**
   * Redireccion a la pagina principal
   */
  	val Home = Redirect(routes.Application.tasks)

	/**
	 * Especifica el formulario de tareas
	 */
	val taskForm = Form("label" -> nonEmptyText)

	/**
	 * Nombre del usuario por defecto
	 */
	val defaultUser : String = "anonimo"


// -- Acciones Feature1

	/**
	 * Controla la peticion por defecto
	 */
	def index = Action { Home }


	/**
	 * Muestra todas las tareas
	 * @return json lista de tareas en formato json
	 */
 	def tasks = Action {
	 //Ok(views.html.index(Task.all(), taskForm))
  		Ok(Json.toJson(Task.allByUser(defaultUser)))
	}

	/**
	 * Muestra una tarea en concreto
	 * @return json tarea en concreto en formato json
	 */
	def showTask(id: Long) = Action {
		Task.findById(id) match {
			case None => NotFound("Error: No se encuentra la tarea")
			case Some(t) => Ok(Json.toJson(t))
		}
	}
  
   /**
    * Controla la insercion de una nueva tarea
    * @return json tarea recien creada en formato json
    */
	def newTask = Action {
		implicit request => taskForm.bindFromRequest.fold(
			errors => BadRequest(views.html.index(Task.allByUser(defaultUser), errors)),
			label =>
			{
				val id = Task.create(defaultUser, label)

				Created(Json.toJson(Task.findById(id)))
			}
		)
	}
   
   /**
    * Controla el borrado de una tarea
    * @param id Identificador de la tarea a eliminar
    */
	def deleteTask(id: Long) = Action {
		Task.findById(id) match {
			case None => NotFound("Error: No se encuentra la tarea")
			case Some(t) => Task.delete(id)
								 Ok("La tarea ha sido borrada")
		}
	}


// -- Acciones Feature2

	def userTasks(login: String) = Action {
		if(TaskUser.findByName(login)) {
			Ok(Json.toJson(Task.allByUser(login)))
		}
		else {
			NotFound("Error: No existe el usuario")
		}
	}

	def userNewTask(login: String) = Action {
		implicit request => taskForm.bindFromRequest.fold(
			errors => BadRequest(views.html.index(Task.allByUser(login), errors)),
			label =>
			{
				if(TaskUser.findByName(login)) {

					val id = Task.create(login, label)

					Ok(Json.toJson(Task.findById(id)))
				}
				else {
					NotFound("Error: No existe el usuario")
				}
			}
		)
	}
}