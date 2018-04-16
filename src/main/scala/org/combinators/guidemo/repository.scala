package org.combinators.guidemo

import java.net.URL
import java.nio.file.Paths

import com.github.javaparser.ast.CompilationUnit
import com.google.inject._
import org.combinators.cls.git.{EmptyInhabitationBatchJobResults, ResultLocation, Results}
import org.combinators.cls.interpreter.{ReflectedRepository, combinator}
import org.combinators.guidemo.Helpers._
import org.combinators.guidemo.concepts.Concepts._
import org.combinators.guidemo.domain.{CoffeeBar, DatabaseType, MenuLayout}
import org.combinators.templating.persistable.JavaPersistable._
import org.combinators.templating.persistable.{BundledResource, Persistable}
import org.combinators.templating.twirl.Java

import scala.meta._

class Repository(coffeeBar: CoffeeBar) extends AbstractModule {

  override def configure(): Unit = {}

  @Provides
  @OrderMenu
  def provideCustomerForm(@BranchName title: String,
      @Location(of = Locatable.Logo) logoLocation: URL,
      @ChoiceDialog optionSelector: CoffeeBarModifier): CompilationUnit = {
    val coffeeBar = Java(readFile("CustomerForm.java")).compilationUnit()
    addOptionSelection(coffeeBar, optionSelector)
    addTitle(coffeeBar, title)
    addLogo(coffeeBar, logoLocation)
    coffeeBar
  }

  @Provides
  @BuildFile
  def provideProjectBuildFile(@ExtraDependencies extraDependencies: Seq[scala.meta.Term]): scala.meta.Source = {
    val buildFile = scala.meta.dialects.Sbt1(readFile("build.sbt")).parse[scala.meta.Source].get
    buildFile.copy(buildFile.children.map {
      case q"libraryDependencies ++= Seq(..$oldDependencies)" =>
        q"""libraryDependencies ++= Seq(..${oldDependencies ++ extraDependencies.toList})"""
      case q"$x" => x
    })
  }


  @Provides
  @BranchName
  def provideBranchName: String = coffeeBar.getBranchName
  @Provides
  @Location(of = Locatable.Database)
  def provideDatabaseLocation(): String = coffeeBar.getProductDatabase.getDatabaseLocation
  @Provides
  @Location(of = Locatable.Logo)
  def provideLogoLocation: URL = coffeeBar.getLogoLocation

  class RestJSONDatabaseModule extends AbstractModule {
    override def configure(): Unit = {}

    @Provides
    @DatabaseAccessCode
    def provideRestJSONProductProvider(@Location(of = Locatable.Database) databaseLocation: String): CoffeeBarModifier =
      coffeBar => new Runnable() {
        def run() = {
          coffeBar.addImport("java.util.List")
          coffeBar.addImport("java.util.ArrayList")
          coffeBar.addImport("com.fasterxml.jackson.databind.ObjectMapper")


          val cls = coffeBar.getClassByName("CustomerForm").get
          val productsMethod =
            Java(
              s"""
                 |public List<String> getProductOptions() {
                 |    ObjectMapper mapper = new ObjectMapper();
                 |    List<String> options;
                 |    try{
                 |        options = mapper.readValue(
                 |        new URL("$databaseLocation"),
                 |        mapper.getTypeFactory().constructCollectionType(List.class, String.class));
                 |    } catch (Exception e) {
                 |        options = new ArrayList<>();
                 |        JOptionPane.showMessageDialog(this, String.format("Could not load options: %s", e.getMessage()));
                 |    }
                 |    return options;
                 |}
               """.stripMargin).methodDeclarations().head

          cls.addMember(productsMethod)
        }
      }

    @Provides
    @ExtraDependencies
    def provideJSONDependencies: Seq[scala.meta.Term] =
      q"""Seq(
            "com.fasterxml.jackson.core" % "jackson-annotations" % "2.8.8",
            "com.fasterxml.jackson.core" % "jackson-core" % "2.8.8",
            "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.8"
         )""" match { case q"Seq(..$xs)" => xs }
  }

  class JDBCDatabaseModule extends AbstractModule {
    override def configure(): Unit = {}

    @Provides
    @DatabaseAccessCode
    def provideJDBCDatabaseProductProvider(@Location(of = Locatable.Database) databaseLocation: String): CoffeeBarModifier =
      coffeBar => new Runnable() {
        def run() = {
          coffeBar.addImport("java.sql.*")
          coffeBar.addImport("java.util.List")
          coffeBar.addImport("java.util.ArrayList")

          val cls = coffeBar.getClassByName("CustomerForm").get
          val productsMethod =
            Java(
              s"""
                 |public List<String> getProductOptions() {
                 |    List<String> options = new ArrayList<>();
                 |    Connection connection = null;
                 |    try{
                 |        Class.forName("org.h2.Driver");
                 |        connection = DriverManager.getConnection("$databaseLocation", "sa", "");
                 |        ResultSet results = connection.prepareStatement("SELECT name FROM coffee").executeQuery();
                 |        while (results.next()) {
                 |            options.add(results.getString("name"));
                 |        }
                 |    } catch (Exception e) {
                 |        JOptionPane.showMessageDialog(this, String.format("Could not load options: %s", e.getMessage()));
                 |    } finally {
                 |        if (connection != null) {
                 |            try {
                 |                connection.close();
                 |            } catch (Exception ex) {
                 |                throw new RuntimeException(ex);
                 |            }
                 |        }
                 |    }
                 |    return options;
                 |}
               """.stripMargin).methodDeclarations().head

          cls.addMember(productsMethod)
        }
      }

    @Provides
    @ExtraDependencies
    def provideJDBCDependencies: Seq[scala.meta.Term] =
      q"""Seq(
           "com.h2database" % "h2" % "1.4.196"
         )""" match { case q"Seq(..$xs)" => xs }
  }

  class DropDownModule extends AbstractModule {
    override def configure(): Unit = {}

    @Provides
    @ChoiceDialog
    def provideDropDownSelector(@DatabaseAccessCode databaseAccessCode: CoffeeBarModifier): CoffeeBarModifier = {
      coffeBar => new Runnable() {
        def run() = {
          addDatabaseAccessCode(coffeBar, databaseAccessCode)
          val cls = coffeBar.getClassByName("CustomerForm").get
          val initMethod = cls.getMethodsByName("initComponents").get(0)
          val getOrders =
            Java(
              s"""
                 |List<String> options = getProductOptions();
                 |JComboBox optionBox = new JComboBox(options.toArray(new String[0]));
                 |if (options.size() > 0) {
                 |    optionBox.setSelectedIndex(0);
                 |    selectedOrder = options.get(0);
                 |    optionBox.addActionListener(e -> { selectedOrder = (String)optionBox.getSelectedItem(); });
                 |}
                 |this.add(optionBox);
             """.stripMargin).statements()

          getOrders.reverse.foreach(stmt => initMethod.getBody.get().addStatement(0, stmt))
        }
      }
    }
  }
  class RadioButtonModule extends AbstractModule {
    override def configure(): Unit = {}
    @Provides
    @ChoiceDialog
    def provideRadioButtonSelector(@DatabaseAccessCode databaseAccessCode: CoffeeBarModifier): CoffeeBarModifier = {
      coffeBar =>
        new Runnable() {
          def run() = {
            addDatabaseAccessCode(coffeBar, databaseAccessCode)
            val cls = coffeBar.getClassByName("CustomerForm").get
            val initMethod = cls.getMethodsByName("initComponents").get(0)
            val getOrders =
              Java(
                s"""
                   |List<String> options = getProductOptions();
                   |ButtonGroup group = new ButtonGroup();
                   |for (String option : options) {
                   |  JRadioButton optionButton = new JRadioButton(option);
                   |  optionButton.addActionListener(e -> { selectedOrder = option; });
                   |  group.add(optionButton);
                   |  this.add(optionButton);
                   |}
                   |if (options.size() > 0) {
                   |    selectedOrder = options.get(0);
                   |}
                 """.stripMargin).statements()
            getOrders.reverse.foreach(stmt => initMethod.getBody.get().addStatement(0, stmt))
          }
        }
    }
  }

  private def databaseModule: AbstractModule = {
    coffeeBar.getProductDatabase.getDatabaseType match {
      case DatabaseType.JDBC =>
        new JDBCDatabaseModule
      case DatabaseType.RestJSON =>
        new RestJSONDatabaseModule
    }
  }

  private def uiSelectorModule: AbstractModule = {
    coffeeBar.getMenuLayout match {
      case MenuLayout.DropDown =>
        new DropDownModule
      case MenuLayout.RadioButtons =>
        new RadioButtonModule
    }
  }

  lazy val injector: Injector =
    Guice.createInjector(this, databaseModule, uiSelectorModule)

  class DummyRepository {
    @combinator object Dummy {
      def apply(): Unit = ()
    }
  }
  def forInhabitation: ReflectedRepository[DummyRepository] =
    ReflectedRepository[DummyRepository](new DummyRepository, classLoader = getClass.getClassLoader)

  implicit val unitPersistable: Persistable.Aux[Unit] = new Persistable {
    type T = Unit
    override def rawText(elem: Unit): Array[Byte] = Array.emptyByteArray
    override def path(elem: Unit) = Paths.get("EMPTY")
  }

  def getResults(implicit resultLocation: ResultLocation): Results = {
    val generatedCode = injector.getInstance(Key.get(classOf[CompilationUnit], classOf[OrderMenu]))
    val generatedBuildFile = injector.getInstance(Key.get(classOf[scala.meta.Source], classOf[BuildFile]))
    EmptyInhabitationBatchJobResults(forInhabitation)
      .addJob[Unit]()
      .compute()
      .addExternalArtifact(BundledResource("gitignore", Paths.get(".gitignore"), getClass))
      .addExternalArtifact(BundledResource("build.properties", Paths.get("project", "build.properties"), getClass))
      .addExternalArtifact(generatedCode)
      .addExternalArtifact(generatedBuildFile)(persistable = BuildFilePersistable)
  }
}
