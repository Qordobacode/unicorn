/*******************************************************************************
 * (C) Copyright 2015 ADP, LLC.
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
 *******************************************************************************/

package unicorn.unibase

/**
 * @author Haifeng Li
 */
package object graph {
  val $doc = "_doc"
  val $table = "_table"

  private[unicorn] val GraphDocumentVertexTable = "unicorn_graph_doc_vertex"
  private[unicorn] val GraphVertexColumnFamily  = "vertex"
  private[unicorn] val GraphInEdgeColumnFamily  = "in"
  private[unicorn] val GraphOutEdgeColumnFamily = "out"
}
