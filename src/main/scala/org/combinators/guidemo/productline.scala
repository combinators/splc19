package org.combinators.guidemo

import java.nio.file.{Files, Paths}

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, InjectedController}
import org.combinators.guidemo.Helpers._
import com.github.javaparser.ast.CompilationUnit
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.{Omega, Type}
import org.combinators.cls.git.{EmptyResults, InhabitationController, Results}
import org.combinators.templating.persistable.JavaPersistable._
import org.combinators.cls.types.syntax._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle

class Productline @Inject()(webJars: WebJarsUtil, lifeCycle: ApplicationLifecycle) extends InhabitationController(webJars, lifeCycle) {

  lazy val repository = new Repository
  lazy val Gamma =
    ReflectedRepository(
      repository,
      classLoader = this.getClass.getClassLoader,
      substitutionSpace = repository.kinding)
  lazy val combinatorComponents = Gamma.combinatorComponents
  lazy val jobs =
    Gamma.InhabitationBatchJob[Form]('OrderMenu(Omega))

  lazy val results: Results = Helpers.addDependencies(this)(EmptyResults().addAll(jobs.run()))

  /** Always prepares result 0 before checking out */
  override def serveFile(name: String): Action[AnyContent] = {
    implicit val ex = defaultExecutionContext
    Action.async(request =>
      super.prepare(0)(request).flatMap(_ => super.serveFile(name)(request))
    )
  }
}

class GUIDemoDataServer() extends InjectedController {
  def logo() = Action {
    Ok(Files.readAllBytes(Paths.get(getClass.getResource("logo.png").toURI))).as("image/png")
  }
  def alternatelogo() = Action {
    Ok(Files.readAllBytes(Paths.get(getClass.getResource("alternatelogo.png").toURI))).as("image/png")
  }
  def productOptions() = Action {
    Ok(s"""["Coffee", "Espresso", "Cappuccino"]""").as("text/plain")
  }
}