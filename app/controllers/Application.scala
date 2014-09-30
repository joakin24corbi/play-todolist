package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.Task

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
	 * Variable para parsear una tarea a JSON
	 */
	implicit val taskWrites = new Writes[Task] {
  		def writes(task: Task) = Json.obj(
    		"id" -> task.id,
    		"label" -> task.label
  		)
	}


// -- Acciones

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

		val json = Json.toJson(Task.all)
  		Ok(json)
	}

	/**
	 * Muestra una tarea en concreto
	 * @return json tarea en concreto en formato json
	 */
	def showTask(id: Long) = Action {
		val json = Json.toJson(Task.findById(id))

		if(json != null) {
  			Ok(json)
  		}
  		else {
  			NotFound("Error: No se encuentra la tarea")
  		}
	}
  
   /**
    * Controla la insercion de una nueva tarea
    * @return json tarea recien creada en formato json
    */
	def newTask = Action {
		implicit request => taskForm.bindFromRequest.fold(
			errors => BadRequest(views.html.index(Task.all(), errors)),
			label =>
			{
				val newTask = Task.create(label)

				Home
			}
		)
	}
   
   /**
    * Controla el borrado de una tarea
    */
	def deleteTask(id: Long) = Action {
		try {
			val task = Task.findById(id)
		}
		catch {
			case e: Exception => NotFound("Error: No se encuentra la tarea")
		}

		Task.delete(id)
		Home
	}
}