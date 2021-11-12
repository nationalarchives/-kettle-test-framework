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

package uk.gov.nationalarchives.pdi.test

import org.pentaho.di.trans.step.StepMetaInterface
import org.pentaho.di.trans.steps.concatfields.ConcatFieldsMeta
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.nationalarchives.pdi.step.jena.model.JenaModelStepMeta
import uk.gov.nationalarchives.pdi.step.jena.serializer.JenaSerializerStepMeta
import uk.gov.nationalarchives.pdi.step.jena.shacl.JenaShaclStepMeta

import java.nio.file.Files
import java.nio.file.Paths
import scala.util.Using

class WorkflowManagerSpec extends AnyWordSpec with Matchers with MockitoSugar {

  private val simpleWorkflow = "simple.ktr"
  private val notAWorkflow = "example.ttl"

  private val plugins: List[Class[_ <: StepMetaInterface]] = List(
    classOf[JenaModelStepMeta],
    classOf[JenaSerializerStepMeta],
    classOf[JenaShaclStepMeta],
    classOf[ConcatFieldsMeta]
  )

  "WorkflowManager.runTransformations" must {

    "return a Right of type Boolean when a workflow is successfully executed" in {
      val workflowFile = Paths.get(this.getClass.getClassLoader.getResource(simpleWorkflow).toURI)
      val workflowParentDir = workflowFile.getParent
      Using(Files.newInputStream(workflowFile)) { is =>
        val result = WorkflowManager.runTransformation(is, workflowParentDir, None, Some(plugins))
        result mustBe Right(true)
      }
    }

    "return a Left of type Throwable when a workflow fails to execute" in {
      val notAWorkflowFile = Paths.get(this.getClass.getClassLoader.getResource(notAWorkflow).toURI)
      val workflowParentDir = notAWorkflowFile.getParent
      Using(Files.newInputStream(notAWorkflowFile)) { is =>
        val result = WorkflowManager.runTransformation(is, workflowParentDir, None, None)
        result match {
          case Left(e) => e mustBe a[Throwable]
          case _       => fail()
        }
      }
    }
  }

}
