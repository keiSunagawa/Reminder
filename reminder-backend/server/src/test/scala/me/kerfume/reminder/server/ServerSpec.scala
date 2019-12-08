// package me.kerfume.reminder.server
// import org.scalatest.FreeSpec
// import org.scalatest.DiagrammedAssertions
// import org.http4s._
// import java.time.LocalDate
// import shapeless.ops.sized
// import me.kerfume.reminder.domain.remind.Remind

// class ServerSpec extends FreeSpec with DiagrammedAssertions {
//   import io.circe.parser
//   import org.http4s.circe._
//   "aa" - {
//     import me.kerfume.reminder.server.controller.RegistController
//     val res = ReminderServer.reminderApp()
//       .run(
//         Request(method = Method.POST, uri = Uri.uri("/regist/date")).withEntity(
//           parser
//             .parse("""{"title": "test task", "trigger": "2019-12-25"}""")
//             .right
//             .get
//         )
//       )
//       .unsafeRunSync()

//     "should status ok" in {
//       assert(res.status == Status.Ok)
//     }

//     val resBody: String =
//       new String(res.body.compile.toList.unsafeRunSync().toArray, "UTF-8")

//     "should body" in {
//       assert(resBody == "regist ok")
//     }

//     "should registed" in {
//       val reminds = Application.remindRepository.findAll()

//       assert(reminds.size == 1)
//       val actual = reminds.head.asInstanceOf[Remind.OfDate]
//       assert(actual.title == "test task")
//       assert(actual.trigger == LocalDate.of(2019, 12, 25))
//     }
//   }
// }
