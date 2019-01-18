package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import gpl.domain.{Graph, GraphDomain, SemanticTypes}
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.templating.twirl.Java

import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java

trait VertexDomain extends SemanticTypes {

  val graph:Graph

  @combinator object vertexBase{
    def apply(): CompilationUnit = {
      Java(
        s"""
           |package gpl;
           |
           |public class Vertex  {
           |  public String name = null;
           |
           |  public  Vertex assignName( String name ) {
           |      this.name = name;
           |  }
           |
           |  public String getName( ) {  return this.name; }
           |
           |}
         """.stripMargin).compilationUnit
    }

    val semanticType: Type = vertexLogic(vertexLogic.base, vertexLogic.empty)
  }


}
