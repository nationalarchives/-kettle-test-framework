package uk.gov.nationalarchives.pdi.test

import org.pentaho.di.trans.step.StepMetaInterface
import org.pentaho.di.trans.steps.concatfields.ConcatFieldsMeta
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.nationalarchives.pdi.step.jena.model.JenaModelStepMeta
import uk.gov.nationalarchives.pdi.step.jena.serializer.JenaSerializerStepMeta
import uk.gov.nationalarchives.pdi.step.jena.shacl.JenaShaclStepMeta

import java.io.FileInputStream
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
      val workflowFile = Paths.get(this.getClass.getClassLoader.getResource(simpleWorkflow).toURI).toFile
      val workflowParentDir = workflowFile.getParent
      Using(new FileInputStream(workflowFile)) { is =>
        val result = WorkflowManager.runTransformation(is, workflowParentDir, None, Some(plugins))
        result mustBe Right(true)
      }
    }

    "return a Left of type Throwable when a workflow fails to execute" in {
      val notAWorkflowFile = Paths.get(this.getClass.getClassLoader.getResource(notAWorkflow).toURI).toFile
      val workflowParentDir = notAWorkflowFile.getParent
      Using(new FileInputStream(notAWorkflowFile)) { is =>
        val result = WorkflowManager.runTransformation(is, workflowParentDir, None, None)
        result match {
          case Left(e) => e mustBe a[Throwable]
          case _       => fail()
        }
      }
    }
  }

}
