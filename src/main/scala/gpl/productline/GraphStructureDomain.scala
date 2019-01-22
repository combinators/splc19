package gpl.productline

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import gpl.domain.{Graph, SemanticTypes}
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._

import org.combinators.templating.twirl.Java

trait GraphStructureDomain extends SemanticTypes {

  val graph:Graph

  @combinator object graphBase {
    def apply(body : Seq[BodyDeclaration[_]]): CompilationUnit = {
      Java(
        s"""
           |package gpl;
           |
           |public class Graph  {
           | ${body.mkString("\n")}
           |
           |}
         """.stripMargin).compilationUnit
    }

    val semanticType: Type = graphLogic(graphLogic.base, graphLogic.extensions) =>:
      'PleaseWork
  }


}
