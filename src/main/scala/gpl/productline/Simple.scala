package gpl.productline

import javax.inject.Inject

import org.webjars.play.WebJarsUtil
import com.github.javaparser.ast.CompilationUnit
import gpl.domain._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.git._
import org.combinators.cls.types.Constructor
import org.combinators.templating.persistable.JavaPersistable._
import play.api.inject.ApplicationLifecycle

import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._

class Simple @Inject()(webJars: WebJarsUtil, lifeCycle: ApplicationLifecycle) extends InhabitationController(webJars, lifeCycle) with SemanticTypes with RoutingEntries {

  // specify desired target by (a) declaring algorithm traits; (b) graph structure
  val graph:Graph = new Target
    with UndirectedEdges with WeightedEdges
    with NeighborStorage with LabeledVertex with UncoloredVertex
    with Prim

    //new undirectedNeighborNodes

  /** KlondikeDomain for Klondike defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new GPLDomain(graph) with VertexDomain with EdgeDomain with extensions {}

  lazy val Gamma = repository.init(ReflectedRepository(repository,
      classLoader = this.getClass.getClassLoader,
      substitutionSpace = kindingGtp.merge(kindingWgt)),
    graph)

  lazy val combinatorComponents = Gamma.combinatorComponents
//
//  lazy val targets1: Seq[Constructor] =Seq(graphSemantics(graphSemantics.base))  //Synthesizer.allTargets   in shared
//
//  lazy val targets2: Seq[Constructor] = Seq(vertexSemantics(vertexSemantics.base))
//
//  lazy val targets3: Seq[Constructor] = Seq(vertexIterSemantics(vertexIterSemantics.base))
//
//  lazy val targets4: Seq[Constructor] = Seq(edgeIfcSemantics(edgeIfcSemantics.base))
//
//  lazy val targets5: Seq[Constructor] = Seq(edgeIterSemantics(edgeIterSemantics.base))
//
//  lazy val targets6: Seq[Constructor] = Seq(neighborIfcSemantics(neighborIfcSemantics.base))
//
//  lazy val targets7: Seq[Constructor] = Seq(workSpaceSemantics(workSpaceSemantics.base))
//
//  lazy val targetFinal:Seq[Constructor] = Seq(vertexSemantics(vertexSemantics.base))
////targets1++targets2++targets3++targets4++targets5++targets6++targets7
//
//  lazy val onlyTarget = Seq(tinySemantics(tinySemantics.base))

  // ALL intersection types must be *.complete because those are the CompilationUnits
  lazy val targets:Seq[Constructor]= Seq(
    vertexLogic(vertexLogic.base, vertexLogic.complete),
    //edgeLogic(edgeLogic.base,edgeLogic.empty),
    edgeLogic(edgeLogic.base,edgeLogic.complete),
    vertexIterLogic(vertexIterLogic.base,vertexIterLogic.complete ),
    edgeIfcLogic(edgeIfcLogic.base,edgeIfcLogic.complete ),
    edgeIterLogic(edgeIterLogic.base,edgeIterLogic.complete ),
    neighborIfcLogic(neighborIfcLogic.base,neighborIfcLogic.complete ),
    neighborLogic(neighborLogic.base,neighborLogic.complete),
    graphLogic(graphLogic.base,graphLogic.complete )//graphLogic.complete)
  )

  lazy val results: Results = EmptyInhabitationBatchJobResults(Gamma)
      .addJobs[CompilationUnit](targets).compute() //hacking

  lazy val controllerAddress: String = graph.name

}
