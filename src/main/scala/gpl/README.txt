

We are looking for a simple example that includes a variable in a combinator's
type specification. Something like:

@combinator AddStuff {
  def apply(unit:CompilationUnit) : CompilationUnit = {
     // this takes compilationUnit and modifies it to add the
     // necessary files in return
  }

   val semanticType:Type = THETA =>: THETA :&: 'GraphSubType

}

class AddStuff(sym:Symbm, newMethods:Seq[BodyDeclaration[_]]) {
  def apply(unit:CompilationUnit) : CompilationUnit = {
     // this takes compilationUnit and modifies it to add the
     // necessary files in return
  }

   val semanticType:Type = THETA =>: THETA :&: symb

}




Then we would have a target that would call for ...

  'GraphSubType :&: 'AnotherType :&: 'ThirdType

And the inhabitation would bring all these combinators together to achieve
the desired result.

ANy thoughts on how to get started with this?



Can we commit this