package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

import play.api.libs.json._
import play.api.libs.functional.syntax._

import java.text.SimpleDateFormat
import java.util.{Date}

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
	val taskForm = Form(
		mapping (
			"id" -> ignored(0L),
			"label" -> nonEmptyText,
			"usuario" -> ignored(""),
			"date" -> optional(date("yyyy.MM.dd"))
		)(Task.apply)(Task.unapply)
	)


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
	 * Muestra todas las tareas del usuario por defecto "anonimo"
	 * @return Lista de tareas en formato JSON
	 */
 	def tasks = Action {
  		Ok(Json.toJson(Task.allByUser(defaultUser)))
	}

	/**
	 * Muestra una tarea en concreto
	 * @param id Identificador de la tarea a buscar
	 * @return Tarea en concreto en formato JSON
	 */
	def showTask(id: Long) = Action {
		Task.findById(id) match {
			case None => NotFound("Error: No se encuentra la tarea")
			case Some(t) => Ok(Json.toJson(t))
		}
	}
  
   /**
    * Controla la insercion de una nueva tarea, utilizando el usuario y una fecha opcional
    * @return Tarea recien creada en formato JSON
    */
	def newTask = Action {
		implicit request => taskForm.bindFromRequest.fold(
			errors => BadRequest,
			data =>
			{
				val id = Task.create(defaultUser, data.label, data.fecha)

				Created(Json.toJson(Task.findById(id)))
			}
		)
	}
   
   /**
    * Controla el borrado de una tarea
    * @param id Identificador de la tarea a eliminar
    * @return Ok or NotFound dependiendo del proceso
    */
	def deleteTask(id: Long) = Action {
		Task.findById(id) match {
			case None => NotFound("Error: No se encuentra la tarea")
			case Some(t) => Task.delete(id)
								 Ok("La tarea ha sido borrada")
		}
	}


// -- Acciones Feature2

	/**
	 * Muestra las tareas de un usuario en concreto
	 * @param login El nombre del usuario
	 * @return Todas las tareas guardadas del usuario en formato JSON
	 */
	def userTasks(login: String) = Action {
		if(TaskUser.findByName(login)) {
			Ok(Json.toJson(Task.allByUser(login)))
		}
		else {
			NotFound("Error: No existe el usuario")
		}
	}

	/**
	 * Introduce una tarea para un determinado usuario añadiendo opcionalmente la fecha
	 * @param login Nombre del usuario que introduce la tarea
	 * @return La tarea ya creada en formato JSON
	 */
	def userNewTask(login: String) = Action {
		implicit request => taskForm.bindFromRequest.fold(
			errors => BadRequest,
			data =>
			{
				if(TaskUser.findByName(login)) {

					val id = Task.create(login, data.label, data.fecha)

					Ok(Json.toJson(Task.findById(id)))
				}
				else {
					NotFound("Error: No existe el usuario")
				}
			}
		)
	}


// -- Acciones Feature3

	/**
	 * Busca todas las tareas en una determinada fecha
	 * @param login Usuario del que se buscan las tareas
	 * @param date Fecha de la que se buscan tareas
	 * @return Lista de tareas del usuario en el dia introducido en formato JSON
	 */
	def userTasksByDate(login: String, date: String) = Action {
		if(TaskUser.findByName(login)) {
			val formatter = new SimpleDateFormat("yyyy.MM.dd")

			try {
				val parsedDate = formatter.parse(date)
				val tasks = Task.allByDate(login, parsedDate)

				Ok(Json.toJson(tasks))
			}
			catch {
				case ex : java.text.ParseException => BadRequest("Error: Formato de fecha")
			}
		}
		else {
			NotFound("Error: No existe el usuario")
		}
	}

	/**
	 * Busca las tareas posteriores a hoy para el usuario por defecto
	 * @return Lista de tareas que cumplen el requisito en formato JSON
	 */
	def unfinishedTasks : Action[AnyContent] = {
		unfinishedUserTasks(defaultUser)
	}

	/**
	 * Busca las tareas posteriores a hoy para un determinado usuario
	 * @param login Usuario del que se buscan las tareas
	 * @return Lista de tareas que cumplen el requisito en formato JSON
	 */
	def unfinishedUserTasks(login: String) = Action {
		if(TaskUser.findByName(login)) {
			val tasks = Task.pendentTasks(login)

			Ok(Json.toJson(tasks))
		}
		else {
			NotFound("Error: No existe el usuario")
		}
	}
}