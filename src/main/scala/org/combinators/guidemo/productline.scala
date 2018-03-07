package org.combinators.guidemo

import javax.inject.Inject

import org.combinators.cls.git.{InhabitationController, Results, RoutingEntries}
import org.combinators.cls.interpreter.CombinatorInfo
import org.combinators.guidemo.domain.CoffeeBar
import org.combinators.guidemo.domain.instances.{Copenhagen, Dortmund, WPI}
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle

abstract class CoffeeBarVariationController(dataProviderController: DataProviderController, webJars: WebJarsUtil, lifeCycle: ApplicationLifecycle)
  extends InhabitationController(webJars, lifeCycle)
  with RoutingEntries {
  val coffeeBar: CoffeeBar
  lazy val repository = new Repository(coffeeBar)
  lazy val Gamma = repository.forInhabitation
  override lazy val combinatorComponents: Map[String, CombinatorInfo] = Gamma.combinatorComponents
  override lazy val results: Results = repository.getResults
  override lazy val controllerAddress: String = coffeeBar.getClass.getSimpleName.toLowerCase
}

class WPIController @Inject()(dataProviderController: DataProviderController, webJars: WebJarsUtil, lifeCycle: ApplicationLifecycle)
  extends CoffeeBarVariationController(dataProviderController, webJars, lifeCycle) {
  lazy val coffeeBar: CoffeeBar = new WPI()
}

class CopenhagenController @Inject()(dataProviderController: DataProviderController, webJars: WebJarsUtil, lifeCycle: ApplicationLifecycle)
  extends CoffeeBarVariationController(dataProviderController, webJars, lifeCycle) {
  lazy val coffeeBar: CoffeeBar = new Copenhagen()
}

class DortmundController @Inject()(dataProviderController: DataProviderController, webJars: WebJarsUtil, lifeCycle: ApplicationLifecycle)
  extends CoffeeBarVariationController(dataProviderController, webJars, lifeCycle) {
  lazy val coffeeBar: CoffeeBar = new Dortmund()
}