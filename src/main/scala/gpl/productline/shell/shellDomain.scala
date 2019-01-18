package gpl.productline.shell

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.BodyDeclaration
import gpl.domain._
import org.combinators.cls.interpreter.combinator
import org.combinators.cls.types.Type
import org.combinators.cls.types.syntax._
import org.combinators.templating.twirl.Java


class shellDomain (override val graph:Graph) extends GraphDomain(graph) with SemanticTypes{
/*
* *
    <feature automatic="selected" name="Gpl"/>
      <feature automatic="selected" name="MainGpl"/>
      <feature automatic="selected" name="Test"/>
      <feature automatic="selected" name="StartHere"/>
      <feature automatic="selected" manual="selected" name="Base"/>
      <feature automatic="selected" name="Benchmark"/>
      <feature manual="selected" name="Prog"/>
  */






}