package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import gpl.domain._
import javax.inject.Inject
import org.combinators.cls.git._
import org.combinators.cls.interpreter.ReflectedRepository
import org.combinators.cls.types.Constructor
import org.combinators.templating.persistable.JavaPersistable._
import org.webjars.play.WebJarsUtil
import play.api.inject.ApplicationLifecycle
import play.api.mvc.{Action, AnyContent}
//This is for Cycle testing
class Next @Inject()(webJars: WebJarsUtil, lifeCycle: ApplicationLifecycle) extends InhabitationController(webJars, lifeCycle) with SemanticTypes with RoutingEntries {

  // specify desired target by (a) declaring algorithm traits; (b) graph structure
  val graph:Graph =  new undirectedCycleNeighborNodes // new undirectedPrimNeighborNodesundirectedCycleNeighborNodes    // new undirectedKruskalNeighborNodes

  /** KlondikeDomain for Klondike defined herein. Controllers are defined in Controllers area. */
  lazy val repository = new GPLDomain(graph) with VertexDomain with EdgeDomain with extensions {}

  lazy val Gamma = repository.init(ReflectedRepository(repository,
      classLoader = this.getClass.getClassLoader,
      substitutionSpace = kindingGtp.merge(kindingWgt)),
    graph)

  lazy val combinatorComponents = Gamma.combinatorComponents

  // git clone -b variation_0 http://localhost:9000/simple/simple.git

  // ALL intersection types must be *.complete because those are the CompilationUnits
  lazy val targets:Seq[Constructor]= Seq(
    // vertex
      vertexLogic(vertexLogic.complete),
     // DEPRECATE vertexIterLogic(vertexIterLogic.base,vertexIterLogic.complete ),
    // edge
      edgeLogic(edgeLogic.base,edgeLogic.complete),
      edgeIfcLogic(edgeIfcLogic.base,edgeIfcLogic.complete ),
     // edgeIterLogic(edgeIterLogic.base,edgeIterLogic.complete ),
    // neighbor
    //  neighborIfcLogic(neighborIfcLogic.base,neighborIfcLogic.complete ),
    //  neighborLogic(neighborLogic.base,neighborLogic.complete),
    // workspace: These should only be generated based on the target
      workSpaceLogic(workSpaceLogic.base,workSpaceLogic.complete),//complete),
      cycleWorkSpaceLogic(cycleWorkSpaceLogic.base,cycleWorkSpaceLogic.complete),
    // GRAPH as final
    graphLogic(graphLogic.complete)
  )

  lazy val results: Results = EmptyInhabitationBatchJobResults(Gamma)
      .addJobs[CompilationUnit](targets).compute() //hacking

  lazy val controllerAddress: String = graph.name

  /** Always prepares result 0 before checking out */
  override def serveFile(name: String): Action[AnyContent] = {
    implicit val ex = defaultExecutionContext
    Action.async(request =>
      super.prepare(0)(request).flatMap(_ => super.serveFile(name)(request))
    )
  }

}
