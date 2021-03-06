/*
 * Copyright 2018 Dept. Protein Evolution, Max Planck Institute for Developmental Biology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.proteinevolution.results.models.resultviews

import play.twirl.api.HtmlFormat

import scala.collection.immutable.ListMap

case class SamCCResultView(jobId: String) extends ResultView {

  override lazy val tabs: ListMap[String, HtmlFormat.Appendable] = ListMap(
    "3D-Structure-With-Axes" ->
    views.html.resultpanels.NGL3DStructure(
      s"/results/files/$jobId/$jobId.pdb",
      jobId + ".pdb",
      jobId,
      "samcc_PDB_AXES"
    ),
    "Plots" -> views.html.resultpanels.samcc(
      s"/results/files/$jobId/out0.png",
      s"/results/files/$jobId/out1.png",
      s"/results/files/$jobId/out2.png",
      s"/results/files/$jobId/out3.png"
    ),
    "NumericalData" -> views.html.resultpanels.fileviewWithDownload(jobId + ".out", jobId, "samcc")
  )

}
