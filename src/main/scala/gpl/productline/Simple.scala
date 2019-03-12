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
  val graph:Graph = new undirectedKruskalNeighborNodes
 // val graph:Graph = new Target
 //   with UndirectedEdges with WeightedEdges
 //   with NeighborStorage with LabeledVertex with UncoloredVertex
 //   with Prim with Kruskal

    //new undirectedNeighborNodes

  /** KlondikeDomain for Klondike defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new GPLDomain(graph) with VertexDomain with EdgeDomain with extensions {}

  lazy val Gamma = repository.init(ReflectedRepository(repository,
      classLoader = this.getClass.getClassLoader,
      substitutionSpace = kindingGtp.merge(kindingWgt)),
    graph)

  lazy val combinatorComponents = Gamma.combinatorComponents

  // ALL intersection types must be *.complete because those are the CompilationUnits
  lazy val targets:Seq[Constructor]= Seq(
    // vertex
      vertexLogic(vertexLogic.base, vertexLogic.complete),
     // DEPRECATE vertexIterLogic(vertexIterLogic.base,vertexIterLogic.complete ),
    // edge
      edgeLogic(edgeLogic.base,edgeLogic.complete),
      edgeIfcLogic(edgeIfcLogic.base,edgeIfcLogic.complete ),
     // edgeIterLogic(edgeIterLogic.base,edgeIterLogic.complete ),
    // neighbor
      neighborIfcLogic(neighborIfcLogic.base,neighborIfcLogic.complete ),
      neighborLogic(neighborLogic.base,neighborLogic.complete),
    // workspace: These should only be generated based on the target
      workSpaceLogic(workSpaceLogic.base,workSpaceLogic.complete),
    // GRAPH as final
    graphLogic(graphLogic.base,graphLogic.complete )
  )

  lazy val results: Results = EmptyInhabitationBatchJobResults(Gamma)
      .addJobs[CompilationUnit](targets).compute() //hacking

  lazy val controllerAddress: String = graph.name

}
