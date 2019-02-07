package gpl.productline.shell

import javax.inject.Inject
import org.webjars.play.WebJarsUtil
import com.github.javaparser.ast.CompilationUnit
import gpl.domain._
import gpl.productline.{GPLDomain, extensions}
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git._
import org.combinators.cls.types.Constructor
import org.combinators.templating.persistable.JavaPersistable._
import play.api.inject.ApplicationLifecycle
//with RoutingEntries
class Simple @Inject()(webJars: WebJarsUtil, lifeCycle: ApplicationLifecycle) extends InhabitationController(webJars, lifeCycle) with SemanticTypes with RoutingEntries {
  /*
* *
    <feature automatic="selected" name="Gpl"/>          // didn't see this
      <feature automatic="selected" name="MainGpl"/>      // didn't see this
      <feature automatic="selected" name="Test"/>           //didn't see this
      <feature automatic="selected" name="StartHere"/>         //didn't see this
      <feature automatic="selected" manual="selected" name="Base"/>
      <feature automatic="selected" name="Benchmark"/>
      <feature manual="selected" name="Prog"/>
  */

  // how to construct main in Prog

  val graph:Graph = new undirectedPrimNeighborNodes

  lazy val repository = new GPLDomain(graph) with extensions {}

  lazy val Gamma = repository.init(ReflectedRepository(repository, classLoader = this.getClass.getClassLoader), graph)

  lazy val combinatorComponents = Gamma.combinatorComponents

  //lazy val targets: Seq[Constructor] = Synthesizer.allTargets(graph)  // perhaps later


//How to select features

  lazy val targets1: Seq[Constructor] =Seq(graphSemantics(graphSemantics.base))  //Synthesizer.allTargets   in shared

  lazy val targets2: Seq[Constructor] = Seq(vertexSemantics(vertexSemantics.base))

  lazy val targets3: Seq[Constructor] = Seq(vertexIterSemantics(vertexIterSemantics.base))

  lazy val targets4: Seq[Constructor] = Seq(edgeIfcSemantics(edgeIfcSemantics.base))

  lazy val targets5: Seq[Constructor] = Seq(edgeIterSemantics(edgeIterSemantics.base))

  lazy val targets6: Seq[Constructor] = Seq(neighborIfcSemantics(neighborIfcSemantics.base))

 // lazy val targets7: Seq[Constructor] = Seq(workSpaceSemantics(workSpaceSemantics.base))

  lazy val targets8: Seq[Constructor] = Seq(graphBMSemantics(graphBMSemantics.extensions))

  lazy val targets9: Seq[Constructor] = Seq(graphProgSemantics(graphProgSemantics.extensions))

  lazy val targetsFinal:Seq[Constructor] = targets1++targets2++targets3++targets4++targets5++targets6++targets8++targets9

  //Seq(vertexSemantics(vertexSemantics.base))
  //targets1++targets2++targets3++targets4++targets5++targets6++targets7

  lazy val results: Results =
    EmptyInhabitationBatchJobResults(Gamma).addJobs[CompilationUnit](targetsFinal).compute()

  lazy val controllerAddress: String = graph.name

}
