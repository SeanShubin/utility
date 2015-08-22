package com.seanshubin.utility.filesystem

import org.scalatest.FunSuite

import scala.collection.mutable.ArrayBuffer

class BranchesTest extends FunSuite {

  import FileSystemIntegrationFake._

  test("create with one named branch") {
    val actual = Tree.createBranches("foo")
    val expected = Branches(List(Tree("foo", Branches.Empty)))
    assert(expected === actual)
  }

  test("create with many named branches named branch") {
    val actual = Tree.createBranches("foo", "bar", "baz")
    val expected = Branches(List(Tree("foo", Branches.Empty), Tree("bar", Branches.Empty), Tree("baz", Branches.Empty)))
    assert(expected === actual)
  }

  test("add nothing") {
    val expected = List()
    val actual = Branches.Empty.add().trees
    assert(actual === expected)
  }

  test("simple add") {
    val expected = List(Tree("aaa", Branches.Empty))
    val actual = Branches.Empty.add("aaa").trees
    assert(actual === expected)
  }

  test("add deep") {
    val expected = List(Tree("aaa", Branches(List(Tree("bbb", Branches.Empty)))))
    val actual = Branches.Empty.add("aaa", "bbb").trees
    assert(actual === expected)
  }

  test("add deep in 2 steps") {
    val expected = List(Tree("aaa", Branches(List(Tree("bbb", Branches.Empty)))))
    val actual = Branches.Empty.add("aaa").add("aaa", "bbb").trees
    assert(actual === expected)
  }

  test("add shallow") {
    val expected = List(Tree("aaa", Branches.Empty), Tree("bbb", Branches.Empty))
    val actual = Branches.Empty.add("aaa").add("bbb").trees
    assert(actual === expected)
  }

  test("branch out") {
    val expected = List(Tree("aaa", Branches(List(Tree("bbb", Branches.Empty), Tree("ccc", Branches.Empty)))))
    val actual = Branches.Empty.add("aaa", "bbb").add("aaa", "ccc").trees
    assert(actual === expected)
  }

  test("branch out three ways") {
    val expected = List(Tree("aaa", Branches(List(Tree("bbb", Branches.Empty), Tree("ccc", Branches.Empty), Tree("ddd", Branches.Empty)))))
    val actual = Branches.Empty.add("aaa", "bbb").add("aaa", "ccc").add("aaa", "ddd").trees
    assert(actual === expected)
  }

  test("add two levels deep after something exists") {
    val aTree = Tree("aaa", Branches.Empty)
    val iTree = Tree("iii", Tree.createBranches("jjj"))
    val expected = List(aTree, iTree)
    val actual = Branches.Empty.
      add("aaa").
      add("iii", "jjj").trees
    assert(actual === expected)
  }

  test("one level deep") {
    val aTree = Tree("aaa", Tree.createBranches("bbb", "ccc", "ddd"))
    val eTree = Tree("eee", Tree.createBranches("fff", "ggg", "hhh"))
    val iTree = Tree("iii", Tree.createBranches("jjj", "kkk", "lll"))
    val expected = List(aTree, eTree, iTree)
    val actual = Branches.Empty.
      add("aaa", "bbb").
      add("aaa", "ccc").
      add("aaa", "ddd").
      add("eee", "fff").
      add("eee", "ggg").
      add("eee", "hhh").
      add("iii", "jjj").
      add("iii", "kkk").
      add("iii", "lll").trees
    assert(actual === expected)
  }

  test("traverse") {
    val aTree = Tree("aaa", Tree.createBranches("bbb", "ccc"))
    val eTree = Tree("eee", Tree.createBranches("fff", "ggg"))
    val branches = Branches(List(aTree, eTree))
    val expected = Seq(
      "before aaa",
      "before aaa bbb",
      "after aaa bbb",
      "before aaa ccc",
      "after aaa ccc",
      "after aaa",
      "before eee",
      "before eee fff",
      "after eee fff",
      "before eee ggg",
      "after eee ggg",
      "after eee"
    )
    val actual: ArrayBuffer[String] = new ArrayBuffer()
    branches.traverse(new TreeVisitor {
      private var pathParts: List[String] = Nil

      private def path: String = pathParts.reverse.mkString(" ")

      override def before(name: String): Unit = {
        pathParts = name :: pathParts
        actual.append(s"before $path")
      }

      override def after(name: String): Unit = {
        actual.append(s"after $path")
        pathParts = pathParts.tail
      }
    })
    assert(actual === expected)
  }

  test("remove") {
    val aTree = Tree("aaa", Tree.createBranches("bbb", "ccc", "ddd"))
    val eTreeBefore = Tree("eee", Tree.createBranches("fff", "ggg", "hhh"))
    val eTreeAfter = Tree("eee", Tree.createBranches("fff", "hhh"))
    val iTree = Tree("iii", Tree.createBranches("jjj", "kkk", "lll"))
    val branches: Branches = Branches(List(aTree, eTreeBefore, iTree))
    val actual = branches.remove(Seq("eee", "ggg"))
    val expected = Branches(List(aTree, eTreeAfter, iTree))
    assert(actual === expected)
  }
}
