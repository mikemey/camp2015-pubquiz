package core

import akka.actor.Actor

object SMSActor {

  case class SendSMS(to: String, message: String)

}

class SMSActor extends Actor {

  import SMSActor._

  def receive: Receive = {
    case SendSMS(number, body) => println(s"send SMS to ${number}, content: ${body}")
  }
}
