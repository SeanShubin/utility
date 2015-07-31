package com.seanshubin.utility.filesystem

case class Tree(name: String, branches: Branches) {
  def traverse(visitor: TreeVisitor): Unit = {
    visitor.before(name)
    branches.traverse(visitor)
    visitor.after(name)
  }
}

object Tree {
  def createBranches(names: String*): Branches = {
    Branches(names.toList.map(createTree))
  }

  def createTree(name: String): Tree = Tree(name, Branches.Empty)
}
