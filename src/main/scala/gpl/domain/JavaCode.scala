package gpl.domain

import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.stmt.{BlockStmt, Statement}

object JavaCode {

  /**
    * Add statements to the end of the given method.
    */
  def appendStatements(method: MethodDeclaration, stmts: Seq[Statement]): MethodDeclaration = {
    if (!method.getBody.isPresent) {
      val bb: BlockStmt = new BlockStmt()
      method.setBody(bb)
    }
    var block = method.getBody.get

    stmts.foreach(s => block = block.addStatement(s))
    method.setBody(block)
    method
  }

  /**
    * Add statements to the beginning of the method.
    */
  def prependStatements(method: MethodDeclaration, stmts: Seq[Statement]): MethodDeclaration = {
    if (!method.getBody.isPresent) {
      val bb: BlockStmt = new BlockStmt()
      method.setBody(bb)
    }
    var block = method.getBody.get

    stmts.reverse.foreach(s => block = block.addStatement(0, s))
    method.setBody(block)
    method
  }
}