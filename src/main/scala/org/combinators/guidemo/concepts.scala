package org.combinators.guidemo

import org.combinators.cls.types._
import syntax._

trait SemanticConcepts {
  object Location {
    def apply(of: Type): Type = 'Location(of)
    val Database: Type = 'Database
    val Logo: Type = 'Logo
  }

  object ChoiceDialog {
    def apply(formShape: Type): Type = 'ChoiceDialog(formShape)
  }
  object OrderMenu {
    def apply(formShape: Type): Type = 'OrderMenu(formShape)
  }
  object FormShape {
    val RadioButtons: Type = 'RadioButtons
    val DropDown: Type = 'DropDown
    val variableFormShape = Variable("alpha")
    lazy val formShapeKinding: Kinding =
      Kinding(variableFormShape)
        .addOption(RadioButtons).addOption(DropDown)
  }


  object BuildFile {
    val Code: Type = 'BuildFile
    val ExtraDependencies: Type = 'ExtraDependencies
  }

  val BranchName: Type = 'BranchName
  val DatabaseAccessCode: Type = 'DatabaseAccessCode
}
