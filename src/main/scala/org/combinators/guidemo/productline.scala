package org.combinators.guidemo

import javax.inject.Inject

import org.combinators.cls.git.{InhabitationController, Results, RoutingEntries}
import org.combinators.cls.interpreter.CombinatorInfo
import org.combinators.guidemo.domain.CoffeeBar
import org.combinators.guidemo.domain.instances.{Copenhagen, Dortmund, WPI}
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle
import play.api.mvc.InjectedController


abstract class CoffeeBarVariationController(webJars: WebJarsUtil, lifeCycle: ApplicationLifecycle)
  extends InhabitationController(webJars, lifeCycle)
  with RoutingEntries {
  val coffeeBar: CoffeeBar
  lazy val repository = new Repository(coffeeBar)
  lazy val Gamma = repository.forInhabitation
  override lazy val combinatorComponents: Map[String, CombinatorInfo] = Gamma.combinatorComponents
  override lazy val results: Results = repository.getResults.compute()
  override lazy val controllerAddress: String = coffeeBar.getClass.getSimpleName.toLowerCase
}

class WPIController @Inject()(webJars: WebJarsUtil, lifeCycle: ApplicationLifecycle)
  extends CoffeeBarVariationController(webJars, lifeCycle) {
  lazy val coffeeBar: CoffeeBar = new WPI()
}

class CopenhagenController @Inject()(webJars: WebJarsUtil, lifeCycle: ApplicationLifecycle)
  extends CoffeeBarVariationController(webJars, lifeCycle) {
  lazy val coffeeBar: CoffeeBar = new Copenhagen()
}

class DortmundController @Inject()(webJars: WebJarsUtil, lifeCycle: ApplicationLifecycle)
  extends CoffeeBarVariationController(webJars, lifeCycle) {
  lazy val coffeeBar: CoffeeBar = new Dortmund()
}

class GUIDemoDataServer() extends InjectedController {
  def productOptions() = Action {
    Ok(s"""["Coffee", "Espresso", "Cappuccino"]""").as("text/plain")
  }
}