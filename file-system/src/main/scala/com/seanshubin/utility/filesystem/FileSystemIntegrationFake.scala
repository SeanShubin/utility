package com.seanshubin.utility.filesystem

import java.io._
import java.nio.charset.Charset
import java.nio.file.{FileVisitor, Path, Paths}
import java.util

import com.seanshubin.utility.filesystem.FileSystemIntegrationFake.Branches

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.collection.immutable.ListMap
import scala.collection.mutable.ArrayBuffer

class FileSystemIntegrationFake extends FileSystemIntegration {
  private var roots: Branches = Branches.Empty
  private var content: Map[Path, ArrayBuffer[Byte]] = ListMap()

  def toMultipleLineString: Seq[String] = {
    roots.toMultipleLineString ++ content.toList.map(contentToString)
  }

  private def contentToString(entry: (Path, Seq[Byte])): String = {
    val (path, bytes) = entry
    s"(${bytes.size} bytes) ${pathToPathParts(path).mkString("/")}"
  }

  class FakeOutputStream(path: Path) extends OutputStream {
    val buffer = content(path)

    override def write(b: Int): Unit = buffer.append(b.toByte)
  }

  override def readAllBytes(path: Path): Seq[Byte] = content(path)

  override def newOutputStream(path: Path): OutputStream = {
    write(path, Seq())
    new FakeOutputStream(path)
  }

  override def createDirectories(path: Path): Path = {
    addFile(path)
    path
  }

  override def walkFileTree(start: Path, visitor: FileVisitor[_ >: Path]): Path = {
    val basePathParts = pathToPathParts(start)
    val tree = roots.treeAt(basePathParts)
    tree.traverse(new FileSystemIntegrationFake.TreeVisitor {
      private var pathParts: List[String] = basePathParts.reverse.tail

      private def path: Path = Paths.get(pathParts.reverse.head, pathParts.reverse.tail: _*)

      override def before(name: String): Unit = {
        pathParts = name :: pathParts
        if (isDirectory(path)) {
          visitor.preVisitDirectory(path, null)
        } else {
          visitor.visitFile(path, null)
        }
      }

      override def after(name: String): Unit = {
        if (isDirectory(path)) {
          visitor.postVisitDirectory(path, null)
        }
        pathParts = pathParts.tail
      }
    })
    start
  }

  override def newInputStream(path: Path): InputStream = new ByteArrayInputStream(content(path).toArray)

  override def write(path: Path, bytes: Seq[Byte]): Path = {
    addFile(path)
    val buffer = new ArrayBuffer[Byte](bytes.size)
    buffer.appendAll(bytes)
    content += (path -> buffer)
    path
  }

  override def write(path: Path, javaLines: java.lang.Iterable[_ <: CharSequence], charset: Charset): Path = {
    val writer = new PrintWriter(newBufferedWriter(path, charset))
    val scalaLines = javaLines.asScala
    for {
      line <- scalaLines
    } {
      writer.println(line)
    }
    writer.flush()
    path
  }

  override def isDirectory(path: Path): Boolean = {
    val pathExists = exists(path)
    val containsContent = content.contains(path)
    val pathIsDirectory = pathExists && !containsContent
    pathIsDirectory
  }

  override def readAllLines(path: Path, charset: Charset): java.util.List[String] = {
    val reader = newBufferedReader(path, charset)
    val scalaLines = readerToLines(reader, Nil)
    val javaLines = new util.ArrayList[String]()
    for {
      line <- scalaLines
    } {
      javaLines.add(line)
    }
    javaLines
  }

  @tailrec
  private def readerToLines(reader: BufferedReader, soFar: List[String]): List[String] = {
    val line = reader.readLine()
    if (line == null) soFar.reverse
    else readerToLines(reader, line :: soFar)
  }

  override def deleteIfExists(path: Path): Boolean = {
    val alreadyExists = exists(path)
    if (alreadyExists) {
      content -= path
      roots = roots.remove(pathToPathParts(path))
    }
    alreadyExists
  }

  override def exists(path: Path): Boolean = {
    val pathParts = pathToPathParts(path)
    roots.pathExists(pathParts)
  }

  override def newBufferedWriter(path: Path, charset: Charset): BufferedWriter =
    new BufferedWriter(new OutputStreamWriter(newOutputStream(path), charset))

  override def newBufferedReader(path: Path, charset: Charset): BufferedReader =
    new BufferedReader(new InputStreamReader(newInputStream(path), charset))

  private def addFile(path: Path): Unit = {
    val pathParts = pathToPathParts(path)
    roots = roots.add(pathParts: _*)
  }

  private def pathToPathParts(path: Path): List[String] = {
    val pathParts = path.iterator().asScala.map(_.toString).toList
    val newHead = if (path.isAbsolute) {
      "/" + pathParts.head
    } else {
      pathParts.head
    }
    newHead :: pathParts.tail
  }
}

object FileSystemIntegrationFake {

  case class Tree(name: String, branches: Branches) {
    def toIndentedMultipleLineString(depth: Int): Seq[String] = {
      val head = indent(depth) + name
      val tail = branches.toIndentedMultipleLineString(depth + 1)
      head +: tail
    }

    private def indent(depth: Int) = "  " * depth

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

  trait TreeVisitor {
    def before(name: String)

    def after(name: String)
  }

  case class Branches(trees: List[Tree]) {
    def toMultipleLineString: Seq[String] = {
      toIndentedMultipleLineString(0)
    }

    def toIndentedMultipleLineString(depth: Int): Seq[String] = {
      def treeToMultipleLineString(tree: Tree): Seq[String] = tree.toIndentedMultipleLineString(depth)

      val lines = trees.flatMap(treeToMultipleLineString)
      lines
    }

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
      trees.foreach(_.traverse(treeVisitor))
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

}
