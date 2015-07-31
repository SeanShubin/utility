package com.seanshubin.utility.filesystem

case class Branches(trees: List[Tree]) {
  def add(path: String*) = addList(path.toList)

  def addList(path: List[String]): Branches = {
    val newBranches = path match {
      case Nil => Branches(Nil)
      case head :: tail =>
        val index = indexOf(head)
        if (index == -1) {
          Branches((Tree(head, Branches.Empty.addList(tail)) :: trees.reverse).reverse)
        } else {
          val oldTree = trees(index)
          val newTree: Tree = oldTree.copy(branches = oldTree.branches.addList(tail))
          val newTrees = trees.updated(index, newTree)
          Branches(newTrees)
        }
    }
    newBranches
  }

  def containsTreeNamed(name: String): Boolean = trees.exists(_.name == name)

  def maybeTreeNamed(name: String): Option[Tree] = trees.find(_.name == name)

  def addTreeNamed(name: String): Branches = Branches(Tree(name, Branches.Empty) :: trees)

  def addOrReplaceTree(updatedTree: Tree): Branches = {
    val index = trees.indexWhere(tree => tree.name == updatedTree.name)
    if (index == -1) {
      Branches(trees :+ updatedTree)
    } else {
      Branches(trees.updated(index, updatedTree))
    }
  }

  def pathExists(pathParts: List[String]): Boolean = {
    pathParts match {
      case Nil => false
      case name :: Nil => containsTreeNamed(name)
      case name :: remain =>
        val maybeTree = trees.find(_.name == name)
        maybeTree match {
          case Some(tree) => tree.branches.pathExists(remain)
          case None => false
        }
    }
  }

  private def indexOf(name: String): Int = {
    def nameMatches(tree: Tree) = tree.name == name
    trees.indexWhere(nameMatches)
  }

  def treeAt(pathParts: Seq[String]): Tree = {
    pathParts match {
      case Nil =>
        throw new RuntimeException("Empty path")
      case head :: Nil =>
        maybeTreeNamed(head) match {
          case Some(tree) => tree
          case None => throw new RuntimeException(s"No tree at $pathParts")
        }
      case head :: tail =>
        maybeTreeNamed(head) match {
          case Some(tree) => tree.branches.treeAt(tail)
          case None => throw new RuntimeException(s"No tree at $pathParts")
        }
    }
  }

  def traverse(treeVisitor: TreeVisitor): Unit = {
    trees.map(_.traverse(treeVisitor))
  }

  def remove(pathParts: Seq[String]): Branches = {
    val newBranches = pathParts match {
      case Nil => this
      case head :: Nil =>
        val index = indexOf(head)
        if (index == -1) {
          throw new RuntimeException(s"'$head' not found")
        } else {
          Branches(dropElementAtIndex(this.trees, index))
        }
      case head :: tail =>
        val index = indexOf(head)
        if (index == -1) {
          throw new RuntimeException(s"'$head' not found")
        } else {
          val tree = trees(index)
          Branches(trees.updated(index, tree.copy(branches = tree.branches.remove(pathParts.tail))))
        }
    }
    newBranches
  }

  private def dropElementAtIndex(trees: List[Tree], index: Int): List[Tree] = {
    val newTrees = trees.take(index) ++ trees.drop(index + 1)
    newTrees
  }
}

object Branches {
  val Empty = Branches(Nil)
}
