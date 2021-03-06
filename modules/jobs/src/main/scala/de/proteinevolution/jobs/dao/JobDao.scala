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

package de.proteinevolution.jobs.dao

import java.time.ZonedDateTime

import de.proteinevolution.jobs.models.Job
import de.proteinevolution.statistics.{ JobEvent, JobEventLog }
import javax.inject.{ Inject, Singleton }
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.indexes.{ Index, IndexType }
import reactivemongo.api.{ Cursor, ReadConcern }
import reactivemongo.bson.{ BSONDateTime, BSONDocument, BSONObjectID }

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class JobDao @Inject()(private val reactiveMongoApi: ReactiveMongoApi)(implicit ec: ExecutionContext) {

  private lazy val jobCollection: Future[BSONCollection] = {
    reactiveMongoApi.database.map(_.collection[BSONCollection]("jobs")).map { collection =>
      collection.indexesManager.ensure(Index(Seq(Job.JOBID -> IndexType.Text), background = true, unique = true))
      collection
    }
  }

  private[jobs] lazy val eventLogCollection: Future[BSONCollection] =
    reactiveMongoApi.database.map(_.collection[BSONCollection]("jobevents"))

  final def findJob(id: String): Future[Option[Job]] =
    jobCollection.flatMap(_.find(BSONDocument(Job.JOBID -> id), None).one[Job])

  // TODO too generic
  def findJobs(selector: BSONDocument): Future[List[Job]] = {
    jobCollection
      .map(_.find(selector, None).cursor[Job]())
      .flatMap(_.collect[List](-1, Cursor.FailOnError[List[Job]]()))
  }

  final def selectJob(jobID: String): Future[Option[Job]] = {
    findJob(jobID)
  }

  final def removeJob(id: String): Future[WriteResult] = {
    jobCollection.flatMap(_.delete().one(BSONDocument(Job.JOBID -> id)))
  }

  final def findAndSortJobs(hash: String, sort: Int = -1): Future[List[Job]] = {
    jobCollection
      .map(
        _.find(BSONDocument(Job.HASH -> hash), None).sort(BSONDocument(Job.DATECREATED -> sort)).cursor[Job]()
      )
      .flatMap(_.collect[List](-1, Cursor.FailOnError[List[Job]]()))
  }

  final def findSortedJob(userId: BSONObjectID, sort: Int = -1): Future[Option[Job]] = {
    jobCollection.flatMap(
      _.find(BSONDocument(
               BSONDocument(Job.DELETION -> BSONDocument("$exists" -> false)),
               BSONDocument(Job.OWNERID  -> userId)
             ),
             None).sort(BSONDocument(Job.DATEUPDATED -> sort)).one[Job]
    )
  }

  final def insertJob(job: Job): Future[Option[Job]] = {
    jobCollection.flatMap(_.insert(ordered = false).one(job)).map { a =>
      if (a.ok) { Some(job) } else { None }
    }
  }

  // TODO this method is too generic, refactor into more specific ones
  def modifyJob(selector: BSONDocument, modifier: BSONDocument): Future[Option[Job]] = {
    jobCollection.flatMap(
      _.findAndUpdate(
        selector,
        modifier.merge(
          BSONDocument(
            "$set" -> BSONDocument(Job.DATEVIEWED -> BSONDateTime(ZonedDateTime.now.toInstant.toEpochMilli))
          )
        ),
        fetchNewObject = true
      ).map(_.result[Job])
    )
  }

  // TODO too generic, make 2 methods out of it
  def countJobs(selector: BSONDocument): Future[Long] = {
    jobCollection.flatMap(_.count(Some(selector), Some(0), 0, None, ReadConcern.Local))
  }

  final def addJobLog(jobEventLog: JobEventLog): Future[WriteResult] =
    eventLogCollection.flatMap(_.insert(ordered = false).one(jobEventLog))

  final def findJobEventLogs(instant: Long): Future[scala.List[JobEventLog]] = {
    eventLogCollection
      .map(
        _.find(BSONDocument(
                 JobEventLog.EVENTS ->
                 BSONDocument(
                   "$elemMatch" ->
                   BSONDocument(
                     JobEvent.TIMESTAMP ->
                     BSONDocument("$lt" -> BSONDateTime(instant))
                   )
                 )
               ),
               None).cursor[JobEventLog]()
      )
      .flatMap(_.collect[List](-1, Cursor.FailOnError[List[JobEventLog]]()))
  }

}
