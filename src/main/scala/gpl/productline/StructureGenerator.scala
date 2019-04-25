package gpl.productline

/**
  * Knows how to return traversal code for the fundamental needs of any graph structure.
  *
  * getVertices(v,bs) = return all Vertices in a graph, using variable 'v' passed in, and
  *   presumably used within incoming bs.
  *
  * getEdgesOf(en,u,bs) = return all edges (u,x) in graph and return as edge variable 'en'
  *   passed in, and presumably used within incoming bs. This method is invoked within
  *   a Graph and since we can't rely on a vertex to store the edges (i.e., it might only
  *   store neighbors) there is an understanding that the implementation might construct
  *   edges anew
  *
  * adjacentVertices(v,bs) = return all neighbor vertices for the 'current' context, to
  *   be included within the Vertex class, using variable 'v' passed in, and presumably
  *   used within incoming bs.
  */
class StructureGenerator {
  def getVerticesBegin(v:String) : String = {
    s"""
       |for (Iterator<Vertex> it$v = getVertices(); it$v.hasNext(); ) {
       |  Vertex $v = it$v.next();
       |""".stripMargin
  }
  def getVerticesEnd(v:String) : String = "}"

  def getEdgesBegin(en:String) : String = {
    s"""
       |for (Edge $en : edges) {
       |  """.stripMargin
  }
  def getEdgesEnd(v:String) : String = "}"

  def getEdgesOfBegin(en:String, u:String) : String = {
    s"""
       |for (Iterator<Edge> it$en = getEdges($u); it$en.hasNext(); ) {
       |  Edge $en = it$en.next();
           """.stripMargin
  }
  def getEdgesOfEnd(v:String) : String = "}"

  // Just the starting point (no closing brace)
  def adjacentVertices(v:String) : String = {
    s"""
       |for (Iterator<Vertex> it$v = neighbors.iterator(); it$v.hasNext(); ) {
       |  Vertex $v = it$v.next();
       """.stripMargin
  }

  def adjacentVerticesEnd(v:String) : String = "}"
}
