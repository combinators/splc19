package org.combinators.guidemo

import java.io.InputStream
import java.nio.file.{Path, Paths}

import com.github.javaparser.ast.CompilationUnit
import org.combinators.templating.persistable.{BundledResource, Persistable, ResourcePersistable}
import org.combinators.cls.git.{InhabitationController, Results}
import org.combinators.templating.twirl.Java

import scala.meta._
import scala.meta.contrib._

object Helpers {
  implicit val persistable: ResourcePersistable.Aux = ResourcePersistable.apply
  type CoffeeBarModifier = CompilationUnit => Runnable

  def addOptionSelection(coffeeBar: CompilationUnit, optionSelection: CoffeeBarModifier): Unit = {
    optionSelection(coffeeBar).run
  }

  def addTitle(coffeeBar: CompilationUnit, title: String): Unit = {
    val cls = coffeeBar.getClassByName("CustomerForm").get
    val initMethod = cls.getMethodsByName("initComponents").get(0)
    initMethod
      .getBody.get()
      .addStatement(0, Java(s"""this.setTitle("$title");""").statement())
  }

  def addLogo(coffeeBar: CompilationUnit, logoLocation: java.net.URL): Unit = {
    val cls = coffeeBar.getClassByName("CustomerForm").get
    val initMethod = cls.getMethodsByName("initComponents").get(0)
    coffeeBar.addImport("java.net.URL")
    initMethod
      .getBody.get()
      .addStatement(0, Java(
        s"""
           |try {
           |  this.add(new JLabel(new ImageIcon(new URL("$logoLocation"))));
           |} catch (Exception e) {
           |
           |}""".stripMargin).statement())
  }

  def addDatabaseAccessCode(coffeeBar: CompilationUnit, databaseAccessCode: CoffeeBarModifier): Unit = {
    databaseAccessCode(coffeeBar).run()
  }

  def readFile(name: String): String =
    scala.io.Source.fromInputStream(getClass.getResourceAsStream(name)).mkString

  implicit val BuildFilePersistable: Persistable.Aux[scala.meta.Source] =
    new Persistable {
      type T = scala.meta.Source
      def rawText(elem: scala.meta.Source): Array[Byte] =
        elem.toString().getBytes()
      def path(elem: scala.meta.Source): Path = Paths.get("build.sbt")
    }
}
