package com.seanshubin.utility.exec

import java.io.File
import java.util

trait ProcessBuilderContract {
  def command(command: String*): ProcessBuilderContract

  def directory(directory: File): ProcessBuilderContract

  def environment: util.Map[String, String]

  def redirectOutput(redirectToMe: ProcessBuilder.Redirect): ProcessBuilderContract

  def redirectError(redirectToMe: ProcessBuilder.Redirect): ProcessBuilderContract

  def start: ProcessContract
}
