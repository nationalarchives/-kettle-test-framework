/*
 * Copyright (c) 2021 The National Archives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.gov.nationalarchives.pdi.test.helpers

import java.io.IOException
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{ FileVisitResult, Files, Path, SimpleFileVisitor }
import scala.util.{ Try, Using }

object IOHelper {

  /** Find a file within a directory.
    * Does not check sub-directories.
    *
    * @param directory the directory to search
    * @param prefix a prefix of the filename to match
    * @param suffix a suffix of the filename to match
    *
    * @return The path of the matching file
    */
  def findFile(directory: Path, prefix: String, suffix: String): Try[Option[Path]] =
    Using(
      Files.find(
        directory,
        1,
        (path, _) =>
          if (Files.isRegularFile(path)) {
            val fileName = path.getFileName.toString
            fileName.startsWith(prefix) && fileName.endsWith(suffix)
          } else {
            false
          }
      )
    ) { use =>
      use.findFirst().map(Option(_)).orElse(Option.empty[Path])
    }

  /** Visits a directory tree deleting all
    * files and folders recursively.
    */
  private class DeleteDirVisitor extends SimpleFileVisitor[Path] {
    @throws[IOException]
    override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
      Files.deleteIfExists(file)
      FileVisitResult.CONTINUE
    }

    @throws[IOException]
    override def postVisitDirectory(dir: Path, exc: IOException): FileVisitResult = {
      if (exc != null) throw exc
      Files.deleteIfExists(dir)
      FileVisitResult.CONTINUE
    }
  }

  private val DELETE_DIR_VISITOR = new DeleteDirVisitor

  /** Deletes a directory and everything within it
    *
    * @param the directory to delete
    */
  @throws[IOException]
  def delete(path: Path): Unit =
    if (!Files.isDirectory(path)) {
      Files.deleteIfExists(path);
    } else {
      Files.walkFileTree(path, DELETE_DIR_VISITOR);
    }
}
