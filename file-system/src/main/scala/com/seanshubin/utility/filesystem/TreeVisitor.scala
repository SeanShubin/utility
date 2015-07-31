package com.seanshubin.utility.filesystem

trait TreeVisitor {
  def before(name: String)

  def after(name: String)
}
